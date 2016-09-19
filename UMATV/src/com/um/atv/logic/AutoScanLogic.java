package com.um.atv.logic;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.um.atv.AutoScanActivity;
import com.um.atv.logic.factory.InterfaceLogic;
import com.um.atv.model.WidgetType;

/**
 * AutoScanLogic
 *
 * @author wangchuanjian
 *
 */
public class AutoScanLogic implements InterfaceLogic {
    // private Context mContext;
    private List<WidgetType> mWidgetList = null;

    public AutoScanLogic(Context mContext) {
        super();
        // this.mContext = mContext;
        Intent intent = new Intent(mContext, AutoScanActivity.class);
        mContext.startActivity(intent);
        ((Activity)mContext).finish();

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
