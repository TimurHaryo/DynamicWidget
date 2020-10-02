package id.train.widget1.data

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {

    private val sp: SharedPreferences by lazy {
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private val spe: SharedPreferences.Editor by lazy {
        sp.edit()
    }

    fun clear() {
        sp.edit().clear().apply()
    }

    var spCheckConnection: Boolean
        get() = sp.getBoolean(WIDGET_CONNECTION, false)
        set(value) {
            spe.putBoolean(WIDGET_CONNECTION, value)
            spe.apply()
        }

    var spWidgetIdToMeasure: Int
        get() = sp.getInt(WIDGET_TO_MEASURE, 0)
        set(value) {
            spe.putInt(WIDGET_TO_MEASURE, value)
            spe.apply()
        }

    var spInitialSize: Int
        get() = sp.getInt(WIDGET_INITIAL_SIZE, 0)
        set(value) {
            spe.putInt(WIDGET_INITIAL_SIZE, value)
            spe.apply()
        }

    companion object {
        private const val PREF_NAME = "widget pref"
        private const val WIDGET_CONNECTION = "handle connection state"
        private const val WIDGET_TO_MEASURE = "to measure"
        private const val WIDGET_INITIAL_SIZE = "initial size"
    }
}