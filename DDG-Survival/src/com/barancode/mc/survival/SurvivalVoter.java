package com.barancode.mc.survival;

import java.util.UUID;

import org.bukkit.Chunk;

public class SurvivalVoter {
	Chunk c;
	UUID p;
	public SurvivalVoter(Chunk c, UUID p){
		this.c = c; this.p = p;
	}
}
