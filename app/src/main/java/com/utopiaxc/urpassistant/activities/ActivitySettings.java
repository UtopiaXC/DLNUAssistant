package com.utopiaxc.urpassistant.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.utopiaxc.urpassistant.ActivityMain;
import com.utopiaxc.urpassistant.R;

import io.github.varenyzc.opensourceaboutpages.AboutPageMessageItem;
import io.github.varenyzc.opensourceaboutpages.MessageCard;

public class ActivitySettings extends AppCompatActivity {
    private MessageCard messageCard;
    private Context thisContext = this;


    //Activity入口
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("Theme", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("theme", R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        //预绑定
        messageCard = findViewById(R.id.settings_card);

        //界面设置函数集合
        setMessageCard();

    }

    private void setMessageCard() {
        AboutPageMessageItem ItemSettings_theme = new AboutPageMessageItem(this)
                .setIcon(getDrawable(R.drawable.theme))
                .setMainText(getString(R.string.theme))
                .setOnItemClickListener(new AboutPageMessageItem.AboutPageOnItemClick() {
                    @Override
                    public void onClick() {
                        final SharedPreferences sharedPreferences = getSharedPreferences("Theme", MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPreferences.edit();

                        AlertDialog.Builder setURP = new AlertDialog.Builder(thisContext);
                        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_selection_theme, null);  //从另外的布局关联组件

                        final RadioButton radioButton_purple = linearLayout.findViewById(R.id.radioButtonPurple);
                        final RadioButton radioButton_black = linearLayout.findViewById(R.id.radioButtonBlack);
                        final RadioButton radioButton_green = linearLayout.findViewById(R.id.radioButtonGreen);
                        final RadioButton radioButton_blue = linearLayout.findViewById(R.id.radioButtonBlue);
                        final RadioButton radioButton_red = linearLayout.findViewById(R.id.radioButtonRed);
                        final RadioButton radioButton_grey = linearLayout.findViewById(R.id.radioButtonGrey);
                        final RadioButton radioButton_pink = linearLayout.findViewById(R.id.radioButtonPink);
                        final RadioButton radioButton_yellow = linearLayout.findViewById(R.id.radioButtonYellow);

                        setURP.setTitle(getString(R.string.theme))
                                .setView(linearLayout)
                                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (radioButton_purple.isChecked()) {
                                            editor.putInt("theme", R.style.AppTheme);
                                            editor.commit();
                                        } else if (radioButton_black.isChecked()) {
                                            editor.putInt("theme", R.style.AppTheme_black);
                                            editor.commit();
                                        } else if (radioButton_green.isChecked()) {
                                            editor.putInt("theme", R.style.AppTheme_green);
                                            editor.commit();
                                        } else if (radioButton_blue.isChecked()) {
                                            editor.putInt("theme", R.style.AppTheme_blue);
                                            editor.commit();
                                        } else if (radioButton_red.isChecked()) {
                                            editor.putInt("theme", R.style.AppTheme_red);
                                            editor.commit();
                                        } else if (radioButton_grey.isChecked()) {
                                            editor.putInt("theme", R.style.AppTheme_grey);
                                            editor.commit();
                                        } else if (radioButton_pink.isChecked()) {
                                            editor.putInt("theme", R.style.AppTheme_pink);
                                            editor.commit();
                                        } else if (radioButton_yellow.isChecked()) {
                                            editor.putInt("theme", R.style.AppTheme_yellow);
                                            editor.commit();
                                        }
                                        recreate();

                                    }
                                })
                                .create()
                                .show();
                    }
                });
        messageCard.addMessageItem(ItemSettings_theme);

        AboutPageMessageItem ItemSettings_layoutOfTimetable = new AboutPageMessageItem(this)
                .setIcon(getDrawable(R.drawable.list))
                .setMainText(getString(R.string.timetable_layout))
                .setOnItemClickListener(new AboutPageMessageItem.AboutPageOnItemClick() {
                    @Override
                    public void onClick() {


                        SharedPreferences sharedPreferences = getSharedPreferences("TimeTable", MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPreferences.edit();

                        AlertDialog.Builder setURP = new AlertDialog.Builder(thisContext);
                        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_selection_timetable_layout, null);  //从另外的布局关联组件

                        final RadioButton radioButton_chart = linearLayout.findViewById(R.id.radioButton_timetable_chart);
                        final RadioButton radioButton_list = linearLayout.findViewById(R.id.radioButton_list);

                        setURP.setTitle(getString(R.string.timetable_layout))
                                .setView(linearLayout)
                                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (radioButton_chart.isChecked()) {
                                            editor.putInt("Layout", 1);
                                            editor.commit();
                                        } else if (radioButton_list.isChecked()) {
                                            editor.putInt("Layout", 2);
                                            editor.commit();
                                        }

                                    }
                                })
                                .create()
                                .show();


                    }
                });
        messageCard.addMessageItem(ItemSettings_layoutOfTimetable);


        AboutPageMessageItem ItemSettings_firstStartPage = new AboutPageMessageItem(this)
                .setIcon(getDrawable(R.drawable.first_fragment))
                .setMainText(getString(R.string.set_first_start_page))
                .setOnItemClickListener(new AboutPageMessageItem.AboutPageOnItemClick() {
                    @Override
                    public void onClick() {


                        SharedPreferences sharedPreferences = getSharedPreferences("FirstFragment", MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPreferences.edit();

                        AlertDialog.Builder setURP = new AlertDialog.Builder(thisContext);
                        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_first_start_page, null);  //从另外的布局关联组件

                        final RadioButton radioButton_home = linearLayout.findViewById(R.id.radioButton_first_home);
                        final RadioButton radioButton_table = linearLayout.findViewById(R.id.radioButton_first_timetable);

                        setURP.setTitle(getString(R.string.set_first_start_page))
                                .setView(linearLayout)
                                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (radioButton_home.isChecked()) {
                                            editor.putInt("Start_first", 1);
                                            editor.commit();
                                        } else if (radioButton_table.isChecked()) {
                                            editor.putInt("Start_first", 2);
                                            editor.commit();
                                        }
                                    }
                                })
                                .create()
                                .show();
                    }
                });
        messageCard.addMessageItem(ItemSettings_firstStartPage);


    }

    //返回键
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences sharedPreferences_toActivity = getSharedPreferences("FirstFragment", MODE_PRIVATE);
        SharedPreferences.Editor editor_toActivity = sharedPreferences_toActivity.edit();
        editor_toActivity.putInt("Start", 3);
        editor_toActivity.commit();

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ActivityMain.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        SharedPreferences sharedPreferences_toActivity = getSharedPreferences("FirstFragment", MODE_PRIVATE);
        SharedPreferences.Editor editor_toActivity = sharedPreferences_toActivity.edit();
        editor_toActivity.putInt("Start", 3);
        editor_toActivity.commit();

        Intent intent = new Intent(this, ActivityMain.class);
        startActivity(intent);
        finish();
    }


}
