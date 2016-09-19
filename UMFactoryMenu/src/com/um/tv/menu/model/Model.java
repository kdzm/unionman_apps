package com.um.tv.menu.model;

import java.util.ArrayList;
import java.util.List;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.ui.FactoryWindow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public abstract class Model {
    public static final Object ViewTagMain = "view_main";
    public static final Object ViewTagChoice = "view_choice";
    public static final Object ViewTagCommand = "view_command";
    public static final Object ViewTagRange = "view_range";
    public static final Object ViewTagInput = "view_input";

    public String mName = null;
    public Model mParent = null;
    public List<Model> mChildrenList = new ArrayList<Model>();
    public abstract View getView(Context context, int position, View convertView, ViewGroup parent);
    public abstract void onItemClick(AdapterView<?> parent, View view, int position, long id);
    public abstract void changeValue(int direct, int position, View view);

    protected Context mContext = null;
    protected FactoryWindow mWindow = null;
    protected CusFactory mFactory = null;
    public Model(Context context, FactoryWindow window, CusFactory factory) {
        mContext = context;
        mWindow = window;
        mFactory = factory;
    }

    public void addChild(Model m) {
        m.mParent = this;
        mChildrenList.add(m);
    }

    public String getParentPath() {
        String path = "";
        while (mParent != null) {
            path = mParent.getParentPath() + "/" + mName;
        }
        return path;
    }

    public String getAbsolutePath() {
        return getParentPath() + "/" + mName;
    }
    
    public void init(){
    	if(mChildrenList != null){
    		for(Model m : mChildrenList){
    			m.init();
    		}
    	}
    }
}
