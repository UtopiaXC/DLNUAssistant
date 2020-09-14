package com.utopiaxc.dlnuassistant.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.dd.processbutton.FlatButton;
import com.utopiaxc.dlnuassistant.ActivityMain;
import com.utopiaxc.dlnuassistant.R;
import com.utopiaxc.dlnuassistant.activities.ActivityEditor;
import com.utopiaxc.dlnuassistant.activities.ActivityUpdateEditor;
import com.utopiaxc.dlnuassistant.fuctions.FunctionsPublicBasic;
import com.utopiaxc.dlnuassistant.sqlite.SQLHelperTimeTable;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.model.Schedule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class FragmentTimeTableChart extends Fragment {
    private TimetableView timetableView;
    private String handlerMessage = null;
    private static ProgressDialog getTimetableDialog = null;

    //设置菜单UI
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_timetable_menu, menu);
    }

    //菜单栏监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.fragment_timetable_refresh:
                getTimetableDialog = ProgressDialog.show(getActivity(), "获取课表", "请稍候，正在获取课表", true);
                new Thread(new getClasses()).start();
                return true;
            case R.id.fragment_timetable_change_week:
                final Dialog setWeek = new Dialog(Objects.requireNonNull(getActivity()));
                @SuppressLint("InflateParams") RelativeLayout relativeLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.alertdialog_number_picker, null);  //从另外的布局关联组件

                final NumberPicker numberPicker = relativeLayout.findViewById(R.id.numberPicker);
                final FlatButton confirm = relativeLayout.findViewById(R.id.numberPicker_confirm);
                final FlatButton cancel = relativeLayout.findViewById(R.id.numberPicker_cancel);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(25);

                setWeek.setTitle(getString(R.string.timetable_layout));
                setWeek.setContentView(relativeLayout);

                confirm.setOnClickListener(view -> {
                    setWeek.dismiss();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TempWeek", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isCurWeek", false);
                    editor.putInt("Week", numberPicker.getValue());
                    editor.apply();
                    SharedPreferences sharedPreferences_toActivity = getActivity().getSharedPreferences("FirstFragment", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor_toActivity = sharedPreferences_toActivity.edit();
                    editor_toActivity.putInt("Start", 2);
                    editor_toActivity.apply();
                    Intent intent = new Intent(getActivity(), ActivityMain.class);
                    startActivity(intent);
                    getActivity().finish();

                });

                cancel.setOnClickListener(view -> setWeek.dismiss());

                setWeek.show();

                return true;

            case R.id.fragment_timetable_start_week:
                final AlertDialog.Builder setStartWeek = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_date_picker, null);

                final DatePicker datePicker = linearLayout.findViewById(R.id.date_picker_intro);

                setStartWeek
                        .setView(linearLayout)
                        .setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {
                            int year = datePicker.getYear();
                            int month = datePicker.getMonth() + 1;
                            int date = datePicker.getDayOfMonth();

                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat sj = new SimpleDateFormat("yyyy-MM-dd");
                            Calendar now = Calendar.getInstance();
                            now.set(year,month,date);
                            System.out.println(year+""+month+""+date);
                            int weekDay = now.get(Calendar.DAY_OF_WEEK);
                            if (year==2020&&month==8&&date==31)
                                weekDay--;
                            weekDay=(weekDay+4)%7;
                            if(weekDay==0)
                                weekDay=7;
                            String today=year+"-"+month+"-"+date;
                            Date d=null;
                            try {
                                d = sj.parse(today);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(d);
                            System.out.println(weekDay);
                            calendar.add(Calendar.DATE, -weekDay+1);
                            System.out.println(sj.format(calendar.getTime()));


                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TimeTable", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("StartWeek", sj.format(calendar.getTime())+ " 00:00:00");
                            editor.apply();
                            SharedPreferences sharedPreferences_toActivity = getActivity().getSharedPreferences("FirstFragment", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor_toActivity = sharedPreferences_toActivity.edit();
                            editor_toActivity.putInt("Start", 2);
                            editor_toActivity.apply();
                            Intent intent = new Intent(getActivity(), ActivityMain.class);
                            startActivity(intent);
                        })
                        .create()
                        .show();

                return true;

            case R.id.fragment_timetable_fast_change:
                SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("TimeTable", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean isVisibilty = sharedPreferences.getBoolean("FastChange", false);
                if (isVisibilty)
                    editor.putBoolean("FastChange", false);
                else
                    editor.putBoolean("FastChange", true);


                editor.apply();
                SharedPreferences sharedPreferences_toActivity = getActivity().getSharedPreferences("FirstFragment", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor_toActivity = sharedPreferences_toActivity.edit();
                editor_toActivity.putInt("Start", 2);
                editor_toActivity.apply();
                Intent intent = new Intent(getActivity(), ActivityMain.class);
                startActivity(intent);
                return true;

            case R.id.fragment_timetable_show_all:
                SharedPreferences sharedPreferences1 = Objects.requireNonNull(getActivity()).getSharedPreferences("TimeTable", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                boolean isVisibilty1 = sharedPreferences1.getBoolean("HideCourse", false);
                if (isVisibilty1)
                    editor1.putBoolean("HideCourse", false);
                else
                    editor1.putBoolean("HideCourse", true);
                editor1.apply();
                Intent intent1 = new Intent(getActivity(), ActivityMain.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //FragmentUI创建
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable_chart, container, false);
        System.out.println("fragmentTimeTableChart被创建");
        SharedPreferences sharedPreferences_curWeek = Objects.requireNonNull(getActivity()).getSharedPreferences("TempWeek", Context.MODE_PRIVATE);
        boolean tempWeek = sharedPreferences_curWeek.getBoolean("isCurWeek", true);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TimeTable", Context.MODE_PRIVATE);
        String start = sharedPreferences.getString("StartWeek", "NULL");

        if (tempWeek) {
            assert start != null;
            if (start.equals("NULL")) {
                getActivity().setTitle(getString(R.string.title_table) + "-" + "第1周");
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.success))
                        .setMessage(getString(R.string.success_get_timetable))
                        .setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {
                        })
                        .create()
                        .show();
            } else {

                try {

                    @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//显示的时间的格式
                    Calendar calendar = Calendar.getInstance();
                    calendar.setFirstDayOfWeek(1);
                    int end_week = calendar.get(Calendar.WEEK_OF_YEAR);
                    calendar.setTime(dateFormat.parse(start));
                    int start_week = calendar.get(Calendar.WEEK_OF_YEAR);
                    int start_year = calendar.get(Calendar.YEAR);
                    calendar.setTime(dateFormat.parse(start_year + "-12-24 00:00:00"));
                    int sum_start_year_weeks = calendar.get(Calendar.WEEK_OF_YEAR);
                    int weeks = end_week - start_week + 1;
                    if (weeks < 1) {
                        int sum_weeks = sum_start_year_weeks - start_week + end_week + 1;
                        System.out.println(sum_weeks);
                        getActivity().setTitle(getString(R.string.title_table) + "-" + "第" + sum_weeks + "周");

                    } else {
                        getActivity().setTitle(getString(R.string.title_table) + "-" + "第" + weeks + "周");
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        } else {
            int weeks = sharedPreferences_curWeek.getInt("Week", 1);
            System.out.println("Test" + weeks);
            getActivity().setTitle(getString(R.string.title_table) + "-" + "第" + weeks + "周");
        }
        System.out.println("SetView");
        return view;
    }

    //Fragment入口
    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("TimeTable", Context.MODE_PRIVATE);

        setTextViewButton();

        if (!sharedPreferences.getBoolean("ClassIsGot", false)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.no_timetable_sql))
                    .setMessage(getString(R.string.get_timetable_sql))
                    .setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {
                        getTimetableDialog = ProgressDialog.show(getActivity(), getString(R.string.alert), getString(R.string.getting_timetable), true);
                        new Thread(new getClasses()).start();

                    })
                    .setNegativeButton(getString(R.string.later), (dialogInterface, i) -> {

                    })
                    .create()
                    .show();
        } else {
            setTimetableView();
        }


    }

    //设置切换周按钮
    private void setTextViewButton() {

        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("TimeTable", Context.MODE_PRIVATE);
        boolean isVisibilty = sharedPreferences.getBoolean("FastChange", false);
        LinearLayout linearLayout = getActivity().findViewById(R.id.timetable_fast_change);
        if (isVisibilty) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }


        FlatButton flatButton_frount = getActivity().findViewById(R.id.textView_frount);
        FlatButton flatButton_now = getActivity().findViewById(R.id.textView_now);
        FlatButton flatButton_next = getActivity().findViewById(R.id.textView_next);

        flatButton_frount.setOnClickListener(view -> {
            SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("TempWeek", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences1.edit();

            boolean isCurWeek = true;
            int curWeek = timetableView.curWeek();
            if (curWeek == 1)
                isCurWeek = false;

            editor.putBoolean("isCurWeek", false);

            SharedPreferences sharedPreferences_curWeek = getActivity().getSharedPreferences("TimeTable", Context.MODE_PRIVATE);
            String start = sharedPreferences_curWeek.getString("StartWeek", "NULL");
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//显示的时间的格式
            if (!isCurWeek) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setFirstDayOfWeek(2);
                    int end_week = calendar.get(Calendar.WEEK_OF_YEAR);
                    int end_year = calendar.get(Calendar.YEAR);
                    calendar.setTime(dateFormat.parse(start));
                    int start_week = calendar.get(Calendar.WEEK_OF_YEAR);
                    int start_year = calendar.get(Calendar.YEAR);
                    int weeks = end_week - start_week + 1;

                    calendar.setTime(dateFormat.parse(start_year + "-12-25 00:00:00"));
                    int sum_start_year_weeks = calendar.get(Calendar.WEEK_OF_YEAR);


                    if (weeks < 1)
                        if (end_year != start_year)
                            editor.putInt("Week", sum_start_year_weeks - start_week + end_week);
                        else
                            editor.putInt("Week", 1);
                    else
                        editor.putInt("Week", weeks - 1);
                } catch (Exception e) {
                    editor.putInt("Week", 1);
                    System.out.println("error");
                }
            } else {
                int cur = timetableView.curWeek();
                System.out.println("else cur test" + cur);
                editor.putInt("Week", cur - 1);
            }

            editor.apply();
            SharedPreferences sharedPreferences_toActivity = getActivity().getSharedPreferences("FirstFragment", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor_toActivity = sharedPreferences_toActivity.edit();
            editor_toActivity.putInt("Start", 2);
            editor_toActivity.apply();
            Intent intent = new Intent(getActivity(), ActivityMain.class);
            startActivity(intent);
            getActivity().finish();
        });

        flatButton_now.setOnClickListener(view -> {
            SharedPreferences sharedPreferences_toActivity = Objects.requireNonNull(getActivity()).getSharedPreferences("FirstFragment", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor_toActivity = sharedPreferences_toActivity.edit();
            editor_toActivity.putInt("Start", 2);
            editor_toActivity.apply();
            Intent intent = new Intent(getActivity(), ActivityMain.class);
            startActivity(intent);
            getActivity().finish();
        });

        flatButton_next.setOnClickListener(view -> {

            SharedPreferences sharedPreferences12 = getActivity().getSharedPreferences("TempWeek", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences12.edit();

            int cur = timetableView.curWeek();
            System.out.println("CurWeek is " + cur);
            boolean isCurWeek = true;
            if (cur == 1)
                isCurWeek = false;
            editor.putBoolean("isCurWeek", false);

            SharedPreferences sharedPreferences_curWeek = getActivity().getSharedPreferences("TimeTable", Context.MODE_PRIVATE);
            String start = sharedPreferences_curWeek.getString("StartWeek", "NULL");
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//显示的时间的格式
            if (!isCurWeek) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setFirstDayOfWeek(2);
                    int end_week = calendar.get(Calendar.WEEK_OF_YEAR);
                    int end_year = calendar.get(Calendar.YEAR);
                    calendar.setTime(dateFormat.parse(start));
                    int start_week = calendar.get(Calendar.WEEK_OF_YEAR);
                    int start_year = calendar.get(Calendar.YEAR);
                    int weeks = end_week - start_week + 1;

                    calendar.setTime(dateFormat.parse(start_year + "-12-25 00:00:00"));
                    int sum_start_year_weeks = calendar.get(Calendar.WEEK_OF_YEAR);

                    if (weeks < 1)
                        if (end_year != start_year)
                            editor.putInt("Week", sum_start_year_weeks - start_week + end_week + 2);
                        else
                            editor.putInt("Week", 1);
                    else
                        editor.putInt("Week", weeks + 1);
                } catch (Exception e) {
                    editor.putInt("Week", 1);
                    System.out.println("error");
                }
            } else {
                System.out.println("else cur test" + cur);
                editor.putInt("Week", cur + 1);
            }

            editor.apply();
            SharedPreferences sharedPreferences_toActivity = getActivity().getSharedPreferences("FirstFragment", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor_toActivity = sharedPreferences_toActivity.edit();
            editor_toActivity.putInt("Start", 2);
            editor_toActivity.apply();
            Intent intent = new Intent(getActivity(), ActivityMain.class);
            startActivity(intent);
            getActivity().finish();
        });

    }

    //设置表内容
    @SuppressLint("Range")
    private void setTimetableView() {
        timetableView = Objects.requireNonNull(getActivity()).findViewById(R.id.id_timetableView);
        SQLHelperTimeTable sqlHelperTimeTable = new SQLHelperTimeTable(getActivity(), "URP_timetable", null, 2);
        SQLiteDatabase sqLiteDatabase = sqlHelperTimeTable.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query("classes", new String[]{"ClassName", "Teacher", "Week", "Data", "Count", "School", "Building", "Room", "Time"}, null, null, null, null, null);
        int course_color = 1;
        int flag = 0;
        final List<Schedule> schedules = new ArrayList<>();
        String[] name_check = new String[40];
        int[] color_check = new int[40];

        while (cursor.moveToNext()) {
            // try {
            String course_name = cursor.getString(cursor.getColumnIndex("ClassName"));
            String room = cursor.getString(cursor.getColumnIndex("Room"));
            String week = cursor.getString(cursor.getColumnIndex("Week"));
            String building = cursor.getString(cursor.getColumnIndex("Building"));
            String teacher = cursor.getString(cursor.getColumnIndex("Teacher"));
            if (cursor.getString(cursor.getColumnIndex("Data")).equals("^") || cursor.getString(cursor.getColumnIndex("Time")).equals("^") || cursor.getString(cursor.getColumnIndex("Count")).equals("^")) {
                continue;
            }


            int day = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Data")));
            int time = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Time")));
            int count = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Count")));
            List<Integer> list = new ArrayList<>();
            String[] weeks = week.split(",");
            for (int i = 0; i < weeks.length; i++) {
                weeks[i] = weeks[i].replace(",", "");
                list.add(Integer.parseInt(weeks[i]));
            }

            boolean isExist = false;

            for (int i = 0; i < name_check.length; i++) {
                if (course_name.equals(name_check[i])) {
                    Schedule schedule = new Schedule(course_name, building + room, teacher, list, time, count, day, color_check[i]);
                    schedules.add(schedule);
                    isExist = true;
                    break;
                }
            }

            if (isExist) {
                continue;
            }


            name_check[flag] = course_name;
            color_check[flag] = course_color;
            Schedule schedule = new Schedule(course_name, building + room, teacher, list, time, count, day, course_color++);
            schedules.add(schedule);

            sqLiteDatabase.close();

            //  } catch (Exception e) {
            //      System.out.println(e.toString());
            //       return;
            //   }


        }

        SharedPreferences sharedPreferences0 = getActivity().getSharedPreferences("TimeTable", Context.MODE_PRIVATE);
        boolean hideCourse = sharedPreferences0.getBoolean("HideCourse", false);
        if (hideCourse)
            timetableView.isShowNotCurWeek(false);
        timetableView.showDateView();
        timetableView.data(schedules)
                .alpha((float) 50, (float) 0, (float) 100)
                .monthWidthDp(20)
                .callback((ISchedule.OnItemClickListener) (v, scheduleList) -> {
                    for (Schedule item : scheduleList) {
                        setItemOnClickListener(item);
                    }
                })
                .callback(this::setFlagOnClickListenser);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TimeTable", Context.MODE_PRIVATE);
        String start = sharedPreferences.getString("StartWeek", "NULL");
        SharedPreferences sharedPreferences_curWeek = getActivity().getSharedPreferences("TempWeek", Context.MODE_PRIVATE);
        boolean curWeek = sharedPreferences_curWeek.getBoolean("isCurWeek", true);
        assert start != null;
        if (start.equals("NULL")) {
            timetableView.curWeek(1)
                    .showView();
            return;
        }


        System.out.println(start);
        timetableView.curWeek(start)
                .showView();

        if (!curWeek) {
            int cur = timetableView.curWeek();
            int to_week = sharedPreferences_curWeek.getInt("Week", 1);
            //更新切换后的日期，从当前周cur->切换的周week
            timetableView.onDateBuildListener()
                    .onUpdateDate(cur, to_week);
            timetableView.changeWeekForce(to_week);
            SharedPreferences.Editor editor = sharedPreferences_curWeek.edit();

            editor.putBoolean("isCurWeek", true);
            System.out.println("isCurWeek被修改为true");

            editor.apply();
        }


    }

    //设置课程点击监听
    @SuppressLint("SetTextI18n")
    private void setItemOnClickListener(Schedule item) {

        SQLHelperTimeTable sqlHelperTimeTable = new SQLHelperTimeTable(getActivity(), "URP_timetable", null, 2);
        SQLiteDatabase sqLiteDatabase = sqlHelperTimeTable.getReadableDatabase();
        String selection_name = item.getName();
        String selection_data = String.valueOf(item.getDay());
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query("classes", new String[]{"ClassName", "ClassId", "Credit", "ClassAttribute", "ExamAttribute", "Teacher", "Week", "Data", "Count", "School", "Building", "Room", "Time"}, "ClassName=? and Data=?", new String[]{selection_name, selection_data}, null, null, null);

        android.app.AlertDialog.Builder CourseMessage = new android.app.AlertDialog.Builder(getActivity());
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_course_message, null);  //从另外的布局关联组件
        TextView textView_name = linearLayout.findViewById(R.id.alertdialog_course_message_name);
        TextView textView_id = linearLayout.findViewById(R.id.alertdialog_course_message_id);
        TextView textView_credit = linearLayout.findViewById(R.id.alertdialog_course_message_credit);
        TextView textView_attribute = linearLayout.findViewById(R.id.alertdialog_course_message_attribute);
        TextView textView_examattribute = linearLayout.findViewById(R.id.alertdialog_course_message_examAttribute);
        TextView textView_week = linearLayout.findViewById(R.id.alertdialog_course_message_week);
        TextView textView_time = linearLayout.findViewById(R.id.alertdialog_course_message_time);
        TextView textView_teacher = linearLayout.findViewById(R.id.alertdialog_course_message_teacher);
        TextView textView_school = linearLayout.findViewById(R.id.alertdialog_course_message_school);
        TextView textView_room = linearLayout.findViewById(R.id.alertdialog_course_message_room);
        String name = "";
        String day = "";
        //noinspection LoopStatementThatDoesntLoop
        while (cursor.moveToNext()) {
            day = cursor.getString(cursor.getColumnIndex("Data"));
            name = cursor.getString(cursor.getColumnIndex("ClassName"));
            textView_name.setText(Objects.requireNonNull(getActivity()).getText(R.string.course_name) + name);
            textView_id.setText(getActivity().getText(R.string.course_id) + cursor.getString(cursor.getColumnIndex("ClassId")));
            textView_credit.setText(getActivity().getText(R.string.credit) + cursor.getString(cursor.getColumnIndex("Credit")));
            textView_attribute.setText(getActivity().getText(R.string.course_attribute) + cursor.getString(cursor.getColumnIndex("ClassAttribute")));
            textView_examattribute.setText(getActivity().getText(R.string.exam_attribute) + cursor.getString(cursor.getColumnIndex("ExamAttribute")));
            textView_week.setText(getActivity().getText(R.string.weeks) + cursor.getString(cursor.getColumnIndex("Week")));
            int count = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Count")));
            int start = Integer.parseInt(cursor.getString(cursor.getColumnIndex("Time")));
            textView_time.setText(getActivity().getText(R.string.course_time) + String.valueOf(start) + "~" + (start + count - 1));
            textView_teacher.setText(getActivity().getText(R.string.teacher) + cursor.getString(cursor.getColumnIndex("Teacher")));
            textView_school.setText(getActivity().getText(R.string.school) + cursor.getString(cursor.getColumnIndex("School")));
            textView_room.setText(getActivity().getText(R.string.room) + cursor.getString(cursor.getColumnIndex("Building")) + cursor.getString(cursor.getColumnIndex("Room")));
            break;
        }
        String finalName = name;
        String finalDay = day;
        CourseMessage.setView(linearLayout)
                .setPositiveButton(Objects.requireNonNull(getActivity()).getText(R.string.confirm), (dialogInterface, i) -> {

                })
                .setNegativeButton(getActivity().getText(R.string.edit), (dialogInterface, i) -> {
                    Intent intent = new Intent(getActivity(), ActivityUpdateEditor.class);
                    intent.putExtra("name", item.getName());
                    intent.putExtra("data", item.getDay());
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).finish();


                })
                .setNeutralButton(getActivity().getString(R.string.delete), (dialogInterface, i) -> {
                    SQLHelperTimeTable sqlHelperTimeTable1 = new SQLHelperTimeTable(getActivity(), "URP_timetable", null, 2);
                    SQLiteDatabase sqLiteDatabase1 = sqlHelperTimeTable1.getWritableDatabase();
                    sqLiteDatabase1.delete("classes", "ClassName = ? and Data = ?", new String[]{finalName, String.valueOf(finalDay)});
                    setTimetableView();
                })
                .create()
                .show();
    }

    //设置旗标监听
    private void setFlagOnClickListenser(int data, int start) {
        Intent intent = new Intent(getActivity(), ActivityEditor.class);
        intent.putExtra("start", start);
        intent.putExtra("data", data);
        startActivity(intent);
        Objects.requireNonNull(getActivity()).finish();
    }


    //获取课程的线程
    class getClasses implements Runnable {

        @Override
        public void run() {
            SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName = sharedPreferences.getString("VPNName", "");
            String VPNPass = sharedPreferences.getString("VPNPass", "");
            String username = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");
            FunctionsPublicBasic function = new FunctionsPublicBasic();
            if (!function.setClassTableSQL(getActivity(), VPNName, VPNPass, username, password)) {
                handlerMessage = "fail";
            } else {
                handlerMessage = "success";
            }
            handler.sendMessage(handler.obtainMessage());


        }
    }

    //异步消息同步
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg) {
            if (handlerMessage.equals("fail")) {
                getTimetableDialog.dismiss();
                new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                        .setTitle(getString(R.string.error))
                        .setMessage(getString(R.string.fail_to_get_timetable))
                        .setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {

                        })
                        .create()
                        .show();
                handlerMessage = "";

            } else {
                handlerMessage = "";
                getTimetableDialog.dismiss();
                SharedPreferences sharedPreferences_t = Objects.requireNonNull(getActivity()).getSharedPreferences("TempWeek", Context.MODE_PRIVATE);
                String start = sharedPreferences_t.getString("StartWeek", "NULL");
                assert start != null;
                if (start.equals("NULL")) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.success))
                            .setMessage(getString(R.string.success_get_timetable))
                            .setPositiveButton(getString(R.string.confirm), (dialogInterface, i) -> {
                            })
                            .create()
                            .show();
                }
                setTimetableView();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler update_handler = new Handler() {
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg) {
            new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setTitle(getActivity().getString(R.string.error))
                    .setMessage(getActivity().getString(R.string.editor_error))
                    .setPositiveButton(getActivity().getString(R.string.confirm), null)
                    .create()
                    .show();
            handlerMessage = "";
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler create_handler = new Handler() {
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg) {
            if (handlerMessage.equals("BlankError")) {
                new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                        .setTitle(getActivity().getString(R.string.error))
                        .setMessage(getActivity().getString(R.string.blank_error))
                        .setPositiveButton(getActivity().getString(R.string.confirm), null)
                        .create()
                        .show();
                handlerMessage = "";
            }
            if (handlerMessage.equals("WeekError")) {
                new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                        .setTitle(getActivity().getString(R.string.error))
                        .setMessage(getActivity().getString(R.string.week_error))
                        .setPositiveButton(getActivity().getString(R.string.confirm), null)
                        .create()
                        .show();
                handlerMessage = "";
            }
            if (handlerMessage.equals("CountError")) {
                new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                        .setTitle(getActivity().getString(R.string.error))
                        .setMessage(getActivity().getString(R.string.count_error))
                        .setPositiveButton(getActivity().getString(R.string.confirm), null)
                        .create()
                        .show();
                handlerMessage = "";
            }

        }
    };
}
