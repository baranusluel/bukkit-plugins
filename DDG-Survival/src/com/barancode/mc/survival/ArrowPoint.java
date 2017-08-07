package com.barancode.mc.survival;

import java.util.UUID;

import org.bukkit.Location;

public class ArrowPoint {
	UUID player;
	String name;
	Location loc;
	public ArrowPoint(UUID player, String name, Location loc){
		this.player = player; this.name = name; this.loc = loc;
	}
}
