package com.pitmasteriq.qsmart.monitor;

public class Unit
{
    private String address, defaultName, name, probe2Name, probe3Name;

    public Unit(String address, String defaultName, String name, String probe2Name, String probe3Name)
    {
        this.address = address;
        this.defaultName = defaultName;
        this.name = name;
        this.probe2Name = probe2Name;
        this.probe3Name = probe3Name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setProbe2Name(String probe2Name)
    {
        this.probe2Name = probe2Name;
    }

    public void setProbe3Name(String probe3Name)
    {
        this.probe3Name = probe3Name;
    }

    public String getAddress()
    {
        return address;
    }

    public String getDefaultName()
    {
        return defaultName;
    }

    public String getName()
    {
        return name;
    }

    public String getProbe2Name()
    {
        return probe2Name;
    }

    public String getProbe3Name()
    {
        return probe3Name;
    }
}
