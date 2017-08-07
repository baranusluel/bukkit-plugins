package com.barancode.mc.ddg.survival;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class DeathInventory {
	String player;
	ItemStack[] inv;
	Location loc;
	public DeathInventory(String player, ItemStack[] inv, Location loc){
		this.player = player; this.inv = inv; this.loc = loc;
	}
}
