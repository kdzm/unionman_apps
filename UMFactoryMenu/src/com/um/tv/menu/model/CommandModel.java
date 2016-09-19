package com.um.tv.menu.model;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusSourceManager;
import com.hisilicon.android.tvapi.impl.CusFactoryImpl;
import com.hisilicon.android.tvapi.impl.CusSourceManagerImpl;
import com.hisilicon.android.tvapi.listener.OnFactoryListener;
import com.hisilicon.android.tvapi.listener.TVMessage;
import com.hisilicon.android.tvapi.constant.EnumFactoryAdcstat;
import com.um.tv.menu.R;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.PQNumInfo;
import com.um.tv.menu.utils.PQNumParser;
import com.um.tv.menu.utils.SocketClient;
import com.um.tv.menu.utils.Utils;

import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import java.lang.reflect.Method;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint({ "HandlerLeak", "UseSparseArrays" })
public class CommandModel extends FunctionModel {
    private static final String TAG = "CommandModel";
    public static final int TypeSetADC = 0;
    public static final int TypeWatchdog = 1;
    public static final int TypeRestore = 2;
    public static final int TypeUartEnable = 3;
    public static final int TypeUartDebug = 4;
    public static final int TypePQUpdate = 5;
    public static final int TypeUpdateDate = 6;
    public static final int TypeUpdateMain = 7;
    public static final int TypeSWVersion = 8;
    public static final int TypeBoard = 9;
    public static final int TypePanel = 10;
    public static final int TypeMainPQVersion = 11;
    public static final int TypeSubPQVersion = 12;
    public static final int TypeDate = 13;
    public static final int TypeTime = 14;
    public static final int TypeWhiteBalanceSource = 15;
    public static final int TypeUSBUpdateMac = 16;
	public static final int TypePqUpdate = 17;
	public static final int TypeAqUpdate = 18;
	public static final int TypeLogoUpdate = 19;
	public static final int TypeMachineTypeUpdate = 20;
	public static final int TypeScreenParamUpdate = 21;
	public static final int TypeSalePicUpdate = 22;
	public static final int TypeResetEeprom = 23;
	public static final int TypeUSBUpdateCustom = 24;
	public static final int TypeUpdatePQNum = 25;
	
    private PQNumParser parser;  
    private List<PQNumInfo> pqNumInfos; 
	
	
    private int mType = 0;

    private TextView mCommandText = null;
    private Context mContext;
    private List<CommandChangeListener> mCommandListenerList = new ArrayList<CommandModel.CommandChangeListener>();

    public CommandModel(Context context, FactoryWindow window, CusFactory factory, int type) {
        super(context, window, factory);
        // TODO Auto-generated constructor stub
        mContext = context;
        mType = type;
    }

    @Override
    public View getView(Context context, int position, View convertView,
            ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null || !convertView.getTag().equals(ViewTagCommand)) {
            convertView = LayoutInflater.from(context).inflate(R.layout.command_layout, null);
            convertView.setTag(ViewTagCommand);
        }
        TextView tvName = (TextView)convertView.findViewById(R.id.tv_name);
        tvName.setText(mName);
        mCommandText = (TextView)convertView.findViewById(R.id.tv_command);
        int cmd = 0;
        Log.d(TAG, "mType "+mType);
        switch (mType) {
            case TypeSetADC:
                cmd = R.string.command_set_adc_tune;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeWatchdog:
                cmd = R.string.command_open_watch_dog;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeRestore:
                cmd = R.string.command_restore_to_default;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeUartEnable:
                cmd = R.string.command_enable_uart;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeUartDebug:
                cmd = R.string.command_enable_uart_debug;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypePQUpdate:
                cmd = R.string.command_update_pq;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeUpdateDate:
                cmd = R.string.command_update_date;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeUpdateMain:
                cmd = R.string.command_update_main;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeSWVersion:
                cmd = R.string.command_sw_version;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeBoard:
                cmd = R.string.command_board;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypePanel:
                cmd = R.string.command_panel;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeMainPQVersion:
                cmd = R.string.command_main_pq_version;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeSubPQVersion:
                cmd = R.string.command_sub_pq_version;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeDate:
                mCommandText.setText(getCurrentDate());
                mHandler.removeMessages(MsgUpdateDate);
                mHandler.sendEmptyMessage(MsgUpdateDate);
                break;
            case TypeTime:
                mCommandText.setText(getCurrentTime());
                mHandler.removeMessages(MsgUpdateTime);
                mHandler.sendEmptyMessage(MsgUpdateTime);
                break;
            case TypeWhiteBalanceSource:
                CusSourceManager sm = CusSourceManagerImpl.getInstance();
                mCommandText.setText(Utils.SourceName[sm.getCurSourceId(0)]);
                break;
            case TypeUSBUpdateMac:
                cmd = R.string.command_usb_update_mac;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeUSBUpdateCustom:
                cmd = R.string.command_usb_update_mac;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeUpdatePQNum:
                cmd = R.string.command_usb_update_mac;
                mCommandText.setText(context.getString(cmd));
                break;    
                
			case TypeResetEeprom:
                cmd = R.string.dlg_title_erase_eeprom;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypePqUpdate:
            	Log.d(TAG, "TypePqUpdate");
                cmd = R.string.command_usb_update_mac;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeAqUpdate:
            	Log.d(TAG, "TypeAqUpdate");
                cmd = R.string.command_usb_update_mac;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeLogoUpdate:
                cmd = R.string.command_usb_update_mac;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeMachineTypeUpdate:
                cmd = R.string.command_usb_update_mac;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeScreenParamUpdate:
                cmd = R.string.command_usb_update_mac;
                mCommandText.setText(context.getString(cmd));
                break;
            case TypeSalePicUpdate:
                cmd = R.string.command_usb_update_mac;
                mCommandText.setText(context.getString(cmd));
                break;
         default:
                break;
        }

        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        // TODO Auto-generated method stub
        switch (mType) {
            case TypeSetADC:
                mCommandText.setText(mContext
                        .getString(R.string.command_setting_adc_tune));
                UmtvManager.getInstance().registerListener(TVMessage.HI_TV_EVT_ADC_ADJ_STATUS, mFactoryListener);
                mFactory.autoCalibration();
                break;
            case TypeRestore:
                showResetCofirmDialog();
                break;
			case TypeResetEeprom:
                showResetEepromCofirmDialog();
                break;
	        case TypeUSBUpdateMac:
	          //usbUpdateMac();
	        	 updateDeviceInfo();
	          break;
	        case TypeUSBUpdateCustom:
			    String path = getExtendStroagePath();
	        	updateCustomInfo(path);
		        break;
	        case TypeUpdatePQNum:	
	        	updatePQNum();
		       break;
            default:
                break;
        }
    }

    private OnFactoryListener mFactoryListener = new OnFactoryListener() {

        @Override
        public void onAutoCalibrationAdjustStatus(int status) {
            // TODO Auto-generated method stub
            String statusStr = "";
            switch(status){
                case EnumFactoryAdcstat.ADCSTAT_BEGIN:
                    break;
                case EnumFactoryAdcstat.ADCSTAT_FINISH:
                    statusStr = mContext.getString(R.string.command_setted_adc_tune_successfully);
                    mCommandText.setText(statusStr);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(
                            MsgUpdateCommand, R.string.command_set_adc_tune, 0, null),
                            MilliDelay);
                    for(CommandChangeListener listener : mCommandListenerList){
                        listener.onStatusChanged(EnumFactoryAdcstat.ADCSTAT_FINISH);
                    }
                    break;
                case EnumFactoryAdcstat.ADCSTAT_TIMEOUT:
                    statusStr = mContext.getString(R.string.command_setted_adc_tune_failed);
                    mCommandText.setText(statusStr);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(
                            MsgUpdateCommand, R.string.command_set_adc_tune, 0, null),
                            MilliDelay);
                    break;
                case EnumFactoryAdcstat.ADCSTAT_FAIL:
                    statusStr = mContext.getString(R.string.command_setted_adc_tune_failed);
                    mCommandText.setText(statusStr);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(
                            MsgUpdateCommand, R.string.command_set_adc_tune, 0, null),
                            MilliDelay);
                    break;
                default:
                    break;
            }
        }
        //@Override
        public void onAutoCalibrationColorTempStatus(int status) {
            // TODO Auto-generated method stub
        }

    };


    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub

    }

    public void registerStatusChangeListener(CommandChangeListener listener) {
        mCommandListenerList.add(listener);
    }

    public void unregisterStatusChangeListener(CommandChangeListener listener) {
        mCommandListenerList.remove(listener);
    }

    public interface CommandChangeListener {
        void onStatusChanged(int status);
    }

    private void notifyStatusChanged(CommandChangeListener listener, int status) {
        int id = status;
        switch (mType) {
            case TypeSetADC:
                listener.onStatusChanged(id);
                break;
            default:
                break;
        }
    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        return mContext.getString(R.string.command_date, "" + year,
                (month < 10 ? "0" + month : "" + month), (day < 10 ? "0" + day
                    : "" + day));
    }

    private String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        return mContext.getString(R.string.command_time, (hour < 10 ? "0"
                    + hour : "" + hour),
                (minute < 10 ? "0" + minute : "" + minute), (second < 10 ? "0"
                    + second : "" + second));
    }

    private static final int MilliDelay = 3000;
    private static final int MsgUpdateCommand = 10001;
    private static final int MsgUpdateDate = 10002;
    private static final int MsgUpdateTime = 10003;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage--->" + msg.what);
            switch (msg.what) {
                case MsgUpdateCommand:
                    mCommandText.setText(mContext.getResources().getString(msg.arg1));
                    break;
                case MsgUpdateDate:
                    mCommandText.setText(getCurrentDate());
                    mHandler.sendEmptyMessageDelayed(MsgUpdateDate, 500);
                    break;
                case MsgUpdateTime:
                    mCommandText.setText(getCurrentTime());
                    mHandler.sendEmptyMessageDelayed(MsgUpdateTime, 500);
                    break;
                default:
                    break;
            }
        };
    };

    private void showResetCofirmDialog(){
        Dialog dlg = new AlertDialog.Builder(mContext)
            .setTitle(mContext.getString(R.string.dlg_title_reset))
            .setMessage(mContext.getString(R.string.dlg_message_reset))
            .setPositiveButton(mContext.getString(R.string.btn_submit), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    CusFactoryImpl.getInstance().resetNVM();
                }
            })
        .setNegativeButton(mContext.getString(R.string.btn_cancel), null).create();
        dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dlg.show();
    }

	    private void showResetEepromCofirmDialog(){
        Dialog dlg = new AlertDialog.Builder(mContext)
            .setTitle(mContext.getString(R.string.dlg_title_erase_eeprom))
            .setMessage(mContext.getString(R.string.dlg_message_erase_eeprom))
            .setPositiveButton(mContext.getString(R.string.btn_submit), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    mFactory.setUmDeviceInfo(0, "reset Eeprom");
                }
            })
        .setNegativeButton(mContext.getString(R.string.btn_cancel), null).create();
        dlg.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dlg.show();
    }

    private void usbUpdateMac(){
         int macIndex = 0;
         int totalMac;
         String sMacaddr = null;
         try
         {
             File indexFile=new File("/mnt/sda/sda1/MacAddressData/MacAddressDataIndex.txt");
             if(indexFile.isFile() && indexFile.exists()){
                 FileReader read = new FileReader(indexFile);
                 BufferedReader bufferedReader = new BufferedReader(read);
                 String sIndex = null;
                 sIndex = bufferedReader.readLine();
           if(sIndex!=null)
                      macIndex = Integer.valueOf(sIndex).intValue();
           else
               macIndex = 0;
              Log.d("Joe test get index", ""+macIndex);
                 read.close();
             }else{
                Toast.makeText(mContext, "open MacAddressDataIndex.txt fail!",
                        Toast.LENGTH_LONG).show();
          return;
             }
         } catch (Exception e) {
                Toast.makeText(mContext, "read MacAddressDataIndex.txt fail!",
                        Toast.LENGTH_LONG).show();
             e.printStackTrace();
          return;
         }

         try
         {
             File macFile=new File("/mnt/sda/sda1/MacAddressData/MacAddressData.txt");
             if(macFile.isFile() && macFile.exists()){
          FileReader in = new FileReader(macFile);
          LineNumberReader reader = new LineNumberReader(in);

          int line = 0;
          while(true)
          {
              sMacaddr = reader.readLine();

              if(line == macIndex)
              {
                    mFactory.setMacAddress(sMacaddr);
                     Log.d("Joe test set mac", ""+sMacaddr);
                    break;
             /*if(mFactory.setMacAddress(sMacaddr)==0)
                   {
                          Log.d("Joe test set mac", ""+sMacaddr);
                          break;
                   }
             else
                   {
                       Toast.makeText(mContext, "set mac addr fail!",
                                      Toast.LENGTH_LONG).show();
                    return;
                    }*/
              }
                 line ++;
          }
             in.close();
             }else{
                Toast.makeText(mContext, "open MacAddressData.txt fail!",
                        Toast.LENGTH_LONG).show();
          return;
             }
         } catch (Exception e) {
                Toast.makeText(mContext, "read MacAddressData.txt fail!",
                        Toast.LENGTH_LONG).show();
             e.printStackTrace();
          return;
         }
         macIndex++;
         try
         {
             File indexFile=new File("/mnt/sda/sda1/MacAddressData/MacAddressDataIndex.txt");
             if(indexFile.isFile() && indexFile.exists()){
                 FileWriter read = new FileWriter(indexFile);
                 BufferedWriter bufferedWriter = new BufferedWriter(read);
                 String sIndex = String.valueOf(macIndex);
                 bufferedWriter.write(sIndex);
                 bufferedWriter.flush();
                 read.close();
             }else{
                Toast.makeText(mContext, "open MacAddressDataIndex.txt fail!",
                        Toast.LENGTH_LONG).show();
                return;
             }
         } catch (Exception e) {
                Toast.makeText(mContext, "write MacAddressDataIndex.txt fail!",
                        Toast.LENGTH_LONG).show();
             e.printStackTrace();
                return;
         }
         try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         if(sMacaddr!=null)
        {
            //if(sMacaddr.equals(mFactory.getMacAddress()))
                Toast.makeText(mContext, "upgrade mac addr success!",
                        Toast.LENGTH_LONG).show();
            //else
                //Toast.makeText(mContext, "upgrade mac addr fail!",
                       // Toast.LENGTH_LONG).show();
         }
         else
         Toast.makeText(mContext, "upgrade mac addr fail!",
                        Toast.LENGTH_LONG).show();
    }
    
	private String getDevInfoFileMountPoint() {
		try {
			IBinder service = ServiceManager.getService("mount");
			if (service != null) {
				IMountService mountService = IMountService.Stub
						.asInterface(service);
				List<android.os.storage.ExtraInfo> mountList = mountService
						.getAllExtraInfos();
				int deviceCount = mountList.size();
				for (int i = 0; i < deviceCount; i++) {
					String mountPoint = mountList.get(i).mMountPoint;
                    Log.v(TAG, "mounted point: " + mountPoint);
                    File file = new File(mountPoint+"/usbburninfo/device_infos.xml");
                    if (file.exists() && file.isFile()) {
                    	Log.v(TAG, "find xml file.");
                    	return mountPoint;
                    }
				}
			}
		} catch (RemoteException e) {
            e.printStackTrace();
            return null;
		}

		return null;
	}
	
	void updateDeviceInfo() {
    	Map<Integer, String> map = null;
    	String mountPoint = getDevInfoFileMountPoint();
    	if (mountPoint == null) {
    		Toast.makeText(mContext, "ERROR: device_infos.xml not found.", 
					Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	try {
	    	InputStream xml = new FileInputStream(new File(mountPoint+"/usbburninfo/device_infos.xml"));
	    	
	    	XmlPullParser pullParser = Xml.newPullParser();
	        pullParser.setInput(xml, "UTF-8"); //涓篜ull瑙ｉ噴鍣ㄨ缃瑙ｆ瀽鐨刋ML鏁版嵁        
	        int event = pullParser.getEventType();
	        
	        while (event != XmlPullParser.END_DOCUMENT) {
	            
	            switch (event) {
	            
	            case XmlPullParser.START_DOCUMENT:
	                break;    
	            case XmlPullParser.START_TAG:   
	            	String name = pullParser.getName();
	            	int devinfoId = -1;
	            	if ("device_info".equals(name)) {
	            		map = new HashMap<Integer, String>();
	            	} else if ("serial_no".equals(name)){
	            		devinfoId = 1;
	                } else if ("hardware_version".equals(name)){
	                	devinfoId = 2;
	                } else if ("device_id".equals(name)){
	                	devinfoId = 3;
	                } else if ("mac".equals(name)) {
	                	devinfoId = 4;
	                } else if ("panel_index".equals(name)) {
	                	devinfoId = 5;
	                } else if ("client_type".equals(name)) {
	                	devinfoId = 6;
	                } else if ("hdmi_hdcp_key_file".equals(name)) {
	                	devinfoId = 7;
	                } else if ("miracast_hdcp_key_file".equals(name)) {
	                	devinfoId = 8;
	                } else if ("project_id".equals(name)) {
	                	devinfoId = 10;
	                }
	            	
	            	if (map != null && devinfoId != -1) {
                		event = pullParser.next();
                		String value = pullParser.getText();
                		if (devinfoId == 7 || devinfoId ==8) {
                			if (!value.startsWith("/")) {
                				value = mountPoint+"/usbburninfo/" + value;
                			}
                		}
                		map.put(devinfoId, value);
                	}
	                break;
	                
	            case XmlPullParser.END_TAG:
	                break;
	            }
	            
	            event = pullParser.next();
	        }
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    		Toast.makeText(mContext, "ERROR: device_infos.xml not found.", 
					Toast.LENGTH_LONG).show();
    		return;
    	} catch (XmlPullParserException e) {
    		e.printStackTrace();
    		Toast.makeText(mContext, "ERROR: parse device_infos.xml failed.", 
					Toast.LENGTH_LONG).show();
        	return;
    	} catch (IOException e) {
    		e.printStackTrace();
    		Toast.makeText(mContext, "ERROR: read device_infos.xml failed.", 
					Toast.LENGTH_LONG).show();
    		return;
    	}
    	
        if (map == null || map.isEmpty()) {
        	Toast.makeText(mContext, "WARN: no device info need to be updated.", 
					Toast.LENGTH_LONG).show();
        	return;
        }
        
        Iterator<Map.Entry<Integer, String>> it = map.entrySet().iterator();  
        while (it.hasNext()) {
        	Map.Entry<Integer, String> entry = it.next();
        	Log.v(TAG, "set DEVINFO: [" + entry.getKey() + "]: " + entry.getValue());
        	int ret = mFactory.setUmDeviceInfo(entry.getKey(), entry.getValue());
        	if (ret != 0) {
        		Toast.makeText(mContext, "ERROR: update device info failed.", 
    					Toast.LENGTH_LONG).show();
            	return;
        	}
        }
        
        Toast.makeText(mContext, "Update device info success.", 
				Toast.LENGTH_LONG).show();
    }
	
	public void updateCustomInfo(String Strpath) {	
		if (Strpath.equals("")){
			Toast.makeText(mContext, "can not find udisk or the custom directory!", Toast.LENGTH_LONG).show();
			return;
		}
		
		File customFile = new File(Strpath+"/custom"); 
		if (!customFile.exists()){
			Toast.makeText(mContext, "can not find the custom directory!", Toast.LENGTH_LONG).show();
			return;
		}
		
		String oldIniPath = Strpath + "/custom/ini";
		String oldPqPath = Strpath + "/custom/pq";
		String oldAqPath = Strpath + "/custom/aq";
		
		File iniFile = new File(oldIniPath);
		File pqFile = new File(oldPqPath);
		File aqFile = new File(oldAqPath);
		if ((!iniFile.exists()) && (!pqFile.exists()) && (!aqFile.exists())){
			Toast.makeText(mContext, "the custom directory is null!", Toast.LENGTH_LONG).show();
			return;
		}
			
		shellExecute("mount -o remount,rw /custom");
		
		if (!iniFile.exists()){
			Toast.makeText(mContext, "the ini directory is null!", 
					Toast.LENGTH_LONG).show();
		}else{
			File[] subFile = iniFile.listFiles();
			
			if (subFile.length > 0){
				if(copyDirAndFile("cp -rf "+oldIniPath+"/* /atv/ini")){
					Log.d(TAG,"update custom ini info success!");
					Toast.makeText(mContext, "update custom ini info success!", Toast.LENGTH_LONG).show();	
				}else{
					Log.d(TAG,"ERROR: copy ini file failed,update custom info failed");
					Toast.makeText(mContext, "ERROR: copy ini file failed,update custom info failed.", 
						Toast.LENGTH_LONG).show();
				}
			}	
		}
		
		if (!pqFile.exists()){
			Toast.makeText(mContext, "the pq directory is null!", Toast.LENGTH_LONG).show();
		}else{
			File[] subFile = pqFile.listFiles();
			
			if (subFile.length > 0){
				if (copyDirAndFile("cp -rf "+oldPqPath+"/* /atv/pq")){
					Log.d(TAG,"update custom pq info success!");
					Toast.makeText(mContext, "update custom pq info success!", Toast.LENGTH_LONG).show();
					try { 
						 Thread.sleep(2000); 
					}catch (InterruptedException e) { 
						e.printStackTrace(); 
					}
				}else{
					Log.d(TAG,"ERROR: copy pq file failed,update custom info failed.");
					Toast.makeText(mContext, "ERROR: copy pq file failed,update custom info failed.", 
							Toast.LENGTH_LONG).show();
				}
			}			
		}
		
		if (!aqFile.exists()){
			Toast.makeText(mContext, "the aq directory is null!", Toast.LENGTH_LONG).show();
		}else{
			File[] subFile = aqFile.listFiles();
			
			if (subFile.length > 0){
				if (copyDirAndFile("cp -rf "+oldAqPath+"/* /atv/aq")){
					Log.d(TAG,"update custom aq info success!");
					Toast.makeText(mContext, "update custom aq info success!", Toast.LENGTH_LONG).show();
					try { 
						 Thread.sleep(2000); 
					}catch (InterruptedException e) { 
						e.printStackTrace(); 
					}
				}else{
					Log.d(TAG,"ERROR: copy aq file failed,update custom info failed.");
					Toast.makeText(mContext, "ERROR: copy aq file failed,update custom info failed.", 
							Toast.LENGTH_LONG).show();
				}
			}			
		}
		
		shellExecute("mount -o remount,ro /custom");
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
			  //杩欓噷鏄�S鍚庢墽琛岀殑浠ｇ爜
			  shellExecute("reboot");
			}
		}, 2 * 1000);
	}
	
	private String getExtendStroagePath(){
		String[] paths = getStoragePaths(mContext);
		String udiskPath = "";
		int i = 0;
		for (String path : paths){
			Log.d(TAG,"getStoragePaths path:"+path);
			File customFile = new File(path+"/custom"); 
			if (customFile.exists()){
				udiskPath = path;
				Log.d(TAG,"getStoragePaths find the udisk custom path:"+udiskPath);			
				break;
			}
		}
		
		return udiskPath;
	}
	
	private  boolean copyDirAndFile(String path) {
		return shellExecute(path);
	}
	
	private  boolean shellExecute(String cmd) {
		SocketClient socketClietn = new SocketClient(); 
		socketClietn.writeMess("system " + cmd);
		int  result = socketClietn.readNetResponseSync();
		if (result == -1) {
			Log.e(TAG, "shellExecute("+cmd+") failed.");
		}
		
		return (result == 0?true:false);
	}
	
	private String[] getStoragePaths(Context cxt) {
        List<String> pathsList = new ArrayList<String>();
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.GINGERBREAD) {
            StringBuilder sb = new StringBuilder();
            File externalFolder = Environment.getExternalStorageDirectory();
            if (externalFolder != null) {
                pathsList.add(externalFolder.getAbsolutePath());
            }           
        } else {
            StorageManager storageManager = (StorageManager) cxt.getSystemService(Context.STORAGE_SERVICE);
            try {
                Method method = StorageManager.class.getDeclaredMethod("getVolumePaths");
                method.setAccessible(true);
                Object result = method.invoke(storageManager);
                if (result != null && result instanceof String[]) {
                    String[] pathes = (String[]) result;
                    StatFs statFs;
                    for (String path : pathes) {
                        if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                            statFs = new StatFs(path);
                            if (statFs.getBlockCount() * statFs.getBlockSize() != 0) {
                                pathsList.add(path);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                File externalFolder = Environment.getExternalStorageDirectory();
                if (externalFolder != null) {
                    pathsList.add(externalFolder.getAbsolutePath());
                }
            }
        }
        
        return pathsList.toArray(new String[pathsList.size()]);
    }
	
	public void updatePQNum() {
		try { 
		File f = new File("/custom/atv/custom_config.xml");
		InputStream is 	=new FileInputStream(f);
  		//InputStream is = this.mContext.getResources().getAssets().open("custom_config.xml");  
  		parser = new PQNumParser();    		     
  	    pqNumInfos = parser.parse(is);  	        
  	    
  	    String tmp = "1";
  	  CusFactory mFactory = UmtvManager.getInstance().getFactory();
  	   // mFactory.setUmDeviceInfo(Factory.UMDEVICE_INFO_ID_PROJECT_ID,"049130101");
		 tmp = mFactory.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_PROJECT_ID);
		 
		 int currindex=0;
		 Log.i("pi","==getpi======"+tmp);

		 for(int i=0;i<pqNumInfos.size();i++){
			 Log.i("pi","==infogetpi======"+pqNumInfos.get(i).getProject_id());
			 if(tmp.equals(pqNumInfos.get(i).getProject_id())){
				 currindex=i;
			 }
		 }
		
		
		 ArrayList<Integer>  list_img=new ArrayList<Integer>();
		 for(int j=0;j<pqNumInfos.size();j++){
			 list_img.add(R.color.transparent);
		 }
		Log.i("pi","=====currindex==="+currindex);

  	   /* int [] dialog_item_img = new int[]{
  	    		R.color.transparent,
  	    		R.color.transparent,
  	    		R.color.transparent,
  	    		R.color.transparent
  	    };*/
  	    //dialog_item_img[currindex]=R.drawable.net_select;
		list_img.set(currindex, R.drawable.net_select);
		
	     AlertDialog.Builder  malertdialog =new AlertDialog.Builder(mContext,R.style.Dialog_undim);
	     malertdialog.setTitle(R.string .pq_ap_version);
	      LayoutInflater factory = LayoutInflater.from(mContext);
	      View myView = factory.inflate(R.layout.pq_layout,null);
	     final ListView lisview =(ListView) myView.findViewById(R.id.setting_list);
	     final ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>();
	     listDialog.clear();	      	           		
		for (int i=0; i<pqNumInfos.size(); i++) {
			HashMap<String, Object> map =  new HashMap<String, Object>(); 
  		map.put("ItemContext", pqNumInfos.get(i).getId()+"   "+pqNumInfos.get(i).getDescribe()+"   "
			    +pqNumInfos.get(i).getProject_id()+"   "+pqNumInfos.get(i).getPanelindex()); 
  		map.put("ItemImg", list_img.get(i)); 		
  		listDialog.add(map);
  	}
		 final SimpleAdapter mSimpleAdapter = new SimpleAdapter(mContext, listDialog, R.layout.pq_item_dialog, 
                new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
		
      lisview.setAdapter(mSimpleAdapter);
		  lisview.setSelection(currindex);
     lisview.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {	
			pqModeItemClick(listDialog,mSimpleAdapter,position);
			 CusFactory mFactorys = UmtvManager.getInstance().getFactory();
			 
		  	 mFactorys.setUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_PROJECT_ID,pqNumInfos.get(position).getProject_id());
		  	 mFactorys.setUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_PANEL_INDEX,pqNumInfos.get(position).getPanelindex());
		 	Log.i("pi","==clickgetpi======"+mFactorys.getUmDeviceInfo(CusFactory.UMDEVICE_INFO_ID_PROJECT_ID));
		  	 
			Log.i("pq","-------"+pqNumInfos.get(position).getId()+ "======="+pqNumInfos.get(position).getProject_id()+"---"+pqNumInfos.get(position).getPanelindex());

		}
	});
     malertdialog.setView(myView);
     AlertDialog mAlertDialog = malertdialog.create();
     mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
     mAlertDialog.show();
	
			
  	    } catch (Exception e) {  
  	        Log.e(TAG, e.getMessage());  
  	    }  
	}
	
	private void pqModeItemClick(
			final ArrayList<HashMap<String, Object>> listDialog,
			final SimpleAdapter mSimpleAdapter, int position) {
	
	    ArrayList<Integer>  list_img=new ArrayList<Integer>();
		 for(int j=0;j<pqNumInfos.size();j++){
			 list_img.add(R.color.transparent);
		 }
		list_img.set(position, R.drawable.net_select);
    	 listDialog.clear();
 		for (int i=0; i<list_img.size(); i++) {
 			HashMap<String, Object> map =  new HashMap<String, Object>(); 
    		map.put("ItemContext", pqNumInfos.get(i).getId()+"   "+pqNumInfos.get(i).getDescribe()+"   "
    			    +pqNumInfos.get(i).getProject_id()+"   "+pqNumInfos.get(i).getPanelindex()); 
    		map.put("ItemImg", list_img.get(i)); 		
    		listDialog.add(map);
    	}
 		mSimpleAdapter.notifyDataSetChanged();
	}
	
}
