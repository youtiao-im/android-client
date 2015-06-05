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
import android.widget.ListView;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.YTApplication;
import im.youtiao.android_client.adapter.CommentCursorAdapter;
import im.youtiao.android_client.data.SyncManager;
import im.youtiao.android_client.greendao.Channel;
import im.youtiao.android_client.greendao.Comment;
import im.youtiao.android_client.greendao.CommentDao;
import im.youtiao.android_client.greendao.CommentHelper;
import im.youtiao.android_client.greendao.DaoSession;
import im.youtiao.android_client.greendao.Feed;
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

    @InjectView(R.id.comment_list)
    ListView commentListView;

    @InjectView(R.id.comment_submit)
    Button commentSubmitBtn;

    @InjectView(R.id.comment_text)
    EditText commentEditText;

    @Inject
    RemoteApi remoteApi;

    @Inject private SyncManager syncManager;


    private static final int URL_LOADER = 1919;

    @Override public void onStart() {
        Log.i(TAG, "OnStart");
        super.onStart();
        syncManager.startCommentSyncForFeed(feed);

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

        getLoaderManager().initLoader(URL_LOADER, null, this);
        commentListView.setAdapter(mAdapter);

        commentSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = commentEditText.getText().toString();
                remoteApi.createFeedComment(feed.getServerId(), content).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(res -> {
                            commentEditText.setText("");
                            commentEditText.setHint("Comment..");
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
            }
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
}
