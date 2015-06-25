package im.youtiao.android_client.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import im.youtiao.android_client.R;
import im.youtiao.android_client.model.Comment;
import im.youtiao.android_client.model.Membership;

public class MemberArrayAdapter extends ArrayAdapter<Membership> {

    private int resourceId;
    private Context mContext;
    public MemberArrayAdapter(Context context, int resource, ArrayList<Membership> objects) {
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
            viewHolder.userAvatarIv = (ImageView)convertView.findViewById(R.id.iv_user_avatar);
            viewHolder.userNameTv = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.userAliasTv = (TextView)convertView.findViewById(R.id.tv_user_alias);
            viewHolder.joinDateTv = (TextView)convertView.findViewById(R.id.tv_join_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Membership membership = getItem(position);
        viewHolder.userNameTv.setText(membership.user.name);
        viewHolder.userAliasTv.setVisibility(View.GONE);  //TODO: add alias
        viewHolder.joinDateTv.setText("5/8/2015");
        return convertView;
    }

    static class ViewHolder {
        public ImageView userAvatarIv;
        public TextView userNameTv;
        public TextView userAliasTv;
        public TextView joinDateTv;
    }

}
