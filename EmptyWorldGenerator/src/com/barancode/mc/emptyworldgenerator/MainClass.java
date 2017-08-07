package com.barancode.mc.emptyworldgenerator;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin{
	
	List<BedrockCoords> blocks = new ArrayList<BedrockCoords>();
	Generator generator = new Generator(this);
	
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return generator;
	}
	
	@Override
	public void onEnable(){
	    saveDefaultConfig();
	    try {
	        Metrics metrics = new Metrics(this);
	        metrics.start();
	    } catch (IOException e) {
	    	getLogger().severe("Metrics for EmtpyWorldGenerator are not working!");
	    }
	    
		if (getConfig().getBoolean("bedrock.enabled")){
			int xCord = getConfig().getInt("bedrock.x");
			int yCord = getConfig().getInt("bedrock.y");
			int zCord = getConfig().getInt("bedrock.z");
			int radius = Math.abs(getConfig().getInt("bedrock.radius"));
			if (radius == 0) blocks.add(new BedrockCoords(xCord, yCord, zCord));
			else {
				for (int x = -radius; x <= radius; x++){
					for (int z = -radius; z <= radius; z++){
						blocks.add(new BedrockCoords(xCord + x, yCord, zCord + z));
					}
				}
			}
		}
		
		getLogger().info("EmptyWorldGenerator has been enabled");
	}
}