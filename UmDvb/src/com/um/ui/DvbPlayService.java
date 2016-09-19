package com.um.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;
import android.view.SurfaceHolder;

import com.um.controller.ParamSave;
import com.um.controller.Player;
import com.um.controller.frequentprog.FrequentProg;
import com.um.controller.frequentprog.FrequentProgManager;
import com.um.dvbstack.Prog;
import com.um.dvbstack.ProviderProgManage;
import com.unionman.dvbplayer.*;
import com.unionman.dvbstorage.SettingsStorage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//@SuppressWarnings("ALL")
public class DvbPlayService extends Service {
    private DvbPlayer mMediaPlayer = null;
    private final static String TAG = "DvbPlayService";
    private IBinder mBinder = null;
    private PlayThread mPlayThread = null;
    private Context mContext = null;
    private SurfaceHolder mSurfaceHolder = null;
    private String mUrl = null;
    private Handler mPostPlayHandler = new Handler();

    private int mProgId = 0;
    private int mDurationTemp = 0;
    private int INTERVAL = 1000;
    private int MIN_VALID_TIME = 5;
    private int TIME_TO_SAVE = 30;
    private FrequentProgManager mFrequentProgManager;

    private Handler mCounterHandler = new Handler();
    private Runnable mCounterRunnable = new Runnable() {
        @Override
        public void run() {
            mCounterHandler.postDelayed(this, INTERVAL);

            // 大于一定间隔保存一次，不频繁操作数据库，也不至于程序异常而丢失数据
            if (++mDurationTemp > TIME_TO_SAVE) {
                saveWatchDurationToDB();
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        mBinder = new DvbServerBinder();
        mFrequentProgManager = new FrequentProgManager(this);
        mPlayThread = new PlayThread();
        mPlayThread.start();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.e(TAG, "DvbPlayService onBind()");
        return mBinder;
    }

    public class DvbServerBinder extends Binder {
    	public static final int ASPECT_CVRS_IGNORE = 0;
    	public static final int ASPECT_CVRS_LETTERBOX = 1;
    	public static final int ASPECT_CVRS_PANSCAN = 2;
    	public static final int ASPECT_CVRS_COMBINED= 3;
    	
    	private int mCurAspectCvrs = ASPECT_CVRS_IGNORE;
    	private SettingsStorage mDvbSettings = null;
    	
        private boolean mIsPlaying = false;
        
        public DvbServerBinder() {
			super();
			mDvbSettings = new SettingsStorage(getContentResolver());
			mCurAspectCvrs = mDvbSettings.getInt("cvrs", ASPECT_CVRS_IGNORE);
		}
        
        public boolean isPlaying() {
            return mIsPlaying;
        }

        public void startPlay(String uri, int delay) {
            Log.i(TAG, "startPlay");
            mPostPlayHandler.removeCallbacksAndMessages(null);
            mPostPlayHandler.postDelayed(new StartPlayRunnable(uri, mCurAspectCvrs), delay);
            mIsPlaying = true;
        }

        public void startPlay(String uri) {
            startPlay(uri, 0);
        }
        
        public void stopPlay() {
            Log.i(TAG, "stopPlay");
            mPostPlayHandler.removeCallbacksAndMessages(null);
            release();
            mIsPlaying = false;
        }

        public void attachView(Context c, SurfaceHolder surfaceHolder) {
            Log.i(TAG, "attachView");
            mContext = c;
            mSurfaceHolder = surfaceHolder;
        }

        public void dettachView() {
            Log.i(TAG, "dettachView");
            mContext = null;
            mSurfaceHolder = null;
        }
        
        public DvbPlayer getMediaPlayer() {
            return mMediaPlayer;
        }


        /**
         * 开始为当前节目计时，如果上一个节目正在计时，则先停止、保存
         * @param progId
         */
        public void startCounter(int progId) {
            if (mProgId != 0) {
                stopCounter();
            }

            mProgId = progId;
            mCounterHandler.removeCallbacks(mCounterRunnable);
            mCounterHandler.postDelayed(mCounterRunnable, INTERVAL);
        }

        public void stopCounter() {
            mCounterHandler.removeCallbacks(mCounterRunnable);

            // 小于最小有效时间的数据认为在切台，不保存；否则切台时会频繁操作数据库；
            // （最后一次的数据可能会丢失掉，但是可以不care）
            if (mDurationTemp > MIN_VALID_TIME) {
                saveWatchDurationToDB(true, mDurationTemp);
            }

            mDurationTemp = 0;
        }
        
        public int getAspectCvrs() {
        	return mCurAspectCvrs;
        }
        
        public boolean setAspectCvrs(int cvrs) {
        	if (0 == doSetAspectCvrs(mMediaPlayer, cvrs)) {
        		mCurAspectCvrs = cvrs;
        		mDvbSettings.putInt("cvrs", cvrs);
        		return true;
        	}
        	
        	return false;
        }
        
    }

    private String getProgName(int progId) {
        Prog prog = ProviderProgManage.GetInstance(this).getProgById(progId);
        if (prog != null) {
            return prog.getName();
        }
        return "";
    }

    private void saveWatchDurationToDB() {
        saveWatchDurationToDB(false, TIME_TO_SAVE);
    }
    /**
     * 保存数据到数据库，更新或新增
     * @param needIncreaseTimes 是否增加观看次数
     */
    private void saveWatchDurationToDB(boolean needIncreaseTimes, int duration) {
        Log.d(TAG, "getProgName(): " + "" + mProgId + " : " + getProgName(mProgId));
        FrequentProg frequentProgInDB = getFrequentProgInDB();
        if (frequentProgInDB != null) {
            mFrequentProgManager.update(mProgId, needIncreaseTimes, duration);
        } else {
            FrequentProg frequentProg = new FrequentProg();
            frequentProg.setProgId(mProgId);
            frequentProg.setProgName(getProgName(mProgId));
            frequentProg.setWatchDuration(duration);
            frequentProg.setWatchTimes(needIncreaseTimes ? 1 : 0);
            frequentProg.setProgMode(Player.GetInstance().getCurMode());
            mFrequentProgManager.add(frequentProg);
        }

        mDurationTemp = 0;
    }

    private FrequentProg getFrequentProgInDB() {
        return mFrequentProgManager.get(mProgId);
    }

    public int getDurationTemp() {
        FrequentProg frequentProgInDB = getFrequentProgInDB();
        if (frequentProgInDB != null) {
            return frequentProgInDB.getWatchDuration();
        }

        return 0;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "DvbPlayService onUnbind()");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");

        mFrequentProgManager.closeDB();

        mPostPlayHandler.removeCallbacksAndMessages(null);
        release();
        mPlayThread.stopThread();

        super.onDestroy();
    }

    private class PlayThread extends Thread {
        private PlayHandler mPlayHandler = null;
        private DvbPlayer mMediaPlayer = null;

        public void doPlay(DvbPlayer mp) {
            mMediaPlayer = mp;
            Handler handler = getHandler();
			handler.removeCallbacksAndMessages(null);
            Message msg = handler.obtainMessage(0, 0);
            handler.sendMessage(msg);

        }

        private class PlayHandler extends Handler {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    if (mMediaPlayer != null) {
                        try {
                            mMediaPlayer.start();
                        } catch (Exception e) {
                            Log.e(TAG, "fatal error: start fail.");
                        }
                    }

                    super.handleMessage(msg);
                }
            }
        }

        @Override
        public void run() {
            Log.v(TAG, "PlayThread is runned!");
            Looper.prepare();
            synchronized (this) {
                mPlayHandler = new PlayHandler();
                notify();
            }

            Looper.loop();
            Log.v(TAG, "PlayThread exit!");
        }

        private Handler getHandler() {
            synchronized (this) {
                if (mPlayHandler == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                return mPlayHandler;
            }
        }

        public void exit() {
            getHandler().post(new Runnable() {
                public void run() {
                    Looper.myLooper().quit();
                }
            });
        }

        void stopThread() {
            Log.v(TAG, "stop PlayThread");
            mPlayHandler.removeMessages(0);
            exit();
        }
    }

    class StartPlayRunnable implements Runnable {
        private String playUri = null;
        private int mCvrs = 0;
        void setPlayUri(String uri) {
            playUri = uri;
        }
        
        StartPlayRunnable() {
        }
        
        StartPlayRunnable(String uri, int cvrs) {
            playUri = uri;
            mCvrs = cvrs;
        }
        
        @Override
        public void run() {
            openVideo(playUri, mCvrs);
            saveLastProgInfo();
        }        
    }

    private void saveLastProgInfo() {
        ParamSave.SaveLastProgInfo(mContext,
                Player.GetInstance().getCurModeProgList().curProgIndex,
                ProviderProgManage.GetInstance(mContext).GetCurMode());
    }

    private void openVideo(String url, int cvrs) {
        Log.i(TAG, "in openVideo().");
        if (mSurfaceHolder == null) {
            Log.e(TAG, "surface is not attched.");
            return;
        }
        if (mMediaPlayer == null) {
            Log.i(TAG, "ready to new MediaPlayer.");
            //try {
                mMediaPlayer = new DvbPlayer();
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.setDisplay(mSurfaceHolder);
                if ((mMediaPlayer.getVideoHeight() != 0)
                        && (mMediaPlayer.getVideoWidth() != 0)) {
                    mSurfaceHolder.setFixedSize(mMediaPlayer.getVideoHeight(),
                            mMediaPlayer.getVideoWidth());
                }
                //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepare();
                doSetAspectCvrs(mMediaPlayer, cvrs);
                mPlayThread.doPlay(mMediaPlayer);
            /*} catch (IOException ex) {
                Log.w(TAG, "Unable to open content: " + url, ex);
                return;
            } catch (IllegalArgumentException ex) {
                Log.w(TAG, "IllegalArgumentException cautch: " + url, ex);
            } catch (Exception ex) {
                Log.e(TAG, "Exception cautch: " + url, ex);
            }*/
            return;            
        }else {
            Log.i(TAG, "switch program.");

            mMediaPlayer.setDataSource(url);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            if ((mMediaPlayer.getVideoHeight() != 0)
                    && (mMediaPlayer.getVideoWidth() != 0)) {
                mSurfaceHolder.setFixedSize(mMediaPlayer.getVideoHeight(),
                        mMediaPlayer.getVideoWidth());
            }
            //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepare();
            doSetAspectCvrs(mMediaPlayer, cvrs);
            mPlayThread.doPlay(mMediaPlayer);

        }
    }
    
    private int doSetAspectCvrs(DvbPlayer mp, int cvrs) {
    	int ret = -1;
    	if (mp == null) {
    		return -1;
    	}
    	
        try {
            Parcel requestParcel = Parcel.obtain();
            requestParcel.writeInterfaceToken("android.media.IMediaPlayer");
            requestParcel.writeInt(9002);
            requestParcel.writeInt(cvrs);
            Parcel replayParcel = Parcel.obtain();
            Class<MediaPlayer> mediaPlayerClass = MediaPlayer.class;
            Method invokeMethod = mediaPlayerClass.getMethod("invoke", Parcel.class, Parcel.class);
            if (invokeMethod != null) {
                invokeMethod.invoke(mp, requestParcel, replayParcel);
                ret = replayParcel.readInt();
            }
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return ret;
    }
    
    private void release() {
        Log.d(TAG, "release");
 
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
