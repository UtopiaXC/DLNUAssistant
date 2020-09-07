package com.utopiaxc.urpassistant.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.utopiaxc.urpassistant.R;
import com.utopiaxc.urpassistant.fuctions.FunctionsPublicBasic;
import com.utopiaxc.urpassistant.sqlite.SQLHelperExamInfo;
import com.utopiaxc.urpassistant.sqlite.SQLHelperGradesList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityExamInfo extends AppCompatActivity {
    private ListView listView;
    private Context context=this;
    private MaterialRefreshLayout refresh;
    private String handerMessgae="";

    //Activity入口
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences=getSharedPreferences("Theme", MODE_PRIVATE);
        int theme=sharedPreferences.getInt("theme", R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradelist);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用

        //预绑定
        listView=findViewById(R.id.listview_grade_list);
        refresh=findViewById(R.id.grades_refresh);
        context=this;

        sharedPreferences=getSharedPreferences("ExamInfo", MODE_PRIVATE);
        if(!sharedPreferences.getBoolean("ExamInfoIsGot",false))
            new AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.first_get_grades)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create()
                    .show();
        else{
            setListView();
        }
        //布局设置
        setRefresh();

    }

    //设置外观
    public void setListView() {
        SQLHelperExamInfo sqlHelperExamInfo = new SQLHelperExamInfo(this,"URP_Exam",null,2);
        SQLiteDatabase sqLiteDatabase=sqlHelperExamInfo.getReadableDatabase();
        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();

        String[] course_name=new String[200];
        String[] location=new String[200];
        String[] time=new String[200];
        int flag=0;

        Cursor cursor = sqLiteDatabase.query("exams", new String[]{"ExamName","ExamSchool","ExamBuilding","ExamRoom","ExamData","ExamTime"}, null, null, null, null, null);
        while(cursor.moveToNext()){
            course_name[flag]=cursor.getString(cursor.getColumnIndex("ExamName"));
            location[flag]=cursor.getString(cursor.getColumnIndex("ExamSchool"))+" "+cursor.getString(cursor.getColumnIndex("ExamBuilding"))+" "+cursor.getString(cursor.getColumnIndex("ExamRoom"));
            time[flag]=cursor.getString(cursor.getColumnIndex("ExamData"))+" "+cursor.getString(cursor.getColumnIndex("ExamTime"));

            System.out.println(location[flag]);

            if(location[flag].equals("  "))
                location[flag]=getString(R.string.unknown);
            if(time[flag].equals(" "))
                time[flag]=getString(R.string.unknown);

            location[flag]=getString(R.string.exam_location)+location[flag];
            time[flag]=getString(R.string.exam_time)+time[flag];
            flag++;
        }
        cursor.close();
        sqLiteDatabase.close();


        for (int i = 0; i <flag; i++) {
            Map<String, Object> showitem = new HashMap<String, Object>();

            showitem.put("name", course_name[i]);
            showitem.put("location", location[i]);
            showitem.put("time", time[i]);
            listitem.add(showitem);
        }



        SimpleAdapter adapter = new SimpleAdapter(context, listitem, R.layout.recycleview_examinfo, new String[]{"name", "location", "time"}, new int[]{R.id.exam_card_exam_name, R.id.exam_location, R.id.exam_time});
        listView.setAdapter(adapter);
    }

    //设置刷新
    public void setRefresh() {
            refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    new Thread(new GetExamInfo()).start();
                }
            });
    }

    //获取考试信息表
    class GetExamInfo implements Runnable{

        @Override
        public void run() {

            FunctionsPublicBasic function = new FunctionsPublicBasic();
            SharedPreferences sharedPreferences=getSharedPreferences("user",MODE_PRIVATE);
            String address=sharedPreferences.getString("address","");
            String username=sharedPreferences.getString("username","");
            String password=sharedPreferences.getString("password","");
            if(function.setExamInfo(context,address,username,password)) {
                handerMessgae="over";
                messageHandler.sendMessage(messageHandler.obtainMessage());

            }
            else{
                handerMessgae="fail";
                messageHandler.sendMessage(messageHandler.obtainMessage());
            }
        }
    }

    //异步消息同步
    @SuppressLint("HandlerLeak")
    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (handerMessgae.equals("fail")) {
                refresh.finishRefresh();
                Toast.makeText(context,getString(R.string.refresh_failed),Toast.LENGTH_LONG).show();
            }else{
                setListView();
                refresh.finishRefresh();
                Toast.makeText(context,getString(R.string.refresh_successful),Toast.LENGTH_LONG).show();
            }
        }
    };

    //返回键
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
