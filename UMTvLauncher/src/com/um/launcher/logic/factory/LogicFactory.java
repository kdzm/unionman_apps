
package com.um.launcher.logic.factory;

import android.content.Context;
import android.util.Log;

import com.um.launcher.R;
import com.um.launcher.logic.model.FactoryResetLogic;
import com.um.launcher.logic.model.NetStateLogic;
import com.um.launcher.logic.model.PictureModeLogic;
import com.um.launcher.logic.model.SeniorModeLogic;
import com.um.launcher.logic.model.SoundModeLogic;
import com.um.launcher.logic.model.SystemInfoLogic;
import com.um.launcher.logic.model.SystemLocalUpdateLogic;
import com.um.launcher.util.Constant;
import com.um.launcher.view.setting.NetSettingDialog;

/**
 * the class of control logic
 *
 * @author huyq
 */
public class LogicFactory {

    private static final String TAG = "LogicFactory";
    private Context mContext;

    public LogicFactory(Context mContext) {
        super();
        this.mContext = mContext;
    }

    /**
     * create logic by index
     *
     * @param index
     * @return
     */
    public InterfaceLogic createLogic(int index) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "index = " + index);
        }
        InterfaceLogic logic = null;
        switch (index) {
            case R.id.set_item_pic:
                // picture setting
                logic = new PictureModeLogic(mContext);
                break;
            case R.id.set_item_sound:
                // voice setting
                logic = new SoundModeLogic(mContext);
                break;
            case R.id.set_item_advanced:
                // senior setting
                logic = new SeniorModeLogic(mContext);
                break;
            case R.id.set_item_appmanage:
                // factory reset
                logic = new FactoryResetLogic(mContext);
                break;
            case R.id.set_item_systeminfo:
                // system info
                logic = new SystemInfoLogic(mContext);
                break;
            case NetSettingDialog.NET_STATE:
                // net state
                logic = new NetStateLogic(mContext);
                break;
            case NetSettingDialog.SYSTEM_LOCAL:
                // system local update
                logic = new SystemLocalUpdateLogic(mContext);
                break;
            default:
                break;
        }
        return logic;
    }
}
