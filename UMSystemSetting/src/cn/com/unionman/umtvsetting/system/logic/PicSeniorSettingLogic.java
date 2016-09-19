package cn.com.unionman.umtvsetting.system.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;


import cn.com.unionman.umtvsetting.system.R;
import cn.com.unionman.umtvsetting.system.interfaces.AudioInterface;
import cn.com.unionman.umtvsetting.system.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.system.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.system.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.system.model.WidgetType;
import cn.com.unionman.umtvsetting.system.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.system.util.Constant;
import cn.com.unionman.umtvsetting.system.util.Util;

import com.hisilicon.android.tvapi.constant.EnumPictureDemo;
/**
 * SoundModeLogic
 *
 * @author wangchuanjian
 *
 */
public class PicSeniorSettingLogic implements InterfaceLogic {

    private static final String TAG = "PicSeniorSettingLogic";
    private Context mContext;

    // private WidgetType mDNR;
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mDNRs = InterfaceValueMaps.auto_adjust;

    public PicSeniorSettingLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // PictureNR
        WidgetType mPictureNR = new WidgetType();
        // set name for PictureNR
        mPictureNR.setName(res.getStringArray(R.array.pic_setting)[3]);
        // set type for PictureNR
        mPictureNR.setType(WidgetType.TYPE_SELECTOR);
        mPictureNR.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "set nr value =" + i);
                }
                return PictureInterface.setNR(InterfaceValueMaps.NR[i][0]);
            }

            @Override
            public int getSysValue() {
                int mode = PictureInterface.getNR();
                return Util.getIndexFromArray(mode, InterfaceValueMaps.NR);
            }
        });
        // set data for PictureNR
        mPictureNR.setData(Util.createArrayOfParameters(InterfaceValueMaps.NR));
        mWidgetList.add(mPictureNR);
        
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
                
                if (i==4) {i=1;}
                
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
        
        
        // SR
        WidgetType mDemoSR = new WidgetType();
        // set name for SR
        mDemoSR.setName(res.getStringArray(R.array.demo_mode_setting)[2]);
        // set type for SR
        mDemoSR.setType(WidgetType.TYPE_SELECTOR);
        mDemoSR.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d("SRlogic", "set current SR mode :" + i);
                }
                return PictureInterface.setSRLevel(i);
            }

            @Override
            public int getSysValue() {
                int res = PictureInterface.getSRLevel();
                if (Constant.LOG_TAG) {
                    Log.d("SRlogic", "get current SR mode :" + res);
                }
                return res;
            }
        });
        // set data for SR
        mDemoSR.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.demo_SR));
        mWidgetList.add(mDemoSR);
        
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
