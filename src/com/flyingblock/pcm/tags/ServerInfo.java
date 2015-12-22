/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.tags;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Nicholas
 */
public class ServerInfo 
{
    public static boolean includeStaff = false;
    public static final String PCM_STAFF_PERM = "pcm.staff";
    
    private static final String[] dummyPlayers = {"Escmo", "Flyguy23ndm"};
    private static final String[] staff = {"Staff1"};
    
    public static String[] getOnlinePlayerNames()
    {
        try{
            List<String> names = new ArrayList<>();
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission(PCM_STAFF_PERM))
                {
                    if(includeStaff)
                        names.add(player.getName());
                }
                else
                    names.add(player.getName());
            }
            return names.toArray(new String[names.size()]);
        }
        catch (NullPointerException e)
        {
            return dummyPlayers;
        }
    }
    
    public static String[] getOnlineStaffNames()
    {
        try {
            List<String> names = new ArrayList<>();
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(player.hasPermission(PCM_STAFF_PERM))
                    names.add(player.getName());
            }
            return names.toArray(new String[names.size()]);
        }
        catch (NullPointerException e)
        {
            return staff;
        }
    }
    
}
