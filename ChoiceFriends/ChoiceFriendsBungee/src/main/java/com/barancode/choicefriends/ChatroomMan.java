package com.barancode.choicefriends;

import java.util.HashMap;
import java.util.UUID;

import com.barancode.choicefriends.objects.ChatLocation;
import com.barancode.choicefriends.objects.Chatroom;

public class ChatroomMan {
	public static HashMap<String, Chatroom> rooms = new HashMap<String, Chatroom>();
	public static HashMap<UUID, ChatLocation> locations = new HashMap<UUID, ChatLocation>();
	
	public static boolean create(String name){
		if (!rooms.containsKey(name)){
			rooms.put(name, new Chatroom(name));
			return true;
		} else {
			return false;
		}
	}
	
	public static void addUser(String name, UUID player){
		rooms.put(name, rooms.get(name).addUser(player));
		locations.put(player, new ChatLocation(name));
	}
	
	public static void removeUser(String name, UUID player){
		Chatroom room = rooms.get(name);
		boolean delete = room.removeUser(player);
		if (!delete) rooms.put(name, room);
		else rooms.remove(name);
		locations.remove(player);
	}
	
	public static String toggleUser(UUID player){
		if (!locations.containsKey(player)) return "error";
		ChatLocation loc = locations.get(player);
		if (loc.currentlyActive){
			loc.currentlyActive = false;
			return "disabled";
		} else {
			loc.currentlyActive = true;
			return "active";
		}
	}
}
