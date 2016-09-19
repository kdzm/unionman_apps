package com.um.usbupgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;

public class SwitchReceiver extends BroadcastReceiver {
	private final String mAction = "android.intent.action.USB_UPGRADE_ACTIVE";
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		if (arg1.getAction().equals(mAction)) {
			String usbUpgrade = SystemProperties.get("persist.sys.usb.upgrade", "false");
			if(!usbUpgrade.equals("true")){
				SystemProperties.set("persist.sys.usb.upgrade", "true");
			}else{
				SystemProperties.set("persist.sys.usb.upgrade", "false");
			}
		}
	}

}
