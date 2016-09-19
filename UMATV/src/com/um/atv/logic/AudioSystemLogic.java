package com.um.atv.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import com.um.atv.R;
import com.um.atv.interfaces.ATVChannelInterface;
import com.um.atv.interfaces.InterfaceValueMaps;
import com.um.atv.logic.factory.InterfaceLogic;
import com.um.atv.model.WidgetType;
import com.um.atv.model.WidgetType.AccessSysValueInterface;
import com.um.atv.util.Util;
import android.util.Log;
import com.hisilicon.android.tvapi.constant.EnumAtvAudsys;
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
        mAudioSystem.setName(res.getStringArray(R.array.channel_setting)[8]);
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
                switch (m)
                {
                	case EnumAtvAudsys.AUDSYS_DK:
                	case EnumAtvAudsys.AUDSYS_DK1_A2:
                	case EnumAtvAudsys.AUDSYS_DK2_A2:
                	case EnumAtvAudsys.AUDSYS_DK3_A2:
                	case EnumAtvAudsys.AUDSYS_DK_NICAM:
                		m = EnumAtvAudsys.AUDSYS_DK;
                		break;
                	case EnumAtvAudsys.AUDSYS_BG:
                	case EnumAtvAudsys.AUDSYS_BG_A2:
                	case EnumAtvAudsys.AUDSYS_BG_NICAM:
                		m = EnumAtvAudsys.AUDSYS_BG;
                		break;
                	case EnumAtvAudsys.AUDSYS_M:
                	case EnumAtvAudsys.AUDSYS_M_BTSC:
                	case EnumAtvAudsys.AUDSYS_M_A2:
                	case EnumAtvAudsys.AUDSYS_M_EIA_J:
                		m = EnumAtvAudsys.AUDSYS_M;
                		break;
                	default:
                		break;
                }
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
