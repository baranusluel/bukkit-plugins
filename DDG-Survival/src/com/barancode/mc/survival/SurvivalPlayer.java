package com.barancode.mc.survival;

import java.util.UUID;

public class SurvivalPlayer {
	int points;
	int power;
	UUID player;
	public SurvivalPlayer(int points, int power, UUID player){
		this.points = points; this.power = power; this.player = player;
	}
}
