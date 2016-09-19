package com.um.dvbstack;
import java.io.UnsupportedEncodingException;


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
    private boolean collected;
    private int track;

    public int getTrack() {
        return track;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public int getProgId()
    {
        return progid;
    }

    public void setProgId(int id) {
        progid = id;
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
	public static final int DB_SERVICE_TYPE_IRD_HDTV  = 0x19;
	
    public void prog_get_localtime(Epg_LocalTime dt)
    {
        getLocalTime(dt) ;
    }

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

    public void setTrack(int track) {
        this.track = track;
    }

    public boolean getCollectFlag() {
        return collected;
    }

    public void setCollectFlag(boolean flag) {
        this.collected = flag;
    }
}
