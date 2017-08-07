package com.barancode.blockprotection.io;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.barancode.blockprotection.Main;

public class PlayerList {
	
	Main plugin;
	public List<UUID> players = new LinkedList<UUID>();
	
	public PlayerList(Main plugin){
		this.plugin = plugin;
	}
    
    public void write(UUID player){
    	try {
    		if (!players.contains(player)){
	    		File saveTo = new File(plugin.getDataFolder(), "playerlist.txt");
	    		saveTo.createNewFile();
	    		
	    		FileWriter fw = new FileWriter(saveTo, true);
	    		PrintWriter pw = new PrintWriter(fw);
	    		
	    		players.add(player);
	    		pw.println(player.toString());
	    		
	    		pw.close();
	    		fw.close();
    		}
    	} catch (IOException e){
    		e.printStackTrace();
    	}
    }
    public void populate(){
    	try {
    		File readFrom = new File(plugin.getDataFolder(), "playerlist.txt");
    		if (!readFrom.exists()){
    			return;
    		}
    		FileReader fr = new FileReader(readFrom);
    		BufferedReader reader = new BufferedReader(fr);
    		
    		String line;
    		
    		while ((line = reader.readLine()) != null){
    			players.add(UUID.fromString(line));
    		}
    		reader.close();
    		fr.close();
    	} catch (IOException e){
    		e.printStackTrace();
    	}
    }
}
