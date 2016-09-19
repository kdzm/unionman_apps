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

import com.hisilicon.android.tvapi.constant.EnumPictureDemo;
import cn.com.unionman.umtvsetting.sound.R;

/**
 * MemcLogic
 *
 * @author wangchuanjian
 *
 */
public class MEMCLogic implements InterfaceLogic {
    private static final String TAG = "MEMCLogic";
    private Context mContext;

    // private WidgetType mMEMClevel;// Memclevel
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mMEMClevelValue = InterfaceValueMaps.MEMC_level;

    public MEMCLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // MEMCLevel
        WidgetType mMEMCLevel = new WidgetType();
        // set name for MEMCLevel
        mMEMCLevel.setName(res.getStringArray(R.array.pic_setting)[2]);
        // set type for MEMCLevel
        mMEMCLevel.setType(WidgetType.TYPE_SELECTOR);
        mMEMCLevel.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setSysValue--->i:" + i + "    value:"
                            + InterfaceValueMaps.MEMC_level[i][0]);
                }
                if (PictureInterface.getDemoMode(EnumPictureDemo.DEMO_MEMC))
                    PictureInterface.setDemoMode(EnumPictureDemo.DEMO_MEMC,
                            false);
                return PictureInterface
                        .setMEMCLevel(InterfaceValueMaps.MEMC_level[i][0]);

            }

            @Override
            public int getSysValue() {
                int mode = PictureInterface.getMEMCLevel();
                if (Constant.LOG_TAG) {
                    Log.d(TAG,
                            "getSysValue--->mode:"
                                    + mode
                                    + "    level:"
                                    + InterfaceValueMaps.MEMC_level
                                    + "    value:"
                                    + Util.getIndexFromArray(mode,
                                            InterfaceValueMaps.MEMC_level));
                }
                return Util.getIndexFromArray(mode,
                        InterfaceValueMaps.MEMC_level);
            }
        });
        // set data for MEMCLevel
        mMEMCLevel.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.MEMC_level));
        mWidgetList.add(mMEMCLevel);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
