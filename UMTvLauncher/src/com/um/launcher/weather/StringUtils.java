package com.um.launcher.weather;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.um.launcher.R;
import android.util.Log;

/**
 * String Utils
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2011-7-22
 */
public class StringUtils {

    /**
     * is null or its length is 0 or it is made by space
     * 
     * <pre>
     * isBlank(null) = true;
     * isBlank(&quot;&quot;) = true;
     * isBlank(&quot;  &quot;) = true;
     * isBlank(&quot;a&quot;) = false;
     * isBlank(&quot;a &quot;) = false;
     * isBlank(&quot; a&quot;) = false;
     * isBlank(&quot;a b&quot;) = false;
     * </pre>
     * 
     * @param str
     * @return if string is null or its size is 0 or it is made by space, return true, else return false.
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    /**
     * is null or its length is 0
     * 
     * <pre>
     * isEmpty(null) = true;
     * isEmpty(&quot;&quot;) = true;
     * isEmpty(&quot;  &quot;) = false;
     * </pre>
     * 
     * @param str
     * @return if string is null or its size is 0, return true, else return false.
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }

    /**
     * compare two string
     * 
     * @param actual
     * @param expected
     * @return
     * @see ObjectUtils#isEquals(Object, Object)
     */
    public static boolean isEquals(String actual, String expected) {
        return true;//ObjectUtils.isEquals(actual, expected);
    }

    /**
     * null string to empty string
     * 
     * <pre>
     * nullStrToEmpty(null) = &quot;&quot;;
     * nullStrToEmpty(&quot;&quot;) = &quot;&quot;;
     * nullStrToEmpty(&quot;aa&quot;) = &quot;aa&quot;;
     * </pre>
     * 
     * @param str
     * @return
     */
    public static String nullStrToEmpty(String str) {
        return (str == null ? "" : str);
    }

    /**
     * capitalize first letter
     * 
     * <pre>
     * capitalizeFirstLetter(null)     =   null;
     * capitalizeFirstLetter("")       =   "";
     * capitalizeFirstLetter("2ab")    =   "2ab"
     * capitalizeFirstLetter("a")      =   "A"
     * capitalizeFirstLetter("ab")     =   "Ab"
     * capitalizeFirstLetter("Abc")    =   "Abc"
     * </pre>
     * 
     * @param str
     * @return
     */
    public static String capitalizeFirstLetter(String str) {
        if (isEmpty(str)) {
            return str;
        }

        char c = str.charAt(0);
        return (!Character.isLetter(c) || Character.isUpperCase(c)) ? str
            : new StringBuilder(str.length()).append(Character.toUpperCase(c)).append(str.substring(1)).toString();
    }

    /**
     * encoded in utf-8
     * 
     * <pre>
     * utf8Encode(null)        =   null
     * utf8Encode("")          =   "";
     * utf8Encode("aa")        =   "aa";
     * utf8Encode("°¡°¡°¡°¡")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
     * </pre>
     * 
     * @param str
     * @return
     * @throws java.io.UnsupportedEncodingException if an error occurs
     */
    public static String utf8Encode(String str) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UnsupportedEncodingException occurred. ", e);
            }
        }
        return str;
    }

    /**
     * encoded in utf-8, if exception, return defultReturn
     * 
     * @param str
     * @param defultReturn
     * @return
     */
    public static String utf8Encode(String str, String defultReturn) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return defultReturn;
            }
        }
        return str;
    }

    /**
     * get innerHtml from href
     * 
     * <pre>
     * getHrefInnerHtml(null)                                  = ""
     * getHrefInnerHtml("")                                    = ""
     * getHrefInnerHtml("mp3")                                 = "mp3";
     * getHrefInnerHtml("&lt;a innerHtml&lt;/a&gt;")                    = "&lt;a innerHtml&lt;/a&gt;";
     * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
     * getHrefInnerHtml("&lt;a&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
     * getHrefInnerHtml("&lt;a href="baidu.com"&gt;innerHtml&lt;/a&gt;")               = "innerHtml";
     * getHrefInnerHtml("&lt;a href="baidu.com" title="baidu"&gt;innerHtml&lt;/a&gt;") = "innerHtml";
     * getHrefInnerHtml("   &lt;a&gt;innerHtml&lt;/a&gt;  ")                           = "innerHtml";
     * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                      = "innerHtml";
     * getHrefInnerHtml("jack&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                  = "innerHtml";
     * getHrefInnerHtml("&lt;a&gt;innerHtml1&lt;/a&gt;&lt;a&gt;innerHtml2&lt;/a&gt;")        = "innerHtml2";
     * </pre>
     * 
     * @param href
     * @return <ul>
     * <li>if href is null, return ""</li>
     * <li>if not match regx, return source</li>
     * <li>return the last string that match regx</li>
     * </ul>
     */
    public static String getHrefInnerHtml(String href) {
        if (isEmpty(href)) {
            return "";
        }

        String hrefReg = ".*<[\\s]*a[\\s]*.*>(.+?)<[\\s]*/a[\\s]*>.*";
        Pattern hrefPattern = Pattern.compile(hrefReg, Pattern.CASE_INSENSITIVE);
        Matcher hrefMatcher = hrefPattern.matcher(href);
        if (hrefMatcher.matches()) {
            return hrefMatcher.group(1);
        }
        return href;
    }

/**
     * process special char in html
     * 
     * <pre>
     * htmlEscapeCharsToString(null) = null;
     * htmlEscapeCharsToString("") = "";
     * htmlEscapeCharsToString("mp3") = "mp3";
     * htmlEscapeCharsToString("mp3&lt;") = "mp3<";
     * htmlEscapeCharsToString("mp3&gt;") = "mp3\>";
     * htmlEscapeCharsToString("mp3&amp;mp4") = "mp3&mp4";
     * htmlEscapeCharsToString("mp3&quot;mp4") = "mp3\"mp4";
     * htmlEscapeCharsToString("mp3&lt;&gt;&amp;&quot;mp4") = "mp3\<\>&\"mp4";
     * </pre>
     * 
     * @param source
     * @return
     */
    public static String htmlEscapeCharsToString(String source) {
        return StringUtils.isEmpty(source) ? source : source.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                                                            .replaceAll("&amp;", "&").replaceAll("&quot;", "\"");
    }

    /**
     * transform half width char to full width char
     * 
     * <pre>
     * fullWidthToHalfWidth(null) = null;
     * fullWidthToHalfWidth("") = "";
     * fullWidthToHalfWidth(new String(new char[] {12288})) = " ";
     * fullWidthToHalfWidth("£¡£¢££¡ç£¥£¦) = "!\"#$%&";
     * </pre>
     * 
     * @param s
     * @return
     */
    public static String fullWidthToHalfWidth(String s) {
        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == 12288) {
                source[i] = ' ';
                // } else if (source[i] == 12290) {
                // source[i] = '.';
            } else if (source[i] >= 65281 && source[i] <= 65374) {
                source[i] = (char)(source[i] - 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    /**
     * transform full width char to half width char
     * 
     * <pre>
     * halfWidthToFullWidth(null) = null;
     * halfWidthToFullWidth("") = "";
     * halfWidthToFullWidth(" ") = new String(new char[] {12288});
     * halfWidthToFullWidth("!\"#$%&) = "£¡£¢££¡ç£¥£¦";
     * </pre>
     * 
     * @param s
     * @return
     */
    public static String halfWidthToFullWidth(String s) {
        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == ' ') {
                source[i] = (char)12288;
                // } else if (source[i] == '.') {
                // source[i] = (char)12290;
            } else if (source[i] >= 33 && source[i] <= 126) {
                source[i] = (char)(source[i] + 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }
    
	public static int[] getWeaResByWeather(String weather) {
		String[] strs = weather.split("×ª|µ½");
		int[] resIds = new int[strs.length];
		for (int i = 0; i < strs.length; i++) {
			resIds[i] = getWeaResByWeather1(strs[i]);
			Log.i("info", "split weather£º" + strs[i]);
		}
		if (resIds.length == 3) {
			if (resIds[0] == 0) {
				int[] newResids = new int[2];
				newResids[0] = resIds[1];
				newResids[1] = resIds[2];
				resIds = newResids;
			}
		} else if (resIds.length == 1) {
			int[] newResids = new int[2];
			newResids[0] = resIds[0];
			newResids[1] = 0;
			resIds = newResids;
		}
		return resIds;
	}
	
	public static int getWeaResByWeather1(String weather) {
		Log.i("info", "getWeaResByWeather1 weather£º" + weather);
		
		if (weather.equals("Òõ")) {
			return R.drawable.ic_weather_cloudy_l;
		} else if (weather.equals("¶àÔÆ")) {
			return R.drawable.ic_weather_partly_cloudy_l;
		} else if (weather.equals("Çç")) {
			return R.drawable.ic_weather_clear_day_l;
		} else if (weather.equals("Ð¡Óê")) {
			return R.drawable.ic_weather_chance_of_rain_l;
		} else if (weather.equals("ÖÐÓê")) {
			return R.drawable.ic_weather_rain_xl;
		} else if (weather.equals("´óÓê")) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals("±©Óê")) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals("´ó±©Óê")) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals("ÌØ´ó±©Óê")) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals("ÕóÓê")) {
			return R.drawable.ic_weather_chance_storm_l;
		} else if (weather.equals("À×ÕóÓê")) {
			return R.drawable.ic_weather_thunderstorm_l;
		} else if (weather.equals("Ð¡Ñ©")) {
			return R.drawable.ic_weather_chance_snow_l;
		} else if (weather.equals("ÖÐÑ©")) {
			return R.drawable.ic_weather_flurries_l;
		} else if (weather.equals("´óÑ©")) {
			return R.drawable.ic_weather_snow_l;
		} else if (weather.equals("±©Ñ©")) {
			return R.drawable.ic_weather_snow_l;
		} else if (weather.equals("±ù±¢")) {
			return R.drawable.ic_weather_icy_sleet_l;
		} else if (weather.equals("Óê¼ÐÑ©")) {
			return R.drawable.ic_weather_icy_sleet_l;
		} else if (weather.equals("·ç")) {
			return R.drawable.ic_weather_windy_l;
		} else if (weather.equals("Áú¾í·ç")) {
			return R.drawable.ic_weather_windy_l;
		} else if (weather.equals("Îí")) {
			return R.drawable.ic_weather_fog_l;
		}
		return 0;
	}
	
	public static int[] myGetWeaResByWeather(String weather) {
		String splitStr = "\u8f6c\u007c\u5230";
		String[] strs = weather.split(splitStr);
		int[] resIds = new int[strs.length];
		for (int i = 0; i < strs.length; i++) {
			resIds[i] = myGetWeaResByWeather1(strs[i]);
			Log.i("info", "split weather£º" + strs[i]);
		}
		
		if (resIds.length == 3) {
			if (resIds[0] == 0) {
				int[] newResids = new int[2];
				newResids[0] = resIds[1];
				newResids[1] = resIds[2];
				resIds = newResids;
			}
		} else if (resIds.length == 1) {
			int[] newResids = new int[2];
			newResids[0] = resIds[0];
			newResids[1] = 0;
			resIds = newResids;
		}
		return resIds;
	}
	
	public static int myGetWeaResByWeather1(String weather) {
		Log.i("info", "myGetWeaResByWeather1 weather£º" + weather);
		
		String cloudyStr = "\u9634";
		String partlyCloudyStr = "\u591A\u4E91";
		String clearDayStr = "\u6674";
		String chanceOfRainStr = "\u5C0F\u96E8";
		String rainStr = "\u4E2D\u96E8";
		String heavyRainStr = "\u5927\u96E8";
		String heavyRainXStr = "\u66B4\u96E8";
		String torrentialRainStr = "\u5927\u66B4\u96E8";
		String rainstormStr = "\u7279\u5927\u66B4\u96E8";
		String showerStr = "\u9635\u96E8";
		String thunderShowerStr = "\u96F7\u9635\u96E8";
		String chanceSnowStr = "\u5C0F\u96EA";
		String moderateSnowStr = "\u4E2D\u96EA";
		String SnowStr = "\u5927\u96EA";
		String heavySnowStr = "\u66B4\u96EA";
		String icyStr = "\u51B0\u96F9";
		String icySleetStr = "\u96E8\u5939\u96EA";
		String windyStr = "\u98CE";
		String tornadoStr = "\u9F99\u5377\u98CE";
		String fogStr = "\u96FE";
		
		if (weather.equals(cloudyStr)) {
			return R.drawable.ic_weather_cloudy_l;
		} else if (weather.equals(partlyCloudyStr)) {
			return R.drawable.ic_weather_partly_cloudy_l;
		} else if (weather.equals(clearDayStr)) {
			return R.drawable.ic_weather_clear_day_l;
		} else if (weather.equals(chanceOfRainStr)) {
			return R.drawable.ic_weather_chance_of_rain_l;
		} else if (weather.equals(rainStr)) {
			return R.drawable.ic_weather_rain_xl;
		} else if (weather.equals(heavyRainStr)) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals(heavyRainXStr)) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals(torrentialRainStr)) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals(rainstormStr)) {
			return R.drawable.ic_weather_heavy_rain_l;
		} else if (weather.equals(showerStr)) {
			return R.drawable.ic_weather_chance_storm_l;
		} else if (weather.equals(thunderShowerStr)) {
			return R.drawable.ic_weather_thunderstorm_l;
		} else if (weather.equals(chanceSnowStr)) {
			return R.drawable.ic_weather_chance_snow_l;
		} else if (weather.equals(moderateSnowStr)) {
			return R.drawable.ic_weather_flurries_l;
		} else if (weather.equals(SnowStr)) {
			return R.drawable.ic_weather_snow_l;
		} else if (weather.equals(heavySnowStr)) {
			return R.drawable.ic_weather_snow_l;
		} else if (weather.equals(icyStr)) {
			return R.drawable.ic_weather_icy_sleet_l;
		} else if (weather.equals(icySleetStr)) {
			return R.drawable.ic_weather_icy_sleet_l;
		} else if (weather.equals(windyStr)) {
			return R.drawable.ic_weather_windy_l;
		} else if (weather.equals(tornadoStr)) {
			return R.drawable.ic_weather_windy_l;
		} else if (weather.equals(fogStr)) {
			return R.drawable.ic_weather_fog_l;
		}
		return 0;
	}
	
	/** 
	  * °ÑÖÐÎÄ×ª³ÉUnicodeÂë 
	  * @param str 
	  * @return 
	  */  
	public static String chineseToUnicode(final String str){  
	     String result="", tmp = "";
	     
	     tmp = gb2312ToUtf8(str);
	     result = gb2312ToUnicode(tmp);
	     return result;  
	}
	
    //GB2312->UTF-8  
    public static String gb2312ToUtf8(String str) {   
  
        String urlEncode = "" ;   
  
        try {   
  
            urlEncode = URLEncoder.encode (str, "UTF-8" );   
  
        } catch (UnsupportedEncodingException e) {   
  
            e.printStackTrace();   
        }   
  
        return urlEncode;   
    } 
    
    //GB2312->unicode  
    public static String gb2312ToUnicode(String str) {   
  
        String urlEncode = "";   
  
        try {   
  
            urlEncode = URLEncoder.encode (str, "unicode");   
  
        } catch (UnsupportedEncodingException e) {   
  
            e.printStackTrace();   
        }   
  
        return urlEncode;   
    } 
}

