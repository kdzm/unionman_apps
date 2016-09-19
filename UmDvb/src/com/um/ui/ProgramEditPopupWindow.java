package com.um.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.um.dvb.R;
import com.um.util.Constant;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.FrameLayout.LayoutParams;

public class ProgramEditPopupWindow extends PopupWindow {
	private static final String TAG = "ProgramEditPopupWindow";
	private Context mContext;
	private Handler mHandler;
	private List<ChannleClassification> mClassList;
	private View localView;
	private ListView mListView;
    private final CountDownTimer countDownTimer = new CountDownTimer(1000 * 60, 5000) {
        public void onTick(long millisUntilFinished) {

        }

        public void onFinish() {
            dismiss();
        }
    };
	private ArrayList<HashMap<String, String>> ListItemData;
	private SimpleAdapter ListItemAdapter;
	public ProgramEditPopupWindow(Context paramContext,Handler handler, List<ChannleClassification> classList,String channelNum) {
		mContext = paramContext;
		mHandler = handler;
		mClassList = classList;
		
		setFocusable(true);
		setWindowLayoutMode(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		 localView = ((LayoutInflater) this.mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.tvlistview, null);
		setContentView(localView);
		mListView = (ListView) localView.findViewById(R.id.channel_list);
		
		mListView.setOnKeyListener(new OnKeyListener() {
				
			@Override
			public boolean onKey(View view, int keycode, KeyEvent event) {
                restarTimer();
				if (event.getAction() == KeyEvent.ACTION_DOWN) {  
					switch (keycode) {
					case KeyEvent.KEYCODE_DPAD_UP:
						 if (mListView.getSelectedItemId() == 0){
							 mListView.setSelection(mListView.getCount()-1);
		                     return true;
						 }
						break;

					case KeyEvent.KEYCODE_DPAD_DOWN:
	                    if (mListView.getSelectedItemId() == mListView.getCount() -1) {
	                    	mListView.setSelection(0);
	                        return true;
	                    }
						break;
					 case KeyEvent.KEYCODE_BACK:
						 Message msg = new Message();
						 msg.what = Constant.ProgramEditPopupWindow_DISMISS;
						 mHandler.sendMessage(msg);
					 break;
					}
				}
				return false;
			}
		});
		
		 int mSelectedClassPos = 0;			   
		 ChannleClassification classification = mClassList.get(mSelectedClassPos);
		 ListItemData = classification.getChanList(); 
		ListItemAdapter = new SimpleAdapter(mContext, ListItemData, 
								R.layout.dvb_programedit_item, 
								new String[] {"position", "name"},
								new int[] {R.id.ListItemTitle, R.id.ListItemText});
		mListView.setAdapter(ListItemAdapter);
		mListView.setSelection(Integer.parseInt(channelNum));
        countDownTimer.start();
	}

    @Override
    public void dismiss() {
        countDownTimer.cancel();
        super.dismiss();
    }

    private void restarTimer() {
        countDownTimer.cancel();
        countDownTimer.start();
    }

}
