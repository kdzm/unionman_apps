package com.unionman.dvbcitysetting.util;

import com.unionman.dvbcitysetting.InstallStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * String Utils
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2011-7-22
 */
public class StringUtils {

    /**
     * 把形如 38/80 这样的字符串转为整形数据的比值，如字符串"38/50"，如果以100作为最大值，则转化后返回78
     * @param percentStr
     * @param maxValue
     * @return
     */
    public static int percentStringToInt(String percentStr, int maxValue) {
        if (!isEmpty(percentStr) && percentStr.contains(InstallStatus.PERCENT_SEPARATOR)) {
            String[] strs = percentStr.split(InstallStatus.PERCENT_SEPARATOR);
            return Integer.parseInt(strs[0]) * maxValue / Integer.parseInt(strs[1]);
        }
        return 0;
    }

    /**
     * 把以给定分隔符分隔分割的String转为以String类型的集合，
     * @param string
     * @param separator 分隔符
     * @return
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
     * 把String类型的集合转为以String，各集合数据以给定分隔符分隔
     * @param stringList String类型的集合
     * @param separator 分隔符
     * @return
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
