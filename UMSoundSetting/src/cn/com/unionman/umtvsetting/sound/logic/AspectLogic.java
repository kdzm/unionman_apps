package cn.com.unionman.umtvsetting.sound.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import cn.com.unionman.umtvsetting.sound.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.sound.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.sound.interfaces.SourceManagerInterface;
import cn.com.unionman.umtvsetting.sound.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.sound.model.WidgetType;
import cn.com.unionman.umtvsetting.sound.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.sound.util.Constant;
import cn.com.unionman.umtvsetting.sound.util.Util;

import cn.com.unionman.umtvsetting.sound.R;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;

/**
 * AspectLogic
 *
 * @author wangchuanjian
 *
 */
public class AspectLogic implements InterfaceLogic {

    private static final String TAG = "AspectLogic";
    private Context mContext;

    // private WidgetType mAspect;// Aspect
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mPictureAspectValue = InterfaceValueMaps.picture_aspect;


    public AspectLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        int curSourceIdx = SourceManagerInterface.getCurSourceId();

        // Aspect
        WidgetType mAspect = new WidgetType();
        // set name for Aspect
        mAspect.setName(res.getStringArray(R.array.pic_setting)[5]);
        // set type for Aspect
        mAspect.setType(WidgetType.TYPE_SELECTOR);
        mAspect.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                int res = 0;
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setSysValue i = " + i);
                }
                int curSourceIdx = SourceManagerInterface.getCurSourceId();

                if(isHDSignal()){
                    if(curSourceIdx == EnumSourceIndex.SOURCE_VGA)
                        res = PictureInterface.setAspect(InterfaceValueMaps.vga_aspect[i][0], false);
                    else
                        res = PictureInterface.setAspect(InterfaceValueMaps.picturehd_aspect[i][0], false);
                }else{
                    if(curSourceIdx == EnumSourceIndex.SOURCE_VGA)
                        res = PictureInterface.setAspect(InterfaceValueMaps.vga_aspect[i][0], false);
                    else
                        res = PictureInterface.setAspect(InterfaceValueMaps.picture_aspect[i][0], false);
                }

                return res;
            }

            @Override
            public int getSysValue() {
                int res = 0;
                int mode = PictureInterface.getAspect();
                int curSourceIdx = SourceManagerInterface.getCurSourceId();
                if(isHDSignal()){
                    if(curSourceIdx == EnumSourceIndex.SOURCE_VGA)
                        res = Util.getIndexFromArray(mode, InterfaceValueMaps.vga_aspect);
                    else
                        res = Util.getIndexFromArray(mode, InterfaceValueMaps.picturehd_aspect);
                }else{
                    if(curSourceIdx == EnumSourceIndex.SOURCE_VGA)
                        res = Util.getIndexFromArray(mode, InterfaceValueMaps.vga_aspect);
                    else
                        res = Util.getIndexFromArray(mode, InterfaceValueMaps.picture_aspect);
                }
                return res;

            }

        });

        if(isHDSignal()){
            if(curSourceIdx == EnumSourceIndex.SOURCE_VGA)
                mAspect.setData(Util.createArrayOfParameters(InterfaceValueMaps.vga_aspect));
            else
                mAspect.setData(Util.createArrayOfParameters(InterfaceValueMaps.picturehd_aspect));
        }else{
            if(curSourceIdx == EnumSourceIndex.SOURCE_VGA)
                mAspect.setData(Util.createArrayOfParameters(InterfaceValueMaps.vga_aspect));
            else
                mAspect.setData(Util.createArrayOfParameters(InterfaceValueMaps.picture_aspect));
        }
        mWidgetList.add(mAspect);
        return mWidgetList;
    }



    public boolean isHDSignal(){

        int curSourceIdx = SourceManagerInterface.getCurSourceId();
        TimingInfo timing = SourceManagerInterface.getTimingInfo();
        int height = timing.getiHeight();
        if(curSourceIdx == EnumSourceIndex.SOURCE_ATV || curSourceIdx == EnumSourceIndex.SOURCE_CVBS1
            ||curSourceIdx == EnumSourceIndex.SOURCE_CVBS2){

            return false;
        }else if(height >720){
            return true;
        }
        else{
            return false;
        }

    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
