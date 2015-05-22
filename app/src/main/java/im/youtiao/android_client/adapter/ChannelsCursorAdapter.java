package im.youtiao.android_client.adapter;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ImageButton;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import java.util.zip.Inflater;

import im.youtiao.android_client.provider.ChannelContentProvider;
import im.youtiao.android_client.provider.StatusFlag;

import im.youtiao.android_client.R;

public class ChannelsCursorAdapter extends CursorTreeAdapter {

    private static final String TAG = ChannelsCursorAdapter.class
            .getCanonicalName();

    private LayoutInflater mInflater;
    private Activity mActivity;
;

    private static final String[] PROJECTION_IDS_TITLE_AND_STATUS = new String[]{
            ChannelContentProvider.COLUMN_ID, ChannelContentProvider.COLUMN_NAME,
            ChannelContentProvider.COLUMN_STATUS_FLAG};

    private static final String[] PROJECTION_ROLES = new String[]{
            ChannelContentProvider.COLUMN_ROLE};

    public ChannelsCursorAdapter(Activity activity) {
        super(getGroupCursor(activity), activity);
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
    }

    private static Cursor getGroupCursor(Activity activity) {
        String[] columns = new String[] { "_id", "role" };

        MatrixCursor matrixCursor= new MatrixCursor(columns);
        activity.startManagingCursor(matrixCursor);

        matrixCursor.addRow(new Object[]{ 1, "owner"});
        matrixCursor.addRow(new Object[]{ 2, "member"});
        return matrixCursor;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        Log.e(TAG, "get ChildrenCursor");
        String role = groupCursor.getString(groupCursor.getColumnIndex("role"));
        Cursor c =  mActivity.getContentResolver().query(ChannelContentProvider.CONTENT_URI,
                PROJECTION_IDS_TITLE_AND_STATUS,
                ChannelContentProvider.COLUMN_ROLE + " = '"
                        + role + "'", null,
                ChannelContentProvider.DEFAULT_SORT_ORDER);
        return c;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_group_channels, null);
        GroupViewHolder holder = new GroupViewHolder();
        holder.groupName = (TextView) view.findViewById(R.id.group_channels_name);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        final GroupViewHolder holder = (GroupViewHolder) view.getTag();
        holder.groupName.setText(cursor.getString(cursor.getColumnIndex("role")));
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_channel, null);
        ChannelViewHolder holder = new ChannelViewHolder();
        holder.name = (TextView) view.findViewById(R.id.channel_name);
        holder.creator = (TextView) view.findViewById(R.id.creator_name);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        final ChannelViewHolder holder = (ChannelViewHolder) view.getTag();
        holder.name.setText(cursor.getString(cursor.getColumnIndex(ChannelContentProvider.COLUMN_NAME)));
    }

    static class GroupViewHolder {
        public TextView groupName;
    }

    static class ChannelViewHolder {
        TextView name;
        TextView creator;
    }
}
