package com.um.tv.menu.app;

import com.um.tv.menu.model.CombinatedModel;
import com.um.tv.menu.model.Model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyListAdapter extends BaseAdapter {
    private static final String TAG = "FactoryWindowAdapter";
    private Context mContext = null;
    private Model mModel = null;

    public MyListAdapter(Context context, CombinatedModel root) {
        mContext = context;
        mModel = root;
    }

    public void updateItems(Model m) {
        Log.d(TAG, "updateItems()--->");
        m.init();
        mModel = m;
        notifyDataSetChanged();
    }

    public Model getCurrentModel() {
        return mModel;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mModel.mChildrenList.size();
    }

    @Override
    public Object getItem(int position ) {
        // TODO Auto-generated method stub
        Log.d(TAG, "getItem--->position:" + position);
        if (position < 0 || position >= mModel.mChildrenList.size()) {
            return null;
        }
        return mModel.mChildrenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        Log.d(TAG, "getItemId--->position:" + position);
        return position;
    }

    private long lastTime = 0;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        long now = System.currentTimeMillis();
        Log.d(TAG, "getView--->position:" + position + "	view:" + convertView + " 	" + (now - lastTime));
        lastTime = now;
        return mModel.getView(mContext, position, convertView, parent);
    }
}
