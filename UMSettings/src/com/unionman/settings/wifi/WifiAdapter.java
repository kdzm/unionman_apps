package com.unionman.settings.wifi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;
public class WifiAdapter extends BaseAdapter
{
  private Context ctx;
  private List<AccessPoint> mData;
  private LayoutInflater mInflater;

	public WifiAdapter(Context paramContext, List<AccessPoint> paramList)
	{
		mInflater = LayoutInflater.from(paramContext);
		ctx = paramContext;
		mData = paramList;
		UMDebug.d("WifiAdapter", "mData= "+ mData.size());
	}

	public int getCount()
	{
		return mData.size();
	}

	public List getData()
	{
		return mData;
	}

	public Object getItem(int paramInt)
	{  UMDebug.d("WifiAdapter", "getItem= "+ paramInt);
		return mData.get(paramInt);
	}

	public long getItemId(int paramInt)
	{UMDebug.d("WifiAdapter", "getItemId= "+ paramInt);
		return paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
	{
		UMDebug.d("WifiAdapter", "index= "+ paramInt);
		ViewHolder localViewHolder;
		if (paramView == null)
		{
			localViewHolder = new ViewHolder();
			paramView = this.mInflater.inflate(R.layout.wifi_list, null);
			localViewHolder.ssidname = ((TextView)paramView.findViewById(R.id.wifi_ssid));
			localViewHolder.imgLock = ((ImageView)paramView.findViewById(R.id.wifi_lock));
			localViewHolder.summory = ((TextView)paramView.findViewById(R.id.wifi_summory));
			paramView.setTag(localViewHolder);
		}
		else
		{
			localViewHolder = (ViewHolder)paramView.getTag();
		}
		AccessPoint localAccessPoint;
		{
			if(mData == null)
			{
				return paramView;
			}
//			if(paramInt >= mData.size())
//			{
//				return null;
//			}
			localAccessPoint = (AccessPoint)this.mData.get(paramInt);
//			umdebug.d("WifiAdapter","localAccessPoint="+localAccessPoint);
			localViewHolder.ssidname.setText(localAccessPoint.getSsid());
			if (localAccessPoint.getmRssi() != Integer.MAX_VALUE)
			{
				localViewHolder.imgLock.setImageDrawable(null);
				localViewHolder.summory.setText(localAccessPoint.getSummary());

			}
			//umdebug.umdebug_trace();
		}
		ImageView localImageView = localViewHolder.imgLock;
		if (localAccessPoint.getSecurity() != 0)
		{   
			int i = 2130837579;
			 localImageView.setImageResource(i);
			localViewHolder.imgLock.setImageLevel(localAccessPoint.getLevel());
		}else{
			 int i = 2130837580;
			 localImageView.setImageResource(i);
				localViewHolder.imgLock.setImageLevel(localAccessPoint.getLevel());
		}
		return paramView;
	}

	protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
	{
		Toast.makeText(this.ctx, "onListItemClick", 1).show();
	}

	public void showInfo()
	{
		Toast.makeText(this.ctx, "showInfo", 1).show();
	}

	public final class ViewHolder
	{
		public ImageView imgLock;
		public TextView ssidname;
		public TextView summory;

		public ViewHolder()
		{
		}
	}
}
