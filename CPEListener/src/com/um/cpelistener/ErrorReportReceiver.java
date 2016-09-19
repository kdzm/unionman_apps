package com.um.cpelistener;

import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * error report,send to CPE
 * am broadcast -a android.unionman.action.ERROR_REPORT --es code_type 10000
 */
public class ErrorReportReceiver extends BroadcastReceiver {
	private final String TAG = "CpeListener--ErrorReportReceiver";
	private String cmd="";
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Logger.i(TAG, "onReceiver, intent action: "+action);
		String code=intent.getStringExtra("code_type");
		if(code!=null){
			cmd="errdate="+getCurrTime()+";errcode="+code;
			Log.i(TAG, cmd);
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
	
	private String getCurrTime(){
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return dateFormat.format(now); 
	}
}
