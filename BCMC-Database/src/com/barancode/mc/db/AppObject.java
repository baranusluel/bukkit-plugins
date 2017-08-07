package com.barancode.mc.db;

import java.util.UUID;

public class AppObject {
	public UUID uuid;
	public String text;
	public long time;
	
	public AppObject(UUID uuid, String text, long time){
		this.uuid = uuid;
		this.text = text;
		this.time = time;
	}
}
