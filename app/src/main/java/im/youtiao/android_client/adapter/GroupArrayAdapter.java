package im.youtiao.android_client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

import im.youtiao.android_client.R;
import im.youtiao.android_client.model.Group;

public class GroupArrayAdapter  extends ArrayAdapter<Group> {
    private int resourceId;
    private Context mContext;
    private int selectedIndex = -1;

    public GroupArrayAdapter(Context context, int resource, LinkedList<Group> objects) {
        super(context, resource, objects);
        this.resourceId = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(resourceId, null);
            viewHolder.groupNameTv = (TextView)convertView.findViewById(R.id.tv_group_name);
            viewHolder.groupMembersCountTv = (TextView)convertView.findViewById(R.id.tv_group_members_count);
            viewHolder.groupSelectedIv = (ImageView)convertView.findViewById(R.id.iv_group_selected);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Group group = getItem(position);
        viewHolder.groupNameTv.setText(group.name);
        viewHolder.groupMembersCountTv.setText(group.membershipsCount + " " + mContext.getString(R.string.subscribers));
        viewHolder.groupSelectedIv.setColorFilter(mContext.getResources().getColor(R.color.tab_icon_selected_color));
        if (position == selectedIndex) {
            viewHolder.groupSelectedIv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.groupSelectedIv.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;
    }

    static class ViewHolder {
        public TextView groupNameTv;
        public TextView groupMembersCountTv;
        public ImageView groupSelectedIv;
    }
}
