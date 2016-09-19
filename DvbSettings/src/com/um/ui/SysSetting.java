package com.um.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.um.controller.AppBaseActivity;
import com.um.controller.FocusAnimator;
import com.um.controller.ParamSave;
import com.um.dvbsettings.R;
import com.um.dvbstack.DVB;
import com.um.dvbstack.ProgManage;
import com.um.dvbstack.Tuner;
import com.unionman.dvbstorage.ContentSchema;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;
import com.unionman.jazzlib.SystemProperties;

public class SysSetting extends AppBaseActivity {

	private static final String TAG = "SysSetting";
	private LinearLayout mBootDVB = null;
	private Context mContext = SysSetting.this;
	private ListView dvb_setting_list;
	private ArrayList<HashMap<String, Object>> listItemData;
	private SimpleAdapter adapter;
	private Bundle playInfo=null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (!DVB.isServerAlive()) {
        	Toast.makeText(this, "DVB鏈嶅姟宸插仠姝<U+E76E>紝涓嶈兘杩涘叆璇ュ簲鐢�", Toast.LENGTH_SHORT).show();
        	finish();
        	return;
        }

        setContentView(R.layout.dvb_setting_layout);
        TextView textView = (TextView) findViewById(R.id.tv_title);
        dvb_setting_list = (ListView) this.findViewById(R.id.dvb_setting_list);
        listItemData = new ArrayList<HashMap<String, Object>>();
        String[] titles;
        String[] instructs;
        int tunnerType = Tuner.GetInstance(DVB.getInstance()).GetType();
        if (tunnerType == Tuner.UM_TRANS_SYS_TYPE_TER) {
            titles = getResources().getStringArray(R.array.dvb_setting_dtmb);
            instructs = getResources().getStringArray(R.array.dvb_setting_instruct_dtmb);
            textView.setText(R.string.terrestrial_settings);
        } else {
            titles = getResources().getStringArray(R.array.dvb_setting_dvbc);
            instructs = getResources().getStringArray(R.array.dvb_setting_instruct_dvbc);
        }
        
        dvb_setting_list.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View view, int keycode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {  
					int position = dvb_setting_list.getSelectedItemPosition();
					switch (keycode) {
					 case KeyEvent.KEYCODE_DPAD_DOWN:
						 if(dvb_setting_list.getSelectedItemPosition() ==dvb_setting_list.getCount() - 1){
							 dvb_setting_list.setSelection(0); 
							 //Log.i(TAG,"KEYCODE_DPAD_DOWN");
						 }
                     break;
			        case KeyEvent.KEYCODE_DPAD_UP:
			            if (dvb_setting_list.getSelectedItemPosition() == 0) {
			            	dvb_setting_list
			                        .setSelection(dvb_setting_list.getCount() - 1);
			            }
			            break;	
			        case KeyEvent.KEYCODE_MENU:
			        	finish();
			        	break;
			    	 case KeyEvent.KEY_SOURCEENTER:	
			    		 listviewItemClick(position);		
			    		 return true;	
					}
				}
				return false;
			}
		});
        
		for (int i=0, size = titles.length; i < size; i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			
			map.put("listItemTitle", titles[i]);
			map.put("listItemText", instructs[i]);
			listItemData.add(map);
		}
        adapter = new SimpleAdapter(this, listItemData, R.layout.dvb_list_item,
        		                     new String[]{"listItemTitle","listItemText"}, new int[] {R.id.ListItemTitle, R.id.ListItemText});
        dvb_setting_list.setAdapter(adapter);
        dvb_setting_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                restarAutoFinishTimer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dvb_setting_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                listviewItemClick(position);
			}
		});
 /*         setContentView(R.layout.sys_setting);

      LinearLayout[] Layout = new LinearLayout[6];

        Layout[0] = (LinearLayout)findViewById(R.id.prog_search);
        Layout[1] = (LinearLayout)findViewById(R.id.mainfreq_set);
        Layout[2] = (LinearLayout)findViewById(R.id.signal_check);
        Layout[3] = (LinearLayout)findViewById(R.id.pwd_set);
        Layout[4] = (LinearLayout)findViewById(R.id.factory_reset);
        Layout[5] = (LinearLayout)findViewById(R.id.dvbboot_set);

        for(int i = 0; i < Layout.length; i++)
        {
            Layout[i].setOnClickListener(handler);
            Layout[i].setOnFocusChangeListener(mFocusChangeListener);
        }

        if (!isTvProduct()) {
            TextView title = (TextView) Layout[0].findViewById(R.id.tv_title);
            ImageView image = (ImageView) Layout[0].findViewById(R.id.dvbsetting_image1);
            title.setText(R.string.tuner_setting);
            image.setBackgroundResource(R.drawable.tuner_setting);
        }
        focusFrame = (ImageView)findViewById(R.id.dvbsettint_foucs);*/
        playInfo =  getIntent().getExtras();

        setAutoFinish(Constant.AUTO_FINISH_WAIT_TIME_SHORT, null);
    }

    private boolean isTvProduct() {
        String product = SystemProperties.get("ro.product.device", "");
        return product.contains("3751");
    }
    
    private void factoryResetConfirmDialog()
    {
  	  AlertDialog.Builder builder =new AlertDialog.Builder(SysSetting.this);
	      LayoutInflater factory = LayoutInflater.from(SysSetting.this);
	      View myView = factory.inflate(R.layout.notify_dialog_cancle,null);
		   Button   mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
		   final Button   mSystemCancleBtn = (Button) myView.findViewById(R.id.user_back_cancle);
		
	      final AlertDialog   mAlertDialog = builder.create();
	      mAlertDialog.show();
	      mAlertDialog.getWindow().setContentView(myView);
        final CountDownTimer countDownTimer = new CountDownTimer(1000*5 + 100, 1000) {//閿熸枻鎷锋椂閿熸垝锛�閿熸枻鎷烽敓缁炴唻鎷烽敓锟�            
        	public void onTick(long millisUntilFinished) {
                String str = getString(R.string.cancel) + "(" + millisUntilFinished/1000 + "s)";
                mSystemCancleBtn.setText(str);
            }

            public void onFinish() {
				mAlertDialog.dismiss();
                restarAutoFinishTimer();
            }
        }.start();

		  mSystemOKBtn.setOnClickListener(new OnClickListener() {
	
		          @Override
		          public void onClick(View arg0) {
		        		mAlertDialog.dismiss();  
		        		
		        		Log.v(TAG, "PROGRESET: ready to reset dvb data...");
		        		
		        		ProgStorage ps = new ProgStorage(getContentResolver());
		                int tunnerType = Tuner.GetInstance(DVB.getInstance()).GetType();
		                Log.v(TAG, "tunnerType = " + tunnerType);
		                int categoryID = (tunnerType==Tuner.UM_TRANS_SYS_TYPE_TER?ContentSchema.CategoryTable.DTMB_ID:ContentSchema.CategoryTable.DVBC_ID);
		        		ps.DeleCategoryProg(new int[]{categoryID});
		        		Log.v(TAG, "clear provider done. categoryID=" + categoryID);
		        		
		                ParamSave.resetAll(SysSetting.this);
		                ProgManage.GetInstance().resetDvbProgList();
		                ProgManage.GetInstance().refreshProgList();
		        		Log.v(TAG, "reset prog db done.");
		        		
		        		/* broadcast FACTORY_RESET */
		        		Log.v(TAG, "ready to send broadcast.");
		        		Intent it = new Intent("com.unionman.dvb.ACTION_DVB_FACTORY_RESET");
		        		it.putExtra("trans_type", Tuner.GetInstance(DVB.getInstance()).GetType());
		        		sendBroadcast(it);
		                SysSetting.this.tips(getResources().getString(R.string.factory_reset_done));
                      countDownTimer.cancel();
                      Log.v(TAG, "ready to start fullscreen play.");
                      startFullscreenPlay();
                      SysSetting.this.finish();
		          }
		      });
		  mSystemCancleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mAlertDialog.dismiss();
                countDownTimer.cancel();
                restarAutoFinishTimer();
			}
		});

        mSystemCancleBtn.setText(getString(R.string.cancel) + "(5s)");

/*        AlertDialog.Builder builder = new AlertDialog.Builder(SysSetting.this);

        builder.setMessage(R.string.factory_reset_confirm);
        builder.setTitle(R.string.note);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

                ParamSave.resetMainFreq(SysSetting.this.getContentResolver());
                ParamSave.resetParentLock(SysSetting.this);

                ParamSave.resetParentPasswd(SysSetting.this);
 
                ProgManage.GetInstance().resetDvbProgList();
                ProgManage.GetInstance().refreshProgList();
        		ProgStorage ps = new ProgStorage(getContentResolver());
        		ps.allClear();

                SysSetting.this.tips(getResources().getString(R.string.factory_reset_done));
            }
        });

        builder.create().show();*/
    }

	private void startFullscreenPlay() {
		Intent intent = new Intent("com.unionman.intent.ACTION_PLAY_DVB");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
    private void checkOpenFactoryReset()
    {
        if (true)
        {
              Log.i(TAG,"checkOpenFactoryReset()");
    	      LayoutInflater factory = LayoutInflater.from(SysSetting.this);
    	      View myView = factory.inflate(R.layout.psw_input_dialog,null);
              final  Dialog   mAlertDialog = new Dialog(mContext,R.style.NobackDialog);
              mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
              mAlertDialog.setContentView(myView);
              mAlertDialog.show();
              
              mAlertDialog.setOnDismissListener(new OnDismissListener() {  				
				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub
					restarAutoFinishTimer();
				}
			});

    	      TextView textView = (TextView) myView.findViewById(R.id.system_back_text);
    	      textView.setText(R.string.input_password_hint);
    		   final Button   mSystemOKBtn = (Button) myView.findViewById(R.id.user_back_ok);
    		   final Button   mSystemCancleBtn = (Button) myView.findViewById(R.id.user_back_cancle);
    		   final EditText edittext =(EditText)   myView.findViewById(R.id.system_back_edittext);
            edittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
/*                    restarAutoFinishTimer();*/
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

//    	      final AlertDialog   mAlertDialog = builder.create();
//    	      mAlertDialog.show();
//    	      mAlertDialog.getWindow().setContentView(myView);
/*            final CountDownTimer countDownTimer = new CountDownTimer(30 * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    mAlertDialog.dismiss();
                    restarAutoFinishTimer();
                }
            }.start();*/
    		  mSystemOKBtn.setOnClickListener(new OnClickListener() {
    	
    		          @Override
    		          public void onClick(View arg0) {
    	                    resetDialogOKBtnClick(edittext, mAlertDialog);
/*                          countDownTimer.cancel();*/
    		          }
    		      });
    		  mSystemCancleBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
                    restarAutoFinishTimer();
/*                    countDownTimer.cancel();*/
					mAlertDialog.dismiss();
				}
			});
    		  mAlertDialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
  				
    				@Override
    				public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
    					 if(arg2.getAction() == KeyEvent.ACTION_DOWN){	
    						 switch (keycode) {
    						 case KeyEvent.KEYCODE_MENU:
    			                    restarAutoFinishTimer();
    								mAlertDialog.dismiss();
    							 break;
    						 case KeyEvent.KEY_SOURCEENTER:	
                                if(mSystemOKBtn.isFocused()){
    								resetDialogOKBtnClick(edittext, mAlertDialog);
                                }
                                if(mSystemCancleBtn.isFocused()){
                                    restarAutoFinishTimer();
                					mAlertDialog.dismiss();
                                }
    				    		 return true;	 
    						 }
    					 }
    					return false;
    				}
    			}); 
/*            final EditText inputServer = new EditText(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            InputFilter[] filters = {new InputFilter.LengthFilter(6)};
            inputServer.setFilters(filters);

            inputServer.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            builder.setTitle(R.string.input_password_hint).setIcon(android.R.drawable.ic_dialog_info)
                    .setView(inputServer).setNegativeButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    if (0 == inputServer.getText().toString().compareTo(ParamSave.GetParentPasswd(SysSetting.this)))
                    {
                        SysSetting.this.factoryResetConfirmDialog();
                    }
                    else
                    {
                        tips(getResources().getString(R.string.password_incorrect) + "!");
                    }
                }
            });
            builder.create().show();*/
        	
        }
        else
        {
            SysSetting.this.factoryResetConfirmDialog();
        }
    }

//    private void checkOpenPwdSetting()
//    {
//        if (ParamSave.GetParentLock(this))
//        {
//            final EditText inputServer = new EditText(this);
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//            InputFilter[] filters = {new InputFilter.LengthFilter(6)};
//            inputServer.setFilters(filters);
//
//            inputServer.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
//            builder.setTitle(R.string.input_password_hint).setIcon(android.R.drawable.ic_dialog_info)
//                    .setView(inputServer).setNegativeButton(R.string.cancel, null);
//            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
//            {
//                public void onClick(DialogInterface dialog, int which)
//                {
//                    if (0 == inputServer.getText().toString().compareTo(ParamSave.GetParentPasswd(SysSetting.this)))
//                    {
//                        Intent it3 = new Intent(SysSetting.this, ParentLock.class);
//                        startActivity(it3);
//                    }
//                    else
//                    {
//                        tips(getResources().getString(R.string.password_incorrect) + "!");
//                    }
//                }
//            });
//            builder.create().show();
//        }
//        else
//        {
//            Intent it3 = new Intent(SysSetting.this, ParentLock.class);
//            startActivity(it3);
//        }
//    }

    private void tips(String s)
    {
    	Toast.makeText(this ,s,Toast.LENGTH_LONG).show();
    }

private void listviewItemClick(int position) {
	int tunnerType = Tuner.GetInstance(DVB.getInstance()).GetType();
	if (tunnerType == Tuner.UM_TRANS_SYS_TYPE_TER) {
	    position += 2;
	}
	switch (position) {
	case 0:
	    String strCasType = SystemProperties.get("persist.sys.dvb.cas.type" , "-1");
	    Log.i("", "get strCasType:" + strCasType);
	    if("TF".equalsIgnoreCase(strCasType)){
	        Intent mainIntent = new Intent("unionman.intent.ation.TFCA");
	        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(mainIntent);
	    }
	    else if("DVT".equalsIgnoreCase(strCasType) ){
	        Intent mainIntent = new Intent("unionman.intent.ation.DVTCA");
	        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(mainIntent);
	    }
	    else if("IRD".equalsIgnoreCase(strCasType)){
	    	Log.i("", "get strCasType:_xinhua" + strCasType);
	        Intent mainIntent = new Intent("unionman.intent.ation.IRDCA");
	        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(mainIntent);
	    }
	    else if("DVN".equalsIgnoreCase(strCasType) ){
	        Intent mainIntent = new Intent("unionman.intent.ation.DVNCA");
	        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(mainIntent);
	    }
	    else if("WF".equalsIgnoreCase(strCasType) ){
	    	Log.i("", "get strCasType:_xinhua" + strCasType);
	        Intent mainIntent = new Intent("unionman.intent.ation.WFCA");
	        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(mainIntent);
	    }
	    else if("-1".equalsIgnoreCase(strCasType)){
	        Toast.makeText(SysSetting.this, "can not find condition access", Toast.LENGTH_SHORT).show();
	    }

//					Intent it0 = new Intent();
//					it0.setClassName("com.um.tfca", "com.um.ui.Tf_condition_access");
//					startActivity(it0);
		break;
	case 1:
	       Intent it1 = new Intent(SysSetting.this, Freset.class);
	        startActivity(it1);
		break;
	case 2:
/*                    Intent it2 = new Intent(SysSetting.this, SignCheck.class);
	    startActivity(it2);*/
	    Intent it2 = new Intent(SysSetting.this, ProgramInfo.class);
	    it2.putExtras(playInfo);
	    startActivity(it2);
		break;
/*	case 3:
	    Intent it4 = new Intent(SysSetting.this, ParentLock.class);
	    startActivity(it4);
		break;				
	case 4:
	    Intent it5 = new Intent(SysSetting.this, SoundChannelSetting.class);
	    startActivity(it5);
		break;*/
	case 3:
	    stopAutoFinishTimer();
	    SysSetting.this.checkOpenFactoryReset();
		break;
	default:
		break;
	}
}

private void resetDialogOKBtnClick(final EditText edittext,
		final Dialog mAlertDialog) {
    String inputNum = edittext.getText().toString();
    String defaultPwd =Settings.Secure.getString(mContext.getContentResolver(), Constant.UMDefaultPwd);
      if(defaultPwd==null){
     	 defaultPwd = Constant.UMDefaultPwdValue;
     	 Log.i(TAG," defaultPwd==null; set defaultPwd ="+Constant.UMDefaultPwdValue);
      }
    String superPwd = Settings.Secure.getString(mContext.getContentResolver(), Constant.UMSuperPwd);
     if(superPwd==null){
 	     superPwd = Constant.UMSuperPwdValue;
     	 Log.i(TAG," superPwd==null; set superPwd ="+ Constant.UMSuperPwdValue);    
     }                     
    String restorePwd = Settings.Secure.getString(mContext.getContentResolver(), Constant.RestorePwd); 
		if(restorePwd==null){
			restorePwd =  defaultPwd;
		}
	  Log.i(TAG,"inputNum="+inputNum+" defaultPwd="+defaultPwd+" restorePwd="+restorePwd);
	  if(inputNum.equals(restorePwd)||inputNum.equals(superPwd))
	{   
		mAlertDialog.dismiss();
	    SysSetting.this.factoryResetConfirmDialog();
	}
	else
	{
	    tips(getResources().getString(R.string.password_incorrect) + "!");
	    edittext.setText("");
	    edittext.requestFocus();
	}
}

 /*   View.OnClickListener handler = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.prog_search:
                    if (!isTvProduct()) {
                        Intent it0 = new Intent(SysSetting.this, TunerSetting.class);
                        startActivity(it0);
                    } else {
                        Intent it3 = getPackageManager().getLaunchIntentForPackage("com.um.dvbsearch");
                        if (it3 != null) {
                            startActivity(it3);
                        }
                    }
                    break;
                case R.id.mainfreq_set:
                    Intent it1 = new Intent(SysSetting.this, Freset.class);
                    startActivity(it1);
                    break;
                case R.id.signal_check:
                    Intent it2 = new Intent(SysSetting.this, SignCheck.class);
                    startActivity(it2);
                    break;
                case R.id.pwd_set:
                    Intent it4 = new Intent(SysSetting.this, ParentLock.class);
                    startActivity(it4);
                    break;
                case R.id.factory_reset:
                    SysSetting.this.checkOpenFactoryReset();
                    break;
                case R.id.dvbboot_set:
                    Intent intent = new Intent(SysSetting.this, BootDVBActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener(){
        public void onFocusChange(View v,boolean hasFocus)
        {
            int[] location = new  int[2] ;
            LinearLayout Layout = (LinearLayout)findViewById(v.getId());

            if(true == hasFocus)
            {
                Layout.getLocationOnScreen(location);
                FocusAnimator focusAnimator = new FocusAnimator();
                focusAnimator.flyFoucsFrame(focusFrame, Layout.getWidth(), Layout.getHeight(), location[0], location[1]);
            }
        }
    };*/
}
