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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.unionman.settings.R;

/**
 * Ａ view for selecting 3-digit number which is the field of a IP. IpfieldPicker
 * consist of tree NumberPickers, mHundredSpinner mTenSpinner and mUnitSpinner.
 * It indicate 3-digit number from 0 to 255.
 *
 */
class MinutefieldPicker extends FrameLayout {

//    private static final boolean DEFAULT_ENABLED_STATE = true;

//    private static final int HOURS_IN_HALF_DAY = 12;

    /**
     * A no-op callback used in the constructor to avoid null checks later in
     * the code.
     */
    private static final OnNumberChangedListener NO_OP_CHANGE_LISTENER = new OnNumberChangedListener() {

		@Override
		public void onNumberChanged(MinutefieldPicker ipfield, int hundred, int ten) {
			// Do nothing
		}
	};

    // state
    private boolean mIs5Begin;

    // ui components

    /**
     * High order digit, from 0 to 2.
     */
    private final NumberPicker mHundredSpinner;

    /**
     * Middle order digit, which in the range of 0 to 9 when high order digit number is small than 2
     * and in the range of 0 to 5 when high order digit number is 2.
     */
    private final NumberPicker mTenSpinner;

    /**
     * Listener to be notify upon current 3-digit number value change.
     */
    private OnNumberChangedListener mOnNumberChangedListener;

    /**
     * The callback interface used to indicate the current 3-digit number has been adjusted.
     */
    public interface OnNumberChangedListener {

        /**
         * Callback invoked while current 3-digit number value change.
         *
         * @param ipfield
         * @param hundred number
         * @param ten number
         * @param unit number
         */
        void onNumberChanged(MinutefieldPicker ipfield, int hundred, int ten);
    }

    /**
     * Create a new IpfieldPicker which consist of three NumberPicker
     *
     * @param context the application environment
     */
    public MinutefieldPicker(Context context) {
        this(context, null);
    }

    /**
     * Create a new IpfieldPicker which consist of three NumberPicker
     *
     * @param context the application environment
     * @param attrs a collection of attributes
     */
    public MinutefieldPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0/*R.attr.TriplePickerStyle*/);
    }

    /**
     * Create a new IpfieldPicker which consist of three NumberPicker
     *
     * @param context the application environment
     * @param attrs a collection of attributes
     * @param defStyle The default style to apply to this view.
     */
    public MinutefieldPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.date_timefield_picker, this, true);
        this.setFocusable(false);
        
        //Hundreds
        mHundredSpinner = (NumberPicker) findViewById(R.id.hundred);
        mHundredSpinner.setRange(0, 5);
        mHundredSpinner.setOnChangeListener(new NumberPicker.OnChangedListener() {

			@Override
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
                int ten = mTenSpinner.getCurrent();
                mTenSpinner.setRange(0, 9, ten % 10);
			}
		});
        
        //Tens
        mTenSpinner = (NumberPicker) findViewById(R.id.ten);
        mTenSpinner.setRange(0, 9);
        mTenSpinner.setOnChangeListener(new NumberPicker.OnChangedListener() {
			
			@Override
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				updateHundredFlag();
//				if (mIs5Begin) {
//					mTenSpinner.setRange(0, 0, 0);
//				}
				onNumberChanged();
			}
		});
        setOnNumberChangedListener(NO_OP_CHANGE_LISTENER);
    }

    /**
     * The flag indicates that whether the high order digit is 2.
     */
    private void updateHundredFlag() {
    	mIs5Begin = mHundredSpinner.getCurrent() == 5 ? true : false;
	}
 
    private void onNumberChanged() {
    	if (mOnNumberChangedListener != null){
    		mOnNumberChangedListener.onNumberChanged(this, getCurrentHundred(), 
    				getCurrentTen());
    	}
    }
    
    /**
     * Return the high order digit.
     * 
     * @return Hundred digit.
     */
    public int getCurrentHundred() {
    	return mHundredSpinner.getCurrent();
    }
    
    /**
     * Return the middle order digit.
     * 
     * @return Ten digit.
     */
    public int getCurrentTen() {
    	return mTenSpinner.getCurrent();
    }
    /**
     * Return IpfieldPicker current value.
     * 
     * @return 3-digit number from 0~255
     */
    public int getCurrentTotal() {
    	return mHundredSpinner.getCurrent() * 10 + mTenSpinner.getCurrent();
    }
    
    /**
     * Sets the listener to be notified on change of the current value.
     * 
     * @param onNumberChangedListener The listener.
     */
    public void setOnNumberChangedListener (OnNumberChangedListener onNumberChangedListener) {
    	mOnNumberChangedListener = onNumberChangedListener;
    }

    /**
     * Sets the current value to IpfieldPicker in the form of 3-digit number.
     * 
     * @param total Current 3-digit number which 0 to 255.
     */
    public void setCurrentTotal(int total) {
        if (total < 0 || total > 59) {
            throw new IllegalArgumentException(
                    "current should be >= 0 and <= 255");
        }
        mHundredSpinner.setCurrent(total / 10);
        mTenSpinner.setCurrent(total % 10);
    }
    
    /**
     * Sets the current value to IpfieldPicker in the form of 3 numbers.
     * 
     * @param hundred High order number which 0 to 2.
     * @param ten Middle order number which 0 to 9.
     * @param unit Low order number which 0 to 9.
     */
    public void setCurrentNumber (int hundred, int ten){
    	mHundredSpinner.setCurrent(hundred % 6);
    	mTenSpinner.setCurrent(ten % 10);
    }

    public NumberPicker getFirstNumberPicker() {
        return mHundredSpinner;
    }

    public NumberPicker getSecondNumberPicker() {
        return mTenSpinner;
    }
}
