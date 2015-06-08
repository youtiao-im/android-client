package im.youtiao.android_client.ui.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;

import de.greenrobot.event.EventBus;
import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.adapter.CommentCursorAdapter;
import im.youtiao.android_client.data.State;
import im.youtiao.android_client.data.SyncManager;
import im.youtiao.android_client.event.FeedStampEvent;
import im.youtiao.android_client.event.FeedStarEvent;
import im.youtiao.android_client.greendao.Comment;
import im.youtiao.android_client.greendao.CommentDao;
import im.youtiao.android_client.greendao.CommentHelper;
import im.youtiao.android_client.greendao.DaoSession;
import im.youtiao.android_client.greendao.Feed;
import im.youtiao.android_client.greendao.FeedDao;
import im.youtiao.android_client.greendao.FeedHelper;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.Logger;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FeedDetailActivity extends RoboActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FeedDetailActivity.class.getCanonicalName();
    private Feed feed;

    public static final String PARAM_FEED = "current_feed";

    @Inject
    CommentCursorAdapter mAdapter;

    @Inject
    DaoSession daoSession;

    @InjectView(R.id.lv_comments)
    ListView commentsLv;
    @InjectView(R.id.btn_comment_submit)
    Button commentSubmitBtn;
    @InjectView(R.id.edtTxt_comment_content)
    EditText commentContentEdtText;

    @InjectView(R.id.tv_creator_name)
    TextView feedCreatorNameTv;
    @InjectView(R.id.tv_created_at)
    TextView feedCreatedAtTv;
    @InjectView(R.id.tv_feed_text)
    TextView feedTextTv;
    @InjectView(R.id.tv_channel_name)
    TextView feedChannelNameTv;
    @InjectView(R.id.tv_feed_comment_count)
    TextView feedCommentCountTv;
    @InjectView(R.id.imgBtn_feed_stamp)
    ImageButton stampImgBtn;
    @InjectView(R.id.imgBtn_feed_star)
    ImageButton starImgBtn;
    @InjectView(R.id.imgBtn_feed_comment)
    ImageButton commentImgBtn;

    @Inject
    RemoteApi remoteApi;

    @Inject
    private SyncManager syncManager;

    private static final int ID_CHECK = 1;
    private static final int ID_CROSS = 2;
    private static final int ID_QUESTION = 3;
    private QuickAction quickAction;

    private static final int URL_LOADER = 1919;

    @Override
    public void onStart() {
        Log.i(TAG, "OnStart");
        super.onStart();
        EventBus.getDefault().register(this);
        syncManager.startCommentSyncForFeed(feed);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    protected void initView() {
        feed.__setDaoSession(daoSession);
        String email = feed.getUser().getEmail();
        feedCreatorNameTv.setText(email.substring(0, email.indexOf("@")));
        feedTextTv.setText(feed.getText());
        //viewHolder.createdAtTv.setText(TimeWrap.wrapTimeDisplyValue(feed.getCreatedAt().getTime()));
        feedCreatedAtTv.setText("3 mins ago");
        feedChannelNameTv.setText("#" + feed.getChannel().getName());
        feedCommentCountTv.setText("" + feed.getComments().size());
        if (feed.getIsStarred()) {
            starImgBtn.setImageResource(R.mipmap.ic_feed_star_true);
        } else {
            starImgBtn.setImageResource(R.mipmap.ic_feed_star_false);
        }
        starImgBtn.setOnClickListener(v -> {
            Log.i(TAG, "starImgBtn clicked");
            EventBus.getDefault().post(new FeedStarEvent(feed));
        });

        commentImgBtn.setOnClickListener(v -> {
            Log.i(TAG, "starImgBtn clicked");
            EventBus.getDefault().post(new FeedStarEvent(feed));
        });

        State.Mark mark = feed.getSymbol() != null ? State.Mark.valueOf(feed.getSymbol()) : State.Mark.DEFAULT;
        switch (mark) {
            case CHECK:
                stampImgBtn.setImageResource(R.mipmap.ic_feed_stamp_check);
                break;
            case CROSS:
                stampImgBtn.setImageResource(R.mipmap.ic_feed_stamp_cross);
                break;
            case QUESTION:
                stampImgBtn.setImageResource(R.mipmap.ic_feed_stamp_question);
                break;
            default:
                stampImgBtn.setImageResource(R.mipmap.ic_feed_stamp_default);
        }

        stampImgBtn.setOnClickListener(v -> {
            Log.i(TAG, "stampImgBtn clicked");
            EventBus.getDefault().post(new FeedStampEvent(v, feed));
        });

        ActionItem checkItem = new ActionItem(ID_CHECK, "Check", getResources().getDrawable(R.mipmap.ic_feed_stamp_check));
        ActionItem crossItem = new ActionItem(ID_CROSS, "Cross", getResources().getDrawable(R.mipmap.ic_feed_stamp_cross));
        ActionItem questionItem = new ActionItem(ID_QUESTION, "Question", getResources().getDrawable(R.mipmap.ic_feed_stamp_question));

        quickAction = new QuickAction(this, QuickAction.HORIZONTAL);
        quickAction.addActionItem(checkItem);
        quickAction.addActionItem(crossItem);
        quickAction.addActionItem(questionItem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        feed = (Feed) intent.getSerializableExtra(PARAM_FEED);

        initView();

        getLoaderManager().initLoader(URL_LOADER, null, this);
        commentsLv.setAdapter(mAdapter);

        commentSubmitBtn.setOnClickListener(v -> {
            String content = commentContentEdtText.getText().toString();
            remoteApi.createFeedComment(feed.getServerId(), content).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(res -> {
                        commentContentEdtText.setText("");
                        commentContentEdtText.setHint("Comment..");
                        CommentDao commentDao = daoSession.getCommentDao();
                        Comment comment = new Comment();
                        comment.setServerId(res.id);
                        comment.setText(res.text);
                        comment.setCreatedAt(res.createdAt);
                        comment.setFeedId(feed.getId());
                        comment.setCreatedBy(((YTApplication) getApplication()).getCurrentUser().getId());
                        comment.setUpdatedAt(res.updatedAt);
                        commentDao.insert(comment);
                        getContentResolver().notifyChange(CommentHelper.CONTENT_URI, null);
                    }, Logger::logThrowable);

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i(TAG, "OnCreateLoader");
        return new CursorLoader(this, CommentHelper.CONTENT_URI,
                CommentHelper.getProjection(), CommentHelper.FEEDID + " = '" + feed.getId() + "'", null, CommentHelper.DEFAULT_SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(TAG, "onLoadFinished: " + data.getCount());
        mAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void onEventMainThread(FeedStarEvent event) {
        Log.i(TAG, "onEventMainThread");
        Feed feed = event.feed;
        feed.setIsStarred(!feed.getIsStarred());
        //TODO: seed request to server
        // update feed on local db
        FeedDao feedDao = daoSession.getFeedDao();
        feedDao.update(feed);

        // update ui
        if (feed.getIsStarred()) {
            starImgBtn.setImageResource(R.mipmap.ic_feed_star_true);
        } else {
            starImgBtn.setImageResource(R.mipmap.ic_feed_star_false);
        }
        getContentResolver().notifyChange(FeedHelper.CONTENT_URI, null);
    }

    public void onEventMainThread(FeedStampEvent event) {
        final Feed fd = event.feed;
        final View v = event.view;
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                ActionItem actionItem = quickAction.getActionItem(pos);
                Log.i("FeedsFragment", "actionItem = " + actionItem.getActionId());
                switch (actionItem.getActionId()) {
                    case ID_CHECK:
                        fd.setSymbol(State.Mark.CHECK.toString());
                        break;
                    case ID_CROSS:
                        fd.setSymbol(State.Mark.CROSS.toString());
                        break;
                    case ID_QUESTION:
                        fd.setSymbol(State.Mark.QUESTION.toString());
                        break;
                }
                FeedDao feedDao = daoSession.getFeedDao();
                feedDao.update(fd);

                State.Mark mark = feed.getSymbol() != null ? State.Mark.valueOf(feed.getSymbol()) : State.Mark.DEFAULT;
                switch(mark) {
                    case CHECK:
                        stampImgBtn.setImageResource(R.mipmap.ic_feed_stamp_check);
                        break;
                    case CROSS:
                        stampImgBtn.setImageResource(R.mipmap.ic_feed_stamp_cross);
                        break;
                    case QUESTION:
                        stampImgBtn.setImageResource(R.mipmap.ic_feed_stamp_question);
                        break;
                    default:
                        stampImgBtn.setImageResource(R.mipmap.ic_feed_stamp_default);
                }

                getContentResolver().notifyChange(FeedHelper.CONTENT_URI, null);
            }
        });
        quickAction.show(v);
    }
}
