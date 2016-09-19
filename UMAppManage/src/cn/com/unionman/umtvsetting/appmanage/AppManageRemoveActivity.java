package cn.com.unionman.umtvsetting.appmanage;

import java.util.List;

import cn.com.unionman.umtvsetting.appmanage.widget.CustomProgressDialog;

import cn.com.unionman.umtvsetting.appmanage.MyApplication;
import cn.com.unionman.umtvsetting.appmanage.thread.MyAppAync;
import cn.com.unionman.umtvsetting.appmanage.util.Constant;
import cn.com.unionman.umtvsetting.appmanage.util.Util;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.unionman.umtvsetting.appmanage.R;
import cn.com.unionman.umtvsetting.appmanage.adapter.AppListAdapter;
import cn.com.unionman.umtvsetting.appmanage.adapter.AppListAdapter.LoadEndListener;

import android.content.pm.IPackageDeleteObserver;

public class AppManageRemoveActivity  extends Activity implements LoadEndListener{
	private static final String TAG = "AppManageRemoveActivity";
    private View appbgView;
	private ListView appListView;
	private List<ResolveInfo> mAppList;
	private MyAppAync mMAppAync = null;
	private MyApplication mApplication = null;
	private CustomProgressDialog mCustomProgressDialog = null;
	private TextView mAppListSize;
	
    private  AlertDialog mAlertDialog;
	private  Button mSystemOKBtn;
	private  Button mSystemCancelBtn;    
    private  CountDownTimer mCountDownTimer = new CountDownTimer(1000*5 + 100, 1000) {
        public void onTick(long millisUntilFinished) {
            String str = getString(R.string.cancel) + "(" + millisUntilFinished/1000 + "s)";
            mSystemCancelBtn.setText(str);
        }

        public void onFinish() {
            mAlertDialog.dismiss();
        }
    };
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_remove);
		mApplication = (MyApplication)getApplicationContext();
		mApplication.setHandler(mFinishHandler);
		appbgView = findViewById(R.id.app_bg_layout);
		appListView = (ListView) findViewById(R.id.app_remove_list);
			appListView.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
					if (arg2.getAction() == KeyEvent.ACTION_DOWN) {  
						//�а������ʱ������ʱ30s��ʧ����Ϣ						
						delay();
						Log.i(TAG,"appListView onKeyDown");
					}
					return false;
				}
			});
		mAppListSize = (TextView)findViewById(R.id.app_list_count_txt);
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

        mMAppAync = new MyAppAync(AppManageRemoveActivity.this, showDialogFlag);
        mAppList = mApplication.getResolveInfos();
        
      
        if (mAppList == null || mAppList.size() == 0) {
            mMAppAync.execute(Util.ALL_APP_WITHOUT_SYSTEM_APP);
        } else {
            bindDataToContainer();
        }
    }
 
    private void updataAppWithoutApps() {
    	
        if (mMAppAync != null) {
            mMAppAync.cancel(true);
            mMAppAync = null;
        }
        mAppList = mApplication.getResolveInfos();
        bindDataToContainer();
    }
    
    private void bindDataToContainer() {
    	hideProgress();
    	appbgView.setVisibility(View.VISIBLE);
    	AppListAdapter adpter = new AppListAdapter(AppManageRemoveActivity.this, mAppList, Util.APP_lIST_UNLOAD);
    	adpter.setmLoadEndListener(AppManageRemoveActivity.this);
    	appListView.setAdapter(adpter);
    	appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    		@Override
    		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
    				long arg3) {
    			Log.e(TAG, "leon... onItemClick");
    			ResolveInfo info = (ResolveInfo) mAppList.get(arg2);
    			unLoadApp(info);
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
                case Util.UPDATE_VIEW_WHITHOUT_APPS:
                	updataAppWithoutApps();
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
    
    private void unLoadApp(final ResolveInfo info){
    	Log.e(TAG, "leon... unLoadApp");
//    	Util.unLoad(AppManageRemoveActivity.this, info);
    	
        if (Util.filterApp(info.activityInfo.applicationInfo)) {
        	Util.showToast(AppManageRemoveActivity.this, R.string.no_del_sys_app);
        } else {

   	      AlertDialog.Builder builder =new AlertDialog.Builder(AppManageRemoveActivity.this);
	      LayoutInflater factory = LayoutInflater.from(AppManageRemoveActivity.this);
	      View myView = factory.inflate(R.layout.user_back,null);
	        mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
	        mSystemCancelBtn = (Button) myView.findViewById(R.id.user_back_cancel);
		      mSystemCancelBtn.setFocusable(true);
		      mSystemCancelBtn.requestFocus();		      
	       mAlertDialog = builder.create();
	      mAlertDialog.show();	
	      mAlertDialog.getWindow().setContentView(myView);
	      mCountDownTimer.start();
		      mSystemOKBtn.setOnClickListener(new OnClickListener() {
		    	  	
		          @Override
		          public void onClick(View arg0) {
/*		      		Intent intent = new Intent();
		    	    intent.setAction(Intent.ACTION_DELETE);
		    	    intent.setData(Uri.parse("package:"+info.activityInfo.applicationInfo.packageName));
		    		startActivity(intent);*/
		              PackageDeleteObserver observer = new PackageDeleteObserver(AppManageRemoveActivity.this);  
		              getPackageManager().deletePackage(info.activityInfo.applicationInfo.packageName, observer, 0);  		        	  
		              mAlertDialog.dismiss();
		              mCountDownTimer.cancel();
		          }
		      });
		      mSystemCancelBtn.setOnClickListener(new OnClickListener() {
		          @Override
		          public void onClick(View arg0) {
		        	  mAlertDialog.dismiss();
		        	  mCountDownTimer.cancel();
		          }
		      });  	
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
        finishHandle.removeMessages(Constant.ACTIVITY_FINISH);
        Message message = new Message();
        message.what = Constant.ACTIVITY_FINISH;
        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME_30s);
    }
    
    
    class PackageDeleteObserver extends IPackageDeleteObserver.Stub {  
        private Context mcontext;  
        public PackageDeleteObserver(Context context){  
            this.mcontext=context;  
        }  
        public void packageDeleted(String packageName, int returnCode) {  
            Looper.prepare();  
            if (returnCode == 1) {  
                Toast.makeText(mcontext, getResources().getString(R.string.uninstall_success), Toast.LENGTH_LONG).show();  
            } else {  
                Toast.makeText(mcontext, getResources().getString(R.string.uninstall_fail), Toast.LENGTH_LONG).show();  
            }  
            Looper.loop();  
        }  
    }  
    
}
