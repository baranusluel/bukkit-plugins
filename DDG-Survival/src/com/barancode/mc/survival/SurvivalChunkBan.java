package com.barancode.mc.survival;

import java.util.UUID;

public class SurvivalChunkBan {
	int x;
	int z;
	UUID banned;
	public SurvivalChunkBan(int x, int z, UUID banned){
		this.x = x; this.z = z; this.banned = banned;
	}
}
