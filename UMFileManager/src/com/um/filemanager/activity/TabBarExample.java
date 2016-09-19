package com.um.filemanager.activity;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.net.wifi.WifiManager;

import com.um.filemanager.R;
import com.um.filemanager.common.CommonActivity;

/**
 * Page container
 * CNcomment:页面容器
 */
public class TabBarExample extends TabActivity {

    // Image Array
    // CNcomment:图片数组
    private int myMenuRes[] = { R.drawable.hisil_tab1, R.drawable.tab2,
                                R.drawable.tab3, R.drawable.tab4
                              };

    // Tab view
    // CNcomment:标签页视图
    TabHost tabHost;

    // local Tab
    // CNcomment:本地标签
    TabSpec firstTabSpec;

    // SAMBA Tab
    // CNcomment:SAMBA标签
    //  TabSpec threeTabSpec;

    // NFS Tab
    // CNcomment:NFS标签
    //  TabSpec fourTabSpec;

    // Tab bar
    // CNcomment:标签栏
    private static TabWidget widget;

    public static boolean getMain_iv;
    private IntentFilter mIntenFilter = null;
    private BroadcastReceiver mReceiver = null;
    private static final String TAG = "TabBarExample";
    //jly 20140317
    private static ImageView main_iv;
    private static TextView main_tv;
    private static LinearLayout main_tab;
    //jly
    /**
     * Page display
     * CNcomment:页面显示
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);
        //jly 20140317
        main_tab = (LinearLayout)findViewById(R.id.LinearLayout01);
        main_iv = (ImageView)findViewById(R.id.main_iv);
        main_tv = (TextView)findViewById(R.id.main_tv);
        //jly
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        widget = (TabWidget) findViewById(android.R.id.tabs);
        //      widget .setGravity(View.FOCUS_LEFT);
        widget.setFocusable(false);
        widget.setSelected(false);
        widget.setClickable(false);
        firstTabSpec = tabHost.newTabSpec("tid1");
        //      threeTabSpec = tabHost.newTabSpec("tid3");
        //      fourTabSpec = tabHost.newTabSpec("tid4");
        // Create tabs
        // CNcomment:创建标签页
        firstTabSpec.setIndicator(getString(R.string.local_tab_title),
                                  getResources().getDrawable(myMenuRes[0]));
        /*firstTabSpec.getTag().get
        View view = tabs.getTabWidget().getChildAt(0);
        ((TextView)view.findViewById(android.R.id.title)).setTextSize(12);
        ((ImageView)view.findViewById(android.R.id.icon)).setPadding(0, -5, 0, 0);
        */
        //      threeTabSpec.setIndicator(getString(R.string.lan_tab_title),
        //              getResources().getDrawable(myMenuRes[2]));
        //      fourTabSpec.setIndicator(getString(R.string.nfs_tab_title),
        //              getResources().getDrawable(myMenuRes[3]));
        firstTabSpec.setContent(new Intent(this, MainExplorerActivity.class));
        //      threeTabSpec.setContent(new Intent(this, SambaActivity.class));
        //      fourTabSpec.setContent(new Intent(this, NFSActivity.class));
        tabHost.addTab(firstTabSpec);
        //      tabHost.addTab(threeTabSpec);
        //      tabHost.addTab(fourTabSpec);
        DisplayMetrics metric = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int densityDpi = metric.densityDpi;
        int count = widget.getChildCount();

        for (int i = 0; i < count; i++) {
            View view = widget.getChildTabViewAt(i);
            view.getLayoutParams().height = 80;
            TextView text = (TextView) view.findViewById(android.R.id.title);

            if (densityDpi < 182) {
                text.setTextSize(28);
            }
            else {
                text.setTextSize(20);
            }
        }

        mIntenFilter = new IntentFilter();
        mIntenFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mIntenFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mIntenFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mIntenFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mIntenFilter.addAction(ConnectivityManager.INET_CONDITION_ACTION);
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (0 != tabHost.getCurrentTab()) {
                    boolean bIsConnect = true;
                    final String action = intent.getAction();

                    if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                        final NetworkInfo networkInfo = (NetworkInfo) intent
                                                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                        bIsConnect = networkInfo != null
                                     && networkInfo.isConnected();
                    }
                    else if (action
                             .equals(ConnectivityManager.CONNECTIVITY_ACTION)
                             || action
                    .equals(ConnectivityManager.INET_CONDITION_ACTION)) {
                        NetworkInfo info = (NetworkInfo)(intent
                                                         .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO));
                        bIsConnect = info.isConnected();
                    }

                    ;

                    if (false == bIsConnect) {
                        Toast.makeText(
                            TabBarExample.this,
                            getString(R.string.network_error_exitnetbrowse),
                            Toast.LENGTH_LONG).show();
                        tabHost.setCurrentTab(0);
                    }
                }
            };
        };
        registerReceiver(mReceiver, mIntenFilter);
        MainExplorerActivity mainActivity = (MainExplorerActivity) getCurrentActivity();
        mainActivity.expandableListView.requestFocus();
        // Label switching, focus on the label, refresh the interface content
        // CNcomment:标签切换时，焦点在标签上，刷新界面内容
        /*tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                switch (tabHost.getCurrentTab()) {*/
        /*case 0:
            widget.requestFocus();
            CommonActivity.cancleToast();
            MainExplorerActivity mainActivity = (MainExplorerActivity) getCurrentActivity();
            if (mainActivity.isFileCut) {
                if (!mainActivity.getPathTxt().getText().toString()
                        .equals("")) {
                    mainActivity.updateList(true);
                    mainActivity.isFileCut = false;
                }
            }
            break;*/
        /*case 1:
            widget.requestFocus();
            CommonActivity.cancleToast();
            SambaActivity smbActivity = (SambaActivity) getCurrentActivity();
            if (smbActivity.IsNetworkDisconnect()) {
                tabHost.setCurrentTab(0);
            } else {
                if (smbActivity.isFileCut) {
                    if (!smbActivity.getPathTxt().getText().toString()
                            .equals("")
                            && !smbActivity
                                    .getPathTxt()
                                    .getText()
                                    .toString()
                                    .equals(smbActivity.getServerName())) {
                        smbActivity.updateList(true);
                        smbActivity.isFileCut = false;
                    }
                }
            }
            break;
        case 2:
            widget.requestFocus();
            CommonActivity.cancleToast();
            NFSActivity nfsActivity = (NFSActivity) getCurrentActivity();
            if (nfsActivity.IsNetworkDisconnect()) {
                tabHost.setCurrentTab(0);
            } else {
                if (nfsActivity.isFileCut) {
                    if (!nfsActivity.getPathTxt().getText().toString()
                            .equals("")) {
                        nfsActivity.updateList(true);
                        nfsActivity.isFileCut = false;
                    }
                }
            }
            break;*/
        /*}
        }
        });*/
    }

    public static ImageView getMain_iv() {
        return main_iv;
    }
    
    public static LinearLayout getMain_tab() {
        return main_tab;
    }


    public static TextView getMain_tv() {
        return main_tv;
    }



    public static TabWidget getWidget() {
        return widget;
    }

    /**
     * cancle Toast
     * CNcomment:取消提示
     */
    protected void onStop() {
        super.onStop();
        CommonActivity.cancleToast();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
