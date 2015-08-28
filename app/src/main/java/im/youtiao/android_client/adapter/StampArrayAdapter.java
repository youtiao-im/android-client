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
import im.youtiao.android_client.model.Stamp;
import im.youtiao.android_client.util.Log;
import im.youtiao.android_client.util.TimeWrap;

public class StampArrayAdapter extends ArrayAdapter<Stamp> {

    private int resourceId;
    private Context mContext;
    private String currentUserId;
    public StampArrayAdapter(Context context, int resource, LinkedList<Stamp> objects, String currentUserId) {
        super(context, resource, objects);
        this.resourceId = resource;
        this.mContext = context;
        this.currentUserId = currentUserId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(resourceId, null);
            viewHolder.userNameText = (TextView)convertView.findViewById(R.id.tv_user_name);
            viewHolder.stampStatusIv = (ImageView)convertView.findViewById(R.id.iv_stamp_status);
            viewHolder.createdAtTv = (TextView)convertView.findViewById(R.id.tv_created_at);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Stamp stamp = getItem(position);
        if (stamp.createdById.equalsIgnoreCase(this.currentUserId)) {
            viewHolder.userNameText.setText(mContext.getResources().getString(R.string.stamp_by_myself));
        } else {
            viewHolder.userNameText.setText(stamp.createdBy.name);
        }
        viewHolder.createdAtTv.setText((TimeWrap.wrapTimeDisplyValue(Math.round(1000 * Double.parseDouble(stamp.createdAt)), mContext)));
        if (stamp != null && stamp.symbol != null) {
            switch (Stamp.Mark.valueOf(stamp.symbol.toUpperCase())) {
                case CHECK:
                    viewHolder.stampStatusIv.setImageResource(R.mipmap.check_filled);
                    viewHolder.stampStatusIv.setColorFilter(mContext.getResources().getColor(R.color.icon_stamp_check_selected_color));
                    break;
                case CROSS:
                    viewHolder.stampStatusIv.setImageResource(R.mipmap.cross_filled);
                    viewHolder.stampStatusIv.setColorFilter(mContext.getResources().getColor(R.color.icon_stamp_cross_selected_color));
                    break;
                case EYE:
                    viewHolder.stampStatusIv.setImageResource(R.mipmap.eye_filled);
                    viewHolder.stampStatusIv.setColorFilter(mContext.getResources().getColor(R.color.icon_stamp_eye_selected_color));
                default:
            }
        }
        return convertView;
    }

    static class ViewHolder {
        public TextView userNameText;
        public ImageView stampStatusIv;
        public TextView createdAtTv;
    }
}
