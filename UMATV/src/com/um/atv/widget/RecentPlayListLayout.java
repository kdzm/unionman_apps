package com.um.atv.widget;

import java.util.ArrayList;

import com.um.atv.R;
import com.hisilicon.android.tvapi.vo.TvProgram;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.impl.CusAtvChannelImpl;
import com.hisilicon.android.tvapi.vo.TvChannelAttr;
import com.um.atv.adapter.RecentAdapter;
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
import android.widget.PopupWindow;
import android.widget.TextView;

public class RecentPlayListLayout extends LinearLayout{
    private String TAG = "RecentPlayListLayout";
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View mRecentPlayView;
    private ListView channelListListView;
    private RecentAdapter mRecentAdapter;
    private boolean isMoveState = false;
    private int focusPosition = 0;
    private int mCurrentPlayPosition = 0;
    private int mCurrentPositionOfChannel = -1;
    private int mPositionOfChannelToMove = -1;
    private int RenamePosition = -1;
    private TextView  tipsInfo;
    private LinearLayout bottom_tip;
    private ArrayList<Integer> List = new ArrayList<Integer>();
    private CusAtvChannelImpl mChannelManager = CusAtvChannelImpl.getInstance();
    private ArrayList<TvProgram> tvChannelList = new ArrayList<TvProgram>();
    public RecentPlayListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRecentPlayView = mLayoutInflater.inflate(R.layout.tvlistview, this);
        channelListListView = (ListView) mRecentPlayView.findViewById(R.id.atv_channel_list1);
        bottom_tip = (LinearLayout)mRecentPlayView.findViewById(R.id.bottom_tip);
        tipsInfo = (TextView)mRecentPlayView.findViewById(R.id.tips_info_text);
        mRecentAdapter = new RecentAdapter(mContext,UmtvManager.getInstance().getAtvChannel().getProgList());
        channelListListView.setAdapter(mRecentAdapter);
        
        tvChannelList = new ArrayList<TvProgram>();
        tvChannelList = ATVChannelInterface.getProgList();
        int prognum = ATVChannelInterface.getCurProgNumber();
        for (int i=0; i<mRecentAdapter.getCount(); i++)
        {
        	TvProgram info = tvChannelList.get(i);
        	if (prognum == info.getiId())
        	{
        		focusPosition = i;
        		mCurrentPlayPosition = i;
        	}
        }
        channelListListView.setSelection(focusPosition);
        mRecentAdapter.notifyDataSetChanged();
    }
     public void dispatchEvent(KeyEvent event) {
         int key = event.getKeyCode();
         if (isMoveState && (key == KeyEvent.KEYCODE_DPAD_DOWN || key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_CENTER)
                    && event.getAction() == KeyEvent.ACTION_UP) {
                if (key == KeyEvent.KEYCODE_DPAD_DOWN) {
                    if (focusPosition > mRecentAdapter.getCount() - 2) {
                        return;
                    }
                    focusPosition = focusPosition + 1;
                    mPositionOfChannelToMove = focusPosition;
                    mRecentAdapter.setMoveFocusPosition(focusPosition);
                    channelListListView.setSelection(focusPosition);
                    mRecentAdapter.notifyDataSetChanged();
                }  else if (key == KeyEvent.KEYCODE_DPAD_UP) {
                    if (focusPosition < 1) {
                        return;
                    }
                    focusPosition = focusPosition - 1;
                    mPositionOfChannelToMove = focusPosition;
                    mRecentAdapter.setMoveFocusPosition(focusPosition);
                    channelListListView.setSelection(focusPosition);
                    mRecentAdapter.notifyDataSetChanged();
                }
       }
     }
     @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
         System.out.println(" keyCode = "+ keyCode + " event = "+event);
         if (keyCode == KeyEvent.KEYCODE_PROG_RED) {
             if(channelListListView.getCount() > 0)
               showDeleteDialog();
         } else if (keyCode == KeyEvent.KEYCODE_F1) {
             if(mRecentAdapter.getCount() > 0){
             focusPosition = channelListListView.getSelectedItemPosition();
             mCurrentPositionOfChannel = focusPosition;
             mRecentAdapter.setMoveFocusPosition(focusPosition);
             mRecentAdapter.notifyDataSetChanged();
             isMoveState = true;
             bottom_tip.setVisibility(View.INVISIBLE);
             tipsInfo.setVisibility(View.VISIBLE);
             tipsInfo.setText(R.string.tips_info_text);
             }
         }else  if (keyCode == KeyEvent.KEYCODE_F2){
             if(mRecentAdapter.getCount() > 0){
            	 tvChannelList = new ArrayList<TvProgram>();
            	 tvChannelList = ATVChannelInterface.getProgList();
            	 TvProgram info = tvChannelList.get(channelListListView.getSelectedItemPosition());
	             //int ChannelID =  ((TvProgram)channelListListView.getSelectedItem()).getiId();
	             int ChannelID =  info.getiId();
	             if(UmtvManager.getInstance().getAtvChannel().getProgInfo(ChannelID).getStChannelAttr().isbSkip())
	             {
	                 UmtvManager.getInstance().getAtvChannel().skip(ChannelID,false);
	             } else {
	                 UmtvManager.getInstance().getAtvChannel().skip(ChannelID,true);
	             }
	             mRecentAdapter.setChannelList(UmtvManager.getInstance().getAtvChannel().getProgList());
	             mRecentAdapter.notifyDataSetChanged();
             }
         }else  if (keyCode == KeyEvent.KEYCODE_PROG_BLUE){
             if(isMoveState){
                bottom_tip.setVisibility(View.VISIBLE);
                tipsInfo.setVisibility(View.INVISIBLE);
                mRecentAdapter.setMoveFocusPosition(-1);
                mRecentAdapter.notifyDataSetChanged();
                mPositionOfChannelToMove = -1;
                isMoveState = false;
             }
             RenamePosition = channelListListView.getSelectedItemPosition();
             mRecentAdapter.setRenamePosition(RenamePosition);
             mRecentAdapter.notifyDataSetChanged();
         }else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
             if (isMoveState){
                 showMoveDialog();
                 isMoveState = false;
                 return true;
             }
         }else if ((event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)
                 && !isMoveState && RenamePosition != -1){
        	 	tvChannelList = new ArrayList<TvProgram>();
    	 		tvChannelList = ATVChannelInterface.getProgList();
	 			TvProgram info = tvChannelList.get(channelListListView.getSelectedItemPosition());
	 			//int ChannelID =  ((TvProgram)channelListListView.getSelectedItem()).getiId();
	 			int ChannelID = info.getiId();
				UmtvManager.getInstance().getAtvChannel().rename(ChannelID ,mRecentAdapter.getNewName());
				Log.d(TAG,"rename channel ChannelID = "+ ChannelID + " getNewName = "+ mRecentAdapter.getNewName());
				mRecentAdapter.setChannelList(UmtvManager.getInstance().getAtvChannel().getProgList());
				mRecentAdapter.setRenamePosition(-1);
				mRecentAdapter.notifyDataSetChanged();
				RenamePosition = -1;
         }
         return super.onKeyDown(keyCode, event);
     }
    private void showMoveDialog() {
         final ChannelEditDialog ced = new ChannelEditDialog(mContext,R.style.Translucent_NoTitle, false);
         ced.setChannelEditListener(new ChannelEditListener() {
                @Override
                public boolean positiveClick() {
                    ced.dismiss();
                    moviChannel();
                    mCurrentPositionOfChannel = -1;
                    mRecentAdapter.setChannelList(UmtvManager.getInstance().getAtvChannel().getProgList());
                    mRecentAdapter.setMoveFocusPosition(-1);
                    mRecentAdapter.notifyDataSetChanged();
                    bottom_tip.setVisibility(View.VISIBLE);
                    tipsInfo.setVisibility(View.INVISIBLE);
                    return true;
                }
                @Override
                public boolean negativeClick() {
                    ced.dismiss();
                    mCurrentPositionOfChannel = -1;
                    mRecentAdapter.setMoveFocusPosition(-1);
                    mRecentAdapter.notifyDataSetChanged();
                    bottom_tip.setVisibility(View.VISIBLE);
                    tipsInfo.setVisibility(View.INVISIBLE);
                    return true;
                }
            });
            ced.show();
    }
    private void showDeleteDialog()
    {
         final ChannelEditDialog ced = new ChannelEditDialog(mContext, R.style.Translucent_NoTitle, true);
         ced.setChannelEditListener(new ChannelEditListener() {
                @Override
                public boolean positiveClick() {
                    ced.dismiss();
                    if(mRecentAdapter.getCount() > 0)
                      {
                    	tvChannelList = new ArrayList<TvProgram>();
            	 		tvChannelList = ATVChannelInterface.getProgList();
        	 			TvProgram info = tvChannelList.get(channelListListView.getSelectedItemPosition());
        	 			//int ChannelID =  ((TvProgram)channelListListView.getSelectedItem()).getiId();
        	 			int ChannelID = info.getiId();
                        int result = UmtvManager.getInstance().getAtvChannel().delete(ChannelID);
                        Log.d(TAG, "delete channel  result =  "+ result+ " ChannelID = "+ ChannelID);
                        mRecentAdapter.setChannelList(UmtvManager.getInstance().getAtvChannel().getProgList());
                        mRecentAdapter.notifyDataSetChanged();
                      }
                    return true;
                }
                @Override
                public boolean negativeClick() {
                    ced.dismiss();
                    return true;
                }
            });
            ced.show();
    }
    private void moviChannel()
    {
        int CurrentID = -1;
        int nextID = -1;
        int tmpPos = -1;
        
        tvChannelList = new ArrayList<TvProgram>();
 		tvChannelList = ATVChannelInterface.getProgList();
 		TvProgram info = tvChannelList.get(mCurrentPositionOfChannel);
        CurrentID = info.getiId();
        if(mCurrentPositionOfChannel < focusPosition){
        	tmpPos = (mCurrentPositionOfChannel + 1);
            for(int i=tmpPos; i<=focusPosition; i++)
            {
            	info = tvChannelList.get(i);
                nextID = info.getiId();
                UmtvManager.getInstance().getAtvChannel().swap(CurrentID,nextID);
                CurrentID = nextID;
            }
            if (mCurrentPlayPosition == mCurrentPositionOfChannel)
            {
            	mCurrentPlayPosition = focusPosition;
            	ATVChannelInterface.setCurProgNumber(mCurrentPlayPosition+1);
            }
            else if (mCurrentPlayPosition > mCurrentPositionOfChannel
            		&& mCurrentPlayPosition <= focusPosition)
            {
            	mCurrentPlayPosition--;
            	ATVChannelInterface.setCurProgNumber(mCurrentPlayPosition+1);
            }
            
        }else if (mCurrentPositionOfChannel > focusPosition){
        	tmpPos = (mCurrentPositionOfChannel - 1);
            for(int i=tmpPos; i>= focusPosition; i--)
            {
            	info = tvChannelList.get(i);
                nextID = info.getiId();
                UmtvManager.getInstance().getAtvChannel().swap(CurrentID,nextID);
                CurrentID = nextID;
            }
            if (mCurrentPlayPosition == mCurrentPositionOfChannel)
            {
            	mCurrentPlayPosition = focusPosition;
            	ATVChannelInterface.setCurProgNumber(mCurrentPlayPosition+1);
            }
            else if (mCurrentPlayPosition >= focusPosition
            		&& mCurrentPlayPosition < mCurrentPositionOfChannel)
            {
            	mCurrentPlayPosition++;
            	ATVChannelInterface.setCurProgNumber(mCurrentPlayPosition+1);
            }
        }
    }
}
