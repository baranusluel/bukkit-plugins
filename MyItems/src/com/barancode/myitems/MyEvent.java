package com.barancode.myitems;

public class MyEvent {
	String command;
	boolean cancel;
	
	public MyEvent(String command, boolean cancel){
		this.command = command;
		this.cancel = cancel;
	}
}
