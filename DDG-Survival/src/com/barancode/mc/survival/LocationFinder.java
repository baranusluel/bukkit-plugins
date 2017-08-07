package com.barancode.mc.survival;

import java.util.Random;

import net.minecraft.server.v1_7_R4.ChunkCoordIntPair;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class LocationFinder {
	Main plugin;
	Random random = new Random();
	public LocationFinder(Main plugin){
		this.plugin = plugin;
	}
	
	public void findAndLoad(final Player p){
		final Location loc = getRandomLocation(p);
		final World world = loc.getWorld();
		int time = -1;
		final CraftPlayer ep = (CraftPlayer)p;
		for (int x = -10; x < 11; x++){
			for (int z = -10; z < 11; z++){
				if ( Math.abs(z) % 2 == 0 ) time++;
				final int finalx = x + ((int)loc.getX() / 16);
				final int finalz = z + ((int)loc.getZ() / 16);
				
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @SuppressWarnings("unchecked")
					@Override
		            public void run(){
		            	world.getChunkAt(finalx, finalz).load();
		            	ep.getHandle().chunkCoordIntPairQueue.add(new ChunkCoordIntPair(finalx, finalz));
		            }
		        }, time);
			}
		}
        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run(){
            	plugin.randomLocationReady.put(p.getName(), loc);
            }
        }, time + 10);
	}
	
	public Location getRandomLocation(Player p){
		Location loc;
		World world = p.getWorld();
		do {
			int x = random.nextInt(10000000) - 5000000;
			int z = random.nextInt(10000000) - 5000000;
			loc = new Location(world, x, 0, z);
			int y = world.getHighestBlockYAt(loc);
			loc.setY(y - 1);
		} while (world.getBlockAt(loc).getType() == Material.STATIONARY_WATER || world.getBlockAt(loc).getType() == Material.STATIONARY_LAVA);
		boolean b = false;
		Chunk c = loc.getChunk();
		for (Chunk tempc : plugin.db.getAllChunks()){
			if (Math.abs(c.getX() - tempc.getX()) < 20 && Math.abs(c.getZ() - tempc.getZ()) < 20) b = true;
		}
		if (b) {
			return getRandomLocation(p);
		}
		else {
			loc.setY(loc.getY() + 4);
			return loc;
		}
	}
}
