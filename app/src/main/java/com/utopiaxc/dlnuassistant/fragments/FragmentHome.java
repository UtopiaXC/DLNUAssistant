package com.utopiaxc.dlnuassistant.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.utopiaxc.dlnuassistant.R;
import com.utopiaxc.dlnuassistant.activities.ActivityExamInfo;
import com.utopiaxc.dlnuassistant.activities.ActivityGradeList;
import com.utopiaxc.dlnuassistant.activities.ActivityNetwork;
import com.utopiaxc.dlnuassistant.fuctions.FunctionsPublicBasic;

import java.util.Objects;

import io.github.varenyzc.opensourceaboutpages.AboutPageMessageItem;
import io.github.varenyzc.opensourceaboutpages.MessageCard;

public class FragmentHome extends Fragment {
    AlertDialog progress;
    private MessageCard messageCard;
    private String HandlerMessage;
    @SuppressWarnings("deprecation")
    private boolean netAccountIsRight;


    //配置FragmentUI
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    //Fragment入口
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle(getString(R.string.title_home));

        //预绑定
        messageCard = getActivity().findViewById(R.id.home_card);

        //设置卡片
        setMessageCard();

    }

    //卡片设置函数
    private void setMessageCard() {
        AboutPageMessageItem ItemHome_getGrades = new AboutPageMessageItem(getActivity())
                .setIcon(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.gradelist))
                .setMainText(getString(R.string.grade_list))
                .setOnItemClickListener(() -> {
                    Intent intent = new Intent(getActivity(), ActivityGradeList.class);
                    startActivity(intent);
                });
        messageCard.addMessageItem(ItemHome_getGrades);

        AboutPageMessageItem ItemHome_getExams = new AboutPageMessageItem(getActivity())
                .setIcon(getActivity().getDrawable(R.drawable.exam))
                .setMainText(getString(R.string.exam_message))
                .setOnItemClickListener(() -> {
                    Intent intent = new Intent(getActivity(), ActivityExamInfo.class);
                    startActivity(intent);
                });
        messageCard.addMessageItem(ItemHome_getExams);

        AboutPageMessageItem ItemHome_network = new AboutPageMessageItem(getActivity())
                .setIcon(getActivity().getDrawable(R.drawable.network))
                .setMainText(getString(R.string.label_network))
                .setOnItemClickListener(() -> {
                    final AlertDialog.Builder setAccount = new AlertDialog.Builder(getActivity());
                    LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_account, null);  //从另外的布局关联组件
                    final EditText login_name = linearLayout.findViewById(R.id.login_username);
                    final EditText login_password = linearLayout.findViewById(R.id.login_password);
                    SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("Net", Context.MODE_PRIVATE);
                    boolean isSet = sharedPreferences.getBoolean("NetIsSet", false);

                    setAccount.setTitle(getString(R.string.net_user_settings_title));
                    if (!isSet) {
                        setAccount.setView(linearLayout)
                                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                                    String username = login_name.getText().toString();
                                    String password = login_password.getText().toString();
                                    if (!username.equals("") && !password.equals("")) {
                                        Thread netUserChecker = new Thread(new checkNetUser(username, password));
                                        showProgress((e1,e2)-> netUserChecker.interrupt());
                                        netUserChecker.start();
                                    }
                                })
                                .create()
                                .show();
                    } else {
                        Intent intent = new Intent(getActivity(), ActivityNetwork.class);
                        startActivity(intent);
                    }
                });
        messageCard.addMessageItem(ItemHome_network);
    }

    public void showProgress(DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alertdialog_progress, null);  //从另外的布局关联组件
        final TextView textView = linearLayout.findViewById(R.id.progressText);
        textView.setText(getString(R.string.executing));
        builder.setTitle(getString(R.string.please_wait));
        builder.setView(linearLayout);
        builder.setPositiveButton(getString(R.string.interrupt),listener);
        builder.create();
        progress=builder.show();
    }

    class checkNetUser implements Runnable {
        String username;
        String password;

        public checkNetUser(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public void run() {
            SharedPreferences sharedPreferences=Objects.requireNonNull(getActivity()).getSharedPreferences("user", Context.MODE_PRIVATE);
            boolean isSet = sharedPreferences.getBoolean("VPNIsSet", false);
            if (!isSet){
                HandlerMessage=getActivity().getString(R.string.vpn_not_set);
                netAccountIsRight=false;
                messageHandler.sendMessage(messageHandler.obtainMessage());
            }
            String VPNUser=sharedPreferences.getString("VPNName","");
            String VPNPass=sharedPreferences.getString("VPNPass","");
            FunctionsPublicBasic function=new FunctionsPublicBasic();
            boolean isCorrect=function.loginNetwork(VPNUser,VPNPass,username,password);
            if (!isCorrect){
                HandlerMessage=getActivity().getString(R.string.net_user_is_correct);
                netAccountIsRight=false;
                messageHandler.sendMessage(messageHandler.obtainMessage());
            }
            else{
                HandlerMessage=getActivity().getString(R.string.net_user_not_correct);
                netAccountIsRight=true;
                messageHandler.sendMessage(messageHandler.obtainMessage());
                sharedPreferences=Objects.requireNonNull(getActivity()).getSharedPreferences("Net", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("NetName", username);
                editor.putString("NetPass", password);
                editor.putBoolean("NetIsSet", true);

                editor.apply();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progress.dismiss();
            if (!netAccountIsRight){
                new AlertDialog.Builder(getActivity())
                        .setTitle(Objects.requireNonNull(getActivity()).getString(R.string.warning))
                        .setMessage(HandlerMessage)
                        .setPositiveButton(getActivity().getString(R.string.confirm), (dialog, which) -> {
                        })
                        .create().show();
            }
            else{
                new AlertDialog.Builder(getActivity())
                        .setTitle(Objects.requireNonNull(getActivity()).getString(R.string.success))
                        .setMessage(HandlerMessage)
                        .setPositiveButton(getActivity().getString(R.string.confirm), (dialog, which) -> {
                            Intent intent = new Intent(getActivity(), ActivityNetwork.class);
                            startActivity(intent);
                        })
                        .create().show();

            }
            HandlerMessage="";
        }
    };


}
