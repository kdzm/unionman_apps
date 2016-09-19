package com.um.ui;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.unionman.dvbplayer.DvbMetaData;
import com.unionman.dvbplayer.DvbPlayer;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;
import com.unionman.jazzlib.*;

import android.text.Layout;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.um.interfaces.AudioInterface;
import com.um.interfaces.InterfaceValueMaps;
import com.um.interfaces.PictureInterface;
import com.um.util.Util;
import com.um.util.Constant;
import com.um.controller.AppBaseActivity;
import com.um.controller.ParamSave;
import com.um.controller.Play_Last_Prog;
import com.um.controller.Player;
import com.um.controller.frequentprog.FrequentProg;
import com.um.controller.frequentprog.FrequentProgManager;
import com.um.dvb.R;
import com.um.dvbstack.DVB;
import com.um.dvbstack.DvbStackSearch;
import com.um.dvbstack.Prog;
import com.um.dvbstack.Prog.Epg_LocalTime;
import com.um.dvbstack.ProgList;
import com.um.dvbstack.ProgManage;
import com.um.dvbstack.ProviderProgManage;
import com.um.dvbstack.Status;
import com.um.dvbstack.Status.StatusListener;
import com.um.dvbstack.Tuner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import com.hisilicon.android.tvapi.constant.EnumSoundTrack;
import com.hisilicon.android.tvapi.constant.EnumPictureAspect;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.Audio;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Dvbplayer_Activity extends AppBaseActivity implements Status.StatusListener {
    private final String TAG = "Dvbplayer_Activity";
    private static final String RESET_MONITORS = "cn.com.unionman.umtvsystemserver.RESET_MONITORS";
    private final String PREFERENCES_TAG = "com.um.dvb";
    private final String PREFERENCES_OFTEN_WATCH_EN = "ofenwatch.enable";
    private final int PROG_LIST_DURATION = 60 * 1000;
    private final int DO_SINGAL_DURATION = 2 * 1000;
    private final int CHANNELBAR__DURATION = 3 * 1000;
    private static final int SIGNAL_CHECK = 1008;
	private static final int NOSIGNAL_POWERDOWN = 1009;
	private boolean IsFullScrenplay;
    private int mLastChanNum[] = {-1,-1};
    public static boolean mDisVolAdjust = false;
	public static int Lock_status = 0;
	public static int forcePlayChannel = 0;
	public Dvbplayer_Activity mInstance = null;
	private int mSelectedClassPos = 0;
	private int mSelectedChanPos = 0;
	private int mSelectedChanInAllPos = 0;
	private LinearLayout mListsLayout = null;
	private ListView mClassChanListView = null;
	private ListView mClassListView = null;
	ListView mWeekdayListView = null;
	private LinearLayout mLeftLayout = null;
	private LinearLayout mRightLayout = null;
	private ImageView mLeftArrowImageView = null;
	private ImageView mRightArrowImageView = null;
	private ChannelListAdapter mChannelAdapter = null;
	private AudioManager mAudioManager = null;
	private List<ChannleClassification> mClassList = new ArrayList<ChannleClassification>();
	private DvbPlayerBroadcastReceive MyBroadcastReceiver = null;
	private DvbCaLockServiceReceiver mCaLockServiceReceiver = null;
	private PlayerMenuContrl menuContrl;
	private DvbSettingPopupWindow SettingWindow;
	private ExitPopLayout mExitLayout;
	private int CLICK_EXIT_BTN = 1;
	private int CLICK_ITEM_BTN = 2;
	private int NORMAL_EXIT = 0;
	private boolean isWindOK = false;
	private SurfaceView mPreview;
	private Player player;
    private LinearLayout mChanNumNameLayout = null;
    private ImageView mChanNumHundImageView = null;
    private ImageView mChanNumTenImageView = null;
    private ImageView mChanNumOneImageView = null;
	private static boolean isCloseIppDialog = false;
	private static boolean isIppOpenFlag = false;
	private static boolean autoFeedCardFlag = false;
	private String dvtChannelNum = "000";
	
	private static int screenSaverMode=0;
	private static int mNoSignalPD = 1;
    private boolean mPowerDownDialogOpenFlag = false;
    private static boolean isNosignalPowerdownMonitorStarted = false;
    // text of no signal
    private TextView noSinalTxt;
    private ImageView screenSaverImg;
    
    private ProgramEditPopupWindow mProgramEditPopupWindow;
    
    private Context mContext = this;

    private static final int PIC_DIALOG_DISMISS_BYTIME = 1;
    private static final int SOUND_DIALOG_DISMISS_BYTIME = 2;
    private static final int ZOOM_DIALOG_DISMISS_BYTIME = 3;
    private static final int TRACK_DIALOG_DISMISS_BYTIME = 4;

    private FastSetToast mFastSetToast;
    private  AlertDialog picAlertdialog;
    private  AlertDialog soundAlertdialog;
    private  AlertDialog zoomAlertdialog;
    private  LinearLayout picdialog;
    private  LinearLayout sounddialog;
    private  LinearLayout zoomdialog;
    private  LinearLayout trackdialog;


    private int picModeIndex;
    private int soundModeIndex;
    private int zoomModeIndex;
    private int trackModeIndex;
    private boolean mChanBarHided = false;
    
    boolean isPicDialogDismiss =true;
    boolean isSoundDialogDismiss =true;
    boolean isZoomDialogDismiss =true;
    boolean isTrackDialogDismiss = true;

    private TextView  pic_menu_btn;
    private TextView  sound_menu_btn;
    private TextView  zoom_menu_btn;
    private TextView  track_menu_btn;
    
    private DvbMetaData playInfo=null;
	private boolean isListShowed = false;
	private FrequentProgManager mFrequentProgManager = null;
	private int channelbarTime = 0;
	private Handler proghandler = new Handler();
	private DvbMetaData metaData;
	
    private static int[][] AUDIO_TRACK_MODE_STRING_MAP = {
			{ EnumSoundTrack.TRACK_STEREO, R.string.track_mode_stereo },
			{ EnumSoundTrack.TRACK_DOUBLE_MONO, R.string.track_mode_joint_stereo },
			{ EnumSoundTrack.TRACK_DOUBLE_LEFT, R.string.track_mode_left },
			{ EnumSoundTrack.TRACK_DOUBLE_RIGHT, R.string.track_mode_right } };
    
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
             switch (msg.what) {
			case Constant.DvbSettingPopupWindow_DISMISS:
				SettingWindow.dismiss();
				break;
			case Constant.ProgramEditPopupWindow_DISMISS:
				mProgramEditPopupWindow.dismiss();
				break;
			case Constant.DvbSettingPopupWindow_DISMISS_AND_SHOW_EPG:
				SettingWindow.dismiss();
				mSelectedClassPos = 0;
                mSelectedChanInAllPos = mClassList.get(mSelectedClassPos).getSelectedChanIndex();
				mChannelNum = String.valueOf(mSelectedChanInAllPos);
                displayClassChanList(mClassList.get(mSelectedClassPos), 1);
				displayClassList(mClassList);

                handler.removeCallbacks(mHideChanBarRunnable);
                handler.post(mHideChanBarRunnable);

                LinearLayout chanLayout3 = (LinearLayout) findViewById(R.id.chanbarLayout);
                chanLayout3.removeAllViews();
                mChanBarHided = true;
                
                displayWeekdayList();
            	showEPG();
				break;
			case Constant.DvbSettingPopupWindow_DISMISS_AND_SHOW_PROGRAMEDITPOPUPWINDOW:

	            
				SettingWindow.dismiss();
	        	Prompt.getInstance().enablePromptShow(false);
				mProgramEditPopupWindow = new ProgramEditPopupWindow(Dvbplayer_Activity.this, handler,mClassList,mChannelNum);
				
	        	mProgramEditPopupWindow.showAtLocation(mPreview, Gravity.TOP, 0, 0);

	        	mProgramEditPopupWindow.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
			        	handler.removeCallbacks(mHideListWithNoBarRun);
			            handler.post(mHideListWithNoBarRun);
			            handler.removeCallbacks(mHideChanBarRunnable);
			            handler.post(mHideChanBarRunnable);		          
			            
			        	showSettingPopup();
					}
				});
				break;
			case SIGNAL_CHECK:
				screenSaverSignalCheckHandle();
				break;
			case NOSIGNAL_POWERDOWN:
			    Intent intent = new Intent();
			    intent.setAction("android.intent.action.NOSIGNAL_POWERDOWN_BROADCAST");
			    mContext.sendBroadcast(intent);
				break;
			}
		}
	};
	
	/*
     * 播放节目的Runnable
     */
    private Runnable mPlayRunnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(mPlayRunnable);
            int chanPos = Integer.parseInt(mShowChanNum)-1;
            mChannelNum = String.valueOf(chanPos);
            mShowChanNum = "000";
            ProgList proglist = player.getSpecifyModeProgList(Play_Last_Prog.GetInstance().lastProgMode);
            if (mSwitching) {
                if (chanPos < proglist.list.size() && chanPos >= 0) {
                    mSelectedClassPos = 0;
                    mSelectedChanInAllPos = Integer.parseInt(mChannelNum);
                    mSelectedChanPos = mSelectedChanInAllPos;
                    mClassList.get(mSelectedClassPos).setSelectedChanIndex(mSelectedChanInAllPos);
                }
            }
            mSwitching = false;
            int channelNum = Integer.parseInt(mChannelNum);
            
            if (forcePlayChannel == 1){
                if (getFullScreenplayFlag()){
                	Log.i("Dvbplayer_Activity", "playforceProg");
                    player.playforceProg(proglist, channelNum);
          
                    Dvbplayer_Activity.this.displayChanBar(proglist.list.size()-1);
                    forcePlayChannel = 0;
                }
            } else {
                if (channelNum < proglist.list.size() && channelNum >= 0){
                    if (getFullScreenplayFlag()){
                        Log.i("Dvbplayer_Activity", ">>>playProg");
                        player.playProg(proglist, channelNum);
                        Dvbplayer_Activity.this.displayChanBar(channelNum);
                        mLastChanNum[0] = mLastChanNum[1];
                        mLastChanNum[1] = channelNum;
                    }
                } else {
                    Toast.makeText(Dvbplayer_Activity.this, R.string.no_such_channel, Toast.LENGTH_SHORT).show();
                }
            }
            mChanNumNameLayout.setVisibility(INVISIBLE);
            handler.removeCallbacks(mHideChanBarRunnable);
            handler.postDelayed(mHideChanBarRunnable, CHANNELBAR__DURATION);
        }
    };

    /*
     * 隐藏ChannelBar的Runnable
     */
	private Runnable mHideChanBarRunnable = new Runnable(){
		public void run(){
			Log.v(TAG, "in HideChanBarRunnable");
            handler.removeCallbacks(mHideChanBarRunnable);

            //隐藏屏幕右上角频道号
            mChanNumNameLayout.setVisibility(INVISIBLE);
            setFullScreenplayFlag(true);
            LinearLayout listlayout = (LinearLayout) findViewById(R.id.chanbarLayout);
            listlayout.removeAllViews();
            mChanBarHided = true;
		}
	};

	private Runnable mProgInfoRunnable = new Runnable(){
		public void run(){
			proghandler.removeCallbacksAndMessages(null);
			if(!mChanBarHided){
				if(!showProgInfo()){
					proghandler.postDelayed(mProgInfoRunnable, 800);
				}
			} 
		}
	};
	
	/*
     * 隐藏频道列表但不显示状态栏的Runnable
     */
    Runnable mDoSingalRunnable = new Runnable(){
        public void run(){
        	doSignalHandle();
        }
    };

	/*
     * 隐藏频道列表但不显示状态栏的Runnable
     */
    Runnable mHideListWithNoBarRun = new Runnable(){
        public void run(){
            handler.removeCallbacks(mHideListRunnable);
            ProgList proglist = player.getSpecifyModeProgList(Play_Last_Prog.GetInstance().lastProgMode);
            if (Integer.parseInt(mChannelNum) < proglist.list.size()){
                //隐藏频道列表
                hideList();
            }
        }
    };
	/*
     * 隐藏频道列表的Runnable
     */
    Runnable mHideListRunnable = new Runnable(){
        public void run(){
            handler.removeCallbacks(mHideListRunnable);
            ProgList proglist = player.getSpecifyModeProgList(Play_Last_Prog.GetInstance().lastProgMode);
            if (Integer.parseInt(mChannelNum) < proglist.list.size()){

                //隐藏频道列表
                hideList();
                if (SettingWindow != null && !SettingWindow.isShowing() && SettingWindow.isShowOnEPGDismiss()) {
                    showSettingPopup();
                    SettingWindow.setShowOnEPGDismiss(false);
                }
                //显示ChannelBar和频道号
                if (SettingWindow == null || !SettingWindow.isShowing()) {
                    displayChanBar(Integer.parseInt(mChannelNum));
                }
            }
        }
    };

    /*
     * 隐藏 全部频道 列表
     */
    private void hideList(){
        setFullScreenplayFlag(true);
        mListsLayout.removeAllViews();
        mDisVolAdjust = false;
        mClassList.get(0).setSelectedChanIndex(mSelectedChanInAllPos);
        EpgManager.getInstance().deinit();
		isListShowed = false;
    }

    
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Log.i(TAG, "DvbPlayerActivity onCreate!!");
		hideStatusbar();
		mInstance = Dvbplayer_Activity.this;
		DVB.getInstance();
		ProviderProgManage prog = ProviderProgManage.GetInstance(this);
		prog.refreshProgList();
        ProviderProgManage.GetInstance(this).SetCurMode(ProgManage.TVPROG);
        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
		if (mode != null && mode.equals("radio")){
            ProviderProgManage.GetInstance(this).SetCurMode(ProgManage.RADIOPROG);
		}
		
		mFrequentProgManager = new FrequentProgManager(this);
		
		if (!Player.GetInstance().Ishaveprog()) {
			restartDvbPlayerActivity();
            return;
		}
		
  		isListShowed = false;
		setContentView(R.layout.dvbplayer);

		mPreview = (SurfaceView) findViewById(R.id.surfaceview01);

        mChanNumNameLayout = (LinearLayout) findViewById(R.id.chan_num_name_layout);
        mChanNumHundImageView = (ImageView) findViewById(R.id.chan_num_hund_image_view);
        mChanNumTenImageView = (ImageView) findViewById(R.id.chan_num_ten_image_view);
        mChanNumOneImageView = (ImageView) findViewById(R.id.chan_num_one_image_view);
        noSinalTxt = (TextView)findViewById(R.id.tv_nosignal);
        screenSaverImg = (ImageView)findViewById(R.id.tv_screensaver);
        mListsLayout = (LinearLayout) findViewById(R.id.quicklistid);
        SurfaceHolder holder = mPreview.getHolder();
        
        String bouquet = intent.getStringExtra("bouquet");
        setContext(this);
        mAudioManager = (AudioManager) Dvbplayer_Activity.this.getSystemService(Context.AUDIO_SERVICE);
        Player.ConnectLister cl = new Player.ConnectLister(){
            @Override
            public void doSomething(int arg1) {
                int progIndex = playLastProg();
                if (progIndex == -1) {
                	return ;
                }
                
                ListView list = (ListView) findViewById(R.id.channel_list_view);

                if(list != null){
                    if (mRightLayout.getVisibility() == View.VISIBLE) {
                        mWeekdayListView.setSelection(0);
                        mWeekdayListView.requestFocus();
                    } else {
                        list.setSelection(progIndex);
                        list.requestFocus();
                    }
                }
                else{
//                	Log.i(TAG, "频道列表为空");
                }
            }

        };
        
		player = Player.GetInstance();
		player.setActivty(this, holder, cl);
		mFastSetToast = new FastSetToast(this);
	}
	
	private int playLastProg() {
        int progIndex = ParamSave.GetLastProgIndex(Dvbplayer_Activity.this,player.getCurMode());
        Log.v(TAG, "progIndex="+progIndex+", max="
        			+Dvbplayer_Activity.this.player.getCurModeProgList().list.size());
        if(progIndex>=Dvbplayer_Activity.this.player.getCurModeProgList().list.size()
        		|| progIndex < 0){
            progIndex = 0;
        }
        
        player.playProg(Dvbplayer_Activity.this.player.getCurModeProgList(), progIndex);
        return progIndex;
	}
	
    private void getNoSignalPDFlag(){
		try {
			Context powerSaveAppContext;
			powerSaveAppContext = createPackageContext("cn.com.unionman.umtvsetting.powersave", Context.CONTEXT_IGNORE_SECURITY);
	       	SharedPreferences sharedata = powerSaveAppContext.getSharedPreferences("PoweritemVal", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
	                  + Context.MODE_MULTI_PROCESS);
	       	mNoSignalPD = sharedata.getInt("autoShutdonw", 1);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} 
    }
    
	private void registerEventMonitors(){
		Log.i(TAG,"registerEventMonitors");
		
		IntentFilter filter = new IntentFilter();   
		filter.addAction(RESET_MONITORS);  
		registerReceiver(systemEventMonitorsReceiver, filter);
	}
	
	private void unRegisterEventMonitors() {
		unregisterReceiver(systemEventMonitorsReceiver);
	}
	
	private void restartDvbPlayerActivity() {
    	Intent it = new Intent(this, EntryActivity.class);
    	Bundle bundle = getIntent().getExtras();
    	if  (bundle != null) {
    		it.putExtras(bundle);
    	}
    	startActivity(it);
    	finish();		
	}
	
	private boolean isFinishedForDvbServerDisabled = false;
	@Override
    public void onResume(){
    	super.onResume();
        Log.i(TAG, "DvbPlayerActivity onResume!!");
        isFinishedForDvbServerDisabled = false;
        if (SystemProperties.get("persist.sys.dvb.enabled", "0").equals("0")) {
        	restartDvbPlayerActivity();
        	isFinishedForDvbServerDisabled = true;
        	return;
        }
        
        getNoSignalPDFlag();
        registerEventMonitors();
        mIsActivityPaused  = false;
        
		ProviderProgManage prog = ProviderProgManage.GetInstance(this);
		prog.refreshProgList();
        ProviderProgManage.GetInstance(this).SetCurMode(ProgManage.TVPROG);
        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
		if (mode != null && mode.equals("radio")){
            ProviderProgManage.GetInstance(this).SetCurMode(ProgManage.RADIOPROG);
		}
		
		if (!Player.GetInstance().Ishaveprog()) {
			restartDvbPlayerActivity();
            return;
		}
		
		try {
	        Context otherContext = createPackageContext(
	                "cn.com.unionman.umtvsetting.system", Context.CONTEXT_IGNORE_SECURITY);
	        SharedPreferences sp = otherContext.getSharedPreferences(
	                  "itemVal", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
	                  + Context.MODE_MULTI_PROCESS);
	        
	        screenSaverMode = sp.getInt("screensaverState", 0);
	        Log.i(TAG,"screensaverState "+screenSaverMode);
        } catch (NameNotFoundException e) {
        	e.printStackTrace();
        }
		
		IntentFilter intentFilter = new IntentFilter();
		String str = new String("com.um.dvbstack.UMSG_DVB_EPG_PF_EVENTS_UPDATE");
		intentFilter.addAction(str); //为BroadcastReceiver指定action，即要监听的消息名字�
		MyBroadcastReceiver = new DvbPlayerBroadcastReceive();
		registerReceiver(MyBroadcastReceiver, intentFilter); //注册监听
		
		Status status = Status.getInstance();
		status.addStatusListener(this);

        handler.removeCallbacks(mHideListRunnable);
		handler.postDelayed(mHideListRunnable, PROG_LIST_DURATION);

        IntentFilter filter = new IntentFilter("com.um.umdvb.UMSG_DVB_CA_UNLOCK_SERVICE");
        filter.addAction("com.um.umdvb.UMSG_DVB_CA_LOCK_SERVICE");
        filter.addAction("com.um.umdvb.UMSG_DVB_DVTCA_LOCK_SERVICE");
		filter.addAction("com.um.umdvb.UMSG_DVB_DVTCA_UNLOCK_SERVICE");
		mCaLockServiceReceiver = new DvbCaLockServiceReceiver();
        registerReceiver(mCaLockServiceReceiver, filter);
        
        int progId = getIntent().getIntExtra(EpgTimerProc.KEY_PROG_ID, -1);
		int curIndex = 0;
        if (progId != -1) {
            curIndex = getIndexById(player.getCurModeProgList(), progId);
        } else {
            curIndex = ParamSave.GetLastProgIndex(this,ProviderProgManage.GetInstance(this).GetCurMode());
        }
        
        mClassList.clear();
        allToOneClass();      //将全部频道分成一个分类
        //classifyChannels();   //对频道进行分类
		//batClass();
		batCategory();
		initHDTVClass();
		//refreshOftenWatchTv();
        collectedToAClass();  //将喜爱频道分成一个分类
		checkCaIconStatus();//检查CA邮件、反授权等图标状态
		
//        Log.v(TAG, "show bouquet: " + mClassList.get(mSelectedClassPos).getClassificationName());
        mSelectedClassPos = 0;
		mClassList.get(mSelectedClassPos).setSelectedChanIndexInAll(curIndex);
		int selectedIndex = mClassList.get(mSelectedClassPos).getSelectedChanIndexInAll();
		if (curIndex != selectedIndex) {
			ParamSave.SaveLastProgInfo(this, selectedIndex, ProviderProgManage.GetInstance(this).GetCurMode());
		}
		
		playLastProg();

        mSelectedChanInAllPos = mClassList.get(mSelectedClassPos).getSelectedChanIndex();
		mChannelNum = String.valueOf(mSelectedChanInAllPos);
		Log.v(TAG, "mChannelNum="+mChannelNum);
        boolean needEPG = getIntent().getBooleanExtra("show_epg", false);
        if (needEPG) {
	        displayClassChanList(mClassList.get(mSelectedClassPos), 1);  //显示 分类频道 列表
	        displayClassList(mClassList);  // 显示 频道分类 列表
	        displayWeekdayList();
	        showEPG();
        }
        //displayChanBar(curIndex);
        handler.post(mHideListRunnable);
        Prompt.getInstance().attachContext(this);
	//add by unionman
        Prompt.getInstance().sendShoweMailOrNot();
        //by end	

        //doSignalHandle(); /*handle in onWindowFocusChanged(boolean)*/

        if (SettingWindow == null || !SettingWindow.isShowing()) {
            Prompt.getInstance().enablePromptShow(true);
        }

        startSettingWindowTimer();
    }

    private void showEPG() {
    	Log.v(TAG, "showEPG");
        Prog prog = getProg(mSelectedChanInAllPos);
        Epg_LocalTime epgLocalTime = new Epg_LocalTime();
        prog.prog_get_localtime(epgLocalTime);
        int curDay = epgLocalTime.weekday;
        Log.d("list.setSelection", " showEPG");

        mRightArrowImageView.setVisibility(INVISIBLE);
        mRightLayout.setVisibility(VISIBLE);
        if (mSelectedClassPos == 0) {
            mSelectedChanInAllPos = mClassChanListView.getSelectedItemPosition();
        }
        Animation animRightIn = AnimationUtils.loadAnimation(Dvbplayer_Activity.this, R.anim.anim_translate_left_in);
        animRightIn.setDuration(300);
        mRightLayout.startAnimation(animRightIn);
        EpgManager.getInstance().setEpgShowFlag(true);
    }

	private boolean isEpgShowed() {
        return EpgManager.getInstance().getEpgShowFlag();
    }

    private boolean mIsActivityPaused = false;
	@Override
	protected void onPause(){
		Log.i(TAG, "DvbPlayerActivity onPause!!");
        mIsActivityPaused  = true;
        mFastSetToast.dismiss();
        
        if (isFinishedForDvbServerDisabled) {
        	super.onPause();
        	return ;
        }
        
        stopNoSignalPowerdownMonitor();
        unRegisterEventMonitors();
        
        proghandler.removeCallbacksAndMessages(null);
    	handler.removeCallbacks(mHideChanBarRunnable);
    	handler.removeCallbacks(mHideListRunnable);
    	handler.removeMessages(SIGNAL_CHECK);
    	
    	if (player != null) {
	    	if (player.getPlayStatus() ==1 ){
	            player.stopPlay();
	    	}
    	}
  		Status.getInstance().removeStatusListener(this);
  		Prompt.getInstance().detachContext();
  		
		if (MyBroadcastReceiver != null) {
			unregisterReceiver(MyBroadcastReceiver);
			MyBroadcastReceiver = null;
		}
		if (mCaLockServiceReceiver != null)
		{
			unregisterReceiver(mCaLockServiceReceiver);
			mCaLockServiceReceiver = null;
		}

	    if ((this.menuContrl != null) && (this.menuContrl.isShowing()))
	        this.menuContrl.dismiss();
	    
	    if ((this.SettingWindow != null) && (this.SettingWindow.isShowing()) && SettingWindow.getDismissOnItemClick()) {
            this.SettingWindow.dismiss();
        }
	    
        stopSettingWindowTimer();
	    stopScreenSaver();
	    removeDialogs();
		
        super.onPause();
        //finish();
	}
	 @Override
		   protected void onStop(){ 
			 	Log.v(TAG, "onStop");
				super.onStop();
			 }

    private void startSettingWindowTimer() {
        if ((this.SettingWindow != null) && (this.SettingWindow.isShowing())) {
            this.SettingWindow.statrCountDownTimer();
        }
    }

    private void stopSettingWindowTimer() {
        if ((this.SettingWindow != null) && (this.SettingWindow.isShowing())) {
            this.SettingWindow.stopCountDownTimer();
        }
    }

    @Override
	protected void onDestroy(){
		Log.i(TAG, "DvbPlayerActivity onDestroy!!");
		mInstance = null;
		if (player != null) {
			player.removeActivty();
		}
		EpgManager.getInstance().deinit();

        if (SettingWindow != null && SettingWindow.isShowing()) {
            SettingWindow.dismiss();
        }
        
	    if (SettingWindow != null){
	    	SettingWindow.unregisterReceiver();
	    }
	    
		super.onDestroy();
	}


	public int GetCurProMode(int shift){
		int mode = ProviderProgManage.GetInstance(this).GetCurMode();
		if((mode+shift)>=3) //ProgManage.MAX_LIST_CNT
			mode = (mode+shift)%3; //ProgManage.MAX_LIST_CNT
		else if((mode+shift)<0) //ProgManage.MAX_LIST_CNT
			mode = 2; //ProgManage.MAX_LIST_CNT
		else
			mode = (mode+shift)%3;

		ProviderProgManage.GetInstance(this).SetCurMode(mode);
		return mode;
	}

	public void inflateList(int animType, String listType){
		LayoutInflater layoutInflater = LayoutInflater
				.from(Dvbplayer_Activity.this);
		mListsLayout.removeAllViews();
		View mAllListsView = layoutInflater.inflate(R.layout.dvbplayer_conlist, null);
		mLeftArrowImageView = (ImageView) mAllListsView.findViewById(R.id.list_left_arrow);
		mRightArrowImageView = (ImageView) mAllListsView.findViewById(R.id.list_right_arrow);
		mListsLayout.addView(mAllListsView);
		mListsLayout.setX(-160f);
		mLeftLayout = (LinearLayout) mAllListsView.findViewById(R.id.left_layout);
		mRightLayout = (LinearLayout) mAllListsView.findViewById(R.id.right_layout);
		EpgManager.getInstance().init(this);
		Animation animation;
		switch (animType) {
			case 1:
				//animation=AnimationUtils.loadAnimation(this, R.anim.anim_translate_left_in);
				animation=AnimationUtils.loadAnimation(this, R.anim.dia_fade_in);
				mAllListsView.startAnimation(animation);
				break;
			default:
				break;
		}

		if ( player.getCurMode()== ProgManage.RADIOPROG){
			SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceview01);
			surfaceView.setBackgroundResource(R.drawable.logo2);
		}else{
			SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceview01);
			surfaceView.setBackgroundColor(0);
		}
	}

	private boolean getFullScreenplayFlag(){
		return IsFullScrenplay;
	}

	private void setFullScreenplayFlag(boolean flag){
		IsFullScrenplay = flag;
	}

	public Prog getProg(int curChannelIndex){
        if (curChannelIndex < 0) {
            return null;
        }

		ProgList proglist = player.getCurModeProgList();
		int progindex = 0;

		if( proglist.list.isEmpty() == false ){
			progindex = Integer.valueOf(proglist.list.get(curChannelIndex).get(
					ProgManage.PROG_VIEWINDEX));
		}
	    return player.getProg(progindex);
	}

	public void displayChanBar(int curChannelIndex){
		View view = null;
        int chanNum = curChannelIndex + 1;
		Log.d(TAG, "displayChanBar Enter");
		channelbarTime = 0;
		//显示右上角频道号
        if (mChanNumNameLayout.getVisibility() == View.VISIBLE) {
            showChannelNum(String.valueOf(chanNum));
        }

		if ( player.getCurMode()== 2){
			SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceview01);
			if( (chanNum%2) == 0){
				surfaceView.setBackgroundResource(R.drawable.logo2);
			}else{
				surfaceView.setBackgroundResource(R.drawable.logo);
			}
		}else{
			handler.removeCallbacks(mHideChanBarRunnable);
			handler.postDelayed(mHideChanBarRunnable, CHANNELBAR__DURATION);
		}

		if (this.findViewById(R.id.chanbar_id) == null){
			LinearLayout chanLayout = (LinearLayout) findViewById(R.id.chanbarLayout);
			
			LayoutInflater layoutInflater = LayoutInflater
					.from(Dvbplayer_Activity.this);
			view = layoutInflater.inflate(R.layout.chanbar2, null);
			chanLayout.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));// 閿熸枻鎷烽敓绲廙L閿熸枻鎷稬inearLayout
			Animation animation;
			//animation=AnimationUtils.loadAnimation(this, R.anim.anim_translate_bottom_in);
			animation=AnimationUtils.loadAnimation(this, R.anim.dia_fade_in);

			view.startAnimation(animation);
			mChanBarHided = false;
		}else{
			view = this.findViewById(R.id.chanbar_id);
		}
		View chanView = view;
		Prog epgprog = getProg(curChannelIndex);

		ArrayList<com.um.dvbstack.Event> pf = new ArrayList<com.um.dvbstack.Event>();
        //showChannelNum((curChannelIndex + 1) + "");
		TextView chanName = (TextView) findViewById(R.id.channel_name_text_view);
		chanName.setText(epgprog.getName());
		chanName.setSelected(true);
		epgprog.getPF(pf);
		

//        Log.v(TAG, "prog info watch time:" + epgprog.getWatchTime());
//        Log.v(TAG, "prog info track:" + epgprog.getTrack());
//        Log.v(TAG, "prog info collect:" + epgprog.getCollectFlag());
//        Log.v(TAG, "prog info set track:" + epgprog.setTrack(3));
	     
		Epg_LocalTime localTime = new Epg_LocalTime();
		epgprog.prog_get_localtime(localTime);
		String str = String.format("%02d:%02d", localTime.hour, localTime.min);
		 TextView chanbar_prog_curtime = (TextView) findViewById(R.id.chanbar_prog_curtime);
		 chanbar_prog_curtime.setText(str);

	    TextView chanbar_prog_name = (TextView) findViewById(R.id.chanbar_prog_name);
	    chanbar_prog_name.setText(epgprog.getName());
        TextView chanbar_prog_num = (TextView) findViewById(R.id.chanbar_prog_num);
        switch (String.valueOf(chanNum).length()) {
		case 1:
			chanbar_prog_num.setText("00"+chanNum);
			break;
		case 2:
			chanbar_prog_num.setText("0"+chanNum);
			break;
		case 3:
			chanbar_prog_num.setText(chanNum+"");
			break;
		default:
			break;
		}
        
		
		TextView pName = (TextView) chanView
				.findViewById(R.id.chanbar_text_p_name);
		TextView pStartTime = (TextView) chanView
				.findViewById(R.id.chanbar_text_p_start_time);
		TextView pEndTime = (TextView) chanView
				.findViewById(R.id.chanbar_txt_p_end_time);
		TextView nextProg = (TextView) chanView
				.findViewById(R.id.chanbar_next_prog);
		TextView cur_prog = (TextView) chanView
				.findViewById(R.id.chanbar_cur_prog);

		if (!pf.isEmpty()){
			cur_prog.setText(R.string.cur_prog);
			pName.setText(pf.get(0).getName());

			Time startTime = pf.get(0).getStartTime();
			Time endTime = pf.get(0).getEndTime();

			pStartTime.setText(String.format("%02d:%02d - ",startTime.hour,startTime.minute));
			pEndTime.setText(String.format("%02d:%02d",endTime.hour,endTime.minute));
			nextProg.setText(R.string.next_prog);
			int progress;
			long curTime = System.currentTimeMillis();
			progress = (int) ((curTime - startTime.toMillis(true)) * 100 / (endTime
					.toMillis(true) - curTime));

			if (progress < 0 || progress > 100){
				progress = 0;
			}
		}else{
			pName.setText("");
			pStartTime.setText("");
			pEndTime.setText("");
		}


		TextView fname = (TextView) chanView
				.findViewById(R.id.chanbar_text_f_name);
		TextView fStartTime = (TextView) chanView
				.findViewById(R.id.chanbar_text_f_start_time);
		TextView fEndTime = (TextView) chanView
				.findViewById(R.id.chanbar_txt_f_end_time);

		if (!(pf.isEmpty())&&(pf.size() > 1)){
			fname.setText(pf.get(1).getName());

			Time fstartTime = pf.get(1).getStartTime();
			Time fendTime = pf.get(1).getEndTime();

			fStartTime.setText(String.format("%02d:%02d - ",fstartTime.hour,fstartTime.minute));
			fEndTime.setText(String.format("%02d:%02d",fendTime.hour,fendTime.minute));
		}else{
			fname.setText("");
			fStartTime.setText("");
			fEndTime.setText("");
		}
		
		proghandler.removeCallbacksAndMessages(null);
		//if(!showProgInfo()) 
		{
			proghandler.postDelayed(mProgInfoRunnable, 200);
		}
		
		Log.d(TAG, "displayChanBar leave");
		
	}

	private boolean showProgInfo() {
		Log.v(TAG, "showProgInfo is challed.");
		TextView resolution = (TextView) findViewById(R.id.resolution);
		TextView videotype = (TextView) findViewById(R.id.vdectype);
		if(resolution == null
			|| videotype == null){
			Log.d(TAG, "resolution = "+resolution+" videotype = "+videotype);
			return false;
		}
		resolution.setText(R.string.video_resolution);
		videotype.setText(R.string.video_codec);

		TextView resolutionVal = (TextView) findViewById(R.id.resolution_val);
		TextView videotypeVal = (TextView) findViewById(R.id.vdectype_val);
		if(resolutionVal == null
			|| videotypeVal == null){
			Log.d(TAG, "resolutionVal = "+resolutionVal+" videotypeVal = "+videotypeVal);
			return false;
		}
		videotypeVal.setText(R.string.unknown);
		resolutionVal.setText(R.string.unknown);

		if (player.getPlayerServive() != null
				&& player.getPlayerServive().getMediaPlayer() != null) {

			String filter[] = new String[] { DvbMetaData.KEY_VIDEO_CODEC_TYPE,
					DvbMetaData.KEY_VIDEO_WIDTH, DvbMetaData.KEY_VIDEO_HEIGHT };
			metaData = player.getPlayerServive().getMediaPlayer()
					.getMetaData(filter);
			if (metaData == null) {
				Log.w(TAG, "metaData is null");
				return false;
			}
			String vcodec = metaData.getString(
					DvbMetaData.KEY_VIDEO_CODEC_TYPE, "");
			int vidWidth = metaData.getInt(DvbMetaData.KEY_VIDEO_WIDTH, 0);
			int vidHeight = metaData.getInt(DvbMetaData.KEY_VIDEO_HEIGHT, 0);

			if (vcodec != null) {
				videotypeVal.setText(vcodec);
			}

			if ((vidWidth != 0) && (vidWidth != 0)) {
				resolutionVal.setText(vidWidth + "*" + vidHeight);
				return true;
			}
		} else {
			Log.d(TAG, "player.getPlayerServive is null");
		}
		return false;
	}

    private String mChannelNum = "000";
    private String mShowChanNum = "000";
    private boolean mSwitching = false;
    private boolean reCall()
    {
		if (getFullScreenplayFlag()) {
			if(getIppOpenflag()){
				if(getCloseIppFlag()){
					Log.i("KEYCODE_CHANNEL_DOWN","com.um.dvb.STOP_IPPV");
					if(mInstance != null){
						Intent intent = new Intent("com.um.dvb.STOP_IPPV");
						mInstance.sendBroadcast(intent);
					}
				}else{
					Log.i("could not runing here!","com.um.dvb.STOP_IPPV");
					//return super.onKeyDown(keyCode, event);
				}
			}

            handler.removeCallbacks(mHideChanBarRunnable);
            handler.removeCallbacks(mPlayRunnable);
            mSwitching = false;
            mShowChanNum = "000";
			int curChannelIndex = ParamSave.GetPreProgIndex(this); //播放最后播放节目
			player.playProg(player.getCurModeProgList(), curChannelIndex);
			mClassList.get(0).setSelectedChanIndex(curChannelIndex);
            mChannelNum = String.valueOf(curChannelIndex);
			mLastChanNum[0] = mLastChanNum[1];
            mLastChanNum[1] = curChannelIndex;
  
			this.displayChanBar(curChannelIndex);
		}
		return true;
    }
    private Bundle getBundlePlayInfo()
    {
        if(player.getPlayerServive().getMediaPlayer()!=null)
        {	
        	playInfo = player.getPlayerServive().getMediaPlayer().getMetaData();
        	if(playInfo!=null)
        	{
        		playInfo.print();
        	}
        	else
        	{
        		Log.i("playInfo","playInfo is null!");
        	}
        }
        else
        {
        	Log.i("playInfo","mMediaPlayer is null");
        }
        Bundle bundle = new Bundle();
        
        Prog prog = getProg(player.getCurModeProgList().curProgIndex);
        bundle.putString("progname", prog.getName());
        bundle.putInt("progid", prog.getServiceId());
        
        
        if(playInfo!=null)
        {
        	bundle.putString("fend_type", playInfo.getString(DvbMetaData.KEY_FEND_TYPE, "DVBC"));
        	bundle.putInt("bandwidth", playInfo.getInt(DvbMetaData.KEY_FEND_BANDWIDTH, 0));
        	bundle.putInt("freq", playInfo.getInt(DvbMetaData.KEY_FEND_FREQ, 0));
        	bundle.putInt("symb", playInfo.getInt(DvbMetaData.KEY_FEND_SYMBOL_RATE_, 0));
        	bundle.putInt("qam", playInfo.getInt(DvbMetaData.KEY_FEND_QAM_MODE, 0));
        	bundle.putInt("vpid",playInfo.getInt(DvbMetaData.KEY_VIDEO_PID, 0x1fff));
        	bundle.putInt("apid", playInfo.getInt(DvbMetaData.KEY_AUDIO_PID, 0x1fff));
        	bundle.putString("vdec_type", playInfo.getString(DvbMetaData.KEY_VIDEO_CODEC_TYPE, ""));
        	bundle.putString("adec_type", playInfo.getString(DvbMetaData.KEY_AUDIO_CODEC_TYPE, ""));
        	bundle.putInt("vid_width", playInfo.getInt(DvbMetaData.KEY_VIDEO_WIDTH, 0));
        	bundle.putInt("vid_height", playInfo.getInt(DvbMetaData.KEY_VIDEO_HEIGHT, 0));
        	bundle.putInt("signalstrenth", playInfo.getInt(DvbMetaData.KEY_FEND_SIGNAL_STRENTH, 0));

        }
        return bundle;
    }

    private void showSettingPopup() {
        if (SettingWindow == null)
        {
            SettingWindow = new DvbSettingPopupWindow(Dvbplayer_Activity.this,handler);
        }
        SettingWindow.setAnimationStyle(R.style.popwindow_view);
        this.SettingWindow.showAtLocation(mPreview, Gravity.TOP, 0, 0);
        SettingWindow.setPlayInfo(getBundlePlayInfo());
        stopScreenSaver();
        Prompt.getInstance().enablePromptShow(false);
        this.SettingWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                Prompt.getInstance().enablePromptShow(true);
            }
        });
    }

    private void switchMode(String mod)
    {
    	    Intent intent = getIntent();
    	    intent.putExtra("mode", mod);
    	    overridePendingTransition(0, 0);
    	    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    	    finish();
    	    overridePendingTransition(0, 0);
    	    startActivity(intent);
    }

    private String intToString(int num) {
        String numString;
        if (num >= 100) {
            numString = "" + num;
        } else if (num < 100 && num >= 10) {
            numString = "0" + num;
        } else if (num < 10 && num > 0) {
            numString = "00" + num;
        } else {
            numString = "001";
        }
        return numString;
    }
	public boolean onKeyDown( int keyCode,  KeyEvent event){
		
		//处理强制切台按键禁止
		if(Lock_status == 1){
			return true;
		}
		
		noSignalPowerdownMonitorKeyHandle();
		screanSaverKeyHandle();
		
		if ((keyCode>=KeyEvent.KEYCODE_0)&&(keyCode<=KeyEvent.KEYCODE_9)){
            mSwitching = true;
            String numberString = "";
            numberString = mShowChanNum;
            if (numberString.length() >= 3){
                String str = numberString.substring(1, 3);
                numberString = str + String.valueOf(keyCode - KeyEvent.KEYCODE_0);
            }else{
                numberString += (keyCode - KeyEvent.KEYCODE_0);
            }

            mShowChanNum = numberString;
            showChannelNum(mShowChanNum);
            handler.removeCallbacks(mHideChanBarRunnable);
            handler.removeCallbacks(mPlayRunnable);
            handler.postDelayed(mPlayRunnable, 1000);
        }

		switch (keyCode){
		case KeyEvent.KEYCODE_HOME:
			if( ProviderProgManage.TVPROG != ProviderProgManage.GetInstance(this).GetCurMode())
			{	
				ProviderProgManage.GetInstance(this).SetCurMode(ProgManage.TVPROG);//退出播放恢复为电视模式
			}
			return false;
		case KeyEvent.KEY_RADIO:
			if( ProviderProgManage.TVPROG == ProviderProgManage.GetInstance(this).GetCurMode())
			{

				if(!player.getRadioProgList().list.isEmpty())
				{
					switchMode("radio");
				}
				else
				{
					Log.i("HC","RADIO PROGLIST IS NULL!!");
				}

			}
			else
			{
				if(!player.getTVProgList().list.isEmpty())
				{
					switchMode("tv");
				}
				else
				{
					Log.i("HC","TV PROGLIST IS NULL!!");
				}
				
			}
			return true;
		case KeyEvent.KEYCODE_FAVORITES:/*KEY_FAVORITES*/
			mSelectedClassPos = mClassList.size()-1;
			ChannleClassification classification = mClassList.get(mSelectedClassPos);
			if (classification.getChanList()==null 
					|| classification.getChanList().size() <= 0) {
				Toast.makeText(this, R.string.fav_prog_not_exist, 2000).show();
				return true;
			}
			
			classification.setSelectedChanIndexInAll(mSelectedChanInAllPos);
			displayClassChanList(classification, 1);  //显示 分类频道 列表
			displayClassList(mClassList);  // 显示 频道分类 列表
			LinearLayout chanLayout = (LinearLayout) findViewById(R.id.chanbarLayout);
			chanLayout.removeAllViews();
			handler.removeCallbacks(mHideChanBarRunnable);
			handler.post(mHideChanBarRunnable);
			mClassListView.setSelection(mSelectedClassPos);
			/*
			if (mSelectedChanPos >= mClassChanListView.getCount()) {
				mSelectedChanPos = 0;
			}
			mSelectedChanInAllPos = mClassList.get(mSelectedClassPos).getChanIndexInAll(mSelectedChanPos);
			mChannelNum = String.valueOf(mSelectedChanInAllPos);
			player.playProg(Dvbplayer_Activity.this.player.getCurModeProgList(), mSelectedChanInAllPos);
			*/
			return true;
		case KeyEvent.KEYCODE_INFO:
			/*
        	Intent itinfo = new Intent();
        	itinfo.setClassName("com.um.dvbsettings", "com.um.ui.ProgramInfo");
        	itinfo.putExtras(getBundlePlayInfo());
	    	startActivity(itinfo);*/
			if (mChanBarHided) {
		    	int currentChannelIndex = player.getCurModeProgList().curProgIndex;
		    	displayChanBar(currentChannelIndex);
				Log.d(TAG, "displayChanBar = "+currentChannelIndex);
			} else {
				handler.post(mHideChanBarRunnable);
			}
	    	return true;
        case KeyEvent.KEYCODE_RECALL:
        	return reCall();
		case KeyEvent.KEYCODE_DPAD_DOWN:
        case KeyEvent.KEYCODE_CHANNEL_DOWN:
            if (mChanNumNameLayout.getVisibility() == VISIBLE) {
                int showNum = Integer.parseInt(mShowChanNum) - 1;
                if (showNum < 1) {
                    showNum = player.getCurModeProgList().list.size();
                }
                mShowChanNum = intToString(showNum);

                showChannelNum(mShowChanNum);
                handler.removeCallbacks(mHideChanBarRunnable);
                handler.removeCallbacks(mPlayRunnable);
                handler.postDelayed(mPlayRunnable, 1000);
                return true;
            } else {
                if (getFullScreenplayFlag()) {
                    if (getIppOpenflag()) {
                        if (getCloseIppFlag()) {
                            Log.i("KEYCODE_CHANNEL_DOWN", "com.um.dvb.STOP_IPPV");
                            if (mInstance != null) {
                                Intent intent = new Intent("com.um.dvb.STOP_IPPV");
                                mInstance.sendBroadcast(intent);
                            }
                        } else {
                            return super.onKeyDown(keyCode, event);
                        }
                    }

                    handler.removeCallbacks(mHideChanBarRunnable);
                    handler.removeCallbacks(mPlayRunnable);
                    mSwitching = false;
                    mShowChanNum = "000";
                    int curChannelIndex = player.playPreProg(player.getCurModeProgList());  //播放上一个频道
                    mClassList.get(0).setSelectedChanIndex(curChannelIndex);
                    mChannelNum = String.valueOf(curChannelIndex);
                    mLastChanNum[0] = mLastChanNum[1];
                    mLastChanNum[1] = curChannelIndex;

                    this.displayChanBar(curChannelIndex);

                    return true;
                }
            }
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
        case KeyEvent.KEYCODE_CHANNEL_UP:
            if (mChanNumNameLayout.getVisibility() == VISIBLE) {
                int showNum = Integer.parseInt(mShowChanNum) + 1;
                if (showNum > player.getCurModeProgList().list.size()) {
                    showNum = 1;
                }
                mShowChanNum = intToString(showNum);

                showChannelNum(mShowChanNum);
                handler.removeCallbacks(mHideChanBarRunnable);
                handler.removeCallbacks(mPlayRunnable);
                handler.postDelayed(mPlayRunnable, 1000);
                return true;
            } else {
                hideList();
                if (getFullScreenplayFlag()) {
                    if (getIppOpenflag()) {
                        if (getCloseIppFlag()) {
                            Log.i("KEYCODE_CHANNEL_UP", "com.um.dvb.STOP_IPPV");
                            if (mInstance != null) {
                                Intent intent = new Intent("com.um.dvb.STOP_IPPV");
                                mInstance.sendBroadcast(intent);
                            }
                        } else {
                            Log.i("Dvbplayer_activity", "keycode_chaannelUp");
                            return super.onKeyDown(keyCode, event);
                        }
                    }

                    handler.removeCallbacks(mHideChanBarRunnable);
                    handler.removeCallbacks(mPlayRunnable);
                    mSwitching = false;
                    mShowChanNum = "000";
                    int curChannelIndex = player.playNextProg(player.getCurModeProgList());  //播放下一个频道
                    mClassList.get(0).setSelectedChanIndex(curChannelIndex);
                    mChannelNum = String.valueOf(curChannelIndex);
                    mLastChanNum[0] = mLastChanNum[1];
                    mLastChanNum[1] = curChannelIndex;

                    this.displayChanBar(curChannelIndex);

                    return true;
                }
            }
			break;
        case KeyEvent.KEYCODE_DPAD_CENTER:
            if (mSwitching){
                handler.removeCallbacks(mHideChanBarRunnable);
                handler.removeCallbacks(mPlayRunnable);
                handler.post(mPlayRunnable);
            }else{
                if (mChanNumNameLayout.getVisibility() == VISIBLE){
                    handler.removeCallbacks(mHideChanBarRunnable);
                    handler.post(mHideChanBarRunnable);
                }else if (getFullScreenplayFlag()){
                    mSelectedClassPos = 0;
                    mSelectedChanInAllPos = mClassList.get(mSelectedClassPos).getSelectedChanIndex();
					mChannelNum = String.valueOf(mSelectedChanInAllPos);
                    displayClassChanList(mClassList.get(mSelectedClassPos), 1);
					displayClassList(mClassList);

                    handler.removeCallbacks(mHideChanBarRunnable);
                    handler.post(mHideChanBarRunnable);

                    LinearLayout chanLayout3 = (LinearLayout) findViewById(R.id.chanbarLayout);
                    chanLayout3.removeAllViews();
                    mChanBarHided = true;
                }
            }
            break;
        case KeyEvent.KEYCODE_BACK:
        	if(getIppOpenflag()){
				Log.i("KEYCODE_BACK","com.um.dvb.STOP_IPPV");
				if(mInstance != null){
					Intent intent = new Intent("com.um.dvb.STOP_IPPV");
					mInstance.sendBroadcast(intent);
				}
				return true;
        	}else if(getAutoFeedCardFlag()){
        		if(mInstance != null){
					Intent intent = new Intent("com.um.dvb.STOP_AUTO_FEED_CARD");
					mInstance.sendBroadcast(intent);
        		}
				return true;
        	}
        	

        	if (!SharedPreferencesUtils.getPreferencesBoolean(mInstance, PREFERENCES_TAG, PREFERENCES_OFTEN_WATCH_EN, false, Context.MODE_PRIVATE)) {
            	return reCall();
        		//return super.onKeyDown(keyCode, event);
            }
            handler.removeCallbacks(mHideChanBarRunnable);
            handler.removeCallbacks(mPlayRunnable);
            handler.post(mHideChanBarRunnable);

        	if(mExitLayout == null)
        	{
        		mExitLayout = new ExitPopLayout(Dvbplayer_Activity.this);
        	}

			final ProgList progList = player.getCurModeProgList();
        	FrequentProg lastFreProg = null;
            if (mLastChanNum[0] != -1){
//                mShowChanNum = String.valueOf(mLastChanNum[0]);
            	int lastProgIndex = mLastChanNum[0];
                lastFreProg = new FrequentProg(Integer.parseInt(progList.list.get(lastProgIndex).get(ProgManage.PROG_ID)),
                        progList.list.get(lastProgIndex).get(ProgManage.PROG_NAME),
                        0,
                        0,
                        ProgManage.TVPROG);
                handler.post(mPlayRunnable);
            }

        	mExitLayout.setDataFromActivity(lastFreProg);//初始化数据，并把上一个节目传到ExitLayout中
        	mExitLayout.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					Prompt.getInstance().enablePromptShow(true);
					FrequentProg prog = mExitLayout.getPlayedProg();
					int exitMode = mExitLayout.getExitMode();
					if(exitMode == NORMAL_EXIT)
					{

					}else if(exitMode == CLICK_EXIT_BTN)
					{
						finish();
					}else if(exitMode == CLICK_ITEM_BTN)
					{
//						ProgList progList = player.getCurModeProgList();

		                int chanIndex = 0;
		                for (int i=0; i<progList.list.size(); i++) {
		                	String progId = progList.list.get(i).get(ProgManage.PROG_ID);
		                	if (progId.equals(Integer.toString(prog.getProgId()))) {
		                		chanIndex = i;
		                		break;
        					}
		                }
		                mShowChanNum = String.valueOf(chanIndex + 1);
		                handler.post(mPlayRunnable);
//		                player.playProg(progList, chanIndex);
		     
		                displayChanBar(chanIndex);
					}
				}
			});
            this.mExitLayout.showAtLocation(mPreview, Gravity.BOTTOM, 0, 0);
            Prompt.getInstance().enablePromptShow(false);
            return true;
        case KeyEvent.KEYCODE_DPAD_LEFT:
        	if (!mDisVolAdjust) {
                //if(mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC)) /*闈欓煶鐘舵€侊紝鑾峰彇闊抽噺涓?*/
                //{
                //    mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                //}
        		mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP);
			}
        	break;
        case KeyEvent.KEYCODE_DPAD_RIGHT:
        	if (!mDisVolAdjust) {
                if(mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC)) /*闈欓煶鐘舵€侊紝鑾峰彇闊抽噺涓?*/
                {
                    mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                }
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
			}
        	break;
        case KeyEvent.KEYCODE_MENU: //菜单键
        	
        	handler.removeCallbacks(mHideListWithNoBarRun);
            handler.post(mHideListWithNoBarRun);
            handler.removeCallbacks(mHideChanBarRunnable);
            handler.post(mHideChanBarRunnable);
            /*
        	if(menuContrl == null)
        	{
        		menuContrl = new PlayerMenuContrl(Dvbplayer_Activity.this, handler,
        				player.getPlayerServive());
        	}
            menuContrl.initMenuControl(getProg(player.getCurModeProgList().curProgIndex));
            this.menuContrl.showAtLocation(mPreview, Gravity.TOP, 0, 0);

        	Prompt.getInstance().enablePromptShow(false);
        	this.menuContrl.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					Prompt.getInstance().enablePromptShow(true);
				}
			});*/
        	showSettingPopup();
        	break;
        case KeyEvent.KEYCODE_PICTUREMODE: 
			Log.i(TAG, "zemin KeyEvent.KEY_PICTUREMODE press");
            pictureModeQuickKeyHandle();
/*			if (mFastSetToast.isShowing("picture_mode")) {
				picModeIndex++;
				if (picModeIndex >= InterfaceValueMaps.picture_mode.length) {
					picModeIndex = 0;
				}

				PictureInterface
						.setPictureMode(InterfaceValueMaps.picture_mode[picModeIndex][0]);
			} else {
				int mode = PictureInterface.getPictureMode();
				picModeIndex = Util.getIndexFromArray(mode,
						InterfaceValueMaps.picture_mode);

				Log.i(TAG, "mode=" + mode);
			}
			
			Log.i(TAG, "picModeIndex=" + picModeIndex);
			mFastSetToast.show("picture_mode",
					InterfaceValueMaps.picture_mode[picModeIndex][1]);*/
        	break;
        case KeyEvent.KEYCODE_SOUNDMODE:
            soundModeQuickKeyHandle();
/*			Log.i(TAG, "KeyEvent.KEY_SOUNDMODE press");
			if (mFastSetToast.isShowing("sound_mode")) {
				soundModeIndex++;
				if (soundModeIndex >= InterfaceValueMaps.sound_mode.length) {
					soundModeIndex = 0;
				}
				AudioInterface.getAudioManager().setSoundMode(
						InterfaceValueMaps.sound_mode[soundModeIndex][0]);
			} else {
				int sound_mode = AudioInterface.getAudioManager()
						.getSoundMode();
				soundModeIndex = Util.getIndexFromArray(sound_mode,
						InterfaceValueMaps.sound_mode);

				Log.i(TAG, "sound_mode" + sound_mode);
			}
			
			Log.i(TAG, "soundModeIndex=" + soundModeIndex);
			mFastSetToast.show("sound_mode",
					InterfaceValueMaps.sound_mode[soundModeIndex][1]);*/
        	break;
        case KeyEvent.KEYCODE_ZOOM:
            zoomQuickKeyHandle();
		/*	Log.i(TAG, "KeyEvent.KEY_ZOOM press");
			if( ProviderProgManage.TVPROG != ProviderProgManage.GetInstance(this).GetCurMode()) {
				break;
			}
			
			String w = SystemProperties.get("persist.sys.reslutionWidth");
        	String h = SystemProperties.get("persist.sys.reslutionHight");
	        if(!((w.equals("4096"))||(w.equals("3840"))&&(h.equals("2160")))){
	        		
	        	
				if (mFastSetToast.isShowing("zoom")) { // not dismiss
					zoomModeIndex++;
					if (zoomModeIndex >= InterfaceValueMaps.picture_aspect.length) {
						zoomModeIndex = 0;
					}
	
					PictureInterface.setAspect(
							InterfaceValueMaps.picture_aspect[zoomModeIndex][0],
							false);
	
				} else { // dismiss
					int aspect = PictureInterface.getAspect(); // display mode 4
																// 画面尺寸
					zoomModeIndex = Util.getIndexFromArray(aspect,
							InterfaceValueMaps.picture_aspect);
					// 根据需求，去除人物，放大1，放大2选项。当获取值为这三者时，设置值为ASPECT_16_9
					if ((aspect == EnumPictureAspect.ASPECT_ZOOM)
							|| (aspect == EnumPictureAspect.ASPECT_ZOOM1)
							|| (aspect == EnumPictureAspect.ASPECT_ZOOM2)
							||(aspect==EnumPictureAspect.ASPECT_AUTO)) {
						PictureInterface.setAspect(EnumPictureAspect.ASPECT_16_9,
								false);
						aspect = PictureInterface.getAspect();
						zoomModeIndex = Util.getIndexFromArray(aspect,
								InterfaceValueMaps.picture_aspect);
						Log.i(TAG,
								"aspect= ASPECT_ZOOM or ASPECT_ZOOM1 or ASPECT_ZOOM2  or ASPECT_AUTO ;set value = ASPECT_16_9; aspect="
										+ aspect);
					}
				}
				
				Log.i(TAG, "zoomModeIndex=" + zoomModeIndex);
				mFastSetToast.show("zoom",
						InterfaceValueMaps.picture_aspect[zoomModeIndex][1]);
	        }*/
        	break;    
        case KeyEvent.KEYCODE_AUDIO:
        	Log.v(TAG, "KEYCODE_AUDIO is presss");
        	DvbPlayer dvbPlayer = player.getPlayerServive().getMediaPlayer();
        	if (dvbPlayer == null) {
        		return true;
        	}
        	int curTrack = dvbPlayer.getAudioCurTrack();
    		int trackCount = dvbPlayer.getAudioTrackCount();
    		
        	if (mFastSetToast.isShowing("audio_track")) {
        		curTrack = (curTrack+1) % trackCount;
        		dvbPlayer.setAudioTrack(curTrack);
        	}
        	String trackStr = getResources().getString(R.string.audio_track);
        	mFastSetToast.show("audio_track", trackStr + "  [" + (curTrack+1) + "/" + trackCount + "]");
        	break;
        case KeyEvent.KEY_TRACK:
            TrackModeQuickKeyHandle();
/*        	Log.v(TAG, "KEY_TRACK is presss");
        	int tmode = HitvManager.getInstance().getAudio().getTrackMode();
        	Log.v(TAG, "track mode:" + tmode);
        	int index = 0;
        	for (int i=0; i<AUDIO_TRACK_MODE_STRING_MAP.length; i++) {
        		if (tmode == AUDIO_TRACK_MODE_STRING_MAP[i][0]) {
        			index = i;
        			break;
        		}
        	}
        	Log.v(TAG, "cur track mode index:" + index);
        	if (mFastSetToast.isShowing("audio_channel")) {
        		index = (index + 1) % AUDIO_TRACK_MODE_STRING_MAP.length;
        		UmtvManager.getInstance().getAudio().setTrackMode(AUDIO_TRACK_MODE_STRING_MAP[index][0]);
        	}
        	
        	mFastSetToast.show("audio_channel", AUDIO_TRACK_MODE_STRING_MAP[index][1]);*/
        	
        	break;
		case KeyEvent.KEY_EPG: // 指南 KEY_EPG key键未映射，注掉
			Log.i(TAG, "KEY_EPG is clicked");
			Log.d(TAG, "KeyEvent.KEY_EPG isListShowed = "+isListShowed);
			if(!isListShowed){
				mSelectedClassPos = 0;
				
				mSelectedChanInAllPos = mClassList.get(mSelectedClassPos)
						.getSelectedChanIndex();
				mChannelNum = String.valueOf(mSelectedChanInAllPos);
				displayClassChanList(mClassList.get(mSelectedClassPos), 1);
				displayClassList(mClassList);

				handler.removeCallbacks(mHideChanBarRunnable);
				handler.post(mHideChanBarRunnable);

				LinearLayout chanLayout3 = (LinearLayout) findViewById(R.id.chanbarLayout);
				chanLayout3.removeAllViews();

				displayWeekdayList();
				showEPG();
				isListShowed = true;
			}
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

    /**
     * 显示频道�
     * @param channelNum 需要显示的频道号，最�?�
     */
    private void showChannelNum(String channelNum){
    	handler.removeCallbacks(mHideListRunnable);
    	hideList();
    	handler.removeCallbacks(mHideChanBarRunnable);
		handler.post(mHideChanBarRunnable);
		
        switch (channelNum.length()){
            case 1:
                setNumToImage("0", mChanNumHundImageView);
                setNumToImage("0", mChanNumTenImageView);
                setNumToImage(channelNum.substring(0, 1), mChanNumOneImageView);
                break;
            case 2:
                setNumToImage("0", mChanNumHundImageView);
                setNumToImage(channelNum.substring(0, 1), mChanNumTenImageView);
                setNumToImage(channelNum.substring(1, 2), mChanNumOneImageView);
                break;
            case 3:
                setNumToImage(channelNum.substring(0, 1), mChanNumHundImageView);
                setNumToImage(channelNum.substring(1, 2), mChanNumTenImageView);
                setNumToImage(channelNum.substring(2, 3), mChanNumOneImageView);
                break;
            default:
                break;
        }

        ArrayList<HashMap<String, String>> list = player.getCurModeProgList().list;
        int index = Integer.parseInt(channelNum) - 1;
        String name = "";
        TextView chanName = (TextView) findViewById(R.id.channel_name_text_view);
        if (index >= 0 && index < list.size()) {
            name = list.get(index).get(ProviderProgManage.PROG_NAME);
            chanName.setText(name);
        } else {
            chanName.setText(name);
        }
    }

    private void setNumToImage(String numBit, ImageView imageBit){
        switch (Integer.parseInt(numBit)){
            case 0:
                imageBit.setImageResource(R.drawable.num_0);
                break;
            case 1:
                imageBit.setImageResource(R.drawable.num_1);
                break;
            case 2:
                imageBit.setImageResource(R.drawable.num_2);
                break;
            case 3:
                imageBit.setImageResource(R.drawable.num_3);
                break;
            case 4:
                imageBit.setImageResource(R.drawable.num_4);
                break;
            case 5:
                imageBit.setImageResource(R.drawable.num_5);
                break;
            case 6:
                imageBit.setImageResource(R.drawable.num_6);
                break;
            case 7:
                imageBit.setImageResource(R.drawable.num_7);
                break;
            case 8:
                imageBit.setImageResource(R.drawable.num_8);
                break;
            case 9:
                imageBit.setImageResource(R.drawable.num_9);
                break;
            default:
                break;
        }
        mChanNumNameLayout.setVisibility(VISIBLE);
    }

	public boolean onKeyUp(int keyCode, KeyEvent event){
		//处理强制切台按键禁止
		if(Lock_status == 1){
			return true;
		}

		switch (keyCode){

		case KeyEvent.KEYCODE_MENU:
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_BACK:		
			return true;

        case KeyEvent.KEYCODE_CHANNEL_UP:
        case KeyEvent.KEYCODE_CHANNEL_DOWN:

		case KeyEvent.KEYCODE_DPAD_UP:
			// shift = -1;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			// shift = 1;
			return true;
		}
		return super.onKeyUp(keyCode, event);

	}

	private void hideStatusbar(){
        Intent _Intent = new Intent();
        _Intent.setComponent(new ComponentName("com.android.systemui",
                "com.android.systemui.SystemUIService"));
        _Intent.putExtra("starttype", 1);
        startService(_Intent);
    }

    private void showStatusbar(){
        Intent _Intent = new Intent();
        _Intent.setComponent(new ComponentName("com.android.systemui",
                "com.android.systemui.SystemUIService"));
        _Intent.putExtra("starttype", 0);
        startService(_Intent);
    }

	class Adapter extends SimpleAdapter{
		ProgList mList = null;

		Adapter(Context context, ProgList proglist, int resource,
				String[] from, int[] to){
			super(context, proglist.list, resource, from, to);

			mList = proglist;
		}

	}

    public class ChannelListAdapter extends BaseAdapter {
        private Context mContext;
        private ProgList mProgList;

        public ChannelListAdapter(Context context, ProgList progList){
            mContext = context;
            mProgList = progList;
        }

        public void setListData(ProgList progList){
        	mProgList = progList;
//        	Log.i(TAG, "更新数据，等待刷新");
        }

        class ViewHolder{
            TextView ProIndex;
            TextView ProName;
        }

        @Override
        public int getCount(){
            return mProgList.list.size();
        }

        @Override
        public Object getItem(int position){
            return mProgList.list.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder viewHolder;
            if (convertView == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.dvbplayer_list, null);
                viewHolder.ProName = (TextView) convertView.findViewById(R.id.Dvbplayer_TextView_01);
                viewHolder.ProIndex = (TextView) convertView.findViewById(R.id.Dvbplayer_TextView_00);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            HashMap<String, String> map = mProgList.list.get(position);
            viewHolder.ProName.setText(map.get("name"));
            viewHolder.ProIndex.setText(String.format("%03d", position+1));
            return convertView;
        }
    }

	private class DvbPlayerBroadcastReceive extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent){
				Log.v(TAG,"receive UMSG_DVB_EPG_PF_EVENTS_UPDATE");
				ProgList proglist = player.getCurModeProgList();
				int curChannel = proglist.curProgIndex;
				if (!mChanBarHided) {
					Log.v(TAG, ">>>DvbPlayerBroadcastReceive");
				    Dvbplayer_Activity.this.displayChanBar(curChannel);
                }
		}

    }
    private int getIndexById(ProgList proglist, int progId) {
        ArrayList<HashMap<String, String>> list = proglist.list;
        for (HashMap<String, String> item : list) {
            if (progId == Integer.parseInt(item.get(ProgManage.PROG_ID))) {
                return list.indexOf(item);
            }
        }
        return 0;
    }
	

	private class DvbCaLockServiceReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("com.um.umdvb.UMSG_DVB_CA_LOCK_SERVICE")){
				Bundle bundle = intent.getExtras();
				if(null == bundle){
					Log.i(TAG, "bundle=null");
					return;
				}
				mChannelNum = String.valueOf(progIdToChanNum(bundle.getInt("progId")));
				mShowChanNum = mChannelNum+"";
				handler.post(mPlayRunnable);
				Lock_status = 1;
				forcePlayChannel = 1;
			}
			else if (intent.getAction().equals("com.um.umdvb.UMSG_DVB_CA_UNLOCK_SERVICE")){
				Lock_status = 0;
			}
			else if (intent.getAction().equals("com.um.umdvb.UMSG_DVB_DVTCA_LOCK_SERVICE")){
				Log.i("Dvbplayer_Activity", "com.um.umdvb.UMSG_DVB_DVTCA_LOCK_SERVICE");
				if(0 == Lock_status){
					int curChannelIndex = player.getCurChannelIndex(player.getCurModeProgList());
					dvtChannelNum = String.valueOf(curChannelIndex + 1);
					Log.i("Dvbplayer_Activity", "dvtChannelNum:"+dvtChannelNum);
				}

				Bundle bundle = intent.getExtras();
				mChannelNum = String.valueOf(progIdToChanNum(bundle.getInt("progId")));
				Log.i("Dvbplayer_Activity", "mChannelNum:"+mChannelNum);
				mShowChanNum = mChannelNum+"";
				handler.post(mPlayRunnable);

				Lock_status = 1;
				forcePlayChannel = 1;
				SystemProperties.set("runtime.unionman.disablekey", "1");

				final int duration = bundle.getInt("duration");
				Log.i("Dvbplayer_Activity", "duration:"+duration);

				handler.removeCallbacks(cancelUrgencyBroadcastRunnable);
				handler.postDelayed(cancelUrgencyBroadcastRunnable, duration*1000);

			}
			else if (intent.getAction().equals("com.um.umdvb.UMSG_DVB_DVTCA_UNLOCK_SERVICE"))
			{
				Lock_status = 0;
				Log.i("Dvbplayer_Activity,receive", "com.um.umdvb.UMSG_DVB_DVTCA_UNLOCK_SERVICE");
				SystemProperties.set("runtime.unionman.disablekey", "0");

				mChannelNum = dvtChannelNum;
				mShowChanNum = mChannelNum+"";
				handler.post(mPlayRunnable);
			}
		}
	}

	private int progIdToChanNum(int progId){
		ProgList proglist = player.getSpecifyModeProgList(Play_Last_Prog.GetInstance().lastProgMode);
		int listProgId = -1;
		int i;

		Log.i("Dvbplayer_Activity", "proglist.list.size(): "+proglist.list.size());
		for (i = 0; i < proglist.list.size(); i++){
			listProgId = Integer.parseInt(proglist.list.get(i).get(ProgManage.PROG_ID));
			Log.i("Dvbplayer_Activity", "listProgId: "+listProgId);
			if (listProgId==progId){
				return (i+1);
			}
		}
		return (progId + 1);
	}

	Runnable cancelUrgencyBroadcastRunnable = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i("Dvbplayer_Activity", "com.um.umdvb.UMSG_DVB_DVTCA_UNLOCK_SERVICE");
			Intent intent = new Intent();
			intent.setAction("com.um.umdvb.UMSG_DVB_DVTCA_UNLOCK_SERVICE");
			mInstance.sendBroadcast(intent);
		}
	};

	private void checkCaIconStatus(){
		Log.i("[checkCaIconStatus]", "com.um.dvb.CHECK_EMAIL");
		if(null == mInstance){
			Log.i(TAG, "mInstance=null");
			return;
		}
		Intent intent = new Intent();

		if(null == intent){
			Log.i(TAG, "intent=null");
			return;
		}

		intent.setAction("com.um.dvb.CHECK_EMAIL");
		mInstance.sendBroadcast(intent);

		intent.setAction("com.um.dvb.CHECK_DETITLE");
		mInstance.sendBroadcast(intent);

		intent.setAction("com.um.dvb.CHECK_PREVIEW");
		mInstance.sendBroadcast(intent);

		int fingerShowTick = Integer.parseInt(SystemProperties.get("runtime.um.dvtFingerShowTick",  "0"));
		Log.i(TAG,"fingerShowTick:" +fingerShowTick);
		Log.i(TAG,"System.currentTimeMillis()/1000:" +(System.currentTimeMillis()/1000));

		if(fingerShowTick > (System.currentTimeMillis()/1000)){
			int duration = fingerShowTick - (int)(System.currentTimeMillis()/1000);
			Log.v(TAG, "duration:" +duration);
			SharedPreferences preferences = mInstance.getSharedPreferences("CA_FINGERID",Context.MODE_WORLD_READABLE);
			Editor editor = preferences.edit();
			editor.putInt("CA_DURATION", duration);
			editor.commit();

			intent.setAction("com.um.dvb.DVTCHECK_FINGER");
			mInstance.sendBroadcast(intent);
		}else{
			fingerShowTick = 0;
			SharedPreferences preferences = mInstance.getSharedPreferences("CA_FINGERSHOWTICK",Context.MODE_WORLD_READABLE);
			Editor editor = preferences.edit();
			editor.putLong("CA_FINGERSHOWTICK", fingerShowTick);
			editor.commit();
		}
	}

	public static boolean getCloseIppFlag(){
		return isCloseIppDialog;
	}

	public static void setCloseIppFlag(boolean flag){
		isCloseIppDialog = flag;
	}

	public static boolean getIppOpenflag(){
		return isIppOpenFlag;
	}

	public static void setIppOpenFlag(boolean flag){
		isIppOpenFlag = flag;
	}

	public static boolean getAutoFeedCardFlag(){
		return autoFeedCardFlag;
	}

	public static void setAutoFeedCardFlag(boolean flag){
		autoFeedCardFlag = flag;
	}

	private ChannleClassification allToOneClass() {
        ProviderProgManage progManager = ProviderProgManage.GetInstance(this);
        ProgList chanList = Dvbplayer_Activity.this.player.getCurModeProgList();
        ChannleClassification classification = new ChannleClassification(getString(R.string.all_channels));

        for (int i = 0; i < chanList.list.size(); i++) {
            String chanName = chanList.list.get(i).get("name").toString();
        	classification.addChan(chanName, i);
        }
//        chanClassificaton.addChan("测试频道--NONG", chanList.list.size());
        mClassList.add(classification);
        return classification;
    }
	
	private void batCategory() {
        ProviderProgManage progManager = ProviderProgManage.GetInstance(this);
        ProgList chanList = Dvbplayer_Activity.this.player.getCurModeProgList();
        HashMap<String,ProgList> batMap= progManager.getDvbBatCategoryProgInfo();
		Iterator iter = batMap.entrySet().iterator();
		iter = batMap.entrySet().iterator();
		while (iter.hasNext()) 
		{
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			String cateName = (String)entry.getKey().toString();
			ProgList progList = (ProgList)entry.getValue();
			if (progList == null || progList.list == null || progList.list.size() <= 0) {
				continue;
			}
			ChannleClassification classification = new ChannleClassification(cateName);
			for(int i=0;i<progList.list.size();i++)
			{
		        for (int j = 0; j < chanList.list.size(); j++) 
		        {
		        	if(chanList.list.get(j).get("name").toString().equals( progList.list.get(i).get("name").toString()))
		        	{
			        	String chanName = progList.list.get(i).get("name").toString();
			        	classification.addChan(chanName, j);
		        	}
		        }
			}
			if(progList.list.size()>0)
			{
			 mClassList.add(classification);
			}
		}
    }
	
	private ChannleClassification collectedToAClass() {
        ProviderProgManage progManager = ProviderProgManage.GetInstance(this);
        ProgList allChanList = Dvbplayer_Activity.this.player.getCurModeProgList();
        ProgList chanList = player.getSpecifyModeProgList(ProgManage.FAVPROG);
        ChannleClassification classification = new ChannleClassification(getString(R.string.my_channels));

        if (mClassList.get(mClassList.size()-1).getClassificationName().equals(getString(R.string.my_channels))) {
        	mClassList.set(mClassList.size()-1, classification);
        } else {
        	mClassList.add(classification);
        }
        
        if(ProviderProgManage.GetInstance(mContext).GetCurMode()!=ProgManage.RADIOPROG)
        {
			for (int i = 0; i < chanList.list.size(); i++) {
				for (int j = 0; j < allChanList.list.size(); j++) 
				{
					if(allChanList.list.get(j).get("name").toString().equals( chanList.list.get(i).get("name").toString()))
					{
						String chanName = chanList.list.get(i).get("name").toString();
						classification.addChan(chanName, j);
					}
				}
			}
        }

        return classification;
	}

	private void initHDTVClass() {
		Log.v(TAG, "initHDTVClass begin.");
		ProviderProgManage progManager = ProviderProgManage.GetInstance(this);
		
		if (progManager.GetCurMode() == ProgManage.RADIOPROG) {
			return;
		}
		
		ProgList allChanList = Dvbplayer_Activity.this.player
				.getCurModeProgList();
		
		if (allChanList == null || allChanList.list == null || allChanList.list.size() <= 0) {
			return ;
		}
		
		ChannleClassification classification = new ChannleClassification(
				getString(R.string.hdtv_channels));

		for (int i = 0; i < allChanList.list.size(); i++) {
			String progName = allChanList.list.get(i).get("name");
			if (progName != null && (progName.contains("高清") 
					|| progName.contains("HD") || progName.contains("3D"))) {
				classification.addChan(progName, i);
				//Log.v(TAG, "find HDTV: " + progName + ", " + i);
			}
		}
		
		if (!classification.getChanList().isEmpty()) {
			mClassList.add(classification);
		}
		Log.v(TAG, "initHDTVClass end.");
		return;
	}
	
	private ChannleClassification refreshOftenWatchTv() {
		Log.v(TAG, "refreshOftenWatchTv begin.");
		
		ChannleClassification classification = null;
		
		String calssName = getString(R.string.ofen_watch);
		for (ChannleClassification cl : mClassList) {
			if (cl.getClassificationName().equals(calssName)) {
				classification = cl;
				break;
			}
		}
		
		if (classification == null) {
			classification = new ChannleClassification(calssName);
			mClassList.add(classification);
		}
		
		classification.clear();
		
		ProviderProgManage progManage = ProviderProgManage.GetInstance(this);
		
		ArrayList<FrequentProg> list = mFrequentProgManager.getAll(1, progManage.GetCurMode());
		if (list != null) {
			ProgList allChanList = Dvbplayer_Activity.this.player
					.getCurModeProgList();
			
			for (FrequentProg prog : list) {
				int progId = prog.getProgId();

				for (int i = 0; i < allChanList.list.size(); i++) {
					int ProgIdTmp = Integer.valueOf(allChanList.list.get(i).get("progid"));
					if (ProgIdTmp == progId) {
						String progName = allChanList.list.get(i).get("name");
						classification.addChan(progName, i);
						//Log.v(TAG, "find Often watch: " + progName + ", " + i);
						break;
					}
				}
			}
		}
		
		Log.v(TAG, "refreshOftenWatchTv end.");
		
		return classification;
	}
	
	private void classifyChannels(){
        ProviderProgManage progManager = ProviderProgManage.GetInstance(this);
        ProgList chanList = Dvbplayer_Activity.this.player.getCurModeProgList();

        for (int i = 0; i < chanList.list.size(); i++) {
            String bouquets[] = getProg(i).getBouquets();
            if (bouquets == null) {
            	return;
            }
            for (int j = 0; j < bouquets.length; j++) {
                if (bouquets[j] == null || bouquets[j].equals("null") || bouquets[j].equals("")) {
                    continue;
                }

                ChannleClassification chanClassification = null;
                for (ChannleClassification chanClass : mClassList) {
                    if (bouquets[j].equals(chanClass.getClassificationName())) {
                        chanClassification = chanClass;
                        break;
                    }
                }

                if (chanClassification == null) {
//                    Log.v(TAG, "new bouquet:" + bouquets[j]);
                    chanClassification = new ChannleClassification(bouquets[j]);
                    mClassList.add(chanClassification);
                }
                String chanName = chanList.list.get(i).get("name").toString();
                chanClassification.addChan(chanName, i);
//                Log.i(TAG, "给分类: "+chanClassification.getClassificationName()+" 添加频道: "+chanName+" 所有频道中的索引: "+i);
            }
        }
	}

	private void displayClassChanList(final ChannleClassification classification, int animType){
		Log.v(TAG, "displayClassChanList");
        handler.removeCallbacks(mHideListRunnable);
        handler.postDelayed(mHideListRunnable, PROG_LIST_DURATION);
		inflateList(animType, classification.getClassificationName());
		mClassChanListView = (ListView) findViewById(R.id.channel_list_view);
		ProgList classifiedProgList = new ProgList();
		classifiedProgList.list = classification.getChanList();
        mChannelAdapter = new ChannelListAdapter(Dvbplayer_Activity.this, classifiedProgList);
        mClassChanListView.setAdapter(mChannelAdapter);
		mClassChanListView.setFocusable(true);
		mClassChanListView.requestFocus();
        Log.d("list.setSelection", " displayClassChanList");
		mClassChanListView.setSelection(Integer.parseInt(mChannelNum));
		
		mDisVolAdjust = true;
		mClassChanListView.setOnKeyListener(new OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if (event.getAction() == KeyEvent.ACTION_DOWN){
                    handler.removeCallbacks(mHideListRunnable);
                    handler.postDelayed(mHideListRunnable, PROG_LIST_DURATION);
                    if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9 || KeyEvent.KEYCODE_RECALL == keyCode){
                        hideList();
                    }
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_UP:
//                            Log.i(TAG, "列表到最顶部："+mClassChanListView.getSelectedItemId());
                            if (mClassChanListView.getSelectedItemId() == 0){
                                mClassChanListView.setSelection(mClassChanListView.getCount()-1);
//                                Log.i(TAG, "跳到列表最底部"+(mClassChanListView.getCount()-1));
                                return true;
                            }
//                            Log.i(TAG, "分类大小:"+mClassList.size());
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
//                            Log.i(TAG, "列表到最底部："+mClassChanListView.getSelectedItemId());
                            if (mClassChanListView.getSelectedItemId() == mClassChanListView.getCount() -1) {
                                mClassChanListView.setSelection(0);
                                return true;
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                            handler.removeCallbacks(mHideListRunnable);
                            handler.post(mHideListRunnable);
//                            Log.i(TAG, "隐藏节目频道列表");
                            break;
                        case KeyEvent.KEYCODE_BACK:
                            handler.removeCallbacks(mHideListRunnable);
                            handler.post(mHideListRunnable);
//							if (mLastChanNum[0] != -1)
//                            {
//                                mShowChanNum = String.valueOf(mLastChanNum[0]);
//                                handler.post(mPlayRunnable);
//                            }
                            Log.i(TAG, "频道列表中，按下返回键");
                            return true;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
//                            Log.i(TAG, "按下向右键");
                                displayWeekdayList();
                                showEPG();
                            break;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
//                            Log.i(TAG, "按下向左键");
                            ChannleClassification classification = mClassList.get(mSelectedClassPos);
                            classification.setSelectedChanIndex(mSelectedChanPos);
                            mLeftArrowImageView.setVisibility(INVISIBLE);;
                            Animation animLeftIn = new TranslateAnimation(0f, 160f, 0f, 0f); 
                            animLeftIn.setDuration(200);
                            animLeftIn.setAnimationListener(new AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
                                }
								@Override
								public void onAnimationRepeat(Animation animation) {
								}
								@Override
								public void onAnimationEnd(Animation animation) {
									mListsLayout.clearAnimation();
									mListsLayout.setX(0f);
								}
							});
                            mListsLayout.startAnimation(animLeftIn);
                            mClassListView.setSelection(mSelectedClassPos);
                            mClassListView.requestFocus();
                            return true;
                        case KeyEvent.KEYCODE_CHANNEL_DOWN:
                        case KeyEvent.KEYCODE_CHANNEL_UP:
                            hideList();
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

		mClassChanListView.setOnItemSelectedListener(new OnItemSelectedListener() {
            View preView;
            TextView tv1;
            TextView tv2;
            TextView preTv1;
            TextView preTv2;

            public void onItemSelected(AdapterView<?> arg0, View view, int arg2, long arg3) {
                mSelectedChanPos = arg2;
                ChannleClassification classification = mClassList.get(mSelectedClassPos);
                classification.setSelectedChanIndex(mSelectedChanPos);
                mSelectedChanInAllPos = classification.getChanIndexInAll(mSelectedChanPos);
                mChannelNum = String.valueOf(mSelectedChanInAllPos);
                Log.v(TAG, "select: " + arg2);
                if (mIsActivityPaused) {
                    Log.v(TAG, "activity is already paused");
                    return;
                }
                player.playProg(Dvbplayer_Activity.this.player.getCurModeProgList(), mSelectedChanInAllPos);
                mLastChanNum[0] = mLastChanNum[1];
                mLastChanNum[1] = arg2;
//                Log.i(TAG, "频道: "+classification.getChanName(arg2)+" 在分类: "+classification.getClassificationName()+" 中的索引："+arg2+"  在所有频道中的索引: "+chanIndex);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
		
		mClassChanListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                View selView = mClassChanListView.getSelectedView();
                if (hasFocus) {
                    mLeftArrowImageView.setVisibility(VISIBLE);
                    mRightArrowImageView.setVisibility(VISIBLE);
                    if (selView != null) {
                        selView.setBackgroundResource(android.R.color.transparent);
                    }
                } else {
                    if (selView != null) {
                        selView.setBackgroundResource(R.drawable.list_item_sel);
                    }
                }
            }
        });

        setFullScreenplayFlag(false);
	}
	
	private void refreshClassChanList(ChannleClassification classification){
//		Log.i(TAG, "更新到的分类："+classification.getClassificationName());
//		classification.showChannels();
		ProgList progList = new ProgList();
		progList.list = classification.getChanList();
		mChannelAdapter.setListData(progList);
		mChannelAdapter.notifyDataSetChanged();
	}
	
	private void displayClassList(List<ChannleClassification> classificationList){
		Log.v(TAG, "displayClassList");
		mClassListView = (ListView) mListsLayout.findViewById(R.id.classified_list_view);
		List<Map<String, String>> classifiedMapList = new ArrayList<Map<String,String>>();
		for (ChannleClassification chanClass : classificationList){
			Map<String, String> map = new HashMap<String, String>();
			map.put("classifiedName", chanClass.getClassificationName());
			classifiedMapList.add(map);
        }
		SimpleAdapter adapter = new SimpleAdapter(Dvbplayer_Activity.this,
				classifiedMapList,
				R.layout.classified_list_item,
				new String[]{"classifiedName"}, 
				new int[]{R.id.classified_name_text_view});
		mClassListView.setAdapter(adapter);
		mClassListView.setOnKeyListener(new OnKeyListener(){
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event){
				handler.removeCallbacks(mHideListRunnable);
		        handler.postDelayed(mHideListRunnable, PROG_LIST_DURATION);
				if (event.getAction() == KeyEvent.ACTION_DOWN){
//					collectedToAClass();
					switch (keyCode){
						case KeyEvent.KEYCODE_DPAD_UP:
							if (mClassListView.getSelectedItemPosition() == 0){
//								mClassListView.setSelection(mClassListView.getCount()-1);
								return true;
							}
							break;
						case KeyEvent.KEYCODE_DPAD_DOWN:
							if (mClassListView.getSelectedItemPosition() == mClassListView.getCount() - 1){
//								mClassListView.setSelection(0);
								return true;
							}
							break;
						case KeyEvent.KEYCODE_DPAD_LEFT:
							return true;
						case KeyEvent.KEYCODE_DPAD_RIGHT:
							if (mClassChanListView.getCount() <= 0) {
								return true;
							}
							if (mSelectedChanPos >= mClassChanListView.getCount()) {
                            	mSelectedChanPos = 0;
                            }
                            mClassChanListView.setSelection(mSelectedChanPos);
    						mClassChanListView.requestFocus();
                            ChannleClassification classification = mClassList.get(mSelectedClassPos);
                            classification.showChannels();
                            mSelectedChanInAllPos = classification.getChanIndexInAll(mSelectedChanPos);
//                            Log.i(TAG, "分类列表，频道在全部频道中的索引："+mSelectedChanInAllPos);
                            mChannelNum = String.valueOf(mSelectedChanInAllPos);
                            player.playProg(Dvbplayer_Activity.this.player.getCurModeProgList(), mSelectedChanInAllPos);
                            
							Animation animation = new TranslateAnimation(0f,
									-140f, 0f, 0f);
							animation.setDuration(200);
							animation.setAnimationListener(new AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
								}

								@Override
								public void onAnimationRepeat(Animation animation) {
								}
								
								@Override
								public void onAnimationEnd(Animation animation) {
									mListsLayout.clearAnimation();
									mListsLayout.setX(-160f);
								}
							});
							mListsLayout.startAnimation(animation);
							return true;
                        case KeyEvent.KEYCODE_BACK:
                            handler.removeCallbacks(mHideListRunnable);
                            handler.post(mHideListRunnable);
                            return true;
						default:
							break;
					}
				}
				return false;
			}
		});
		
		mClassListView.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mSelectedClassPos = arg2;
//				Log.i(TAG, "选中分类："+mSelectedClassPos);
				ChannleClassification classification = mClassList.get(mSelectedClassPos);
				if (classification.getClassificationName().equals(getString(R.string.my_channels))) {
					refreshClassChanList(collectedToAClass());
				} if (classification.getClassificationName().equals(getString(R.string.ofen_watch))) {
					refreshClassChanList(refreshOftenWatchTv());
				} else {
					refreshClassChanList(mClassList.get(mSelectedClassPos));
				}
				int selectedChan = classification.getSelectedChanIndex();
				for (int i = 0; i < mClassChanListView.getChildCount(); i++) {
					mClassChanListView.getChildAt(i).setBackgroundResource(android.R.color.transparent);
//					if (i != selectedChan) {
//						mClassChanListView.getChildAt(i).setBackgroundResource(android.R.color.transparent);
//					} else {
//						mClassChanListView.getChildAt(i).setBackgroundResource(R.drawable.list_item_sel);
//					}
				}
				mClassChanListView.setSelection(selectedChan);
			}
			@Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

	private void displayWeekdayList(){
		Log.v(TAG, "displayWeekdayList");
		Prog prog = getProg(mSelectedChanInAllPos);
		Epg_LocalTime epgLocalTime = new Epg_LocalTime();
		prog.prog_get_localtime(epgLocalTime);
		final int curDay = epgLocalTime.weekday;
		String weekday[] = new String[]{
				getString(R.string.sunday),
				getString(R.string.monday),
				getString(R.string.tuesday),
				getString(R.string.wednesday),
				getString(R.string.thursday),
				getString(R.string.friday),
				getString(R.string.saturday)};
		List<String> weekdayTmp = new ArrayList<String>();
		ArrayList<Map<String, String>> weekdayList = new ArrayList<Map<String,String>>();
		for (int i = 0; i < 7; i++) {
			weekdayTmp.add(weekday[(i+curDay)%7]);
		}
		mWeekdayListView = (ListView) mListsLayout.findViewById(R.id.weekday_list_view);
        mWeekdayListView.setSelection(0);
        mWeekdayListView.setSelected(true);
        mWeekdayListView.requestFocus();
		for (String day : weekdayTmp) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("weekday", day);
			weekdayList.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(Dvbplayer_Activity.this,
				weekdayList, 
				R.layout.week_list_item, 
				new String[]{"weekday"}, 
				new int[]{R.id.weekday_text_view});
		mWeekdayListView.setAdapter(adapter);
		mWeekdayListView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
		        handler.removeCallbacks(mHideListRunnable);
		        handler.postDelayed(mHideListRunnable, PROG_LIST_DURATION);
				if (event.getAction() == KeyEvent.ACTION_DOWN){
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_UP:
						if (mWeekdayListView.getSelectedItemPosition() == 0) {
//							mWeekdayListView.setSelection(6);
							return true;
						}
						break;
					case KeyEvent.KEYCODE_DPAD_DOWN:
						if (mWeekdayListView.getSelectedItemPosition() == 6) {
//							mWeekdayListView.setSelection(0);
							return true;
						}
						break;
					case KeyEvent.KEYCODE_DPAD_LEFT:
						mClassChanListView.setSelection(mSelectedChanPos);
						mClassChanListView.requestFocus();
						Animation animLeftOut = AnimationUtils.loadAnimation(Dvbplayer_Activity.this, R.anim.anim_translate_left_out);
                        animLeftOut.setDuration(300);
                        animLeftOut.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {
							}
							@Override
							public void onAnimationRepeat(Animation animation) {
							}
							@Override
							public void onAnimationEnd(Animation animation) {
								mRightLayout.setVisibility(INVISIBLE);
								EpgManager.getInstance().setEpgShowFlag(false);
							}
						});
                        mRightLayout.startAnimation(animLeftOut);
						return true;
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						Log.v(TAG, "change focus to scheduel list.");
                        ListView epgDetailListView = EpgManager.getInstance().getListView();
                        if (epgDetailListView != null) {
                            if (epgDetailListView.getCount() <= 0) {
                                return true;
                            }
                            mWeekdayListView.getSelectedView().setBackgroundResource(R.drawable.list_item_sel);
                            epgDetailListView.setSelection(0);
                            epgDetailListView.requestFocus();
                        }
						return true;
                    case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    case KeyEvent.KEYCODE_CHANNEL_UP:
                        hideList();
                        return false;
                    case KeyEvent.KEYCODE_BACK:
                        handler.removeCallbacks(mHideListRunnable);
                        handler.post(mHideListRunnable);
                        return true;
					default:
						break;
					}
				}
				return false;
			}
		});
		
		mWeekdayListView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Log.v(TAG, "weekday list selected: " + arg2);
				Epg_LocalTime epgLocalTime = new Epg_LocalTime();
		        (new Prog(DVB.getInstance())).prog_get_localtime(epgLocalTime);
		        int curDay = epgLocalTime.weekday;
				EpgManager.getInstance().displayEpgList(mSelectedChanInAllPos, arg2);
				mWeekdayListView.requestFocus();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	@Override
	public void OnMessage(Message msg) {
		if (msg.what==Prompt.STATUS_TUNER) {
			doSignalHandle();
		} else if (msg.what == Prompt.STATUS_PROG){
			if (msg.arg1 == Prompt.subtype.UMSG_DVB_DB_PROGRAM_BASIC_INFO_CHANGE.ordinal()) {
				Log.v(TAG, "cur played program is changed, replay it...");
				Toast.makeText(this, R.string.prog_change_notify, 3*1000).show();
				if (player != null) {
					player.stopPlay();
					playLastProg();
				}
			}
		} else {
			Prompt.getInstance().handleMessage(msg);
		}
	}

	private void doSignalHandle() {
		getPowerDownDialogFlag();
		if (!isWindOK && mPowerDownDialogOpenFlag == false){
			return;
		}
		
		boolean flag = 	SystemProperties.get("sys.dvb.tuner.status", "nosignal").equals("strong");
		Message msg = new Message();
		msg.what = Prompt.STATUS_TUNER;
		if (flag)/*signal surpport*/{
			stopScreenSaver();
			stopNoSignalPowerdownMonitor();
			msg.arg1 = Prompt.STRONG_SIGNAL;
		} else {
			startScreenSaver();
			startNoSignalPowerdownMonitor();
			msg.arg1 = Prompt.NO_SIGNAL;
		}
		Prompt.getInstance().handleMessage(msg);
	}
	
	private void startScreenSaver() {
		if (screenSaverMode==0) {
			startSignalTxt();
		} else {
			showScreenSaver();
		}
	}
	
	private void stopScreenSaver() {
		if (screenSaverMode==0) {
			stopSignalTxt();
		} else {
			closeScreanSaver();
		}
	}
	
    private void stopSignalTxt(){
        if (noSinalTxt != null) {
        	if (noSinalTxt.getVisibility() == View.VISIBLE){
        		noSinalTxt.setVisibility(View.INVISIBLE);
                stopScreenTxtMoving();
        	}
        }
    }
    
    private void startSignalTxt(){
        if (noSinalTxt != null) {
        	if (noSinalTxt.getVisibility() == View.INVISIBLE){
        		noSinalTxt.setVisibility(View.VISIBLE);
        		noSinalTxt.requestLayout();
                startScreenTxtMoving();
        	}
        }
    }
    
	private void getPowerDownDialogFlag(){
		try {
			Context systemServerAppContext;
			systemServerAppContext = createPackageContext("cn.com.unionman.umtvsystemserver", Context.CONTEXT_IGNORE_SECURITY);
	       	SharedPreferences sharedata = systemServerAppContext.getSharedPreferences("powerDownDialog", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE
	                  + Context.MODE_MULTI_PROCESS);
	       	mPowerDownDialogOpenFlag = sharedata.getBoolean("powerDownDialogFlag", false);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} 
    }
    
    private void startNoSignalPowerdownMonitor(){
		Log.i(TAG, "startNoSignalPowerdownMonitor " + isNosignalPowerdownMonitorStarted);
		
		if (mNoSignalPD == 1){
			if (!isNosignalPowerdownMonitorStarted) {
				handler.sendEmptyMessageDelayed(NOSIGNAL_POWERDOWN, 300000);
				isNosignalPowerdownMonitorStarted = true;
			}
		}
	}
	
	private void stopNoSignalPowerdownMonitor() {
		Log.i(TAG, "stopNoSignalPowerdownMonitor " + isNosignalPowerdownMonitorStarted);

		if (mNoSignalPD == 1){
			if (isNosignalPowerdownMonitorStarted) {
				handler.removeMessages(NOSIGNAL_POWERDOWN);
				isNosignalPowerdownMonitorStarted = false;
				
				getPowerDownDialogFlag();
				Log.i(TAG, "mPowerDownDialogOpenFlag " + mPowerDownDialogOpenFlag);
				if (mPowerDownDialogOpenFlag == true){
					Intent intent3 = new Intent();
					intent3.setAction("android.intent.action.NOSIGNAL_POWERDOWN_BROADCAST");
					intent3.putExtra("mode", "close");
					mContext.sendBroadcast(intent3);
				}
			}
		}
	}
	
	private void noSignalPowerdownMonitorKeyHandle(){
		
		stopNoSignalPowerdownMonitor();
		boolean flag = 	SystemProperties.get("sys.dvb.tuner.status", "nosignal").equals("strong");
		if (((noSinalTxt != null) && (noSinalTxt.getVisibility() == View.VISIBLE))
				||((screenSaverImg != null) && (screenSaverImg.getVisibility() == View.VISIBLE))
				||(flag == false)){
			startNoSignalPowerdownMonitor();
		}
	}
	
	private void windowFocusChangeHandle(boolean focus){
		
		if (focus){
			handler.postDelayed(mDoSingalRunnable, DO_SINGAL_DURATION);
		}else{
		
			getPowerDownDialogFlag();
			if (mPowerDownDialogOpenFlag == false){
				stopNoSignalPowerdownMonitor();
			}
			stopScreenSaver();
			if (screenSaverMode == 1){
				handler.removeMessages(SIGNAL_CHECK);
				closeScreanSaver();
			}
		}
	}
	
	private BroadcastReceiver systemEventMonitorsReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			Log.d(TAG, "-------->systemEventMonitorsReceiver---->");
			if (intent.getAction().equals(RESET_MONITORS)) {
				resetMonitors();
			} 
		}
	};
	
	private void resetMonitors() {
		Log.d(TAG, "-------->resetMonitors---->");
		boolean flag = 	SystemProperties.get("sys.dvb.tuner.status", "nosignal").equals("strong");
		if (((noSinalTxt != null) && (noSinalTxt.getVisibility() == View.VISIBLE))
				||((screenSaverImg != null) && (screenSaverImg.getVisibility() == View.VISIBLE))
				|| (flag == false)){
			stopNoSignalPowerdownMonitor();
			startNoSignalPowerdownMonitor();
		}
	}
	
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        isWindOK = hasFocus;
        super.onWindowFocusChanged(hasFocus);
        windowFocusChangeHandle(hasFocus);
    }
    
	private int imgIDs[] = {
			R.drawable.ss00,
			R.drawable.ss01,
			R.drawable.ss02,
			R.drawable.ss03,
			R.drawable.ss04,
			R.drawable.ss05,
			R.drawable.ss06,
			R.drawable.ss07,
			R.drawable.ss08,
			R.drawable.ss09,
	};
	private int imgIdx = 0;
	private Timer screenSaverTimer ;
	private Handler mScreenSaverHandler = new Handler(){     
	        public void handleMessage(Message msg) {     
	            switch (msg.what) {         
	            case 1:        
	            	  Log.i(TAG,"handleMessage");
	            	  screenSaverImg.setImageResource(imgIDs[imgIdx++]);
			    	  if (imgIdx>=imgIDs.length) {
			    		  imgIdx=0;
			    	  }    
	                break;         
	            }         
	            super.handleMessage(msg);     
	        }     
	             
	    }; 
	private TimerTask screenSavertask ;
   
    private void showScreenSaver() {
		Log.i(TAG,"showScreenSaver");
    	if (screenSaverImg.getVisibility()!=View.VISIBLE) {
    		
    		screenSaverImg.setVisibility(View.VISIBLE);
	    	screenSaverTimer = new Timer(true);
	    	screenSavertask= new TimerTask(){  
		  	      public void run() { 
		              Message message = new Message();         
		              message.what = 1;         
		              mScreenSaverHandler.sendMessage(message); 
		  	   }  
		  	};
	    	screenSaverTimer.schedule(screenSavertask, 1000, 6000); 
    	}
    }
    private void closeScreanSaver() {
    	Log.i(TAG,"closeScreanSaver");
    	
    	WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    	if (screenSaverImg.getVisibility()==View.VISIBLE)
    	{
    		screenSaverImg.setVisibility(View.GONE);
    		screenSaverTimer.cancel();
    		screenSaverTimer = null;
    	}
    }
    
	private void screanSaverKeyHandle(){
		if (screenSaverMode == 1){
			handler.removeMessages(SIGNAL_CHECK);
			closeScreanSaver();
			boolean flag = 	SystemProperties.get("sys.dvb.tuner.status", "nosignal").equals("strong");
			if (flag == false){
				handler.sendEmptyMessageDelayed(SIGNAL_CHECK, 3000);
			}
		}
	}
	
	private void screenSaverSignalCheckHandle(){
		boolean flag = 	SystemProperties.get("sys.dvb.tuner.status", "nosignal").equals("strong");
		if (flag == false){
			startScreenSaver();
		}
	}
    /* 
    * 设置控件所在的位置YY，并且不改变宽高， 
    * XY为绝对位置 
    */ 
    public static void setLayout(View view,int x,int y) 
    { 
    	FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
	    layoutParams.leftMargin = x;
	    layoutParams.topMargin = y;
	    view.setLayoutParams(layoutParams); 
	    view.postInvalidate();
    } 
    private int txtMovingDown = 1*25;
    private int txtMovingLeft = 1*25;
    private int txtMovingStep = 100;
	private Timer dymScreenTxtTimer ;
	private Handler mdymScreenTxtHandler = new Handler(){     
	        public void handleMessage(Message msg) {     
	            switch (msg.what) {         
	            case 101:        
	            	View view = noSinalTxt;
	            	if (mPreview.getHeight()==0||mPreview.getWidth()==0)
	            		break;
	            	
	            	FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
	                int currLeft = layoutParams.leftMargin;  
	                int currTop = layoutParams.topMargin;  
	            	Log.i("haha","currLeft "+currLeft+" currTop "+currTop);
	            	if (txtMovingDown==1) {
	            		currTop += txtMovingStep;
	            		if (currTop>mPreview.getHeight()-view.getHeight()) {
	            			txtMovingDown = 0;
	            			currTop = mPreview.getHeight()-view.getHeight();
	            		}
	            	} else {
	            		currTop -= txtMovingStep;
	            		if (currTop<0) {
	            			txtMovingDown = 1;
	            			currTop = 0;
	            		}
	            	}
	            	
	            	if (txtMovingLeft==1) {
	            		currLeft += txtMovingStep;
	            		if (currLeft>mPreview.getWidth()-view.getWidth()) {
	            			txtMovingLeft = 0;
	            			currLeft = mPreview.getWidth()-view.getWidth();
	            		}
	            	} else {
	            		currLeft -= txtMovingStep;
	            		if (currLeft<0) {
	            			txtMovingLeft = 1;
	            			currLeft = 0;
	            		}
	            	}
	            	setLayout(view, currLeft,currTop);
	                break;         
	            }         
	            super.handleMessage(msg);     
	        }     
	             
	    }; 
	    
	private TimerTask dymScreenTxtTask ;
	
	private void startScreenTxtMoving() {
		noSinalTxt.setVisibility(View.VISIBLE);
		if (noSinalTxt!=null&&noSinalTxt.getVisibility() == View.VISIBLE){
			txtMovingDown = 1*25;
		    txtMovingLeft = 1*25;
			dymScreenTxtTimer = new Timer(true);
			dymScreenTxtTask= new TimerTask(){  
		  	      public void run() {
		              Message message = new Message();         
		              message.what = 101;         
		              mdymScreenTxtHandler.sendMessage(message); 
		  	   }  
		  	};
		  	dymScreenTxtTimer.schedule(dymScreenTxtTask, 0, 3000);
		}
	}
	private void stopScreenTxtMoving() {
		mdymScreenTxtHandler.removeMessages(101);
		noSinalTxt.setVisibility(View.INVISIBLE);
		if (dymScreenTxtTimer!=null) {
			dymScreenTxtTimer.cancel();
		 	dymScreenTxtTimer = null;
		}
		
		if (dymScreenTxtTask != null){
			dymScreenTxtTask.cancel();
			dymScreenTxtTask = null;
		}
	}
	
	
    /**
     * handler of finish dialog
     */
    private Handler finishHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
        	switch (msg.what) {
			case PIC_DIALOG_DISMISS_BYTIME:
				//picAlertdialog.dismiss();
                picdialog.removeAllViews();
				isPicDialogDismiss=true;
				break;
			case SOUND_DIALOG_DISMISS_BYTIME:
				// soundAlertdialog.dismiss();
                sounddialog.removeAllViews();
	             isSoundDialogDismiss=true;
				break;
			case ZOOM_DIALOG_DISMISS_BYTIME:
				//zoomAlertdialog.dismiss();
                zoomdialog.removeAllViews();
				isZoomDialogDismiss=true;
				break;
            case TRACK_DIALOG_DISMISS_BYTIME:
                 trackdialog.removeAllViews();
                 isTrackDialogDismiss=true;
                 break;
			default:
				break;
			}

        };
    };

    /**
     * set delay time to finish activity
     */
    public void delay(int msg) {
        finishHandle.removeMessages(msg);
        Message message = new Message();
        message.what = msg;
        finishHandle.sendMessageDelayed(message, Constant.DISPEAR_TIME);
    }
	
	private void removeDialogs(){
    	finishHandle.removeMessages(PIC_DIALOG_DISMISS_BYTIME);
    	finishHandle.removeMessages(SOUND_DIALOG_DISMISS_BYTIME);
    	finishHandle.removeMessages(ZOOM_DIALOG_DISMISS_BYTIME);
    	
    	if(!isSoundDialogDismiss){
    		//soundAlertdialog.dismiss();
            sounddialog.removeAllViews();
    		isSoundDialogDismiss=true;
    	}
    	
    	if(!isZoomDialogDismiss){
    		//zoomAlertdialog.dismiss();
            zoomdialog.removeAllViews();
    		isZoomDialogDismiss=true;
    	}
    	
    	if(!isPicDialogDismiss){
    		//picAlertdialog.dismiss();
            picdialog.removeAllViews();
    		isPicDialogDismiss=true;
    	}
    }

    private void pictureModeQuickKeyHandle(){
        if(!isSoundDialogDismiss){
            sounddialog.removeAllViews();
            isSoundDialogDismiss=true;
        }
        if(!isZoomDialogDismiss){
            zoomdialog.removeAllViews();
            isZoomDialogDismiss=true;
        }



        if(!isPicDialogDismiss){ //not dismiss
            delay(PIC_DIALOG_DISMISS_BYTIME);
            picModeIndex++;
            if(picModeIndex>=InterfaceValueMaps.picture_mode.length){
                picModeIndex=0;
            }
            Log.i(TAG,"picModeIndex="+picModeIndex);
            PictureInterface.setPictureMode(InterfaceValueMaps.picture_mode[picModeIndex][0]);
            picdialog = (LinearLayout) this.findViewById(R.id.mydialog);
            LayoutInflater factory = LayoutInflater.from(mContext);
            View myView = factory.inflate(R.layout.selector_view_dialog,null);
            pic_menu_btn =(TextView) myView.findViewById(R.id.menu_btn);
            pic_menu_btn.setText(InterfaceValueMaps.picture_mode[picModeIndex][1]);
            picdialog.removeAllViews();
            picdialog.addView(myView);

        }else{  //dismiss
            int mode = PictureInterface.getPictureMode();
            picModeIndex= Util.getIndexFromArray(mode,InterfaceValueMaps.picture_mode);
            Builder	 mPicBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
            Log.i(TAG,"mode="+mode);
            Log.i(TAG,"picModeIndex="+picModeIndex);
            picdialog = (LinearLayout) this.findViewById(R.id.mydialog);
            LayoutInflater factory = LayoutInflater.from(mContext);
            View myView = factory.inflate(R.layout.selector_view_dialog,null);
            pic_menu_btn =(TextView) myView.findViewById(R.id.menu_btn);
            pic_menu_btn.setText(InterfaceValueMaps.picture_mode[picModeIndex][1]);
            isPicDialogDismiss=false;
            picdialog.removeAllViews();
            picdialog.addView(myView);
            delay(PIC_DIALOG_DISMISS_BYTIME);
        }
    }
    private void soundModeQuickKeyHandle(){
        if(!isPicDialogDismiss){
            //picAlertdialog.dismiss();
            picdialog.removeAllViews();
            isPicDialogDismiss=true;
        }
        if(!isZoomDialogDismiss){
            //zoomAlertdialog.dismiss();
            zoomdialog.removeAllViews();
            isZoomDialogDismiss=true;
        }
        if(!isSoundDialogDismiss){ //not dismiss
            delay(SOUND_DIALOG_DISMISS_BYTIME);
            soundModeIndex++;
            if(soundModeIndex>=InterfaceValueMaps.sound_mode.length){
                soundModeIndex=0;
            }
            Log.i(TAG, "soundModeIndex=" + soundModeIndex);
            AudioInterface.getAudioManager().setSoundMode(InterfaceValueMaps.sound_mode[soundModeIndex][0]);

            sounddialog = (LinearLayout) this.findViewById(R.id.mydialog);
            LayoutInflater sound_factory = LayoutInflater.from(mContext);
            View sound_myView = sound_factory.inflate(R.layout.selector_view_dialog,null);
            sound_menu_btn =(TextView) sound_myView.findViewById(R.id.menu_btn);
            sound_menu_btn.setText(InterfaceValueMaps.sound_mode[soundModeIndex][1]);
            sounddialog.removeAllViews();
            sounddialog.addView(sound_myView);

        }else{   // dismiss
            int sound_mode = AudioInterface.getAudioManager().getSoundMode();
            soundModeIndex= Util.getIndexFromArray(sound_mode,InterfaceValueMaps.sound_mode);
            Builder	 mSoundBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
            Log.i(TAG,"sound_mode"+sound_mode);
            Log.i(TAG,"soundModeIndex="+soundModeIndex);
            sounddialog = (LinearLayout) this.findViewById(R.id.mydialog);
            LayoutInflater sound_factory = LayoutInflater.from(mContext);
            View sound_myView = sound_factory.inflate(R.layout.selector_view_dialog,null);
            sound_menu_btn =(TextView) sound_myView.findViewById(R.id.menu_btn);
            sound_menu_btn.setText(InterfaceValueMaps.sound_mode[soundModeIndex][1]);
            isSoundDialogDismiss =false;
            sounddialog.removeAllViews();
            sounddialog.addView(sound_myView);
            delay(SOUND_DIALOG_DISMISS_BYTIME);
        }
    }

    private void zoomQuickKeyHandle(){
        if(!isPicDialogDismiss){
            //picAlertdialog.dismiss();
            picdialog.removeAllViews();
            isPicDialogDismiss=true;
        }
        if(!isSoundDialogDismiss){
            //soundAlertdialog.dismiss();
            sounddialog.removeAllViews();
            isSoundDialogDismiss=true;
        }

        if(!isZoomDialogDismiss){ //not dismiss
            delay(ZOOM_DIALOG_DISMISS_BYTIME);
            zoomModeIndex++;
            if(zoomModeIndex>=InterfaceValueMaps.picture_aspect.length){
                zoomModeIndex=0;
            }
            Log.i(TAG,"zoomModeIndex="+zoomModeIndex);
            PictureInterface.setAspect(InterfaceValueMaps.picture_aspect[zoomModeIndex][0], false);

            zoomdialog = (LinearLayout) this.findViewById(R.id.mydialog);
            LayoutInflater zoom_factory = LayoutInflater.from(mContext);
            View zoom_myView = zoom_factory.inflate(R.layout.selector_view_dialog,null);
            zoom_menu_btn =(TextView) zoom_myView.findViewById(R.id.menu_btn);
            zoom_menu_btn.setText(InterfaceValueMaps.picture_aspect[zoomModeIndex][1]);
            zoomdialog.removeAllViews();
            zoomdialog.addView(zoom_myView);
        }else{   // dismiss
            int aspect = PictureInterface.getAspect();          //display mode  4 闁汇垹顭峰鎵焊閸濆嫷鍤�
            zoomModeIndex= Util.getIndexFromArray(aspect,InterfaceValueMaps.picture_aspect);
            //闁哄秷顬冨畵渚�閿熺晫婀撮柨娑樿嫰楠炴捇姊介妶鍕溄闁绘せ鏅槐婵嬪绩閹佷海1闁挎稑鏈弬浣瑰緞閿熶粙鏌呮径鎰╋拷闁靛棗鍊哥紞瀣嚔瀹勬澘绲块柛濠囨？鐠愮喐娼诲▎搴ｇ憦闁兼澘鎳忓鍌炴晬瀹�喚鍟庣紓鍐惧枛閿熻姤绋夌弧鍦玃ECT_16_9
            if((aspect==EnumPictureAspect.ASPECT_ZOOM)||(aspect==EnumPictureAspect.ASPECT_ZOOM1)||(aspect==EnumPictureAspect.ASPECT_ZOOM2||(aspect==EnumPictureAspect.ASPECT_AUTO))){
                PictureInterface.setAspect( EnumPictureAspect.ASPECT_16_9,false);
                aspect = PictureInterface.getAspect();
                zoomModeIndex = Util.getIndexFromArray(aspect,InterfaceValueMaps.picture_aspect);
                Log.i(TAG,"aspect= ASPECT_ZOOM or ASPECT_ZOOM1 or ASPECT_ZOOM2 or ASPECT_AUTO;set value = ASPECT_16_9; aspect="+aspect);
            }
            // Builder  mZoomBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
            Log.i(TAG,"aspect="+aspect);
            Log.i(TAG,"zoomModeIndex="+zoomModeIndex);
            zoomdialog = (LinearLayout) this.findViewById(R.id.mydialog);
            LayoutInflater zoom_factory = LayoutInflater.from(mContext);
            View zoom_myView = zoom_factory.inflate(R.layout.selector_view_dialog,null);
            zoom_menu_btn =(TextView) zoom_myView.findViewById(R.id.menu_btn);
            zoom_menu_btn.setText(InterfaceValueMaps.picture_aspect[zoomModeIndex][1]);
            isZoomDialogDismiss=false;
            zoomdialog.removeAllViews();
            zoomdialog.addView(zoom_myView);
            delay(ZOOM_DIALOG_DISMISS_BYTIME);
        }
    }


    private void TrackModeQuickKeyHandle(){
        if(!isSoundDialogDismiss){
            sounddialog.removeAllViews();
            isSoundDialogDismiss=true;
        }
        if(!isZoomDialogDismiss){
            zoomdialog.removeAllViews();
            isZoomDialogDismiss=true;
        }
        if(!isPicDialogDismiss){
            picdialog.removeAllViews();
            isPicDialogDismiss=true;
        }

        if(!isTrackDialogDismiss){ //not dismiss
            delay(TRACK_DIALOG_DISMISS_BYTIME);
            trackModeIndex++;
            if(trackModeIndex>=4){
                trackModeIndex=0;
            }
            Log.i(TAG,"trackModeIndex="+trackModeIndex);
            UmtvManager.getInstance().getAudio().setTrackMode(InterfaceValueMaps.track_mode[trackModeIndex][0]);
            //PictureInterface.setPictureMode(InterfaceValueMaps.track_mode[trackModeIndex][0]);
            trackdialog = (LinearLayout) this.findViewById(R.id.mydialog);
            LayoutInflater factory = LayoutInflater.from(mContext);
            View myView = factory.inflate(R.layout.selector_view_dialog,null);
            track_menu_btn =(TextView) myView.findViewById(R.id.menu_btn);
            track_menu_btn.setText(InterfaceValueMaps.track_mode[trackModeIndex][1]);
            trackdialog.removeAllViews();
            trackdialog.addView(myView);
        }else{  //dismiss
            int mode = UmtvManager.getInstance().getAudio().getTrackMode();
            trackModeIndex= Util.getIndexFromArray(mode,InterfaceValueMaps.track_mode);
            //   Builder	 mPicBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
            trackdialog = (LinearLayout) this.findViewById(R.id.mydialog);
            LayoutInflater factory = LayoutInflater.from(mContext);
            View myView = factory.inflate(R.layout.selector_view_dialog,null);
            track_menu_btn =(TextView) myView.findViewById(R.id.menu_btn);
            track_menu_btn.setText(InterfaceValueMaps.track_mode[trackModeIndex][1]);
            isTrackDialogDismiss=false;
            trackdialog.removeAllViews();
            trackdialog.addView(myView);
            delay(TRACK_DIALOG_DISMISS_BYTIME);
        }
    }
}
