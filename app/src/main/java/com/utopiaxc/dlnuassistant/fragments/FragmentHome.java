package com.utopiaxc.dlnuassistant.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.utopiaxc.dlnuassistant.R;
import com.utopiaxc.dlnuassistant.activities.ActivityExamInfo;
import com.utopiaxc.dlnuassistant.activities.ActivityGradeList;
import com.utopiaxc.dlnuassistant.activities.ActivityNetwork;

import java.util.Objects;

import io.github.varenyzc.opensourceaboutpages.AboutPageMessageItem;
import io.github.varenyzc.opensourceaboutpages.MessageCard;

public class FragmentHome extends Fragment {
    private MessageCard messageCard;


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
        messageCard=getActivity().findViewById(R.id.home_card);

        //设置卡片
        setMessageCard();

    }

    //卡片设置函数
    private void setMessageCard(){
        AboutPageMessageItem ItemHome_getGrades=new AboutPageMessageItem(getActivity())
                .setIcon(Objects.requireNonNull(getActivity()).getDrawable(R.drawable.gradelist))
                .setMainText(getString(R.string.grade_list))
                .setOnItemClickListener(() -> {
                    Intent intent=new Intent(getActivity(), ActivityGradeList.class);
                    startActivity(intent);
                });
        messageCard.addMessageItem(ItemHome_getGrades);

        AboutPageMessageItem ItemHome_getExams=new AboutPageMessageItem(getActivity())
                .setIcon(getActivity().getDrawable(R.drawable.exam))
                .setMainText(getString(R.string.exam_message))
                .setOnItemClickListener(() -> {
                    Intent intent=new Intent(getActivity(), ActivityExamInfo.class);
                    startActivity(intent);
                });
        messageCard.addMessageItem(ItemHome_getExams);

        AboutPageMessageItem ItemHome_network=new AboutPageMessageItem(getActivity())
                .setIcon(getActivity().getDrawable(R.drawable.network))
                .setMainText(getString(R.string.label_network))
                .setOnItemClickListener(() -> {
                    Intent intent=new Intent(getActivity(), ActivityNetwork.class);
                    startActivity(intent);
                });
        messageCard.addMessageItem(ItemHome_network);


    }



}
