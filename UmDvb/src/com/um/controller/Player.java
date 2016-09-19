package com.um.controller;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;

import com.um.dvbstack.Prog;
import com.um.dvbstack.ProgList;
import com.um.dvbstack.ProgManage;
import com.um.dvbstack.ProviderProgManage;
import com.um.ui.DvbPlayService;
import com.unionman.dvbplayer.DvbPlayer;

import java.util.ArrayList;
import java.util.HashMap;


public class Player
{
	public final String PROG_VIEWINDEX = "index";
	public final String PROG_ID = "progid";
	public final String PROG_NAME = "progid";
	
	private int curChannelIndex = 0;
	private static int playStatus = 0; /*0???1???2???*/
	private DvbPlayService.DvbServerBinder mService = null;
	Context mContext = null;
	private boolean isconnect;
	final String TAG = "AVPlay";
	private static boolean isStartService = false;
	
	public SurfaceHolder holder;

	public ArrayList<HashMap<String, String>> chanlist;
	private static Player mPlayer;
	

	public static Player GetInstance()
	{
		if (mPlayer == null)
		{
			mPlayer = new Player();			
		}
		
		return mPlayer;
	}
	
	private Player()
	{
		ProviderProgManage.GetInstance(mContext);
	}
	
	public void finalize()
	{
		if(isconnect && mContext != null)
		{
			try{
			mContext.unbindService(conn);
			}catch(Exception e)
			{
				
			}
		}
	}
	
	private AppBaseActivity mBaseActivity = null;
	
	
	public void setActivty(AppBaseActivity appBase, SurfaceHolder h, ConnectLister cl)
	{
		mContext = appBase;
		mBaseActivity = appBase;
		holder = h;
		Intent intent = new Intent(mContext, DvbPlayService.class);
		
		if(!isStartService)
			mContext.startService(intent); // ?startService,??unbindService??
		
		isStartService = true;
		
		mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
		connectLister = cl;
		Log.e(TAG, "DvbPlayService setActivty");
		//startService(intent);
	}
	
	public void removeActivty()
	{
		Log.e(TAG, "DvbPlayService removeActivty");

		try{
		mContext.unbindService(conn);
		}catch(Exception e)
		{
			
			e.printStackTrace();	
		}
	}

	public boolean Ishaveprog()
	{
		boolean b = false;
		ProgList proglist = getCurModeProgList();
		b = proglist.list.isEmpty();
		return !b;
	}
	public ProgList getCurModeProgList()
	{
		int mode;
		mode = ProviderProgManage.GetInstance(mContext).GetCurMode();
		return getSpecifyModeProgList(mode);

	}
	
	public int getCurMode()
	{
		int mode;
		mode = ProviderProgManage.GetInstance(mContext).GetCurMode();
		
		return mode;
	}
	
	public ProgList getSpecifyModeProgList(int mode)
	{
		ProgList mProgList = null;	
		switch (mode)
		{
		case ProgManage.ALLPROG:
			mProgList =  getAllProg();
			break;
		case ProgManage.TVPROG:
			mProgList =  getTVProgList();
			break;
		case ProgManage.RADIOPROG:
			mProgList = getRadioProgList();
			break;
		case ProgManage.FAVPROG:
			mProgList = getFavTVProgList();
			break;
			default:
				mProgList = getAllProg();
				break;
		}

		return mProgList;
	}
	
	
	public ProgList getAllProg()
	{
		return ProviderProgManage.GetInstance(mContext).getAllProgList();
	}
	
	public ProgList getTVProgList()
	{
		return ProviderProgManage.GetInstance(mContext).getTVProgList();
	}
	
	public ProgList getFavTVProgList()
	{
		return ProviderProgManage.GetInstance(mContext).getFavTVProgList();
	}

	public ProgList getRadioProgList()
	{
		return ProviderProgManage.GetInstance(mContext).getRadioProgList();
	}	

	public interface ConnectLister
	{
		void doSomething(int arg1);
	}
	
	ConnectLister connectLister = null;
		
	// ?
	private final ServiceConnection conn = new ServiceConnection()
	{

		// @Override
		public void onServiceDisconnected(ComponentName name)
		{
			mService = null;
			Log.e(TAG, " onServiceDisconnected()");
		}

		// @Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mService = (DvbPlayService.DvbServerBinder)service;
			Log.e(TAG, " onServiceConnected()");
			
			if(Player.this.connectLister != null)
			{
				connectLister.doSomething(0);
				Player.this.connectLister = null;
			}
			
            mBaseActivity.setMediaPlayer(mService.getMediaPlayer());
  		}

	};

	public int playNextProg(ProgList proglist)
	{
		int cnt = proglist.list.size();
		int index = 0;
		int curListProgIdex = proglist.curProgIndex;
		Play_Last_Prog.GetInstance().lastProg= proglist.curProgIndex;
		Play_Last_Prog.GetInstance().lastProgMode = ProviderProgManage.GetInstance(mContext).GetCurMode();
		
		if((curListProgIdex+1) >= cnt)
		{
			index = 0;
		}
		else
		{
			index = curListProgIdex+1;
		}
		
		playProg(proglist, index);	
//		ParamSave.SaveLastProgInfo(mContext, index, ProviderProgManage.GetInstance(mContext).GetCurMode());
		return index;
	}
	
	public int playPreProg(ProgList proglist)
	{
		int cnt = proglist.list.size();
		int index = 0;
		int curListProgIdex = proglist.curProgIndex;
		
		Play_Last_Prog.GetInstance().lastProg= proglist.curProgIndex;
		Play_Last_Prog.GetInstance().lastProgMode = ProviderProgManage.GetInstance(mContext).GetCurMode();
		
		if(curListProgIdex-1 <0)
		{
			index = cnt-1;
		}
		else
		{
			index = curListProgIdex-1;
		}
		

		playProg(proglist, index);	
//		ParamSave.SaveLastProgInfo(mContext, index, ProviderProgManage.GetInstance(mContext).GetCurMode());
		return index;
	}
	
	public void playforceProg(ProgList proglist, int progIndex)
	{
		if (mService == null)
		{
			return ;
		}
		
		PlayData data = new PlayData();
		data.holder = holder;
		data.url = "dvb://" + progIndex;

		mService.attachView(mBaseActivity, data.holder);
		mService.startPlay(data.url, 100);
		playStatus = 1;
	}
	
	public void playProg(ProgList proglist, int progIndex)
	{
		String progId = proglist.list.get(progIndex).get(ProgManage.PROG_ID);
		
		if (mService == null)
		{
			return ;
		}
		
		Play_Last_Prog.GetInstance().lastProg= proglist.curProgIndex;
		Play_Last_Prog.GetInstance().lastProgMode = ProviderProgManage.GetInstance(mContext).GetCurMode();

        if (!DvbPlayer.isServerAlive())
        {
            return;
        }

		/*????????��*/
		if ((playStatus == 1) && (ProviderProgManage.GetInstance(mContext).lastprogid != null))
		{
			if(progId.equals(ProviderProgManage.GetInstance(mContext).lastprogid))
			{
					//Log.i("HC","lastproid is equals:",""+progId);
					return;
			}		
		}
		if( proglist.list.isEmpty()== false)
		{
			ProviderProgManage.GetInstance(mContext).lastprogid =progId;
			PlayData data = new PlayData();
			data.holder = holder;
			data.url = "dvb://" + progId;

            proglist.curProgIndex = progIndex;
			mService.attachView(mBaseActivity, data.holder);
			mService.startPlay(data.url, 100);
	        mService.startCounter(getProg(progIndex).getProgId());

			int globalProgIndex = Integer.valueOf(
					proglist.list.get(progIndex).get(ProgManage.PROG_VIEWINDEX));
			
			ProviderProgManage.GetInstance(mContext).setPlayProgIndex(globalProgIndex);
//			ParamSave.SaveLastProgInfo(mContext, progIndex, ProviderProgManage.GetInstance(mContext).GetCurMode());
			playStatus = 1;
		}
	}
	
	public void stopPlay()
	{
		Log.e(TAG, " stopPlay()");
		if (mService == null)
		{
			return ;
		}
	    mService.stopPlay();
        mService.stopCounter();
		Log.e(TAG, " stopPlay()");
		playStatus = 2;
		
	}
	
	public int getPlayStatus()
	{
		return playStatus;
	}
	
	public void playCurProg()
	{
		ProgList proglist;
		int progIndex = 0;
		Log.e(TAG, " playCurProg()");
		if (isconnect == false)
		{
			return;
		}
		Log.e(TAG, " playCurProg()");
		proglist = getCurModeProgList();
		progIndex = proglist.curProgIndex;
		
		playProg(proglist,progIndex);
	}
	
	//????idProgList?ProgList��??
	//????�ҧ֦�
	public int getCurChannelIndex(ProgList proglist)
	{
		return ProviderProgManage.GetInstance(mContext).getCurChannelIndex(proglist);
	}
	
	public Prog getProg(int index)
	{
		return ProviderProgManage.GetInstance(mContext).getProg(index);
	}
	
	public DvbPlayService.DvbServerBinder getPlayerServive() {
		return mService;
	}
}
