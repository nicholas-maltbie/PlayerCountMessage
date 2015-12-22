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
public class PlayCommand extends FlyguyCommand
{
    public static final String[] HELP_PCM_MESSAGE1 = {
        "Player Count Message Play Commands: [1]",
        "  &6/pcm play view   &4|&r views current player list text",
        "  &6/pcm play anim   &4|&r views current player list animation type",
        "  &6/pcm play loops  &4|&r views current player list loops of animation",
        "  &6/pcm play inter  &4|&r views current player list interval of animation",
        "  &6/pcm play frame  &4|&r views current player list frame size",
        "  &6/pcm play reload &4|&r loads player list from config",
        "  &6/pcm play save   &4|&r saves current player list to config",
        "To view page 2, type &b/pcm play help 2"};
    
    public static final String[] HELP_PCM_MESSAGE2 = {
        "Player Count Message Play Commands: [2]",
        "  &6/pcm play add [text]              &4|&r adds a line with text to current player list",
        "  &6/pcm play delete [line#]          &4|&r deletes a line, starts at line 1",
        "  &6/pcm play insert [line#] [text]   &4|&r adds a line after line# set to [text]",
        "  &6/pcm play set [line#] text [text] &4|&r sets a line\'s text to [text]",
        "  &6/pcm play set [line#] anim [type] &4|&r sets a line\'s animation to [type]",
        "  &6/pcm play set [line#] inter [num] &4|&r sets a line\'s interval to num",
        "  &6/pcm play set [line#] loops [num] &4|&r sets a line\'s loops to num",
        "  &6/pcm play setSize [num]           &4|&r sets the frame size for the player list",
        "to view page 3, type &b/pcm play help 3"};
    
    public static final String[] HELP_PCM_MESSAGE3 = {
        "Player Count Message Play Commands: [3]",
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
    
    public static final String WRAP_FORWARD = "wrap_fwd";
    public static final String WRAP_BACKWARD = "wrap_bck";
    public static final String SCROLL_FORWARD = "scroll_fwd";
    public static final String SCROLL_BACKWARD = "scroll_bck";
    public static final String FRAMES = "frames";
    public static final String NONE = "none";
    
    public static final List<String> types = Arrays.asList(WRAP_FORWARD, WRAP_BACKWARD, SCROLL_FORWARD, SCROLL_BACKWARD, FRAMES, NONE);
    
    public static String getStringOfType(StringAnimationType type)
    {
        switch(type)
        {
            case SCROLL_FWD:
                return SCROLL_FORWARD;
            case SCROLL_BCK:
                return SCROLL_BACKWARD;
            case WRAP_SCROLL_FWD:
                return WRAP_FORWARD;
            case WRAP_SCROLL_BCK:
                return WRAP_BACKWARD;
            case FRAME_BY_FRAME:
                return FRAMES;
            default:
                return NONE;
        }
    }
    
    public static StringAnimationType getTypeFromString(String type)
    {
        if(type.equalsIgnoreCase(WRAP_FORWARD))
            return StringAnimationType.WRAP_SCROLL_FWD;
        else if(type.equalsIgnoreCase(WRAP_BACKWARD))
            return StringAnimationType.WRAP_SCROLL_BCK;
        else if(type.equalsIgnoreCase(SCROLL_FORWARD))
            return StringAnimationType.SCROLL_FWD;
        else if(type.equalsIgnoreCase(SCROLL_BACKWARD))
            return StringAnimationType.SCROLL_BCK;
        else if(type.equalsIgnoreCase(FRAMES))
            return StringAnimationType.FRAME_BY_FRAME;
        return StringAnimationType.NONE;
    }
    
    public static final String[] arguments = {"view", "anim", "loops", "inter", "reload", "save", "delete", "add", "set", "help", "insert", "setSize", "frame"};

    private JavaPlugin plugin;
    private PingAnimationSave anim;
    private PingAnimationConfig config;

    public PlayCommand(Command command, JavaPlugin plugin, ArrayList<FlyguyCommand> subCommands, PingAnimationSave anim, PingAnimationConfig config)
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
        else if(args[0].equalsIgnoreCase("delete"))
            return deleteLine(sender, args);
        else if(args[0].equalsIgnoreCase("add"))
            return addLine(sender, args);
        else if(args[0].equalsIgnoreCase("set"))
            return changeLine(sender, args);
        else if(args[0].equalsIgnoreCase("help"))
            return sendHelp(sender, args);
        else if(args[0].equalsIgnoreCase("insert"))
            return insertLine(sender, args);
        else if(args[0].equalsIgnoreCase("setSize"))
            return setFrameSize(sender, args);
        else if(args[0].equalsIgnoreCase("frame"))
            return viewFrameSize(sender, args);
        else
        {
            sender.sendMessage("That isn't a registered command in PlayerCountMessage, try /pcm play help for help");
            return true;
        }
    }
    
    private boolean viewFrameSize(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current frame size: " + anim.getPlayerFrameSize());
        return true;
    }

    private boolean setFrameSize(CommandSender sender, String[] args)
    {
        if(args.length != 2)
            sender.sendMessage("You need to specify the new frame size");
        else
        {
            try {
                int size = PingAnimationConfig.bound(Integer.parseInt(args[1]), PingAnimationConfig.MIN_PLAYER_FRAME, PingAnimationConfig.MAX_PLAYER_FRAME);
                anim.setPlayerFrameSize(size);
                sender.sendMessage("Set fame size to to " + Integer.toString(size) + "; bounds are [" + PingAnimationConfig.MIN_PLAYER_FRAME + ", " + PingAnimationConfig.MAX_PLAYER_FRAME + "]");
            }
            catch (NumberFormatException e) {
                sender.sendMessage("the computer didn't like your number, please try again");
            }
        }
        return true;
    }
    
    private boolean deleteLine(CommandSender sender, String[] args)
    {
        if(args.length != 2 || !args[1].matches("[0-9]+"))
            sender.sendMessage("You need to specify which line to delete");
        else if((Integer.parseInt(args[1]) - 1) < 0 || (Integer.parseInt(args[1]) - 1) > anim.getPlayers())
            sender.sendMessage("That's not a line, view the current player list with /pcm play view");
        else
        {
            sender.sendMessage("Removed line " + (args[1]) + ": " + anim.getPlayer(Integer.parseInt(args[1]) - 1));
            anim.removePlayer(Integer.parseInt(args[1]) - 1);
        }
        return true;
    }

    private boolean addLine(CommandSender sender, String[] args)
    {
        String add = "";
        for(int i = 1; i < args.length; i++)
        {
            add = add.concat(args[i]);
            if(i != args.length - 1)
                add = add.concat(" ");
        }
        anim.addPlayer(add, PingAnimationConfig.STRING_DEFAULT_ANIMATION, PingAnimationConfig.DEFAULT_INTERVAL, PingAnimationConfig.DEFAULT_LOOPS);
        sender.sendMessage("Added \"" + add + "\"");
        return true;
    }

    private boolean insertLine(CommandSender sender, String[] args)
    {
        String add = "";
        if(args.length < 2 || !args[1].matches("[0-9]+"))
            sender.sendMessage("You need to specify which line to insert after");
        else if((Integer.parseInt(args[1]) - 1) < 0 || (Integer.parseInt(args[1])) >= anim.getPlayers())
            sender.sendMessage("That's not a line, view the current player list with /pcm play view");
        else
        {
            for(int i = 2; i < args.length; i++)
            {
                add = add.concat(args[i]);
                if(i != args.length - 1)
                    add = add.concat(" ");
            }
            anim.insertPlayer(add, PingAnimationConfig.STRING_DEFAULT_ANIMATION, Integer.parseInt(args[1]), PingAnimationConfig.DEFAULT_INTERVAL, PingAnimationConfig.DEFAULT_LOOPS);
            sender.sendMessage("inserted \"" + add + "\" at line " + (args[1]));
        }
        return true;
    }

    private static List<String> subCommands = new ArrayList<>(Arrays.asList("text", "anim", "inter", "loops"));
    
    private boolean changeLine(CommandSender sender, String[] args)
    {
        if(args.length < 2 || !args[1].matches("[0-9]+"))
            sender.sendMessage("You need to specify which line to change");
        else if((Integer.parseInt(args[1]) - 1) < 0 || Integer.parseInt(args[1]) - 1 > anim.getPlayers())
            sender.sendMessage("That's not a line, view the current player list with /pcm play view");
        else if(args.length < 3)
            sender.sendMessage("You need to specify what you want to set " + subCommands.toString());
        else if(!subCommands.contains(args[2]))
            sender.sendMessage("That's not a valid item to edit");
        else if(args.length < 4)
            sender.sendMessage("You haven't specified a value to set");
        else
        {
            int num = Integer.parseInt(args[1]) - 1;
            String line = anim.getPlayer(num);
            StringAnimationType type = anim.getPlayerType(num);
            int interval = anim.getPlayerInterval(num);
            int loops = anim.getPlayerLoops(num);
            
            if(args[2].equalsIgnoreCase("text"))
            {
                String add = "";
                for(int i = 3; i < args.length; i++)
                {
                    add = add.concat(args[i]);
                    if(i != args.length - 1)
                        add = add.concat(" ");
                }

                anim.insertPlayer(add, type, num, interval, loops);
                anim.removePlayer(Integer.parseInt(args[1]));

                sender.sendMessage("Changed Line " + (1+num) + "'s text to \"" + add + "\"");
            }
            else if(args[2].equalsIgnoreCase("anim"))
            {
                if(!types.contains(args[3]))
                    sender.sendMessage("That's not a valid aniamtion type; " + types.toString());
                else
                {
                    anim.setPlayer(line, getTypeFromString(args[3]), num, interval, loops);
                    sender.sendMessage("Changed Line " + (1+num) + "'s aniamtion to " + args[3]);
                }
            }
            else if(args[2].equalsIgnoreCase("inter"))
            {
                try {
                    int newInterval = PingAnimationConfig.bound(Integer.parseInt(args[3]), PingAnimationConfig.MIN_INTERVAL, PingAnimationConfig.MAX_INTERVAL);
                    anim.setPlayer(line, type, num, newInterval, loops);
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
                    anim.setPlayer(line, type, num, interval, newLoops);
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
        sender.sendMessage("Current player list is: ");
        for(int i = 0; i < anim.getPlayers(); i++) {
            sender.sendMessage("Line " + (i+1) + ": " + anim.getPlayer(i));
        }
        return true;
    }
    
    private boolean viewAnimation(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current animations are: ");
        for(int i = 0; i < anim.getPlayers(); i++) {
            sender.sendMessage("Line " + (i+1) + ": " + PlayCommand.getStringOfType(anim.getPlayerType(i)));
        }
        return true;
    }
    
    private boolean viewIntervals(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current intervals are: ");
        for(int i = 0; i < anim.getPlayers(); i++) {
            sender.sendMessage("Line " + (i+1) + ": " + Integer.toString(anim.getPlayerInterval(i)));
        }
        return true;        
    }
    
    private boolean viewLoops(CommandSender sender, String[] args)
    {
        sender.sendMessage("Current loops are: ");
        for(int i = 0; i < anim.getPlayers(); i++) {
            sender.sendMessage("Line " + (i+1) + ": " + Integer.toString(anim.getPlayerLoops(i)));
        }
        return true;        
    }
    
    private boolean loadFromConfig(CommandSender sender, String[] args)
    {
        config.reloadPlayers();
        sender.sendMessage("Reloaded the config, view with /pcm play view");
        return true;
    }
    
    private boolean saveConfig(CommandSender sender, String[] args)
    {
        config.savePlayers();
        sender.sendMessage("Saved current player list to the config, view with /pcm play view");
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
