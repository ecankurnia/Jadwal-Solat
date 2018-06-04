package com.mataca.sholatyuk.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mataca.sholatyuk.R
import com.mataca.sholatyuk.db.DatabaseHelper
import com.mataca.sholatyuk.experimental.Android
import com.mataca.sholatyuk.network.api.ShalatClient
import com.mataca.sholatyuk.ui.home.HomeActivity
import kotlinx.coroutines.experimental.launch

class SplashScreenActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName
    private val databaseHelper by lazy {
        DatabaseHelper(
                context = this@SplashScreenActivity,
                name = DatabaseHelper.DATABASE_NAME,
                factory = null,
                version = DatabaseHelper.DATABASE_VERSION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        doLoadData()
    }

    /**
     * @description Load data from API and save it to local database
     */
    private fun doLoadData() {
        launch(Android) {
            val itemCountDataCityLocal = databaseHelper.countDataCity()
            val resultDataKota = ShalatClient.getCityData().await()
            val intentHomeActivity = Intent(this@SplashScreenActivity, HomeActivity::class.java)
            intentHomeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            if (itemCountDataCityLocal == resultDataKota.count) {
                startActivity(intentHomeActivity)
            } else {
                databaseHelper.deleteDataCity()
                databaseHelper.insertDataCity(resultDataKota.data)
                startActivity(intentHomeActivity)
            }
        }
    }
}
