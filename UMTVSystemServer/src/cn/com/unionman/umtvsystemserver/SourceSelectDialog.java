package cn.com.unionman.umtvsystemserver;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.CountDownTimer;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;

public class SourceSelectDialog extends Dialog{
	private static String TAG = "SourceSelectDialog";
	private static final String UM_CLOSE_SYSTEM_DIALOG_ACTION = "cn.com.unionman.close.systemdialog.action";
    public static final String INTENT_ATV = "android.intent.umaction.UMATV";
    public static final String INTENT_DTV = "com.unionman.intent.ACTION_PLAY_DVB";
    public static final String INTENT_PORT = "android.intent.PortPlayer";
	private Context mContext;
	private int timeOut;
	private TextView mTime, mWarn;
	private CountDownTimer mTimer;
	private Button OKBtn;
	private Button CancelBtn;
	private Timer mDismissTimer;
	private TimerTask mDismissTimerTask;
	private int mSourceIndex = 0;
	
	public SourceSelectDialog(Context context, int time, int sourceIndex) {
		super(context, R.style.Translucent_NoTitle);
		timeOut = time;
		mContext = context;
		mSourceIndex = sourceIndex;
		setContentView(R.layout.sourceselect_dialog_layout);
		mTime = (TextView) findViewById(R.id.timeout_txt);
		mWarn = (TextView) findViewById(R.id.source_select_warn_txt);
		mTime.setText(10+"s");
		OKBtn = (Button) findViewById(R.id.ok_btn);
		CancelBtn = (Button) findViewById(R.id.cancel_btn);
		CancelBtn.requestFocus();
		
		Log.i("heh", "==SourceSelectDialog=");
		
		int sourceStrid = getSourceStrById(sourceIndex);
		String warnStr = "";
		warnStr = mContext.getString(R.string.source_detect_warn1)
				+mContext.getString(sourceStrid)
				+mContext.getString(R.string.source_detect_warn4)
				+mContext.getString(R.string.source_detect_warn2)
				+mContext.getString(R.string.source_detect_warn3)
				+mContext.getString(sourceStrid)
				+mContext.getString(R.string.source_detect_warn4)
				+mContext.getString(R.string.source_detect_warn5);
		
	
		mWarn.setText(warnStr);
		OKBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				doOk();
			}
		});
		CancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				doCancel();
			}
		});
		mTimer =  new CountDownTimer(1000*timeOut, 1000) {//��ʱ�䣬 ���ʱ��
        	 
            public void onTick(long millisUntilFinished) {
            	mTime.setText(millisUntilFinished/1000+"s");
            }
 
            public void onFinish() {
            	doCancel();
            }
        }; 
        mTimer.start();
	}
	
	private boolean isTFProduct(){
		String tmp = "";
		boolean isTF = false;
		
		tmp = SystemProperties.get("ro.umtv.sw.version");
		tmp = tmp.substring(11,13);
		if (tmp.contains("TF")){
			isTF = true;
		}else{
			isTF = false;
		}
		
		return isTF;
	}
	
	private int getSourceStrById(int sourceIndex){
		int strid = 0;
		ArrayList<Integer> mAllSourceList = new ArrayList<Integer>();
		ArrayList<Integer> mCVBSList = new ArrayList<Integer>();
		ArrayList<Integer> mYPBRList = new ArrayList<Integer>();
		ArrayList<Integer> mHDMIList = new ArrayList<Integer>();
        //获取所有端口，端口不能超过10个
		
        mAllSourceList = SourceManagerInterface.getSourceList(); 
		 for(int i=0;i<mAllSourceList.size();i++){
	    	   if(mAllSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS1||
	    		  mAllSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS2||
	    		  mAllSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS3){
	    		   mCVBSList.add(mAllSourceList.get(i));
	    		   
	    	   }else if(mAllSourceList.get(i)==EnumSourceIndex.SOURCE_YPBPR1||
	    			   mAllSourceList.get(i)==EnumSourceIndex.SOURCE_YPBPR2){
	    		   mYPBRList.add(mAllSourceList.get(i));
	    	   }else if(mAllSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI1||
	    			    mAllSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI2||
	    			    mAllSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI3||
	    			    mAllSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI4){
	    		   mHDMIList.add(mAllSourceList.get(i));
	    	   }
	       }
		 
		 Log.i("heh", "==mCVBSList="+mCVBSList.size());
		 Log.i("heh", "==mYPBRList="+mYPBRList.size());
		 Log.i("heh", "==mHDMIList="+mHDMIList.size());
		switch (sourceIndex){
			case EnumSourceIndex.SOURCE_CVBS1:
				
				if(mCVBSList.size()==1){
					strid = R.string.CVBS;
				}else{
					int index=mCVBSList.indexOf(EnumSourceIndex.SOURCE_CVBS1);
					if(index==0){
						strid = R.string.CVBS1;
					}else if(index==1){
						strid = R.string.CVBS2;
					}else if(index==2){
						strid = R.string.CVBS3;
					}
				}
				
				break;
			case EnumSourceIndex.SOURCE_CVBS2:
				
				if(mCVBSList.size()==1){
					strid = R.string.CVBS;
				}else{
					int indexcvbs2=mCVBSList.indexOf(EnumSourceIndex.SOURCE_CVBS2);
					if(indexcvbs2==0){
						strid = R.string.CVBS1;
					}else if(indexcvbs2==1){
						strid = R.string.CVBS2;
					}else if(indexcvbs2==2){
						strid = R.string.CVBS3;
					}
				}
				
				break;
			case EnumSourceIndex.SOURCE_CVBS3:
				
				if(mCVBSList.size()==1){
					strid = R.string.CVBS;
				}else{
					int indexcvbs3=mCVBSList.indexOf(EnumSourceIndex.SOURCE_CVBS3);
					if(indexcvbs3==0){
						strid = R.string.CVBS1;
					}else if(indexcvbs3==1){
						strid = R.string.CVBS2;
					}else if(indexcvbs3==2){
						strid = R.string.CVBS3;
					}
				}
				
				break;
			case EnumSourceIndex.SOURCE_YPBPR1:
				
					if(mYPBRList.size()==1){
						strid = R.string.YPBPR;
					}else{
						int indexypbpr=mYPBRList.indexOf(EnumSourceIndex.SOURCE_YPBPR1);
						if(indexypbpr==0){
							strid = R.string.YPBPR1;
						}else if(indexypbpr==1){
							strid = R.string.YPBPR2;
						}
					}
				
				break;
				
			case EnumSourceIndex.SOURCE_YPBPR2:

					if(mYPBRList.size()==1){
						strid = R.string.YPBPR;
					}else{
						int indexypbpr1=mYPBRList.indexOf(EnumSourceIndex.SOURCE_YPBPR2);
						if(indexypbpr1==0){
							strid = R.string.YPBPR1;
						}else if(indexypbpr1==1){
							strid = R.string.YPBPR2;
						}
					}
				
				
				break;
			case EnumSourceIndex.SOURCE_HDMI1:
				
				int indexhdmi=mHDMIList.indexOf(EnumSourceIndex.SOURCE_HDMI1);
				if(indexhdmi==0){
					strid = R.string.HDMI1;
				}else if(indexhdmi==1){
					strid = R.string.HDMI2;
				}else if(indexhdmi==2){
					strid = R.string.HDMI3;
				}else if(indexhdmi==3){
					strid = R.string.HDMI4;
				}
				
				break;
			case EnumSourceIndex.SOURCE_HDMI2:
				int indexhdmi2=mHDMIList.indexOf(EnumSourceIndex.SOURCE_HDMI2);
				if(indexhdmi2==0){
					strid = R.string.HDMI1;
				}else if(indexhdmi2==1){
					strid = R.string.HDMI2;
				}else if(indexhdmi2==2){
					strid = R.string.HDMI3;
				}else if(indexhdmi2==3){
					strid = R.string.HDMI4;
				}
				break;
			case EnumSourceIndex.SOURCE_HDMI3:
				int indexhdmi3=mHDMIList.indexOf(EnumSourceIndex.SOURCE_HDMI3);
				if(indexhdmi3==0){
					strid = R.string.HDMI1;
				}else if(indexhdmi3==1){
					strid = R.string.HDMI2;
				}else if(indexhdmi3==2){
					strid = R.string.HDMI3;
				}else if(indexhdmi3==3){
					strid = R.string.HDMI4;
				}
				break;
			case EnumSourceIndex.SOURCE_HDMI4:
				int indexhdmi4=mHDMIList.indexOf(EnumSourceIndex.SOURCE_HDMI4);
				if(indexhdmi4==0){
					strid = R.string.HDMI1;
				}else if(indexhdmi4==1){
					strid = R.string.HDMI2;
				}else if(indexhdmi4==2){
					strid = R.string.HDMI3;
				}else if(indexhdmi4==3){
					strid = R.string.HDMI4;
				}
				break;
			case EnumSourceIndex.SOURCE_VGA:
				strid = R.string.VGA;
				break;
			default:
				strid = R.string.CVBS;
				break;
				
		}
		
		return strid;
	}
	
	private void doOk() {
    	mTimer.cancel();
    	dismiss();
    	doSourceSelect(mSourceIndex);
	}
	
	private void doCancel() {
    	mTimer.cancel();
    	dismiss();
	}
	
	public void myCancel() {
		doCancel();
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_SOURCE){
    		mDismissTimer = new Timer();
    		mDismissTimerTask = new TimerTask() {
				@Override
				public void run() {
					doCancel();
					mDismissTimerTask.cancel();
					mDismissTimer.cancel();
				}
			};
			mDismissTimer.schedule(mDismissTimerTask, 500);
    	}
    	super.onKeyDown(keyCode, event);
    	return false;
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	super.onWindowFocusChanged(hasFocus);
    	
    	if (hasFocus){
    		registerSystemDialogCloseReceiver();
    	}else{
    		unregisterSystemDialogCloseReceiver();
    	}
    }
    
    private BroadcastReceiver systemDialogCloseReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        	Log.e(TAG, "systemDialogCloseReceiver UM_CLOSE_SYSTEM_DIALOG_ACTION");
            String action = intent.getAction();
            if (action.equals(UM_CLOSE_SYSTEM_DIALOG_ACTION)){
            	String reason = intent.getStringExtra("reason");
            	Log.e(TAG, "systemDialogCloseReceiver reason:"+reason);
            	if ((reason != null) && (!reason.equals("SelectSourceDialog"))){
            		doCancel();
            	}
            }
        }
    };
    
    private void registerSystemDialogCloseReceiver(){
    	IntentFilter filter = new IntentFilter(UM_CLOSE_SYSTEM_DIALOG_ACTION);
    	mContext.registerReceiver(systemDialogCloseReceiver, filter);
    }
    
    private void unregisterSystemDialogCloseReceiver(){
    	mContext.unregisterReceiver(systemDialogCloseReceiver);
    }
    
    private void sendsystemDialogCloseBroadCast(){
    	Intent intent = new Intent(UM_CLOSE_SYSTEM_DIALOG_ACTION);
    	intent.putExtra("reason", "SelectSourceDialog");
    	mContext.sendBroadcast(intent);
    }
    
    private void doSourceSelect(int destid){
        
    	Log.d(TAG,"doSourceSelect destid:"+destid);
    	
        Intent intent = new Intent();
        if (isTFProduct()){
            if (destid == EnumSourceIndex.SOURCE_YPBPR1){
            	destid = EnumSourceIndex.SOURCE_CVBS1; 
            }
        }
     

        intent.putExtra("SourceName", destid);
        //intent.putExtra("SourceNameStr",mContext.getString(getSourceStrById(destid)));
        
        if (destid == EnumSourceIndex.SOURCE_DVBC || destid == EnumSourceIndex.SOURCE_DTMB){
            intent.setAction(INTENT_DTV);
        }
        else if (destid == EnumSourceIndex.SOURCE_ATV){
        	intent.setAction(INTENT_ATV);
        }else{
        	intent.setAction(INTENT_PORT);
        }
        
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
            mContext.startActivity(intent);
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        sendsystemDialogCloseBroadCast();
    }
}
