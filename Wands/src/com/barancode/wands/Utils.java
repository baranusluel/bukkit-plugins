package com.barancode.wands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Utils {
	public static String color(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static List<String> color(List<String> list){
		List<String> newlist = new ArrayList<String>();
		for (String s : list){
			newlist.add(color(s));
		}
		return newlist;
	}
	
	public static Block getTargetBlock(Player player, int range) {
		Location loc = player.getEyeLocation();
		Vector dir = loc.getDirection().normalize();
		for (int i = 0; i <= range; i++) {
			Block b = loc.add(dir).getBlock();
			if (b != null && b.getType() != Material.AIR){
				return b;
			}
		}
		return null;
	}
}
