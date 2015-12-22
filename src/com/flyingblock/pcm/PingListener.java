package com.flyingblock.pcm;

import com.flyingblock.pcm.animation.PingAnimation;
import com.flyingblock.pcm.animation.PingAnimationSave;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.ScheduledPacket;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.flyingblock.pcm.tags.CurrentTag;
import com.flyingblock.pcm.tags.Tag;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.ClosedChannelException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PingListener extends PacketAdapter
{
    public static int PING_INTERVAL = 100;

    private static final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    
    private static List<PingResponseThread> handlers = new ArrayList<>();
    
    private JavaPlugin pluginRef;
    private PingAnimationSave pingSave;
    
    public PingListener(JavaPlugin pluginRef, PingAnimationSave pingSave)  
    {
        super(pluginRef, ListenerPriority.HIGHEST, PacketType.Status.Server.OUT_SERVER_INFO, PacketType.Status.Server.OUT_PING);
        manager.addPacketListener(this);
        this.pluginRef = pluginRef;
        this.pingSave = pingSave;
    }
    
    @Override
    public void onPacketSending(PacketEvent event)
    {
        PingAnimation pings = pingSave.develop(event.getPlayer());
        
        if (true /*work someting here later*/) {
            if (event.getPacket().getType() == PacketType.Status.Server.OUT_SERVER_INFO) {
                for(PingResponseThread thread : handlers) {
                    if(thread.getAdress().getAddress().equals(event.getPlayer().getAddress().getAddress()))
                    {
                        event.setCancelled(true);
                        thread.stop();
                    }
                }
                event.setCancelled(true);
                PingResponseThread thread = new PingResponseThread(event, pings, this);
                thread.start();
                handlers.add(thread);
            }
            if(event.getPacket().getType() == PacketType.Status.Server.OUT_PING) {
                for(PingResponseThread thread : handlers)
                {
                    if(thread.getAdress().equals(event.getPlayer().getAddress()))
                    {
                        event.setCancelled(thread.cancelOut);
                    }
                }
            }
        }
    }
    
    public void unregister()
    {
        manager.removePacketListener(this);
    }

    private static class PingResponseThread extends Thread
    {
        private Tag[] tags = {new CurrentTag()};
        private static final UUID randomUUID = UUID.randomUUID();

        private Player player;
        private PingAnimation animation;
        private PacketEvent event;
        private PingListener listener;
        
        private boolean cancelOut = true;
        
        private int millis;
        
        public PingResponseThread(PacketEvent event, PingAnimation pingAnimation, PingListener listener) {
            this.player = event.getPlayer();
            this.event = event;
            this.animation = pingAnimation;
            this.listener = listener;
        }
        
        public InetSocketAddress getAdress()
        {
            return player.getAddress();
        }
        
        @SuppressWarnings("deprecation")
        @Override
        public void run() 
        {
            long time = System.currentTimeMillis();
            WrappedServerPing copy = WrappedServerPing.fromJson(event.getPacket().getServerPings().read(0).toJson());
            copy.setVersionProtocol(0);
            try 
            {
                while (!animation.isOver(millis) && player.isOnline())
                {
                    manager.recieveClientPacket(player, new PacketContainer(PacketType.Status.Client.IN_PING));
                    PacketContainer serverInfo = manager.createPacket(PacketType.Status.Server.OUT_SERVER_INFO);
                    serverInfo.getServerPings().write(0, copy);
                    
                    PingData data = animation.getPing(millis);
                    for(Tag t : tags)
                        data.applyTo(t);
                    
                    WrappedServerPing ping = serverInfo.getServerPings().read(0);
                    
                    List<WrappedGameProfile> profiles = new ArrayList<>();
                    for(String name : data.getPlayers())
                        profiles.add(new WrappedGameProfile(randomUUID, name));
                    ping.setVersionName(data.getPlayerCount());
                    //ping.setVersionProtocol(manager.getProtocolVersion(player));
                    ping.setPlayers(profiles);
                    ping.setMotD(data.getMotd());
                    ping.setFavicon(data.getImage());
                    ping.setPlayersMaximum(Bukkit.getMaxPlayers());
                    ping.setPlayersVisible(true);
                    ping.setPlayersOnline(Bukkit.getOnlinePlayers().size());
                    
                    manager.sendServerPacket(player, serverInfo, false);
                    
                    Thread.sleep(PING_INTERVAL);
                    long last = time;
                    time = System.currentTimeMillis();
                    millis += time - last;
                }
            } catch (IllegalAccessException | FieldAccessException | InterruptedException | InvocationTargetException e) {
                e.printStackTrace();
                handlers.remove(this);
                return;
            }
            
            if(player.isOnline())
            {
                PingData data = animation.getLastPing();
                for(Tag t : tags)
                    data.applyTo(t);

                event.setCancelled(false);
                event.setReadOnly(true);
                
                WrappedServerPing ping = event.getPacket().getServerPings().read(0);

                List<WrappedGameProfile> profiles = new ArrayList<>();
                for(String name : data.getPlayers())
                    profiles.add(new WrappedGameProfile(randomUUID, ChatColor.translateAlternateColorCodes('&', name)));
                ping.setPlayers(profiles);
                ping.setMotD(ChatColor.translateAlternateColorCodes('&', data.getMotd()));
                ping.setFavicon(data.getImage());
                ping.setPlayersMaximum(Bukkit.getMaxPlayers());
                ping.setPlayersVisible(true);
                ping.setPlayersOnline(Bukkit.getOnlinePlayers().size());
                
                cancelOut = false;
                
                ScheduledPacket packet = new ScheduledPacket(event.getPacket(), event.getPlayer(), false);
                packet.schedule();
            }
            handlers.remove(this);
        }
    }
}

//WrappedServerPing ping = event.getPacket().getServerPings().read(0);
/*ping.setMotD(Integer.toString(currentPingToDisplay));

PingData data = pingDatas[currentPingToDisplay];

List<WrappedGameProfile> profiles = new ArrayList<>();
for(String name : data.getPlayers())
    profiles.add(new WrappedGameProfile(randomUUID, name));
ping.setPlayers(profiles);
ping.setMotD(data.getMotd());
ping.setFavicon(data.getImage());
ping.setPlayersMaximum(Bukkit.getMaxPlayers());
ping.setPlayersVisible(true);
ping.setPlayersOnline(Bukkit.getOnlinePlayers().length);

ScheduledPacket packet = new ScheduledPacket(event.getPacket(), event.getPlayer(), false);
packet.schedule();
System.out.println("YAY");*/
    
/*
        @Override
        public void onPacketSending(PacketEvent event)
        {
            if (pluginRef.getConfiguration().getPings().length > 0) {
                if (event.getPacket().getType() == PacketType.Status.Server.OUT_SERVER_INFO) {
                    event.setCancelled(true);
                    thread = new PingResponseThread(event, 3, 300, pluginRef.getConfiguration().getPings(), adapter);
                    thread.start();
                }
                if(event.getPacket().getType() == PacketType.Status.Server.OUT_PING) {
                    if(!event.isReadOnly())
                        event.setCancelled(true);
                }
            }
        }
    };
manager.addPacketListener(adapter);*/