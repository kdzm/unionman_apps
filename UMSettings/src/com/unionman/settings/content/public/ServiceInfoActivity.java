package com.unionman.settings.content;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.TextView;

import com.unionman.settings.R;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.custom.CheckRadioButton.OnCheckedChangeListener;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.hisilicon.android.HiDisplayManager;

import android.net.Uri;
import android.os.SystemProperties;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.String;

public class ServiceInfoActivity extends RightWindowBase {

	private TextView tv_serviceAuthen = null;
	private TextView tv_serviceUserId = null;
	private TextView tv_platform = null;
	private TextView tv_serviceITMS = null;
	private TextView tv_serviceNTP = null;
	private TextView tv_serviceUpgrade = null;
	
	private Context mContext ;
	private static final String USERID_KEY= "UserID";
	private static final String STB_CONFIG_URL = "content://stbconfig/summary";
	//zte
	private static final String strITMS = "http://183.235.21.203:9090/web/tr069";
	private static final String strAuthen = "http://183.235.3.110:8082/EDS/jsp/AuthenticationURL";
	private static final String strUpgrade = "http://itms.gd.chinamobile.com:8088/itms-server/itms";
	//huawei
	private static final String strITMS_HW = "https://183.235.3.215:37020/acs";
	private static final String strAuthen_HW = "http://183.235.3.110:8082/EDS/jsp/AuthenticationURL";
	private static final String strUpgrade_HW = "http://183.235.3.110:8082/EDS/jsp/upgrade.jsp";
	private static final String TAG = "com.unionman.settings.content.display--ServiceInfoActivity--";
	
	private static int zte_userid = 1;
	private static int zte_itms = 15;
	private static int zte_auth = 11;
	private static int zte_upgrade = 0;
	private static int hw_userid = 3;
	private static int hw_itms = 15;
	private static int hw_auth = 11;
	private static int hw_upgrade = 0;
	

	public ServiceInfoActivity(Context paramContext) {
		super(paramContext);
		// TODO Auto-generated constructor stub
		mContext = paramContext;
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInvisible() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		
	}

	@Override
	public void setId() {
		this.levelId = 1001;

	}
	
	 public String readTxtFile(int which){
		 Logger.i(TAG, "readTxtFile()--");
	        try {
	                String encoding="UTF-8";
	                File file=new File("/data/keydata/keydata.txt");
	                if(file.isFile() && file.exists()){ //
	                    InputStreamReader read = new InputStreamReader(
	                    new FileInputStream(file),encoding);//
	                    BufferedReader bufferedReader = new BufferedReader(read);
	                    String lineTxt = null;
	                    int i = 1;
	                    while((lineTxt = bufferedReader.readLine()) != null){
	                        
	                        Logger.i(TAG, "line = "+ i +"  lineTxt ------ "+lineTxt);
	                        if (i == which) {
								return lineTxt;
							}else {
								i++;
							}	                       	                        
	                    }
	                    read.close();
	        }else{
	   		 	Logger.i(TAG, "not found File--");
	        }
	        } catch (Exception e) {
	        	Logger.i(TAG, "read file error --");
	            e.printStackTrace();
	        }
	     return null;
	    }

	@Override
	public void setView() {
		Logger.i(TAG, "setView()--");
		this.layoutInflater.inflate(R.layout.service_info, this);
		this.tv_serviceAuthen = (TextView)findViewById(R.id.service_authentication_address);
		this.tv_serviceUserId = (TextView)findViewById(R.id.service_user_id);
		this.tv_platform = (TextView)findViewById(R.id.service_platform);
		this.tv_serviceITMS = (TextView)findViewById(R.id.service_itms_address);
		this.tv_serviceNTP = (TextView)findViewById(R.id.service_ntp_address);
		this.tv_serviceUpgrade =(TextView)findViewById(R.id.service_upgrade_address);
		String strSerialno = SystemProperties.get("ro.serialno", "");
	//	String strMac = strSerialno.substring(strSerialno.length() - 12, strSerialno.length());
		if (strSerialno.substring(40, 41).equals("0")) {
			Logger.i(TAG, "huawei--");
			this.tv_platform.setText(ServiceInfoActivity.this.getResources().getString(R.string.server_platform_hw));
			String strUserID = getDataFromStbConfig(USERID_KEY);
//			if (strUserID == null || strUserID.isEmpty()) {
//				strUserID = readTxtFile(hw_userid);
//			}
			this.tv_serviceUserId.setText(strUserID);
			this.tv_serviceITMS.setText(readTxtFile(hw_itms));
			this.tv_serviceAuthen.setText(readTxtFile(hw_auth));
			this.tv_serviceUpgrade.setText(this.strUpgrade);
			this.tv_serviceNTP.setText(getNtpServer());
		}else if(strSerialno.substring(40, 41).equals("1")){
			Logger.i(TAG, "zte--");
			this.tv_platform.setText(ServiceInfoActivity.this.getResources().getString(R.string.server_platform_zte));
			String strUserID = getDataFromStbConfig(USERID_KEY);
//			if (strUserID == null || strUserID.isEmpty()) {
//				strUserID = readTxtFile(zte_userid);
//			}
			this.tv_serviceUserId.setText(strUserID);
			this.tv_serviceITMS.setText(readTxtFile(zte_itms));
			this.tv_serviceAuthen.setText(readTxtFile(zte_auth));
			this.tv_serviceUpgrade.setText(this.strUpgrade);
			this.tv_serviceNTP.setText(getNtpServer());
		}
		
	}

	private String getNtpServer() {
		Logger.i(TAG,"getNtpServer()--");
		String strNtpServer = Settings.Secure.getString(
				this.context.getContentResolver(), "ntp_server");
		if (strNtpServer == null) {
			String strSerialno = SystemProperties.get("ro.serialno", "");
			if (strSerialno.substring(40, 41).equals("0")) {
				strNtpServer = "183.235.3.59";
			}else if (strSerialno.substring(40, 41).equals("1")) {
				strNtpServer = "221.181.100.40";
			}
			
		}
		return strNtpServer;
	}
	
	public String getDataFromStbConfig(String key){
		Log.i(TAG, "getDataFromStbConfig()----");
		String keyString = key;
		String valueString = null;
		ContentResolver cr = getContext().getContentResolver();
		Uri uri = Uri.parse(STB_CONFIG_URL);
		String[] prj = {keyString};
		
		Cursor cu =  cr.query(uri, prj, null, null, null);		
		if (cu == null){
			valueString = null;
			Log.i(TAG, keyString+" value is null ======= ");
		}else  if (cu.getCount() > 0) {
			cu.moveToNext();
			valueString = cu.getString(0);
			Log.i(TAG, keyString+" value is  ======= "+valueString);
		
		}
		return valueString;
	}
}
