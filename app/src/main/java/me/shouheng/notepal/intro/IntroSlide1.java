package me.shouheng.notepal.intro;

import android.os.Bundle;
import android.view.View;

import me.shouheng.notepal.R;


/**
 * Created by Wang Shouheng on 2017/12/6. */
public class IntroSlide1 extends IntroFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        background.setBackgroundResource(R.color.intro_color_1);
        title.setText(R.string.intro_1_title);
        image.setVisibility(View.GONE);
        imageSmall.setImageResource(R.mipmap.ic_launcher);
        imageSmall.setVisibility(View.VISIBLE);
        description.setText(R.string.intro_1_description);
    }
}