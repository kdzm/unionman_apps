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
 */
public class DatePicker extends FrameLayout {

    private OnNumberChangedListener mOnNumberChangedListener;

	/**
	 */
    private final NumberPicker mYearField1;
    private final NumberPicker mYearField2;
    private final NumberPicker mYearField3;
    private final NumberPicker mYearField4;

    /**
     */
    private final NumberPicker mMonthField1;
    private final NumberPicker mMonthField2;

    /**
     */
    private final NumberPicker mDayField1;
    private final NumberPicker mDayField2;

    /**
     *
     */
    public DatePicker(Context context) {
        this(context, null);
        this.setFocusable(false);
    }

    /**
     */
    public DatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0/*R.attr.TriplePickerStyle*/);
    }

    /**
     */
    public DatePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.date_picker, this, true);

        mYearField1 = (NumberPicker) findViewById(R.id.year1);
        mYearField2 = (NumberPicker) findViewById(R.id.year2);
        mYearField3 = (NumberPicker) findViewById(R.id.year3);
        mYearField4 = (NumberPicker) findViewById(R.id.year4);

        mMonthField1 = (NumberPicker) findViewById(R.id.month1);
        mMonthField2 = (NumberPicker) findViewById(R.id.month2);

        mDayField1 = (NumberPicker) findViewById(R.id.day1);
        mDayField2 = (NumberPicker) findViewById(R.id.day2);

        mYearField1.setRange(2, 2);
        mYearField2.setRange(0, 0);
        mYearField3.setRange(0, 9);
        mYearField3.setOnChangeListener(new NumberPicker.OnChangedListener() {

            @Override
            public void onChanged(NumberPicker picker, int oldVal, int newVal) {
                mYearField3.setRange(0, 9, newVal % 10);
                mYearField4.setRange(0, 9, mYearField4.getCurrent() % 10);
                mMonthField1.setRange(0, 1, mMonthField1.getCurrent() % 2);
                mMonthField2.setRange(0, 9, mMonthField2.getCurrent() % 10);
                if (isLeapYear(getCurrentYear())) {
                    if (getCurrentMonth() == 2) {
                        mDayField1.setRange(0, 2, mDayField1.getCurrent() % 3);
                        mDayField2.setRange(0, 9, mDayField1.getCurrent() % 10);
                    }
                } else if (getCurrentMonth() == 2) {
                    mDayField1.setRange(0, 2, mDayField1.getCurrent() % 3);
                    mDayField2.setRange(0, 8, mDayField1.getCurrent() % 9);
                } else {
                    mDayField1.setRange(0, 3, mDayField1.getCurrent() % 4);
                    mDayField2.setRange(0, 9, mDayField1.getCurrent() % 10);
                }

                if (mMonthField1.getCurrent() == 0 && mMonthField2.getCurrent() == 0) {
                    mMonthField2.setCurrent(1);
                }
                if (mDayField1.getCurrent() == 0 && mDayField2.getCurrent() == 0) {
                    mDayField2.setCurrent(1);
                }
                onNumberChanged();
            }
        });

        mYearField4.setRange(0, 9);
        mYearField4.setOnChangeListener(new NumberPicker.OnChangedListener() {

            @Override
            public void onChanged(NumberPicker picker, int oldVal, int newVal) {
                mYearField4.setRange(0, 9, newVal % 10);
                mMonthField1.setRange(0, 1, mMonthField1.getCurrent() % 2);
                mMonthField2.setRange(0, 9, mMonthField2.getCurrent() % 10);
                if (isLeapYear(getCurrentYear())) {
                    if (getCurrentMonth() == 2) {
                        mDayField1.setRange(0, 2, mDayField1.getCurrent() % 3);
                        mDayField2.setRange(0, 9, mDayField2.getCurrent() % 10);
                    }
                } else {
                    if (getCurrentMonth() == 2) {
                        mDayField1.setRange(0, 2, mDayField1.getCurrent() % 3);
                        mDayField2.setRange(0, 8, mDayField2.getCurrent() % 9);
                    } else {
                        mDayField1.setRange(0, 3, mDayField1.getCurrent() % 4);
                        mDayField2.setRange(0, 9, mDayField2.getCurrent() % 10);
                    }
                }

                if (mMonthField1.getCurrent() == 0 && mMonthField2.getCurrent() == 0) {
                    mMonthField2.setCurrent(1);
                }
                if (mDayField1.getCurrent() == 0 && mDayField2.getCurrent() == 0) {
                    mDayField2.setCurrent(1);
                }
                onNumberChanged();
            }
        });

        mMonthField1.setRange(0, 1);
        mMonthField1.setOnChangeListener(new NumberPicker.OnChangedListener() {
            @Override
            public void onChanged(NumberPicker picker, int oldVal, int newVal) {
                int month = getCurrentMonth();
                if (newVal == 1) {
                    int ten = mMonthField2.getCurrent();
                    mMonthField2.setRange(0, 1, ten % 2);
                } else if (month == 2) {
                    if (isLeapYear(getCurrentYear())) {
                        mDayField1.setRange(0, 2, mDayField1.getCurrent() % 3);
                        mDayField2.setRange(0, 9, mDayField2.getCurrent() % 10);
                    } else {
                        mDayField1.setRange(0, 2, mDayField1.getCurrent() % 3);
                        mDayField2.setRange(0, 8, mDayField2.getCurrent() % 9);
                    }
                } else {
                    int ten = mMonthField2.getCurrent();
                    mMonthField2.setRange(1, 9, ten);
                }

                if (newVal == 0 && mMonthField2.getCurrent() == 0) {
                    mMonthField2.setCurrent(1);
                }
                if (mDayField1.getCurrent() == 0 && mDayField2.getCurrent() == 0) {
                    mDayField2.setCurrent(1);
                }
            }
        });

        mMonthField2.setRange(0, 9, 1);
        mMonthField2.setOnChangeListener(new NumberPicker.OnChangedListener() {
            @Override
            public void onChanged(NumberPicker picker, int oldVal, int newVal) {
                updateMonthTenFlag();
                if (mIsMonthFiled1Begin) {
                    mMonthField2.setRange(0, 2, mMonthField2.getCurrent() % 3);
                } else if (isLeapYear(getCurrentYear())) {
                    if (getCurrentMonth() == 2) {
                        mDayField1.setRange(0, 2, mDayField1.getCurrent() % 3);
                        mDayField2.setRange(0, 9, mDayField2.getCurrent() % 10);
                    }
                } else {
                    if (getCurrentMonth() == 2) {
                        mDayField1.setRange(0, 2, mDayField1.getCurrent() % 3);
                        mDayField2.setRange(0, 8, mDayField2.getCurrent() % 9);
                    } else {
                        mDayField1.setRange(0, 3, mDayField1.getCurrent() % 4);
                        mDayField2.setRange(0, 9, mDayField2.getCurrent() % 10);
                    }
                }

                if (newVal == 0 && mMonthField1.getCurrent() == 0) {
                    mMonthField2.setRange(1, 9, mMonthField2.getCurrent() % 10);
                }
                if (mDayField1.getCurrent() == 0 && mDayField2.getCurrent() == 0) {
                    mDayField2.setCurrent(1);
                }
                onNumberChanged();
            }
        });

        mDayField1.setRange(0, 3);
        mDayField1.setOnChangeListener(new NumberPicker.OnChangedListener() {
            @Override
            public void onChanged(NumberPicker picker, int oldVal, int newVal) {
                int ten = mDayField2.getCurrent();
                if (newVal == 3) {
                    int month = getCurrentMonth();
                    if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                        mDayField2.setRange(0, 1, ten % 2);
                    } else {
                        mDayField2.setRange(0, 0, 0);
                    }
                } else if (newVal == 2) {
                    mDayField2.setRange(0, 9, ten % 10);
                } else if (newVal == 0) {
                    mDayField2.setRange(1, 9, ten % 10);
                } else {
                    mDayField2.setRange(0, 9, ten % 10);
                }

                if (mDayField1.getCurrent() == 0 && mDayField2.getCurrent() == 0) {
                    mDayField2.setCurrent(1);
                }
            }
        });

        mDayField2.setRange(0, 9, 1);
        mDayField2.setOnChangeListener(new NumberPicker.OnChangedListener() {
            @Override
            public void onChanged(NumberPicker picker, int oldVal, int newVal) {
                updateDayTenFlag();
                if (mIsDayFiled3Begin) {
                    mDayField2.setRange(0, 1, mDayField2.getCurrent() % 2);
                }
                onNumberChanged();
            }
        });

        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onPickerClickListener != null) {
                    onPickerClickListener.onClick(v);
                }
            }
        };

        mYearField1.getNumberText().setOnClickListener(onClickListener);
        mYearField2.getNumberText().setOnClickListener(onClickListener);
        mYearField3.getNumberText().setOnClickListener(onClickListener);
        mYearField4.getNumberText().setOnClickListener(onClickListener);
        mMonthField1.getNumberText().setOnClickListener(onClickListener);
        mMonthField2.getNumberText().setOnClickListener(onClickListener);
        mDayField1.getNumberText().setOnClickListener(onClickListener);
        mDayField2.getNumberText().setOnClickListener(onClickListener);
    }

    private boolean mIsDayFiled3Begin;
    private boolean mIsMonthFiled1Begin;
    private void updateMonthTenFlag() {
        mIsMonthFiled1Begin = mMonthField1.getCurrent() == 1;
    }

    private void updateDayTenFlag() {
        mIsDayFiled3Begin = mDayField1.getCurrent() == 3;
    }

    private void onNumberChanged() {
        if (mOnNumberChangedListener != null){
            mOnNumberChangedListener.onNumberChanged(this, getCurrentYear(),
                    getCurrentMonth(), getCurrentDay());
        }
    }

    public interface OnNumberChangedListener {
        void onNumberChanged(DatePicker datePicker, int year, int month, int day);
    }

    public int getCurrentYear() {
        return mYearField1.getCurrent() * 1000
                + mYearField2.getCurrent() * 100
                + mYearField3.getCurrent() * 10
                + mYearField4.getCurrent();
    }

    public int getCurrentMonth() {
        return mMonthField1.getCurrent() * 10
                + mMonthField2.getCurrent();
    }

    public int getCurrentDay() {
        return mDayField1.getCurrent() * 10
                + mDayField2.getCurrent();
    }

    public void setDate(int year, int month, int day) {
        mYearField1.setCurrent(year / 1000);
        mYearField2.setCurrent(year / 100 % 10);
        mYearField3.setCurrent(year / 10 % 10);
        mYearField4.setCurrent(year % 10);

        mMonthField1.setCurrent(month / 10);
        mMonthField2.setCurrent(month % 10);

        mDayField1.setCurrent(day / 10);
        mDayField2.setCurrent(day % 10);
    }

    public int[] getDate() {
        int[] date = new int[3];
        int year = mYearField1.getCurrent() * 1000
                + mYearField2.getCurrent() * 100
                + mYearField3.getCurrent() * 10
                + mYearField4.getCurrent();
        int month = mMonthField1.getCurrent() * 10
                + mMonthField2.getCurrent();
        int day = mDayField1.getCurrent() * 10
                + mDayField2.getCurrent();
        date[0] = year;
        date[1] = month;
        date[2] = day;
        return date;
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

    private boolean isLeapYear(int year) {
        if (year % 100 == 0) {
            if (year % 400 == 0) {
                return true;
            }
        } else {
            if (year % 4 == 0) {
                return true;
            }
        }
        return false;
    }

    public void setOnPickerClickListener(OnPickerClickListener onPickerClick) {
        this.onPickerClickListener = onPickerClick;
    }
    private OnPickerClickListener onPickerClickListener;
}
