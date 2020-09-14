package com.utopiaxc.dlnuassistant.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import com.utopiaxc.dlnuassistant.R;
import com.utopiaxc.dlnuassistant.fuctions.FunctionsPublicBasic;

import java.util.Objects;

import io.github.varenyzc.opensourceaboutpages.AboutPageMessageItem;
import io.github.varenyzc.opensourceaboutpages.LogoCard;
import io.github.varenyzc.opensourceaboutpages.MessageCard;

public class ActivityAbout extends AppCompatActivity {
    private LogoCard logoCard ;
    private MessageCard messageCard;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static String getUpdateMessage = "";
    private static ProgressDialog getUpDialog = null;
    private FunctionsPublicBasic fuctions=new FunctionsPublicBasic();

    //Activity入口
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences=getSharedPreferences("Theme",MODE_PRIVATE);
        int theme=sharedPreferences.getInt("theme",R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        logoCard =  findViewById(R.id.logo);
        messageCard=findViewById(R.id.message);
        setUserMessages();


    }

    //检查更新方法
    public void update_check(Context contexts) {
        context = contexts;
        getUpDialog = ProgressDialog.show(context, getText(R.string.update_check), getString(R.string.update_checking), true);

        new Thread(new checkupdateRunnable()).start();
    }

    //检查更新线程
    class checkupdateRunnable implements Runnable{
        @Override
        public void run() {
            try {
                PackageManager packageManager = getPackageManager();
                PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
                String version = packInfo.versionName;

                String latest_version = fuctions.getHTML("https://www.utopiaxc.cn/Version_Control/DLNUAssistant_release.txt");


                if (latest_version.equals("error")) {
                    getUpdateMessage = (String) getText(R.string.net_error);
                } else if (latest_version.equals(version)) {
                    getUpdateMessage = (String) getText(R.string.no_update);
                } else {
                    getUpdateMessage = (String) getText(R.string.has_update);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageHandler.sendMessage(messageHandler.obtainMessage());
        }
    }


    //异步消息同步
    @SuppressLint("HandlerLeak")
    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getUpDialog.dismiss();
            if (getUpdateMessage.contentEquals(getText(R.string.has_update))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.download_newversion));
                builder.setMessage(getString(R.string.has_update));
                builder.setPositiveButton(getText(R.string.download_newversion), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.utopiaxc.cn/Version_Control/DLNUAssistant_release.apk"))));
                //设定“取消"按钮的功能
                builder.setNegativeButton(R.string.cancel, (dialog, which) -> {

                });
                builder.setNeutralButton(getText(R.string.goto_github), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/UtopiaXC/DLNUAssistant"))));
                builder.show();
            } else if (getUpdateMessage.contentEquals(getText(R.string.no_update))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAbout.this);
                builder.setTitle(getString(R.string.congratulations));
                builder.setMessage(getString(R.string.no_update));
                builder.setPositiveButton(getText(R.string.confirm), null);
                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAbout.this);
                builder.setTitle(getString(R.string.unknow_error));
                builder.setMessage(getString(R.string.again_please ));
                builder.setPositiveButton(getText(R.string.confirm), null);
                builder.show();
            }
        }
    };

    //开发信息卡片
    private void setUserMessages(){
        try {
            final String version=String.valueOf(getPackageManager().getPackageInfo(this.getPackageName(),0).versionName);
            AboutPageMessageItem item1 = new AboutPageMessageItem(this).
                    setIcon(getDrawable(R.drawable.update)).
                    setDescriptionText(version).
                    setMainText(getString(R.string.update_check)).
                    setOnItemClickListener(() -> update_check(ActivityAbout.this));
            logoCard.addMessageItem(item1);

            AboutPageMessageItem item2 = new AboutPageMessageItem(this).
                    setIcon(getDrawable(R.drawable.developer)).
                    setDescriptionText(getString(R.string.tag_license)).
                    setMainText(getString(R.string.open_source_license)).
                    setOnItemClickListener(() -> {
                        Intent intent = new Intent(ActivityAbout.this,ActivityLicence.class);
                        startActivity(intent);
                    });

            logoCard.addMessageItem(item2);
            AboutPageMessageItem item3 = new AboutPageMessageItem(this).
                    setIcon(getDrawable(R.drawable.log)).
                    setDescriptionText(getString(R.string.update_log_tag)).
                    setMainText(getString(R.string.update_log)).
                    setOnItemClickListener(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAbout.this);
                        builder.setTitle(getString(R.string.update_log));
                        builder.setMessage(getString(R.string.update_log_info));
                        builder.setPositiveButton(getText(R.string.confirm), (dialog, which) -> {
                        });
                        builder.show();
                    });
            logoCard.addMessageItem(item3);

            AboutPageMessageItem item4 = new AboutPageMessageItem(this).
                    setIcon(getDrawable(R.drawable.helper)).
                    setDescriptionText(getString(R.string.tag_helper_and_solution)).
                    setMainText(getString(R.string.helper_and_solution)).
                    setOnItemClickListener(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAbout.this);
                        builder.setMessage(getString(R.string.starting_helper));
                        builder.setPositiveButton(getText(R.string.confirm), null);
                        builder.setCancelable(false);
                        builder.show();
                    });
            logoCard.addMessageItem(item4);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        AboutPageMessageItem author_1=new AboutPageMessageItem(this)
                .setIcon(getDrawable(R.drawable.utopiaxc))
                .setMainText(getString(R.string.utopiaxc))
                .setDescriptionText("https://github.com/utopiaxc")
                .setOnItemClickListener(() -> {
                    Intent intent = new Intent();
                    //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://github.com/utopiaxc");
                    intent.setData(content_url);
                    startActivity(intent);
                });
        messageCard.addMessageItem(author_1);
    }

    //返回键
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
