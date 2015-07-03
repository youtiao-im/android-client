package im.youtiao.android_client.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;


import java.util.LinkedList;

import de.greenrobot.event.EventBus;
import im.youtiao.android_client.R;
import im.youtiao.android_client.adapter.CommentArrayAdapter;
import im.youtiao.android_client.dao.BulletinHelper;
import im.youtiao.android_client.dao.DaoHelper;
import im.youtiao.android_client.dao.DaoMaster;
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.data.SyncManager;
import im.youtiao.android_client.event.BulletinStampEvent;
import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Comment;
import im.youtiao.android_client.model.Stamp;
import im.youtiao.android_client.rest.RemoteApi;
import im.youtiao.android_client.util.Logger;
import im.youtiao.android_client.wrap.BulletinWrap;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BulletinDetailActivity extends RoboActionBarActivity {
    private static final String TAG = BulletinDetailActivity.class.getCanonicalName();

    public static final String PARAM_BULLETIN = "current_bulletin";

    @Inject
    DaoSession daoSession;

    @InjectView(R.id.lv_comments)
    ListView commentsLv;
    @InjectView(R.id.btn_comment_submit)
    Button commentSubmitBtn;
    @InjectView(R.id.edtTxt_comment_content)
    EditText commentContentEdtText;

    @InjectView(R.id.tv_user_name)
    TextView bulletinCreatorNameTv;
    @InjectView(R.id.tv_created_at)
    TextView bulletinCreatedAtTv;
    @InjectView(R.id.tv_bulletin_text)
    TextView bulletinTextTv;
    @InjectView(R.id.tv_group_name)
    TextView bulletinGroupNameTv;
    @InjectView(R.id.tv_bulletin_comment_count)
    TextView bulletinCommentCountTv;
    @InjectView(R.id.tv_bulletin_checks_count)
    TextView bulletinChecksCountTv;
    @InjectView(R.id.tv_bulletin_crosses_count)
    TextView bulletinCrossesCountTv;
    @InjectView(R.id.imgBtn_bulletin_check)
    ImageButton checkImgBtn;
    @InjectView(R.id.imgBtn_bulletin_cross)
    ImageButton crossImgBtn;
    @InjectView(R.id.imgBtn_bulletin_comment)
    ImageButton commentImgBtn;
    @Inject
    RemoteApi remoteApi;
    @Inject
    private SyncManager syncManager;

    Bulletin bulletin;

    private LinkedList<Comment> comments = new LinkedList<Comment>();
    CommentArrayAdapter mAdapter;

    @Override
    public void onStart() {
        Log.i(TAG, "OnStart");
        super.onStart();
        EventBus.getDefault().register(this);
        loadComments();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    protected void initView(Bulletin bulletin) {
        bulletinCreatorNameTv.setText(bulletin.createdBy.name);
        bulletinTextTv.setText(bulletin.text);
        bulletinCreatedAtTv.setText("3 mins ago");
        bulletinGroupNameTv.setText("#" + bulletin.group.name);
        bulletinCommentCountTv.setText("" + bulletin.comments_count);
        bulletinCreatorNameTv.setText(bulletin.createdBy.name);
        bulletinTextTv.setText(bulletin.text);
        bulletinCreatedAtTv.setText("3 mins ago");
        bulletinGroupNameTv.setText("#" + bulletin.group.name);
        bulletinCommentCountTv.setText("" + bulletin.comments_count);
        bulletinChecksCountTv.setText("" + bulletin.checksCount);
        bulletinCrossesCountTv.setText("" + bulletin.crossesCount);

        checkImgBtn.setColorFilter(getResources().getColor(R.color.icon_unselected_color));
        crossImgBtn.setColorFilter(getResources().getColor(R.color.icon_unselected_color));
        commentImgBtn.setColorFilter(getResources().getColor(R.color.icon_unselected_color));
        if (bulletin.stamp != null && bulletin.stamp.symbol != null) {
            switch (Stamp.Mark.valueOf(bulletin.stamp.symbol.toUpperCase())) {
                case CHECK:
                    checkImgBtn.setColorFilter(getResources().getColor(R.color.icon_selected_color));
                    break;
                case CROSS:
                    crossImgBtn.setColorFilter(getResources().getColor(R.color.icon_selected_color));
                    break;
                default:
            }
        }

        checkImgBtn.setOnClickListener(v -> {
            Log.i(TAG, "checkImgBtn clicked");
            EventBus.getDefault().post(new BulletinStampEvent(bulletin, Stamp.Mark.CHECK.toString().toLowerCase()));

        });

        crossImgBtn.setOnClickListener(v -> {
            Log.i(TAG, "crossImgBtn clicked");
            EventBus.getDefault().post(new BulletinStampEvent(bulletin, Stamp.Mark.CROSS.toString().toLowerCase()));
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        bulletin = (Bulletin) intent.getSerializableExtra(PARAM_BULLETIN);

        initView(bulletin);
        mAdapter = new CommentArrayAdapter(this, R.layout.row_comment, comments);
        commentsLv.setAdapter(mAdapter);

        commentSubmitBtn.setOnClickListener(v -> {
            String content = commentContentEdtText.getText().toString();
            remoteApi.createComment(bulletin.id, content).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(res -> {
                        commentContentEdtText.setText("");
                        commentContentEdtText.setHint("Comment..");
                        comments.addFirst(res);
                        bulletinCommentCountTv.setText("" + comments.size());
                        mAdapter.notifyDataSetChanged();
                    }, Logger::logThrowable);

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bulletin_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadComments() {
        remoteApi.listComments(bulletin.id, null, 100)
                .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    for (Comment item : resp) {
                        comments.add(item);
                    }
                    mAdapter.notifyDataSetChanged();
                }, Logger::logThrowable);

    }

    public void onEventMainThread(BulletinStampEvent event) {
        final Bulletin bulletin = event.bulletin;
        final String symbol = event.symbol;
        remoteApi.markBulletin(bulletin.id, symbol.toLowerCase())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resp -> {
                    initView(resp);
                    DaoHelper.insertOrUpdate(daoSession, BulletinWrap.validate(resp));
                    getContentResolver().notifyChange(BulletinHelper.CONTENT_URI, null);
                }, Logger::logThrowable);
    }
}
