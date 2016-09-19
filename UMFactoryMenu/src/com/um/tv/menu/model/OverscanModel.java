package com.um.tv.menu.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class OverscanModel extends Model {
    public String[] mItemNames = Utils.ItemsOverscan;

    public OverscanModel(Context context, FactoryWindow window, CusFactory factory) {
        super(context, window, factory);
        mName = Utils.DisplayNameOverscan;

        initChildren();
    }

    private void initChildren() {
        ChoiceModel sourceModel = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeOverscanSource);
        sourceModel.mName = mItemNames[0];
        addChild(sourceModel);

        RangeModel hsizeModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeOverscanHSize);
        hsizeModel.mName = mItemNames[1];
        sourceModel.registeSourceChangeListener(hsizeModel.getSourceChangeListener());
        addChild(hsizeModel);

        RangeModel hpositionModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeOverscanHPosition);
        hpositionModel.mName = mItemNames[2];
        sourceModel.registeSourceChangeListener(hpositionModel.getSourceChangeListener());
        addChild(hpositionModel);

        RangeModel vsizeModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeOverscanVSize);
        vsizeModel.mName = mItemNames[3];
        sourceModel.registeSourceChangeListener(vsizeModel.getSourceChangeListener());
        addChild(vsizeModel);

        RangeModel vpositionModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeOverscanVPosition);
        vpositionModel.mName = mItemNames[4];
        sourceModel.registeSourceChangeListener(vpositionModel.getSourceChangeListener());
        addChild(vpositionModel);
        
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