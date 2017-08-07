package com.barancode.choicefriends.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Chatroom {
	public String name;
	public List<UUID> users = new ArrayList<UUID>();
	
	public Chatroom(String name){
		this.name = name;
	}
	
	public Chatroom addUser(UUID player){
		if (!users.contains(player)) users.add(player);
		return this;
	}
	
	// Returns whether the room is empty and should be removed
	public boolean removeUser(UUID player){
		if (users.contains(player)) users.remove(player);
		if (users.size() < 1) return true;
		return false;
	}
}
