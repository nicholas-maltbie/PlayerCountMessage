/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.flyingblock.pcm.commands;

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
public class ImgCommand extends FlyguyCommand
{
    public static final String[] HELP_PCM_MESSAGE1 = {
        "Player Count Message Vers Commands: [1]",
        "  &6/pcm img view   &4|&r views current image name",
        "  &6/pcm img anim   &4|&r views current image animation type",
        "  &6/pcm img loops  &4|&r views current image loops of animation",
        "  &6/pcm img inter  &4|&r views current image interval of animation",
        "  &6/pcm img reload &4|&r loads image from config",
        "  &6/pcm img save   &4|&r saves current image to config",
        "To view page 2, type &b/pcm img help 2"};
    
    public static final String[] HELP_PCM_MESSAGE2 = {
        "Player Count Message Vers Commands: [2]",
        "  &6/pcm img set name [text] &4|&r sets the image\'s text to [text]",
        "  &6/pcm img set anim [type] &4|&r sets the image\'s animation to [type]",
        "  &6/pcm img set inter [num] &4|&r sets the image\'s interval to num",
        "  &6/pcm img set loops [num] &4|&r sets the image\'s loops to num",
        "to view page 3, type &b/pcm img help 3"};
    
    public static final String[] HELP_PCM_MESSAGE3 = {
        "Player Count Message Vers Commands: [3]",
        "&6[text] &rcan include spaces; &6[num] &ris a number",
        "&6[type] &rthese correspond to config types:",
        "        &6\"gif\" &4= &rgif",
        "        &6\"png\" &4= &rpng",};
    
    public static final String[][] HELP_MESSAGES = {
            HELP_PCM_MESSAGE1,
            HELP_PCM_MESSAGE2,
            HELP_PCM_MESSAGE3,
        };
    
    public static final String[] arguments = {"view", "anim", "loops", "inter", "reload", "save", "set", "help",};

    private JavaPlugin plugin;
    private PingAnimationSave anim;
    private PingAnimationConfig config;

    public ImgCommand(Command command, JavaPlugin plugin, ArrayList<FlyguyCommand> subCommands, PingAnimationSave anim, PingAnimationConfig config)
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
            return chagneImage(sender, args);
        else if(args[0].equalsIgnoreCase("help"))
            return sendHelp(sender, args);
        else
        {
            sender.sendMessage("That isn't a registered command in PlayerCountMessage, try /pcm img help for help");
            return true;
        }
    }
    
    private static List<String> animations = new ArrayList<>(Arrays.asList(PingAnimationConfig.GIF_FILE, PingAnimationConfig.PNG_FILE));
    private static List<String> subCommands = new ArrayList<>(Arrays.asList("name", "anim", "inter", "loops"));
    
    private boolean chagneImage(CommandSender sender, String[] args)
    {
        if(args.length < 2)
            sender.sendMessage("You need to specify what you want to set " + subCommands.toString());
        else if(!subCommands.contains(args[1]))
            sender.sendMessage("That's not a valid item to edit");
        else if(args.length < 3)
            sender.sendMessage("You haven't specified a value to set");
        else
        {            
            if(args[1].equalsIgnoreCase("name"))
            {
                String add = "";
                for(int i = 2; i < args.length; i++)
                {
                    add = add.concat(args[i]);
                    if(i != args.length - 1)
                        add = add.concat(" ");
                }

                anim.setImagePath(add);

                anim.updateImage();
                sender.sendMessage("Changed the loaded image to \"" + add + "\" and updated the image");
            }
            else if(args[1].equalsIgnoreCase("anim"))
            {
                if(!animations.contains(args[2]))
                    sender.sendMessage("That's not a valid aniamtion type; " + animations.toString());
                else
                {
                    anim.setImageAnimation(PingAnimationConfig.parseImageType(args[2]));
                    sender.sendMessage("Changed the image's aniamtion to " + args[2] + " and updated image");
                }
                anim.updateImage();
            }
            else if(args[1].equalsIgnoreCase("inter"))
            {
                try {
                    int newInterval = PingAnimationConfig.bound(Integer.parseInt(args[2]), PingAnimationConfig.MIN_INTERVAL, PingAnimationConfig.MAX_INTERVAL);
                    anim.setImageInterval(newInterval);
                    sender.sendMessage("Changed the image's interval to " + Integer.toString(newInterval) + "; bounds are [" + PingAnimationConfig.MIN_INTERVAL + ", " + PingAnimationConfig.MAX_INTERVAL + "]");
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
        sender.sendMessage("Current image file name is: " + anim.getImagePath());
        return true;
    }
    
    private boolean viewAnimation(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current animation is: " + PingAnimationConfig.getImageAnimationSaveName(anim.getImageAnimation()));
        return true;
    }
    
    private boolean viewIntervals(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current interval is: " + anim.getImageInterval());
        return true;        
    }
    
    private boolean viewLoops(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current loops are: " + anim.getImageLoops());
        return true;        
    }
    
    private boolean loadFromConfig(CommandSender sender, String[] args)
    {
        config.reloadImage();
        sender.sendMessage("Reloaded the image data from config and image");
        anim.updateImage();
        return true;
    }
    
    private boolean saveConfig(CommandSender sender, String[] args)
    {
        config.saveImage();
        sender.sendMessage("Saved current image data to the config, view with /pcm img view");
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
