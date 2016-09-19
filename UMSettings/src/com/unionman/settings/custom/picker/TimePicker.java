/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.unionman.settings.custom.picker;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.unionman.settings.R;

/**
 * A view for selecting 12-digit number which indicate a IP. It consist of
 * four IpfiledPicker. Each IpfieldPicker is a 3-digit. Calling getIp() can get
 * the stand form of IP xxx.xxx.xxx.xxx.
 */
public class TimePicker extends FrameLayout {

    // ui components

	/**
	 * First field of Time.
	 */
    private final HourfieldPicker mFirstField;

    /**
     * Second field of Time.
     */
    private final MinutefieldPicker mSecondField;

    /**
     * Third field of Time.
     */
    private final MinutefieldPicker mThirdField;

    /**
     * Create a new IpPicker which consist of four IpfieldPickers.
     *
     * @param context the application environment
     */
    public TimePicker(Context context) {
        this(context, null);
        this.setFocusable(false);
    }

    /**
     * Create a new IpPicker which consist of four IpfieldPickers.
     *
     * @param context the application environment
     * @param attrs a collection of attributes
     */
    public TimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0/*R.attr.TriplePickerStyle*/);
    }

    /**
     * Create a new IpPicker which consist of four IpfieldPickers.
     *
     * @param context the application environment
     * @param attrs a collection of attributes
     * @param defStyle The default style to apply to this view.
     */
    public TimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.date_time_picker, this, true);
        
        //Hundreds
        mFirstField = (HourfieldPicker) findViewById(R.id.first);
        mSecondField = (MinutefieldPicker) findViewById(R.id.second);
        mThirdField = (MinutefieldPicker) findViewById(R.id.third);

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickerClickListener.onClick(v);
            }
        };
        mFirstField.getFirstNumberPicker().getNumberText().setOnClickListener(onClickListener);
        mFirstField.getSecondNumberPicker().getNumberText().setOnClickListener(onClickListener);
        mSecondField.getFirstNumberPicker().getNumberText().setOnClickListener(onClickListener);
        mSecondField.getSecondNumberPicker().getNumberText().setOnClickListener(onClickListener);
        mThirdField.getFirstNumberPicker().getNumberText().setOnClickListener(onClickListener);
        mThirdField.getSecondNumberPicker().getNumberText().setOnClickListener(onClickListener);
    }
    
    /*
     * @see android.view.View#onFocusChanged(boolean, int, android.graphics.Rect)
     */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
    		Rect previouslyFocusedRect) {
    	// TODO Auto-generated method stub
    	Log.d("Test", "IpPicker onFoucusChanged");
    	super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }
    
    
    /*
     * @see android.view.ViewGroup#focusSearch(android.view.View, int)
     */
    @Override
    public View focusSearch(View focused, int direction) {
    	// TODO Auto-generated method stub
    	Log.d("Test", "IpPicker focusSearch");
    	return super.focusSearch(focused, direction);
    }

    public void setTime(int hour, int minute, int second) {
        mFirstField.setCurrentTotal(hour);
        mSecondField.setCurrentTotal(minute);
        mThirdField.setCurrentTotal(second);
    }

    public int[] getTime() {
        int[] time = new int[3];
        time[0] = mFirstField.getCurrentTotal();
        time[1] = mSecondField.getCurrentTotal();
        time[2] = mThirdField.getCurrentTotal();

        return time;
    }

    public int getCurrentHour() {
        return mFirstField.getCurrentTotal();
    }

    public int getCurrentMinute() {
        return mSecondField.getCurrentTotal();
    }

    public int getCurrentSecond() {
        return mThirdField.getCurrentTotal();
    }

    public void setOnPickerClickListener(OnPickerClickListener onPickerClick) {
        this.onPickerClickListener = onPickerClick;
    }
    private OnPickerClickListener onPickerClickListener;
}
