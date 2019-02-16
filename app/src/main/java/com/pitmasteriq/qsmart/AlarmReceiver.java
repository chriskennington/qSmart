package com.pitmasteriq.qsmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.pitmasteriq.qsmart.monitor.ExceptionManager;
import com.pitmasteriq.qsmart.monitor.MonitorActivity;
import com.pitmasteriq.qsmart.monitor.Preferences;

public class AlarmReceiver extends AppCompatActivity
{
    private PowerManager.WakeLock wl;
    private MediaPlayer mp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_receiver);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ConstraintLayout container = findViewById(R.id.alarm_container);

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "alarmReceiver");
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        container.setOnClickListener((View v)->{
            Intent i = new Intent(getApplicationContext(), MonitorActivity.class);
            startActivity(i);
        });

        if(prefs.getBoolean(Preferences.SOUND, true))
        {
            if (mp == null)
            {
                Uri sound = Uri.parse(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());
                mp = MediaPlayer.create(this, sound);
            }

            mp.start();
        }

        TextView errors = findViewById(R.id.alarm_errors);
        errors.setText(ExceptionManager.get().getExceptionString());
    }


    @Override
    protected void onStop()
    {
        super.onStop();

        try {
            if(wl.isHeld())
                wl.release();
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        }


        try {
            if (mp.isPlaying())
                mp.stop();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        }
    }
}
