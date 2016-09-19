package com.um.upgrade.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UpgradeInfoBean {
	private final static String TAG = UpgradeInfoBean.class.getSimpleName()+"----U668";
    private final static boolean LOG_EN = true;
    public final static String FORCE_MODE = "force";
    public final static String MANUAL_MODE = "manual";
    public final static String SILENT_MODE = "silent";
    public final static String RECOVERY_PARTITION_MODE = "recovery";

	private String mProductModel = null;
	private String mVendor = null;
	private String mVersion = null;
	private String mHardwareVersion = null;
	private String mSoftMinVersion = null;
	private String mSoftMaxVersion = null;
	private String mUpdateMode = null;
	private List<Serial> mSerialList = null;
    private List<Software> mSoftwareList = null;
	private List<Packet> mPacketList =null;
	private String mDescription = null;
	
	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public UpgradeInfoBean() {
		mSerialList = new ArrayList<UpgradeInfoBean.Serial>();
        mSoftwareList = new ArrayList<Software>();
		mPacketList = new ArrayList<UpgradeInfoBean.Packet>();
	}

	public class Serial{
        private String from = null;
        private String to = null;
        private int length = 0;

        public Serial(String from , String to, int len) {
            this.from = from;
            this.to = to;
            this.length = len;
        }

        public String getSerialFrom()
        {
            return this.from;
        }

        public void setSerialFrom(String from)
        {
             this.from = from;

        }

        public String getSerialTo()
        {
            return this.to;
        }

        public void setSerialTo(String to)
        {
             this.to = to;

        }

        public int getSerialLength()
        {
            return this.length;
        }

        public void setSerialLength(int length)
        {
             this.length = length;
        }
	}

    public class Software {
        private String min = null;
        private String max = null;

        public Software(String min, String max) {
            this.min = min;
            this.max = max;
        }

        public void setMin (String min) {
            this.min = min;
        }

        public void setMax (String max) {
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public String getMax() {
            return max;
        }
    }

	public class Packet{
        public static final String ZIP_TYPE = "zip";
        public static final String APK_TYPE = "apk";

		private String type = null;
		private String url = null;
        private String packageName = null;
        private String displayName = null;
        private String detail = null;
        private String packageVersion = null;
		
		public Packet(String type, String url) {
			this.type = type;
			this.url = url;
		}

        public Packet(String type, String url, String packageName, String displayName, String detail, String packageVersion) {
            this.type = type;
            this.url = url;
            this.packageName = packageName;
            this.displayName = displayName;
            this.detail = detail;
            this.packageVersion = packageVersion;
        }
		
		public String getPacketType() {
			return this.type;
		}
		
		public void setPacketType(String type) {
			 this.type = type;
		}
		
		public String getPacketUrl() {
			return this.url;
		}
		
		public void setPacketUrl(String url) {
			 this.url = url;
		}

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getPackageVersion() {
            return packageVersion;
        }

        public void setPackageVersion(String packageVersion) {
            this.packageVersion = packageVersion;
        }
	}
	
	public String getProductModel()
	{
		return this.mProductModel;
	}
	
	public void setProductModel(String productModel)
	{
		
		this.mProductModel = productModel;
	}
	
	public String getVendor()
	{
		
		return this.mVendor;
	}
	
	public void setVendor(String vendor)
	{
		
		this.mVendor = vendor;
	}
	
	public String getSoftMinVersion()
	{
		
		return this.mSoftMinVersion;
	}
	
	public void setSoftMinVersion(String softMinVersion)
	{
		
		this.mSoftMinVersion = softMinVersion;
	}
	
	public String getSoftMaxVersion()
	{
		
		return this.mSoftMaxVersion;
	}
	
	public void setSoftMaxVersion(String softMaxVersion)
	{
		
		this.mSoftMaxVersion = softMaxVersion;
	}
	
	public List<Serial> getSerials()
	{
		return this.mSerialList;
	}

    public List<Software> getSoftwares()
    {
        return this.mSoftwareList;
    }
	
	public void setSerialList(List<Serial>  serialList)
	{
		
		this.mSerialList = serialList;
	}
	
	public void setSerial(String from, String to , int len) {
		this.mSerialList.add(new Serial(from, to, len));
	}

    public void setSoftware(String min, String max) {
        this.mSoftwareList.add(new Software(min, max));
    }

	public List<Packet> getPacketList()
	{
		return this.mPacketList;
    }
	
	public void setPacketList(List<Packet>  packetList)
	{
		this.mPacketList = packetList;
	}
	
	public void setPacket(String type, String url) {
		this.mPacketList.add(new Packet(type, url));
	}

    public void setPacket(String type, String url, String packageName, String displayName, String detail, String packageVersion) {
        this.mPacketList.add(new Packet(type, url, packageName, displayName, detail, packageVersion));
    }
	
	public void setVersion(String version) {
		
		this.mVersion = version;
	}
	
	public String getVersion() {
		return mVersion;
	}

	public String getHardVersion() {
		
		return this.mHardwareVersion;
	}
	
	public void setHardVersion(String hardwareVersion) {
		this.mHardwareVersion = hardwareVersion;
	}
	
	public void setUpdateMode(String updateMode) {
		this.mUpdateMode = updateMode;
	}
	
	public String getUpdateMode() {
		return mUpdateMode;
	}
	
	@Override
	public String toString() {
        if (LOG_EN) Log.i(TAG, "mProductModel="+mProductModel+"   mVendor="+mVendor+"   mVersion="+mVersion+"   mSoftMinVersion="+
                mSoftMinVersion+"   mSoftMaxVersion="+mSoftMaxVersion+"  mHardwareVersion="+mHardwareVersion);

		return "mProductModel="+mProductModel+"  mVendor="+mVendor+"  mVersion="+mVersion+"  mSoftMinVersion="+mSoftMinVersion+"  mSoftMaxVersion="+mSoftMaxVersion+
				"  mHardwareVersion="+mHardwareVersion;
	}
}
