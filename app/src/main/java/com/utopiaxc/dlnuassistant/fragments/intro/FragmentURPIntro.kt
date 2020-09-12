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

var isURPLogin = false
lateinit var buttonURPCheck: ButtonProgressBar
lateinit var buttonURPHelp: ButtonProgressBar
lateinit var usernameURP: EditText
lateinit var passwordURP: EditText
var isURPChecked = false
lateinit var fatherURPContext: ActivityIntro

class FragmentURPIntro(var context: ActivityIntro) : Fragment(), SlidePolicy {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_urp_intro, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fatherURPContext = this.activity as ActivityIntro
        buttonURPCheck = view.findViewById(R.id.intro_urp_check)
        buttonURPHelp = view.findViewById(R.id.intro_urp_help)
        usernameURP = view.findViewById(R.id.editTextIntroURPUsername)
        passwordURP = view.findViewById(R.id.editTextIntroURPPassword)

        buttonURPCheck.setOnClickListener {
            hideKeyboard(view)
            Thread(checkURP()).start()
            buttonURPCheck.startLoader();
        }

        buttonURPHelp.setOnClickListener{
            hideKeyboard(view)
            AlertDialog.Builder(this.activity)
                    .setTitle("帮助信息")
                    .setMessage("首先，请您知悉，本账号并非统一通行证帐号，而是URP教务系统帐号。默认帐号为学号，密码为身份证后六位。\n\n" +
                            "第二，如果您不记得或者从未设置过本密码，或者并不清楚使用的是什么账户，请按照如下步骤操作：\n" +
                            "①连接民大校园网或VPN或使用网页VPN。\n" +
                            "②访问教务网站，网页VPN请直接选取，校园网或客户端VPN请访问https://zhjw.dlnu.edu.cn。\n" +
                            "③登录统一通行证并进入综合教务系统。\n" +
                            "④在上方标签选中个人管理，在左侧学籍管理选框中选择个人信息。\n" +
                            "⑤在右侧登录框中选择更改密码。\n" +
                            "⑥输入原密码并设置新密码。\n" +
                            "⑦附注：如果你忘记了原密码，请在弹出的修改密码窗口右键选择查看网页源代码，使用ctrl+f进行搜索，找到oldPass，该变量为你的旧密码。\n\n" +
                            "第三，如果你通过上述方法无法获取或更改密码，那么很抱歉，我也无能为力。您可以选择等待以后的更新，或者选择跳过该步骤进入软件并使用与教务系统无关的功能，谢谢。")
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

    class checkURP : Runnable {
        override fun run() {
            isURPChecked = false
            if (usernameURP.text.toString()==""||passwordURP.text.toString()==""){
                messageURPHandler.sendMessage(messageURPHandler.obtainMessage())
                return
            }
            var sharedPreferences= fatherNetContext.getSharedPreferences("user", Context.MODE_PRIVATE)
            val isSet=sharedPreferences.getBoolean("VPNIsSet", false)
            if (!isSet){
                messageURPHandler.sendMessage(messageURPHandler.obtainMessage())
                return
            }
            val VPNName=sharedPreferences.getString("VPNName", "")
            val VPNPass=sharedPreferences.getString("VPNPass", "")
            if (FunctionsPublicBasic().testURP(VPNName, VPNPass, usernameURP.text.toString(), passwordURP.text.toString())) {
                sharedPreferences= fatherURPContext.getSharedPreferences("user", Context.MODE_PRIVATE)
                val editor=sharedPreferences.edit()
                editor.putString("username", usernameURP.text.toString())
                editor.putString("password",  passwordURP.text.toString())
                editor.putBoolean("URPIsSet", true)
                editor.apply()
                isURPChecked = true
                isURPLogin=true
            }
            messageURPHandler.sendMessage(messageURPHandler.obtainMessage())
        }
    }

    override val isPolicyRespected: Boolean
        get() = isURPLogin

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
                                        isURPLogin = true
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
        fun newInstance(context: ActivityIntro): FragmentURPIntro {
            return FragmentURPIntro(context)
        }
    }


    fun hideKeyboard(view: View) {
        val imm = view.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

@SuppressLint("HandlerLeak")
private var messageURPHandler: Handler = object : Handler(Looper.getMainLooper()) {

    override fun handleMessage(msg: Message?) {
        if (isURPChecked) {
            buttonURPCheck.stopLoader()
            fatherURPContext.goNext()
        } else {
            buttonURPCheck.reset()
            AlertDialog.Builder(fatherURPContext)
                    .setTitle("失败！")
                    .setMessage("您的账户信息不正确，请重试，或者参考帮助")
                    .setPositiveButton("确认", null)
                    .create()
                    .show()
        }
    }
}