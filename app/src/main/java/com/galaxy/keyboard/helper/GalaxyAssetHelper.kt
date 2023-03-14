@file:JvmName("DemoUtils")
package com.galaxy.keyboard.helper

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import java.io.FileOutputStream

class GalaxyAssetHelper(private val context: Context) {

    companion object {
        private val DB_NAME = "ayemin.db"
    }

    @SuppressLint("WrongConstant")
    fun copyDatabase() {
        val dbFile = context.getDatabasePath(DB_NAME)
        if (dbFile.exists()) {
            return
        }

        val `is` = context.assets.open(DB_NAME)
        val os = FileOutputStream(dbFile)

        val buffer = ByteArray(1024)
        while (`is`.read(buffer) > 0) {
            os.write(buffer)
            Log.d("#DB", "writing>>")
        }

        os.flush()
        os.close()
        `is`.close()
        Log.d("#DB", "completed..")
    }
}