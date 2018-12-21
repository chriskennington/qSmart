package com.pitmasteriq.qsmart.monitor;

public class Temperature
{
    public static int f2c(int f)
    {
        if(f == 0 || f == 999)
            return f;

        float temp = (float) ((f-32) * (5.0/9.0));
        return roundUpOrDown(temp);
    }

    public static int f2cr(int f)
    {
        if(f == 0 || f == 999)
            return f;

        float temp = (float) ((5.0/9.0) * f);
        return roundUpOrDown(temp);
    }

    private static int roundUpOrDown(float temp)
    {
        if (temp > 0)
            temp += 0.5;
        else if (temp < 0)
            temp -= 0.5;

        return (int) temp;
    }
}
