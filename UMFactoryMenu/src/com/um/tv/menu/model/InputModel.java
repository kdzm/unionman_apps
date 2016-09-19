package com.um.tv.menu.model;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.hisilicon.android.tvapi.CusFactory;
import com.um.tv.menu.R;
import com.um.tv.menu.ui.FactoryWindow;

public class InputModel extends FunctionModel {
    private static final String TAG = "InputModel";
    public static final int TypeFrequencyOffset = 0;

    private int mType = 0;

    private int mDigits = 0;
    private int mLength = -1;

    private EditText mEtValue = null;
    private String mCurrentValue = "";
    private String mUnit = "";

    enum NumSystem{
        Binary,
        Decimal,
        Hexadecimal
    }

    private NumSystem mNumSystem = NumSystem.Decimal;

    public InputModel(Context context, FactoryWindow window, CusFactory factory, int type) {
        super(context, window, factory);
        // TODO Auto-generated constructor stub
        mType = type;
        init();
    }

    @Override
    public View getView(Context context, int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null || !convertView.getTag().equals(ViewTagInput)) {
            convertView = LayoutInflater.from(context).inflate(R.layout.input_layout, null);
            convertView.setTag(ViewTagInput);
        }
        TextView tvName = (TextView)convertView.findViewById(R.id.tv_name);
        tvName.setText(mName);

        mEtValue = (EditText)convertView.findViewById(R.id.et_value);

        mEtValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                Log.d(TAG,"onFocusChange-------hasFocus:" + hasFocus);
//                if(hasFocus){
//                    String originalString = mEtValue.getText().toString();
//                    int textLength = originalString.length();
//                    mEtValue.setSelection(textLength);
//                    Selection.setSelection((Spannable)(mEtValue.getText()), originalString.length());
//                    mEtValue.setText(originalString);
//                }else{
//                    String value = mEtValue.getEditableText().toString();
//                    value = value.replaceFirst("^0*", "");
//                    mEtValue.setText("" + value);
//                    onValueChange(mEtValue.getEditableText().toString());
//                }

                if(!hasFocus){
                    String value = mEtValue.getEditableText().toString();
                    value = value.replaceFirst("^0*", "");
                    mEtValue.setText("" + value);
                    onValueChange(mEtValue.getEditableText().toString());
                }
            }
        });

        if(mNumSystem == NumSystem.Decimal){
//            mEtValue.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        if(mLength > 0){
//            mEtValue.setMaxWidth(mLength);
            mEtValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mLength)});
        }
        mEtValue.setText(mCurrentValue);

        TextView tvUnit = (TextView)convertView.findViewById(R.id.tv_unit);
        tvUnit.setText(mUnit);

        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub
            Log.d(TAG,"changeValue-------------direct:" + direct + "-----position:" + position);
    }

    private void onValueChange(String value){
        switch (mType) {
        case TypeFrequencyOffset:
            long l = Long.valueOf(value);
            mFactory.setFrequencyOffset((int)l);
            break;

        default:
            break;
        }
    }

    @Override
    public void init() {
        switch (mType) {
        case TypeFrequencyOffset:
            int frequency = mFactory.getFrequencyOffset();
            Log.d(TAG, "init-----------------FrequencyOffset:" + frequency);
            mCurrentValue = "" + frequency;
            mUnit = "kHz";
            mLength = 5;
            break;
        default:
            break;
        }
    }
}
