package com.flyingblock.pcm.commands;

import com.flyingblock.pcm.animation.PingAnimationSave;
import com.flyingblock.pcm.save.PingAnimationConfig;
import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

public class MyCommandHandler extends CommandHandler
{
    private static PingAnimationConfig config;
    private static PingAnimationSave anim;
    
    private PcmCommand pcm;
    private MotdCommand motd;
    private VersCommand vers;
    private ImgCommand img;
    private PlayCommand play;

    public static void setConfig(PingAnimationConfig config)
    {
        MyCommandHandler.config = config;
    }
    
    public static void setAnimation(PingAnimationSave anim)
    {
        MyCommandHandler.anim = anim;
    }
    
    public MyCommandHandler(JavaPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public void setupCommands(JavaPlugin plugin)
    {
        play = new PlayCommand(plugin.getCommand("play"), plugin, new ArrayList<FlyguyCommand>(), anim, config);
        motd = new MotdCommand(plugin.getCommand("motd"), plugin, new ArrayList<FlyguyCommand>(), anim, config);
        vers = new VersCommand(plugin.getCommand("vers"), plugin, new ArrayList<FlyguyCommand>(), anim, config);
        img = new ImgCommand(plugin.getCommand("img"), plugin, new ArrayList<FlyguyCommand>(), anim, config);
        ArrayList<FlyguyCommand> subCmds = new ArrayList<>();
        subCmds.add(play);
        subCmds.add(motd);
        subCmds.add(vers);
        subCmds.add(img);
        
        pcm = new PcmCommand(plugin.getCommand("pcm"), plugin, subCmds, anim, config);
        this.addCommand(pcm);
    }

}
