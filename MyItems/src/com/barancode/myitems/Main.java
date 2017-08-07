package com.barancode.myitems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	Events events;
	Commands commands;
	FileConfiguration config;
	HashMap<String, MyItem> items = new HashMap<String, MyItem>();
	
	public void onEnable(){
		events = new Events(this);
		commands = new Commands(this);
		
		getServer().getPluginManager().registerEvents(events, this);
		getCommand("myitems").setExecutor(commands);
		
		if (!getDataFolder().exists()){
			try {
				Files.createDirectory(Paths.get(getDataFolder().getAbsolutePath()));
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		
		saveDefaultConfig();
		config = getConfig();
		for (String s : config.getConfigurationSection("items").getKeys(false))
			items.put(s, new MyItem(s, this));
	}
}
