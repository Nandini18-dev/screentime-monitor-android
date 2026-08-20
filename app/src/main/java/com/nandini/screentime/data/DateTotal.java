package com.nandini.screentime.data;

/** Simple aggregate row: a date bucket and a summed value (ms or bytes or count). */
public class DateTotal {
    public String date;
    public long total;
}
