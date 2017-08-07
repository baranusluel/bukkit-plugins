package com.barancode.choicefriends;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Utils {
	public static UUID isOnline(String name){
		for (ProxiedPlayer p : Main.instance.getProxy().getPlayers()){
			if (p.getName().equals(name)){
				return p.getUniqueId();
			}
		}
		return null;
	}
	
	// If coming from user input, use this instead. They can't spell :P
	public static UUID isOnlineIgnoreCase(String name){
		for (ProxiedPlayer p : Main.instance.getProxy().getPlayers()){
			if (p.getName().equalsIgnoreCase(name)){
				return p.getUniqueId();
			}
		}
		return null;
	}
	
	public static boolean isOnline(UUID uuid){
		for (ProxiedPlayer p : Main.instance.getProxy().getPlayers()){
			if (p.getUniqueId().equals(uuid)){
				return true;
			}
		}
		return false; 
	}
	
	public static UUID formatUUID(String string){
		if (!string.contains("-")){
			string = string.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
		}
		return UUID.fromString(string);
	}
	
	public static boolean sendBungeeMessage(String target, String label, String message){
		ServerInfo server = Main.instance.getProxy().getServerInfo(target);
		if (server == null) return false;
		if (server.getPlayers().size() == 0) return false;
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
	    
		out.writeUTF("ChoiceWorks");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF(label + ":" + message);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());
		
	    server.sendData("BungeeCord", out.toByteArray());
	    return true;
	}
}
