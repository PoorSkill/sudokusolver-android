package com.poorskill.sudokusolver.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.poorskill.sudokusolver.ui.settings.PlayerPreferences
import com.poorskill.sudokusolver.ui.settings.Theme

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerPreferences.updateLanguage(this)
        when (PlayerPreferences.getApplicationThemePreferences(this)) {
            Theme.Light -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Theme.Dark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Theme.System -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    override fun onRestart() {
        recreate()
        super.onRestart()
    }

}
