
package com.um.launcher.logic.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;

import com.um.launcher.R;
import com.um.launcher.logic.factory.InterfaceLogic;
import com.um.launcher.model.WidgetType;

/**
 * system info
 *
 * @author wangchuanjian
 */
public class SystemInfoLogic implements InterfaceLogic {

    private Context mContext;

    public SystemInfoLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        WidgetType mInfo = null;
        // CPU Performance
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.system_info)[0]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // Access to the CPU performance information time, the information of
        // setInfo
        mInfo.setInfo(res.getStringArray(R.array.system_info_value)[0]);
        mWidgetList.add(mInfo);
        // GPU Performance
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.system_info)[1]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // Access to the CPU performance information time, the information of
        // setInfo
        mInfo.setInfo(res.getStringArray(R.array.system_info_value)[1]);
        mWidgetList.add(mInfo);

        // Memory
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.system_info)[2]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // Access to the memory information time, the information of setInfo
        mInfo.setInfo(res.getStringArray(R.array.system_info_value)[2]);
        mWidgetList.add(mInfo);

        // Storage space
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.system_info)[3]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // Access to the storage of spatial information, the information of
        // setInfo
        mInfo.setInfo(res.getStringArray(R.array.system_info_value)[3]);
        mWidgetList.add(mInfo);

        // Android version
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.system_info)[4]);
        mInfo.setType(WidgetType.TYPE_TEXTVIEW);
        // Access to Android version information, the information of setInfo
        mInfo.setInfo(res.getStringArray(R.array.system_info_value)[4]);
        mWidgetList.add(mInfo);

        // Software version
        mInfo = new WidgetType();
        mInfo.setName(res.getStringArray(R.array.system_info)[5]);
        mInfo.setType(WidgetType.TYPE_LONG_TEXT);
        // Access to the software version information time, the information of
        // setInfo
        // mInfo.setInfo(res.getStringArray(R.array.system_info_value)[5]);
        mInfo.setInfo(Build.DISPLAY);
        mWidgetList.add(mInfo);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
    }

    @Override
    public void dismissDialog() {
        // TODO Auto-generated method stub
    }

}
