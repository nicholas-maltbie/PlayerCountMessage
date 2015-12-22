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
public class MotdCommand extends FlyguyCommand
{
    public static final String[] HELP_PCM_MESSAGE1 = {
        "Player Count Message Motd Commands: [1]",
        "  &6/pcm motd view   &4|&r views current motd text",
        "  &6/pcm motd anim   &4|&r views current motd animation type",
        "  &6/pcm motd loops  &4|&r views current motd loops of animation",
        "  &6/pcm motd inter  &4|&r views current motd interval of animation",
        "  &6/pcm motd frame  &4|&r views current frame size of motd",
        "  &6/pcm motd reload &4|&r loads motd from config",
        "  &6/pcm motd save   &4|&r saves current motd to config",
        "To view page 2, type &b/pcm motd help 2"};
    
    public static final String[] HELP_PCM_MESSAGE2 = {
        "Player Count Message Motd Commands: [2]",
        "  &6/pcm motd set [line#] text [text] &4|&r sets a line\'s text to [text]",
        "  &6/pcm motd set [line#] anim [type] &4|&r sets a line\'s animation to [type]",
        "  &6/pcm motd set [line#] inter [num] &4|&r sets a line\'s interval to num",
        "  &6/pcm motd set [line#] loops [num] &4|&r sets a line\'s loops to num",
        "  &6/pcm motd setSize [num]           &4|&r sets the frame size for the motd",
        "to view page 3, type &b/pcm motd help 3"};
    
    public static final String[] HELP_PCM_MESSAGE3 = {
        "Player Count Message Motd Commands: [3]",
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

    public MotdCommand(Command command, JavaPlugin plugin, ArrayList<FlyguyCommand> subCommands, PingAnimationSave anim, PingAnimationConfig config)
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
            sender.sendMessage("That isn't a registered command in PlayerCountMessage, try /pcm help for help");
            return true;
        }
    }

    private boolean viewFrame(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current motd frame size: " + anim.getMotdFrameSize());
        return true;
    }
    

    private boolean setFrameSize(CommandSender sender, String[] args)
    {
        if(args.length != 2)
            sender.sendMessage("You need to specify the new frame size");
        else
        {
            try {
                int size = PingAnimationConfig.bound(Integer.parseInt(args[1]), PingAnimationConfig.MIN_MOTD_FRAME, PingAnimationConfig.MAX_MOTD_FRAME);
                anim.setMotdFrameSize(size);
                sender.sendMessage("Set fame size to to " + Integer.toString(size) + "; bounds are [" + PingAnimationConfig.MIN_MOTD_FRAME + ", " + PingAnimationConfig.MAX_MOTD_FRAME + "]");
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
        if(args.length < 2 || !args[1].matches("[0-9]+"))
            sender.sendMessage("You need to specify which line to change");
        else if((Integer.parseInt(args[1]) - 1) < 0 || Integer.parseInt(args[1]) - 1 > anim.getMotdLength())
            sender.sendMessage("That's not a line, view the current player list with /pcm motd view");
        else if(args.length < 3)
            sender.sendMessage("You need to specify what you want to set " + subCommands.toString());
        else if(!subCommands.contains(args[2]))
            sender.sendMessage("That's not a valid item to edit");
        else if(args.length < 4)
            sender.sendMessage("You haven't specified a value to set");
        else
        {
            int num = Integer.parseInt(args[1]) - 1;
            String line = anim.getMotdLine(num);
            StringAnimationType type = anim.getMotdLineType(num);
            int interval = anim.getMotdInterval(num);
            int loops = anim.getMotdLoops(num);
            
            if(args[2].equalsIgnoreCase("text"))
            {
                String add = "";
                for(int i = 3; i < args.length; i++)
                {
                    add = add.concat(args[i]);
                    if(i != args.length - 1)
                        add = add.concat(" ");
                }

                anim.setMotdLine(add, type, interval, loops, num);

                sender.sendMessage("Changed Line " + (1+num) + "'s text to \"" + add + "\"");
            }
            else if(args[2].equalsIgnoreCase("anim"))
            {
                if(!PlayCommand.types.contains(args[3]))
                    sender.sendMessage("That's not a valid aniamtion type; " + PlayCommand.types.toString());
                else
                {
                    anim.setMotdLine(line, PlayCommand.getTypeFromString(args[3]), interval, loops, num);
                    sender.sendMessage("Changed Line " + (1+num) + "'s aniamtion to " + args[3]);
                }
            }
            else if(args[2].equalsIgnoreCase("inter"))
            {
                try {
                    int newInterval = PingAnimationConfig.bound(Integer.parseInt(args[3]), PingAnimationConfig.MIN_INTERVAL, PingAnimationConfig.MAX_INTERVAL);
                    anim.setMotdLine(line, type, newInterval, loops, num);
                    sender.sendMessage("Changed Line " + (1+num) + "'s interval to " + Integer.toString(newInterval) + "; bounds are [" + PingAnimationConfig.MIN_INTERVAL + ", " + PingAnimationConfig.MAX_INTERVAL + "]");
                }
                catch (NumberFormatException e) {
                    sender.sendMessage("the computer didn't like your interval, please try again");
                }
            }
            else if(args[2].equalsIgnoreCase("loops"))
            {
                try {
                    int newLoops = PingAnimationConfig.bound(Integer.parseInt(args[3]), PingAnimationConfig.MIN_LOOPS, PingAnimationConfig.MAX_LOOPS);
                    anim.setMotdLine(line, type, interval, newLoops, num);
                    sender.sendMessage("Changed Line " + (1+num) + "'s loops to " + Integer.toString(newLoops) + "; bounds are [" + PingAnimationConfig.MIN_LOOPS + ", " + PingAnimationConfig.MAX_LOOPS + "]");
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
        sender.sendMessage("Current motd is: ");
        for(int i = 0; i < anim.getMotdLength(); i++) {
            sender.sendMessage("Line " + (i+1) + ": " + anim.getMotdLine(i));
        }
        return true;
    }
    
    private boolean viewAnimation(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current animations are: ");
        for(int i = 0; i < anim.getMotdLength(); i++) {
            sender.sendMessage("Line " + (i+1) + ": " + PlayCommand.getStringOfType(anim.getMotdLineType(i)));
        }
        return true;
    }
    
    private boolean viewIntervals(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current intervals are: ");
        for(int i = 0; i < anim.getMotdLength(); i++) {
            sender.sendMessage("Line " + (i+1) + ": " + Integer.toString(anim.getMotdInterval(i)));
        }
        return true;        
    }
    
    private boolean viewLoops(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current loops are: ");
        for(int i = 0; i < anim.getMotdLength(); i++) {
            sender.sendMessage("Line " + (i+1) + ": " + Integer.toString(anim.getMotdLoops(i)));
        }
        return true;        
    }
    
    private boolean loadFromConfig(CommandSender sender, String[] args)
    {
        config.reloadMotd();
        sender.sendMessage("Reloaded the config, view with /pcm motd view");
        return true;
    }
    
    private boolean saveConfig(CommandSender sender, String[] args)
    {
        config.saveMotd();
        sender.sendMessage("Saved current motd to the config, view with /pcm motd view");
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
