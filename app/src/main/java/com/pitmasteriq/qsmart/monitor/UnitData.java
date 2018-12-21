package com.pitmasteriq.qsmart.monitor;

public class UnitData
{
    private int pitSet;
    private int probe1Temp, probe2Temp, probe3Temp;
    private int pitAlarm, delayPitSet, delayTime, probe2Alarm, probe2PitSet, probe2TargetTemp, probe3Alarm, probe3PitSet, probe3TargetTemp;
    private int blowerPower, minutesPast;
    private short flagValue;

    public UnitData(){}

    public int getPitSet()
    {
        return pitSet;
    }

    public void setPitSet(int pitSet)
    {
        this.pitSet = pitSet;
    }

    public int getProbe1Temp()
    {
        return probe1Temp;
    }

    public void setProbe1Temp(int probe1Temp)
    {
        this.probe1Temp = probe1Temp;
    }

    public int getProbe2Temp()
    {
        return probe2Temp;
    }

    public void setProbe2Temp(int probe2Temp)
    {
        this.probe2Temp = probe2Temp;
    }

    public int getProbe3Temp()
    {
        return probe3Temp;
    }

    public void setProbe3Temp(int probe3Temp)
    {
        this.probe3Temp = probe3Temp;
    }

    public int getPitAlarm()
    {
        return pitAlarm;
    }

    public void setPitAlarm(int pitAlarm)
    {
        this.pitAlarm = pitAlarm;
    }

    public int getDelayPitSet()
    {
        return delayPitSet;
    }

    public void setDelayPitSet(int delayPitSet)
    {
        this.delayPitSet = delayPitSet;
    }

    public int getDelayTime()
    {
        return delayTime;
    }

    public void setDelayTime(int delayTime)
    {
        this.delayTime = delayTime;
    }

    public int getProbe2Alarm()
    {
        return probe2Alarm;
    }

    public void setProbe2Alarm(int probe2Alarm)
    {
        this.probe2Alarm = probe2Alarm;
    }

    public int getProbe2PitSet()
    {
        return probe2PitSet;
    }

    public void setProbe2PitSet(int probe2PitSet)
    {
        this.probe2PitSet = probe2PitSet;
    }

    public int getProbe2TargetTemp()
    {
        return probe2TargetTemp;
    }

    public void setProbe2TargetTemp(int probe2TargetTemp)
    {
        this.probe2TargetTemp = probe2TargetTemp;
    }

    public int getProbe3Alarm()
    {
        return probe3Alarm;
    }

    public void setProbe3Alarm(int probe3Alarm)
    {
        this.probe3Alarm = probe3Alarm;
    }

    public int getProbe3PitSet()
    {
        return probe3PitSet;
    }

    public void setProbe3PitSet(int probe3PitSet)
    {
        this.probe3PitSet = probe3PitSet;
    }

    public int getProbe3TargetTemp()
    {
        return probe3TargetTemp;
    }

    public void setProbe3TargetTemp(int probe3TargetTemp)
    {
        this.probe3TargetTemp = probe3TargetTemp;
    }

    public int getBlowerPower()
    {
        return blowerPower;
    }

    public void setBlowerPower(int blowerPower)
    {
        this.blowerPower = blowerPower;
    }

    public int getMinutesPast()
    {
        return minutesPast;
    }

    public void setMinutesPast(int minutesPast)
    {
        this.minutesPast = minutesPast;
    }

    public short getFlagValue()
    {
        return flagValue;
    }

    public void setFlagValue(short flagValue)
    {
        this.flagValue = flagValue;
    }

    public int getTemperatureHash()
    {
        return pitSet + probe1Temp + probe2Temp + probe3Temp;
    }


    public void zeroOut()
    {
        pitSet=999;
        probe1Temp=999;
        probe2Temp=999;
        probe3Temp=999;
        pitAlarm=0;
        delayPitSet=0;
        delayTime=0;
        probe2Alarm=0;
        probe2PitSet=0;
        probe2TargetTemp=0;
        probe3Alarm=0;
        probe3PitSet=0;
        probe3TargetTemp=0;
        flagValue = 0;
    }
}

