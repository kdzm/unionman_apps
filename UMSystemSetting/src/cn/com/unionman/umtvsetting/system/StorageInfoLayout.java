package cn.com.unionman.umtvsetting.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.unionman.umtvsetting.system.util.Constant;
import cn.com.unionman.umtvsetting.system.logic.MountInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StorageInfoLayout extends LinearLayout{
	private final static String TAG = "StorageInfoLayout";
    private final static String MOUNT_LABLE = "mountLable";
    private final static String MOUNT_TYPE = "mountType";
    private final static String MOUNT_PATH = "mountPath";
    private final static String MOUNT_NAME = "mountName";
	private Context mContext;
	private TextView mInternalTotalTxt;
	private TextView mInternalAvailTxt;
	private TextView mExternalTotalTxt;
	private TextView mExternalAvailTxt;
	private TextView mExternalDeviceNameTxt;
	private TextView mExternalDeviceTotalTxt;
	private TextView mExternalDeviceAvailTxt;
	private ProgressBar mInternalPg;
	private ProgressBar mExternalPg;
	private ProgressBar mExternalDevicePg;
	private Handler storageInfoHandler;
	private LinearLayout mInternalLayout, mExternalLayout;
	private LinearLayout mParentGroup;
	public StorageInfoLayout(Context context,Handler handler) {
		super(context);
		mContext = context;
		storageInfoHandler=handler;
	    LayoutInflater inflater = LayoutInflater.from(context);
	    View parent = inflater.inflate(R.layout.setting_storage_info, this);
	    mParentGroup = (LinearLayout) findViewById(R.id.setting_storage_bg_layout);
	    
	    mInternalLayout = (LinearLayout) findViewById(R.id.setting_storageinfointernal_layout);
	    mExternalLayout = (LinearLayout) findViewById(R.id.setting_storageinfoexternal_layout);
		mInternalTotalTxt = (TextView) findViewById(R.id.internal_total_memory_size_txt);
		mInternalAvailTxt = (TextView) findViewById(R.id.internal_avail_memory_size_txt);
		mExternalTotalTxt = (TextView) findViewById(R.id.external_total_memory_size_txt);
		mExternalAvailTxt = (TextView) findViewById(R.id.external_avail_memory_size_txt);
		mInternalPg       = (ProgressBar) findViewById(R.id.internal_storage_pg);
		mExternalPg  	  = (ProgressBar) findViewById(R.id.external_storage_pg);
		initData();
		extendStorageDeviceProcess();
	}

	private void initData(){
		setTextData(mInternalTotalTxt, StorageUtil.getTotalInternalMemorySize());
		setTextData(mInternalAvailTxt, StorageUtil.getAvailableInternalMemorySize());
		setTextData(mExternalTotalTxt, StorageUtil.getTotalExternalMemorySize());
		setTextData(mExternalAvailTxt, StorageUtil.getAvailableExternalMemorySize());
		mInternalPg.setProgress((int) ((StorageUtil.getTotalInternalMemorySize()-StorageUtil.getAvailableInternalMemorySize())
				                        *100/StorageUtil.getTotalInternalMemorySize()));
		mExternalPg.setProgress((int) ((StorageUtil.getTotalExternalMemorySize()-StorageUtil.getAvailableExternalMemorySize())
				                        *100/StorageUtil.getTotalExternalMemorySize()));
	}
	
	private void setTextData(TextView view, long size){
		long tmpsize = size/(1024*1024);
		if (tmpsize > 1024){
			double tmp1 = size;
			double tmp2 = tmp1/(1024*1024*1024);
			 java.text.DecimalFormat  df = new   java.text.DecimalFormat("#.##");   
			view.setText(df.format(tmp2)+"G");
		}else{
			view.setText(size/(1024*1024)+"M");
		}
		
	}
	
	private void reLoadData(){
		mParentGroup.removeAllViews();
		initData();
		mParentGroup.addView(mInternalLayout);
		mParentGroup.addView(mExternalLayout);
		extendStorageDeviceProcess();
	}
	
	private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
                || action.equals(Intent.ACTION_MEDIA_REMOVED)
            || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {

                if (action.equals(Intent.ACTION_MEDIA_REMOVED)
                || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                	reLoadData();
                   invalidate();
                   Log.d(TAG,"myleon... usbReceiver ACTION_MEDIA_REMOVED");
                }
                else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)){
                	reLoadData();
                	invalidate();
                	Log.d(TAG,"myleon... usbReceiver ACTION_MEDIA_MOUNTED");
                }
            }
        }
    };
    
    public void registerUSBroadcastReceiver(){
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(Intent.ACTION_UMS_DISCONNECTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        usbFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        usbFilter.addDataScheme("file");
        mContext.registerReceiver(usbReceiver, usbFilter);
    }
    
    public void unregisterUSBroadcastReceiver(){
    	mContext.unregisterReceiver(usbReceiver);
    }
    
	   @Override
	    public void onWindowFocusChanged(boolean hasFocus) {
	        if (hasFocus) {
	        	delayForDialog();
	        } else {
	        	storageInfoHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
	        }
	        super.onWindowFocusChanged(hasFocus);
	    }
	   
	    public void delayForDialog() {
	    	storageInfoHandler.removeMessages(Constant.DIALOG_DISMISS_BYTIME);
	        Message message = new Message();
	        message.what = Constant.DIALOG_DISMISS_BYTIME;
	        storageInfoHandler.sendMessageDelayed(message, Constant.DISPEAR_TIME_LONG);
	    }
	    
	    private void extendStorageDeviceViewAdd(ViewGroup parent, String deviceName, long totalSize, long availSize){
		    LayoutInflater inflater = LayoutInflater.from(mContext);
		    View deviceView = inflater.inflate(R.layout.storage_extend_device_item, null);
		    
		    mExternalDeviceNameTxt = (TextView) deviceView.findViewById(R.id.external_storage_device_name_txt);
		    mExternalDeviceTotalTxt = (TextView) deviceView.findViewById(R.id.external_storage_device_total_memory_size_txt);
			mExternalDeviceAvailTxt = (TextView) deviceView.findViewById(R.id.external_storage_device_avail_memory_size_txt);
			mExternalDevicePg  	  = (ProgressBar) deviceView.findViewById(R.id.external_storage_device_pg);
			
			mExternalDeviceNameTxt.setText(deviceName);
			setTextData(mExternalDeviceTotalTxt, totalSize);
			setTextData(mExternalDeviceAvailTxt, availSize);
			mExternalDevicePg.setProgress((int) ((totalSize-availSize)
                    *100/totalSize));
			
			LayoutParams param1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			param1.gravity = Gravity.CENTER;
			param1.topMargin = 30;
			parent.addView(deviceView, param1);
			Log.d(TAG,"myleon... extendStorageDeviceViewAdd");
	    }
	    
	    private void extendStorageDeviceProcess(){
	    	final List<Map<String, String>> li = getMountEquipmentList();
	    	for (int i = 0; i < li.size(); i++) {
                Log.d(TAG, li.get(i).get(MOUNT_NAME));
                StatFs stat = new StatFs(li.get(i).get(MOUNT_PATH));
                
                extendStorageDeviceViewAdd(mParentGroup, li.get(i).get(MOUNT_NAME), stat.getTotalBytes(), stat.getAvailableBytes());
            }
	    	
	    }
	    
	    /**
	     * get the list of mount equipment
	     *
	     * @return
	     */
	    private List<Map<String, String>> getMountEquipmentList() {
	        String[] mountType = mContext.getResources().getStringArray(
	                R.array.mount_type);
	        MountInfo info = new MountInfo(mContext);
	        List<Map<String, String>> childList = new ArrayList<Map<String, String>>();
	        for (int j = 0; j < mountType.length; j++) {
	            for (int i = 0; i < info.index; i++) {
	                if (info.type[i] == j) {
	                    if (info.path[i] != null && info.path[i].contains("/mnt")) {
	                        Map<String, String> map = new HashMap<String, String>();
	                        // map.put(MOUNT_DEV, info.dev[i]);
	                        map.put(MOUNT_TYPE, String.valueOf(info.type[i]));
	                        map.put(MOUNT_PATH, info.path[i]);
	                        // map.put(MOUNT_LABLE, info.label[i]);
	                        map.put(MOUNT_LABLE, "");
	                        String devname;
	                        if (info.path[i].contains("/storage")){
	                        	devname = new String(mContext.getString(R.string.storein));
	                        	devname = devname + info.partition[i];
	                        }else{
	                        	devname = new String(mContext.getString(R.string.udisk));
	                        	devname = devname + info.partition[i];
	                        }
	                        map.put(MOUNT_NAME, devname);
	                        childList.add(map);
	                    }
	                }
	            }
	        }
	        return childList;
	    }
	    
}
