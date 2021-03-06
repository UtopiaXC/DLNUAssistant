package com.utopiaxc.dlnuassistant.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import com.utopiaxc.dlnuassistant.R;

import java.util.Objects;

import io.github.varenyzc.opensourceaboutpages.AboutPageMessageItem;
import io.github.varenyzc.opensourceaboutpages.MessageCard;

public class ActivityLicence extends AppCompatActivity {
    private MessageCard messageCard;

    //Activity入口
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences=getSharedPreferences("Theme",MODE_PRIVATE);
        int theme=sharedPreferences.getInt("theme",R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licence);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        messageCard=findViewById(R.id.licences);
        setMessageCard();

    }

    private void setMessageCard(){

        AboutPageMessageItem ItemLicence_Jsoup=new AboutPageMessageItem(this)
                .setIcon(getDrawable(R.drawable.developer))
                .setMainText("Jsoup")
                .setDescriptionText("Powered By Jonathan Hedley")
                .setOnItemClickListener(() -> {
                    Intent intent = new Intent();
                    //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://github.com/jhy/jsoup/blob/master/LICENSE");
                    intent.setData(content_url);
                    startActivity(intent);
                });
        messageCard.addMessageItem(ItemLicence_Jsoup);



        AboutPageMessageItem ItemLicence_AboutPage=new AboutPageMessageItem(this)
                .setIcon(getDrawable(R.drawable.developer))
                .setMainText("Open Source About Page")
                .setDescriptionText("Powered By varenyzc")
                .setOnItemClickListener(() -> {
                    Intent intent = new Intent();
                    //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://github.com/varenyzc/OpenSourceAboutPage/blob/master/LICENSE");
                    intent.setData(content_url);
                    startActivity(intent);
                });
        messageCard.addMessageItem(ItemLicence_AboutPage);

        AboutPageMessageItem ItemLicence_AndroidProcessButton=new AboutPageMessageItem(this)
                .setIcon(getDrawable(R.drawable.developer))
                .setMainText("Android Process Button")
                .setDescriptionText("Powered By dmytrodanylyk")
                .setOnItemClickListener(() -> {
                    Intent intent = new Intent();
                    //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://github.com/dmytrodanylyk/android-process-button/blob/master/LICENSE.md");
                    intent.setData(content_url);
                    startActivity(intent);
                });
        messageCard.addMessageItem(ItemLicence_AndroidProcessButton);

        AboutPageMessageItem ItemLicence_PullRefreshLayout=new AboutPageMessageItem(this)
                .setIcon(getDrawable(R.drawable.developer))
                .setMainText("Android Pull Refresh Layout")
                .setDescriptionText("Powered By baoyongzhang")
                .setOnItemClickListener(() -> {
                    Intent intent = new Intent();
                    //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://github.com/baoyongzhang/android-PullRefreshLayout/blob/master/LICENSE");
                    intent.setData(content_url);
                    startActivity(intent);
                });
        messageCard.addMessageItem(ItemLicence_PullRefreshLayout);

        AboutPageMessageItem ItemLicence_TimeTableView=new AboutPageMessageItem(this)
                .setIcon(getDrawable(R.drawable.developer))
                .setMainText("TimeTable View")
                .setDescriptionText("Powered By zfman")
                .setOnItemClickListener(() -> {
                    Intent intent = new Intent();
                    //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://github.com/zfman/TimetableView/blob/master/LICENSE");
                    intent.setData(content_url);
                    startActivity(intent);
                });
        messageCard.addMessageItem(ItemLicence_TimeTableView);

        AboutPageMessageItem ItemLicence_FlowView=new AboutPageMessageItem(this)
                .setIcon(getDrawable(R.drawable.developer))
                .setMainText("FlowLayout")
                .setDescriptionText("Powered By hongyangAndroid")
                .setOnItemClickListener(() -> {
                    Intent intent = new Intent();
                    //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://github.com/hongyangAndroid/FlowLayout/blob/master/LICENSE");
                    intent.setData(content_url);
                    startActivity(intent);
                });
        messageCard.addMessageItem(ItemLicence_FlowView);
    }

    //返回键
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
