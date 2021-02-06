package com.poorskill.sudokusolver.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.poorskill.sudokusolver.R
import java.util.*

abstract class PlayerPreferences {

    companion object {
        //Reads only locale at start of application
        @SuppressLint("ConstantLocale")
        private val defaultLocale: Locale = Locale.getDefault()

        private fun getPrefsEditor(context: Context): SharedPreferences.Editor {
            return getSharedPreferences(context).edit()
        }

        private fun getSharedPreferences(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        internal fun setApplicationThemePreferences(theme: Theme, context: Context) {
            val themeId: Int = when (theme) {
                Theme.Light -> 1
                Theme.Dark -> 2
                Theme.System -> 0
            }
            getPrefsEditor(context).putInt(
                context.getString(R.string.prefApplicationThemePrefKey),
                themeId
            ).apply()
        }

        internal fun getApplicationThemePreferences(context: Context): Theme {
            return when (getSharedPreferences(context).getString(
                context.getString(R.string.prefApplicationThemePrefKey),
                "0"
            )!!.toInt()) {
                1 -> Theme.Light
                2 -> Theme.Dark
                else ->
                    Theme.System
            }
        }

        internal fun getSudokuSizePreferences(context: Context): Int {
            return getSharedPreferences(context).getString(
                context.getString(R.string.prefSudokuFieldSizePrefKey),
                "9"
            )!!.toInt()
        }

        internal fun updateLanguage(context: Context) {
            var languageCode = getSharedPreferences(context).getString(
                context.getString(R.string.prefApplicationLanguagePrefKey),
                context.getString(R.string.systemKey)
            )
            if (languageCode.equals(context.getString(R.string.systemKey))) {
                languageCode = defaultLocale.language
            }
            setLocale(context, languageCode.toString())
        }

        private fun setLocale(context: Context, languageCode: String) {
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            val resources = context.resources
            val config = resources.configuration
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.displayMetrics)
            println(locale.language)
        }

        fun getSudokuTextSizePreferences(context: Context): Float {
            return when (getSharedPreferences(context).getString(
                context.getString(R.string.prefTextSizePrefKey),
                "75"
            )) {
                "50" -> 1.75f
                "25" -> 2f
                "100" -> .85f
                else -> 1.25f
            }
        }
    }
}