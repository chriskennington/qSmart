package com.pitmasteriq.qsmart.monitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;


public class TemperatureTextView extends android.support.v7.widget.AppCompatTextView
{
    private SharedPreferences prefs;

    private TemperatureTextView temperatureTextView;

    public TemperatureTextView(Context context)
    {
        super(context);
    }

    public TemperatureTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TemperatureTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    public void setTemperature(int temperature, int relative, int modifier)
    {
        if(prefs == null)
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean isF = (prefs.getString(Preferences.TEMPERATURE_UNITS,"0").equals("0"));

        if (!isF)
        {
            if (temperature == 0)
                setText(String.valueOf(0));
            else
            {
                setText(String.valueOf(f2c(temperature) + f2cr(relative) * modifier));
            }
        }
        else
        {
            setText(String.valueOf(temperature + (relative * modifier)));
        }
    }

    public void setTemperature(int temperature)
    {
        if(prefs == null)
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean isF = (prefs.getString(Preferences.TEMPERATURE_UNITS,"0").equals("0"));

        if (!isF)
        {
            if(temperature == 0)
                setText(String.valueOf(0));
            else
                setText(String.valueOf(f2c(temperature)));
        }
        else
        {
            setText(String.valueOf(temperature));
        }
    }

    private int f2c(int f)
    {
        float temp = (float) ((f-32) * (5.0/9.0));
        return roundUpOrDown(temp);
    }

    private int f2cr(int f)
    {
        float temp = (float) ((5.0/9.0) * f);
        return roundUpOrDown(temp);
    }

    private static int roundUpOrDown(float temp)
    {
        if (temp > 0)
            temp += 0.5;
        else if (temp < 0)
            temp -= 0.5;

        return (int) temp;
    }
}
