package com.utopiaxc.urpassistant.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import java.util.List;
import java.util.Set;

public class ActivityUpdateEditor extends AppCompatActivity {
    Context context = this;
    EditText editText_name;
    EditText editText_id;
    EditText editText_credit;
    EditText editText_attribute;
    EditText editText_examattribute;
    EditText editText_teacher;
    EditText editText_school;
    EditText editText_room;
    String weeks = "";
    Set<Integer> set;
    int integer_count;
    int integer_time;
    int end;
    int editData;
    String name;
    String data;
    boolean weekschanged = false;
    boolean timechanged = false;
    boolean weekchanged = false;

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
        name = intent.getStringExtra("name");
        data = String.valueOf(intent.getIntExtra("data",0));
        SQLHelperTimeTable sqlHelperTimeTable = new SQLHelperTimeTable(this, "URP_timetable", null, 2);
        SQLiteDatabase sqLiteDatabase = sqlHelperTimeTable.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query("classes", new String[]{"ClassName", "ClassId", "Credit", "ClassAttribute", "ExamAttribute", "Teacher", "Week", "Data", "Count", "School", "Building", "Room", "Time"}, "ClassName=? and Data=?", new String[]{name, data}, null, null, null);


        editText_name = findViewById(R.id.activity_editor_name);
        editText_id = findViewById(R.id.activity_editor_id);
        editText_credit = findViewById(R.id.activity_editor_credit);
        editText_attribute = findViewById(R.id.activity_editor_attribute);
        editText_examattribute = findViewById(R.id.activity_editor_examAttribute);
        editText_teacher = findViewById(R.id.activity_editor_teacher);
        editText_school = findViewById(R.id.activity_editor_school);
        editText_room = findViewById(R.id.activity_editor_room);

        String time = "";
        String count = "";

        while (cursor.moveToNext()) {
            editText_name.setHint(cursor.getString(cursor.getColumnIndex("ClassName")));
            editData = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Data")));
            weeks = cursor.getString(cursor.getColumnIndex("Week"));
            time = cursor.getString(cursor.getColumnIndex("Time"));
            count = cursor.getString(cursor.getColumnIndex("Count"));
            editText_id.setHint(cursor.getString(cursor.getColumnIndex("ClassId")));
            editText_credit.setHint(cursor.getString(cursor.getColumnIndex("Credit")));
            editText_attribute.setHint(cursor.getString(cursor.getColumnIndex("ClassAttribute")));
            editText_examattribute.setHint(cursor.getString(cursor.getColumnIndex("ExamAttribute")));
            editText_teacher.setHint(cursor.getString(cursor.getColumnIndex("Teacher")));
            editText_school.setHint(cursor.getString(cursor.getColumnIndex("School")));
            editText_room.setHint(cursor.getString(cursor.getColumnIndex("Building")) + cursor.getString(cursor.getColumnIndex("Room")));
            break;
        }

        integer_count = Integer.parseInt(count);
        integer_time = Integer.parseInt(time);
        end = integer_time + integer_count - 1;

        Button button_time = findViewById(R.id.course_time_selector);
        button_time.setText(getString(R.string.starter) + time + getString(R.string.ender) + (integer_time + integer_count - 1));

        Button button_week = findViewById(R.id.course_week_selector);
        button_week.setText(getString(R.string.week_day) + (editData + 1));

        Button button_weeks = findViewById(R.id.course_weeks_selector);
        button_weeks.setText(weeks);

        button_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_course_time, null);  //从另外的布局关联组件
                AlertDialog.Builder setCourseTime = new AlertDialog.Builder(context);
                NumberPicker numberPicker_start = linearLayout.findViewById(R.id.start_time_picker);
                numberPicker_start.setMinValue(1);
                numberPicker_start.setMaxValue(12);
                numberPicker_start.setValue(integer_time);
                NumberPicker numberPicker_end = linearLayout.findViewById(R.id.end_time_picker);
                numberPicker_end.setMinValue(1);
                numberPicker_end.setMaxValue(12);
                if (end != 0)
                    numberPicker_end.setValue(end);
                setCourseTime.setView(linearLayout)
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                integer_time = numberPicker_start.getValue();
                                end = numberPicker_end.getValue();
                                if (end < integer_time) {
                                    Toast.makeText(context, getString(R.string.add_time_error), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                integer_count = end - integer_time + 1;
                                button_time.setText(getString(R.string.starter) + integer_time + " " + getString(R.string.ender) + end);
                                timechanged=true;
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .create()
                        .show();
            }
        });

        button_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_week_selector, null);  //从另外的布局关联组件
                AlertDialog.Builder setWeek = new AlertDialog.Builder(context);
                SeekBar seekBar = linearLayout.findViewById(R.id.week_day_selector);
                TextView textView = linearLayout.findViewById(R.id.week_day_display);
                final int progress = editData;
                textView.setText(getString(R.string.selected_week) + (progress + 1));
                seekBar.setProgress(editData);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        textView.setText(getString(R.string.selected_week) + (progress + 1));
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
                                editData = seekBar.getProgress();
                                button_week.setText(getString(R.string.week_day) + (editData + 1));
                                weekchanged = true;
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .create()
                        .show();
            }
        });

        button_weeks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_weeks_selector, null);  //从另外的布局关联组件
                AlertDialog.Builder setWeeks = new AlertDialog.Builder(context);
                TagFlowLayout flowLayout = linearLayout.findViewById(R.id.weeks_selector);

                List<String> list = new ArrayList<String>();
                for (int i = 1; i < 26; i++)
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
                                set = flowLayout.getSelectedList();
                                System.out.println(set);
                                weeks = "";
                                for (int temp : set)
                                    weeks += (temp + 1) + ",";
                                weeks = weeks.substring(0, weeks.length() - 1);
                                button_weeks.setText(weeks);
                                weekschanged = true;
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
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
                return true;

            case R.id.activity_editor_confirm:

                System.out.println("checked");

                SQLHelperTimeTable sqlHelperTimeTable = new SQLHelperTimeTable(this, "URP_timetable", null, 2);
                SQLiteDatabase sqLiteDatabase = sqlHelperTimeTable.getWritableDatabase();
                if (!editText_name.getText().toString().equals("")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("ClassName", editText_name.getText().toString());
                    sqLiteDatabase.update("classes",
                            contentValues,
                            "ClassName = ?",
                            new String[]{name});
                }

                if (!editText_id.getText().toString().equals("")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("ClassId", editText_id.getText().toString());
                    sqLiteDatabase.update("classes",
                            contentValues,
                            "ClassName = ?",
                            new String[]{name});
                }

                if (!editText_credit.getText().toString().equals("")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Credit", editText_credit.getText().toString());
                    sqLiteDatabase.update("classes",
                            contentValues,
                            "ClassName = ?",
                            new String[]{name});
                }

                if (!editText_attribute.getText().toString().equals("")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("ClassAttribute", editText_attribute.getText().toString());
                    sqLiteDatabase.update("classes",
                            contentValues,
                            "ClassName = ?",
                            new String[]{name});
                }

                if (!editText_examattribute.getText().toString().equals("")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("ExamAttribute", editText_examattribute.getText().toString());
                    sqLiteDatabase.update("classes",
                            contentValues,
                            "ClassName = ?",
                            new String[]{name});
                }


                if (!editText_teacher.getText().toString().equals("")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Teacher", editText_teacher.getText().toString());
                    sqLiteDatabase.update("classes",
                            contentValues,
                            "ClassName = ?",
                            new String[]{name});
                }

                if (!editText_school.getText().toString().equals("")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("School", editText_school.getText().toString());
                    sqLiteDatabase.update("classes",
                            contentValues,
                            "ClassName = ?",
                            new String[]{name});
                }

                if (!editText_teacher.getText().toString().equals("")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Teacher", editText_teacher.getText().toString());
                    sqLiteDatabase.update("classes",
                            contentValues,
                            "ClassName = ?",
                            new String[]{name});
                }

                if (!editText_room.getText().toString().equals("")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Room", editText_room.getText().toString());
                    contentValues.put("Building", "");
                    sqLiteDatabase.update("classes",
                            contentValues,
                            "ClassName = ?",
                            new String[]{name});
                }

                if (weekschanged) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Week", weeks);
                    sqLiteDatabase.update("classes",
                            contentValues,
                            "ClassName = ? and Data=?",
                            new String[]{name, data});

                }

                if (weekchanged) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Data", editData);
                    sqLiteDatabase.update("classes",
                            contentValues,
                            "ClassName = ? and Data = ?",
                            new String[]{name, data});
                }

                if (timechanged) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("Time", integer_time);
                    sqLiteDatabase.update("classes",
                            contentValues,
                            "ClassName = ? and Data = ?",
                            new String[]{name, data});

                    System.out.println(integer_count);

                    ContentValues contentValues1 = new ContentValues();
                    contentValues1.put("Count", integer_count);
                    sqLiteDatabase.update("classes",
                            contentValues1,
                            "ClassName = ? and Data = ?",
                            new String[]{name, data});

                }

                startActivity(intent);
                finish();
                return true;
                default:
                    return false;
        }
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
