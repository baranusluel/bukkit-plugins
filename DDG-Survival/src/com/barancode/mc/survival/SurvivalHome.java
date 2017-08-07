package com.barancode.mc.survival;

import java.util.UUID;

public class SurvivalHome {
	UUID player;
	int x;
	int y;
	int z;
	public SurvivalHome(UUID player, int x, int y, int z){
		this.player = player; this.x = x; this.y = y; this.z = z;
	}
}
