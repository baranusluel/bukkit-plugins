package com.barancode.itemsandvisibility;

import org.bukkit.inventory.ItemStack;

public class CustomItem {
	ItemStack item;
	String command;
	int slot;
	boolean menu;
	public CustomItem(ItemStack item, String command, int slot, boolean menu){
		this.item = item;
		this.command = command;
		this.slot = slot;
		this.menu = menu;
	}
}
