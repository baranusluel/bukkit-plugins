package com.barancode.mc.emptyworldgenerator;


import java.util.Iterator;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class Generator extends ChunkGenerator{
	MainClass plugin;
	
	public Generator(MainClass plugin){
		this.plugin = plugin;
	}
	
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid){
		byte[][] result = new byte[world.getMaxHeight() / 16][];
		
		Iterator<BedrockCoords> it = plugin.blocks.iterator();
		while (it.hasNext()){
			BedrockCoords block = it.next();
			if (block.x >= chunkX * 16 && block.x < (chunkX + 1) * 16 && block.z >= chunkZ * 16 && block.z < (chunkZ + 1) * 16){
				int x = block.x % 16;
				if (x < 0) x += 16;
				int z = block.z % 16;
				if (z < 0) z += 16;
				setBlock(result, x, block.y, z, (byte)7);
				it.remove();
			}
		}
		
		return result;
	}
	
    public Location getFixedSpawnLocation(World world, Random random) {
    	return new Location(world, plugin.getConfig().getInt("bedrock.x"), plugin.getConfig().getInt("bedrock.y") + 1, plugin.getConfig().getInt("bedrock.z"));
    }
    
    private void setBlock(byte[][] result, int x, int y, int z, byte blkid) {
        if (result[y >> 4] == null) {
          result[y >> 4] = new byte[4096];
        }
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
    }
}

