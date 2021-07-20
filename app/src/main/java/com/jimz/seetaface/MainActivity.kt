package com.jimz.seetaface

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jimz.seetaface.seetafaceEngine.DetectionEngine
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        thread {
            DetectionEngine().initPath(this)
        }
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_face_detection -> {
                gotoActivity(DetectionActivity::class.java)
            }
            R.id.btn_face_feature_points -> {
                gotoActivity(FeaturePointActivity::class.java)
            }
            R.id.btn_face_match -> {
                gotoActivity(MatchActivity::class.java)
            }
            R.id.btn_face_identify -> {
                gotoActivity(IdentifyFacesActivity::class.java)
            }
        }
    }

    private fun <T> gotoActivity(clz: Class<T>) {
        val intent = Intent(this@MainActivity, clz)
        startActivity(intent)
    }
}