package com.um.tv.menu.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class FactorySingleKeyModel extends Model {
	public FactorySingleKeyModel(Context context, FactoryWindow window,
			CusFactory factory) {
		super(context, window, factory);
		// TODO Auto-generated constructor stub
		mName = Utils.DisplayNameFacSingleKey;
	}

	@Override
	public View getView(Context context, int position, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
        return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeValue(int direct, int position, View view) {
		// TODO Auto-generated method stub
		
	}
	
}
