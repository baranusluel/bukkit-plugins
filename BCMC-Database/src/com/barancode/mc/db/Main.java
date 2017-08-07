package com.barancode.mc.db;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	static GlobalDatabase db = new GlobalDatabase();
	
	public void onEnable(){
		db.initialize();
	}
	
	public void onDisable(){
		db.quit();
	}
}