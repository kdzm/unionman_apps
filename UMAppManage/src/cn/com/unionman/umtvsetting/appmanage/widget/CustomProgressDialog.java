
package cn.com.unionman.umtvsetting.appmanage.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.unionman.umtvsetting.appmanage.R;

/**
 * A custom schedule frame
 *
 * @author huyq
 */
public class CustomProgressDialog{

    private View mLoading;

    public CustomProgressDialog(Activity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLoading = inflater.inflate(R.layout.app_loading_dialog, null);
        LinearLayout.LayoutParams lay = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lay.gravity = Gravity.CENTER;
        activity.addContentView(mLoading, lay);
    }

    public void showLoading() {
        showLoading(null);
    }

    public void showLoading(final String message) {
        TextView text = (TextView) mLoading.findViewById(R.id.title);
        if (message != null) {
            text.setVisibility(View.VISIBLE);
            text.setText(message);
        }
        mLoading.setVisibility(View.VISIBLE);
        ImageView imageView = (ImageView) mLoading.findViewById(R.id.img);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }

    public void hideLoading() {
        if (mLoading != null) {
            ImageView imageView = (ImageView) mLoading.findViewById(R.id.img);
            AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
            animationDrawable.stop();
            mLoading.setVisibility(View.GONE);
        }
    }

}
