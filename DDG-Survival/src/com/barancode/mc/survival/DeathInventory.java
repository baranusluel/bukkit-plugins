package com.barancode.mc.survival;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class DeathInventory {
	String player;
	ItemStack[] inv;
	ItemStack[] armorinv;
	Location loc;
	public DeathInventory(String player, ItemStack[] inv, ItemStack[] armorinv, Location loc){
		this.player = player; this.inv = inv; this.loc = loc; this.armorinv = armorinv;
	}
}
