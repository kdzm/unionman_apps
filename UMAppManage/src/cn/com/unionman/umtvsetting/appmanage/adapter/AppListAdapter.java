package cn.com.unionman.umtvsetting.appmanage.adapter;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.PackageManager.NameNotFoundException;
import cn.com.unionman.umtvsetting.appmanage.R;
import cn.com.unionman.umtvsetting.appmanage.util.Util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppListAdapter extends BaseAdapter{
	
	private static final String TAG = "AppListAdapter"; 
	public static final int APPSORT_NAME_TYPE = 1;
	public static final int APPSORT_SIZE_TYPE = 2;
	private int mlisttype = 0;
	private Map<String, SoftReference<Bitmap>> mImageCache = new HashMap<String, SoftReference<Bitmap>>();
	private List<ResolveInfo> mAppList = new ArrayList<ResolveInfo>();
	private ArrayList<View> mActive = new ArrayList<View>();
    private Context mContext = null;
    private LayoutInflater mLayoutInflater = null;
    private PackageManager mPackManager;
    private long packageSize = 0;
    private LoadEndListener mLoadEndListerner;
    private List<AppEntry> mListAppEntry;
    private int mAppSortType = APPSORT_NAME_TYPE;
    private boolean mAppSortSizeFinish = false;
    private long packageSizeSort = 0;
    private boolean mAppLoadSizeFinish = false;
    
	public AppListAdapter(Context context, List<ResolveInfo> applist, int listtype) {
		mContext = context;
		mAppList = applist;
		mlisttype = listtype;
		mLayoutInflater = LayoutInflater.from(mContext);
		mPackManager = mContext.getPackageManager();
		mListAppEntry = new ArrayList<AppEntry>(applist.size());
        Message msg = mMainHandler.obtainMessage(
                MainHandler.MSG_PACKAGE_SIZE_LOAD, 0);
        mMainHandler.sendMessage(msg);
	}
	
	
    public void setmLoadEndListener(LoadEndListener mLoadEndListerner) {
        this.mLoadEndListerner = mLoadEndListerner;
    }
    
    public interface LoadEndListener {
        void onEndListerner(int currentPage);
    }
    
    private void appSizeLoad(){
    	if (mAppList != null){
    		for (int i = 0; i < mAppList.size(); i++){
    			ResolveInfo info = mAppList.get(i);
    			requestAppSize(info);
    			AppEntry entry = new AppEntry();
    			entry.position = i;
    			entry.packageName = info.activityInfo.packageName;
    			mListAppEntry.add(entry);
    		}
    	}
    }
    
    public void setAppSortType(int sortType){
    	mAppSortType = sortType;
    	if (mAppSortType == APPSORT_SIZE_TYPE){
            Message msg = mMainHandler.obtainMessage(
                    MainHandler.MSG_PACKAGE_SIZE_SORT, 0);
            mMainHandler.sendMessage(msg);
    	}else{
        	if (mLoadEndListerner != null){
        		mLoadEndListerner.onEndListerner(0);
        	}
    	}
    }
    
    public void sortByAppSize(){
    	
    	if ((mListAppEntry != null) && (mAppSortSizeFinish == false)){
        	Collections.sort(mListAppEntry);
        	mAppSortSizeFinish = true;
    	}
    	
    	if (mLoadEndListerner != null){
    		mLoadEndListerner.onEndListerner(0);
    	}
    }
    
    private int getInfoPosition(int position){
    	int retpos = 0;
    	
    	if (mAppSortType == APPSORT_NAME_TYPE){
    		retpos = position;
    	}else{
    		AppEntry node = mListAppEntry.get(position);
    		retpos = node.position;
    	}
    	
    	return retpos;
    }
    
	@Override
	public int getCount() {
        if (null != mAppList) {
            return mAppList.size();
        }
        return 0;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public Object getItem(int position) {
		
		return mAppList.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
        ViewHolder holder = null;
        if (convertView == null) {
        	convertView = mLayoutInflater.inflate(R.layout.app_list_item, null);
            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView
                    .findViewById(R.id.app_icon_img);
            holder.appName = (TextView) convertView
                    .findViewById(R.id.app_name_txt);
            holder.appSize = (TextView) convertView
                    .findViewById(R.id.app_size_txt);
            holder.appVersion = (TextView) convertView
                    .findViewById(R.id.app_version_txt);
            holder.appControl = (Button) convertView
                    .findViewById(R.id.app_control_btn);
            convertView.setTag(holder);
            
            mActive.add(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        // binding data
        try {
        	int realPosition = getInfoPosition(position);
            ResolveInfo info = mAppList.get(realPosition);
            holder.packageName = info.activityInfo.packageName;
            
            //requestAppSize(info);
            
            Bitmap bitmap = getBitmapByPath(info.activityInfo.packageName);
            if (bitmap != null) {
                holder.appIcon.setBackgroundDrawable(new BitmapDrawable(bitmap));
            } else {
            	initItemIcon(info, position, holder);
                holder.appIcon.setBackgroundDrawable(new BitmapDrawable(
                        getBitmapByPath(info.activityInfo.packageName)));
            }
            
            holder.appName.setText(info.loadLabel(mPackManager).toString());
            holder.appSize.setText(formateFileSize(getAppsize(info.activityInfo.packageName)));
            holder.appVersion.setText(getAppVersion(info));
            if (mlisttype == Util.APP_lIST_UPDATE){
            	holder.appControl.setText(R.string.update);
            }else if (mlisttype == Util.APP_lIST_UNLOAD){
            	holder.appControl.setText(R.string.unload);
            }else{
            	holder.appControl.setVisibility(View.INVISIBLE);
            }
            
        } catch (Exception e) {
            holder.appName.setText("");
            holder.appSize.setText("");
            holder.appVersion.setText("");
            holder.appIcon.setBackgroundDrawable(null);
        }
        
        Log.e(TAG, "leon... mAppList.size() - 1:"+mAppList.size()+",position:"+position);
        if (position == mAppList.size() - 1){
        	if (mLoadEndListerner != null){
        		Log.e(TAG, "leon... loading finish");
        		mLoadEndListerner.onEndListerner(0);
        	}
        }
        
		return convertView;
	}
	
	private String getAppVersion(ResolveInfo info){
		try{
			PackageInfo packageinfo = mPackManager.getPackageInfo(info.activityInfo.packageName, 0);
			return packageinfo.versionName;
		}catch(Exception ex){
            Log.e(TAG, "NoSuchMethodException") ;  
            ex.printStackTrace() ;  
		}
		 return null;
	}
	
	private void requestAppSize(ResolveInfo info){
		try{
			mPackManager.getPackageSizeInfo(info.activityInfo.packageName, new PkgSizeObserver());
		}catch(Exception ex){
            Log.e(TAG, "NoSuchMethodException") ;  
            ex.printStackTrace() ;  
		}
	}
	
	private long getAppsize(String packageName){
		long packageSize = 0;
		AppEntry entry = getAppEntry(packageName);
    	if (entry != null){
    		packageSize = entry.appSize; 
    	}
    	
    	return packageSize;
	}
	
    //系统函数，字符串转换 long -String (kb)  
    private String formateFileSize(long size){  
        return Formatter.formatFileSize(mContext, size);   
    } 
    
	private void initItemIcon(ResolveInfo info, int position, ViewHolder holder) {
        if (info == null) {
            return;
        }
        String pkg = info.activityInfo.packageName;
        Drawable draw = null;
        int width = 0;
        int height = 0;
        // define pre converted image width and height
        int newWidth = 100;
        int newHeight = 100;
        // the create operation pictures of Matrix object
        Matrix matrix = new Matrix();
        Bitmap resizedBitmap = null;

        draw = info.loadIcon(mPackManager);
        if (draw != null) {
            width = draw.getIntrinsicHeight();
            height = draw.getIntrinsicWidth();
            Bitmap bitmap = drawableToBitmap(draw);
            // calculate the scaling rate, new dimensions in addition to the
            // original size
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // scale the image motion
            matrix.postScale(scaleWidth, scaleHeight);
            // create a new picture
            resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth,
                    newHeight, true);
            addBitmapToCache(pkg, resizedBitmap);
        } else {
            Bitmap bitmapOrg = BitmapFactory.decodeResource(
                    mContext.getResources(), R.drawable.ic_launcher);
            width = bitmapOrg.getWidth();
            height = bitmapOrg.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // scale the image motion
            matrix.postScale(scaleWidth, scaleHeight);
            resizedBitmap = Bitmap.createScaledBitmap(bitmapOrg, newWidth,
                    newHeight, true);
            addBitmapToCache(pkg, resizedBitmap);
        }
    }
	
    private Bitmap drawableToBitmap(Drawable draw) {
        int width = draw.getIntrinsicWidth();
        int height = draw.getIntrinsicHeight();
        Bitmap.Config config = draw.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        draw.setBounds(0, 0, width, height);
        draw.draw(canvas);
        return bitmap;
    }
    
    private Bitmap getBitmapByPath(String path) {
        SoftReference<Bitmap> softBitmap = mImageCache.get(path);
        if (softBitmap == null) {
            return null;
        }

        Bitmap bitmap = softBitmap.get();
        return bitmap;
    }
    
    private void addBitmapToCache(String path, Bitmap bitmap) {

        SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(bitmap);
        mImageCache.put(path, softBitmap);
    }
    
    public class PkgSizeObserver extends IPackageStatsObserver.Stub{  
        /*** 回调函数， 
         * @param pStatus ,返回数据封装在PackageStats对象中 
         * @param succeeded  代表回调成功 
         */   
        @Override  
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)  
                throws RemoteException {
        	long cachesize = 0, datasize = 0, codesize = 0, totalsize = 0;
            cachesize = pStats.cacheSize  ; //缓存大小  
            datasize = pStats.dataSize  ;  //数据大小   
            codesize = pStats.codeSize  ;  //应用程序大小  
            totalsize = cachesize + datasize + codesize ;
            packageSize = totalsize;
            packageSizeSort = totalsize;
            Log.i(TAG, "cachesize--->"+cachesize+" datasize---->"+datasize+ " codeSize---->"+codesize);
            AppEntry entry = new AppEntry();
            entry.appSize = packageSize;
            entry.packageName = pStats.packageName;
            Message msg = mMainHandler.obtainMessage(
                    MainHandler.MSG_PACKAGE_SIZE_CHANGED, entry);
            
            mMainHandler.sendMessage(msg);
        }  
    }  
    
    public AppEntry getAppEntry(String packageName){
    	if (mListAppEntry != null){
    		for (int i = 0; i < mListAppEntry.size(); i++){
    			if (mListAppEntry.get(i).packageName.equals(packageName)){
    				return mListAppEntry.get(i);
    			}
    		}
    	}
    	
    	return null;
    }
    
    /**
     * view holder
     */
    public class ViewHolder {
        public ImageView appIcon;
        public TextView appName;
        public TextView appSize;
        public TextView appVersion;
        public Button appControl;
        public String packageName;
    }

    public class AppEntry implements Comparable<AppEntry>{
    	public String packageName;
    	public long appSize;
    	public int position;
    	
    	@Override
    	public int compareTo(AppEntry arg0) {
    		if (this.appSize == arg0.appSize){
    			return 0;
    		}else if (this.appSize > arg0.appSize){
    			return -1;
    		}else{
    			return 1;
    		}
    	}
    }
    
    class MainHandler extends Handler {
        static final int MSG_REBUILD_COMPLETE = 1;
        static final int MSG_PACKAGE_SIZE_LOAD = 2;
        static final int MSG_PACKAGE_SIZE_SORT = 3;
        static final int MSG_PACKAGE_SIZE_CHANGED = 4;
        static final int MSG_ALL_SIZES_COMPUTED = 5;
        static final int MSG_RUNNING_STATE_CHANGED = 6;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            	case MSG_PACKAGE_SIZE_LOAD:
            		appSizeLoad();
            		break;
            	case MSG_PACKAGE_SIZE_CHANGED:
            		AppEntry obj = (AppEntry)msg.obj;
            		AppEntry entry = getAppEntry(obj.packageName);
            		if (entry != null){
                		entry.appSize = obj.appSize;
            		}
            		onPackageSizeChanged(obj.packageName);
            		msg.obj = null;
            		if (entry.position == mAppList.size() - 1){
            			mAppLoadSizeFinish = true;
            		}
            		break;
            	case MSG_PACKAGE_SIZE_SORT:
            		if (mAppLoadSizeFinish == true){
                		sortByAppSize();
            		}else{
                        mMainHandler.sendEmptyMessageDelayed(MainHandler.MSG_PACKAGE_SIZE_SORT, 1000);
            		}
            		break;
            	default:
            		break;
            }
        }
    }

    final MainHandler mMainHandler = new MainHandler();
    
    public void onPackageSizeChanged(String packageName) {
        for (int i=0; i<mActive.size(); i++) {
        	ViewHolder holder = (ViewHolder)mActive.get(i).getTag();
        	
            if ((holder.packageName != null)&& holder.packageName.equals(packageName)) {
            	AppEntry entry = getAppEntry(packageName);
            	if (entry != null){
            		long packageSize = entry.appSize;
            		holder.appSize.setText(formateFileSize(packageSize));
            	}
                return;
            }
        }
    }
}
