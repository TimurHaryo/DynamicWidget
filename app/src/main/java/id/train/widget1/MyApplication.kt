package id.train.widget1

import android.app.Application
import id.train.widget1.data.PrefManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        prefManager = PrefManager(this)
    }

    companion object {
        @get:Synchronized
        lateinit var prefManager: PrefManager
    }
}