package cn.com.unionman.umtvsetting.picture.logic.factory;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;





import cn.com.unionman.umtvsetting.picture.R;
import cn.com.unionman.umtvsetting.picture.interfaces.PictureInterface;
import cn.com.unionman.umtvsetting.picture.interfaces.SourceManagerInterface;
import cn.com.unionman.umtvsetting.picture.logic.AVCLogic;
import cn.com.unionman.umtvsetting.picture.logic.AspectLogic;
import cn.com.unionman.umtvsetting.picture.logic.AudioSystemLogic;

import cn.com.unionman.umtvsetting.picture.logic.BacklightLogic;
import cn.com.unionman.umtvsetting.picture.logic.BalanceLogic;
import cn.com.unionman.umtvsetting.picture.logic.BassLogic;
import cn.com.unionman.umtvsetting.picture.logic.BlueExtendLogic;
import cn.com.unionman.umtvsetting.picture.logic.BrightnessModeLogic;
import cn.com.unionman.umtvsetting.picture.logic.ChannelSkipLogic;
import cn.com.unionman.umtvsetting.picture.logic.ColorDemoLogic;
import cn.com.unionman.umtvsetting.picture.logic.ColorSystemLogic;
import cn.com.unionman.umtvsetting.picture.logic.ColorTempLogic;
import cn.com.unionman.umtvsetting.picture.logic.ConstastModeLogic;
import cn.com.unionman.umtvsetting.picture.logic.DBDRDemoLogic;
import cn.com.unionman.umtvsetting.picture.logic.DCIDemoLogic;
import cn.com.unionman.umtvsetting.picture.logic.DCILogic;
import cn.com.unionman.umtvsetting.picture.logic.DynamicBLightLogic;
import cn.com.unionman.umtvsetting.picture.logic.EQLogic;

import cn.com.unionman.umtvsetting.picture.logic.FleshToneLogic;
import cn.com.unionman.umtvsetting.picture.logic.HueModeLogic;
import cn.com.unionman.umtvsetting.picture.logic.MEMCDemoLogic;
import cn.com.unionman.umtvsetting.picture.logic.MEMCLogic;

import cn.com.unionman.umtvsetting.picture.logic.MenuOf3DDepthLogic;
import cn.com.unionman.umtvsetting.picture.logic.MenuOf3DLRSwitchLogic;
import cn.com.unionman.umtvsetting.picture.logic.MenuOf3DModeLogic;
import cn.com.unionman.umtvsetting.picture.logic.MenuOf3DTo2DLogic;
import cn.com.unionman.umtvsetting.picture.logic.MenuOf3DViewLogic;
import cn.com.unionman.umtvsetting.picture.logic.NRDemoLogic;
import cn.com.unionman.umtvsetting.picture.logic.PicSeniorSettingLogic;
import cn.com.unionman.umtvsetting.picture.logic.PictureModeLogic;
import cn.com.unionman.umtvsetting.picture.logic.PictureNRLogic;
import cn.com.unionman.umtvsetting.picture.logic.SPDIFDelayLogic;
import cn.com.unionman.umtvsetting.picture.logic.SPDIFLogic;
import cn.com.unionman.umtvsetting.picture.logic.SRDemoLogic;
import cn.com.unionman.umtvsetting.picture.logic.SRSLogic;
import cn.com.unionman.umtvsetting.picture.logic.SaturationModeLogic;
import cn.com.unionman.umtvsetting.picture.logic.SharpnessDemoLogic;
import cn.com.unionman.umtvsetting.picture.logic.SharpnessModeLogic;
import cn.com.unionman.umtvsetting.picture.logic.SingleListenerLogic;
import cn.com.unionman.umtvsetting.picture.logic.SoundHangModeLogic;
import cn.com.unionman.umtvsetting.picture.logic.SoundModeLogic;
import cn.com.unionman.umtvsetting.picture.logic.SubWooferLogic;
import cn.com.unionman.umtvsetting.picture.logic.SubWooferVolLogic;
import cn.com.unionman.umtvsetting.picture.logic.TrebleLogic;
import cn.com.unionman.umtvsetting.picture.logic.VGALogic;
import cn.com.unionman.umtvsetting.picture.util.Constant;

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
            	//Backlight
            	boolean dynamicEnable = PictureInterface.isDynamicBLEnable();
            	if (dynamicEnable) {
            		Toast.makeText(mContext, R.string.picmode_backlight_string_help, Toast.LENGTH_SHORT).show();
				}else {
					logic = new BacklightLogic(mContext);
				}
				break;	
            case 6:
            	//Hue
            	logic = new HueModeLogic(mContext);
            	break;
            case 7:
            	//ColorTemp
            	logic = new ColorTempLogic(mContext);
            	break;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            	logic = new VGALogic(mContext);
            	break;
/*            case 10:
            	logic = new PicSeniorSettingLogic(mContext);
            	break;*/
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
