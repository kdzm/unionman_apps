package com.um.cpelistener;

import java.net.InetAddress;
import java.net.Socket;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

/**
 * 诊断状态，包括： 1,None 2,Requested 诊断请求 3,Complete 诊断正常完成 4,9822 RTSP错误 5,9823
 * 媒体格式不支持 6,9824 解码失败 7,9825 节目链接错误 8,9826 无流，但RTSP正常 9,9827 无组播流
 * 
 * am startservice -n com.um.cpelistener/.ListenService
 * rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov
 * http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8
 * rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp
 * 
 * @author Eniso
 *
 */
public class MainActivity extends Activity {
	private static String TAG = "Eniso";
	public static final int PLAY_SUCCESS = 1000;
	public static final int PLAY_FAILED = 1001;

	public static final int MEDIA_FAST_FORWORD_COMPLETE = 20;
	public static final int MEDIA_FAST_BACKWORD_COMPLETE = 21;
	public static final int MEDIA_INFO_PREPARE_PROGRESS = 710;
	/** Audio play fail */
	public static final int MEDIA_INFO_AUDIO_FAIL = 1000;
	/** Video play fail */
	public static final int MEDIA_INFO_VIDEO_FAIL = 1001;
	/** network erro/unknown */
	public static final int MEDIA_INFO_NETWORK = 1002;
	/** time out */
	public static final int MEDIA_INFO_TIMEOUT = 1003;
	/** media not support */
	public static final int MEDIA_INFO_NOT_SUPPORT = 1004;
	/** net-player buffer is empty */
	public static final int MEDIA_INFO_BUFFER_EMPTY = 1005;
	/** net-player buffer is starting */
	public static final int MEDIA_INFO_BUFFER_START = 1006;
	/** net-player buffer is enough */
	public static final int MEDIA_INFO_BUFFER_ENOUGH = 1007;
	/** net-player buffer is full */
	public static final int MEDIA_INFO_BUFFER_FULL = 1008;
	/** net-player buffer download finish */
	public static final int MEDIA_INFO_BUFFER_DOWNLOAD_FIN = 1009;
	/** The Fist frame time */
	public static final int MEDIA_INFO_FIRST_FRAME_TIME = 1010;
	/** stream 3D mode */
	public static final int MEDIA_INFO_STREM_3D_MODE = 1011;
	/** I frame error */
	public static final int MEDIA_INFO_STREM_IFRAME_ERROR = 1012;
	/** stream norm switch */
	public static final int MEDIA_INFO_STREM_NORM_SWITCH = 1013;
	/** hiplayer Video INFO string, set only */
	public static final int KEY_PARAMETER_VIDEO_POSITION_INFO = 6009;

	private Toast mToast = null;
	private Context mContext = null;
	private Handler mHandler = null;
	private Runnable mRunnable = null;
	private SurfaceView mSurfaceView = null;
	private MediaPlayer mPlayer = null;
	private OnBufferingUpdateListener mOnBufferingUpdateListener = null;
	private OnCompletionListener mOnCompletionListener = null;
	private OnErrorListener mOnErrorListener = null;
	private OnInfoListener mOnInfoListener = null;
	private OnVideoSizeChangedListener mVideoSizeChangedListener = null;
	private OnPreparedListener mOnPreparedListener = null;
	private long mErrorTimes = 0;
	private int mResult = PLAY_FAILED;
	private String mReason = "None";
	private Boolean hasPlayed = false;
	private long startTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		String playurl = (b == null) ? null : b.getString("PlayURL");
		Log.d(TAG, "playurl = " + playurl);
		if (playurl == null)
			finish();
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (mResult == PLAY_SUCCESS)
					return;
				mResult = msg.what;
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (mResult == PLAY_SUCCESS) {
							showToast("播控诊断，播放成功");
						} else {
							showToast("播控诊断，播放失败");
						}
					}
				});
				mHandler.postDelayed(mRunnable, 500);
			}
		};
		mRunnable = new Runnable() {
			Boolean sendFlag = false;

			@Override
			public void run() {
				mHandler.removeCallbacks(this);
				if (sendFlag)
					return;
				sendFlag = true;
				sendThread();
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				finish();
			}
		};
		setContentView(R.layout.activity_main);
		initSurfaceView(R.id.video_plane, playurl);
		initPlayerListener();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		mContext = this;
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void sendThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				sendResultToCPE("play_diagnostics", mReason, mReason.length());
			}
		}).start();
	}

	private void sendResultToCPE(String cmd, String msg, int len) {
		Socket socket_client = null;
		try {
			Log.d(TAG, "send_message = " + cmd + ", " + msg + ", " + len);
			InetAddress addr = InetAddress.getByName("127.0.0.1");
			socket_client = new Socket(addr, 23416);
			// SocketMessage.send_message(socket_client, "play_diagnostics", 1,
			// "6", 1);
			SocketMessage.send_message(socket_client, cmd, 1, msg, len);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket_client.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void showToast(String text) {
		if (mToast == null)
			mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
		mToast.setDuration(Toast.LENGTH_LONG);
		mToast.setText(text);
		mToast.show();
	}

	private void initPlayerListener() {
		mOnBufferingUpdateListener = new OnBufferingUpdateListener() {
			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				Log.d(TAG, "onBufferingUpdate====>" + percent);
			}
		};
		mOnCompletionListener = new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.d(TAG, "onCompletion");
				if (!hasPlayed) {// 没有画面输出
					if (mReason.equals("None"))
						mReason = "9824";// 6,9824 解码失败
					mHandler.sendEmptyMessage(PLAY_FAILED);
				}
			}
		};
		mOnErrorListener = new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.d(TAG, "onError====>what=" + what + ", extra=" + extra);
				return false;
			}
		};
		mOnInfoListener = new OnInfoListener() {
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				Log.d(TAG, "onInfo====>what=" + what + ", extra=" + extra);
				// 设置开始时间
				if (startTime == 0) {
					hasPlayed = false;
					startTime = SystemClock.elapsedRealtime();
				}
				switch (what) {
				case MEDIA_INFO_AUDIO_FAIL:// Audio play fail
				case MEDIA_INFO_VIDEO_FAIL:// Video play fail
					mErrorTimes++;
					mReason = "9824";// 6,9824 解码失败
					break;
				case MEDIA_INFO_NETWORK:// network erro / unknown
					mErrorTimes++;// 网络问题，此时很有可能是地址出错了
					mReason = "9825";// 7,9825 节目链接错误
					break;
				case MEDIA_INFO_NOT_SUPPORT:// media not support
					mErrorTimes = 100;
					mReason = "9823";// 5,9823 媒体格式不支持
					break;
				case MediaPlayer.MEDIA_INFO_UNKNOWN:
					break;
				case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:// 渲染开始
					mErrorTimes = 0;
					mReason = "Complete";// 3,Complete 诊断正常完成
					hasPlayed = true;
					break;
				default:
					break;
				}
				if (mErrorTimes > 100)
					mErrorTimes = 100;
				if (hasPlayed) {
					mHandler.sendEmptyMessageDelayed(PLAY_SUCCESS, 3000);
				} else if ((SystemClock.elapsedRealtime() - startTime) > 30 * 1000) {// 超过30秒没有播放成功
					mReason = "9824";// 6,9824 解码失败
					mHandler.sendEmptyMessage(PLAY_FAILED);
				} else if (mErrorTimes >= 5) {
					mHandler.sendEmptyMessageDelayed(PLAY_FAILED, 3000);
				}
				return false;
			}
		};
		mVideoSizeChangedListener = new OnVideoSizeChangedListener() {
			@Override
			public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
				Log.d(TAG, "onInfo====>width=" + width + ", height=" + height);
			}
		};
		mOnPreparedListener = new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				Log.d(TAG, "onPrepared====>play");
				mErrorTimes = 0;
				mp.start();
			}
		};
	}

	/** 初始化MediaPlayer对象 */
	private void initMediaPlayer(SurfaceHolder holder) {
		if (mPlayer != null)
			return;
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer.setDisplay(holder);
		mPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
		mPlayer.setOnCompletionListener(mOnCompletionListener);
		mPlayer.setOnErrorListener(mOnErrorListener);
		mPlayer.setOnInfoListener(mOnInfoListener);
		mPlayer.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
		mPlayer.setOnPreparedListener(mOnPreparedListener);
	}

	/** 初始化SurfaceView对象 */
	private void initSurfaceView(int resId, final String playurl) {
		mSurfaceView = (SurfaceView) findViewById(resId);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.addCallback(new SurfaceHolder.Callback() {
			/** 当Surface被修改的时候调用 */
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				Log.d(TAG, "Surface Changed");
			}

			/** 当Surface被创建的时候调用 */
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				Log.d(TAG, "Surface Created");
				/** 初始化MediaPlayer对象 */
				showToast("播控诊断，数秒后自动退出");
				initMediaPlayer(holder);
				try {
					mErrorTimes = 0;
					mReason = "None";
					mResult = PLAY_FAILED;
					// mPlayer.setDataSource("http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8");
					mPlayer.setDataSource(playurl);
					mPlayer.prepareAsync();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/** 当Surface被销毁的时候调用 */
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.d(TAG, "Surface Destroyed");
				try {
					mPlayer.stop();
					mPlayer.release();
					mPlayer = null;
					mHandler.post(mRunnable);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
