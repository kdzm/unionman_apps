package cn.com.unionman.umtvsetting.picture.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.com.unionman.umtvsetting.picture.interfaces.ATVChannelInterface;
import cn.com.unionman.umtvsetting.picture.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.picture.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.picture.interfaces.SourceManagerInterface;
import cn.com.unionman.umtvsetting.picture.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.AccessProgressInterface;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.picture.util.Constant;
import cn.com.unionman.umtvsetting.picture.util.Util;

import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import cn.com.unionman.umtvsetting.picture.R;
import com.hisilicon.android.tvapi.constant.EnumColorSystem;

/**
 * PictureModeLogic
 *
 * @author wangchuanjian
 *
 */

public class SharpnessModeLogic implements InterfaceLogic {

    private static final String TAG = "PictureModeLogic";
    private Context mContext;
    // private List<WidgetType> mWidgetList = null;

    // private WidgetType mHiHue;

    private Handler mHandler;

    // private int[][] mPictureModeValue = InterfaceValueMaps.picture_mode;
    // private int[][] mColorSystemValue = InterfaceValueMaps.color_system;

    private int mBrightness = 0;
    private int mContrast = 0;
    private int mSharpness = 0;
    private int mSaturation = 0;
    private int mHue = 0;
    private int mColorTmp = 0;
    private int mBackLight = 0;
    public SharpnessModeLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        if (Constant.LOG_TAG) {
            Log.d("test", "PictureLogic ");
        }
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();


        // Sharpness
        WidgetType HiSharpness = new WidgetType();
        // set name for Sharpness
        HiSharpness
                .setName(res.getStringArray(R.array.picture_mode_setting)[5]);
        // set type for Sharpness
        HiSharpness.setType(WidgetType.TYPE_PROGRESS);
        HiSharpness.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setProgress i = " + i);
                }

                mBrightness = PictureInterface.getBrightness();
                mContrast = PictureInterface.getContrast();
                mSaturation = PictureInterface.getSaturation();
                mColorTmp = PictureInterface.getColorTemp();
                mBackLight = PictureInterface.getBacklight();
                if (isHueEnable()) {
                    mHue = PictureInterface.getHue();
                }

                PictureInterface
                        .setPictureMode(InterfaceValueMaps.picture_mode[3][0]);
                RefreshPictureSelector();

                PictureInterface.setSaturation(mSaturation);
                PictureInterface.setContrast(mContrast);
                PictureInterface.setBrightness(mBrightness);
                PictureInterface.setColorTemp(mColorTmp);
                PictureInterface.setBacklight(mBackLight);
                PictureInterface.setSharpness(i);
                

                if (isHueEnable()) {
                    PictureInterface.setHue(mHue);
                }
                return i;

            }

            @Override
            public int getProgress() {

                return PictureInterface.getSharpness();
            }
        });
        mWidgetList.add(HiSharpness);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    /**
     * refresh selector of Picture
     */
    private void RefreshPictureSelector() {
        Message msg = new Message();
        msg.what = Constant.SETTING_UI_REFRESH_VIEWS;
        List<String> Stringlist = new ArrayList<String>();
        Stringlist.add(mContext.getResources().getStringArray(
                R.array.picture_mode_setting)[0]);
        msg.obj = Stringlist;
        mHandler.sendMessage(msg);
    }

    /**
     * is Hue enable or not
     *
     * @return
     */
    private boolean isHueEnable() {
        int mCurSourceIdx = SourceManagerInterface.getSelectSourceId();
        int colorsystem = 0;
        if(mCurSourceIdx == EnumSourceIndex.SOURCE_ATV){
            colorsystem = ATVChannelInterface.getCurrentColorSystem();
            Log.d(TAG,"atv current program colorsystem = "+colorsystem);
            if(colorsystem == EnumColorSystem.CLRSYS_NTSC || colorsystem == EnumColorSystem.CLRSYS_NTSC443
               ||colorsystem == EnumColorSystem.CLRSYS_NTSC_50){

                 return true;
            }
            else{
                return false;
            }

        }
        else if(mCurSourceIdx == EnumSourceIndex.SOURCE_CVBS1 || mCurSourceIdx == EnumSourceIndex.SOURCE_CVBS2){

            colorsystem = PictureInterface.getColorSystem();

            Log.d(TAG,"av current  colorsystem = "+colorsystem);

            if(colorsystem == EnumColorSystem.CLRSYS_NTSC || colorsystem == EnumColorSystem.CLRSYS_NTSC443
               ||colorsystem == EnumColorSystem.CLRSYS_NTSC_50){

                 return true;
            }
            else{
                return false;
            }

        }
        else{

            return false;
        }

    }
    public boolean isHueMode() {
    	// TODO Auto-generated method stub
    	if(TAG.equals("HueModeLogic")){
    		return true;
    	}else{
    		return false;
    	}
    	
    }
}
