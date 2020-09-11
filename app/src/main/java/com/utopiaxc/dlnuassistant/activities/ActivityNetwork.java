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
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
    TextView textViewNetworkStop;
    Button buttonNetworkStop;
    Button buttonOffline;
    Button buttonChangeSet;
    @SuppressWarnings("deprecation")
    private static ProgressDialog progressDialog = null;
    HashMap<String, String> messages;
    boolean messagesIsGot = false;
    boolean isLogout;
    String setCheckBack;
    boolean isBookedSet;
    boolean netFunction;

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
        textViewNetworkStop=findViewById(R.id.textViewNetworkStop);
        textViewNetworkStop.setVisibility(View.GONE);
        buttonOffline = findViewById(R.id.buttonOffline);
        buttonOffline.setVisibility(View.GONE);
        buttonChangeSet = findViewById(R.id.buttonChangeSet);
        buttonChangeSet.setVisibility(View.GONE);
        buttonNetworkStop=findViewById(R.id.buttonNetworkStop);
        buttonNetworkStop.setVisibility(View.GONE);
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
            isLogout= function.logoutNetwork(VPNName, VPNPass, NetName, NetPass);
            messageHandlerLogout.sendMessage(messageHandlerLogout.obtainMessage());
        }
    }


    class checkSet implements Runnable{

        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName = sharedPreferences.getString("VPNName", null);
            String VPNPass = sharedPreferences.getString("VPNPass", null);
            sharedPreferences = getSharedPreferences("Net", Context.MODE_PRIVATE);
            String NetName = sharedPreferences.getString("NetName", null);
            String NetPass = sharedPreferences.getString("NetPass", null);

            FunctionsPublicBasic function = new FunctionsPublicBasic();
            setCheckBack=function.getSetCheck(VPNName, VPNPass, NetName, NetPass);
            messageHandlerSetCheck.sendMessage(messageHandlerSetCheck.obtainMessage());
        }
    }

    class bookSet implements Runnable{
        private int setNum;
        bookSet(int setNum){
            this.setNum=setNum;
        }

        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName = sharedPreferences.getString("VPNName", null);
            String VPNPass = sharedPreferences.getString("VPNPass", null);
            sharedPreferences = getSharedPreferences("Net", Context.MODE_PRIVATE);
            String NetName = sharedPreferences.getString("NetName", null);
            String NetPass = sharedPreferences.getString("NetPass", null);

            FunctionsPublicBasic function = new FunctionsPublicBasic();
            isBookedSet=function.bookSet(VPNName, VPNPass, NetName, NetPass,setNum);
            messageHandlerBookSet.sendMessage(messageHandlerSetCheck.obtainMessage());
        }
    }

    class stopNet implements  Runnable{

        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName = sharedPreferences.getString("VPNName", null);
            String VPNPass = sharedPreferences.getString("VPNPass", null);
            sharedPreferences = getSharedPreferences("Net", Context.MODE_PRIVATE);
            String NetName = sharedPreferences.getString("NetName", null);
            String NetPass = sharedPreferences.getString("NetPass", null);

            FunctionsPublicBasic function = new FunctionsPublicBasic();
            netFunction=function.stopNetwork(VPNName, VPNPass, NetName, NetPass);
            messageHandlerNetFunctions.sendMessage(messageHandlerSetCheck.obtainMessage());
        }
    }

    class reopenNet implements  Runnable{

        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName = sharedPreferences.getString("VPNName", null);
            String VPNPass = sharedPreferences.getString("VPNPass", null);
            sharedPreferences = getSharedPreferences("Net", Context.MODE_PRIVATE);
            String NetName = sharedPreferences.getString("NetName", null);
            String NetPass = sharedPreferences.getString("NetPass", null);

            FunctionsPublicBasic function = new FunctionsPublicBasic();
            netFunction=function.reopenNetwork(VPNName, VPNPass, NetName, NetPass);
            messageHandlerNetFunctions.sendMessage(messageHandlerSetCheck.obtainMessage());
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
                textViewNetworkStop.setText(messages.get("statue"));
                textViewNetworkStop.setVisibility(View.VISIBLE);
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
                buttonChangeSet.setText("预约套餐");
                buttonChangeSet.setOnClickListener(e -> {
                    progressDialog = ProgressDialog.show(context, "正在查询预约状态", "请稍候", true);
                    new Thread(new checkSet()).start();
                });
                buttonNetworkStop.setText("停复机");
                buttonNetworkStop.setOnClickListener(e->{
                    if (textViewNetworkStop.getText().equals("停复机状态：停机")){
                        new AlertDialog.Builder(context)
                                .setTitle(Objects.requireNonNull(context).getString(R.string.warning))
                                .setMessage("确认复通？（停机后将开启网络使用，同时解冻流量与时长）")
                                .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                                    progressDialog = ProgressDialog.show(context, "正在操作复通", "请稍候", true);
                                    new Thread(new reopenNet()).start();
                                })
                                .setNegativeButton(context.getString(R.string.cancel),null)
                                .create().show();
                    }else {
                        new AlertDialog.Builder(context)
                                .setTitle(Objects.requireNonNull(context).getString(R.string.warning))
                                .setMessage("确认停机？（停机后将暂停网络使用，冻结流量与时长直至复通）")
                                .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                                    progressDialog = ProgressDialog.show(context, "正在操作停机", "请稍候", true);
                                    new Thread(new stopNet()).start();
                                })
                                .setNegativeButton(context.getString(R.string.cancel), null)
                                .create().show();
                    }
                });
                buttonNetworkStop.setVisibility(View.VISIBLE);


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
    private Handler messageHandlerNetFunctions = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if (netFunction){
                new AlertDialog.Builder(context)
                        .setTitle(Objects.requireNonNull(context).getString(R.string.success))
                        .setMessage("操作成功")
                        .setPositiveButton("确认", (dialog, which) -> {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        })
                        .create().show();
            }else{
                new AlertDialog.Builder(context)
                        .setTitle(Objects.requireNonNull(context).getString(R.string.error))
                        .setMessage("操作失败")
                        .setPositiveButton("确认", (dialog, which) -> {

                        })
                        .create().show();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler messageHandlerSetCheck = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if (setCheckBack.equals("ERROR")){
                new AlertDialog.Builder(context)
                        .setTitle(Objects.requireNonNull(context).getString(R.string.error))
                        .setMessage("网络错误")
                        .setPositiveButton("确认", (dialog, which) -> {
                        })
                        .create().show();
            }
            else if (setCheckBack.equals("DONTHAVE")){
                new AlertDialog.Builder(context)
                        .setTitle(Objects.requireNonNull(context).getString(R.string.warning))
                        .setMessage("无法查询到您的系统套餐，可能系统存在变更，目前仅支持本科生套餐修改。")
                        .setPositiveButton("确认", (dialog, which) -> {
                        })
                        .create().show();
            }
            else{
                new AlertDialog.Builder(context)
                        .setTitle("您预约的下周期套餐")
                        .setMessage(setCheckBack)
                        .setPositiveButton("修改", (dialog, which) -> {
                            AlertDialog.Builder bookSet = new AlertDialog.Builder(context);
                            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_set_selector, null);  //从另外的布局关联组件

                            final RadioButton radioButtonTen = linearLayout.findViewById(R.id.radioButtonTenSet);
                            final RadioButton radioButtonTwenty = linearLayout.findViewById(R.id.radioButtonTwentySet);
                            final RadioButton radioButtonThirty = linearLayout.findViewById(R.id.radioButtonThirtySet);

                            bookSet.setTitle("预约套餐")
                                    .setView(linearLayout)
                                    .setPositiveButton(getString(R.string.confirm), (dialog1, which1) -> {
                                        if (radioButtonTen.isChecked()) {
                                            progressDialog = ProgressDialog.show(context, "正在提交预约", "请稍候", true);
                                            new Thread(new bookSet(2)).start();
                                        } else if (radioButtonTwenty.isChecked()) {
                                            progressDialog = ProgressDialog.show(context, "正在提交预约", "请稍候", true);
                                            new Thread(new bookSet(4)).start();
                                        }else if (radioButtonThirty.isChecked()){
                                            progressDialog = ProgressDialog.show(context, "正在提交预约", "请稍候", true);
                                            new Thread(new bookSet(3)).start();
                                        }
                                    })
                                    .create()
                                    .show();

                        })
                        .setNegativeButton("确认",(dialog, which) -> {

                        })
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

    @SuppressLint("HandlerLeak")
    private Handler messageHandlerBookSet = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if (isBookedSet){
                new AlertDialog.Builder(context)
                        .setTitle(Objects.requireNonNull(context).getString(R.string.success))
                        .setMessage("套餐更改成功")
                        .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                        })
                        .create().show();
            }else{
                new AlertDialog.Builder(context)
                        .setTitle(Objects.requireNonNull(context).getString(R.string.error))
                        .setMessage("套餐更改失败")
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
