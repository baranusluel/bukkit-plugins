package com.barancode.mc.bloodlines.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.barancode.mc.bloodlines.Main;

public class AdminCommands implements CommandExecutor{
	
	Main plugin;
	
	public AdminCommands(Main plugin){
		this.plugin = plugin;
	}
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("setspawn")){
			
		}
		return false;
	}
}
