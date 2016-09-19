package com.unionman.settings.menumanager;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import java.util.Map;

import com.unionman.settings.R;
import com.unionman.settings.tools.Logger;

public class MenuListAdapter extends BaseAdapter {
	private Context ctx;
	private List<Map<String, Object>> mData;
	private LayoutInflater mInflater;
	private int okPosition = -1;
	private static final String TAG="com.unionman.settings.menumamager-MenuListAdapter";
	private ViewHolder localViewHolder;
	private LinearLayout oldLayout;
	public MenuListAdapter(Context paramContext,List<Map<String, Object>> paramList) {
		this.ctx = paramContext;
		this.mData = paramList;
		this.mInflater = LayoutInflater.from(paramContext);
	}

	public int getCount() {
		return this.mData.size();
	}

	public Object getItem(int paramInt) {
		return this.mData.get(paramInt);
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public int getOkPosition() {
		return this.okPosition;
	}
//	int[] resImage = {R.drawable.devinfor,R.drawable.netset,R.drawable.netinfo,R.drawable.dataset};
	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		Logger.i(TAG, "getView okPosition="+okPosition+" position="+paramInt);
		if (paramView == null) {
			localViewHolder = new ViewHolder();
			paramView = this.mInflater.inflate(R.layout.menu_item, null);
			localViewHolder.title = ((TextView) paramView.findViewById(R.id.list_textid));
			localViewHolder.picture = (ImageView) paramView.findViewById(R.id.list_imgid);
			localViewHolder.layout = ((LinearLayout) paramView.findViewById(R.id.list_layout));
			paramView.setTag(localViewHolder);

			if (paramInt != this.okPosition){
//				localViewHolder.layout.setBackground(this.ctx.getResources().getDrawable(R.drawable.selector_menuitem));

				localViewHolder.title.setText((String) ((Map) this.mData.get(paramInt)).get("title"));
				localViewHolder.title.setTextColor(ctx.getResources().getColor(R.color.gray));
				localViewHolder.picture.setBackground(ctx.getResources().
						getDrawable((Integer) ((Map)this.mData.get(paramInt)).get("pic2")));

			}else{
				Logger.i(TAG, "oldLayout");
//				oldLayout=((LinearLayout) paramView.findViewById(R.id.list_layout));
//				localViewHolder.layout.setBackground(this.ctx.getResources().getDrawable(R.drawable.selector_menuitem_selected));
				localViewHolder.title.setText((String) ((Map) this.mData.get(paramInt)).get("title"));
				localViewHolder.title.setTextColor(ctx.getResources().getColor(R.color.white));
				localViewHolder.picture.setBackground(ctx.getResources().
						getDrawable((Integer) ((Map)this.mData.get(paramInt)).get("pic")));

			}
		}
		else {
			localViewHolder = (ViewHolder)paramView.getTag();
			if (paramInt != this.okPosition){
				localViewHolder.title.setText((String) ((Map) this.mData.get(paramInt)).get("title"));
				localViewHolder.title.setTextColor(ctx.getResources().getColor(R.color.gray));
				localViewHolder.picture.setBackground(ctx.getResources().
						getDrawable((Integer) ((Map)this.mData.get(paramInt)).get("pic2")));

//				localViewHolder.layout.setBackground(this.ctx.getResources().getDrawable(R.drawable.selector_menuitem));
			}else{
				localViewHolder.title.setText((String) ((Map) this.mData.get(paramInt)).get("title"));
				localViewHolder.title.setTextColor(ctx.getResources().getColor(R.color.white));
				localViewHolder.picture.setBackground(ctx.getResources().
						getDrawable((Integer) ((Map)this.mData.get(paramInt)).get("pic")));
//				Logger.i(TAG, "oldLayout");
//				oldLayout=((LinearLayout) paramView.findViewById(R.id.list_layout));
//				localViewHolder.layout.setBackground(this.ctx.getResources().getDrawable(R.drawable.selector_menuitem_selected));
			}
		}
		//localViewHolder = (ViewHolder) paramView.getTag();
//		localViewHolder.title.setText((String) ((Map) this.mData.get(paramInt)).get("title"));
//		localViewHolder.picture.setBackground(ctx.getResources().
//				getDrawable((Integer) ((Map)this.mData.get(paramInt)).get("pic")));

		return paramView;
	}

	public void setOkPosition(int paramInt) {
		this.okPosition = paramInt;
	}
	
	public void resetBackground() {
		Logger.i(TAG, "resetBackground");
		//oldLayout.setBackground(this.ctx.getResources().getDrawable(R.drawable.selector_menuitem));
		//oldLayout.setBackground(ctx.getResources().getDrawable(R.color.white));

	}

	public final class ViewHolder {
		public LinearLayout layout;
		public TextView title;
		public ImageView picture;
	}
}