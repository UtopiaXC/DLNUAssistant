package com.utopiaxc.dlnuassistant

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
import com.utopiaxc.dlnuassistant.fragments.intro.*

class ActivityIntro : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make sure you don't call setContentView!
        setTransformer(AppIntroPageTransformerType.Zoom)
        isWizardMode=true
        supportActionBar?.hide()

        // Call addSlide passing your Fragments.
        // You can use AppIntroFragment to use a pre-built fragment
        addSlide(AppIntroFragment.newInstance(
                title = "欢迎使用民大助手",
                description = "©UtopiaXC 2020 All Rights Reserved.",
                backgroundColor = Color.parseColor("#7c4dff"),
                imageDrawable = R.drawable.logo
        ))


        addSlide(FragmentVPNIntro.newInstance(this))

        addSlide(FragmentNetIntro.newInstance(this))

        addSlide(FragmentURPIntro.newInstance(this))

        addSlide(FragmentSyncIntro.newInstance(this))

        addSlide(FragmentWeekIntro.newInstance(this))



        addSlide(AppIntroFragment.newInstance(
                title = "配置完成！",
                description = "即刻开启民大校园生活",
                backgroundColor = Color.parseColor("#7c4dff"),
                imageDrawable = R.drawable.logo
        ))
    }

    fun goNext(){
        goToNextSlide()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"
        val sharedPreferences = getSharedPreferences("Intro", MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.putBoolean("isFirst", false)
        editor.apply()
        val intent = Intent(this, ActivityMain::class.java)
        startActivity(intent)
        finish()
    }

}