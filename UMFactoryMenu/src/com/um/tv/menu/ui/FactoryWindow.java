package com.um.tv.menu.ui;

import com.um.tv.menu.R;
import com.um.tv.menu.app.MyListAdapter;
import com.um.tv.menu.model.CombinatedModel;
import com.um.tv.menu.model.Model;
import com.um.tv.menu.utils.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.os.SystemProperties;

public class FactoryWindow extends FrameLayout {
    private static final String TAG = "FactoryWindow";
    private MyListView mListView = null;
    private CombinatedModel mRootModel = null;
    private boolean isShowing = false;
    private TextView mTvHeader = null;

    public FactoryWindow(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public FactoryWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public FactoryWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        View root = LayoutInflater.from(context).inflate(R.layout.factory_window_layout, this);
        mListView = (MyListView)root.findViewById(R.id.lv_main);

        mListView.setOnItemSelectedListener(mOnItemSelected);

        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mTvHeader = (TextView)root.findViewById(R.id.tv_header);

        mListView.requestFocus();
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setCallback(WindowCallback callback) {
        mWindowCallback = callback;
        mListView.setWindowCallback(callback);
    }

    public void updateItems(Model root) {
        if (mWindowCallback != null && root != null) {
            mWindowCallback.update(root);
        }
    }

    public void dismiss(){
    	Log.d(TAG,"myleon...dismiss mWindowCallback:"+mWindowCallback);
        if (mWindowCallback != null) {
            mWindowCallback.dismiss();
        }
    }
    
    public void updateTitle(String title) {
        mTvHeader.setText(title);
    }

    public void invalidateViews() {
        Log.d(TAG, "invalidateViews()");
        mListView.invalidateViews();
    }

    public void setShowing(boolean isShowing) {
        if (Utils.DEBUG) { Log.d(TAG, "setShowing--->" + isShowing); }
        //      if(isShowing){
        //          mListView.requestFocus();
        //      }
        if(isShowing)
        	SystemProperties.set("persist.sys.umfacshowed", "true");
        else
        	SystemProperties.set("persist.sys.umfacshowed", "false");
        
        this.isShowing = isShowing;
    }

    public void init() {
        updateItems(mRootModel);
        updateTitle(Utils.DisplayNameFactoryMenu);
    }

    private WindowCallback mWindowCallback = null;

    public interface WindowCallback {
        void show();
        void dismiss();
        void update(Model model);
        void changeValue(int direct);
        void gotoUpperLevel();
    }

    public void setAdapter(MyListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    public void setOnItemSelected(AdapterView.OnItemSelectedListener listener) {
        mListView.setOnItemSelectedListener(listener);
    }

    public void setOnItemClick(AdapterView.OnItemClickListener listener) {
        mListView.setOnItemClickListener(listener);
    }

    public void setSelection(int index) {
        mListView.setSelection(0);
    }

    public View getSelectedView() {
        return mListView.getSelectedView();
    }

    public int getSelectedItemPosition() {
        return mListView.getSelectedItemPosition();
    }

    public void printWindowSize() {
        Log.d("FactoryManager", "HeaderWidth:" + mTvHeader.getWidth() + "    HeaderHeight:" + mTvHeader.getHeight());
    }

    private AdapterView.OnItemSelectedListener mOnItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position,
                long id) {
            // TODO Auto-generated method stub
            View mInputText = view.findViewById(R.id.et_value);
            if(mInputText != null && mInputText instanceof EditText){
                mInputText.requestFocus();
            } else {
                View et = mListView.findViewById(R.id.et_value);
                if(et != null){
                    et.clearFocus();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub

        }
    };
}