package com.unionman.settings.tools;

import java.io.PrintStream;
import com.unionman.settings.tools.Logger;
import com.unionman.settings.tools.UMDebug;
public class IPUtil
{
    public static final String TAG="com.unionman.settings.tools--IPUtil--";
  private static int[] changeToInt(String paramString)
  {
      Logger.i(TAG, "changeToInt()--");
    int[] arrayOfInt;
    if (paramString == null)
    {
      arrayOfInt = null;
    }
    while (true)
    {
      
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
  
  public static boolean checkSegment(String paramString1, String paramString2, String paramString3){
      Logger.i(TAG, "checkSegment()--");
	   int i = toInt(paramString1);
	   int j = toInt(paramString2);
	   int k = toInt(paramString3);
	   UMDebug.umdebug_trace();
	   if((i & j) == (k & j))
	      return true;
	   else {
	      return false;
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

  public static boolean checkHost255(String paramString1, String paramString2)
  {
      Logger.i(TAG, "checkHost255()--");
    if ((!checkIP(paramString1)) || (!checkMask(paramString2)))
		return false;
    int i;
    int j;
    do
    {
      
      i = toInt(paramString1);
      j = toInt(paramString2);
	  UMDebug.umdebug_trace();
    }
    while ((i == 0) || (j == 0) || ((i | j) == -1));
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
        return true;
      }
      if ((arrayOfInt[i] < 0) || (arrayOfInt[i] > 255))
        break;
    }
	return true;
  }

  public static boolean checkMask(String paramString)
  {
    if (!isIPNum(paramString))
		return false;
 /*   
    int i;
    do
    {
      
      i = 1 + (0xFFFFFFFF ^ toInt(paramString));
	  UMDebug.umdebug_trace();
    }
    while ((i & i - 1) != 0);
  */  
    
    int[] arrayOfInt;
    arrayOfInt = changeToInt(paramString);
    UMDebug.umdebug_trace();
    for (int i = 0; i < arrayOfInt.length ; i++){
    	if ((arrayOfInt[i] < 0) || (arrayOfInt[i] > 255)) 
    	{
    		return false;
    	}
    }
    
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
//    do
//    {
      arrayOfInt = changeToInt(paramString);
	  UMDebug.umdebug_trace();
//    }while (arrayOfInt == null);
    for (int i = 0; i < arrayOfInt.length ; i++)
    {
//      if (i >= arrayOfInt.length)
//      {
//        if (arrayOfInt[0] == 0)
//          break;
//        return true;
//      }
      if ((arrayOfInt[i] < 0) || (arrayOfInt[i] > 255))        
      {
         return false; 
      }
    }
	return true;
  }

  private static boolean isIPNum(String paramString)
  {
      Logger.i(TAG, "isIPNum()--");
    if (paramString == null || paramString.equals("0.0.0.0"))
      return false;
    for (int i = 0; i < paramString.length(); i++)
    {
        if ("0123456789.".indexOf(paramString.charAt(i)) == -1)
        return false;
    }
	return true;
  }

  public static void main(String[] paramArrayOfString)
  {
      Logger.i(TAG, "main()--");
    System.out.println(getNetmaskLength("255.255.255.0"));
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
}