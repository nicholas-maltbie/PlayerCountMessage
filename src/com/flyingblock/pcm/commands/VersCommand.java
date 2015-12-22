/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.commands;

import com.flyingblock.pcm.animation.Animation.StringAnimationType;
import com.flyingblock.pcm.animation.PingAnimationSave;
import com.flyingblock.pcm.save.PingAnimationConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Nick_Pro
 */
public class VersCommand extends FlyguyCommand
{
    public static final String[] HELP_PCM_MESSAGE1 = {
        "Player Count Message Vers Commands: [1]",
        "  &6/pcm vers view   &4|&r views current version text",
        "  &6/pcm vers anim   &4|&r views current version animation type",
        "  &6/pcm vers loops  &4|&r views current version loops of animation",
        "  &6/pcm vers inter  &4|&r views current version interval of animation",
        "  &6/pcm vers frame  &4|&r views current version size of vers",
        "  &6/pcm vers reload &4|&r loads vers from config",
        "  &6/pcm vers save   &4|&r saves current vers to config",
        "To view page 2, type &b/pcm vers help 2"};
    
    public static final String[] HELP_PCM_MESSAGE2 = {
        "Player Count Message Vers Commands: [2]",
        "  &6/pcm vers set text [text] &4|&r sets the verion\'s text to [text]",
        "  &6/pcm vers set anim [type] &4|&r sets the verion\'s animation to [type]",
        "  &6/pcm vers set inter [num] &4|&r sets the verion\'s interval to num",
        "  &6/pcm vers set loops [num] &4|&r sets the verion\'s loops to num",
        "  &6/pcm vers setSize [num]           &4|&r sets the frame size for the vers",
        "to view page 3, type &b/pcm vers help 3"};
    
    public static final String[] HELP_PCM_MESSAGE3 = {
        "Player Count Message Vers Commands: [3]",
        "&6[text] &rcan include spaces; &6[num] &ris a number",
        "&6[type] &rthese correspond to config types:",
        "        &6\"wrap_fwd\"   &4= &rwrap forward",
        "        &6\"wrap_bck\"   &4= &rwrap back",
        "        &6\"scroll_fwd\" &4= &rscroll forward",
        "        &6\"scroll_bck\" &4= &rscroll back",
        "        &6\"frames\"     &4= &rframes",
        "        &6\"none\"       &4= &rnone"};
    
    public static final String[][] HELP_MESSAGES = {
            HELP_PCM_MESSAGE1,
            HELP_PCM_MESSAGE2,
            HELP_PCM_MESSAGE3,
        };
    
    public static final String[] arguments = {"view", "anim", "loops", "inter", "reload", "save", "set", "help", "setSize", "frame"};

    private JavaPlugin plugin;
    private PingAnimationSave anim;
    private PingAnimationConfig config;

    public VersCommand(Command command, JavaPlugin plugin, ArrayList<FlyguyCommand> subCommands, PingAnimationSave anim, PingAnimationConfig config)
    {
        super(plugin, command, subCommands);
        this.config = config;
        this.anim = anim;
    }

    @Override
    public boolean doCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(args.length == 0)
        {
            sender.sendMessage(cmd.getDescription() + " Usage: " + cmd.getUsage());
            return true;
        }
        else if(args[0].equalsIgnoreCase("view"))
            return viewMessage(sender, args);
        else if(args[0].equalsIgnoreCase("anim"))
            return viewAnimation(sender, args);
        else if(args[0].equalsIgnoreCase("loops"))
            return viewLoops(sender, args);
        else if(args[0].equalsIgnoreCase("inter"))
            return viewIntervals(sender, args);
        else if(args[0].equalsIgnoreCase("reload"))
            return loadFromConfig(sender, args);
        else if(args[0].equalsIgnoreCase("save"))
            return saveConfig(sender, args);
        else if(args[0].equalsIgnoreCase("set"))
            return changeLine(sender, args);
        else if(args[0].equalsIgnoreCase("help"))
            return sendHelp(sender, args);
        else if(args[0].equalsIgnoreCase("frame"))
            return viewFrame(sender, args);
        else if(args[0].equalsIgnoreCase("setSize"))
            return setFrameSize(sender, args);
        else
        {
            sender.sendMessage("That isn't a registered command in PlayerCountMessage, try /pcm vers help for help");
            return true;
        }
    }

    private boolean viewFrame(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current version frame size: " + anim.getVersionFrameSize());
        return true;
    }
    

    private boolean setFrameSize(CommandSender sender, String[] args)
    {
        if(args.length != 2)
            sender.sendMessage("You need to specify the new frame size");
        else
        {
            try {
                int size = PingAnimationConfig.bound(Integer.parseInt(args[1]), PingAnimationConfig.MIN_VERSION_FRAME, PingAnimationConfig.MAX_VERSION_FRAME);
                anim.setVersionFrameSize(size);
                sender.sendMessage("Set fame size to to " + Integer.toString(size) + "; bounds are [" + PingAnimationConfig.MIN_VERSION_FRAME + ", " + PingAnimationConfig.MAX_VERSION_FRAME + "]");
            }
            catch (NumberFormatException e) {
                sender.sendMessage("the computer didn't like your number, please try again");
            }
        }
        return true;
    }
    
    private static List<String> subCommands = new ArrayList<>(Arrays.asList("text", "anim", "inter", "loops"));
    
    private boolean changeLine(CommandSender sender, String[] args)
    {
        if(args.length < 2)
            sender.sendMessage("You need to specify what you want to set " + subCommands.toString());
        else if(!subCommands.contains(args[1]))
            sender.sendMessage("That's not a valid item to edit");
        else if(args.length < 3)
            sender.sendMessage("You haven't specified a value to set");
        else
        {            
            if(args[1].equalsIgnoreCase("text"))
            {
                String add = "";
                for(int i = 2; i < args.length; i++)
                {
                    add = add.concat(args[i]);
                    if(i != args.length - 1)
                        add = add.concat(" ");
                }

                anim.setVersionName(add);

                sender.sendMessage("Changed the verion text to \"" + add + "\"");
            }
            else if(args[1].equalsIgnoreCase("anim"))
            {
                if(!PlayCommand.types.contains(args[2]))
                    sender.sendMessage("That's not a valid aniamtion type; " + PlayCommand.types.toString());
                else
                {
                    anim.setVersionType(PlayCommand.getTypeFromString(args[2]));
                    sender.sendMessage("Changed the version's aniamtion to " + args[2]);
                }
            }
            else if(args[1].equalsIgnoreCase("inter"))
            {
                try {
                    int newInterval = PingAnimationConfig.bound(Integer.parseInt(args[2]), PingAnimationConfig.MIN_INTERVAL, PingAnimationConfig.MAX_INTERVAL);
                    anim.setVersionInterval(newInterval);
                    sender.sendMessage("Changed the version's interval to " + Integer.toString(newInterval) + "; bounds are [" + PingAnimationConfig.MIN_INTERVAL + ", " + PingAnimationConfig.MAX_INTERVAL + "]");
                }
                catch (NumberFormatException e) {
                    sender.sendMessage("the computer didn't like your interval, please try again");
                }
            }
            else if(args[1].equalsIgnoreCase("loops"))
            {
                
                try {
                    int newLoops = PingAnimationConfig.bound(Integer.parseInt(args[2]), PingAnimationConfig.MIN_LOOPS, PingAnimationConfig.MAX_LOOPS);
                    anim.setVersionLoops(newLoops);
                    sender.sendMessage("Changed the version's loops to " + Integer.toString(newLoops) + "; bounds are [" + PingAnimationConfig.MIN_LOOPS + ", " + PingAnimationConfig.MAX_LOOPS + "]");
                }
                catch (NumberFormatException e) {
                    sender.sendMessage("the computer didn't like your loops, please try again");
                }
            }
        }
        return true;
    }

    private boolean viewMessage(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current text is: " + anim.getVersionName());
        return true;
    }
    
    private boolean viewAnimation(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current animation is: " + PlayCommand.getStringOfType(anim.getVersionType()));
        return true;
    }
    
    private boolean viewIntervals(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current interval is: " + anim.getVersionInterval());
        return true;        
    }
    
    private boolean viewLoops(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current loops are: " + anim.getVersionLoops());
        return true;        
    }
    
    private boolean loadFromConfig(CommandSender sender, String[] args)
    {
        config.reloadVersion();
        sender.sendMessage("Reloaded the config, view with /pcm vers view");
        return true;
    }
    
    private boolean saveConfig(CommandSender sender, String[] args)
    {
        config.saveVersion();
        sender.sendMessage("Saved current version to the config, view with /pcm vers view");
        return true;
    }

    private boolean sendHelp(CommandSender sender, String[] args)
    {
        if(args.length == 1)
            for(String line : HELP_MESSAGES[0])
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        else if(args.length == 2)
        {
            try {
                int lineNum = Integer.parseInt(args[1]);
                for(String line : HELP_MESSAGES[lineNum-1])
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
            catch (NumberFormatException e) {
                sender.sendMessage("the computer didn't like your number, please try again");
            }
            catch (IndexOutOfBoundsException e) {
                sender.sendMessage("that help page (" + args[1] + ") doesn't exist");
            }
        }
        return true;
    }

    @Override
    public List<String> getValidArgument(CommandSender sender, Command cmd, String label, String[] args) 
    {
        if(args.length < 2)
            return Arrays.asList(arguments);
        return new ArrayList<>();
    }
}
