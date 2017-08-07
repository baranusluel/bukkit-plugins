package com.barancode.mc.bloodlines;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.barancode.mc.bloodlines.commands.AdminCommands;
import com.barancode.mc.bloodlines.events.PlayerEvents;
import com.barancode.mc.bloodlines.items.ClassSelector;
import com.barancode.mc.bloodlines.storage.Variables;

public class Main extends JavaPlugin{
	
	public static FileConfiguration config = null;
	public static BukkitScheduler scheduler = null;
	public static Logger log = null;
	
	AdminCommands admincommands = new AdminCommands(this);
	PlayerEvents playerevents = new PlayerEvents(this);
	
	public void onEnable(){
		saveDefaultConfig();
		config = getConfig();
		scheduler = Bukkit.getScheduler();
		log = getLogger();
		Variables.loadVars();
		setupCommands();
		setupItems();
		getServer().getPluginManager().registerEvents(playerevents, this);
	}
	
	public void onDisable(){
		saveConfig();
		scheduler.cancelAllTasks();
	}
	
	public void setupCommands(){
	    getCommand("setspawn").setExecutor(admincommands);
	}
	
	public void setupItems(){
		ClassSelector.initialize();
	}
}
