package com.unionman.umerrorinfo;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * @Description According to the code_type to get remind infos and show dialog.       
 * @author weiyi.nong
 * @email weiyi.nong@unionman.com.cn
 * @date 2016-6-20
 */
public class DialogActivity extends Activity {
	private TextView mSuggestTextView;
	private TextView mTipTextView;
	private TextView mCodeTextView;
	private String strTip = "";
	private String strSuggestion = "";
	private SharedPreferences sharedPreferences; 
	private Editor editor; 
	
	/**
	 * @Description When receiver close broadcast,finish this activty.
	 *
	 */
	public class Receiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent  intent) {
			String strAction = intent.getAction();
			if (strAction.equals(DataManager.CLOSE_ACTION)) {
                finish();
			}else if(strAction.equals(DataManager.DATA_UPDAPT_ACTION)){
				String strCodeType = intent.getStringExtra("code_type");
				if(!TextUtils.isEmpty(strCodeType)){
					initUI(strCodeType);
				}
			}
			
		} 
		
	} 
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		Intent intent = this.getIntent();
		String strCodeType = intent.getStringExtra("code_type");
		initView();
		initUI(strCodeType);
		registerBroadcastReceiver();
		aboutSharedPreferences();
	}

	/**
	 * @Description As soon as dialog activity show,save isDiaogShow flag as true.
	 *
	 */
	private void aboutSharedPreferences() {
		sharedPreferences= getSharedPreferences(DataManager.SharedPreferences_name, Context.MODE_PRIVATE);
		editor= sharedPreferences.edit();
		editor.putBoolean(DataManager.SP_FLAG_SHOW, true);
		editor.commit();
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		saveSP();
		
	}
	/**
	 * @Description While the  dialog activity is dismiss,save isDiaogShow flag as false.
	 *
	 */
	private void saveSP() {
		editor.putString(DataManager.SP_CODE_TYPE,"");
		editor.putBoolean(DataManager.SP_FLAG_SHOW, false);
		editor.commit();
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		saveSP();
	}
	
	private void registerBroadcastReceiver() {
		BroadcastReceiver receiver = new Receiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DataManager.DATA_UPDAPT_ACTION);
		intentFilter.addAction(DataManager.CLOSE_ACTION);
		registerReceiver(receiver, intentFilter);

	}

	
	private void initView() {
		mCodeTextView = ((TextView) findViewById(R.id.code_info));
		mTipTextView = ((TextView) findViewById(R.id.tip_info));
		mSuggestTextView = ((TextView) findViewById(R.id.suggest_info));
	}

	/**
	 * @Description Get remind infos according to the code_type.
	 * 
	 */
	private void initUI(String strCodeType) {
		
		if (strCodeType.equals("10000")) {
			showInfo(R.string.tip_10000, R.string.suggest_10000,strCodeType);

		} else if (strCodeType.equals("10001")) {
			showInfo(R.string.tip_10001, R.string.suggest_10001,strCodeType);

		} else if (strCodeType.equals("10010")) {
			showInfo(R.string.tip_10010, R.string.suggest_10010,strCodeType);

		} else if (strCodeType.equals("10030")) {
			showInfo(R.string.tip_10030, R.string.suggest_10030,strCodeType);

		} else if (strCodeType.equals("10040")) {
			showInfo(R.string.tip_10040, R.string.suggest_10040,strCodeType);

		} else if (strCodeType.equals("10041")) {

			showInfo(R.string.tip_10041, R.string.suggest_10041,strCodeType);
		} else if (strCodeType.equals("10043")) {
			showInfo(R.string.tip_10043, R.string.suggest_10043,strCodeType);

		} else if (strCodeType.equals("10044")) {
			showInfo(R.string.tip_10044, R.string.suggest_10044,strCodeType);

		} else if (strCodeType.equals("10045")) {
			showInfo(R.string.tip_10045, R.string.suggest_10045,strCodeType);

		} else if (strCodeType.equals("29001")) {
			showInfo(R.string.tip_29001, R.string.suggest_29001,strCodeType);

		} else if (strCodeType.equals("29002")) {
			showInfo(R.string.tip_29002, R.string.suggest_29002,strCodeType);

		} else if (strCodeType.equals("29003")) {
			showInfo(R.string.tip_29003, R.string.suggest_29003,strCodeType);

		} else if (strCodeType.equals("29004")) {
			showInfo(R.string.tip_29004, R.string.suggest_29004,strCodeType);

		} else if (strCodeType.equals("29005")) {
			showInfo(R.string.tip_29005, R.string.suggest_29005,strCodeType);

		} else if (strCodeType.equals("29006")) {
			showInfo(R.string.tip_29006, R.string.suggest_29006,strCodeType);

		} else if (strCodeType.equals("29007")) {
			showInfo(R.string.tip_29007, R.string.suggest_29007,strCodeType);

		} else if (strCodeType.equals("29008")) {
			showInfo(R.string.tip_29008, R.string.suggest_29008,strCodeType);

		} else if (strCodeType.equals("29009")) {
			showInfo(R.string.tip_29009, R.string.suggest_29009,strCodeType);

		} else if (strCodeType.equals("29010")) {
			showInfo(R.string.tip_29010, R.string.suggest_29010,strCodeType);

		} else if (strCodeType.equals("29011")) {
			showInfo(R.string.tip_29011, R.string.suggest_29011,strCodeType);

		} else if (strCodeType.equals("29012")) {
			showInfo(R.string.tip_29012, R.string.suggest_29012,strCodeType);

		} else if (strCodeType.equals("29013")) {
			showInfo(R.string.tip_29013, R.string.suggest_29013,strCodeType);

		} else if (strCodeType.equals("29014")) {
			showInfo(R.string.tip_29014, R.string.suggest_29014,strCodeType);

		} else if (strCodeType.equals("29015")) {
			showInfo(R.string.tip_29015, R.string.suggest_29015,strCodeType);

		} else if (strCodeType.equals("29016")) {
			showInfo(R.string.tip_29016, R.string.suggest_29016,strCodeType);

		} else if (strCodeType.equals("29017")) {
			showInfo(R.string.tip_29017, R.string.suggest_29017,strCodeType);

		} else if (strCodeType.equals("29018")) {
			showInfo(R.string.tip_29018, R.string.suggest_29018,strCodeType);

		} else if (strCodeType.equals("29019")) {
			showInfo(R.string.tip_29019, R.string.suggest_29019,strCodeType);

		} else if (strCodeType.equals("29020")) {
			showInfo(R.string.tip_29020, R.string.suggest_29020,strCodeType);

		} else if (strCodeType.equals("29021")) {
			showInfo(R.string.tip_29021, R.string.suggest_29021,strCodeType);

		} else if (strCodeType.equals("29022")) {
			showInfo(R.string.tip_29022, R.string.suggest_29022,strCodeType);

		} else if (strCodeType.equals("29023")) {
			showInfo(R.string.tip_29023, R.string.suggest_29023,strCodeType);

		}else if (strCodeType.equals("29024")) {
			showInfo(R.string.tip_29024, R.string.suggest_29024,strCodeType);

		}else if (strCodeType.equals("29025")) {
			showInfo(R.string.tip_29025, R.string.suggest_29025,strCodeType);

		}else if (strCodeType.equals("29026")) {
			showInfo(R.string.tip_29026, R.string.suggest_29026,strCodeType);

		}else if (strCodeType.equals("29027")) {
			showInfo(R.string.tip_29027, R.string.suggest_29027,strCodeType);

		}else if (strCodeType.equals("29028")) {
			showInfo(R.string.tip_29028, R.string.suggest_29028,strCodeType);

		}else if (strCodeType.equals("29029")) {
			showInfo(R.string.tip_29029, R.string.suggest_29029,strCodeType);

		}else if (strCodeType.equals("29030")) {
			showInfo(R.string.tip_29030, R.string.suggest_29030,strCodeType);

		}else if (strCodeType.equals("29031")) {
			showInfo(R.string.tip_29031, R.string.suggest_29031,strCodeType);

		}else if (strCodeType.equals("29032")) {
			showInfo(R.string.tip_29032, R.string.suggest_29032,strCodeType);

		}else if (strCodeType.equals("29033")) {
			showInfo(R.string.tip_29033, R.string.suggest_29033,strCodeType);

		}else if (strCodeType.equals("29034")) {
			showInfo(R.string.tip_29034, R.string.suggest_29034,strCodeType);

		}else if (strCodeType.equals("29035")) {
			showInfo(R.string.tip_29035, R.string.suggest_29035,strCodeType);
			
		}else if (strCodeType.equals("29036")) {
			showInfo(R.string.tip_29036, R.string.suggest_29036,strCodeType);

		}else if (strCodeType.equals("29037")) {
			showInfo(R.string.tip_29037, R.string.suggest_29037,strCodeType);

		}else if (strCodeType.equals("29038")) {
			showInfo(R.string.tip_29038, R.string.suggest_29038,strCodeType);

		}else if (strCodeType.equals("29039")) {
			showInfo(R.string.tip_29039, R.string.suggest_29039,strCodeType);

		}else if (strCodeType.equals("29040")) {
			showInfo(R.string.tip_29040, R.string.suggest_29040,strCodeType);

		}
	}

	/**
	 * @Description Set remind infos to views.
	 * 
	 */
	private void showInfo(int tipStringId, int suggestStringId,String strCodeType) {
		strTip = getString(tipStringId).trim();
		strSuggestion = getString(suggestStringId).trim();
		mCodeTextView.setText(getString(R.string.code_title) + " "
				+ strCodeType);
		mTipTextView.setText(strTip);
		mSuggestTextView.setText(strSuggestion);
	}
	
	
}
