package com.pitmasteriq.qsmart.monitor;

import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.pitmasteriq.qsmart.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class ScanningFragment extends DialogFragment
{
    private static final String LEGACY_IQ_REGEX = "[I][Q][A-F0-9]{4}";
    private static final String IQ_REGEX = "[i][Q][A-F0-9]{4}";
    private static final int SCAN_FREQUENCY = 10000;
    private static final int SCAN_TIME = 2500;


    public interface ScanningFragmentEvent
    {
        void onUnitSelected(String address, String name, boolean legacy);
    }


    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner btScanner;

    private Timer timer;
    private Handler handler = new Handler();
    private ArrayList<ScanResult> scanResults = new ArrayList<>();

    private ScanningFragmentEvent listener;
    private TextView txtTitle, txtSingleUnit;
    private ImageView imgIcon;
    private Spinner spnUnits;
    private Button btnConnect;



    public ScanningFragment()
    {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_scanner, container, false);

        btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        txtTitle = v.findViewById(R.id.scanner_title);
        txtSingleUnit = v.findViewById(R.id.scanner_single_name);
        imgIcon = v.findViewById(R.id.scanner_icon);
        spnUnits = v.findViewById(R.id.scanner_spinner);
        btnConnect = v.findViewById(R.id.scanner_btn_connect);



        btnConnect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(scanResults.size() == 1)
                    listener.onUnitSelected(scanResults.get(0).address, scanResults.get(0).getName(), scanResults.get(0).isLegacy());
                else if(scanResults.size() > 1)
                {
                    int index = spnUnits.getSelectedItemPosition();
                    listener.onUnitSelected(scanResults.get(index).address, scanResults.get(index).getName(), scanResults.get(index).isLegacy());
                }

                dismiss();
            }
        });

        return v;
    }


    @Override
    public void onStart()
    {
        super.onStart();

        //start scanning
        //addBondedDevicesToScanResults();

        handler.postDelayed(stopScanRunnable, SCAN_TIME);
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
        btScanner.startScan(null, settings, scanCallback);
    }


    @Override
    public void onStop()
    {
        super.onStop();
        btScanner.stopScan(scanCallback);
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof ScanningFragmentEvent)
        {
            listener = (ScanningFragmentEvent) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement ScanningFragmentEvent");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    private void addBondedDevicesToScanResults()
    {
        for(BluetoothDevice d : btAdapter.getBondedDevices())
        {
            String name = d.getName();
            if(name.matches(IQ_REGEX))
            {
                ScanResult sr = new ScanResult(d.getAddress(), name, false);
                if(!scanResults.contains(sr))
                    scanResults.add(sr);
            }
            else if(name.matches(LEGACY_IQ_REGEX))
            {
                ScanResult sr = new ScanResult(d.getAddress(), name, true);
                if(!scanResults.contains(sr))
                    scanResults.add(sr);
            }
        }
    }


    private Runnable stopScanRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            stopScan();
        }
    };

    private void stopScan()
    {
        btScanner.stopScan(scanCallback);

        //hide progress icon
        imgIcon.setVisibility(View.INVISIBLE);

        if(scanResults.size() == 0)
        {
            txtTitle.setText(R.string.no_compatible_devices_scanned);
            btnConnect.setText(R.string.close);
            btnConnect.setVisibility(View.VISIBLE);
        }
        else if (scanResults.size() == 1)
        {
            txtTitle.setText(R.string.connect_to_scanned_device);
            txtSingleUnit.setText(scanResults.get(0).getName());
            txtSingleUnit.setVisibility(View.VISIBLE);
            btnConnect.setText(R.string.connect);
            btnConnect.setVisibility(View.VISIBLE);
        }
        else
        {
            txtTitle.setText(R.string.select_device_to_connect);
            spnUnits.setAdapter(getSpinnerList());
            spnUnits.setVisibility(View.VISIBLE);
            btnConnect.setText(R.string.connect);
            btnConnect.setVisibility(View.VISIBLE);
        }
    }


    private ArrayAdapter<String> getSpinnerList()
    {
        List<String> names = new ArrayList<>();

        for(ScanResult sr : scanResults)
            names.add(sr.getName());


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, names);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return arrayAdapter;
    }


    private ScanCallback scanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result)
        {
            super.onScanResult(callbackType, result);

            BluetoothDevice device = result.getDevice();
            String name = result.getDevice().getName();

            if (name == null)
            {
                Log.v(LogTag.BLUETOOTH, "Name null for " + result.getDevice().getAddress());
            }
            else
            {
                if(name.matches(IQ_REGEX))
                {
                    ScanResult sr = new ScanResult(device.getAddress(), name, false);
                    if(!scanResults.contains(sr))
                        scanResults.add(sr);
                }
                else if(name.matches(LEGACY_IQ_REGEX))
                {
                    ScanResult sr = new ScanResult(device.getAddress(), name, true);
                    if(!scanResults.contains(sr))
                        scanResults.add(sr);
                }
            }
        }
    };








    private class ScanResult
    {
        private String address, name;
        private boolean legacy;

        public ScanResult(String address, String name, boolean legacy)
        {
            this.address = address;
            this.name = name;
            this.legacy = legacy;
        }

        public String getAddress()
        {
            return address;
        }

        public String getName()
        {
            return name;
        }

        public boolean isLegacy()
        {
            return legacy;
        }

        @Override
        public boolean equals(Object obj)
        {
            ScanResult o = (ScanResult)obj;
            return (address.equals(o.getAddress()));
        }
    }
}
