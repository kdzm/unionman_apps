package com.um.tv.menu.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class AudioModel extends Model {
    public String mDisplayName = "Audio";
    public String[] mItemNames = Utils.ItemsAudio;

    public AudioModel(Context context, FactoryWindow window, CusFactory factory) {
        super(context, window, factory);
        mName = mDisplayName;

        initChildren();
    }

    private void initChildren() {
        ChoiceModel nrThresholdModel = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeNRThreshold);
        nrThresholdModel.mName = mItemNames[0];
        addChild(nrThresholdModel);

        RangeModel avcThresholdModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeAVCThreshold);
        avcThresholdModel.mName = mItemNames[1];
        addChild(avcThresholdModel);

        ChoiceModel hiDEVTypeModel = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeHiDEVType);
        hiDEVTypeModel.mName = mItemNames[2];
        addChild(hiDEVTypeModel);

        ChoiceModel overmodulationModel = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeOverModulation);
        overmodulationModel.mName = mItemNames[3];
        addChild(overmodulationModel);

        InputModel frequencyoffsetModel = new InputModel(mContext, mWindow, mFactory, InputModel.TypeFrequencyOffset);
        frequencyoffsetModel.mName = mItemNames[4];
        addChild(frequencyoffsetModel);

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