package com.um.atv.widget;

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hisilicon.android.tvapi.constant.EnumAtvAudsys;
import com.hisilicon.android.tvapi.constant.EnumAtvClrsys;
import com.hisilicon.android.tvapi.constant.EnumColorSystem;
import com.hisilicon.android.tvapi.constant.EnumSignalStat;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.constant.EnumSoundStereo;
import com.hisilicon.android.tvapi.impl.SourceManagerImpl;
import com.hisilicon.android.tvapi.vo.TimingInfo;
import com.hisilicon.android.tvapi.vo.TvProgram;
import com.um.atv.R;
import com.um.atv.ATVMainActivity;
import com.um.atv.interfaces.ATVChannelInterface;
import com.um.atv.interfaces.InterfaceValueMaps;
import com.um.atv.interfaces.PictureInterface;
import com.um.atv.interfaces.SourceManagerInterface;
import com.um.atv.interfaces.AudioInterface;
import com.um.atv.util.Constant;
import com.um.atv.util.Util;

/**
 * when you change channel ,SignalShow will show. it includes source
 * name,channel number,color format,sound format,timing.
 *
 * @author wangchuanjian
 *
 */
public class SignalShow extends LinearLayout {
    private static final String TAG = "SignalShow";
    private ATVMainActivity mContext;

    private ImageView channelNumber1st;
    private ImageView channelNumber2nd;
    private ImageView channelNumber3th;
    
    private TextView buttomchannelNumberTxt;
    private TextView buttomchannelNameTxt;
    // text of color format
    private TextView colorFormatTxt;
    // text of sound format
    private TextView soundFormatTxt;

    private RelativeLayout channelNumberLayout;
    // layout of format
    private RelativeLayout mFormatLayout;
    // private SettingLayout mSettingLayout;
    // get instance of SourceManager
    private SourceManagerImpl mSourceManager = SourceManagerImpl.getInstance();
    // private CusAtvChannelImpl mChannelManager = CusAtvChannelImpl.getInstance();
    // resume number
    private int mResumeNum = 0;
    
    private ArrayList<TvProgram> atvChannelList = new ArrayList<TvProgram>();

    private int[] imageId;
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
        this.mContext = (ATVMainActivity) context;
        LayoutInflater mLinflater = LayoutInflater.from(getContext());
        mLinflater.inflate(R.layout.singnal_show, this);
        channelNumberLayout = (RelativeLayout) findViewById(R.id.channel_number_layout);
        mFormatLayout = (RelativeLayout) findViewById(R.id.format_lay);
        channelNumber1st = (ImageView) findViewById(R.id.channel_number_1st);
        channelNumber2nd = (ImageView) findViewById(R.id.channel_number_2nd);
        channelNumber3th = (ImageView) findViewById(R.id.channel_number_3th);
        buttomchannelNumberTxt = (TextView) findViewById(R.id.buttom_channel_number_txt);
        buttomchannelNameTxt = (TextView) findViewById(R.id.buttom_channel_name_txt);
        colorFormatTxt = (TextView) findViewById(R.id.color_format_txt);
        soundFormatTxt = (TextView) findViewById(R.id.sound_format_txt);
        imageId = new int[] {
        		R.drawable.channel_number_0,
        		R.drawable.channel_number_1,
        		R.drawable.channel_number_2,
        		R.drawable.channel_number_3,
        		R.drawable.channel_number_4,
        		R.drawable.channel_number_5,
        		R.drawable.channel_number_6,
        		R.drawable.channel_number_7,
        		R.drawable.channel_number_8,
        		R.drawable.channel_number_9,
        };
    }


    public String getCurSourceName(int sourceid){
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
                sourceName = getResources().getString(R.string.CVBS1);
                break;
            case EnumSourceIndex.SOURCE_CVBS2:
                sourceName = getResources().getString(R.string.CVBS2);
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
            case EnumSourceIndex.SOURCE_VGA:
                sourceName = getResources().getString(R.string.VGA);
                break;
            default:
                sourceName = getResources().getString(R.string.ATV);
                break;
        }
        return sourceName;

    }
    /**
     * refresh panel info by sourceId and force
     *
     * @param sourceId
     * @param force
     */
    public void refreshPanelInfo(int sourceId, boolean force) {
        int signalStatus = mSourceManager.getSignalStatus();
        if (signalStatus != EnumSignalStat.SIGSTAT_SUPPORT && !force) {
            return;
        }
        if (Constant.LOG_TAG) {
            Log.d(TAG, "refreshPanelInfo() SourceID:" + sourceId
                    + "   currentSignalStatus:" + signalStatus);
        }
        
        int channelNumber = ATVChannelInterface.getCurProgNumber();
        if (channelNumber <= 0)
        {
        	return ;
        }
        int number1st = ((channelNumber/100)%10);
        int number2nd = ((channelNumber/10)%10);
        int number3th = (channelNumber%10);

        if (number1st == 0)
        {
        	channelNumber1st.setBackgroundResource(R.color.transparent);
        }
        else
        {
        	channelNumber1st.setBackgroundResource(imageId[number1st]);
        }
        
        if (number1st == 0 && number2nd == 0)
    	{
    		channelNumber2nd.setBackgroundResource(R.color.transparent);
    	}
    	else
    	{
    		channelNumber2nd.setBackgroundResource(imageId[number2nd]);
    	}
        
        channelNumber3th.setBackgroundResource(imageId[number3th]);

        this.setVisibility(View.VISIBLE);
        channelNumberLayout.setVisibility(View.GONE);
        channelNumberLayout.setVisibility(View.VISIBLE);
        mFormatLayout.setVisibility(View.GONE);
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 5000);
    }

    public void showChannelInfo() {
        int color = ATVChannelInterface.getCurrentColorSystem();
        int sound = ATVChannelInterface.getCurrentAudioSystem();
        
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumIntegerDigits(3);
        nf.setMinimumIntegerDigits(3);
        
        String channelName = "";
        atvChannelList = ATVChannelInterface.getProgList();
        int CurNumber = ATVChannelInterface.getCurProgNumber();
        for (int i=0; i<atvChannelList.size(); i++)
        {
        	TvProgram  info = atvChannelList.get(i);
        	if (CurNumber == info.getiId())
        	{
        		channelName = info.getStrName();
        	}
        }
        
        buttomchannelNumberTxt.setText(nf.format(ATVChannelInterface.getCurProgNumber()));
        buttomchannelNameTxt.setText(channelName);
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
       // case EnumAtvAudsys.AUDSYS_LL: 为unknown设置为默认伴音制式
            soundStr = "L";
            break;
        default:
            soundStr = "D/K";
            break;
        }
        soundFormatTxt.setText(soundStr);

        this.setVisibility(View.VISIBLE);
        mFormatLayout.setVisibility(View.GONE);
        mFormatLayout.setVisibility(View.VISIBLE);
        channelNumberLayout.setVisibility(View.GONE);
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
    	int number1st = ((channelNum/100)%10);
        int number2nd = ((channelNum/10)%10);
        int number3th = (channelNum%10);
        
        if (number1st == 0)
        {
        	channelNumber1st.setBackgroundResource(R.color.transparent);
        }
        else
        {
        	channelNumber1st.setBackgroundResource(imageId[number1st]);
        }
        
        if (number1st == 0 && number2nd == 0)
    	{
    		channelNumber2nd.setBackgroundResource(R.color.transparent);
    	}
    	else
    	{
    		channelNumber2nd.setBackgroundResource(imageId[number2nd]);
    	}
        
        channelNumber3th.setBackgroundResource(imageId[number3th]);
        
        this.setVisibility(View.VISIBLE);
        channelNumberLayout.setVisibility(View.GONE);
        channelNumberLayout.setVisibility(View.VISIBLE);
        mFormatLayout.setVisibility(View.GONE);
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 5000);
    }
    
    public boolean isPlayProgram()
    {
    	if (getVisibility() == View.VISIBLE)
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public int getCurChannelNumber()
    {
    	int channelNumber = ATVChannelInterface.getCurProgNumber();
    	
    	return channelNumber;
    }

}
