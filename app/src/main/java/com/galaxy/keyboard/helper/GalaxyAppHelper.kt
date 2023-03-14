package com.galaxy.keyboard.helper

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.galaxy.keyboard.R
import com.galaxy.keyboard.input.GalaxyKeyboardService

class GalaxyAppHelper {
    companion object {
        var LoginState: Boolean = false
        fun GetAppDeveloper(): String {
            return GalaxyConstant.APP_DEVELOPER
        }

        fun GetCurrentKeyboardId(context: Context): String {
            val oldDefaultKeyboard: String =
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.DEFAULT_INPUT_METHOD
                )
            Log.e("OldDefaultKeyboard", oldDefaultKeyboard)
            return oldDefaultKeyboard
        }

        fun IsGalaxyKeyboardDefault(context: Context): Boolean {
            val oldDefaultKeyboard: String =
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.DEFAULT_INPUT_METHOD
                )
            Log.e("OldDefaultKeyboard", oldDefaultKeyboard)
            return oldDefaultKeyboard == context.getString(R.string.galaxy_keyboard_id)
        }

        fun IsGalaxyKeyboardEnabled(context: Context): Boolean {
            val im = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager;
            val enabledList = im.enabledInputMethodList
            for (m in enabledList) {
                if (m.id == context.getString(R.string.galaxy_keyboard_id)) {
                    return true
                }
            }
            return false
        }

        fun SetLoginState(ls: Boolean) {
            this.LoginState = ls
        }

        fun GetLoginState(): Boolean {
            return this.LoginState
        }

        fun SwitchKeyboard(context: Context) {
            (context!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
        }

        fun GetCurrentDataPack(context: Context): Boolean {
            return context.getSharedPreferences(GalaxyConstant.GALAXY_SHARED_PREFERENCE_KEY,
                Context.MODE_PRIVATE).getString(GalaxyConstant.CURRENT_DATA_PACK_PREFERENCE_KEY,
                GalaxyConstant.STANDARD_DATA_PACK_NAME)
                .equals(GalaxyConstant.STANDARD_DATA_PACK_NAME)
        }

        fun SetCurrentDataPack(context: Context, isStandard: Boolean) {
            val sharedPref = context.getSharedPreferences(GalaxyConstant.GALAXY_SHARED_PREFERENCE_KEY,
                Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putString(GalaxyConstant.CURRENT_DATA_PACK_PREFERENCE_KEY,
                    if (isStandard) GalaxyConstant.STANDARD_DATA_PACK_NAME else GalaxyConstant.OFFICE_DATA_PACK_NAME)
                apply()
            }
        }

        fun GetCurrentFontSizeIndex(context: Context): Int {
            return context.getSharedPreferences(GalaxyConstant.GALAXY_SHARED_PREFERENCE_KEY,
                Context.MODE_PRIVATE).getInt(GalaxyConstant.CURRENT_FONT_SIZE_PREFERENCE_KEY, 1)
        }

        fun SetCurrentFontSizeIndex(context: Context, fontSizeIndex: Int) {
            val sharedPref = context.getSharedPreferences(GalaxyConstant.GALAXY_SHARED_PREFERENCE_KEY,
                Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putInt(GalaxyConstant.CURRENT_FONT_SIZE_PREFERENCE_KEY, fontSizeIndex)
                apply()
            }
        }

        fun GetCurrentSkinColorIndex(context: Context): Int {
            return context.getSharedPreferences(GalaxyConstant.GALAXY_SHARED_PREFERENCE_KEY,
                Context.MODE_PRIVATE).getInt(GalaxyConstant.CURRENT_SKIN_COLOR_PREFERENCE_KEY, 5)
        }

        fun SetCurrentSkinColorIndex(context: Context, skinColorIndex: Int) {
            val sharedPref = context.getSharedPreferences(GalaxyConstant.GALAXY_SHARED_PREFERENCE_KEY,
                Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putInt(GalaxyConstant.CURRENT_SKIN_COLOR_PREFERENCE_KEY, skinColorIndex)
                apply()
            }
        }

        fun GetCurrentGlassyStateIndex(context: Context): Int {
            return context.getSharedPreferences(GalaxyConstant.GALAXY_SHARED_PREFERENCE_KEY,
                Context.MODE_PRIVATE).getInt(GalaxyConstant.CURRENT_GLASSY_STATE_PREFERENCE_KEY, 2)
        }

        fun SetCurrentGlassyState(context: Context, glassStateIndex: Int) {
            val sharedPref = context.getSharedPreferences(GalaxyConstant.GALAXY_SHARED_PREFERENCE_KEY,
                Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putInt(GalaxyConstant.CURRENT_GLASSY_STATE_PREFERENCE_KEY, glassStateIndex)
                apply()
            }
        }

        fun GetCurrentAppTextSize(context: Context): Int {
            val curTextIndex = GetCurrentFontSizeIndex(context)
            var curFontSize = 0
            when (curTextIndex) {
                0 -> curFontSize =
                    context.resources.getDimensionPixelOffset(R.dimen.app_text_size_small)
                1 -> curFontSize =
                    context.resources.getDimensionPixelOffset(R.dimen.app_text_size_normal)
                2 -> curFontSize =
                    context.resources.getDimensionPixelOffset(R.dimen.app_text_size_big)
            }
            return curFontSize / context.getResources().getDisplayMetrics().scaledDensity.toInt()
        }

        fun GetCurrentBGColorId(context: Context): Int {
            val curSkinIndex = GetCurrentSkinColorIndex(context)
            val curGlassyIndex = GetCurrentGlassyStateIndex(context)
            var curBGColorId = 0
            when (curSkinIndex) {
                0 -> {
                    val bgIdList = intArrayOf( R.color.colorRed0, R.color.colorRed25, R.color.colorRed50, R.color.colorRed75, R.color.colorRed )
                    curBGColorId = bgIdList[curGlassyIndex]
                }
                1 -> {
                    val bgIdList = intArrayOf( R.color.colorBlue0, R.color.colorBlue25, R.color.colorBlue50, R.color.colorBlue75, R.color.colorBlue )
                    curBGColorId = bgIdList[curGlassyIndex]
                }
                2 -> {
                    val bgIdList = intArrayOf( R.color.colorGreen0, R.color.colorGreen25, R.color.colorGreen50, R.color.colorGreen75, R.color.colorGreen )
                    curBGColorId = bgIdList[curGlassyIndex]
                }
                3 -> {
                    val bgIdList = intArrayOf( R.color.colorOrange0, R.color.colorOrange25, R.color.colorOrange50, R.color.colorOrange75, R.color.colorOrange )
                    curBGColorId = bgIdList[curGlassyIndex]
                }
                4 -> {
                    val bgIdList = intArrayOf( R.color.colorPurple0, R.color.colorPurple25, R.color.colorPurple50, R.color.colorPurple75, R.color.colorPurple )
                    curBGColorId = bgIdList[curGlassyIndex]
                }
                5 -> {
                    val bgIdList = intArrayOf( R.color.colorBlack0, R.color.colorBlack25, R.color.colorBlack50, R.color.colorBlack75, R.color.colorBlack )
                    curBGColorId = bgIdList[curGlassyIndex]
                }
            }
            return curBGColorId
        }

        fun Context.GalaxyToast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        fun Context.GalaxyToast(stringId: Int) =
            Toast.makeText(this, resources.getText(stringId), Toast.LENGTH_SHORT).show()

        fun CheckNetworkState(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {
                try {
                    val activeNetworkInfo = connectivityManager.activeNetworkInfo;
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                        Log.i("update_statut", "Network is available : true");
                        return true;
                    }
                } catch (e: Exception) {
                    Log.i("NetworkStateException", "" + e.message);
                }
            }
            return false
        }
    }
}