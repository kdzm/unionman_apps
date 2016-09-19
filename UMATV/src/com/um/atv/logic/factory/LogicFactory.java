package com.um.atv.logic.factory;

import android.content.Context;
import android.util.Log;





import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.um.atv.ATVMainActivity;
import com.um.atv.interfaces.SourceManagerInterface;
import com.um.atv.logic.AudioSystemLogic;
import com.um.atv.logic.AutoScanLogic;
import com.um.atv.logic.ChangeModeLogic;
import com.um.atv.logic.ChannelEditLogic;
import com.um.atv.logic.ChannelSkipLogic;
import com.um.atv.logic.ColorDemoLogic;
import com.um.atv.logic.ColorSystemLogic;
import com.um.atv.logic.FineTuneLogic;
import com.um.atv.logic.ManualScanLogic;
import com.um.atv.util.Constant;
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
            // set channel
            switch (item) {
            case 0:
            	// AutoScan
                logic = new AutoScanLogic(mContext);
                
                break;
            case 1:
            	// ManualScan
                logic = new ManualScanLogic(mContext);
                
                break;
            case 2:
            	// FineTune
                logic = new FineTuneLogic(mContext);
                
                break;
            case 3:
            	// channel edit
                logic = new ChannelEditLogic(mContext);
                break;
           /* case 6:
            	// ChangeMode
                //logic = new ChangeModeLogic(mContext);
                
                break;*/
            /*case 7:
            	// ColorSystem
                logic = new ColorSystemLogic(mContext);
                break;*/
            /*case 8:
            	// SoundSystem
                logic = new AudioSystemLogic(mContext);
                break;*/
           /* case 7:
            	// ChannelSkip
                logic = new ChannelSkipLogic(mContext);
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
