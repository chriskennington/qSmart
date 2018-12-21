package com.pitmasteriq.qsmart.monitor;

import java.util.ArrayList;

public class ExceptionManager
{
    enum ExceptionType
    {
        ALARM, NOTIFY, GENERAL
    }

    private static ExceptionManager instance;

    private ArrayList<Exception> exceptions = new ArrayList<>();
    private long prevFlags = 0;
    private boolean shouldNotify;
    private boolean shouldAlarm;



    public static ExceptionManager get()
    {
        if(instance == null)
            instance = new ExceptionManager();

        return instance;
    }


    private ExceptionManager()
    {
        exceptions.add(new Exception(1, "Enclosure hot", ExceptionType.ALARM));
        exceptions.add(new Exception(2, "Food1 probe error", ExceptionType.ALARM));
        exceptions.add(new Exception(4, "Food2 probe error", ExceptionType.ALARM));
        exceptions.add(new Exception(8, "Food 1 done", ExceptionType.ALARM));
        exceptions.add(new Exception(16, "Food 2 done", ExceptionType.ALARM));
        exceptions.add(new Exception(32, "Pit is too hot", ExceptionType.ALARM));
        exceptions.add(new Exception(64, "Pit is too cold", ExceptionType.ALARM));
        exceptions.add(new Exception(128, "Lid off detected", ExceptionType.NOTIFY));
        exceptions.add(new Exception(256, "Delay pit set activated", ExceptionType.NOTIFY));
        exceptions.add(new Exception(512, "Food1 probe pit set activated", ExceptionType.NOTIFY));
        exceptions.add(new Exception(1024, "Food2 probe pit set activated", ExceptionType.NOTIFY));
        exceptions.add(new Exception(2048, "Pit probe error", ExceptionType.ALARM));
        exceptions.add(new Exception(4096, "Food1 probe not present", ExceptionType.GENERAL));
        exceptions.add(new Exception(8192, "Food2 probe not present", ExceptionType.GENERAL));
        exceptions.add(new Exception(16384, "Connection lost", ExceptionType.ALARM));

        shouldNotify = false;
        shouldAlarm = false;
    }


    private void toggleException(int id, boolean set)
    {
        for(Exception e : exceptions)
        {
            if(e.getId() == id)
                if(set)
                {
                    e.activate();
                    if (e.getType() == ExceptionType.NOTIFY)
                    {
                        shouldNotify = true;
                    }

                    if (e.getType() == ExceptionType.ALARM)
                    {
                        shouldNotify = true;
                        shouldAlarm = true;
                    }
                }
                else
                    e.deactivate();
        }
    }

    public int getExceptionHashCode()
    {
        int ids = 0;
        for(Exception e : exceptions)
        {
            if(e.isActive())
                ids += e.getId();
        }

        return ids;
    }



    public void updateExceptionsList(short flags)
    {
        if(flags == 0)
        {
            shouldAlarm = false;
            shouldNotify = false;
        }

        if(flags != prevFlags)
        {
            for (int i = ExceptionStates.ALARM_BITS - 1; i >= 0; i--)
            {
                int id = (1 << i);
                int f = (flags & id);
                toggleException(id, f > 0);
            }

            prevFlags = flags;
        }
    }

    public boolean shouldAlarm() { return shouldAlarm; }

    public boolean shouldNotify()
    {
        return shouldNotify;
    }

    public void notified() { shouldNotify = false; }
    public void alarmSounded() { shouldAlarm = false; }

    public boolean hasActiveAlarm()
    {
        for(Exception e : exceptions)
        {
            if (e.isActive() && e.getType() == ExceptionType.ALARM)
                return true;
        }

        return false;
    }

    public boolean hasActiveNotification()
    {
        for(Exception e : exceptions)
        {
            if (e.isActive() && e.getType() == ExceptionType.NOTIFY)
                return true;
        }

        return false;
    }

    public String getExceptionString()
    {
        String result = "";

        for(Exception e : exceptions)
        {
           if(e.isActive())
               result += e.getName() + "\n";
        }

        return result;
    }















    private class Exception
    {
        int id;
        long activated;
        ExceptionType type;
        String name;

        public Exception(int id, String name, ExceptionType type)
        {
            this.id = id;
            this.name = name;
            this.type = type;
            activated = 0;
        }

        public int getId() { return id; }
        public boolean isActive() { return activated > 0; }
        public String getName() { return  name; }
        public ExceptionType getType() { return type; }
        public void activate() { activated = System.currentTimeMillis(); }
        public void deactivate() { activated = 0; }
    }

















    public static ArrayList<String> getExceptions(short flagValue)
    {
        ArrayList<String> exceptions = new ArrayList<>();

        for(int i=1; i <= 16384; i=i*2)
        {
            String name = getExceptionName(i);
            if ((flagValue & i) == i)
            {
                if(!exceptions.contains(name))
                    exceptions.add(name);
            }
            else
            {
                exceptions.remove(name);
            }
        }

        return exceptions;
    }


    private static String getExceptionName(int index)
    {
        switch(index)
        {
            case 1: return "Enclosure hot";
            case 2: return "Food1 probe error";
            case 4: return "Food2 probe error";
            case 8: return "Food 1 done";
            case 16: return "Food 2 done";
            case 32: return "Pit is too hot";
            case 64: return "Pit is too cold";
            case 128: return "Lid off detected";
            case 256: return "Delay pit set activated";
            case 512: return "Food1 probe pit set activated";
            case 1024: return "Food2 probe pit set activated";
            case 2048: return "Pit probe error";
            case 4096: return "Food1 probe not present";
            case 8192: return "Food2 probe not present";
            case 16384: return "Connection lost";
        }

        return "null";
    }
}

