package com.um.atv.logic;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.um.atv.RecentActivity;
import com.um.atv.logic.factory.InterfaceLogic;
import com.um.atv.model.WidgetType;

/**
 * Channel editor
 *
 * @author wangchuanjian
 *
 */
public class ChannelEditLogic implements InterfaceLogic {
    // private static final String TAG = "ChannelEditorLogic";
    // private Context mContext;
    // private WidgetType mChannelEditor;// Channel editor
    private List<WidgetType> mWidgetList = null;

    public ChannelEditLogic(Context mContext) {
        super();
        Intent intent = new Intent(mContext, RecentActivity.class);
        mContext.startActivity(intent);
        // this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
