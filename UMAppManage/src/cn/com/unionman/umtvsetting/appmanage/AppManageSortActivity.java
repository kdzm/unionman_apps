package cn.com.unionman.umtvsetting.appmanage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ListView;

import java.util.List;

import cn.com.unionman.umtvsetting.appmanage.adapter.AppListAdapter;
import cn.com.unionman.umtvsetting.appmanage.adapter.AppListAdapter.LoadEndListener;
import cn.com.unionman.umtvsetting.appmanage.thread.MyAppAync;
import cn.com.unionman.umtvsetting.appmanage.util.Constant;
import cn.com.unionman.umtvsetting.appmanage.util.Util;
import cn.com.unionman.umtvsetting.appmanage.widget.CustomProgressDialog;

public class AppManageSortActivity  extends Activity implements LoadEndListener, View.OnClickListener{
	private static final String TAG = "AppManageSortActivity";
    private View appbgView;
	private ListView appListView;
	private List<ResolveInfo> mAppList;
	private MyAppAync mMAppAync = null;
	private MyApplication mApplication = null;
	private CustomProgressDialog mCustomProgressDialog = null;
	private View mSortName, mSortSize;
	private AppListAdapter madpter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_sort);
		mApplication = (MyApplication)getApplicationContext();
		mApplication.setHandler(mFinishHandler);
		appbgView = findViewById(R.id.app_sort_bg_layout);
		appListView = (ListView) findViewById(R.id.app_sort_list);
			appListView.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
					if (arg2.getAction() == KeyEvent.ACTION_DOWN) {  
						//锟叫帮拷锟斤拷锟斤拷锟绞憋拷锟斤拷锟斤拷锟绞�0s锟斤拷失锟斤拷锟斤拷息	      
					delay();
					Log.i(TAG,"appListView onKeyDown");
					}
					return false;
				}
			});
		mSortName = findViewById(R.id.app_sort_name_txt);
		mSortSize = findViewById(R.id.app_sort_size_txt);
		mSortName.setSelected(true);
		mSortName.setOnClickListener(this);
		mSortSize.setOnClickListener(this);
		registReceiver();
		updataApp(true);
		
	}
	
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mApplication = (MyApplication) context.getApplicationContext();
            mAppList = mApplication.getResolveInfos();
            if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
                mApplication.clearList();
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)
                    || intent.getAction()
                            .equals(Intent.ACTION_PACKAGE_REPLACED)) {
                mApplication.clearList();
            }
            updataApp(true);
        }
    };
    
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
    	
        if (mMAppAync != null) {
            mMAppAync.cancel(true);
            mMAppAync = null;
        }

        showProgress();

        mMAppAync = new MyAppAync(AppManageSortActivity.this, showDialogFlag);
        mAppList = mApplication.getResolveInfos();
        if (mAppList == null || mAppList.size() == 0) {
            mMAppAync.execute(Util.ALL_APP_WITHOUT_SYSTEM_APP);
       
        } else {
            bindDataToContainer();
     
        }
    }
 
    private void bindDataToContainer() {

    	hideProgress();
    	appbgView.setVisibility(View.VISIBLE);
    	madpter = new AppListAdapter(AppManageSortActivity.this, mAppList, Util.APP_lIST_SORT);
    	madpter.setmLoadEndListener(AppManageSortActivity.this);
    	appListView.setAdapter(madpter);
    }
    
    public void showProgress() {
        if (mCustomProgressDialog == null) {
            mCustomProgressDialog = new CustomProgressDialog(this);
        }

        mCustomProgressDialog.showLoading();
    }

    /**
     * let the dialog dismiss
     */
    public void hideProgress() {
        if (mCustomProgressDialog != null) {
            mCustomProgressDialog.hideLoading();
            mCustomProgressDialog = null;
        }
    }
    
    private Handler mFinishHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Util.UPDATE_VIEW:
                    updataApp(true);
                    break;
                case Util.UPDATE_VIEW_WHITHOUT_APPS:
                	updataAppWithoutApps();
                	break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };
private void updataAppWithoutApps() {
    	
        if (mMAppAync != null) {
            mMAppAync.cancel(true);
            mMAppAync = null;
        }
        mAppList = mApplication.getResolveInfos();
        bindDataToContainer();
    }
    @Override
    protected void onDestroy() {
    	super.onDestroy();
        if (null != mAppList && mAppList.size() > 0) {
            mAppList.clear();
            mAppList = null;
        }
        if (null != mApplication) {
            mApplication.clearList();
            mApplication = null;
        }
        
        unregisterReceiver(mReceiver);
    };

    @Override
    public void onEndListerner(int arg0) {
    	Log.e(TAG, "leon... onEndListerner");
    	madpter.notifyDataSetChanged();
    }
    
    @Override
    public void onClick(View arg0) {
    	
    	switch (arg0.getId()){
    		case R.id.app_sort_name_txt:
    			madpter.setAppSortType(AppListAdapter.APPSORT_NAME_TYPE);
    			mSortSize.setSelected(false);
    			mSortName.setSelected(true);
    			break;
    		case R.id.app_sort_size_txt:
    			madpter.setAppSortType(AppListAdapter.APPSORT_SIZE_TYPE);
    			mSortName.setSelected(false);
    			mSortSize.setSelected(true);
    			break;
    		default:
    			break;
    	}
    	
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            delay();
        } else {
            finishHandle.removeMessages(Constant.ACTIVITY_FINISH);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * handler of finish activity
     */
    private Handler finishHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == Constant.ACTIVITY_FINISH)
                finish();
        };
    };

    /**
     * set delay time to finish activity
     */
    public void delay() {
    	Log.i(TAG,"calling delay()");
        finishHandle.removeMessages(Constant.ACTIVITY_FINISH);
        Message message = new Message();
        message.what = Constant.ACTIVITY_FINISH;
        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME_30s);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.i(TAG,"activity onKeyDown");
		delay();
		return super.onKeyDown(keyCode, event);
	}
    
    
}
