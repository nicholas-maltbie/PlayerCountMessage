/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.tags;

import java.util.Arrays;

/**
 *
 * @author Nicholas
 */
public class Alignment extends Format
{
    public static final String CENTER_TAG = "%cent%";
    public static final String RIGHT_TAG = "%right%";
    public static final String LEFT_TAG = "%left%";
    
    public static final String[] targets = {CENTER_TAG,RIGHT_TAG,LEFT_TAG};
    
    public Alignment()
    {
        super(Arrays.asList(targets));
    }
    
    @Override
    public boolean reset()
    {
        return false;
    }
    
    public boolean removeLine(String str)
    {
        return false;
    }
    
    @Override
    public String[] format(String[] lines)
    {
        String[] targetsArray = getTargets().toArray(new String[getTargets().size()]);
        int longest = 0;
        for(String line : lines)
        {
            int length = MineLetter.length(removeTargets(line, targetsArray));
            if(length > longest)
                longest = length;
        }
        
        String[] allignedLines = new String[lines.length];
        for(int i = 0; i < lines.length; i++)
        {
            String line = lines[i];
            if(line.contains(CENTER_TAG))
            {
                line = removeTargets(line, targetsArray);
                int origLength = MineLetter.length(line);
                while(MineLetter.length(line) < longest/2 + origLength/2)
                    line = " ".concat(line);
            }
            else if(line.contains(LEFT_TAG))
            {
                line = removeTargets(line, targetsArray);
                //do nothing
            }
            else if(line.contains(RIGHT_TAG))
            {
                line = removeTargets(line, targetsArray);
                while(MineLetter.length(line) < longest)
                    line = " ".concat(line);  
            }
            allignedLines[i] = line;
        }
        return allignedLines;
    }
}
