package net.choicecraft.ChoiceWorks;

import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CEvent extends Event{
	// This is a ChoiceWorks event.
	// It can be used for different things: specified by getType()
	// To listen for this event, you would use it just like
	// a normal Bukkit API Event. Within a class that implements Listener,
	// you would place a method that listens for the CEvent, just like any other event.
	
	private static final HandlerList handlers = new HandlerList();
	private String type;
	private Map<String, String> arguments;
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	public CEvent(String type, Map<String, String> arguments){
		this.type = type;
		this.arguments = arguments;
	}
	
	// This specifies what type of event this is. The current options are:
	// - bungee.incoming
	public String getType(){
		return type;
	}
	
	// This contains the information/data.
	// For the bungee.incoming information, these are:
	// - label
	// - data
	public Map<String, String> getArguments(){
		return arguments;
	}
}
