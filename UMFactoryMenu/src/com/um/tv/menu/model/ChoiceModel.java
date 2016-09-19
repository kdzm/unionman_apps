package com.um.tv.menu.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.impl.CusPictureImpl;
import com.hisilicon.android.tvapi.impl.CusSourceManagerImpl;
import com.um.tv.menu.R;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.ShellUtils;
import com.um.tv.menu.utils.SystemUtils;
import com.um.tv.menu.utils.Utils;
import com.um.tv.menu.utils.PropertyUtils;
import com.hisilicon.android.tvapi.UmtvManager;
import com.hisilicon.android.tvapi.CusSourceManager;
import com.hisilicon.android.tvapi.constant.EnumSignalStat;
import com.hisilicon.android.tvapi.constant.EnumSoundChannel;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;

public class ChoiceModel extends FunctionModel {
    private static final String TAG = "ChoiceModel";
    public static final int TypeADCSource = 0;
    public static final int TypeWhiteBlanceSource = 1;
    public static final int TypeOverscanSource = 2;
    public static final int TypePictureModeSource = 3;
    public static final int TypeNonLinearSource = 4;
    public static final int TypePowerOnMode = 5;
    public static final int Type3DDetectLevel = 6;
    public static final int TypeTestPattern = 7;
    public static final int TypeInitialChannel = 8;
    public static final int TypeWhiteBlanceColorTemp = 9;
    public static final int TypeNRThreshold = 10;
    public static final int TypeAVCThreshold = 11;
    public static final int TypeHiDEVType = 12;
    public static final int TypeWatchDog = 13;
    public static final int TypeUartEnable = 14;
    public static final int TypeUartDebug = 15;
    public static final int TypeAgingMode = 16;
    public static final int TypePanelIndex = 17;
    public static final int TypePanelFlipMirror = 18;
    public static final int TypePowerMusic = 19;
    public static final int TypeLvbsEnable = 20;
    public static final int TypeLvbsSpreadEnable = 21;
    public static final int TypeDdrSpreadEnable = 22;
    public static final int TypeVBOEnable = 23;
    public static final int TypeVBOSpreadEnable = 24;
    public static final int TypeGMACEnable = 25;
    public static final int TypeGMACSpreadEnable = 26;
    public static final int TypeUSBSpreadEnable = 27;
    public static final int TypePLLEnable = 28;
    public static final int TypeCIEnable = 29;
    public static final int TypeVDACEnable = 30;
    public static final int TypeOverModulation = 31;
    public static final int TypePicModeTvSource = 32;
    public static final int TypeVBOEmphasis = 33;
    public static final int TypePWMOffset = 34;
    public static final int TypeTunerLNA = 35;
    public static final int TypeDdrRefresh = 36;

    public String[] mChoices = null;
    public int mCurrentIndex = 0;

    private ImageView mIvPrevious = null;
    private ImageView mIvNext = null;

    private List<SourceChangeListener> mSourceListenerList = new ArrayList<ChoiceModel.SourceChangeListener>();

    private int mType = 0;

    public ChoiceModel(Context context, FactoryWindow window, CusFactory factory, int type) {
        super(context, window, factory);
        // TODO Auto-generated constructor stub
        mType = type;
        init();
    }

    @Override
    public View getView(Context context, int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null || !convertView.getTag().equals(ViewTagChoice)) {
            convertView = LayoutInflater.from(context).inflate(R.layout.choice_layout, null);
            convertView.setTag(ViewTagChoice);
        }
        TextView tvName = (TextView)convertView.findViewById(R.id.tv_name);
        tvName.setText(mName);
        TextView tvValue = (TextView)convertView.findViewById(R.id.tv_value);
        tvValue.setText(mChoices[mCurrentIndex]);
        mIvPrevious = (ImageView)convertView.findViewById(R.id.iv_previous);
        mIvNext = (ImageView)convertView.findViewById(R.id.iv_next);

        refreshArrowStatus();
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub
        boolean changed = false;
        if (direct < 0) {
            mCurrentIndex--;
            if (mCurrentIndex < 0) {
                mCurrentIndex = 0;
            } else {
                changed = true;
            }
        } else if (direct > 0) {
            mCurrentIndex++;
            if (mCurrentIndex >= mChoices.length) {
                mCurrentIndex = mChoices.length - 1;
            } else {
                changed = true;
            }
        }

        if (changed) {
            switch (mType) {
            case TypePicModeTvSource:
                int sourceId = Utils.SourcePicModeTvSourceValue[mCurrentIndex];
                CusSourceManagerImpl.getInstance().deselectSource(CusSourceManagerImpl.getInstance().getCurSourceId(0), true);
                CusSourceManagerImpl.getInstance().selectSource(sourceId, 0);
                break;
            case TypePictureModeSource:
                int picMode = Utils.SourcePictureModeValue[mCurrentIndex];
                CusPictureImpl.getInstance().setPictureMode(picMode);
                break;
            case TypePowerOnMode:
                mFactory.setPoweronMode(Utils.SourcePowerOnModeValue[mCurrentIndex]);
                break;
            case TypeTestPattern:
                mFactory.setTestPattern(Utils.SourceTestPatternValue[mCurrentIndex]);
                break;
            case TypeNRThreshold:
                mFactory.setNRThreshold(Utils.SourceNRThresholdValue[mCurrentIndex]);
                break;
            case TypeAVCThreshold:
                mFactory.setAVCThreshold(Utils.SourceAVCThresholdvalue[mCurrentIndex]);
                break;
            case TypeHiDEVType:
               // mFactory.setHiDEVType(Utils.SourceHiDEVTypeValue[mCurrentIndex]);
                break;
            case TypeWatchDog:
                mFactory.enableWdt(Utils.SourceWatchDogValue[mCurrentIndex]);
                break;
            case TypeUartEnable:
              //  m Factory.enableUart(Utils.SourceUartValue[mCurrentIndex]);
                break;
            case TypeUartDebug:
               // mF actory.enableUartDebug(Utils.SourceUartDebugValue[mCurrentIndex]);
                break;
            case TypeAgingMode:
                mFactory.enableAgingMode(Utils.SourceAgingModeValue[mCurrentIndex]);
                break;
            case TypePanelIndex:
                mFactory.setPanelIndex(Utils.SourcePanelIndexValue[mCurrentIndex]);
                break;
            case TypePanelFlipMirror:
                mFactory.setPanelFlipMirror(Utils.SourcePanelFlipMirrorValue[mCurrentIndex]);
                break;
            case TypePowerMusic:
                mFactory.enablePowerMusic(Utils.SourcePowerMusicValue[mCurrentIndex]);
                break;
            case TypeLvbsEnable:
                mFactory.enableLvds(Utils.SourceLvbsEnableValue[mCurrentIndex]);
                break;
            case TypeLvbsSpreadEnable:
                mFactory.enableLvdsSpread(Utils.SourceLvbsSpreadEnableValue[mCurrentIndex]);
                break;
            case TypeDdrSpreadEnable:
                mFactory.enableDdrSpread(Utils.SourceDdrSpreadEnableValue[mCurrentIndex]);
                break;

            case TypeVBOEnable:
                mFactory.enableVBOClk(Utils.SourceVBOEnableValue[mCurrentIndex]);
                break;
            case TypeVBOSpreadEnable:
                mFactory.enableVBOSpread(Utils.SourceVBOSpreadEnableValue[mCurrentIndex]);
                break;
            case TypeVBOEmphasis:
                mFactory.setVBOEmphasis(Utils.SourceVBOEmphasisValue[mCurrentIndex]);
                break;
            case TypeUSBSpreadEnable:
                mFactory.enableUSB3Spread(Utils.SourceUSBSpreadEnableValue[mCurrentIndex]);
                break;

            case TypeGMACEnable:
//                mFactory.enableGmacClk(Utils.SourceGMACEnableValue[mCurrentIndex]);
                break;
            case TypeGMACSpreadEnable:
                mFactory.enableGmacClk(Utils.SourceGMACEnableValue[mCurrentIndex]);
                mFactory.enableGmacSpread(Utils.SourceGMACSpreadEnableValue[mCurrentIndex]);
                break;

            case TypePLLEnable:
                mFactory.enablePLL(Utils.SourcePLLEnableValue[mCurrentIndex]);
                break;

            case TypeCIEnable:
                mFactory.enableCiClk(Utils.SourceCIEnableValue[mCurrentIndex]);
                break;

            case TypeVDACEnable:
                mFactory.enableVDAC(Utils.SourceVDACEnableValue[mCurrentIndex]);
                break;

            case TypeOverModulation:
                mFactory.setOverModulation(Utils.SourceOverModulationValue[mCurrentIndex]);
                break;
            case TypePWMOffset:
                Utils.intallPWMOffset();
                System.out.println(" Utils.SourcePWMOffsetValue[mCurrentIndex] =  "+Utils.SourcePWMOffsetValue[mCurrentIndex]+"  mCurrentIndex = "+mCurrentIndex);
                mFactory.setPwmOffset(Utils.SourcePWMOffsetValue[mCurrentIndex]);
                break;
            case TypeTunerLNA:
            	tunerLNASwitch(Utils.TunerLNASwitch[mCurrentIndex]);
                break;
            case TypeDdrRefresh:
                mFactory.setSuspendMode(Utils.SourceDdrRefreshValue[mCurrentIndex]);
                break;
            default:
                break;
            }
            initSourceDate();
            mWindow.invalidateViews();
            refreshArrowStatus();
        }
    }
    
    public void tunerLNASwitch(boolean bOnOff){
    	Log.d(TAG, "tunerLNA Switch "+ bOnOff);
    	int curid = UmtvManager.getInstance().getSourceManager().getSelectSourceId();
    	int bitVal = 0;
    	//int channelVal = PropertyUtils.getInt("persist.sys.tunerLNA", 0);
    	int channelVal = mFactory.getTunerLNA();
		
    	if(EnumSourceIndex.SOURCE_ATV == curid){

		}else if(EnumSourceIndex.SOURCE_DVBC == curid){
			
		}else if(EnumSourceIndex.SOURCE_DTMB == curid){
			
		}
    	
    	/*Assume that ATV DVBC DTMB has 8 status ATV 0,1; DVBC 0,2; DTMB 0,4 */
    	/*ATV & DVBC 0, 1, 2, 3 etc.*/
    	Log.d(TAG, "tunerLNA channelVal is " + channelVal);
    	if(bOnOff){
    		bitVal = 1 << curid;
    		channelVal |= bitVal;
    		SystemUtils.shellExecute("echo LNA on > /proc/msp/tuner");
    	}else{
    		bitVal = 7 - (1 << curid);
    		channelVal &= bitVal;
    		SystemUtils.shellExecute("echo LNA off > /proc/msp/tuner");	
    	}

    	//PropertyUtils.setInt("persist.sys.tunerLNA", channelVal);
    	mFactory.setTunerLNA(channelVal);
    	Log.d(TAG, "tunerLNA channelVal is " + channelVal);
    	
    }

    public void initSourceDate(){
        for(SourceChangeListener listener : mSourceListenerList){
            notifySourceChanged(listener, mCurrentIndex);
        }
    }
    public void registeSourceChangeListener(SourceChangeListener listener) {
        mSourceListenerList.add(listener);
    }

    public void unregisterSourceChangeListener(SourceChangeListener listener) {
        mSourceListenerList.remove(listener);
    }

    public void refreshArrowStatus() {
        if(mIvPrevious != null && mIvNext != null){
            mIvPrevious.setEnabled(mCurrentIndex > 0);
            mIvNext.setEnabled(mCurrentIndex < mChoices.length - 1);
        }
    }

    public interface SourceChangeListener {
        void onSourceChanged(int srcId);
    }

    private void notifySourceChanged(SourceChangeListener listener, int sourceId) {
        int id = sourceId;
        switch (mType) {
            case TypeADCSource:
                id = Utils.SourceADCValue[sourceId];
                break;
            case TypeWhiteBlanceSource:
                id = Utils.SourceWBValue[sourceId];
                break;
            case TypeOverscanSource:
                id = Utils.SourceOverscanValue[sourceId];
                //SourceManagerImpl.getInstance().selectSource(id, 0);
                break;
            case TypeNonLinearSource:
                id = Utils.SourceNonLinearValue[sourceId];
                break;
            case TypeWhiteBlanceColorTemp:
                id = Utils.SourceWhiteBalanceColorTempValue[sourceId];
                //Log.d(TAG," TypeWhiteBlanceColorTemp mCurrentIndex "+mCurrentIndex);
                //PictureImpl.getInstance().setColorTemp(mCurrentIndex);
                break;
            default:
                break;
        }
        listener.onSourceChanged(id);
    }

    @Override
    public void init() {
        switch (mType) {
            case TypeADCSource:
                mChoices = Utils.SourceADC;
                break;
            case TypeWhiteBlanceSource:
                mChoices = Utils.SourceWhiteBlance;
                break;
            case TypeOverscanSource:
                mChoices = Utils.SourceOverscan;
                initIndex(CusSourceManagerImpl.getInstance().getCurSourceId(0), Utils.SourceOverscanValue);
                break;
            case TypePicModeTvSource:
                mChoices = Utils.SourcePicModeTvSource;
                initIndex(CusSourceManagerImpl.getInstance().getCurSourceId(0),
                Utils.SourcePicModeTvSourceValue);
                break;
            case TypePictureModeSource:
                mChoices = Utils.SourcePictureMode;
                initIndex(CusPictureImpl.getInstance().getPictureMode(), Utils.SourcePictureModeValue);
                break;
            case TypeNonLinearSource:
                mChoices = Utils.SourceNonLinear;
                break;
            case TypePowerOnMode:
                mChoices = Utils.SourcePowerOnMode;
                initIndex(mFactory.getPoweronMode(), Utils.SourcePowerOnModeValue);
                break;
            case Type3DDetectLevel:
                mChoices = Utils.Source3DDetectLevel;
                break;
            case TypeTestPattern:
                mChoices = Utils.SourceTestPattern;
                initIndex(mFactory.getTestPattern(), Utils.SourceTestPatternValue);
                break;
            case TypeInitialChannel:
                mChoices = Utils.SourceInitialChannel;
                break;
            case TypeWhiteBlanceColorTemp:
                mChoices = Utils.SourceWhiteBalanceColorTemp;
                break;
            case TypeNRThreshold:
                mChoices = Utils.SourceNRThreshold;
                initIndex(mFactory.getNRThreshold(), Utils.SourceNRThresholdValue);
                break;
            case TypeAVCThreshold:
                mChoices = Utils.SourceAVCThreshold;
                initIndex(mFactory.getAVCThreshold(), Utils.SourceAVCThresholdvalue);
                break;
            case TypeHiDEVType:
                mChoices = Utils.SourceHiDEVType;
                break;
            case TypeWatchDog:
                mChoices = Utils.SourceWatchDog;
                mCurrentIndex = mFactory.isWdtEnable() ? 1 : 0;
                break;
            case TypeUartEnable:
                mChoices = Utils.SourceUart;
                mCurrentIndex = mFactory.isUartEnable() ? 1 : 0;
                break;
            case TypeUartDebug:
                mChoices = Utils.SourceUartDebug;
                mCurrentIndex = 0;//mFactory.isUartDebugEnable() ? 1 : 0;
                break;
            case TypeAgingMode:
                mChoices = Utils.SourceAgingMode;
                mCurrentIndex = mFactory.isAgingModeEnable() ? 1 : 0;
                break;
            case TypePanelIndex:
                mChoices = Utils.SourcePanelIndex;
                initIndex(mFactory.getPanelIndex(), Utils.SourcePanelIndexValue);
                break;
            case TypePanelFlipMirror:
                mChoices = Utils.SourcePanelFlipMirror;
                initIndex(mFactory.getPanelFlipMirror(), Utils.SourcePanelFlipMirrorValue);
                break;
            case TypePowerMusic:
                mChoices = Utils.SourcePowerMusic;
                mCurrentIndex = mFactory.isPowerMusicEnable() ? 1 : 0;
                break;
            case TypeLvbsEnable:
                mChoices = Utils.SourceLvbsEnable;

                break;
            case TypeLvbsSpreadEnable:
                mChoices = Utils.SourceLvbsSpreadEnable;
                mCurrentIndex = mFactory.isLvdsSpreadEnable() ? 1 : 0;
                break;

            case TypeDdrSpreadEnable:
                mChoices = Utils.SourceDdrSpreadEnable;
                mCurrentIndex = mFactory.isDdrSpreadEnable() ? 1 : 0;
                break;

            case TypeVBOEnable:
                mChoices = Utils.SourceVBOEnable;
                mCurrentIndex = mFactory.isVBOClkEnable() ? 1 : 0;
                break;
            case TypeVBOSpreadEnable:
                mChoices = Utils.SourceVBOSpreadEnable;
                mCurrentIndex = mFactory.isVBOSpreadEnable() ? 1 : 0;
                break;

            case TypeVBOEmphasis:
                mChoices = Utils.SourceVBOEmphasis;
                initIndex(mFactory.getVBOEmphasis(), Utils.SourceVBOEmphasisValue);
                break;
            case TypeGMACEnable:
                mChoices = Utils.SourceGMACEnable;
                mCurrentIndex = mFactory.isGmacClkEnable() ? 1 : 0;
                break;
            case TypeGMACSpreadEnable:
                mChoices = Utils.SourceGMACSpreadEnable;
                mCurrentIndex = mFactory.isGmacSpreadEnable() ? 1 : 0;
                break;

            case TypeUSBSpreadEnable:
                mChoices = Utils.SourceUSBSpreadEnable;
                mCurrentIndex = mFactory.isUSB3SpreadEnable() ? 1 : 0;
                break;

            case TypePLLEnable:
                mChoices = Utils.SourcePLLEnable;
                mCurrentIndex = mFactory.isPLLEnable() ? 1 : 0;
                break;

            case TypeCIEnable:
                mChoices = Utils.SourceCIEnable;
                mCurrentIndex = mFactory.isCiClkEnable() ? 1 : 0;
                break;

            case TypeVDACEnable:
                mChoices = Utils.SourceVDACEnable;
                mCurrentIndex = mFactory.isVDACEnable() ? 1 : 0;
                break;

            case TypeOverModulation:
                mChoices = Utils.SourceOverModulation;
                initIndex(mFactory.getOverModulation(), Utils.SourceOverModulationValue);
                break;

            case TypePWMOffset:
                  Utils.intallPWMOffset();
                  mChoices = Utils.SourcePWMOffset;
                  System.out.println( " mFactory.getPwmOffset() = "+mFactory.getPwmOffset());
                  initIndex(mFactory.getPwmOffset(), Utils.SourcePWMOffsetValue);
                break;
            case TypeTunerLNA:
                mChoices = Utils.SourceWatchDog;
                //int channelVal = PropertyUtils.getInt("persist.sys.tunerLNA", 0);
                int channelVal = mFactory.getTunerLNA();
				Log.d(TAG, "channelVal "+ channelVal);
                int curid = UmtvManager.getInstance().getSourceManager().getSelectSourceId();
                int bitVal = channelVal & (1 << curid);
                mCurrentIndex = bitVal != 0 ? 1 : 0;
                Log.d(TAG, "tunerLNA "+ channelVal + " bitVal "+bitVal);
                break;
            case TypeDdrRefresh:
                mChoices = Utils.SourceDdrRefresh;
                initIndex(mFactory.getSuspendMode() > 1 ? 0 : mFactory.getSuspendMode(),
                        Utils.SourceDdrRefreshValue);
                break;
            default:
                break;
        }
    }

    private void initIndex(int currentValue, int[] values){
        if(values == null) return;
        for(int i = 0; i < values.length; i++){
            if(currentValue == values[i]){
                mCurrentIndex = i;
                break;
            }
        }
    }
}
