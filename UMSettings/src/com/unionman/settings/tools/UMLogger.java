package com.unionman.settings.tools;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UMLogger
{
  private static final String DEFALUT_FORMAT = "";
  private static final String FILE = "/data/log.properties";
  private final String FORMAT;
  private final String TAG;
  private final Level level;

  protected UMLogger(Level paramLevel, String paramString1, String paramString2)
  {
    this.level = paramLevel;
    this.TAG = paramString1;
    this.FORMAT = paramString2;
  }

  private String formatStr(int paramInt, StackTraceElement paramStackTraceElement, String paramString)
  {
    String str1 = this.FORMAT;
    if (paramString == null)
      paramString = "null";
    String str2 = str1.replaceAll("%p", Level.getLevel(paramInt).name()).replaceAll("%t", Thread.currentThread().getName()).replaceAll("%n", System.getProperty("line.separator")).replaceAll("%m", paramString).replaceAll("%d", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
    if (paramStackTraceElement != null)
      str2 = str2.replaceAll("%c", paramStackTraceElement.getClassName().replace("$", "\\$")).replaceAll("%F", paramStackTraceElement.getFileName()).replaceAll("%M", paramStackTraceElement.getMethodName()).replaceAll("%l", ""+paramStackTraceElement.getLineNumber());
    return str2;
  }

  // ERROR //
  public static UMLogger getLogger(Class paramClass)
  {
	  return new UMLogger(Level.debug, ""+paramClass.getSimpleName(), "%s");
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +5 -> 6
    //   4: aconst_null
    //   5: areturn
    //   6: aload_0
    //   7: invokevirtual 141	java/lang/Class:getSimpleName	()Ljava/lang/String;
    //   10: astore_1
    //   11: getstatic 144	net/sunniwell/common/log/SWLogger$Level:none	Lnet/sunniwell/common/log/SWLogger$Level;
    //   14: astore_2
    //   15: ldc 8
    //   17: astore_3
    //   18: new 146	java/util/Properties
    //   21: dup
    //   22: invokespecial 147	java/util/Properties:<init>	()V
    //   25: astore 4
    //   27: new 149	java/io/File
    //   30: dup
    //   31: ldc 11
    //   33: invokespecial 150	java/io/File:<init>	(Ljava/lang/String;)V
    //   36: astore 5
    //   38: aload 5
    //   40: invokevirtual 154	java/io/File:exists	()Z
    //   43: ifeq +172 -> 215
    //   46: new 156	java/io/FileInputStream
    //   49: dup
    //   50: aload 5
    //   52: invokespecial 159	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   55: astore 19
    //   57: aload 19
    //   59: astore 6
    //   61: aload 6
    //   63: ifnull +118 -> 181
    //   66: aload 4
    //   68: aload 6
    //   70: invokevirtual 163	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   73: aload 4
    //   75: invokevirtual 166	java/util/Properties:isEmpty	()Z
    //   78: ifne +103 -> 181
    //   81: aload_0
    //   82: invokevirtual 170	java/lang/Class:getPackage	()Ljava/lang/Package;
    //   85: invokevirtual 173	java/lang/Package:getName	()Ljava/lang/String;
    //   88: astore 12
    //   90: aload 4
    //   92: aload 12
    //   94: invokevirtual 174	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   97: astore 13
    //   99: aload 13
    //   101: ifnull +244 -> 345
    //   104: aload 13
    //   106: invokestatic 177	net/sunniwell/common/log/SWLogger$Level:getLevel	(Ljava/lang/String;)Lnet/sunniwell/common/log/SWLogger$Level;
    //   109: ifnonnull +20 -> 129
    //   112: goto +233 -> 345
    //   115: aload 12
    //   117: ldc 179
    //   119: invokevirtual 183	java/lang/String:lastIndexOf	(Ljava/lang/String;)I
    //   122: istore 14
    //   124: iload 14
    //   126: ifgt +115 -> 241
    //   129: aload 4
    //   131: ldc 184
    //   133: invokevirtual 174	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   136: invokestatic 177	net/sunniwell/common/log/SWLogger$Level:getLevel	(Ljava/lang/String;)Lnet/sunniwell/common/log/SWLogger$Level;
    //   139: astore 16
    //   141: aload 16
    //   143: ifnull +6 -> 149
    //   146: aload 16
    //   148: astore_2
    //   149: aload 13
    //   151: invokestatic 177	net/sunniwell/common/log/SWLogger$Level:getLevel	(Ljava/lang/String;)Lnet/sunniwell/common/log/SWLogger$Level;
    //   154: astore 17
    //   156: aload 17
    //   158: ifnull +6 -> 164
    //   161: aload 17
    //   163: astore_2
    //   164: aload 4
    //   166: ldc 185
    //   168: invokevirtual 174	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   171: astore 18
    //   173: aload 18
    //   175: ifnull +6 -> 181
    //   178: aload 18
    //   180: astore_3
    //   181: aload 6
    //   183: ifnull +8 -> 191
    //   186: aload 6
    //   188: invokevirtual 190	java/io/InputStream:close	()V
    //   191: new 2	net/sunniwell/common/log/SWLogger
    //   194: dup
    //   195: aload_2
    //   196: aload_1
    //   197: aload_3
    //   198: invokespecial 192	net/sunniwell/common/log/SWLogger:<init>	(Lnet/sunniwell/common/log/SWLogger$Level;Ljava/lang/String;Ljava/lang/String;)V
    //   201: areturn
    //   202: astore 20
    //   204: aload 20
    //   206: invokevirtual 195	java/io/FileNotFoundException:printStackTrace	()V
    //   209: aconst_null
    //   210: astore 6
    //   212: goto -151 -> 61
    //   215: ldc 2
    //   217: ldc 197
    //   219: invokevirtual 201	java/lang/Class:getResourceAsStream	(Ljava/lang/String;)Ljava/io/InputStream;
    //   222: astore 6
    //   224: aload 6
    //   226: ifnonnull -165 -> 61
    //   229: ldc 2
    //   231: ldc 203
    //   233: invokevirtual 201	java/lang/Class:getResourceAsStream	(Ljava/lang/String;)Ljava/io/InputStream;
    //   236: astore 6
    //   238: goto -177 -> 61
    //   241: aload 12
    //   243: iconst_0
    //   244: iload 14
    //   246: invokevirtual 207	java/lang/String:substring	(II)Ljava/lang/String;
    //   249: astore 12
    //   251: aload 4
    //   253: aload 12
    //   255: invokevirtual 174	java/util/Properties:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   258: astore 13
    //   260: aload 13
    //   262: ifnull -147 -> 115
    //   265: aload 13
    //   267: invokestatic 177	net/sunniwell/common/log/SWLogger$Level:getLevel	(Ljava/lang/String;)Lnet/sunniwell/common/log/SWLogger$Level;
    //   270: astore 15
    //   272: aload 15
    //   274: ifnonnull -145 -> 129
    //   277: goto -162 -> 115
    //   280: astore 10
    //   282: aload 10
    //   284: invokevirtual 208	java/lang/Exception:printStackTrace	()V
    //   287: aload 6
    //   289: ifnull -98 -> 191
    //   292: aload 6
    //   294: invokevirtual 190	java/io/InputStream:close	()V
    //   297: goto -106 -> 191
    //   300: astore 11
    //   302: aload 11
    //   304: invokevirtual 209	java/io/IOException:printStackTrace	()V
    //   307: goto -116 -> 191
    //   310: astore 8
    //   312: aload 6
    //   314: ifnull +8 -> 322
    //   317: aload 6
    //   319: invokevirtual 190	java/io/InputStream:close	()V
    //   322: aload 8
    //   324: athrow
    //   325: astore 9
    //   327: aload 9
    //   329: invokevirtual 209	java/io/IOException:printStackTrace	()V
    //   332: goto -10 -> 322
    //   335: astore 7
    //   337: aload 7
    //   339: invokevirtual 209	java/io/IOException:printStackTrace	()V
    //   342: goto -151 -> 191
    //   345: goto -230 -> 115
    //
    // Exception table:
    //   from	to	target	type
    //   46	57	202	java/io/FileNotFoundException
    //   66	99	280	java/lang/Exception
    //   104	112	280	java/lang/Exception
    //   115	124	280	java/lang/Exception
    //   129	141	280	java/lang/Exception
    //   149	156	280	java/lang/Exception
    //   164	173	280	java/lang/Exception
    //   241	260	280	java/lang/Exception
    //   265	272	280	java/lang/Exception
    //   292	297	300	java/io/IOException
    //   66	99	310	finally
    //   104	112	310	finally
    //   115	124	310	finally
    //   129	141	310	finally
    //   149	156	310	finally
    //   164	173	310	finally
    //   241	260	310	finally
    //   265	272	310	finally
    //   282	287	310	finally
    //   317	322	325	java/io/IOException
    //   186	191	335	java/io/IOException
  }

  private StackTraceElement getStackTraceElement()
  {
	int i = 0;
    StackTraceElement[] arrayOfStackTraceElement = new Throwable().getStackTrace();
    if (arrayOfStackTraceElement != null)
    {
	    for (i = 0; i< arrayOfStackTraceElement.length; i++)
	    {	      
	      if (!arrayOfStackTraceElement[i].getClassName().equals(getClass().getName()))
	    	  return arrayOfStackTraceElement[i];
	    }
    }
    return null;
   
  }

  private void print(Level paramLevel, String paramString)
  {
    int i = paramLevel.intValue;
    //if (i >= this.level.intValue)
      Log.println(i, this.TAG, formatStr(i, getStackTraceElement(), paramString));
  }

  public void d(String paramString)
  {
  	Log.d(this.TAG, paramString);
   // print(Level.debug, paramString);
  }

  public void e(String paramString)
  {
  	Log.e(this.TAG, paramString);
    //print(Level.error, paramString);
  }

  public void i(String paramString)
  {
  	Log.i(this.TAG, paramString);
   // print(Level.info, paramString);
  }

  public void w(String paramString)
  {
  	Log.w(this.TAG, paramString);
    //print(Level.warn, paramString);
  }

  static enum Level
  {
	debug, info, warn, error, none;
    int intValue;
    private Level(int l)
    {
      this.intValue = l;
    }
    private Level()
    {
    	
    }
    public static Level getLevel(int paramInt)
    {
      Level[] arrayOfLevel = values();
      for (int i = 0; i< arrayOfLevel.length ; i++)
      {
        Level localLevel = arrayOfLevel[i];
        if(localLevel.intValue == paramInt)
        	return localLevel;
      }
      return null;
    }

    public static Level getLevel(String paramString)
    {
      if (paramString == null)
        return null;
      try
      {
        Level localLevel = valueOf(paramString.toLowerCase());
        return localLevel;
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      return null;
    }
  }
}
