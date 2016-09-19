package com.unionman.settings.custom;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import java.util.StringTokenizer;

import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.tools.StringUtils;

public class IpAddrEditText extends LinearLayout
  implements View.OnFocusChangeListener
{
  private static final String DIGITS = "0123456789";
  private boolean bEnable = true;
  private boolean bError = false;
  private boolean candown = false;
  private boolean canup = false;
  private EditText editText1;
  private EditText editText2;
  private EditText editText3;
  private EditText editText4;
  private Context mContext;
  
  private View.OnKeyListener mOnKeyListener = new View.OnKeyListener()
  {
    public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      if (paramAnonymousInt == 19)
        return  IpAddrEditText.this.canup;
      if ((paramAnonymousInt == 4) && (paramAnonymousKeyEvent.getAction() == 0))
  	  {
  		if (paramAnonymousInt == 20)
      		return IpAddrEditText.this.candown;
		else
			return true;	
  	  }
	  
      if ((paramAnonymousInt == 4) && (paramAnonymousKeyEvent.getAction() == 1))
      {
        StringUtils.delText((EditText)paramAnonymousView);
        return true;
      }
      return false;
    }
  };
  private LinearLayout.LayoutParams params;

  public IpAddrEditText(Context paramContext)
  {
    this(paramContext, null);
  }

  public IpAddrEditText(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    this.params = new LinearLayout.LayoutParams(70, 35);
    initView(this.mContext);
  }

  private String getNumString(EditText paramEditText)
  {
    try
    {
      return Integer.parseInt(paramEditText.getText().toString())+"";
    }
    catch (Exception localException)
    {
      return "";
    }
  }

  private boolean handleLeftKey()
  {
    if ((this.editText4.isFocused()) && (this.editText4.getSelectionStart() == 0))
      this.editText3.requestFocus();
    View localView;
//    do
    {
 //     return true;
      if ((this.editText3.isFocused()) && (this.editText3.getSelectionStart() == 0))
      {
        this.editText2.requestFocus();
        return true;
      }
      if ((this.editText2.isFocused()) && (this.editText2.getSelectionStart() == 0))
      {
        this.editText1.requestFocus();
        return true;
      }
      if ((!this.editText1.isFocused()) || (this.editText1.getSelectionStart() != 0))
        return false;
      localView = focusSearch(17);
    }
//    while (localView == null);
    localView.requestFocus();
    return true;
  }

  private void initEditText(EditText paramEditText)
  {
    paramEditText.setText("");
    paramEditText.setPadding(0, 0, 5, 3);
    paramEditText.setInputType(2);
    InputFilter[] arrayOfInputFilter = new InputFilter[1];
    arrayOfInputFilter[0] = new InputFilter.LengthFilter(3);
    paramEditText.setFilters(arrayOfInputFilter);
    paramEditText.addTextChangedListener(new IpEditBoxWatcher(paramEditText));
    paramEditText.setTextColor(-1);
    paramEditText.setGravity(17);
    paramEditText.setSingleLine();
    paramEditText.setBackgroundDrawable(null);
    paramEditText.setOnFocusChangeListener(this);
  }

  private void initView(Context paramContext)
  {
    setOrientation(0);
    this.params.gravity = 17;
    setLayoutParams(this.params);
    this.editText1 = new EditText(paramContext);
    this.editText1.setOnKeyListener(this.mOnKeyListener);
    this.editText2 = new EditText(paramContext);
    this.editText2.setOnKeyListener(this.mOnKeyListener);
    this.editText3 = new EditText(paramContext);
    this.editText3.setOnKeyListener(this.mOnKeyListener);
    this.editText4 = new EditText(paramContext);
    this.editText4.setOnKeyListener(this.mOnKeyListener);
    initEditText(this.editText1);
    initEditText(this.editText2);
    initEditText(this.editText3);
    initEditText(this.editText4);
    addView(this.editText1, this.params);
    addView(this.editText2, this.params);
    addView(this.editText3, this.params);
    addView(this.editText4, this.params);
    setBackgroundResource(2130837536);
  }

  private boolean validate(String paramString)
  {
    if ((paramString.equals("")) || (paramString.length() == 0))
      return true;
    if (Integer.parseInt(paramString) > 255)
    {
      Toast.makeText(this.mContext, "ip地址 错误", 0).show();
      return false;
    }
    return true;
  }

  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() == 0)
      switch (paramKeyEvent.getKeyCode())
      {
      default:
      case 21:
      }
    do
      return super.dispatchKeyEvent(paramKeyEvent);
    while (!handleLeftKey());
 //   return true;
  }

  public String getText()
  {
    StringBuffer localStringBuffer = new StringBuffer(15);
    localStringBuffer.append(getNumString(this.editText1)).append(".").append(getNumString(this.editText2)).append(".").append(getNumString(this.editText3)).append(".").append(getNumString(this.editText4));
    return localStringBuffer.toString();
  }

  public boolean isFocusedByIpEditText()
  {
    return (this.editText1.isFocused()) || (this.editText2.isFocused()) || (this.editText3.isFocused()) || (this.editText4.isFocused());
  }

  public boolean isFocusedByIpEditTextOne()
  {
    return this.editText1.isFocused();
  }

  public void obtainFocus()
  {
    this.editText1.requestFocus();
  }

  public void onFocusChange(View paramView, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (this.bEnable)
      {
        setBackgroundResource(2130837538);
        ((EditText)paramView).selectAll();
        return;
      }
      setBackgroundResource(2130837537);
      return;
    }
    setBackgroundResource(2130837536);
  }

  public void setCanDown(boolean paramBoolean)
  {
    if (paramBoolean);
    for (boolean bool = false; ; bool = true)
    {
      this.candown = bool;
      return;
    }
  }

  public void setCanUp(boolean paramBoolean)
  {
    if (paramBoolean);
    for (boolean bool = false; ; bool = true)
    {
      this.canup = bool;
      return;
    }
  }

  public void setEnabled(boolean paramBoolean)
  {
    this.bEnable = paramBoolean;
    this.editText1.setEnabled(paramBoolean);
    this.editText2.setEnabled(paramBoolean);
    this.editText3.setEnabled(paramBoolean);
    this.editText4.setEnabled(paramBoolean);
  }

  public void setFocusable(boolean paramBoolean)
  {
    this.editText1.setFocusable(paramBoolean);
    this.editText2.setFocusable(paramBoolean);
    this.editText3.setFocusable(paramBoolean);
    this.editText4.setFocusable(paramBoolean);
  }

  public void setImeOptions(int paramInt)
  {
    this.editText1.setImeOptions(paramInt);
    this.editText2.setImeOptions(paramInt);
    this.editText3.setImeOptions(paramInt);
    this.editText4.setImeOptions(paramInt);
  }

  public void setText(String paramString)
  {
	  Log.d("DKDKD","paramStringaa=="+paramString);
    if (paramString.equals(""))
    {
      this.editText1.setText("");
      this.editText2.setText("");
      this.editText3.setText("");
      this.editText4.setText("");
      return;
    }
//    StringTokenizer localStringTokenizer;
    String[] arrayOfString =paramString.split("\\.");
    Log.d("DKDKD","fffff="+arrayOfString.toString());
//    for(int i =0;i<4;i++)
//    {
    Log.d("DKDKD","paramStringaa=="+arrayOfString[0]+"paramStringaa=="+arrayOfString[1]+"paramStringaa=="+arrayOfString[2]+"paramStringaa=="+arrayOfString[3]);
    	this.editText1.setText(arrayOfString[0]);
        this.editText2.setText(arrayOfString[1]);
        this.editText3.setText(arrayOfString[2]);
        this.editText4.setText(arrayOfString[3]);
//    }
//    do
//    {
////      return;
//      localStringTokenizer = new StringTokenizer(paramString, ".");
//      
//      arrayOfString = new String[localStringTokenizer.countTokens()];
//	  umdebug.umdebug_trace();
//    }
//    while (localStringTokenizer.countTokens() != 4);
//    for (String token : paramString.split(".")) {
//        System.err.println(token);
//    }
//    for (int i = 0; ; i++)
//    {
//      if (!localStringTokenizer.hasMoreTokens())
//      {
//        this.editText1.setText(arrayOfString[0]);
//        this.editText2.setText(arrayOfString[1]);
//        this.editText3.setText(arrayOfString[2]);
//        this.editText4.setText(arrayOfString[3]);
//        return;
//      }
//      arrayOfString[i] = localStringTokenizer.nextToken();
//    }
  }

  class IpEditBoxWatcher implements TextWatcher
  {
    private EditText editText;
    private String tmp = "";

    public IpEditBoxWatcher(EditText arg2)
    {
      this.editText = arg2;
    }

    public void afterTextChanged(Editable paramEditable)
    {
      String str = paramEditable.toString();
      int i = str.length();
      if ((IpAddrEditText.this.bError) || (str.equals(this.tmp)))
      	{
      		return ;
      	}
	  /* qi.tang
      StringBuffer localStringBuffer;
      int j;
      label46: int k;
      do
      {
        return;
        localStringBuffer = new StringBuffer();
        j = 0;
        if (j < i)
          break;
        this.tmp = localStringBuffer.toString();
        k = this.tmp.length();
        if (k != 0)
          break label164;
      }
      while (IpAddrEditText.this.validate(this.tmp));
      IpAddrEditText.this.bError = true;
      while (true)
      {
        if (!IpAddrEditText.this.bError)
          break label245;
        this.editText.setText("255");
        this.editText.selectAll();
        IpAddrEditText.this.bError = false;
        return;
        if ("0123456789".indexOf(str.charAt(j)) >= 0)
          localStringBuffer.append(str.charAt(j));
        j++;
        break label46;
        label164: if (k >= 3)
        {
          if (IpAddrEditText.this.validate(this.tmp))
          {
            View localView = this.editText.focusSearch(66);
            if (localView != null)
              localView.requestFocus();
          }
          else
          {
            IpAddrEditText.this.bError = true;
          }
        }
        else
        {
          if (IpAddrEditText.this.validate(this.tmp))
            break;
          IpAddrEditText.this.bError = true;
        }
      }
      label245: this.editText.setText(this.tmp);
      this.editText.setSelection(k);
      this.editText.invalidate();
      */
    }

    public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
      this.tmp = paramCharSequence.toString();
    }

    public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
    }
  }
}
