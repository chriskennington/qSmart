package com.pitmasteriq.qsmart.monitor;

public class GraphData
{
    private long date;
    private int pitSet, probe1, probe2, probe3;

    public GraphData(long date, int pitSet, int probe1, int probe2, int probe3)
    {
        this.date = date;
        this.pitSet = pitSet;
        this.probe1 = probe1;
        this.probe2 = probe2;
        this. probe3 = probe3;
    }

    public long getDate()
    {
        return date;
    }

    public int getPitSet()
    {
        return pitSet;
    }

    public int getProbe1()
    {
        return probe1;
    }

    public int getProbe2()
    {
        return probe2;
    }

    public int getProbe3()
    {
        return probe3;
    }
}
