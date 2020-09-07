package com.utopiaxc.urpassistant.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dd.processbutton.FlatButton;
import com.dd.processbutton.ProcessButton;
import com.utopiaxc.urpassistant.ActivityMain;
import com.utopiaxc.urpassistant.R;

public class FragmentTimeTable extends Fragment {

    //配置FragmentUI
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        getActivity().setTitle(getString(R.string.title_table));
        return view;
    }

    //Fragment入口
    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FlatButton flatButton_chart = getActivity().findViewById(R.id.button_chart);
        FlatButton flatButton_all = getActivity().findViewById(R.id.button_all);

        flatButton_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences("TimeTable",getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("Layout",1);
                editor.commit();
                SharedPreferences sharedPreferences_toActivity = getActivity().getSharedPreferences("FirstFragment", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor_toActivity = sharedPreferences_toActivity.edit();
                editor_toActivity.putInt("Start", 2);
                editor_toActivity.commit();
                Intent intent=new Intent(getActivity(), ActivityMain.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        flatButton_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences("TimeTable",getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putInt("Layout",2);
                editor.commit();
                SharedPreferences sharedPreferences_toActivity = getActivity().getSharedPreferences("FirstFragment", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor_toActivity = sharedPreferences_toActivity.edit();
                editor_toActivity.putInt("Start", 2);
                editor_toActivity.commit();
                Intent intent=new Intent(getActivity(), ActivityMain.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


    }
}
