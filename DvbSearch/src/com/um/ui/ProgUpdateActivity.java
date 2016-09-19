package com.um.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import com.um.dvbsearch.R;

import com.um.controller.AppBaseActivity;
import com.um.controller.ParamSave;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Prog;
import com.um.dvbstack.ProgList;
import com.um.dvbstack.ProgManage;
import com.um.dvbstack.Tuner;
import android.os.AsyncTask;
import com.unionman.dvbstorage.ContentSchema;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;
import com.unionman.dvbstorage.SettingsStorage;

public class ProgUpdateActivity extends AppBaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prog_update);

		Log.i("ProgUpdateActivity", "onCreate");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("ProgUpdateActivity", "onResume");
		new ThreadProvider(ProgUpdateActivity.this).execute("Provider");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void ProgUpdateProvider() {

		ProgManage pm = ProgManage.GetInstance();
		pm.refreshProgList();
		ProgStorage ps = new ProgStorage(getContentResolver());
		int turnType = ParamSave.getProgType(this);
		int categoryID = ((turnType == Tuner.UM_TRANS_SYS_TYPE_CAB) ? ContentSchema.CategoryTable.DVBC_ID
				: ContentSchema.CategoryTable.DTMB_ID);
		String categoryName = ((turnType == Tuner.UM_TRANS_SYS_TYPE_CAB) ? ContentSchema.CategoryTable.DVBC
				: ContentSchema.CategoryTable.DTMB);
		ps.addCategory(categoryID, categoryName);
		ArrayList<ProgInfo> pgList = new ArrayList<ProgInfo>();
		ArrayList<Integer> catList = new ArrayList<Integer>();
		int progid = 0;
		ProgInfo pginfo = null;
		Prog pg = null;

		/* add TV Prog */
		ProgList pl = pm.getTVProgList();
		ps.addCategory(ContentSchema.CategoryTable.TV_ID,
				ContentSchema.CategoryTable.TV);
		catList.add(Integer.valueOf(categoryID));
		catList.add(Integer.valueOf(ContentSchema.CategoryTable.TV_ID));
		for (int i = 0; i < pl.list.size(); i++) {
			HashMap<String, String> hm = pl.list.get(i);
			progid = Integer.parseInt(hm.get(ProgManage.PROG_ID));
			pg = pm.getProgById(progid);
			if (pg.getTransType() == turnType) {
				pginfo = new ProgInfo(pg.getProgId(), pg.getTsId(),
						pg.getServiceId(), ProgInfo.SERVICE_TYPE_TV,
						pg.getName(), pg.getLogicNum(), pg.getBouquetIds(),
						pg.getBouquets());
				pgList.add(pginfo);
			}
		}
		ps.addProgList(pgList, true, catList);

		pgList.clear();
		catList.clear();

		/* add RADIO Prog */
		ProgList rdpl = pm.getRadioProgList();
		ps.addCategory(ContentSchema.CategoryTable.RADIO_ID,
				ContentSchema.CategoryTable.RADIO);
		catList.add(Integer.valueOf(categoryID));
		catList.add(Integer.valueOf(ContentSchema.CategoryTable.RADIO_ID));
		for (int i = 0; i < rdpl.list.size(); i++) {
			HashMap<String, String> hm = rdpl.list.get(i);
			progid = Integer.parseInt(hm.get(ProgManage.PROG_ID));
			pg = pm.getProgById(progid);
			if (pg.getTransType() == turnType) {
				pginfo = new ProgInfo(pg.getProgId(), pg.getTsId(),
						pg.getServiceId(), ProgInfo.SERVICE_TYPE_RADIO,
						pg.getName(), pg.getLogicNum(), pg.getBouquetIds(),
						pg.getBouquets());
				pgList.add(pginfo);
			}
		}
		ps.addProgList(pgList, true, catList);
	}

	private class ThreadProvider extends AsyncTask<String, Integer, String> {
		private Context mContext = null;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		public ThreadProvider(Context paramContext) {
			mContext = paramContext;
		}

		protected String doInBackground(String[] paramArrayOfString) {
			ProgUpdateProvider();
			//ParamSave.setProgSyncStatus(mContext, 1);
			return "OK";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			finish();
			super.onPostExecute(result);
		}

	}
}
