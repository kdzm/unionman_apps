package com.unionman.settingwizard.ui;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

import com.unionman.settingwizard.R;
import com.unionman.settingwizard.util.BitmapCtl;

import android.app.IActivityManager;
import android.app.ActivityManagerNative;
import android.app.Activity;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LanguageSetupActivity extends Activity {
    private String TAG = "LanguageSetupActivity";
    private ArrayList<Locale> mLocale;
    private String mDefaultDisplayLanguage;
    private Boolean firstRun = true;
    private ListView languageList;
    private ImageView mReflectedView;
    private LinearLayout mContentView;
    private LanguageAdapter mLanguageAdapter = null;
    private Boolean mFirstRun = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_setup);

        int postition = 0;

        mDefaultDisplayLanguage = Locale.getDefault().getDisplayLanguage();

        languageList = (ListView) findViewById(R.id.lv_languages);
        Button nextStepBtn = (Button) findViewById(R.id.btn_next_step);
        Button lastStepBtn = (Button) findViewById(R.id.btn_last_step);
        nextStepBtn.setOnClickListener(new MyClickListener());
        lastStepBtn.setOnClickListener(new MyClickListener());
        nextStepBtn.requestFocus();
        mLocale = new ArrayList<Locale>();
        getNeedLanguage();
        mLanguageAdapter = new LanguageAdapter(LanguageSetupActivity.this);
        languageList.setAdapter(mLanguageAdapter);
        languageList.setOnItemClickListener(itemClickListener);
        languageList.setOnFocusChangeListener(focusListener);
        languageList.setOnItemSelectedListener(selectListener);

        postition = mLocale.indexOf(Locale.getDefault());
        languageList.setSelection(postition);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        mReflectedView = (ImageView) findViewById(R.id.imgv_reflection);
        mContentView = (LinearLayout) findViewById(R.id.content_layout);

        new BitmapCtl().setReflectionSync(mContentView, mReflectedView);

    }

    private View preItemView = null;

    public OnItemSelectedListener selectListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long arg3) {
            Log.v(TAG, ">>>>>>>>>>>>>>>>>Selected" + pos);
            if (languageList.isFocused()) {
                TextView tv = (TextView) view.findViewById(R.id.tv_language_name);
                ;
                tv.setTextColor(Color.WHITE);

                if (preItemView != null && view != preItemView) {
                    tv = (TextView) preItemView.findViewById(R.id.tv_language_name);
                    tv.setTextColor(Color.BLACK);
                }

                preItemView = view;
            } else {
                TextView tv = (TextView) view.findViewById(R.id.tv_language_name);
                ;
                tv.setTextColor(Color.BLACK);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                LinearLayout layoutTemp = (LinearLayout) parent.getChildAt(i);
                TextView tv = (TextView) layoutTemp.findViewById(R.id.tv_language_name);
                tv.setTextColor(Color.BLACK);
            }
        }
    };

    public OnFocusChangeListener focusListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                View view = (View) languageList.getSelectedView();
                if (view != null) {
                    TextView tv = (TextView) view.findViewById(R.id.tv_language_name);
                    tv.setTextColor(Color.WHITE);
                    preItemView = view;
                }
            } else {
                int count = ((ListView) v).getCount();
                for (int i = 0; i < count; i++) {
                    //LinearLayout layoutTemp = (LinearLayout) ((ListView) v).getChildAt(i);
                    TextView tv = (TextView) preItemView.findViewById(R.id.tv_language_name);
                    tv.setTextColor(Color.BLACK);
                }
            }
        }
    };

    public OnItemClickListener itemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
            try {
                IActivityManager am = ActivityManagerNative.getDefault();
                Configuration config = am.getConfiguration();

                Locale locale = mLocale.get(pos);
                config.locale = locale;
                setLanguage(config);
            } catch (RemoteException e) {

            }
        }

    };

    private void setLanguage(Configuration config) {
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            am.updateConfiguration(config);
            BackupManager.dataChanged("com.android.providers.settings");
        } catch (RemoteException e) {

        }
    }

    private Locale getLocale(String loc) {
        Locale[] locales = Locale.getAvailableLocales();

        for (Locale l : locales) {
            String tmp = l.getLanguage() + "_" + l.getCountry();
            if (loc.equals(tmp)) {
                return l;
            }
        }

        return null;
    }

    private void getNeedLanguage() {
        String[] string = this.getAssets().getLocales();
        String ISO3Country;
        String ISO3Language;
        for (String str : string) {
            Locale locale = getLocale(str);

            if (locale != null) {
                ISO3Country = locale.getISO3Country();
                ISO3Language = locale.getISO3Language();
                if ((ISO3Country.equals("USA") && ISO3Language.equals("eng"))
                        || (ISO3Country.equals("CHN") && ISO3Language.equals("zho"))
                        || (ISO3Country.equals("TWN") && ISO3Language.equals("zho"))) {
                    mLocale.add(locale);
                }
            }

            //test(locale);
        }

        sortLanuage();
    }

    private void sortLanuage() {
        Collections.sort(mLocale, LANUAGE_COMPARATOR);
    }

    private final static Comparator<Locale> LANUAGE_COMPARATOR =
            new Comparator<Locale>() {
                private final Collator collator = Collator.getInstance();

                public int compare(Locale locale1, Locale locale2) {
                    return collator.compare(locale1.getCountry(), locale2.getCountry());
                }
            };

    private void test(Locale locale) {
        if (locale != null) {
            Log.v(TAG, "==================================================");
            Log.v(TAG, " : " + locale.getCountry());
            Log.v(TAG, " : " + locale.getDisplayCountry());
            Log.v(TAG, " : " + locale.getDisplayLanguage());
            Log.v(TAG, " : " + locale.getDisplayName());
            Log.v(TAG, " : " + locale.getDisplayVariant());
            Log.v(TAG, " : " + locale.getISO3Country());
            Log.v(TAG, " : " + locale.getISO3Language());
            Log.v(TAG, " : " + locale.getLanguage());
            Log.v(TAG, " : " + locale.getVariant());
        }
    }

    class MyClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Intent intent;
            switch (id) {
                case R.id.btn_next_step:
                    intent = new Intent(LanguageSetupActivity.this, ScreenSetupActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.btn_last_step:
                    intent = new Intent(LanguageSetupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                getNeedLanguage();
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, getResources().getString(R.string.more_time_to_exit), Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class LanguageAdapter extends BaseAdapter {
        private Context myCon;

        public LanguageAdapter(Context con) {
            myCon = con;
        }

        public Object getItem(int arg0) {
            return arg0;
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        public int getCount() {
            return mLocale.size();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(myCon).inflate(R.layout.language_name_item, null);
                holder.lanuage = (TextView) convertView.findViewById(R.id.tv_language_name);
                holder.img = (ImageView) convertView.findViewById(R.id.imgv_language_select);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Locale locale = mLocale.get(position);
            if (locale != null) {
                if (locale.getISO3Country().equals("USA") && locale.getISO3Language().equals("eng")) {
                    holder.lanuage.setText(R.string.usa_eng);
                } else if (locale.getISO3Country().equals("CHN") && locale.getISO3Language().equals("zho")) {
                    holder.lanuage.setText(R.string.chn_zho);
                } else if (locale.getISO3Country().equals("TWN") && locale.getISO3Language().equals("zho")) {
                    holder.lanuage.setText(R.string.twn_zho);
                } else {
                    holder.lanuage.setText(locale.getDisplayLanguage());
                }
            }

            if (languageList.getSelectedItemPosition() == position) {
                holder.img.setImageResource(R.drawable.item_select);

            } else {
                convertView.setBackgroundDrawable(null);
            }
            return convertView;
        }
    }

    final class ViewHolder {
        TextView lanuage;
        ImageView img;
    }
}
