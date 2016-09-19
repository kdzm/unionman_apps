
package com.um.launcher.view.setting;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.um.launcher.R;
import com.um.launcher.logic.factory.InterfaceLogic;
import com.um.launcher.logic.factory.LogicFactory;

/**
 * Network settings window contents according to the change of mFlag dialog
 *
 * @author huyq
 */
public class NetSettingDialog extends Dialog {

    private Context mContext;
    private int mFlag;
    private LogicFactory mLogicFactory = null;
    private InterfaceLogic mInterfaceLogic;
    public static final int NET_STATE = 11;
    public static final int SYSTEM_LOCAL = 12;

    public final static int FLAG_NET = 1;
    public final static int FLAG_ETHER = 2;
    public final static int FLAG_WIFI = 3;
    public final static int FLAG_STATE = 4;
    public final static int SYSTEM_UPDATE = 5;
    public final static int SYSTEM_LOCAL_UPDATE = 6;
    public final static int NET_NO_UPDATE = 7;
    public final static int SELECT_UPDATE_VERSION= 8;
    // dialogs in the ui
    private EtherSetting mEtherSetting;
    private WifiSetting mWifiSetting;
    private CustomSettingView mCustomSettingView;
    private SystemUpdateView mSystemUpDateView;
    private SelectUpdateVersion selectupdateversion;
    // Control NetSettingDialog display content
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setContent(msg.what, 0);
        }
    };

    public EtherSetting getEtherSettingDialog() {
        return mEtherSetting;
    }

    public NetSettingDialog(Context context, int flag) {
        super(context, R.style.Translucent_NoTitle);
        mContext = context;
        mFlag = flag;
        mLogicFactory = new LogicFactory(mContext);
        setContent(flag, 0);
    }

    // Focus returned from the three level menu,
    // set the focus of two level menu NetSetting
    private void setContent(int flag, int focus) {
        mFlag = flag;
        switch (flag) {
            case FLAG_NET:
                // netsetting
                setContentView(new NetSetting(mContext, mHandler, focus));
                break;
            case FLAG_ETHER:
                // EtherSetting
                mEtherSetting = new EtherSetting(mContext, mHandler);
                setContentView(mEtherSetting);
                break;
            case FLAG_WIFI:
                // WifiSetting
                mWifiSetting = new WifiSetting(mContext, mHandler);
                setContentView(mWifiSetting);
                break;
            case FLAG_STATE:
                // net state
                setContentView(new CustomSettingView(mContext, mContext
                        .getResources().getString(R.string.net_state_setting),
                        mLogicFactory.createLogic(NET_STATE)));
                break;
            case SYSTEM_UPDATE:
                // System UpDate
                mSystemUpDateView = new SystemUpdateView(mContext, mHandler, focus);
                setContentView(mSystemUpDateView);
                break;
            case SYSTEM_LOCAL_UPDATE:
                // Local update
                setContentView(new CustomSettingView(mContext, mContext
                        .getResources().getString(R.string.system_update_local),
                        mLogicFactory.createLogic(SYSTEM_LOCAL)));

                mInterfaceLogic = mLogicFactory.createLogic(SYSTEM_LOCAL);
                mCustomSettingView = new CustomSettingView(mContext, mContext
                        .getResources().getString(R.string.system_update_local),
                        mInterfaceLogic);
                setContentView(mCustomSettingView);
                break;
            case SELECT_UPDATE_VERSION:
                selectupdateversion = new SelectUpdateVersion(mContext);
                 setContentView(selectupdateversion);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        switch (mFlag) {
            case FLAG_NET:
                super.onBackPressed();
                return;
            case FLAG_ETHER:
                setContent(FLAG_NET, 0);
                return;
            case FLAG_WIFI:
                setContent(FLAG_NET, 1);
                return;
            case FLAG_STATE:
                setContent(FLAG_NET, 2);
                return;
            case SYSTEM_UPDATE:
                super.onBackPressed();
                return;
            case SYSTEM_LOCAL_UPDATE:
                setContent(SYSTEM_UPDATE, 0);
                return;
            case SELECT_UPDATE_VERSION:
                setContent(SYSTEM_UPDATE, 0);
                 return;
            default:
                break;
        }
        super.onBackPressed();
    }

    @Override
    public void dismiss() {
        if (null != mEtherSetting) {
            mEtherSetting.dismissChildDialog();
        }
        if (null != mWifiSetting) {
            mWifiSetting.dismissChildDialog();
        }
        if (null != selectupdateversion) {
            selectupdateversion.dismissChildDialog();
        }
        if (null != mInterfaceLogic) {
            mInterfaceLogic.dismissDialog();
        }
        super.dismiss();
    }

    @Override
    protected void onStop() {
        if (mEtherSetting != null) {
            mEtherSetting.onStop();
            mEtherSetting = null;
        }
        super.onStop();
    }
}
