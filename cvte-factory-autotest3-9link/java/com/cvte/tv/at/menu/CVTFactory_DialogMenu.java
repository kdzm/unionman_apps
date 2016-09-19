
package com.cvte.tv.at.menu;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.cvte.tv.at.R;
import com.cvte.tv.at.api.CvteFacAPI;
import com.cvte.tv.at.util.Utils;
import com.cvte.tv.at.util.Utils.DialogPage;
import com.cvte.tv.at.util.Utils.Transdirection;

import java.io.BufferedReader;

/**
 * CVTFactory_DialogMenu 对话框模块
 *
 * @author Leajen_Ren
 * @version V1.1 2014-06-24 更新了变量命名
 * @Package com.cvte.tv.at.at.job
 * @Description 这里的代码其实可以实现主程序升级，HDCP，MAC，升级及擦除，6M30升级模板，不过现在仅用来做频道表的导入和导出功能
 */
public class CVTFactory_DialogMenu extends Dialog {

    private static CvteFacAPI sFacapi;
    private static Context sContext;
    private static Activity sParentact;

    private static TextView sTextview_dialog_title;
    private static TextView sTextview_dialog_confirm;
    private static TextView sTextview_dialog_cancel;
    private static TextView sTextview_dialog_hint;

    private static int UPGRATE_END_FAIL = 0;
    private static int UPGRATE_END_SUCCESS = 1;
    private static int UPGRATE_END_FILE_NOT_FOUND = 2;
    private static int UPGRATE_END_SUCCESS_MAIN = 3;
    private static int UPGRATE_START = 4;
    private static int UPGRATE_END_FILE_CANNOT_DELETE = 5;
    private static int UPGRATE_END_DEVICE_NOT_FOUND = 6;
    private static boolean WaitActionFinish = false;

    public static int IMPORT_EXPORT_CH_DELAY = 6000;

    private DialogPage mParentItemId;

    private boolean mUSBUpgradeFlag = false;
    private boolean mAllResetResult = false;
    private boolean mTvResetResult = false;

    private static enum upgradeStatus {
        Fail("Fail!"),
        Success("Success!"),
        FileNotFound("File Not Found!"),
        PleasePlugUSB("Please Plug USB!"),
        FileCannotDelete("file Can't delete!"),
        USBDeviceNotFound("USB Device Not Found!"),
        Rebooting("Rebooting! Please Wait...");

        private String name;

        private upgradeStatus(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }


    public enum EnumUpgradeStatus {
        // status fail
        E_UPGRADE_FAIL,
        // status success
        E_UPGRADE_SUCCESS,
        // file not found
        E_UPGRADE_FILE_NOT_FOUND,
        // file delete fail
        E_UPGRADE_FILE_DELETE_FAIL,
        // USB Device not found
        E_UPGRADE_DEVICE_NOT_FOUND,
    }

    public CVTFactory_DialogMenu(Activity act, Context context, int theme) {
        super(context, theme);
        mParentItemId = DialogPage.values()[theme];
        sContext = context;
        sParentact = act;
        sFacapi = CvteFacAPI.getInstance(sContext);
        FacKeyReceiverRegister(sContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.LOG("CVTFactory_DialogMenu: onCreate mParentItemId=" + mParentItemId);
        super.onCreate(savedInstanceState);
        switch (mParentItemId) {
            case id_facmenu_channel_export:
            case id_facmenu_channel_import:
                setContentView(R.layout.facmenu_root_dialog);
                break;
            default:
                Utils.LOG("No Style for mParentItemId");
                return;
        }

        sTextview_dialog_title = (TextView) findViewById(R.id.facmenu_dialog_title);
        sTextview_dialog_confirm = (TextView) findViewById(R.id.id_facmenu_dialogmenu_confirm);
        sTextview_dialog_cancel = (TextView) findViewById(R.id.id_facmenu_dialogmenu_cancel);
        sTextview_dialog_hint = (TextView) findViewById(R.id.id_facmenu_dialogmenu_title);
        sTextview_dialog_confirm.setBackgroundResource(R.drawable.button_state);
        sTextview_dialog_cancel.setBackgroundResource(R.drawable.button_state);
        registerListeners();
        mUSBUpgradeFlag = true;
        mAllResetResult = false;
        mTvResetResult = false;

        switch (mParentItemId) {
            case id_facmenu_channel_export:
                sTextview_dialog_title.setText(R.string.str_facmenu_channelcontrol);
                sTextview_dialog_hint.setText(R.string.str_facmenu_exportchannel);
                break;
            case id_facmenu_channel_import:
                sTextview_dialog_title.setText(R.string.str_facmenu_channelcontrol);
                sTextview_dialog_hint.setText(R.string.str_facmenu_importchannel);
                break;
            default:
                Utils.LOG("CVTFactory_DialogMenu mParentItemId = null");
                dismiss();
                break;
        }
    }

    @Override
    public void show() {
        Utils.LOG("CVTFactory_DialogMenu: show");
        super.show();
    }

    @Override
    public void dismiss() {
        Utils.LOG("CVTFactory_DialogMenu: dismiss");
        FacKeyReceiverUnRegister(sContext);
        sParentact.finish();
        super.dismiss();
    }

    private void registerListeners() {
        sTextview_dialog_confirm.setOnClickListener(listener);
        sTextview_dialog_cancel.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.id_facmenu_dialogmenu_confirm:
                    if (mUSBUpgradeFlag) {
                        DialogMenuConfirm(mParentItemId);
                    } else {
                        DialogMenuCancel(mParentItemId);
                    }
                    break;
                case R.id.id_facmenu_dialogmenu_cancel:
                    DialogMenuCancel(mParentItemId);
                    mAllResetResult = false;
                    mTvResetResult = false;
                    break;
                default:
                    break;
            }
        }
    };

    static int ChannelTableControl(Transdirection pos) {
        int ret = 0;
        boolean result = false;

        switch (pos) {
            case CHExport:
                Utils.LOG("ChannelTableControl Export");

                result = sFacapi.ExportAllProgramTable();
                if (result)
                    Utils.LOG("Export Channel DB OK");
                else
                    Utils.LOG("Export Channel DB NG");

                break;
            case CHImport:
                Utils.LOG("ChannelTableControl Import");

                result = sFacapi.ImportAllProgramTable();
                if (result)
                    Utils.LOG("Import Channel DB OK");
                else
                    Utils.LOG("Import Channel DB NG");

                break;
            default:
                return EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
        }

        if (result) {
            ret = EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal();
            Utils.LOG("ret = EnumUpgradeStatus.E_UPGRADE_SUCCESS");
        } else {
            ret = EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
            Utils.LOG("ret = EnumUpgradeStatus.E_UPGRADE_FAIL");
        }

        return ret;
    }

    public static boolean ChannelTableControlAction(final Transdirection pos) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (CVTFactory_DialogMenu.getHandler() != null) {
                    int upgrate_status;
                    CVTFactory_DialogMenu.getHandler().sendEmptyMessage(UPGRATE_START);

                    upgrate_status = ChannelTableControl(pos);

                    for (int wait = 0; wait < IMPORT_EXPORT_CH_DELAY; wait++) {
                        sFacapi.ThreadSleep(1);
                        if (WaitActionFinish)
                            break;
                    }
                    if (upgrate_status == EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal()) {
                        CVTFactory_DialogMenu.getHandler().sendEmptyMessage(UPGRATE_END_SUCCESS);// Export Success
                    } else if (upgrate_status == EnumUpgradeStatus.E_UPGRADE_DEVICE_NOT_FOUND.ordinal()) {
                        CVTFactory_DialogMenu.getHandler().sendEmptyMessage(UPGRATE_END_DEVICE_NOT_FOUND);
                    } else {
                        CVTFactory_DialogMenu.getHandler().sendEmptyMessage(UPGRATE_END_FAIL);
                    }
                }
            }
        }).start();
        return true;
    }

    public void DialogMenuConfirm(DialogPage which) {
        switch (which) {
            case id_facmenu_channel_export:
                ChannelTableControlAction(Transdirection.CHExport);
                mUSBUpgradeFlag = false;
                break;
            case id_facmenu_channel_import:
                ChannelTableControlAction(Transdirection.CHImport);
                mUSBUpgradeFlag = false;
                break;
            default:
                break;
        }
    }

    private void DialogMenuCancel(DialogPage which) {
        switch (which) {
            case id_facmenu_channel_export:
            case id_facmenu_channel_import:
                break;
            default:
                break;
        }
        mAllResetResult = false;
        mTvResetResult = false;
        this.dismiss();
    }

    private static ProgressDialog progressDialog = null;
    protected static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == UPGRATE_START) {
                progressDialog = getProgressDialog();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (progressDialog != null)
                    progressDialog.show();
            } else if (msg.what == UPGRATE_END_SUCCESS) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                sTextview_dialog_hint.setText(upgradeStatus.Success.toString());
            } // UpgradeMain Prepare Success
            else if (msg.what == UPGRATE_END_SUCCESS_MAIN) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                sTextview_dialog_hint.setText(upgradeStatus.Rebooting.toString());
            } else if (msg.what == UPGRATE_END_FILE_NOT_FOUND) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                sTextview_dialog_hint.setText(upgradeStatus.FileNotFound.toString());
            } else if (msg.what == UPGRATE_END_FILE_CANNOT_DELETE) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                sTextview_dialog_hint.setText(upgradeStatus.FileCannotDelete.toString());
            } else if (msg.what == UPGRATE_END_DEVICE_NOT_FOUND) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                sTextview_dialog_hint.setText(upgradeStatus.USBDeviceNotFound.toString());
            } // UpgradeMain Prepare Success
            else {
                if (progressDialog != null)
                    progressDialog.dismiss();
                sTextview_dialog_hint.setText(upgradeStatus.Fail.toString());
            }
        }

        ;
    };
    private static BufferedReader bufferedreader;

    private static ProgressDialog getProgressDialog() {

        // if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(sContext);
            progressDialog
                    .setMessage(sContext
                            .getString(R.string.str_textview_factory_otheroption_waiting_val));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
        }
        return progressDialog;
    }

    public static Handler getHandler() {
        return handler;
    }

    private void FacKeyReceiverRegister(Context sContext) {
        IntentFilter filter = new IntentFilter(Utils.CVTE_BROADCAST_SCANEND);
        sContext.registerReceiver(mReceiver, filter);
        WaitActionFinish = false;
    }

    private void FacKeyReceiverUnRegister(Context sContext) {
        WaitActionFinish = false;
        sContext.unregisterReceiver(mReceiver);
    }


    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            Utils.LOG("AT BroadcastReceiver action:" + action);
            if (action.equals(Utils.CVTE_BROADCAST_SCANEND)) {
                WaitActionFinish = true;
            }
        }
    };
}
