package com.barancode.mc.experia;

import org.bukkit.entity.Entity;

public class GiantHit {
	String player;
	int hits;
	Entity giant;
	public GiantHit(String player, int hits, Entity giant){
		this.player = player; this.hits = hits; this.giant = giant;
	}
}
