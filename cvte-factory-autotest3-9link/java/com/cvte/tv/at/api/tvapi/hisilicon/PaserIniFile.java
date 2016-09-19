package com.cvte.tv.at.api.tvapi.hisilicon;


import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class PaserIniFile {

    private String charSet = "UTF-8";

    private Map<String, String> sections = new HashMap<String, String>();

    static PaserIniFile mPaserIniFile = null ;

    public void set(String key, String value) {
        sections.put(key.toLowerCase(), value);
    }


    public String get(String key) {
        return sections.get(key.toLowerCase());
    }


    /**
     * 当前操作的文件对像
     */
    private File file = null;

    private PaserIniFile() {
        this.file = new File("/tvconfig/config/panel/PNL_CUSTOMER_DEFAULT.ini");
        sections.put("m_bpanellvds_ti_mode","0");
        sections.put("m_bpanelswapport","0");
        sections.put("m_uctibitmode","0");
        sections.put("m_bpaneldither","0");
        sections.put("mirror_osd_type","0");
        sections.put("swing_level","0");
       // sections.put("gammaTableNo","0");
        sections.put("gammatableno","0");
        sections.put("osdWidth","1920");
        sections.put("osdHeight","1080");
        initFromFile(file);

        Log.d("PaserIniFile", "sections size = " + sections.size());
        Log.d("PaserIniFile", "m_bpanellvds_ti_mode = " + get("m_bpanellvds_ti_mode"));
        Log.d("PaserIniFile", "m_bpanelswapport = " + get("m_bpanelswapport"));
        Log.d("PaserIniFile", "m_uctibitmode = " + get("m_uctibitmode"));
        Log.d("PaserIniFile", "m_bpaneldither = " + get("m_bpaneldither"));
        Log.d("PaserIniFile", "mirror_osd_type = " + get("mirror_osd_type"));
        Log.d("PaserIniFile", "swing_level = " + get("swing_level"));
        Log.d("PaserIniFile", "gammaTableNo = " + get("gammaTableNo"));
        Log.d("PaserIniFile", "osdwidth = " + get("osdwidth"));
        Log.d("PaserIniFile", "osdheight = " + get("osdheight"));
    }

    public static PaserIniFile getInstance() {

        if(mPaserIniFile == null ) {
            mPaserIniFile = new PaserIniFile();
        }
        return mPaserIniFile ;
    }


    /**
     * 从输入流初始化IniFile
     *
     * @param inputStream
     */
    private void initFromInputStream(InputStream inputStream) {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, charSet));
            toIniFile(bufferedReader);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件初始化IniFile
     *
     * @param file
     */
    private void initFromFile(File file) {
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            toIniFile(bufferedReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从BufferedReader 初始化IniFile
     *
     * @param bufferedReader
     */
    private void toIniFile(BufferedReader bufferedReader) {
        String strLine;
        int count = sections.size() ;
        try {
            while (((strLine = bufferedReader.readLine()) != null) && (count >0)) {
                Log.d("toIniFile", "strLine : " + strLine);
                strLine=strLine.replace(" ", "");
                Log.d("toIniFile", "after delete  kongge , strLine : " + strLine);
                if (strLine == null) continue;

                String[] keyValue = strLine.split("=");

                if( !keyValue[0].equalsIgnoreCase("m_bPanelLVDS_TI_MODE") && !keyValue[0].equalsIgnoreCase("m_bPanelSwapPort") &&!keyValue[0].equalsIgnoreCase("m_ucTiBitMode")
                  && !keyValue[0].equalsIgnoreCase("m_bPanelDither") && !keyValue[0].equalsIgnoreCase("MIRROR_OSD_TYPE")&& !keyValue[0].equalsIgnoreCase("SWING_LEVEL")
                  && !keyValue[0].equalsIgnoreCase("gammaTableNo")&& !keyValue[0].equalsIgnoreCase("osdWidth")&& !keyValue[0].equalsIgnoreCase("osdHeight"))
                continue;

                Log.d("toIniFile", "keyValue.lenth=" + keyValue.length);
                if (keyValue.length == 2) {
                   if(keyValue[1].contains("#")) {
                         String[] keyValue2 = keyValue[1].split("#");
                    if (keyValue2.length == 2) {
                        Log.d("toIniFile", "keyValue2[0]=" + keyValue2[0]);
                        Log.d("toIniFile", "keyValue2[1]=" + keyValue2[1]);
                    }
                       keyValue[1] = keyValue2[0] ;
                   }
                    sections.put(keyValue[0].replace(" ", "").toLowerCase(), keyValue[1].replace(" ", "").replace(";",""));
                    Log.d("toIniFile", "keyValue[0]=" + keyValue[0].replace(" ", "") + " , keyValue[1]=" + keyValue[1].replace(" ", ""));
                    count--; //use for reduce loop times
                }
                if(keyValue[0].equals("Sub_pq_path")) break;
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
