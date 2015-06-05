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


public class ChannelAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private Activity mActivity;

    private static final String TAG = ChannelAdapter.class
            .getCanonicalName();

    public ChannelAdapter(Activity activity, Cursor cursor) {
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
        holder.separator = (TextView) view.findViewById(R.id.separator);
        holder.name = (TextView) view.findViewById(R.id.channel_name);
        holder.creator = (TextView) view.findViewById(R.id.creator_name);
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
            holder.separator.setText("owner".equalsIgnoreCase(role) ? "My Channels" : "Joined Channels");
            holder.separator.setVisibility(View.VISIBLE);
        } else {
            holder.separator.setVisibility(View.GONE);
        }
        holder.name.setText(name);
    }

    private static class ViewHolder {
        TextView separator;
        TextView name;
        TextView creator;
    }

}
