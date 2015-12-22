package com.flyingblock.pcm.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

abstract public class CommandHandler implements TabExecutor
{
	private ArrayList<FlyguyCommand> commands = new ArrayList<FlyguyCommand>();
	
	public CommandHandler(JavaPlugin plugin)
	{
		setupCommands(plugin);
		setup(plugin);
	}
	
	abstract public void setupCommands(JavaPlugin plugin);
	
	public void addCommand(FlyguyCommand cmd)
	{
		commands.add(cmd);
	}
	
	public void close(JavaPlugin plugin)
	{
		for(FlyguyCommand flyCmd : commands)
		{
			plugin.getCommand(flyCmd.getCommand().getName()).setExecutor(null);
			plugin.getCommand(flyCmd.getCommand().getName()).setTabCompleter(null);
		}
	}
	
	public void setup(JavaPlugin plugin)
	{
		for(FlyguyCommand flyCmd : commands)
		{
			plugin.getCommand(flyCmd.getCommand().getName()).setExecutor(this);
			plugin.getCommand(flyCmd.getCommand().getName()).setTabCompleter(this);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		for(FlyguyCommand flyCmd : commands)
			if(flyCmd.getCommand().getName().equalsIgnoreCase(cmd.getName()))
				return flyCmd.onCommand(sender, cmd, label, args);
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) 
	{
		for(FlyguyCommand flyCmd : commands)
			if(flyCmd.getCommand().getName().equals(cmd.getName()))
				return flyCmd.onTabComplete(sender, cmd, label, args);
		return new ArrayList<String>();
	}
}
