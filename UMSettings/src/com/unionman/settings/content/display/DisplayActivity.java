package com.unionman.settings.content;

import java.lang.reflect.InvocationTargetException;
import android.R.integer;
import android.R.string;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.http.SslCertificate;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.custom.CustomDialog;
import com.unionman.settings.custom.CheckRadioButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
//import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.unionman.settings.R;
import com.unionman.settings.custom.CheckRadioButton;
import com.unionman.settings.layoutmanager.ConstantList;
import com.unionman.settings.layoutmanager.RightWindowBase;
import com.unionman.settings.tools.Logger;

import com.hisilicon.android.HiDisplayManager;

public class DisplayActivity extends RightWindowBase {

	private Position currentPosition = null;
	private RadioButton  rb_4k_25;
	private RadioButton  rb_4k_30;
	private RadioButton  rb_1080p_60;
	private RadioButton  rb_1080p_50;
	private RadioButton  rb_1080i_60;
	private RadioButton  rb_1080i_50;
	private RadioButton  rb_Pal;
	private RadioButton  rb_720p_60;
	private RadioButton  rb_720p_50;
	private RadioButton  rb_Ntsc;
	private RadioGroup radioGroup;
	private RadioGroup radioGroup2;

	private SeekBar sb_Left;
	private SeekBar sb_Right;
	private SeekBar sb_Top;
	private SeekBar sb_Bottom;

	private CheckRadioButton crb_Box;
	private CheckRadioButton crb_videobBox;

	private Handler handler = new Handler();
	private int[] modeValues = new int[] { 0, 1, 5, 6, 7, 8, 11, 14, 0x101,
			0x102 };
	private HiDisplayManager displayManager = null;
	private static final String TAG = "com.unionman.settings.content.display--DisplayActivity";

	private int currentmode;
	
	private CustomDialog localCustomDialog;
	
	public DisplayActivity(Context paramContext) {
		super(paramContext);
	}

	@Override
	public void initData() {

	}

	public void onInvisible() {

	}

	public void onResume() {
		if (crb_Box.isChecked()) {
			crb_Box.setChecked(false);
		}
		/*
		 * if(crb_videobBox.isChecked()){ crb_videobBox.setChecked(false); }
		 */
	}

	public void setId() {
		this.frameId = 7;
		this.levelId = 1001;

	}

	@Override
	public void setView() {
		Logger.i(TAG,"setView()--");

		this.layoutInflater.inflate(R.layout.display, this);
		// radioGroup = (RadioGroup) findViewById(R.id.RadioGroup);
		// radioGroup2 = (RadioGroup)findViewById(R.id.RadioGroup2);

		// 初始化button
		initRadioButton();

		// 设置选定的RadioBtn选项
		setDefaultRadioBtn();

		// 设置radioButton的点击事件
		setButtonListern();

		// 初始化seekBar
		initSeekBar();
		// 设置seekBar的初始化长度
		setDefaultSeekBar();
		// 设置seekBar的监听事件
		setOnSeekBarOnChange_fun();
		crb_Box = (CheckRadioButton) findViewById(R.id.checkBoxPic);
		crb_Box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(
					CheckRadioButton paramCheckRadioButton, boolean paramBoolean) {
				if (paramCheckRadioButton.isChecked()) {
					try {
						DisplayActivity.this.layoutManager
								.showLayout(ConstantList.FRAME_DISPLAY_PIC);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		});
		/*
		 * crb_videobBox = (CheckRadioButton)findViewById(R.id.checkBoxvideo);
		 * crb_videobBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		 * 
		 * @Override public void onCheckedChanged( CheckRadioButton
		 * paramCheckRadioButton, boolean paramBoolean) {
		 * if(paramCheckRadioButton.isChecked()){ try {
		 * DisplayActivity.this.layoutManager
		 * .showLayout(ConstantList.FRAME_DISPLAY_VIDEO);
		 * 
		 * } catch (Exception e) { e.printStackTrace(); }
		 * 
		 * }
		 * 
		 * } });
		 */
	}

	// 设置RadioButton的点击事件
	private void setButtonListern() {
		Logger.i(TAG,"setButtonListern()--");
		rb_1080p_50.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (displayManager == null) {
					displayManager = new HiDisplayManager();

				}
				rb_1080p_50.setChecked(true);
				rb_1080p_50.setFocusable(true);
				rb_4k_25.setChecked(false);
				rb_4k_30.setChecked(false);
				rb_1080p_60.setChecked(false);
				rb_720p_60.setChecked(false);
				rb_720p_50.setChecked(false);
				rb_Pal.setChecked(false);
				rb_Ntsc.setChecked(false);
				displayManager.setFmt(modeValues[1]);
				showDialog();
			}
		});
		rb_1080p_60.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (displayManager == null) {
					displayManager = new HiDisplayManager();

				}
				rb_1080p_60.setChecked(true);
				rb_1080p_60.setFocusable(true);
				rb_4k_25.setChecked(false);
				rb_4k_30.setChecked(false);
				rb_1080p_50.setChecked(false);
				rb_720p_60.setChecked(false);
				rb_720p_50.setChecked(false);
				rb_Pal.setChecked(false);
				rb_Ntsc.setChecked(false);
				displayManager.setFmt(modeValues[0]);
				showDialog();
			}
		});
		rb_720p_60.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (displayManager == null) {
					displayManager = new HiDisplayManager();

				}
				rb_720p_60.setChecked(true);
				rb_720p_60.setFocusable(true);
				rb_4k_25.setChecked(false);
				rb_4k_30.setChecked(false);
				rb_1080p_50.setChecked(false);
				rb_1080p_60.setChecked(false);
				rb_720p_50.setChecked(false);
				rb_Pal.setChecked(false);
				rb_Ntsc.setChecked(false);
				displayManager.setFmt(modeValues[4]);
				showDialog();
			}
		});
		rb_720p_50.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (displayManager == null) {
					displayManager = new HiDisplayManager();

				}
				rb_720p_50.setChecked(true);
				rb_720p_50.setFocusable(true);
				rb_4k_25.setChecked(false);
				rb_4k_30.setChecked(false);
				rb_1080p_50.setChecked(false);
				rb_1080p_60.setChecked(false);
				rb_720p_60.setChecked(false);
				rb_Pal.setChecked(false);
				rb_Ntsc.setChecked(false);
				displayManager.setFmt(modeValues[5]);
				showDialog();
			}
		});
		rb_Pal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (displayManager == null) {
					displayManager = new HiDisplayManager();

				}
				rb_Pal.setChecked(true);
				rb_Pal.setFocusable(true);
				rb_4k_25.setChecked(false);
				rb_4k_30.setChecked(false);
				rb_4k_25.setChecked(false);
				rb_4k_30.setChecked(false);
				rb_1080p_50.setChecked(false);
				rb_1080p_60.setChecked(false);
				rb_720p_60.setChecked(false);
				rb_720p_50.setChecked(false);
				rb_Ntsc.setChecked(false);
				displayManager.setFmt(modeValues[6]);
				showDialog();
			}
		});
		rb_Ntsc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (displayManager == null) {
					displayManager = new HiDisplayManager();

				}
				rb_Ntsc.setChecked(true);
				rb_Ntsc.setFocusable(true);
				rb_4k_25.setChecked(false);
				rb_4k_30.setChecked(false);
				rb_1080p_50.setChecked(false);
				rb_1080p_60.setChecked(false);
				rb_720p_60.setChecked(false);
				rb_720p_50.setChecked(false);
				rb_Pal.setChecked(false);
				displayManager.setFmt(modeValues[7]);
				showDialog();
			}
		});

		rb_4k_25.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(displayManager == null){
					displayManager = new HiDisplayManager();
					
				}
				rb_4k_25.setChecked(true);
				rb_4k_25.setFocusable(true);
				rb_Ntsc.setChecked(false);
				rb_4k_30.setChecked(false);
				rb_1080p_50.setChecked(false);
				rb_1080p_60.setChecked(false);
				rb_720p_60.setChecked(false);
				rb_720p_50.setChecked(false);
				rb_Pal.setChecked(false);
				displayManager.setFmt(modeValues[8]);
				showDialog();
			}
		}) ;

		rb_4k_30.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(displayManager == null){
					displayManager = new HiDisplayManager();
					
				}
				rb_4k_30.setChecked(true);
				rb_4k_30.setFocusable(true);
				rb_4k_25.setChecked(false);
				rb_Ntsc.setChecked(false);
				rb_1080p_50.setChecked(false);
				rb_1080p_60.setChecked(false);
				rb_720p_60.setChecked(false);
				rb_720p_50.setChecked(false);
				rb_Pal.setChecked(false);
				displayManager.setFmt(modeValues[9]);
				showDialog();
			}
		}) ;
	}

	// 初始化radioButton
	private void initRadioButton() {
		Logger.i(TAG,"initRadioButton()--");
		rb_4k_25 = (RadioButton) findViewById(R.id.rb_4k_25);
		rb_4k_30 = (RadioButton) findViewById(R.id.rb_4k_30);
		rb_1080p_50 = (RadioButton) findViewById(R.id.rb_1080p_50);
		rb_1080p_60 = (RadioButton) findViewById(R.id.rb_1080p_60);
		rb_720p_60 = (RadioButton) findViewById(R.id.rb_720p_60);
		rb_720p_50 = (RadioButton) findViewById(R.id.rb_720p_50);
		rb_Pal = (RadioButton) findViewById(R.id.rb_Pal);
		rb_Ntsc = (RadioButton) findViewById(R.id.rb_Ntsc);

	}

	// 设置seekBar的监听事件
	private void setOnSeekBarOnChange_fun() {
		Logger.i(TAG,"setOnSeekBarOnChange_fun()--");
		sb_Left.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (currentPosition == null) {
					currentPosition = getPotion();
				}
				currentPosition.left = progress;
				handler.post(new Runnable() {
					@Override
					public void run() {
						displayManager.setOutRange(currentPosition.left,
								currentPosition.top, currentPosition.right,
								currentPosition.bottom);
						displayManager.SaveParam();
					}
				});

			}
		});

		sb_Right.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (currentPosition == null) {
					currentPosition = getPotion();
				}
				currentPosition.right = progress;
				handler.post(new Runnable() {
					@Override
					public void run() {
						displayManager.setOutRange(currentPosition.left,
								currentPosition.top, currentPosition.right,
								currentPosition.bottom);
						displayManager.SaveParam();
					}
				});

			}
		});

		sb_Bottom.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (currentPosition == null) {
					currentPosition = getPotion();
				}
				currentPosition.bottom = progress;
				handler.post(new Runnable() {
					@Override
					public void run() {
						displayManager.setOutRange(currentPosition.left,
								currentPosition.top, currentPosition.right,
								currentPosition.bottom);
						displayManager.SaveParam();
					}
				});

			}
		});
		sb_Top.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (currentPosition == null) {
					currentPosition = getPotion();
				}
				currentPosition.top = progress;
				handler.post(new Runnable() {
					@Override
					public void run() {
						displayManager.setOutRange(currentPosition.left,
								currentPosition.top, currentPosition.right,
								currentPosition.bottom);
						displayManager.SaveParam();
					}
				});

			}
		});
	}

	// 设置seekBar的初始化长度
	private void setDefaultSeekBar() {
		Logger.i(TAG,"setDefaultSeekBar()--");
		currentPosition = getPotion();
		sb_Left.setProgress(currentPosition.left);
		sb_Right.setProgress(currentPosition.right);
		sb_Top.setProgress(currentPosition.top);
		sb_Bottom.setProgress(currentPosition.bottom);
	}

	// 设置默认的radioBtn
	private void setDefaultRadioBtn() {
		Logger.i(TAG,"setDefaultRadioBtn()--");
		// TODO Auto-generated method stub
		if (displayManager == null) {
			displayManager = new HiDisplayManager();
		}
		currentmode = displayManager.getFmt();
		Logger.i(TAG, "currentmode" + currentmode);
		// 1080p-60hz
		if (currentmode == 0) {
			rb_1080p_60.setChecked(true);
			rb_1080p_60.setFocusable(true);
			rb_4k_25.setChecked(false);
			rb_4k_30.setChecked(false);
			rb_Ntsc.setChecked(false);
			rb_1080p_50.setChecked(false);
			rb_720p_60.setChecked(false);
			rb_720p_50.setChecked(false);
			rb_Pal.setChecked(false);
		}
		// 720p-60hz
		if (currentmode == 7) {
			rb_720p_60.setChecked(true);
			rb_720p_60.setFocusable(true);
			rb_4k_25.setChecked(false);
			rb_4k_30.setChecked(false);
			rb_Ntsc.setChecked(false);
			rb_1080p_50.setChecked(false);
			rb_1080p_60.setChecked(false);
			rb_720p_50.setChecked(false);
			rb_Pal.setChecked(false);
		}
		// pal
		if (currentmode == 11) {
			rb_Pal.setChecked(true);
			rb_Pal.setFocusable(true);
			rb_4k_25.setChecked(false);
			rb_4k_30.setChecked(false);
			rb_Ntsc.setChecked(false);
			rb_1080p_50.setChecked(false);
			rb_1080p_60.setChecked(false);
			rb_720p_60.setChecked(false);
			rb_720p_50.setChecked(false);
		}
		// Ntsc
		if (currentmode == 14) {
			rb_Ntsc.setChecked(true);
			rb_Ntsc.setFocusable(true);
			rb_4k_25.setChecked(false);
			rb_4k_30.setChecked(false);
			rb_1080p_50.setChecked(false);
			rb_1080p_60.setChecked(false);
			rb_720p_60.setChecked(false);
			rb_720p_50.setChecked(false);
			rb_Pal.setChecked(false);
		}
		// 1080-50hz
		if (currentmode == 1) {
			rb_1080p_50.setChecked(true);
			rb_1080p_50.setFocusable(true);
			rb_4k_25.setChecked(false);
			rb_4k_30.setChecked(false);
			rb_Ntsc.setChecked(false);
			rb_1080p_60.setChecked(false);
			rb_720p_60.setChecked(false);
			rb_720p_50.setChecked(false);
			rb_Pal.setChecked(false);
		}
		// 720-50hz
		if (currentmode == 8) {
			rb_720p_50.setChecked(true);
			rb_720p_50.setFocusable(true);
			rb_4k_25.setChecked(false);
			rb_4k_30.setChecked(false);
			rb_Ntsc.setChecked(false);
			rb_1080p_50.setChecked(false);
			rb_1080p_60.setChecked(false);
			rb_720p_60.setChecked(false);
			rb_Pal.setChecked(false);
		}

		// 4k-25hz
		if (currentmode == 0x106) {
			rb_4k_25.setChecked(true);
			rb_4k_25.setFocusable(true);
			rb_4k_30.setChecked(false);
			rb_Ntsc.setChecked(false);
			rb_1080p_50.setChecked(false);
			rb_1080p_60.setChecked(false);
			rb_720p_60.setChecked(false);
			rb_720p_50.setChecked(false);
			rb_Pal.setChecked(false);
		}

		// 4k-30hz
		if (currentmode == 0x106) {
			rb_4k_30.setChecked(true);
			rb_4k_30.setFocusable(true);
			rb_4k_25.setChecked(false);
			rb_Ntsc.setChecked(false);
			rb_1080p_50.setChecked(false);
			rb_1080p_60.setChecked(false);
			rb_720p_60.setChecked(false);
			rb_720p_50.setChecked(false);
			rb_Pal.setChecked(false);
		}

	}

	private Position getPotion() {
		Logger.i(TAG,"getPotion()--");
		Position position_cur = new Position();
		if (displayManager == null) {
			displayManager = new HiDisplayManager();
		}
		Rect rect = displayManager.getOutRange();
		position_cur.width = 100;
		position_cur.height = 100;
		position_cur.left = rect.left;
		position_cur.top = rect.top;
		position_cur.right = rect.right;
		position_cur.bottom = rect.bottom;

		Logger.i(TAG, "position_cur.left" + position_cur.left);
		Logger.i(TAG, "position_cur.right" + position_cur.right);
		Logger.i(TAG, "position_cur.top" + position_cur.top);
		Logger.i(TAG, "position_cur.bottom" + position_cur.bottom);
		return position_cur;

	}

	public static class Position {

		public int bottom;
		public int right;
		public int top;
		public int left;
		public int height;
		public int width;

	}

	private void initSeekBar() {
		Logger.i(TAG,"initSeekBar()--");
		sb_Left = (SeekBar) findViewById(R.id.seekLeft);

		sb_Right = (SeekBar) findViewById(R.id.seekBarRight);

		sb_Top = (SeekBar) findViewById(R.id.seekBarTop);

		sb_Bottom = (SeekBar) findViewById(R.id.seekBarBottom);

	}
	
	private void showDialog() {
		Logger.i(TAG,"showDialog()--");

		localCustomDialog = new CustomDialog(this.context, -2, -2,
				R.layout.dialog_display, R.style.dialog);
		Button localButton1 = (Button) localCustomDialog
				.findViewById(R.id.reset_yes);
		Button localButton2 = (Button) localCustomDialog
				.findViewById(R.id.reset_no);
		localButton1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				handler.removeCallbacks(resetDiaplayTimer);
				localCustomDialog.dismiss();
				displayManager.SaveParam();
				currentmode = displayManager.getFmt();
			}
		});
		localButton2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramAnonymousView) {
				handler.removeCallbacks(resetDiaplayTimer);
				resetDisplay();
				localCustomDialog.dismiss();
			}
		});
		localCustomDialog.setCancelable(false);
		localCustomDialog.show();
		handler.postDelayed(resetDiaplayTimer, 6000);
	}
	
	Runnable resetDiaplayTimer=new Runnable() {
		
		@Override
		public void run() {
			localCustomDialog.dismiss();
			resetDisplay();
		}
	};
	
	private void resetDisplay(){

		Logger.i(TAG, "resetDisplay "+currentmode);
		displayManager.setFmt(currentmode);
		displayManager.SaveParam();
		setDefaultRadioBtn();
	}

}
