package com.pitmasteriq.qsmart.monitor;

public class ExceptionStates
{
    public static final int ALARM_BITS = 11;

    //direct from IQ
    public static final int ENCLOSUREHOT               = 1;
    public static final int PROBE2ERROR                = 2;
    public static final int PROBE3ERROR                = 4;
    public static final int FOOD1DONE                  = 8;
    public static final int FOOD2DONE                  = 16;
    public static final int PITHOT                     = 32;
    public static final int PITCOLD                    = 64;
    public static final int LIDOFF                     = 128;
    public static final int DELAYPITSETACTIVATED       = 256;
    public static final int PROBE2PITSETACTIVATED      = 512;
    public static final int PROBE3PITSETACTIVATED      = 1024;

    //calculated from values
    public static final int PROBE1ERROR                = 2048;
    public static final int PROBE2NOTPRESENT           = 4096;
    public static final int PROBE3NOTPRESENT           = 8192;
    public static final int CONNECTIONLOST             = 16384;
}
