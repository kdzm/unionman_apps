package com.um.tv.menu.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class NonLinearModel extends Model {
    public String[] mItemNames = Utils.ItemsNonLinear;

    public NonLinearModel(Context context, FactoryWindow window, CusFactory factory) {
        super(context, window, factory);
        mName = Utils.DisplayNameNonLinear;

        initChildren();
    }

    private void initChildren() {
        ChoiceModel sourceModel = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeNonLinearSource);
        sourceModel.mName = mItemNames[0];
        addChild(sourceModel);

        RangeModel osd0Model = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeNonLinearOSD0);
        osd0Model.mName = mItemNames[1];
        sourceModel.registeSourceChangeListener(osd0Model.getSourceChangeListener());
        addChild(osd0Model);

        RangeModel osd25Model = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeNonLinearOSD25);
        osd25Model.mName = mItemNames[2];
        sourceModel.registeSourceChangeListener(osd25Model.getSourceChangeListener());
        addChild(osd25Model);

        RangeModel osd50Model = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeNonLinearOSD50);
        osd50Model.mName = mItemNames[3];
        sourceModel.registeSourceChangeListener(osd50Model.getSourceChangeListener());
        addChild(osd50Model);

        RangeModel osd75Model = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeNonLinearOSD75);
        osd75Model.mName = mItemNames[4];
        sourceModel.registeSourceChangeListener(osd75Model.getSourceChangeListener());
        addChild(osd75Model);

        RangeModel osd100Model = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeNonLinearOSD100);
        osd100Model.mName = mItemNames[5];
        sourceModel.registeSourceChangeListener(osd100Model.getSourceChangeListener());
        addChild(osd100Model);
        
        sourceModel.initSourceDate();
    }

    @Override
    public View getView(Context context, int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub
        return mChildrenList.get(position).getView(context, position, convertView, parent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub
        mChildrenList.get(position).changeValue(direct, position, view);
    }
}
