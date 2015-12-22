/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.tags;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Nicholas
 */
abstract public class Format 
{    
    private List<String> targets;
    
    public Format(String target)
    {
        this.targets = Arrays.asList(target);
    }
    
    public Format(List<String> targets)
    {
        this.targets = targets;
    }
    
    public List<String> getTargets()
    {
        return targets;
    }
    
    public boolean containsTarget(String str)
    {
        for(String target : targets)
            if(str.contains(target))
                return true;
        return false;
    }
    
    public static String removeTargets(String str, String[] targets)
    {
        for(String target : targets)
            str = str.replace(target, "");
        return str;
    }
    
    abstract public boolean reset();
    abstract public String[] format(String[] lines);    
}
