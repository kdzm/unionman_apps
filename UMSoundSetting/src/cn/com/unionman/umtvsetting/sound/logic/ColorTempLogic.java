package cn.com.unionman.umtvsetting.sound.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import cn.com.unionman.umtvsetting.sound.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.sound.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.sound.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.sound.model.WidgetType;
import cn.com.unionman.umtvsetting.sound.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.sound.util.Constant;
import cn.com.unionman.umtvsetting.sound.util.Util;

import cn.com.unionman.umtvsetting.sound.R;

/**
 * Color Temp
 *
 * @author wangchuanjian
 *
 */
public class ColorTempLogic implements InterfaceLogic {

    private static final String TAG = "ColorTempLogic";
    private Context mContext;

    // private WidgetType mPictureClrtmp;
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mPictureClrtmpValue = InterfaceValueMaps.picture_clrtmp;

    public ColorTempLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "getWidgetTypeList() ");
        }
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // ColorTemp
        WidgetType mPictureClrtmp = new WidgetType();
        // set name for ColorTemp
        mPictureClrtmp
                .setName(res.getStringArray(R.array.color_temp_setting)[0]);
        // set type for ColorTemp
        mPictureClrtmp.setType(WidgetType.TYPE_SELECTOR);
        mPictureClrtmp
                .setmAccessSysValueInterface(new AccessSysValueInterface() {

                    @Override
                    public int setSysValue(int i) {
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "setSysValue i = " + i);
                        }
                        int ret = PictureInterface
                                .setColorTemp(InterfaceValueMaps.picture_clrtmp[i][0]);
                        return ret;
                    }

                    @Override
                    public int getSysValue() {
                        int color = PictureInterface.getColorTemp();
                        if (Constant.LOG_TAG) {
                            Log.d(TAG, "getColorTempmode = " + color);
                        }
                        return Util.getIndexFromArray(color,
                                InterfaceValueMaps.picture_clrtmp);
                    }
                });
        // set data for ColorTemp
        mPictureClrtmp.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.picture_clrtmp));
        mWidgetList.add(mPictureClrtmp);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub
    }
}
