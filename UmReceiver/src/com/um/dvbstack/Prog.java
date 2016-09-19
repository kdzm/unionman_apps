package com.um.dvbstack;
import android.text.format.Time;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.os.Parcel;
import android.text.format.Time;
import android.util.Log;


public class Prog
{
	public  int mNativeContext; // accessed by native methods
	public Prog(DVB dvb)
	{
		mNativeContext = dvb.mNativeContext;
				
	}

	public static class Epg_LocalTime 
	{
		public int 	mjd;
		public int 	year;
		public int  month;
		public int  day;
		public int  weekday;
		public int  hour;
		public int  min;
		public int  sec;
	}
	
	private int progid;
	private String progname;
	private String[] bouquetNames;
	private int tsId;
	private int serviceId;
    private int[] bouquetIds = new int[16];

	public int getProgId()
	{
		return progid;
	}
	
	public String getName()
	{
		return progname;
	}

	public String[] getBouquets()
	{
	    return bouquetNames;
	}

    public int getTsId()
    {
        return tsId;
    }

    public int getServiceId()
    {
        return serviceId;
    }

    public int[] getBouquetIds() {
        return bouquetIds;
    }

    public void setBouquetIds(int[] bids )
    {
        bouquetIds = bids;
    }
    
	public boolean isFavProg = true;
	
	public int service_type;
	public static final int DB_SVRVICE_TYPE_UNKEW =0x0;
	public static final int DB_SERVICE_TYPE_DTV    = 0x01;
	public static final int DB_SERVICE_TYPE_DRADIO   = 0x02;
	public static final int DB_SERVICE_TYPE_TELTEXT    = 0x03;
	public static final int DB_SERVICE_TYPE_NVOD_REF   = 0x04;
	public static final int DB_SERVICE_TYPE_NVOD_TS    = 0x05;
	public static final int DB_SERVICE_TYPE_MOSAIC   = 0x06;
	public static final int DB_SERVICE_TYPE_DATABROAD  = 0x0C;
	public static final int DB_SERVICE_TYPE_DATASTOCK1  = 0x80;
	public static final int DB_SERVICE_TYPE_DATASTOCK2  = 0x81;
	public static final int DB_SERVICE_TYPE_DATASTOCK3  = 0x88;
	
							
							public int getPF(ArrayList<Event> pf)
	{
		int len = EVENT_PACKET_LEN*2 + 4; /*in and out param*/
		byte[] pfbuf = new byte[len];
		
		
		if(getPFEvent(progid, pfbuf, len) == 0)
		{
			Log.i("Prog","befroe parser");
			parser(pf, pfbuf, len);
			Log.i("Prog","after parser");
			return 0;
		}
		return -1;	
	}
	
	public int getWeek(ArrayList<Event> events, int mjd)
	{
		int len = EVENT_PACKET_LEN*1000 + 4 ; /*in and out param*/
		byte[] schbuf = new byte[len];
		
		if(getSchEvent(progid, schbuf, len, mjd) == 0)
		{
			parser(events, schbuf, len);			
		}

		return 0;
	}
	
	public void prog_get_localtime(Epg_LocalTime dt) 
	{
		getLocalTime(dt) ;
	}
	
//	typedef struct dvbPROGLIST_EVENT_S
//	{
//	    UM_U32 type;
//	    UM_U32 eventid;
//	    UM_U16  u16sYear; /*    u16Year = 2011 */
//	    UM_U8   u8sMonth; /*    u8Month = 7    */
//	    UM_U8   u8sDay;   /*    u8Day = 5      */
//	    UM_U8   u8sWeekday;
//	    UM_U8   u8sHour;
//	    UM_U8   u8sMin;
//	    UM_U8   u8sSec;
//	    UM_U16  u16eYear; /*    u16Year = 2011 */
//	    UM_U8   u8eMonth; /*    u8Month = 7    */
//	    UM_U8   u8eDay;   /*    u8Day = 5      */
//	    UM_U8   u8eWeekday;
//	    UM_U8   u8eHour;
//	    UM_U8   u8eMin;
//	    UM_U8   u8eSec;
//	    UM_U16  u16len;
//	    UM_U8   reser[6];
//	    UM_U8   data[EPG_MAX_EVENTNAME_LENGTH];/*total 32Byte*/
//	    
//	}UM_EVENT_PACKET;
	private native int getSchEvent(int progid, byte[] buf, int len, int mjd);
	private native int getPFEvent(int progid, byte[] buf, int len);
	private native int getLocalTime(Epg_LocalTime dt) ;
	final int EPG_MAX_EVENTNAME_LENGTH = 256;
	final int EVENT_PACKET_LEN = 284;//7+EPG_MAX_EVENTNAME_LENGTH/4;
	
	private int byte2int(byte a, byte b)
	{
		String hex = "";
		hex += Integer.toHexString(a&0xff);
		hex += Integer.toHexString(b&0xff);
		
		return Integer.valueOf(hex, 16);
	}
	
	private int parser(ArrayList<Event> pf, byte[] buf, int len)
	{

		int offset = 4;
		int count = 0 ;
		int length =buf[0]*16*16*16+buf[1]*16*16+buf[2]*16+buf[3];
		len = length>buf.length?buf.length:length;
		while((len - offset)>=EVENT_PACKET_LEN)
		{
			Time s;
			Time e;
			String name;

			int start_year	= byte2int(buf[ offset + 9 ], buf[ offset + 8 ]);
			int start_month	= buf[offset + 10];
			int start_day	= buf[offset + 11];
			int start_hour	= buf[offset + 13];
			int start_min	= buf[offset + 14];
			int start_sec	= buf[offset + 15];
			
			int end_year	= byte2int(buf[ offset + 17 ], buf[ offset + 16 ]);
			int end_month	= buf[offset + 18];
			int end_day		= buf[offset + 19];
			int end_hour	= buf[offset + 21];
			int end_min		= buf[offset + 22];
			int end_sec		= buf[offset + 23];
			
			s = new Time();
			e = new Time();
			s.set(start_sec, start_min, start_hour, start_day, start_month, start_year);
			e.set(end_sec, end_min, end_hour, end_day, end_month, end_year);
			//Log.i("Prog","run here 122");
			int name_len = byte2int(buf[ offset + 25 ], buf[ offset + 24 ]);

				try {
					name = new String(buf, offset + 28, name_len, "UnicodeBig");
				} catch (UnsupportedEncodingException e1) {
					name = "";
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	
//			name = new String("epg test: ��������");
			Event evt = new Event(s,e,name);
			pf.add(evt);	
			count++ ;
			//Log.i("Prog","run here 128");			
			offset += EVENT_PACKET_LEN;
		}
		Log.i("Prog","return elist");			
		return 0;
	}
	public void setName(byte[] name)
	{
		//CharsetDetector det = new CharsetDetector();
		
		//String code = det.guestEncoding(name);
		try {
			progname = new String(name, "UnicodeBig");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

    private boolean modifyPorg(int progId, String filed, int value) {
        int ret = -1;
        Parcel request = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        request.writeInterfaceToken("android.Dvbstack.IProgManage");
        request.writeInt(0);
        request.writeInt(progId);
        request.writeString(filed);
        request.writeInt(value);
        if (0 == ProgManage.GetInstance().invoke(request, reply)) {
            ret = reply.readInt();
        }

        return (ret == 0);
    }

    private int getProgInofInt(int progId, String filed) {
        Parcel request = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        request.writeInterfaceToken("android.Dvbstack.IProgManage");
        request.writeInt(1);
        request.writeInt(progId);
        request.writeString(filed);
        if (0 == ProgManage.GetInstance().invoke(request, reply)) {
            if (0 == reply.readInt()) {
                return reply.readInt();
            }
        }

        return -1;
    }

    //track --  0:left  1:right  2:stereo  3:mono
    public boolean setTrack(int track) {
        return modifyPorg(progid, "track", track);
    }

    public int getTrack() {
        int ret = getProgInofInt(progid, "track");
        if (ret == -1) {
            return 0;
        }
        return ret;
    }

    public int getWatchTime() {
        int ret = getProgInofInt(progid, "watch_time");
        if (ret == -1) {
            return 0;
        }
        return ret;
    }

    public boolean getCollectFlag() {
        int ret = getProgInofInt(progid, "collected");
        return (ret == 1);
    }

    public boolean setCollectFlag(boolean flag) {
        return modifyPorg(progid, "collected", (flag ? 1:0));
    }
}
//	
//Prog epgprog = new Prog(DVB.GetInstance());
//ArrayList<com.um.dvbstack.Event> pf = null;
//
//epgprog.getPF(pf);
//
////chanbar_text_p_name
//TextView pname = (TextView) this.findViewById(R.id.chanbar_text_p_name);
//TextView pStartTime = (TextView)this.findViewById(R.id.chanbar_text_p_start_time);
//TextView pEndTime	= (TextView)this.findViewById(R.id.chanbar_txt_p_end_time);
//
//pname.setText(pf.get(0).getName());
//
//Time startTime = pf.get(0).getStartTime();
//Time endTime = pf.get(0).getEndTime();
//pStartTime.setText(startTime.hour + ":" + startTime.minute);
//pEndTime.setText(endTime.hour + ":" + endTime.minute);