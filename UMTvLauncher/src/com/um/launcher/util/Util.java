
package com.um.launcher.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.lang.reflect.Method;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff.Mode;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.PorterDuffXfermode;
import android.hardware.usb.IUsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.um.launcher.MyApplication;
import com.um.launcher.R;
import com.um.launcher.view.LayoutTag;

/**
 * util
 *
 * @author janey
 */
public class Util {

    // Access to all applications (to change for change)
    public static final int ALL_APP = 0;
    // page left most focus tag set
    public static HashMap<LayoutTag, Boolean> LEFT_FOUSE_MAP = new HashMap<LayoutTag, Boolean>();
    // A page on the right focus set
    public static HashMap<LayoutTag, Boolean> RIGTH_FOUSE_MAP = new HashMap<LayoutTag, Boolean>();
    // The current focus
    public static LayoutTag FOCUSE_TAG = null;
    // To clear the data successfully
    public static final int CLEAR_USER_DATA = 1;
    // Remove data failed
    public static final int NOT_CLEAR_USER_DATA = 2;
    // onkeydown count
    private static int mKeyNum = 0;
    
    private static final String TAG = "Util";
    
    public synchronized static int getmKeyNum() {
        return mKeyNum;
    }

    public synchronized static void setmKeyNum(int mKeyNum) {
        Util.mKeyNum = mKeyNum;
    }

    // public static HashMap<String, Integer> PKG_MAP = null;

    /**
     * get all app
     *
     * @param mContext
     * @return applist
     */
    public static List<ResolveInfo> getAllApps(Context context) {
        if (context == null) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        // all app list
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> tempAppList = packageManager.queryIntentActivities(
                mainIntent, 0);
        // Application of filter does not need to display
        HashMap<String, Boolean> map = filterAppParse(context);
        for (int i = 0; tempAppList != null && i < tempAppList.size();) {
            ResolveInfo info = tempAppList.get(i);
            String pkg = info.activityInfo.packageName;
            if (map.get(pkg) != null) {
                tempAppList.remove(i);
            } else {
                i++;
            }
        }
        Collections.sort(tempAppList, new ResolveInfo.DisplayNameComparator(
                packageManager));
        return tempAppList;
    }

    /**
     * The analytical needs to filter the XML file
     *
     * @param context
     * @return Application of set
     */
    private static HashMap<String, Boolean> filterAppParse(Context context) {
        // The application list filter
        HashMap<String, Boolean> filterList = new HashMap<String, Boolean>();
        if (context != null) {
            InputStream fis = context.getResources().openRawResource(
                    R.raw.filter_apps);
            // to parse the XML file (DOM analysis)
            if (fis != null) {
                DocumentBuilderFactory xmlparser = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder xmlDOC;
                Document doc = null;
                try {
                    xmlDOC = xmlparser.newDocumentBuilder();
                    doc = xmlDOC.parse(fis);
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null == doc) {
                    return filterList;
                }
                NodeList listItem = doc.getElementsByTagName("Application");
                for (int i = 0; i < listItem.getLength(); i++) {
                    if (listItem.item(i).hasChildNodes()) {
                        NodeList list = listItem.item(i).getChildNodes();
                        String name = "";
                        for (int j = 0; j < list.getLength(); j++) {
                            String nodeName = list.item(j).getNodeName();
                            String nodeText = list.item(j).getTextContent();
                            if (nodeName.equalsIgnoreCase("PackageName")) {
                                name = nodeText;

                            }
                        }
                        filterList.put(name.trim(), true);
                    }
                }
            }
        }
        return filterList;
    }

    /**
     * Determine the current language environment is Chinese
     *
     * @return isChinese
     */
    public static boolean isInChinese() {
        boolean isChinese = false;
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        if ("zh".equals(language)) {
            isChinese = true;
        }
        return isChinese;
    }

    /**
     * show toast by object
     *
     * @param context
     * @param object
     */
    public static void showToast(Context context, Object object) {
        if (context == null || object == null) {
            return;
        }
        if (object instanceof Integer) {
            int id = (Integer) object;
            Toast.makeText(context, context.getResources().getString(id),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, (String) object, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Determine whether the network is available
     *
     * @param context
     * @return boolean
     */
    public boolean isNetworkAvilable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
        } else {
            NetworkInfo[] networkInfos = connectivityManager
                    .getAllNetworkInfo();

            if (networkInfos != null) {
                for (int i = 0, count = networkInfos.length; i < count; i++) {
                    if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Judge a application is an application that is not a system, if it is to
     * return true, otherwise it returns false.
     *
     * @param info
     * @return
     */
    public static boolean filterApp(ApplicationInfo info) {
        // Some applications can be updated, if the user to download an
        // application system to update the original,
        // it is the system application, this is the judgment of this case
        if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {// Judging Is
                                                              // System
                                                              // application
            return true;
        }
        return false;
    }

    /**
     * unInstall app
     *
     * @param context
     * @param appInfo
     */
    public static void unLoad(final Context context, final ResolveInfo info) {
        final ApplicationInfo appInfo = info.activityInfo.applicationInfo;

        /*
         * new AlertDialog.Builder(context)
         * .setTitle(context.getText(R.string.please_sure))
         * .setIcon(android.R.drawable.ic_dialog_alert)
         * .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
         * { public void onClick(DialogInterface dialog, int which) { // unLoad
         * app here
         */// judge is a system application?
        if (filterApp(appInfo)) {
            showToast(context, R.string.no_del_sys_app);
        } else {
            String strUri = "package:" + appInfo.packageName;
            // Uri is used to access to uninstall the package name
            Uri uri = Uri.parse(strUri);
            Intent deleteIntent = new Intent();
            deleteIntent.setAction(Intent.ACTION_DELETE);
            deleteIntent.setData(uri);
            context.startActivity(deleteIntent);
        }
    }

    /*
     * }) .setNegativeButton(R.string.cancel, null) .create().show(); }
     */

    private static MyApplication myApplication;

    /**
     * Notice of the application to unInstall application
     *
     * @param context
     * @param info
     */
    public static void syncApps(Context context, ResolveInfo info) {

        myApplication = (MyApplication) context.getApplicationContext();
        List<ResolveInfo> list = myApplication.getResolveInfos();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(info)) {
                list.remove(info);
                if (Constant.LOG_TAG) {
                    Log.d("debug", "package === ( " + info + " ) has removed");
                }
                break;
            }
        }
        myApplication.setResolveInfos(list);
    }

    /**
     * Public warning dialog box
     *
     * @param context
     * @param title
     * @param content
     * @param drawable
     * @return
     */
    public static Builder createWarnDialog(final Context context, String title,
            String content, Drawable drawable) {
        Builder builder = new AlertDialog.Builder(context);

        if (drawable != null) {
            builder.setIcon(drawable);
        }

        if (!TextUtils.isEmpty(title)) {

            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(content)) {

            builder.setMessage(content);
        }
        return builder;
    }

    public static void saveCurSourceToPrefer(Context context, int curSourceIdx) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SOURCEDATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constant.SOURCEDATA, curSourceIdx);
        editor.commit();
    }

    public static int getCurSourceToPrefer(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Constant.SOURCEDATA, Context.MODE_PRIVATE);

        return preferences.getInt(Constant.SOURCEDATA, EnumSourceIndex.SOURCE_ATV);
    }

    public static void notifyDTVStopPlay(Context context) {
    	UtilDtv.getInstance().stopPlay();
    }

    private static SurfaceView mSurfaceView = null;

    public static void setSurfaceView(SurfaceView mSV)
    {
    	mSurfaceView = mSV;
    }
    
    public static void notifyDTVStartPlay(Context context, boolean isFullScreen) {
    	Log.v(TAG, "notifyDTVStartPlay(): context=" + context 
    				+ ", isFullScreen=" + isFullScreen
    				+ ", mSurfaceView=" + mSurfaceView);
    	
    	if(isFullScreen) {
    		UtilDtv.getInstance().stopPlay();
    		return;
    	}
    	
    	UtilDtv.getInstance().startPlay(context, mSurfaceView);
    }

    public static void notifyDTVSmallWindow(Context context) {
    	Log.v(TAG, "notifyDTVSmallWindow(): context=" + context
				+ ", mSurfaceView=" + mSurfaceView);
    	UtilDtv.getInstance().startPlay(context, mSurfaceView);
    }


    // Callback
    private static ClearUserDataObserver mClearUserDataObserver;

    /**
     * To determine whether the system application, kill the running program
     *
     * @param pckName
     */
    public static void ForceQuit(Context context, String pckName) {
        String pck = null;

        PackageManager packageManager = context.getPackageManager();
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningProcesses;
        runningProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo rp : runningProcesses) {
            pck = rp.processName;
            try {
                ApplicationInfo applicationInfo = packageManager.getPackageInfo(pck,
                        0).applicationInfo;
                if (pckName.equals(pck) && filterApp(applicationInfo)) {
                    forceStopPackage(context, pck);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * To stop an application
     *
     * @param pckName
     * @param allAppsActivity
     */
    public static void forceStopPackage(final Context context, final String
            pckName) {
        new AlertDialog.Builder(context)
                .setTitle(context.getText(R.string.force_stop_package))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // forceStopPackage here
                                ActivityManager am =
                                        (ActivityManager) context
                                                .getSystemService(Context.ACTIVITY_SERVICE);
                                am.forceStopPackage(pckName);
                                Method method;
                                try {
                                    method =
                                            Class.forName("android.app.ActivityManager").getMethod(
                                                    "forceStopPackage",
                                                    String.class);
                                    method.invoke(am, pckName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    /**
     * Clear data
     *
     * @param context
     * @param info
     * @param handler
     */
    public static void clearData(final Context context, final ApplicationInfo
            info, final Handler handler) {
        if (info != null && info.manageSpaceActivityName != null) {
            if (!ActivityManager.isUserAMonkey()) {
                Intent intent = new Intent(Intent.ACTION_DEFAULT);
                intent.setClassName(info.packageName,
                        info.manageSpaceActivityName);
                ((Activity) context).startActivityForResult(intent, -1);
            }
        } else {
            new AlertDialog.Builder(context)
                    .setTitle(context.getText(R.string.clear_data_dlg_title))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(context.getText(R.string.clear_data_dlg_text))
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Clear user data here
                                    clearAppUserData(context, handler, info.packageName);
                                }
                            })
                    .setNegativeButton(R.string.cancel, null)
                    .create().show();
        }
    }

    // Clear APK data
    public static void clearAppUserData(final Context context, final Handler
            handler, final String pkgname) {
        if (mClearUserDataObserver == null) {

            mClearUserDataObserver = new ClearUserDataObserver(handler);
        }
        ActivityManager am = (ActivityManager)
                (((Activity) context)).getSystemService(Context.ACTIVITY_SERVICE);
        boolean res = am.clearApplicationUserData(pkgname,
                mClearUserDataObserver);
    }

    /**
     * Remove the default settings
     *
     * @param context
     * @param packageName
     */
    public static void clearDefault(final Context context, final String
            packageName) {
        new AlertDialog.Builder(context)
                .setTitle(context.getText(R.string.clear_default))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // clearDefault here
                                PackageManager packageManager = context.getPackageManager();
                                packageManager.clearPackagePreferredActivities(packageName);
                                try {
                                    IBinder b = ServiceManager.getService(Context.USB_SERVICE);
                                    IUsbManager mUsbManager = IUsbManager.Stub.asInterface(b);
                                    mUsbManager.clearDefaults(packageName, UserHandle.myUserId());
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }
    
    public static void appNoSupportPrompt(Context context, int resId){
    	TextView toastText = null;
    	View toastRoot = LayoutInflater.from(context).inflate(R.layout.mytoast_layout, null);
        //View toastRoot = getLayoutInflater().inflate(R.layout.mytoast_layout, null);    
    	toastText = (TextView)toastRoot.findViewById(R.id.message);
    	toastText.setText(resId);
        Toast toastStart = new Toast(context);  
        toastStart.setGravity(Gravity.CENTER, 0, 0);  
        toastStart.setDuration(Toast.LENGTH_SHORT);  
        toastStart.setView(toastRoot);  
        toastStart.show();  
        
    }
    
    public static Bitmap createReflectedImageExt(Context mContext, int imageId)
    {
    	Bitmap originalImage = BitmapFactory.decodeResource(mContext.getResources(), imageId);
        int width = originalImage.getWidth(); 
        int height = originalImage.getHeight();
        Matrix matrix = new Matrix(); 
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, 
                (height / 2), width, height / 2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, 
                (height / 2), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(reflectionImage, 0, 0, null);
        Paint paint = new Paint(); 
        LinearGradient shader = new LinearGradient(0, 
        		0, 0, reflectionImage.getHeight(), 0x50ffffff, 0x00ffffff, 
                TileMode.MIRROR);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, 0, width, bitmapWithReflection.getHeight(), paint);
        return bitmapWithReflection;  
    }
    
    public static Bitmap createReflectedImage(Context mContext, View view, int size)
    {
    	String path = "/mnt/sda/sda1/"+view.getId()+".png";
    	view.setDrawingCacheEnabled(true);    
        Bitmap originalImage = view.getDrawingCache();   

        Log.d("Util","createReflectedImage----------------------");
        if (originalImage != null)
        {
        	Log.d("Util","createReflectedImage---------------------- originalImage != null");
	        int width = originalImage.getWidth(); 
	        int height = originalImage.getHeight();
	        Matrix matrix = new Matrix(); 
	        matrix.preScale(1, -1);
	        //matrix.postSkew(kx, ky);
	        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, 
	                (height - height / size), width, height / size, matrix, false);
	        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, 
	                (height / size), Config.ARGB_8888);
	        Canvas canvas = new Canvas(bitmapWithReflection);
	        canvas.drawBitmap(reflectionImage, 0, 0, null);
	        Paint paint = new Paint(); 
	        LinearGradient shader = new LinearGradient(0, 
	        		0, 0, reflectionImage.getHeight(), 0x70ffffff, 0x00ffffff, 
	                TileMode.MIRROR);
	        paint.setShader(shader);
	        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	        canvas.drawRect(0, 0, width, bitmapWithReflection.getHeight(), paint);
	        view.setDrawingCacheEnabled(false);
	        view.destroyDrawingCache();
	        /*
	        try {
	        	File mFile = new File(path);  
	        	mFile.createNewFile();
		        FileOutputStream out = new FileOutputStream(mFile);  
		        bitmapWithReflection.compress(Bitmap.CompressFormat.PNG, 0, out);
		        out.flush(); 
	        } catch (Exception e) {  
	        	Log.d("Util","hahalgj----------------------");
	            e.printStackTrace();  
	        }*/  
	        return bitmapWithReflection; 
        }else{
	        view.setDrawingCacheEnabled(false);
	        view.destroyDrawingCache();
        	return null;
        }
    }
}
