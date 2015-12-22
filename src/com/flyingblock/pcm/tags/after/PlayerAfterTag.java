/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.tags.after;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 *
 * @author Nick_Pro
 */
public class PlayerAfterTag extends AfterTag
{
    public static String guestName = "Guest";
    public static final String USER_TAG = "%user%";
    public static final String IP_TAG = "%ip%";
    
    public static final String[] PLAYER_AFTER_TAGS = {USER_TAG, IP_TAG};
    
    public PlayerAfterTag()
    {
        super(Arrays.asList(PLAYER_AFTER_TAGS));
    }

    @Override
    public String applyTo(String str, Player player) 
    {
        if(player == null || player.getAddress() == null)
            return stripTags(str);
        String name = PlayerInfoIP.getPlayerName(player);
        if(name == null)
            name = guestName;
        String ip = player.getAddress().getAddress().toString();
        
        
        while(this.containsTarget(str))
        {
            if(str.contains(USER_TAG))
                str = str.replaceFirst(USER_TAG, name);
            else if(str.contains(IP_TAG))
                str = str.replaceFirst(IP_TAG, ip);
        }
        return str;
    }

    @Override
    public boolean reset() 
    {
        return false;
    }

    @Override
    public boolean removeLine(String str) 
    {
        return false;
    }
    
    
    
}
