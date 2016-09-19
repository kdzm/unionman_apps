package com.um.tv.menu.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class WhiteBlanceModel extends Model {
    public String[] mItemNames = Utils.ItemsWhiteBlance;

    public WhiteBlanceModel(Context context, FactoryWindow window,
                            CusFactory factory) {
        super(context, window, factory);
        // TODO Auto-generated constructor stub
        mName = Utils.DisplayNameWhiteBalance;

        initChildren();
    }

    private void initChildren() {
        CommandModel sourceModel = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeWhiteBalanceSource);
        sourceModel.mName = "Source";
        addChild(sourceModel);

		ChoiceModel wbModel = new ChoiceModel(mContext, mWindow, mFactory, ChoiceModel.TypeWhiteBlanceColorTemp);
		wbModel.mName = "White Blance";
		addChild(wbModel);
        RangeModel rgainModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeWhiteBlanceRGain);
        rgainModel.mName = "R Gain";
        wbModel.registeSourceChangeListener(rgainModel.getSourceChangeListener());
        addChild(rgainModel);

        RangeModel ggainModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeWhiteBlanceGGain);
        ggainModel.mName = "G Gain";
        wbModel.registeSourceChangeListener(ggainModel.getSourceChangeListener());
        addChild(ggainModel);

        RangeModel bgainModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeWhiteBlanceBGain);
        bgainModel.mName = "B Gain";
        wbModel.registeSourceChangeListener(bgainModel.getSourceChangeListener());
        addChild(bgainModel);

        RangeModel roffsetModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeWhiteBlanceROffset);
        roffsetModel.mName = "R Offset";
        wbModel.registeSourceChangeListener(roffsetModel.getSourceChangeListener());
        addChild(roffsetModel);

        RangeModel goffsetModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeWhiteBlanceGOffset);
        goffsetModel.mName = "G Offset";
        wbModel.registeSourceChangeListener(goffsetModel.getSourceChangeListener());
        addChild(goffsetModel);

        RangeModel boffsetModel = new RangeModel(mContext, mWindow, mFactory, RangeModel.TypeWhiteBlanceBOffset);
        boffsetModel.mName = "B Offset";
        wbModel.registeSourceChangeListener(boffsetModel.getSourceChangeListener());
        addChild(boffsetModel);
        
        wbModel.initSourceDate();
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
    }

    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub
        mChildrenList.get(position).changeValue(direct, position, view);
    }
}
