package com.unionman.filebrowser.localplayer.adapter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.unionman.filebrowser.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceAdapter extends BaseAdapter {
	private static final String TAG = "LocalPlayer--GridviewBaseAdapter";
	private LayoutInflater mInflater;
	private List<Map<String, String>> groupmap;
	private Context context;

	public DeviceAdapter(List<Map<String, String>> groupmap,
			Context context) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.groupmap = groupmap;
		for (Map<String, String> map : groupmap) {
			for (Entry<String, String> entry : map.entrySet()) {
				Log.i(TAG, "groupmap entry.key=" + entry.getKey());
				Log.i(TAG, "groupmap entry.value=" + entry.getValue());
			}
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return groupmap.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		for (Map<String, String> map : groupmap) {
			for (Entry<String, String> entry : map.entrySet()) {
				Log.i(TAG, "groupmap entry.key=" + entry.getKey());
				Log.i(TAG, "groupmap entry.value=" + entry.getValue());
			}
		}

		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.device_item, null);
			holder = new ViewHolder();
			holder.mImageView = (ImageView) convertView.findViewById(R.id.iv_device_icon);
			holder.mTextView = (TextView) convertView.findViewById(R.id.tv_device_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		int type = Integer.parseInt(groupmap.get(position).get("mountType"));
		switch (type) {
		case 0:
			holder.mImageView.setImageResource(R.drawable.device_usb_bg);
			String str = groupmap.get(position).get("mountPath").toString();
			String name[] = str.split("\\/");
			holder.mTextView.setText(name[name.length - 1]);
			break;
		case 2:
			holder.mImageView.setImageResource(R.drawable.device_sdcard_bg);
			holder.mTextView.setText("sdcard");
			break;
		default:
			holder.mImageView.setImageResource(R.drawable.device_sdcard_bg);
			holder.mTextView.setText("sdcard");
			break;
		}
		return convertView;
	}

	public class ViewHolder {
		public ImageView mImageView;
		public TextView mTextView;
	}

}
