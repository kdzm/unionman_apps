package com.um.launcher.view.setting;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.um.launcher.R;

public class SelectUpdateVersion extends LinearLayout {
    private static final String TAG = "SelectUpdateVersion";
    private Context mContext;
    private SystemUpdateDialog mSystemUpdateDialog;
    private ListView list = null;
    private Toast mToast;
    private SimpleAdapter listItemAdapter = null;
    private ArrayList<HashMap<String, Object>> listItem = null;
    private final  int GETUPDATAVERSIONSUCCESS = 1;
    private final  int NETWORKWERROR = 2;
    public SelectUpdateVersion(Context context) {
        super(context);
         mContext = context;
         LayoutInflater inflater = LayoutInflater.from(context);
         View parent = inflater.inflate(R.layout.select_update_version, this);
         initView(parent, 0);
    }
     private void initView(View parent, int focus) {
         list = (ListView) findViewById(R.id.tv_version_listview);
         listItem = new ArrayList<HashMap<String, Object>>();
         getUpdataVersionFromServer();
         listItemAdapter = new SimpleAdapter(mContext, listItem, R.layout.list_update_version_item,
                 new String[]{"TV_Version_Name"},
                 new int[]{R.id.tv_version_name});

         list.setAdapter(listItemAdapter);
         list.setSelected(true);
         list.requestFocus();
         list.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                String path = (String) listItem.get(arg2).get("TV_Version_File_path");
                Log.d(TAG,  "select posion is "+arg2);
                if(path != null){
        if(null != mSystemUpdateDialog && mSystemUpdateDialog.isShowing())
        {  return ;
        }else{
                    NetUpdate.setserverFilePath(path);
                    boolean flag = isNetworkAvailable(mContext);
                    if (flag) {
                        createDialog(350, 400, SystemUpdateDialog.NET_HAVE_UPDATE);
                    } else {
                        createDialog(350, 400, SystemUpdateDialog.NET_NO_UPDATE);
                    }
            }
                }
            }
         });
     }
     public Handler mHandler = new Handler() {
         public void handleMessage(Message msg) {
             switch (msg.what){
             case  GETUPDATAVERSIONSUCCESS:
                 list.setAdapter(listItemAdapter);
                 list.deferNotifyDataSetChanged();
                 break;
             case NETWORKWERROR:
                 if (mToast == null) {
                        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
                    }

                    mToast.setText(R.string.networkerror);
                    mToast.show();
                 break;
             }
         }
     };
    /**
     * create dialog by height,width and save
     *
     * @param height
     * @param width
     * @param save
     */
    public void createDialog(int height, int width, int save) {
        mSystemUpdateDialog = new SystemUpdateDialog(mContext, save, null);
        mSystemUpdateDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        Window window = mSystemUpdateDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = height;
        lp.width = width;
        window.setAttributes(lp);
        mSystemUpdateDialog.show();

    }
    /**
     * Determine the current network is available
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null && cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()) {
            return true;
        } else {
            // If it is used to determine the network connection
            // use the cm.getActiveNetworkInfo().isAvailable();
            if (null == cm) {
                return false;
            }
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * Hidden child dialog if is showing
     */
    public void dismissChildDialog() {
        if (null != mSystemUpdateDialog && mSystemUpdateDialog.isShowing()) {
            mSystemUpdateDialog.dismiss();
        }
    }
    public void getUpdataVersionFromServer()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpPost request = new HttpPost("http://10.67.212.121/image/json/version.json");
                JSONObject param = new JSONObject();
                StringEntity se;
                try {
                    se = new StringEntity(param.toString());
                    request.setEntity(se);
                    HttpResponse httpResponse = new DefaultHttpClient().execute(request);
                     if(httpResponse.getStatusLine().getStatusCode()==200){
                         StringBuilder builder = new StringBuilder();
                         BufferedReader bufferedReader2 = new BufferedReader(
                                    new InputStreamReader(httpResponse.getEntity().getContent()));
                         for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2 .readLine()) {
                                builder.append(s);
                            }
                         Log.i("SelectUpdateVersion", ">>>>>>" + builder.toString());
                         JSONObject jsonObject = new JSONObject(builder.toString());
                         JSONArray jsonArray = jsonObject.getJSONArray("version");
                         for(int i=0;i<jsonArray.length();i++){
                             JSONObject jsonObject2 = (JSONObject)jsonArray.opt(i);
                             if(jsonObject2 != null){
                             HashMap<String, Object> map = new HashMap<String, Object>();
                             map.put("TV_Version_Name",jsonObject2.getString("versionName"));
                             map.put("TV_Version_File_path",jsonObject2.getString("url"));
                             listItem.add(map);
                             System.out.println(" versionName = "+ jsonObject2.getString("versionName"));
                             System.out.println(" url = "+ jsonObject2.getString("url"));
                             }
                         }
                         listItemAdapter = new SimpleAdapter(mContext, listItem, R.layout.list_update_version_item,
                                 new String[]{"TV_Version_Name"},
                                 new int[]{R.id.tv_version_name});
                         mHandler.sendEmptyMessage(GETUPDATAVERSIONSUCCESS);
                      }else
                      {
                         Log.i("SelectUpdateVersion", "server return error");
                      }
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                     mHandler.sendEmptyMessage(NETWORKWERROR);
                    e.printStackTrace();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

}
