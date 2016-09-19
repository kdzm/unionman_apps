package com.um.tv.menu.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class VbyOneModel extends Model {
    public String mDisplayName = "VbyOneModel";
    public String[] mItemNames = Utils.ItemsVBO;

    public VbyOneModel(Context context, FactoryWindow window, CusFactory factory) {
        super(context, window, factory);
        mName = mDisplayName;

        initChildren();
    }

    private void initChildren() {
        ChoiceModel vbo = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeVBOEnable);
        vbo.mName = mItemNames[0];
        addChild(vbo);

        ChoiceModel spread = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeVBOSpreadEnable);
        spread.mName = mItemNames[1];
        addChild(spread);
        
        RangeModel ratio = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeVBOSpreadRatio);
        ratio.mName = mItemNames[2];
        addChild(ratio);
        
        RangeModel freq = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeVBOSpreadFreq);
        freq.mName = mItemNames[3];
        addChild(freq);
        
        RangeModel drvCurrent = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeVBODrvCurrent);
        drvCurrent.mName = mItemNames[4];
        addChild(drvCurrent);

        ChoiceModel emphasis = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeVBOEmphasis);
        emphasis.mName = mItemNames[5];
        addChild(emphasis);
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
