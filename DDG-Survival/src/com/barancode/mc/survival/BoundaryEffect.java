package com.barancode.mc.survival;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
			      ParticleEffect.FLAME.display(loc, 0.5F, 0.5F, 0.5F, 0.0F, 5, p);
			  }
			  loc = c.getBlock(x, 0, 15).getLocation();
			  sideloc = loc; sideloc.setZ(sideloc.getZ() + 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
				  ParticleEffect.FLAME.display(loc, 0.5F, 0.5F, 0.5F, 0.0F, 5, p);
			  }
		  }
		  for (int z = 0; z < 16; z++){
			  loc = c.getBlock(0, 0, z).getLocation();
			  sideloc = loc; sideloc.setX(sideloc.getX() - 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
				  ParticleEffect.FLAME.display(loc, 0.5F, 0.5F, 0.5F, 0.0F, 5, p);
			  }
			  loc = c.getBlock(15, 0, z).getLocation();
			  sideloc = loc; sideloc.setX(sideloc.getX() + 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
				  ParticleEffect.FLAME.display(loc, 0.5F, 0.5F, 0.5F, 0.0F, 5, p);
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
				  ParticleEffect.HAPPY_VILLAGER.display(loc, 0.5F, 0.5F, 0.5F, 0.0F, 2, p);
			  }
			  loc = c.getBlock(x, 0, 15).getLocation();
			  sideloc = loc; sideloc.setZ(sideloc.getZ() + 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
				  ParticleEffect.HAPPY_VILLAGER.display(loc, 0.5F, 0.5F, 0.5F, 0.0F, 2, p);
			  }
		  }
		  for (int z = 0; z < 16; z++){
			  loc = c.getBlock(0, 0, z).getLocation();
			  sideloc = loc; sideloc.setX(sideloc.getX() - 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
				  ParticleEffect.HAPPY_VILLAGER.display(loc, 0.5F, 0.5F, 0.5F, 0.0F, 2, p);
			  }
			  loc = c.getBlock(15, 0, z).getLocation();
			  sideloc = loc; sideloc.setX(sideloc.getX() + 1);
			  if (!chunks.contains(sideloc.getChunk())){
				  loc.setY(world.getHighestBlockYAt(loc) + 1);
				  ParticleEffect.HAPPY_VILLAGER.display(loc, 0.5F, 0.5F, 0.5F, 0.0F, 2, p);
			  }
		  }
	  }
	  
	public void start(){
        plugin.scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run(){
				List<Chunk> chunks = plugin.db.getAllChunks();
				ConcurrentHashMap<Chunk, UUID> chunkowners = new ConcurrentHashMap<Chunk, UUID>();
				ConcurrentHashMap<UUID, List<Chunk>> playerchunks = new ConcurrentHashMap<UUID, List<Chunk>>();
				for (Chunk c : chunks){
					UUID owner = plugin.db.getOwner(c);
					chunkowners.put(c, owner);
					
					if (!playerchunks.containsKey(owner)){
						List<Chunk> tempchunks = new LinkedList<Chunk>();
						tempchunks.add(c);
						playerchunks.put(owner, tempchunks);
					} else {
						List<Chunk> tempchunks = playerchunks.get(owner);
						tempchunks.add(c);
						playerchunks.put(owner, tempchunks);
					}
				}
				
    			for (Player p : Bukkit.getOnlinePlayers()){
    				if (playerchunks.containsKey(p.getUniqueId())){
	    				List<Chunk> ownchunks = playerchunks.get(p.getUniqueId());
	    				for (Chunk c : ownchunks){
	    					showOwn(p, c, ownchunks);
	    				}
	    				
	    				for (Chunk c : chunks){
	    					if (!ownchunks.contains(c)) show(p, c, playerchunks.get(chunkowners.get(c)));
	    				}
    				} else {
	    				for (Chunk c : chunks){
	    					show(p, c, playerchunks.get(chunkowners.get(c)));
	    				}
    				}
    			}
            }
        }, 0L, 20L);
	  }
}
