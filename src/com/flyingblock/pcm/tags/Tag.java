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
public abstract class Tag 
{    
    private List<String> targets;
    
    public Tag(String target)
    {
        this.targets = Arrays.asList(target);
    }
    
    public Tag(List<String> targets)
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
    
    public String stripTags(String str)
    {
        for(String target : getTargets()) {
            str = str.replaceAll(target, "");
        }
        return str;
    }
    
    abstract public boolean reset();
    abstract public boolean removeLine(String str);
    abstract public String applyTo(String str);
    
    public static int numberOfMentions(String str, String target)
    {
        if(target.equals(""))
            return 0;
        String copy = str;
        int mentions = 0;
        while(copy.contains(target))
        {
            mentions++;
            copy = copy.replaceFirst(target, "");
        }
        return mentions;
    }
}
