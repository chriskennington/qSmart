package com.pitmasteriq.qsmart.monitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.constraint.Group;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.pitmasteriq.qsmart.MyApplication;
import com.pitmasteriq.qsmart.R;
import com.pitmasteriq.qsmart.database.DatabaseHelper;
import com.pitmasteriq.qsmart.monitor.Device.ConnectionStatus;

import java.util.ArrayList;




public class MonitorActivity extends AppCompatActivity implements ScanningFragment.ScanningFragmentEvent, OnParamEdited
{
    public static final int BT_REQUEST = 1000;

    private DeviceManager deviceManager;

    private BluetoothService service;
    private boolean serviceBound = false;
    private Intent serviceIntent;
    private View progressHolder;



    private Group pitAlarmGroup, probe2AlarmGroup, probe3AlarmGroup, probe2PitSetGroup, probe3PitSetGroup,
                delayGroup;


    private TextView txtProbe1Temp, txtPitSet, txtDelayPitSet, txtAlarmLow, txtAlarmHigh;
    private TextView txtName, txtDelayTime, txtProbe2Name, txtProbe3Name;
    private TextView txtProbe2Temp, txtProbe2Alarm, txtProbe2PitSet, txtProbe2Target;
    private TextView txtProbe3Temp, txtProbe3Alarm, txtProbe3PitSet, txtProbe3Target;
    private ImageView imgErrorIcon, imgStatus;
    private ImageButton btnConfigChange;
    private Toolbar toolbar;
    private LineChart graph;

    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private DatabaseHelper dbHelper;
    private UnitData currentData = null;
    private SharedPreferences prefs;

    private ArrayList<Entry> psEntries, p1Entries, p2Entries, p3Entries;
    private LineDataSet psDataSet, p1DataSet, p2DataSet, p3DataSet;
    private LineData lineData;
    private int graphDataCount = 0;
    private long lastGraphTime = 0;

    private boolean graphInitialized = false;
    private ArrayList<GraphData> graphData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        deviceManager = DeviceManager.get();

        serviceIntent = new Intent(this, BluetoothService.class);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        dbHelper = new DatabaseHelper(this);
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        progressHolder = findViewById(R.id.mon_progress_holder);

        toolbar = findViewById(R.id.mon_toolbar);
        toolbar.setTitle("IQ130 Monitor");
        setSupportActionBar(toolbar);


        graph = findViewById(R.id.mon_chart);
        pitAlarmGroup = findViewById(R.id.mon_grp_pit_alarm);
        probe2AlarmGroup = findViewById(R.id.mon_probe2_grp_alarm);
        probe3AlarmGroup = findViewById(R.id.mon_probe3_grp_alarm);
        probe2PitSetGroup = findViewById(R.id.mon_probe2_grp_pit_set);
        probe3PitSetGroup = findViewById(R.id.mon_probe3_grp_pit_set);
        delayGroup = findViewById(R.id.mon_delay_grp);

        txtName = findViewById(R.id.mon_name);

        txtProbe1Temp = findViewById(R.id.mon_probe1_temp);
        txtPitSet = findViewById(R.id.mon_pit_set);
        txtAlarmHigh = findViewById(R.id.mon_alarm_high_value);
        txtAlarmLow = findViewById(R.id.mon_alarm_low_value);
        txtDelayPitSet = findViewById(R.id.mon_delay_pit_set);

        txtProbe2Name = findViewById(R.id.mon_probe2_name);
        txtProbe2Temp = findViewById(R.id.mon_probe2_temp);
        txtProbe2Alarm = findViewById(R.id.mon_probe2_alarm);
        txtProbe2PitSet = findViewById(R.id.mon_probe2_pit_set);
        txtProbe2Target = findViewById(R.id.mon_probe2_target);

        txtProbe3Name = findViewById(R.id.mon_probe3_name);
        txtProbe3Temp = findViewById(R.id.mon_probe3_temp);
        txtProbe3Alarm = findViewById(R.id.mon_probe3_alarm);
        txtProbe3PitSet = findViewById(R.id.mon_probe3_pit_set);
        txtProbe3Target = findViewById(R.id.mon_probe3_target);

        txtDelayTime = findViewById(R.id.mon_delay_time);
        imgErrorIcon = findViewById(R.id.mon_error_icon);
        imgStatus = findViewById(R.id.mon_status);
        btnConfigChange = findViewById(R.id.mon_btn_config_change);


        txtName.setOnLongClickListener((View v) ->
            {
                disconnect();
                return true;
            }
        );




        //set on click listener for pit set temperature
        txtPitSet.setOnClickListener(onPitSetClick);


        //set on click listener for error icon
        imgErrorIcon.setOnClickListener((View v) -> showExceptionsPopup());


        if(prefs.getBoolean(Preferences.MONITOR_FIRST_RUN, true))
        {
            prefs.edit().putBoolean(Preferences.MONITOR_FIRST_RUN, false).apply();

            runSetup();
        }


        //start update thread
        new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                try
                {
                    while(!isInterrupted())
                    {
                        sleep(2000);

                        runOnUiThread(()->{
                                updateUi();
                                updateGraph();
                            }
                        );
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        MyApplication.toggleActive();

        checkBluetoothStatus();

        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        //update name labels
        if(prefs.getBoolean(Preferences.UNIT_CONNECTED, false))
        {
            if(deviceManager.device() != null)
            {
                txtName.setText(deviceManager.device().getDisplayName());
                txtProbe2Name.setText(deviceManager.device().getProbe2Name());
                txtProbe3Name.setText(deviceManager.device().getProbe3Name());
            }
        }

        if(!graphInitialized)
            initializeGraph();
    }


    @Override
    protected void onStop()
    {
        super.onStop();

        MyApplication.toggleActive();

        if(serviceBound)
            unbindService(serviceConnection);

        if(deviceManager.device() != null)
            dbHelper.saveDevice(deviceManager.device());

        if(!prefs.getBoolean(Preferences.UNIT_CONNECTED, false) &&
                !prefs.getBoolean(Preferences.UNINTENTIONAL_DISCONNECT, false))
        {
            Log.d(LogTag.DEBUG, "Stopping service");
            stopService(serviceIntent);
        }
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.monitor, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.param_pit_set:
                Snackbar.make(findViewById(R.id.mon_snackbar_parent), "pitset", Snackbar.LENGTH_LONG).show();
                break;

            case R.id.menu_mon_settings:
                startActivity(new Intent(this, MonitorSettingsActivity.class));
                break;

            case R.id.menu_mon_bluetooth:

                if(btAdapter.isEnabled())
                    new ScanningFragment().show(getFragmentManager(), "scanner");
                else
                    Snackbar.make(findViewById(R.id.mon_snackbar_parent), "Please enable bluetooth", Snackbar.LENGTH_LONG).show();
                break;

            case R.id.menu_mon_disconnect:
                if(serviceBound)
                    service.disconnect();
                break;

            case R.id.menu_mon_help:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pitmasteriq.com/pages/qsmart-2-0-help"));
                startActivity(browserIntent);
                break;
        }

        return true;
    }


    @Override
    public void onUnitSelected(String address, String name, boolean legacy)
    {
        service.connect(address);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case BT_REQUEST:
                if(resultCode == Activity.RESULT_CANCELED)
                {
                    showBluetoothRequiredAlert();
                }

        }
    }


    private void runSetup()
    {
        //Ask for temp units
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please select a temperature unit.");
        builder.setNegativeButton("Celsius", (DialogInterface dialog, int which) ->
            {
                prefs.edit().putString(Preferences.TEMPERATURE_UNITS, "1").apply();
                dialog.dismiss();
            }
        );

        builder.setPositiveButton("Fahrenheit", (DialogInterface dialog, int which) ->
            {
                prefs.edit().putString(Preferences.TEMPERATURE_UNITS, "0").apply();
                dialog.dismiss();
            }
        );

        builder.show();
    }




    private void showExceptionsPopup()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("IQ130 Alarms");
        builder.setMessage(ExceptionManager.get().getExceptionString());
        builder.setPositiveButton(android.R.string.ok, (DialogInterface dialog, int which) -> dialog.dismiss());

        builder.show();
    }

    public void showParamSelectionPopup(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.param_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                int selector = 0;
                switch(item.getItemId())
                {
                    case R.id.param_pit_set: selector = 1;
                        break;
                    case R.id.param_pit_alarm: selector = 4;
                        break;
                    case R.id.param_delay_pit_set: selector = 11;
                        break;
                    case R.id.param_delay_time: selector = 10;
                        break;
                    case R.id.param_probe2_alarm: selector = 2;
                        break;
                    case R.id.param_probe2_pit_set: selector = 13;
                        break;
                    case R.id.param_probe2_target: selector = 12;
                        break;
                    case R.id.param_probe3_alarm: selector = 3;
                        break;
                    case R.id.param_probe3_pit_set: selector = 15;
                        break;
                    case R.id.param_probe3_target: selector = 14;
                        break;
                }

                if(selector == 10)
                {
                    int[] data = ConfigBuilder.getSelectedConfigValues(selector, currentData);
                    TimeParamEditor frag = TimeParamEditor.newInstance(selector, data[0]);
                    frag.show(getSupportFragmentManager(), "param");
                }
                else
                {
                    int[] data = ConfigBuilder.getSelectedConfigValues(selector, currentData);
                    if(prefs.getString(Preferences.TEMPERATURE_UNITS, "0").equals("0"))
                    {
                        IntegerParamEditor frag = IntegerParamEditor.newInstance(selector, data[0], data[1], data[2]);
                        frag.show(getSupportFragmentManager(), "param");
                    }
                    else
                    {
                        IntegerParamEditor frag = IntegerParamEditor.newInstance(selector, data[0], data[3], data[4]);
                        frag.show(getSupportFragmentManager(), "param");
                    }
                }

                return true;
            }
        });
        popup.show();
    }

    private void showBluetoothRequiredAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bluetooth");
        builder.setMessage("This feature requires bluetooth to function properly.");
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                checkBluetoothStatus();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });

        builder.show();
    }

    private void disconnect()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Disconnect?");
        builder.setMessage("Would you like to disconnect from this IQ130?");

        builder.setPositiveButton("disconnect", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(deviceManager.device() != null)
                    dbHelper.saveDevice(deviceManager.device());
                service.disconnect();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void loadGraphData(boolean firstLoad)
    {
        int session = prefs.getInt(Preferences.SESSION_ID, 1);

        if(firstLoad)
            graphData = dbHelper.loadData(session);
        else
            graphData = dbHelper.loadData(session, lastGraphTime);

        for(GraphData data : graphData)
        {
            if(data.getDate() > lastGraphTime)
                lastGraphTime = data.getDate();

            if(prefs.getString(Preferences.TEMPERATURE_UNITS, "0").equals("0"))
            {
                //F
                psEntries.add(new Entry(graphDataCount, data.getPitSet()));
                p1Entries.add(new Entry(graphDataCount, (data.getProbe1() == 999)? 0 : data.getProbe1()));
                p2Entries.add(new Entry(graphDataCount, (data.getProbe2() == 999)? 0 : data.getProbe2()));
                p3Entries.add(new Entry(graphDataCount, (data.getProbe3() == 999)? 0 : data.getProbe3()));
            }
            else
            {
                //C
                psEntries.add(new Entry(graphDataCount, Temperature.f2c(data.getPitSet())));
                p1Entries.add(new Entry(graphDataCount, (data.getProbe1() == 999)? 0 : Temperature.f2c(data.getProbe1())));
                p2Entries.add(new Entry(graphDataCount, (data.getProbe2() == 999)? 0 : Temperature.f2c(data.getProbe2())));
                p3Entries.add(new Entry(graphDataCount, (data.getProbe3() == 999)? 0 : Temperature.f2c(data.getProbe3())));
            }
            graphDataCount++;
        }

        updateGraph();
    }




    private void updateUi()
    {

        if(deviceManager.device() != null)
            currentData = deviceManager.device().data();

        if(currentData == null)
        {
            return;
        }

        if (deviceManager.device().connectionStatus() != ConnectionStatus.CONNECTED)
        {
            currentData.zeroOut();
        }


        String name = deviceManager.device().getDisplayName();
        String probe2Name = deviceManager.device().getProbe2Name();
        String probe3Name = deviceManager.device().getProbe3Name();

        switch(deviceManager.device().connectionStatus())
        {
            case CONNECTED:
                imgStatus.setImageResource(R.drawable.cs_connected);
                break;

            case LOST_CONNECTION:
                imgStatus.setImageResource(R.drawable.cs_lost_connection);
                break;

            default:
                imgStatus.setImageResource(R.drawable.cs_disconnected);
                break;
        }

        //CONNECTION ICON AND CONFIG CHANGE BUTTON
        if(prefs.getBoolean(Preferences.UNIT_CONNECTED, false))
        {
            btnConfigChange.setVisibility(View.VISIBLE);

            txtName.setText(name);
            txtProbe2Name.setText(probe2Name);
            txtProbe3Name.setText(probe3Name);
        }
        else
        {
            btnConfigChange.setVisibility(View.INVISIBLE);

            txtName.setText("Device Name");
            txtProbe2Name.setText(getString(R.string.probe2default));
            txtProbe3Name.setText(getString(R.string.probe3default));
        }

        //PROBE 1 VALUES
        if(currentData.getProbe1Temp() != 999 && currentData.getProbe1Temp() != 0)
            txtProbe1Temp.setText(String.valueOf(currentData.getProbe1Temp()));
        else
            txtProbe1Temp.setText(getString(R.string.no_value));


        //PROBE 2 VALUES
        if(currentData.getProbe2Temp() != 999 && currentData.getProbe2Temp() != 0)
            txtProbe2Temp.setText(String.valueOf(currentData.getProbe2Temp()));
        else
            txtProbe2Temp.setText(getString(R.string.no_value));

        if(currentData.getProbe2Alarm() > 0 )
        {
            txtProbe2Alarm.setText(String.valueOf(currentData.getProbe2Alarm()));
            probe2AlarmGroup.setVisibility(View.VISIBLE);
        }
        else
        {
            probe2AlarmGroup.setVisibility(View.INVISIBLE);
        }

        if(currentData.getProbe2PitSet() > 0 || currentData.getProbe2TargetTemp() > 0)
        {
            txtProbe2PitSet.setText(String.valueOf(currentData.getProbe2PitSet()));
            txtProbe2Target.setText(String.valueOf(currentData.getProbe2TargetTemp()));
            probe2PitSetGroup.setVisibility(View.VISIBLE);
        }
        else
        {
            probe2PitSetGroup.setVisibility(View.INVISIBLE);
        }


        //PROBE 3 VALUES
        if(currentData.getProbe3Temp() != 999 && currentData.getProbe3Temp() != 0)
            txtProbe3Temp.setText(String.valueOf(currentData.getProbe3Temp()));
        else
            txtProbe3Temp.setText(getString(R.string.no_value));



        if(currentData.getProbe3Alarm() > 0)
        {
            txtProbe3Alarm.setText(String.valueOf(currentData.getProbe3Alarm()));
            probe3AlarmGroup.setVisibility(View.VISIBLE);
        }
        else
        {
            probe3AlarmGroup.setVisibility(View.INVISIBLE);
        }

        if(currentData.getProbe3PitSet() > 0 || currentData.getProbe3TargetTemp() > 0)
        {
            txtProbe3PitSet.setText(String.valueOf(currentData.getProbe3PitSet()));
            txtProbe3Target.setText(String.valueOf(currentData.getProbe3TargetTemp()));
            probe3PitSetGroup.setVisibility(View.VISIBLE);
        }
        else
        {
            probe3PitSetGroup.setVisibility(View.INVISIBLE);
        }

        //Pit Set
        if(currentData.getPitSet() != 999 && currentData.getPitSet() != 0)
            txtPitSet.setText(String.valueOf(currentData.getPitSet()));
        else
            txtPitSet.setText(getString(R.string.no_value));


        // Show Pit Alarm Group
        if(currentData.getPitAlarm() > 0)
        {
            int dev = currentData.getPitAlarm();
            int pitSet = currentData.getPitSet();

            txtAlarmLow.setText(String.valueOf(pitSet - dev));
            txtAlarmHigh.setText(String.valueOf(pitSet + dev));
            pitAlarmGroup.setVisibility(View.VISIBLE);
        }
        else
        {
            pitAlarmGroup.setVisibility(View.INVISIBLE);
        }

        //DELAY TIME/SET
        if(currentData.getDelayTime() > 0 || currentData.getDelayPitSet() > 0)
        {
            if(currentData.getDelayTime() != 0)
            {
                int minutes = (currentData.getDelayTime() * 15) - currentData.getMinutesPast();
                int hours = (int) Math.floor(minutes / 60);
                minutes = minutes % 60;
                txtDelayTime.setText(String.format("%02d:%02d", hours, minutes));
            }
            else
            {
                txtDelayTime.setText(String.format("%02d:%02d", 0, 0));
            }

            txtDelayPitSet.setText(currentData.getDelayPitSet());
            delayGroup.setVisibility(View.VISIBLE);
        }
        else
        {
            delayGroup.setVisibility(View.INVISIBLE);
        }


        //Exceptions Icon
        if(ExceptionManager.getExceptions(currentData.getFlagValue()).size() > 0)
        {
            imgErrorIcon.setVisibility(View.VISIBLE);
        }
        else
        {
            imgErrorIcon.setVisibility(View.INVISIBLE);
        }

        //if a connection is still active, set ui to be updated again
        if(deviceManager.device().connectionStatus() == ConnectionStatus.CONNECTED)
        {
            loadGraphData(false);
        }
    }



    private void showGraphOptionsPopup(View v)
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_graph_options, null);

        final CheckBox graphOptionPitSet, graphOptionProbe1, graphOptionProbe2, graphOptionProbe3;
        TextView graphOptionExport;


        graphOptionPitSet = view.findViewById(R.id.pref_graph_pit_set);
        graphOptionProbe1 = view.findViewById(R.id.pref_graph_probe1);
        graphOptionProbe2 = view.findViewById(R.id.pref_graph_probe2);
        graphOptionProbe3 = view.findViewById(R.id.pref_graph_probe3);
        graphOptionExport= view.findViewById(R.id.pref_graph_export);



        graphOptionPitSet.setChecked(prefs.getBoolean(Preferences.GRAPH_PIT_SET, true));
        graphOptionProbe1.setChecked(prefs.getBoolean(Preferences.GRAPH_PROBE1, true));
        graphOptionProbe2.setChecked(prefs.getBoolean(Preferences.GRAPH_PROBE2, false));
        graphOptionProbe3.setChecked(prefs.getBoolean(Preferences.GRAPH_PROBE3, false));

        graphOptionExport.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MonitorActivity.this, ExportActivity.class));
            }
        });


        PopupWindow popup = new PopupWindow(this);
        popup.setContentView(view);
        popup.setOutsideTouchable(true);

        popup.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                prefs.edit().putBoolean(Preferences.GRAPH_PIT_SET, graphOptionPitSet.isChecked())
                        .putBoolean(Preferences.GRAPH_PROBE1, graphOptionProbe1.isChecked())
                        .putBoolean(Preferences.GRAPH_PROBE2, graphOptionProbe2.isChecked())
                        .putBoolean(Preferences.GRAPH_PROBE3, graphOptionProbe3.isChecked()).apply();

                updateGraph();
            }
        });


        popup.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);
    }



    private void initializeGraph()
    {
        graph.getAxisRight().setEnabled(false);
        graph.getDescription().setEnabled(false);
        graph.setMaxVisibleValueCount(25);
        graph.setScaleEnabled(false);
        graph.getXAxis().setDrawLabels(false);

        graph.getAxisLeft().setAxisMaximum(400);
        graph.getAxisLeft().setAxisMinimum(0);

        graph.setOnLongClickListener(new View.OnLongClickListener()
         {
             @Override
             public boolean onLongClick(View v)
             {
                 showGraphOptionsPopup(v);
                 return true;
             }
         });

        psEntries = new ArrayList<>();
        p1Entries = new ArrayList<>();
        p2Entries = new ArrayList<>();
        p3Entries = new ArrayList<>();

        //Pit Set data set
        psDataSet = new LineDataSet(psEntries, "Pit Set");
        psDataSet.setColor(Color.BLUE);
        psDataSet.setDrawCircles(false);
        psDataSet.setDrawValues(false);


        //probe 1 data set
        p1DataSet = new LineDataSet(p1Entries, "Pit Temp");
        p1DataSet.setColor(Color.MAGENTA);
        p1DataSet.setDrawCircles(false);
        p1DataSet.setDrawValues(false);

        //probe 2 data set
        p2DataSet = new LineDataSet(p2Entries, "Food 1");
        p2DataSet.setColor(Color.RED);
        p2DataSet.setDrawCircles(false);
        p2DataSet.setDrawValues(false);

        //probe 1 data set
        p3DataSet = new LineDataSet(p3Entries, "Food 2");
        p3DataSet.setColor(Color.GREEN);
        p3DataSet.setDrawCircles(false);
        p3DataSet.setDrawValues(false);

        lineData = new LineData();
        graphInitialized = true;
    }



    private void updateGraph ()
    {
        lineData = new LineData();

        psDataSet.notifyDataSetChanged();
        p1DataSet.notifyDataSetChanged();
        p2DataSet.notifyDataSetChanged();
        p3DataSet.notifyDataSetChanged();

        if(prefs.getBoolean(Preferences.GRAPH_PIT_SET, true))
            lineData.addDataSet(psDataSet);
        else
            lineData.removeDataSet(psDataSet);

        if(prefs.getBoolean(Preferences.GRAPH_PROBE1, true))
            lineData.addDataSet(p1DataSet);
        else
            lineData.removeDataSet(p1DataSet);

        if(prefs.getBoolean(Preferences.GRAPH_PROBE2, false))
            lineData.addDataSet(p2DataSet);
        else
            lineData.removeDataSet(p2DataSet);

        if(prefs.getBoolean(Preferences.GRAPH_PROBE3, false))
            lineData.addDataSet(p3DataSet);
        else
            lineData.removeDataSet(p3DataSet);


        graph.setData(lineData);
        graph.invalidate();
    }


    private Runnable buildPasscodeRequest = new Runnable()
    {
        @Override
        public void run()
        {
            final EditText input = new EditText(MonitorActivity.this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);

            AlertDialog.Builder builder = new AlertDialog.Builder(MonitorActivity.this);
            builder.setTitle("Passcode");
            builder.setMessage("Enter a 4 digit number.");
            builder.setView(input);

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if(input.getText().toString().length() != 4)
                        input.setError("The passcode must be 4 digits");

                    if(input.getText().toString().equals("0000"))
                        input.setError("0000 is not allowed. Choose a different number.");

                    deviceManager.device().setPasscode(input.getText().toString());
                    prefs.edit().putString(Preferences.PASSCODE, input.getText().toString()).apply();
                    service.passcodeEntered();
                }
            });

            builder.show();
        }
    };


    private Runnable notifyPasscodeIsWrong = new Runnable()
    {
        @Override
        public void run()
        {
            if(progressHolder.getVisibility() == View.VISIBLE)
                progressHolder.setVisibility(View.GONE);


            AlertDialog.Builder builder = new AlertDialog.Builder(MonitorActivity.this);
            builder.setTitle("Passcode Incorrect");
            builder.setMessage("The passcodes did not match. Would you like to change the passcode?");
            builder.setNegativeButton("No", (DialogInterface dialog, int which)-> dialog.dismiss() );
            builder.setPositiveButton("Yes", (DialogInterface dialog, int which)-> buildPasscodeRequest.run() );
            builder.show();

        }
    };



    private BluetoothService.OnServiceEvent
            serviceEvents = new BluetoothService.OnServiceEvent()
    {
        @Override
        public void onUnitConnected(String address)
        {
            Snackbar.make(findViewById(R.id.mon_snackbar_parent), "Unit connected", Snackbar.LENGTH_SHORT).show();


            loadGraphData(true);

            runOnUiThread(()->{
                if(progressHolder.getVisibility() == View.VISIBLE)
                    progressHolder.setVisibility(View.GONE);
            });
        }

        @Override
        public void onUnitDisconnected(String address, boolean intentional)
        {
            Snackbar.make(findViewById(R.id.mon_snackbar_parent), "Unit disconnected", Snackbar.LENGTH_SHORT).show();

            if(intentional)
            {
                currentData.zeroOut();
                graph.clear();
                graphData.clear();
                lastGraphTime = 0;


                //increment session id
                int session = prefs.getInt(Preferences.SESSION_ID, 1);
                prefs.edit().putInt(Preferences.SESSION_ID, (session + 1)).apply();
            }
        }


        @Override
        public void onUnitAlreadyConnected()
        {
            Snackbar.make(findViewById(R.id.mon_snackbar_parent), "Unit already connected", Snackbar.LENGTH_SHORT).show();

            if(progressHolder.getVisibility() == View.VISIBLE)
                progressHolder.setVisibility(View.GONE);
        }

        @Override
        public void onPasscodeWrong()
        {
            runOnUiThread(notifyPasscodeIsWrong);
        }

        @Override
        public void onUnitConnecting()
        {
            progressHolder.setVisibility(View.VISIBLE);
        }

        @Override
        public void onDataUpdate(UnitData data, boolean update)
        {
        }

        @Override
        public void onWriteSuccess()
        {
            Snackbar.make(findViewById(R.id.mon_snackbar_parent), "Parameter changed successfully", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onWriteFailed()
        {
            Snackbar.make(findViewById(R.id.mon_snackbar_parent), "Parameter failed to change", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionFailed()
        {
            Snackbar.make(findViewById(R.id.mon_snackbar_parent), "Connection failed", Snackbar.LENGTH_SHORT).show();
            if(progressHolder.getVisibility() == View.VISIBLE)
                progressHolder.setVisibility(View.GONE);
        }

        @Override
        public void onPasscodeRequest()
        {
            runOnUiThread(buildPasscodeRequest);
        }

        @Override
        public void onBluetoothDisabled()
        {
            Snackbar.make(findViewById(R.id.mon_snackbar_parent), "Bluetooth is disabled. Please enable it and retry the connection.", Snackbar.LENGTH_SHORT).show();
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service1)
        {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service1;
            service = binder.getService();
            serviceBound = true;
            service.setCallbacks(serviceEvents);
            service.stopAlarm();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            serviceBound = false;
        }


    };


    private View.OnClickListener onPitSetClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if(prefs.getBoolean(Preferences.UNIT_CONNECTED, false))
            {
                if(currentData != null) {
                    int[] data = ConfigBuilder.getSelectedConfigValues(1, currentData);
                    IntegerParamEditor frag = IntegerParamEditor.newInstance(1, data[0], data[1], data[2]);
                    frag.show(getSupportFragmentManager(), "param");
                }
            }
        }
    };

    @Override
    public void onEdited(int selector, int newValue)
    {
        service.writeConfigChange(selector, newValue);
    }


    private void checkBluetoothStatus()
    {
        if(!btAdapter.isEnabled())
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BT_REQUEST);
    }
}
