/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.tags;

import java.util.Arrays;
import java.util.Calendar;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 *  
 * @author Nick_Pro
 */
public class CurrentTag extends Tag
{
    public static String world = "world";
    
    public static final String HOURS = "%hour%";
    public static final String HOURS_24 = "%hour24%";
    public static final String MINUTE = "%minute%";
    public static final String SECOND = "%second%";
    public static final String MILLISECOND = "%millis%";
    public static final String AM_PM = "%ampm%";
    public static final String GAME_HOURS = "%ghour%";
    public static final String GAME_HOURS_24 = "%ghour24%";
    public static final String GAME_MINUTE = "%gminute%";
    public static final String GAME_SECOND = "%gsecond%";
    public static final String WEATHER = "%weather%";
    public static final String GAME_AM_PM = "%gampm%";
    
    
    public CurrentTag()
    {
        super(Arrays.asList(HOURS, HOURS_24, MINUTE, SECOND, MILLISECOND,
                GAME_HOURS, GAME_HOURS_24, GAME_MINUTE, GAME_SECOND, AM_PM,
                GAME_AM_PM, WEATHER));
    }
    
    @Override
    public String applyTo(String str)
    {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int hour12 = hour % 12;
        String ampm = "am";
        if(hour >= 12)
            ampm = "pm";
        if(hour == 0)
            hour = 12;
        if(hour12 == 0)
            hour12 = 12;
        World world = Bukkit.getWorlds().get(0);
        for(World w : Bukkit.getWorlds())
        {
            if(w.getName().equalsIgnoreCase(CurrentTag.world))
                world = w;
        }
        
        String weather = "Sunny";
        if(world.hasStorm())
            weather = "Stormy";
        int time = (int)((world.getTime()+6000) % 24000);
        
        int gHour = time/1000;
        int gHour12 = gHour%12;
        String gampm = "am";
        if(gHour >= 12)
            gampm = "pm";
        if(gHour == 0)
            gHour = 12;
        if(gHour12 == 0)
            gHour12 = 12;
        
        while(this.containsTarget(str))
        {
            if(str.contains(HOURS))
                str = str.replaceFirst(HOURS, Integer.toString(hour12));
            else if(str.contains(HOURS_24))
                str = str.replaceFirst(HOURS_24, Integer.toString(hour));
            else if(str.contains(MINUTE))
                str = str.replaceFirst(MINUTE, Integer.toString(c.get(Calendar.MINUTE)));
            else if(str.contains(SECOND))
                str = str.replaceFirst(SECOND, Integer.toString(c.get(Calendar.SECOND)));
            else if(str.contains(MILLISECOND))
                str = str.replaceFirst(MILLISECOND, Integer.toString(c.get(Calendar.MILLISECOND)));
            else if(str.contains(GAME_HOURS))
                str = str.replaceFirst(GAME_HOURS, Integer.toString(gHour12));
            else if(str.contains(GAME_HOURS_24))
                str = str.replaceFirst(GAME_HOURS_24, Integer.toString(gHour));
            else if(str.contains(GAME_MINUTE))
                str = str.replaceFirst(GAME_MINUTE, Integer.toString((int)((time%1000)/16.66666666666)));
            else if(str.contains(GAME_SECOND))
                str = str.replaceFirst(GAME_SECOND, Integer.toString((int)((time%(50/3))/.2777777777777)));
            else if(str.contains(WEATHER))
                str = str.replaceFirst(WEATHER, weather);
            else if(str.contains(AM_PM))
                str = str.replaceFirst(AM_PM, ampm);
            else if(str.contains(GAME_AM_PM))
                str = str.replaceFirst(GAME_AM_PM, gampm);
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
