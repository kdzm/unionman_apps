package com.um.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.um.controller.AppBaseActivity;
import com.um.controller.FocusAnimator;
import com.um.controller.ParamSave;
import com.um.dvbstack.Tuner;
import com.um.dvbstack.DVB;
import android.os.SystemProperties;
import com.um.dvbsearch.R;
import com.unionman.dvbstorage.ContentSchema;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;


public class Searchmenu extends AppBaseActivity {
   
    protected static final String TAG = "Searchmenu";
	private ListView listView;
	private SimpleAdapter listItemAdapter;
	private ArrayList<HashMap<String, Object>> ListItemData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		setContentView(R.layout.dvb_search_mainmenu);
	}
	
	private void initListView() {
		listView = (ListView) findViewById(R.id.dvb_setting_list);
		ListItemData = new ArrayList<HashMap<String, Object>>();
		int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
		Log.i(TAG,"onCreate() tunerType="+tunerType);
		
		String[] titles = null; 
		String[] instructs = null;
		if(tunerType == 2){  //DVB-C	
			titles = getResources().getStringArray(R.array.dvb_setting);
			instructs = getResources().getStringArray(R.array.dvb_setting_instruct);
		}else if(tunerType == 3){  //DTMB	
			titles = getResources().getStringArray(R.array.dvb_setting_dtmb);
			instructs = getResources().getStringArray(R.array.dvb_setting_instruct_dtmb);		
		}
		
		for (int i = 0; i < titles.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ListItemTitle", titles[i]);
			map.put("ListItemText", instructs[i]);
			ListItemData.add(map);
		}
		
		if (hasTvProgs(tunerType == 3)) {
			/* show channel editor entry */
			HashMap<String, Object> map = new HashMap<String, Object>();			
			map.put("ListItemTitle", getResources().getString(R.string.chan_edit));
			map.put("ListItemText", getResources().getString(R.string.chan_edit_help));
			ListItemData.add(map);
		}
		
		listItemAdapter = new SimpleAdapter(Searchmenu.this, ListItemData, 
				R.layout.dvb_list_item, 
				new String[] {"ListItemTitle", "ListItemText"},
				new int[] {R.id.ListItemTitle, R.id.ListItemText});
		 listView.setAdapter(listItemAdapter);
		 listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position,
						long arg3) {
					listviewItemClick(position);
				}
			});
	}
	
	private boolean hasTvProgs(boolean isDtmb) {
		int progTypeId = isDtmb ? ContentSchema.CategoryTable.DTMB_ID 
							: ContentSchema.CategoryTable.DVBC_ID;

		ProgStorage progStorage = new ProgStorage(getContentResolver());
		ArrayList<ProgInfo> progList = progStorage.getProgOrderBy(new int[] { progTypeId,  
				ContentSchema.CategoryTable.TV_ID }, null, false);
		if (progList != null && progList.size() > 0) {
			for (ProgInfo pi : progList) {
				if (pi.valid) {
					return true;
				}
			}
		}	
		return false;
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		int position = listView.getSelectedItemPosition();
		switch (keyCode) {
	     case KeyEvent.KEYCODE_MENU:
			//finish();
			break;
	     case KeyEvent.KEYCODE_BACK:
			Intent it = new Intent(Intent.ACTION_MAIN);
			it.addCategory(Intent.CATEGORY_HOME);
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(it);
			finish();
	    	return true;
		 case KeyEvent.KEY_SOURCEENTER:	
			 listviewItemClick(position);
    		 return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initListView();
		Log.v(TAG, "onResume");
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.v(TAG, "onPause");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.v(TAG, "onDestroy");
	}
	
	
	private void listviewItemClick(int position) {
		int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
		Log.i(TAG,"tunerType="+tunerType);
		int fre = ParamSave.GetMainFreq();
		int symbl = 6875;
		int qam = 3;
		int type = 0;
		int band = 8;
		
		if (tunerType == Tuner.UM_TRANS_SYS_TYPE_TER) {
			// Skip Full-Scan item
			if (position >= 2) {
				position++;
			}
		}
		
		Intent intent = new Intent();
		Bundle bundle;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			intent.putExtras(extras);
		}
		switch (position)
		{
			case 0:
		        type = 0;
		        bundle = new Bundle();
		        bundle.putInt("type", type);
		        bundle.putInt("tunertype", tunerType);
		        bundle.putInt("band", band);
		        bundle.putInt("fre", fre);
		        bundle.putInt("sym", symbl);
		        bundle.putInt("qam", qam);
		        intent.putExtras(bundle);
		    	intent.setClassName("com.um.dvbsearch", "com.um.ui.Search");
		    	Searchmenu.this.startActivity(intent);
				//Searchmenu.this.finish();
				break;
			case 1:
				if (tunerType == 3) {
					intent.setClassName("com.um.dvbsearch",
							"com.um.ui.MenualSearchWireless");
					intent.putExtras(getIntent().getExtras());
					Searchmenu.this.startActivity(intent);
				} else if (tunerType == 2) {
					intent.setClassName("com.um.dvbsearch",
							"com.um.ui.MenualSearch");
					Searchmenu.this.startActivity(intent);
				}
				//Searchmenu.this.finish();
				break;
			case 2:
		        type = 2;
				bundle = new Bundle();
		        bundle.putInt("type", type);
		        bundle.putInt("tunertype", tunerType);
		        bundle.putInt("band", band);
		        bundle.putInt("fre", fre);
		        bundle.putInt("sym", symbl);
		        bundle.putInt("qam", qam);
		        intent.putExtras(bundle);
		    	intent.setClassName("com.um.dvbsearch", "com.um.ui.Search");
		    	Searchmenu.this.startActivity(intent);
				//Searchmenu.this.finish();
				break;
			case 3:
				 bundle = new Bundle();
                 bundle.putInt("tunertype", tunerType);
                 intent.putExtras(bundle);
                 intent.setAction("com.um.action.CHANNELS_EDITOR");
                 startActivityForResult(intent, 3);
				break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 3) {
			if (hasTvProgs(Tuner.GetInstance(DVB.getInstance()).GetType() == 3)) {
				startFullscreenPlay();
			}
		}
	}
	
	private void startFullscreenPlay() {
		Intent intent = new Intent("com.unionman.intent.ACTION_PLAY_DVB");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	
}
    
