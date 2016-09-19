package com.um.atv.logic;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.um.atv.ManualScanActivity;
import com.um.atv.logic.factory.InterfaceLogic;
import com.um.atv.model.WidgetType;

/**
 * ManualScanLogic
 *
 * @author wangchuanjian
 *
 */
public class ManualScanLogic implements InterfaceLogic {

    // private Context mContext;
    private List<WidgetType> mWidgetList = null;

    public ManualScanLogic(Context mContext) {
        super();
        // this.mContext = mContext;
        Intent intent = new Intent(mContext, ManualScanActivity.class);
        mContext.startActivity(intent);
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
