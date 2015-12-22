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
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.events.ScheduledPacket;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.flyingblock.pcm.PingData;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale.Builder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PingListener implements PacketListener
{
    public static int PING_INTERVAL = 100;

    private static ProtocolManager manager;
    
    private static List<PingResponseThread> handlers = new ArrayList<>();
    
    private JavaPlugin pluginRef;
    private PingAnimationSave pingSave;
    
    public PingListener(JavaPlugin pluginRef, PingAnimationSave pingSave)  
    {
        //super(pluginRef, ListenerPriority.HIGHEST, PacketType.Status.Server.OUT_SERVER_INFO, PacketType.Status.Server.OUT_PING);
        this.pluginRef = pluginRef;
        this.pingSave = pingSave;
        manager = ProtocolLibrary.getProtocolManager();
    }
    
    public void unregister()
    {
        ProtocolLibrary.getProtocolManager().removePacketListener(this);
    }

    @Override
    public void onPacketSending(PacketEvent event) 
    {
        PingAnimation pings = pingSave.develop(event.getPlayer());
        
        if (true) {
            if (event.getPacket().getType() == PacketType.Status.Server.OUT_SERVER_INFO) {
                for(PingResponseThread thread : handlers) {
                    if(thread.getAdress().getAddress().equals(event.getPlayer().getAddress().getAddress()))
                    {
                        event.setCancelled(true);
                        return;
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
                        //event.setCancelled(thread.cancelOut);
                    }
                }
            }
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent pe)
    {
        
    }

    @Override
    public ListeningWhitelist getSendingWhitelist() 
    {
        return ListeningWhitelist.newBuilder().priority(ListenerPriority.HIGH).
                types(PacketType.Status.Server.OUT_SERVER_INFO).build();
    }

    @Override
    public ListeningWhitelist getReceivingWhitelist() 
    {
        return new ListeningWhitelist(ListenerPriority.LOW);
    }

    @Override
    public Plugin getPlugin() 
    {
        return pluginRef;
    }

    private static class PingResponseThread extends Thread
    {

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
    
    public static void main(String[] args)
    {
        for(int i = 0; i < 400; i++)
        {
            System.out.print(i + ",");
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