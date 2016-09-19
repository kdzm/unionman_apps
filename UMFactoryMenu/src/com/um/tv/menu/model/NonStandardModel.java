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

public class NonStandardModel extends Model {
    public String[] mItemNames = Utils.ItemsNonStandard;

    public NonStandardModel(Context context, FactoryWindow window, CusFactory factory) {
        super(context, window, factory);
        mName = Utils.DisplayNameNonStandard;

        initChildren();
    }

    private void initChildren() {
        CombinatedModel vif = new CombinatedModel(mContext, mWindow, mFactory, CombinatedModel.TypeVIF);
        vif.mName = mItemNames[0];
        addChild(vif);

        CombinatedModel vd = new CombinatedModel(mContext, mWindow, mFactory, CombinatedModel.TypeVD);
        vd.mName = mItemNames[1];
        addChild(vd);

        AudioModel audio = new AudioModel(mContext, mWindow, mFactory); 
        audio.mName = mItemNames[2];
        addChild(audio);

        CombinatedModel demod = new CombinatedModel(mContext, mWindow, mFactory, CombinatedModel.TypeDemod);
        demod.mName = mItemNames[3];
        addChild(demod);
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
