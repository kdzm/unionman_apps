package com.portplayer.widget;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hisilicon.android.tvapi.constant.EnumAtvAudsys;
import com.hisilicon.android.tvapi.constant.EnumAtvClrsys;
import com.hisilicon.android.tvapi.constant.EnumColorSystem;
import com.hisilicon.android.tvapi.constant.EnumSignalStat;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.impl.SourceManagerImpl;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.portplayer.R;
import com.portplayer.PortMainActivity;
import com.portplayer.interfaces.ATVChannelInterface;
import com.portplayer.interfaces.InterfaceValueMaps;
import com.portplayer.interfaces.PictureInterface;
import com.portplayer.interfaces.SourceManagerInterface;
import com.portplayer.util.Constant;
import com.portplayer.util.Util;

/**
 * when you change channel ,SignalShow will show. it includes source
 * name,channel number,color format,sound format,timing.
 *
 * @author wangchuanjian
 *
 */
public class SignalShow extends LinearLayout {
    private static final String TAG = "SignalShow";
    private PortMainActivity mContext;
    // text of source name
    private TextView sourceNameTxt;
    // text of channel number
    private TextView channelNumberTxt;
    // text of color format
    private TextView colorFormatTxt;
    // text of sound format
    private TextView soundFormatTxt;
    // text of timing
    private TextView timingTxt;
    // text of change line
    private TextView changelineTxt;
    // layout of format
    private LinearLayout mFormatLayout;
    // private SettingLayout mSettingLayout;
    // get instance of SourceManager
    private SourceManagerImpl mSourceManager = SourceManagerImpl.getInstance();
    // private AtvChannelImpl mChannelManager = AtvChannelImpl.getInstance();
    // resume number
    private int mResumeNum = 0;

    /**
     * handler of set SignalShow gone
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            setVisibility(View.INVISIBLE);
        }
    };

    public SignalShow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = (PortMainActivity) context;
        LayoutInflater mLinflater = LayoutInflater.from(getContext());
        mLinflater.inflate(R.layout.singnal_show, this);
        mFormatLayout = (LinearLayout) findViewById(R.id.format_lay);
        sourceNameTxt = (TextView) findViewById(R.id.source_name_txt);
        channelNumberTxt = (TextView) findViewById(R.id.channel_number_txt);
        colorFormatTxt = (TextView) findViewById(R.id.color_format_txt);
        soundFormatTxt = (TextView) findViewById(R.id.sound_format_txt);
        timingTxt = (TextView) findViewById(R.id.timing_txt);
        changelineTxt = (TextView) findViewById(R.id.change_line_txt);
    }


    /*public String getCurSourceName(int sourceid){
        String sourceName = "";

        switch (sourceid) {
            case EnumSourceIndex.SOURCE_DVBC:
                sourceName = getResources().getString(R.string.DVBC);
                break;
            case EnumSourceIndex.SOURCE_DTMB:
                sourceName = getResources().getString(R.string.DTMB);
                break;
            case EnumSourceIndex.SOURCE_ATV:
                sourceName = getResources().getString(R.string.ATV);
                break;
            case EnumSourceIndex.SOURCE_CVBS1:
                sourceName = getResources().getString(R.string.CVBS);
                break;
            case EnumSourceIndex.SOURCE_CVBS2:
                sourceName = getResources().getString(R.string.CVBS2);
                break;
            case EnumSourceIndex.SOURCE_CVBS3:
                sourceName = getResources().getString(R.string.CVBS3);
                break;                
            case EnumSourceIndex.SOURCE_YPBPR1:
                sourceName = getResources().getString(R.string.YPBPR1);
                break;
            case EnumSourceIndex.SOURCE_HDMI1:
                sourceName = getResources().getString(R.string.HDMI1);
                break;
            case EnumSourceIndex.SOURCE_HDMI2:
                sourceName = getResources().getString(R.string.HDMI2);
                break;
            case EnumSourceIndex.SOURCE_HDMI3:
                sourceName = getResources().getString(R.string.HDMI3);
                break;
            case EnumSourceIndex.SOURCE_HDMI4:
                sourceName = getResources().getString(R.string.HDMI4);
                break;            
            case EnumSourceIndex.SOURCE_VGA:
                sourceName = getResources().getString(R.string.VGA);
                break;
            default:
                sourceName = getResources().getString(R.string.ATV);
                break;
        }
        return sourceName;

    }*/
    /**
     * refresh panel info by sourceId and force
     *
     * @param sourceId
     * @param force
     */
    public void refreshPanelInfo(int sourceId, boolean force) {
        //sourceNameTxt.setText(getCurSourceName(sourceId));
    	Log.i("tf", "===="+sourceId);
    	sourceNameTxt.setText(getSourceStrById(sourceId));
        int signalStatus = mSourceManager.getSignalStatus();
        if (signalStatus != EnumSignalStat.SIGSTAT_SUPPORT && !force) {
            return;
        }
        //if (Constant.LOG_TAG) {
            Log.d(TAG, "refreshPanelInfo() SourceID:" + sourceId
                    + "   currentSignalStatus:" + signalStatus);
       // }
        if (sourceId == EnumSourceIndex.SOURCE_ATV) {
            int color = ATVChannelInterface.getCurrentColorSystem();
            int sound = ATVChannelInterface.getCurrentAudioSystem();

            channelNumberTxt.setText("      "
                    + ATVChannelInterface.getCurProgNumber());
            // int color = mChannelManager.getColorSystem();
            String colorStr = "";
            switch (color) {
            case EnumAtvClrsys.CLRSYS_AUTO:
                colorStr = "AUTO";
                break;
            case EnumAtvClrsys.CLRSYS_NTSC:
            case EnumAtvClrsys.CLRSYS_NTSC443:
                colorStr = "NTSC";
                break;
            case EnumAtvClrsys.CLRSYS_PAL:
            case EnumAtvClrsys.CLRSYS_PAL_60:
            case EnumAtvClrsys.CLRSYS_PAL_M:
            case EnumAtvClrsys.CLRSYS_PAL_N:
            case EnumAtvClrsys.CLRSYS_PAL_NC:
                colorStr = "PAL";
                break;
            case EnumAtvClrsys.CLRSYS_SECAM:
                colorStr = "SECAM";
                break;
            default:
                colorStr = "PAL";
                break;
            }
            colorFormatTxt.setText(colorStr);

            String soundStr = "";
            switch (sound) {
            case EnumAtvAudsys.AUDSYS_DK:
            case EnumAtvAudsys.AUDSYS_DK1_A2:
            case EnumAtvAudsys.AUDSYS_DK2_A2:
            case EnumAtvAudsys.AUDSYS_DK3_A2:
            case EnumAtvAudsys.AUDSYS_DK_NICAM:
                soundStr = "D/K";
                break;
            case EnumAtvAudsys.AUDSYS_BG:
            case EnumAtvAudsys.AUDSYS_BG_A2:
            case EnumAtvAudsys.AUDSYS_BG_NICAM:
                soundStr = "BG";
                break;
            case EnumAtvAudsys.AUDSYS_M:
            case EnumAtvAudsys.AUDSYS_M_A2:
            case EnumAtvAudsys.AUDSYS_M_BTSC:
            case EnumAtvAudsys.AUDSYS_M_EIA_J:
                soundStr = "M";
                break;
            case EnumAtvAudsys.AUDSYS_I:
                soundStr = "I";
                break;
            case EnumAtvAudsys.AUDSYS_L:
            case EnumAtvAudsys.AUDSYS_LL:
                soundStr = "L";
                break;
            default:
                soundStr = "D/K";
                break;
            }
            soundFormatTxt.setText("    " + soundStr);

        } else if (sourceId == EnumSourceIndex.SOURCE_CVBS1
                || sourceId == EnumSourceIndex.SOURCE_CVBS2
                || sourceId == EnumSourceIndex.SOURCE_CVBS3) {
            if (Constant.LOG_TAG) {
                Log.d(TAG, "refreshPanelInfo--->AV Source");
            }
            String colorStr = "";
            if (signalStatus == EnumSignalStat.SIGSTAT_SUPPORT) {
                int color = PictureInterface.getColorSystem();
                switch(color){
                    case EnumColorSystem.CLRSYS_AUTO:
                        colorStr = "AUTO";
                        break;
                    case EnumColorSystem.CLRSYS_PAL:
                    case EnumColorSystem.CLRSYS_PAL_M:
                    case EnumColorSystem.CLRSYS_PAL_N:
                    case EnumColorSystem.CLRSYS_PAL_60:
                        colorStr = "PAL";
                        break;
                    case EnumColorSystem.CLRSYS_NTSC:
                    case EnumColorSystem.CLRSYS_NTSC443:
                    case EnumColorSystem.CLRSYS_NTSC_50:
                        colorStr = "NTSC";
                        break;
                    case EnumColorSystem.CLRSYS_SECAM:
                        colorStr = "SECAM";
                        break;
                    default:
                        colorStr = "";
                        break;
                }
            }
            colorFormatTxt.setText(colorStr);
        } else { // Non RF source of Timing information
            String timing = "";
            TimingInfo ti = SourceManagerInterface.getTimingInfo();
            int width = ti.getiWidth();
            int height = ti.getiHeight();
            int frame = ti.getiFrame();
            boolean isInterlace = ti.isbInterlace();
            if (Constant.LOG_TAG) {
                Log.d(TAG, "refreshPanelInfo_width:" + width + "    height:"
                        + height + "    frame:" + frame + "    isInterlace:"
                        + isInterlace);
            }
            if (sourceId == EnumSourceIndex.SOURCE_YPBPR1
                    || sourceId == EnumSourceIndex.SOURCE_YPBPR2) {
                if (signalStatus == EnumSignalStat.SIGSTAT_SUPPORT) {
                    if (isInterlace) { // Interlace
                        timing = "" + height + "i" + "/" + frame + "Hz";
                    } else {
                        timing = "" + height + "p" + "/" + frame + "Hz";
                    }
                } else {
                    timing = "";
                }
            } else if (sourceId == EnumSourceIndex.SOURCE_HDMI1
                    || sourceId == EnumSourceIndex.SOURCE_HDMI2
                    || sourceId == EnumSourceIndex.SOURCE_HDMI3
                    || sourceId == EnumSourceIndex.SOURCE_HDMI4) {
                if (signalStatus == EnumSignalStat.SIGSTAT_SUPPORT) {
                    int hdmiFmt = ti.getiHDMIFmt();
                   // if (Constant.LOG_TAG) {
                        Log.d(TAG, "refreshPanelInfo--->HDMI:--->hdmiFmt:"
                                + hdmiFmt);
                   // }
                    if (hdmiFmt == 0 || hdmiFmt == 2) { // HI_MW_HDMI_FORMAT_HDMI
                                                        // ||
                                                        // HI_MW_HDMI_FORMAT_MHL
                        if (isInterlace) { // Interlaced
                            timing = "" + height + "i" + "/" + frame + "Hz";
                        } else {
                            timing = "" + height + "p" + "/" + frame + "Hz";
                        }
                    } else if (hdmiFmt == 1) { // HI_MW_HDMI_FORMAT_DVI
                        timing = "" + width + "x" + height + "/" + frame + "Hz";
                    }
                } else {
                    Log.d(TAG,"--------no SignalShow--------");
                    timing = "";
                }
            } else if (sourceId == EnumSourceIndex.SOURCE_VGA) {
                if (signalStatus == EnumSignalStat.SIGSTAT_SUPPORT) {
                    timing = "" + width + "x" + height + "/" + frame + "Hz";
                } else {
                    timing = "";
                }
            }
            colorFormatTxt.setText(timing);
        }

        this.setVisibility(View.VISIBLE);
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 5000);
    }


    public void focusHide(){
        mHandler.removeMessages(0);
        this.setVisibility(View.GONE);
    }

    /**
     * change channel by channelNum
     *
     * @param channelNum
     */
    public void changingChannel(int channelNum) {
        if (mSourceManager.getSelectSourceId() == EnumSourceIndex.SOURCE_ATV) {
            channelNumberTxt.setText("      " + channelNum);
        }
        this.setVisibility(View.VISIBLE);
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 5000);
    }
    
    private String getSourceStrById(int sourceIndex){
		int strid = 0;
		ArrayList<Integer> mAllSourceList = new ArrayList<Integer>();
		ArrayList<Integer> mCVBSList = new ArrayList<Integer>();
		ArrayList<Integer> mYPBRList = new ArrayList<Integer>();
		ArrayList<Integer> mHDMIList = new ArrayList<Integer>();
        //获取所有端口，端口不能超过10个
		
        mAllSourceList = SourceManagerInterface.getSourceList(); 
		 for(int i=0;i<mAllSourceList.size();i++){
	    	   if(mAllSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS1||
	    		  mAllSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS2||
	    		  mAllSourceList.get(i)==EnumSourceIndex.SOURCE_CVBS3){
	    		   mCVBSList.add(mAllSourceList.get(i));
	    		   
	    	   }else if(mAllSourceList.get(i)==EnumSourceIndex.SOURCE_YPBPR1||
	    			   mAllSourceList.get(i)==EnumSourceIndex.SOURCE_YPBPR2){
	    		   mYPBRList.add(mAllSourceList.get(i));
	    	   }else if(mAllSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI1||
	    			    mAllSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI2||
	    			    mAllSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI3||
	    			    mAllSourceList.get(i)==EnumSourceIndex.SOURCE_HDMI4){
	    		   mHDMIList.add(mAllSourceList.get(i));
	    	   }
	       }
		 
		 Log.i("heh", "==mCVBSList="+mCVBSList.size());
		 Log.i("heh", "==mYPBRList="+mYPBRList.size());
		 Log.i("heh", "==mHDMIList="+mHDMIList.size());
		switch (sourceIndex){
			case EnumSourceIndex.SOURCE_CVBS1:
				
				if(mCVBSList.size()==1){
					strid = R.string.CVBS;
				}else{
					int index=mCVBSList.indexOf(EnumSourceIndex.SOURCE_CVBS1);
					if(index==0){
						strid = R.string.CVBS1;
					}else if(index==1){
						strid = R.string.CVBS2;
					}else if(index==2){
						strid = R.string.CVBS3;
					}
				}
				
				break;
			case EnumSourceIndex.SOURCE_CVBS2:
				
				if(mCVBSList.size()==1){
					strid = R.string.CVBS;
				}else{
					int indexcvbs2=mCVBSList.indexOf(EnumSourceIndex.SOURCE_CVBS2);
					if(indexcvbs2==0){
						strid = R.string.CVBS1;
					}else if(indexcvbs2==1){
						strid = R.string.CVBS2;
					}else if(indexcvbs2==2){
						strid = R.string.CVBS3;
					}
				}
				
				break;
			case EnumSourceIndex.SOURCE_CVBS3:
				
				if(mCVBSList.size()==1){
					strid = R.string.CVBS;
				}else{
					int indexcvbs3=mCVBSList.indexOf(EnumSourceIndex.SOURCE_CVBS3);
					if(indexcvbs3==0){
						strid = R.string.CVBS1;
					}else if(indexcvbs3==1){
						strid = R.string.CVBS2;
					}else if(indexcvbs3==2){
						strid = R.string.CVBS3;
					}
				}
				
				break;
			case EnumSourceIndex.SOURCE_YPBPR1:
				
				
					if(mYPBRList.size()==1){
						strid = R.string.YPBPR;
					}else{
						int indexypbpr=mYPBRList.indexOf(EnumSourceIndex.SOURCE_YPBPR1);
						if(indexypbpr==0){
							strid = R.string.YPBPR1;
						}else if(indexypbpr==1){
							strid = R.string.YPBPR2;
						}
					}
				
				break;
				
			case EnumSourceIndex.SOURCE_YPBPR2:
			
					if(mYPBRList.size()==1){
						strid = R.string.YPBPR;
					}else{
						int indexypbpr1=mYPBRList.indexOf(EnumSourceIndex.SOURCE_YPBPR2);
						if(indexypbpr1==0){
							strid = R.string.YPBPR1;
						}else if(indexypbpr1==1){
							strid = R.string.YPBPR2;
						}
					}
					Log.i("fk", "===SOURCE_YPBPR2===++++");
					Log.i("fk", "===SOURCE_YPBPR2==="+mContext.getString(strid));
				break;
			case EnumSourceIndex.SOURCE_HDMI1:
				
				int indexhdmi=mHDMIList.indexOf(EnumSourceIndex.SOURCE_HDMI1);
				if(indexhdmi==0){
					strid = R.string.HDMI1;
				}else if(indexhdmi==1){
					strid = R.string.HDMI2;
				}else if(indexhdmi==2){
					strid = R.string.HDMI3;
				}else if(indexhdmi==3){
					strid = R.string.HDMI4;
				}
				
				break;
			case EnumSourceIndex.SOURCE_HDMI2:
				int indexhdmi2=mHDMIList.indexOf(EnumSourceIndex.SOURCE_HDMI2);
				if(indexhdmi2==0){
					strid = R.string.HDMI1;
				}else if(indexhdmi2==1){
					strid = R.string.HDMI2;
				}else if(indexhdmi2==2){
					strid = R.string.HDMI3;
				}else if(indexhdmi2==3){
					strid = R.string.HDMI4;
				}
				break;
			case EnumSourceIndex.SOURCE_HDMI3:
				int indexhdmi3=mHDMIList.indexOf(EnumSourceIndex.SOURCE_HDMI3);
				if(indexhdmi3==0){
					strid = R.string.HDMI1;
				}else if(indexhdmi3==1){
					strid = R.string.HDMI2;
				}else if(indexhdmi3==2){
					strid = R.string.HDMI3;
				}else if(indexhdmi3==3){
					strid = R.string.HDMI4;
				}
				break;
			case EnumSourceIndex.SOURCE_HDMI4:
				int indexhdmi4=mHDMIList.indexOf(EnumSourceIndex.SOURCE_HDMI4);
				if(indexhdmi4==0){
					strid = R.string.HDMI1;
				}else if(indexhdmi4==1){
					strid = R.string.HDMI2;
				}else if(indexhdmi4==2){
					strid = R.string.HDMI3;
				}else if(indexhdmi4==3){
					strid = R.string.HDMI4;
				}
				break;
			case EnumSourceIndex.SOURCE_VGA:
				strid = R.string.VGA;
				break;
			default:
				strid = R.string.CVBS;
				break;
				
		}

		return getResources().getString(strid);
	}

}
