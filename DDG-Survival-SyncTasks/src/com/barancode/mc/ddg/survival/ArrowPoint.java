package com.barancode.mc.ddg.survival;

import org.bukkit.Location;

public class ArrowPoint {
	String player;
	String name;
	Location loc;
	public ArrowPoint(String player, String name, Location loc){
		this.player = player; this.name = name; this.loc = loc;
	}
}
