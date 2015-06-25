package im.youtiao.android_client.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

import im.youtiao.android_client.R;
import im.youtiao.android_client.model.Comment;

public class CommentArrayAdapter extends ArrayAdapter<Comment> {

    private int resourceId;
    private Context mContext;
    public CommentArrayAdapter(Context context, int resource, LinkedList<Comment> objects) {
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
            viewHolder.creatorNameTv = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.createdAtTv = (TextView)convertView.findViewById(R.id.tv_created_at);
            viewHolder.commentContentTv = (TextView)convertView.findViewById(R.id.tv_user_alias);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Comment comment = getItem(position);
        viewHolder.creatorNameTv.setText(comment.createdBy.name);
        viewHolder.commentContentTv.setText(comment.text);
        viewHolder.createdAtTv.setText("3 mins ago");
        return convertView;
    }

    static class ViewHolder {
        public TextView creatorNameTv;
        public TextView commentContentTv;
        public TextView createdAtTv;
    }
}
