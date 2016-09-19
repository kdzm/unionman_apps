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


public class RecentAdapter extends BaseAdapter {
    private String TAG = "RecentAdapter";
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<TvProgram> tvChannelList = new ArrayList<TvProgram>();
    private ArrayList<Integer> List = new ArrayList<Integer>();
    private int moveFocusPosition = -1;
    private int reNamePosition = -1;
    private EditText  Nametext;
    private TextView channelName;
    private String NewChannelName;
    private class RecentViewHolder {
        TextView channelNumTxt;
        TextView channelNameTxt;
        ImageView skip_img;
        ImageView moveImg;
        EditText  reNametext;
    }

    public RecentAdapter(Context c, ArrayList<TvProgram> tvList) {
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
            convertView = mInflater.inflate(R.layout.app_info_item, null);
            holder = new RecentViewHolder();
            holder.channelNameTxt = (TextView) convertView
                    .findViewById(R.id.channel_name_txt);
            holder.channelNumTxt = (TextView) convertView
                    .findViewById(R.id.channel_num_txt);
            holder.moveImg = (ImageView) convertView
                    .findViewById(R.id.move_img);
            holder.skip_img = (ImageView) convertView.findViewById(R.id.skip_img);
            holder.reNametext = (EditText)convertView.findViewById(R.id.edit_channel_name_txt);
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
            Drawable moveIcon = mContext.getResources().getDrawable(
                    R.drawable.move);
            holder.moveImg.setImageDrawable(moveIcon);
            holder.moveImg.setVisibility(View.INVISIBLE);
            if(info.getStChannelAttr() != null && info.getStChannelAttr().isbSkip())
            {
                 holder.skip_img.setVisibility(View.VISIBLE);
                 holder.channelNumTxt.setTextColor(Color.RED);
                 holder.channelNameTxt.setTextColor(Color.RED);
            }else{
                 holder.skip_img.setVisibility(View.INVISIBLE);
                 holder.channelNumTxt.setTextColor(Color.WHITE);
                 holder.channelNameTxt.setTextColor(Color.WHITE);
            }
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
        if(reNamePosition == position)
        {
             holder.channelNameTxt.setVisibility(View.GONE);
             holder.reNametext.setVisibility(View.VISIBLE);
             channelName = holder.channelNameTxt;
             Nametext = holder.reNametext;
             Nametext.setFocusable(true);
             Nametext.requestFocus();
             Nametext.setText(info.getStrName());
             Nametext.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP )
                    {
                        Nametext.setFocusable(false);
                        NewChannelName = Nametext.getText().toString();
                        Nametext.setVisibility(View.GONE);
                        channelName.setVisibility(View.VISIBLE);
                    }
                    return false;
                }
            });
        }
        if (moveFocusPosition == position) {
            holder.moveImg.setVisibility(View.VISIBLE);
        } else {
            holder.moveImg.setVisibility(View.GONE);
        }
        return convertView;
    }
    public int getMoveFocusPosition() {
        return moveFocusPosition;
    }
    public void setMoveFocusPosition(int moveFocusPosition) {
        this.moveFocusPosition = moveFocusPosition;
    }
    public void setRenamePosition(int RenamePosition)
    {
        this.reNamePosition = RenamePosition ;
    }
    public String getNewName()
    {
        return NewChannelName;

    }
    public int getRenamePosition()
    {
        return this.reNamePosition;
    }
}
