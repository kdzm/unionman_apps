package com.um.tv.menu.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * String Utils
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2011-7-22
 */
public class StringUtils {

    /**
     * 鎶婁互缁欏畾鍒嗛殧绗﹀垎闅斿垎鍓茬殑String杞负浠tring绫诲瀷鐨勯泦鍚堬紝
     * @param string
     * @param separator 鍒嗛殧绗�     * @return
     */
    public static List<String> stringToList(String string, String separator) {
        List<String> strings = new ArrayList<String>();
        String[] stringArray = string.split(separator);
        for (String str : stringArray) {
            strings.add(str);
        }
        return strings;
    }

    /**
     * 鎶奡tring绫诲瀷鐨勯泦鍚堣浆涓轰互String锛屽悇闆嗗悎鏁版嵁浠ョ粰瀹氬垎闅旂鍒嗛殧
     * @param stringList String绫诲瀷鐨勯泦鍚�     * @param separator 鍒嗛殧绗�     * @return
     */
    public static String listToString(List<String> stringList, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : stringList) {
            stringBuilder.append(str).append(separator);
        }
        return stringBuilder.toString();
    }

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
}
