package com.um.ca;

import com.um.dvb.R;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Tf_updatebar extends BroadcastReceiver{
	int updateflag = 1;
	int updateprocess = 0;
	public Handler handler=new Handler();
	private ProgressBar updatebar;
	private TextView update_status;
	LinearLayout UpdateLayout;
	private Activity mInstance = null;
	public Tf_updatebar(Activity activity) {
		mInstance = activity;
	}
    private void start_updatebar()
    {
    	if(null == mInstance){
    		Log.i("start_updatebar", "Dvbplayer_Activity.mInstance is null");
			return;
    	}	
        LinearLayout blankProgressLayout = (LinearLayout) mInstance.findViewById(R.id.progress_blank_layout);
        UpdateLayout = (LinearLayout) LayoutInflater.from(mInstance).inflate(R.layout.tf_updatebar, null);
        blankProgressLayout.removeAllViews();
        blankProgressLayout.addView(UpdateLayout);
        
        update_status = (TextView)UpdateLayout.findViewById(R.id.textViewUpdating);
        update_status.setGravity(Gravity.CENTER);
		
		if(1 == updateflag)          //flag_int = 1  loaddata
		{
			update_status.setText(R.string.tf_ca_scale_receivepatch);
		}
		else                       //update
		{
			update_status.setText(R.string.tf_ca_scale_patching);
		}
  	
		//2.启动计时器：
		handler.postDelayed(runnable, 100);//每两秒执行一次runnable.
		
		updatebar	  = (ProgressBar)UpdateLayout.findViewById(R.id.updateProgressBar);
		updatebar.setProgress(updateprocess);
		TextView progress = (TextView)UpdateLayout.findViewById(R.id.update_percent);
		progress.setText(updateprocess+" %");
    }
    private void stop_updatebar()
    {
    	handler.removeCallbacks(runnable);
    	
    	if(null == mInstance){
    		Log.i("stop_updatebar", "Dvbplayer_Activity.mInstance is null");
			return;
    	}	
    	LinearLayout blankProgressLayout = (LinearLayout) mInstance.findViewById(R.id.progress_blank_layout);
    	blankProgressLayout.removeAllViews();
    }
    
	Runnable runnable=new Runnable(){
		@Override
		public void run() {
		// TODO Auto-generated method stub
		//要做的事情

			int process_value = 0;
			int mark_value = 0;
	        Ca ca = new Ca(DVB.getInstance());
	        /*CA Version*/
	        byte[] process_data = new byte[4];
	        int[] len = new int[1];

			
	        ca.CaGetUpdateProgress(process_data, len);

			process_value = process_data[3]<<24|process_data[2]<<16|process_data[1]<<8|process_data[0];
			Log.i("TF_updatebar_receiver", "Auto-generated method stub process_value"+process_value);
			Log.i("TF_updatebar_receiver", "Auto-generated method stub len"+len[0]);
			
			updatebar	  = (ProgressBar)UpdateLayout.findViewById(R.id.updateProgressBar);
			updatebar.setProgress(process_value);
			TextView progress = (TextView)UpdateLayout.findViewById(R.id.update_percent);
			progress.setText(process_value+" %");
			
			//onLoadData(process_value);	
		    handler.postDelayed(this, 100);
		}
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!DVB.isServerAlive()) {
			return;
		}
		// TODO Auto-generated method stub
		if (intent.getAction().equals("com.um.umdvb.UMSG_DVB_CA_START_PROGRESS_RECEIVEPATCH"))
		{
			updateflag = 1;
			Bundle bundle = intent.getExtras();
			updateprocess = bundle.getInt("progress");
			Log.i("TF_updatebar_receiver", "接受到升级消息 START_PROGRESS_RECEIVEPATCH"+updateprocess);
			start_updatebar();

		}
		else if(intent.getAction().equals("com.um.umdvb.UMSG_DVB_CA_STOP_PROGRESS_RECEIVEPATCH"))
		{
			updateflag = 1;
			
			Bundle bundle = intent.getExtras();
			updateprocess = bundle.getInt("progress");
			Log.i("TF_updatebar_receiver", "接受到升级消息 STOP_PROGRESS_RECEIVEPATCH"+updateprocess);
			stop_updatebar();
		}	
		else if(intent.getAction().equals("com.um.umdvb.UMSG_DVB_CA_START_PROGRESS_PATCHING"))
		{
			updateflag = 0;
			
			Bundle bundle = intent.getExtras();
			updateprocess = bundle.getInt("progress");
			Log.i("TF_updatebar_receiver", "接受到升级消息 START_PROGRESS_PATCHING"+updateprocess);
			start_updatebar();
		}
		else if(intent.getAction().equals("com.um.umdvb.UMSG_DVB_CA_STOP_PROGRESS_PATCHING"))
		{
			updateflag = 0;
			
			Bundle bundle = intent.getExtras();
			updateprocess = bundle.getInt("progress");
			Log.i("TF_updatebar_receiver", "接受到升级消息 STOP_PROGRESS_PATCHING"+updateprocess);
			stop_updatebar();
		}
	}
	
}


