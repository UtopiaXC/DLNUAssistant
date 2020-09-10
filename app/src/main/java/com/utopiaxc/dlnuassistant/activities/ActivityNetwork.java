package com.utopiaxc.dlnuassistant.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.utopiaxc.dlnuassistant.R;
import com.utopiaxc.dlnuassistant.fuctions.FunctionsPublicBasic;


import java.util.Objects;

public class ActivityNetwork extends AppCompatActivity {
    TextView textViewAccount;
    TextView textViewBalance;
    TextView textViewUsedTime;
    TextView textViewUsedBand;
    TextView textViewCondition;
    TextView textViewSet;
    Button buttonOffline;
    Button buttonChangeSet;
    //Activity入口
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences=getSharedPreferences("Theme",MODE_PRIVATE);
        int theme=sharedPreferences.getInt("theme", R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        Bind();
        new Thread(new getMessages()).start();

    }

    private void Bind(){
        textViewAccount=findViewById(R.id.textViewNetworkAccount);
        textViewBalance=findViewById(R.id.textViewNetworkBalance);
        textViewUsedTime=findViewById(R.id.textViewNetworkUsedTime);
        textViewUsedBand=findViewById(R.id.textViewNetworkUsedBand);
        textViewCondition=findViewById(R.id.textViewNetworkCondition);
        textViewSet=findViewById(R.id.textViewNetworkSet);
        buttonOffline=findViewById(R.id.buttonOffline);
        buttonChangeSet=findViewById(R.id.buttonChangeSet);
    }

    class getMessages implements Runnable{

        @Override
        public void run() {
            SharedPreferences sharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName=sharedPreferences.getString("VPNName",null);
            String VPNPass=sharedPreferences.getString("VPNPass",null);
            sharedPreferences=getSharedPreferences("Net", Context.MODE_PRIVATE);
            String NetName=sharedPreferences.getString("NetName",null);
            String NetPass=sharedPreferences.getString("NetPass",null);
            FunctionsPublicBasic function = new FunctionsPublicBasic();

        }
    }

    //返回键
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
