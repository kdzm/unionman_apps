package cn.com.unionman.umtvsetting.sound.logic.factory;

import android.content.Context;
import android.util.Log;
import cn.com.unionman.umtvsetting.sound.interfaces.SourceManagerInterface;
import cn.com.unionman.umtvsetting.sound.logic.AVCLogic;
import cn.com.unionman.umtvsetting.sound.logic.AspectLogic;
import cn.com.unionman.umtvsetting.sound.logic.AudioSystemLogic;
import cn.com.unionman.umtvsetting.sound.logic.BacklightLogic;
import cn.com.unionman.umtvsetting.sound.logic.BalanceLogic;
import cn.com.unionman.umtvsetting.sound.logic.BassLogic;
import cn.com.unionman.umtvsetting.sound.logic.BlueExtendLogic;
import cn.com.unionman.umtvsetting.sound.logic.BrightnessModeLogic;
import cn.com.unionman.umtvsetting.sound.logic.ChangeModeLogic;
import cn.com.unionman.umtvsetting.sound.logic.ChannelSkipLogic;
import cn.com.unionman.umtvsetting.sound.logic.ColorDemoLogic;
import cn.com.unionman.umtvsetting.sound.logic.ColorSystemLogic;
import cn.com.unionman.umtvsetting.sound.logic.ColorTempLogic;
import cn.com.unionman.umtvsetting.sound.logic.ConstastModeLogic;
import cn.com.unionman.umtvsetting.sound.logic.DBDRDemoLogic;
import cn.com.unionman.umtvsetting.sound.logic.DCIDemoLogic;
import cn.com.unionman.umtvsetting.sound.logic.DCILogic;
import cn.com.unionman.umtvsetting.sound.logic.DynamicBLightLogic;
import cn.com.unionman.umtvsetting.sound.logic.EQLogic;
import cn.com.unionman.umtvsetting.sound.logic.FleshToneLogic;
import cn.com.unionman.umtvsetting.sound.logic.HueModeLogic;
import cn.com.unionman.umtvsetting.sound.logic.MEMCDemoLogic;
import cn.com.unionman.umtvsetting.sound.logic.MEMCLogic;
import cn.com.unionman.umtvsetting.sound.logic.MenuOf3DDepthLogic;
import cn.com.unionman.umtvsetting.sound.logic.MenuOf3DLRSwitchLogic;
import cn.com.unionman.umtvsetting.sound.logic.MenuOf3DModeLogic;
import cn.com.unionman.umtvsetting.sound.logic.MenuOf3DTo2DLogic;
import cn.com.unionman.umtvsetting.sound.logic.MenuOf3DViewLogic;
import cn.com.unionman.umtvsetting.sound.logic.NRDemoLogic;
import cn.com.unionman.umtvsetting.sound.logic.PictureModeLogic;
import cn.com.unionman.umtvsetting.sound.logic.PictureNRLogic;
import cn.com.unionman.umtvsetting.sound.logic.SPDIFDelayLogic;
import cn.com.unionman.umtvsetting.sound.logic.SPDIFLogic;
import cn.com.unionman.umtvsetting.sound.logic.SRDemoLogic;
import cn.com.unionman.umtvsetting.sound.logic.SRSLogic;
import cn.com.unionman.umtvsetting.sound.logic.SaturationModeLogic;
import cn.com.unionman.umtvsetting.sound.logic.SharpnessDemoLogic;
import cn.com.unionman.umtvsetting.sound.logic.SharpnessModeLogic;
import cn.com.unionman.umtvsetting.sound.logic.SingleListenerLogic;
import cn.com.unionman.umtvsetting.sound.logic.SoundHangModeLogic;
import cn.com.unionman.umtvsetting.sound.logic.SoundModeLogic;
import cn.com.unionman.umtvsetting.sound.logic.SoundSpeakerLogic;
import cn.com.unionman.umtvsetting.sound.logic.SubWooferLogic;
import cn.com.unionman.umtvsetting.sound.logic.SubWooferVolLogic;
import cn.com.unionman.umtvsetting.sound.logic.TrebleLogic;
import cn.com.unionman.umtvsetting.sound.util.Constant;

import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
public class LogicFactory {

    private static final String TAG = "LogicFactory";
    private Context mContext;

    public LogicFactory(Context mContext) {
        super();
        this.mContext = mContext;
    }

    /**
     * create logic by category and item
     *
     * @param category
     * @param item
     * @return
     */
    public InterfaceLogic createLogic(int category, int item) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "category = " + category + ";item=" + item);
        }
        InterfaceLogic logic = null;
        switch (category) {
        case 0:
            // PictureSet
            switch (item) {
            case 0:
                // PictureMode
                logic = new PictureModeLogic(mContext);
                break;
            case 1:
            	//Constast
            	logic = new ConstastModeLogic(mContext);
            	break;
            case 2:
            	//Brightness
            	logic = new BrightnessModeLogic(mContext);
            	break;
            case 3:
            	//Saturation
            	logic = new SaturationModeLogic(mContext);
            	break;
            case 4:
            	//Sharpness
            	logic = new SharpnessModeLogic(mContext);
            	break;
            case 5:
            	//Hue
            	logic = new HueModeLogic(mContext);
            	break;
            case 6:
            	//ColorTemp
            	logic = new ColorTempLogic(mContext);
            	break;
            case 7:
            	//Backlight
            	logic = new BacklightLogic(mContext);
            	break;
            case 8:
            	// Aspect
            	logic = new AspectLogic(mContext);
            	break;
            case 9:
            	//logic = new VGALogic(mContext);
            	break;
//            case 1:
//                // ColorTemp
//                logic = new ColorTempLogic(mContext);
//                break;
//            case 2:
//                // Memclevel
//                logic = new MEMCLogic(mContext);
//                break;
//            case 3:
//                // PictureNR
//                logic = new PictureNRLogic(mContext);
//
//                break;
//            case 4:
//                // DCI
//                logic = new DCILogic(mContext);
//                break;
///*            case 5:
//                // FleshTone
//                logic = new FleshToneLogic(mContext);
//                break;
//            case 6:
//                // BlueExtend
//                logic = new BlueExtendLogic(mContext);
//                break;*/
//            case 5:
//                // Aspect or VGA
//                if(SourceManagerInterface.getSelectSourceId() == EnumSourceIndex.SOURCE_VGA) {
//                    logic = new VGALogic(mContext);
//                }else{
//                    logic = new AspectLogic(mContext);
//                }
//                break;
//            case 6:
//                // Aspect
//                logic = new AspectLogic(mContext);
//                break;
            default:
                break;
            }
            break;
        case 1:
            // Sound set
            switch (item) {
            case 0:
                // SoundMode
                logic = new SoundModeLogic(mContext);
                break;
/*            case 1:
                // Treble
                logic = new TrebleLogic(mContext);
                break;
            case 2:
                // Bass
                logic = new BassLogic(mContext);
                break;*/
            case 4:
                // EQ
                logic = new EQLogic(mContext);
                break;
            case 1:
                // Balance
                logic = new BalanceLogic(mContext);
                break;
/*            case 3:
                // SRS
                logic = new SRSLogic(mContext);
                break;*/
            case 2:
                // AVC
                logic = new AVCLogic(mContext);
                break;
            case 3:
                // SPDIF output
                logic = new SPDIFLogic(mContext);
                break;
/*            case 8:
                // SpdifDelay
                logic = new SPDIFDelayLogic(mContext);
                break;
            case 9:
                // SubWoofer
                logic = new SubWooferLogic(mContext);
                break;
            case 10:
                // SubWooferVol
                logic = new SubWooferVolLogic(mContext);
                break;*/
/*            case 6:
                // SPDIF output
                logic = new SPDIFLogic(mContext);
                break;*/
/*            case 7:
                // SoundHangMode
                logic = new SoundHangModeLogic(mContext);
                break;*/
/*            case 4:
                // SoundHangMode
                logic = new SoundSpeakerLogic(mContext);
                break;*/
            default:
                break;
            }
            break;
/*
            case 2:
            // set 3D
            switch (item) {
            case 0:
                // 3D Mode
                logic = new MenuOf3DModeLogic(mContext);
                break;
            case 1:
                // 3D LR Switch
                logic = new MenuOf3DLRSwitchLogic(mContext);
                break;
            case 2:
                // 3D TO 2D
                logic = new MenuOf3DTo2DLogic(mContext);
                break;
            case 3:
                // 3D depth
                logic = new MenuOf3DDepthLogic(mContext);
                break;
            case 4:
                // 3D View
                logic = new MenuOf3DViewLogic(mContext);
                break;
            default:
                break;

                }
                break;*/
//            case 2:
//            // set channel
//            switch (item) {
//            case 0:
//                // ChannelSkip
//                logic = new ChannelSkipLogic(mContext);
//                break;
//            case 1:
//                // ColorSystem
//                logic = new ColorSystemLogic(mContext);
//                break;
//            case 2:
//                // SoundSystem
//                logic = new AudioSystemLogic(mContext);
//                break;
//            case 3:
//                // FineTune
//                logic = new FineTuneLogic(mContext);
//                break;
//            case 4:
//                // ManualScan
//                logic = new ManualScanLogic(mContext);
//                break;
//            case 5:
//                // AutoScan
//                logic = new AutoScanLogic(mContext);
//                break;
//            case 6:
//                // channel edit
//                logic = new ChannelEditLogic(mContext);
//                break;
//            case 7:
//                // ChangeMode
//                logic = new ChangeModeLogic(mContext);
//                break;
//            default:
//                break;
//            }
//            break;

        case 2:
            // Advanced settings
            switch (item) {
            case 0:
                // DynamicBLight
                logic = new DynamicBLightLogic(mContext);
                break;
            case 1:
                // Backlight
                logic = new BacklightLogic(mContext);
                break;
            case 2:
                // SignalListener
                logic = new SingleListenerLogic(mContext);
                break;
            default:
                break;
            }
            break;
            case 3:
            // The demo mode
            switch (item) {
            case 0:
                // MEMC
                logic = new MEMCDemoLogic(mContext);
                break;
/*            case 2:
                // NR
                logic = new NRDemoLogic(mContext);
                break;
            case 3:
                // Sharpness
                logic = new SharpnessDemoLogic(mContext);
                break;*/
            case 1:
                // DCI
                logic = new DCIDemoLogic(mContext);
                break;
            case 2:
                // SR
                logic = new SRDemoLogic(mContext);
                break;
/*            case 6:
                // DBRC
                logic = new DBDRDemoLogic(mContext);
                break;*/
            default:
                break;
            }
            break;

        default:
            break;
        }
        return logic;
    }
}
