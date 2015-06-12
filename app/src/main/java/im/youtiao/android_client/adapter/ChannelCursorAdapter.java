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

import com.google.inject.Inject;

import im.youtiao.android_client.R;
import im.youtiao.android_client.greendao.Channel;
import im.youtiao.android_client.greendao.ChannelHelper;
import im.youtiao.android_client.greendao.DaoSession;


public class ChannelCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private Activity mActivity;
    private DaoSession daoSession;

    private static final String TAG = ChannelCursorAdapter.class
            .getCanonicalName();

    @Inject
    public ChannelCursorAdapter(Activity activity, DaoSession daoSession) {
        super(activity, null, false);
        this.mActivity = activity;
        this.mInflater = LayoutInflater.from(activity);
        this.daoSession = daoSession;
    }

    @Override
    public void changeCursor(Cursor cursor) {
        Log.i(TAG, "changeCursor");
        super.changeCursor(cursor);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.i(TAG, "newView:" + this.getCount());
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
        Log.i(TAG, "bindView:" + this.getCount());
        final ViewHolder holder = (ViewHolder) view.getTag();

        boolean needSeparator = false;
        final int position = cursor.getPosition();

        final Channel channel = ChannelHelper.fromCursor(cursor);
        channel.__setDaoSession(this.daoSession);

        final String name = channel.getName();
        final String role = channel.getRole();
        final String creator = channel.getUser().getEmail();


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
        holder.creatorTv.setText(creator);
    }

    private static class ViewHolder {
        TextView separatorTv;
        TextView nameTv;
        TextView creatorTv;
    }

}
