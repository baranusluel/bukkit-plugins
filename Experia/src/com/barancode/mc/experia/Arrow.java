package com.barancode.mc.experia;

import org.bukkit.Location;

public class Arrow {
	
	  Main plugin;

	  public Arrow(Main plugin)
	  {
	    this.plugin = plugin;
	  }
	  
	  public static void indicate(Location loc){			
			for (int i = 2; i < 8; i++){
				ParticleEffect.EXPLODE.display(new Location(loc.getWorld(), loc.getX(), loc.getY() + i, loc.getZ()), 0.3F, 0F, 0.3F, 0.0F, 50);
			}
	  }
}
