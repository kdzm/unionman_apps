
package com.unionman.netsetup.logic.factory;

import android.content.Context;
import android.util.Log;
import com.unionman.netsetup.logic.model.NetStateLogic;
import com.unionman.netsetup.util.Constant;
import com.unionman.netsetup.view.setting.NetSettingDialog;

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
           /* case R.id.set_item_pic:
                // picture setting
                logic = new PictureModeLogic(mContext);
                R.id.all_app_title;
                break;
            case R.id.set_item_sound:
                // voice setting
               // logic = new SoundModeLogic(mContext);
                break;
            case R.id.set_item_advanced:
                // senior setting
               // logic = new SeniorModeLogic(mContext);
                break;
            case R.id.set_item_recover:
                // factory reset
                logic = new FactoryResetLogic(mContext);
                break;
            case R.id.set_item_systeminfo:
                // system info
                logic = new SystemInfoLogic(mContext);
                break;*/
            case NetSettingDialog.NET_STATE:
                // net state
                logic = new NetStateLogic(mContext);
                break;
           /* case NetSettingDialog.SYSTEM_LOCAL:
                // system local update
                logic = new SystemLocalUpdateLogic(mContext);
                break;*/
            default:
                break;
        }
        return logic;
    }
}
