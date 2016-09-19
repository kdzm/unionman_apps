package com.um.channelseditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChannelAdapter extends BaseAdapter {
	private final String TAG = ChannelAdapter.class.getSimpleName()+"----U668";
	private final boolean LOGE = true;
	
	private final String CHAN_NUM = "chan_num";
	private final String CHAN_NAME = "chan_name";
	
	private Context mContext;
	private List<Map<String, String>> mTempMaps;
	private int mSortingItemId = -1;
	private Map<String, ChannelInfo>  mChanInfo;
	
	public ChannelAdapter(Context context, int[] chanNums, String[] chanNames ,Map<String, ChannelInfo>  mChanMaps) {
		super();
		if(LOGE) Log.v(TAG, "construct a ChannelAdapter object");
		mContext = context;
		mChanInfo = mChanMaps;
		fillData(chanNums, chanNames);
	}
	
	private void fillData(int[] chanNums, String[] chanNames) {
		if(chanNums.length != chanNames.length) {
			Log.e(TAG, "chanNums.length != chanNames.length");
			return;
		}
		
		mTempMaps = new ArrayList<Map<String,String>>();
		Map<String, String> map;
		for (int i = 0; i < chanNames.length; i++) {
			map = new HashMap<String, String>();
			map.put(CHAN_NUM, String.valueOf(chanNums[i]));
			map.put(CHAN_NAME, chanNames[i]);
			mTempMaps.add(map);
		}
	}
	
	public String[] getChanNames() {
		String[] values = null;
		for (int i = 0; i < mTempMaps.size(); i++) {
			if (values == null) {
				values = new String[mTempMaps.size()];
			}
			values[i] = mTempMaps.get(i).get(CHAN_NAME);
		}
		return values;
	}
	
	public int[] getChanNums() {
		int[] values = null;
		for (int i = 0; i < mTempMaps.size(); i++) {
			if (values == null) {
				values = new int[mTempMaps.size()];
			}
			values[i] = Integer.parseInt(mTempMaps.get(i).get(CHAN_NUM));
		}
		return values;
	}
	
	public void refreshData(int[] chanNums, String[] chanNames, int sortingItemId) {
		mSortingItemId = sortingItemId;
		fillData(chanNums, chanNames);
		notifyDataSetChanged();
	}
	
	public void refreshData(int[] chanNums, String[] chanNames) {
		fillData(chanNums, chanNames);
		notifyDataSetChanged();
	}
	
	public void refreshData(int sortingItemId) {
		mSortingItemId = sortingItemId;
		notifyDataSetChanged();
	}
	
	public int getSortItemId() {
		return mSortingItemId;
	}
	
	public void refreshCancelSort() {
		mSortingItemId = -1;
		notifyDataSetChanged();
	}

	public class ViewHolder {
		public TextView chanNumTextView;
		public TextView chanNameTextView;
		public ImageView sortImageView;
		public ImageView favImageView;
		public ImageView delImageView;
		public ImageView hideImageView;
	}
	
	@Override
	public int getCount() {
		return mTempMaps.size();
	}

	@Override
	public Object getItem(int position) {
		return mTempMaps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		TextView chanNum, chanName;
//		View itemView = LayoutInflater.from(mContext).inflate(R.layout.chan_editor_list_item, null);
//		chanNum = (TextView) itemView.findViewById(R.id.chan_num_text_view);
//		chanName = (TextView) itemView.findViewById(R.id.chan_name_text_view);
//		chanNum.setText(mTempMaps.get(position).get(CHAN_NUM));
//		chanName.setText(mTempMaps.get(position).get(CHAN_NAME));
//		return itemView;

		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.chan_editor_list_item, null);
			viewHolder.chanNumTextView = (TextView) convertView.findViewById(R.id.chan_num_text_view);
			viewHolder.chanNameTextView = (TextView) convertView.findViewById(R.id.chan_name_text_view);
			viewHolder.sortImageView = (ImageView) convertView.findViewById(R.id.item_img_move);
			viewHolder.favImageView = (ImageView) convertView.findViewById(R.id.item_img_like);
			viewHolder.delImageView = (ImageView) convertView.findViewById(R.id.item_img_del);
			viewHolder.hideImageView = (ImageView) convertView.findViewById(R.id.item_img_hide);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String chanName = mTempMaps.get(position).get(CHAN_NAME);
		viewHolder.chanNumTextView.setText(mTempMaps.get(position).get(CHAN_NUM));
		viewHolder.chanNameTextView.setText(chanName);
		
		ChannelInfo chanInfo= mChanInfo.get(chanName);
		if(mSortingItemId == position)
		{
			viewHolder.sortImageView.setVisibility(View.VISIBLE);
		}
		else
		{
			viewHolder.sortImageView.setVisibility(View.INVISIBLE);	
		}
		
		if(chanInfo.getFav()==true)
		{
			viewHolder.favImageView.setVisibility(View.VISIBLE);	
		}
		else
		{
			viewHolder.favImageView.setVisibility(View.INVISIBLE);		
		}
		
		if(chanInfo.getHided()==true)
		{
			viewHolder.hideImageView.setVisibility(View.VISIBLE);	
		}
		else
		{
			viewHolder.hideImageView.setVisibility(View.INVISIBLE);		
		}
		
		if(chanInfo.getValid()==true)
		{
			viewHolder.delImageView.setVisibility(View.INVISIBLE);		
		}
		else
		{	
			viewHolder.delImageView.setVisibility(View.VISIBLE);	
		}
		
		return convertView;
	}
}
