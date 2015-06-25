package im.youtiao.android_client.adapter;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.DaoSession;


public class CommentCursorAdapter extends CursorAdapter {
    private static final String TAG = CommentCursorAdapter.class
            .getCanonicalName();
    private LayoutInflater mInflater;
    private Activity mActivity;
    private DaoSession daoSession;

    @Inject
    public CommentCursorAdapter(Activity activity, DaoSession daoSession) {
        super(activity, null, false);
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        this.daoSession = daoSession;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.row_comment, parent,
                false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.creatorNameTv = (TextView)view.findViewById(R.id.tv_user_name);
        viewHolder.createdAtTv = (TextView)view.findViewById(R.id.tv_created_at);
        viewHolder.commentContentTv = (TextView)view.findViewById(R.id.tv_user_alias);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
//        final ViewHolder viewHolder = (ViewHolder) view.getTag();
//        final Comment comment = CommentHelper.fromCursor(cursor);
//        comment.__setDaoSession(daoSession);
//        String email = comment.getUser().getEmail();
//        viewHolder.creatorNameTv.setText(email.substring(0, email.indexOf("@")));
//        viewHolder.commentContentTv.setText(comment.getText());
//        viewHolder.createdAtTv.setText("3 mins ago");
    }

    static class ViewHolder {
        public TextView creatorNameTv;
        public TextView commentContentTv;
        public TextView createdAtTv;
    }
}
