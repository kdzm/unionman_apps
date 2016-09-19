package cn.com.unionman.umtvsetting.picture.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import cn.com.unionman.umtvsetting.picture.interfaces.AudioInterface;
import cn.com.unionman.umtvsetting.picture.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.picture.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.picture.model.WidgetType;
import cn.com.unionman.umtvsetting.picture.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.picture.util.Constant;
import cn.com.unionman.umtvsetting.picture.util.Util;

import cn.com.unionman.umtvsetting.picture.R;

/**
 * SoundModeLogic
 *
 * @author wangchuanjian
 *
 */
public class SoundModeLogic implements InterfaceLogic {

    private static final String TAG = "SoundModeLogic";
    private Context mContext;

    // private WidgetType mSoundMode;
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mSoundModes = InterfaceValueMaps.auto_adjust;

    public SoundModeLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();

        // SoundMode
        WidgetType mSoundMode = new WidgetType();
        // set name for SoundMode
        mSoundMode.setName(res.getStringArray(R.array.voice_setting)[0]);
        // set name for SoundMode
        mSoundMode.setType(WidgetType.TYPE_SELECTOR);
        mSoundMode.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "setSysValue i = " + i);
                }

                return AudioInterface
                        .setSoundMode(InterfaceValueMaps.sound_mode[i][0]);
            }

            @Override
            public int getSysValue() {
                int mode = AudioInterface.getSoundMode();
                return Util.getIndexFromArray(mode,
                        InterfaceValueMaps.sound_mode);
            }
        });
        // set data for SoundMode
        mSoundMode.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.sound_mode));
        mWidgetList.add(mSoundMode);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

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
