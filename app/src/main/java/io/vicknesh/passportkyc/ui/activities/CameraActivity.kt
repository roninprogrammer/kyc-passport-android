package io.vicknesh.passportkyc.ui.activities


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.vicknesh.passportkyc.R
import io.vicknesh.passportkyc.common.IntentData
import io.vicknesh.passportkyc.ui.fragments.CameraMLKitFragment
import org.jmrtd.lds.icao.MRZInfo

class CameraActivity : AppCompatActivity(), CameraMLKitFragment.CameraMLKitCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CameraMLKitFragment())
            .commit()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onPassportRead(mrzInfo: MRZInfo) {
        val intent = Intent()
        intent.putExtra(IntentData.KEY_MRZ_INFO, mrzInfo)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onError() {
        onBackPressed()
    }

    companion object {

        private val TAG = CameraActivity::class.java.simpleName
    }
}