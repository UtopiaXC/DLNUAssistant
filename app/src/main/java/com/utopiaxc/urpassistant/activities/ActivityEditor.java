package com.utopiaxc.urpassistant.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.utopiaxc.urpassistant.ActivityMain;
import com.utopiaxc.urpassistant.R;
import com.utopiaxc.urpassistant.sqlite.SQLHelperTimeTable;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityEditor extends AppCompatActivity {
    Context context=this;
    EditText editText_name;
    EditText editText_id;
    EditText editText_credit;
    EditText editText_attribute;
    EditText editText_examattribute;
    EditText editText_teacher;
    EditText editText_school;
    EditText editText_room;
    String weeks="";
    Set<Integer> set;
    int data;
    int start;
    int end;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_editor_confirm, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("Theme", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("theme", R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        Intent intent = getIntent();
        data = intent.getIntExtra("data", 1);
        start = intent.getIntExtra("start", 1);
        end=0;

        editText_name = findViewById(R.id.activity_editor_name);
        editText_name.setHint(getString(R.string.must_to_fill));
        editText_id = findViewById(R.id.activity_editor_id);
        editText_id.setHint(getString(R.string.hint_default_unknown));
        editText_credit = findViewById(R.id.activity_editor_credit);
        editText_credit.setHint(getString(R.string.hint_default_unknown));
        editText_attribute = findViewById(R.id.activity_editor_attribute);
        editText_attribute.setHint(getString(R.string.hint_default_unknown));
        editText_examattribute =findViewById(R.id.activity_editor_examAttribute);
        editText_examattribute.setHint(getString(R.string.hint_default_unknown));
        editText_teacher = findViewById(R.id.activity_editor_teacher);
        editText_teacher.setHint(getString(R.string.hint_default_unknown));
        editText_school = findViewById(R.id.activity_editor_school);
        editText_school.setHint(getString(R.string.hint_default_unknown));
        editText_room = findViewById(R.id.activity_editor_room);
        editText_room.setHint(getString(R.string.hint_default_unknown));

        Button button_time=findViewById(R.id.course_time_selector);
        button_time.setText(getString(R.string.starter)+start+" "+getString(R.string.ender)+getString(R.string.click_to_choose));

        Button button_week=findViewById(R.id.course_week_selector);
        button_week.setText(getString(R.string.week_day)+(data+1));

        Button button_weeks=findViewById(R.id.course_weeks_selector);

        button_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_course_time, null);  //从另外的布局关联组件
                AlertDialog.Builder setCourseTime = new AlertDialog.Builder(context);
                NumberPicker numberPicker_start=linearLayout.findViewById(R.id.start_time_picker);
                numberPicker_start.setMinValue(1);
                numberPicker_start.setMaxValue(12);
                numberPicker_start.setValue(start);
                NumberPicker numberPicker_end=linearLayout.findViewById(R.id.end_time_picker);
                numberPicker_end.setMinValue(1);
                numberPicker_end.setMaxValue(12);
                if(end!=0)
                    numberPicker_end.setValue(end);
                setCourseTime.setView(linearLayout)
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                start=numberPicker_start.getValue();
                                end=numberPicker_end.getValue();
                                if(end<start) {
                                    Toast.makeText(context, getString(R.string.add_time_error), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                button_time.setText(getString(R.string.starter)+start+" "+getString(R.string.ender)+end);
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel),null)
                        .create()
                        .show();
            }
        });

        button_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_week_selector, null);  //从另外的布局关联组件
                AlertDialog.Builder setWeek = new AlertDialog.Builder(context);
                SeekBar seekBar=linearLayout.findViewById(R.id.week_day_selector);
                TextView textView=linearLayout.findViewById(R.id.week_day_display);
                final int progress = data;
                textView.setText(getString(R.string.selected_week)+ (progress+1));
                seekBar.setProgress(data);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textView.setText(getString(R.string.selected_week)+(progress+1));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                setWeek.setView(linearLayout)
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                data=seekBar.getProgress();
                                button_week.setText(getString(R.string.week_day)+(data+1));
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel),null)
                        .create()
                        .show();
            }
        });

        button_weeks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_weeks_selector, null);  //从另外的布局关联组件
                AlertDialog.Builder setWeeks = new AlertDialog.Builder(context);
                TagFlowLayout flowLayout=linearLayout.findViewById(R.id.weeks_selector);

                List<String> list = new ArrayList<String>();
                for(int i=1;i<26;i++)
                    list.add(String.valueOf(i));

                TagAdapter tagAdapter = new TagAdapter(list) {
                    @Override
                    public View getView(FlowLayout parent, int position, Object o) {

                        TextView view = (TextView) View.inflate(context, R.layout.flowlayout_textview_selected, null);
                        view.setText(list.get(position));
                        return view;
                    }
                };
                //预先设置选中
                //tagAdapter.setSelectedList(set);

                flowLayout.setAdapter(tagAdapter);

                //设置最大选中数
                flowLayout.setMaxSelectCount(-1);



                setWeeks.setView(linearLayout)
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                set=flowLayout.getSelectedList();
                                System.out.println(set);
                                weeks="";
                                for(int temp:set)
                                    weeks+=(temp+1)+",";
                                weeks=weeks.substring(0,weeks.length()-1);
                                button_weeks.setText(weeks);
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel),null)
                        .create()
                        .show();


            }
        });

    }

    //返回键
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences sharedPreferences_toActivity = getSharedPreferences("FirstFragment", MODE_PRIVATE);
        SharedPreferences.Editor editor_toActivity = sharedPreferences_toActivity.edit();
        editor_toActivity.putInt("Start", 2);
        editor_toActivity.commit();
        Intent intent = new Intent(this, ActivityMain.class);
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(intent);
                finish();
                break;

            case R.id.activity_editor_confirm:

                String CourseNmae = editText_name.getText().toString();
                String ID = editText_id.getText().toString();
                String Credit = editText_credit.getText().toString();
                String Attribute = editText_attribute.getText().toString();
                String ExamAttribute = editText_examattribute.getText().toString();
                String Teacher = editText_teacher.getText().toString();
                String School = editText_school.getText().toString();
                String Room = editText_room.getText().toString();
                String Week = weeks;

                if (CourseNmae.equals("") || Week.equals("") || end==0) {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.blank_error))
                            .setPositiveButton(getString(R.string.confirm),null)
                            .create()
                            .show();
                    return false;
                }

                if (!checkWeek(Week)) {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.week_error))
                            .setPositiveButton(getString(R.string.confirm),null)
                            .create()
                            .show();
                    return false;
                }
                int count_insert = end-start+1;

                ContentValues values = new ContentValues();
                values.put("ClassName", CourseNmae);
                values.put("Week", Week);
                values.put("Data", data + 1);
                values.put("Time", start);
                values.put("Count", count_insert);
                values.put("Building", "");
                values.put("Way", "");
                if (ID.equals(""))
                    values.put("ClassId", getString(R.string.unknown));
                else
                    values.put("ClassId", ID);

                if (Credit.equals(""))
                    values.put("Credit",getString(R.string.unknown));
                else
                    values.put("Credit", Credit);

                if (Attribute.equals(""))
                    values.put("ClassAttribute", getString(R.string.unknown));
                else
                    values.put("ClassAttribute", Attribute);

                if (ExamAttribute.equals(""))
                    values.put("ExamAttribute", getString(R.string.unknown));
                else
                    values.put("ExamAttribute", ExamAttribute);

                if (Teacher.equals(""))
                    values.put("Teacher", getString(R.string.unknown));
                else
                    values.put("Teacher", Teacher);

                if (School.equals(""))
                    values.put("School", getString(R.string.unknown));
                else
                    values.put("School", School);

                if (Room.equals(""))
                    values.put("Room", getString(R.string.unknown));
                else
                    values.put("Room", Room);

                SQLHelperTimeTable sql = new SQLHelperTimeTable(this, "URP_timetable", null, 2);
                SQLiteDatabase sqliteDatabase = sql.getWritableDatabase();
                sqliteDatabase.insert("classes", null, values);












               startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    boolean checkWeek(String weeks) {
        String reg = "[^0-9,]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(weeks);
        boolean isMatch = matcher.find();
        if (isMatch || weeks.charAt(0) == ',' || weeks.charAt(weeks.length() - 1) == ',') {
            return false;
        }
        String week[] = weeks.split(",");
        for (String week_match : week) {
            try {
                week_match = week_match.replace(",", "");
                int week_int = Integer.valueOf(week_match);
                if (week_int < 0 || week_int > 25) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {

        SharedPreferences sharedPreferences_toActivity = getSharedPreferences("FirstFragment", MODE_PRIVATE);
        SharedPreferences.Editor editor_toActivity = sharedPreferences_toActivity.edit();
        editor_toActivity.putInt("Start", 2);
        editor_toActivity.commit();

        Intent intent = new Intent(this, ActivityMain.class);
        startActivity(intent);
        finish();
    }
}
