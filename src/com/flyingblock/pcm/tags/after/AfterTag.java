/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.tags.after;

import com.flyingblock.pcm.tags.Tag;
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author Nick_Pro
 */
public abstract class AfterTag extends Tag
{
    public AfterTag(String target)
    {
        super(target);
    }
    
    public AfterTag(List<String> targets)
    {
        super(targets);
    }
    
    /**
     * Use the overloaded method applyTo(String str, Player player) instead
     * @param str a string to pass
     * @return the string passed
     * @deprecated because it is not valid in this type of tag
     */
    @Deprecated
    @Override
    public final String applyTo(String str)
    {
        return str;
    }
    
    abstract public String applyTo(String str, Player player);
}
