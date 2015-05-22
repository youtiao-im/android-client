package im.youtiao.android_client.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import im.youtiao.android_client.R;
import im.youtiao.android_client.dao.ChannelDAO;
import im.youtiao.android_client.provider.ChannelContentProvider;
import im.youtiao.android_client.provider.StatusFlag;


public class ChannelAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private final int mTitleIndex;
    private final int mInternalIdIndex;
    private final int mInternalStatusIndex;
    private Activity mActivity;

    private static final String[] PROJECTION_IDS_TITLE_AND_STATUS = new String[]{
            ChannelContentProvider.COLUMN_ID, ChannelContentProvider.COLUMN_NAME,
            ChannelContentProvider.COLUMN_STATUS_FLAG};

    public ChannelAdapter(Activity activity) {
        super(activity, getManagedCursor(activity), true);
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        final Cursor c = getCursor();

        mInternalIdIndex = c
                .getColumnIndexOrThrow(ChannelContentProvider.COLUMN_ID);
        mTitleIndex = c
                .getColumnIndexOrThrow(ChannelContentProvider.COLUMN_NAME);
        mInternalStatusIndex = c
                .getColumnIndexOrThrow(ChannelContentProvider.COLUMN_STATUS_FLAG);
    }

    private static Cursor getManagedCursor(Activity activity) {
        return activity.managedQuery(ChannelContentProvider.CONTENT_URI,
                PROJECTION_IDS_TITLE_AND_STATUS,
                ChannelContentProvider.COLUMN_STATUS_FLAG + " != "
                        + StatusFlag.DELETE, null,
                ChannelContentProvider.DEFAULT_SORT_ORDER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = mInflater.inflate(R.layout.row_channel, parent,
                false);
        ViewHolder holder = new ViewHolder();
        holder.name = (TextView) view.findViewById(R.id.channel_name);
        holder.creator = (TextView) view.findViewById(R.id.creator_name);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(cursor.getString(mTitleIndex));
        final int status = cursor.getInt(mInternalStatusIndex);
        final Long id = cursor.getLong(mInternalIdIndex);
    }

    private static class ViewHolder {
        TextView name;
        TextView creator;
    }

}
