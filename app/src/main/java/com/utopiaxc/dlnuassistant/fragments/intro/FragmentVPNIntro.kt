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

var isLogin = false
lateinit var buttonCheck: ButtonProgressBar
lateinit var buttonHelp: ButtonProgressBar
lateinit var username: EditText
lateinit var password: EditText
var isChecked = false
lateinit var fatherContext: ActivityIntro

class FragmentVPNIntro(var context: ActivityIntro) : Fragment(), SlidePolicy {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_vpn_intro, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fatherContext = this.activity as ActivityIntro
        buttonCheck = view.findViewById(R.id.intro_vpn_check)
        buttonHelp = view.findViewById(R.id.intro_vpn_help)

        password = view.findViewById(R.id.editTextIntroVPNPassword)
        username = view.findViewById(R.id.editTextIntroVPNUsername)

        buttonCheck.setOnClickListener {
            hideKeyboard(view)
            Thread(checkVPN()).start()
            buttonCheck.startLoader()
        }

        buttonHelp.setOnClickListener{
            hideKeyboard(view)
            AlertDialog.Builder(this.activity)
                    .setTitle("帮助信息")
                    .setMessage("首先，请您知悉，默认帐号为学号，密码为身份证后八位。\n\n" +
                            "第二，VPN帐号为网页VPN，即大连民族大学校外访问系统，地址为：http://210.30.0.110/。" +
                            "请您先确认您是否可以在浏览器正确登录\n\n" +
                            "第三，如果您无法在其他浏览器登录该网站，那么很抱歉，您将无法使用本软件。如果需要相关帮助请联系网络处")
                    .setPositiveButton("确认",null)
                    .setNegativeButton("浏览器访问"){_,i->
                        run {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.addCategory(Intent.CATEGORY_BROWSABLE)
                            intent.setData(Uri.parse("http://210.30.0.110/"))
                            startActivity(intent)
                        }
                    }
                    .create()
                    .show()
        }
    }

    class checkVPN : Runnable {
        override fun run() {
            isChecked = false
            if (username.text.toString()==""||password.text.toString()==""){
                messageHandler.sendMessage(messageHandler.obtainMessage())
                return
            }

            if (FunctionsPublicBasic().checkVPNCookie(username.text.toString(), password.text.toString())) {
                val sharedPreferences= fatherContext.getSharedPreferences("user", Context.MODE_PRIVATE)
                val editor=sharedPreferences.edit()
                editor.putString("VPNName", username.text.toString())
                editor.putString("VPNPass", password.text.toString())
                editor.putBoolean("VPNIsSet", true)
                editor.apply()
                isChecked = true
                isLogin=true
            }
            messageHandler.sendMessage(messageHandler.obtainMessage())
        }
    }

    override val isPolicyRespected: Boolean
        get() = isLogin

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
                                        isLogin = true
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
        fun newInstance(context: ActivityIntro): FragmentVPNIntro {
            return FragmentVPNIntro(context)
        }
    }


    fun hideKeyboard(view: View) {
        val imm = view.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


}

@SuppressLint("HandlerLeak")
private var messageHandler: Handler = object : Handler(Looper.getMainLooper()) {

    override fun handleMessage(msg: Message?) {
        if (isChecked) {
            buttonCheck.stopLoader()
            fatherContext.goNext()
        } else {
            buttonCheck.reset()
            AlertDialog.Builder(fatherContext)
                    .setTitle("失败！")
                    .setMessage("您的账户信息不正确，请重试，或者参考帮助")
                    .setPositiveButton("确认", null)
                    .create()
                    .show()
        }
    }
}