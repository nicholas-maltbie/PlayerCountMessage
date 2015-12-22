/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.tags;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;

/**
 *
 * @author Nick_Pro
 */
public class DateTag extends Tag
{
    public static final String MONTH_TAG = "%month%";
    public static final String SHORT_MONTH_TAG = "%smonth%";
    public static final String MONTH_NUMBER_TAG = "%-month-%";
    public static final String DAY_TAG = "%day%";
    public static final String SHORT_DAY_TAG = "%sday%";
    public static final String DAY_NUMBER_TAG = "%-day-%";
    public static final String YERAR_TAG = "%year%";
    public static final String YEAR_NUMBER_TAG = "%-year-%";
    public static final String[] tags = {MONTH_TAG, MONTH_NUMBER_TAG, DAY_TAG, DAY_NUMBER_TAG, YERAR_TAG, YEAR_NUMBER_TAG, SHORT_MONTH_TAG, SHORT_DAY_TAG};
    
    public DateTag()
    {
        super(Arrays.asList(tags));
    }
    
    @Override
    public String applyTo(String str)
    {
        Calendar c = Calendar.getInstance();

        DateFormatSymbols dfs = new DateFormatSymbols();
        String month = dfs.getMonths()[c.get(Calendar.MONTH)];
        String day = dfs.getWeekdays()[c.get(Calendar.DAY_OF_WEEK)];
        String smonth = dfs.getShortMonths()[c.get(Calendar.MONTH)];
        String sday  = dfs.getShortWeekdays()[c.get(Calendar.DAY_OF_WEEK)];
        while(this.containsTarget(str))
        {
            if(str.contains(MONTH_TAG))
                str = str.replaceFirst(MONTH_TAG, month);
            else if(str.contains(MONTH_NUMBER_TAG))
                str = str.replaceFirst(MONTH_NUMBER_TAG, Integer.toString(c.get(Calendar.MONTH)));
            else if(str.contains(DAY_TAG))
                str = str.replaceFirst(DAY_TAG, day);
            else if(str.contains(DAY_NUMBER_TAG))
                str = str.replaceFirst(DAY_NUMBER_TAG, Integer.toString(c.get(Calendar.DAY_OF_MONTH)));
            else if(str.contains(SHORT_DAY_TAG))
                str = str.replaceFirst(SHORT_DAY_TAG, sday);
            else if(str.contains(SHORT_MONTH_TAG))
                str = str.replaceFirst(SHORT_MONTH_TAG, smonth);
            else if(str.contains(YERAR_TAG))
                str = str.replaceFirst(YERAR_TAG, Integer.toString(c.get(Calendar.YEAR)));
            else if(str.contains(YEAR_NUMBER_TAG))
                str = str.replaceFirst(YEAR_NUMBER_TAG, Integer.toString(c.get(Calendar.YEAR) % 100));
        }
        return str;
    }
    
    @Override
    public boolean removeLine(String str)
    {
        return false;
    }
    
    @Override
    public boolean reset()
    {
        return false;
    }
    
}