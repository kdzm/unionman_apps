/*
 * Copyright (C) 2007 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.um.music;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.SearchManager;
import android.app.AlertDialog.Builder;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.audiofx.AudioEffect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnKeyListener;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.um.music.MediaPlayMenuActivity;
import com.um.music.IMediaPlaybackService;
import com.um.music.R;
import com.hisilicon.android.sdkinvoke.HiSdkinvoke;
import com.um.music.MusicUtils.ServiceToken;
import com.um.music.util.*;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.constant.EnumSoundTrack;

public class MediaPlaybackActivity extends Activity implements MusicUtils.Defs,
    View.OnTouchListener, View.OnLongClickListener {
    private static final int USE_AS_RINGTONE = CHILD_MENU_BASE;
    private static final String TAG = "MediaPlaybackActivity";
    private boolean mSeeking = false;
    private boolean mPowerdown = true;
    private boolean mDeviceHasDpad;
    private long mStartSeekPos = 0;
    private long mLastSeekEventTime;
    private WakeLock mWakeLock;
    private IMediaPlaybackService mService = null;
    private RepeatingImageButton mPrevButton;
    private ImageButton mPauseButton;
    private RepeatingImageButton mNextButton;
    //private ImageButton mRepeatButton;
    private ImageButton mShuffleButton;
    private ImageButton mQueueButton;
    private Worker mAlbumArtWorker;
    private AlbumArtHandler mAlbumArtHandler;
    private Toast mToast;
    private int mTouchSlop;
    private CustomToast myToast;

    public static Lyric mLyric;
    public static LyricView lyricView;
    boolean isPlay = false;
    private Camera mCamera = new Camera();

    private ServiceToken mToken;
    public static Long elapseTime;
    public static Long nowTime;
    public static Long isNext;
    public static boolean isFirst = false;
    public static boolean lyricThreaTmp = false; 
    public static boolean isCease = false; 
    public String  filename = "";

    private long mPosition;
    private boolean isPlaying = false;
    public static MediaFileList mediaFileList = null;
    public static MediaFileListService mediaFileListService = null;
    public MyServiceConnection conn = null;
    private String currPlayPath = null;
    public static String HOME = "";

    private Thread lyricThread = null;
    
    private LinearLayout menuLinearLayout;
    private String[] listMenuItems;
    private ArrayList<HashMap<String, Object>> listMenuName = new ArrayList<HashMap<String, Object>>(); 
	private String[] listMenuItemVals;
    private int[] listMenuItemRightImgs;
    private int[] listMenuItemLeftImgs;
    private SimpleAdapter mSchedule;
    
    private Context mContext = this;
    
    private ImageView imgView [];

    private boolean stopPlay = true;
    
    private final static int PROGRESS_ACCURACY = 1000;
    
    private static final int SOUND_DIALOG_DISMISS_BYTIME = 2;
    private static final int TRACK_DIALOG_DISMISS_BYTIME = 4;
    

    private  AlertDialog trackAlertdialog;
    private  AlertDialog soundAlertdialog;

    private int trackModeIndex;
    private int soundModeIndex;

    boolean isTrackDialogDismiss =true;
    boolean isSoundDialogDismiss =true;
    
    private TextView  sound_menu_btn;
    private TextView  track_menu_btn;
    public static final int DISPEAR_TIME_3s = 3000;
    
    public MediaPlaybackActivity() {
    }

    private BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                if (mWakeLock != null) {
                    if (mWakeLock.isHeld()) {
                        mWakeLock.release();
                    }
                }

                if (mService == null) {
                    return;
                }

                if (mPowerdown) {
                    try {
                        if (mService.isPlaying()) {
                            isPlaying = true;
                            doPauseResume();
                        }
                    }
                    catch (RemoteException ex) {
                    }
                }

                try {
                    mPosition = mService.position();
                }
                catch (RemoteException ex) {
                }
            }

            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                mProgress.setProgress((int)(PROGRESS_ACCURACY * mPosition / mDuration));

                try {
                    if (mService != null && !mService.isPlaying()) {
                        if (isPlaying) {
                            doPauseResume();
                            isPlaying = false;
                        }
                    }
                }
                catch (RemoteException ex) {
                }
            }
        }
    };

    private void registerpowerReceiver() {
        IntentFilter powerFilter = new IntentFilter();
        powerFilter.addAction(Intent.ACTION_SCREEN_OFF);
        powerFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(powerReceiver, powerFilter);
    }

    public class MyServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            mediaFileListService = ((MediaFileListService.MyBinder) service)
                                   .getService();
        }

        public void onServiceDisconnected(ComponentName arg0) {
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        lyricThreaTmp=false;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mAlbumArtWorker = new Worker("album art worker");
        mAlbumArtHandler = new AlbumArtHandler(mAlbumArtWorker.getLooper());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.audio_player);
        String chipVersion = HiSdkinvoke.getChipVersion();
        Log.i(TAG, "chipVersion:" + chipVersion);

        if (chipVersion.equals("Unknown chip ID"))
        { finish(); }

        mCurrentTime = (TextView) findViewById(R.id.currenttime);
        mTotalTime = (TextView) findViewById(R.id.totaltime);
       
        mProgress = (SeekBar) findViewById(R.id.progress);
        mAlbum = (ImageView) findViewById(R.id.album);
        mArtistName = (RotateTextView) findViewById(R.id.artistname);
        mAlbumName = (RotateTextView) findViewById(R.id.albumname);
        mAlbumName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				lyricThreaTmp=false;
			}
		});
        mTrackName = (RotateTextView) findViewById(R.id.trackname);
        lyricView = (LyricView) findViewById(R.id.audio_lrc);
        lyricView.setFocusable(false);
        View v = (View) mArtistName.getParent();
        v.setOnTouchListener(this);
        v.setOnLongClickListener(this);
        v = (View) mAlbumName.getParent();
        v.setOnTouchListener(this);
        v.setOnLongClickListener(this);
        v = (View) mTrackName.getParent();
        v.setOnTouchListener(this);
        mItemImage = (ImageView) findViewById(R.id.ItemImage);
        mItemTitle = (TextView) findViewById(R.id.ItemTitle);
        //mItemPath = (TextView) findViewById(R.id.ItemText);
        mPrevButton = (RepeatingImageButton) findViewById(R.id.prev);
        mPrevButton.setOnClickListener(mPrevListener);
        mPrevButton.setRepeatListener(mRewListener, 260);
        mPauseButton = (ImageButton) findViewById(R.id.pause);
        mPauseButton.requestFocus();
        mPauseButton.animate().scaleX(1.1f).scaleY(1.1f)
        .setDuration(100).start();
        mPauseButton.setOnClickListener(mPauseListener);
        mNextButton = (RepeatingImageButton) findViewById(R.id.next);
        mNextButton.setOnClickListener(mNextListener);
        mNextButton.setRepeatListener(mFfwdListener, 260);
        seekmethod = 1;
        mDeviceHasDpad = (getResources().getConfiguration().navigation == Configuration.NAVIGATION_DPAD);
        mQueueButton = (ImageButton) findViewById(R.id.curplaylist);
        mQueueButton.setOnClickListener(mQueueListener);
        mShuffleButton = ((ImageButton) findViewById(R.id.shuffle));
        mShuffleButton.setOnClickListener(mShuffleListener);
        //mRepeatButton = ((ImageButton) findViewById(R.id.repeat));
        
        imgView = new ImageView[]{mPrevButton,mPauseButton,mNextButton,mQueueButton,mShuffleButton/*,mRepeatButton*/};
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
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
                                   .getClass().getName());
        //if (mProgress instanceof SeekBar) {
            //SeekBar seeker = (SeekBar) mProgress;
        mProgress.setMax(PROGRESS_ACCURACY);
        mProgress.setOnSeekBarChangeListener(mSeekListener);
        mProgress.setOnKeyListener(new OnKeyListener(){

            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // TODO Auto-generated method stub

            	long position = 0;
            	long duration = 0;
            	if (arg2.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT
            			|| arg2.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT)
            	{
            		try {
            			if (mService != null) {
                            if (!mService.isPlaying()) {
                                mService.play();
                            }
            			}
            		}
            		catch (RemoteException ex) {
                    }
            		
            		long now = SystemClock.elapsedRealtime();
                    if ((now - mLastSeekEventTime) < 250) {
                        //mLastSeekEventTime = now;
                        return true;
                    }
                    else
                    {
                    	mLastSeekEventTime = now;
                    }
                    
                    switch (arg2.getKeyCode())
                    {
                    	case KeyEvent.KEYCODE_DPAD_LEFT:
                    		try {
        	            		position = mService.position();
        	            		//duration = mService.duration();
	                    		if (position > 5000)
	                    		{
	                    			mService.seek(position - 5000);
	                    			
	                    		}
	                    		else
	                    		{
	                    			mService.seek(0);
	                    		}
	                    		queueNextRefresh(200);
                    		}
                    		catch (RemoteException ex) {
                            }
                    		break;
                    	case KeyEvent.KEYCODE_DPAD_RIGHT:
                    		try {
        	            		position = mService.position();
        	            		duration = mService.duration();
	                    		if ((duration - position) > 5000)
	                    		{
	                    			mService.seek(position + 4000);
	                    		}
	                    		else
	                    		{
	                    			mService.seek(duration);
	                    		}
	                    		queueNextRefresh(200);
                    		}
                    		catch (RemoteException ex) {
                            }
                    		break;
                    	default:
                    		break;
                    }
                    return true;
            	}

            	return false;
            }});

        
        registerpowerReceiver();
        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        Intent intent = getIntent();
        mediaFileList = intent.getParcelableExtra("MediaFileList");

        if (mediaFileList != null && mediaFileList.getId() == 1) {
            Intent service = new Intent(Constants.ACTION);
            conn = new MyServiceConnection();
            MediaPlaybackActivity.this.bindService(service, conn,
                                                   Context.BIND_AUTO_CREATE);
        }

        if (Environment.getExternalStorageDirectory().exists()) {
            HOME = Environment.getExternalStorageDirectory().toString()
                   + "/music";
        }
        else {
            HOME = "/var/music";
        }          
    }

    int mInitialX = -1;
    int mLastX = -1;
    int mTextWidth = 0;
    int mViewWidth = 0;
    boolean mDraggingLabel = false;

    TextView textViewForContainer(View v) {
        View vv = v.findViewById(R.id.artistname);

        if (vv != null)
        { return (TextView) vv; }

        vv = v.findViewById(R.id.albumname);

        if (vv != null)
        { return (TextView) vv; }

        vv = v.findViewById(R.id.trackname);

        if (vv != null)
        { return (TextView) vv; }

        return null;
    }

    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        TextView tv = textViewForContainer(v);

        if (tv == null) {
            return false;
        }

        if (action == MotionEvent.ACTION_DOWN) {
            v.setBackgroundColor(0xff606060);
            mInitialX = mLastX = (int) event.getX();
            mDraggingLabel = false;
        }
        else if (action == MotionEvent.ACTION_UP
                 || action == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(0);

            if (mDraggingLabel) {
                Message msg = mLabelScroller.obtainMessage(0, tv);
                mLabelScroller.sendMessageDelayed(msg, 1000);
            }
        }
        else if (action == MotionEvent.ACTION_MOVE) {
            if (mDraggingLabel) {
                int scrollx = tv.getScrollX();
                int x = (int) event.getX();
                int delta = mLastX - x;

                if (delta != 0) {
                    mLastX = x;
                    scrollx += delta;

                    if (scrollx > mTextWidth) {
                        // scrolled the text completely off the view to the left
                        scrollx -= mTextWidth;
                        scrollx -= mViewWidth;
                    }

                    if (scrollx < -mViewWidth) {
                        // scrolled the text completely off the view to the
                        // right
                        scrollx += mViewWidth;
                        scrollx += mTextWidth;
                    }

                    tv.scrollTo(scrollx, 0);
                }

                return true;
            }

            int delta = mInitialX - (int) event.getX();

            if (Math.abs(delta) > mTouchSlop) {
                // start moving
                mLabelScroller.removeMessages(0, tv);

                // Only turn ellipsizing off when it's not already off, because
                // it
                // causes the scroll position to be reset to 0.
                if (tv.getEllipsize() != null) {
                    tv.setEllipsize(null);
                }

                Layout ll = tv.getLayout();

                // layout might be null if the text just changed, or ellipsizing
                // was just turned off
                if (ll == null) {
                    return false;
                }

                // get the non-ellipsized line width, to determine whether
                // scrolling
                // should even be allowed
                mTextWidth = (int) tv.getLayout().getLineWidth(0);
                mViewWidth = tv.getWidth();

                if (mViewWidth > mTextWidth) {
                    tv.setEllipsize(TruncateAt.END);
                    v.cancelLongPress();
                    return false;
                }

                mDraggingLabel = true;
                tv.setHorizontalFadingEdgeEnabled(true);
                v.cancelLongPress();
                return true;
            }
        }

        return false;
    }

    Handler mLabelScroller = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView tv = (TextView) msg.obj;
            int x = tv.getScrollX();
            x = x * 3 / 4;
            tv.scrollTo(x, 0);

            if (x == 0) {
                tv.setEllipsize(TruncateAt.END);
            }
            else {
                Message newmsg = obtainMessage(0, tv);
                mLabelScroller.sendMessageDelayed(newmsg, 15);
            }
        }
    };
    
    public boolean onLongClick(View view) {
        CharSequence title = null;
        String mime = null;
        String query = null;
        String artist;
        String album;
        String song;
        long audioid;
        
        try {
            artist = mService.getArtistName();
            album = mService.getAlbumName();
            song = mService.getTrackName();
            audioid = mService.getAudioId();
        }
        catch (RemoteException ex) {
            return true;
        }
        catch (NullPointerException ex) {
            // we might not actually have the service yet
            return true;
        }

        if (MediaStore.UNKNOWN_STRING.equals(album)
            && MediaStore.UNKNOWN_STRING.equals(artist) && song != null
            && song.startsWith("recording")) {
            // not music
            return false;
        }

        if (audioid < 0) {
            return false;
        }

        Cursor c = MusicUtils.query(this, ContentUris.withAppendedId(
                                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audioid),
                                    new String[] { MediaStore.Audio.Media.IS_MUSIC }, null, null,
                                    null);
        boolean ismusic = true;

        if (c != null) {
            if (c.moveToFirst()) {
                ismusic = c.getInt(0) != 0;
            }

            c.close();
        }

        if (!ismusic) {
            return false;
        }

        boolean knownartist = (artist != null)
                              && !MediaStore.UNKNOWN_STRING.equals(artist);
        boolean knownalbum = (album != null)
                             && !MediaStore.UNKNOWN_STRING.equals(album);

        if (knownartist && view.equals(mArtistName.getParent())) {
            title = artist;
            query = artist;
            mime = MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE;
        }
        else if (knownalbum && view.equals(mAlbumName.getParent())) {
            title = album;

            if (knownartist) {
                query = artist + " " + album;
            }
            else {
                query = album;
            }

            mime = MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE;
        }
        else if (view.equals(mTrackName.getParent()) || !knownartist
                 || !knownalbum) {
            if ((song == null) || MediaStore.UNKNOWN_STRING.equals(song)) {
                // A popup of the form "Search for null/'' using ..." is pretty
                // unhelpful, plus, we won't find any way to buy it anyway.
                return true;
            }

            title = song;

            if (knownartist) {
                query = artist + " " + song;
            }
            else {
                query = song;
            }

            mime = "audio/*"; // the specific type doesn't matter, so don't
            // bother retrieving it
        }
        else {
            throw new RuntimeException("shouldn't be here");
        }

        title = getString(R.string.mediasearch, title);
        Intent i = new Intent();
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(MediaStore.INTENT_ACTION_MEDIA_SEARCH);
        i.putExtra(SearchManager.QUERY, query);

        if (knownartist) {
            i.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist);
        }

        if (knownalbum) {
            i.putExtra(MediaStore.EXTRA_MEDIA_ALBUM, album);
        }

        i.putExtra(MediaStore.EXTRA_MEDIA_TITLE, song);
        i.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, mime);
        startActivity(Intent.createChooser(i, title));
        return true;
    }

    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            mLastSeekEventTime = 0;
            mFromTouch = true;
        }
        public void onProgressChanged(SeekBar bar, int progress,
        boolean fromuser) {
            if (!fromuser || (mService == null))
            { return; }

                mPosOverride = mDuration * progress / PROGRESS_ACCURACY;
                //try {
                    //mService.seek(mPosOverride);
                //}
                //catch (RemoteException ex) {
                //}
                if (!mFromTouch) {
                    //refreshNow();
                    mPosOverride = -1;
                }
            }
        public void onStopTrackingTouch(SeekBar bar) {
            mPosOverride = -1;
            mFromTouch = false;
        }
    };

    private View.OnClickListener mQueueListener = new View.OnClickListener() {
        public void onClick(View v) {
            try {
                currPlayPath = mService.getPath();
            }
            catch (RemoteException ex) {
            }
            stopPlay = false;
            TmpMediaPlaybackActivity.activity = MediaPlaybackActivity.this;
            Intent intent = new Intent();
            intent.setClassName("com.um.music",
                                "com.um.music.FileListAcvitity");
           // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(intent.getData(), "audio/*");
            intent.putExtra("MediaFileList", mediaFileList);
            intent.putExtra("path", currPlayPath);
            startActivity(intent);
        }
    };

    private View.OnClickListener mShuffleListener = new View.OnClickListener() {
        public void onClick(View v) {
            toggleShuffle(1);
        }
    };
    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
        }
    };

    private View.OnClickListener mPrevListener = new View.OnClickListener() {
        public void onClick(View v) {
            doForPrev();
        }
    };

    private View.OnClickListener mNextListener = new View.OnClickListener() {
        public void onClick(View v) {
            doForNext();
        }
    };

    private RepeatingImageButton.RepeatListener mRewListener = new RepeatingImageButton.RepeatListener() {
        public void onRepeat(View v, long howlong, int repcnt) {
            scanBackward(repcnt, howlong);
        }
    };

    private RepeatingImageButton.RepeatListener mFfwdListener = new RepeatingImageButton.RepeatListener() {
        public void onRepeat(View v, long howlong, int repcnt) {
            scanForward(repcnt, howlong);
        }
    };

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
    	// TODO Auto-generated method stub
    	if (stopPlay == true)
    	{
	    	paused = true;
        	MediaPlaybackService.isStopedPlayer = true;
	        if (mService != null) {
	            try {
	                mService.stop();
	                Log.i(TAG,"onStop,so we stop music playing");
	            }
	            catch (RemoteException e) {
	                e.printStackTrace();
	            }
	        }
	        mHandler.removeMessages(REFRESH);
	        unregisterReceiver(mStatusListener);
	        MusicUtils.unbindFromService(mToken);
	        mService = null;
	        Common.isFirstMediaFile = false;
	        Common.isLastMediaFile = false;
    	}
    	stopPlay = true;
    	super.onPause();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        paused = false;
        mToken = MusicUtils.bindToService(this, osc);
        if (mToken == null) {
            // something went wrong
            mHandler.sendEmptyMessage(QUIT);
        }

        IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
        f.addAction(MediaPlaybackService.META_CHANGED);
        registerReceiver(mStatusListener, new IntentFilter(f));
        updateTrackInfo();
        long next = refreshNow();
        queueNextRefresh(next);
        MediaPlaybackService.isStopedPlayer = false;
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public void onResume() {

        super.onResume();
        startPlayback();
        setPauseButtonImage();
    }

    @Override
    public void onDestroy() {
        mAlbumArtWorker.quit();
        removeDialogs();
        super.onDestroy();

        if (powerReceiver != null) {
            unregisterReceiver(powerReceiver);
        }

        if (conn != null) {
            unbindService(conn);
            stopService(new Intent(Constants.ACTION));
        }
    }

    
    private void removeDialogs(){
    	finishHandle.removeMessages(SOUND_DIALOG_DISMISS_BYTIME);
    	finishHandle.removeMessages(TRACK_DIALOG_DISMISS_BYTIME);
    	
    	if(!isSoundDialogDismiss){
    		soundAlertdialog.dismiss();
    		isSoundDialogDismiss=true;
    	}
    	
    	if(!isTrackDialogDismiss){
    		trackAlertdialog.dismiss();
    		isTrackDialogDismiss=true;
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Don't show the menu items if we got launched by path/filedescriptor,
        // or
        // if we're in one shot mode. In most cases, these menu items are not
        // useful in those modes, so for consistency we never show them in these
        // modes, instead of tailoring them to the specific file being played.
        if (MusicUtils.getCurrentAudioId() >= 0) {
            menu.add(0, GOTO_START, 0, R.string.goto_start).setIcon(
                R.drawable.ic_menu_music_library);
            menu.add(0, PARTY_SHUFFLE, 0, R.string.party_shuffle); // icon will
            // be set in
            // onPrepareOptionsMenu()
            SubMenu sub = menu.addSubMenu(0, ADD_TO_PLAYLIST, 0,
                                          R.string.add_to_playlist).setIcon(
                              android.R.drawable.ic_menu_add);
            // these next two are in a separate group, so they can be
            // shown/hidden as needed
            // based on the keyguard state
            menu.add(1, USE_AS_RINGTONE, 0, R.string.ringtone_menu_short)
            .setIcon(R.drawable.ic_menu_set_as_ringtone);
            menu.add(1, DELETE_ITEM, 0, R.string.delete_item).setIcon(
                R.drawable.ic_menu_delete);
            Intent i = new Intent(
                AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);

            if (getPackageManager().resolveActivity(i, 0) != null) {
                menu.add(0, EFFECTS_PANEL, 0, R.string.effectspanel).setIcon(
                    R.drawable.ic_menu_eq);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mService == null)
        { return false; }

        MenuItem item = menu.findItem(PARTY_SHUFFLE);

        if (item != null) {
            int shuffle = MusicUtils.getCurrentShuffleMode();

            if (shuffle == MediaPlaybackService.SHUFFLE_AUTO) {
                item.setIcon(R.drawable.ic_menu_party_shuffle);
                item.setTitle(R.string.party_shuffle_off);
            }
            else {
                item.setIcon(R.drawable.ic_menu_party_shuffle);
                item.setTitle(R.string.party_shuffle);
            }
        }

        item = menu.findItem(ADD_TO_PLAYLIST);

        if (item != null) {
            SubMenu sub = item.getSubMenu();
            MusicUtils.makePlaylistMenu(this, sub);
        }

        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        menu.setGroupVisible(1, !km.inKeyguardRestrictedInputMode());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        try {
            switch (item.getItemId()) {
                case GOTO_START:
                    intent = new Intent();
                    intent.setClass(this, MusicBrowserActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;

                case USE_AS_RINGTONE: {
                        // Set the system setting to make this the current ringtone
                        if (mService != null) {
                            MusicUtils.setRingtone(this, mService.getAudioId());
                        }
                       return true;
                    }

                case PARTY_SHUFFLE:
                    MusicUtils.togglePartyShuffle();
                    setShuffleButtonImage();
                    break;

                case NEW_PLAYLIST: {
                        intent = new Intent();
                        intent.setClass(this, CreatePlaylist.class);
                        startActivityForResult(intent, NEW_PLAYLIST);
                        return true;
                    }

                case PLAYLIST_SELECTED: {
                        long[] list = new long[1];
                        list[0] = MusicUtils.getCurrentAudioId();
                        long playlist = item.getIntent().getLongExtra("playlist", 0);
                        MusicUtils.addToPlaylist(this, list, playlist);
                        return true;
                    }

                case DELETE_ITEM: {
                        if (mService != null) {
                            long[] list = new long[1];
                            list[0] = MusicUtils.getCurrentAudioId();
                            Bundle b = new Bundle();
                            String f;

                            if (android.os.Environment.isExternalStorageRemovable()) {
                                f = getString(R.string.delete_song_desc,
                                              mService.getTrackName());
                            }
                            else {
                                f = getString(R.string.delete_song_desc_nosdcard,
                                              mService.getTrackName());
                            }

                            b.putString("description", f);
                            b.putLongArray("items", list);
                            intent = new Intent();
                            intent.setClass(this, DeleteItems.class);
                            intent.putExtras(b);
                            startActivityForResult(intent, -1);
                        }

                        return true;
                    }

                case EFFECTS_PANEL: {
                        Intent i = new Intent(
                            AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION,
                                   mService.getAudioSessionId());
                        startActivityForResult(i, EFFECTS_PANEL);
                        return true;
                    }
            }
        }
        catch (RemoteException ex) {
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case NEW_PLAYLIST:
                Uri uri = intent.getData();

                if (uri != null) {
                    long[] list = new long[1];
                    list[0] = MusicUtils.getCurrentAudioId();
                    int playlist = Integer.parseInt(uri.getLastPathSegment());
                    MusicUtils.addToPlaylist(this, list, playlist);
                }

                break;
        }
    }

    private final int keyboard[][] = {
        {
            KeyEvent.KEYCODE_Q, KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_E,
            KeyEvent.KEYCODE_R, KeyEvent.KEYCODE_T, KeyEvent.KEYCODE_Y,
            KeyEvent.KEYCODE_U, KeyEvent.KEYCODE_I, KeyEvent.KEYCODE_O,
            KeyEvent.KEYCODE_P,
        },
        {
            KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_S, KeyEvent.KEYCODE_D,
            KeyEvent.KEYCODE_F, KeyEvent.KEYCODE_G, KeyEvent.KEYCODE_H,
            KeyEvent.KEYCODE_J, KeyEvent.KEYCODE_K, KeyEvent.KEYCODE_L,
            KeyEvent.KEYCODE_DEL,
        },
        {
            KeyEvent.KEYCODE_Z, KeyEvent.KEYCODE_X, KeyEvent.KEYCODE_C,
            KeyEvent.KEYCODE_V, KeyEvent.KEYCODE_B, KeyEvent.KEYCODE_N,
            KeyEvent.KEYCODE_M, KeyEvent.KEYCODE_COMMA,
            KeyEvent.KEYCODE_PERIOD, KeyEvent.KEYCODE_ENTER
        }

    };

    private int lastX;
    private int lastY;

    private boolean seekMethod1(int keyCode) {
        if (mService == null)
        { return false; }

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 3; y++) {
                if (keyboard[y][x] == keyCode) {
                    int dir = 0;

                    // top row
                    if (x == lastX && y == lastY)
                    { dir = 0; }
                    else if (y == 0 && lastY == 0 && x > lastX)
                    { dir = 1; }
                    else if (y == 0 && lastY == 0 && x < lastX)
                    { dir = -1; }
                    // bottom row
                    else if (y == 2 && lastY == 2 && x > lastX)
                    { dir = -1; }
                    else if (y == 2 && lastY == 2 && x < lastX)
                    { dir = 1; }
                    // moving up
                    else if (y < lastY && x <= 4)
                    { dir = 1; }
                    else if (y < lastY && x >= 5)
                    { dir = -1; }
                    // moving down
                    else if (y > lastY && x <= 4)
                    { dir = -1; }
                    else if (y > lastY && x >= 5)
                    { dir = 1; }

                    lastX = x;
                    lastY = y;

                    try {
                        mService.seek(mService.position() + dir * 5);
                    }
                    catch (RemoteException ex) {
                    }

                    refreshNow();
                    return true;
                }
            }
        }

        lastX = -1;
        lastY = -1;
        return false;
    }

    private boolean seekMethod2(int keyCode) {
        if (mService == null)
        { return false; }

        for (int i = 0; i < 10; i++) {
            if (keyboard[0][i] == keyCode) {
                int seekpercentage = 100 * i / 10;

                try {
                    mService.seek(mService.duration() * seekpercentage / 100);
                }
                catch (RemoteException ex) {
                }

                refreshNow();
                return true;
            }
        }

        return false;
    }

    
    private int count = 0; 
    Handler tmpHander = new Handler() {
   		public void handleMessage(Message msg) {
   			Log.i(TAG, "=======tmpHander========");
   			count = 0;
            super.handleMessage(msg);
        }
   	};
   	
   	
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.ACTION_DOWN:
                if (mPowerdown) {
                    doPauseResume();
                }

                break;

            case KeyEvent.KEYCODE_BACK:    

    	    	count++;
    	    	 if(count<2)
    	         {
    	       		//Toast.makeText(mContext,mContext.getResources().getString(R.string.toast_exit),Toast.LENGTH_SHORT).show();
    	    	  if(!isTrackDialogDismiss){
    	    		trackAlertdialog.dismiss();
    	    		isTrackDialogDismiss=true;
    	    	  }
    	    	    	
  	        	  myToast=new CustomToast(mContext);
  	        	  myToast.getmDialog().setOnDismissListener(new OnDismissListener() {
  					
  					@Override
  					public void onDismiss(DialogInterface arg0) {
  						// TODO Auto-generated method stub
  						if(myToast.isBackDismiss()==true){
  							mPowerdown = false;
  							finish();
  						}
  						
  					}
  	        	  });
  	        	  myToast.setMessage(R.string.toast_exit);
  	        	  myToast.showTime(2000);
  	        	  myToast.show();
  	        	  
  	        	  
  	          
    	            tmpHander.sendEmptyMessageDelayed(0, 2000);//ï¿½Ú¢ï¿½ï¿½Ö·ï¿½ï¿½ï¿½ï¿½Ó³ï¿½ï¿½ï¿½Ï¢
    	            return true;
    	         }
    	        else{  	 
    	        	count=0;
    	        	mPowerdown = false;
    		
    	        }
            	     	
        }

        return super.onKeyUp(keyCode, event);
    }

    private boolean useDpadMusicControl() {
        if (mDeviceHasDpad
            && (mPrevButton.isFocused() || mNextButton.isFocused() || mPauseButton
                .isFocused())) {
            return true;
        }

        return false;
    }
   
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int direction = -1;
        int repcnt = event.getRepeatCount();
        
        String titleName;     

        if ((seekmethod == 0) ? seekMethod1(keyCode) : seekMethod2(keyCode))
        { return true; }
      
		switch (keyCode) {
		case KeyEvent.KEY_PLAY_PAUSE:
			Log.i(TAG,"KeyEvent.KEY_PLAY_PAUSE");
			  doPauseResume();	
			break;
		case KeyEvent.KEY_CEASE:
		    Log.i(TAG,"KeyEvent.KEY_CEASE");
		    
	    	if (mService != null) {
		        try {
		            mService.stop();
		        }
		        catch (RemoteException e) {
		            e.printStackTrace();
		        }
               isCease = true;  
               setPauseButtonImage();
		    }
		    break;
		case KeyEvent.KEY_PREV:
			Log.i(TAG,"KeyEvent.KEY_PREV");
			doForPrev();
			break;
		case KeyEvent.KEY_NEXT:
		  Log.i(TAG,"KeyEvent.KEY_NEXT");
		    doForNext();
		    break;
		case KeyEvent.KEY_TRACK:
         	TrackModeQuickKeyHandle();
         	break;
		case KeyEvent.KEYCODE_SOUNDMODE:
           	soundModeQuickKeyHandle();
           	break;
        case KeyEvent.KEYCODE_MENU:
	         try{
	        	  String path = mService.getPath();
	        	  titleName = path;}
	        	 //titleName = path.substring(path.lastIndexOf('/') + 1); 
	        catch (RemoteException ex) {
	            return true;
	        }
	         TmpMediaPlaybackActivity.activity = this;
    		Intent intent=new Intent(MediaPlaybackActivity.this,MediaPlayMenuActivity.class); 
    		intent.putExtra("titleName", titleName);
    		//intent.putExtra("oneurl", oneurl); 
    		
    		startActivity(intent); 
    		stopPlay = false;
			return true;
            case KeyEvent.KEYCODE_SLASH:
                seekmethod = 1 - seekmethod;
                return true;
                /*
                 * case KeyEvent.KEYCODE_DPAD_LEFT: if (!useDpadMusicControl()) {
                 * break; } if (!mPrevButton.hasFocus()) {
                 * mPrevButton.requestFocus(); } scanBackward(repcnt,
                 * event.getEventTime() - event.getDownTime()); return true; case
                 * KeyEvent.KEYCODE_DPAD_RIGHT: if (!useDpadMusicControl()) { break;
                 * } if (!mNextButton.hasFocus()) { mNextButton.requestFocus(); }
                 * scanForward(repcnt, event.getEventTime() - event.getDownTime());
                 * return true;
                 */

            case KeyEvent.KEYCODE_S:
                toggleShuffle(1);
                return true;

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_SPACE:
                doPauseResume();
                return true;

            case KeyEvent.KEYCODE_FORWARD:
                if (mService == null)
                { return false; }

                long now1 = SystemClock.elapsedRealtime();
                if ((now1 - mLastSeekEventTime) > 250) {
                    mLastSeekEventTime = now1;
                    mPosOverride = mDuration * (mProgress.getProgress() + 50)
                                   / PROGRESS_ACCURACY;

                    try {
                        mService.seek(mPosOverride);
                    }
                    catch (RemoteException ex) {
                    }

                    // trackball event, allow progress updates
                    if (!mFromTouch) {
                        refreshNow();
                        mPosOverride = -1;
                    }
                }

                return true;

            case KeyEvent.KEYCODE_MEDIA_REWIND:
                if (mService == null)
                { return false; }

                long now = SystemClock.elapsedRealtime();
                if ((now - mLastSeekEventTime) > 250) {
                    mLastSeekEventTime = now;
                    mPosOverride = mDuration * (mProgress.getProgress() - 50)
                                   / PROGRESS_ACCURACY;

                    try {
                        mService.seek(mPosOverride);
                    }
                    catch (RemoteException ex) {
                    }

                    // trackball event, allow progress updates
                    if (!mFromTouch) {
                        refreshNow();
                        mPosOverride = -1;
                    }
                }

                return true;

            case KeyEvent.KEYCODE_PAGE_DOWN:
                if (mService != null) {
                    if (!mSeeking && mStartSeekPos >= 0) {
                        setTime();
                        mPauseButton.requestFocus();

                        try {
                            mService.next();
                        }
                        catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    else {
                        scanForward(-1, event.getEventTime() - event.getDownTime());
                        mPauseButton.requestFocus();
                        mStartSeekPos = -1;
                    }
                }

                mSeeking = false;
                mPosOverride = -1;
                return true;

            case KeyEvent.KEYCODE_PAGE_UP:
                if (mService != null) {
                    if (!mSeeking && mStartSeekPos >= 0) {
                        mPauseButton.requestFocus();

                        try {
                            setTime();

                            if (mStartSeekPos < PROGRESS_ACCURACY) {
                                mService.prev();
                            }
                            else {
                                mService.seek(0);
                            }
                        }
                        catch (RemoteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    else {
                        scanBackward(-1, event.getEventTime() - event.getDownTime());
                        mPauseButton.requestFocus();
                        mStartSeekPos = -1;
                    }
                }

                mSeeking = false;
                mPosOverride = -1;
                return true;

            case KeyEvent.KEYCODE_MEDIA_STOP:
                if (mService != null) {
                    try {
                        mService.stop();
                    }
                    catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    finish();
                }

                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void scanBackward(int repcnt, long delta) {
        if (mService == null)
        { return; }

        try {
            if (repcnt == 0) {
                mStartSeekPos = mService.position();
                mLastSeekEventTime = 0;
                mSeeking = false;
            }
            else {
                mSeeking = true;

                if (delta < 5000) {
                    // seek at 10x speed for the first 5 seconds
                    delta = delta * 10;
                }
                else {
                    // seek at 40x after that
                    delta = 50000 + (delta - 5000) * 40;
                }

                long newpos = mStartSeekPos - delta;

                if (newpos < 0) {
                    // move to previous track
                    mService.prev();
                    long duration = mService.duration();
                    mStartSeekPos += duration;
                    newpos += duration;
                }

                if (((delta - mLastSeekEventTime) > 250) || repcnt < 0) {
                    mService.seek(newpos);
                    mLastSeekEventTime = delta;
                }

                if (repcnt >= 0) {
                    mPosOverride = newpos;
                }
                else {
                    mPosOverride = -1;
                }

                refreshNow();
            }
        }
        catch (RemoteException ex) {
        }
    }

    private void scanForward(int repcnt, long delta) {
        if (mService == null)
        { return; }

        try {
            if (repcnt == 0) {
                mStartSeekPos = mService.position();
                mLastSeekEventTime = 0;
                mSeeking = false;
            }
            else {
                mSeeking = true;

                if (delta < 5000) {
                    // seek at 10x speed for the first 5 seconds
                    delta = delta * 10;
                }
                else {
                    // seek at 40x after that
                    delta = 50000 + (delta - 5000) * 40;
                }

                long newpos = mStartSeekPos + delta;
                long duration = mService.duration();

                if (newpos >= duration) {
                    // move to next track
                    mService.next();
                    mStartSeekPos -= duration; // is OK to go negative
                    newpos -= duration;
                }

                if (((delta - mLastSeekEventTime) > 250) || repcnt < 0) {
                    mService.seek(newpos);
                    mLastSeekEventTime = delta;
                }

                if (repcnt >= 0) {
                    mPosOverride = newpos;
                }
                else {
                    mPosOverride = -1;
                }

                refreshNow();
            }
        }
        catch (RemoteException ex) {
        }
    }

    private void doPauseResume() {
    	if(isCease){
    		  if (mService != null) {
                  try {
					mService.openFile(filename);
					mService.play();
					isCease = false;  
					setPauseButtonImage();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
    		  }
    	}else{
	        try {
	            if (mService != null) {
	                if (mService.isPlaying()) {
	                    mService.pause();                   
	                }
	                else {
	                    mService.play();
	                }
	
	                refreshNow();
	                setPauseButtonImage();
	            }
	        }
	        catch (RemoteException ex) {
	        }
    	}

    }
    private void doForCease() { 
    	if (mService != null) {
        try {
            mService.stop();
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }

        finish();
    }}
///////
    private void toggleShuffle(int i) {
        if (mService == null) {
            return;
        }
        Log.e("MediaPlaybackActivity", "toggleShuffle");
        try {
//            int mode = MediaPlaybackService.getPlayMode();
        	int mode = sharedPreferencesOpration(Constants.SHARED, "currPlayMode",0, 0, false); 	
            mode = (mode + i) % 5;
            Log.i(TAG,"toggleShuffle() playMode="+mode);
            MediaPlaybackService.setPlayMode(mode);
            mService.setShuffleMode(MediaPlaybackService.SHUFFLE_NONE);
            switch(mode)
            {
            case MediaPlaybackService.SEQUENCE_PLAYER:
                mService.setRepeatMode(MediaPlaybackService.REPEAT_NONE);
                mShuffleButton.setBackgroundResource(R.drawable.sequence_selector);
                showToast(R.string.repeat_off_notif);
                break;
            case MediaPlaybackService.CIRCULATE_PLAYER:
                mService.setRepeatMode(MediaPlaybackService.REPEAT_ALL);
                mShuffleButton.setBackgroundResource(R.drawable.circulate_selector);
                showToast(R.string.repeat_all_notif);
                break;
            case MediaPlaybackService.SINGLE_PLAYER:
                mShuffleButton.setBackgroundResource(R.drawable.single_selector);
                showToast(R.string.shuffle_off_notif);
                 break;
            case MediaPlaybackService.SINGLE_CIRCULATE:
                 mService.setRepeatMode(MediaPlaybackService.REPEAT_CURRENT);
                 mShuffleButton.setBackgroundResource(R.drawable.single_circulate_selector);
                    showToast(R.string.repeat_current_notif);
                    break;
            case MediaPlaybackService.RANDOM_PLAYER:
                 mService.setRepeatMode(MediaPlaybackService.REPEAT_ALL);
                 mService.setShuffleMode(MediaPlaybackService.SHUFFLE_NORMAL);
                 mShuffleButton.setBackgroundResource(R.drawable.random_selector);
                 showToast(R.string.shuffle_on_notif);
                 break;
            }
            sharedPreferencesOpration(Constants.SHARED, "currPlayMode",mode, 0, true);   
        }
        catch (RemoteException ex) {
        }
    }

    private void showToast(int resid) {
        if (mToast == null) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }

        mToast.setText(resid);
        mToast.show();
    }

    private void startPlayback() {
        if (mService == null)
        { return; }

        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null && uri.toString().length() > 0) {
            String scheme = uri.getScheme();
            if ("file".equals(scheme)) {
                filename = uri.getPath();
            }
            else {
                filename = uri.toString();
            }
            try {
                mService.stop();
                mService.openFile(filename);
                mService.play();
                setIntent(new Intent());
                SetSingal();
            }
            catch (RemoteException ex) {
                Log.d("MediaPlaybackActivity", "couldn't start playback: " + ex);
            }
        }

        updateTrackInfo();
        long next = refreshNow();
        queueNextRefresh(next);
    }
    private void SetSingal(){
        mediaFileList.setSignal();
     }
    private ServiceConnection osc = new ServiceConnection() {
        public void onServiceConnected(ComponentName classname, IBinder obj) {
            mService = IMediaPlaybackService.Stub.asInterface(obj);
            MediaPlaybackService.isStopedPlayer = false;
            startPlayback();

            // new Thread(new UIUpdateThread()).start();
            try {
                // Assume something is playing when the service says it is,
                // but also if the audio ID is valid but the service is paused.
                if (mService.getAudioId() >= 0 || mService.isPlaying()
                || mService.getPath() != null) {
                    // something is playing now, we're done
                    //mRepeatButton.setVisibility(View.GONE);
                    mShuffleButton.setVisibility(View.VISIBLE);
                    mQueueButton.setVisibility(View.VISIBLE);
                    toggleShuffle(0);
                    Log.i(TAG,"ServiceConnection onServiceConnected()");
                    setPauseButtonImage();
                    return;
                }
            }
            catch (RemoteException ex) {
            }

            // Service is dead or not playing anything. If we got here as part
            // of a "play this file" Intent, exit. Otherwise go to the Music
            // app start screen.
            if (getIntent().getData() == null) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(MediaPlaybackActivity.this,
                                MusicBrowserActivity.class);
                startActivity(intent);
            }

            finish();
        }
        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };
    
/*    private  IMediaPlaybackService msvcToMenu(){
    		
    	return mService;
    }*/
    

    private void setShuffleButtonImage() {
        if (mService == null)
        { return; }

        try {
            switch (mService.getShuffleMode()) {
                case MediaPlaybackService.SHUFFLE_NONE:
                    //jly
                    mShuffleButton
                    .setBackgroundResource(R.drawable.hisil_ic_mp_shuffle_off_btn);
                    break;

                case MediaPlaybackService.SHUFFLE_AUTO:
                    mShuffleButton
                    .setBackgroundResource(R.drawable.ic_mp_partyshuffle_on_btn);
                    break;
                default:
                    mShuffleButton
                    .setBackgroundResource(R.drawable.hisil_ic_mp_shuffle_on_btn);
                    break;
            }
        }
        catch (RemoteException ex) {
        }
    }

    private void setPauseButtonImage() {
        try {
            if ( mService != null && mService.isPlaying() ) {
            	Log.i(TAG," isCease="+isCease);
            	if(isCease){
            		mPauseButton.setBackgroundResource(R.drawable.play_selector);
            	}else{
                	mPauseButton.setBackgroundResource(R.drawable.pause_selector);
            	}
                //mPauseButton
                //.setImageResource(R.drawable.hisil_ic_media_ff);
            }
            else {
            	mPauseButton.setBackgroundResource(R.drawable.play_selector);
                //mPauseButton.setImageResource(R.drawable.hisil_ic_appwidget_music_play);
            }
        }
        catch (RemoteException ex) {
        }
    }

    private ImageView mAlbum;
    private TextView mCurrentTime;
    private TextView mTotalTime;
    private RotateTextView mArtistName;
    private RotateTextView mAlbumName;
    private RotateTextView mTrackName;
    private ListView mCurrList;
    private ImageView mItemImage;
    private TextView mItemTitle;
    //private TextView mItemPath;
    private SeekBar mProgress;
    private long mPosOverride = -1;
    private boolean mFromTouch = false;
    private long mDuration;
    private int seekmethod;
    private boolean paused;

    private static final int REFRESH = 1;
    private static final int QUIT = 2;
    private static final int GET_ALBUM_ART = 3;
    private static final int ALBUM_ART_DECODED = 4;
    private static final int list = 0;
    private static final int CanNotFindFile = 5;

    private void queueNextRefresh(long delay) {
        if (!paused) {
            Message msg = mHandler.obtainMessage(REFRESH);
            mHandler.removeMessages(REFRESH);
            mHandler.sendMessageDelayed(msg, delay);
        }
    }

    private long refreshNow() {
        if (mService == null)
        { return 500; }

        try {
            long pos = mPosOverride < 0 ? mService.position() : mPosOverride;
            long remaining = PROGRESS_ACCURACY - (pos % PROGRESS_ACCURACY);

            if ((pos >= 0) && (mDuration > 0)) {
                mCurrentTime.setText(MusicUtils
                                     .makeTimeString(this, pos / PROGRESS_ACCURACY));

                if (mService.isPlaying()) {
                    mCurrentTime.setVisibility(View.VISIBLE);
                }
                else {
                    // blink the counter
                    int vis = mCurrentTime.getVisibility();
                    mCurrentTime
                    .setVisibility(vis == View.INVISIBLE ? View.VISIBLE
                                   : View.INVISIBLE);
                    remaining = 500;
                }

                mProgress.setProgress((int)(PROGRESS_ACCURACY * pos / mDuration));
            }
            else {
                mCurrentTime.setText("--:--");
                mProgress.setProgress(0);
            }

            // return the number of milliseconds until the next full second, so
            // the counter can be updated at just the right time
            return 1000;//remaining;
        }
        catch (RemoteException ex) {
        }

        return 500;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ALBUM_ART_DECODED:
                	/*
                    Bitmap mBitmap = null;
                    Bitmap tempBp = null;
                    Bitmap drawBitmap = null;

                    if (null == (Bitmap) msg.obj) {
                        mBitmap = BitmapFactory.decodeResource(
                                      MediaPlaybackActivity.this.getResources(),
                                      R.drawable.a0);
                    }
                    else {
                        mBitmap = BitmapFactory.decodeResource(
                                      MediaPlaybackActivity.this.getResources(),
                                      R.drawable.music_center_bg);
                    }

                    WeakReference<Bitmap> weakBp = new WeakReference<Bitmap>(
                        mBitmap);
                    tempBp = createReflectedImages(weakBp.get());
                    WeakReference<Bitmap> weaktempBp = new WeakReference<Bitmap>(
                        tempBp);
                    drawBitmap = transformImageBitmap(weaktempBp.get(), 2);
                    WeakReference<Bitmap> weakdrBp = new WeakReference<Bitmap>(
                        drawBitmap);
                    mAlbum.setImageBitmap(weakdrBp.get());
                    mAlbum.invalidate();
                    weakBp.clear();
                    weaktempBp.clear();
                    weakdrBp.clear();*/
                    break;
                case REFRESH:
                    long next = refreshNow();
                    queueNextRefresh(next);
                    break;

                case QUIT:
                    // This can be moved back to onCreate once the bug that prevents
                    // Dialogs from being started from onCreate/onResume is fixed.
                    new AlertDialog.Builder(MediaPlaybackActivity.this)
                    .setTitle(R.string.service_start_error_title)
                    .setMessage(R.string.service_start_error_msg)
                    .setPositiveButton(R.string.service_start_error_button,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                        int whichButton) {
                            finish();
                        }
                    }).setCancelable(false).show();
                    break;
                case CanNotFindFile:
                    finish();
                default:
                    break;
            }
        }
    };

    private BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(MediaPlaybackService.META_CHANGED)) {
                // redraw the artist/title info and
                // set new max for progress bar
                 try {
                     if(mService != null)
                     {
                     String mFileToPlay = mService.getPath();
                     if(mFileToPlay == null)
                     {
                         showToast(R.string.can_not_find_file);
                         mHandler.sendEmptyMessageDelayed(CanNotFindFile,100);
                     }
                     else{
                       File file = new File(mFileToPlay);
                        if( !file.exists())
                        {
                            showToast(R.string.can_not_find_file);
                            mHandler.sendEmptyMessageDelayed(CanNotFindFile,100);
                        }
                       }
                     }
                 }
                 catch (RemoteException ex) {
                     Log.i(TAG, "updateTrackInfo path == null:" + ex);
                 }
                updateTrackInfo();
                setPauseButtonImage();
                queueNextRefresh(1);
            }
            else if (action.equals(MediaPlaybackService.PLAYSTATE_CHANGED)) {
                setPauseButtonImage();
            }
        }
    };

    private static class AlbumSongIdWrapper {
        public long albumid;
        public long songid;

        AlbumSongIdWrapper(long aid, long sid) {
            albumid = aid;
            songid = sid;
        }
    }

    private void updateTrackInfo() {
        if (mService == null) {
            return;
        }

        try {
            String path = mService.getPath();

            if (path == null) {
                try {
                    if (mService.isPlaying()) {
                        doPauseResume();
                    }
                }
                catch (RemoteException ex) {
                    Log.i(TAG, "updateTrackInfo path == null:" + ex);
                }

                return;
            }

            long songid = mService.getAudioId();

            if (songid < 0 && path.toLowerCase().startsWith("http://")) {
                // Once we can get album art and meta data from MediaPlayer, we
                // can show that info again when streaming.
                ((View) mArtistName).setVisibility(View.INVISIBLE);
                ((View) mAlbumName).setVisibility(View.INVISIBLE);
                mAlbum.setVisibility(View.GONE);
                mTrackName.setText(path);
                mAlbumArtHandler.removeMessages(GET_ALBUM_ART);
                mAlbumArtHandler.obtainMessage(GET_ALBUM_ART,
                                               new AlbumSongIdWrapper(-1, -1)).sendToTarget();
            }
            else {
                mService.getMusicInfo();
                ((View) mArtistName).setVisibility(View.INVISIBLE);
                ((View) mAlbumName).setVisibility(View.INVISIBLE);
                String artistName = mService.getArtistName();
                
                Log.i(TAG, "srtist"+artistName+"1111111111111111");
                
                if (MediaStore.UNKNOWN_STRING.equals(artistName)
                    || null == artistName || "".equals(artistName)) {
                    artistName = getString(R.string.unknown_artist_name);
                }

                Log.i(TAG, "============artistName==========="+artistName);
                //mArtistName.setText(artistName);
                String albumName = mService.getAlbumName();
                long albumid = mService.getAlbumId();

                if (MediaStore.UNKNOWN_STRING.equals(albumName)
                    || null == albumName || "".equals(albumName)) {
                    albumName = getString(R.string.unknown_album_name);
                    albumid = -1;
                }

                mAlbumName.setText(albumName);
                String titleName = path.substring(path.lastIndexOf('/') + 1);
                mTrackName.setText(titleName);
                mAlbumArtHandler.removeMessages(GET_ALBUM_ART);
                mAlbumArtHandler.obtainMessage(GET_ALBUM_ART,
                                               new AlbumSongIdWrapper(albumid, songid)).sendToTarget();
                mAlbum.setVisibility(View.VISIBLE);
            }
 
            if(!lyricThreaTmp){
            	new Thread(new startLyricThread()).start();
            	}
            mDuration = mService.duration();
            mTotalTime.setText(MusicUtils
                               .makeTimeString(this, mDuration / PROGRESS_ACCURACY));
        }
        catch (RemoteException ex) {
            finish();
        }
    }

    class startLyricThread implements Runnable {
        @Override
        public void run() {
 
            // TODO Auto-generated method stub
            Audio pli = null;
            File f = null;

            try {
                pli = new Audio(mTrackName.getText().toString(),
                                mService.getPath(), 0L, true);
                pli.setTitle(mTrackName.getText().toString());
                pli.setAlbum(mAlbumName.getText().toString());
                pli.setArtist(mArtistName.getText().toString());
                pli.setTrack(mService.getTrackName());
                pli.setPath(mService.getPath());
                String type = mService.getPath().substring(
                                  mService.getPath().lastIndexOf("."));
                f = new File(mService.getPath().replace(type, ".lrc"));
            }
            catch (RemoteException ex) {
            }

            WeakReference<Audio> weakRf_audio = new WeakReference<Audio>(pli);
            WeakReference<File> weakRf_file = new WeakReference<File>(f);
            Lyric lyric;

            if (f.exists()) {
                lyric = new Lyric(weakRf_file.get(), weakRf_audio.get());
            }
            else {
                lyric = new Lyric(weakRf_audio.get());
            }

            WeakReference<Lyric> weakRf_lyric = new WeakReference<Lyric>(lyric);
            lyricView.setmLyric(weakRf_lyric.get());

            int sentenceSize = weakRf_lyric.get().list.size();
            if (sentenceSize > 0) {
                Sentence last = weakRf_lyric.get().list.get(weakRf_lyric.get().list.size() - 1);
                last.setToTime(mDuration);
            }
            lyricView.setSentencelist(weakRf_lyric.get().list);
            //jly 20140314
            //            lyricView.setNotCurrentPaintColor(Color.GREEN);
            //            lyricView.setCurrentPaintColor(Color.YELLOW);
            lyricView.setNotCurrentPaintColor(Color.BLACK);
            lyricView.setCurrentPaintColor(Color.BLACK);
            //            lyricView.setCurrentTextSize(24);
            //            lyricView.setLrcTextSize(24);
            lyricView.setCurrentTextSize(40);
            lyricView.setLrcTextSize(22);
            lyricView.setTexttypeface(Typeface.SERIF);
            lyricView.setBrackgroundcolor(Color.TRANSPARENT);
            lyricView.setTextHeight(50);
            if (lyricThread == null ) {
          
                lyricThread = new Thread(new UIUpdateThread());
                lyricThread.start();
            }
            pli = null;
            f = null;
            lyric = null;
            System.gc();
        }
    }

    class UIUpdateThread implements Runnable {
    	
        long time = 100;

        public void run() {
        	lyricThreaTmp=true;
            boolean isplaying = false;

            try {
                isplaying = mService.isPlaying();
            }
            catch (RemoteException ex) {
            }

            while (isplaying) {
                try {
                    if (null == mService)
                    { return; }
                    
                    lyricView.updateIndex(mService.position());
                    mLyricHandler.post(mUpdateResults);
                    Thread.sleep(time);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                catch (RemoteException ex) {
                    Log.i(TAG, "RemoteException" + ex);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    Handler mLyricHandler = new Handler();
    Runnable mUpdateResults = new Runnable() {
        public void run() {
            lyricView.invalidate();
        }
    };

    public class AlbumArtHandler extends Handler {
        private long mAlbumId = -1;

        public AlbumArtHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            long albumid = ((AlbumSongIdWrapper) msg.obj).albumid;
            long songid = ((AlbumSongIdWrapper) msg.obj).songid;

            if (msg.what == GET_ALBUM_ART
                && (mAlbumId != albumid || albumid < 0)) {
                // while decoding the new image, show the default album art
                Message numsg = mHandler.obtainMessage(ALBUM_ART_DECODED, null);
                mHandler.removeMessages(ALBUM_ART_DECODED);
                mHandler.sendMessageDelayed(numsg, 300);
                // Don't allow default artwork here, because we want to fall
                // back to song-specific
                // album art if we can't find anything for the album.
                Bitmap bm = MusicUtils.getArtwork(MediaPlaybackActivity.this,
                                                  songid, albumid, false);

                if (bm == null) {
                    bm = MusicUtils.getArtwork(MediaPlaybackActivity.this,
                                               songid, -1);
                    albumid = -1;
                }

                if (bm != null) {
                    numsg = mHandler.obtainMessage(ALBUM_ART_DECODED, bm);
                    mHandler.removeMessages(ALBUM_ART_DECODED);
                    mHandler.sendMessage(numsg);
                }

                mAlbumId = albumid;
            }
        }
    }

    private static class Worker implements Runnable {
        private final Object mLock = new Object();
        private Looper mLooper;

        /**
         * Creates a worker thread with the given name. The thread then runs a
         * {@link android.os.Looper}.
         * @param name
         *            A name for the new thread
         */
        Worker(String name) {
            Thread t = new Thread(null, this, name);
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();

            synchronized (mLock) {
                while (mLooper == null) {
                    try {
                        mLock.wait();
                    }
                    catch (InterruptedException ex) {
                    }
                }
            }
        }

        public Looper getLooper() {
            return mLooper;
        }

        public void run() {
            synchronized (mLock) {
                Looper.prepare();
                mLooper = Looper.myLooper();
                mLock.notifyAll();
            }

            Looper.loop();
        }

        public void quit() {
            mLooper.quit();
        }
    }

    public void setTime() {
        if (elapseTime == null) {
            elapseTime = SystemClock.elapsedRealtime();
            isFirst = true;
        }
        else {
            isFirst = false;
            nowTime = SystemClock.elapsedRealtime();
        }

        if (nowTime != null) {
            Log.d(TAG, "mNextListener= " + (nowTime - elapseTime));
            isNext = nowTime - elapseTime;
            elapseTime = nowTime;
        }
    }

    public Bitmap createReflectedImages(Bitmap bitmap) {
        final int reflectionGap = 0;
        Bitmap originalImage = bitmap;
        WeakReference<Bitmap> weakImage = new WeakReference<Bitmap>(
            originalImage);
        int width_bitmap = weakImage.get().getWidth();
        int heiget_bitmap = weakImage.get().getHeight();
        int width_view = mAlbum.getWidth();
        int height_view = mAlbum.getHeight();
        int width = 0;
        int height = 0;
        double scaled_index = 1.0;

        if (width_bitmap >= width_view && heiget_bitmap >= height_view) {
            BigDecimal b1 = new BigDecimal(Integer.toString(width_bitmap));
            BigDecimal b2 = new BigDecimal(Integer.toString(width_view));
            width = width_view;
            scaled_index = b1.divide(b2, 5, BigDecimal.ROUND_HALF_UP)
                           .doubleValue();
            BigDecimal b3 = new BigDecimal(Integer.toString(heiget_bitmap));
            BigDecimal b4 = new BigDecimal(Double.toString(scaled_index));
            double height_t = b3.divide(b4, 5, BigDecimal.ROUND_HALF_UP)
                              .doubleValue();
            height = (int) height_t;
        }
        else if (width_bitmap < width_view && heiget_bitmap < height_view) {
            width = width_bitmap;
            height = heiget_bitmap;
        }
        else if (width_bitmap >= width_view && heiget_bitmap < height_view) {
            BigDecimal b1 = new BigDecimal(Integer.toString(width_bitmap));
            BigDecimal b2 = new BigDecimal(Integer.toString(width_view));
            width = width_view;
            scaled_index = b1.divide(b2, 5, BigDecimal.ROUND_HALF_UP)
                           .doubleValue();
            BigDecimal b3 = new BigDecimal(Integer.toString(heiget_bitmap));
            BigDecimal b4 = new BigDecimal(Double.toString(scaled_index));
            double height_t = b3.divide(b4, 5, BigDecimal.ROUND_HALF_UP)
                              .doubleValue();
            height = (int) height_t;
        }
        else if (width_bitmap < width_view && heiget_bitmap >= height_view) {
            BigDecimal b1 = new BigDecimal(Integer.toString(heiget_bitmap));
            BigDecimal b2 = new BigDecimal(Integer.toString(height_view));
            height = height_view;
            scaled_index = b1.divide(b2, 5, BigDecimal.ROUND_HALF_UP)
                           .doubleValue();
            BigDecimal b3 = new BigDecimal(Integer.toString(width_bitmap));
            BigDecimal b4 = new BigDecimal(Double.toString(scaled_index));
            double width_t = b3.divide(b4, 5, BigDecimal.ROUND_HALF_UP)
                             .doubleValue();
            width = (int) width_t;
        }

        Bitmap mBitmap = Bitmap.createScaledBitmap(weakImage.get(), width,
                                                   height, true);
        WeakReference<Bitmap> weakScaked = new WeakReference<Bitmap>(mBitmap);
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        Bitmap reflectionImage = Bitmap.createBitmap(weakScaked.get(), 0,
                                                     height / 4 * 3, width, height / 4, matrix, false);
        WeakReference<Bitmap> weakbp = new WeakReference<Bitmap>(
            reflectionImage);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                                                          (height + height / 4), Config.ARGB_8888);
        WeakReference<Bitmap> weakbpwf = new WeakReference<Bitmap>(
            bitmapWithReflection);
        Canvas canvas = new Canvas(weakbpwf.get());
        canvas.drawBitmap(weakScaked.get(), 0, 0, null);
        Paint deafaultPaint = new Paint();
        deafaultPaint.setAntiAlias(true);
        canvas.drawBitmap(weakbp.get(), 0, height + reflectionGap, null);
        weakbp.get().recycle();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        LinearGradient shader = new LinearGradient(0, weakScaked.get()
                                                   .getHeight(), 0, weakbpwf.get().getHeight() + reflectionGap,
                                                   0x70ffffff, 0x00ffffff, TileMode.MIRROR);
        weakScaked.get().recycle();
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(
                              android.graphics.PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, height, width, weakbpwf.get().getHeight()
                        + reflectionGap, paint);
        return weakbpwf.get();
    }

    private Bitmap transformImageBitmap(Bitmap child, int rotationAngle) {
        Transformation t = new Transformation();
        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);
        mCamera.save();
        final Matrix imageMatrix = t.getMatrix();
        final int imageHeight = child.getHeight();
        final int imageWidth = child.getWidth();
        final int rotation = Math.abs(rotationAngle);
        mCamera.translate(0.0f, 0.0f, 100.0f);

        // As the angle of the view gets less, zoom in
        if (rotation < 60) {
            float zoomAmount = (float)(-300 + (rotation * 1.5));
            mCamera.translate(0.0f, 0.0f, zoomAmount);
        }

        mCamera.rotateY(rotationAngle);
        mCamera.getMatrix(imageMatrix);
        imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
        imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
        mCamera.restore();
        Bitmap newBit = Bitmap.createBitmap(child, 0, 0, imageWidth,
                                            imageHeight, imageMatrix, true);
        WeakReference<Bitmap> weakbp = new WeakReference<Bitmap>(newBit);
        Canvas canvas = new Canvas(weakbp.get());
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawBitmap(weakbp.get(), 0, 0, paint);
        return weakbp.get();
    }
    
    private void soundModeQuickKeyHandle(){
    	Log.i(TAG,"KeyEvent.KEY_SOUNDMODE press");
    	Log.i(TAG,"isSoundDialogDismiss="+isSoundDialogDismiss);
    	
    	if(!isTrackDialogDismiss){
    		trackAlertdialog.dismiss();
    		isTrackDialogDismiss=true;
    	}
    	
    	if(!isSoundDialogDismiss){ //not dismiss
			 delay(SOUND_DIALOG_DISMISS_BYTIME); 
			 soundModeIndex++;
			 if(soundModeIndex>=FileUtil.sound_mode.length){
				 soundModeIndex=0;
			 }
			 Log.i(TAG,"soundModeIndex="+soundModeIndex);
			 UmtvManager.getInstance().getAudio().setSoundMode(FileUtil.sound_mode[soundModeIndex][0]);
			   sound_menu_btn.setText(FileUtil.sound_mode[soundModeIndex][1]);        		
    	}else{   // dismiss
      	  int sound_mode = UmtvManager.getInstance().getAudio().getSoundMode();
      	  soundModeIndex= FileUtil.getIndexFromArray(sound_mode,FileUtil.sound_mode); 
      	  Builder	 mSoundBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
				 Log.i(TAG,"sound_mode"+sound_mode);
				 Log.i(TAG,"soundModeIndex="+soundModeIndex);
 	      LayoutInflater sound_factory = LayoutInflater.from(mContext);
 	      View sound_myView = sound_factory.inflate(R.layout.selector_view_dialog,null);
 	         sound_menu_btn =(TextView) sound_myView.findViewById(R.id.menu_btn);
 	      sound_menu_btn.setText(FileUtil.sound_mode[soundModeIndex][1]);
 	     soundAlertdialog = mSoundBuilder.create();

	        Window sound_window = soundAlertdialog.getWindow();
	        WindowManager.LayoutParams sound_lp = sound_window.getAttributes();
	        sound_lp.y=350;
	        sound_window.setAttributes(sound_lp);
	        soundAlertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	        soundAlertdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);  	        
	        soundAlertdialog.show();
	        soundAlertdialog.getWindow().setContentView(sound_myView);	        
	        isSoundDialogDismiss =false;  	        
		    delay(SOUND_DIALOG_DISMISS_BYTIME);  
    	}           	 		             
    }
    
    private void TrackModeQuickKeyHandle(){
    	
    	if(!isSoundDialogDismiss){
    		soundAlertdialog.dismiss();
    		isSoundDialogDismiss=true;
    	}
    	
    	if(!isTrackDialogDismiss){ //not dismiss 
			 delay(TRACK_DIALOG_DISMISS_BYTIME); 
			 trackModeIndex++;
			 if(trackModeIndex>=4){
				trackModeIndex=0;
			 }
			 Log.i(TAG,"trackModeIndex="+trackModeIndex);
			 
			 UmtvManager.getInstance().getAudio().setTrackMode(FileUtil.track_mode[trackModeIndex][0]);
	
			 track_menu_btn.setText(FileUtil.track_mode[trackModeIndex][1]);        		
    	}else{  //dismiss
       	 int mode = UmtvManager.getInstance().getAudio().getTrackMode();
       	trackModeIndex= FileUtil.getIndexFromArray(mode,FileUtil.track_mode);
   	    Builder	 mPicBuilder = new AlertDialog.Builder(mContext,R.style.Dialog_backgroundDimEnabled_false);
			
 	      LayoutInflater factory = LayoutInflater.from(mContext);
 	      View myView = factory.inflate(R.layout.selector_view_dialog,null);
 	     track_menu_btn =(TextView) myView.findViewById(R.id.menu_btn);
 	    	
 	    track_menu_btn.setText(FileUtil.track_mode[trackModeIndex][1]);
 	   trackAlertdialog = mPicBuilder.create();
 	       
	        Window window = trackAlertdialog.getWindow();
	        WindowManager.LayoutParams lp = window.getAttributes();
	        lp.y=350;
	        window.setAttributes(lp);
	        trackAlertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	        trackAlertdialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);  	        
	        trackAlertdialog.show();	
	        trackAlertdialog.getWindow().setContentView(myView);
	        isTrackDialogDismiss=false;
		   delay(TRACK_DIALOG_DISMISS_BYTIME);         		
    	}     			         	
    }
    
    private Handler finishHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
        	switch (msg.what) {
			
			case TRACK_DIALOG_DISMISS_BYTIME:
				trackAlertdialog.dismiss();
				isTrackDialogDismiss=true;
				break;
			case SOUND_DIALOG_DISMISS_BYTIME:
				soundAlertdialog.dismiss();
				isSoundDialogDismiss=true;
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
        finishHandle.sendMessageDelayed(message, DISPEAR_TIME_3s);
    }
    
    public void doForPrev() {
		if (mService == null)
		{ return; }

		try {
		    setTime();
		    if (isNext != null && isNext >= 500 || isFirst) {
		    int mode = MediaPlaybackService.getPlayMode();
		    if((mode == MediaPlaybackService.SEQUENCE_PLAYER ||
		       mode == MediaPlaybackService.SINGLE_PLAYER     ||
		       mode == MediaPlaybackService.SINGLE_CIRCULATE) &&
		       Common.isFirstMediaFile){
		       showToast(R.string.isthefirstfile);
		    }else{
		       mService.prev();
	            filename = mService.getPath();
		    }
		    }
		}
		catch (RemoteException ex) {
		}
	}

	public void doForNext() {
		if (mService == null)
		{ return; }
		try {
		    setTime();

		    if (isNext != null && isNext >= 500 || isFirst)
		    {
		        int mode = MediaPlaybackService.getPlayMode();
		         if((mode == MediaPlaybackService.SEQUENCE_PLAYER ||
		             mode == MediaPlaybackService.SINGLE_PLAYER     ||
		             mode == MediaPlaybackService.SINGLE_CIRCULATE) &&
		             Common.isLastMediaFile){
		                 showToast(R.string.isthelastfile);
		         }else{
		            mService.next();
		            filename = mService.getPath();
		         }
		    }
		}
		catch (RemoteException ex) {
		}
	}

	public static class TmpMediaPlaybackActivity {
    	public static Activity activity;
    }
	
    public int sharedPreferencesOpration(String name, String key, int value, int defaultValue, boolean isEdit) {
        if (isEdit) {
            SharedPreferences.Editor editor = getSharedPreferences(name,  Context.MODE_PRIVATE).edit();
            editor.putInt(key, value);
            editor.commit();
            return 0;
        }
        else {
            return getSharedPreferences(name, Context.MODE_PRIVATE).getInt(key, defaultValue);
        }
    }
}