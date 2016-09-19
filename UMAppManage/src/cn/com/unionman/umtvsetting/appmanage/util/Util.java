package cn.com.unionman.umtvsetting.appmanage.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.com.unionman.umtvsetting.appmanage.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * including the selector view control operation data transform second level
 * menu
 *
 * @author wangchuanjian
 *
 */
public class Util {

	public static final int ALL_APP = 0;
	public static final int ALL_APP_WITHOUT_SYSTEM_APP = 1;
    public static final int UPDATE_VIEW = 101;
    public static final int UPDATE_VIEW_WHITHOUT_APPS = 102;
    public static final int APP_lIST_SORT = 201;
    public static final int APP_lIST_UPDATE = 202;
    public static final int APP_lIST_UNLOAD = 203;
    /**
     * get index of Parameters from array
     *
     * @param mode
     * @param arrays
     * @return index of Parameters
     */
    public static int getIndexFromArray(int mode, int[][] arrays) {
        int num = 0;
        if (Constant.LOG_TAG) {
            Log.i("getIndexFromArray", "getIndexFromArray");
        }
        for (int i = 0; i < arrays.length; i++) {
            if (Constant.LOG_TAG) {
                Log.i("getIndexFromArray", "getIndexFromArray=" + i);
            }
            if (arrays[i][0] == mode) {
                num = i;
                return num;
            }
        }
        return num;
    }

    /**
     * create array of parameters
     *
     * @param arrays
     * @return array of Parameters
     */
    public static int[] createArrayOfParameters(int[][] arrays) {
        int[] num = new int[arrays.length];
        if (Constant.LOG_TAG) {
            Log.i("createArrayOfParameters", "createArrayOfParameters");
        }
        for (int i = 0; i < arrays.length; i++) {
            if (Constant.LOG_TAG) {
                Log.i("createArrayOfParameters", "createArrayOfParameters=" + i);
            }
            num[i] = arrays[i][1];
        }
        return num;
    }
    
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
    
    public static List<ResolveInfo> getAllAppsWithoutSystemApps(Context context) {
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
           
            if ((map.get(pkg) != null) || (filterApp(info.activityInfo.applicationInfo))) {
                tempAppList.remove(i);
           /* } else if(pkg.equals("com.shafa.market")||pkg.equals("com.apowo.hysg.jlkjTV")||pkg.equals("com.vogins.wodou")){      
            		tempAppList.remove(i);*/
            }else {
                i++;
            }
        }
        
       /* for(int i=0;i<tempAppList.size();i++){
        	String strtmp=tempAppList.get(i).activityInfo.packageName;
        	if(strtmp.equals("com.shafa.market")||strtmp.equals("com.apowo.hysg.jlkjTV")||strtmp.equals("com.trans.pvz")){
        		tempAppList.remove(i);
        	}
        }*/
        Collections.sort(tempAppList, new ResolveInfo.DisplayNameComparator(
                packageManager));
        return tempAppList;
    }
    
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
    
    public static void installApp(final Context context,File file) {    
        Log.e("OpenFile", file.getName());  
        Intent intent = new Intent();  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        intent.setAction(android.content.Intent.ACTION_VIEW);  
        intent.setDataAndType(Uri.fromFile(file),  
                        "application/vnd.android.package-archive");  
        context.startActivity(intent);  
    }   
    
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
}
