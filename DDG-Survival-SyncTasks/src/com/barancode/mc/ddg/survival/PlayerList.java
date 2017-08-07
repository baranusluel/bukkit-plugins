package com.barancode.mc.ddg.survival;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class PlayerList {
	
	Main plugin;
	List<String> players = new LinkedList<String>();
	
	public PlayerList(Main plugin){
		this.plugin = plugin;
	}
    
    public void write(String player){
    	try {
    		if (!players.contains(player)){
	    		File saveTo = new File(plugin.getDataFolder(), "playerlist.txt");
	    		saveTo.createNewFile();
	    		
	    		FileWriter fw = new FileWriter(saveTo, true);
	    		PrintWriter pw = new PrintWriter(fw);
	    		
	    		players.add(player);
	    		pw.println(player);
	    		
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
    			players.add(line);
    		}
    		reader.close();
    		fr.close();
    	} catch (IOException e){
    		e.printStackTrace();
    	}
    }
}
