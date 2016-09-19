package com.unionman.settings.custom;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.unionman.settings.R;
import com.unionman.settings.tools.UMDebug;

public class CheckRadioButton extends LinearLayout
  implements View.OnFocusChangeListener
{
	public static final int CHECKBOX = 1;
	public static final int RADIO = -1;
	private boolean actionDown = false;
	private LinearLayout bg;
	private boolean canUse = true;
	private int checkModel;
	private Drawable checkedImage;
	private Context ctx;
	private boolean hasImage;
	private ImageView imageView;
	private boolean isChecked;
	private OnCheckedChangeListener mOnCheckedChangeListener;
	private TextView textView1;
	private TextView textView2;
	private Drawable unCheckImage;

	public CheckRadioButton(Context paramContext)
	{
		super(paramContext);
		this.ctx = paramContext;
		((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(R.layout.check_radio_button, this);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			  public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
			  {
			    	CheckRadioButton.this.setSelected(paramAnonymousBoolean);
			  }
		});
		this.checkModel = 1;
		this.isChecked = false;
		this.hasImage = true;
		this.bg = ((LinearLayout)findViewById(R.id.bg));
		this.textView1 = ((TextView)findViewById(R.id.button_textView));
		this.textView2 = ((TextView)findViewById(R.id.button_textView2));
		ColorStateList localColorStateList = getResources().getColorStateList(R.drawable.selector_crb_text);
		setTextColor1(localColorStateList);
		setTextColor2(localColorStateList);
		this.imageView = ((ImageView)findViewById(R.id.button_imageview));
		 Log.d("CheckRadioButton","hasImage==="+hasImage);
		if (this.hasImage)
		{
		  setUnCheckImage(this.ctx.getResources().getDrawable(R.drawable.check_unchecked_normal));
		  Log.d("CheckRadioButton","hasImage1111==="+hasImage);
		  setCheckedImage(this.ctx.getResources().getDrawable(R.drawable.check_checked_normal));
		}
		setCheckedState(this.isChecked);
		this.imageView.setVisibility(View.GONE);

	}

	public CheckRadioButton(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		this.ctx = paramContext;
		((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(R.layout.check_radio_button, this);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
		  public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
		  {
		    CheckRadioButton.this.setSelected(paramAnonymousBoolean);
		  }
		});
		initWidget(paramContext.obtainStyledAttributes(paramAttributeSet,      		
				R.styleable.checkRadioButton_1));
	}

	private void initWidget(TypedArray paramTypedArray)
	{
		this.checkModel = paramTypedArray.getInt(18, 1);
		this.isChecked = paramTypedArray.getBoolean(16, false);
		this.hasImage = paramTypedArray.getBoolean(8, true);
		this.bg = ((LinearLayout)findViewById(R.id.bg));
		this.textView1 = ((TextView)findViewById(R.id.button_textView));
		this.textView2 = ((TextView)findViewById(R.id.button_textView2));
		int i = (int)paramTypedArray.getDimension(3, -1.0F);
		int j = (int)paramTypedArray.getDimension(7, -1.0F);
		UMDebug.d("CheckRadioButton", i + "    " + j+",hasImage="+ this.hasImage);
		if (i > -1)
		{
		      LinearLayout.LayoutParams localLayoutParams1 = new LinearLayout.LayoutParams(340, -2);
		      localLayoutParams1.leftMargin = i;
		      this.textView1.setLayoutParams(localLayoutParams1);
		}
		if (j > -1)
		{
		      LinearLayout.LayoutParams localLayoutParams2 = new LinearLayout.LayoutParams(300, -2);
		      localLayoutParams2.leftMargin = j;
		      this.textView2.setLayoutParams(localLayoutParams2);
		}
		setText1(paramTypedArray.getString(0) == null ?"" :paramTypedArray.getString(0));
		setText2(paramTypedArray.getString(4) == null ?"" :paramTypedArray.getString(4));
		setTextColor1(paramTypedArray.getColorStateList(1));
		setTextColor2(paramTypedArray.getColorStateList(5));
		setTextSize1(paramTypedArray.getDimension(2, 25.0F));
		setTextSize2(paramTypedArray.getDimension(6, 25.0F));
		this.imageView = ((ImageView)findViewById(R.id.button_imageview));
		if (this.hasImage)
		{
			int k = paramTypedArray.getResourceId(10, R.drawable.check_unchecked_normal);
			setUnCheckImage(this.ctx.getResources().getDrawable(k));
			int m = paramTypedArray.getResourceId(11, R.drawable.check_checked_normal);
			setCheckedImage(this.ctx.getResources().getDrawable(m));
			
		}
		else
			imageView.setVisibility(View.GONE);
		setCheckedState(this.isChecked);
		paramTypedArray.recycle();
		

	}

	public int getCheckModel()
	{
		return this.checkModel;
	}

	public Drawable getCheckedImage()
	{
		return this.checkedImage;
	}

	public OnCheckedChangeListener getOnCheckedChangeListener()
	{
		return this.mOnCheckedChangeListener;
	}

	public String getText2()
	{
		return this.textView2.getText().toString();
	}

	public Drawable getUnCheckImage()
	{
		return this.unCheckImage;
	}

	public void invalidat()
	{
		setCheckedState(this.isChecked);
	}

	public boolean isCanUse()
	{
		return this.canUse;
	}

	public boolean isChecked()
	{
		return this.isChecked;
	}

	public void onFocusChange(View paramView, boolean paramBoolean)
	{
		Log.e("SUNNIWELL", "This custom view's focus has changed =" + paramBoolean);
		dispatchSetSelected(paramBoolean);
	}

	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
	{
		if ((paramInt == 23) || (paramInt == 66))
		{
			this.actionDown = true;
			requestFocus();
		}
		return super.onKeyDown(paramInt, paramKeyEvent);
	}

	public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
	{
		UMDebug.d("CheckRadioButton","paramInt="+paramInt+",canUse="+canUse+",checkModel="+checkModel+"isChecked=="+isChecked);
		if (((paramInt == 23) || (paramInt == 66)) && (this.actionDown))
		{
			this.actionDown = false;
			if (!this.canUse)
				return true;
			switch (this.checkModel)
			{
				case 0:
					toggle();
					break;
				case 1:
					setChecked(!this.isChecked );					
					break;
			}
		}		
		return super.onKeyDown(paramInt, paramKeyEvent);
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent)
	{
		if (!this.canUse)
			return true;
		switch(paramMotionEvent.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				Log.d("View", "down.....");
				this.actionDown = true;
				requestFocus();
				break;
			case MotionEvent.ACTION_MOVE:
				if ((paramMotionEvent.getX() < 0.0F) || (paramMotionEvent.getY() < 0.0F) || (paramMotionEvent.getX() > getWidth()) || (paramMotionEvent.getY() > getHeight()))
				{
				    this.actionDown = false;				    
				}
				break;
			case MotionEvent.ACTION_UP:
				if (!this.actionDown)
				{
					clearFocus();
					return true;
				}
				switch (this.checkModel)
				{
					case 0:
					  this.actionDown = false;
					  break;
					case 1:
					  setChecked(true);
					  break;
				}
				toggle();
				break;
		}
		return actionDown;
	}

	public void setBackSelector(int paramInt)
	{
		this.bg.setBackgroundResource(paramInt);
	}

	public void setCanchecked(boolean paramBoolean)
	{
		this.canUse = paramBoolean;
	}

	public void setCheckModel(int paramInt)
	{
		this.checkModel = paramInt;
	}

	public void setChecked(boolean paramBoolean)
	{  
		 Log.d("CheckRadioButton3333","hasImage==="+hasImage);
		if (this.hasImage)
		{   
			Log.d("CheckRadioButton3333","hasparamBoolean==="+paramBoolean);
			if(paramBoolean)
				this.imageView.setBackgroundDrawable(this.checkedImage);
			else
				this.imageView.setBackgroundDrawable(this.unCheckImage);
		}
				
		this.isChecked = paramBoolean;
		if (this.mOnCheckedChangeListener != null){
			Log.d("CheckRadioButton444","mOnCheckedChangeListener==="+mOnCheckedChangeListener);
			this.mOnCheckedChangeListener.onCheckedChanged(this, this.isChecked);
		}
	}

	public void setCheckedImage(Drawable paramDrawable)
	{
		this.checkedImage = paramDrawable;
	}

	public void setCheckedState(boolean paramBoolean)
	{   Log.d("CheckRadioButton","setCheckedState==="+paramBoolean);
		if (paramBoolean)
		{   Log.d("CheckRadioButton","hasImage==="+hasImage);
			if (this.hasImage)
				this.imageView.setBackgroundDrawable(this.checkedImage);
		}
		else{
		Log.d("CheckRadioButton","hasImage111==="+hasImage);
			if (this.hasImage)
				this.imageView.setBackgroundDrawable(this.unCheckImage);
		}
		this.isChecked = paramBoolean;
	}

	public void setOnCheckedChangeListener(OnCheckedChangeListener paramOnCheckedChangeListener)
	{
		this.mOnCheckedChangeListener = paramOnCheckedChangeListener;
	}

	public void setText1(String paramString)
	{
		this.textView1.setText(paramString);
	}

	public void setText2(String paramString)
	{
		this.textView2.setText(paramString);
	}

	public void setTextColor1(ColorStateList paramColorStateList)
	{
		if (paramColorStateList == null)
		{
			this.textView1.setTextColor(-1);
			return;
		}
		this.textView1.setTextColor(paramColorStateList);
	}

	public void setTextColor2(ColorStateList paramColorStateList)
	{
		if (paramColorStateList == null)
		{
			this.textView2.setTextColor(-1);
			return;
		}
		this.textView2.setTextColor(paramColorStateList);
	}

	public void setTextSize1(float paramFloat)
	{
		this.textView1.setTextSize(paramFloat);
	}

	public void setTextSize2(float paramFloat)
	{
		this.textView2.setTextSize(paramFloat);
	}

	public void setUnCheckImage(Drawable paramDrawable)
	{
		this.unCheckImage = paramDrawable;
	}

	public void setViewFalse()
	{
		setEnabled(false);
		setCanchecked(false);
		this.canUse = false;
	}

	public void setViewState(boolean paramBoolean)
	{
		setFocusable(paramBoolean);
		setEnabled(paramBoolean);
		setFocusableInTouchMode(paramBoolean);
		this.canUse = paramBoolean;
	}

	public void toggle()
	{
		this.isChecked = !this.isChecked;
		Log.d("CheckRadioButton3333","toggle==="+isChecked);
		setChecked(this.isChecked);
	}

	public static abstract interface OnCheckedChangeListener
	{
		public abstract void onCheckedChanged(CheckRadioButton paramCheckRadioButton, boolean paramBoolean);
	}
}
