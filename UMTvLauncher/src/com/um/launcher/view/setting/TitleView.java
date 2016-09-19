
package com.um.launcher.view.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.um.launcher.R;

/**
 * CustomSettingView titel
 *
 * @author tang_shengchang
 */
public class TitleView extends LinearLayout {

    private TextView mTitleText;

    public TitleView(Context context, String title) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        inflater.inflate(R.layout.title_view, this);
        mTitleText = (TextView) findViewById(R.id.title_txt);
        mTitleText.setText(title);

    }
}
