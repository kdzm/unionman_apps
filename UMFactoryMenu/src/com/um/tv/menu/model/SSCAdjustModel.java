package com.um.tv.menu.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.R;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class SSCAdjustModel extends Model {
    public String[] mItemNames = Utils.ItemsSSCAdjust;

    public SSCAdjustModel(Context context, FactoryWindow window,
                          CusFactory factory) {
        super(context, window, factory);
        mName = Utils.DisplayNameSSCAdjust;

        initChildren();
    }

    private void initChildren() {
    	VbyOneModel vbyone = new VbyOneModel(mContext, mWindow, mFactory);
    	vbyone.mName = mItemNames[0];
    	addChild(vbyone);
    	
    	LVDSModel lvbs = new LVDSModel(mContext, mWindow, mFactory);
    	lvbs.mName = mItemNames[1];
    	addChild(lvbs);
    	
    	DDRModel ddr = new DDRModel(mContext, mWindow, mFactory);
    	ddr.mName = mItemNames[2];
    	addChild(ddr);
    	
    	GMacModel gmac = new GMacModel(mContext, mWindow, mFactory);
    	gmac.mName = mItemNames[3];
    	addChild(gmac);
    	
//    	UsbModel usb = new UsbModel(mContext, mWindow, mFactory);
//    	usb.mName = mItemNames[4];
//    	addChild(usb);
    	
//    	PLLModel pll = new PLLModel(mContext, mWindow, mFactory);
//    	pll.mName = mItemNames[5];
//    	addChild(pll);
    	
    	CIModel ci = new CIModel(mContext, mWindow, mFactory);
    	ci.mName = mItemNames[6];
    	addChild(ci);
    	
    	VDACModel vdac = new VDACModel(mContext, mWindow, mFactory);
    	vdac.mName = mItemNames[7];
    	addChild(vdac);
    	
    }

    @Override
    public View getView(Context context, int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null || !convertView.getTag().equals(ViewTagMain)) {
            convertView = LayoutInflater.from(context).inflate(R.layout.factory_menu_list_item, null);
            convertView.setTag(ViewTagMain);
        }
        TextView main = (TextView)convertView;
        main.setText(mChildrenList.get(position).mName);
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
    	mChildrenList.get(position).onItemClick(parent, view, position, id);
    }

    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub
        mChildrenList.get(position).changeValue(direct, position, view);
    }
}
