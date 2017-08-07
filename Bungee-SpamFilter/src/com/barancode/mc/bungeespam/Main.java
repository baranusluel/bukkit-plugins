package com.barancode.mc.bungeespam;

import java.util.HashMap;
import java.util.HashSet;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class Main extends Plugin implements Listener{
	
	HashMap<String, Message> messages = new HashMap<String, Message>();
	HashSet<String> words = new HashSet<String>();

	@Override
	public void onEnable(){
    	getProxy().getPluginManager().registerListener(this, this);
    	populateSet("shit", "crap", "fuck", "dick", "penis", "vagina", "boob", "cock", "anus", "panooch", "dildo", "ass", "pussy", "cunt", "whore", "fag", 
    			"slut", "queer", "bitch", "bastard", "gay", "nigger", "bloody", "asshole", "fucker", "motherfucker", "motherfucka", "fucka", "fucking");
	}
	
	public void populateSet(String... list){
		for (String s : list){
			words.add(s);
		}
	}
	
    @EventHandler
    public void onChat(ChatEvent event) {
    	if (event.getMessage().startsWith("/")) return;
    	
    	String name = ((ProxiedPlayer)event.getSender()).getName();
    	if (messages.containsKey(name)){
    		Message m = messages.get(name);
    		if (((System.currentTimeMillis() - m.time) / 1000) < 3){
	    		String oldcontent = m.content;
	    		String newcontent = event.getMessage();
	    		if (Math.abs(oldcontent.length() - newcontent.length()) < 3){
	    			if (oldcontent.toLowerCase().contains(newcontent.toLowerCase()) || newcontent.toLowerCase().contains(oldcontent.toLowerCase())){
	    				((ProxiedPlayer)event.getSender()).sendMessage(new ComponentBuilder(ChatColor.RED + "Do not spam!").create());
	    				event.setCancelled(true);
	    			}
	    		}
    		}
    		messages.remove(name);
    	}
    	messages.put(name, new Message(event.getMessage(), System.currentTimeMillis()));
    	
    	String newmessage = "";
    	int uppercase = 0;
    	boolean exclamation = false;
		for(int i = 0; i < event.getMessage().length(); i++){
			char c = event.getMessage().charAt(i);
			if (Character.isLetter(c) && Character.isUpperCase(c)){
				if (uppercase < 4) uppercase++;
				else c = Character.toLowerCase(c);
			}
			if (c == 33){
				if (!exclamation) exclamation = true;
				else continue;
			}
			newmessage += c;
		}
    	
    	String newmessage2 = "";
    	for (String s : newmessage.split(" ")){
    		if (words.contains(s.toLowerCase().replaceAll("[^a-zA-Z0-9]+",""))){
    			for (int i = 0; i < s.length(); i++){
    				newmessage2 += "*";
    			}
    			newmessage2 += " ";
    		} else {
    			newmessage2 += s + " ";
    		}
    	}
    	
    	event.setMessage(newmessage2);
    }
}
