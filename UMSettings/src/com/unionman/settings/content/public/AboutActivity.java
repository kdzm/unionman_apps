package com.unionman.settings.content;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.unionman.settings.R;
import com.um.huanauth.data.HuanClientAuth;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.wifi.UMWifiManager;

public class AboutActivity extends RightWindowBase implements OnKeyListener {
	
	private UMWifiManager wifiManager;
	private ListView listView;
	private TextView tvHead;
	private Button btn_bottom_focus;
	private SimpleAdapter mSimpleAdaper;
	private OnFocusChangeListener focusListener;
	
	private String[] listName;
	private String[] listVal;
	private boolean isFirst;
	private final byte FROM_LEFT_SIDE = 20;
	private final byte FROM_HEAD = 21;
	private final byte FROM_NAIL = 22;
	private final byte RESET_VAL = 23;
	private final byte FROM_DOWN = 24;
	private byte focusFrom = FROM_LEFT_SIDE;

	private static final String TAG="AboutActivity";

	public AboutActivity(Context paramContext) {
		super(paramContext);
	}

	private String getWifiMac() {
		Logger.i(TAG,"getWifiMac()--");
		String strWifiMac = this.wifiManager.getMac();
		if ((strWifiMac == null) || (strWifiMac.equals("")))
			return this.context.getText(R.string.about_wifi_mac_disable).toString();
		return strWifiMac.toUpperCase();
	}
	
	@Override
	public void onInvisible() {};

	public void initData() {
		Logger.i(TAG,"initData()--");
		
		//鍒濆鍖栬鍥俱�
		listView = (ListView)findViewById(R.id.lvAboutWindow);
		tvHead = (TextView)findViewById(R.id.tvTitle);
		btn_bottom_focus = (Button)findViewById(R.id.bt_bottom_device_focus);

		//鍒濆鍖栨暟瀛楀�銆�
		Resources rcs = getResources();
		listName = new String[]{
				rcs.getString(R.string.about_device_model),			//璁惧鍨嬪彿
				rcs.getString(R.string.about_software_versions),		//杞欢鐗堟湰
				rcs.getString(R.string.cpu),					//CPU
				rcs.getString(R.string.gpu),					//GPU
				rcs.getString(R.string.sound_config),			//闊冲搷閰嶇疆
				rcs.getString(R.string.network_connections),	//缃戠粶杩炴帴
				rcs.getString(R.string.internal_storage),		//鍐呭瓨
				rcs.getString(R.string.storage_space),			//瀛樺偍绌洪棿
				//rcs.getString(R.string.the_id_of_the_joy_net),	//娆㈢綉璐﹀彿
				//rcs.getString(R.string.device_id),				//璁惧ID
				//rcs.getString(R.string.equipment_type),			//璁惧绫诲瀷
				rcs.getString(R.string.about_mac),				//MAC鍦板潃
				rcs.getString(R.string.about_wifi_mac),			//WLAN MAC
				rcs.getString(R.string.about_sequence),			//搴忓垪鍙�
				rcs.getString(R.string.the_time_of_compile)		//缂栬瘧鏃堕棿
		};
		//璇诲彇鍊笺�
		refreshListVal();
		//鍒涘缓骞堕厤缃�閰嶅櫒銆�
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
		HashMap<String, Object> map;
		for(byte idx = 0; idx < listName.length; idx++){
			map = new HashMap<String, Object>();
			map.put("name", listName[idx]);
			map.put("val", listVal[idx]);
			list.add(map);
		}
		mSimpleAdaper = new SimpleAdapter(getContext(), list,
				R.layout.about_contents_layout,
				new String[]{
						"name",
						"val"
				},
				new int[]{
						R.id.tvName,
						R.id.tvVal
				});
		listView.setAdapter(mSimpleAdaper);
		listView.setOnKeyListener(this);
		isFirst = true;
		initFocusListener();
		listView.setOnFocusChangeListener(focusListener);
		btn_bottom_focus.setOnFocusChangeListener(focusListener);
		tvHead.setOnFocusChangeListener(focusListener);
		
	}// initData  --  end.
	
	private void initFocusListener() {
		focusListener = new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				switch (arg0.getId()) {
				case R.id.lvAboutWindow:
					Log.i("yeah" , "about---lvAboutWindow---arg1="+arg1);
					if (arg1) {
						listView.setSelector(R.drawable.setitem_focus);
						if(focusFrom == FROM_HEAD){
							listView.setSelection(listName.length-1);
						}else if(FROM_NAIL == focusFrom){
							listView.setSelection(0);
						}else {
							listView.setSelection(0);
						}
						
						focusFrom = RESET_VAL;
					}else {
						listView.setSelector(R.drawable.tui_numberpicker_trans);
					}
					break;
				case R.id.tvTitle:
					Log.i("yeah" , "about---tvTitle---arg1="+arg1);
					if(focusFrom == RESET_VAL)
						focusFrom = FROM_HEAD;
						listView.requestFocus();
					break;
				case R.id.bt_bottom_device_focus:
					Log.i("yeah" , "about---bt_bottom_device_focus---arg1="+arg1);
					focusFrom = FROM_NAIL;
					listView.requestFocus();
					break;

				default:
					break;
				}
			}
		};
	}

	/**
	 * 鍒锋柊璁惧淇℃伅銆�
	 * */
	private void refreshListVal(){
		Resources rcs = getResources();
		HuanClientAuth huanClientAuth = new HuanClientAuth(this.context);
		listVal = new String[]{
				SystemProperties.get("ro.product.model", ""),				//璁惧鍨嬪彿
				SystemProperties.get("ro.build.version.incremental", ""),	//杞欢鐗堟湰
				rcs.getString(R.string.CPUType),							//CPU
				rcs.getString(R.string.GPUType),							//GPU
				rcs.getString(R.string.sound),								//闊冲搷閰嶇疆
				rcs.getString(R.string.network),							//缃戠粶杩炴帴
				rcs.getString(R.string.samsung_ddr3_1g),					//鍐呭瓨
				rcs.getString(R.string.toshiba_emmc_8g),					//瀛樺偍绌洪棿
				//huanClientAuth.getHuanid(),									//娆㈢綉甯愬彿
				//huanClientAuth.getDeviceid(),								//璁惧ID
				//huanClientAuth.getDevicemode(),								//璁惧绫诲瀷
				getStrMac(SystemProperties.get("ro.serialno", "")),			//MAC
				getWifiMac(),												//WIFI MAC
				getDevSerNo(SystemProperties.get("ro.serialno", "")),		//搴忓垪鍙�
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.format(new Date(Build.TIME)),			//缂栬瘧鏃堕棿
		};
	}
	
	/**
	 * 鑾峰彇璁惧搴忓垪鍙枫�
	 * */
	private String getDevSerNo(String ser){
		String result;
		try{
			result = ser.substring(0, 32);
		}catch(IndexOutOfBoundsException e){
			Log.w(TAG, "Your serial number is too short,Is it can't read the correct no?");
			result = ser;
		}
		return result;
	}
	
	public void onResume() {
		Logger.i(TAG,"onResume()--");
		refreshListVal();
		mSimpleAdaper.notifyDataSetChanged();
	}

	public void setId() {
		Logger.i(TAG,"setId()--");
		this.frameId = 4;
		this.levelId = 1001;
		this.wifiManager = UMWifiManager.getInstance(this.context);
	}

	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.about, this);
	}
	
	//background_divider
	private String getStrMac(String str){
		String StrMac;
		if(str.length()>= 32){
			str= str.substring(0, str.length());
			str= str.substring(str.length()-12, str.length());
			StrMac= str.substring(0, 2)+":"
			+str.substring(2, 4)+":"
			+str.substring(4, 6)+":"
			+str.substring(6, 8)+":"
			+str.substring(8, 10)+":"
			+str.substring(10, 12);
			return StrMac;
		}
		return "";
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		Log.d(TAG, "listView onKey,keyCode="+keyCode
				+" is key down?"+(event.getAction()==KeyEvent.ACTION_DOWN));
		if(event.getAction() == KeyEvent.ACTION_DOWN){
//			if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
//				Log.d(TAG, "up key.");
//				listView.setSelection(0);
//				return true;
//			}else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
//				Log.d(TAG, "down key.");
//				listView.setSelection(listName.length-1);
//				return true;
//			}
		}
		return false;
	}
}
