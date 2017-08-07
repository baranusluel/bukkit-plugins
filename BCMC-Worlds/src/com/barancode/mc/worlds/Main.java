package com.barancode.mc.worlds;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public void onEnable(){
		saveDefaultConfig();
		for (String s : getConfig().getConfigurationSection("worlds").getKeys(false)){
			WorldCreator wc = new WorldCreator(s);
			String generator = getConfig().getString("worlds." + s + ".generator");
			boolean flat = getConfig().getBoolean("worlds." + s + ".flat");
			if (generator != null) wc.generator(generator);
			if (flat) wc.type(WorldType.FLAT);
			wc.createWorld();
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 2 && args[0].equalsIgnoreCase("tp")){
			((Player)sender).teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
		}
		return true;
	}
}
