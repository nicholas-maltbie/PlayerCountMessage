package com.flyingblock.pcm.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

abstract public class FlyguyCommand
{
	ArrayList<FlyguyCommand> subCommands;
	JavaPlugin plugin;
	Command cmd;
	
	public FlyguyCommand(JavaPlugin plugin, Command command, ArrayList<FlyguyCommand> subCommands)
	{
		this.cmd = command;
		this.subCommands = subCommands;
		this.plugin = plugin;		
	}
	
	public void addSubCommand(FlyguyCommand cmd)
	{
		subCommands.add(cmd);
	}
	
	public FlyguyCommand(JavaPlugin plugin, String command, String permission, ArrayList<FlyguyCommand> subCommands, ArrayList<String> alises, String description, String permMessage,
			String label, String usage)
	{
		this.cmd = plugin.getCommand(command);
		cmd.setPermission(permission);
		cmd.setAliases(alises);
		cmd.setDescription(description);
		cmd.setPermissionMessage(permMessage);
		cmd.setLabel(label);
		cmd.setUsage(usage);
		
		this.plugin = plugin;
		this.subCommands = subCommands;
	}
	
	public Command getCommand()
	{
		return cmd;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase(this.cmd.getName()) && (cmd.getPermission() == null || sender.hasPermission(this.cmd.getPermission())))
		{
			for(FlyguyCommand subCmd : subCommands)
			{
				if(args.length > 0 && subCmd.getCommand().getName().equalsIgnoreCase(args[0]))
				{
					String[] newArgs = new String[args.length-1];
					for(int i = 0; i < newArgs.length; i++)
						newArgs[i] = args[i+1];
					return subCmd.onCommand(sender, plugin.getCommand(args[0]), label, newArgs);
				}
			}
			return doCommand(sender,cmd,label,args);			
		}
		return false;
	}
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
	{
		for(FlyguyCommand subCmd : subCommands)
		{
			String[] newArgs = new String[args.length-1];
			for(int i = 0; i < newArgs.length; i++)
				newArgs[i] = args[i+1];
			if(args[0].equalsIgnoreCase(subCmd.getCommand().getName()) && (subCmd.getCommand().getPermission() == null || sender.hasPermission(subCmd.getCommand().getPermission())))
				return subCmd.onTabComplete(sender, subCmd.getCommand(), label, newArgs);
		}
		ArrayList<String> tabs = new ArrayList<String>();
		for(FlyguyCommand subCmd : subCommands)
			if(subCmd.getCommand().getPermission() == null || sender.hasPermission(subCmd.getCommand().getPermission()))
				tabs.add(subCmd.getCommand().getName());
		for(String tab : getValidArgument(sender, cmd, label, args))
			tabs.add(tab);
		
		int i = 0;
		while(i < tabs.size())
		{
			String tab = tabs.get(i);
			if(args[args.length-1].length() > tab.length())
				tabs.remove(tab);
			else if(!tab.substring(0, args[args.length-1].length()).equalsIgnoreCase(args[args.length-1]))
				tabs.remove(tab);
			else
				i++;
		}
		
		return tabs;
	}
	
	abstract public List<String> getValidArgument(CommandSender sender, Command cmd, String label, String[] args);
	abstract public boolean doCommand(CommandSender sender, Command cmd, String label, String[] args);
}
