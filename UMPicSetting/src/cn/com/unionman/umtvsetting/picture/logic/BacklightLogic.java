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
import cn.com.unionman.umtvsetting.picture.util.Constant;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.constant.EnumColorSystem;
import cn.com.unionman.umtvsetting.picture.R;

/**
 * BacklightLogic
 *
 * @author wangchuanjian
 *
 */
public class BacklightLogic implements InterfaceLogic {
    private static final String TAG = "BacklightLogic";
    private Context mContext;
    private Handler mHandler ;
    // private WidgetType mBacklight;// Backlight
    // private List<WidgetType> mWidgetList = null;

    private int mBrightness = 0;
    private int mContrast = 0;
    private int mSharpness = 0;
    private int mSaturation = 0;
    private int mHue = 0;
    private int mColorTmp = 0;
    
    public BacklightLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // Backlight
        WidgetType mBacklight = new WidgetType();
        // set name for Backlight
        mBacklight.setName(res.getStringArray(R.array.senior_setting)[1]);
        // set type for Backlight
        mBacklight.setType(WidgetType.TYPE_PROGRESS);
        mBacklight.setmAccessProgressInterface(new AccessProgressInterface() {

            @Override
            public int setProgress(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "PictureInterface.setBacklight(i) = " + i);
                }
                mSaturation = PictureInterface.getSaturation();
                mSharpness = PictureInterface.getSharpness();
                mBrightness = PictureInterface.getBrightness();
                mColorTmp = PictureInterface.getColorTemp();
                mContrast = PictureInterface.getContrast();
                if (isHueEnable()) {
                    mHue = PictureInterface.getHue();
                }

                PictureInterface
                        .setPictureMode(InterfaceValueMaps.picture_mode[3][0]);
                RefreshPictureSelector();

                PictureInterface.setContrast(mContrast);
                PictureInterface.setSaturation(mSaturation);
                PictureInterface.setSharpness(mSharpness);
                PictureInterface.setBrightness(mBrightness);
                PictureInterface.setColorTemp(mColorTmp);
                PictureInterface.setBacklight(i);
                if (isHueEnable()) {
                    PictureInterface.setHue(mHue);
                }
                return i;
            }

            @Override
            public int getProgress() {
                if (Constant.LOG_TAG) {
                }

                return PictureInterface.getBacklight();
            }
        });
        mWidgetList.add(mBacklight);

        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub
    	mHandler = handler;
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
    

    public boolean isHueMode() {
    	// TODO Auto-generated method stub
    	if(TAG.equals("HueModeLogic")){
    		return true;
    	}else{
    		return false;
    	}
    	
    }
}
