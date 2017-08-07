package com.barancode.mc.ddg.survival;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BoundaryEffect {
	
	  Main plugin;

	  public BoundaryEffect(Main plugin)
	  {
	    this.plugin = plugin;
	  }
	  
	public void show(Player p, Chunk c, List<Chunk> chunks){
		  if (Math.abs(p.getLocation().getChunk().getX() - c.getX()) > 1 || Math.abs(p.getLocation().getChunk().getZ() - c.getZ()) > 1){
			  return;
		  }
		  World world = c.getWorld();
		  Location loc;
		  Location sideloc;
		  for (int x = 0; x < 16; x++){
			  loc = c.getBlock(x, 0, 0).getLocation();
			  sideloc = loc; sideloc.setZ(sideloc.getZ() - 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
			      ParticleEffects.sendToPlayer(ParticleEffects.FIRE, p, loc, 0.5F, 0.5F, 0.5F, 0.0F, 5);
			  }
			  loc = c.getBlock(x, 0, 15).getLocation();
			  sideloc = loc; sideloc.setZ(sideloc.getZ() + 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
				  ParticleEffects.sendToPlayer(ParticleEffects.FIRE, p, loc, 0.5F, 0.5F, 0.5F, 0.0F, 5);
			  }
		  }
		  for (int z = 0; z < 16; z++){
			  loc = c.getBlock(0, 0, z).getLocation();
			  sideloc = loc; sideloc.setX(sideloc.getX() - 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
			      ParticleEffects.sendToPlayer(ParticleEffects.FIRE, p, loc, 0.5F, 0.5F, 0.5F, 0.0F, 5);
			  }
			  loc = c.getBlock(15, 0, z).getLocation();
			  sideloc = loc; sideloc.setX(sideloc.getX() + 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
			      ParticleEffects.sendToPlayer(ParticleEffects.FIRE, p, loc, 0.5F, 0.5F, 0.5F, 0.0F, 5);
			  }
		  }
	  }
	  
	  public void showOwn(Player p, Chunk c, List<Chunk> chunks){
		  if (Math.abs(p.getLocation().getChunk().getX() - c.getX()) > 1 || Math.abs(p.getLocation().getChunk().getZ() - c.getZ()) > 1){
			  return;
		  }
		  World world = c.getWorld();
		  Location loc;
		  Location sideloc;
		  for (int x = 0; x < 16; x++){
			  loc = c.getBlock(x, 0, 0).getLocation();
			  sideloc = loc; sideloc.setZ(sideloc.getZ() - 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
			      ParticleEffects.sendToPlayer(ParticleEffects.GREEN_SPARKLE, p, loc, 0.5F, 0.5F, 0.5F, 0.0F, 1);
			  }
			  loc = c.getBlock(x, 0, 15).getLocation();
			  sideloc = loc; sideloc.setZ(sideloc.getZ() + 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
				  ParticleEffects.sendToPlayer(ParticleEffects.GREEN_SPARKLE, p, loc, 0.5F, 0.5F, 0.5F, 0.0F, 1);
			  }
		  }
		  for (int z = 0; z < 16; z++){
			  loc = c.getBlock(0, 0, z).getLocation();
			  sideloc = loc; sideloc.setX(sideloc.getX() - 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
			      ParticleEffects.sendToPlayer(ParticleEffects.GREEN_SPARKLE, p, loc, 0.5F, 0.5F, 0.5F, 0.0F, 1);
			  }
			  loc = c.getBlock(15, 0, z).getLocation();
			  sideloc = loc; sideloc.setX(sideloc.getX() + 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
			      ParticleEffects.sendToPlayer(ParticleEffects.GREEN_SPARKLE, p, loc, 0.5F, 0.5F, 0.5F, 0.0F, 1);
			  }
		  }
	  }
	  
	  @SuppressWarnings("deprecation")
	public void start(){
	        plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
	            @Override
	            public void run(){
    				List<Chunk> chunks = plugin.db.getAllChunks();
	    			for (Player p : Bukkit.getOnlinePlayers()){
	    				List<Chunk> playerchunks = plugin.db.getChunks(p.getName());
	    				for (Chunk c : playerchunks){
	    					showOwn(p, c, playerchunks);
	    				}
	    				
	    				for (Chunk c : chunks){
	    					if (!playerchunks.contains(c)) show(p, c, plugin.db.getChunks(plugin.db.getOwner(c)));
	    				}
	    			}
	            }
	        }, 0L, 20L);
	  }
}
