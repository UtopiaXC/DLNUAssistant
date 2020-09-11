package com.utopiaxc.dlnuassistant.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.utopiaxc.dlnuassistant.R;
import com.utopiaxc.dlnuassistant.fuctions.FunctionsPublicBasic;
import java.util.HashMap;
import java.util.Objects;
import io.github.varenyzc.opensourceaboutpages.AboutPageMessageItem;
import io.github.varenyzc.opensourceaboutpages.MessageCard;

public class ActivityNetwork extends AppCompatActivity {
    AlertDialog progress;
    private MessageCard messageCard;
    Context context;
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

        Thread getNetworkMessages=new Thread(new getMessages());
        showProgress((dialogInterface, i) -> {
            getNetworkMessages.interrupt();
            finish();
        });
        getNetworkMessages.start();
    }

    public void showProgress(DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_progress, null);  //从另外的布局关联组件
        final TextView textView = linearLayout.findViewById(R.id.progressText);
        textView.setText(getString(R.string.executing));
        builder.setTitle(getString(R.string.please_wait));
        builder.setView(linearLayout);
        builder.setPositiveButton(getString(R.string.interrupt),listener);
        builder.create();
        progress=builder.show();
        progress.setCanceledOnTouchOutside(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_network, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.activity_network_refresh:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                return true;
            case R.id.activity_network_logout:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.confirm_logout_network))
                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                            SharedPreferences sharedPreferences = Objects.requireNonNull(this.getSharedPreferences("Net", MODE_PRIVATE));
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("NetName", "");
                            editor.putString("NetPass", "");
                            editor.putBoolean("NetIsSet", false);
                            editor.apply();
                            finish();
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .create().show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void Bind() {
        messageCard = findViewById(R.id.network_card);
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

    class logout implements Runnable {
        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName = sharedPreferences.getString("VPNName", null);
            String VPNPass = sharedPreferences.getString("VPNPass", null);
            sharedPreferences = getSharedPreferences("Net", Context.MODE_PRIVATE);
            String NetName = sharedPreferences.getString("NetName", null);
            String NetPass = sharedPreferences.getString("NetPass", null);
            FunctionsPublicBasic function = new FunctionsPublicBasic();
            isLogout = function.logoutNetwork(VPNName, VPNPass, NetName, NetPass);
            messageHandlerLogout.sendMessage(messageHandlerLogout.obtainMessage());
        }
    }


    class checkSet implements Runnable {

        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName = sharedPreferences.getString("VPNName", null);
            String VPNPass = sharedPreferences.getString("VPNPass", null);
            sharedPreferences = getSharedPreferences("Net", Context.MODE_PRIVATE);
            String NetName = sharedPreferences.getString("NetName", null);
            String NetPass = sharedPreferences.getString("NetPass", null);

            FunctionsPublicBasic function = new FunctionsPublicBasic();
            setCheckBack = function.getSetCheck(VPNName, VPNPass, NetName, NetPass);
            messageHandlerSetCheck.sendMessage(messageHandlerSetCheck.obtainMessage());
        }
    }

    class bookSet implements Runnable {
        private int setNum;

        bookSet(int setNum) {
            this.setNum = setNum;
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
            isBookedSet = function.bookSet(VPNName, VPNPass, NetName, NetPass, setNum);
            messageHandlerBookSet.sendMessage(messageHandlerSetCheck.obtainMessage());
        }
    }

    class stopNet implements Runnable {

        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName = sharedPreferences.getString("VPNName", null);
            String VPNPass = sharedPreferences.getString("VPNPass", null);
            sharedPreferences = getSharedPreferences("Net", Context.MODE_PRIVATE);
            String NetName = sharedPreferences.getString("NetName", null);
            String NetPass = sharedPreferences.getString("NetPass", null);

            FunctionsPublicBasic function = new FunctionsPublicBasic();
            netFunction = function.stopNetwork(VPNName, VPNPass, NetName, NetPass);
            messageHandlerNetFunctions.sendMessage(messageHandlerSetCheck.obtainMessage());
        }
    }

    class reopenNet implements Runnable {

        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String VPNName = sharedPreferences.getString("VPNName", null);
            String VPNPass = sharedPreferences.getString("VPNPass", null);
            sharedPreferences = getSharedPreferences("Net", Context.MODE_PRIVATE);
            String NetName = sharedPreferences.getString("NetName", null);
            String NetPass = sharedPreferences.getString("NetPass", null);

            FunctionsPublicBasic function = new FunctionsPublicBasic();
            netFunction = function.reopenNetwork(VPNName, VPNPass, NetName, NetPass);
            messageHandlerNetFunctions.sendMessage(messageHandlerSetCheck.obtainMessage());
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progress.cancel();
            if (messagesIsGot) {
                AboutPageMessageItem aboutPageMessageItemAccount = new AboutPageMessageItem(context)
                        .setIcon(getDrawable(R.drawable.netuser))
                        .setMainText(getString(R.string.account_message))
                        .setDescriptionText(messages.get("account"))
                        .setOnItemClickListener(() -> new AlertDialog.Builder(context)
                                .setTitle(getString(R.string.account_message))
                                .setMessage(messages.get("account"))
                                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                })
                                .create().show());
                messageCard.addMessageItem(aboutPageMessageItemAccount);

                AboutPageMessageItem aboutPageMessageItemBalance = new AboutPageMessageItem(context)
                        .setIcon(getDrawable(R.drawable.netbalance))
                        .setMainText(getString(R.string.balance))
                        .setDescriptionText(messages.get("balance"))
                        .setOnItemClickListener(() -> new AlertDialog.Builder(context)
                                .setTitle(getString(R.string.balance))
                                .setMessage(messages.get("balance"))
                                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                })
                                .create().show());
                messageCard.addMessageItem(aboutPageMessageItemBalance);

                AboutPageMessageItem aboutPageMessageItemUsedTime = new AboutPageMessageItem(context)
                        .setIcon(getDrawable(R.drawable.nettime))
                        .setMainText(getString(R.string.used_time))
                        .setDescriptionText(messages.get("usedtime"))
                        .setOnItemClickListener(() -> new AlertDialog.Builder(context)
                                .setTitle(getString(R.string.used_time))
                                .setMessage(messages.get("usedtime"))
                                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                })
                                .create().show());
                messageCard.addMessageItem(aboutPageMessageItemUsedTime);

                String leftBand = "";
                try {
                    if (Objects.requireNonNull(messages.get("set")).contains("300G")) {
                        if (Objects.requireNonNull(messages.get("usedband")).contains("GB"))
                            leftBand = "\n剩余流量：" + (300 - Double.parseDouble(Objects.requireNonNull(messages.get("usedband")).replace(" GB", ""))) + " GB";
                        else
                            leftBand = "\n剩余流量：" + (300 - (Double.parseDouble(Objects.requireNonNull(messages.get("usedband")).replace(" MB", ""))) / 1024.0) + " GB";

                    }
                    if (Objects.requireNonNull(messages.get("set")).contains("200G")) {
                        if (Objects.requireNonNull(messages.get("usedband")).contains("GB"))
                            leftBand = "\n剩余流量：" + (200 - Double.parseDouble(Objects.requireNonNull(messages.get("usedband")).replace(" GB", ""))) + " GB";
                        else
                            leftBand = "\n剩余流量：" + (200 - (Double.parseDouble(Objects.requireNonNull(messages.get("usedband")).replace(" MB", ""))) / 1024.0) + " GB";

                    }
                    if (Objects.requireNonNull(messages.get("set")).contains("100G")) {
                        if (Objects.requireNonNull(messages.get("usedband")).contains("GB"))
                            leftBand = "\n剩余流量：" + (100 - Double.parseDouble(Objects.requireNonNull(messages.get("usedband")).replace(" GB", ""))) + " GB";
                        else
                            leftBand = "\n剩余流量：" + (100 - (Double.parseDouble(Objects.requireNonNull(messages.get("usedband")).replace(" MB", ""))) / 1024.0) + " GB";
                    }
                } catch (Exception e) {
                    System.out.println("Left Error");
                }


                String finalLeftBand = leftBand;
                AboutPageMessageItem aboutPageMessageItemUsedBand = new AboutPageMessageItem(context)
                        .setIcon(getDrawable(R.drawable.netband))
                        .setMainText(getString(R.string.used_band))
                        .setDescriptionText(messages.get("usedband"))
                        .setOnItemClickListener(() -> new AlertDialog.Builder(context)
                                .setTitle(getString(R.string.used_band))
                                .setMessage("已用流量：" + messages.get("usedband") + finalLeftBand)
                                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                })
                                .create().show());
                messageCard.addMessageItem(aboutPageMessageItemUsedBand);

                AboutPageMessageItem aboutPageMessageItemOverdate = new AboutPageMessageItem(context)
                        .setIcon(getDrawable(R.drawable.netovertime))
                        .setMainText(getString(R.string.overdate))
                        .setDescriptionText(messages.get("overdate"))
                        .setOnItemClickListener(() -> new AlertDialog.Builder(context)
                                .setTitle(getString(R.string.overdate))
                                .setMessage(messages.get("overdate"))
                                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                })
                                .create().show());
                messageCard.addMessageItem(aboutPageMessageItemOverdate);

                AboutPageMessageItem aboutPageMessageItemOffline = new AboutPageMessageItem(context)
                        .setIcon(getDrawable(R.drawable.netoffline))
                        .setMainText(getString(R.string.online))
                        .setDescriptionText(messages.get("online") + getString(R.string.click_to_offline_all))
                        .setOnItemClickListener(() -> {
                            if (Objects.requireNonNull(messages.get("online")).equals("离线")) {
                                new AlertDialog.Builder(context)
                                        .setTitle(getString(R.string.warning))
                                        .setMessage(getString(R.string.no_device))
                                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                        })
                                        .create().show();
                            } else {
                                new AlertDialog.Builder(context)
                                        .setTitle(getString(R.string.warning))
                                        .setMessage(getString(R.string.offline_all_device))
                                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                            Thread makeoffline=new Thread(new logout());
                                            showProgress((e1,e2)-> makeoffline.interrupt());
                                            makeoffline.start();
                                        })
                                        .setNegativeButton(getString(R.string.cancel), null)
                                        .create().show();
                            }
                        });
                messageCard.addMessageItem(aboutPageMessageItemOffline);

                AboutPageMessageItem aboutPageMessageItemSet = new AboutPageMessageItem(context)
                        .setIcon(getDrawable(R.drawable.netset))
                        .setMainText(getString(R.string.network_set))
                        .setDescriptionText(messages.get("set") + getString(R.string.click_to_bookset))
                        .setOnItemClickListener(() -> {
                            Thread checkBookSet= new Thread(new checkSet());
                            showProgress((e1,e2)-> checkBookSet.interrupt());
                            checkBookSet.start();
                        });
                messageCard.addMessageItem(aboutPageMessageItemSet);

                AboutPageMessageItem aboutPageMessageItemStatue = new AboutPageMessageItem(context);
                aboutPageMessageItemStatue.setIcon(getDrawable(R.drawable.netstatue));
                aboutPageMessageItemStatue.setMainText(getString(R.string.network_statue));
                aboutPageMessageItemStatue.setDescriptionText(messages.get("statue") + getString(R.string.click_to_stop));
                aboutPageMessageItemStatue.setOnItemClickListener(() -> {
                    if (Objects.requireNonNull(messages.get("statue")).equals("停机")) {
                        new AlertDialog.Builder(context)
                                .setTitle(getString(R.string.warning))
                                .setMessage(getString(R.string.confirm_to_reopen))
                                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                    Thread reopenNet = new Thread(new reopenNet());
                                    showProgress((e1, e2) -> reopenNet.interrupt());
                                    reopenNet.start();
                                })
                                .setNegativeButton(getString(R.string.cancel), null)
                                .create().show();
                    } else {
                        new AlertDialog.Builder(context)
                                .setTitle(getString(R.string.warning))
                                .setMessage(getString(R.string.confirm_stop_net))
                                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                    Thread stopNet = new Thread(new stopNet());
                                    showProgress((e1, e2) -> stopNet.interrupt());
                                    stopNet.start();
                                })
                                .setNegativeButton(getString(R.string.cancel), null)
                                .create().show();
                    }
                });
                messageCard.addMessageItem(aboutPageMessageItemStatue);


            } else {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.fail_to_get_network_messages))
                        .setPositiveButton(getString(R.string.try_again), (dialog, which) -> {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        })
                        .setNegativeButton(getString(R.string.donot_try_again), null)
                        .create().show();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler messageHandlerNetFunctions = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            progress.dismiss();
            if (netFunction) {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.success))
                        .setMessage(getString(R.string.success))
                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        })
                        .create().show();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.error))
                        .setMessage(getString(R.string.fail))
                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {

                        })
                        .create().show();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler messageHandlerSetCheck = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            progress.dismiss();
            if (setCheckBack.equals("ERROR")) {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.error))
                        .setMessage(getString(R.string.net_error))
                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        })
                        .create().show();
            } else if (setCheckBack.equals("DONTHAVE")) {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.warning))
                        .setMessage(getString(R.string.cannot_check_set))
                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        })
                        .create().show();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.set_your_book))
                        .setMessage(setCheckBack)
                        .setPositiveButton(getString(R.string.correct), (dialog, which) -> {
                            AlertDialog.Builder bookSet = new AlertDialog.Builder(context);
                            LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_set_selector, null);  //从另外的布局关联组件

                            final RadioButton radioButtonTen = linearLayout.findViewById(R.id.radioButtonTenSet);
                            final RadioButton radioButtonTwenty = linearLayout.findViewById(R.id.radioButtonTwentySet);
                            final RadioButton radioButtonThirty = linearLayout.findViewById(R.id.radioButtonThirtySet);

                            bookSet.setTitle(getString(R.string.book_set))
                                    .setView(linearLayout)
                                    .setPositiveButton(getString(R.string.confirm), (dialog1, which1) -> {
                                        if (radioButtonTen.isChecked()) {
                                            Thread setBookSet= new Thread(new bookSet(2));
                                            showProgress((e1,e2)-> setBookSet.interrupt());
                                            setBookSet.start();
                                        } else if (radioButtonTwenty.isChecked()) {
                                            Thread setBookSet= new Thread(new bookSet(4));
                                            showProgress((e1,e2)-> setBookSet.interrupt());
                                            setBookSet.start();
                                        } else if (radioButtonThirty.isChecked()) {
                                            Thread setBookSet= new Thread(new bookSet(3));
                                            showProgress((e1,e2)-> setBookSet.interrupt());
                                            setBookSet.start();
                                        }
                                    })
                                    .create()
                                    .show();

                        })
                        .setNegativeButton(getString(R.string.confirm), (dialog, which) -> {

                        })
                        .create().show();
            }

        }
    };

    @SuppressLint("HandlerLeak")
    private Handler messageHandlerLogout = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            progress.dismiss();
            if (isLogout) {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.success))
                        .setMessage(getString(R.string.succeed_make_offline))
                        .setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        })
                        .create().show();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.error))
                        .setMessage(getString(R.string.fail_make_offline))
                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        })
                        .create().show();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler messageHandlerBookSet = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progress.cancel();
            if (isBookedSet) {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.success))
                        .setMessage(getString(R.string.succeed_change_set))
                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        })
                        .create().show();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.error))
                        .setMessage(getString(R.string.fail_change_set))
                        .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        })
                        .create().show();
            }
        }
    };
}
