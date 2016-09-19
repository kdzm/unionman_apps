package com.unionman.settingwizard.wifi;

import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unionman.settingwizard.R;
import com.unionman.settingwizard.network.WiFiCtl;

public class WifiAdapter extends BaseAdapter {
    private String TAG = "WifiAdapter";
    private int mRssi;
    private Context mContext;
    private AP mScanResult;
    private ArrayList<AP> mList;
    WiFiCtl mWifiCtl;
    String connectedSsid = null;
    private WifiInfo mWifiInfo;

    int[] mImageLock = new int[]{R.drawable.ic_wifi_lock_signal_1,
            R.drawable.ic_wifi_lock_signal_2, R.drawable.ic_wifi_lock_signal_3,
            R.drawable.ic_wifi_lock_signal_4, R.drawable.ic_wifi_lock_signal_5};

    int[] mImageUnlock = new int[]{R.drawable.ic_wifi_signal_1,
            R.drawable.ic_wifi_signal_2, R.drawable.ic_wifi_signal_3,
            R.drawable.ic_wifi_signal_4, R.drawable.ic_wifi_signal_5};

    public WifiAdapter(Context paramContext, ArrayList<AP> mList, WiFiCtl mWifiCtl) {
        this.mContext = paramContext;
        this.mList = mList;
        this.mWifiCtl = mWifiCtl;
        mWifiInfo = mWifiCtl.getWiFiInfo();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    int getLevel() {
        if (mRssi == Integer.MAX_VALUE) {
            return -1;
        }
        return WifiManager.calculateSignalLevel(mRssi, 4);
    }

    /* 
     *
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
//        int level = 0;
//        int security =0;
//        String ssid = null;

        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.wifi_list_item, null);
            holder.level = (ImageView) convertView.findViewById(R.id.imgv_level);
            holder.ssid = (TextView) convertView.findViewById(R.id.tv_ssid);
            holder.connection = (TextView) convertView.findViewById(R.id.tv_connect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mScanResult = mList.get(position);

        String ssid = mScanResult.ssid;
        int security = mScanResult.getSecurity();
        int level = mScanResult.getLevel();
        Log.v(TAG, "getView ,getLevel " + level + " ssid " + ssid + " postion " + position);

        holder.ssid.setText(ssid);

        if (security != WiFiCtl.SECURITY_NONE) {
            holder.level.setImageResource(mImageLock[level + 1]);
        } else {
            holder.level.setImageResource(mImageUnlock[level + 1]);
        }

        WifiInfo info = ((WifiManager) mContext.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        Log.v(TAG, "==========info=========" + info.getSSID());
        if (mScanResult.getLevel() == Integer.MAX_VALUE) {
            holder.connection.setText(R.string.not_reach);
        } else if (info != null
                && info.getSSID() != null
                && info.getSSID().equals("\"" + ssid + "\"")
                && isWifiConnect()) {
            holder.connection.setText(R.string.aleady_connect);
        } else if (info != null
                && info.getSSID() != null
                && info.getSSID().equals("\"" + ssid + "\"")){

        } else {
            holder.connection.setText("");
        }
        return convertView;
    }

    public boolean isWifiConnect() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }
}

class ViewHolder {
    ImageView level;
    TextView ssid;
    TextView connection;
}

