/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm;

import com.comphenix.protocol.ProtocolLibrary;
import com.flyingblock.pcm.PingListener;
import com.flyingblock.pcm.animation.PingAnimationSave;
import com.flyingblock.pcm.commands.MyCommandHandler;
import com.flyingblock.pcm.save.PingAnimationConfig;
import com.flyingblock.pcm.tags.after.PlayerInfoIP;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Nicholas
 */
public class PcmMain extends JavaPlugin
{
    private PingAnimationConfig config;
    private PingAnimationSave saveAnimation;
    private PingListener pingListener;
    private MyCommandHandler cmdHandler;
    
    @Override
    public void onEnable()
    {
        config = new PingAnimationConfig(this);
        saveAnimation = config.getPingAnimation();
        pingListener = new PingListener(this, saveAnimation);
        ProtocolLibrary.getProtocolManager().addPacketListener(pingListener);
        PlayerInfoIP.setup(this);
        MyCommandHandler.setAnimation(saveAnimation);
        MyCommandHandler.setConfig(config);
        cmdHandler = new MyCommandHandler(this);
    }
    
    @Override
    public void onDisable()
    {
        PlayerInfoIP.close();
        config.saveToConfig();
        cmdHandler.close(this);
        pingListener.unregister();
    }
    
}
