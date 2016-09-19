package com.um.videoplayer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.um.videoplayer.R;
import com.um.videoplayer.activity.VideoActivity.TmpVideoActivity;


import android.R.color;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
import com.um.videoplayer.utility.base.SubtitleFileFilter;
import com.um.videoplayer.view.MySeekBar;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import com.um.videoplayer.activity.MultListAdapter;

public class FileListAcvitity extends ListActivity {
	private final static String TAG = "VideoActivity FileListAcvitity";
    private MediaFileList mediaFileList;
    private ListView mPlayListView;
    public ArrayList<VideoModel> currList;
    public SimpleAdapter listItemAdapter;
    ArrayList<HashMap<String, Object>> listItem;
    public static MediaFileListService mediaFileListService = null;
    public MyServiceConnection conn = null;
    private String currPlayPath;
	private String fileName;
    private boolean stopPlay = true;
    private static final String LIST_CHANGE_ACTION ="VideoListFocusChange";
	private static final String SONG_CHANGE_ACTION = "SongChanged";
	private static final String VIDEO_LIST_FINISH_ACTION = "medialistdestroy";
    private boolean isFileChanged = false;
	
    private BroadcastReceiver bcrIntenal2 =new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		Log.i(TAG,"========getAction======="+intent.getAction()+"=============");
    		 if (LIST_CHANGE_ACTION.equals(intent.getAction()))
    	        {
    	            Bundle bundle = intent.getExtras();
    	            if (bundle != null)
    	            {
    	                String currpath = bundle.getString("currpath");
    	                Log.i(TAG,"==============="+currpath+"=============");
    	                updtaePlayList(currpath);
    	            }
    	        }
    		 }
    	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        isFileChanged = false;
        setContentView(R.layout.media_picker_activity);
        Intent intent = getIntent();
        mediaFileList = intent.getParcelableExtra("MediaFileList");
        currPlayPath = intent.getStringExtra("path");
		Log.i(TAG,"onCreate currPlayPath "+ currPlayPath); 
		Log.i(TAG,"mediaFileList "+ mediaFileList); 
        if (mediaFileList != null && mediaFileList.getId() == 1) {
            Intent service = new Intent(Constants.ACTION);
            conn = new MyServiceConnection();
            FileListAcvitity.this.bindService(service, conn,
                                              Context.BIND_AUTO_CREATE);
        }

        mPlayListView = getListView();
        //mPlayListView.setSelector(getResources().getDrawable(
                                      //R.drawable.mselector));
        listItem = new ArrayList<HashMap<String, Object>>();
        listItemAdapter = new SimpleAdapter(this, listItem,
                                            R.layout.list_items,
                                            new String[] { "ItemImage", "ItemTitle"},
                                            new int[] { R.id.ItemImage, R.id.ItemTitle});
        mPlayListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
            int position, long id) {
                // TODO Auto-generated method stubif(mService == null)
            	stopPlay = false;
                fileName = currList.get(position).getPath();
				Log.i(TAG,"filename "+fileName);
				isFileChanged = true;
                try {
					/*
					Uri path = Uri.parse(filename);
					Intent intent = new Intent();  
					intent.setAction(SONG_CHANGE_ACTION);             
					intent.putExtra("currpath", filename);
					sendStickyBroadcast(intent);
					mediaFileListService.setCurrPosition(position);
                    */
					mediaFileListService.setCurrPosition(position);
                }
                catch (Exception ex) {
                    Log.d("MediaPlaybackActivity", "couldn't start: " + ex);
                }

                finish();
            }
        });
        mPlayListView.setAdapter(listItemAdapter);
        mPlayListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                mediaFileListService.getList();

                if (null != mediaFileListService
                && null != mediaFileListService.getList()) {
                    if (mediaFileListService.getList().size() > currList.size()) {
                        updtaePlayList();
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void updtaePlayList(String path) {
    Log.i(TAG, "=========path:"+path+"sixe:"+listItem.size());
    	listItem.clear();
        for (int i = 0; i < currList.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();

            if (path != null && path.equals(currList.get(i).getPath())) {
                // jly
                map.put("ItemImage",
                        R.drawable.hisil_indicator_ic_mp_playing_large);        
               /* Message msg = new Message();
                msg.what = 1;
                msg.arg1 = i;
                mHandler.sendMessage(msg);*/
               //j=i;
            }
            else {
                // jly
                map.put("ItemImage", R.drawable.hisil_ic_tab_albums_unselected);
            }

            map.put("ItemTitle", currList.get(i).getTitle());
            map.put("ItemText", currList.get(i).getPath());
            
            listItem.add(map);
            Log.i(TAG, "------------sixe:"+listItem.size());
            if (path != null && path.equals(currList.get(i).getPath())) 
            {
            	getListView().setSelection(i);
            }
        }
        ((SimpleAdapter) getListView().getAdapter()).notifyDataSetChanged();
    }

    public void updtaePlayList() {
    	listItem.clear();
        for (int i = 0; i < mediaFileListService.getList().size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemTitle", currList.get(i).getTitle());
            map.put("ItemText", currList.get(i).getPath());
            listItem.add(map);
        }

        ((SimpleAdapter) getListView().getAdapter()).notifyDataSetChanged();
    }

    public class MyServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            mediaFileListService = ((MediaFileListService.MyBinder) service)
                                   .getService();
            currList = mediaFileListService.getList();
            updtaePlayList(currPlayPath);
        }

        public void onServiceDisconnected(ComponentName arg0) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	switch(keyCode) {
			case KeyEvent.KEYCODE_BACK:
				stopPlay = false;
				break;
			default:
				break;
		}
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	if (stopPlay == true)
    	{
    		((VideoActivity)TmpVideoActivity.activity).onPause();
    	}
    	stopPlay = true;
    	super.onPause();
    }
    
    @Override
    protected void onDestroy() {
    	Intent intent = new Intent();  
        intent.setAction(VIDEO_LIST_FINISH_ACTION);  
        sendBroadcast(intent); 
        
        if (conn != null) {
            unbindService(conn);
            stopService(new Intent(Constants.ACTION));
            unregisterReceiver(bcrIntenal2);
        }
		
		if(isFileChanged){
			Log.d(TAG, "isFileCHanged = "+isFileChanged);
			Uri path = Uri.parse(fileName);
			Intent mintent = new Intent();  
			mintent.setAction(SONG_CHANGE_ACTION);			  
			mintent.putExtra("currpath", fileName);
			sendBroadcast(mintent);
			isFileChanged = false;
		}
		
        super.onDestroy();

    }

  /*  Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int i = msg.arg1;
                    Log.i(TAG,"setSelection2:"+i);
                    getListView().setSelection(i);
                    getListView().invalidateViews();
            }
        };
    };*/
    
    protected void onResume() {
    	IntentFilter listchange = new IntentFilter(LIST_CHANGE_ACTION);
        FileListAcvitity.this.registerReceiver(bcrIntenal2, listchange);
        super.onResume();
    };
  

}
