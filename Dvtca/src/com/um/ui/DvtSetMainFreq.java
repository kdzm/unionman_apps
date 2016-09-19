package com.um.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.um.controller.AppBaseActivity;
import com.um.dvbstack.Ca;
import com.um.dvbstack.DVB;
import com.um.dvtca.R;
import com.unionman.jazzlib.SystemProperties;

public class DvtSetMainFreq extends AppBaseActivity{
	final String TAG = "DvtSetMainFreq";
    private static String MAIN_FREQ_DVBC = "persist.sys.dvb.dvbc.mainfreq";
    private static String MAIN_FREQ_DTMB = "persist.sys.dvb.dtmb.mainfreq";
    private static int DEFAULT_MAIN_FREQ = 227;
	TextView tvMainFreq;
	Button 	btnSure;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dvt_set_main_freq);

		findView();
		showMainFreq();
		
		btnSure.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int mainFreq = Integer.parseInt(tvMainFreq.getText().toString());
				Log.i(TAG, "mainFreq:"+mainFreq);
				int cmdType = 41;//set main freq
				int lparam = mainFreq*100;
				int rparam = 0;
				final Ca ca = new Ca(DVB.getInstance());
				int ret = ca.CaCmdProcess(cmdType, lparam, rparam);
				
                saveMainFreq(mainFreq);
//				SystemProperties.set("persist.sys.um.mainfreq", Integer.toString(mainFreq * 100));
				
				new AlertDialog.Builder(DvtSetMainFreq.this)
				.setMessage(R.string.main_freq_set_success)
				.setPositiveButton("ok", null)
				.show();
			}
		});
	}

	@Override
	protected void onPause(){
        super.onPause();
        finish();
	}
	
	private void findView(){
		tvMainFreq = (TextView)findViewById(R.id.set_main_frq_edit);
		btnSure = (Button)findViewById(R.id.set_main_frq_button);
	}
	
	private void showMainFreq(){		
        int mainFreq = getMainFreq() / 100;
//		int mainFreq = Integer.parseInt(SystemProperties.get("persist.sys.um.mainfreq", "0"))/100;
		Log.i(TAG,"mainFreq= "+mainFreq);
		tvMainFreq.setText(Integer.toString(mainFreq));
	}
    private void saveMainFreq(int freq) {
        SystemProperties.set("persist.sys.um.mainfreq", Integer.toString(freq * 100));
    }
    private int getMainFreq()
    {
        return Integer.parseInt(SystemProperties.get("persist.sys.um.mainfreq", "0"))/100;
    }
}
