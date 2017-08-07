package com.barancode.mc.assistant;

public class Main {

	public static void main(String[] args) {
		if (args[0].equalsIgnoreCase("configurevotifier")){
			try {
				new ConfigureVotifier();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

}
