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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Group group = getItem(position);
        viewHolder.groupNameTv.setText(group.name);
        return convertView;
    }

    static class ViewHolder {
        public TextView groupNameTv;
    }
}
