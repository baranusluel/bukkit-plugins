package com.barancode.mc.bloodlines.storage;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.barancode.mc.bloodlines.Main;
import com.barancode.mc.bloodlines.tools.Utils;

public class Variables {
	public static Location worldSpawn = null;
	public static String prefix = null;
	
	public static void loadVars(){
		worldSpawn = new Location(Bukkit.getWorld("world"), Main.config.getDouble("world-spawn.x"), Main.config.getDouble("world-spawn.y"), Main.config.getDouble("world-spawn.z"));
		prefix = Utils.colorize(Main.config.getString("strings.prefix"));
	}
	
	public static void setWorldSpawn(Location loc){
		worldSpawn = loc;
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		Main.config.set("world-spawn.x", x);
		Main.config.set("world-spawn.y", y);
		Main.config.set("world-spawn.z", z);
		
	}
}
