package com.pitmasteriq.qsmart.monitor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DataParser
{
    private static final int TEMPERATURE_OFFSET = 145;

    public static UnitData parseData(byte[] rData, boolean celsius)
    {
        UnitData data = new UnitData();

        if(!celsius)
        {
            short value = 0;
            data.setPitAlarm((short) rData[0]);
            data.setDelayTime((int) rData[1]);
            value = bytesToShort((byte) 0, rData[2]);
            data.setDelayPitSet((short) ((value == 0) ? 0 : value + TEMPERATURE_OFFSET));
            data.setProbe1Temp(bytesToShort(rData[3], rData[4]));
            data.setProbe2Temp(bytesToShort(rData[5], rData[6]));
            data.setProbe3Temp(bytesToShort(rData[7], rData[8]));
            data.setMinutesPast(bitShiftRight(rData[9], 4));
            value = bytesToShort((byte) 0, rData[10]);
            data.setPitSet((short) ((value == 0) ? 0 : value + TEMPERATURE_OFFSET));
            data.setProbe2Alarm(bytesToShort((byte) 0, rData[11]));
            data.setProbe3Alarm(bytesToShort((byte) 0, rData[12]));
            data.setBlowerPower((short) rData[13]);
            data.setFlagValue(getFlagValue(bytesToShort(rData[14], rData[15])));
            data.setProbe2TargetTemp(bytesToShort((byte) 0, rData[16]));
            value = bytesToShort((byte) 0, rData[17]);
            data.setProbe2PitSet((short) ((value == 0) ? 0 : value + TEMPERATURE_OFFSET));
            data.setProbe3TargetTemp(bytesToShort((byte) 0, rData[18]));
            value = bytesToShort((byte) 0, rData[19]);
            data.setProbe3PitSet((short) ((value == 0) ? 0 : value + TEMPERATURE_OFFSET));
        }
        else
        {
            short value = 0;
            data.setPitAlarm(Temperature.f2cr((short) rData[0]));
            data.setDelayTime((int) rData[1]);
            value = bytesToShort((byte) 0, rData[2]);
            data.setDelayPitSet(Temperature.f2c((short) ((value == 0) ? 0 : value + TEMPERATURE_OFFSET)));
            data.setProbe1Temp(Temperature.f2c(bytesToShort(rData[3], rData[4])));
            data.setProbe2Temp(Temperature.f2c(bytesToShort(rData[5], rData[6])));
            data.setProbe3Temp(Temperature.f2c(bytesToShort(rData[7], rData[8])));
            data.setMinutesPast(bitShiftRight(rData[9], 4));
            value = bytesToShort((byte) 0, rData[10]);
            data.setPitSet(Temperature.f2c((short) ((value == 0) ? 0 : value + TEMPERATURE_OFFSET)));
            data.setProbe2Alarm(Temperature.f2c(bytesToShort((byte) 0, rData[11])));
            data.setProbe3Alarm(Temperature.f2c(bytesToShort((byte) 0, rData[12])));
            data.setBlowerPower((short) rData[13]);
            data.setFlagValue(getFlagValue(bytesToShort(rData[14], rData[15])));
            data.setProbe2TargetTemp(Temperature.f2c(bytesToShort((byte) 0, rData[16])));
            value = bytesToShort((byte) 0, rData[17]);
            data.setProbe2PitSet(Temperature.f2c((short) ((value == 0) ? 0 : value + TEMPERATURE_OFFSET)));
            data.setProbe3TargetTemp(Temperature.f2c(bytesToShort((byte) 0, rData[18])));
            value = bytesToShort((byte) 0, rData[19]);
            data.setProbe3PitSet(Temperature.f2c((short) ((value == 0) ? 0 : value + TEMPERATURE_OFFSET)));
        }



        return data;
    }



    private static short bitShiftRight(byte i, int amt)
    {
        return (short) (i >> amt);
    }





    private static short bytesToShort(byte i, byte j)
    {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(j);
        bb.put(i);
        return bb.getShort(0);
    }



    private static short getFlagValue(short value)
    {
        short flagValue = 0;

        for (int i = ExceptionStates.ALARM_BITS - 1; i >= 0; i--)
        {
            int f = (value & (1 << i));
            boolean set = (f != 0);

            if(set)
                flagValue += f;
        }
        return flagValue;
    }
}
