package com.utopiaxc.dlnuassistant.fragments.intro

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.github.appintro.SlidePolicy
import com.utopiaxc.dlnuassistant.ActivityIntro
import com.utopiaxc.dlnuassistant.R
import com.utopiaxc.dlnuassistant.fuctions.FunctionsPublicBasic
import github.ishaan.buttonprogressbar.ButtonProgressBar

var isNetLogin = false
lateinit var buttonNetCheck: ButtonProgressBar
lateinit var buttonNetHelp: ButtonProgressBar
lateinit var usernameNet: EditText
lateinit var passwordNet: EditText
var isNetChecked = false
lateinit var fatherNetContext: ActivityIntro

class FragmentNetIntro(var context: ActivityIntro) : Fragment(), SlidePolicy {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_net_intro, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fatherNetContext = this.activity as ActivityIntro

        buttonNetCheck = view.findViewById(R.id.intro_net_check)
        buttonNetHelp = view.findViewById(R.id.intro_net_help)
        usernameNet = view.findViewById(R.id.editTextIntroNetUsername)
        passwordNet = view.findViewById(R.id.editTextIntroNetPassword)
        usernameNet.focusable

        buttonNetCheck.setOnClickListener {
            hideKeyboard(view)
            Thread(checkNet()).start()
            buttonNetCheck.startLoader();
        }

        buttonNetHelp.setOnClickListener{
            hideKeyboard(view)
            AlertDialog.Builder(this.activity)
                    .setTitle("帮助信息")
                    .setMessage("一、校园网账户默认为学号，密码默认为生日（年月日八位形式）\n\n" +
                            "二、如果您无法验证成功您的校园网账户，你要确认您没有在VPN设置中选择跳过，" +
                            "然后请您能正确通过校园网验证，包括使用手机电脑等设备连接、登录综合信息查询站等\n\n" +
                            "三、如果您已查证VPN已设置成功，且能够正常访问校园网账户，请您联系开发者提供反馈，谢谢")
                    .setPositiveButton("确认", null)
                    .setNegativeButton("信息门户"){ _, i->
                        run {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(Uri.parse("http://210.30.0.110/"))
                            startActivity(intent)
                        }
                    }
                    .create()
                    .show()

        }
    }

    class checkNet : Runnable {
        override fun run() {
            isNetChecked = false
            if (usernameNet.text.toString()==""||passwordNet.text.toString()==""){
                messageNetHandler.sendMessage(messageNetHandler.obtainMessage())
                return
            }

            var sharedPreferences= fatherNetContext.getSharedPreferences("user", Context.MODE_PRIVATE)
            val isSet=sharedPreferences.getBoolean("VPNIsSet",false)
            if (!isSet){
                messageNetHandler.sendMessage(messageNetHandler.obtainMessage())
                return
            }
            val VPNName=sharedPreferences.getString("VPNName", "")
            val VPNPass=sharedPreferences.getString("VPNPass", "")
            for (i in 1..3) {
                if (FunctionsPublicBasic().loginNetwork(VPNName, VPNPass, usernameNet.text.toString(), passwordNet.text.toString())) {
                    sharedPreferences = fatherNetContext.getSharedPreferences("Net", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("NetName", usernameNet.text.toString())
                    editor.putString("NetPass", passwordNet.text.toString())
                    editor.putBoolean("NetIsSet", true)
                    editor.apply()
                    isNetChecked = true
                    isNetLogin = true
                    break
                }
            }
            messageNetHandler.sendMessage(messageNetHandler.obtainMessage())
        }
    }

    override val isPolicyRespected: Boolean
        get() = isNetLogin

    override fun onUserIllegallyRequestedNextPage() {
        AlertDialog.Builder(this.activity).setTitle("抱歉")
                .setMessage("你需要首先通过信息认证")
                .setPositiveButton("确认", null)
                .setNegativeButton("跳过") { _, i ->
                    run {
                        AlertDialog.Builder(this.activity)
                                .setTitle("确认跳过？")
                                .setMessage("如果您跳过本引导，您需要在中心中重新设置账号信息")
                                .setPositiveButton("确认") { _, i ->
                                    run {
                                        isNetLogin = true
                                        context.goNext()
                                    }
                                }
                                .setNegativeButton("取消", null)
                                .create()
                                .show()
                    }
                }
                .create()
                .show()

    }

    companion object {
        fun newInstance(context: ActivityIntro): FragmentNetIntro {
            return FragmentNetIntro(context)
        }
    }


    fun hideKeyboard(view: View) {
        val imm = view.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}

@SuppressLint("HandlerLeak")
private var messageNetHandler: Handler = object : Handler(Looper.getMainLooper()) {

    override fun handleMessage(msg: Message?) {
        if (isNetChecked) {
            buttonNetCheck.stopLoader()
            fatherNetContext.goNext()
        } else {
            buttonNetCheck.reset()
            AlertDialog.Builder(fatherNetContext)
                    .setTitle("失败！")
                    .setMessage("您的账户信息不正确，请重试，或者参考帮助（请确保您已设置完成VPN账户而不是跳过）")
                    .setPositiveButton("确认", null)
                    .create()
                    .show()
        }
    }
}