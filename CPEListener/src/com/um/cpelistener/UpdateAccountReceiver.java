package com.um.cpelistener;

import java.net.InetAddress;
import java.net.Socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * update account,send to CPE
 * am broadcast -a com.unionman.action.UPDATE_ACCOUNT --es userid jltest5 --es userpw 1
 */
public class UpdateAccountReceiver extends BroadcastReceiver {
	private final String TAG = "CpeListener--UpdateAccountReceiver";
	private String cmd="";
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Logger.i(TAG, "onReceiver, intent action: "+intent.getAction());
		String userid=intent.getStringExtra("userid");
		String userpw=intent.getStringExtra("userpw");
		if(userid!=null&&userpw!=null){
			cmd="userid="+userid+";userpw="+userpw;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					sendAccountToCPE(cmd, "", cmd.length());
				}
			}).start();
		}
	}
	
	private void sendAccountToCPE(String cmd, String msg, int len) {
		Socket socket_client = null;
		try {
			Log.d(TAG, "send_message = " + cmd + ", " + msg + ", " + len);
			InetAddress addr = InetAddress.getByName("127.0.0.1");
			socket_client = new Socket(addr, 23416);
			SocketMessage.send_message(socket_client, cmd, 101, msg, len);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket_client.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
