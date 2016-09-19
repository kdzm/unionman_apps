package com.unionman.dvbcitysetting.util;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.unionman.dvbcitysetting.data.City;
import com.unionman.dvbcitysetting.data.ConfigContent;
import com.unionman.dvbcitysetting.data.Province;
import com.unionman.dvbcitysetting.data.State;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2014/11/3.
 */
public class CitySettingHelper {
    public final static String PACKAGES_SEPARATOR = ";";
    public final static String DEFAULT_CITY_NAME;
    private final static String SDCARD_ROOT = Environment.getExternalStorageDirectory().getPath();
    public final static String SDCARD_LOCAL_ROOT = SDCARD_ROOT + "/vendor/dvb/install_packages";
    public final static String SYSTEM_LOCAL_ROOT = "/system/vendor/dvb/install_packages";
    public final static String DEFAULT_CITY_FLODER = "default_city";
    public final static String CURRENT_CITY_CONFIG_FILE = "current_city_config_file";
    public final static String INSTALLING_CITY_CONFIG_FILE = "installing_city_config_file";
    public final static String INSTALLED_CITY_CONFIG_FILE = "installed_city_config_file";
    public final static String CONFIG_FILE = "config.xml";
    public final static String LAST_INSTALLED_PACKAGE = "last_installed_package";
    public final static String DVB_ENABLE = "persist.sys.dvb.enabled";
    public final static String DVB_INSTALLED = "persist.sys.dvb.installed";
    public final static String DVB_CAS_TYPE = "persist.sys.dvb.cas.type";
    public final static String DVB_LOCAL_CAS_TYPES = "persist.sys.dvb.cas.supports";
    public final static String DVB_CITY_NAME = "persist.sys.dvb.cas.area";
    public final static String DVB_PLAYER_SERVICE = "dvbserver";
    public final static String KEY_LAUNCH_TYPE = "launch_type";
    public final static String KEY_MAIN_FREQ = "persist.sys.dvb.main_freq";
    static {
        ConfigContent configContent = null;
        try {
            configContent = getConfigFileContent(
                    new File(SYSTEM_LOCAL_ROOT + File.separator + DEFAULT_CITY_FLODER + File.separator + CONFIG_FILE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        DEFAULT_CITY_NAME = getCityFullName(configContent);
    }

    public static String getCityFullName(ConfigContent configContent){
        StringBuilder stringBuilder = new StringBuilder();
        if (configContent != null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(configContent.getProvince()).append(CitySettingHelper.PACKAGES_SEPARATOR)
                    .append(configContent.getState()).append(CitySettingHelper.PACKAGES_SEPARATOR)
                    .append(configContent.getCity());

        }
        return stringBuilder.toString();
    }

    public static int[] getIndex(List<Province> provinces, String name) {
        Log.d("DEFAULT_CITY_NAME: ", "" + DEFAULT_CITY_NAME);
        String[] names = name.split(PACKAGES_SEPARATOR);
        String provinceName = names[0];
        String stateName = names[1];
        String cityName = "";
        int[] index;
        if (names.length >= 3) {
            cityName = names[2];
            index = new int[]{0, 0, 0};
        } else {
            index = new int[]{0, 0};
        }
        for (Province province : provinces) {
            if (province.getName().equals(provinceName)) {
                index[0] = provinces.indexOf(province);
                List<State> states = province.getStates();
                Log.d("citycity", "getIndex: " + province.getName());
                for (State state : states) {
                    if (state.getName().equals(stateName)) {
                        index[1] = states.indexOf(state);

                        if (names.length >= 3) {
                            List<City> cities = state.getCities();

                            Log.d("citycity", "getIndex: " + state.getName());
                            for (City city : cities) {
                                if (city.getName().equals(cityName)) {
                                    index[2] = cities.indexOf(city);

                                    Log.d("citycity", "getIndex: " + city.getName());
                                    Log.d("citycity", "getIndex0: " + index[0]);
                                    Log.d("citycity", "getIndex1: " + index[1]);
                                    Log.d("citycity", "getIndex2: " + index[2]);
                                    return index;
                                }
                            }
                        }
                    }
                }
            }
        }

        return index;
    }

    private static boolean isTheSameState(ConfigContent configContent1, ConfigContent configContent2) {
        if (isTheSameProvince(configContent1, configContent2)
                && ((configContent1.getState().equals(configContent2.getState())
                    || configContent1.getState().contains(configContent2.getState())
                    || configContent2.getState().contains(configContent1.getState())))) {
                return true;
        }
        return false;
    }
    private static boolean isTheSameProvince(ConfigContent configContent1, ConfigContent configContent2) {
        if (configContent1.getProvince().equals(configContent2.getProvince())
                || configContent1.getProvince().contains(configContent2.getProvince())
                || configContent2.getProvince().equals(configContent1.getProvince())) {
            return true;
        }

        return false;
    }

    public static List<Province> createProvince(List<ConfigContent> configContents) {
        List<Province> provinces = new ArrayList<Province>();
        Collections.sort(configContents, PROVINCE_COMPARATOR);
        Collections.sort(configContents, STATE_COMPARATOR);
        Collections.sort(configContents, CITY_COMPARATOR);
        configContents.add(new ConfigContent());

        Province province = new Province();
        State state = new State();
        City city = new City();
        for (int i = 0, size = configContents.size(); i < size - 1; i++) {
            if ((configContents.get(i).getProvince().equals(configContents.get(i + 1).getProvince()))
                    && (configContents.get(i).getState().equals(configContents.get(i + 1).getState()))
                    && (configContents.get(i).getCity().equals(configContents.get(i + 1).getCity()))) {
                // do nothing, skip the same one
            }else if ((configContents.get(i).getProvince().equals(configContents.get(i + 1).getProvince()))
                    && (configContents.get(i).getState().equals(configContents.get(i + 1).getState()))) {
                city.setName(configContents.get(i).getCity());
                city.setPackages(configContents.get(i).getPackagesPath());
                city.setConfigFilePath(configContents.get(i).getConfigFilePath());
                state.addCity(city);
                city = new City();
            } else if (configContents.get(i).getProvince().equals(configContents.get(i + 1).getProvince())) {
                city.setName(configContents.get(i).getCity());
                city.setPackages(configContents.get(i).getPackagesPath());
                city.setConfigFilePath(configContents.get(i).getConfigFilePath());
                state.addCity(city);
                city = new City();

                state.setName(configContents.get(i).getState());
                state.setConfigFilePath(configContents.get(i).getConfigFilePath());
                province.addState(state);
                state = new State();
            } else {
                city.setName(configContents.get(i).getCity());
                city.setPackages(configContents.get(i).getPackagesPath());
                city.setConfigFilePath(configContents.get(i).getConfigFilePath());
                state.addCity(city);
                city = new City();
                state.setName(configContents.get(i).getState());
                state.setConfigFilePath(configContents.get(i).getConfigFilePath());
                province.addState(state);
                state = new State();

                province.setName(configContents.get(i).getProvince());
                provinces.add(province);
                province = new Province();
            }
        }

        return provinces;
    }

    private final static Comparator<ConfigContent> PROVINCE_COMPARATOR =
            new Comparator<ConfigContent>() {
                private final Collator collator = Collator.getInstance();

                public int compare(ConfigContent locale1, ConfigContent locale2) {
                    return collator.compare(locale1.getProvince(), locale2.getProvince());
                }
            };

    private final static Comparator<ConfigContent> STATE_COMPARATOR =
            new Comparator<ConfigContent>() {
                private final Collator collator = Collator.getInstance();

                public int compare(ConfigContent locale1, ConfigContent locale2) {
                    if (locale1.getProvince().equals(locale2.getProvince())) {
                        return collator.compare(locale1.getState(), locale2.getState());
                    }
                    return 0;
                }
            };

    private final static Comparator<ConfigContent> CITY_COMPARATOR =
            new Comparator<ConfigContent>() {
                private final Collator collator = Collator.getInstance();

                public int compare(ConfigContent locale1, ConfigContent locale2) {
                    if (locale1.getProvince().equals(locale2.getProvince())
                            && locale1.getState().equals(locale2.getState())) {
                        return collator.compare(locale1.getCity(), locale2.getCity());
                    }
                    return 0;
                }
            };

    public static List<ConfigContent> getAllConfigFileContent(List<File> files) {
        ArrayList<ConfigContent> configContents = new ArrayList<ConfigContent>();
        for (File file : files) {
            try {
                ConfigContent configContent = getConfigFileContent(file);
                configContents.add(configContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return configContents;
    }

    /**
     * 根据城市的配置文件的路径得出城市的拼音名称
     * @param path like /sdcard/vendor/dvb/install_packages/shanxi/shengwang/config.xml
     * @return
     */
    public static String getCityNameByPath(String path) {
        if (StringUtils.isBlank(path)) {
            return "";
        }

        String name = "";
        if (path.startsWith(SDCARD_LOCAL_ROOT)) {
            String temp = path.replace(SDCARD_LOCAL_ROOT, "").replace(CONFIG_FILE, "");
            name = temp.substring(1, temp.length() - 1).replace("/", "_");
        } else if (path.startsWith(SYSTEM_LOCAL_ROOT)) {
            String temp = path.replace(SYSTEM_LOCAL_ROOT, "").replace(CONFIG_FILE, "");
            name = temp.substring(1, temp.length() - 1).replace("/", "_");
        }

        return name;
    }

    public static ConfigContent getConfigFileContent(File configFile) throws Exception{
        ConfigContent configContent = null;
        List<String> packages = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new FileInputStream(configFile), "UTF-8");
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    String str = parser.getName();
                    if ("cityconfig".equals(str)) {
                        configContent = new ConfigContent();
                        packages = new ArrayList<String>();
                    } else if ("city".equals(str)) {
                        if (configContent != null) {
                            configContent.setProvince(parser.getAttributeValue(null, "province"));
                            configContent.setState(parser.getAttributeValue(null, "state"));
                            String cityName = parser.getAttributeValue(null, "city");
                            if (StringUtils.isBlank(cityName)) {
                                configContent.setCity("- - -");
                            } else {
                                configContent.setCity(cityName);
                            }
                            configContent.setCityCode(parser.getAttributeValue(null, "code"));
                        }
                    } else if("feature".equals(str)) {
                        assert configContent != null;
                        configContent.setMainFreq(Integer.parseInt(parser.getAttributeValue(null, "mainfreq")));
                        String caSupport = parser.getAttributeValue(null, "casupport");
                        String[] _caSupport = caSupport.split(";");
                        List<String> caTypes = new ArrayList<String>();
                        Collections.addAll(caTypes, _caSupport);
                        configContent.setCaSupport(caTypes);
                        configContent.setCaSupportStr(caSupport);
                    }else if ("package".equals(str)) {
                        String value = parser.getAttributeValue(0);
                        if (!value.startsWith("/")) {
                            value = configFile.getParent() + File.separator + FileUtils.getFileName(value);
                        }
                        assert packages != null;
                        packages.add(value);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    String endTag = parser.getName();
                    if ("cityconfig".equals(endTag)) {
                        assert configContent != null;
                        configContent.setPackagesPath(packages);
                        configContent.setConfigFilePath(configFile.getPath());
                        return configContent;
                    }
                    break;
                default:
                    break;
            }
            event = parser.next();
        }

        return null;
    }
}
