package com.pitmasteriq.qsmart.monitor;


import android.util.Log;

import java.util.Locale;

public class Device
{
    public static final String LEGACY_IQ_REGEX = "[I][Q][A-F0-9]{4}";
    public static final String IQ_REGEX = "[i][Q][A-F0-9]{4}";

    public enum ConnectionStatus
    {
        CONNECTED, DISCONNECTED, LOST_CONNECTION
    }

    private String address;
    private String name, displayName, probe2Name, probe3Name;

    private short passcode;

    private ConnectionStatus connectionStatus;
    private UnitData data;


    private boolean requiresPasscode;


    public Device(String address, String name)
    {
        this.address = address;
        this.name = name;
        this.displayName = name;
        this.probe2Name = "Food 1";
        this.probe3Name = "Food 2";

        if(name == null)
        {
            this.name = "IQ" + address.replaceAll(":", ""). substring(8, 12);
            requiresPasscode = true;
        }
        else if(name.matches(LEGACY_IQ_REGEX))
            requiresPasscode = true;
        else
            requiresPasscode = false;

        //set default passcode
        passcode = 0;
    }

    public String getAddress() { return address; }

    public String getName() { return name; }

    public String getDisplayName() { return displayName; }
    public String getProbe2Name() { return probe2Name; }
    public String getProbe3Name() { return probe3Name; }

    public void setDisplayName(String name) { displayName = name; }
    public void setProbe2Name(String name) { probe2Name = name; }
    public void setProbe3Name(String name) { probe3Name = name; }


    public boolean doesRequirePasscode() { return requiresPasscode; }

    /**
     * Check if this device is the same as another.
     * @param address
     * @return True if the address of this object is the same as the address paramter
     */
    public boolean is(String address) { return this.address.equals(address);}

    public void connectionStateChanged(ConnectionStatus newState)
    {
        this.connectionStatus = newState;

        if(newState != ConnectionStatus.CONNECTED)
            data.zeroOut();
    }

    public void setPasscode(short code) { this.passcode = code; }
    public void setPasscode(String code) { this.passcode = Short.parseShort(code); Log.e("debug", "set passcode to " + passcode);}

    public short getPasscode() { return passcode; }
    public String getPasscodeString() { return String.format(Locale.getDefault(),"%07d", passcode); }

    public void setUnitData(UnitData ud) { data = ud; }
    public UnitData data() { return data; }

    public ConnectionStatus connectionStatus() { return connectionStatus; }


    public void load(Device device)
    {
        if(device != null)
        {
            this.displayName = device.getDisplayName();
            this.probe2Name = device.getProbe2Name();
            this.probe3Name = device.getProbe3Name();
            this.passcode = device.getPasscode();
        }
    }
}
