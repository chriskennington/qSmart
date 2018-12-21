package com.pitmasteriq.qsmart;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.pitmasteriq.qsmart.cheatsheet.SmokingCheatSheet;
import com.pitmasteriq.qsmart.database.DatabaseHelper;
import com.pitmasteriq.qsmart.monitor.LogTag;
import com.pitmasteriq.qsmart.monitor.MonitorActivity;
import com.pitmasteriq.qsmart.monitor.Preferences;
import com.pitmasteriq.qsmart.tempchart.TempChartActivity;

import java.io.File;

public class DirectoryActivity extends Activity
{
    private static final int ALL_PERMISSIONS = 1;
    private static final int LOCATION_REQUEST = 2;
    private static final int STORAGE_REQUEST = 3;

    private SharedPreferences prefs;

    private String[] preferrerdPermissions = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String[] requiredPermissions = { Manifest.permission.ACCESS_COARSE_LOCATION};
    private String[] permissionRationaleTitles = {"Location Services", };
    private String[] permissionRationale = {"Location services are required by Bluetooth to scan for devices. This app requires the use of Bluetooth and its scanning feature. For this app to function properly, location service permissions are required and you location services should be enabled."};
    private int[] permissionRequestCode = {LOCATION_REQUEST};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.prepareDatabase();

        firstRunSetup();
        checkPermissions();
    }


    private void firstRunSetup()
    {
        //setup default preferences
        prefs.edit().putInt(Preferences.SESSION_ID, 1).apply();



        if(prefs.getBoolean(Preferences.DIRECTORY_FIRST_RUN, true))
        {
            prefs.edit().putBoolean(Preferences.DIRECTORY_FIRST_RUN, false).apply();
            {
                //TODO first run setup stuff here.
                askForPermissions();

                //Rename old database from data.db to pmiq_data.db
                File database = getApplicationContext().getDatabasePath("data.db");
                if(database.exists())
                {
                    File newPath = getApplicationContext().getDatabasePath("pmiq_data.db");

                    if(database.renameTo(newPath))
                        Log.i(LogTag.DEBUG, "Old database renamed");
                    else
                        Log.i(LogTag.DEBUG, "Could not rename database");
                }
            }
        }
    }


    private void askForPermissions()
    {
        ActivityCompat.requestPermissions(this, preferrerdPermissions, ALL_PERMISSIONS);
    }

    private void checkPermissions()
    {
        boolean needPermissions = false;
        for(int i=0; i< requiredPermissions.length; i++)
        {
            if(ActivityCompat.checkSelfPermission(this, requiredPermissions[i]) != PackageManager.PERMISSION_GRANTED)
            {
                needPermissions = true;
            }
        }

        if(needPermissions)
        {
            ActivityCompat.requestPermissions(this, requiredPermissions, ALL_PERMISSIONS);
        }
    }



    public void onMonitorClicked(View v)
    {
        startActivity(new Intent(this, MonitorActivity.class));
    }



    public void onTempChartClicked(View v)
    {
        startActivity(new Intent(this, TempChartActivity.class));
    }


    public void onCheatSheetClicked(View v)
    {
        startActivity(new Intent(this, SmokingCheatSheet.class));
    }
}
