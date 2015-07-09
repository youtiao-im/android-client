package im.youtiao.android_client.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

import im.youtiao.android_client.R;
import im.youtiao.android_client.model.Comment;
import im.youtiao.android_client.model.Membership;

public class MemberArrayAdapter extends ArrayAdapter<Membership> {

    private int resourceId;
    private Context mContext;
    public MemberArrayAdapter(Context context, int resource, LinkedList<Membership> objects) {
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
            viewHolder.userNameTv = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.memberRoleTv = (TextView)convertView.findViewById(R.id.tv_role);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Membership membership = getItem(position);
        viewHolder.userNameTv.setText(membership.user.name);
        viewHolder.memberRoleTv.setText(membership.role.toString());
        return convertView;
    }

    static class ViewHolder {
        public TextView userNameTv;
        public TextView memberRoleTv;
    }

}