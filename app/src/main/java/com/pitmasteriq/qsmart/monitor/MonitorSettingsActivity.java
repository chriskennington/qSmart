package com.pitmasteriq.qsmart.monitor;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.pitmasteriq.qsmart.R;


public class MonitorSettingsActivity extends PreferenceActivity
{
    private static SharedPreferences prefs;
    private static DeviceManager deviceManager = DeviceManager.get();

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener()
    {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value)
        {
            String stringValue = value.toString();


            switch(preference.getKey())
            {
                case Preferences.DISPLAY_NAME:
                    if(stringValue.length() == 0)
                    {
                        if(deviceManager.device() != null) {
                            prefs.edit().putString(Preferences.DISPLAY_NAME, prefs.getString(Preferences.DEFAULT_NAME, "IQ130")).apply();
                            deviceManager.device().setDisplayName(deviceManager.device().getName());
                            preference.setSummary(deviceManager.device().getDisplayName());
                        }
                    }
                    else
                    {
                        if(deviceManager.device() != null) {
                            deviceManager.device().setDisplayName(stringValue);
                            preference.setSummary(stringValue);
                        }
                    }

                    break;

                case Preferences.PROBE2_NAME:
                    if(stringValue.length() == 0)
                    {
                        prefs.edit().putString(Preferences.PROBE2_NAME, "Food 1").apply();
                        deviceManager.device().setProbe2Name("Food 1");
                        preference.setSummary(deviceManager.device().getProbe2Name());
                    }
                    else
                    {
                        deviceManager.device().setProbe2Name(stringValue);
                        preference.setSummary(stringValue);
                    }
                    break;

                case Preferences.PROBE3_NAME:
                    if(stringValue.length() == 0)
                    {
                        prefs.edit().putString(Preferences.PROBE3_NAME, "Food 2").apply();
                        deviceManager.device().setProbe3Name("Food 1");
                        preference.setSummary(deviceManager.device().getProbe3Name());
                    }
                    else
                    {
                        deviceManager.device().setProbe3Name(stringValue);
                        preference.setSummary(stringValue);
                    }
                    break;

                case Preferences.PASSCODE:
                    deviceManager.device().setPasscode(stringValue);


                default:
                    preference.setSummary(stringValue);
            }

            return true;
        }
    };


    private static void bindPreferenceSummaryToValue(Preference preference)
    {
        try
        {
            preference.setSummary(prefs.getString(preference.getKey(), ""));

            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        }
        catch(Exception e){e.printStackTrace();}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setupActionBar();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
    }


    @Override
    protected void onStart()
    {
        super.onStart();

    }


    @Override
    protected void onStop()
    {
        super.onStop();
    }



    private void setupActionBar()
    {
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }




    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);



            if(prefs.getBoolean(Preferences.UNIT_CONNECTED, false))
            {
                bindPreferenceSummaryToValue(findPreference(Preferences.DISPLAY_NAME));
                bindPreferenceSummaryToValue(findPreference(Preferences.PROBE2_NAME));
                bindPreferenceSummaryToValue(findPreference(Preferences.PROBE3_NAME));
                bindPreferenceSummaryToValue(findPreference(Preferences.PASSCODE));
                if(deviceManager.device() != null)
                    findPreference(Preferences.PASSCODE).setDefaultValue(deviceManager.device().getPasscodeString());
            }
            else
            {
                findPreference(Preferences.DISPLAY_NAME).setEnabled(false);
                findPreference(Preferences.PROBE2_NAME).setEnabled(false);
                findPreference(Preferences.PROBE3_NAME).setEnabled(false);
                findPreference(Preferences.PASSCODE).setEnabled(false);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), MonitorSettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
