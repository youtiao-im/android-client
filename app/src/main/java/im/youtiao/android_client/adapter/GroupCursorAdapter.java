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
import im.youtiao.android_client.dao.DaoSession;
import im.youtiao.android_client.dao.GroupHelper;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.wrap.GroupWrap;


public class GroupCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private Activity mActivity;
    private DaoSession daoSession;

    private static final String TAG = GroupCursorAdapter.class
            .getCanonicalName();

    @Inject
    public GroupCursorAdapter(Activity activity, DaoSession daoSession) {
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
        final View view = mInflater.inflate(R.layout.row_group, parent,
                false);
        ViewHolder holder = new ViewHolder();
        holder.separatorTv = (TextView) view.findViewById(R.id.tv_separator);
        holder.nameTv = (TextView) view.findViewById(R.id.tv_group_name);
        holder.creatorTv = (TextView) view.findViewById(R.id.tv_user_name);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.i(TAG, "bindView:" + this.getCount());
        final ViewHolder holder = (ViewHolder) view.getTag();

        boolean needSeparator = false;
        final int position = cursor.getPosition();


        final Group group = GroupWrap.wrap(GroupHelper.fromCursor(cursor));

        final String name = group.name;
        final String role = group.membership.role;
        if (position == 0) {
            needSeparator = true;
        } else {
            cursor.moveToPosition(position - 1);
            Group lastGroup = GroupWrap.wrap(GroupHelper.fromCursor(cursor));
            String lastRole = lastGroup.membership.role;
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
        //holder.creatorTv.setText(creator);
    }

    private static class ViewHolder {
        TextView separatorTv;
        TextView nameTv;
        TextView creatorTv;
    }

}
