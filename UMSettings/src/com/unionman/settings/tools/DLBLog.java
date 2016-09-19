package com.unionman.settings.tools;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.unionman.settings.tools.UMDebug;


public class DLBLog
{
  private static final String DEF_Format = "noinit|%d|%p|%t@%c:%L-%M(?)|%m%n";
  private static final String DEF_Project = "noinit";
  private static final int MSG_SPLIT_SIZE = 4096;
  private static final String PName_D = "stdout.DEBUG.format";
  private static final String PName_E = "stdout.ERROR.format";
  private static final String PName_I = "stdout.INFO.format";
  private static final String PName_Level = "stdout.level";
  private static final String PName_Project = "stdout.project.name";
  private static final String PName_Tag = "stdout.tag.name";
  private static final String PName_V = "stdout.VERBOSE.format";
  private static final String PName_W = "stdout.WARN.format";
  private static String PValue_D;
  private static String PValue_E;
  private static String PValue_I;
  private static String PValue_Project;
  private static String PValue_Tag = "class.getSimpleName()";
  private static String PValue_V;
  private static String PValue_W;
  private static List<LogMsg> initLogInfos;
  private static boolean isLoaded;
  private static LogLevel logLevel;
  private String Log_Class_SimpleName = null;
  private String Log_Class_name = null;
  private String Log_Tag = null;

  static
  {
    PValue_Project = "noinit";
    PValue_V = "noinit|%d|%p|%t@%c:%L-%M(?)|%m%n";
    PValue_D = "noinit|%d|%p|%t@%c:%L-%M(?)|%m%n";
    PValue_I = "noinit|%d|%p|%t@%c:%L-%M(?)|%m%n";
    PValue_W = "noinit|%d|%p|%t@%c:%L-%M(?)|%m%n";
    PValue_E = "noinit|%d|%p|%t@%c:%L-%M(?)|%m%n";
    logLevel = LogLevel.INFO;
    isLoaded = false;
    initLogInfos = new ArrayList();
    init();
  }

  public static void d(String paramString)
  {
  	Log.d(null, paramString);
   // printLog(null, LogLevel.DEBUG, paramString);
  }

  public static void d(String paramString1, String paramString2)
  {
    //printLog(paramString1, LogLevel.DEBUG, paramString2);
    Log.d(paramString1, paramString2);
  }

  public static void e(String paramString)
  {
    //printLog(null, LogLevel.ERROR, paramString);
    Log.e(null, paramString);
  }

  public static void e(String paramString1, String paramString2)
  {
    //printLog(paramString1, LogLevel.WARN, paramString2);
    Log.e(paramString1, paramString2);
  }

  public static void e(String paramString1, String paramString2, Throwable paramThrowable)
  {
    //printLog(paramString1, LogLevel.WARN, paramString2 + Log.getStackTraceString(paramThrowable));
    Log.e(paramString1, paramString2 + Log.getStackTraceString(paramThrowable));
  }

  public static void e(String paramString, Throwable paramThrowable)
  {
   // printLog(null, LogLevel.ERROR, paramString + Log.getStackTraceString(paramThrowable));
   Log.e(null, paramString + Log.getStackTraceString(paramThrowable));
  }

  public static void e(Throwable paramThrowable)
  {
    //printLog(null, LogLevel.ERROR, Log.getStackTraceString(paramThrowable));
    Log.e(null, Log.getStackTraceString(paramThrowable));
  }

  private static String getMsg(StackTraceElement paramStackTraceElement, String paramString1, String paramString2, String paramString3)
  {
	String localObject = "";
	try
	{
	  localObject = paramString2.replace("%P", PValue_Project).replace("%p", paramString1).replace("%t", Thread.currentThread().getName()).replace("%n", System.getProperty("line.separator")).replace("%d", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
	  if (paramStackTraceElement == null)
		  return localObject;
	  String str = localObject;
	  while(localObject!= null)
	  {
	  		UMDebug.umdebug_trace();
		  localObject = ((String)localObject).replace("%c", "noclass").replace("%F", "no").replace("%M", "nomethod").replace("%L", "0");
		  if(localObject.equals(str))
			  break;
		  str = ((String)localObject).replace("%c", paramStackTraceElement.getClassName().replaceAll("^.*\\.", ""))
				  .replace("%F", paramStackTraceElement.getFileName())
				  .replace("%M", paramStackTraceElement.getMethodName())
				  .replace("%L", "" + paramStackTraceElement.getLineNumber());
	  }
	}
	catch (Exception localException)
	{
	  w(null, "throws Exception when combine msg:" + (String)localObject);
	  w(null, "throws Exception when combine msg:" + paramString3);
	}
	return localObject;
  }

  private static Properties getProperties()
  {
	  Properties ret = loadFileAsProperties("/data/dlblog.properties");
	  if(ret != null)
		  return ret;
	  ret = loadFileAsProperties("/dlblog.properties");
	  if(ret != null)
		  return ret;
	  ret = loadFileAsProperties("dlblog.properties");
	  if(ret != null)
		  return ret;
	  return null;
  }

  private static StackTraceElement getSTE()
  {
    StackTraceElement[] arrayOfStackTraceElement = new Throwable().getStackTrace();
    StackTraceElement localStackTraceElement = null;
    if (arrayOfStackTraceElement != null)
    {
      localStackTraceElement = null;
      if (0 == 0)
      {
        int i = arrayOfStackTraceElement.length;
        localStackTraceElement = null;
        if (i > 3)
          localStackTraceElement = arrayOfStackTraceElement[3];
      }
    }
    return localStackTraceElement;
  }

  private static String getTag(StackTraceElement paramStackTraceElement)
  {
    if (paramStackTraceElement == null)
      return "";
    return paramStackTraceElement.getClassName().replaceAll("\\$.*", "");
  }

  public static void i(String paramString)
  {
    //printLog(null, LogLevel.INFO, paramString);
    Log.i(null, paramString);
  }

  public static void i(String paramString1, String paramString2)
  {
   // printLog(paramString1, LogLevel.INFO, paramString2);
   Log.i(paramString1, paramString2);
  }

  private static void init()
  {
    try
    {
      Properties localProperties = getProperties();
      if (localProperties == null)
      {
        w(null, "not found: dlblog.properties,set loglevel to Warn");
        logLevel = LogLevel.WARN;
        return;
      }
      try
      {
        String str1 = localProperties.getProperty("stdout.level");
        if (str1 != null)
          logLevel = parseLevel(str1);
        String str2 = localProperties.getProperty("stdout.tag.name");
        if (str2 != null)
          PValue_Tag = str2;
        String str3 = localProperties.getProperty("stdout.project.name");
        if (str3 != null)
          PValue_Project = str3;
        String str4 = localProperties.getProperty("stdout.VERBOSE.format");
        if (str4 != null)
          PValue_V = str4;
        String str5 = localProperties.getProperty("stdout.DEBUG.format");
        if (str5 != null)
          PValue_D = str5;
        String str6 = localProperties.getProperty("stdout.INFO.format");
        if (str6 != null)
          PValue_I = str6;
        String str7 = localProperties.getProperty("stdout.WARN.format");
        if (str7 != null)
          PValue_W = str7;
        String str8 = localProperties.getProperty("stdout.ERROR.format");
        if (str8 != null)
          PValue_E = str8;
        printLog(null, LogLevel.DEBUG, "logLevel=" + localProperties.getProperty("stdout.level") + "," + logLevel);
        outputlogmsg();
        return;
      }
      catch (Exception localException)
      {
          w(null, "load dlblog.properties error: " + localException.getMessage() + ". set loglevel to Warn");
          logLevel = LogLevel.WARN;
      }
    }
    finally
    {
      isLoaded = true;
    }
  }

  private static Properties loadClassFileAsProperties(String paramString)
  {
	  return null;
  }

  // ERROR //
  private static Properties loadFileAsProperties(String paramString)
  {
	  return null;
  }

  private static void outputlogmsg()
  {
	  Iterator localIterator = initLogInfos.iterator();
	    while (true)
	    {
	    UMDebug.umdebug_trace();
	      if (!localIterator.hasNext())
	      {
	        initLogInfos.clear();
	        return;
	      }
	      LogMsg localLogMsg = (LogMsg)localIterator.next();
			switch (localLogMsg.loglevel)
			{		
				case VERBOSE:
				  Log.v(localLogMsg.tag, localLogMsg.msg.replace("noinit", PValue_Project));
				  break;
				case DEBUG:
				  Log.d(localLogMsg.tag, localLogMsg.msg.replace("noinit", PValue_Project));
				  break;
				case INFO:
				  Log.i(localLogMsg.tag, localLogMsg.msg.replace("noinit", PValue_Project));
				  break;
				case WARN:
				  Log.w(localLogMsg.tag, localLogMsg.msg.replace("noinit", PValue_Project));
				  break;
				case ERROR:
				  Log.e(localLogMsg.tag, localLogMsg.msg.replace("noinit", PValue_Project));
				  break;
				default:
				  break;
			}
	    }
  }

  private static LogLevel parseLevel(String paramString)
  {
    if ("VERBOSE".equalsIgnoreCase(paramString))
      return LogLevel.VERBOSE;
    if ("DEBUG".equalsIgnoreCase(paramString))
      return LogLevel.DEBUG;
    if ("INFO".equalsIgnoreCase(paramString))
      return LogLevel.INFO;
    if ("WARN".equalsIgnoreCase(paramString))
      return LogLevel.WARN;
    if ("ERROR".equalsIgnoreCase(paramString))
      return LogLevel.ERROR;
    if ("FATAL".equalsIgnoreCase(paramString))
      return LogLevel.NOLOG;
    return LogLevel.DEBUG;
  }

  private static void printLog(String paramString1, LogLevel paramLogLevel, String paramString2)
  {
	  /*
	  String str6=null,str7=null, str8=null;
	  StackTraceElement localStackTraceElement2 = null;
	  List<LogMsg> localList = initLogInfos;
    if (!isLoaded)
    {
      localStackTraceElement2 = getSTE();
      str6 = "noinit|%d|%p|%t@%c:%L-%M(?)|%m%n".replace("%p", paramLogLevel.toString()).replace("%t", Thread.currentThread().getName()).replace("%n", System.getProperty("line.separator")).replace("%d", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
      if (localStackTraceElement2 == null)
      {
        str7 = str6.replace("%c", "noclass").replace("%F", "no").replace("%M", "nomethod").replace("%L", "0");
        str8 = str7.replace("%m", paramString2);
       
        if (paramString1 != null)
        	localList.add(new LogMsg(paramLogLevel, paramString1, str8));
      }
    }
    while (paramLogLevel.ordinal() < logLevel.ordinal())
      while (true)
      {
         str7 = str6.replace("%c", localStackTraceElement2.getClassName().replaceAll("^.*\\.", "")).replace("%F", localStackTraceElement2.getFileName()).replace("%M", localStackTraceElement2.getMethodName()).replace("%L", localStackTraceElement2.getLineNumber());
         continue;
         paramString1 = getTag(localStackTraceElement2);
      }
    StackTraceElement localStackTraceElement1 = getSTE();
    if ((paramString2 == null) || (paramString2.length() <= 4096))
    {
      switch ($SWITCH_TABLE$com$duolebo$tools$DLBLog$LogLevel()[paramLogLevel.ordinal()])
      {
      default:
        return;
      case 1:
        if (paramString1 != null);
        while (true)
        {
          Log.v(paramString1, getMsg(localStackTraceElement1, paramLogLevel.levelName, PValue_V, paramString2));
          return;
          paramString1 = getTag(localStackTraceElement1);
        }
      case 2:
        if (paramString1 != null);
        while (true)
        {
          Log.d(paramString1, getMsg(localStackTraceElement1, paramLogLevel.toString(), PValue_D, paramString2));
          return;
          paramString1 = getTag(localStackTraceElement1);
        }
      case 3:
        if (paramString1 != null);
        while (true)
        {
          Log.i(paramString1, getMsg(localStackTraceElement1, paramLogLevel.toString(), PValue_I, paramString2));
          return;
          paramString1 = getTag(localStackTraceElement1);
        }
      case 4:
        if (paramString1 != null);
        while (true)
        {
          Log.w(paramString1, getMsg(localStackTraceElement1, paramLogLevel.toString(), PValue_W, paramString2));
          return;
          paramString1 = getTag(localStackTraceElement1);
        }
      case 5:
      }
      if (paramString1 != null);
      while (true)
      {
        Log.e(paramString1, getMsg(localStackTraceElement1, paramLogLevel.toString(), PValue_E, paramString2));
        return;
        paramString1 = getTag(localStackTraceElement1);
      }
    }
    int j;
    for (int i = 0; i < paramString2.length(); i += 4096)
    {
      j = i + 4096;
      if (j > paramString2.length())
        j = paramString2.length();
      switch ($SWITCH_TABLE$com$duolebo$tools$DLBLog$LogLevel()[paramLogLevel.ordinal()])
      {
      default:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      }
    }
    if (paramString1 != null);
    for (String str5 = paramString1; ; str5 = getTag(localStackTraceElement1))
    {
      Log.v(str5, getMsg(localStackTraceElement1, paramLogLevel.toString(), PValue_V, paramString2.substring(i, j)));
      break;
    }
    if (paramString1 != null);
    for (String str4 = paramString1; ; str4 = getTag(localStackTraceElement1))
    {
      Log.d(str4, getMsg(localStackTraceElement1, paramLogLevel.toString(), PValue_D, paramString2.substring(i, j)));
      break;
    }
    if (paramString1 != null);
    for (String str3 = paramString1; ; str3 = getTag(localStackTraceElement1))
    {
      Log.i(str3, getMsg(localStackTraceElement1, paramLogLevel.toString(), PValue_I, paramString2.substring(i, j)));
      break;
    }
    if (paramString1 != null);
    for (String str2 = paramString1; ; str2 = getTag(localStackTraceElement1))
    {
      Log.w(str2, getMsg(localStackTraceElement1, paramLogLevel.toString(), PValue_W, paramString2.substring(i, j)));
      break;
    }
    if (paramString1 != null);
    for (String str1 = paramString1; ; str1 = getTag(localStackTraceElement1))
    {
      Log.e(str1, getMsg(localStackTraceElement1, paramLogLevel.toString(), PValue_E, paramString2.substring(i, j)));
      break;
    }
    */
  }

  public static void v(String paramString)
  {
    //printLog(null, LogLevel.INFO, paramString);
    Log.v(null, paramString);
  }

  public static void v(String paramString1, String paramString2)
  {
    //printLog(paramString1, LogLevel.INFO, paramString2);
    Log.v(paramString1, paramString2);
  }

  public static void w(String paramString)
  {
    //printLog(null, LogLevel.WARN, paramString);
    Log.w(null, paramString);
  }

  public static void w(String paramString1, String paramString2)
  {
    //printLog(paramString1, LogLevel.WARN, paramString2);
    Log.w(paramString1, paramString2);
  }

  public static void w(String paramString, Throwable paramThrowable)
  {
    //printLog(paramString, LogLevel.WARN, Log.getStackTraceString(paramThrowable));
    Log.w(paramString, Log.getStackTraceString(paramThrowable));
  }

  public static void w(Throwable paramThrowable)
  {
    //printLog(null, LogLevel.WARN, Log.getStackTraceString(paramThrowable));
    Log.w(null, Log.getStackTraceString(paramThrowable));
  }

  private static enum LogLevel
  {
	  VERBOSE, DEBUG,  INFO, WARN, ERROR, NOLOG
  }

  public static class LogMsg
  {
    DLBLog.LogLevel loglevel;
    String msg;
    String tag;

    public LogMsg(DLBLog.LogLevel paramLogLevel, String paramString1, String paramString2)
    {
      this.loglevel = paramLogLevel;
      this.tag = paramString1;
      this.msg = paramString2;
    }
  }
}
