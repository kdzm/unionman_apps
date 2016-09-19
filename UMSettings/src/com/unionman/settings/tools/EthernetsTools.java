package com.unionman.settings.tools;

import android.util.Log;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.unionman.settings.tools.UMDebug;
import com.unionman.settings.tools.Logger;

public class EthernetsTools
{
  public static final String TAG="com.unionman.settings.tools--EthernetsTools--";

  private static int[] changeToInt(String paramString)
  {
    Logger.i(TAG, "changeToInt()--");
    int[] arrayOfInt = null ;
    if (paramString == null)
      arrayOfInt = null;
    while (true)
    {
      UMDebug.umdebug_trace();
      String[] arrayOfString = paramString.split("\\.");
      if (arrayOfString.length != 4)
        return null;
      try
      {
        arrayOfInt = new int[4];
        for (int i = 0; i < arrayOfString.length; i++)
          arrayOfInt[i] = Integer.parseInt(arrayOfString[i]);
		return arrayOfInt;
      }
      catch (Exception localException)
      {
      }
	  return null;
    }
    
  }

  public static boolean check(String paramString1, String paramString2, String paramString3)
  {
    Logger.i(TAG, "check()--");
    if ((!checkIP(paramString1)) || (!checkMask(paramString2)) || (!checkIP(paramString3)))
		 return false;
    int i;
    int j;
    int k;
    do
    {
     
      i = toInt(paramString1);
      j = toInt(paramString2);
      k = toInt(paramString3);
	  UMDebug.umdebug_trace();
    }
    while ((i == 0) || (j == 0) || (k == 0) || ((i & j) != (k & j)));
    return true;
  }

  public static boolean checkIP(String paramString)
  {
    Logger.i(TAG, "checkIP()--");
    if (!isIPNum(paramString))
		return false;
    int[] arrayOfInt;
    do
    {
      
      arrayOfInt = changeToInt(paramString);
	  UMDebug.umdebug_trace();
    }
    while (arrayOfInt == null);
    for (int i = 0; ; i++)
    {
      if (i >= arrayOfInt.length)
      {
        if ((arrayOfInt[0] == 0) || (arrayOfInt[3] == 0) || (arrayOfInt[0] > 223))
          break;
        
      }
      if ((arrayOfInt[i] < 0) || (arrayOfInt[i] > 255))
        break;
    }
	return true;
  }

  public static boolean checkMask(String paramString)
  {
    Logger.i(TAG, "checkMask()--");
    if (!isIPNum(paramString))
		return false;
    int i;
    do
    {
      
      i = 1 + (0xFFFFFFFF ^ toInt(paramString));
	  UMDebug.umdebug_trace();
    }
    while ((i & i - 1) != 0);
    return true;
  }

  public static int getNetmaskLength(String paramString)
  {
    Logger.i(TAG, "getNetmaskLength()--");
    int i = toInt(paramString);
    int j = 0;
    for (int k = 1; ; k++)
    {
      if (k >= 32)
        return j;
      if ((0x1 & i >> k) == 1)
        j++;
    }
  }

  private void httpClientTest()
  {
    Logger.i(TAG, "httpClientTest()--");
    DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
    try
    {
      HttpGet localHttpGet = new HttpGet("http://www.google.com");
      localDefaultHttpClient.execute(localHttpGet).getStatusLine().getStatusCode();
      localHttpGet.abort();
      return;
    }
    catch (IOException localIOException)
    {
    }
  }

  public static String intToIp(int paramInt)
  {
    Logger.i(TAG, "intToIp()--");
    return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
  }

  public static boolean isIP(String paramString)
  {
    Logger.i(TAG, "isIP()--");
    if (!isIPNum(paramString))
		return false;
    int[] arrayOfInt;
    do
    {
      
      arrayOfInt = changeToInt(paramString);
	  UMDebug.umdebug_trace();
    }
    while (arrayOfInt == null);
    for (int i = 0; ; i++)
    {
      if (i >= arrayOfInt.length)
      {
        if (arrayOfInt[0] == 0)
          break;
        return true;
      }
      if ((arrayOfInt[i] < 0) || (arrayOfInt[i] > 255))
        break;
    }
	return true;
  }

  private static boolean isIPNum(String paramString)
  {
    Logger.i(TAG, "isIPNum()--");
    if (paramString == null)
      return false;
    for (int i = 0; ; i++)
    {
      if (i >= paramString.length())
        return true;
      if ("0123456789.".indexOf(paramString.charAt(i)) == -1)
        break;
    }
	return true;
  }

  private final void pingHostname()
  {
    Logger.i(TAG, "pingHostname()--");
    try
    {
      Runtime.getRuntime().exec("ping -c 1 -w 100 www.google.com").waitFor();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
    }
	catch (UnknownHostException localUnknownHostException)
    {
    }
    catch (IOException localIOException)
    {
    }
    
  }

  private static int toInt(String paramString)
  {
    Logger.i(TAG, "toInt()--");
    int[] arrayOfInt = changeToInt(paramString);
    int i = 0;
    if (arrayOfInt != null)
      i = arrayOfInt[0] << 24 | arrayOfInt[1] << 16 | arrayOfInt[2] << 8 | arrayOfInt[3];
    return i;
  }

  public boolean IPcheck(String paramString)
  {
    Logger.i(TAG, "IPcheck()--");
	Log.d(TAG, " text " + paramString);
	if (paramString != null && !paramString.isEmpty()) {
	    String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
	            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
	            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
	            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
	    if (paramString.matches(regex)) {
	        return true;
	    } else {
	        return false;
	    }
	}
	return false;
  }

  public boolean checkReachableByIP(String paramString)
  {
    Logger.i(TAG, "checkReachableByIP()--");
    try
    {
      boolean bool = InetAddress.getByName(paramString).isReachable(1000);
      return bool;
    }
    catch (UnknownHostException localUnknownHostException)
    {
      localUnknownHostException.printStackTrace();
      return false;
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
    return false;
  }
}