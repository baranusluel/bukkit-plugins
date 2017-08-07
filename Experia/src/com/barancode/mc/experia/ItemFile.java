package com.barancode.mc.experia;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class ItemFile {
	
	Main plugin;
	
	public ItemFile(Main plugin){
		this.plugin = plugin;
	}
    
    public void write(String name, String option, String value){
    	try {
    		File saveTo = new File(plugin.getDataFolder(), name + ".txt");
    		saveTo.createNewFile();
    		
    		FileReader fr = new FileReader(saveTo);
    		BufferedReader reader = new BufferedReader(fr);
    		
    		List<String> list = new LinkedList<String>();
    		String s;
    		
    		while ((s = reader.readLine()) != null){
    			if (!s.contains(option)){
    				list.add(s);
    			}
    		}
    				
    		reader.close();
    		fr.close();
    		
    		saveTo.delete();
    		File newsaveTo = new File(plugin.getDataFolder(), name + ".txt");
    		newsaveTo.createNewFile();
    		FileWriter fw = new FileWriter(newsaveTo, true);
    		PrintWriter pw = new PrintWriter(fw);
    		
    		list.add(option + ": " + value);
    		
    		for (int i = 0; i < list.size(); i++){
    			pw.println(list.get(i));
    		}
    		
    		pw.close();
    		fw.close();
    	} catch (IOException e){
    		e.printStackTrace();
    	}
    }
    public String read(String name, String option){
    	try {
    		File readFrom = new File(plugin.getDataFolder(), name + ".txt");
    		if (!readFrom.exists()){
    			return null;
    		}
    		FileReader fr = new FileReader(readFrom);
    		BufferedReader reader = new BufferedReader(fr);
    		
    		String line;
    		
    		while ((line = reader.readLine()) != null){
    			if (line.contains(option)){
    	    		reader.close();
    	    		fr.close();
    				line = line.replaceAll(option + ": ", "");
    				return line;
    			}
    		}
    		reader.close();
    		fr.close();
    	} catch (IOException e){
    		e.printStackTrace();
    	}
		return null;
    }
}
