package com.unionman.settings.content;

import android.content.Context;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.LayoutManager;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.hisilicon.android.HiDisplayManager;
import com.unionman.settings.R;
import com.unionman.settings.tools.Logger;


public class DisplayPic extends RightWindowBase {
	
	
	private SeekBar sb_brightness;
	private SeekBar sb_hue;
	private SeekBar sb_contrast;
	private SeekBar sb_saturation;
	
    private int init_brightness = 50;
    private int init_hue = 50;
    private int init_contrast = 50;
    private int init_saturation = 50;
    private Handler handler;
    private HiDisplayManager display_manager = null;

	private static final String TAG = "com.unionman.settings.content.display--DisplayPic";
	public DisplayPic(Context paramContext) {
		super(paramContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInvisible() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setId() {
		this.frameId = ConstantList.FRAME_DISPLAY_PIC;
		this.levelId = 1002;

	}

	@Override
	public void setView() {
		Logger.i(TAG,"setView()--");
		this.layoutInflater.inflate(R.layout.display_pic, this);
		initSeekBar();
		getSeekBarDefaultSize();
		setSeekbarChangeListen();
	}

	private void getSeekBarDefaultSize() {
		Logger.i(TAG,"getSeekBarDefaultSize()--");
		if(display_manager == null){
			display_manager = new HiDisplayManager();
		}
		  init_brightness = display_manager.getBrightness();
	        if(init_brightness < 0 || init_brightness > 100)
	        {
	            init_brightness = 50;
	        }

	        init_hue = display_manager.getHue();
	        if(init_hue < 0 || init_hue > 100)
	        {
	            init_hue= 50;
	        }

	        init_contrast = display_manager.getContrast();
	        if(init_contrast < 0 || init_contrast > 100)
	        {
	            init_contrast = 50;
	        }

	        init_saturation = display_manager.getSaturation();
	        if(init_saturation < 0 || init_saturation > 100)
	        {
	            init_saturation = 50;
	        }
		sb_brightness.setProgress(init_brightness);
		sb_hue.setProgress(init_hue);
		sb_contrast.setProgress(init_contrast);
		sb_saturation.setProgress(init_saturation);
	}

	private void setSeekbarChangeListen() {
		Logger.i(TAG,"setSeekbarChangeListen()--");
		handler = new Handler();
		sb_brightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {					
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(progress > 100){progress = 100;}
				if(progress <0 ){progress = 0;}
				init_brightness = progress;
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						if(display_manager == null){
							display_manager = new HiDisplayManager();
						}
						  display_manager.setBrightness(init_brightness);
						  display_manager.SaveParam();
					}
				});
			}
		});
		sb_hue.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {					
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(progress > 100){progress = 100;}
				if(progress <0 ){progress = 0;}
				init_hue = progress;
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						if(display_manager == null){
							display_manager = new HiDisplayManager();
						}
						 display_manager.setHue(init_hue);
						 display_manager.SaveParam();
					}
				});
			}
		});
		sb_contrast.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {					
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(progress > 100){progress = 100;}
				if(progress <5 ){progress = 5;}
				init_contrast = progress;
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						if(display_manager == null){
							display_manager = new HiDisplayManager();
						}
						 display_manager.setContrast(init_contrast);
						 display_manager.SaveParam();
					}
				});
			}
		});
		sb_saturation.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {					
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(progress > 100){progress = 100;}
				if(progress <0 ){progress = 0;}
				init_saturation = progress;
				
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						if(display_manager == null){
							display_manager = new HiDisplayManager();
						}
						  display_manager.setSaturation(init_saturation);
						  display_manager.SaveParam();
					}
				});
			}
		});
		
	}

	private void initSeekBar() {
		Logger.i(TAG,"initSeekBar()--");
		sb_brightness = (SeekBar) findViewById(R.id.seekb);
		sb_brightness.requestFocus();
		sb_hue = (SeekBar) findViewById(R.id.seekh);
		sb_contrast = (SeekBar) findViewById(R.id.seekc);
		sb_saturation = (SeekBar) findViewById(R.id.seeks);
		
	}

}
