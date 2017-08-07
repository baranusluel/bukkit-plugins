package com.barancode.mc.bloodlines.managers;

import org.bukkit.entity.Player;

import com.barancode.mc.bloodlines.storage.Variables;

public class PlayerManager {
	public static void tpWorldSpawn(Player p){
		p.teleport(Variables.worldSpawn);
	}
}
