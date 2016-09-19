package com.um.atv.adapter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.hisilicon.android.tvapi.vo.TvProgram;
import com.um.atv.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class TvlistAdapter extends BaseAdapter {
    private String TAG = "RecentAdapter";
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<TvProgram> tvChannelList = new ArrayList<TvProgram>();
    private ArrayList<Integer> List = new ArrayList<Integer>();

    private class RecentViewHolder {
        TextView channelNumTxt;
        TextView channelNameTxt;
    }

    public TvlistAdapter(Context c, ArrayList<TvProgram> tvList) {
        this.mContext = c;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tvChannelList = tvList;

    }
    public void setChannelList(ArrayList<TvProgram> tvList)
    {
        this.tvChannelList = tvList;
    }
    public int getCount() {
        return tvChannelList.size();
    }
    @Override
    public Object getItem(int position) {
        return tvChannelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @SuppressWarnings("rawtypes")
    public ArrayList getChannelList()
    {
        return tvChannelList;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        RecentViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tvch_info_item, null);
            holder = new RecentViewHolder();
            holder.channelNameTxt = (TextView) convertView
                    .findViewById(R.id.channel_name_txt);
            holder.channelNumTxt = (TextView) convertView
                    .findViewById(R.id.channel_num_txt);
            convertView.setTag(holder);
        } else {
            holder = (RecentViewHolder) convertView.getTag();
        }
        TvProgram  info = tvChannelList.get(position);
        if (null != info)
        {
            DecimalFormat format = new DecimalFormat("000");
            // Set position as channel show index
            String number = format.format(info.getiId());
            holder.channelNumTxt.setText(number);

            String name = info.getStrName();
            holder.channelNameTxt.setText(name);
            int ChannelID = info.getiId();
            Log.d(TAG,"ChannelID = "+ ChannelID + " name = "+ name);
        }
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                notifyDataSetChanged();
            }
        });
   
        return convertView;
    }
}
