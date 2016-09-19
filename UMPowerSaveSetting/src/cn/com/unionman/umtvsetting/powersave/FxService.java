package cn.com.unionman.umtvsetting.powersave;

import java.text.DecimalFormat;
import java.util.ArrayList;


import cn.com.unionman.umtvsetting.powersave.interfaces.SystemSettingInterface;
import cn.com.unionman.umtvsetting.powersave.util.Util;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FxService extends Service 
{

	//���帡�����ڲ���
	RelativeLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    //���������������ò��ֲ���Ķ���
	WindowManager mWindowManager;
	
	private TextView mFloatView;
	
	private static final String TAG = "FxService";
	private ImageView imageView;
	private long begin = 0;
	
	private boolean flag = true;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
		    ArrayList<Integer> data  =	SystemSettingInterface.getBrightnessHistogram();
		    int sum = 0;
	    	int sumMultiplyIndex =0;
	    	for(int m=0;m<data.size();m++){
//	    		Log.i(TAG,m+"  ="+data.get(m));
	    		sum = sum + data.get(m);
	    		sumMultiplyIndex = sumMultiplyIndex +data.get(m)*(m+1);

	    	}
	    	double average = 0;
	    	String averageStr ="0";
            if(sum!=0){
            	average = (double)sumMultiplyIndex/(double)sum;
                DecimalFormat df = new DecimalFormat("#.0");
                averageStr = df.format(average);
            }
	    	Log.i(TAG,"sum="+sum+" sumMultiplyIndex ="+sumMultiplyIndex+" average="+average+" averageStr="+averageStr);	
	    	mFloatView.setText(averageStr);
			startAnimation(average);
		}
		
	};
	
	@Override
	public void onCreate() 
	{
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "oncreat");
		createFloatView();
        //Toast.makeText(FxService.this, "create FxService", Toast.LENGTH_LONG);		
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void createFloatView()
	{
		wmParams = new WindowManager.LayoutParams();
		//��ȡWindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
		//����window type
		wmParams.type = LayoutParams.TYPE_PHONE; 
		//����ͼƬ��ʽ��Ч��Ϊ����͸��
        wmParams.format = PixelFormat.RGBA_8888; 
        //���ø������ڲ��ɾ۽���ʵ�ֲ���������������ɼ�ڵĲ�����
        wmParams.flags = 
//          LayoutParams.FLAG_NOT_TOUCH_MODAL |
          LayoutParams.FLAG_NOT_FOCUSABLE
//          LayoutParams.FLAG_NOT_TOUCHABLE
          ;
        
        //��������ʾ��ͣ��λ��Ϊ����ö�
        wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM; 
        
        // ����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ
        wmParams.x = 30;
        wmParams.y = 30;

        /*// ������ڳ������
        wmParams.width = 200;
        wmParams.height = 80;*/
        
        //������ڳ������  
        wmParams.width = Util.dip2px(FxService.this, 400);
        Log.i(TAG,"wmParams.width="+wmParams.width);
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //��ȡ����������ͼ���ڲ���
        mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.float_layout, null);
    	imageView = (ImageView) mFloatLayout.findViewById(R.id.iv_needle);
    	
        //���mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        
        Log.i(TAG, "mFloatLayout-->left" + mFloatLayout.getLeft());
        Log.i(TAG, "mFloatLayout-->right" + mFloatLayout.getRight());
        Log.i(TAG, "mFloatLayout-->top" + mFloatLayout.getTop());
        Log.i(TAG, "mFloatLayout-->bottom" + mFloatLayout.getBottom());      
        
        //�������ڰ�ť
        mFloatView = (TextView)mFloatLayout.findViewById(R.id.float_id);
        
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth()/2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight()/2);
        
		new Thread() {

			@Override
			public void run() {
				super.run();
				while (flag) {
					try {
						sleep(1000);
						Message msg = new Message();
						handler.sendMessage(msg);

					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		}.start();
        //���ü�����ڵĴ����ƶ�
        mFloatView.setOnTouchListener(new OnTouchListener() 
        {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				// TODO Auto-generated method stub
				//getRawX�Ǵ���λ���������Ļ����꣬getX������ڰ�ť�����
				wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth()/2;
				//Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth()/2);
				Log.i(TAG, "RawX" + event.getRawX());
				Log.i(TAG, "X" + event.getX());
				//25Ϊ״̬���ĸ߶�
	            wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight()/2 - 25;
	           // Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredHeight()/2);
	            Log.i(TAG, "RawY" + event.getRawY());
	            Log.i(TAG, "Y" + event.getY());
	             //ˢ��
	            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
				return false;
			}
		});	
        
        mFloatView.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				Toast.makeText(FxService.this, "onClick", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mFloatLayout != null)
		{
			mWindowManager.removeView(mFloatLayout);
		}
		flag = false;
	}
	
	protected void startAnimation(double d) {
		AnimationSet animationSet = new AnimationSet(true);
		/**
		 * ǰ������������ת����ʼ�ͽ���Ķ��������������Բ�ĵ�λ��
		 */
		// Random random = new Random();
		int end = getDuShu(d);

		Log.i("", "********************begin:" + begin + "***end:" + end);
		RotateAnimation rotateAnimation = new RotateAnimation(begin, end, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f);
		rotateAnimation.setDuration(1000);
		animationSet.addAnimation(rotateAnimation);
		imageView.startAnimation(animationSet);
		begin = end;
	}
	
	public int getDuShu(double number) {
		double a = 0;
		if (number!=0){
			a = number/32*180;
		}
		return (int) a;
	}
}
