package com.utopiaxc.dlnuassistant.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.utopiaxc.dlnuassistant.R;
import com.utopiaxc.dlnuassistant.fuctions.FunctionsPublicBasic;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ActivityNetwork extends AppCompatActivity {
    Context context;
    TextView textViewAccount;
    TextView textViewBalance;
    TextView textViewUsedTime;
    TextView textViewUsedBand;
    TextView textViewCondition;
    TextView textViewSet;
    Button buttonOffline;
    Button buttonChangeSet;
    @SuppressWarnings("deprecation")
    private static ProgressDialog progressDialog = null;
    HashMap<String, String> messages;
    boolean messagesIsGot=false;

    //Activity入口
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("Theme", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("theme", R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        Bind();
        context=this;
        progressDialog = ProgressDialog.show(context, "正在获取", "正在获取校园网信息", true);
        new Thread(new getMessages()).start();

    }

    private void Bind() {
        textViewAccount = findViewById(R.id.textViewNetworkAccount);
        textViewAccount.setVisibility(View.GONE);
        textViewBalance = findViewById(R.id.textViewNetworkBalance);
        textViewBalance.setVisibility(View.GONE);
        textViewUsedTime = findViewById(R.id.textViewNetworkUsedTime);
        textViewUsedTime.setVisibility(View.GONE);
        textViewUsedBand = findViewById(R.id.textViewNetworkUsedBand);
        textViewUsedBand.setVisibility(View.GONE);
        textViewCondition = findViewById(R.id.textViewNetworkCondition);
        textViewCondition.setVisibility(View.GONE);
        textViewSet = findViewById(R.id.textViewNetworkSet);
        textViewSet.setVisibility(View.GONE);
        buttonOffline = findViewById(R.id.buttonOffline);
        buttonOffline.setVisibility(View.GONE);
        buttonChangeSet = findViewById(R.id.buttonChangeSet);
        buttonChangeSet.setVisibility(View.GONE);
    }

    class getMessages implements Runnable {

        @Override
        public void run() {
            int flag=0;
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName = sharedPreferences.getString("VPNName", null);
            String VPNPass = sharedPreferences.getString("VPNPass", null);
            sharedPreferences = getSharedPreferences("Net", Context.MODE_PRIVATE);
            String NetName = sharedPreferences.getString("NetName", null);
            String NetPass = sharedPreferences.getString("NetPass", null);
            for (int i=0;i<3;i++){
                FunctionsPublicBasic function = new FunctionsPublicBasic();
                 messages=new HashMap<>();
                if(function.getNetworkMessages(VPNName, VPNPass, NetName, NetPass, messages)){
                    messagesIsGot=true;
                    messageHandler.sendMessage(messageHandler.obtainMessage());
                    break;
                }
            }
            if (!messagesIsGot){
                messageHandler.sendMessage(messageHandler.obtainMessage());
            }
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if (messagesIsGot) {
                textViewBalance.setText(messages.get("balance"));
                textViewBalance.setVisibility(View.VISIBLE);
                textViewAccount.setText(messages.get("account"));
                textViewAccount.setVisibility(View.VISIBLE);
                textViewCondition.setText(messages.get("online"));
                textViewCondition.setVisibility(View.VISIBLE);
                textViewSet.setText(messages.get("set"));
                textViewSet.setVisibility(View.VISIBLE);
                buttonChangeSet.setVisibility(View.VISIBLE);
                buttonChangeSet.setText("更改套餐");
            }else{
                new AlertDialog.Builder(context)
                        .setTitle(Objects.requireNonNull(context).getString(R.string.warning))
                        .setMessage("获取信息失败")
                        .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                        })
                        .create().show();
            }
        }
    };


    //返回键
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
