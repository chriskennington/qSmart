package com.pitmasteriq.qsmart.monitor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.constraint.Group;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.pitmasteriq.qsmart.R;
import com.pitmasteriq.qsmart.database.DatabaseHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ExportActivity extends AppCompatActivity
{
    private static final String DIRECTORY = "qSmart" + File.separator + "exports";


    private DatabaseHelper dbHelper;
    private Map<String, String> nameMap = new HashMap<>();
    private Spinner nameList;
    private long[] dateMinMax;
    private EditText startDate, endDate;
    private Group startGroup, endGroup;
    private ImageButton btnSearch;
    private long lngStartDate, lngEndDate;
    private String address;
    private SharedPreferences prefs;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

    private ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        startGroup = findViewById(R.id.export_start_grp);
        endGroup = findViewById(R.id.export_end_grp);
        startDate = findViewById(R.id.export_start_date);
        endDate = findViewById(R.id.export_end_date);
        btnSearch = findViewById(R.id.export_search);
        progress = findViewById(R.id.export_progress);

        nameList = findViewById(R.id.export_name_list);
        nameList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                address = nameMap.keySet().toArray()[position].toString();
                dateMinMax = dbHelper.getMinMaxDateForAddress(address);
                startGroup.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        dbHelper = new DatabaseHelper(this);

        loadNames();
    }


    private void loadNames()
    {
        nameMap = dbHelper.getDataNameList();
        ArrayList<String> names = new ArrayList<>();

        for(String s : nameMap.values())
            names.add(s);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameList.setAdapter(arrayAdapter);
    }


    public void onSelectStartDateClick(View v)
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateMinMax[0]);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog d = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                startDate.setText(String.format("%d/%d/%d", month, dayOfMonth, year));
                endGroup.setVisibility(View.VISIBLE);

                c.set(year, month, dayOfMonth);
                lngStartDate = c.getTimeInMillis();

                //clear end date
                endDate.setText("");
                btnSearch.setVisibility(View.INVISIBLE);
            }
        }, year, month, day);

        d.getDatePicker().setMinDate(dateMinMax[0]);
        d.getDatePicker().setMaxDate(dateMinMax[1]);
        d.show();
    }



    public void onSelectEndDateClick(View v)
    {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dateMinMax[1]);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog d = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                endDate.setText(String.format("%d/%d/%d", month, dayOfMonth, year));
                btnSearch.setVisibility(View.VISIBLE);

                c.set(year, month, dayOfMonth, 23,59);
                lngEndDate = c.getTimeInMillis();
            }
        }, year, month, day);

        d.getDatePicker().setMinDate(lngStartDate);
        d.getDatePicker().setMaxDate(dateMinMax[1]);
        d.show();
    }


    public void search(View v)
    {
        if(!hasWritePermission())
        {
            return;
        }

        final EditText input = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filename");
        builder.setMessage("Enter a filename.");
        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                writeFile(input.getText().toString());
            }
        });

        builder.show();
    }

    private void writeFile(String name)
    {
        progress.setVisibility(View.VISIBLE);
        boolean isF = prefs.getString(Preferences.TEMPERATURE_UNITS, "0").equals("0");

        ArrayList<GraphData> data = dbHelper.getExportData(address, lngStartDate, lngEndDate);

        String headerString = "Date,Time,Pit Set,Pit Temp,Food1,Food2" + System.getProperty("line.separator");
        String dataString = "";

        Calendar c = Calendar.getInstance();

        for(GraphData d : data)
        {
            c.setTimeInMillis(d.getDate());

            dataString += dateFormat.format(c.getTime()) + ",";
            dataString += timeFormat.format(c.getTime()) + ",";
            if(isF)
            {
                dataString += d.getPitSet() + ",";
                dataString += d.getProbe1() + ",";
                dataString += d.getProbe2() + ",";
                dataString += d.getProbe3();
            }
            else
            {
                dataString += Temperature.f2c(d.getPitSet()) + ",";
                dataString += Temperature.f2c(d.getProbe1()) + ",";
                dataString += Temperature.f2c(d.getProbe2()) + ",";
                dataString += Temperature.f2c(d.getProbe3());
            }
            dataString += System.getProperty("line.separator");
        }


        try
        {
            File output = getOutputFile(name);
            FileWriter fileWriter = new FileWriter(output);
            fileWriter.write(headerString);
            fileWriter.write(dataString);
            fileWriter.flush();
            fileWriter.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return;
        }

        progress.setVisibility(View.INVISIBLE);
        Snackbar.make(findViewById(R.id.export_snackbar_parent), "Exported " + data.size() + " lines of data.", Snackbar.LENGTH_LONG).show();
    }


    private boolean hasWritePermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);

            return false;
        }
        else
            return true;
    }


    private File getOutputFile(String filename)
    {

        if(filename.length() == 0)
        {
            filename = String.valueOf(lngStartDate);
        }


        int fileIncrementer = 1;

        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();

        File outputDirectory = new File(externalStorage + File.separator + DIRECTORY );

        if(!outputDirectory.exists()){
            outputDirectory.mkdirs();
        }

        File outputFile = new File(externalStorage + File.separator + DIRECTORY + File.separator + filename+ ".csv");

        while (outputFile.exists())
        {
            outputFile = new File(externalStorage + File.separator + DIRECTORY
                    + File.separator + filename + "(" + fileIncrementer + ").csv");
            fileIncrementer++;
        }

        return outputFile;
    }
}
