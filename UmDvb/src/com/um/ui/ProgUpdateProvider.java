package com.um.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import com.um.controller.ParamSave;
import com.um.dvbstack.DVB;
import com.um.dvbstack.Prog;
import com.um.dvbstack.ProgList;
import com.um.dvbstack.ProgManage;
import com.um.dvbstack.Tuner;
import com.unionman.dvbstorage.ContentSchema;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;

public class ProgUpdateProvider {

	public ProgUpdateProvider() {
		// TODO Auto-generated constructor stub
	}

    public static void ProgSyncProvider(Context context) {
		ProgManage pm = ProgManage.GetInstance();
		ProgStorage ps = new ProgStorage(context.getContentResolver());
		pm.refreshProgList();
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
}
