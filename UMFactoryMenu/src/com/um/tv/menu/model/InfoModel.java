package com.um.tv.menu.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class InfoModel extends Model {
    public String mDisplayName = "Info";
    public String[] mItemNames = Utils.ItemsInfo;

    public InfoModel(Context context, FactoryWindow window, CusFactory factory) {
        super(context, window, factory);
        mName = mDisplayName;

        initChildren();
    }

    private void initChildren() {
        CommandModel swversionModel = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeSWVersion);
        swversionModel.mName = mItemNames[0];
        addChild(swversionModel);

        CommandModel boardModel = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeBoard);
        boardModel.mName = mItemNames[1];
        addChild(boardModel);

        CommandModel panelModel = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypePanel);
        panelModel.mName = mItemNames[2];
        addChild(panelModel);

        CommandModel mainpqversionModel = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeMainPQVersion);
        mainpqversionModel.mName = mItemNames[3];
        addChild(mainpqversionModel);

        CommandModel subpqversionModel = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeSubPQVersion);
        subpqversionModel.mName = mItemNames[4];
        addChild(subpqversionModel);

        CommandModel dateModel = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeDate);
        dateModel.mName = mItemNames[5];
        addChild(dateModel);

        CommandModel timeModel = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeTime);
        timeModel.mName = mItemNames[6];
        addChild(timeModel);
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
