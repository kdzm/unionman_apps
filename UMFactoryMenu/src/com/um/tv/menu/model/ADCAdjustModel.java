package com.um.tv.menu.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class ADCAdjustModel extends Model {
    public String[] mItemNames = Utils.ItemsADC;

    public ADCAdjustModel(Context context, FactoryWindow window, CusFactory factory) {
        super(context, window, factory);
        mName = Utils.DisplayNameADC;

        initChildren();
    }

    private void initChildren() {
        ChoiceModel sourceModel = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeADCSource);
        sourceModel.mName = mItemNames[0];
        addChild(sourceModel);

        CommandModel setTuneModel = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeSetADC);
        setTuneModel.mName = mItemNames[1];
        addChild(setTuneModel);

        RangeModel rgainModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeADCRGain);
        rgainModel.mName = mItemNames[2];
        sourceModel.registeSourceChangeListener(rgainModel.getSourceChangeListener());
        setTuneModel.registerStatusChangeListener(rgainModel.getCommandChangeListener());
        addChild(rgainModel);

        RangeModel ggainModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeADCGGain);
        ggainModel.mName = mItemNames[3];
        sourceModel.registeSourceChangeListener(ggainModel.getSourceChangeListener());
        setTuneModel.registerStatusChangeListener(ggainModel.getCommandChangeListener());
        addChild(ggainModel);

        RangeModel bgainModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeADCBGain);
        bgainModel.mName = mItemNames[4];
        sourceModel.registeSourceChangeListener(bgainModel.getSourceChangeListener());
        setTuneModel.registerStatusChangeListener(bgainModel.getCommandChangeListener());
        addChild(bgainModel);

        RangeModel roffsetModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeADCROffset);
        roffsetModel.mName = mItemNames[5];
        sourceModel.registeSourceChangeListener(roffsetModel.getSourceChangeListener());
        setTuneModel.registerStatusChangeListener(roffsetModel.getCommandChangeListener());
        addChild(roffsetModel);

        RangeModel goffsetModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeADCGOffset);
        goffsetModel.mName = mItemNames[6];
        sourceModel.registeSourceChangeListener(goffsetModel.getSourceChangeListener());
        setTuneModel.registerStatusChangeListener(goffsetModel.getCommandChangeListener());
        addChild(goffsetModel);

        RangeModel boffsetModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeADCBOffset);
        boffsetModel.mName = mItemNames[7];
        sourceModel.registeSourceChangeListener(boffsetModel.getSourceChangeListener());
        setTuneModel.registerStatusChangeListener(boffsetModel.getCommandChangeListener());
        addChild(boffsetModel);
        
        sourceModel.initSourceDate();
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
        mChildrenList.get(position).onItemClick(parent, view, position, id);
    }

    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub
        mChildrenList.get(position).changeValue(direct, position, view);
    }
}
