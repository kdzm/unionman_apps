package com.unionman.dvbcitysetting.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.unionman.dvbcitysetting.R;

/**
 * Created by hjian on 2015/5/7.
 */
public class CitySpinner extends Spinner {
    private SpinnerPopup mPopup;
    private int mMaxHeight = 400;

    public CitySpinner(Context context) {
        super(context);
        init(context, null);
    }

    public CitySpinner(Context context, int mode) {
        super(context, mode);
        init(context, null);
    }

    public CitySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CitySpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public CitySpinner(Context context, AttributeSet attrs, int defStyle, int mode) {
        super(context, attrs, defStyle, mode);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPopup = new DropdownPopup(context, attrs);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CitySpinner);
            mMaxHeight = (int) a.getDimension(R.styleable.CitySpinner_dropdownMaxHeight, mMaxHeight);
        }
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter);

        if (mPopup != null) {
            mPopup.setAdapter(new DropDownAdapter(adapter));
        }
    }

    @Override
    public boolean performClick() {
        if (!mPopup.isShowing()) {
            mPopup.show(getTextDirection(), getTextAlignment());
        }

        return true;
    }

    private class DropdownPopup extends ListPopupWindow implements SpinnerPopup {
        private CharSequence mHintText;
        private ListAdapter mAdapter;

        public DropdownPopup(Context context, AttributeSet attrs) {
            super(context, attrs, 0);

            setAnchorView(CitySpinner.this);
            setModal(true);
            setPromptPosition(POSITION_PROMPT_ABOVE);
            setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView parent, View v, int position, long id) {
                    CitySpinner.this.setSelection(position);
                    if (getOnItemClickListener() != null) {
                        CitySpinner.this.performItemClick(v, position, mAdapter.getItemId(position));
                    }
                    dismiss();
                }
            });
        }

        @Override
        public void setAdapter(ListAdapter adapter) {
            super.setAdapter(adapter);
            mAdapter = adapter;
        }

        public CharSequence getHintText() {
            return mHintText;
        }

        public void setPromptText(CharSequence hintText) {
            // Hint text is ignored for dropdowns, but maintain it here.
            mHintText = hintText;
        }


        public void show(int textDirection, int textAlignment) {
            final boolean wasShowing = isShowing();

            setInputMethodMode(ListPopupWindow.INPUT_METHOD_NOT_NEEDED);
            super.show();
            final ListView listView = getListView();
            int itemSize = listView.getAdapter().getCount();
            int itemHeight = (int) getResources().getDimension(R.dimen.spinner_item_height);
            int listHeight = itemHeight * itemSize;
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listView.setTextDirection(textDirection);
            listView.setTextAlignment(textAlignment);
            Log.d("", "itemSize" + itemSize + ", itemHeight" + itemHeight + ", listHeight" + listHeight + ", maxHeight" + mMaxHeight);
            if (listHeight  >= mMaxHeight) {
                ((DropdownPopup) mPopup).setHeight(mMaxHeight);
            } else {
                ((DropdownPopup) mPopup).setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            setSelection(CitySpinner.this.getSelectedItemPosition());

            if (wasShowing) {
                // Skip setting up the layout/dismiss listener below. If we were previously
                // showing it will still stick around.
                return;
            }

            DropdownPopup.super.show();
        }
    }

    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;

        /**
         * <p>Creates a new ListAdapter wrapper for the specified adapter.</p>
         *
         * @param adapter the Adapter to transform into a ListAdapter
         */
        public DropDownAdapter(SpinnerAdapter adapter) {
            this.mAdapter = adapter;
            if (adapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter) adapter;
            }
        }

        public int getCount() {
            return mAdapter == null ? 0 : mAdapter.getCount();
        }

        public Object getItem(int position) {
            return mAdapter == null ? null : mAdapter.getItem(position);
        }

        public long getItemId(int position) {
            return mAdapter == null ? -1 : mAdapter.getItemId(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return getDropDownView(position, convertView, parent);
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return (mAdapter == null) ? null : mAdapter.getDropDownView(position, convertView, parent);
        }

        public boolean hasStableIds() {
            return mAdapter != null && mAdapter.hasStableIds();
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(observer);
            }
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(observer);
            }
        }

        /**
         * If the wrapped SpinnerAdapter is also a ListAdapter, delegate this call.
         * Otherwise, return true.
         */
        public boolean areAllItemsEnabled() {
            final ListAdapter adapter = mListAdapter;
            if (adapter != null) {
                return adapter.areAllItemsEnabled();
            } else {
                return true;
            }
        }

        /**
         * If the wrapped SpinnerAdapter is also a ListAdapter, delegate this call.
         * Otherwise, return true.
         */
        public boolean isEnabled(int position) {
            final ListAdapter adapter = mListAdapter;
            if (adapter != null) {
                return adapter.isEnabled(position);
            } else {
                return true;
            }
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEmpty() {
            return getCount() == 0;
        }
    }

    private interface SpinnerPopup {
        public void setAdapter(ListAdapter adapter);

        /**
         * Show the popup
         */
        public void show(int textDirection, int textAlignment);

        /**
         * Dismiss the popup
         */
        public void dismiss();

        /**
         * @return true if the popup is showing, false otherwise.
         */
        public boolean isShowing();

        /**
         * Set hint text to be displayed to the user. This should provide
         * a description of the choice being made.
         * @param hintText Hint text to set.
         */
        public void setPromptText(CharSequence hintText);
        public CharSequence getHintText();

        public void setBackgroundDrawable(Drawable bg);
        public void setVerticalOffset(int px);
        public void setHorizontalOffset(int px);
        public Drawable getBackground();
        public int getVerticalOffset();
        public int getHorizontalOffset();
    }
}
