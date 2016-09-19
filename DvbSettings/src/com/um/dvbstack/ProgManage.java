package com.um.dvbstack;


import android.os.Parcel;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ProgManage {
    public final static String PROG_VIEWINDEX = "index";
    public final static String PROG_ID = "progid";
    public final static String PROG_NAME = "name";

    private final static String TAG = "PROGMANAGE";
    public  int mNativeContext; // accessed by native methods

    static ProgManage mPM;
    private Prog[] mProgList = null;//= new Prog[0] ;
    private static ArrayList<Prog> plist = new ArrayList<Prog>();

    private List<ProgList> allList = new ArrayList<ProgList>();

    public final static int ALLPROG = 0;
    public final static int TVPROG  = 1;
    public final static int RADIOPROG = 2;
    public final static int FAVPROG = 3;
    public final static int NVODPROG = 4;

    public final static int BATPROG = 5;

    public final static int MAX_LIST_CNT = 6;

    private static int  curMode=TVPROG;
    public  static String  lastprogid=null;
    private static ProgManage me;
    public ProgManage(DVB dvb)
    {
        mNativeContext = dvb.mNativeContext;
    }
    public static ProgManage GetInstance()
    {
        if(null == me)
        {
            Log.i(TAG,"PROGM GetInstance call!!!");

            me = new ProgManage(DVB.getInstance());

            Log.i(TAG,"PROGM GetInstance call ....!!!");
            int maxList = me.MAX_LIST_CNT;
            for(int i=0; i<maxList; i++)
            {
                me.allList.add(null);
            }

            me.getDvbProgList(plist);
            me.initAllProg();
            me.initTvProg();
            me.initRadioProg();

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

                HashMap<String, String> map = new HashMap<String, String>();
                map.put(PROG_VIEWINDEX, "" + (i));
                map.put(PROG_ID, String.valueOf(mProgList[i].getProgId()));
                map.put(PROG_NAME, "" + mProgList[i].getName());
                allchan.add(map);
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
                if((mProgList[i].service_type == Prog.DB_SERVICE_TYPE_DTV)||
					(mProgList[i].service_type == Prog.DB_SERVICE_TYPE_IRD_HDTV))
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
        list.curProgIndex = 0;

        allList.set(TVPROG, list);
    }

    private void initRadioProg()
    {

        ArrayList<HashMap<String, String>> maplist = new ArrayList<HashMap<String, String>>();
        if(mProgList!=null)
        {
            for (int i = 0; i < mProgList.length; i++)
            {
                if(mProgList[i].service_type == Prog.DB_SERVICE_TYPE_DRADIO)
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
        list.curProgIndex = 0;

        allList.set(RADIOPROG, list);
    }

    private Prog[] CreateProgList(int count)
    {
        Log.i(TAG,"CreateProgList count:"+ count);
        if(count ==0)
        {
            mProgList = null;

        }
        mProgList = new Prog[count];

        for(int i = 0; i<count; i++)
        {
            mProgList[i] = new Prog(DVB.getInstance());
        }
        return mProgList;
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
        int globalProgIndex = ProgManage.GetInstance().getCurPlayProgIndex();
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
        me.getDvbProgList(plist);
        me.initAllProg();
        me.initTvProg();
        me.initRadioProg();

    }

    public int invoke(Parcel request, Parcel reply) {
        int ret = native_invoke(request, reply);
        reply.setDataPosition(0);
        return ret;
    }

    public final native int resetDvbProgList();
    public final native int getDvbProgList(ArrayList<Prog> plist);
    private final native int native_invoke(Parcel request, Parcel reply);

}
