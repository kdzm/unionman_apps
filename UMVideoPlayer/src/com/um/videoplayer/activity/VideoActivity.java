package com.um.videoplayer.activity;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.Locale;
import java.util.zip.Inflater;

import android.R.string;
import android.app.Activity;
import android.os.Looper;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hisilicon.android.tvapi.CusAudio;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.hisilicon.android.tvapi.CusPicture;

import com.hisilicon.android.mediaplayer.HiMediaPlayer;
import com.hisilicon.android.mediaplayer.HiMediaPlayer.OnCompletionListener;
import com.hisilicon.android.mediaplayer.HiMediaPlayer.OnErrorListener;
import com.hisilicon.android.mediaplayer.HiMediaPlayer.OnFastBackwordCompleteListener;
import com.hisilicon.android.mediaplayer.HiMediaPlayer.OnInfoListener;
import com.hisilicon.android.mediaplayer.HiMediaPlayer.OnPreparedListener;
import com.hisilicon.android.mediaplayer.HiMediaPlayerInvoke;

import android.app.AlertDialog.Builder;

import com.um.videoplayer.interfaces.InterfaceValueMaps;
import com.um.videoplayer.interfaces.PictureInterface;
import com.um.videoplayer.util.Util;
import  com.um.videoplayer.interfaces.AudioInterface;
import com.hisilicon.android.tvapi.constant.EnumPictureAspect;

import com.um.videoplayer.R;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Parcel;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.provider.MediaStore.Video;
import android.media.AudioManager;
import android.os.SystemProperties;
import com.hisilicon.android.HiDisplayManager;
import com.hisilicon.android.DispFmt;
import com.um.videoplayer.activity.base.ActivityFrame;
import com.um.videoplayer.control.BDInfo;
import com.um.videoplayer.control.base.AudioFormat;
import com.um.videoplayer.control.base.Constant;
import com.um.videoplayer.control.base.Constant.KeyCode;
import com.um.videoplayer.control.base.LanguageXmlParser;
import com.um.videoplayer.dao.DBOperateHelper;
import com.um.videoplayer.listmanager.MediaFileList;
import com.um.videoplayer.model.EncodeNameValue;
import com.um.videoplayer.model.ModelBDInfo;
import com.um.videoplayer.model.ModelDVDInfo;
import com.um.videoplayer.util.Common;
import com.um.videoplayer.util.Constants;
import com.um.videoplayer.util.CustomToast;
import com.um.videoplayer.util.EncodeXmlParser;
import com.um.videoplayer.util.FileUtil;
import com.um.videoplayer.util.MyToast;
import com.um.videoplayer.util.SubXmlParser;
import com.um.videoplayer.util.Tools;
import com.um.videoplayer.utility.DialogTool;
import com.um.videoplayer.utility.LogTool;
import com.um.videoplayer.utility.base.SubtitleFileFilter;
import com.um.videoplayer.view.MySeekBar;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import com.um.videoplayer.activity.MultListAdapter;


public class VideoActivity extends ActivityFrame implements SurfaceHolder.Callback {
    private final static String TAG = "VideoActivity";
    private Context context   = VideoActivity.this;
    private Activity activity = VideoActivity.this;
    private DBOperateHelper operater;
    private Common common = null;
    private Tools tools = null;
    private static final String VIDEO_LIST_FINISH_ACTION = "medialistdestroy";
    
    private Boolean iSmedialist=false;
    
    private AlertDialog mAlertDialog;
    
    private AlertDialog mSubAlertDialog=null;
    private AlertDialog mSwiAlertDialog=null;
    private AlertDialog mColorAlertDialog=null;
    private AlertDialog mEffectAlertDialog=null;
  
    
    private SimpleAdapter menuSchedule;

    private HisiVideoView videoView  = null;

    public HiDisplayManager mDisplayManager = null;

    private RelativeLayout mediaInfoLayout;

    private View mediaControllerLayout;

    private LinearLayout btnLinearLayout;

    private SeekBar videoSeekBar;

    private ButtonClickListener mClick = null;

    private ImageView page_up, rewind, play, forward, page_down;

    private TextView timeTextView;

    private EditText subPath;
    private int subCount = 0;
    private int hiType1 = 0;
    private int hiType2 = 0;

    private ImageView playStatus;

    private Dialog menuDialog    = null;
    private Dialog subSetDialog  = null;
    private Dialog subtitleSetDialog = null;
    private Dialog audioSetDialog = null;
    private Dialog subtitleAdvSetDialog = null;
    private Dialog stereoItemDialog = null;
    private Dialog pointDialog = null;

    private float progerssFwRwind = -1;

    private int position;

    private SQLiteDatabase database = null;

    private ArrayList < HashMap < String, Object >> list = null;

    private List < HashMap < String, Object >> marklist = null;

    private List <VideoModel> mediaList = null;

    private String currPath = null;

    private String getCurrPath;

    private int getCurrId;

    private long getCurrSize;

    private String getCurrName;

    private String getCurrMode;

    private String[] pathstr = null;

    private String getPlayPath;

    private String[] menuItems;
    private String[] mode;
    private String[] colorValue = {"0xffffff", "0x000000", "0xff0000", "0xffff00", "0x0000ff", "0x00ff00"};
    private String[] videoFormatValue = {"MPEG2", "MPEG4", "AVS", "H263", "H264", "REAL8", "REAL9",
                                         "VC1", "VP6", "VP6F", "VP6A", "MJPEG", "SORENSON", "DIVX3",
                                         "RAW", "JPEG", "VP8", "MSMPEG4V1", "MSMPEG4V2", "MSVIDEO1",
                                         "WMV1", "WMV2", "RV10", "RV20", "SVQ1", "SVQ3", "H261", "VP3",
                                         "VP5", "CINEPAK", "INDEO2", "INDEO3", "INDEO4", "INDEO5",
                                         "MJPEGB", "MVC", "HEVC", "DV", "HUFFYUV","DIVX" ,"REALMAGICMPEG4" ,
                                         "VP9","WMV3","BUTT"
                                        };

    private List <EncodeNameValue> encodeList;

    private boolean isFullScreen = false;
    private boolean isStop = false;

    private boolean isContinue = true;

    private boolean isAfterReturn = true;

    private boolean haveOnNewIntent = false;

    private boolean isThreadStart = false;

    private boolean isSecondProgress = false;

    private boolean isSeekBarSelected = true;

    private boolean isMarkDBChange = true;

    private int isShowSub = 0;

    private boolean haveLeftRightOpration = false;

    private boolean rewindOrForward = false;

    private boolean isCurrVideoMark = true;

    private boolean isEncode = false;

    private int openFromMark = -1;

    private long startTime  = 0;
    private long startTime1 = 0;
    private long startTime2 = 0;

    private long mLastSeekTime  = 0;
    
    private Handler ctrlBarHandler = null;
    private MyThread mThread = null;

    private Handler dHandler = null;
    private DThread dThread = null;
    private boolean isDShow = false;
    private ToastUtil toast = null;
    private View currView = null;
    private Handler vHandler = null;
    private VThread vThread;

    private Handler nameSizeDismissHandler = null;
    private NameSizeDismissThread nameSizeDismissThread = null;
    private DolbyDisplayThread dolbyDisplayThread = null;

    private static int screenWidth  = 0;
    private static int screenHeight = 0;

    private int seekBarMax = 1000;

    private int total = 0;

    private int lastPosition = 0;

    int defaultFocus = 3;

    private int rewindRate  = 1;
    private int forwardRate = 1;

    private int soundRate = 1;
    private int selectedSubId = 0;
    private int selectedTrack = 0;
    private String selectedColor = "0x00";
    private int selectedColorPos = 0;
    private int selectedSizes    = -1;
    private int selectedEffect   = -1;
    private int selectedPosition = 36;
    private int selectedTime = -1;
    private int PositionStep = 18;
    private int selectedAudio = 0;
    private int selectedSubEncode = 0;
    private int selectedSpace  = -1;
    private int selectedLSpace = -1;
    private int selectedDolbyRangeInfo = -1;

    private SurfaceHolder mSubHolder;
    private SurfaceHolder mSubtiteHolder;
    private SurfaceView mSubtitelView;

    private MediaFileList mediaFileList = null;
    public static MediaFileListService mediaFileListService = null;
    public MyServiceConnection conn = null;

    private boolean isFirstClick = true;

    private int mType = 0;
    private int mCurrentMode = 0;
    private int mMVCType = 0;
    private boolean isEnded = false;
    private BDInfo mBDInfo;
    public static boolean m3DMode = false;
    private static int mCurrent3DMode = 0;
    private static int mFmt = 0;
    private int mDolbyCertification = 0;
    private int showBitRate = 0;
    private LanguageXmlParser mLanguageXmlParser;

    private static int HI_2D_MODE = 0;
    private static int HI_3D_MODE = 1;
    private static final int HI_VIDEOFORMAT_TYPE_MVC = 35;

    private boolean[] flags = new boolean[] {false, true, false, false, false,false};

    private TextView bitRate = null;

    private static final String bitRateBegin = "Bit rate:";
    private static final String bitRateEnd = "Mbps";
    
    //private ArrayList<String> groups;
    private ListView advOptItemsList;
   
    private boolean[] subtitleItem = new boolean[] {false, true};
    //TV ADD
    private BroadcastReceiver exStorageReceiver;
    
    private  String[] track_mode;
    private  String[] sound_mode;
    
    private int[] sound_mode_flag;
    private  String[] modeVal;
    ListView menuList;
    private ArrayList<HashMap<String, Object>> listName = new ArrayList<HashMap<String, Object>>(); 
    /* private SimpleAdapter mSchedule;*/
    private String[] listItemVals;
    private String[] listItems;
   // private String[] listItemsHelp;
    private int[] listItemRightImgs;
    private int[] listItemLeftImgs;
   // private HashMap<String, Object> map;
   // private ListAdapter mListAdapter;*/
    private View mView;
    private static final String PIC_SET_FINISH_ACTION = "cn.com.unionman.picture.finish";
	private static final String SOUND_SET_FINISH_ACTION = "cn.com.unionman.sound.finish";
	private static final String SONG_CHANGE_ACTION = "SongChanged";
	private static final String LIST_CHANGE_ACTION ="VideoListFocusChange";
	public View[] imgView;
	private ImageButton mQueueButton;
	private ImageButton mModeButton;
	private String currPlayPath = null;
	private boolean stopPlay = true;
	private boolean resumePlay = false;
	private boolean isFileChanged = false; 
	private BroadcastReceiver bcVideoActivity =new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		Log.i(TAG,"========getAction======="+intent.getAction()+"=============");
    		 if (SONG_CHANGE_ACTION.equals(intent.getAction()))
    	        {
    	            Bundle bundle = intent.getExtras();
    	            if (bundle != null)
    	            {
    	                String currpath = bundle.getString("currpath");
    	                Log.i(TAG,"==============="+currpath+"============= currPlayPath.equals(currpath) ");
						if(true){
							if((null != currPlayPath) && (currpath != null)){
								if(!currPlayPath.equals(currpath)){
									updateCurrentPosition();
									getCurrPath = currpath;
									currPlayPath = getCurrPath;
									reinit(currPlayPath);
									isFileChanged = true;							
								}
							}else{
								updateCurrentPosition();
								getCurrPath = currpath;
								currPlayPath = getCurrPath;
								reinit(currPlayPath);
								isFileChanged = true;
							}
							
						}else{
							resumePlay = true; 
						}
    	            }
    	        }
    		 }
    	};
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        PictureInterface.setAspect( EnumPictureAspect.ASPECT_16_9,false);
        Common.haveShowNoFileToast = false;
        Common.isLastMediaFile = false;
        isEnded = false;
		resumePlay = false;
        mBDInfo = new BDInfo();
        toast = new ToastUtil();
        init();
      
		mQueueButton = (ImageButton) findViewById(R.id.curplaylist);
		mModeButton = (ImageButton) findViewById(R.id.curplaymode);
		 switch (common.getMode()){
   	     case 0: mModeButton.setBackgroundResource(R.drawable.sequence_selector);break;
   	     case 1: mModeButton.setBackgroundResource(R.drawable.circulate_selector);break;
   	     case 2: mModeButton.setBackgroundResource(R.drawable.single_circulate_selector);break;
   	     case 3: mModeButton.setBackgroundResource(R.drawable. single_selector);break;
   	     case 4: mModeButton.setBackgroundResource(R.drawable.random_selector);break;}
		Log.w(TAG, "onCreate mediaFileList "+mediaFileList);
		mQueueButton.setOnClickListener(mQueueListener);
		mModeButton.setOnClickListener(mModeListener);				
    }
	
    private View.OnClickListener mQueueListener = new View.OnClickListener() {
        public void onClick(View v) {
        	iSmedialist=true;
			Log.w(TAG, "mQueueListener onClick "+ currPlayPath + " getCurrPath " + getCurrPath + " currPath "+ currPath);
			Log.w(TAG, "mediaFileList "+mediaFileList);
			/*
            try {
                //currPlayPath = mService.getPath();
                currPlayPath = ((Button) v).getTag().toString();
            }
            catch (RemoteException ex) {
            }*/
	        stopPlay = false;    
	        currPlayPath = getCurrPath;
			TmpVideoActivity.activity = VideoActivity.this;

            Intent intent = new Intent();

            intent.setClassName("com.um.videoplayer",
                                "com.um.videoplayer.activity.FileListAcvitity");
           // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.setDataAndType(intent.getData(), "video/*");
            intent.putExtra("MediaFileList", mediaFileList);
            intent.putExtra("path", currPlayPath);
            startActivity(intent);
        }
        
    };
    
    private View.OnClickListener mModeListener = new View.OnClickListener() {
        public void onClick(View v) {
        	//getPlayModeDialog();

        	 int arg2 =common.getMode()+1;
        	 if( arg2 > 4){
        		 arg2=0;
        	 }
        	 
        	 switch (arg2){
       	     case 0: mModeButton.setBackgroundResource(R.drawable.sequence_selector);break;
       	     case 1: mModeButton.setBackgroundResource(R.drawable.circulate_selector);break;
       	     case 2: mModeButton.setBackgroundResource(R.drawable.single_circulate_selector);break;
       	     case 3: mModeButton.setBackgroundResource(R.drawable. single_selector);break;
       	     case 4: mModeButton.setBackgroundResource(R.drawable.random_selector);break;}
        	 
        	 common.switchPlayModel(arg2);
             common.sharedPreferencesOpration(Constants.SHARED, "currPlayMode",
                                              arg2, 0, true);
            // modeDialog.dismiss();
             /*toast.showMessage(VideoActivity.this,
                               getString(R.string.now_video_playmode) + mode[arg2],
                               Toast.LENGTH_SHORT);*/
             toast.showMessage(VideoActivity.this,
                      mode[arg2],
                     Toast.LENGTH_SHORT);
        }
        
    };
    
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean pathFlag = intent.getBooleanExtra("pathFlag", true);

        if (pathFlag) {
            setIntent(intent);
            haveOnNewIntent = true;
            isAfterReturn = true;
            init();
        }
        else {
            subPath.setText(intent.getStringExtra("path"));
        }
    };


    public void surfaceCreated(SurfaceHolder holder) {}

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {}

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {}

    private void canRunInThread() {
        EncodeXmlParser encodexmlParser = new EncodeXmlParser(context);
        encodeList = encodexmlParser.getEncodesList().get(0);

        if (common.sharedPreferencesOpration(Constants.SHARED, "currPlayMode", 0, Constants.ALLNOCYCLE, false) == 0) {
            flags[3] = false;
            common.setMode(Constants.ALLNOCYCLE);
        }
        else {
            flags[3] = true;
            common.setMode(common.sharedPreferencesOpration(Constants.SHARED, "currPlayMode", 0, Constants.ALLNOCYCLE, false));
        }

        selectedAudio = common.sharedPreferencesOpration(Constants.SHARED, "channer", 0, 0, false);
        selectedSubEncode = common.sharedPreferencesOpration(Constants.SHARED, "subtitleEncode", 0, 0, false);
        selectedSizes = common.sharedPreferencesOpration(Constants.SHARED, "subtitleSizes", 0, 25, false);
        selectedColorPos = common.sharedPreferencesOpration(Constants.SHARED, "selectedColor", 0, 0, false);
        selectedPosition = common.sharedPreferencesOpration(Constants.SHARED, "selectedPosition", 0, 36, false);
        selectedEffect = common.sharedPreferencesOpration(Constants.SHARED, "selectedEffect", 0, 0, false);
        selectedSpace = common.sharedPreferencesOpration(Constants.SHARED, "selectedSpace", 0, 0, false);
        selectedLSpace = common.sharedPreferencesOpration(Constants.SHARED, "selectedLSpace", 0, 0, false);
        selectedDolbyRangeInfo = common.sharedPreferencesOpration(Constants.SHARED, "selectedDolbyRangeInfo", 0, 100, false);
        selectedColor = colorValue[selectedColorPos];

        if ((mediaFileList != null) && (mediaFileList.getId() == 1)) {
            if (m3DMode) {
                menuItems = getResources().getStringArray(R.array.items_fm_3D_mode);
            }
            else {
                menuItems = getResources().getStringArray(R.array.items_fm_normal_mode);
            }

            ContentResolver resolver = context.getContentResolver();
            Log.d(TAG, "getCurrPath = " + getCurrPath);
            Cursor cursor = null;

            try {
                cursor = resolver.query(Video.Media.getContentUri("external"),
                                        new String[] {"_id", "_display_name", "_data"},
                                        "_data = '" + getCurrPath + "'", null, "_data");

                while (cursor.moveToNext()) {
                    getCurrId = cursor.getInt(0);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            if (getCurrId != 0) {
                if (m3DMode) {
                    menuItems = getResources().getStringArray(R.array.items_3D_mode);
                }
                else {
                    menuItems = getResources().getStringArray(R.array.items_normal_mode);
                }
            }
        }
        else {
            if (m3DMode) {
                menuItems = getResources().getStringArray(R.array.items_3D_mode);
            }
            else {
                menuItems = getResources().getStringArray(R.array.items_normal_mode);
            }
        }

        mode = getResources().getStringArray(R.array.mode);
        mediaList = Common.getData();
        playStatus  = (ImageView)findViewById(R.id.playStauts_ImageView);
        btnLinearLayout = (LinearLayout)findViewById(R.id.btnLinearLayout);
        page_up = (ImageView) findViewById(R.id.page_up);
        rewind = (ImageView) findViewById(R.id.rewind);
        play = (ImageView) findViewById(R.id.play_pause);
        play.setBackgroundResource(R.drawable.pause_button);
        //        play.requestFocus();
        forward   = (ImageView) findViewById(R.id.forward);
        page_down = (ImageView) findViewById(R.id.page_down);
        bitRate = (TextView) findViewById(R.id.bitrate);
        
        mQueueButton = (ImageButton) findViewById(R.id.curplaylist);
		mModeButton = (ImageButton) findViewById(R.id.curplaymode);
        imgView = new View[] {page_up,rewind,play,forward,page_down,mQueueButton,mModeButton};
        for (int i = 0; i < imgView.length; i++){
        	imgView[i].setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if (hasFocus) {
			         
			            v.animate().scaleX(1.1f).scaleY(1.1f)
			                        .setDuration(100).start();
			        } else {
			    
			            v.animate().scaleX(1.0f).scaleY(1.0f)
			                        .setDuration(100).start();
			        }
				}
			});
        }
        
        if (showBitRate == 0) {
            bitRate.setVisibility(View.GONE);
        }
        mClick = new ButtonClickListener();
        page_up.setOnClickListener(mClick);
        rewind.setOnClickListener(mClick);
        play.setOnClickListener(mClick);
        forward.setOnClickListener(mClick);
        page_down.setOnClickListener(mClick);

        if (flags[1] == true) {
            rewind.setVisibility(View.VISIBLE);
            forward.setVisibility(View.VISIBLE);
        }

        database = Common.getDataBase(this);
        //checkDbData(getCurrPath);
    }

    public class MyServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            mediaFileListService = ((MediaFileListService.MyBinder)service).getService();

            if (mediaFileListService != null && !Common.isLoadSuccess()) {
                mediaFileListService.setThreadStart();
            }
        }

        public void onServiceDisconnected(ComponentName arg0) {}
    }

    public void init() {
        Intent intent = getIntent();
        mDisplayManager = new HiDisplayManager();
        DispFmt mDispFmt = mDisplayManager.GetDisplayCapability();

        if (mDispFmt == null) {
            m3DMode = false;
        }
        else if (mDispFmt.is_support_3d == 0) {
            m3DMode = false;
        }
        else if (mDispFmt.is_support_3d == 1) {
            m3DMode = true;
        }

        mFmt = mDisplayManager.getFmt();
        Log.i(TAG, "m3DMode: " + m3DMode + "  mFmt: " + mFmt);
        mMVCType = 0;
        mLanguageXmlParser = new LanguageXmlParser(context);
        mediaFileList = intent.getParcelableExtra("MediaFileList");
        mModelBDInfo = (ModelBDInfo) intent.getSerializableExtra("bd");
        mModelDVDInfo = (ModelDVDInfo) intent.getSerializableExtra("dvd");

        if (mediaFileList != null) {
            if (mediaFileList.getId() == 1) {
                Intent service = new Intent(Constants.ACTION);
                conn = new MyServiceConnection();
                context.bindService(service, conn, Context.BIND_AUTO_CREATE);
            }

            isFirstClick = true;
            getVideoInfo_noCycle(mediaFileList.getCurrVideoInfo());
        }
        else {
            Uri uri = intent.getData();
            getCurrPath = uri.toString();
            File file = new File(getCurrPath);
            getCurrName = file.getName();
            getCurrSize = file.length();
        }

        getScreenSize();
        common = new Common(context, VideoActivity.this, screenWidth);

        if (common.sharedPreferencesOpration(Constants.SHARED, "ContinuePlay", 0, 1, false) == 1) {
            flags[0] = true;
        }
        else {
            flags[0] = false;
        }

        if (common.sharedPreferencesOpration(Constants.SHARED, "BackAndForward", 0, 1, false) == 1) {
            flags[1] = true;
        }
        else {
            flags[1] = false;
        }

        if (common.sharedPreferencesOpration(Constants.SHARED, "Proportion", 0, 0, false) == 1) {
            flags[2] = true;
            isFullScreen = true;
        }
        else {
            flags[2] = false;
            isFullScreen = false;
        }

        if (common.sharedPreferencesOpration(Constants.SHARED, "DolbyCertification", 0, 1, false) == 1) {
            flags[4] = true;
            mDolbyCertification = 1;
        }
        else {
            flags[4] = false;
            mDolbyCertification = 0;
        }

        if (common.sharedPreferencesOpration(Constants.SHARED, "showBitRate", 0, 0, false) == 1) {
            flags[5] = true;
            showBitRate = 1;
        }
        else {
            flags[5] = false;
            showBitRate = 0;
        }

        tools = new Tools();
        list = new ArrayList < HashMap < String, Object >> ();
        marklist = new ArrayList < HashMap < String, Object >> ();
        operater = new DBOperateHelper();
        ctrlBarHandler = new Handler();
        mThread  = new MyThread();
        vHandler = new Handler();
        nameSizeDismissHandler = new Handler();
        nameSizeDismissThread = new NameSizeDismissThread();
        dolbyDisplayThread = new DolbyDisplayThread();
        canRunInThread();
        videoView = (HisiVideoView) findViewById(R.id.videoView);
        videoSeekBar = (SeekBar) findViewById(R.id.videoSeekBar);
        mediaInfoLayout = (RelativeLayout) findViewById(R.id.mediaInfo);
        timeTextView = (TextView) findViewById(R.id.timeText);
        mediaControllerLayout = findViewById(R.id.mediaControllerLayout);
        
        mSubtitelView = (SurfaceView)findViewById(R.id.surface_view_subtitle);
        // set Subtitle SurfaceView
        mSubtiteHolder = mSubtitelView.getHolder();
        mSubtiteHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        mSubtiteHolder.setFormat(PixelFormat.RGBA_8888);
        mSubtiteHolder.addCallback(new SurfaceHolder.Callback(){
        	 @Override
            public void surfaceDestroyed(SurfaceHolder holder){
            //TODO Auto-generated method stub
            }
            @Override
            public void surfaceCreated(SurfaceHolder holder){
                Surface surface;
                if (mSubtiteHolder != null){
                    surface = mSubtiteHolder.getSurface();
                    videoView.setSubtitleSurface(surface);
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height){
                }
            });
        
        mediaControllerLayout.setVisibility(View.INVISIBLE);
        mediaInfoLayout.getBackground().setAlpha(100);
        setVideoScale(Constants.SCREEN_FULL);
        videoSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        videoView.setOnPreparedListener(mPreparedListener);
        videoView.setOnCompletionListener(mCompletionListener);
        videoView.setOnErrorListener(mOnErrorListener);
        videoView.setOnFastBackwordCompleteListener(mFastBackwordCompleteListener);
        videoView.setVideoPath(getCurrPath);
        
        IntentFilter listSet = new IntentFilter(VIDEO_LIST_FINISH_ACTION);
        context.registerReceiver(mListReceiver, listSet);
        
        showLoadProgressDialog();
    }

	    public void reinit(String newPath) {
        Intent intent = getIntent();
        mDisplayManager = new HiDisplayManager();
        DispFmt mDispFmt = mDisplayManager.GetDisplayCapability();

        if (mDispFmt == null) {
            m3DMode = false;
        }
        else if (mDispFmt.is_support_3d == 0) {
            m3DMode = false;
        }
        else if (mDispFmt.is_support_3d == 1) {
            m3DMode = true;
        }

        mFmt = mDisplayManager.getFmt();
        Log.i(TAG, "m3DMode: " + m3DMode + "  mFmt: " + mFmt);
        mMVCType = 0;
        mLanguageXmlParser = new LanguageXmlParser(context);
        mediaFileList = intent.getParcelableExtra("MediaFileList");
        mModelBDInfo = (ModelBDInfo) intent.getSerializableExtra("bd");
        mModelDVDInfo = (ModelDVDInfo) intent.getSerializableExtra("dvd");

        getCurrPath = newPath;
        getPlayPath = getCurrPath;
        File file = new File(getCurrPath);
        getCurrName = file.getName();
        getCurrSize = file.length();

        getScreenSize();
        common = new Common(context, VideoActivity.this, screenWidth);

        if (common.sharedPreferencesOpration(Constants.SHARED, "ContinuePlay", 0, 1, false) == 1) {
            flags[0] = true;
        }
        else {
            flags[0] = false;
        }

        if (common.sharedPreferencesOpration(Constants.SHARED, "BackAndForward", 0, 1, false) == 1) {
            flags[1] = true;
        }
        else {
            flags[1] = false;
        }

        if (common.sharedPreferencesOpration(Constants.SHARED, "Proportion", 0, 0, false) == 1) {
            flags[2] = true;
            isFullScreen = true;
        }
        else {
            flags[2] = false;
            isFullScreen = false;
        }

        if (common.sharedPreferencesOpration(Constants.SHARED, "DolbyCertification", 0, 1, false) == 1) {
            flags[4] = true;
            mDolbyCertification = 1;
        }
        else {
            flags[4] = false;
            mDolbyCertification = 0;
        }

        if (common.sharedPreferencesOpration(Constants.SHARED, "showBitRate", 0, 0, false) == 1) {
            flags[5] = true;
            showBitRate = 1;
        }
        else {
            flags[5] = false;
            showBitRate = 0;
        }

        tools = new Tools();
        list = new ArrayList < HashMap < String, Object >> ();
        marklist = new ArrayList < HashMap < String, Object >> ();
        operater = new DBOperateHelper();
        ctrlBarHandler = new Handler();
        mThread  = new MyThread();
        vHandler = new Handler();
        nameSizeDismissHandler = new Handler();
        nameSizeDismissThread = new NameSizeDismissThread();
        dolbyDisplayThread = new DolbyDisplayThread();
        canRunInThread();
        videoView = (HisiVideoView) findViewById(R.id.videoView);
        videoSeekBar = (SeekBar) findViewById(R.id.videoSeekBar);
        mediaInfoLayout = (RelativeLayout) findViewById(R.id.mediaInfo);
        timeTextView = (TextView) findViewById(R.id.timeText);
        mediaControllerLayout = findViewById(R.id.mediaControllerLayout);
        mediaControllerLayout.setVisibility(View.INVISIBLE);
        mediaInfoLayout.getBackground().setAlpha(100);
        setVideoScale(Constants.SCREEN_FULL);
        videoSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        videoView.setOnPreparedListener(mPreparedListener);
        videoView.setOnCompletionListener(mCompletionListener);
        videoView.setOnErrorListener(mOnErrorListener);
        videoView.setOnFastBackwordCompleteListener(mFastBackwordCompleteListener);
        videoView.setVideoPath(getCurrPath);
        showLoadProgressDialog();
    }

	    
	    private void updateCurrentPosition(){//when change file but no perform on Stop
	    	if (database != null) {
			int currPosition = videoView.getCurrentPosition();
            ContentValues values = new ContentValues();
            Log.i(TAG,"currPosition:"+currPosition+" total:"+total);

            if ((total - 10000) > currPosition) {
                values.put("last_play_postion", currPosition);
                database.update(Constants.TABLE_VIDEO, values, "_data=?",
                                new String[] { getPlayPath });
            }
            else {
                values.put("last_play_postion", 0);
                database.update(Constants.TABLE_VIDEO, values, "_data=?",
                                new String[] { getPlayPath });
            }
        }
	    	
	    	
	    }
    OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
        public void onStopTrackingTouch(SeekBar seekBar) {}
        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                startTime = System.currentTimeMillis();
                long now = System.currentTimeMillis();
                if ((now - mLastSeekTime) > 300)
                {
                	long tmpProgress = progress;//避免progress*videoView.getDuration()的指超出int取值范围
                	progerssFwRwind = ((tmpProgress*videoView.getDuration())/seekBar.getMax());
                }
            }
        }
    };

    OnPreparedListener mPreparedListener = new OnPreparedListener() {
        public void onPrepared(HiMediaPlayer mp) {
            inflateAudioInfo();
            inflateSubtitleInfo();
            setVideoCvrs();
            setVideoStep();
            checkDbData(getPlayPath);
            Parcel mediaInfo = videoView.getMediaInfo();
            mediaInfo.readInt();
            int mFormat = mediaInfo.readInt();
            mediaInfo.readInt();
            int rate = mediaInfo.readInt();
            Log.i(TAG,"=======================================rate is:"+rate);
            float ratedetial = (float)rate/1000000;
            ratedetial=(float)(Math.round(ratedetial*1000))/1000;

            String bitRateContent = bitRateBegin + String.valueOf(ratedetial) + bitRateEnd;
            bitRate.setText(bitRateContent);

            long mSize = mediaInfo.readLong();
            getCurrSize = mSize;
			mediaInfo.recycle();
            isThreadStart = false;

            if (m3DMode == true && mFormat == HI_VIDEOFORMAT_TYPE_MVC) {
                Log.i(TAG, "HI_VIDEOFORMAT_TYPE_MVC");
                videoView.setStereoStrategy(1);
                mDisplayManager.SetStereoOutMode(1);
                mCurrentMode = 1;
                mMVCType = 1;
            }

            if (!isAfterReturn) {
                lastPosition = getLastPosition(getCurrPath);
                start();

                if (mDolbyCertification == 1)
                { nameSizeDismissHandler.postDelayed(dolbyDisplayThread, 3000); }
                else
                { doExtraOpration(); }

                mp.seekTo(lastPosition);
                isAfterReturn = true;
            }
            else if (mModelBDInfo != null && mModelBDInfo.getChapterPosition() != 0) {
                if (mModelBDInfo.getChapterPosition() < videoView.getDuration()) {
                    if (mDolbyCertification == 1)
                    { nameSizeDismissHandler.postDelayed(dolbyDisplayThread, 3000); }
                    else
                    { doExtraOpration(); }

                    videoView.seekTo(mModelBDInfo.getChapterPosition());
                    start();
                }
            }
            else if (mModelDVDInfo != null && mModelDVDInfo.getChapterPosition() != 0) {
                if (mModelDVDInfo.getChapterPosition() < videoView.getDuration()) {
                    if (mDolbyCertification == 1)
                    { nameSizeDismissHandler.postDelayed(dolbyDisplayThread, 3000); }
                    else
                    { doExtraOpration(); }

                    videoView.seekTo(mModelDVDInfo.getChapterPosition());
                    start();
                }
            }
            else if (flags[0] == true) {
                if (isContinue && (lastPosition = getLastPosition(getPlayPath)) / 1000 > 0) {
                    getFlashPlayDialog();
                }
                else {
                    start();

                    if (mDolbyCertification == 1)
                    { nameSizeDismissHandler.postDelayed(dolbyDisplayThread, 3000); }
                    else
                    { doExtraOpration(); }

                    if (openFromMark > 0) {
                        videoView.seekTo(openFromMark);
                        openFromMark = -1;
                    }
                }
            }
            else {
                start();

                if (mDolbyCertification == 1)
                { nameSizeDismissHandler.postDelayed(dolbyDisplayThread, 3000); }
                else
                { doExtraOpration(); }

                if (openFromMark > 0) {
                    videoView.seekTo(openFromMark);
                    openFromMark = -1;
                }
            }

            dismissProgressDialog();
            //operater.updatePlayTime(VideoActivity.this, getCurrId);
        }
    };

    OnCompletionListener mCompletionListener = new OnCompletionListener() {
        public void onCompletion(HiMediaPlayer mp) {
            isContinue = false;
            Common.isShowLoadingToast = false;
            updatePositon(getPlayPath);
            mp.setLooping(false);

            if (common.getMode() == Constants.ALLNOCYCLE) {
                if (mediaFileList != null) {
                    getVideoInfo_noCycle(mediaFileList.getNextVideoInfo_NoCycle(null));

                    if (Common.isLastMediaFile) {
                        finishPlayer();
                    }
                    else {
                        videoView.setVideoPath(getCurrPath);
                    }
                }
                else {
                    finishPlayer();
                }
            }
            else if (common.getMode() == Constants.ALLCYCLE) {
                if (mediaFileList != null) {
                    getVideoInfo(mediaFileList.getNextVideoInfo(null));
                }
                else {
                    finishPlayer();
                }
            }
            else if (common.getMode() == Constants.ONECYCLE) {
                mp.setLooping(true);
                videoView.setVideoPath(getCurrPath);
            }
            else if (common.getMode() == Constants.ONENOCYCLE) {
                finishPlayer();
            }
            else if (common.getMode() == Constants.RANDOM) {
                if (mediaFileList != null) {
                    getVideoInfo(mediaFileList.getRandomVideoInfo(null));
                }
                else {
                    finishPlayer();
                }
            }

            initSeekSecondaryProgress();
            videoView.setStereoVideoFmt(0);
            mDisplayManager.SetRightEyeFirst(0);
            mDisplayManager.SetStereoOutMode(HI_2D_MODE);
            mType = 0;
            mMVCType = 0;
            mCurrentMode = 0;
        }
    };

    private void finishPlayer() {
        ctrlBarHandler.removeCallbacks(mThread);
        play.setBackgroundResource(R.drawable.play_button);
        //        play.setImageResource(R.drawable.hisil_ic_media_play);
        activity.finish();
        return;
    }

    OnFastBackwordCompleteListener mFastBackwordCompleteListener = new OnFastBackwordCompleteListener() {
        public void onFastBackwordComplete(HiMediaPlayer mp) {
            play.setBackgroundResource(R.drawable.pause_button);
            //          play.setImageResource(R.drawable.hisil_ic_media_ff);
            playStatus.setVisibility(View.INVISIBLE);
            forwardRate = 1;
            rewindRate = 1;
            rewindOrForward = false;
        }
    };

    OnErrorListener mOnErrorListener = new OnErrorListener() {
        public boolean onError(HiMediaPlayer mp, int what, int extra) {
            int messageId;

            if (mediaFileListService != null) {
                mediaFileListService.setStopFlag(true);
            }

            if (what == HiMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK) {
                messageId = com.android.internal.R.string.VideoView_error_text_invalid_progressive_playback;
            }
            else {
                messageId = com.android.internal.R.string.VideoView_error_text_unknown;
            }

            if (mediaFileList != null) {
                if (mediaFileList.getCurrentPathVideoSize()==1) {
                    notSupportDialog(messageId);
                } else {
                    Toast.makeText(VideoActivity.this, getResources().getString(messageId),
                            Toast.LENGTH_LONG).show();
                    Common.isResume = false;
                    play.setBackgroundResource(R.drawable.pause_button);
                    // play.setImageResource(R.drawable.hisil_ic_media_ff);
                    initSeekSecondaryProgress();
                    Common.isShowLoadingToast = true;
                    isContinue = true;
                    getVideoInfo(mediaFileList.getNextVideoInfo(null));
                }
            }
            return false;
        }
    };

    public class ButtonClickListener implements OnClickListener {
        public void onClick(View v) {
            if (v == play) {
                doForPlayorPause();
            }
            else if (v == page_up) {
                doForPageUP();
            }
            else if (v == page_down) {
                doForPageDown();
            }
            else if (v == rewind) {
                setRewind();
            }
            else if (v == forward) {
                setForward();
            }

            startThread();
        }
    }
    

    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView < ? > arg0, View arg1, int position,
        long arg3) {
        	
        	finishHandle.removeMessages(ACTIVITY_FINISH);
			
        	
        	switch (position){
        		case 0 :
        			menuDialog.dismiss();
        			File file=new File(getCurrPath);
        			
        			String DolbyVideo = null;
        			if (mDolbyCertification == 1) {
        	        	Parcel reply = getInfo(HiMediaPlayerInvoke.CMD_GET_VIDEO_INFO);
        	            reply.readInt();
        	            int format = reply.readInt();
        	            if (videoFormatValue.length <= format) {
        					return;
        				}
        	            DolbyVideo = videoFormatValue[format];
        	        }
        			
        			int vDuration = videoView.getDuration();
        			/*	String resolution=videoView.getWidth()+"*"+videoView.getHeight();*/
        			
        			TimingInfo ti=UmtvManager.getInstance().getSourceManager().getTimingInfo();
        			String resolution=ti.getiWidth()+" × "+ti.getiHeight();
        			
        			String mDolbyInfo = null;
    	            List<String> audioList = videoView.getAudioTrackLanguageList();

    	            if (audioList != null && audioList.size() != 0) {
    	                String currAudio = audioList.get(videoView.getSelectAudioTrackId());
    	                currAudio = currAudio.substring(2, currAudio.length());
    	                //mDolbyInfo = getResources().getString(R.string.DolbyTrack) + currAudio;
    	                mDolbyInfo = currAudio;
    	            }
    	            else {
    	                //mDolbyInfo = getResources().getString(R.string.DolbyTrack) + getResources().getString(R.string.noTrack);
    	                mDolbyInfo = getResources().getString(R.string.noTrack);
    	            }
        	        
    	            FileUtil fileutil= new FileUtil(context);
        			fileutil.showFileInfo(file,DolbyVideo,vDuration,mDolbyInfo,resolution);
        			fileutil.getDialog().setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface arg0) {
							// TODO Auto-generated method stub
							menuDialog.show();
							delay();
						}
					});
        			break;
    		    case 3 ://å£°é“
    		    	menuDialog.dismiss();
        			listviewItemClick(3);
        			//menuList.setSelection(3);
        			/*if(subtitleSetDialog!=null){
        				
        			}*/
    		
        			break;
        		case 1://å›¾åƒæ¨¡å¼
        			menuDialog.dismiss();
        			Intent intent_pic = new Intent("cn.com.unionman.umtvsetting.picture.service.ACTION");
                	context.startService(intent_pic);
        			
        			//listviewItemClick(3);
        			break;
        		case 2://ä¼´éŸ³æ¨¡å¼
        			//listviewItemClick(4);
        			menuDialog.dismiss();
        			Intent intent_voice = new Intent("cn.com.unionman.umtvsetting.sound.service.ACTION");
        			context.startService(intent_voice);
        			break;
        		case 4 ://å£°é“
        			if(videoView.getAudioTrackNumber() != 0){
            			listviewItemClick(4);
        			}
        			//menuList.setSelection(3);
        			/*if(subtitleSetDialog!=null){
        				
        			}*/
    		
        			break;
        		default:
        			break;
        	}
        	
        	
        	
        	
        	/*è§†é¢‘æ’­æ”¾åŽŸç”Ÿè®¾ç½®èœå•ç³»ç»Ÿè®¾ç½®
            if (m3DMode) {
                switch (position) {
                    case 0 :
                            subControl();

                        if ((menuDialog != null)) {
                            menuDialog.dismiss();
                        }

                        break;

                    case 1:
                        if (mDolbyCertification == 0) {
                            showAudioListDialog();
                        }
                        else if (mDolbyCertification == 1) {
                            audioControl();

                            if ((menuDialog != null)) {
                                menuDialog.dismiss();
                            }
                        }

                        break;

                    case 2:
                        set3DTiming();

                        if ((menuDialog != null)) {
                            menuDialog.dismiss();
                        }

                        break;

                    case 3:
                        set3DDialog();

                        if ((menuDialog != null)) {
                            menuDialog.dismiss();
                        }

                        break;

                    case 4:
                        showAdvancedOptions();
                        break;

                    default:
                        break;
                }
            }
            else {
                switch (position) {
                    case 0:
                        subControl();

                        if ((menuDialog != null)) {
                            menuDialog.dismiss();
                        }

                        break;

                    case 1:
                        if (mDolbyCertification == 0) {
                            showAudioListDialog();
                        }
                        else if (mDolbyCertification == 1) {
                            audioControl();

                            if ((menuDialog != null)) {
                                menuDialog.dismiss();
                            }
                        }

                        break;

                    case 2:
                        set3DDialog();

                        if ((menuDialog != null)) {
                            menuDialog.dismiss();
                        }

                        break;

                    case 3:
                        showAdvancedOptions();
                        break;

                    default:
                        break;
                }
            }
        */}
    };

    OnItemClickListener subItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView < ? > arg0, View arg1, int arg2,
        long arg3) {
        	Log.i(TAG, "============onItemClick===========");
            switch (arg2) {
                case 0 :
                	    finishHandle.removeMessages(SUBDIALOG_FINISH);
                        subSelect(500, -120);
                    break;

                case 1 :
                	    finishHandle.removeMessages(SUBDIALOG_FINISH);
                    showSubtitleSelectDialog();
                    break;

                case 2 :
                	
                	/*if (subtitleSetDialog != null)
                    { subtitleSetDialog.dismiss(); }*/
                	finishHandle.removeMessages(SUBDIALOG_FINISH);
                	
                	WindowManager.LayoutParams lp=subtitleSetDialog.getWindow().getAttributes();
                	lp.alpha=0.0f;
                	subtitleSetDialog.getWindow().setAttributes(lp);
                	
                    String[] subSettingItems = getResources().getStringArray(R.array.sub_adv_items);
                    setAdvSubtitleDialog(R.string.subsetTitle, subSettingItems, subAdvItemClickListener);

                    

                    break;
            }
        }
    };
    OnItemClickListener audioItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView < ? > arg0, View arg1, int arg2,
        long arg3) {
            switch (arg2) {
                case 0 :
                        showAudioListDialog();
                    break;

                case 1 :
                    subSeekbar(0, -230, R.string.DolbyRangeInfo, 100);
                    break;
            }
        }
    };

    OnItemClickListener subAdvItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView < ? > arg0, View arg1, int arg2,
        long arg3) {
            switch (arg2) {
               /* case 0:
                    setTimeDialog(0,-230,R.string.time);
                    break;*/

               /* case 1:
                    int pos = encodeList.size() - 1;
                    String[] encode = new String[pos + 1];
                    int selectedEncode = getSubEncode(HiMediaPlayerInvoke.CMD_GET_SUB_FONT_ENCODE);

                    for (int i = 0; i < encodeList.size(); i++) {
                        encode[i] = encodeList.get(i).getEncodeName();

                        if (selectedEncode == encodeList.get(i).getEncodeValue()) {
                            pos = i;
                        }
                    }

                    selectedSubEncode = common.sharedPreferencesOpration(Constants.SHARED, "subtitleEncode", 0, 0, false);

                    if (selectedSubEncode == 0) {
                        pos = 0;
                    }

                    setSubDialog(500, -150, screenWidth / 2, -1, pos, encode, new EachItemSelectedListener("encode"),
                                 new EachItemClickListener("encode"));
                    break;*/

                case 0:
                	
                	if(subCount!=0){
                		final String[] colors = getResources().getStringArray(R.array.colors);
                    	
                        int position = 0;
                        for (int i = 0; i < colorValue.length; i++) {
                             if (colorValue[i].equals(selectedColor)) {
                                 position = i;
                                 break;
                             }
                        }

                        /*  setSubDialog(500, -120, screenWidth / 3, -1, position, colors, new EachItemSelectedListener("colors"),
                                      new EachItemClickListener("colors"));*/

        				int tcolorIndex = position;
     
    			        int [] dialog_item_img = new int[]{
    			   			R.color.transparent,  
    		 	   			R.color.transparent, 
    		 	   			R.color.transparent,
    		 	   		    R.color.transparent,
    		 	   	        R.color.transparent,
    		 	   			R.color.transparent};
        			    dialog_item_img[tcolorIndex] = R.drawable.net_select; 
        		  	    final AlertDialog.Builder  malertdialog =new AlertDialog.Builder(context,R.style.Dialog_item);
        		        malertdialog.setTitle(getResources().getStringArray(R.array.sub_adv_items)[1]);
        		        LayoutInflater factory = LayoutInflater.from(context);
        		        View myView = factory.inflate(R.layout.menu_sounddialog_layout,null);
        		        final ListView lisview = (ListView) myView.findViewById(R.id.menu_soundsetting_list);
        		        final ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
        			    for (int i=0; i<colors.length; i++) {
    	    				HashMap<String, Object> map =  new HashMap<String, Object>(); 
    	    				map.put("ItemContext", colors[i]); 
    	    				map.put("ItemImg", dialog_item_img[i]); 		
    	    				listDialog.add(map);}
        			    final SimpleAdapter mSimpleAdapter = new SimpleAdapter(context, listDialog, R.layout.menu_soundsetting_item_dialog, 
        					                                         new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
        		        lisview.setAdapter(mSimpleAdapter);
        			    lisview.setSelection(tcolorIndex);
        		        lisview.setOnItemClickListener(new OnItemClickListener() {
        				@Override
        				public void onItemClick(AdapterView<?> arg0, View arg1,
        						int selectItemScolor, long arg3) {
        	
        					selectedColor = colorValue[selectItemScolor];
        	                setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_COLOR, Integer.parseInt(selectedColor.replace("0x", ""), 16));
        	                selectedColorPos =  selectItemScolor;
        	                common.sharedPreferencesOpration(Constants.SHARED, "selectedColor", selectedColorPos, 0, true);
        				    int [] item_dialog_item_img = new int[]{
        		      	   			R.color.transparent,  
        		       	   			R.color.transparent, 
        		       	   			R.color.transparent,
        		       	   			R.color.transparent,
        		       	   			R.color.transparent,
        		       	   			R.color.transparent, 
        		       	   			R.color.transparent 
        		      	      };
        		    	      item_dialog_item_img[selectItemScolor] = R.drawable.net_select;
        		    	      listDialog.clear();
        			     		for (int i=0; i<colors.length; i++) {
        			     			HashMap<String, Object> map =  new HashMap<String, Object>();
        			        		map.put("ItemContext", colors[i]); 
        			        		map.put("ItemImg", item_dialog_item_img[i]); 		
        			        		listDialog.add(map);}
        			     		mSimpleAdapter.notifyDataSetChanged();} });
    		    		     malertdialog.setView(myView);
    		    		     mColorAlertDialog = malertdialog.create();
    		    		     mColorAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
    		    		     mColorAlertDialog.show();
    		    		     subcolordelay();
    		    		     mColorAlertDialog.setOnDismissListener(new OnDismissListener() {
    		    				
    		    				@Override
    		    				public void onDismiss(DialogInterface arg0) {
    		    					// TODO Auto-generated method stub
    		    					//subtitleAdvSetDialog.dismiss();
    		    					subadvdelay();
    		    				}});
    		    		     lisview.setOnKeyListener(new View.OnKeyListener() {
    		    				
    		    				@Override
    		    				public boolean onKey(View arg0, int keycode, KeyEvent event) {
    		    					// TODO Auto-generated method stub
    		    					if(keycode == KeyEvent.KEYCODE_DPAD_DOWN || keycode == KeyEvent.KEYCODE_DPAD_UP ||keycode == KeyEvent.KEYCODE_DPAD_CENTER ){
    		    						subcolordelay();
    		    					}
    		    					return false;
    		    				}
    		    			});
                	}
                	
    	
                    break;

                case 1:
                    subSeekbar(0, -230, R.string.size, 100);
                    break;

                    /*case 4:
                        subSeekbar(0, -230, R.string.position, screenHeight);
                        break;*/
                case 2:
                    final String[] effect = getResources().getStringArray(R.array.effect);
                    /*setSubDialog(500, -120, screenWidth / 3, -1, selectedEffect, effect,
                                 new EachItemSelectedListener("effect"), new EachItemClickListener("effect"));*/
     
                    int eposition = 0;
                   /* for (int i = 0; i < effect.length; i++) {
                         if (effect[i].equals(selectedEffect)) {
                             eposition = i;
                             break;
                         }
                    }*/
                    eposition = selectedEffect;
    				int teffectIndex = eposition;
 
			        int [] dialog_item_eimg = new int[]{
			   			R.color.transparent,  
		 	   			R.color.transparent, 
		 	   			R.color.transparent,
		 	   		    R.color.transparent,
		 	   	        R.color.transparent,
		 	   			R.color.transparent};
    			    dialog_item_eimg[teffectIndex] = R.drawable.net_select; 
    		  	    final AlertDialog.Builder  emalertdialog =new AlertDialog.Builder(context,R.style.Dialog_item);
    		  	    emalertdialog.setTitle(getResources().getStringArray(R.array.sub_adv_items)[3]);
    	
    		        LayoutInflater efactory = LayoutInflater.from(context);
    		        View myeView = efactory.inflate(R.layout.menu_sounddialog_layout,null);
    		        final ListView liseview = (ListView) myeView.findViewById(R.id.menu_soundsetting_list);
    		        final ArrayList<HashMap<String, Object>> elistDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
    			    for (int i=0; i<effect.length; i++) {
	    				HashMap<String, Object> emap =  new HashMap<String, Object>(); 
	    				emap.put("ItemContext", effect[i]); 
	    				emap.put("ItemImg", dialog_item_eimg[i]); 		
	    				elistDialog.add(emap);}
    			    final SimpleAdapter meSimpleAdapter = new SimpleAdapter(context, elistDialog, R.layout.menu_soundsetting_item_dialog, 
    					                                         new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
    		        liseview.setAdapter(meSimpleAdapter);
    			    liseview.setSelection(teffectIndex);
    		        liseview.setOnItemClickListener(new OnItemClickListener() {
    				@Override
    				public void onItemClick(AdapterView<?> arg0, View arg1,
    						int selectItemEffect, long arg3) {
    	
    					selectedEffect = selectItemEffect;
                        setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_STYLE, selectItemEffect);
                        common.sharedPreferencesOpration(Constants.SHARED, "selectedEffect", selectedEffect, 0, true);
                        
    				    int [] item_dialog_item_eimg = new int[]{
    		      	   			R.color.transparent,  
    		       	   			R.color.transparent, 
    		       	   			R.color.transparent,
    		       	   			R.color.transparent,
    		       	   			R.color.transparent,
    		       	   			R.color.transparent, 
    		       	   			R.color.transparent 
    		      	      };
    		    	      item_dialog_item_eimg[selectedEffect] = R.drawable.net_select;
    		    	      elistDialog.clear();
    			     		for (int i=0; i<effect.length; i++) {
    			     			HashMap<String, Object> emap =  new HashMap<String, Object>();
    			        		emap.put("ItemContext", effect[i]); 
    			        		emap.put("ItemImg", item_dialog_item_eimg[i]); 		
    			        		elistDialog.add(emap);}
    			     		meSimpleAdapter.notifyDataSetChanged();} });
		    		     emalertdialog.setView(myeView);
		    		     mEffectAlertDialog = emalertdialog.create();
		    		     mEffectAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
		    		     mEffectAlertDialog.show();
		    		     subeffedelay();
		    		     mEffectAlertDialog.setOnDismissListener(new OnDismissListener() {
		    				@Override
		    				public void onDismiss(DialogInterface arg0) {
		    					// TODO Auto-generated method stub
		    					//subtitleAdvSetDialog.dismiss();
		    					subadvdelay();
		    				}});
		    		     liseview.setOnKeyListener(new View.OnKeyListener() {
		    				
		    				@Override
		    				public boolean onKey(View arg0, int keycode, KeyEvent event) {
		    					// TODO Auto-generated method stub
		    					if(keycode == KeyEvent.KEYCODE_DPAD_DOWN || keycode == KeyEvent.KEYCODE_DPAD_UP ||keycode == KeyEvent.KEYCODE_DPAD_CENTER ){
		    						subeffedelay();
		    					}
		    					return false;
		    				}
		    			});
            
                    break;

                case 3:
                    subSeekbar(0, -230, R.string.line_space, 100);
                    break;

                case 4:
                    subSeekbar(0, -230, R.string.space, 100);
                    break;

                default:
                    break;
            }
        }
    };

    class EachItemClickListener implements OnItemClickListener {
        private String tag;

        EachItemClickListener(String tag) {
            this.tag = tag;
        }
        public void onItemClick(AdapterView < ? > arg0, View arg1, int arg2,
                                long arg3) {
            if ("no".equals(tag))
            {}
            else if ("colors".equals(tag)) {
                selectedColor = colorValue[arg2];
                setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_COLOR, Integer.parseInt(selectedColor.replace("0x", ""), 16));
                selectedColorPos =  arg2;
                common.sharedPreferencesOpration(Constants.SHARED, "selectedColor", selectedColorPos, 0, true);
            }
            else if ("tracks".equals(tag)) {
                selectedTrack = arg2;
                setNewFounction(HiMediaPlayerInvoke.CMD_SET_AUDIO_TRACK_PID, arg2);
            }
            else if ("audio_chan_mode".equals(tag)) {
                selectedAudio = arg2;
                setNewFounction(HiMediaPlayerInvoke.CMD_SET_AUDIO_CHANNEL_MODE, arg2);
                common.sharedPreferencesOpration(Constants.SHARED, "channer", arg2, 0, true);
            }
            else if ("effect".equals(tag)) {
                selectedEffect = arg2;
                setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_STYLE, arg2);
                common.sharedPreferencesOpration(Constants.SHARED, "selectedEffect", selectedEffect, 0, true);
            }
            else if ("change".equals(tag)) {
                setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_ID, arg2);
            }
            else if ("encode".equals(tag)) {
                int selectedEncode = encodeList.get(arg2).getEncodeValue();
                {
                    setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_ENCODE, selectedEncode);
                    common.sharedPreferencesOpration(Constants.SHARED, "subtitleEncode", selectedEncode, 0, true); /*Get user's setting of play mode*/       /*CNcomment: ä¿å­˜å­—å¹•é€‰æ‹© */
                }
            }

            startTime1 = System.currentTimeMillis();
        }
    }

    class EachItemSelectedListener implements OnItemSelectedListener {
        private String tag;

        EachItemSelectedListener(String tag) {
            this.tag = tag;
        }

        public void onItemSelected(AdapterView < ? > arg0, View arg1, int arg2,
                                   long arg3) {
            startTime1 = System.currentTimeMillis();
        }

        public void onNothingSelected(AdapterView < ? > arg0) {}
    }

    private void setTimeDialog(int x,int y,int titleId){
        LayoutInflater layout   = activity.getLayoutInflater();
        RelativeLayout relative = (RelativeLayout)layout.inflate(R.layout.timeset, null);
        TextView timetitle = (TextView)relative.findViewById(R.id.timesettitle);
        timetitle.setText(titleId);
        Button sure = (Button)relative.findViewById(R.id.timebutton);
        final EditText time = (EditText)relative.findViewById(R.id.timevalue);
        sure.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String timevalue = time.getText().toString();
                try {
                    int value = Integer.parseInt(timevalue);
                    subTime(value);
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }});
        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
                || (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    dialog.cancel();
                }

                return false;
            }
        });
        common.setDialog(dialog, relative, x, y, 5);
    }

    protected Dialog onCreateDialog(int id) {
        Dialog d = null;

        switch (id) {
            case 1 :
                d = getPathDialog();
                break;

            case 2 :
                d = getMarkDialog();
                break;

            case 3 :
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.video_no_set_tag).setPositiveButton(R.string.confirm, null);
                d = builder.create();
                break;

            default:
                break;
        }

        return d;
    }

    protected Dialog getDefaultSettingDialog(int pTitleId, View pView) {
        Dialog _Dialog = new Dialog(this, R.style.styleDialog);

        if (pTitleId != -1)
        { _Dialog.setTitle(pTitleId); }
        _Dialog.setContentView(pView);
      
        return _Dialog;
    }
    private void setVideoCvrs() {
        if (isFullScreen)
        { videoView.setVideoCvrs(Constants.SCREEN_FULL); }
        else
        { videoView.setVideoCvrs(Constants.SCREEN_DEFAULT); }
    }
    private void setVideoStep() {
        int duration = videoView.getDuration() / 1000;

        if (duration < 2 * 60) {
            Log.i(TAG, "setVideoStep:1,1");
            common.setStep(3);
            common.setInitStep(3);
            common.setAccStep(3);
        }
        else if (duration < 10 * 60) {
            Log.i(TAG, "setVideoStep:1,2");
            common.setStep(3);
            common.setInitStep(3);
            common.setAccStep(5);
        }
        else if (duration < 30 * 60) {
            Log.i(TAG, "setVideoStep:5,5");
            common.setStep(5);
            common.setInitStep(5);
            common.setAccStep(5);
        }
        else {
            Log.i(TAG, "setVideoStep:5,10");
            common.setStep(5);
            common.setInitStep(5);
            common.setAccStep(10);
        }
    }
    private void subSelect(int x, int y) {
    	/*List<String> _List = new ArrayList <String>();
        _List.add(getString(R.string.showsub));
        _List.add(getString(R.string.closesub));
        if (_List == null) {
            return;
        }
        
        AlertDialog.Builder _Builder = new AlertDialog.Builder(this);
        _Builder.setTitle(getString(R.string.subsetTitle));
        String[] _Strings = new String[_List.size()];
        _Builder.setSingleChoiceItems(_List.toArray(_Strings),
        isShowSub, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface pDialog, int pWhich) {
                if (pWhich == 0) {
                    isShowSub = 0;
                    videoView.enableSubtitle(isShowSub);
                    //videoView.setSubVertical(100);
                }
                else if (pWhich == 1) {
                    isShowSub = 1;
                    videoView.enableSubtitle(isShowSub);
                }

                pDialog.dismiss();
            }
        });
        _Builder.setOnItemSelectedListener(new EachItemSelectedListener("subSelect"));
        Dialog _Dialog = _Builder.show();
        DialogTool.disableBackgroundDim(_Dialog);
        DialogTool.setDefaultSelectDisplay(_Dialog);
        dialogAutoDismiss(_Dialog);*/
    	
    
    	LayoutInflater mLinflater = activity.getLayoutInflater();       
        View mView = mLinflater.inflate(R.layout.dialog_layout, null);
        ListView selSubItemsList =(ListView) mView.findViewById(R.id.setting_list);//æ³¨æ„å¼ºè½¬å¦åˆ™ç±»åž‹ä¸åŒ¹é…   
        final ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	
        final String [] subitle =new String[]{
        		getString(R.string.showsub),
        		getString(R.string.closesub)
        };
        int [] dialog_item_img = new int[]{
	   			R.color.transparent,  
 	   			R.color.transparent
	      };
        
	      dialog_item_img[isShowSub] = R.drawable.net_select;
		for (int i=0; i<subitle.length; i++) {
			HashMap<String, Object> map =  new HashMap<String, Object>(); 
			map.put("ItemContext", subitle[i]); 
			map.put("ItemImg", dialog_item_img[i]); 		
			listDialog.add(map);
		}
        final SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listDialog, R.layout.setting_rabtn_dialog, 
                new String[] {"ItemContext","ItemImg"}, new int[]{R.id.sing_dialog_item,R.id.sing_dialog_img});
        selSubItemsList.setAdapter(mSimpleAdapter);
        selSubItemsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				if(position!=isShowSub){
					videoView.enableSubtitle(position);
					isShowSub=position; }
				int [] dialog_item_img = new int[]{
		   			R.color.transparent,  
	 	   			R.color.transparent };
			    dialog_item_img[position] = R.drawable.net_select;
			    listDialog.clear();
			    for (int i=0; i<subitle.length; i++) {
					HashMap<String, Object> map =  new HashMap<String, Object>(); 
					map.put("ItemContext", subitle[i]); 
					map.put("ItemImg", dialog_item_img[i]); 		
					listDialog.add(map); }
			    mSimpleAdapter.notifyDataSetChanged();
			}
		});
        AlertDialog.Builder _Builder = new AlertDialog.Builder(this,R.style.Dialog_backgroundDimEnabled_false);
       /* AlertDialog  _Dialog =  _Builder.create(); 
        _Dialog.show();
        _Dialog.getWindow().setContentView(mView);
        dialogAutoDismiss(_Dialog); */
        
        mSubAlertDialog =  _Builder.create(); 
        mSubAlertDialog.show();
        mSubAlertDialog.getWindow().setContentView(mView);
        
        
        subisdelay();
        mSubAlertDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				subdelay();
			}
		});
        mSubAlertDialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
			   if(    
					arg1 == KeyEvent.KEYCODE_DPAD_UP
					||arg1 == KeyEvent.KEYCODE_DPAD_DOWN
					||arg1 == KeyEvent.KEYCODE_DPAD_CENTER){
				   subisdelay();
				}
				return false;
			}
		});
   
        
    }

    private void subSeekbar(int x, int y, final int title, final int valueMax) {
        LayoutInflater layout   = activity.getLayoutInflater();
        RelativeLayout relative = (RelativeLayout)layout.inflate(R.layout.setseekbar, null);
        TextView seekbarTitle = (TextView)relative.findViewById(R.id.seekBarTitle);
        final TextView seekbarValue = (TextView)relative.findViewById(R.id.seekBarValue);
        SeekBar seekbar = (SeekBar)relative.findViewById(R.id.seekBar);

        if (title == R.string.time) {
            int max = 100;
            seekbarTitle.setText(R.string.time);
            seekbar.setMax(max);

            if (selectedTime == -1) {
                seekbarValue.setText("0ms");
                seekbar.setProgress(50);
            }
            else {
                seekbarValue.setText(selectedTime + "ms");
                seekbar.setProgress((selectedTime + Constants.timeMax) * max / valueMax);
            }
        }
        else if (title == R.string.position) {
            int max = 100;
            seekbarTitle.setText(R.string.position);
            seekbar.setMax(max);

            if (selectedPosition == -1) {
                int pro = 5;
                seekbar.setProgress(pro);
                int hight = valueMax * pro / max;
                seekbarValue.setText("" + hight);
            }
            else {
                seekbarValue.setText("" + selectedPosition);
                seekbar.setProgress(selectedPosition * max / valueMax);
            }
        }
        else if (title == R.string.size) {
            int max = 20;
            seekbarTitle.setText(R.string.size);
            seekbar.setMax(max);

            if (selectedSizes == -1) {
                seekbarValue.setText("25");
                seekbar.setProgress(5);
            }
            else {
                seekbarValue.setText("" + selectedSizes);
                seekbar.setProgress(selectedSizes * max / valueMax);
            }
        }
        else if (title == R.string.space) {
            int max = 20;
            seekbarTitle.setText(R.string.space);
            seekbar.setMax(20);

            if (selectedSpace == -1) {
                seekbarValue.setText("10");
                seekbar.setProgress(2);
            }
            else {
                seekbarValue.setText("" + selectedSpace);
                seekbar.setProgress(selectedSpace * max / valueMax);
            }
        }
        else if (title == R.string.line_space) {
            int max = 20;
            seekbarTitle.setText(R.string.line_space);
            seekbar.setMax(20);

            if (selectedLSpace == -1) {
                seekbarValue.setText("20");
                seekbar.setProgress(4);
            }
            else {
                seekbarValue.setText("" + selectedLSpace);
                seekbar.setProgress(selectedLSpace * max / valueMax);
            }
        }
        else if (title == R.string.DolbyRangeInfo) {
            int max = 100;
            seekbarTitle.setText(R.string.DolbyRangeInfo);
            seekbar.setMax(100);

            if (selectedDolbyRangeInfo == -1) {
                seekbarValue.setText("20");
                seekbar.setProgress(4);
            }
            else {
                seekbarValue.setText("" + selectedDolbyRangeInfo);
                seekbar.setProgress(selectedDolbyRangeInfo * max / valueMax);
            }
        }

        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {}
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser) {
                if (title == R.string.time) {
                    selectedTime = progress * valueMax / seekBar.getMax()
                                   - Constants.timeMax;
                    subTime(selectedTime);
                    seekbarValue.setText(selectedTime + "ms");
                }
                else if (title == R.string.position) {
                    selectedPosition = progress * valueMax / seekBar.getMax();
                    setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_VERTICAL, selectedPosition);
                    seekbarValue.setText("" + selectedPosition);
                    common.sharedPreferencesOpration(Constants.SHARED, "selectedPosition", selectedPosition, 0, true);
                }
                else if (title == R.string.size) {
                    if (progress <= 3) {
                        progress = 3;
                        seekBar.setProgress(progress);
                    }

                    selectedSizes = progress * valueMax / seekBar.getMax();
                    setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_SIZE, selectedSizes);
                    seekbarValue.setText("" + selectedSizes);
                    common.sharedPreferencesOpration(Constants.SHARED, "subtitleSizes", selectedSizes, 0, true);
                }
                else if (title == R.string.space) {
                    selectedSpace = progress * valueMax / seekBar.getMax();
                    setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_SPACE, selectedSpace);
                    seekbarValue.setText("" + selectedSpace);
                    common.sharedPreferencesOpration(Constants.SHARED, "selectedSpace", selectedSpace, 0, true);
                }
                else if (title == R.string.line_space) {
                    selectedLSpace = progress * valueMax / seekBar.getMax();
                    setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_LINESPACE, selectedLSpace);
                    seekbarValue.setText("" + selectedLSpace);
                    common.sharedPreferencesOpration(Constants.SHARED, "selectedLSpace", selectedLSpace, 0, true);
                }
                else if (title == R.string.DolbyRangeInfo) {
                    selectedDolbyRangeInfo = progress * valueMax / seekBar.getMax();
                    setNewFounction(HiMediaPlayerInvoke.CMD_SET_DOLBY_RANGEINFO, selectedDolbyRangeInfo);
                    seekbarValue.setText("" + selectedDolbyRangeInfo);
                    common.sharedPreferencesOpration(Constants.SHARED, "selectedDolbyRangeInfo", selectedDolbyRangeInfo, 0, true);
                    toast.showMessage(context, getString(R.string.DolbyRangeInfoToast), Toast.LENGTH_SHORT);
                }

                startTime1 = System.currentTimeMillis();
            }
        });
        Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
                || (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    dialog.cancel();
                }

                return false;
            }
        });
        common.setDialog(dialog, relative, x, y, 5);
        dialogAutoDismiss(dialog);
    }

    public void setSubPathDialog() {
        LayoutInflater inflater = activity.getLayoutInflater();
        RelativeLayout relative = (RelativeLayout)inflater.inflate(R.layout.setsubpath, null);
        subPath = (EditText)relative.findViewById(R.id.subPath);
        final Button subPathButton = (Button)relative.findViewById(R.id.subPathButton);
        final Button subconfirm = (Button)relative.findViewById(R.id.subconfirm);
        final Button subcancel = (Button)relative.findViewById(R.id.subcancle);
        final Dialog subDialog = new Dialog(context, R.style.dialog);
        OnClickListener subPathListener = new OnClickListener() {
            public void onClick(View v) {
                if (v == subPathButton) {
                    Intent intent = new Intent();
                    intent.setClassName("com.hisilicon.android.mediacenter",
                                        "com.hisilicon.android.mediacenter.activity.TabBarExample");
                    intent.putExtra("subFlag", true);
                    activity.startActivity(intent);
                }
                else if (v == subconfirm) {
                    String path   = subPath.getText().toString().trim();
                    String suffix = path.substring(path.lastIndexOf(".") + 1, path.length());

                    if (!((path == null) || (path == "") || (path.length() == 0))) {
                        String[] subStyle = {"srt", "sub", "lrc", "sst"};
                        int subStyleCount = subStyle.length;
                        int i   = 0;
                        int ret = subPath(path);

                        if (ret != 0) {
                            toast.showMessage(context, R.string.wrongsub, Toast.LENGTH_SHORT);
                            subControl();
                        }
                    }

                    subDialog.cancel();
                }
                else if (v == subcancel) {
                    subDialog.cancel();
                }
            }
        };
        subPathButton.setOnClickListener(subPathListener);
        subconfirm.setOnClickListener(subPathListener);
        subcancel.setOnClickListener(subPathListener);
        common.setDialog(subDialog, relative, 0, -230, 3);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 0:
                subPath.setText(data.getStringExtra("path"));
                break;

            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void set3DDialog() {
        String[] stereoItems_3D_1 = getResources().getStringArray(R.array.items_2D_3D);
        String[] stereoItems_3D_2 = getResources().getStringArray(R.array.items_3D_2D);
        String[] stereoItems_3D_3 = getResources().getStringArray(R.array.items_2D_2D_1);
        String[] stereoItems_3D_4 = getResources().getStringArray(R.array.items_2D_2D_2);
        mCurrent3DMode = mDisplayManager.GetStereoOutMode();
        Log.i(TAG, "mCurrentMode:" + mCurrentMode + " mCurrent3DMode:" + mCurrent3DMode);
        int mode = mCurrent3DMode;

        if (mode == 0 && mCurrentMode == 1 && mMVCType == 1 && m3DMode == true)
        { mCurrentMode = 0; }
        else if (mode == 0 && (mCurrentMode == 2 || mCurrentMode == 3) && mMVCType == 0 && m3DMode == true)
        { mCurrentMode = 4; }
        
        LayoutInflater mLinflater = activity.getLayoutInflater();
        
        View mView = mLinflater.inflate(R.layout.setting_view, null);
        ListView stereoItemsList =(ListView) mView.findViewById(R.id.menuoptions_list);//æ³¨æ„å¼ºè½¬å¦åˆ™ç±»åž‹ä¸åŒ¹é…
       
        //ListView stereoItemsList = new ListView(context);
        //stereoItemsList.setBackgroundResourcor);
        //stereoItemsList.setSelector(getResources().getDrawable(R.drawable.item_background_selector));

        if (mCurrentMode == 0 && mMVCType == 0 && m3DMode == true) {
            stereoItemsList.setAdapter(new MenuListAdapter(VideoActivity.this, stereoItems_3D_1, 1, 8, true));
            stereoItemsList.setSelection(1);
        }
        else if (mCurrentMode == 0 && mMVCType == 1 && m3DMode == true) {
            stereoItemsList.setAdapter(new MenuListAdapter(VideoActivity.this, stereoItems_3D_1, 1, 7, true));
        }
        else if ((mCurrentMode == 1 || mCurrentMode == 2 || mCurrentMode == 3) && m3DMode == true) {
            stereoItemsList.setAdapter(new MenuListAdapter(VideoActivity.this, stereoItems_3D_2, 1, 3, true));
        }
        else if (mCurrentMode == 0 && m3DMode == false) {
            stereoItemsList.setAdapter(new MenuListAdapter(VideoActivity.this, stereoItems_3D_3, 1, 3, true));
        }
        else if ((mCurrentMode == 1 && m3DMode == false) || mCurrentMode == 4) {
            stereoItemsList.setAdapter(new MenuListAdapter(VideoActivity.this, stereoItems_3D_4, 1, 3, true));
        }

        stereoItemDialog = getDefaultSettingDialog(R.string.stereoMode_change, mView);
        //stereoItemDialog = getDefaultSettingDialog(R.string.stereoMode_change, stereoItemsList);
        //Common.setDialogWidth(stereoItemDialog, screenWidth / 2, 500, -120);
        stereoItemDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                popUpMenuDialog();
            }
        });
        stereoItemDialog.show();
        dialogAutoDismiss(stereoItemDialog);
        stereoItemsList.setOnItemSelectedListener(new EachItemSelectedListener("3D"));
        stereoItemsList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView < ? > arg0,
            View arg1, int arg2, long arg3) {
                stereoItemDialog.dismiss();

                switch (arg2) {
                    case 0 :
                        if (m3DMode) {
                            if (mCurrentMode == 0) {
                                mDisplayManager.SetStereoOutMode(HI_3D_MODE);
                                mCurrentMode = 1;
                            }
                            else if (mCurrentMode == 1) {
                                mDisplayManager.SetStereoOutMode(HI_2D_MODE);
                                mCurrentMode = 0;
                            }
                            else if (mCurrentMode == 2 || mCurrentMode == 3
                            || mCurrentMode == 3 || mCurrentMode == 4) {
                                videoView.setStereoVideoFmt(0);
                                mDisplayManager.SetStereoOutMode(HI_2D_MODE);
                                mCurrentMode = 0;
                            }
                        }
                        else {
                            if (mCurrentMode == 0) {
                                videoView.setStereoVideoFmt(1);
                                mDisplayManager.SetStereoOutMode(HI_2D_MODE);
                                mCurrentMode = 1;
                            }
                            else if (mCurrentMode == 1) {
                                videoView.setStereoVideoFmt(0);
                                mDisplayManager.SetStereoOutMode(HI_2D_MODE);
                                mCurrentMode = 0;
                            }
                        }

                        break;

                    case 1:
                        if (m3DMode) {
                            if (mCurrentMode == 0) {
                                videoView.setStereoVideoFmt(1);
                                mDisplayManager.SetStereoOutMode(HI_3D_MODE);
                                mCurrentMode = 2;
                            }
                            else if (mCurrentMode == 1 || mCurrentMode == 2) {
                                if (mDisplayManager.GetRightEyeFirst() == 0) {
                                    mDisplayManager.SetRightEyeFirst(1);
                                }
                                else if (mDisplayManager.GetRightEyeFirst() == 1) {
                                    mDisplayManager.SetRightEyeFirst(0);
                                }
                            }
                        }
                        else {
                            videoView.setStereoVideoFmt(2);
                            mDisplayManager.SetStereoOutMode(HI_2D_MODE);
                            mCurrentMode = 1;
                        }

                        break;

                    case 2:
                        if (mCurrentMode == 0) {
                            videoView.setStereoVideoFmt(2);
                            mDisplayManager.SetStereoOutMode(HI_3D_MODE);
                            mCurrentMode = 3;
                        }

                    case 3:
                        if (mCurrentMode == 0) {
                            videoView.setStereoVideoFmt(1);
                            mDisplayManager.SetStereoOutMode(HI_2D_MODE);
                            mCurrentMode = 4;
                        }

                    case 4:
                        if (mCurrentMode == 0) {
                            videoView.setStereoVideoFmt(2);
                            mDisplayManager.SetStereoOutMode(HI_2D_MODE);
                            mCurrentMode = 4;
                        }

                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void set3DTiming() {
        String[] stereoItems_3D_Timing = getResources().getStringArray(R.array.items_3D_Timing);
        mCurrent3DMode = mDisplayManager.GetStereoOutMode();
        int mode = mCurrent3DMode;
        Log.i(TAG, "mCurrentMode:" + mCurrentMode + " mCurrent3DMode:" + mCurrent3DMode + " Fmt:" + mDisplayManager.getFmt());
        ListView stereoItemsList = new ListView(context);
        //stereoItemsList.setBackgroundResource(R.drawable.dialog_background_selector);
        stereoItemsList.setSelector(getResources().getDrawable(R.drawable.item_background_selector));
        stereoItemsList.setAdapter(new MenuListAdapter(VideoActivity.this, stereoItems_3D_Timing, 1, 3, true));

        if (mode == 0)
        { stereoItemsList.setSelection(mode); }
        else
        { stereoItemsList.setSelection(mode - 1); }

        stereoItemDialog = getDefaultSettingDialog(R.string.stereoMode_change, stereoItemsList);
        //Common.setDialogWidth(stereoItemDialog, screenWidth / 2, 500, -120);
        stereoItemDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                popUpMenuDialog();
            }
        });
        stereoItemDialog.show();
        dialogAutoDismiss(stereoItemDialog);
        stereoItemsList.setOnItemSelectedListener(new EachItemSelectedListener("3D"));
        stereoItemsList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView < ? > arg0,
            View arg1, int arg2, long arg3) {
                stereoItemDialog.dismiss();

                switch (arg2) {
                    case 0 :
                        HI_3D_MODE = 1;

                        if (mCurrent3DMode != 1) {
                            Log.i(TAG, "HI_3D_MODE:" + HI_3D_MODE);
                            mDisplayManager.SetStereoOutMode(HI_3D_MODE);
                        }

                        break;

                    case 1 :
                        HI_3D_MODE = 2;

                        if (mCurrent3DMode != 2) {
                            Log.i(TAG, "HI_3D_MODE:" + HI_3D_MODE);

                            if (mFmt == HiDisplayManager.ENC_FMT_1080i_50 || mFmt == HiDisplayManager.ENC_FMT_1080i_60)
                            { mDisplayManager.SetStereoOutMode(HI_3D_MODE); }
                            else if (mFmt == HiDisplayManager.ENC_FMT_720P_50) {
                                mDisplayManager.setFmt(HiDisplayManager.ENC_FMT_1080i_50);
                                mDisplayManager.SetStereoOutMode(HI_3D_MODE);
                            }
                            else if (mFmt == HiDisplayManager.ENC_FMT_720P_60) {
                                mDisplayManager.setFmt(HiDisplayManager.ENC_FMT_1080i_60);
                                mDisplayManager.SetStereoOutMode(HI_3D_MODE);
                            }
                            else {
                                mDisplayManager.setFmt(HiDisplayManager.ENC_FMT_1080i_50);
                                mDisplayManager.SetStereoOutMode(HI_3D_MODE);
                            }
                        }

                        break;

                    case 2 :
                        HI_3D_MODE = 3;

                        if (mCurrent3DMode != 3) {
                            Log.i(TAG, "HI_3D_MODE:" + HI_3D_MODE);

                            if (mFmt == HiDisplayManager.ENC_FMT_720P_50 || mFmt == HiDisplayManager.ENC_FMT_720P_60 || mFmt == HiDisplayManager.ENC_FMT_1080P_24)
                            { mDisplayManager.SetStereoOutMode(HI_3D_MODE); }
                            else {
                                DispFmt mDispFmt = mDisplayManager.GetDisplayCapability();

                                if (mDispFmt.ENC_FMT_1080P_24 == 1) {
                                    mDisplayManager.setFmt(HiDisplayManager.ENC_FMT_1080P_24);
                                    mDisplayManager.SetStereoOutMode(HI_3D_MODE);
                                }
                                else if (mFmt == HiDisplayManager.ENC_FMT_1080i_50) {
                                    mDisplayManager.setFmt(HiDisplayManager.ENC_FMT_720P_50);
                                    mDisplayManager.SetStereoOutMode(HI_3D_MODE);
                                }
                                else if (mFmt == HiDisplayManager.ENC_FMT_1080i_60) {
                                    mDisplayManager.setFmt(HiDisplayManager.ENC_FMT_720P_60);
                                    mDisplayManager.SetStereoOutMode(HI_3D_MODE);
                                }
                                else {
                                    mDisplayManager.setFmt(HiDisplayManager.ENC_FMT_720P_60);
                                    mDisplayManager.SetStereoOutMode(HI_3D_MODE);
                                }
                            }
                        }

                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void setAudioTrack() {
        Parcel getCount;
        getCount = getInfo(HiMediaPlayerInvoke.CMD_GET_AUDIO_INFO);
        getCount.readInt();
        int trackCount = getCount.readInt();
        String[] tracks = {getResources().getString(R.string.noTrack)};

        if (trackCount > 0) {
            tracks = new String[trackCount];

            for (int i = 0; i < trackCount; i++) {
                tracks[i] = getCount.readString();
                getCount.readInt();
                getCount.readInt();
                getCount.readInt();

                if (tracks[i].equals("")) {
                    tracks[i] = getResources().getString(R.string.soundTrack) + (i + 1);
                }
            }
        }
		getCount.recycle();
		
        setSubDialog(500, -120, screenWidth / 3, -1, selectedTrack, tracks,
                     new EachItemSelectedListener("tracks"), new EachItemClickListener("tracks"));
    }

    private void setMark() {
        String[] markItems = getResources().getStringArray(R.array.items_mark);
        /*LayoutInflater mLinflater = activity.getLayoutInflater();
        
        View mView = mLinflater.inflate(R.layout.setting_view, null);
        ListView markItemsList =(ListView) mView.findViewById(R.id.menuoptions_list);//æ³¨æ„å¼ºè½¬å¦åˆ™ç±»åž‹ä¸åŒ¹é…
*/      ListView markItemsList = new ListView(context);
        markItemsList.setBackgroundResource(R.drawable.dialog_background_selector);
        markItemsList.setSelector(getResources().getDrawable(R.drawable.item_background_selector));
        markItemsList.setAdapter(new MenuListAdapter(VideoActivity.this, markItems, 1, 0, true, 2));
        //final Dialog markItemDialog = getDefaultSettingDialog(R.string.select_tag, mView);
        final Dialog markItemDialog = getDefaultSettingDialog(R.string.select_tag, markItemsList);
        Common.setDialogWidth(markItemDialog, screenWidth / 2, 500, -120);
        markItemDialog.show();
        markItemsList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView < ? > arg0, View arg1,
            int arg2, long arg3) {
                markItemDialog.dismiss();

                switch (arg2) {
                    case 0 :
                        setMarkDialog();
                        break;

                    case 1:
                        if (isMarkDBChange) {
                            list.clear();
                            list = getMark();
                            isMarkDBChange = false;
                        }

                        marklist = getMarkByPath(getCurrPath);

                        if ((marklist.size() == 0) || (marklist == null)) {
                            showDialog(3);
                        }
                        else {
                            currPath = getCurrPath;
                            isCurrVideoMark = true;
                            showDialog(2);
                        }

                        break;

                    case 2:
                        if (isMarkDBChange) {
                            list.clear();
                            list = getMark();
                            isMarkDBChange = false;
                        }

                        showDialog(1);
                        break;

                    case 3:
                        delAllMarksDialog();
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void setMarkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final int mark = videoView.getCurrentPosition();
        RelativeLayout relative = (RelativeLayout) getLayoutInflater().inflate(R.layout.setmark, null);
        final EditText markname = (EditText) relative.findViewById(R.id.markname);
        markname.setWidth(screenWidth / 2);
        builder.setTitle(getString(R.string.markpointStr) + " " + Common.getTimeFormatValue(mark));
        builder.setView(relative);
        builder.setPositiveButton(R.string.savemarkStr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                saveMark(markname, mark);
            }
        }).setNegativeButton(R.string.cancelmarkStr, null);
        builder.create().show();
    }

    private void popUpMenuDialog() {/*
        DispFmt mDispFmt = mDisplayManager.GetDisplayCapability();

        if (mDispFmt == null) {
            m3DMode = false;
        }
        else if (mDispFmt.is_support_3d == 0) {
            m3DMode = false;
        }
        else if (mDispFmt.is_support_3d == 1) {
            m3DMode = true;
        }

        if (m3DMode) {
            menuItems = getResources().getStringArray(R.array.items_3D_mode);
        }
        else {
            menuItems = getResources().getStringArray(R.array.items_normal_mode);
        }
        LayoutInflater mLinflater = activity.getLayoutInflater();
       
        View mView = mLinflater.inflate(R.layout.setting_view, null);
        ListView menuList =(ListView) mView.findViewById(R.id.menuoptions_list);//æ³¨æ„å¼ºè½¬å¦åˆ™ç±»åž‹ä¸åŒ¹é…

        if (getCurrPath.startsWith("dvd:") || (getCurrMode != null && getCurrMode.equals("video/dvd")))
        { menuList.setAdapter(new MenuListAdapter(this, menuItems, 1, 9, true, 0)); }
        else if (m3DMode)
        { menuList.setAdapter(new MenuListAdapter(this, menuItems, 1, 0, true, 0)); }
        else
        { menuList.setAdapter(new MenuListAdapter(this, menuItems, 1, 0, true, 1)); }

        menuList.setOnItemSelectedListener(new EachItemSelectedListener("menu"));
        menuList.setOnItemClickListener(itemClickListener);
        //menuList.setBackgroundResource(R.drawable.dialog_background_selector);
        //menuList.setSelector(getResources().getDrawable(R.drawable.item_background_selector));
        menuDialog = getDefaultSettingDialog(R.string.video_control_menu, mView);

        //Common.setDialogWidth(menuDialog, screenWidth / 2, 500, -120);
        menuDialog.show();
        dialogAutoDismiss(menuDialog);
    */
    	 
         track_mode = new String[]{
     			getResources().getString(R.string.stereo) ,
     			getResources().getString(R.string.joint_stereo),
     			getResources().getString(R.string.left_track),
     			getResources().getString(R.string.right_track)
     	 };
         
    	modeVal = new String []{
        		getResources().getString( R.string.picmode_standard_string), 
        		getResources().getString( R.string.picmode_dynamic_string),  
        		getResources().getString( R.string.picmode_softness_string),  
        		getResources().getString( R.string.picmode_user_string)
           };
    	sound_mode = new String[]{
    			getResources().getString(R.string.sndmode_standard_string) ,
    			getResources().getString(R.string.sndmode_movie_string),
    			getResources().getString(R.string.sndmode_music_string),
    			getResources().getString(R.string.sndmode_dialog_string),
    			getResources().getString(R.string.sndmode_user_string),
    	 };
    	  sound_mode_flag = new int[]{
     			0,
     			1,
     			2,
     			4,
     			8,
     	 };
    	  LayoutInflater mLinflater = activity.getLayoutInflater();
          
    	  mView = mLinflater.inflate(R.layout.menu_view, null);
          menuList =(ListView) mView.findViewById(R.id.menuview_list);//æ³¨æ„å¼ºè½¬å¦åˆ™ç±»åž‹ä¸åŒ¹é…  
          
          /*int smode =UmtvManager.getInstance().getAudio().getSoundMode(); 
	      int  smodeIndex= FileUtil.getIndexFromArray(smode,FileUtil.sound_mode);
	      String soundModeStr="";
	      soundModeStr = getResources().getString(FileUtil.sound_mode[smodeIndex][1]);
	      
	      int mode = UmtvManager.getInstance().getPicture().getPictureMode();  
	       int modeIndex= FileUtil.getIndexFromArray(mode,
	    		   FileUtil.picture_mode);
	      String videoModeStr="";
	      videoModeStr = getResources().getString(FileUtil.picture_mode[modeIndex][1]);*/
	      
	      String TrackModeStr="";
	    	int tmode = UmtvManager.getInstance().getAudio().getTrackMode();
	    	int  tmodeIndex= FileUtil.getIndexFromArray(tmode,FileUtil.track_mode);
	    	TrackModeStr = getResources().getString(FileUtil.track_mode[tmodeIndex][1]);
	    	
	    	String AudioTrackStr=getResources().getString(R.string.no_track);
	    	if (videoView.getAudioTrackNumber() != 0) {
	    		
	    		AudioTrackStr= videoView.getAudioTrackLanguageList().get(videoView.getSelectAudioTrackId());
	    	}
	    

	     
          /*listItemVals = new String[]{
        		     null,
        		     TrackModeStr,null,null
      			}; */

	    	
	    	listItemVals = new String[]{
       		     null,null,null,null,AudioTrackStr
     			}; 
          
          listItemRightImgs = new int[]{
          		R.drawable.touming,
      			R.drawable.touming,
      			R.drawable.touming,
      			R.drawable.touming,
      			R.drawable.selector_view_right_gred
      			}; 
      	
      	listItemLeftImgs = new int[]{
      			R.drawable.touming,
      			R.drawable.touming,
      			R.drawable.touming,
      			R.drawable.touming,
      			R.drawable.selector_view_left_gred
      			}; 
      	listItems = new String[]{
          		getResources().getStringArray(R.array.videomenu)[0],
          		getResources().getStringArray(R.array.videomenu)[1],
          		getResources().getStringArray(R.array.videomenu)[2],
          		getResources().getStringArray(R.array.videomenu)[3],
          		getResources().getStringArray(R.array.videomenu)[4]
      	};
      	listName.clear();
      	for (int i=0; i<listItems.length; i++) {
      	    HashMap<String, Object> map = new HashMap<String, Object>(); 
      		map.put("ItemContext", listItems[i]);
      		map.put("ItemVal",listItemVals[i]);
      		map.put("ItemRightImg",listItemRightImgs[i]);
      		map.put("ItemLeftImg",listItemLeftImgs[i]);
      		listName.add(map);
      	}
      	 menuSchedule = new SimpleAdapter(this, listName,
  									                R.layout.menu_view_option, 
  									                new String[] {"ItemContext","ItemVal","ItemRightImg","ItemLeftImg"},      
  									                new int[] {R.id.setting_option_item_txt,R.id.setting_option_item_val,R.id.right_arrow_img,R.id.left_arrow_img});
          menuList.setAdapter(menuSchedule);

          menuList.setOnItemSelectedListener(new EachItemSelectedListener("menu"));
          menuList.setOnItemClickListener(itemClickListener);
          
          menuDialog = getDefaultSettingDialog(R.string.video_control_menu, mView);
          menuDialog.show();
          delay();
          menuDialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
			   if(    
					arg1 == KeyEvent.KEYCODE_DPAD_LEFT
					||arg1 == KeyEvent.KEYCODE_DPAD_RIGHT
					||arg1 == KeyEvent.KEYCODE_DPAD_UP
					||arg1 == KeyEvent.KEYCODE_DPAD_DOWN){
				   delay();
				}
			   
				int position = menuList.getSelectedItemPosition();
			   if (arg2.getAction() == KeyEvent.ACTION_DOWN && position==4 && (arg1==KeyEvent.KEYCODE_DPAD_LEFT || arg1==KeyEvent.KEYCODE_DPAD_RIGHT)) {  
					listviewItemClick(4);					
				}
				return false;
			}
		});
          //dialogAutoDismiss(menuDialog);
    }

    public void setSubDialog(int x, int y, int width, int title, int selectedItem, String[] subSettingItems,
                             OnItemSelectedListener subItemSelectedListener,
                             OnItemClickListener subAdvItemClickListener) {
        ListView subItemList = new ListView(context);

        subItemList.setFocusableInTouchMode(true);
        subItemList.setAdapter(new MenuListAdapter((Activity)context, subSettingItems, 1, 0, true, -1));
        subItemList.setSelection(selectedItem);
        subItemList.setOnItemSelectedListener(subItemSelectedListener);
        subItemList.setOnItemClickListener(subAdvItemClickListener);
        //subItemList.setBackgroundResource(R.drawable.dialog_background_selector);
        subSetDialog = getDefaultSettingDialog(title, subItemList);
        subSetDialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                //dialogDismiss();
                int type = 0;
                Parcel replytype = getInfo(HiMediaPlayerInvoke.CMD_GET_SUB_ISBMP);
                replytype.readInt();
                type = replytype.readInt();
                hiType2 = type;
				replytype.recycle();
                if ((hiType1 != hiType2) && (subtitleSetDialog != null)) {
                    subtitleSetDialog.dismiss();
                    subControl();
                }
            }
        });
        Common.setDialogWidth(subSetDialog, width, x, y);
        subSetDialog.show();
        dialogAutoDismiss(subSetDialog);
    }

    public void setSubtitleDialog(int title, String[] subSettingItems, OnItemClickListener subItemClickListener) {
        
    	//ListView subItemList = new ListView(context);
        
   	 	LayoutInflater mLinflater = activity.getLayoutInflater();
     
   	 	View mView = mLinflater.inflate(R.layout.setting_view, null);
   	 	ListView subItemList =(ListView) mView.findViewById(R.id.menuoptions_list);//æ³¨æ„å¼ºè½¬å¦åˆ™ç±»åž‹ä¸åŒ¹é…
        //subItemList.setBackgroundResourcor);
        //subItemList.setSelector(getResources().getDrawable(R.drawable.item_background_selector));
        subItemList.setAdapter(new MenuListAdapter(activity, subSettingItems, 1, 0, true, 2));
        subItemList.setOnItemSelectedListener(new EachItemSelectedListener("setSubtitle"));
        subItemList.setOnItemClickListener(subItemClickListener);
        //subtitleSetDialog = getDefaultSettingDialog(title, subItemList);
        subtitleSetDialog = getDefaultSettingDialog(title, mView);
        //Common.setDialogWidth(subtitleSetDialog, screenWidth / 2, 500, -120);
        /*subtitleSetDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                popUpMenuDialog();
            }
        });*/
        subtitleSetDialog.show();
       // dialogAutoDismiss(subtitleSetDialog);
        
        
        subdelay();
        subtitleSetDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				menuDialog.show();
				delay();
			}
		});
        subtitleSetDialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
			   if(    
					arg1 == KeyEvent.KEYCODE_DPAD_UP
					||arg1 == KeyEvent.KEYCODE_DPAD_DOWN){
				    subdelay();
				}
				return false;
			}
		});
    }
    public void setAudioDialog(int title, String[] audioSettingItems, OnItemClickListener audioItemClickListener) {
        //ListView audioItemList = new ListView(context);
        //audioItemList.setBackgroundResourcor);
    	LayoutInflater mLinflater = activity.getLayoutInflater();     
   	 	View mView = mLinflater.inflate(R.layout.setting_view, null);
   	 	ListView audioItemList =(ListView) mView.findViewById(R.id.menuoptions_list);
   	 	
        //audioItemList.setSelector(getResources().getDrawable(R.drawable.item_background_selector));
        audioItemList.setAdapter(new MenuListAdapter(activity, audioSettingItems, 1, 0, true, 4));
        audioItemList.setOnItemSelectedListener(new EachItemSelectedListener("setSubtitle"));
        audioItemList.setOnItemClickListener(audioItemClickListener);
        audioSetDialog = getDefaultSettingDialog(title, mView);
        //Common.setDialogWidth(audioSetDialog, screenWidth / 2, 500, -120);
        audioSetDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                popUpMenuDialog();
            }
        });
        audioSetDialog.show();
        dialogAutoDismiss(audioSetDialog);
    }
    public void setAdvSubtitleDialog(int title, String[] subSettingItems, OnItemClickListener subAdvItemClickListener) {
        int type = 0;
        int subType = 0;
        Parcel reply = getInfo(HiMediaPlayerInvoke.CMD_GET_SUB_INFO);
        reply.readInt();
        subCount = reply.readInt();
		reply.recycle();
        Log.i(TAG, "SubCount:" + subCount);
        //ListView subItemList = new ListView(context);
        //subItemList.setBackgroundResourcor);
        LayoutInflater mLinflater = activity.getLayoutInflater();     
   	 	View mView = mLinflater.inflate(R.layout.setting_view, null);
   	 	ListView subItemList =(ListView) mView.findViewById(R.id.menuoptions_list);
        //subItemList.setSelector(getResources().getDrawable(R.drawable.item_background_selector));

        if (subCount == 0) {
            subType = 0;
            subItemList.setAdapter(new MenuListAdapter(activity, subSettingItems, subCount, subType, true, 3));
            Log.i(TAG, "there is no sub,subCount:" + subCount);
            
            subItemList.getSelector().setAlpha(0);
        }
        else if (subCount != 0) {
            if (isShowSub == 1) {
                Log.i(TAG, "sub is hiding");
                subItemList.setAdapter(new MenuListAdapter(activity, subSettingItems, subCount, subType, true, 3));
            }
            else if (isShowSub == 0) {
                if (subCount == 1) {
                    Parcel replytype = getInfo(HiMediaPlayerInvoke.CMD_GET_SUB_ISBMP);
                    replytype.readInt();
                    type = replytype.readInt();
					replytype.recycle();
                    hiType1 = type;
                    Log.i(TAG, "there is one sub,and the subtype is:" + hiType1);

                    if (type == 0) {
                        subType = 1;
                    }
                    else if (type == 1) {
                        subType = 2;
                    }

                    switch (subType) {
                        case 1: {
                                subItemList.setAdapter(new MenuListAdapter(activity, subSettingItems, subCount, subType, true, 3));
                                break;
                            }

                        case 2: {
                                subItemList.setAdapter(new MenuListAdapter(activity, subSettingItems, subCount, subType, true, 3));
                                break;
                            }
                    }
                }
                else if (subCount >= 2) {
                    Parcel replytype = getInfo(HiMediaPlayerInvoke.CMD_GET_SUB_ISBMP);
                    replytype.readInt();
                    type = replytype.readInt();
					replytype.recycle();

                    hiType1 = type;
                    Log.i(TAG, "there are more than one subs,and the subtype is:" + hiType1);

                    if (type == 0) {
                        subType = 3;
                    }
                    else if (type == 1) {
                        subType = 4;
                    }

                    switch (subType) {
                        case 3: {
                                subItemList.setAdapter(new MenuListAdapter(activity, subSettingItems, subCount, subType, true, 3));
                                break;
                            }

                        case 4: {
                                subItemList.setAdapter(new MenuListAdapter(activity, subSettingItems, subCount, subType, true, 3));
                                break;
                            }
                    }
                }
            }
        }

        subItemList.setOnItemSelectedListener(new EachItemSelectedListener("setAdvSubtitle"));
        subItemList.setOnItemClickListener(subAdvItemClickListener);
        subtitleAdvSetDialog = getDefaultSettingDialog(title, mView);
        //Common.setDialogWidth(subtitleAdvSetDialog, screenWidth / 2, 500, -120);
        /*subtitleAdvSetDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                subControl();
            }
        });*/
        subtitleAdvSetDialog.show();
        /*ialogAutoDismiss(subtitleAdvSetDialog);*/
        
        
        subadvdelay();
        subtitleAdvSetDialog.setOnDismissListener(new OnDismissListener() {
 			
 			@Override
 			public void onDismiss(DialogInterface arg0) {
 				// TODO Auto-generated method stub
 				
 				WindowManager.LayoutParams lp=subtitleSetDialog.getWindow().getAttributes();
            	lp.alpha=1.0f;
            	subtitleSetDialog.getWindow().setAttributes(lp);
 				/*subtitleSetDialog.dismiss();*/
            	subdelay();
 			}
 		});
        subtitleAdvSetDialog.setOnKeyListener(new OnKeyListener() {
 			
 			@Override
 			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
 				// TODO Auto-generated method stub
 			   if(    
 					arg1 == KeyEvent.KEYCODE_DPAD_UP
 					||arg1 == KeyEvent.KEYCODE_DPAD_DOWN
 					||arg1 == KeyEvent.KEYCODE_DPAD_CENTER){
 				   subadvdelay();
 				}
 				return false;
 			}
 		});
    }

    private void getFlashPlayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.continue_playvideo);
        StringBuffer msgstr = new StringBuffer();
        msgstr.append(getString(R.string.willjumpto));
        msgstr.append(" ");
        msgstr.append(Common.getTimeFormatValue(lastPosition));
        msgstr.append(" ");
        msgstr.append(getString(R.string.toplay));
        builder.setMessage(msgstr.toString());
        builder.setPositiveButton(R.string.yes,
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                common.setPositive(true);
            }
        }).setNegativeButton(R.string.not, null);
        pointDialog = builder.create();
        pointDialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                if (common.isPositive()) {
                    videoView.seekTo(lastPosition);
                }
                else {
                    updatePositon(getCurrPath);
                }

                start();
                doExtraOpration();
                common.setPositive(false);
            }
        });
        pointDialog.show();
    }

    private void notSupportDialog(int msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(com.android.internal.R.string.VideoView_error_title);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.confirm, null);
        Dialog notSptDialog = builder.create();
        notSptDialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                activity.finish();
            }
        });
        notSptDialog.show();
    }

    private void getPlayModeDialog() {
        ListView modeListView = new ListView(VideoActivity.this);
        ArrayAdapter <String> adapter = new ArrayAdapter <String>(VideoActivity.this,
                                                                  android.R.layout.simple_list_item_1,
                                                                  android.R.id.text1,
                                                                  mode);
        modeListView.setAdapter(adapter);
        //modeListView.setBackgroundResourcor);
        modeListView.setSelector(getResources().getDrawable(R.drawable.item_background_selector));
        final Dialog modeDialog = getDefaultSettingDialog(R.string.choicePlayMode, modeListView);
        modeListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView < ? > arg0, View arg1, int arg2,
            long arg3) {
                common.switchPlayModel(arg2);
                common.sharedPreferencesOpration(Constants.SHARED, "currPlayMode",
                                                 arg2, 0, true);
                modeDialog.dismiss();
                /*toast.showMessage(VideoActivity.this,
                                  getString(R.string.now_video_playmode) + mode[arg2],
                                  Toast.LENGTH_SHORT);*/
                toast.showMessage(VideoActivity.this,
                        mode[arg2],
                        Toast.LENGTH_SHORT);
            }
        });
        Common.setDialogWidth(modeDialog, screenWidth / 2, 500, -120);
        modeDialog.show();
    }

    private void delAllMarksDialog() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(R.string.del_all);
        builder1.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                database.delete(Constants.VIDEO_BOOKMARK, null, null);
                toast.showMessage(context, R.string.del_success, Toast.LENGTH_SHORT);
                isMarkDBChange = true;
            }
        }).setNegativeButton(R.string.cancle, null);
        builder1.create().show();
    }

    private Dialog getPathDialog() {
        Dialog pathDialog = null;

        if ((list == null) || (list.size() == 0)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(VideoActivity.this);
            builder.setTitle(R.string.no_set_tag).setPositiveButton(R.string.confirm, null);
            pathDialog = builder.create();
        }
        else {
            TreeSet <String> pathSet = new TreeSet <String>();

            for (int i = 0; i < list.size(); i++) {
                pathSet.add(list.get(i).get("path").toString());
            }

            pathstr = pathSet.toArray(new String[pathSet.size()]);
            ListView pathListView = new ListView(VideoActivity.this);
            pathListView.setItemsCanFocus(true);
            pathListView.setAdapter(new PathAdapter());
            pathDialog = new Dialog(context, R.style.dialog);
            pathDialog.setContentView(pathListView);
            Common.setDialogWidth(pathDialog, screenWidth, 0, 0);
        }

        pathDialog.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                removeDialog(1);
            }
        });
        return pathDialog;
    }

    private Dialog getMarkDialog() {
        ListView markListView = new ListView(VideoActivity.this);
        markListView.setItemsCanFocus(true);
        markListView.setAdapter(new MarkAdapter());
        Dialog markDialog = new Dialog(context, R.style.dialog);
        markDialog.setContentView(markListView);
        Common.setDialogWidth(markDialog, screenWidth / 2, 0, 0);
        markDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                removeDialog(2);
            }
        });
        return markDialog;
    }

    class PathAdapter extends BaseAdapter {
        public int getCount() {
            return pathstr.length;
        }

        public Object getItem(int position) {
            return pathstr[position];
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.getpath, null);
                holder = new ViewHolder();
                holder.content = (TextView)convertView.findViewById(R.id.content);
                holder.watch  = (Button) convertView.findViewById(R.id.watch);
                holder.delete = (Button) convertView.findViewById(R.id.delete);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.content.setWidth((int)(screenWidth * 0.5));
            String tempCurrentPath = pathstr[position];
            holder.content.setText(tempCurrentPath.substring(tempCurrentPath.lastIndexOf("/") + 1,
                                                             tempCurrentPath.length()));
            holder.watch.setTag(tempCurrentPath);
            holder.delete.setTag(tempCurrentPath);
            holder.watch.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (marklist != null) {
                        marklist.clear();
                    }

                    currPath = ((Button) v).getTag().toString();
                    marklist = getMarkByPath(currPath);
                    removeDialog(1);
                    showDialog(2);
                    isCurrVideoMark = false;
                }
            });
            holder.delete.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    currPath = ((Button) v).getTag().toString();
                    String sql = "delete from " + Constants.VIDEO_BOOKMARK
                                 + " where _data='" + currPath + "'";
                    database.execSQL(sql);
                    toast.showMessage(VideoActivity.this, R.string.del_success,
                                      Toast.LENGTH_SHORT);
                    isMarkDBChange = true;
                    removeDialog(1);
                }
            });
            return convertView;
        }
    }

    class MarkAdapter extends BaseAdapter {
        public int getCount() {
            return marklist.size();
        }

        public Object getItem(int position) {
            return marklist.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            String mark = marklist.get(position).get("mark").toString();
            LayoutInflater inflater = getLayoutInflater();
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.getmark, null);
                holder = new ViewHolder();
                holder.content = (TextView) convertView.findViewById(R.id.markpointView);
                holder.marknameView = (TextView) convertView.findViewById(R.id.marknameView);
                holder.watch  = (Button) convertView.findViewById(R.id.watch);
                holder.delete = (Button) convertView.findViewById(R.id.delete);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.content.setText(Common.getTimeFormatValue(Long.parseLong(marklist.get(position).get("mark").toString())));
            String markname = marklist.get(position).get("markname").toString().trim();
            String dateStr = marklist.get(position).get("date").toString();

            if ((markname == null) || markname.equals("") || (markname.length() == 0)) {
                holder.marknameView.setText(dateStr);
            }
            else {
                holder.marknameView.setText(markname);
            }

            holder.watch.setTag(mark);
            holder.delete.setTag(mark);
            holder.watch.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    String currMark = ((Button) v).getTag().toString();
                    Integer intmark = new Integer(currMark);

                    if (!isCurrVideoMark) {
                        getCurrPath = currPath;
                        File file = new File(getCurrPath);

                        if (file.exists()) {
                            getCurrName = file.getName();
                            getCurrSize = file.length();
                            openFromMark = intmark;
                            videoView.setVideoPath(getCurrPath);
                        }
                        else {
                            toast.showMessage(context, R.string.nofile,
                                              Toast.LENGTH_SHORT);
                            Log.i(TAG, "there have not this file");
                        }
                    }
                    else {
                        videoView.seekTo(intmark);
                    }

                    isContinue = false;
                    removeDialog(2);
                }
            });
            holder.delete.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    String currMark = ((Button) v).getTag().toString();
                    String sql = "delete from " + Constants.VIDEO_BOOKMARK
                                 + " where bookmark='" + currMark + "'";
                    database.execSQL(sql);
                    toast.showMessage(VideoActivity.this, R.string.del_success,
                                      Toast.LENGTH_SHORT);
                    isMarkDBChange = true;
                    removeDialog(2);
                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        TextView content;
        TextView marknameView;
        Button watch;
        Button delete;
    }

    private void doExtraOpration() {
        total = videoView.getDuration();
        TextView totalTV = (TextView) findViewById(R.id.timetotal);

        if (total > 0) {
            totalTV.setText(Common.getTimeFormatValue(total));
        }
        else {
            totalTV.setText(R.string.nototaltime);
        }

        int temp = total / 1000 / 60;
        common.limitHour   = temp / 60;
        common.limitMinute = temp;
        Common.setDuration(total);
        TextView mediaSize = (TextView) findViewById(R.id.mediaSize);
        TextView mediaName = (TextView) findViewById(R.id.mediaName);

        if (mDolbyCertification == 0) {
            mediaSize.setText(tools.formatSize(getCurrSize));
        }
        else if (mDolbyCertification == 1) {
            Parcel reply = getInfo(HiMediaPlayerInvoke.CMD_GET_VIDEO_INFO);
            reply.readInt();
            int format = reply.readInt();
			reply.recycle();
            if (videoFormatValue.length <= format) {
				return;
			}
            mediaSize.setText(getResources().getString(R.string.DolbyVideo) + videoFormatValue[format] + "\t" + tools.formatSize(getCurrSize));
        }

        mediaName.setText(getCurrName);
        setInfoCue();

        if(nameSizeDismissThread!=null && nameSizeDismissHandler != null)
        	nameSizeDismissHandler.postDelayed(nameSizeDismissThread, Constants.NAME_SIZE_HIDE_TIME);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.showMediaController :
                    msg = obtainMessage(Constants.showMediaController);
                    float time = videoView.getCurrentPosition();
                    timeTextView.setText(Common.getTimeFormatValue((int) time));

                    if (!haveLeftRightOpration) {
                    	if (total == 0)
                    	{
                    		total = videoView.getDuration();
                    	}
                        videoSeekBar.setProgress((int)(time / total * seekBarMax));
                        timeTextView.setText(Common.getTimeFormatValue((int) time));
                    }
                    else {
                        timeTextView.setText(Common.getTimeFormatValue((int)((float)position / seekBarMax * total)));
                    }
                    if (isSeekBarSelected) {
                        videoSeekBar.setThumb(VideoActivity.this.getResources().getDrawable(
                                R.drawable.hisil_yuandian_selected));
                    }

                    sendMessageDelayed(msg, 1000);
                    break;

                case Constants.hideMediaController:
                    removeMessages(Constants.showMediaController);
                    removeMessages(Constants.hideMediaController);
                    break;

                case Constants.switchSubtitle:
                    if (selectedSubId - 1 < 0)
                    { selectedSubId = videoView.getSubtitleNumber() - 1; }
                    else
                    { selectedSubId--; }

                    videoView.setSelectSubtitleId(selectedSubId);
                    videoView.setSubtitleId(selectedSubId);
                    int _SubNum = videoView.getSubtitleNumber();
                    int _ExtNum = videoView.getExtSubtitleNumber();
                    int _Select = selectedSubId;

                    if (_ExtNum != 0) {
                        if (_Select >= (_SubNum - _ExtNum))
                        { _Select = _Select - (_SubNum - _ExtNum); }
                        else if (_Select < (_SubNum - _ExtNum))
                        { _Select = _Select + _ExtNum; }
                    }

                    break;

                case Constants.switchAudioTrack:
                    if (selectedTrack - 1 < 0)
                    { selectedTrack = videoView.getAudioTrackNumber() - 1; }
                    else
                    { selectedTrack--; }
                    
                    videoView.setSelectAudioTrackId(selectedTrack);
                    videoView.setAudioTrackPid(selectedTrack);
                    break;

                default:
                    break;
            }
        }
    };

    class NameSizeDismissThread implements Runnable {
        public void run() {
        	
        	if( mediaControllerLayout.getVisibility() == View.INVISIBLE){
        		mediaInfoLayout.setVisibility(View.INVISIBLE);
        	}
            
            nameSizeDismissHandler.removeCallbacks(this);
        }
    }

    class DolbyDisplayThread implements Runnable {
        public void run() {
            doExtraOpration();
        }
    }

    class MyThread implements Runnable {
        public void run() {
        	if (isThreadStart)
        	{
	            long endTime  = System.currentTimeMillis();
	            long distance = endTime - startTime;
	            
	            if (distance >= Constants.CTRLBAR_HIDE_TIME) {
	                ctrlbarDismiss();
	            }
	
	            ctrlBarHandler.postDelayed(this, 1000);
        	}
        }
    }

    class DThread  implements Runnable {
        Dialog dialog = null;
        Dialog predialog = null;

        public DThread(Dialog dialog) {
            this.dialog = dialog;
        }
        public void SetDialog(Dialog dialog) {
            this.predialog = this.dialog;
            this.dialog = dialog;
        }
        public void run() {
            long endTime  = System.currentTimeMillis();
            long distance = endTime - startTime1;

            if (distance >= Constants.DIALOG_SHOW) {
                if (dialog != null)
                { dialog.dismiss(); }

                if (predialog != null)
                { predialog.dismiss(); }

                dHandler.removeCallbacks(dThread);
                isDShow = false;
            }
            else {
                dHandler.postDelayed(this, 3000);
            }
        }
    }

    class VThread implements Runnable {
        View view = null;

        public VThread(View view) {
            this.view = view;
        }
        public void run() {
            long endTime  = System.currentTimeMillis();
            long distance = endTime - startTime2;

            if (distance >= Constants.VIEW_SHOW) {
                view.setVisibility(View.INVISIBLE);
                vHandler.removeCallbacks(vThread);
                currView = null;
            }
            else {
                vHandler.postDelayed(this, 1000);
            }
        }
    }

    private void dialogAutoDismiss(Dialog dialog) {
        if (!isDShow) {
            dHandler = new Handler();
            dThread = new DThread(dialog);
            startTime1 = System.currentTimeMillis();
            dHandler.post(dThread);
            isDShow = true;
        }
        else {
            if (dThread != null)
            { dThread.SetDialog(dialog); }

            startTime1 = System.currentTimeMillis();
        }
    }

    private void viewInvisible(View nextView) {
        if (nextView != currView) {
            if (currView != null) {
                currView.setVisibility(View.INVISIBLE);
                vHandler.removeCallbacks(vThread);
            }

            currView = nextView;
            vThread = new VThread(nextView);
            startTime2 = System.currentTimeMillis();
            vHandler.post(vThread);
        }
        else {
            startTime2 = System.currentTimeMillis();
        }
    }

    private void startThread() {
        if (!isThreadStart) {
            startTime = System.currentTimeMillis();
            mHandler.sendEmptyMessage(Constants.showMediaController);
            ctrlBarHandler.post(mThread);
            isThreadStart = true;
        }
        else {
            startTime = System.currentTimeMillis();
        }
    }

    private void ctrlbarDismiss() {
        
        
        if(!iSmedialist){
        	mediaInfoLayout.setVisibility(View.INVISIBLE);
        	mediaControllerLayout.setVisibility(View.INVISIBLE);
        	isSeekBarSelected = true;
        }
        
        
        
        initSeekSecondaryProgress();
        ctrlBarHandler.removeCallbacks(mThread);
        if(!iSmedialist){
        	mHandler.sendEmptyMessage(Constants.hideMediaController);
        }
        
        isThreadStart = false;
        isSeekBarSelected = false;
    }

    private void dialogDismiss() {
        if (dHandler != null) {
            dHandler.removeCallbacks(dThread);
            dThread  = null;
            dHandler = null;
        }

        isDShow = false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if ((MotionEvent.ACTION_DOWN == event.getAction()) || (MotionEvent.ACTION_MOVE == event.getAction())) {
            mediaControllerLayout.setVisibility(View.VISIBLE);
            setInfoCue();
            mediaInfoLayout.setVisibility(View.VISIBLE);
            startThread();
            return false;
        }

        return super.onTouchEvent(event);
    }

    public static class ToastUtil {

        private static Handler handler = new Handler(Looper.getMainLooper());

        private static Toast toast = null;

        private static Object synObj = new Object();

        public static void showMessage(final Context act, final String msg) {
            showMessage(act, msg, Toast.LENGTH_SHORT);
        }

        public static void showMessage(final Context act, final int msg) {
            showMessage(act, msg, Toast.LENGTH_SHORT);
        }

        public static void showMessage(final Context act, final String msg,
                                       final int len) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    synchronized (synObj) {
                        if (toast != null) {
                            // toast.cancel();
                            toast.setText(msg);
                            toast.setDuration(len);
                        }
                        else {
                            toast = Toast.makeText(act, msg, len);
                        }

                        toast.show();
                    }
                }
            });
        }

        public static void showMessage(final Context act, final int msg,
                                       final int len) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    synchronized (synObj) {
                        if (toast != null) {
                            // toast.cancel();
                            toast.setText(msg);
                            toast.setDuration(len);
                        }
                        else {
                            toast = Toast.makeText(act, msg, len);
                        }

                        toast.show();
                    }
                }
            });
        }
    }
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean _IsValid = false;

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (haveLeftRightOpration) {
                    videoView.seekTo((int) progerssFwRwind);
                    videoSeekBar.clearFocus();
                    initSeekSecondaryProgress();
                    _IsValid = true;
                    common.setStep(common.getInitStep() / 1000);
                    videoStart();
                }
                break;
            default:
                break;
        }

        if (_IsValid) {
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }
    
    private int count = 0; 
    Handler tmpHander = new Handler() {
   		public void handleMessage(Message msg) {
   			Log.i(TAG, "=======tmpHander========");
   			count = 0;
            super.handleMessage(msg);
        }
   	};
   	
   	
    /**
     * handler of finish dialog
     */
    private Handler videoFinishHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
        	switch (msg.what) {
			case PIC_DIALOG_DISMISS_BYTIME:
				//picAlertdialog.dismiss();
                picdialog.removeAllViews();
				isPicDialogDismiss=true;
				break;
			case SOUND_DIALOG_DISMISS_BYTIME:
				 //soundAlertdialog.dismiss();
                 sounddialog.removeAllViews();
	             isSoundDialogDismiss=true;
				break;
			case ZOOM_DIALOG_DISMISS_BYTIME:
				//zoomAlertdialog.dismiss();
                zoomdialog.removeAllViews();
				isZoomDialogDismiss=true;
				break;
			case TRACK_DIALOG_DISMISS_BYTIME:
				//trackAlertdialog.dismiss();
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
        videoFinishHandle.removeMessages(msg);
        Message message = new Message();
        message.what = msg;
        videoFinishHandle.sendMessageDelayed(message, Constants.DISPEAR_TIME);
    }   	
    
    private static final int PIC_DIALOG_DISMISS_BYTIME = 1;
    private static final int SOUND_DIALOG_DISMISS_BYTIME = 2;
    private static final int ZOOM_DIALOG_DISMISS_BYTIME = 3;
    private static final int TRACK_DIALOG_DISMISS_BYTIME = 4;
    
    private  AlertDialog picAlertdialog;
    private  AlertDialog soundAlertdialog;
    private  AlertDialog zoomAlertdialog; 
    private  AlertDialog trackAlertdialog; 
    private  LinearLayout picdialog;
    private  LinearLayout sounddialog;
    private  LinearLayout zoomdialog;
    private  LinearLayout trackdialog;

    private int picModeIndex;
    private int soundModeIndex;
    private int zoomModeIndex;
    private int trackModeIndex;
    
    boolean isPicDialogDismiss =true;
    boolean isSoundDialogDismiss =true;
    boolean isZoomDialogDismiss =true;
    boolean isTrackDialogDismiss =true;
    
    private TextView  pic_menu_btn;
    private TextView  sound_menu_btn;
    private TextView  zoom_menu_btn;
    private TextView  track_menu_btn;
   	
    private void pictureModeQuickKeyHandle(){
       	Log.i(TAG, "zemin KeyEvent.KEY_PICTUREMODE press");    
    	Log.i(TAG,"isPicDialogDismiss="+isPicDialogDismiss);
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
    	if(!isTrackDialogDismiss){
    		//trackAlertdialog.dismiss();
            trackdialog.removeAllViews();
    		isTrackDialogDismiss=true;
    	}
    	if(!isPicDialogDismiss){ //not dismiss 
			 delay(PIC_DIALOG_DISMISS_BYTIME); 
			 picModeIndex++;
			 if(picModeIndex>=InterfaceValueMaps.picture_mode.length){
				 picModeIndex=0;
			 }
			 Log.i(TAG,"picModeIndex="+picModeIndex);
			  PictureInterface.setPictureMode(InterfaceValueMaps.picture_mode[picModeIndex][0]);
			  pic_menu_btn.setText(InterfaceValueMaps.picture_mode[picModeIndex][1]);        		
    	}else{  //dismiss
       	 int mode = PictureInterface.getPictureMode(); 
   	    picModeIndex= Util.getIndexFromArray(mode,InterfaceValueMaps.picture_mode);
     	 AlertDialog.Builder	 mPicBuilder = new AlertDialog.Builder(context,R.style.Dialog_backgroundDimEnabled_false);
			 Log.i(TAG,"mode="+mode);
			 Log.i(TAG,"picModeIndex="+picModeIndex);
          picdialog = (LinearLayout) this.findViewById(R.id.mydialog);
 	      LayoutInflater factory = LayoutInflater.from(context);
 	      View myView = factory.inflate(R.layout.selector_view_dialog,null);
 	       pic_menu_btn =(TextView) myView.findViewById(R.id.menu_btn);
 	       pic_menu_btn.setText(InterfaceValueMaps.picture_mode[picModeIndex][1]);
 	     //  picAlertdialog = mPicBuilder.create();
 	       
	     //   Window window = picAlertdialog.getWindow();
	   //     WindowManager.LayoutParams lp = window.getAttributes();
       //     lp.y = 250;
	   //     window.setAttributes(lp);
	  //      picAlertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	  //      picAlertdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
	 //       picAlertdialog.show();
	 //       picAlertdialog.getWindow().setContentView(myView);

            picdialog.addView(myView);
	        isPicDialogDismiss=false;
		   delay(PIC_DIALOG_DISMISS_BYTIME);         		
    	}     			         	
    }
    
    private void soundModeQuickKeyHandle(){
    	Log.i(TAG,"KeyEvent.KEY_SOUNDMODE press");
    	Log.i(TAG,"isSoundDialogDismiss="+isSoundDialogDismiss);
    	
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
    	if(!isTrackDialogDismiss){
    		//trackAlertdialog.dismiss();
            trackdialog.removeAllViews();
    		isTrackDialogDismiss=true;
    	}

    	if(!isSoundDialogDismiss){ //not dismiss
			 delay(SOUND_DIALOG_DISMISS_BYTIME); 
			 soundModeIndex++;
			 if(soundModeIndex>=InterfaceValueMaps.sound_mode.length){
				 soundModeIndex=0;
			 }
			 Log.i(TAG,"soundModeIndex="+soundModeIndex);
			   AudioInterface.getAudioManager().setSoundMode(InterfaceValueMaps.sound_mode[soundModeIndex][0]);
			   sound_menu_btn.setText(InterfaceValueMaps.sound_mode[soundModeIndex][1]);        		
    	}else{   // dismiss
      	  int sound_mode = AudioInterface.getAudioManager().getSoundMode();
      	  soundModeIndex= Util.getIndexFromArray(sound_mode,InterfaceValueMaps.sound_mode); 
      	  Builder	 mSoundBuilder = new AlertDialog.Builder(context,R.style.Dialog_backgroundDimEnabled_false);
				 Log.i(TAG,"sound_mode"+sound_mode);
				 Log.i(TAG, "soundModeIndex=" + soundModeIndex);
          sounddialog  = (LinearLayout) this.findViewById(R.id.mydialog);
 	      LayoutInflater sound_factory = LayoutInflater.from(context);
 	      View sound_myView = sound_factory.inflate(R.layout.selector_view_dialog,null);
 	         sound_menu_btn =(TextView) sound_myView.findViewById(R.id.menu_btn);
 	      sound_menu_btn.setText(InterfaceValueMaps.sound_mode[soundModeIndex][1]);
/* 	     soundAlertdialog = mSoundBuilder.create();

	        Window sound_window = soundAlertdialog.getWindow();
	        WindowManager.LayoutParams sound_lp = sound_window.getAttributes();
	        sound_lp.y=350;
	        sound_window.setAttributes(sound_lp);
	        soundAlertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	        soundAlertdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);  	        
	        soundAlertdialog.show();
	        soundAlertdialog.getWindow().setContentView(sound_myView);*/
	        isSoundDialogDismiss =false;
            sounddialog.addView(sound_myView);
            delay(SOUND_DIALOG_DISMISS_BYTIME);
    	}           	 		             
    }
    
    private void zoomQuickKeyHandle(){
    	Log.i(TAG,"KeyEvent.KEY_ZOOM press");
    	Log.i(TAG,"isZoomDialogDismiss="+isZoomDialogDismiss);
    	
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
    	if(!isTrackDialogDismiss){
    		//trackAlertdialog.dismiss();
            trackdialog.removeAllViews();
    		isTrackDialogDismiss=true;
    	}
    	
    	if(!isZoomDialogDismiss){ //not dismiss
			 delay(ZOOM_DIALOG_DISMISS_BYTIME); 
			 zoomModeIndex++;
			 if(zoomModeIndex>=InterfaceValueMaps.picture_aspect.length){
				 zoomModeIndex=0;
			 }
			 Log.i(TAG,"zoomModeIndex="+zoomModeIndex);
			PictureInterface.setAspect(InterfaceValueMaps.picture_aspect[zoomModeIndex][0],false);
			  zoom_menu_btn.setText(InterfaceValueMaps.picture_aspect[zoomModeIndex][1]);        		
    	}else{   // dismiss
    	  int aspect = PictureInterface.getAspect();          //display mode  4 é¢å©šæ½°çå“„î‡­
   	     zoomModeIndex= Util.getIndexFromArray(aspect,InterfaceValueMaps.picture_aspect); 
   	    //éè§„åµé—‡ï¿½çœ°é”›å±½å¹“é—„ã‚„æ±‰é—â•‹ç´é€æƒ§ã‡1é”›å±¾æ–æ¾¶ï¿½é–«å¤ã€éŠ†å‚šç¶‹é‘¾å³°å½‡éŠé—´è´Ÿæ©æ¬Žç¬é‘°å‘®æ¤‚é”›å²ƒî†•ç¼ƒî†¼ï¿½æ¶“ç¯ˆSPECT_16_9
   	    if((aspect==EnumPictureAspect.ASPECT_ZOOM)||(aspect==EnumPictureAspect.ASPECT_ZOOM1)||(aspect==EnumPictureAspect.ASPECT_ZOOM2)){
   	 	   PictureInterface.setAspect( EnumPictureAspect.ASPECT_16_9,false);
   	 	   aspect = PictureInterface.getAspect(); 
   	 	  zoomModeIndex = Util.getIndexFromArray(aspect,InterfaceValueMaps.picture_aspect); 
   	 	   Log.i(TAG,"aspect= ASPECT_ZOOM or ASPECT_ZOOM1 or ASPECT_ZOOM2 ;set value = ASPECT_16_9; aspect="+aspect);
   	    }                 	
       	    Builder  mZoomBuilder = new AlertDialog.Builder(context,R.style.Dialog_backgroundDimEnabled_false);
					 Log.i(TAG,"aspect="+aspect);
					 Log.i(TAG,"zoomModeIndex="+zoomModeIndex);
            zoomdialog = (LinearLayout) this.findViewById(R.id.mydialog);
       	      LayoutInflater zoom_factory = LayoutInflater.from(context);
       	      View zoom_myView = zoom_factory.inflate(R.layout.selector_view_dialog,null);
       	       zoom_menu_btn =(TextView) zoom_myView.findViewById(R.id.menu_btn);
       	      zoom_menu_btn.setText(InterfaceValueMaps.picture_aspect[zoomModeIndex][1]);
/*       	      zoomAlertdialog = mZoomBuilder.create();
       	      
		        Window zoom_window = zoomAlertdialog.getWindow();
		        WindowManager.LayoutParams zoom_lp = zoom_window.getAttributes();
		        zoom_lp.y=350;
		        zoom_window.setAttributes(zoom_lp);
		        zoomAlertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		        zoomAlertdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		        zoomAlertdialog.show();
		        zoomAlertdialog.getWindow().setContentView(zoom_myView);	*/
		        isZoomDialogDismiss=false;
               zoomdialog.addView(zoom_myView);
            delay(ZOOM_DIALOG_DISMISS_BYTIME);
    	}
    }
    private void TrackModeQuickKeyHandle(){
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
    	
    	if(!isTrackDialogDismiss){ //not dismiss 
			 delay(TRACK_DIALOG_DISMISS_BYTIME); 
			 trackModeIndex++;
			 if(trackModeIndex>=4){
				trackModeIndex=0;
			 }
			 Log.i(TAG,"trackModeIndex="+trackModeIndex);
			 UmtvManager.getInstance().getAudio().setTrackMode(InterfaceValueMaps.track_mode[trackModeIndex][0]);
			  //PictureInterface.setPictureMode(InterfaceValueMaps.track_mode[trackModeIndex][0]);
			 track_menu_btn.setText(InterfaceValueMaps.track_mode[trackModeIndex][1]);        		
    	}else{  //dismiss
       	 int mode = UmtvManager.getInstance().getAudio().getTrackMode();
       	trackModeIndex= Util.getIndexFromArray(mode,InterfaceValueMaps.track_mode);
   	    //Builder	 mPicBuilder = new AlertDialog.Builder(context,R.style.Dialog_backgroundDimEnabled_false);

            trackdialog = (LinearLayout) this.findViewById(R.id.mydialog);
 	      LayoutInflater factory = LayoutInflater.from(context);
 	      View myView = factory.inflate(R.layout.selector_view_dialog,null);
 	     track_menu_btn =(TextView) myView.findViewById(R.id.menu_btn);
 	    	
 	    track_menu_btn.setText(InterfaceValueMaps.track_mode[trackModeIndex][1]);
/* 	   trackAlertdialog = mPicBuilder.create();
 	       
	        Window window = trackAlertdialog.getWindow();
	        WindowManager.LayoutParams lp = window.getAttributes();
	        lp.y=350;
	        window.setAttributes(lp);
	        trackAlertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	        trackAlertdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);  	        
	        trackAlertdialog.show();	
	        trackAlertdialog.getWindow().setContentView(myView);*/
	        isTrackDialogDismiss=false;
            trackdialog.addView(myView);
            delay(TRACK_DIALOG_DISMISS_BYTIME);
    	}     			         	
    }
   	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_ESCAPE) {
        	if (isThreadStart) {    
            	ctrlbarDismiss();
                return true;
            }
        	
        	count++;
	    	 if(count<2)
	         {
	    		Log.i(TAG, "count:"+count);
	       		//Toast.makeText(context,context.getResources().getString(R.string.toast_exit),Toast.LENGTH_SHORT).show();
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
	        	if(!isTrackDialogDismiss){
	        		//trackAlertdialog.dismiss();
                    trackdialog.removeAllViews();
	        		isTrackDialogDismiss=true;
	        	}
	        	
	        	if(!isZoomDialogDismiss){ 
	        		//zoomAlertdialog.dismiss();
                    zoomdialog.removeAllViews();
	        		isZoomDialogDismiss=true;
	        	}
	            final CustomToast myToast=new CustomToast(context);
	            myToast.getmDialog().setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface arg0) {
						// TODO Auto-generated method stub
						if(myToast.isBackDismiss() == true){
							finish();
						}
					}
				});
	    		myToast.setMessage(R.string.toast_exit);
	    		myToast.showTime(2000);
	    		myToast.show();
	    		tmpHander.sendEmptyMessageDelayed(0, 2000);//ç¬¬â‘¡ç§å‘é€å»¶è¿Ÿæ¶ˆæ¯
	                return true;
	         }
	        else{
	        	count=0;

	            if (mModelBDInfo != null) {
	                Intent pIntent = new Intent(VideoActivity.this, BDActivityNavigation.class);
	                pIntent.putExtra("BDISOName", getCurrName);
	                pIntent.putExtra("BDISOPath", mModelBDInfo.getBDISOPath());
	                startActivityWithAnim(pIntent);
	                finish();
	                return true;
	            }
	            else if (mModelDVDInfo != null) {
	                Intent pIntent = new Intent(VideoActivity.this, DVDActivityNavigation.class);
	                pIntent.putExtra("DVDISOName", getCurrName);
	                pIntent.putExtra("DVDISOPath", mModelDVDInfo.getDVDISOPath());
	                startActivityWithAnim(pIntent);
	                finish();
	                return true;
	            }
	        	
	        }
	  
        }
        else if ((keyCode != KeyEvent.KEYCODE_VOLUME_UP) && (keyCode != KeyEvent.KEYCODE_VOLUME_DOWN)
                 && (keyCode != KeyEvent.KEYCODE_VOLUME_MUTE)) {

            if ((keyCode == Constants.TV_INFO)
                || (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) || (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
                || (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) || (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND)
                || (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) || (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT)
                || (keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {

                

            	setInfoCue();
                mediaInfoLayout.setVisibility(View.VISIBLE);
                mediaControllerLayout.setVisibility(View.VISIBLE);
                startThread();
                Log.i(TAG,"onkeydown() keyCode="+keyCode+" do startThread()");

                if(!isSeekBarSelected){
                	videoSeekBar.setThumb(this.getResources().getDrawable(R.drawable.hisil_yuandian));
                    btnLinearLayout.getChildAt(defaultFocus).requestFocus();
                }
            }

            switch (keyCode) {
    		case KeyEvent.KEY_PLAY_PAUSE:
    			Log.i(TAG,"KeyEvent.KEY_PLAY_PAUSE");
    			 doForPlayorPause();
    			 if(!videoView.isPlaying()){ // show bar for pause
                     mediaInfoLayout.setVisibility(View.VISIBLE);
                     mediaControllerLayout.setVisibility(View.VISIBLE);
                     startThread(); 
                     play.requestFocus();
                    
    			 }
    			 isSeekBarSelected = false;
                 videoSeekBar.setThumb(this.getResources().getDrawable(R.drawable.hisil_yuandian));
                 defaultFocus=3;
                 
    			break;
    		case KeyEvent.KEY_CEASE:
    		    Log.i(TAG,"KeyEvent.KEY_CEASE");
    		    
				if (rewindOrForward) {
				    playStatus.setVisibility(View.INVISIBLE);
				    forwardRate = 1;
				    rewindRate = 1;
				    rewindOrForward = false;
				    videoView.resume();
				    play.setBackgroundResource(R.drawable.pause_button);
				    //                    play.setImageResource(R.drawable.hisil_ic_media_ff);
				}
    		    videoView.seekTo(0);
                try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                if(videoView.isPlaying()){
                    videoView.pause(); 
                }
                play.setBackgroundResource(R.drawable.play_button); 
                
                isSeekBarSelected = false;
                videoSeekBar.setThumb(this.getResources().getDrawable(R.drawable.hisil_yuandian));
                defaultFocus=3;
                
   			 if(!videoView.isPlaying()){ // show bar for cease
                 mediaInfoLayout.setVisibility(View.VISIBLE);
                 mediaControllerLayout.setVisibility(View.VISIBLE);
                 startThread(); 
                 play.requestFocus();
			 }
    		    break;
    		case KeyEvent.KEY_PREV:
    			Log.i(TAG,"KeyEvent.KEY_PREV");
    			 doForPageUP();
    			break;
    		case KeyEvent.KEY_NEXT:
    		    Log.i(TAG,"KeyEvent.KEY_NEXT");
    		    doForPageDown();
    		    break;
	            case KeyEvent.KEYCODE_PICTUREMODE:
	            	pictureModeQuickKeyHandle();
	            	break;
	            case KeyEvent.KEYCODE_SOUNDMODE:
	            	soundModeQuickKeyHandle();
	            	break;
	            case KeyEvent.KEYCODE_ZOOM:
	            	String w = SystemProperties.get("persist.sys.reslutionWidth");
	            	String h = SystemProperties.get("persist.sys.reslutionHight");
	            	if(!((w.equals("4096"))||(w.equals("3840"))&&(h.equals("2160")))){
	            		zoomQuickKeyHandle();
	            	}
	            	
	            	break;  
	            case KeyEvent.KEY_TRACK:
	             	TrackModeQuickKeyHandle();
	             	break;
                case KeyEvent.KEYCODE_MENU:
                    popUpMenuDialog();
                    break;

                case KeyEvent.KEYCODE_INFO:
                    setInfoCue();
                    mediaInfoLayout.setVisibility(View.VISIBLE);
                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                	Log.i(TAG,"isSeekBarSelected="+isSeekBarSelected);
                    if (isSeekBarSelected) {
                    	//videoView.pause();
                        videoSeekBar.requestFocus();
                        progerssFwRwind = progerssFwRwind == -1 ? videoView.getCurrentPosition() : progerssFwRwind;
                        advanceAndRetreat(common.getStep(), true);
                        haveLeftRightOpration = true;
                        mLastSeekTime = System.currentTimeMillis();
                        play.clearFocus();
                        play.setBackgroundResource(R.drawable.pause_button);
                        videoSeekBar.requestFocus();
                    }
                    else {
                    	videoSeekBar.setThumb(getResources().getDrawable(R.drawable.hisil_yuandian));
                        int count = btnLinearLayout.getChildCount();

                        if (defaultFocus >= count - 1) {
                            defaultFocus = 0;
                        }
                        else if (flags[1]) {
                            defaultFocus += 1;
                        }
                        /*else {
                            defaultFocus += 2;
                        }*/

                        btnLinearLayout.getChildAt(defaultFocus).requestFocus();
                    }

                    break;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (isSeekBarSelected) {
                    	//videoView.pause();
                        videoSeekBar.requestFocus();
                        progerssFwRwind = progerssFwRwind == -1 ? videoView.getCurrentPosition() : progerssFwRwind;
                        advanceAndRetreat(common.getStep(), false);
                        haveLeftRightOpration = true;
                        mLastSeekTime = System.currentTimeMillis();
                        play.clearFocus();
                        play.setBackgroundResource(R.drawable.pause_button);
                        videoSeekBar.requestFocus();
                    }
                    else {
                        int count = btnLinearLayout.getChildCount();
       
                        

                        if (defaultFocus < 1) {
                            defaultFocus = count - 1;
                        }
                        else if (flags[1]) {
                            defaultFocus -= 1;
                        }
                       /* else {
                            defaultFocus -= 2;
                        }*/

                        btnLinearLayout.getChildAt(defaultFocus).requestFocus();

                    }

                    break;

                case  KeyEvent.KEYCODE_FORWARD:
                    setForward();
                    break;

                case KeyEvent.KEYCODE_MEDIA_REWIND:
                    setRewind();
                    break;

                case KeyEvent.KEYCODE_MEDIA_STOP:
                    finish();
                    break;

                case KeyEvent.KEYCODE_PAGE_DOWN:
                    if (rewindOrForward) {
                        playStatus.setVisibility(View.INVISIBLE);
                        forwardRate = 1;
                        rewindRate = 1;
                        rewindOrForward = false;
                        videoView.resume();
                    }

                    if (mediaFileList != null) {
                        Common.isResume = false;
                        play.setBackgroundResource(R.drawable.pause_button);
                        //                    play.setImageResource(R.drawable.hisil_ic_media_ff);
                        initSeekSecondaryProgress();
                        Common.isShowLoadingToast = true;
                        isContinue = true;
                        getVideoInfo(mediaFileList.getNextVideoInfo(null));
                    }

                    break;

                case KeyEvent.KEYCODE_PAGE_UP:
                    if (rewindOrForward) {
                        playStatus.setVisibility(View.INVISIBLE);
                        forwardRate = 1;
                        rewindRate = 1;
                        rewindOrForward = false;
                        videoView.resume();
                    }

                    if (mediaFileList != null) {
                        Common.isResume = false;
                        play.setBackgroundResource(R.drawable.pause_button);
                        //                    play.setImageResource(R.drawable.hisil_ic_media_ff);
                        initSeekSecondaryProgress();
                        Common.isShowLoadingToast = true;
                        isContinue = true;
                        getVideoInfo(mediaFileList.getPreVideoInfo(null));
                    }

                    break;

                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if (haveLeftRightOpration) {
                        videoSeekBar.setProgress(position);
                        videoView.seekTo((int) progerssFwRwind);
                        initSeekSecondaryProgress();
                    }
                    else {
                        if (rewindOrForward) {
                            playStatus.setVisibility(View.INVISIBLE);
                            forwardRate = 1;
                            rewindRate = 1;
                            rewindOrForward = false;
                            videoView.resume();
                            play.setBackgroundResource(R.drawable.pause_button);
                            //                        play.setImageResource(R.drawable.hisil_ic_media_ff);
                        }
                        else {
                            play_pause();
                            play.requestFocus();
                            isSeekBarSelected = false;
                            videoSeekBar.setThumb(this.getResources().getDrawable(R.drawable.hisil_yuandian));
                        }
                    }

                    break;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (isThreadStart) {
                     isSeekBarSelected = true;
                     btnLinearLayout.getChildAt(defaultFocus).clearFocus();
                     videoSeekBar.setThumb(this.getResources().getDrawable(R.drawable.hisil_yuandian_selected));
                    }
                    else {
                        selectedPosition -= PositionStep;

                        if (selectedPosition < 0)
                        { selectedPosition = 0; }

                        videoView.setSubVertical(selectedPosition);
                        common.sharedPreferencesOpration(Constants.SHARED, "selectedPosition", selectedPosition, 0, true);
                    }

                    break;

                case KeyEvent.KEYCODE_DPAD_UP:
                    if (isThreadStart) {
                     btnLinearLayout.getChildAt(defaultFocus).requestFocus();
                     isSeekBarSelected = false;
                     videoSeekBar.setThumb(this.getResources().getDrawable(R.drawable.hisil_yuandian));
                    }
                    else {
                        selectedPosition += PositionStep;

                        if (selectedPosition > (screenHeight - 36))
                        { selectedPosition = screenHeight - 36; }

                        videoView.setSubVertical(selectedPosition);
                        common.sharedPreferencesOpration(Constants.SHARED, "selectedPosition", selectedPosition, 0, true);
                    }

                    break;

                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (haveLeftRightOpration) {
                        videoSeekBar.setProgress(position);
                        videoView.seekTo((int) progerssFwRwind);
                        initSeekSecondaryProgress();
                    }
                    else {
                        if (rewindOrForward) {
                            playStatus.setVisibility(View.INVISIBLE);
                            forwardRate = 1;
                            rewindRate = 1;
                            rewindOrForward = false;
                            videoView.resume();
                            play.setBackgroundResource(R.drawable.pause_button);
                            //                        play.setImageResource(R.drawable.hisil_ic_media_ff);
                        }
                        else {
                            play_pause();
                            play.requestFocus();
                            isSeekBarSelected = false;
                            Log.i(TAG, "===========pause======================");
                            videoSeekBar.setThumb(this.getResources().getDrawable(R.drawable.hisil_yuandian));
                            defaultFocus=3;
                        }
                    }

                    break;

                case KeyEvent.KEYCODE_SUB:
                    if (videoView.getSubtitleNumber() != 0) {
                        Message message = new Message();
                        message.what = Constants.switchSubtitle;
                        int _SubNum = videoView.getSubtitleNumber();
                        int _ExtNum = videoView.getExtSubtitleNumber();
                        int _Select = selectedSubId;

                        if (_ExtNum != 0) {
                            if (_Select >= (_SubNum - _ExtNum))
                            { _Select = _Select - (_SubNum - _ExtNum); }
                            else if (_Select < (_SubNum - _ExtNum))
                            { _Select = _Select + _ExtNum; }
                        }

                        toast.showMessage(context, getString(R.string.toastSubtitle, new Object[]
                                                             { videoView.getSubtitleLanguageList().get(_Select) }), Toast.LENGTH_SHORT);

                        if (selectedSubId + 1 >= videoView.getSubtitleNumber())
                        { selectedSubId = 0; }
                        else
                        { selectedSubId++; }

                        mHandler.removeMessages(Constants.switchSubtitle);
                        mHandler.sendMessageDelayed(message, 2000);
                    }

                    break;

                case KeyEvent.KEYCODE_AUDIO:
                    if (videoView.getAudioTrackNumber() != 0) {
                        Message message = new Message();
                        message.what = Constants.switchAudioTrack;
                        toast.showMessage(context, getString(R.string.toastAudio, new Object[]
                                                             { videoView.getAudioTrackLanguageList().get(selectedTrack) }), Toast.LENGTH_SHORT);

                        if (selectedTrack + 1 >= videoView.getAudioTrackNumber())
                        { selectedTrack = 0; }
                        else
                        { selectedTrack++; }
              
                        mHandler.removeMessages(Constants.switchAudioTrack);
                        mHandler.sendMessageDelayed(message, 2000);
                    }

                    break;

                default:
                    return super.onKeyDown(keyCode, event);
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void showHelp() {
        try {
            InputStream is = null;
            is = this.getResources().openRawResource(R.raw.help);
            StringBuffer buffer = new StringBuffer();
            buffer.append("\n");
            byte[] b = new byte[is.available()];

            do {
                int count = is.read(b);

                if (count < 0) {
                    break;
                }

                String str = new String(b, 0, count, "utf-8");
                buffer.append(str);
            }
            while (true);

            is.close();
            LayoutInflater factory = LayoutInflater.from(this);
            View myView = factory.inflate(R.layout.help, null);
            TextView textView = (TextView) myView.findViewById(R.id.help_text);
            textView.setText(buffer.toString());
            new AlertDialog.Builder(this).setView(myView).setPositiveButton(
                this.getString(R.string.close), null).create().show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void advanceAndRetreat(int step, boolean isAdvance) {
        if (videoView.isPlaying()) {
            play.setBackgroundResource(R.drawable.play_button);
            //           play.setImageResource(R.drawable.hisil_ic_media_play);
            //videoView.pause();
        }

        if (isAdvance) {
            progerssFwRwind += step;
        }
        else {
            progerssFwRwind -= step;
        }

        int prepostion = position;
        position = (int)Math.ceil(progerssFwRwind / total * seekBarMax);
        if (progerssFwRwind > total) {
            position = seekBarMax;
            progerssFwRwind = total;
        }
        else if (position <= 0) {
            position = 0;
            progerssFwRwind = 0;
        }

        if (isAdvance) {
            for (int i = prepostion; i < position; i++) {
                videoSeekBar.setProgress(i);
            }
        }
        else {
            for (int i = prepostion; i > position; i--) {
                videoSeekBar.setProgress(i);
            }
        }
        videoSeekBar.setProgress(position);
        startThread();
        isSecondProgress = true;
        step = step + common.getAccStep();

        if (step > common.getAccStep() * 18)
        { step = common.getAccStep() * 18; }

        common.setStep(step / 1000);
    }

    private void initSeekSecondaryProgress() {
        progerssFwRwind = -1;
        videoSeekBar.setSecondaryProgress(0);
        haveLeftRightOpration = false;
    }

    private void setRewind() {
        if (rewindRate < 32) {
            if (!(videoView.isPlaying())) {
                videoView.start();
            }

            forwardRate = 1;
            rewindRate *= 2;
            videoView.setSpeed(-rewindRate);

            if (!playStatus.isShown()) {
                playStatus.setVisibility(View.VISIBLE);
            }

            switch (rewindRate) {
                case 2:
                    playStatus.setImageResource(R.drawable.fb_x2);
                    break;

                case 4:
                    playStatus.setImageResource(R.drawable.fb_x4);
                    break;

                case 8:
                    playStatus.setImageResource(R.drawable.fb_x8);
                    break;

                case 16:
                    playStatus.setImageResource(R.drawable.fb_x16);
                    break;

                case 32:
                    playStatus.setImageResource(R.drawable.fb_x32);
                    break;
            }

            play.setBackgroundResource(R.drawable.play_button);
            //            play.setImageResource(R.drawable.hisil_ic_media_play);
            rewindOrForward = true;
        }
        else if (rewindOrForward) {
            playStatus.setVisibility(View.INVISIBLE);
            forwardRate = 1;
            rewindRate = 1;
            rewindOrForward = false;
            videoView.resume();
            play.setBackgroundResource(R.drawable.pause_button);
            //            play.setImageResource(R.drawable.hisil_ic_media_ff);
        }
    }

    private void setForward() {
        if (forwardRate < 32) {
            if (!(videoView.isPlaying())) {
                videoView.start();
            }

            rewindRate   = 1;
            forwardRate *= 2;
            videoView.setSpeed(forwardRate);

            if (!playStatus.isShown()) {
                playStatus.setVisibility(View.VISIBLE);
            }

            switch (forwardRate) {
                case 2:
                    playStatus.setImageResource(R.drawable.ff_x2);
                    break;

                case 4:
                    playStatus.setImageResource(R.drawable.ff_x4);
                    break;

                case 8:
                    playStatus.setImageResource(R.drawable.ff_x8);
                    break;

                case 16:
                    playStatus.setImageResource(R.drawable.ff_x16);
                    break;

                case 32:
                    playStatus.setImageResource(R.drawable.ff_x32);
                    break;
            }

            play.setBackgroundResource(R.drawable.play_button);
            //            play.setImageResource(R.drawable.hisil_ic_media_play);
            rewindOrForward = true;
        }
        else if (rewindOrForward) {
            playStatus.setVisibility(View.INVISIBLE);
            forwardRate = 1;
            rewindRate = 1;
            rewindOrForward = false;
            videoView.resume();
            play.setBackgroundResource(R.drawable.pause_button);
            //            play.setImageResource(R.drawable.hisil_ic_media_ff);
        }
    }

    private void setNewFounction(int flag, int rate) {
        Parcel requestParcel = Parcel.obtain();
        requestParcel.writeInt(flag);
        requestParcel.writeInt(rate);
        Parcel replayParcel = Parcel.obtain();
        videoView.invoke(requestParcel, replayParcel);
    }

    private Parcel getInfo(int flag) {
        Parcel requestParcel = Parcel.obtain();
        requestParcel.writeInt(flag);
        Parcel replayParcel = Parcel.obtain();
        videoView.invoke(requestParcel, replayParcel);
        replayParcel.setDataPosition(0);
        return replayParcel;
    }

    private void subControl() {
        String[] subSettingItems = getResources().getStringArray(R.array.sub_items);
        setSubtitleDialog(R.string.subsetTitle, subSettingItems, subItemClickListener);
    }
    private void audioControl() {
        String[] audioSettingItems = getResources().getStringArray(R.array.audio_items);
        setAudioDialog(R.string.audiosetTitle, audioSettingItems, audioItemClickListener);
    }
    private int getSubEncode(int flag) {
        Parcel replayParcel = getInfo(flag);
        replayParcel.readInt();
        return replayParcel.readInt();
    }

    private void subTime(int value) {
        selectedTime = value;
        Parcel requestParcel = Parcel.obtain();
        requestParcel.writeInt(HiMediaPlayerInvoke.CMD_SET_SUB_TIME_SYNC);
        requestParcel.writeInt(0);
        requestParcel.writeInt(0);
        requestParcel.writeInt(value);
        Parcel replayParcel = Parcel.obtain();
        videoView.invoke(requestParcel, replayParcel);
    }

    private int subPath(String path) {
        Parcel requestParcel = Parcel.obtain();
        requestParcel.writeInt(HiMediaPlayerInvoke.CMD_SET_SUB_EXTRA_SUBNAME);
        requestParcel.writeString(path);
        Parcel replayParcel = Parcel.obtain();
        videoView.invoke(requestParcel, replayParcel);
        return replayParcel.readInt();
    }

    private List < HashMap < String, Object >> getMarkByPath(String path) {
        List < HashMap < String, Object >> marklist = new ArrayList < HashMap < String, Object >> ();

        for (int i = 0; i < list.size(); i++) {
            if (path.equals(list.get(i).get("path"))) {
                marklist.add(list.get(i));
            }
        }

        Collections.sort(marklist, common.new MarkComparator());
        return marklist;
    }

    private ArrayList < HashMap < String, Object >> getMark() {
        Cursor cursor = null;
        ArrayList < HashMap < String, Object >> list = new ArrayList < HashMap < String, Object >> ();

        try {
            cursor = database.query(Constants.VIDEO_BOOKMARK, new String[] {"_data", "bookmark_name", "bookmark",
                                                                            "date_added"
                                                                           }, null, null, null, null, null);
            HashMap <String, Object> map = null;

            while (cursor.moveToNext()) {
                map = new HashMap <String, Object>();
                map.put("path", cursor.getString(cursor.getColumnIndex("_data")));
                map.put("mark", cursor.getString(cursor.getColumnIndex("bookmark")));
                map.put("markname", cursor.getString(cursor.getColumnIndex("bookmark_name")));
                map.put("date", cursor.getString(cursor.getColumnIndex("date_added")));
                list.add(map);
            }
        }
        catch (Exception e) {
            // TODO: handle exception
        }
        finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return list;
    }

    private void saveMark(EditText markname, int mark) {
        String str = markname.getText().toString().trim();
        String formatMark = Common.getTimeFormatValue(mark);
        boolean isRepeat = operater.checkMarkPoint(database, getCurrPath, formatMark, str);

        if (isRepeat) {
            toast.showMessage(VideoActivity.this, R.string.tag_exist, Toast.LENGTH_SHORT);
        }
        else {
            operater.insertBookMark(database, getCurrPath, mark, str);

            if ((str == null) || (str.length() == 0)) {
                toast.showMessage(VideoActivity.this, getString(R.string.video_tag) + formatMark
                                  + getString(R.string.save_success), Toast.LENGTH_SHORT);
            }
            else {
                toast.showMessage(VideoActivity.this, str + getString(R.string.save_success), Toast.LENGTH_SHORT);
            }

            isMarkDBChange = true;
        }
    }

    private void play_pause() {
        if (Common.isResume) {
            try {
                Thread.sleep(1000);
                Common.isResume = false;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (videoView.isPlaying()) {
            play.setBackgroundResource(R.drawable.play_button);
            //          play.setImageResource(R.drawable.hisil_ic_media_play);
            videoView.pause();
        }
        else {
            if (this.isStop) {
                videoView = (HisiVideoView) findViewById(R.id.videoView);
                videoView.setVideoPath(getCurrPath);
                isContinue = false;
                play.setBackgroundResource(R.drawable.pause_button);
                //                play.setImageResource(R.drawable.hisil_ic_media_ff);
                this.isStop = false;
            }
            else {
                play.setBackgroundResource(R.drawable.pause_button);
                //              play.setImageResource(R.drawable.hisil_ic_media_ff);
                videoStart();
            }
        }
    }

    private void start() {
        videoStart();
        setNewFounction(HiMediaPlayerInvoke.CMD_SET_AUDIO_CHANNEL_MODE, selectedAudio);
        setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_ENCODE, selectedSubEncode);
    }

    private void videoStart() {
        videoView.start();
        forwardRate = 1;
        rewindRate = 1;
        playStatus.setVisibility(View.INVISIBLE);
        videoView = (HisiVideoView) findViewById(R.id.videoView);
    }

    private void getVideoInfo(VideoModel model) {
        if (!isFirstClick) {
            videoView.setStereoVideoFmt(0);
            mDisplayManager.SetRightEyeFirst(0);
            mDisplayManager.SetStereoOutMode(HI_2D_MODE);
            mType = 0;
            mCurrentMode = 0;
            mMVCType = 0;
            ctrlbarDismiss();
            showLoadProgressDialog();
            videoView.reset();
        }

        getVideoInfo_noCycle(model);
        videoView.setVideoPath(getCurrPath);
    }

    private void getVideoInfo_noCycle(VideoModel model) {
        if (model != null) {
            if (Common.isLoadSuccess() || isFirstClick) {
                getCurrPath = model.getPath();
                getPlayPath = getCurrPath;
                getCurrMode = model.getMimeType();

                if ((model.getMimeType() != null) && model.getMimeType().equals("video/bd")) {
                    mBDInfo.openBluray(getCurrPath);
                    mModelBDInfo = new ModelBDInfo();
                    mModelBDInfo.setPath(getCurrPath);
                    mModelBDInfo.setBDISOPath(model.getISOPath());
                    mModelBDInfo.setPosterCache(false);
                    StringBuffer _Buf = new StringBuffer();
                    _Buf.append(Constant.BD_PREFIX);
                    _Buf.append(getCurrPath);
                    _Buf.append("?playlist=");
                    _Buf.append(mBDInfo.getDefaultPlaylist());
                    getCurrPath = _Buf.toString();
                }
                else if ((model.getMimeType() != null) && model.getMimeType().equals("video/dvd")) {
                    mModelDVDInfo = new ModelDVDInfo();
                    mModelDVDInfo.setPath(getCurrPath);
                    mModelDVDInfo.setDVDISOPath(model.getISOPath());
                    mModelDVDInfo.setPosterCache(false);
                    StringBuffer _Buf = new StringBuffer();
                    _Buf.append(Constant.DVD_PREFIX);
                    _Buf.append(getCurrPath);
                    getCurrPath = _Buf.toString();
                }
                else if (!isFirstClick) {
                    mModelBDInfo = null;
                    mModelDVDInfo = null;
                }

                getCurrSize = model.getSize();
                getCurrName = model.getTitle();
                getCurrId = model.getId();
                isFirstClick = false;
            }
        }
        else {
            if (Common.isLoadSuccess()) {
                if (!Common.haveShowNoFileToast) {
                    MyToast.getInstance(context, getResources().getString(R.string.havenofile));
                    Common.haveShowNoFileToast = true;
                }
            }
            else {
                if (Common.isShowLoadingToast) {
                    MyToast.getInstance(context, getResources().getString(R.string.isloading));
                }
            }
        }
    }

    private void setInfoCue() {
    	
        TextView subCue = null;

        if (mDolbyCertification == 0)
        { subCue = (TextView) findViewById(R.id.sub); }
        else if (mDolbyCertification == 1)
        { subCue = (TextView) findViewById(R.id.audio); }

        Parcel reply = getInfo(HiMediaPlayerInvoke.CMD_GET_SUB_INFO);
        reply.readInt();
        subCount = reply.readInt();

        if (subCount != 0) {
            int selectSub = selectedSubId;
            int isExt = 0;
            String Sub = "";
            String SubFormat = "";

            for (int i = 0; i < subCount; i++) {
                int tempid = reply.readInt();
                int tempisExt = reply.readInt();
                String tempSub = reply.readString();
                String tempSubFormat = videoView.mSubFormat[reply.readInt()];

                if (tempid == selectSub) {
                    isExt = tempisExt;
                    Sub = tempSub;
                    SubFormat = tempSubFormat;
                    break;
                }
            }

            if (Sub == null)
            { Sub = ""; }

            int _SubNum = videoView.getSubtitleNumber();
            int _ExtNum = videoView.getExtSubtitleNumber();
            int _Select = selectedSubId;

            if (_ExtNum != 0) {
                if (_Select >= (_SubNum - _ExtNum))
                { _Select = _Select - (_SubNum - _ExtNum); }
                else if (_Select < (_SubNum - _ExtNum))
                { _Select = _Select + _ExtNum; }
            }

            _Select++;
            Sub = mLanguageXmlParser.getLanguage(Sub);

            if (isExt == 0 && Sub.equals("-")) {
                subCue.setText(_Select + "/" + _SubNum + " " + getResources().getString(R.string.subintitle) + " " + SubFormat);
            }
            else if (isExt == 0 && Sub != "") {
                subCue.setText(_Select + "/" + _SubNum + " " + getResources().getString(R.string.subintitle) + " " + SubFormat + " [ " + Sub + " ]");
            }
            else if (isExt == 1 && Sub.equals("-")) {
                subCue.setText(_Select + "/" + _SubNum + " " + getResources().getString(R.string.subexttitle) + " " + SubFormat);
            }
            else if (isExt == 1 && Sub != "") {
                subCue.setText(_Select + "/" + _SubNum + " " + getResources().getString(R.string.subexttitle) + " " + SubFormat + " [ " + Sub + " ]");
            }
        }
        else {
            subCue.setText(R.string.nosubTitle);
        }
		reply.recycle();
        if (mDolbyCertification == 0) {
            TextView audioCue = (TextView) findViewById(R.id.audio);
            List<String> audioList = videoView.getAudioTrackLanguageList();

            if (audioList != null && audioList.size() != 0) {
                String currAudio = audioList.get(videoView.getSelectAudioTrackId());
                currAudio = currAudio.substring(2, currAudio.length());
                audioCue.setText(currAudio);
            }
            else {
                audioCue.setText(R.string.noTrack);
            }
        }
        else {
            String mDolbyInfo = null;
            TextView audioCue = (TextView) findViewById(R.id.sub);
            List<String> audioList = videoView.getAudioTrackLanguageList();

            if (audioList != null && audioList.size() != 0) {
                String currAudio = audioList.get(videoView.getSelectAudioTrackId());
                currAudio = currAudio.substring(2, currAudio.length());
                mDolbyInfo = getResources().getString(R.string.DolbyTrack) + currAudio + "\t";
            }
            else {
                mDolbyInfo = getResources().getString(R.string.DolbyTrack) + getResources().getString(R.string.noTrack) + "\t";
            }

            reply = getInfo(HiMediaPlayerInvoke.CMD_GET_DOLBYINFO);
            reply.readInt();
            int accode = reply.readInt();
            String mMonoInfo = null;

            switch (accode) {
                case 0:
                    mMonoInfo = "1+1";
                    break;

                case 1:
                    mMonoInfo = "1/0";
                    break;

                case 2:
                    mMonoInfo = "2/0";
                    break;

                case 3:
                    mMonoInfo = "3/0";
                    break;

                case 4:
                    mMonoInfo = "2/1";
                    break;

                case 5:
                    mMonoInfo = "3/1";
                    break;

                case 6:
                    mMonoInfo = "2/2";
                    break;

                case 7:
                    mMonoInfo = "3/2";
                    break;
            }

            mDolbyInfo += getResources().getString(R.string.DolbyMono) + mMonoInfo + "\t";
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mDolbyInfo += getResources().getString(R.string.DolbyVolume) + current + "\t";
            reply.recycle();
            reply = getInfo(HiMediaPlayerInvoke.CMD_GET_AUDIO_CHANNEL_MODE);
            reply.readInt();
            int streamtype = reply.readInt();
            String[] channels = getResources().getStringArray(R.array.channel);
            mDolbyInfo += getResources().getString(R.string.DolbyChannel) + channels[streamtype];
            audioCue.setText(mDolbyInfo);
            reply.recycle();
        }

        TextView mediaSize = (TextView) findViewById(R.id.mediaSize);

        if (mDolbyCertification == 0) {
            mediaSize.setText(tools.formatSize(getCurrSize));
        }
        else if (mDolbyCertification == 1) {
            reply = getInfo(HiMediaPlayerInvoke.CMD_GET_VIDEO_INFO);
            reply.readInt();
            int format = reply.readInt();
			reply.recycle();
            if (videoFormatValue.length <= format) {
				return;
			}
            mediaSize.setText(getResources().getString(R.string.DolbyVideo) + videoFormatValue[format] + "\t" + tools.formatSize(getCurrSize));
        }
       // Log.i(TAG, "mDolbyInfo:"+mDolbyInfo+"currAudio:"+currAudio+"SubFormat:"+SubFormat);
    }

    private void setScreenSize() {
        if (!isFullScreen) {
            setVideoScale(Constants.SCREEN_DEFAULT);
            mediaControllerLayout.setVisibility(View.VISIBLE);
            mediaControllerLayout.getBackground().setAlpha(255);

            if (!mHandler.hasMessages(Constants.showMediaController)) {
                mHandler.sendEmptyMessage(Constants.showMediaController);
            }
        }
        else {
            setVideoScale(Constants.SCREEN_FULL);
            mediaControllerLayout.getBackground().setAlpha(0);
            startThread();
        }

        isFullScreen = !isFullScreen;
    }

    private void getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getRealMetrics(dm);
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
    }

    private View getView() {
        ScrollView scrollview   = new ScrollView(VideoActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout relative = (RelativeLayout)inflater.inflate(R.layout.help_info, null);
        TextView help_mediaName = (TextView)relative.findViewById(R.id.help_mediaName);
        TextView help_mediaPath = (TextView)relative.findViewById(R.id.help_mediaPath);
        TextView help_mediaSize = (TextView)relative.findViewById(R.id.help_mediaSize);
        help_mediaName.setText(getCurrName);
        help_mediaPath.setText(getCurrPath);
        help_mediaSize.setText(tools.formatSize(getCurrSize));
        scrollview.addView(relative);
        return scrollview;
    }

    private void setVideoScale(int flag) {
        Log.i(TAG, "screenWidth:" + screenWidth + " screenHeight:" + screenHeight);

        switch (flag) {
            case Constants.SCREEN_FULL:
                videoView.setVideoScale(screenWidth, screenHeight);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;

            case Constants.SCREEN_DEFAULT:
                int videoWidth = videoView.getVideoWidth();
                int videoHeight = videoView.getVideoHeight();
                int mHeight = screenHeight - 2 * mediaControllerLayout.getLayoutParams().height - 43; /*43 is height of statusbar*//*CNcomment:43ä¸ºstatusbarçš„é«˜åº¦*/
                int mWidth = screenWidth * mHeight / screenHeight;
                Log.i(TAG, "mWidth:" + mWidth + " mHeight:" + mHeight);

                if ((videoWidth > 0) && (videoHeight > 0)) {
                    if (videoWidth * mHeight > mWidth * videoHeight) {
                        mHeight = mWidth * videoHeight / videoWidth;
                    }
                    else if (videoWidth * mHeight <= mWidth * videoHeight) {
                        mWidth = videoWidth * mHeight / videoHeight;
                    }
                }

                videoView.setVideoScale(mWidth, mHeight);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
        }
    }

    private void checkDbData(String path) {
        Cursor cursor = null;

        try {
            cursor = database.query(Constants.TABLE_VIDEO,
                                    new String[] { "_id" }, " _data = ? ", new String[] { path },
                                    null, null, null);

            if (cursor.getCount() == 0) {
                ContentValues value = new ContentValues();
                value.put("_data", path);
                value.put("recommended", 0);
                database.insert(Constants.TABLE_VIDEO, null, value);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    private int getLastPosition(String path) {
        int positon   = 0;
        Cursor cursor = null;

        try {
            if (database == null) {
                database = Common.getDataBase(context);
            }

            cursor = database.query(Constants.TABLE_VIDEO, new String[] {
                                        "_id", "last_play_postion"
                                    }, " _data = ? ",
                                    new String[] { path }, null, null, null);

            if (cursor.moveToNext()) {
                positon = cursor.getInt(cursor.getColumnIndex("last_play_postion"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }

        return positon;
    }

    private void updatePositon(String path) {
        ContentValues values = new ContentValues();
        values.put("last_play_postion", 0);

        if (database == null) {
            database = Common.getDataBase(context);
        }

        database.update(Constants.TABLE_VIDEO, values, "_data=?", new String[] { getCurrPath });
    }
private int getLastSelectSubtitleId(String path)
    {
        Cursor cursor = null;
        try {
            if (database == null) {
                database = Common.getDataBase(context);
            }

            cursor = database.query(Constants.TABLE_VIDEO, new String[] {
                                        "_id", "last_SelectSubtitleId"
                                    }, " _data = ? ",
                                    new String[] { path }, null, null, null);

            if (cursor.moveToNext()) {
                return cursor.getInt(cursor.getColumnIndex("last_SelectSubtitleId"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return 0;
    }

    private void updateSelectSubtitleId(String path)
    {
        ContentValues values = new ContentValues();
        values.put("last_SelectSubtitleId", selectedSubId);

        if (database == null) {
            database = Common.getDataBase(context);
        }
        database.update(Constants.TABLE_VIDEO, values, "_data=?", new String[] { path });
    }

    private void updateFav() {
        Cursor favCursor = null;

        try {
            favCursor = database.query(Constants.TABLE_VIDEO, new String[] {"_id"}, "_data=? and recommended=? ",
                                       new String[] { getCurrPath, "1"}, null, null, null);

            if (favCursor.getCount() <= 0) {
                ContentValues values = new ContentValues();
                values.put("recommended", 1);
                database.update(Constants.TABLE_VIDEO, values, "_data=?",
                                new String[] { getCurrPath });
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (favCursor != null) {
                favCursor.close();
                favCursor = null;
            }
        }
    }

    public void isInVideoBroadcast(int isInVideo) {
        Intent broadcastIntent = new Intent(Constants.STARTVIDEO);
        broadcastIntent.putExtra(Constants.ISVIDEOPLAYING, isInVideo);
        sendBroadcast(broadcastIntent);
    }

    private void inflateAudioInfo() {
        List<String> _AudioInfoList = videoView.getAudioInfoList();
        List<String> _AudioLanguageList = new ArrayList<String>();
        List<String> _AudioFormatList = new ArrayList<String>();
        List<String> _AudioSampleRateList = new ArrayList<String>();
        List<String> _AudioChannelList = new ArrayList<String>();

        if (_AudioInfoList == null) {
            Log.e("TAG", "inflateAudioInfo failed");
            return;
        }

        String _Language = "";
        String _Format = "";
        String _SampleRate = "";
        String _Channel = "";
        int _Index = 0;

        for (int i = 0; i < _AudioInfoList.size(); i++) {
            if (i % 4 == 0) {
                _Language = mLanguageXmlParser.getLanguage(_AudioInfoList.get(i));
            }
            else if (i % 4 == 1) {
                _Index = Integer.parseInt(_AudioInfoList.get(i));
                _Format = AudioFormat.getFormat(_Index);

                if (_Format.equals("")) {
                    StringBuffer _Buf = new StringBuffer();
                    _Buf.append(context.getString(R.string.mediaInfoUnknown));
                    _Buf.append("(");
                    _Buf.append(_Index);
                    _Buf.append(")");
                    _Format = _Buf.toString();
                }
                else if (_Format.equals("AC3") && mDolbyCertification == 1) {
                    _Format = "Dolby Digital";
                }
                else if (_Format.equals("EAC3") && mDolbyCertification == 1) {
                    _Format = "Dolby Digital Plus";
                }

                _AudioFormatList.add(_Format);
            }
            else if (i % 4 == 2) {
                _SampleRate = Integer.parseInt(_AudioInfoList.get(i)) / 1000 + "KHz";
                _AudioSampleRateList.add(_SampleRate);
            }
            else if ((i % 4 == 3)) {
                _Channel = _AudioInfoList.get(i);
                _AudioChannelList.add(_Channel);
                StringBuffer _Buf = new StringBuffer();
                _Buf.append((i / 4 + 1));
                _Buf.append(".");
                _Buf.append(_Language);
                _Buf.append(" ");
                _Buf.append(_Format);
                _Buf.append(" ");
                _Buf.append(_Channel);
                _AudioLanguageList.add(_Buf.toString());
            }
        }

        videoView.setAudioTrackLanguageList(_AudioLanguageList);
        videoView.setAudioTrackNumber(_AudioLanguageList.size());
        videoView.setAudioFormatList(_AudioFormatList);
        videoView.setAudioSampleRateList(_AudioSampleRateList);
        videoView.setAudioChannelList(_AudioChannelList);

        if (mDolbyCertification == 1)
        { setNewFounction(HiMediaPlayerInvoke.CMD_SET_DOLBY_RANGEINFO, selectedDolbyRangeInfo); }
    }

    private void inflateSubtitleInfo() {
        if (mModelBDInfo != null && hasExtraSubtitlePath()) {
            List<String> _ExtraSubtitleList = mModelBDInfo.getExtraSubtitleList();
            int i = 0;

            for (String _Path : _ExtraSubtitleList) {
                videoView.setSubtitlePath(_Path);
            }
        }

        List<String> _TempInternalList = videoView.getInternalSubtitleLanguageInfoList();
        List<String> _TempExtList = videoView.getExtSubtitleLanguageInfoList();
        List<String> _ResultInternalList = new ArrayList<String>();
        List<String> _ResultExtList = new ArrayList<String>();

        for (int i = 0; i < _TempInternalList.size(); i++) {
            _ResultInternalList.add(mLanguageXmlParser.getLanguage(_TempInternalList.get(i)));
        }

        for (int i = 0; i < _TempExtList.size(); i++) {
            _ResultExtList.add(mLanguageXmlParser.getLanguage(_TempExtList.get(i)));
        }

        List<String> _PGSInternalList = _ResultInternalList;
        List<String> _PGSExtList = _ResultExtList;
        List<String> _List = new ArrayList<String>();
        int _Index = 0;

        for (int i = 0; i < _PGSExtList.size(); i = i + 2) {
            _Index++;
            StringBuffer _Buf = new StringBuffer();
            _Buf.append(_Index);
            _Buf.append(".");
            _Buf.append(getResources().getString(R.string.subexttitle));
            _Buf.append(" ");
            _Buf.append(_PGSExtList.get(i));
            _Buf.append(" ");
            _Buf.append(_PGSExtList.get(i + 1));
            _List.add(_Buf.toString());
        }

        for (int i = 0; i < _PGSInternalList.size(); i = i + 2) {
            _Index++;
            StringBuffer _Buf = new StringBuffer();
            _Buf.append(_Index);
            _Buf.append(".");
            _Buf.append(getResources().getString(R.string.subintitle));
            _Buf.append(" ");
            _Buf.append(_PGSInternalList.get(i));
            _Buf.append(" ");
            _Buf.append(_PGSInternalList.get(i + 1));
            _List.add(_Buf.toString());
        }

        videoView.setSubtitleLanguageList(_List);
        videoView.setSubtitleNumber(_List.size());
        videoView.setExtSubtitleNumber(_PGSExtList.size() / 2);

        selectedSubId=getLastSelectSubtitleId(getCurrPath);
    videoView.setSelectSubtitleId(selectedSubId);
    videoView.setSubtitleId(selectedSubId);
        if (_PGSExtList.size() != 0) {
            videoView.setSelectSubtitleId(_List.size() - _PGSExtList.size() / 2);
            videoView.setSubtitleId(_List.size() - _PGSExtList.size() / 2);
            selectedSubId = _List.size() - _PGSExtList.size() / 2;
        }

        setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_VERTICAL, selectedPosition);
        setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_STYLE, selectedEffect);
        setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_SIZE, selectedSizes);
        setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_SPACE, selectedSpace);
        setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_LINESPACE, selectedLSpace);
        setNewFounction(HiMediaPlayerInvoke.CMD_SET_SUB_FONT_COLOR, Integer.parseInt(selectedColor.replace("0x", ""), 16));
    }

    private boolean hasExtraSubtitlePath() {
        boolean _Ret = false;
        String _BDPath = mModelBDInfo.getPath();

        if (_BDPath.startsWith("/mnt/iso")) {
            _BDPath = mModelBDInfo.getBDISOPath();
            getPlayPath = mModelBDInfo.getBDISOPath();
            int _Index = _BDPath.lastIndexOf(File.separator);

            if (_Index != -1) {
                _BDPath = _BDPath.substring(0, _Index);
            }
            else {
                return false;
            }
        }

        List<String> _ExtraSubtitleList = new ArrayList<String>();
        List<String> _IdxSubtitleList = new ArrayList<String>();
        boolean _HasIdxSubtitle = false;
        File _File = new File(_BDPath);

        if (_File.exists() && _File.isDirectory()) {
            File[] _SubFiles = _File.listFiles(new SubtitleFileFilter());

            for (File _SubFile : _SubFiles) {
                String _Path = _SubFile.getAbsolutePath();
                String _Name = _SubFile.getName();
                String _MovieNoSuffix = getCurrName.substring(0, getCurrName.lastIndexOf('.') != -1 ? getCurrName.lastIndexOf('.') : getCurrName.length());

                if (mModelBDInfo.getBDISOPath() == null)
                { _MovieNoSuffix = getCurrName; }

                String _NameNoSuffix = _Name.substring(0, _Name.lastIndexOf('.'));
                Log.i(TAG, "SubFile:" + _Path + " " + _Name);

                if (_MovieNoSuffix.equals(_NameNoSuffix)) {
                    _ExtraSubtitleList.add(_Path);
                    _Ret = true;
                }

                if (_Name.toLowerCase(Locale.getDefault()).endsWith(".idx")) {
                    // find .idx file
                    String _PathNoSuffix = _Path.substring(0, _Path.lastIndexOf('.'));
                    _IdxSubtitleList.add(_PathNoSuffix);
                    _HasIdxSubtitle = true;
                }
            }

            if (_HasIdxSubtitle) {
                // delete .sub file by .idx file
                for (int i = 0; i < _ExtraSubtitleList.size(); i++) {
                    String _ExtraSubtitle = _ExtraSubtitleList.get(i);

                    for (int j = 0; j < _IdxSubtitleList.size(); j++) {
                        if (_ExtraSubtitle.equalsIgnoreCase(_IdxSubtitleList.get(j) + ".sub")) {
                            _ExtraSubtitleList.remove(i);
                        }
                    }
                }
            }
        }

        mModelBDInfo.setExtraSubtitleList(_ExtraSubtitleList);
        return _Ret;
    }

    private String getLastExtraSubtitleLanguage(String path) {
        Parcel reply = getInfo(HiMediaPlayerInvoke.CMD_GET_SUB_INFO);
        reply.readInt();
        int _Num = reply.readInt();
        int _IsExt = 0;
        String _ExtSubLanguage = "";
		reply.recycle();
        if (subCount != 0) {
            for (int i = 0; i < _Num; i++) {
                int tempid = reply.readInt();
                int tempisExt = reply.readInt();
                String tempSub = reply.readString();

                if (i == _Num - 1) {
                    _IsExt = tempisExt;
                    _ExtSubLanguage = tempSub;
                    break;
                }
            }
        }

        if (_IsExt == 1)
        { return _ExtSubLanguage; }

        return "";
    }
    private void showAudioListDialog() {
    	Log.i(TAG, "getAudioTrackNumber"+videoView.getAudioTrackNumber());
    	
        final List<String> _List = videoView.getAudioTrackLanguageList();

        if (_List == null) {
            return;
        }
        final AlertDialog.Builder  malertdialog =new AlertDialog.Builder(context,R.style.Dialog_item);
	  	  
	    malertdialog.setTitle(getResources().getStringArray(R.array.videomenu)[4]);
	      
    	LayoutInflater mLinflater = activity.getLayoutInflater();       
        View mView = mLinflater.inflate(R.layout.menu_sounddialog_layout, null);

        ListView showAudioItemsList =(ListView) mView.findViewById(R.id.menu_soundsetting_list);//æ³¨æ„å¼ºè½¬å¦åˆ™ç±»åž‹ä¸åŒ¹é…   
        final ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	
        final String [] audioTrack =_List.toArray(new String[_List.size()]);
        
        int dialog_item_img[]  = null;
        dialog_item_img=new int[_List.size()];
        for(int i=0; i<_List.size(); i++){
        	dialog_item_img[i]=R.color.transparent;
        }
   
	      dialog_item_img[videoView.getSelectAudioTrackId()] = R.drawable.net_select;//
	      
	      Log.i(TAG, "getAudioTrackNumber"+videoView.getAudioTrackNumber());
		for (int i=0; i<_List.size(); i++) {
			HashMap<String, Object> map =  new HashMap<String, Object>(); 
			map.put("ItemContext", audioTrack[i]); 
			map.put("ItemImg", dialog_item_img[i]); 		
			listDialog.add(map);
		}
		
        final SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listDialog,  R.layout.menu_soundsetting_item_dialog, 
                new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
        showAudioItemsList.setAdapter(mSimpleAdapter);
        
        int totalHeight = 0;  
        if(_List.size()>4){
        	for (int i = 0; i < 4; i++) {  
                View listItem = showAudioItemsList.getAdapter().getView(i, null, showAudioItemsList);  
                listItem.measure(0, 0);  
                totalHeight += listItem.getMeasuredHeight();  
               }  
               
               ViewGroup.LayoutParams params = showAudioItemsList.getLayoutParams();  
               
               params.height = totalHeight  
                 + (showAudioItemsList.getDividerHeight() * (3));  
               showAudioItemsList.setLayoutParams(params);
        }
        
        showAudioItemsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i(TAG,"onItemClick position="+position);
				
				
				selectedTrack=position;
				videoView.setSelectAudioTrackId(selectedTrack);
                videoView.setAudioTrackPid(selectedTrack);
                
                notifyListviewForDataChange();
				int dialog_item_img[]  = null;
				dialog_item_img=new int[_List.size()];
		        for(int i=0; i<_List.size(); i++){
		        	dialog_item_img[i]=R.color.transparent;
		        }
			      dialog_item_img[position] = R.drawable.net_select;
			      listDialog.clear();
			      for (int i=0; i<_List.size(); i++) {
						HashMap<String, Object> map =  new HashMap<String, Object>(); 
						map.put("ItemContext", audioTrack[i]); 
						map.put("ItemImg", dialog_item_img[i]); 		
						listDialog.add(map);
					}
			      mSimpleAdapter.notifyDataSetChanged();
			}
		});
        
        malertdialog.setView(mView);
        mAlertDialog = malertdialog.create();
        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
        mAlertDialog.show();
        dialogdelay();
        mAlertDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				delay();
			}
		});
        mAlertDialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface arg0, int keycode, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if(keycode == KeyEvent.KEYCODE_DPAD_DOWN || keycode == KeyEvent.KEYCODE_DPAD_UP ||keycode == KeyEvent.KEYCODE_DPAD_CENTER ){
					dialogdelay();
				}
				return false;
			}
		});
	     
       
        /*AlertDialog.Builder _Builder = new AlertDialog.Builder(this);
        _Builder.setTitle(getString(R.string.dialogTitleAudioSelect, new Object[]
                                    { videoView.getAudioTrackNumber() }));
        String[] _Strings = new String[_List.size()];
        _Builder.setSingleChoiceItems(_List.toArray(_Strings),
        videoView.getSelectAudioTrackId(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface pDialog, int pWhich) {
                doSwitchAudio(pWhich);
                pDialog.dismiss();
            }
        });
        _Builder.setOnItemSelectedListener(new EachItemSelectedListener("showAudio"));
        Dialog _Dialog = _Builder.show();
        DialogTool.disableBackgroundDim(_Dialog);
        DialogTool.setDefaultSelectDisplay(_Dialog);
        dialogAutoDismiss(_Dialog);*/
    }

    private void doSwitchAudio(int pAudioId) {
        selectedTrack = pAudioId;
        videoView.setSelectAudioTrackId(pAudioId);
        videoView.setAudioTrackPid(pAudioId);
        toast.showMessage(context, getString(R.string.toastAudio, new Object[]
                                             { videoView.getAudioTrackLanguageList().get(pAudioId) }), Toast.LENGTH_SHORT);
    }

    private void showSubtitleSelectDialog() {
       final List<String> _List = videoView.getSubtitleLanguageList();
        
       LayoutInflater mLinflater = activity.getLayoutInflater();       
        View mView = mLinflater.inflate(R.layout.dialog_layout, null);
        ListView subtitleLanguageItemList =(ListView) mView.findViewById(R.id.setting_list);//æ³¨æ„å¼ºè½¬å¦åˆ™ç±»åž‹ä¸åŒ¹é…   
             
        int dialog_item_img[]  = null;
        dialog_item_img=new int[_List.size()];
        final ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	
        final String [] subtitleLanguageList =_List.toArray(new String[_List.size()]);
        final int _SubNum = _List.size();
        final int _ExtNum = videoView.getExtSubtitleNumber();
        
        if (_List.size() == 0) {
             int dialog_item_img_null=R.color.transparent;
             String subtitleLanguageListNull =getResources().getString(R.string.nosubTitle);
     	     listDialog.clear();
 			 HashMap<String, Object> map1 =  new HashMap<String, Object>(); 
 			 map1.put("ItemContext", subtitleLanguageListNull); 
 			 map1.put("ItemImg", dialog_item_img_null); 		
 			 listDialog.add(map1);
 			subtitleLanguageItemList.setSelector(new ColorDrawable(Color.TRANSPARENT));//åŽ»æŽ‰listviewçš„ItemèƒŒæ™¯
             SimpleAdapter mSimpleAdapter1 = new SimpleAdapter(this, listDialog, R.layout.setting_rabtn_dialog, 
                     new String[] {"ItemContext","ItemImg"}, new int[]{R.id.sing_dialog_item,R.id.sing_dialog_img});
             subtitleLanguageItemList.setAdapter(mSimpleAdapter1);
        }
		  else{
		       
		        /*_Builder.setTitle(getString(R.string.dialogTitleSubtitleSelect, new Object[]
		                                    { videoView.getSubtitleNumber() }));*/
		        String[] _Strings = new String[_List.size()];
		        int _Select = selectedSubId;
		        if (_ExtNum != 0) {
		            if (_Select >= (_SubNum - _ExtNum))
		            { _Select = _Select - (_SubNum - _ExtNum); }
		            else if (_Select < (_SubNum - _ExtNum))
		            { _Select = _Select + _ExtNum; }
		        }  
		       
		        for(int i=0; i<_List.size(); i++){
		        	dialog_item_img[i]=R.color.transparent;
		        }
		        //videoView.getSubtitleNumber()
			    dialog_item_img[_Select] = R.drawable.net_select;
			    listDialog.clear();
				for (int i=0; i<_List.size(); i++) {
					HashMap<String, Object> map =  new HashMap<String, Object>(); 
					map.put("ItemContext", subtitleLanguageList[i]); 
					map.put("ItemImg", dialog_item_img[i]); 		
					listDialog.add(map);
				}
		        final SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listDialog, R.layout.setting_rabtn_dialog, 
		                new String[] {"ItemContext","ItemImg"}, new int[]{R.id.sing_dialog_item,R.id.sing_dialog_img});
		        subtitleLanguageItemList.setAdapter(mSimpleAdapter);
		        
		        int totalHeight = 0;  
		        if(_List.size()>4){
		        	for (int i = 0; i < 4; i++) {  
		                View listItem = subtitleLanguageItemList.getAdapter().getView(i, null, subtitleLanguageItemList);  
		                listItem.measure(0, 0);  
		                totalHeight += listItem.getMeasuredHeight();  
		               }  
		               
		               ViewGroup.LayoutParams params = subtitleLanguageItemList.getLayoutParams();  
		               
		               params.height = totalHeight  
		                 + (subtitleLanguageItemList.getDividerHeight() * (3));  
		               subtitleLanguageItemList.setLayoutParams(params);
		        }
		         
		        
		        
		        subtitleLanguageItemList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position,
							long arg3) {
						// TODO Auto-generated method stub
						
						int _SubID = position;
		                if (_ExtNum == 0) {
		                    doSwitchSubtitle(_SubID, position);
		                }
		                else {
		                    if (position >= _ExtNum)
		                    { _SubID = position - _ExtNum; }
		                    else if (position < _ExtNum)
		                    { _SubID = position + (_SubNum - _ExtNum); }
		
		                    doSwitchSubtitle(_SubID, position);
		                }
		                
						int dialog_item_img[]  = null;
						dialog_item_img=new int[_List.size()];
				        for(int i=0; i<_List.size(); i++){
				        	dialog_item_img[i]=R.color.transparent; }
				        dialog_item_img[position] = R.drawable.net_select;
				        listDialog.clear();
				        for (int i=0; i<_List.size(); i++) {
							HashMap<String, Object> map =  new HashMap<String, Object>(); 
							map.put("ItemContext", subtitleLanguageList[i]); 
							map.put("ItemImg", dialog_item_img[i]); 		
							listDialog.add(map); }
				        mSimpleAdapter.notifyDataSetChanged();
					}
				});
		  }
		        
		       
		       /*AlertDialog.Builder _Builder = new AlertDialog.Builder(this);
		        final int _SubNum = _List.size();
		        final int _ExtNum = videoView.getExtSubtitleNumber();
		        _Builder.setTitle(getString(R.string.dialogTitleSubtitleSelect, new Object[]
		                                    { videoView.getSubtitleNumber() }));
		        String[] _Strings = new String[_List.size()];
		        int _Select = selectedSubId;
		        if (_ExtNum != 0) {
		            if (_Select >= (_SubNum - _ExtNum))
		            { _Select = _Select - (_SubNum - _ExtNum); }
		            else if (_Select < (_SubNum - _ExtNum))
		            { _Select = _Select + _ExtNum; }
		        }
		
		        _Builder.setSingleChoiceItems(_List.toArray(_Strings), _Select,
		        new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface pDialog, int pWhich) {
		                int _SubID = pWhich;
		
		                if (_ExtNum == 0) {
		                    doSwitchSubtitle(_SubID, pWhich);
		                    pDialog.dismiss();
		                }
		                else {
		                    if (pWhich >= _ExtNum)
		                    { _SubID = pWhich - _ExtNum; }
		                    else if (pWhich < _ExtNum)
		                    { _SubID = pWhich + (_SubNum - _ExtNum); }
		
		                    doSwitchSubtitle(_SubID, pWhich);
		                    pDialog.dismiss();
		                }
		            }
		        });
		        _Builder.setOnItemSelectedListener(new EachItemSelectedListener("showSubtitle"));
		        Dialog _Dialog = _Builder.show();
		        DialogTool.disableBackgroundDim(_Dialog);
		        DialogTool.setDefaultSelectDisplay(_Dialog);
		        //dialogAutoDismiss(_Dialog);
		       // }
*/  
        AlertDialog.Builder _Builder = new AlertDialog.Builder(this,R.style.Dialog_backgroundDimEnabled_false);
        mSwiAlertDialog =  _Builder.create(); 
        mSwiAlertDialog.show();
        mSwiAlertDialog.getWindow().setContentView(mView);

       subswidelay();
       mSwiAlertDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				subdelay();
			}
		});
       mSwiAlertDialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
			   if(    
					arg1 == KeyEvent.KEYCODE_DPAD_UP
					||arg1 == KeyEvent.KEYCODE_DPAD_DOWN
					||arg1 == KeyEvent.KEYCODE_DPAD_CENTER){
				   subswidelay();
				}
				return false;
			}
		});
    }

    private void doSwitchSubtitle(int pSubtitleId, int select) {
        selectedSubId = pSubtitleId;
        videoView.setSelectSubtitleId(pSubtitleId);
        videoView.setSubtitleId(pSubtitleId);
        toast.showMessage(context, getString(R.string.toastSubtitle, new Object[]
                                             { videoView.getSubtitleLanguageList().get(select) }), Toast.LENGTH_SHORT);
    }

    private void showAdvancedOptions() {
	
    	LayoutInflater mLinflater = activity.getLayoutInflater();
        
        View mView = mLinflater.inflate(R.layout.dialog_layout, null);
        advOptItemsList =(ListView) mView.findViewById(R.id.setting_list);//æ³¨æ„å¼ºè½¬å¦åˆ™ç±»åž‹ä¸åŒ¹é… 
        
        ArrayList<String> groups = new ArrayList<String>();
		groups.add(getResources().getStringArray(R.array.setting_items)[0]);
		groups.add(getResources().getStringArray(R.array.setting_items)[1]);
		groups.add(getResources().getStringArray(R.array.setting_items)[2]);
		groups.add(getResources().getStringArray(R.array.setting_items)[3]);
		groups.add(getResources().getStringArray(R.array.setting_items)[4]);
		
        MultListAdapter adapter = new MultListAdapter(this,groups);
        HashMap<Integer, Boolean>hm=new HashMap<Integer, Boolean>();
		hm.put(0,flags[0]);
		hm.put(1,flags[1]);
		hm.put(2,flags[2]);
		hm.put(3,flags[3]);
		hm.put(4,flags[4]);
	
        adapter.setIsSelected(hm);
        advOptItemsList.setAdapter(adapter);
         
		advOptItemsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Boolean isChecked = false;
				//// å–å¾—ViewHolderå¯¹è±¡ï¼Œè¿™æ ·å°±çœåŽ»äº†é€šè¿‡å±‚å±‚çš„findViewByIdåŽ»å®žä¾‹åŒ–æˆ‘ä»¬éœ€è¦çš„cbå®žä¾‹çš„æ­¥éª¤  
				MultListAdapter.ViewHolder  viewHolder=(MultListAdapter.ViewHolder)view.getTag();						
				viewHolder.cb.toggle();// æŠŠCheckBoxçš„é€‰ä¸­çŠ¶æ€æ”¹ä¸ºå½“å‰çŠ¶æ€çš„å,gridviewç¡®ä¿æ˜¯å•ä¸€é€‰ä¸­
				MultListAdapter.getIsSelected().put(position, viewHolder.cb.isChecked());//å°†CheckBoxçš„é€‰ä¸­çŠ¶å†µè®°å½•ä¸‹æ¥ 
				
                Iterator<Entry<Integer, Boolean>> iter = MultListAdapter.getIsSelected().entrySet().iterator();
                while(iter.hasNext()){
                    Map.Entry<Integer, Boolean> info= iter.next();
                    if( info.getKey() == position ) {
                    	isChecked = info.getValue();//èŽ·å–å€¼
                    }
                        
                }
				
                flags[position] = isChecked;
                startTime1 = System.currentTimeMillis();

                if (position == 0 && flags[0] == false) {
                    common.sharedPreferencesOpration(Constants.SHARED, "ContinuePlay", 0, 0, true);
                }
                else if (position == 0 && flags[0] == true) {
                    common.sharedPreferencesOpration(Constants.SHARED, "ContinuePlay", 1, 0, true);
                }
                else if (position == 1 && flags[1] == true) {
                    rewind.setVisibility(View.VISIBLE);
                    forward.setVisibility(View.VISIBLE);
                    common.sharedPreferencesOpration(Constants.SHARED, "BackAndForward", 1, 0, true);
                }
                else if (position == 1 && flags[1] == false) {
                    rewind.setVisibility(View.INVISIBLE);
                    forward.setVisibility(View.INVISIBLE);
                    common.sharedPreferencesOpration(Constants.SHARED, "BackAndForward", 0, 0, true);
                }
                else if (position == 2 && flags[2] == true) {
                    if (videoView.setVideoCvrs(Constants.SCREEN_FULL) != -1) {
                        isFullScreen = true;
                        common.sharedPreferencesOpration(Constants.SHARED, "Proportion", 1, 0, true);
                    }
                }
                else if (position == 2 && flags[2] == false) {
                    if (videoView.setVideoCvrs(Constants.SCREEN_DEFAULT) != -1) {
                        isFullScreen = false;
                        common.sharedPreferencesOpration(Constants.SHARED, "Proportion", 0, 0, true);
                    }
                }
                else if (position == 3 && flags[3] == true) {
                    common.setMode(Constants.ONECYCLE);
                    common.sharedPreferencesOpration(Constants.SHARED, "currPlayMode", Constants.ONECYCLE, 0, true);
                }
                else if (position == 3 && flags[3] == false) {
                    common.setMode(Constants.ALLNOCYCLE);
                    common.sharedPreferencesOpration(Constants.SHARED, "currPlayMode", Constants.ALLNOCYCLE, 0, true);
                }
                else if (position == 4 && flags[4] == true) {
                    mDolbyCertification = 1;
                    common.sharedPreferencesOpration(Constants.SHARED, "DolbyCertification", mDolbyCertification, 0, true);
                }
                else if (position == 4 && flags[4] == false) {
                    mDolbyCertification = 0;
                    common.sharedPreferencesOpration(Constants.SHARED, "DolbyCertification", mDolbyCertification, 0, true);
                }
                else if (position == 5 && flags[5] == true) {
                    showBitRate = 1;
                    bitRate.setVisibility(View.VISIBLE);
                    common.sharedPreferencesOpration(Constants.SHARED, "showBitRate", showBitRate, 0, true);
                }
                else if (position == 5 && flags[5] == false) {
                    showBitRate = 0;
                    bitRate.setVisibility(View.INVISIBLE);
                    common.sharedPreferencesOpration(Constants.SHARED, "showBitRate", showBitRate, 0, true);
                }
			}
		});
		
        AlertDialog.Builder _Builder = new AlertDialog.Builder(this);
        
       //_Builder.setView(mView);
   
        Dialog _Dialog =  _Builder.create(); 
        _Dialog.show();
    
        _Dialog.getWindow().setContentView(mView);

        dialogAutoDismiss(_Dialog);
    }

    protected void onRestart() {
		
		Log.i(TAG,"onRestart ");
        if (!haveOnNewIntent) {
            isAfterReturn = false;
        }
        else {
            haveOnNewIntent = false;
        }

        super.onRestart();
    }

    protected void onStart() {
		Log.i(TAG,"onStart ");
        super.onStart();
    }

    protected void onResume() {
		IntentFilter listchange = new IntentFilter(SONG_CHANGE_ACTION);
        VideoActivity.this.registerReceiver(bcVideoActivity, listchange);
		
		Log.i(TAG,"onResume ");
		//set property true
		SystemProperties.set("sys.umvideoplay", "true");
		Log.i(TAG, "=== SET sys.umvideoplay true");
    	exStorageReceiver=new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
            String path=intent.getData().getPath();
            if(videoView!=null)
            {
                String dataSourcePath=videoView.getmUri().getPath();
                if(dataSourcePath.indexOf(path)>=0)
                {
                	Toast.makeText(VideoActivity.this, R.string.can_not_find_file, Toast.LENGTH_SHORT).show();
                	finish();
                }
            }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addDataScheme("file");
        context.registerReceiver(exStorageReceiver, filter);
        
        IntentFilter listViewSet = new IntentFilter(PIC_SET_FINISH_ACTION);
        listViewSet.addAction(SOUND_SET_FINISH_ACTION);
        context.registerReceiver(mListViewReceiver, listViewSet);
        
        isInVideoBroadcast(0);
        if (database == null) {
            database = Common.getDataBase(this);
            checkDbData(getCurrPath);
        }
        super.onResume();
    }

    protected void onPause() {
		Log.i(TAG,"onPause " + stopPlay);
		//set proper false
		SystemProperties.set("sys.umvideoplay", "false");
		Log.i(TAG, "=== SET sys.umvideoplay false");
		if (stopPlay == true)
		{
	        isInVideoBroadcast(1);
	        Common.setLoadSuccess(false);

	        Log.i(TAG,"on stop:"+(database==null));
	        if (database != null) {
	            int currPosition = videoView.getCurrentPosition();
	            ContentValues values = new ContentValues();
	            Log.i(TAG,"currPosition:"+currPosition+" total:"+total);

	            if ((total - 10000) > currPosition) {
	                values.put("last_play_postion", currPosition);
	                database.update(Constants.TABLE_VIDEO, values, "_data=?",
	                                new String[] { getPlayPath });
	            }
	            else {
	                values.put("last_play_postion", 0);
	                database.update(Constants.TABLE_VIDEO, values, "_data=?",
	                                new String[] { getPlayPath });
	            }
	        }

	        Common.isScanDialogShow = false;

	        if (subSetDialog != null) {
	            subSetDialog.dismiss();
	            subSetDialog = null;
	        }

	        if (menuDialog != null) {
	            menuDialog.dismiss();
	            menuDialog = null;
	        }

	        if (subtitleSetDialog != null) {
	            subtitleSetDialog.dismiss();
	            subtitleSetDialog = null;
	        }

	        if (subtitleAdvSetDialog != null) {
	            subtitleAdvSetDialog.dismiss();
	            subtitleAdvSetDialog = null;
	        }

	        if (stereoItemDialog != null) {
	            stereoItemDialog.dismiss();
	            stereoItemDialog = null;
	        }

	        if (currView != null) {
	            vHandler.removeCallbacks(vThread);
	            vHandler = null;
	        }

	        if ((pointDialog != null) && pointDialog.isShowing()) {
	            pointDialog.dismiss();
	            pointDialog = null;
	        }

	        if (videoView != null) {
	            videoView.setStereoVideoFmt(0);
	            mType = 0;
	            mCurrentMode = 0;
	            isEnded = true;
	            videoView.destroyPlayer();
	            mDisplayManager.SetRightEyeFirst(0);
	            mDisplayManager.SetStereoOutMode(HI_2D_MODE);
	        }

	        if (database.isOpen()) {
	            database.close();
	        }

	        database = null;

	        if (nameSizeDismissHandler != null) {
	            nameSizeDismissHandler.removeCallbacks(nameSizeDismissThread);
	            nameSizeDismissHandler = null;
	            nameSizeDismissThread = null;
	        }

	        if (conn != null) {
	            unbindService(conn);
	            stopService(new Intent(Constants.ACTION));
	        }

	        Log.i(TAG, "mFmt:" + mFmt + " mDisplayManager.getFmt:" + mDisplayManager.getFmt());

	        if (mFmt != mDisplayManager.getFmt())
	        { mDisplayManager.setFmt(mFmt); }

	        context.unregisterReceiver(exStorageReceiver);
	        context.unregisterReceiver(mListViewReceiver);
	        finish();
		}
        stopPlay = true;
		if(isFileChanged){
			Log.d(TAG, "isFileChanged = "+isFileChanged);
			Intent intent = new Intent();  
	        intent.setAction(LIST_CHANGE_ACTION);             
	        intent.putExtra("currpath", getCurrPath);
	        //sendStickyBroadcast(intent);	
	        sendBroadcast(intent);
	        isFileChanged = false;
		}
        super.onPause();
    }

    protected void onStop() {
    updateSelectSubtitleId(getCurrPath);
        super.onStop();
    }

    protected void onDestroy() {
    	unregisterReceiver(mListReceiver);
    	unregisterReceiver(bcVideoActivity);
    	
        super.onDestroy();
    }
    
    
    private void listviewItemClick(int position) {
		int mPositon = position;
		
		  
			  if (mPositon == 1) {
	            
				int tmode = UmtvManager.getInstance().getAudio().getTrackMode();
			  	   
				int f= FileUtil.getIndexFromArray(tmode,FileUtil.track_mode);
				int tmodeIndex = f>3?0:f;
			   // int smodeIndex = FileUtil.getIndexFromArray(smode,FileUtil.sound_mode);
			  //  int flag=0;
			    /*Log.i(TAG,"ZEMIN modeIndex="+smodeIndex);
			      for(int i=0;i<sound_mode_flag.length;i++){
			    	  if(smode==sound_mode_flag[i]){
			    		  flag=i;
			    	  }
			      }
			      Log.i(TAG,"ZEMIN flag="+flag); */
			      int [] dialog_item_img = new int[]{
			   			R.color.transparent,  
		 	   			R.color.transparent, 
		 	   			R.color.transparent, 
		 	   			R.color.transparent
			      };
			      dialog_item_img[tmodeIndex] = R.drawable.net_select;
			      
		  	   final AlertDialog.Builder  malertdialog =new AlertDialog.Builder(context,R.style.Dialog_item);
		  	  
		      malertdialog.setTitle(listItems[position]);
		      LayoutInflater factory = LayoutInflater.from(context);
		      View myView = factory.inflate(R.layout.menu_sounddialog_layout,null);
		    final ListView lisview = (ListView) myView.findViewById(R.id.menu_soundsetting_list);
		    final ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
			for (int i=0; i<track_mode.length; i++) {
				HashMap<String, Object> map =  new HashMap<String, Object>(); 
				map.put("ItemContext", track_mode[i]); 
				map.put("ItemImg", dialog_item_img[i]); 		
				listDialog.add(map);
			}
			final SimpleAdapter mSimpleAdapter = new SimpleAdapter(context, listDialog, R.layout.menu_soundsetting_item_dialog, 
					                                         new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
		     lisview.setAdapter(mSimpleAdapter);
			 lisview.setSelection(tmodeIndex);
		     lisview.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int selectItemSoundMode, long arg3) {
	
					UmtvManager.getInstance().getAudio().setTrackMode(selectItemSoundMode);
					//notifyListviewForDataChange();
					notifyListviewForDataChange();
					
					

				    int [] item_dialog_item_img = new int[]{
		      	   			R.color.transparent,  
		       	   			R.color.transparent, 
		       	   			R.color.transparent, 
		       	   			R.color.transparent, 
		       	   			R.color.transparent 
		      	      };
		    	      item_dialog_item_img[selectItemSoundMode] = R.drawable.net_select;
		    	      listDialog.clear();
			     		for (int i=0; i<track_mode.length; i++) {
			     			HashMap<String, Object> map =  new HashMap<String, Object>();
			        		map.put("ItemContext", track_mode[i]); 
			        		map.put("ItemImg", item_dialog_item_img[i]); 		
			        		listDialog.add(map);
			        	}
			     		mSimpleAdapter.notifyDataSetChanged();
	//			   lisview.setSelection(selectItemSoundMode);
				}
			});
		     malertdialog.setView(myView);
		     mAlertDialog = malertdialog.create();
		     mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); 
		     mAlertDialog.show();
		     dialogdelay();
		     mAlertDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub
					delay();
				}
			});
		     //dialogAutoDismiss(mAlertDialog);
		     
		     lisview.setOnKeyListener(new View.OnKeyListener() {
				
				@Override
				public boolean onKey(View arg0, int keycode, KeyEvent event) {
					// TODO Auto-generated method stub
					if(keycode == KeyEvent.KEYCODE_DPAD_DOWN || keycode == KeyEvent.KEYCODE_DPAD_UP ||keycode == KeyEvent.KEYCODE_DPAD_CENTER ){
						dialogdelay();
					}
					return false;
				}
			});
	
		 
		}  
			  else if(mPositon == 3){
				  subControl();
		
			  }
			  else if(mPositon == 4){
				  showAudioListDialog();
			  }
		 /* else if(3==position){

		      Log.i(TAG,"zemin  position="+position);
		      
		      
	          int mode = UmtvManager.getInstance().getPicture().getPictureMode();  
	          final  int modeIndex= FileUtil.getIndexFromArray(mode,FileUtil.picture_mode);
		 	    int [] dialog_item_img = new int[]{
			    		R.color.transparent,
			    		R.color.transparent,
			    		R.color.transparent,
			    		R.color.transparent,
			    };
	      	   dialog_item_img[modeIndex]=R.drawable.net_select;  
	      	     	   
	    	      AlertDialog.Builder  malertdialog =new AlertDialog.Builder(context,R.style.Dialog_item);
	    	      malertdialog.setTitle(listItems[position]);
	    	      LayoutInflater factory = LayoutInflater.from(context);
	    	      View myView = factory.inflate(R.layout.menu_sounddialog_layout,null);
	    	     final ListView lisview =(ListView) myView.findViewById(R.id.menu_soundsetting_list);
	    	     final ArrayList<HashMap<String, Object>> listDialog = new ArrayList<HashMap<String, Object>>(); 	      	           		
	   		for (int i=0; i<modeVal.length; i++) {
	   			HashMap<String, Object> map =  new HashMap<String, Object>(); 
	      		map.put("ItemContext", modeVal[i]); 
	      		map.put("ItemImg", dialog_item_img[i]); 		
	      		listDialog.add(map);
	      	}
	   		   final SimpleAdapter mSimpleAdapter = new SimpleAdapter(context, listDialog, R.layout.menu_soundsetting_item_dialog, 
	                    new String[] {"ItemContext","ItemImg"}, new int[]{R.id.tv_dialog_item,R.id.tv_dialog_item_img});
	    		
	          lisview.setAdapter(mSimpleAdapter);
			  lisview.setSelection(modeIndex);
	  	      malertdialog.setView(myView);
	    	   mAlertDialog = malertdialog.create();
	    	   mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	    	   mAlertDialog.show();	
	  	      Log.i(TAG,"zemin  malertdialog.create().show()");
	          lisview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int selectItemSoundMode, long arg3) {	
					UmtvManager.getInstance().getPicture().setPictureMode(selectItemSoundMode);
					notifyListviewForDataChange();
					//menuSchedule.notifyDataSetChanged();
				   	
	       	    int [] item_dialog_item_img = new int[]{
	       	    		R.color.transparent,
	       	    		R.color.transparent,
	       	    		R.color.transparent,
	    	    		R.color.transparent
	       	    };
	       	    item_dialog_item_img[selectItemSoundMode]=R.drawable.net_select;	      	    
	            	 listDialog.clear();
		     		for (int i=0; i<modeVal.length; i++) {
		     			HashMap<String, Object> map =  new HashMap<String, Object>(); 
		        		map.put("ItemContext", modeVal[i]); 
		        		map.put("ItemImg", item_dialog_item_img[i]); 		
		        		listDialog.add(map);
		        	}
		     		mSimpleAdapter.notifyDataSetChanged();

				}
			});
		      
	          Message msg = new Message();
	      	msg.what = Constant.PIC_DIALOG_ITEM_SHOW;
	      	mHandler.sendMessage(msg); 
	      	mAlertDialog.setOnDismissListener(new OnDismissListener() {			
				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub
			      	Message msg = new Message();
			      	msg.what = Constant.PIC_DIALOG_ITEM_DISMISS;
			      	mHandler.sendMessage(msg); 
				}
			});
	      	
	          dialogAutoDismiss(mAlertDialog);
		  }  
		  else if(2==position){}
		  else {}
			  
			  */
			  
	}
    
    

	private void notifyListviewForDataChange() {

          /*int smode =UmtvManager.getInstance().getAudio().getSoundMode(); 
	      int  smodeIndex= FileUtil.getIndexFromArray(smode,FileUtil.sound_mode);*/
		
	     /* String soundModeStr="";
	      soundModeStr = getResources().getString(FileUtil.sound_mode[smodeIndex][1]);
	      
	      int mode = UmtvManager.getInstance().getPicture().getPictureMode();  
	      int modeIndex= FileUtil.getIndexFromArray(mode,FileUtil.picture_mode);
	      String videoModeStr="";
	      videoModeStr = getResources().getString(FileUtil.picture_mode[modeIndex][1]);*/
		
		 String TrackModeStr="";
	     int tmode = UmtvManager.getInstance().getAudio().getTrackMode();
	     int  tmodeIndex= FileUtil.getIndexFromArray(tmode,FileUtil.track_mode);
	     TrackModeStr = getResources().getString(FileUtil.track_mode[tmodeIndex][1]);
	      
	      /*Log.i(TAG,"videoModeStr="+videoModeStr);
	      Log.i(TAG,"soundModeStr="+soundModeStr);*/
	     String AudioTrackStr=getResources().getString(R.string.no_track);
	    	if (videoView.getAudioTrackNumber() != 0) {
	    		
	    		AudioTrackStr= videoView.getAudioTrackLanguageList().get(videoView.getSelectAudioTrackId());
	    	}
	      listItemVals = new String[]{
     		     null,
     		    null,null,null,AudioTrackStr
   			}; 
		
		listName.clear();
		for (int i=0; i<listItems.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>(); 
			map.put("ItemContext", listItems[i]);
			map.put("ItemVal",listItemVals[i]);
			map.put("ItemRightImg",listItemRightImgs[i]);
			map.put("ItemLeftImg",listItemLeftImgs[i]);
			listName.add(map);
		}
		menuSchedule.notifyDataSetChanged();
 }
	
	
	 private BroadcastReceiver mListViewReceiver = new BroadcastReceiver() {

	        @Override
	        public void onReceive(Context content, Intent intent) {
	            // TODO Auto-generated method stub
	            String action = intent.getAction();
	            if (Constants.LOG_TAG) {
	                Log.d(TAG, "onReceive--->action:" + action);
	            }
	            if (action.equals(PIC_SET_FINISH_ACTION)) {
	                             	
	            	popUpMenuDialog();
	            	menuList.setSelection(1);
	               
	            } else if (action.equals(SOUND_SET_FINISH_ACTION)) {
	            	
	            	
	            	popUpMenuDialog();
	            	menuList.setSelection(2);
	            	
	            } 
	        }
	    };
	    
	    private BroadcastReceiver mListReceiver = new BroadcastReceiver() {

	        @Override
	        public void onReceive(Context content, Intent intent) {
	            // TODO Auto-generated method stub
	            String action = intent.getAction();
	            if (Constants.LOG_TAG) {
	                Log.d(TAG, "onReceive--->action:" + action);
	            }
	            if(action.equals(VIDEO_LIST_FINISH_ACTION)){
	            	iSmedialist=false;
	            	startThread();
	            }
	        }
	    };
	    
	    
	    private static final int ACTIVITY_FINISH = 0;
	    private static final int DIALOG_FINISH = 1;
	    private static final int SUBDIALOG_FINISH = 2;
	    private static final int SUB_IS_DIALOG_FINISH = 3;
	    private static final int SUB_SWI_DIALOG_FINISH = 4;
	    private static final int SUB_ADV_DIALOG_FINISH = 5;
	    private static final int SUB_COL_DIALOG_FINISH=6;
	    private static final int SUB_EFF_DIALOG_FINISH =7;
	    
	    public static final int DISPEAR_TIME_30s = 30000;
	    

	    /**
	     * handler of finish activity
	     */
	    private Handler finishHandle = new Handler() {
		        public void handleMessage(android.os.Message msg) {
			            if (msg.what == ACTIVITY_FINISH){
			            	
			            	if ((menuDialog != null)&&menuDialog.isShowing()) {
		                        menuDialog.dismiss();
		                    }
			            	else{
			            		Log.i(TAG, "kkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
			            	}
			            	
			        }else if(msg.what == DIALOG_FINISH){
			        	if(mAlertDialog!=null&&mAlertDialog.isShowing()){
			        		mAlertDialog.dismiss();
			        	}
		            	
		            }else if(msg.what == SUBDIALOG_FINISH){
		            	if(subtitleSetDialog!=null&&subtitleSetDialog.isShowing()){
		            		subtitleSetDialog.dismiss();
		            	}
		            	
		            }else if(msg.what == SUB_IS_DIALOG_FINISH){
		            	if(mSubAlertDialog!=null&&mSubAlertDialog.isShowing()){
		            		mSubAlertDialog.dismiss();
		            	}
		            	
		            }
		            else if(msg.what == SUB_SWI_DIALOG_FINISH){
		            	if(mSwiAlertDialog!=null&&mSwiAlertDialog.isShowing()){
		            		mSwiAlertDialog.dismiss();
		            	}
		            	
		            }else if(msg.what == SUB_ADV_DIALOG_FINISH){
		            	if(subtitleAdvSetDialog!=null&&subtitleAdvSetDialog.isShowing()){
		            		subtitleAdvSetDialog.dismiss();
		            	}
		            }
		            else if(msg.what == SUB_COL_DIALOG_FINISH){
		            	if(mColorAlertDialog!=null&&mColorAlertDialog.isShowing()){
		            		mColorAlertDialog.dismiss();
		            	}
		            	
		            }
		            else if(msg.what == SUB_EFF_DIALOG_FINISH){
		            	if(mEffectAlertDialog!=null&&mEffectAlertDialog.isShowing()){
		            		mEffectAlertDialog.dismiss();
		            	}
		            	
		            }
			            
			            
		            
		            
		        }
	    };

	    /**
	     * set delay time to finish activity
	     */
	    public void subcolordelay() {
	        finishHandle.removeMessages(SUB_COL_DIALOG_FINISH);
	        Message message = new Message();
	        message.what = SUB_COL_DIALOG_FINISH;
	        finishHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
	    }
	    public void subeffedelay() {
	        finishHandle.removeMessages(SUB_EFF_DIALOG_FINISH);
	        Message message = new Message();
	        message.what = SUB_EFF_DIALOG_FINISH;
	        finishHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
	    }
	    
	    public void subadvdelay() {
	        finishHandle.removeMessages(SUB_ADV_DIALOG_FINISH);
	        Message message = new Message();
	        message.what = SUB_ADV_DIALOG_FINISH;
	        finishHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
	    }
	    
	    public void subswidelay() {
	        finishHandle.removeMessages(SUB_SWI_DIALOG_FINISH);
	        Message message = new Message();
	        message.what = SUB_SWI_DIALOG_FINISH;
	        finishHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
	    }
	    
	    public void subisdelay() {
	        finishHandle.removeMessages(SUB_IS_DIALOG_FINISH);
	        Message message = new Message();
	        message.what = SUB_IS_DIALOG_FINISH;
	        finishHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
	    }
	    
	    public void subdelay() {
	        finishHandle.removeMessages(SUBDIALOG_FINISH);
	        Message message = new Message();
	        message.what = SUBDIALOG_FINISH;
	        finishHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
	    }
	    
	    public void delay() {
	        finishHandle.removeMessages(ACTIVITY_FINISH);
	        Message message = new Message();
	        message.what = ACTIVITY_FINISH;
	        finishHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
	    }
	    
	    public void dialogdelay() {
	        finishHandle.removeMessages(DIALOG_FINISH);
	        Message message = new Message();
	        message.what = DIALOG_FINISH;
	        finishHandle.sendMessageDelayed(message, DISPEAR_TIME_30s);
	    }
		
		public void doForPlayorPause() {
				if (rewindOrForward) {
				    playStatus.setVisibility(View.INVISIBLE);
				    forwardRate = 1;
				    rewindRate = 1;
				    rewindOrForward = false;
				    videoView.resume();
				    play.setBackgroundResource(R.drawable.pause_button);
				    //                    play.setImageResource(R.drawable.hisil_ic_media_ff);
				}
				else {
				    play_pause();
				}				
		}

		public void doForPageUP() {
			if (rewindOrForward) {
			    playStatus.setVisibility(View.INVISIBLE);
			    forwardRate = 1;
			    rewindRate = 1;
			    rewindOrForward = false;
			    videoView.resume();
			}

			if (mediaFileList != null) {
			    Common.isResume = false;
			    play.setBackgroundResource(R.drawable.pause_button);
			    //                    play.setImageResource(R.drawable.hisil_ic_media_ff);
			    initSeekSecondaryProgress();
			    Common.isShowLoadingToast = true;
			    isContinue = true;
			    if(mediaFileListService.getCurrPosition()==0){
			        Toast.makeText(context,context.getResources().getString(R.string.toast_firstvideo), Toast.LENGTH_SHORT).show();
			    }else{
			    	getVideoInfo(mediaFileList.getPreVideoInfo_NoCycle(null));
			    }
			    
			}
		}

		public void doForPageDown() {
			if (rewindOrForward) {
			    playStatus.setVisibility(View.INVISIBLE);
			    forwardRate = 1;
			    rewindRate = 1;
			    rewindOrForward = false;
			    videoView.resume();
			}

			if (mediaFileList != null) {
			    Common.isResume = false;
			    play.setBackgroundResource(R.drawable.pause_button);
			    //                    play.setImageResource(R.drawable.hisil_ic_media_ff);
			    initSeekSecondaryProgress();
			    Common.isShowLoadingToast = true;
			    isContinue = true;
			    if(mediaFileListService.getCurrPosition()==(mediaFileListService.getList().size()-1)){
			        Toast.makeText(context,context.getResources().getString(R.string.toast_lastvideo), Toast.LENGTH_SHORT).show();
			    }else{
			    	getVideoInfo(mediaFileList.getNextVideoInfo_NoCycle(null));
			    }
			    
			}
		}

		public static class TmpVideoActivity {
			public static Activity activity;
		}
		
}
