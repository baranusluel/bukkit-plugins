package com.barancode.mc.survival;

import java.util.UUID;

public class SurvivalChunk {
	int x;
	int z;
	int time;
	UUID player;
	public SurvivalChunk(int x, int z, int time, UUID player){
		this.x = x; this.z = z; this.time = time; this.player = player;
	}
}
