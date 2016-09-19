package com.um.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.um.controller.AppBaseActivity;
import com.um.controller.FocusAnimator;
import com.um.controller.SignalCheck;
import com.um.dvbsettings.R;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Tuner;
public class SignCheck extends AppBaseActivity {
	private EditText fre_et;
	private EditText smblet;
	private Button btn;
	private ProgressBar bar;
	private SignalCheck check;
    private TextView[] textQam = new TextView[4];
    private int qam = 3;
    private ImageView focusFrame;// 白色框

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_check);

        fre_et=(EditText)findViewById(R.id.sign_edit_frq);
        fre_et.setText("227");
		smblet=(EditText)findViewById(R.id.sign_edit_synbol);
		smblet.setText("6875");

        textQam[0] = (TextView)findViewById(R.id.textViewQam1);
        textQam[1] = (TextView)findViewById(R.id.textViewQam2);
        textQam[2] = (TextView)findViewById(R.id.textViewQam3);
        textQam[3] = (TextView)findViewById(R.id.textViewQam4);

        btn = (Button)findViewById(R.id.sign_check_btn);
        btn.setOnClickListener(new View.OnClickListener() {
			
//			@Override
			public void onClick(View v) {
				 int fre = 0;
				 int smbl = 0;

				String s = fre_et.getText().toString();
				if(s.isEmpty()==false)
				{
					fre = Integer.parseInt(s);
				}	
				
				Log.i("DVBACTIVITY","DVB LOCK FRE:"+fre);
				String smbls = smblet.getText().toString();
				if(smbls.isEmpty()==false)
				{
					smbl = Integer.parseInt(smbls);
				}	
				Log.i("DVBACTIVITY","DVB LOCK smbl:"+smbl);
				Log.i("DVBACTIVITY","DVB getSelectedItemPosition qam:"+qam);

				Tuner tu = new Tuner(DVB.getInstance());
				tu.Lock(0, fre*100, smbl, qam);
		    	check.start();
			}
		});

        qam = 3;
        textQam[2].setTextColor(getResources().getColor(R.color.yellow));

        fre_et.setOnFocusChangeListener(signalCheckFocusChange);
        smblet.setOnFocusChangeListener(signalCheckFocusChange);
        btn.setOnFocusChangeListener(signalCheckFocusChange);
        for(int i = 0; i < textQam.length; i++)
        {
            textQam[i].setOnClickListener(onClickHandler);
            textQam[i].setOnFocusChangeListener(signalCheckFocusChange);
        }
        this.focusFrame = (ImageView)findViewById(R.id.foucs_frame);
    }
    
    SignalCheck.TunerCheckListener tl = new SignalCheck.TunerCheckListener() {
		
		public void setParam(int snr, int[] ber, int strength) {
		
			Log.i("DVBACTIVITY","setParam,snr:"+snr);
			Log.i("DVBACTIVITY","setParam,ber:"+ber);
			Log.i("DVBACTIVITY","setParam,streng:"+strength);
			ProgressBar snrbar = (ProgressBar)findViewById(R.id.progressSignCheckQulty);
			TextView snrText = (TextView)findViewById(R.id.textViewSingalQualityValue);
			snrbar.setProgress(snr%101);
			snrText.setText(snr + "Buv");
			
			ProgressBar berbar = (ProgressBar)findViewById(R.id.progressBarSignCheckBer);
			TextView berText = (TextView)findViewById(R.id.textViewSingalBerValue);
			berbar.setProgress(ber[0]%101);
			if(ber[0] == 100)
			{
				berText.setText(1 + "");
			}
			else
				berText.setText(ber[1] + "E-" + ber[2]);
			
			ProgressBar strengthbar = (ProgressBar)findViewById(R.id.progressBarSignCheckStrength);	
			TextView strengthText = (TextView)findViewById(R.id.textViewSingalStrengthValue);
			strengthbar.setProgress(strength%101);
			strengthText.setText(strength + ".0dB");
		}
	};
	@Override
    public void onStart()
    {
    	super.onStart();
    	
    	check = new SignalCheck();
    	check.setListener(tl);

    }
	@Override
    public void onStop()
    {
    	super.onStop();
    	
    	check.stop();
    	check = null;
    }

    View.OnClickListener onClickHandler = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            for(int i = 0; i < textQam.length; i++)
            {
                if(v.getId() == textQam[i].getId())
                {
                    qam = i+1;
                    textQam[i].setTextColor(getResources().getColor(R.color.yellow));
                }else
                {
                    textQam[i].setTextColor(getResources().getColor(R.color.white));
                }
            }
        }
    };

    View.OnFocusChangeListener signalCheckFocusChange = new View.OnFocusChangeListener(){
        public void onFocusChange(View v,boolean hasFocus)
        {
            int with = 0;
            int hight = 0;
            int[] location = new  int[2] ;
            LinearLayout Layout = null;

            switch (v.getId())
            {
                case R.id.sign_edit_frq:
                    Layout = (LinearLayout)findViewById(R.id.lagoutfreq);
                    with = Layout.getWidth();
                    hight = Layout.getHeight();
                    Layout.getLocationOnScreen(location);
                    break;
                case R.id.sign_edit_synbol:
                    Layout = (LinearLayout)findViewById(R.id.lagoutsymbol);
                    with = Layout.getWidth();
                    hight = Layout.getHeight();
                    Layout.getLocationOnScreen(location);
                    break;
                case R.id.sign_check_btn:
                case R.id.textViewQam1:
                case R.id.textViewQam2:
                case R.id.textViewQam3:
                case R.id.textViewQam4:
                    with = v.getWidth();
                    hight = v.getHeight();
                    v.getLocationOnScreen(location);
                    break;
            }

            if(true == hasFocus)
            {
                Log.i("DVBACTIVITY","with:" + with + " hight:" + hight + " x:" + location[0] + " y:" + location[1]);
                FocusAnimator focusAnimator = new FocusAnimator();
                focusAnimator.flyFoucsFrame(focusFrame, with, hight, location[0], location[1]);
            }
        }
    };
}