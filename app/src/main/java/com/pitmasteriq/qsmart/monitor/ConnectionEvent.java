package com.pitmasteriq.qsmart.monitor;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

class ConnectionEvent
{
    enum Type
    {
        ConnectionStateChanged,
        CharacteristicChanged,
        CharacteristicWrite,
        GattDescriptorWrite,
        CharacteristicRead,
        PasscodeWrite
    }

    enum Status
    {
        Fail,
        Success,
        Connected,
        Disconnected,
        Unintentional_Disconnect,
        Intentional_Disconnect
    }


    private Type eventType;
    private Status eventStatus;
    private BluetoothGatt eventGatt;
    private BluetoothGattCharacteristic eventCharacteristic;

    ConnectionEvent(Type type, Status status)
    {
        eventType = type;
        eventStatus = status;
    }

    ConnectionEvent(Type type, Status status, BluetoothGatt gatt)
    {
        eventType = type;
        eventStatus = status;
        eventGatt = gatt;
    }

    ConnectionEvent(Type type, Status status, BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
    {
        eventType = type;
        eventStatus = status;
        eventGatt = gatt;
        eventCharacteristic = characteristic;
    }


    Type type() { return eventType; }
    Status status() { return eventStatus; }
    BluetoothGatt gatt() { return eventGatt; }
    BluetoothGattCharacteristic characteristic() { return eventCharacteristic; }
}
