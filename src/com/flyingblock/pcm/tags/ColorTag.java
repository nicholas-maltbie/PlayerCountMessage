/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.tags;

import org.bukkit.ChatColor;

/**
 *
 * @author Nicholas
 */
public class ColorTag extends Tag
{    
    public ColorTag(char from)
    {
        super(from + "");
    }
    
    @Override
    public String applyTo(String str)
    {
        return ChatColor.translateAlternateColorCodes(this.getTargets().get(0).charAt(0), str);
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
