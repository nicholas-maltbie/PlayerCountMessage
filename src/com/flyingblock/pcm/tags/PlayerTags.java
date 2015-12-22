/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.tags;

import java.util.Arrays;
import org.bukkit.Bukkit;

/**
 *
 * @author Nicholas
 */
public class PlayerTags extends Tag
{
    public static final String PLAYER_TAG = "%player%";
    public static final String ONLINE_TAG = "%online%";
    public static final String MAX_TAG = "%max%";
    public static final String MORE_TAG = "%more%";
    public static final String STAFF_TAG = "%staff%";
    public static final String[] tags = {PLAYER_TAG, ONLINE_TAG, MORE_TAG, STAFF_TAG, MAX_TAG};
    
    public static final String NO_PLAYER = "NOT_ENOUGH_PLAYERS";
    
    private int player;
    private int staff;
    private String[] staffNames;
    private String[] playerNames;
    
    public PlayerTags()
    {
        super(Arrays.asList(tags));
        reset();
    }
    
    public String getNextPlayer()
    {
        String name = NO_PLAYER;
        if(player < playerNames.length)
        {
            name = playerNames[player];
            player++;
        }
        return name;
    }
    
    public String getNextStaff()
    {
        String name = NO_PLAYER;
        if(staff < staffNames.length)
        {
            name = staffNames[staff];
            staff++;
        }
        return name;
    }
    
    @Override
    public String applyTo(String str)
    {
        while(this.containsTarget(str))
        {
            if(str.contains(PLAYER_TAG))
                str = str.replaceFirst(PLAYER_TAG, getNextPlayer());
            else if(str.contains(STAFF_TAG))
                str = str.replaceFirst(STAFF_TAG, getNextStaff());
            else if(str.contains(ONLINE_TAG))
                str = str.replaceFirst(ONLINE_TAG, Integer.toString(Bukkit.getOnlinePlayers().size()));
            else if(str.contains(MORE_TAG))
                str = str.replaceFirst(MORE_TAG, Integer.toString(playerNames.length - player));
            else if(str.contains(MAX_TAG))
                str = str.replaceFirst(MAX_TAG, Integer.toString(Bukkit.getMaxPlayers()));
        }
        return str;
    }
    
    @Override
    public boolean removeLine(String str)
    {
        int playersLeft = playerNames.length - player;
        int staffLeft = staffNames.length - staff;
        return numberOfMentions(str, PLAYER_TAG) > playersLeft ||
                numberOfMentions(str, STAFF_TAG) > staffLeft ||
                str.contains(MORE_TAG) && playersLeft == 0;
    }
    
    @Override
    public boolean reset()
    {
        String[] updatedPlayers = ServerInfo.getOnlinePlayerNames();
        String[] updatedStaff = ServerInfo.getOnlineStaffNames();
        boolean changed = player != 0 || playerNames != updatedPlayers || staff != 0 || staffNames != updatedStaff;
        player = 0;
        staff = 0;
        playerNames = updatedPlayers;
        staffNames = updatedStaff;
        return changed;
    }
    
}
