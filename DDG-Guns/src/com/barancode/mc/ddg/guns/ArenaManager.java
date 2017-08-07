package com.barancode.mc.ddg.guns;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ArenaManager {
	  Main plugin;

	  public ArenaManager(Main plugin)
	  {
	    this.plugin = plugin;
	  }
	  
	  public void teleportToJoin(Player p){
		  String worldname = plugin.df.getCustomConfig().getString("joinspawn.world");
		  if (worldname == null) return;
          World w = Bukkit.getWorld(worldname);
          double x = plugin.df.getCustomConfig().getDouble("joinspawn.x");
          double y = plugin.df.getCustomConfig().getDouble("joinspawn.y");
          double z = plugin.df.getCustomConfig().getDouble("joinspawn.z");
          float yaw = (float)plugin.df.getCustomConfig().getDouble("joinspawn.yaw");
          float pitch = (float)plugin.df.getCustomConfig().getDouble("joinspawn.pitch");
          Location location = new Location(w, x, y, z, yaw, pitch);

          p.teleport(location);
	  }
}
