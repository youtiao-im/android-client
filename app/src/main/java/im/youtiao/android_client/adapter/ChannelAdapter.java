package im.youtiao.android_client.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import im.youtiao.java_sdk.core.Channel;

public class ChannelAdapter extends ArrayAdapter<Channel> {

    private int res;
    private LayoutInflater layoutInflater;

    public ChannelAdapter(Context context, int resource, List<Channel> objects) {
        super(context, resource, objects);
        this.res = resource;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(res, null);
        }

        Channel channel = getItem(position);
        if (channel == null) {
            Log.d("FeedAdapter:", position + "");
        }

        //TODO: set channel item view
        return convertView;
    }

}
