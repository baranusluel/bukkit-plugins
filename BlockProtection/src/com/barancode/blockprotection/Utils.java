package com.barancode.blockprotection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Utils {
	public static int getInt(String s) throws Exception{
		return Integer.parseInt(s);
	}
	
	public static Player closestPlayer(Location l){
		Player[] players = Bukkit.getOnlinePlayers();
		Player closest = players[0];
		double closestDist = closest.getLocation().distanceSquared(l);
		for (Player loop : players) {
			double i = loop.getLocation().distanceSquared(l);
		    if (i < closestDist) {
		        closestDist = i;
		        closest = loop;
		    }
		}
		return closest;
	}
}
