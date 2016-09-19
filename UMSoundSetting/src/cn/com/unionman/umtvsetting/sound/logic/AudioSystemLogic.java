package cn.com.unionman.umtvsetting.sound.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import cn.com.unionman.umtvsetting.sound.interfaces.ATVChannelInterface;
import cn.com.unionman.umtvsetting.sound.interfaces.InterfaceValueMaps;
import cn.com.unionman.umtvsetting.sound.logic.factory.InterfaceLogic;
import cn.com.unionman.umtvsetting.sound.model.WidgetType;
import cn.com.unionman.umtvsetting.sound.model.WidgetType.AccessSysValueInterface;
import cn.com.unionman.umtvsetting.sound.util.Util;

import cn.com.unionman.umtvsetting.sound.R;

/**
 * AudioSystemLogic
 *
 * @author wangchuanjian
 *
 */
public class AudioSystemLogic implements InterfaceLogic {

    private Context mContext;

    // private WidgetType mAudioSystem;// AudioSystem
    // private List<WidgetType> mWidgetList = null;
    // private int[][] mAudioSystemValue = InterfaceValueMaps.audio_system;

    public AudioSystemLogic(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public List<WidgetType> getWidgetTypeList() {
        List<WidgetType> mWidgetList = new ArrayList<WidgetType>();
        Resources res = mContext.getResources();
        // AudioSystem
        WidgetType mAudioSystem = new WidgetType();
        // set name for AudioSystem
        mAudioSystem.setName(res.getStringArray(R.array.channel_setting)[2]);
        // set type for AudioSystem
        mAudioSystem.setType(WidgetType.TYPE_SELECTOR);
        mAudioSystem.setmAccessSysValueInterface(new AccessSysValueInterface() {

            @Override
            public int setSysValue(int i) {
                return ATVChannelInterface
                        .setAudioSystem(InterfaceValueMaps.audio_system[i][0]);
            }

            @Override
            public int getSysValue() {
                int m = ATVChannelInterface.getCurrentAudioSystem();
                return Util.getIndexFromArray(m,
                        InterfaceValueMaps.audio_system);
            }
        });
        // set data for AudioSystem
        mAudioSystem.setData(Util
                .createArrayOfParameters(InterfaceValueMaps.audio_system));
        mWidgetList.add(mAudioSystem);
        return mWidgetList;
    }

    @Override
    public void setHandler(Handler handler) {
        // TODO Auto-generated method stub

    }

}
