package com.barancode.mc.survival;

import java.util.UUID;

public class SurvivalChunkMute {
	int x;
	int z;
	UUID muted;
	public SurvivalChunkMute(int x, int z, UUID muted){
		this.x = x; this.z = z; this.muted = muted;
	}
}
