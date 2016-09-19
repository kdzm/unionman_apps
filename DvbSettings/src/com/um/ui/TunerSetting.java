package com.um.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.Spinner;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.um.controller.AppBaseActivity;
import com.um.controller.FocusAnimator;
import com.um.dvbstack.DVB;
import com.um.dvbstack.ProgManage;
import com.um.dvbstack.Tuner;
import com.um.dvbsettings.R;
import com.unionman.dvbstorage.SettingsStorage;

/**
 * Created by Administrator on 13-12-21.
 */
public class TunerSetting extends AppBaseActivity {
	private Button button1;
    private TextView[] textMode = new TextView[2];
    private int tunerMode = 0;
    private LinearLayout layoutSure = null;
    private Tuner tuner;
    private int mWidthPixels;
    private int mHeightPixels;
    private static final String TAG = "TunerSetting";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuner_setting);
        tuner = Tuner.GetInstance(DVB.getInstance());
        int type = tuner.GetType();
        
        Log.i(TAG,"type"+ type );
        
        tunerMode = type-2;
        tunerMode = tunerMode>0?tunerMode:0;
        textMode[0] = (TextView)findViewById(R.id.dvbcsearch);
        textMode[1] = (TextView)findViewById(R.id.dtmbsearch);
        
        
        for(int i = 0; i < textMode.length; i++)
        {
            textMode[i].setOnClickListener(modeOnClickHandler);
            textMode[i].setOnFocusChangeListener(signalCheckFocusChange);
        }

        findViewById(R.id.search_check_btn).setOnClickListener(modeOnClickHandler);
        findViewById(R.id.search_check_btn).setOnFocusChangeListener(signalCheckFocusChange);
		
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        mWidthPixels = mDisplayMetrics.widthPixels;
        mHeightPixels = mDisplayMetrics.heightPixels;
        layoutSure = (LinearLayout)findViewById(R.id.search_sure);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(880*mWidthPixels/1280, 76*mHeightPixels/720);
        params.topMargin = 200*mHeightPixels/720;
        layoutSure.setLayoutParams(params);
    }

    public void onResume(){
        super.onResume();
        textMode[tunerMode].setTextColor(getResources().getColor(R.color.yellow));
    }

    @Override
    protected void onPause(){
        super.onPause();

    }



    View.OnClickListener modeOnClickHandler = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            if(v.getId() == R.id.search_check_btn)
            {
                tuner.SetType(tunerMode+2);
                SettingsStorage ss = new SettingsStorage(TunerSetting.this.getContentResolver());
                ss.putInt("tuner_type",tunerMode+2);    /* -s:1  -c:2  -t:3*/
                finish();
            }

            for(int i = 0; i < textMode.length; i++)
            {
                if(v.getId() == textMode[i].getId())
                {
					tunerMode = i;
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(880, 76);
                    textMode[i].setTextColor(getResources().getColor(R.color.yellow));
                    //params.topMargin = 470*mHeightPixels/720;
                    //layoutSure.setLayoutParams(params);
                }else
                {
                    textMode[i].setTextColor(getResources().getColor(R.color.white));
                }
            }
        }
    };

    View.OnFocusChangeListener signalCheckFocusChange = new View.OnFocusChangeListener(){
        public void onFocusChange(View v,boolean hasFocus)
        {
            View view = null;
            int id  = 0;

            switch (v.getId())
            {
                case R.id.search_check_btn:
                    id = v.getId();
                    if(0 == tunerMode)
                        findViewById(R.id.search_check_btn).setNextFocusUpId(R.id.dvbcsearch);
                    else if(2 == tunerMode)
                        findViewById(R.id.search_check_btn).setNextFocusUpId(R.id.dtmbsearch);
                    break;
                case R.id.dvbcsearch:
                case R.id.dtmbsearch:
                    id = v.getId();
                    break;
                default:
                    id = 0;
                    break;
            }

            view = findViewById(id);
            if(null != view)
            {
                int[] location = new  int[2] ;
                if(true == hasFocus)
                {
                    view.getLocationOnScreen(location);
                    ImageView focusFrame = (ImageView)findViewById(R.id.search_focus);
                    FocusAnimator focusAnimator = new FocusAnimator();
                    focusAnimator.flyFoucsFrame(focusFrame, view.getWidth(), view.getHeight(), location[0], location[1]);
                }

            }
        }


    };
}