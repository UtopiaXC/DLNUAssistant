package com.utopiaxc.dlnuassistant.fragments.intro

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.github.appintro.SlidePolicy
import com.utopiaxc.dlnuassistant.ActivityIntro
import com.utopiaxc.dlnuassistant.R
import com.utopiaxc.dlnuassistant.fuctions.FunctionsPublicBasic
import github.ishaan.buttonprogressbar.ButtonProgressBar

lateinit var fatherWeekContext:ActivityIntro
lateinit var buttonSetWeek: ButtonProgressBar
lateinit var picker:DatePicker
var isFirstSet=false

class FragmentWeekIntro(var context: ActivityIntro) : Fragment(), SlidePolicy {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_firstweek_intro, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonSetWeek=view.findViewById(R.id.intro_week_select)
        picker=view.findViewById(R.id.date_picker_intro)
        fatherWeekContext=context
        buttonSetWeek.setOnClickListener{
            val year: Int = picker.year
            val month: Int = picker.month + 1
            val date: Int = picker.dayOfMonth
            val start = FunctionsPublicBasic.resultMonday("$year-$month-$date")
            val sharedPreferences = activity!!.getSharedPreferences("TimeTable", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("StartWeek", start + " 00:00:00")
            editor.apply()
            isFirstSet=true

            fatherWeekContext.goNext()
        }
    }

    override val isPolicyRespected: Boolean
        get() = isFirstSet

    override fun onUserIllegallyRequestedNextPage() {
        AlertDialog.Builder(this.activity)
                .setTitle("警告")
                .setMessage("请先选择学期初始周数")
                .setPositiveButton("确认", null)
                .create()
                .show()
    }

   companion object {
        fun newInstance(context: ActivityIntro): FragmentWeekIntro {
            return FragmentWeekIntro(context)
        }
    }
}