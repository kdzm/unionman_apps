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

public class NextPageModel extends Model {
    public String[] mItemNames = Utils.ItemsPage2;

    public NextPageModel(Context context, FactoryWindow window, CusFactory factory) {
        super(context, window, factory);
        mName = Utils.DisplayNameNext;

        initChildren();
    }

    private void initChildren() {
    	CommandModel Pq = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypePqUpdate);
        Pq.mName = mItemNames[0];
        addChild(Pq);

        CommandModel Aq = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeAqUpdate);
        Aq.mName = mItemNames[1];
        addChild(Aq);

        CommandModel logo = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeLogoUpdate); 
        logo.mName = mItemNames[2];
        addChild(logo);

        CommandModel machine = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeMachineTypeUpdate);
        machine.mName = mItemNames[3];
        addChild(machine);
        
        CommandModel screen = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeScreenParamUpdate);
        screen.mName = mItemNames[3];
        addChild(screen);
        
        CommandModel sale = new CommandModel(mContext, mWindow, mFactory, CommandModel.TypeSalePicUpdate);
        sale.mName = mItemNames[3];
        addChild(sale);
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
    	mChildrenList.get(position).onItemClick(parent, view, position, id);
    }

    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub
        mChildrenList.get(position).changeValue(direct, position, view);
    }
}
