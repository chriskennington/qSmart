package com.pitmasteriq.qsmart.monitor;

public class ConfigBuilder
{
    public static final int PIT_SET = 1;
    public static final int PIT_ALARM_DEV = 4;
    public static final int DELAY_PIT_SET = 11;
    public static final int DELAY_TIME = 10;
    public static final int PROBE2_ALARM = 2;
    public static final int PROBE2_PIT_SET = 13;
    public static final int PROBE2_TARGET = 12;
    public static final int PROBE3_ALARM = 3;
    public static final int PROBE3_PIT_SET = 15;
    public static final int PROBE3_TARGET = 14;

    public static int[] getSelectedConfigValues(int selector, UnitData currentData)
    {
        int[] data = new int[5];

        switch (selector)
        {
            case 1:
                data[0] = currentData.getPitSet();
                data[1] = 150;
                data[2] = 400;
                data[3] = 66;
                data[4] = 204;
                break;
            case 2:
                data[0] = currentData.getProbe2Alarm();
                data[1] = 50;
                data[2] = 250;
                data[3] = 10;
                data[4] = 121;
                break;
            case 3:
                data[0] = currentData.getProbe3Alarm();
                data[1] = 50;
                data[2] = 250;
                data[3] = 10;
                data[4] = 121;
                break;
            case 4:
                data[0] = currentData.getPitAlarm();
                data[1] = 20;
                data[2] = 100;
                data[3] = 11;
                data[4] = 56;
                break;
            case 10:
                data[0] = currentData.getDelayTime();
                data[1] = 0;
                data[2] = 96;
                break;
            case 11:
                data[0] = currentData.getDelayPitSet();
                data[1] = 150;
                data[2] = 400;
                data[3] = 66;
                data[4] = 204;
                break;
            case 12:
                data[0] = currentData.getProbe2TargetTemp();
                data[1] = 50;
                data[2] = 250;
                data[3] = 10;
                data[4] = 121;
                break;
            case 13:
                data[0] = currentData.getProbe2PitSet();
                data[1] = 150;
                data[2] = 400;
                data[3] = 66;
                data[4] = 204;
                break;
            case 14:
                data[0] = currentData.getProbe3TargetTemp();
                data[1] = 50;
                data[2] = 250;
                data[3] = 10;
                data[4] = 121;
                break;
            case 15:
                data[0] = currentData.getProbe3PitSet();
                data[1] = 150;
                data[2] = 400;
                data[3] = 66;
                data[4] = 204;
                break;
        }

        return data;
    }
}