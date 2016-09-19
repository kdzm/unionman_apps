package com.um.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.um.controller.ParamSave;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Prog;
import com.um.dvbstack.ProgList;
import com.um.dvbstack.ProgManage;
import com.um.dvbstack.Tuner;
import com.unionman.dvbstorage.ContentSchema;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class ProgramSyncService extends IntentService {
	private final String TAG = "ProgramSyncService";

	public ProgramSyncService() {
		super("ProgramSyncService");
	}

	private boolean getProgSyncFlag() {
		int turnType = Tuner.GetInstance(DVB.getInstance()).GetType();
		if (turnType == Tuner.UM_TRANS_SYS_TYPE_CAB) {
			return ParamSave.getProgDVBCSyncStatus(this) != 1;
		} else {
			return ParamSave.getProgDTMBSyncStatus(this) != 1;
		}
	}

	void setProgSyncFlag(boolean syncFlag) {
		int turnType = Tuner.GetInstance(DVB.getInstance()).GetType();
		if (turnType == Tuner.UM_TRANS_SYS_TYPE_CAB) {
			ParamSave.setDVBCProgSyncStatus(this, syncFlag ? 0 : 1);
		} else {
			ParamSave.setDTMBProgSyncStatus(this, syncFlag ? 0 : 1);
		}
	}

	private void ProgramSyncProvider() {
		Log.i(TAG, "ProgramSyncProvider start");
		ProgManage pm = ProgManage.GetInstance();
		pm.refreshProgList();
		ProgStorage ps = new ProgStorage(getContentResolver());
		int turnType = Tuner.GetInstance(DVB.getInstance()).GetType();
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
		Log.i(TAG, "ProgramSyncProvider size  " + pl.list.size());

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
		Log.i(TAG, "save tv start");
		ps.addProgList(pgList, true, catList);
		Log.i(TAG, "save tv end");

		pgList.clear();
		catList.clear();

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
		Log.i(TAG, "save radio start");
		ps.addProgList(pgList, true, catList);

		Log.i(TAG, "save radio end");
		Log.i(TAG, "ProgramSyncProvider end");
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		Log.v(TAG, "come in onHandleIntent()");
		if (!DVB.isServerAlive()) {
			Log.e(TAG, "Dvb service is not alive.");
			return ;
		}
		
		if (getProgSyncFlag()) {
			Log.v(TAG, "before ProgramSyncProvider()");
			ProgramSyncProvider();
			Log.v(TAG, "after ProgramSyncProvider()");
			setProgSyncFlag(false);
			
			Intent intent = new Intent();  
			intent.setAction("com.unionman.dvb.ACTION_DVB_SYNC_PROG_READY");
			sendBroadcast(intent);
		}
	}

}
