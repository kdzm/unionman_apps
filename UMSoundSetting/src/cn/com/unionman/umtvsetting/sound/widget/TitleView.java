package cn.com.unionman.umtvsetting.sound.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.unionman.umtvsetting.sound.R;

/**
 * title of second menu
 *
 * @author tang_shengchang HiSI.ltd <br>
 *         Radio interface listview
 */
public class TitleView extends LinearLayout {

    private TextView titleTxt;

    public TitleView(Context context, String title) {
        super(context);
        LayoutInflater mLinflater = LayoutInflater.from(getContext());

        mLinflater.inflate(R.layout.title_view, this);
        titleTxt = (TextView) findViewById(R.id.menu_title);
        titleTxt.setText(title);

    }

}
