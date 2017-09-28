package com.gagz.instafilter.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.gagz.instafilter.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    private val TIME_LIMIT = 1500
    private var backPressed:Long = 0

    companion object {

        val REQUEST_CODE_PHOTO_FILTER = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {

        super.onResume()
        bindViews()
    }

    override fun onPause() {

        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {

            REQUEST_CODE_PHOTO_FILTER -> {

                when (resultCode) {

                    Activity.RESULT_OK -> {
                        toast("Photo saved in Instafilter.")
                    }

                    else -> {
                    }
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun bindViews() {

        val billabong = Typeface.createFromAsset(assets, "font/billabong.ttf")
        logoText.typeface = billabong

        initBackgroundVideo()
        initTapListener()
    }

    private fun initBackgroundVideo() {

        loopVideo.setZOrderOnTop(false)
        loopVideo.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.start()
        }
        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.video}")
        loopVideo.setVideoURI(videoUri)
    }

    private fun initTapListener() {

        backgroundView.setOnClickListener {

            val intent = intentFor<PhotoFilterActivity>()
            startActivityForResult(intent, REQUEST_CODE_PHOTO_FILTER)
        }
    }

    override fun onBackPressed() {

        if (TIME_LIMIT + backPressed > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            toast("Press back again to exit")
        }
        backPressed = System.currentTimeMillis()
    }
}