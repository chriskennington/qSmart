package com.pitmasteriq.qsmart;

import android.app.Application;
import android.content.Context;

import com.pitmasteriq.qsmart.database.DatabaseHelper;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraMailSender;

@AcraCore(buildConfigClass = BuildConfig.class)
@AcraMailSender(mailTo = "qsmart@pitmasteriq.com")
public class MyApplication extends Application
{
    private static boolean isActive;

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        //ACRA.init(this);
    }

    public static boolean isActive() {return isActive;}
    public static void toggleActive(){ isActive = !isActive; }


}
