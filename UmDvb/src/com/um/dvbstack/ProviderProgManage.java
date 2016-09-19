package com.um.dvbstack;


import android.content.ContentResolver;
import android.content.Context;

import com.um.controller.ParamSave;
import com.unionman.dvbstorage.ContentSchema;
import com.unionman.dvbstorage.ProgInfo;
import com.unionman.dvbstorage.ProgStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class ProviderProgManage {
    public static final int ALL_ID = 0;
    public static final int TV_ID = 1;
    public static final int RADIO_ID = 2;
    public static final int DVBC_ID = 3;
    public static final int DTMB_ID = 4;
    public static final int FAV_ID = 5;

	public final static String PROG_VIEWINDEX = "index";
	public final static String PROG_ID = "progid";
	public final static String PROG_NAME = "name";
		
	private final static String TAG = "PROGMANAGE";

	static ProviderProgManage mPM;
	private static Prog[] mProgList = null;//= new Prog[0] ;
	
//	private static ArrayList<Prog> plist = new ArrayList<Prog>();
	
	private List<ProgList> allList = new ArrayList<ProgList>(); 
	private List<ProgList> batList = new ArrayList<ProgList>();
	private List<String>  batListName = new ArrayList<String>();
	private HashMap<String,ProgList> batCategory = new HashMap<String,ProgList>();
	
	public final static int ALLPROG = 0;
	public final static int TVPROG  = 1;
	public final static int RADIOPROG = 2;
	public final static int FAVPROG = 3;
	public final static int NVODPROG = 4;
	
	public final static int BATPROG = 5;
	
	public final static int MAX_LIST_CNT = 6;
	
	private static int  curMode=TVPROG;
    public  static String  lastprogid=null;
	private static ProviderProgManage me;
    private ContentResolver contentResolver = null;
    private ProgStorage progStorage;
    static private Context mContext;
	private ProviderProgManage() {
	}

    public static ProviderProgManage GetInstance(Context context) {
    	mContext = context;
        if(null == me) {   	
            me = new ProviderProgManage();
            me.contentResolver = context.getContentResolver();
            me.progStorage = new ProgStorage(me.contentResolver);

            int maxList = MAX_LIST_CNT;
            for(int i=0; i<maxList; i++)
            {
                me.allList.add(null);
            }

            mProgList = me.getDvbProgList();
            me.initAllProg();
            me.initTvProg();
            me.initRadioProg();
            me.initDvbBatProgList();
            me.initFavTVProg();
        }

        return me;
    }

	private void initAllProg()
	{

		ArrayList<HashMap<String, String>> allchan = new ArrayList<HashMap<String, String>>();
		if(mProgList!=null)
		{		
			for (int i = 0; i < mProgList.length; i++)
			{
				if((mProgList[i].isHide==false)&&(mProgList[i].isValid==true))
				{
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(PROG_VIEWINDEX, "" + (i));
					map.put(PROG_ID, String.valueOf(mProgList[i].getProgId()));
					map.put(PROG_NAME, "" + mProgList[i].getName());
					allchan.add(map);
				}
			}
		}
		ProgList list = new ProgList();
		
		list.list = allchan;
		list.curProgIndex = 0;
		
		allList.set(ALLPROG, list);
	}
	
	private void initTvProg()
	{

		ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
		if(mProgList!=null)
		{
			for (int i = 0; i < mProgList.length; i++)
			{
				if((mProgList[i].service_type == Prog.DB_SERVICE_TYPE_DTV)
						&&(mProgList[i].isHide==false)&&(mProgList[i].isValid==true))
				{
                    String[] bouquets = mProgList[i].getBouquets();
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(PROG_VIEWINDEX, "" + (i));
                    map.put(PROG_ID, String.valueOf(mProgList[i].getProgId()));
                    map.put(PROG_NAME, "" + mProgList[i].getName());
                    maplist.add(map);
				}
			}
		}
		ProgList list = new ProgList();
		
		list.list = maplist;
		list.curProgIndex = ParamSave.GetLastProgIndex(mContext, TVPROG);
		
		allList.set(TVPROG, list);		
	}
	
	private void initFavTVProg()
	{
		ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
		if(mProgList!=null)
		{
			for (int i = 0; i < mProgList.length; i++)
			{
				if((mProgList[i].service_type == Prog.DB_SERVICE_TYPE_DTV)
					&&(mProgList[i].isHide==false)
					&&(mProgList[i].isValid==true)
					&&mProgList[i].isFavProg==true)
				{
                    String[] bouquets = mProgList[i].getBouquets();
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(PROG_VIEWINDEX, "" + (i));
                    map.put(PROG_ID, String.valueOf(mProgList[i].getProgId()));
                    map.put(PROG_NAME, "" + mProgList[i].getName());
                    maplist.add(map);
				}
			}
		}
		ProgList list = new ProgList();
		
		list.list = maplist;
		list.curProgIndex = ParamSave.GetLastProgIndex(mContext, TVPROG);
		allList.set(FAVPROG, list);		
	}
	
	private void initRadioProg()
	{

		ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
		if(mProgList!=null)
		{
			for (int i = 0; i < mProgList.length; i++)
			{
				if((mProgList[i].service_type == Prog.DB_SERVICE_TYPE_DRADIO)
						&&(mProgList[i].isHide==false)&&(mProgList[i].isValid==true))
				{
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(PROG_VIEWINDEX, "" + (i));
					map.put(PROG_ID, String.valueOf(mProgList[i].getProgId()));
					map.put(PROG_NAME, "" + mProgList[i].getName());
					maplist.add(map);
				}
			}
		}
		ProgList list = new ProgList();
		
		list.list = maplist;
		list.curProgIndex = ParamSave.GetLastProgIndex(mContext, RADIOPROG);
		allList.set(RADIOPROG, list);		
	}

	public void  SetCurMode(int mode)
	{
		curMode = mode;
	}
	
	public int GetCurMode()
	{
		return curMode;
	}

	public ProgList getAllProgList()
	{
		return allList.get(ALLPROG);
	}
	
	public ProgList getTVProgList()
	{
		return allList.get(TVPROG);
		
	}
	
	public ProgList getFavTVProgList()
	{
		return allList.get(FAVPROG);
		
	}

	public ProgList getRadioProgList()
	{
		return allList.get(RADIOPROG);
	}
	
	public Prog getProg(int index)
	{
		return mProgList[index];
	}

    public Prog getProgById(int progId) {
        if(mProgList!=null) {
            for (Prog progTemp : mProgList) {
                if (progId == progTemp.getProgId()) {
                    return progTemp;
                }
            }
        }
        return null;
    }
	
	private int curChannelIndex = 0;
	
	public void setPlayProgIndex(int index)
	{
		curChannelIndex = index;
	}
	
	public int getCurPlayProgIndex()
	{
		return this.curChannelIndex;
	}
	

	public int getCurChannelIndex(ProgList proglist)
	{
		int globalProgIndex = getCurPlayProgIndex();
		int index = 0;
		
		int progid = this.mProgList[globalProgIndex].getProgId();
		
		for(int i=0 ; i<mProgList.length; i++)
		{
			int listProgId = Integer.valueOf(proglist.list.get(i).get(PROG_ID));
			
			if(progid == listProgId)
			{
				proglist.curProgIndex = i;
				return i;
			}
		}
		
		return index;
	}
	public void refreshProgList()
	{
		mProgList = me.getDvbProgList();
		me.initAllProg();
		me.initTvProg();
		me.initRadioProg();
		me.initDvbBatProgList();
		me.initFavTVProg();
	}

//    public int invoke(Parcel request, Parcel reply) {
//        int ret = native_invoke(request, reply);
//        reply.setDataPosition(0);
//        return ret;
//    }

	public boolean resetDvbProgList() {
        return progStorage.DeleCategoryProg(new int[]{DVBC_ID, DTMB_ID});
    }

	public List<ProgList> initDvbBatProgList(){
		batList.removeAll(batList);
		batListName.retainAll(batListName);
		batCategory.clear();
        int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
        int progTypeId;
        if (tunerType == Tuner.UM_TRANS_SYS_TYPE_TER) {
            progTypeId = DTMB_ID;
        } else  {
            progTypeId = DVBC_ID;
        }
        
        Map catMap = progStorage.getCategory();
		Iterator iter = catMap.entrySet().iterator();
		iter = catMap.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Prog[] progs = null;
			HashMap<String, String> map = null ;
			ArrayList<HashMap<String, String>> maplist = null;
			HashMap.Entry entry = (HashMap.Entry) iter.next();
			int cateID = (Integer)entry.getKey();
			String cateName = (String)entry.getValue().toString();
			if(cateID>ContentSchema.CategoryTable.FAV_ID)
			{
				batListName.add(cateName.toString());
		        ArrayList<ProgInfo> progInfos = progStorage.getProgOrderBy(new int[]{progTypeId, TV_ID, cateID},ContentSchema.ProgsTable.ORDER,true);
		        if (progInfos != null) 
		        {
		            progs = new Prog[progInfos.size()];
		            for (ProgInfo progInfo : progInfos) 
		            {
		                progs[progInfos.indexOf(progInfo)] = createProg(progInfo);
		            }	
			     }
		        
				if(progs!=null)
				{
					maplist = new ArrayList<HashMap<String, String>>();
					for (int i = 0; i < progs.length; i++)
					{
						if((progs[i].service_type == Prog.DB_SERVICE_TYPE_DTV)
								&&(progs[i].isHide==false)&&(progs[i].isValid==true))
						{
							map = new HashMap<String, String>();
							map.put(PROG_VIEWINDEX, "" + (i));
							map.put(PROG_ID, String.valueOf(progs[i].getProgId()));
							map.put(PROG_NAME, "" + progs[i].getName());
							maplist.add(map);
						}
					}
				}
				
				ProgList list = new ProgList();
				list.list = maplist;
				list.curProgIndex = 0;
				
				batList.add(list);
				batCategory.put(cateName, list);
			}
        }
		return batList;
	}
	
	public List<String> getDvbBatNameList()
	{
		return batListName;
	}
	public List<ProgList>getDvbBatProgList()
	{
		return batList;
	}
	
	public HashMap<String,ProgList> getDvbBatCategoryProgInfo()
	{
		return batCategory;
	}
	
	public Prog[] getDvbProgList(){
        int tunerType = Tuner.GetInstance(DVB.getInstance()).GetType();
        int progTypeId;
        if (tunerType == Tuner.UM_TRANS_SYS_TYPE_TER) {
            progTypeId = DTMB_ID;
        } else  {
            progTypeId = DVBC_ID;
        }
        ArrayList<ProgInfo> progInfos = progStorage.getProgOrderBy(new int[]{progTypeId}, ContentSchema.ProgsTable.ORDER,true);
        if (progInfos != null) {
            Prog[] progs = new Prog[progInfos.size()];
            for (ProgInfo progInfo : progInfos) {
                progs[progInfos.indexOf(progInfo)] = createProg(progInfo);
            }

            return progs;
        }
        return new Prog[0];
    }

    private Prog createProg(ProgInfo progInfo) {
        Prog prog = new Prog(DVB.getInstance());
        prog.setProgId(progInfo.getProgId());
        prog.setName(progInfo.getProgName());
        prog.setTrack(progInfo.track);
        prog.service_type = progInfo.getServiceType();
        prog.isFavProg = progInfo.fav;
        prog.setTrack(progInfo.track);
        prog.isHide = progInfo.hiden;
        prog.isValid = progInfo.valid;
        prog.setServiceId(progInfo.getServiceId());
        return prog;
    }
}
