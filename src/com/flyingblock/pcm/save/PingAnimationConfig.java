/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.save;

import com.flyingblock.pcm.PingListener;
import com.flyingblock.pcm.animation.Animation.ImageAnimationType;
import com.flyingblock.pcm.animation.Animation.StringAnimationType;
import com.flyingblock.pcm.animation.PingAnimationSave;
import com.flyingblock.pcm.tags.CurrentTag;
import com.flyingblock.pcm.tags.ServerInfo;
import com.flyingblock.pcm.tags.after.PlayerAfterTag;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * forward
 * @author Nick_Pro
 */
public class PingAnimationConfig
{
    public static final String DEFAULT_IMAGE_PATH = "server-icon.gif";
    
    public static final int MOTD_LINES = 2;
    
    public static final StringAnimationType STRING_DEFAULT_ANIMATION = StringAnimationType.NONE;
    public static final ImageAnimationType IMAGE_DEFAULT_ANIMATION = ImageAnimationType.GIF;
    public static final int DEFAULT_INTERVAL = 100;
    public static final int DEFAULT_LOOPS = 1;
    
    public static final String SERVER_LIST_FILE = "serverInfo.yml";
    
    public static final String ANIMATION_LENGTH = "length";
    public static final String PING_INTERVAL = "ping_interval";
    public static final String GUEST = "guestname";
    public static final String WORLD = "world";
    public static final String INCLUDE_STAFF = "players-and-staff";
    
    public static final String PLAYERS = "players";
    public static final String MOTD = "motd";
    public static final String VERSION = "version";
    public static final String IMAGE = "server-icon";
    
    public static final String ANIMATION_TYPE = "animation";
    public static final String ANIMATION_TYPES = "animations";
        public static final String WRAPPED_FWD = "wrap forward";
        public static final int WRAPPED_FWD_INT = 0;
        public static final String WRAPPED_BCK = "wrap back";
        public static final int WRAPPED_BCK_INT = 1;
        public static final String SCROLL_FWD = "scroll forward";
        public static final int SCROLL_FWD_INT = 2;
        public static final String SCROLL_BCK = "scroll back";
        public static final int SCROLL_BCK_INT = 3;
        public static final String FRAME_BY_FRAME = "frames";
        public static final int FRAME_BY_FRAME_INT = 4;
        public static final String NO_ANIMATION = "none";
        public static final int NO_ANIMATION_INT = 5;
        
        public static final String GIF_FILE = "gif";
        public static final int GIF_FILE_INT = 0;
        public static final String PNG_FILE = "png";
        public static final int PNG_FILE_INT = 1;
    
    public static final String FRAME_SIZE = "frame_size";
    public static final String LINES = "lines";
    public static final String LINE = "text";
    public static final String INTERVALS = "intervals";
    public static final String INTERVAL = "interval";
    public static final String LOOPS = "loops";
    public static final String IMAGE_PATH = "image_name";
    
    public static final int MIN_LOOPS = -1;
    public static final int MAX_LOOPS = 1000;
    public static final int MIN_INTERVAL = 1;
    public static final int MAX_INTERVAL = 10000;
    
    public static final int MIN_PING = 10;
    public static final int MAX_PING = 1000;
    
    public static final int MIN_PLAYER_FRAME = 10;
    public static final int MAX_PLAYER_FRAME = 120;
    public static final int MIN_VERSION_FRAME = 10;
    public static final int MAX_VERSION_FRAME = 100;
    public static final int MIN_MOTD_FRAME = 10;
    public static final int MAX_MOTD_FRAME = 100;
    
    private PingAnimationSave pingAnimation;
    private ConfigAccessor config;
    private FileConfiguration file;
    private JavaPlugin plugin;
    
    public PingAnimationConfig(JavaPlugin plugin)
    {
        this.plugin = plugin;
        config = new ConfigAccessor(plugin, SERVER_LIST_FILE);
        config.reloadConfig();
        if(!new File(plugin.getDataFolder(), SERVER_LIST_FILE).exists())
        {
            config.saveDefaultConfig();
        }
        file = config.getConfig();
        
        List<String> playerLines = file.getStringList(PLAYERS + "." + LINES);
        int playerLength = playerLines.size();
        List<String> playerAnimsStrings = file.getStringList(PLAYERS + "." + ANIMATION_TYPES);
        List<StringAnimationType> playerAnims = new ArrayList<>();
        for(String animString : playerAnimsStrings) {
            playerAnims.add(parseType(animString));
        }
        playerAnims = resize(playerAnims, STRING_DEFAULT_ANIMATION, playerLength);
        List<Integer> playerIntervals = boundArray(file.getIntegerList(PLAYERS + "." + INTERVALS), MIN_INTERVAL, MAX_INTERVAL);
        playerIntervals = resize(playerIntervals, DEFAULT_INTERVAL, playerLength);
        List<Integer> playerLoops = boundArray(file.getIntegerList(PLAYERS + "." + LOOPS), MIN_LOOPS, MAX_LOOPS);
        playerLoops = resize(playerLoops, DEFAULT_LOOPS, playerLength);
        Integer playerFrameSize = bound(file.getInt(PLAYERS + "." + FRAME_SIZE), MIN_PLAYER_FRAME, MAX_PLAYER_FRAME);
        
        List<String> motdLines = file.getStringList(MOTD + "." + LINES);
        while(motdLines.size() > MOTD_LINES) {
            motdLines.remove(motdLines.size()-1);
        }
        while(motdLines.size() < MOTD_LINES)
            motdLines.add("");
        int motdLength = motdLines.size();
        List<String> motdAnimsStrings = file.getStringList(MOTD + "." + ANIMATION_TYPES);
        List<StringAnimationType> motdAnims = new ArrayList<>();
        for(String animString : motdAnimsStrings) {
            motdAnims.add(parseType(animString));
        }
        motdAnims = resize(motdAnims, STRING_DEFAULT_ANIMATION, motdLength);
        List<Integer> motdIntervals = boundArray(file.getIntegerList(MOTD + "." + INTERVALS), MIN_INTERVAL, MAX_INTERVAL);
        motdIntervals = resize(motdIntervals, DEFAULT_INTERVAL, motdLength);
        List<Integer> motdLoops = boundArray(file.getIntegerList(MOTD + "." + LOOPS), MIN_LOOPS, MAX_LOOPS);
        motdLoops = resize(motdLoops, DEFAULT_LOOPS, motdLength);
        Integer motdFrameSize = bound(file.getInt(MOTD + "." + FRAME_SIZE), MIN_MOTD_FRAME, MAX_MOTD_FRAME);
        
        String versionLine = file.getString(VERSION + "." + LINE);
        StringAnimationType versionAnim = parseType(file.getString(VERSION + "." + ANIMATION_TYPE));
        Integer versionInterval = bound(file.getInt(VERSION + "." + INTERVAL), MIN_INTERVAL, MAX_INTERVAL);
        Integer versionLoops = bound(file.getInt(VERSION + "." + LOOPS), MIN_LOOPS, MAX_LOOPS);
        Integer versionFrameSize = bound(file.getInt(VERSION + "." + FRAME_SIZE), MIN_VERSION_FRAME, MAX_VERSION_FRAME);
        
        String imagePath = file.getString(IMAGE + "." + IMAGE_PATH);
        ImageAnimationType imageAnimation = parseImageType(file.getString(IMAGE + "." + ANIMATION_TYPE));
        Integer imageInterval = bound(file.getInt(IMAGE + "." + INTERVAL), MIN_INTERVAL, MAX_INTERVAL);
        Integer imageLoops = bound(file.getInt(IMAGE + "." + LOOPS), MIN_LOOPS, MAX_LOOPS);
        
        Integer length = bound(file.getInt(ANIMATION_LENGTH), 0, Integer.MAX_VALUE);
        Integer pingInterval = bound(file.getInt(PING_INTERVAL), MIN_PING, MAX_PING);
        String guestName = file.getString(GUEST, "Guest");
        String world = file.getString(WORLD, "world");
        boolean includeStaff = file.getBoolean(INCLUDE_STAFF, true);
        
        ServerInfo.includeStaff = includeStaff;
        CurrentTag.world = world;
        PlayerAfterTag.guestName = guestName;
        PingListener.PING_INTERVAL = pingInterval;
        
        File serverIcon = new File(plugin.getDataFolder(), imagePath);
        if(!serverIcon.exists())
        {
            try {
                Files.copy(plugin.getResource("server-icon.gif"), serverIcon.toPath());
            } catch (IOException ex) {
                Logger.getLogger(PingAnimationConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        pingAnimation = new PingAnimationSave(
            playerLines, playerAnims, playerIntervals, playerLoops,
            motdLines, motdAnims, motdIntervals, motdLoops,
            versionLine, versionAnim, versionInterval, versionLoops,
            imagePath, imageAnimation, imageInterval, imageLoops,
            length, playerFrameSize, motdFrameSize, versionFrameSize, plugin);
    }
    
    public void reloadPlayers()
    {
        config.reloadConfig();
        file = config.getConfig();
        List<String> playerLines = file.getStringList(PLAYERS + "." + LINES);
        int playerLength = playerLines.size();
        List<String> playerAnimsStrings = file.getStringList(PLAYERS + "." + ANIMATION_TYPES);
        List<StringAnimationType> playerAnims = new ArrayList<>();
        for(String animString : playerAnimsStrings) {
            playerAnims.add(parseType(animString));
        }
        playerAnims = resize(playerAnims, STRING_DEFAULT_ANIMATION, playerLength);
        List<Integer> playerIntervals = boundArray(file.getIntegerList(PLAYERS + "." + INTERVALS), MIN_INTERVAL, MAX_INTERVAL);
        playerIntervals = resize(playerIntervals, DEFAULT_INTERVAL, playerLength);
        List<Integer> playerLoops = boundArray(file.getIntegerList(PLAYERS + "." + LOOPS), MIN_LOOPS, MAX_LOOPS);
        playerLoops = resize(playerLoops, DEFAULT_LOOPS, playerLength);
        Integer playerFrameSize = bound(file.getInt(PLAYERS + "." + FRAME_SIZE), MIN_PLAYER_FRAME, MAX_PLAYER_FRAME);
        
        while(pingAnimation.getPlayers() > 0)
            pingAnimation.removePlayer(0);
        for(int i = 0; i < playerLength; i++)
            pingAnimation.addPlayer(playerLines.get(i), playerAnims.get(i), playerIntervals.get(i), playerLoops.get(i));
        
        pingAnimation.setPlayerFrameSize(playerFrameSize);
    }
    
    public void reloadMotd()
    {
        config.reloadConfig();
        file = config.getConfig();
        
        List<String> motdLines = file.getStringList(MOTD + "." + LINES);
        int motdLength = motdLines.size();
        List<String> motdAnimsStrings = file.getStringList(MOTD + "." + ANIMATION_TYPES);
        List<StringAnimationType> motdAnims = new ArrayList<>();
        for(String animString : motdAnimsStrings) {
            motdAnims.add(parseType(animString));
        }
        motdAnims = resize(motdAnims, STRING_DEFAULT_ANIMATION, motdLength);
        List<Integer> motdIntervals = boundArray(file.getIntegerList(MOTD + "." + INTERVALS), MIN_INTERVAL, MAX_INTERVAL);
        motdIntervals = resize(motdIntervals, DEFAULT_INTERVAL, motdLength);
        List<Integer> motdLoops = boundArray(file.getIntegerList(MOTD + "." + LOOPS), MIN_LOOPS, MAX_LOOPS);
        motdLoops = resize(motdLoops, DEFAULT_LOOPS, motdLength);
        Integer motdFrameSize = bound(file.getInt(MOTD + "." + FRAME_SIZE), MIN_MOTD_FRAME, MAX_MOTD_FRAME);
        
        
        while(pingAnimation.getMotdLength() > 0)
            pingAnimation.removeMotdLine(0);
        for(int i = 0; i < motdLength; i++)
            pingAnimation.addMotdLine(motdLines.get(i), motdAnims.get(i), motdIntervals.get(i), motdLoops.get(i));
        pingAnimation.setMotdFrameSize(motdFrameSize);
    }
    
    public void reloadVersion()
    {
        config.reloadConfig();
        file = config.getConfig();
        
        pingAnimation.setVersionName(file.getString(VERSION + "." + LINE));
        pingAnimation.setVersionType(parseType(file.getString(VERSION + "." + ANIMATION_TYPE)));
        pingAnimation.setVersionInterval(bound(file.getInt(VERSION + "." + INTERVAL), MIN_INTERVAL, MAX_INTERVAL));
        pingAnimation.setVersionLoops(bound(file.getInt(VERSION + "." + LOOPS), MIN_LOOPS, MAX_LOOPS));
        pingAnimation.setVersionFrameSize(bound(file.getInt(VERSION + "." + FRAME_SIZE), MIN_VERSION_FRAME, MAX_VERSION_FRAME));
    }
    
    public void reloadImage()
    {
        config.reloadConfig();
        file = config.getConfig();
        
        File serverIcon = new File(plugin.getDataFolder(), file.getString(IMAGE + "." + IMAGE_PATH));
        if(!serverIcon.exists())
        {
            try {
                FileUtils.copyInputStreamToFile(plugin.getResource("server-icon.gif"), serverIcon);
            } catch (IOException ex) {
                Logger.getLogger(PingAnimationConfig.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        pingAnimation.setImagePath(file.getString(IMAGE + "." + IMAGE_PATH));
        pingAnimation.setImageAnimation(parseImageType(file.getString(IMAGE + "." + ANIMATION_TYPE)));
        pingAnimation.setImageInterval(bound(file.getInt(IMAGE + "." + INTERVAL), MIN_INTERVAL, MAX_INTERVAL));
        pingAnimation.setImageLoops(bound(file.getInt(IMAGE + "." + LOOPS), MIN_LOOPS, MAX_LOOPS));
    }
    
    public void reloadConfig()
    {
        reloadPlayers();
        reloadMotd();
        reloadVersion();
        reloadImage();
        config.reloadConfig();
        file = config.getConfig();
        
        pingAnimation.setLength(bound(file.getInt(ANIMATION_LENGTH), 0, Integer.MAX_VALUE));
        String guestName = file.getString(GUEST, "Guest");
        String world = file.getString(WORLD, "world");
        boolean includeStaff = file.getBoolean(INCLUDE_STAFF, true);
        
        ServerInfo.includeStaff = includeStaff;
        CurrentTag.world = world;
        PlayerAfterTag.guestName = guestName;
        PingListener.PING_INTERVAL = bound(file.getInt(PING_INTERVAL), 10, 1000);
    }
    
    public void savePlayers()
    {
        List<String> playerLines = new ArrayList<>();
        List<String> playerAnims = new ArrayList<>();
        List<Integer> playerIntervals = new ArrayList<>();
        List<Integer> playerLoops = new ArrayList<>();
        for(int i = 0; i < pingAnimation.getPlayers(); i++) {
            playerLines.add(pingAnimation.getPlayer(i));
            playerAnims.add(getAnimationSaveName(pingAnimation.getPlayerType(i)));
            playerIntervals.add(pingAnimation.getPlayerInterval(i));
            playerLoops.add(pingAnimation.getPlayerLoops(i));
        }
        
        file = config.getConfig();
        file.set(PLAYERS + "." + LINES, playerLines);
        file.set(PLAYERS + "." + ANIMATION_TYPES, playerAnims);
        file.set(PLAYERS + "." + INTERVALS, playerIntervals);
        file.set(PLAYERS + "." + LOOPS, playerLoops);
        file.set(PLAYERS + "." + FRAME_SIZE, pingAnimation.getPlayerFrameSize());
        config.saveConfig();
    }
    
    public void saveMotd()
    {
        List<String> motdLines = new ArrayList<>();
        List<String> motdAnims = new ArrayList<>();
        List<Integer> motdIntervals = new ArrayList<>();
        List<Integer> motdLoops = new ArrayList<>();
        for(int i = 0; i < pingAnimation.getMotdLength(); i++) {
            motdLines.add(pingAnimation.getMotdLine(i));
            motdAnims.add(getAnimationSaveName(pingAnimation.getMotdLineType(i)));
            motdIntervals.add(pingAnimation.getMotdInterval(i));
            motdLoops.add(pingAnimation.getMotdLoops(i));
        }
        
        file = config.getConfig();
        file.set(MOTD + "." + LINES, motdLines);
        file.set(MOTD + "." + ANIMATION_TYPES, motdAnims);
        file.set(MOTD + "." + INTERVALS, motdIntervals);
        file.set(MOTD + "." + LOOPS, motdLoops);
        file.set(MOTD + "." + FRAME_SIZE, pingAnimation.getMotdFrameSize());
        config.saveConfig();
    }
    
    public void saveVersion()
    {
        file = config.getConfig();
        file.set(VERSION + "." + LINE, pingAnimation.getVersionName());
        file.set(VERSION + "." + ANIMATION_TYPE, getAnimationSaveName(pingAnimation.getVersionType()));
        file.set(VERSION + "." + INTERVAL, pingAnimation.getVersionInterval());
        file.set(VERSION + "." + LOOPS, pingAnimation.getVersionLoops());
        file.set(VERSION + "." + FRAME_SIZE, pingAnimation.getVersionFrameSize());
        config.saveConfig();
    }
    
    public void saveImage()
    {
        file = config.getConfig();
        file.set(IMAGE + "." + IMAGE_PATH, pingAnimation.getImagePath());
        file.set(IMAGE + "." + ANIMATION_TYPE, getImageAnimationSaveName(pingAnimation.getImageAnimation()));
        file.set(IMAGE + "." + INTERVAL, pingAnimation.getImageInterval());
        file.set(IMAGE + "." + LOOPS, pingAnimation.getImageLoops());
        config.saveConfig();
    }
    
    public void saveToConfig()
    {
        savePlayers();
        saveMotd();
        saveVersion();
        saveImage();
        file.set(GUEST, PlayerAfterTag.guestName);
        file.set(WORLD, CurrentTag.world);
        file.set(INCLUDE_STAFF, ServerInfo.includeStaff);
        file = config.getConfig();
        file.set(ANIMATION_LENGTH, pingAnimation.getLength());
        file.set(PING_INTERVAL, PingListener.PING_INTERVAL);
        config.saveConfig();
    }
    
    public PingAnimationSave getPingAnimation()
    {
        return pingAnimation;
    }
    
    public static final ImageAnimationType parseImageType(String value)
    {
        if(value.equalsIgnoreCase(PNG_FILE))
            return ImageAnimationType.PNG;
        return ImageAnimationType.GIF;
    }
    
    public static final String getImageAnimationSaveName(ImageAnimationType anim)
    {
        switch(anim)
        {
            case PNG:
                return PNG_FILE;
            default:
                return GIF_FILE;
        }
    }
    
    public static final StringAnimationType parseType(String value)
    {
        if(value.equalsIgnoreCase(WRAPPED_FWD))
            return StringAnimationType.WRAP_SCROLL_FWD;
        else if(value.equalsIgnoreCase(WRAPPED_BCK))
            return StringAnimationType.WRAP_SCROLL_BCK;
        else if(value.equalsIgnoreCase(SCROLL_FWD))
            return StringAnimationType.SCROLL_FWD;
        else if(value.equalsIgnoreCase(SCROLL_BCK))
            return StringAnimationType.SCROLL_BCK;
        else if(value.equalsIgnoreCase(FRAME_BY_FRAME))
            return StringAnimationType.FRAME_BY_FRAME;
        return StringAnimationType.NONE;
    }
    
    public static final String getAnimationSaveName(StringAnimationType anim)
    {
        switch(anim)
        {
            case WRAP_SCROLL_FWD:
                return WRAPPED_FWD;
            case WRAP_SCROLL_BCK:
                return WRAPPED_BCK;
            case SCROLL_FWD:
                return SCROLL_FWD;
            case SCROLL_BCK:
                return SCROLL_BCK;
            case FRAME_BY_FRAME:
                return FRAME_BY_FRAME;
            default:
                return NO_ANIMATION;
        }
    }
    
    public static final List<Integer> boundArray(List<Integer> ints, int lower, int upper)
    {
        List<Integer> numbers = new ArrayList<>();
        for(Integer i : ints) {
            numbers.add(bound(i, lower, upper));
        }
        return numbers;
    }
    
    public static final int bound(int num, int lower, int upper)
    {
        if(num > upper)
            return upper;
        else if(num < lower)
            return lower;
        return num;
    }
    
    public static <T> List<T> resize(List<T> list, T fill, int length)
    {
        List<T> newList = new ArrayList<>();
        for(int i = 0; i < length; i++) {
            if(i < list.size()) {
                newList.add(list.get(i));
            }
            else {
                newList.add(fill);
            }
        }
        return newList;
    }
}
