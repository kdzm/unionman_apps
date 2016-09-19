package cn.com.unionman.umtvsetting.system.logic.factory;

import android.content.Context;
import android.util.Log;





import cn.com.unionman.umtvsetting.system.interfaces.SourceManagerInterface;
import cn.com.unionman.umtvsetting.system.logic.AVCLogic;
import cn.com.unionman.umtvsetting.system.logic.AspectLogic;
import cn.com.unionman.umtvsetting.system.logic.AudioSystemLogic;
import cn.com.unionman.umtvsetting.system.logic.AutoPowerdownLogic;
import cn.com.unionman.umtvsetting.system.logic.AutoStandbyLogic;
import cn.com.unionman.umtvsetting.system.logic.BacklightLogic;
import cn.com.unionman.umtvsetting.system.logic.BalanceLogic;
import cn.com.unionman.umtvsetting.system.logic.BassLogic;
import cn.com.unionman.umtvsetting.system.logic.BlueExtendLogic;
import cn.com.unionman.umtvsetting.system.logic.BlueToothLogic;
import cn.com.unionman.umtvsetting.system.logic.BrightnessModeLogic;
import cn.com.unionman.umtvsetting.system.logic.ChangeModeLogic;
import cn.com.unionman.umtvsetting.system.logic.ColorDemoLogic;
import cn.com.unionman.umtvsetting.system.logic.ColorSystemLogic;
import cn.com.unionman.umtvsetting.system.logic.ColorTempLogic;
import cn.com.unionman.umtvsetting.system.logic.ConstastModeLogic;
import cn.com.unionman.umtvsetting.system.logic.DBDRDemoLogic;
import cn.com.unionman.umtvsetting.system.logic.DCIDemoLogic;
import cn.com.unionman.umtvsetting.system.logic.DCILogic;
import cn.com.unionman.umtvsetting.system.logic.DemoModeLogic;
import cn.com.unionman.umtvsetting.system.logic.DynamicBLightLogic;
import cn.com.unionman.umtvsetting.system.logic.EQLogic;
import cn.com.unionman.umtvsetting.system.logic.FleshToneLogic;
import cn.com.unionman.umtvsetting.system.logic.HueModeLogic;
import cn.com.unionman.umtvsetting.system.logic.KeySoundLogic;
import cn.com.unionman.umtvsetting.system.logic.MEMCDemoLogic;
import cn.com.unionman.umtvsetting.system.logic.MEMCLogic;
import cn.com.unionman.umtvsetting.system.logic.MenuOf3DLRSwitchLogic;
import cn.com.unionman.umtvsetting.system.logic.MenuOf3DModeLogic;
import cn.com.unionman.umtvsetting.system.logic.MenuOf3DTo2DLogic;
import cn.com.unionman.umtvsetting.system.logic.MenuOf3DViewLogic;
import cn.com.unionman.umtvsetting.system.logic.NRDemoLogic;
import cn.com.unionman.umtvsetting.system.logic.PicSeniorSettingLogic;
import cn.com.unionman.umtvsetting.system.logic.PictureModeLogic;
import cn.com.unionman.umtvsetting.system.logic.PictureNRLogic;
import cn.com.unionman.umtvsetting.system.logic.PowerDemoLogic;
import cn.com.unionman.umtvsetting.system.logic.SPDIFDelayLogic;
import cn.com.unionman.umtvsetting.system.logic.SPDIFLogic;
import cn.com.unionman.umtvsetting.system.logic.SRDemoLogic;
import cn.com.unionman.umtvsetting.system.logic.SRSLogic;
import cn.com.unionman.umtvsetting.system.logic.SaturationModeLogic;
import cn.com.unionman.umtvsetting.system.logic.ScreenSaverLogic;
import cn.com.unionman.umtvsetting.system.logic.SecureModeLogic;
import cn.com.unionman.umtvsetting.system.logic.SharpnessDemoLogic;
import cn.com.unionman.umtvsetting.system.logic.SharpnessModeLogic;
import cn.com.unionman.umtvsetting.system.logic.SingleListenerLogic;
import cn.com.unionman.umtvsetting.system.logic.SleepOnLogic;
import cn.com.unionman.umtvsetting.system.logic.SoundHangModeLogic;
import cn.com.unionman.umtvsetting.system.logic.SoundModeLogic;
import cn.com.unionman.umtvsetting.system.logic.StandbyLedLogic;
import cn.com.unionman.umtvsetting.system.logic.StorageLogic;
import cn.com.unionman.umtvsetting.system.logic.SubWooferLogic;
import cn.com.unionman.umtvsetting.system.logic.SubWooferVolLogic;
import cn.com.unionman.umtvsetting.system.logic.SystemLangureLogic;
import cn.com.unionman.umtvsetting.system.logic.TrebleLogic;
import cn.com.unionman.umtvsetting.system.util.Constant;

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
            	/*logic = new VGALogic(mContext);*/
            	break;
            case 10:
            	logic = new PicSeniorSettingLogic(mContext);
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
            case 1:
                // EQ
                logic = new EQLogic(mContext);
                break;
            case 2:
                // Balance
                logic = new BalanceLogic(mContext);
                break;
            case 3:
                // SRS
                logic = new SRSLogic(mContext);
                break;
            case 4:
                // AVC
                logic = new AVCLogic(mContext);
                break;
            case 5:
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
            case 6:
                // SoundHangMode
                logic = new SoundHangModeLogic(mContext);
                break;
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
        	//Power Settings
        	switch (item) {
        		case 0:
        			logic = new DynamicBLightLogic(mContext);
        			break;
        		case 1:
        			logic = new PowerDemoLogic(mContext);
        			break;
        		case 2:
        			logic = new AutoPowerdownLogic(mContext);
        			break;
        		case 3:
        			logic = new AutoStandbyLogic(mContext);
        			break;
        		default:
        			break;
        	}
        	break;
        case 3:
        	//System setting
        	switch (item) {
        	case 7:
        		logic  = new SystemLangureLogic(mContext);
        		break;
        	
        	case 2:
        		logic = new SleepOnLogic(mContext);
        		break;
/*        	case 7:
        		logic  = new ScreenSaverLogic(mContext);
        		break;*/
        	case 0:
        		logic = new KeySoundLogic(mContext);
        		break;
        	case 1:
        		logic = new StandbyLedLogic(mContext);
        		break;
        	case 6:
        		logic = new DemoModeLogic(mContext);
        		break;
        	case 11:
        		logic = new StorageLogic(mContext);
        		break;
        	case 4:
        		logic = new SecureModeLogic(mContext);
        		break;
        	case 5:
        		logic = new BlueToothLogic(mContext);
        		break;
        	}
        	break;
        default:
            break;
        }
        return logic;
    }
}
