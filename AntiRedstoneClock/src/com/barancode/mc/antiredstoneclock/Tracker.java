package com.barancode.mc.antiredstoneclock;

public class Tracker {
	long last;
	int amount;
	public Tracker(){
		amount = 0;
		updateTime();
	}
	public void updateTime(){
		last = System.currentTimeMillis();
	}
	public void increaseAmount(){
		amount++;
	}
}
