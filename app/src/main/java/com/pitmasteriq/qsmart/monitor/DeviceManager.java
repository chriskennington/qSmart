package com.pitmasteriq.qsmart.monitor;

import android.content.Context;

public class DeviceManager
{
    private static DeviceManager instance = null;
    private Context context;

    private Device device;

    public static DeviceManager get()
    {
        if(instance == null)
            instance = new DeviceManager();

        return instance;
    }


    public Device device() { return device; }
    public void newDevice(String address, String name) { device = new Device(address, name); }
}
