package main.java.com.barancode.choiceinvis;

import java.util.UUID;

public class PendingInvis {
	int timeLeft = 0;
	long start = 0;
	UUID uuid = null;
	public PendingInvis(int timeLeft, long start, UUID uuid){
		this.timeLeft = timeLeft;
		this.start = start;
		this.uuid = uuid;
	}
}
