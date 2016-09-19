
package com.um.launcher;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusSourceManager;
import com.um.launcher.data.AppAdapter;
import com.um.launcher.data.AppAdapter.LoadEndListener;
import com.um.launcher.thread.MyAppAync;
import com.um.launcher.thread.MyAppAyncThread;
import com.um.launcher.util.Constant;
import com.um.launcher.util.Util;
import com.um.launcher.view.CustomGridView;
import com.um.launcher.view.CustomProgressDialog;
import com.um.launcher.view.MyAppScrollLayout;
import com.um.launcher.view.MyPopWindow;

/**
 * MY Application
 */
@SuppressLint("UseSparseArrays")
public class MyAppActivity extends Activity implements OnItemClickListener,
        OnItemSelectedListener, LoadEndListener {
    private static final String TAG = "MyAppActivity";
    public static final int REFRESH_PAGE = 100;
    public static final int UPDATE_VIEW = 101;
    // private static final int LINE = 3;
    // private static final int LIST = 6;
    // asynchronous data loading
    private MyAppAyncThread mMAppAync = null;
    private MyAppScrollLayout mRootLayout;
    public static boolean isSnapRight = false;
    // Is this the first time into the activity
    private boolean isfirst = true;
    private ResolveInfo mResolveInfo = null;
    // list of CustomGridView
    private List<CustomGridView> mCustomeGridViewlist = null;
    // pop window when click the menu
    private MyPopWindow mMultiPop;
    private CustomProgressDialog mCustomProgressDialog = null;
    // image of left arrow
    private ImageView mLeftArrowImg = null;
    // image of right arrow
    private ImageView mRightArrowImg = null;
    // text of page
    private TextView mPageText;
    // text of app title
    private TextView mAppTitleText;
    // text of info title
    private TextView mInfoTitleText;
    // the current page number
    private int mCurrentNum = 1;
    private int mPageNumber = 0;
    // application list
    private List<ResolveInfo> mAppList = null;
    // list for every gridview
    private List<ResolveInfo> mGridAppList = null;
    private MyApplication mApplication = null;

    /**
     * Registered receiver to monitor changes of application
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "onReceive =intent:" + intent.getAction());
            }
            mApplication = (MyApplication) context.getApplicationContext();
            mAppList = mApplication.getResolveInfos();
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                mApplication.clearList();
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)
                    || intent.getAction()
                            .equals(Intent.ACTION_PACKAGE_REPLACED)) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "inner receiver app added ");
                }
                mApplication.clearList();
            }
            if (null != mRootLayout) {
                mRootLayout.setToScreen(0);
                mRootLayout.removeAllViews();
            }
            updataApp(true);
        }
    };

    /**
     * clear data
     */
    private Handler mDataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Util.CLEAR_USER_DATA:
                    Util.showToast(MyAppActivity.this, R.string.clear_data_suc);
                    break;
                case Util.NOT_CLEAR_USER_DATA:
                    Util.showToast(MyAppActivity.this, R.string.clear_data_fail);
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * control the title bar to hide / appear
     */
    public void setTitleViewGoneOrVisible(boolean show) {
        if (show) {
            if (mCurrentNum == 1) {
                mLeftArrowImg.setVisibility(View.GONE);
            } else {
                mLeftArrowImg.setVisibility(View.VISIBLE);
            }
            mRightArrowImg.setVisibility(View.VISIBLE);
            // mPageText.setVisibility(View.VISIBLE);
            mAppTitleText.setVisibility(View.VISIBLE);
            mInfoTitleText.setVisibility(View.VISIBLE);
        } else {
            mLeftArrowImg.setVisibility(View.GONE);
            mRightArrowImg.setVisibility(View.GONE);
            // mPageText.setVisibility(View.GONE);
            mAppTitleText.setVisibility(View.GONE);
            mInfoTitleText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onCreat");
        }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.my_app_main);
        initView();
        initData();
    }

    /**
     * initialization of data
     */
    private void initData() {
        mApplication = (MyApplication) MyAppActivity.this.getApplication()
                .getApplicationContext();
        mApplication.setHandler(mFinishHandler);
        updataApp(true);
        registReceiver();
    }

    /**
     * initialization of widget
     */
    private void initView() {
        mRootLayout = (MyAppScrollLayout) findViewById(R.id.root);
        LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        params.leftMargin = 100;
        params.rightMargin = 100;
        params.topMargin = 147;
        params.bottomMargin = 50;
        mRootLayout.setLayoutParams(params);
        mLeftArrowImg = (ImageView) this.findViewById(R.id.left_arrow);
        mRightArrowImg = (ImageView) this.findViewById(R.id.right_arrow);
        mPageText = (TextView) findViewById(R.id.page_num);
        mAppTitleText = (TextView) findViewById(R.id.all_app_title);
        mInfoTitleText = (TextView) findViewById(R.id.info_title);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!mRootLayout.isFinished()) {
            return true;
        }
        CustomGridView focusedView = (CustomGridView) mRootLayout
                .getChildAt(mRootLayout.getCurrentScreen());
        ResolveInfo info = (ResolveInfo) focusedView.getSelectedItem();
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
            case KeyEvent.KEYCODE_MENU:
                mMultiPop = new MyPopWindow(MyAppActivity.this, getResources()
                        .getStringArray(R.array.app_management), info);
                mMultiPop.show();
                mMultiPop.init();
                break;
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
            case KeyEvent.KEYCODE_PROG_RED:
                // to stop
                Util.forceStopPackage(MyAppActivity.this,
                        info.activityInfo.applicationInfo.packageName);
                break;
            case KeyEvent.KEYCODE_PROG_GREEN:
                // delete the default settings
                Util.clearDefault(MyAppActivity.this,
                        info.activityInfo.applicationInfo.packageName);
                break;
            case KeyEvent.KEYCODE_PROG_YELLOW:
                // clear data
                Util.clearData(MyAppActivity.this,
                        info.activityInfo.applicationInfo, mDataHandler);
                break;
            case KeyEvent.KEYCODE_PROG_BLUE:
                // uninstall
                Util.unLoad(MyAppActivity.this, info);
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onPause");
        }
        super.onPause();
        isSnapRight = false;
        if (mMultiPop != null && mMultiPop.isShowing()) {
            mMultiPop.dismiss();
        }
    }

    @Override
    protected void onResume() {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "current num --->" + mCurrentNum);
        }
        super.onResume();
        // initView();
    }

    @Override
    protected void onStop() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onStop");
        }
        // dismissDialog();
        isSnapRight = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (Constant.LOG_TAG) {
            Log.d(TAG, "onDestroy");
        }
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (null != mAppList && mAppList.size() > 0) {
            mAppList.clear();
            mAppList = null;
        }
        if (null != mApplication) {
            mApplication.clearList();
            mApplication = null;
        }

    }

    /**
     * radio listeners
     */
    private void registReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        intentFilter.addDataScheme("package");
        registerReceiver(mReceiver, intentFilter);
    }

    /**
     * refreshFlag if need the pop-up dialog box
     */
    private void updataApp(boolean showDialogFlag) {
        if (mMultiPop != null && mMultiPop.isShowing()) {
            mMultiPop.dismiss();
        }
        if (mMAppAync != null) {
            mMAppAync.interrupt();
            mMAppAync = null;
        }
        if (null != mRootLayout) {
            mRootLayout.removeAllViews();
        }
        if (null != mCustomeGridViewlist && mCustomeGridViewlist.size() > 0) {
            mCustomeGridViewlist.clear();
            mCustomeGridViewlist = null;
        }
        showProgress();

        mMAppAync = new MyAppAyncThread(MyAppActivity.this);
        mAppList = mApplication.getResolveInfos();
        if (mAppList == null || mAppList.size() == 0) {
            isfirst = true;
            mMAppAync.myStart(Util.ALL_APP);
        } else {
            bindDataToContainer();
        }
    }

    /**
     * bind the application list
     */
    private void bindDataToContainer() {
        int lineNumber = 3;
        int listNumber = 6;
        mPageNumber = (mAppList.size() % (lineNumber * listNumber) == 0 ? 0 : 1)
                + (mAppList.size() / (lineNumber * listNumber));
        if (Constant.LOG_TAG) {
            Log.i(TAG, "pageNumber---->" + mPageNumber);
        }
        mCustomeGridViewlist = new ArrayList<CustomGridView>();
        for (int i = 0; i < mPageNumber; i++) {
            CustomGridView appPage = new CustomGridView(MyAppActivity.this,
                    mFinishHandler);
            appPage.setFocusable(true);
            appPage.setFocusableInTouchMode(true);
            appPage.setSelector(R.drawable.white_border);
            appPage.setOnItemClickListener(MyAppActivity.this);
            appPage.setOnItemSelectedListener(MyAppActivity.this);
            mGridAppList = new ArrayList<ResolveInfo>();
            for (int j = lineNumber * listNumber * i; j < lineNumber
                    * listNumber * (i + 1); j++) {
                if (j < mAppList.size()) {
                    mGridAppList.add(mAppList.get(j));
                } else {
                    break;
                }
            }
            // get the "i" page data
            appPage.setVerticalSpacing(6);
            appPage.setFocusable(true);
            appPage.setFocusableInTouchMode(true);
            appPage.setHorizontalSpacing(6);
            appPage.setNumColumns(6);
            AppAdapter adpter = new AppAdapter(MyAppActivity.this,
                    mGridAppList, i, mPageNumber);
            adpter.setmLoadEndListener(MyAppActivity.this);
            appPage.setAdapter(adpter);
            if (Constant.LOG_TAG) {
                Log.d(TAG,
                        "appPage=" + appPage + "---childcount = "
                                + appPage.getChildCount());
            }
            mRootLayout.addView(appPage);
            mCustomeGridViewlist.add(appPage);
        }
        mFinishHandler.sendEmptyMessage(REFRESH_PAGE);
    }

    /**
     * set current focus
     */
    private void resetFocus() {
        mCurrentNum = 1;
        if (null != mCustomeGridViewlist) {
            mCustomeGridViewlist.get(0).requestFocusFromTouch();
            mCustomeGridViewlist.get(0).setSelection(0);
            mPageText.setText((mRootLayout.getCurrentScreen() + 1) + "/"
                    + mPageNumber);
            mLeftArrowImg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            if (mCustomeGridViewlist != null && mCustomeGridViewlist.size() > 1
                    && isfirst) {
                if (Constant.LOG_TAG) {
                    Log.d(TAG, "CustomeGridViewlist.get(0).getChildCount()="
                            + mCustomeGridViewlist.get(0).getChildCount());
                }
                resetFocus();
                isfirst = false;
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }

    private Handler mFinishHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    finish();
                    break;
                case 1:
                    break;
                case REFRESH_PAGE:
                    resetImg();
                    break;
                case UPDATE_VIEW:
                    updataApp(true);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    /**
     * show page number and set the arrow show or gone
     */
    private void resetImg() {
        if (null != mPageText) {
            mPageText.setText((mRootLayout.getCurrentScreen() + 1) + "/"
                    + mPageNumber);
        }
        if (mRootLayout.getCurrentScreen() == 0) {
            mLeftArrowImg.setVisibility(View.GONE);
            mRightArrowImg.setVisibility(mPageNumber == 1 ? View.GONE : View.VISIBLE);

        } else if (mRootLayout.getCurrentScreen() == mPageNumber - 1) {
            mLeftArrowImg.setVisibility(View.VISIBLE);
            mRightArrowImg.setVisibility(View.GONE);
        } else {
            mLeftArrowImg.setVisibility(View.VISIBLE);
            mRightArrowImg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * display the progress dialog
     */
    public void showProgress() {
        if (mCustomProgressDialog == null) {
            mCustomProgressDialog = CustomProgressDialog.createDialog(this);
        }
        // set whether ProgressDialog can press return key to cancel
        mCustomProgressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        // let ProgressDialog display
        setTitleViewGoneOrVisible(false);
        mCustomProgressDialog.show();
    }

    /**
     * let the dialog dismiss
     */
    public void dismissDialog() {
        if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
            setTitleViewGoneOrVisible(true);
            mCustomProgressDialog.dismiss();
            mCustomProgressDialog = null;
        }
    }

    /**
     * show next Screen
     */
    public void snapToNextScreen() {
        mRootLayout.snapToScreen(mRootLayout.getCurrentScreen() + 1);
    }

    /**
     * show previous Screen
     */
    public void snapToPreScreen() {
        mRootLayout.snapToScreen(mRootLayout.getCurrentScreen() - 1);
    }

    public boolean isFinished() {
        return mRootLayout.isFinished();
    }

    public static CusSourceManager getSourceManager() {
        return UmtvManager.getInstance().getSourceManager();
    }
	

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        String pkg = null;
        String cls = null;
        ResolveInfo res = (ResolveInfo) arg0.getAdapter().getItem(arg2);
        // the package name and the Activity of the application
        if (null == res) {
            return;
        }
        pkg = res.activityInfo.packageName;
        cls = res.activityInfo.name;
        if (TextUtils.isEmpty(pkg) || TextUtils.isEmpty(cls)) {
            return;
        }
        if(pkg.equalsIgnoreCase("com.um.dvb")
        ||pkg.equalsIgnoreCase("com.um.dvbsettings")
        ||pkg.equalsIgnoreCase("com.um.epg")
        ||pkg.equalsIgnoreCase("com.um.dvbsearch")
        ||pkg.equalsIgnoreCase("com.um.dvtca")
        ||pkg.equalsIgnoreCase("com.um.com.um.umsmcupdate")
        ||pkg.equalsIgnoreCase("com.um.tfca")
        ||pkg.equalsIgnoreCase("com.um.dvbsettings")
        )
        {
            Log.i(TAG, "um dvb apk");
			//int mCurrentSourceIdx = getSourceManager().getCurSourceId(0);;

			//getSourceManager().deselectSource(mCurrentSourceIdx, true);
			//getSourceManager().selectSource(1, 0);//SOURCE_DVBC   = 1
        }
		else
		{
            Log.i(TAG, "not um dvb apk");
		}
        try {
            ComponentName componentName = new ComponentName(pkg, cls);
            Intent mIntent = new Intent(Intent.ACTION_MAIN);
            mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mIntent.setComponent(componentName);
            this.startActivity(mIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    /**
     * when currentPage is equals to mPageNumber-1 that means all the data has
     * loaded
     */
    @Override
    public void onEndListerner(int currentPage) {
        if (Constant.LOG_TAG) {
            Log.i(TAG, "onEndListerner--->" + currentPage);
            Log.i(TAG, "pageNumber--->" + mPageNumber);
        }
        if (currentPage == mPageNumber - 1) {
            dismissDialog();
        }
    }

    public int getCurrentNum() {
        return mCurrentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.mCurrentNum = currentNum;
    }
}
