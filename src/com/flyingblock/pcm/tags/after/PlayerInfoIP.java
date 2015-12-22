package com.flyingblock.pcm.tags.after;

import com.flyingblock.pcm.save.SerFile;
import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerInfoIP
{
    public static final String SAVE_FILE_NAME = "PlayerNameMap.ser";
    
    private static File nameDir;
    private static File uuidDir;
    private static Map<InetAddress, String> playerNames;
    private static Map<InetAddress, UUID> playerIds;
    private static Listener playerJoinHandler;
    
    private PlayerInfoIP()
    {
        //you can't do this, hahahahahaha
    }
    
    public static String getPlayerName(InetAddress key)
    {
        return playerNames.get(key);
    }
    
    public static UUID getPlayerUUID(InetAddress key)
    {
        return playerIds.get(key);
    }
    
    public static UUID getPlayerUUID(Player player)
    {
        return playerIds.get(player.getAddress().getAddress());
    }
    
    public static String getPlayerName(Player player)
    {
        return getPlayerName(player.getAddress().getAddress());
    }
    
    public static void setup(JavaPlugin plugin)
    {
        nameDir = new File(plugin.getDataFolder(), SAVE_FILE_NAME);
        SerFile nameSave = new SerFile(nameDir);
        if(!nameDir.exists())
            nameSave.save(new HashMap<>());
        nameSave.read();
        if(!(nameSave.getData() instanceof HashMap))
        {
            nameDir.delete();
            nameSave.save(new HashMap<>());
            nameSave.read();
        }
        playerNames = (Map<InetAddress, String>) nameSave.getData();
        
        playerJoinHandler = new Listener()
                {
                    @EventHandler
                    public void onPlayerJoin(PlayerJoinEvent event)
                    {
                        playerNames.put(event.getPlayer().getAddress().getAddress(), event.getPlayer().getName());
                    }
                };
        plugin.getServer().getPluginManager().registerEvents(playerJoinHandler, plugin);
    }
    
    public static void close()
    {
        PlayerJoinEvent.getHandlerList().unregister(playerJoinHandler);
        SerFile nameSave = new SerFile(nameDir);
        nameSave.save(playerNames);
        //System.out.println(playerData);
    }
    
}
