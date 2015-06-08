package im.youtiao.android_client.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import im.youtiao.android_client.R;
import im.youtiao.android_client.greendao.Channel;
import im.youtiao.android_client.greendao.ChannelHelper;


public class ChannelCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private Activity mActivity;

    private static final String TAG = ChannelCursorAdapter.class
            .getCanonicalName();

    public ChannelCursorAdapter(Activity activity, Cursor cursor) {
        super(activity, cursor, false);
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        final Cursor c = getCursor();
    }

    @Override
    public void changeCursor(Cursor cursor) {
        Log.i(TAG, "changeCursor");
        super.changeCursor(cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.i(TAG, "newView");
        final View view = mInflater.inflate(R.layout.row_channel, parent,
                false);
        ViewHolder holder = new ViewHolder();
        holder.separatorTv = (TextView) view.findViewById(R.id.tv_separator);
        holder.nameTv = (TextView) view.findViewById(R.id.tv_channel_name);
        holder.creatorTv = (TextView) view.findViewById(R.id.tv_creator_name);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.i(TAG, "bindView");
        final ViewHolder holder = (ViewHolder) view.getTag();

        boolean needSeparator = false;
        final int position = cursor.getPosition();

        final Channel channel = ChannelHelper.fromCursor(cursor);

        final String name = channel.getName();
        final String role = channel.getRole();


        if (position == 0) {
            needSeparator = true;
        } else {
            cursor.moveToPosition(position - 1);
            Channel lastChannel = ChannelHelper.fromCursor(cursor);
            String lastRole = lastChannel.getRole();
            Log.i(TAG, "lastRole=" + lastRole + ", role=" + role);
            if (!lastRole.equalsIgnoreCase(role)) {
                Log.i(TAG, "set needSeparator be true");
                needSeparator = true;
            }
            cursor.moveToPosition(position);
        }

        if (needSeparator) {
            holder.separatorTv.setText("owner".equalsIgnoreCase(role) ? "My Channels" : "Joined Channels");
            holder.separatorTv.setVisibility(View.VISIBLE);
        } else {
            holder.separatorTv.setVisibility(View.GONE);
        }
        holder.nameTv.setText(name);
    }

    private static class ViewHolder {
        TextView separatorTv;
        TextView nameTv;
        TextView creatorTv;
    }

}
