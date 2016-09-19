
package com.um.gallery3d.app;

import java.io.File;

import android.R.color;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.um.gallery3d.R;
import com.um.gallery3d.ui.GLRootView;
import com.um.gallery3d.ui.GLView.PICTURE_STATE;
import com.um.gallery3d.util.FileUtil;
import com.um.gallery3d.util.CustomToast;

public class SettingDialog extends Dialog {
	
	public static final int DISPEAR_TIME_30s = 30000;
	
	private static final int DIALOG_DISMISS = 0;
	private static final int SET_DELAY = 1;

    private Context context;

    private ListView settings;

    private SettingAdapter settingsAdapter;

    private SettingAdapter slideAdapter;

    private GLRootView mGLRootView;

    private int slidetime = 0;

    private int slidemode = 0;
    
    private String picpath;
    

    public String getPicpath() {
		return picpath;
	}

	public void setPicpath(String picpath) {
		this.picpath = picpath;
	}

	private int storeValues[] = {
            0, 0
    };
    
    
    final static int active = 1;
    
    final static int unactive = 0;
    
    View view;
    
    private String values[][] = new String[6][2];

    private SharedPreferences share = null;

    private Editor editor = null;

    public SettingDialog(Context context, GLRootView mGLRootView) {
        //super(context);
        super(context, R.style.dialog);
        this.context = context;
        this.mGLRootView = mGLRootView;
        share = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
        editor = share.edit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //context.setTheme(R.style.dialog);
        setContentView(R.layout.menu_view);
        view=findViewById(R.id.menu_containerlayout) ;
        settings = (ListView) findViewById(R.id.menuview_list);
        settingsAdapter = new SettingAdapter(context.getResources().getStringArray(
                R.array.picturesetting));
        settings.setAdapter(settingsAdapter);
        settingsAdapter.setPicStatus(active);
        slidetime = share.getInt("slidetime", 0);
        slidemode = share.getInt("slidemode", 0);
        storeValues[0] = slidetime;
        storeValues[1] = slidemode;

        values[0] = context.getResources().getStringArray(R.array.slidetime);
        values[1] = context.getResources().getStringArray(R.array.slidemode);

        slideAdapter = new SettingAdapter(context.getResources().getStringArray(R.array.slide),
                values, storeValues);
        setListener();
        delay();
    }
    
    private void setDialogContent(int textid, int showTime){
      final CustomToast myToast=new CustomToast(context);
  	  myToast.setMessage(textid);
  	  myToast.showTime(showTime);
  	  myToast.show();
    }

		private void setListener() {
			
			settings.setOnKeyListener(new android.view.View.OnKeyListener() {
				@Override
				public boolean onKey(View arg0, int keycode, KeyEvent event) {
					// TODO Auto-generated method stub
	 				
				        if (event.getAction() == KeyEvent.ACTION_DOWN) {
				        
				        	if((keycode==KeyEvent.KEYCODE_DPAD_UP || keycode==KeyEvent.KEYCODE_DPAD_DOWN) &&  settingsAdapter.getPicStatus()==active){
				        		delay();
				        	}
				        	
				        	if((keycode==KeyEvent.KEYCODE_DPAD_UP || keycode==KeyEvent.KEYCODE_DPAD_DOWN || keycode==KeyEvent.KEYCODE_DPAD_LEFT|| keycode==KeyEvent.KEYCODE_DPAD_RIGHT ) &&  slideAdapter.getPicStatus()==active){
				        		settingsdelay();
				        	
				        	}
				        	
				        	
				        int position = settings.getSelectedItemPosition();
						
						switch (keycode) {
						
							case KeyEvent.KEYCODE_DPAD_CENTER:
								
				                if (settingsAdapter.getPicStatus() == active) {
				                	mHandler.removeMessages(DIALOG_DISMISS);
				                	
				                	Log.i("getview", "settingsAdapterosition:" + position);
				                    switch (position) {
				                    
				                        case 0:
				                        	view.setVisibility(View.INVISIBLE);
				                        	FileUtil util = new FileUtil(context);
				                        	File PicFILE = new File(picpath);
				                            util.showFileInfo(PicFILE);
				                            util.getaDialog().setOnDismissListener(new OnDismissListener() {
												
												@Override
												public void onDismiss(DialogInterface arg0) {
													// TODO Auto-generated method stub
													view.setVisibility(View.VISIBLE);
													delay();
												}
											});
				                         
				                            break;
				                        case 1:
				                            mGLRootView.setState(PICTURE_STATE.ROTATE);
				                            setDialogContent(R.string.rotateinfo, 3000);
				                            dismiss();
				                            break;
				                        case 2:
				                  
				                            settings.setAdapter(slideAdapter);
				                            slideAdapter.setPicStatus(active);
				                            settingsAdapter.setPicStatus(unactive);
				                            settingsdelay();
				                            break;
				                          
				                    }
				                } 
				                
				                break;
			            
							case KeyEvent.KEYCODE_DPAD_LEFT:
							
									if (slideAdapter.getPicStatus() ==  active) {
										
										Log.i("getview", "settingsAdapterosition:" + position);
										Log.i("getview", "settingsAdapterslidetime:" + slidetime);
										Log.i("getview", "slideAdapterosition:" + position);
										Log.i("getview", "slideAdapterslidetime:" + slidemode);
					                    switch (position) {
					                        case 0:
					                        	
					                        	if(slidetime == 0)
					                        	{
					                        		slidetime=slidetime+5;
					                        	}
					                        	else{
					                        		slidetime--;
					                        		}
					                        	
					                            slidetime %= 6;
					                            editor.putInt("slidetime", slidetime);
					                            editor.commit();
					                            storeValues[0] = slidetime;
					                            slideAdapter = new SettingAdapter(context.getResources()
					                                    .getStringArray(R.array.slide), values, storeValues);
					                            settings.setAdapter(slideAdapter);
					                            settingsAdapter.setPicStatus(unactive);
					                            slideAdapter.setPicStatus(active);
					                            slideAdapter.notifyDataSetChanged();
					                            
					                            break;
					                        case 1:
					                        	
					                        	slidemode++;
					                        	
					                            slidemode %= 2;
					                            editor.putInt("slidemode", slidemode);
					                            editor.commit();
					                            storeValues[1] = slidemode;
					                            slideAdapter = new SettingAdapter(context.getResources()
					                                    .getStringArray(R.array.slide), values, storeValues);
					                            settings.setAdapter(slideAdapter);
					                            settingsAdapter.setPicStatus(unactive);
					                            slideAdapter.setPicStatus(active);
					                            slideAdapter.notifyDataSetChanged();
					                            settings.setSelection(position);
					                            break;
					                    }
					                }
									
								break;	
			
								case KeyEvent.KEYCODE_DPAD_RIGHT:
			
										if (slideAdapter.getPicStatus() ==  active) {
											
						                    switch (position) {
						                        case 0:  

						                        	slidetime++;                  	
						                            slidetime %= 6;
						                            editor.putInt("slidetime", slidetime);
						                            editor.commit();
						                            storeValues[0] = slidetime;
						                            slideAdapter = new SettingAdapter(context.getResources()
						                                    .getStringArray(R.array.slide), values, storeValues);
						                            settings.setAdapter(slideAdapter);
						                            settingsAdapter.setPicStatus(unactive);
						                            slideAdapter.setPicStatus(active);
						                            slideAdapter.notifyDataSetChanged();
						                            
						                            break;
						                        case 1:
						                        	
						                        	slidemode++;
						                        	
						                        	
						                            slidemode %= 2;
						                            editor.putInt("slidemode", slidemode);
						                            editor.commit();
						                            storeValues[1] = slidemode;
						                            slideAdapter = new SettingAdapter(context.getResources()
						                                    .getStringArray(R.array.slide), values, storeValues);
						                            settings.setAdapter(slideAdapter);
						                            settingsAdapter.setPicStatus(unactive);
						                            slideAdapter.setPicStatus(active);
						                            slideAdapter.notifyDataSetChanged();
						                            settings.setSelection(position);
						                            break;
						                    }
						                }
											
									break;	
								case KeyEvent.KEYCODE_BACK:
									
									if (slideAdapter.getPicStatus() ==  active){
										
										settings.setAdapter(settingsAdapter);
										settings.setSelection(2);
			                            slideAdapter.setPicStatus(unactive);
			                            settingsAdapter.setPicStatus(active);
			                            delay();
			                            return true;
									}
									
						}
					 }
				
					return false;
				}
	
	 				});
	}

    private class SettingAdapter extends BaseAdapter {

        private String[] contents;

        private String[][] values;

        private int[] storeValues;

        private TextView key;

        private TextView value;
        
        private ImageView leftimg;
        
        private ImageView rightimg;
        
        private int picStatus = unactive;

        public SettingAdapter(String[] contents) {
            this.contents = contents;
        }

        public SettingAdapter(String[] contents, String[][] values, int[] storeValues) {
            this.contents = contents;
            this.values = values;
            this.storeValues = storeValues;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return contents.length;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return contents[arg0];
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View view, ViewGroup arg2) {
            // TODO Auto-generated method stub
            view = LayoutInflater.from(context).inflate(R.layout.menu_view_option, null);
            key = (TextView) view.findViewById(R.id.setting_option_item_txt);
            value = (TextView) view.findViewById(R.id.setting_option_item_val);
            leftimg =(ImageView)view.findViewById(R.id.left_arrow_img);
            rightimg =(ImageView)view.findViewById(R.id.right_arrow_img);
            
            key.setText(contents[position]);
            if (values != null) {
                Log.i("Animation", "position:" + position + values[position][storeValues[position]]);
                value.setText(values[position][storeValues[position]]);
                value.setVisibility(View.VISIBLE);
                
            }else{
            	leftimg.setBackgroundColor(color.transparent);
            	rightimg.setBackgroundColor(color.transparent);
            }
            return view;
        }

		public int getPicStatus() {
			return picStatus;
		}

		public void setPicStatus(int picStatus) {
			this.picStatus = picStatus;
		}
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        settings.setAdapter(settingsAdapter);
        settingsAdapter.setPicStatus(active);
        slideAdapter.setPicStatus(unactive);
        delay();
    }
    
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DIALOG_DISMISS:           
                dismiss();
                break;
            case SET_DELAY:           
            	settings.setAdapter(settingsAdapter);
				settings.setSelection(2);
                slideAdapter.setPicStatus(unactive);
                settingsAdapter.setPicStatus(active);
                delay();
                break;
            default:
                break;
            }
        }
    };
    public void delay() {
       mHandler.removeMessages(DIALOG_DISMISS);
       mHandler.sendEmptyMessageDelayed(DIALOG_DISMISS, DISPEAR_TIME_30s);
    }
    
    public void settingsdelay() {
        mHandler.removeMessages(SET_DELAY);
        mHandler.sendEmptyMessageDelayed(SET_DELAY, DISPEAR_TIME_30s);
     }

}
