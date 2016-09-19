package com.unionman.dvbcitysetting;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.unionman.dvbcitysetting.data.City;
import com.unionman.dvbcitysetting.data.ConfigContent;
import com.unionman.dvbcitysetting.data.InstalledCityInfo;
import com.unionman.dvbcitysetting.data.LocalAdapter;
import com.unionman.dvbcitysetting.data.Province;
import com.unionman.dvbcitysetting.data.State;
import com.unionman.dvbcitysetting.util.BitmapCtl;
import com.unionman.dvbcitysetting.util.CitySettingHelper;
import com.unionman.dvbcitysetting.util.DvbUtils;
import com.unionman.dvbcitysetting.util.FileUtils;
import com.unionman.dvbcitysetting.util.InitDvbFeatureService;
import com.unionman.dvbcitysetting.util.PackageUtils;
import com.unionman.dvbcitysetting.util.PreferencesUtils;
import com.unionman.dvbcitysetting.util.PropertyUtils;
import com.unionman.dvbcitysetting.util.StringUtils;
import com.unionman.dvbcitysetting.widget.SweetAlertDialog;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CitySettingActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    private static final String TAG = "CitySettingActivity";
	private static final int ACTIVITY_FINISH = 0;
	private static final int DISPEAR_TIME_30s = 30000;
	protected static final int DISMISS_DIALOG = 1;
    protected static final int UPDATA_UI = 2;
	private Spinner provinceSpinner = null;  //省级（省、直辖市）
    private Spinner stateSpinner = null;     //地级市
    private Spinner citySpinner = null;    //县级（区、县、县级市）
    private ArrayAdapter<Province> provinceAdapter = null;  //省级适配器
    private ArrayAdapter<State> stateAdapter = null;    //地级适配器
    private ArrayAdapter<City> cityAdapter = null;    //县级适配器

    private List<Province> provinces = null;
    private Province selecedProvince;
    private State selectedState;
    private City selectedCity;
    private String selectFullName;
    private boolean isInSettingWizard;
    private  SweetAlertDialog mSweetAlertDialog;

    private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        private SweetAlertDialog statusDialog;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive() action=" + action);
            if (InstallStatus.ACTION_CITY_INSTALL.equals(action)) {
                String status = intent.getStringExtra(InstallStatus.STATUS);
                Log.i(TAG,"status="+status);
                if (!StringUtils.isEmpty(status) && status.contains(InstallStatus.PERCENT_SEPARATOR)) {
                    int value = StringUtils.percentStringToInt(status, 100);
                    Log.d("", "=====percentStringToInt: " + value);
                } else if (InstallStatus.INSTALL_PACKAGE_START.equals(status)) {
                    String title = getString(R.string.city_dvb_installing_title);
                    String content = getString(R.string.city_dvb_installing_content);
                    String confirm = getString(R.string.dialog_ok);
                    PreferencesUtils.putBoolean(CitySettingActivity.this, "inttall_changed", true);
                    statusDialog = showDialog(SweetAlertDialog.PROGRESS_TYPE, title, content, confirm);
                } else if (InstallStatus.INSTALL_PACKAGE_SUCCESS.equals(status)){
                    String title = getString(R.string.city_install_success_title);
                    String content = getString(R.string.city_install_success_msg);
                    String confirm = getString(R.string.dialog_ok);
                    statusDialog.setTitleText(title).setContentText(content).setConfirmText(confirm);
                    statusDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                } else if(InstallStatus.START_DVBSERVER_SUCCESS.equals(status)) {
                    statusDialog.dismiss();

                    if(isInSettingWizard){
	        			Intent intent_finish = new Intent();
	        			intent_finish.setClassName("com.unionman.settingwizard", "com.unionman.settingwizard.ui.SetupFinishActivity");
	        			startActivity(intent_finish);
                       }
        			finish();
                } else if (InstallStatus.INSTALL_PACKAGE_FAILED.equals(status)) {
                    String title = getString(R.string.city_install_failed_title);
                    String content = getString(R.string.city_install_failed_msg);
                    String confirm = getString(R.string.dialog_ok);
                    mSweetAlertDialog = showDialog(SweetAlertDialog.ERROR_TYPE, title, content, confirm);
                } else if (InstallStatus.CITY_ALREADY_INSTALLED.equals(status)){
                    if(isInSettingWizard){
        			Intent intent_finish = new Intent();
        			intent_finish.setClassName("com.unionman.settingwizard", "com.unionman.settingwizard.ui.SetupFinishActivity");
        			startActivity(intent_finish);
           			finish();
                    }else{
                        String title = getString(R.string.city_already_install_title);
                        String content = getString(R.string.city_already_install_msg);
                        String confirm = getString(R.string.dialog_ok);
                        mSweetAlertDialog = showDialog(SweetAlertDialog.WARNING_TYPE, title, content, confirm);  
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent=getIntent();
        isInSettingWizard=intent.getBooleanExtra("isInSettingWizard", false);
        Log.i(TAG,"onCreate() isInSettingWizard="+isInSettingWizard);
        if(isInSettingWizard){
            setContentView(R.layout.activity_city_setting2);
            super.onCreate(savedInstanceState);

            Button nextStepBtn = (Button) findViewById(R.id.btn_next_step);
            nextStepBtn.requestFocus();
        }else{
            setContentView(R.layout.activity_city_setting3);
            super.onCreate(savedInstanceState);

            Button finishBtn = (Button) findViewById(R.id.btn_finish);
            finishBtn.requestFocus();
        }

        new Thread(new GetDataThread()).start();
    }

    private SweetAlertDialog.OnSweetClickListener cancelClickListener = new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismiss();
        }
    };
    private SweetAlertDialog.OnSweetClickListener confirmClickListener = new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismiss();
            initDvbLocalFeature(selectedCity.getConfigFilePath());
        }
    };

    private int checkInstallStatus() {
        String installedConfig = PreferencesUtils.getString(this, CitySettingHelper.INSTALLED_CITY_CONFIG_FILE, "");
        String packageStr = PreferencesUtils.getString(this, CitySettingHelper.LAST_INSTALLED_PACKAGE, "");
        String installingConfig = PreferencesUtils.getString(this, CitySettingHelper.INSTALLING_CITY_CONFIG_FILE, "");

        InstalledCityInfo installedCityInfo = new InstalledCityInfo();
        installedCityInfo.setInstalledConfigFilePath(installedConfig);
        installedCityInfo.setInstallingConfigFilePath(installingConfig);
        installedCityInfo.setInstalledPackages(StringUtils.stringToList(packageStr, CitySettingHelper.PACKAGES_SEPARATOR));

        CityDvbInstaller cityDvbInstaller = new CityDvbInstaller(this, null, installedCityInfo);
        List<String> uninstallPackages = cityDvbInstaller.getUninstallFile();
        
        // installed标志未置上，则认为未安装
        if (PropertyUtils.getInt(CitySettingHelper.DVB_INSTALLED, 0) == 0) {
        	return -1;
        }
        
        if (!cityDvbInstaller.isFinish()) {
            return CityDvbInstaller.TYPE_RE_INSTALL;
		} else if (uninstallPackages.size() == 1
				&& PackageUtils.getPackageInfoByFile(this,
						uninstallPackages.get(0)).packageName
						.equals("com.unionman.cityfeature")) {
			return -1;
		} else if (!cityDvbInstaller.isComplete()) {
            return CityDvbInstaller.TYPE_FIX_INSTALL;
        }

        return -1;
    }

    private String getCaNameByType(String type) {
        if (!StringUtils.isBlank(type)) {
            if (type.equals("tf")){
                return getResources().getString(R.string.tf_ca);
            } else if (type.equals("dvt")){
                return getResources().getString(R.string.dvt_ca);
            }
        }
        return null;
    }

    private String getCaNamesByTypes(List<String> types) {
        if (types != null && !types.isEmpty()) {
            String caNames = "\n";
            for (String type : types) {
                caNames += getCaNameByType(type) + "\n";
            }
            return caNames;
        }
        return null;
    }

    private void showExplainText(String configPath) {
        List<String> caTypes = null;
        try {
            ConfigContent content = CitySettingHelper.getConfigFileContent(new File(configPath));
            if (content != null) {
                caTypes = content.getCaSupport();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (caTypes != null) {
            String explain = getResources().getString(R.string.city_ca_support)
                    + getCaNamesByTypes(caTypes);
            ((TextView)findViewById(R.id.tv_explain_text_1)).setText(explain);
        }
    }

    private void init() {
        if (provinces != null) {
            setupSpinner();
        }
        registerStatusReceiver();

        Intent intent = getIntent();
        int type;
        if (intent.hasExtra(CitySettingHelper.KEY_LAUNCH_TYPE)){
            type = getIntent().getIntExtra(CitySettingHelper.KEY_LAUNCH_TYPE, CityDvbInstaller.TYPE_INSTALL_NEW);
        } else {
            type = checkInstallStatus();
        }

        if (type == CityDvbInstaller.TYPE_FIX_INSTALL) {
            String title = getString(R.string.city_fix_install_title);
            String content = getString(R.string.city_fix_install_msg);
            String confirm = getString(R.string.yes);
            String cancel = getString(R.string.no);
            mSweetAlertDialog =  showDialog(SweetAlertDialog.WARNING_TYPE, title, content, confirm, cancel, true, confirmClickListener, cancelClickListener);
        } else if (type == CityDvbInstaller.TYPE_RE_INSTALL) {
            String title = getString(R.string.city_re_install_title);
            String content = getString(R.string.city_re_install_msg);
            String confirm = getString(R.string.yes);
            String cancel = getString(R.string.no);
            mSweetAlertDialog =  showDialog(SweetAlertDialog.ERROR_TYPE, title, content, confirm, cancel, true, confirmClickListener, cancelClickListener);
        }

        if(isInSettingWizard){
            Button nextStepBtn = (Button) findViewById(R.id.btn_next_step);
            Button lastStepBtn = (Button) findViewById(R.id.btn_last_step);
            nextStepBtn.setOnClickListener(this);
            lastStepBtn.setOnClickListener(this);
            nextStepBtn.requestFocus();
        }else{
            Button cancelBtn = (Button) findViewById(R.id.btn_cancel);
            Button finishBtn = (Button) findViewById(R.id.btn_finish);
            cancelBtn.setOnClickListener(this);
            finishBtn.setOnClickListener(this);
            finishBtn.requestFocus();
        }
    }

    private void setupSpinner() {
        provinceSpinner = (Spinner) findViewById(R.id.spin_province);
        stateSpinner = (Spinner) findViewById(R.id.spin_state);
        citySpinner = (Spinner) findViewById(R.id.spin_city);      

        String cityFullName = getInstallingCityFullName();
        int[] index = CitySettingHelper.getIndex(provinces, cityFullName);
        int provinceIndex = index[0];
        int stateIndex = index[1];

        //绑定适配器和值
        provinceAdapter = new ArrayAdapter<Province>(this, R.layout.simple_spinner_item, provinces){
     	   @Override
           public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
    		   parent.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
					// TODO Auto-generated method stub
					   if(arg2.getAction() == KeyEvent.ACTION_DOWN){
						   Log.i(TAG,"getDropDownView() keycode="+arg1);
						   delay();
					   }
					return false;
				}
			});
               return super.getDropDownView(position, convertView, parent);
           }
   	
    };
        provinceAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
        provinceSpinner.setAdapter(provinceAdapter);
        provinceSpinner.setSelection(provinceIndex, true);  //
        selecedProvince = provinces.get(provinceIndex);

        stateAdapter = new ArrayAdapter<State>(this, R.layout.simple_spinner_item, selecedProvince.getStates()){
     	   @Override
           public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
    		   parent.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
					// TODO Auto-generated method stub
					   if(arg2.getAction() == KeyEvent.ACTION_DOWN){
						   Log.i(TAG,"getDropDownView() keycode="+arg1);
						   delay();
					   }
					return false;
				}
			});
               return super.getDropDownView(position, convertView, parent);
           }
   	
    };
        stateAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
        stateSpinner.setAdapter(stateAdapter);
        stateSpinner.setSelection(stateIndex, true);  //
        selectedState = selecedProvince.getStates().get(stateIndex);

        if (index.length >= 3) {
            int cityIndex = index[2];
            List<City> cities = selectedState.getCities();

            cityAdapter = new ArrayAdapter<City>(this, R.layout.simple_spinner_item, cities){
          	   @Override
               public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
        		   parent.setOnKeyListener(new OnKeyListener() {
    				
    				@Override
    				public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
    					// TODO Auto-generated method stub
    					   if(arg2.getAction() == KeyEvent.ACTION_DOWN){
    						   Log.i(TAG,"getDropDownView() keycode="+arg1);
    						   delay();
    					   }
    					return false;
    				}
    			});
                   return super.getDropDownView(position, convertView, parent);
               }
       	
        };

            cityAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
            citySpinner.setAdapter(cityAdapter);
            citySpinner.setSelection(cityIndex, true);
            selectedCity = selectedState.getCities().get(cityIndex);
            showExplainText(selectedCity.getConfigFilePath());
        } else {
            citySpinner.setEnabled(false);
            showExplainText(selectedState.getConfigFilePath());
        }

        selectFullName = cityFullName;

        provinceSpinner.setOnItemSelectedListener(this);
        stateSpinner.setOnItemSelectedListener(this);
        citySpinner.setOnItemSelectedListener(this);       
    
    }
    
    private void registerStatusReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(InstallStatus.ACTION_CITY_UNINSTALL);
        intentFilter.addAction(InstallStatus.ACTION_CITY_INSTALL);
        registerReceiver(mStatusReceiver, intentFilter);
    }

/*    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        ImageView reflectedView = (ImageView) findViewById(R.id.imgv_reflection);
        LinearLayout contentView = (LinearLayout) findViewById(R.id.content_layout);
        new BitmapCtl().setReflectionSync(contentView, reflectedView);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mStatusReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	Log.i(TAG,"onKeyDown()");
        	delay();
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            	if(!isInSettingWizard){
            		finish();
            	}
                return true;
                
            case KeyEvent.KEYCODE_SOURCE:
            	Log.i(TAG,"click KEY_SOURCE,return true");
            	if(isInSettingWizard){
                	return true;   
            	}
                break;          
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_last_step:
               Intent intent = new Intent();
                intent.setClassName("com.unionman.settingwizard", "com.unionman.settingwizard.ui.SourceSetupActivity");
                startActivity(intent);
                finish();
                break;
            case R.id.btn_next_step:
            case R.id.btn_finish:
                if (selectedCity == null) {
                    initDvbLocalFeature(selectedState.getConfigFilePath());
                } else {
                    initDvbLocalFeature(selectedCity.getConfigFilePath());
                }

                if (!isCaCardInsert()) {
                    String title = getString(R.string.dialog_no_ca_card);
                    String content = getString(R.string.ca_not_found_tips);
                    String confirm = getString(R.string.dialog_ok);
                    showDialog(SweetAlertDialog.WARNING_TYPE, title, content, confirm);
                }
                break;
            case R.id.btn_cancel:
             	 finish();
            	break;
            default:
                break;
        }
    }

    private SweetAlertDialog showDialog(int type,
                                        String title,
                                        String msg,
                                        String confirm,
                                        String cancel,
                                        boolean showCancleButton,
                                        SweetAlertDialog.OnSweetClickListener confirmlistener,
                                        SweetAlertDialog.OnSweetClickListener canclelistener) {
        SweetAlertDialog dialog = new SweetAlertDialog(this, type)
                .setTitleText(title)
                .setContentText(msg)
                .setCancelText(cancel)
                .setConfirmText(confirm)
                .showCancelButton(showCancleButton)
                .setConfirmClickListener(confirmlistener)
                .setCancelClickListener(canclelistener);

        dialog.show();
        if(!isInSettingWizard){
	        delayForDialog();
	        dialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
					// TODO Auto-generated method stub
	             if(arg2.getAction() == KeyEvent.ACTION_DOWN){
	            	 //有按键操作时发送30s消失的延时消息
	            	 delayForDialog();
	             }
					return false;
				}
			});
	        dialog.setOnDismissListener(new OnDismissListener() {					
				@Override
				public void onDismiss(DialogInterface arg0) {
					Log.i(TAG,"dialog onDismiss() removeMessages");
					dialogHandle.removeMessages(DISMISS_DIALOG);
				}
			});
        }
        return dialog;
    }

    private SweetAlertDialog showDialog(int type, String title, String msg, String confirm) {
        return showDialog(type, title, msg, confirm, "", false, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        }, null);
    }

    // 留这个接口，暂时返回true，不判断是否插卡；后续加入checkca相关功能，如果返回false则提示插入ca卡等信息
    private boolean isCaCardInsert() {
        return true;
    }

    private String getInstallingCityFullName() {
        String cityFullName = CitySettingHelper.DEFAULT_CITY_NAME;;
        String path =  PreferencesUtils.getString(this, CitySettingHelper.INSTALLING_CITY_CONFIG_FILE, "");
        Log.d(TAG, "path: " + path);
        Log.d(TAG, "isFileExist(path): " + FileUtils.isFileExist(path));
        if (FileUtils.isFileExist(path)) {
            ConfigContent configContent;
            try {
                configContent = CitySettingHelper.getConfigFileContent(new File(path));
                cityFullName = CitySettingHelper.getCityFullName(configContent);

                Log.d(TAG, "cityFullName: " + cityFullName);
                Log.d(TAG, "configContent.getCity(): " + configContent.getCity());
            } catch (Exception e) {
                cityFullName = CitySettingHelper.DEFAULT_CITY_NAME;
                e.printStackTrace();
            }
        }

        Log.d(TAG, "cityFullName ret: " + cityFullName);
        return cityFullName;
    }

    private void initDvbLocalFeature(String config) {
        Intent intent = new Intent(this, InitDvbFeatureService.class);
        intent.putExtra(CitySettingHelper.CURRENT_CITY_CONFIG_FILE, config);
        startService(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int id = adapterView.getId();
        Log.i(TAG,"onItemSelected()");
    	delay();    	
        switch (id) {
            case R.id.spin_province:
                selecedProvince = provinces.get(i);
                stateAdapter = new ArrayAdapter<State>(this, R.layout.simple_spinner_item, selecedProvince.getStates());
                stateAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
                stateSpinner.setAdapter(stateAdapter);
                break;
            case R.id.spin_state:
                selectedState = selecedProvince.getStates().get(i);
                ArrayList<City> cities = selectedState.getCities();

                cityAdapter = new ArrayAdapter<City>(this, R.layout.simple_spinner_item, cities);
                cityAdapter.setDropDownViewResource(R.layout.simple_spinner_item);
                citySpinner.setAdapter(cityAdapter);
                if (cities.isEmpty()) {
                    selectFullName = selecedProvince.getName() + ";" + selectedState.getName();
                    selectedCity = null;
                    citySpinner.setEnabled(false);
                    showExplainText(selectedState.getConfigFilePath());
                } else {
                    citySpinner.setEnabled(true);
                }
                break;
            case R.id.spin_city:
                selectedCity = selectedState.getCities().get(i);
                selectFullName = selecedProvince.getName() + ";"  + selectedState.getName() + ";"  + selectedCity.getName();
                showExplainText(selectedCity.getConfigFilePath());
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    	  Log.i(TAG,"onNothingSelected()");
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	if(!isInSettingWizard){
        	Log.i(TAG,"onWindowFocusChanged hasFocus="+hasFocus);
	        if (hasFocus) {
	            delay();
	        } else {
	            finishHandle.removeMessages(ACTIVITY_FINISH);
	        }
    	}
        super.onWindowFocusChanged(hasFocus);
    }    

    /**
     * handler of finish activity
     */
    private Handler finishHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == ACTIVITY_FINISH)
                finish();
        };
    };

    private Handler mUpdateUIHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATA_UI:
                    init();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    class GetDataThread implements Runnable {
        public void run() {
            ArrayList<File> configFiles = new ArrayList<File>();
            try {
                FileUtils.getFilesWithName(new File(CitySettingHelper.SYSTEM_LOCAL_ROOT), CitySettingHelper.CONFIG_FILE, configFiles);
                FileUtils.getFilesWithName(new File(CitySettingHelper.SDCARD_LOCAL_ROOT), CitySettingHelper.CONFIG_FILE, configFiles);
                List<ConfigContent> configContents = CitySettingHelper.getAllConfigFileContent(configFiles);
                provinces = CitySettingHelper.createProvince(configContents);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Message message = new Message();
            message.what = UPDATA_UI;
            mUpdateUIHandler.sendMessage(message);
        }
    }

    /**
     * set delay time to finish activity
     */
    public void delay() {
    	if(!isInSettingWizard){
        	Log.i(TAG,"delay() is calling");
            finishHandle.removeMessages(ACTIVITY_FINISH);
            Message message = new Message();
            message.what = ACTIVITY_FINISH;
            finishHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
    	}

    }  
    
    private Handler dialogHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == DISMISS_DIALOG){
	            	Log.i(TAG,"mSweetAlertDialog="+mSweetAlertDialog);
	            	if(mSweetAlertDialog!=null){
	            	mSweetAlertDialog.dismiss();
	            	}
            }
        };
    };
    
    public void delayForDialog() {
    	if(!isInSettingWizard){
    	Log.i(TAG,"delayForDialog() is calling");
    	dialogHandle.removeMessages(DISMISS_DIALOG);
        Message message = new Message();
        message.what = DISMISS_DIALOG;
        dialogHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
    	}
    }  
    
}
