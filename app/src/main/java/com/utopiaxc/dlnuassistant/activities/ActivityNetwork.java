package com.utopiaxc.dlnuassistant.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import java.util.Objects;

public class ActivityNetwork extends AppCompatActivity {
    Context context;
    TextView textViewAccount;
    TextView textViewBalance;
    TextView textViewUsedTime;
    TextView textViewUsedBand;
    TextView textViewOverdate;
    TextView textViewCondition;
    TextView textViewSet;
    Button buttonOffline;
    Button buttonChangeSet;
    @SuppressWarnings("deprecation")
    private static ProgressDialog progressDialog = null;
    HashMap<String, String> messages;
    boolean messagesIsGot = false;
    boolean isLogout;

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
        context = this;
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
        textViewOverdate = findViewById(R.id.textViewNetworkOverdate);
        textViewOverdate.setVisibility(View.GONE);
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
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName = sharedPreferences.getString("VPNName", null);
            String VPNPass = sharedPreferences.getString("VPNPass", null);
            sharedPreferences = getSharedPreferences("Net", Context.MODE_PRIVATE);
            String NetName = sharedPreferences.getString("NetName", null);
            String NetPass = sharedPreferences.getString("NetPass", null);
            for (int i = 0; i < 5; i++) {
                FunctionsPublicBasic function = new FunctionsPublicBasic();
                messages = new HashMap<>();
                if (function.getNetworkMessages(VPNName, VPNPass, NetName, NetPass, messages)) {
                    messagesIsGot = true;
                    messageHandler.sendMessage(messageHandler.obtainMessage());
                    break;
                }
            }
            if (!messagesIsGot) {
                messageHandler.sendMessage(messageHandler.obtainMessage());
            }
        }
    }

    class logout implements Runnable{
        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName = sharedPreferences.getString("VPNName", null);
            String VPNPass = sharedPreferences.getString("VPNPass", null);
            sharedPreferences = getSharedPreferences("Net", Context.MODE_PRIVATE);
            String NetName = sharedPreferences.getString("NetName", null);
            String NetPass = sharedPreferences.getString("NetPass", null);
            FunctionsPublicBasic function = new FunctionsPublicBasic();
            if (function.logoutNetwork(VPNName, VPNPass, NetName, NetPass)){
                isLogout=true;
                messageHandlerLogout.sendMessage(messageHandlerLogout.obtainMessage());
            }else {
                isLogout = false;
                messageHandlerLogout.sendMessage(messageHandlerLogout.obtainMessage());
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
                textViewSet.setText(messages.get("set"));
                textViewSet.setVisibility(View.VISIBLE);
                textViewOverdate.setText(messages.get("overdate"));
                textViewOverdate.setVisibility(View.VISIBLE);
                textViewUsedTime.setText(messages.get("usedtime"));
                textViewUsedTime.setVisibility(View.VISIBLE);
                textViewUsedBand.setText(messages.get("usedband"));
                textViewUsedBand.setVisibility(View.VISIBLE);
                textViewCondition.setText(messages.get("online"));
                textViewCondition.setVisibility(View.VISIBLE);
                buttonOffline.setVisibility(View.VISIBLE);
                buttonOffline.setText("强制下线");
                buttonOffline.setOnClickListener(e -> {
                    if (textViewCondition.getText().equals("状态：离线")){
                        new AlertDialog.Builder(context)
                                .setTitle(Objects.requireNonNull(context).getString(R.string.warning))
                                .setMessage("当前无在线设备？")
                                .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                                })
                                .create().show();
                    }else {
                        new AlertDialog.Builder(context)
                                .setTitle(Objects.requireNonNull(context).getString(R.string.warning))
                                .setMessage("确认强制下线所有设备？")
                                .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                                    progressDialog = ProgressDialog.show(context, "正在离线", "请稍候", true);
                                    new Thread(new logout()).start();
                                })
                                .setNegativeButton(context.getString(R.string.cancel), null)
                                .create().show();
                    }
                });
                buttonChangeSet.setVisibility(View.VISIBLE);
                buttonChangeSet.setText("更改套餐");
                buttonChangeSet.setOnClickListener(e -> {
                    new AlertDialog.Builder(context)
                            .setTitle(Objects.requireNonNull(context).getString(R.string.warning))
                            .setMessage("当前功能还在开发")
                            .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                            })
                            .create().show();
                });


            } else {
                new AlertDialog.Builder(context)
                        .setTitle(Objects.requireNonNull(context).getString(R.string.warning))
                        .setMessage("获取信息失败，受到学校土豆服务器限制，请多尝试几次。如果您的密码已修改，请点击不重试后进入页面点击右上角菜单进行注销")
                        .setPositiveButton("重试", (dialog, which) -> {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        })
                        .setNegativeButton("不重试",null)
                        .create().show();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler messageHandlerLogout = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if (isLogout){
                new AlertDialog.Builder(context)
                        .setTitle(Objects.requireNonNull(context).getString(R.string.success))
                        .setMessage("已成功请求下线接口，如未下线请重新尝试")
                        .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        })
                        .create().show();
            }else{
                new AlertDialog.Builder(context)
                        .setTitle(Objects.requireNonNull(context).getString(R.string.error))
                        .setMessage("请求下线接口失败，请尝试重新下线")
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
