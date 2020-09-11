package com.utopiaxc.dlnuassistant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntro2;
import com.github.appintro.AppIntroCustomLayoutFragment;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.github.appintro.model.SliderPage;
import com.utopiaxc.dlnuassistant.fragments.FragmentCenter;

public class ActivityIntro extends AppIntro2 {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setColorTransitionsEnabled(true);
        showStatusBar(true);


        SliderPage sliderPage=new SliderPage();
        sliderPage.setTitle("欢迎使用民大助理");
        sliderPage.setDescription("这是一款专为大连民族大学学生设计的一款助手应用");
        //设置背景颜色
        sliderPage.setBackgroundColor(Color.parseColor("#7c4dff"));
        //addSlide(AppIntroFragment.newInstance(R.layout.activity_about));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        sliderPage=new SliderPage();
        sliderPage.setTitle("这是第二页");
        sliderPage.setDescription("我还没想好写什么");
        //设置背景颜色
        sliderPage.setBackgroundColor(Color.parseColor("#2979ff"));
        //addSlide(AppIntroFragment.newInstance(R.layout.activity_about));
        addSlide(AppIntroFragment.newInstance(sliderPage));



    }

    @Override
    protected void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    protected void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
    }
}