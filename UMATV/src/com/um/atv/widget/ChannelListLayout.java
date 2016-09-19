package com.um.atv.widget;

import java.util.ArrayList;

import com.um.atv.R;
import com.hisilicon.android.tvapi.vo.TvProgram;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.impl.CusAtvChannelImpl;
import com.hisilicon.android.tvapi.vo.TvChannelAttr;
import com.um.atv.adapter.TvlistAdapter;
import com.um.atv.interfaces.ATVChannelInterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChannelListLayout extends LinearLayout{
    private String TAG = "ChannelListLayout";
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View mTvlistView;
    private ListView channelListListView;
    private TvlistAdapter mTvlistAdapter;
    private int focusPosition = -1;
    private boolean ischangeChannel = false;
    
    private ArrayList<Integer> List = new ArrayList<Integer>();
    private CusAtvChannelImpl mChannelManager = CusAtvChannelImpl.getInstance();
    private ArrayList<TvProgram> tvChannelCurList = new ArrayList<TvProgram>();

    private ArrayList<String> tvlList = new ArrayList<String>();
    public ChannelListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTvlistView = mLayoutInflater.inflate(R.layout.channellistview, this);
        channelListListView = (ListView) mTvlistView.findViewById(R.id.atv_channel_list1);  
        tvChannelCurList = new ArrayList<TvProgram>();
        for(int i=0;i < ATVChannelInterface.getProgList().size();i++)
        {	
        	TvProgram infotmp=ATVChannelInterface.getProgList().get(i);
        	
        	if(!UmtvManager.getInstance().getAtvChannel().getProgInfo(infotmp.getiId()).getStChannelAttr().isbSkip())
        	{
        		tvChannelCurList.add(infotmp);
        	}
        }
    
        
        mTvlistAdapter = new TvlistAdapter(mContext,tvChannelCurList);
        //mTvlistAdapter = new TvlistAdapter(mContext,UmtvManager.getInstance().getAtvChannel().getProgList());
        channelListListView.setAdapter(mTvlistAdapter);
        
 
        int prognum = ATVChannelInterface.getCurProgNumber();
        for (int i=0; i<mTvlistAdapter.getCount(); i++)
        {
        	TvProgram info = tvChannelCurList.get(i);
        	if (prognum == info.getiId())
        	{
        		focusPosition = i;
        	}
        }
        channelListListView.setSelection(focusPosition);
        mTvlistAdapter.notifyDataSetChanged();
        channelListListView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode){
					case KeyEvent.KEYCODE_DPAD_UP:
						if (channelListListView.getSelectedItemId() == 0) {
							channelListListView.setSelection(channelListListView.getCount()-1);
							return true;
						}
						break;
					case KeyEvent.KEYCODE_DPAD_DOWN:
						if (channelListListView.getSelectedItemId() == channelListListView.getCount() -1) {
							channelListListView.setSelection(0);
                            return true;
                        }
						break;
					case KeyEvent.KEYCODE_DPAD_CENTER:
						break;
					}
				}
				return false;
			}
		});

    }
    
    public void dispatchEvent(KeyEvent event) {
        int key = event.getKeyCode();
        if (  (key == KeyEvent.KEYCODE_DPAD_DOWN || key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_CENTER)
                   && event.getAction() == KeyEvent.ACTION_UP) {
        	/*
               if (key == KeyEvent.KEYCODE_DPAD_DOWN) {
                  
                   focusPosition = focusPosition + 1;
                   if (focusPosition > mTvlistAdapter.getCount() - 1) {
                	   focusPosition=0;
                   }
                   channelListListView.setSelection(focusPosition);
                   mTvlistAdapter.notifyDataSetChanged();
               }  else if (key == KeyEvent.KEYCODE_DPAD_UP) {
            	   focusPosition = focusPosition - 1;
               
                   if (focusPosition < 0) {
                	   focusPosition = mTvlistAdapter.getCount() - 1;
                   }
                  
                   channelListListView.setSelection(focusPosition);
                   mTvlistAdapter.notifyDataSetChanged();
               }else */
        	if (key == KeyEvent.KEYCODE_DPAD_CENTER) {
            	   if(ischangeChannel && mTvlistAdapter.getCount() > 0){
                   	   
                       //int ChannelID =  ((TvProgram)channelListListView.getSelectedItem()).getiId();
                   	   /* tvChannelList = new ArrayList<TvProgram>();
                   	    tvChannelList = ATVChannelInterface.getProgList();*/
                   	    
                   	 tvChannelCurList = new ArrayList<TvProgram>();
                     for(int i=0;i < ATVChannelInterface.getProgList().size();i++)
                     {	
                     	TvProgram infotmp=ATVChannelInterface.getProgList().get(i);
                     	
                     	if(!UmtvManager.getInstance().getAtvChannel().getProgInfo(infotmp.getiId()).getStChannelAttr().isbSkip())
                     	{
                     		tvChannelCurList.add(infotmp);
                     	}
                     }
           				TvProgram info = tvChannelCurList.get(channelListListView.getSelectedItemPosition());
           				int ChannelID = info.getiId();
           				mChannelManager.selectProg(ChannelID);
           				((Activity)mContext).finish();
                      }else{
                          ischangeChannel = true;
                      }
                   //mTvlistAdapter.notifyDataSetChanged();
               /*}*/
               }
      }
    }
    
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	switch (keyCode) {
		 case KeyEvent.KEY_SOURCEENTER:	
			 Log.i(TAG, "onKeyDown()== KEY_SOURCEENTER is click");

      	   if(ischangeChannel && mTvlistAdapter.getCount() > 0){
             	   
                 //int ChannelID =  ((TvProgram)channelListListView.getSelectedItem()).getiId();
             	   /* tvChannelList = new ArrayList<TvProgram>();
             	    tvChannelList = ATVChannelInterface.getProgList();*/
             	    
             	 tvChannelCurList = new ArrayList<TvProgram>();
               for(int i=0;i < ATVChannelInterface.getProgList().size();i++)
               {	
               	TvProgram infotmp=ATVChannelInterface.getProgList().get(i);
               	
               	if(!UmtvManager.getInstance().getAtvChannel().getProgInfo(infotmp.getiId()).getStChannelAttr().isbSkip())
               	{
               		tvChannelCurList.add(infotmp);
               	}
               }
     				TvProgram info = tvChannelCurList.get(channelListListView.getSelectedItemPosition());
     				int ChannelID = info.getiId();
     				mChannelManager.selectProg(ChannelID);
     				((Activity)mContext).finish();
                }else{
                    ischangeChannel = true;
                }
             //mTvlistAdapter.notifyDataSetChanged();
         /*}*/


    		 return true;
		}
       /* System.out.println(" keyCode = "+ keyCode + " event = "+event);
        if ((event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)
                && !isMoveState && RenamePosition != -1){
                int ChannelID =  ((TvProgram)channelListListView.getSelectedItem()).getiId();
      
               mRecentAdapter.setChannelList(UmtvManager.getInstance().getAtvChannel().getProgList());
               mRecentAdapter.setRenamePosition(-1);
               mRecentAdapter.notifyDataSetChanged();
               RenamePosition = -1;
        }*/
        return super.onKeyDown(keyCode, event);
    }


}
