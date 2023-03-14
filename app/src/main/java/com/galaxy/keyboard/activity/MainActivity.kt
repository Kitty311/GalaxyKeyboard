package com.galaxy.keyboard.activity

import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.galaxy.keyboard.R
import com.galaxy.keyboard.databinding.ActivityMainBinding
import com.galaxy.keyboard.helper.GalaxyAppHelper
import com.galaxy.keyboard.helper.GalaxyAssetHelper
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

//        getStartAndEndDayByToday(Calendar.getInstance())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("This app developed by", GalaxyAppHelper.GetAppDeveloper())

//        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        val galaxyAssetHelper = GalaxyAssetHelper(this)
        galaxyAssetHelper.copyDatabase()

        if (!checkPermission())
            requestPermission()

    }

    private fun checkPermission(): Boolean {
//        val result1 = ContextCompat.checkSelfPermission(this, permission.RECEIVE_SMS)
//        val result2 = ContextCompat.checkSelfPermission(this, permission.READ_SMS)
//        val result3 = ContextCompat.checkSelfPermission(this, permission.SEND_SMS)
//        val result4 =
//            ContextCompat.checkSelfPermission(this, permission.READ_PHONE_STATE)
//        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED
        return true;
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf<String>(
                permission.RECEIVE_SMS,
                permission.READ_SMS,
                permission.SEND_SMS,
                permission.READ_PHONE_STATE
            ),
            311
        )
    }

    fun getStartAndEndDayByToday(today: Calendar): ArrayList<String> {
        val startAndEndDayArray = ArrayList<String>()
        // Get the day of this week today
        val todayWeekDay = today.get(Calendar.DAY_OF_WEEK)
        // Below you can get the start day of week; it would be changeable according to local time zone
        today.add(Calendar.DAY_OF_MONTH, 1 - todayWeekDay)
        val calStartDayOfWeek = today.time
        // Below you can get the end day of week; it would be changeable according to local time zone
        today.add(Calendar.DAY_OF_MONTH, 6)
        val calEndDayOfWeek = today.time
        // Here you can set the date format by using SimpleDateFormat
        val sdfStartDayOfWeek = SimpleDateFormat("yyyy-MM-dd")
        val sdfEndDayOfWeek = SimpleDateFormat("yyyy-MM-dd")
        // Make the array with start date and end date
        startAndEndDayArray.add(sdfStartDayOfWeek.format(calStartDayOfWeek))
        startAndEndDayArray.add(sdfEndDayOfWeek.format(calEndDayOfWeek))
        return startAndEndDayArray
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}