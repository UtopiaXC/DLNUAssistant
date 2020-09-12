package com.utopiaxc.dlnuassistant.fragments.intro

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.appintro.SlidePolicy
import com.utopiaxc.dlnuassistant.ActivityIntro
import com.utopiaxc.dlnuassistant.R
import com.utopiaxc.dlnuassistant.fragments.FragmentCenter
import com.utopiaxc.dlnuassistant.fuctions.FunctionsPublicBasic
import github.ishaan.buttonprogressbar.ButtonProgressBar
import java.util.*

var syncResult = false
var ClassIsGot=false
var ExamIsGot=false
var GradeIsGot=false
lateinit var buttonSync: ButtonProgressBar

class FragmentSyncIntro(var context: ActivityIntro) : Fragment(), SlidePolicy {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sync_intro, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonSync=view.findViewById(R.id.intro_sync)
        fatherContext=context
        buttonSync.setOnClickListener{
            buttonSync.startLoader()
            Thread(getMessages()).start()
        }
    }

    class getMessages : Runnable {
        override fun run() {
            val sharedPreferences = fatherContext.getSharedPreferences("user", Context.MODE_PRIVATE);
            val VPNName= sharedPreferences.getString("VPNName", "")
            val VPNPass= sharedPreferences.getString("VPNPass", "")
            val username= sharedPreferences.getString("username", "")
            val password= sharedPreferences.getString("password", "")
            for (i in 1..3) {
                ClassIsGot = FunctionsPublicBasic().setClassTableSQL(fatherContext, VPNName, VPNPass, username, password)
                ExamIsGot = FunctionsPublicBasic().setExamInfo(fatherContext, VPNName, VPNPass, username, password)
                GradeIsGot = FunctionsPublicBasic().setGrades(fatherContext, VPNName, VPNPass, username, password)
                if (ClassIsGot&&ExamIsGot&&GradeIsGot)
                    break
            }
            messageHandler_getAll.sendMessage(messageHandler_getAll.obtainMessage())
        }

    }


    override val isPolicyRespected: Boolean
        get() = syncResult

    override fun onUserIllegallyRequestedNextPage() {
        AlertDialog.Builder(this.activity)
                .setTitle("请稍候")
                .setMessage("正在为您同步信息")
                .setPositiveButton("确认", null)
                .create()
                .show()
    }

    companion object {
        fun newInstance(context: ActivityIntro): FragmentSyncIntro {
            return FragmentSyncIntro(context)
        }
    }
}

@SuppressLint("HandlerLeak")
private val messageHandler_getAll: Handler = object : Handler() {
    override fun handleMessage(msg: Message) {
        syncResult=true
        buttonSync.stopLoader()
        fatherContext.goNext()
    }
}