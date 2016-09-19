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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.com.unionman.umtvsetting.appmanage.adapter.AppListAdapter;
import cn.com.unionman.umtvsetting.appmanage.adapter.AppListAdapter.LoadEndListener;
import cn.com.unionman.umtvsetting.appmanage.thread.MyAppAync;
import cn.com.unionman.umtvsetting.appmanage.util.Util;
import cn.com.unionman.umtvsetting.appmanage.widget.CustomProgressDialog;

public class AppManageUpdateActivity  extends Activity implements LoadEndListener{
	private static final String TAG = "AppManageUpdateActivity";
    private View appbgView;
	private ListView appListView;
	private List<ResolveInfo> mAppList;
	private MyAppAync mMAppAync = null;
	private MyApplication mApplication = null;
	private CustomProgressDialog mCustomProgressDialog = null;
	private TextView mAppListSize;
	private UpdateHelper mUpdateHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_update);
		mApplication = (MyApplication)getApplicationContext();
		mApplication.setHandler(mFinishHandler);
		appbgView = findViewById(R.id.app_update_bg_layout);
		appListView = (ListView) findViewById(R.id.app_update_list);
		mAppListSize = (TextView)findViewById(R.id.app_update_list_count_txt);
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

        mMAppAync = new MyAppAync(AppManageUpdateActivity.this, showDialogFlag);
        mAppList = mApplication.getResolveInfos();
        if (mAppList == null || mAppList.size() == 0) {
            mMAppAync.execute(Util.ALL_APP);
        } else {
            bindDataToContainer();
        }
    }
 
    private void bindDataToContainer() {
    	hideProgress();
    	appbgView.setVisibility(View.VISIBLE);
    	AppListAdapter adpter = new AppListAdapter(AppManageUpdateActivity.this, mAppList, Util.APP_lIST_UPDATE);
    	adpter.setmLoadEndListener(AppManageUpdateActivity.this);
    	appListView.setAdapter(adpter);
    	appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
    				long arg3) {
    			Log.e(TAG, "leon... onItemClick");
    			ResolveInfo info = (ResolveInfo) mAppList.get(arg2);
    			updateApp(info);
    		}
    	});
    	mAppListSize.setText(String.valueOf(mAppList.size()));
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
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };
    
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
    }
    
    private void updateApp(ResolveInfo info){
    	checkVersion(info);
    }
    
    public void checkVersion(ResolveInfo info){    
    	mUpdateHelper = new UpdateHelper(AppManageUpdateActivity.this, info);
    	mUpdateHelper.updateStart();
    } 
    
}
