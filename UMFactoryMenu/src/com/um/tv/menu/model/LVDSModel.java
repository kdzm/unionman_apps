package com.um.tv.menu.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class LVDSModel extends Model {
    public String mDisplayName = "LVDS";
    public String[] mItemNames = Utils.ItemsLVDS;

    public LVDSModel(Context context, FactoryWindow window, CusFactory factory) {
        super(context, window, factory);
        mName = mDisplayName;

        initChildren();
    }

    private void initChildren() {
    	ChoiceModel lvdsEnable = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeLvbsEnable);
    	lvdsEnable.mName = mItemNames[0];
    	addChild(lvdsEnable);
    	
    	ChoiceModel lvbsSpreadEnable = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeLvbsSpreadEnable);
    	lvbsSpreadEnable.mName = mItemNames[1];
    	addChild(lvbsSpreadEnable);
    	
    	RangeModel ratio = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeLvdsSpreadRatio);
    	ratio.mName = mItemNames[2];
    	addChild(ratio);
    	
    	RangeModel freq = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeLvdsSpreadFreq);
    	freq.mName = mItemNames[3];
    	addChild(freq);
    	
    	RangeModel current = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeLvdsDrvCurrent);
    	current.mName = mItemNames[4];
    	addChild(current);
    	
    	RangeModel voltage = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeLvdsComVoltage);
    	voltage.mName = mItemNames[5];
    	addChild(voltage);
    }

    @Override
    public View getView(Context context, int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return mChildrenList.get(position).getView(context, position, convertView, parent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
//        mChildrenList.get(position).onItemClick(parent, view, position, id);
        mWindow.updateItems(this);
    }

    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub
        mChildrenList.get(position).changeValue(direct, position, view);
    }
}