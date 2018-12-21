package com.pitmasteriq.qsmart.monitor;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;


public class LocalBluetoothCallback extends BluetoothGattCallback
{
    private static final int FAILURE = 0;
    private static final int SUCCESS = 1;
    private static final int CONNECTED = 10;
    private static final int DISCONNECTED = 11;


    private BluetoothCallbackInterface callback;
    private BluetoothGatt btGatt;





    LocalBluetoothCallback(BluetoothCallbackInterface callback)
    {
        this.callback = callback;
    }





    LocalBluetoothCallback(BluetoothGatt gatt, BluetoothCallbackInterface callback)
    {
        btGatt = gatt;
        this.callback = callback;
    }


    public String getAddress()
    {
        if(btGatt != null)
            return btGatt.getDevice().getAddress();
        else
            return null;
    }


    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int state)
    {
        super.onConnectionStateChange(gatt, status, state);

        Log.i("PMIQ", "CONNECTION state status: " + status);

        switch(state)
        {
            case BluetoothGatt.STATE_CONNECTED:
                gatt.discoverServices();
                break;

            case BluetoothGatt.STATE_DISCONNECTED:
                ConnectionEvent.Status s;

                if (status == 0)
                    s = ConnectionEvent.Status.Intentional_Disconnect;
                else
                    s = ConnectionEvent.Status.Unintentional_Disconnect;


                ConnectionEvent e = new ConnectionEvent(
                        ConnectionEvent.Type.ConnectionStateChanged,
                        s,
                        gatt);
                callback.onEvent(e);
                break;
        }

    }





    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status)
    {
        super.onServicesDiscovered(gatt, status);

        if (status == BluetoothGatt.GATT_SUCCESS)
        {
            //send message to main activity of success

            ConnectionEvent e = new ConnectionEvent(
                    ConnectionEvent.Type.ConnectionStateChanged,
                    ConnectionEvent.Status.Connected,
                    gatt);
            callback.onEvent(e);
        }
        else
        {
            //send message to main activity of failure
            ConnectionEvent e = new ConnectionEvent(
                    ConnectionEvent.Type.ConnectionStateChanged,
                    ConnectionEvent.Status.Fail,
                    gatt);
            callback.onEvent(e);
        }
    }





    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
    {
        ConnectionEvent e = new ConnectionEvent(
                ConnectionEvent.Type.CharacteristicChanged,
                ConnectionEvent.Status.Success,
                gatt,
                characteristic);
        callback.onEvent(e);
    }


    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
        super.onCharacteristicRead(gatt, characteristic, status);

        if(status == BluetoothGatt.GATT_SUCCESS )
        {
            ConnectionEvent e = new ConnectionEvent(
                    ConnectionEvent.Type.CharacteristicRead,
                    ConnectionEvent.Status.Success,
                    gatt,
                    characteristic
            );

            callback.onEvent(e);
        }
        else
        {
            ConnectionEvent e = new ConnectionEvent(
                    ConnectionEvent.Type.CharacteristicRead,
                    ConnectionEvent.Status.Fail,
                    gatt,
                    characteristic
            );

            callback.onEvent(e);
        }

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
    {
        super.onCharacteristicWrite(gatt, characteristic, status);

        if (status == BluetoothGatt.GATT_SUCCESS)
        {
            //get connection type (passcode write or characteristic write)
            ConnectionEvent.Type type = (characteristic.getUuid().equals(Uuid.PASSCODE))?
                    ConnectionEvent.Type.PasscodeWrite: ConnectionEvent.Type.CharacteristicWrite;

            ConnectionEvent e = new ConnectionEvent(
                    type,
                    ConnectionEvent.Status.Success,
                    gatt,
                    characteristic
            );
            callback.onEvent(e);
        }
        else
        {
            ConnectionEvent e = new ConnectionEvent(
                    ConnectionEvent.Type.CharacteristicWrite,
                    ConnectionEvent.Status.Fail,
                    gatt,
                    characteristic
            );
            callback.onEvent(e);
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
    {
        super.onDescriptorWrite(gatt, descriptor, status);

        if (status == BluetoothGatt.GATT_SUCCESS)
        {
            ConnectionEvent e = new ConnectionEvent(
                    ConnectionEvent.Type.GattDescriptorWrite,
                    ConnectionEvent.Status.Success,
                    gatt);
            callback.onEvent(e);
        }
    }
}
