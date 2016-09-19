package cn.com.unionman.umtvsetting.powersave.util;

import android.content.Context;
import android.util.Log;

/**
 * including the selector view control operation data transform second level
 * menu
 *
 * @author wangchuanjian
 *
 */
public class Util {

    /**
     * get index of Parameters from array
     *
     * @param mode
     * @param arrays
     * @return index of Parameters
     */
    public static int getIndexFromArray(int mode, int[][] arrays) {
        int num = 0;
        if (Constant.LOG_TAG) {
            Log.i("getIndexFromArray", "getIndexFromArray");
        }
        for (int i = 0; i < arrays.length; i++) {
            if (Constant.LOG_TAG) {
                Log.i("getIndexFromArray", "getIndexFromArray=" + i);
            }
            if (arrays[i][0] == mode) {
                num = i;
                return num;
            }
        }
        return num;
    }

    /**
     * create array of parameters
     *
     * @param arrays
     * @return array of Parameters
     */
    public static int[] createArrayOfParameters(int[][] arrays) {
        int[] num = new int[arrays.length];
        if (Constant.LOG_TAG) {
            Log.i("createArrayOfParameters", "createArrayOfParameters");
        }
        for (int i = 0; i < arrays.length; i++) {
            if (Constant.LOG_TAG) {
                Log.i("createArrayOfParameters", "createArrayOfParameters=" + i);
            }
            num[i] = arrays[i][1];
        }
        return num;
    }
    
    
    /** 
     * ����ֻ�ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * ����ֻ�ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
    public  static boolean isHomeSourClick=false;
}
