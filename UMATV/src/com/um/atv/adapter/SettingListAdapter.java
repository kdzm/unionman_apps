package com.um.atv.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hisilicon.android.tvapi.constant.Enum3DConstant;
import com.um.atv.R;
import com.um.atv.interfaces.ATVChannelInterface;
import com.um.atv.interfaces.AudioInterface;
import com.um.atv.interfaces.InterfaceValueMaps;
import com.um.atv.interfaces.Video3DInterface;
import com.um.atv.util.Constant;
import com.um.atv.util.Util;
import com.um.atv.widget.SettingLayout;

/**
 * one adapter ,use in setting layout
 *
 * @author wangchuanjian
 *
 */
public class SettingListAdapter extends BaseAdapter {
    private LayoutInflater mLinflater;
    private Context mContext;
    // parent view
    private View mParent;
    // data of setting menu
    private ArrayList<Map<String, String>> mSettingMenuData = new ArrayList<Map<String, String>>();

    public SettingListAdapter(Context context, View view) {
        this.mContext = context;
        this.mParent = view;
    }

    public void setList(String[] items) {
        mSettingMenuData.clear();
        Log.d("sla", "+++length :"+items.length);
        for (int i = 0; i < items.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("text", items[i]);
            mSettingMenuData.add(map);
        }
    }

    /**
     * get list of setting menu data
     *
     * @return
     */
    public ArrayList<Map<String, String>> getList() {
        return mSettingMenuData;
    }

    @Override
    public int getCount() {
        if (mSettingMenuData == null) {
            return 0;
        }
        return mSettingMenuData.size();
    }

    @Override
    public Object getItem(int position) {
        return mSettingMenuData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null) {
            mLinflater = LayoutInflater.from(mContext);
            view = mLinflater.inflate(R.layout.menu_item_view, null);
            viewHolder = new ViewHolder();
            viewHolder.colorTxt = (TextView) view
                    .findViewById(R.id.menu_item_color);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (mSettingMenuData != null && mSettingMenuData.size() > position) {
            Map<String, String> map = mSettingMenuData.get(position);
            viewHolder.colorTxt.setText(String.format(map.get("text")));
        }
        int[][] mSPDIFOutput = InterfaceValueMaps.SPDIF_output;
        int mode = AudioInterface.getSPDIFOutput();
        int mode3d = Video3DInterface.get3dMode();
        int index = Util.getIndexFromArray(mode, mSPDIFOutput);
        int foucsIndex = ((SettingLayout) mParent).getFocusIndex();
        if (foucsIndex == 1) {
            if (Constant.LOG_TAG) {
                Log.d(null, "spdifadapter==" + AudioInterface.getSPDIFOutput());
            }
            if (!AudioInterface.isSubWooferEnable() && position == 10) {
                viewHolder.colorTxt.setTextColor(Color.GRAY);
            } else if (index == 0 && position == 8) {
                viewHolder.colorTxt.setTextColor(Color.GRAY);
            } else {
                viewHolder.colorTxt.setTextColor(Color.WHITE);
            }
        } else if (foucsIndex == 2) {
            if ((mode3d == Enum3DConstant.TV_3DMODE_OFF)
                    && (position >= 1 && position <= 4)) {
                viewHolder.colorTxt.setTextColor(Color.GRAY);
            } else if((mode3d == Enum3DConstant.TV_3DMODE_2DT3D)
                      && position == 2) {
                viewHolder.colorTxt.setTextColor(Color.GRAY);
            }
            else if ((mode3d != Enum3DConstant.TV_3DMODE_SBS)
                    && (position == 1)) {
                viewHolder.colorTxt.setTextColor(Color.GRAY);
            } else if((mode3d == Enum3DConstant.TV_3DMODE_SBS
                      || mode3d == Enum3DConstant.TV_3DMODE_TAB
                      || mode3d == Enum3DConstant.TV_3DMODE_FP)
                      && position == 4){
                viewHolder.colorTxt.setTextColor(Color.GRAY);
            }
            else {
                viewHolder.colorTxt.setTextColor(Color.WHITE);
            }

        } else if(foucsIndex == 3){
            if((ATVChannelInterface.getAvailProgCount() == 0) && position == 3){
                viewHolder.colorTxt.setTextColor(Color.GRAY);
            }else{
                viewHolder.colorTxt.setTextColor(Color.WHITE);
            }
        } else {
            viewHolder.colorTxt.setTextColor(Color.WHITE);
        }
        return view;
    }

    /**
     * ViewHolder
     *
     * @author huyq
     *
     */
    private static class ViewHolder {
        // text of color
        public TextView colorTxt;
    }

}
