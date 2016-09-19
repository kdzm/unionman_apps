package com.unionman.settingwizard;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import android.net.EthernetDataTracker;
import android.net.ethernet.EthernetManager;

/**
 * Created by Administrator on 13-10-30.
 */
public class MyService extends Service {
    private IntentFilter mIntentFilter;
    public static int mEthernetCurrentStatus = -1;

    private final BroadcastReceiver mEthSettingsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = -1;
            int rel = -1;

            if (intent.getAction().equals(EthernetManager.ETHERNET_STATE_CHANGED_ACTION)) {
                message = intent.getIntExtra(EthernetManager.EXTRA_ETHERNET_STATE, rel);

                switch (message) {
                    case EthernetDataTracker.EVENT_PHY_LINK_UP:
                        mEthernetCurrentStatus = EthernetDataTracker.EVENT_PHY_LINK_UP;
                        Log.w("ServiceNetworkSetupActivity", "EVENT_PHY_LINK_UP ");
                        //show phy link up
                        break;
                    case EthernetDataTracker.EVENT_PHY_LINK_DOWN:
                        mEthernetCurrentStatus = EthernetDataTracker.EVENT_PHY_LINK_DOWN;
                        Log.w("ServiceNetworkSetupActivity", "EVENT_PHY_LINK_DOWN ");
                        //show phy link down
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mIntentFilter = new IntentFilter(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mEthSettingsReceiver, mIntentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mEthSettingsReceiver);
    }
}
