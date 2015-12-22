/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.tags;

/**
 *
 * @author Nicholas
 */
public class TestTag extends Tag
{
    private int number;
    
    public TestTag(String replace)
    {
        super("%test%");
    }
    
    @Override
    public String applyTo(String str)
    {
        while(this.containsTarget(str))
        {
            int begin = str.indexOf(getTargets().get(0));
            if(begin + getTargets().get(0).length() == str.length())
                str = str.substring(0, begin).concat(this.getTargets().get(0) + " " + number);
            else
                str = str.substring(0,begin).concat(this.getTargets().get(0) + " " + number).concat(str.substring(begin + getTargets().get(0).length()));
            number++;
        }
        return str;
    }
    
    @Override
    public boolean removeLine(String str)
    {
        return !containsTarget(str);
    }
    
    @Override
    public boolean reset()
    {
        boolean changed = number == 0;
        number = 0;
        return changed;
    }    
}
