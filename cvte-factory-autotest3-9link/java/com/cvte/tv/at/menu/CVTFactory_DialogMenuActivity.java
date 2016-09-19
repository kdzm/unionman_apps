
package com.cvte.tv.at.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.cvte.tv.at.R;
import com.cvte.tv.at.api.CvteFacAPI;
import com.cvte.tv.at.util.Utils;
import com.cvte.tv.at.util.Utils.ActionType;
import com.cvte.tv.at.util.Utils.DialogPage;

/**
 * CVTFactory_DialogMenuActivity 快捷菜单模块
 *
 * @author Leajen_Ren
 * @version V1.1 2014-06-24 更新了变量命名
 * @Package com.cvte.tv.at.at.job
 * @Description 这里的代码可以说是工厂菜单的雏形，不过M6有工厂菜单，所以这里的菜单便就设计成了快捷频道导出的功能。
 */
public class CVTFactory_DialogMenuActivity extends Activity {
    private static CvteFacAPI mFacapi;
    private CVTFactory_DialogMenu mDlg;
    private LinearLayout mLinear_facmenu_dialog_list;

    private int LayoutID = DialogPage.id_facmenu_channel_export.ordinal();
    private final int SHOW_DIALOG = 100;
    private String sActionStr;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_DIALOG:
//                    startsurfaceview();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.LOG("<Dialog-ACT> onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        View v = View.inflate(this, R.layout.dialogroot, null);
        setContentView(v, params);
        super.onCreate(savedInstanceState);

        mLinear_facmenu_dialog_list = (LinearLayout) findViewById(R.id.dialog_list);
        mLinear_facmenu_dialog_list.setVisibility(View.VISIBLE);
        mFacapi = CvteFacAPI.getInstance(getApplicationContext());
    }

    @Override
    protected void onResume() {
        Intent intent = getIntent();
        sActionStr = intent.getStringExtra("ActionType");
        intent.removeExtra("ActionType");

        if (sActionStr == null) {
            Utils.LOG("sActionStr = null endself");
        } else {

            switch (ActionType.values()[Integer.parseInt(sActionStr)]) {
                case Act_ExportCH:
                    StartChannelExport();
                    break;
                case Act_ImportCH:
                    StartChannelImport();
                    break;
                case Act_ImportCUSCH:
                    // StartChannelImport();
                    break;
                default:
                    Utils.LOG("ATActivity_onCreate_action = ActionType.Act_NULL endself");
                    endself();
                    return;
            }
        }
        super.onResume();
        mHandler.sendEmptyMessage(SHOW_DIALOG);
    }

    private void endself() {
        Utils.LOG("<Dialog-ACT> endself");
        if (mDlg != null)
            mDlg.dismiss();
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finialsurfaceview();
        System.gc();
        System.exit(0);
        Utils.LOG("<Dialog-ACT> onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.LOG("<Dialog-ACT> onDestroy");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean bRet = true;
        int currentid = this.getCurrentFocus().getId();

        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                switch (currentid) {
                    case R.id.id_facmenu_dialogmenu_exportchannel:
                        StartChannelExport();
                        break;
                    case R.id.id_facmenu_dialogmenu_importchannel:
                        StartChannelImport();
                        break;
                    default:
                        return false;
                }
                break;
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_MENU:
                endself();
                break;
            default:
                bRet = false;
                break;
        }
        return bRet;
    }

    private void StartChannelExport() {
        Utils.LOG("<Dialog-ACT> Choice Channel Export");
        mLinear_facmenu_dialog_list.setVisibility(View.GONE);
        mDlg = new CVTFactory_DialogMenu(this,
                CVTFactory_DialogMenuActivity.this,
                DialogPage.id_facmenu_channel_export.ordinal());
        mDlg.show();
    }

    private void StartChannelImport() {
        Utils.LOG("<Dialog-ACT> Choice Channel Import");
        mLinear_facmenu_dialog_list.setVisibility(View.GONE);
        mDlg = new CVTFactory_DialogMenu(this,
                CVTFactory_DialogMenuActivity.this,
                DialogPage.id_facmenu_channel_import.ordinal());
        mDlg.show();
    }

    private void startsurfaceview() {
        mFacapi.setATScreen(true);
    }

    private void finialsurfaceview() {
        mFacapi.setATScreen(false);
    }

}
