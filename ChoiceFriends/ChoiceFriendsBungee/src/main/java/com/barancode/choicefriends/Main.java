package com.barancode.choicefriends;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.barancode.choicefriends.commands.CRTCommand;
import com.barancode.choicefriends.commands.FCommand;
import com.barancode.choicefriends.commands.MsgCommand;
import com.barancode.choicefriends.commands.ReplyCommand;
import com.barancode.choicefriends.commands.RoomCommand;
import com.barancode.choicefriends.database.DatabaseConnection;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import com.google.common.io.ByteStreams;

public class Main extends Plugin{
	
	static HashMap<UUID, String> playerServers = new HashMap<UUID, String>();
	static HashSet<UUID> playerHiding = new HashSet<UUID>();
	public static Main instance;
	{
		instance = this;
	}
	public static DatabaseConnection db = new DatabaseConnection();
	public static TaskScheduler scheduler;
	Configuration config = null;
	
	@Override
	public void onEnable(){
		getProxy().getPluginManager().registerListener(this, new Events());
		
		getProxy().getPluginManager().registerCommand(this, new FCommand());
		getProxy().getPluginManager().registerCommand(this, new MsgCommand("cmsg"));
		getProxy().getPluginManager().registerCommand(this, new RoomCommand());
		getProxy().getPluginManager().registerCommand(this, new CRTCommand());
		getProxy().getPluginManager().registerCommand(this, new ReplyCommand());
		getProxy().getPluginManager().registerCommand(this, new MsgCommand("cw"));
		
		scheduler = getProxy().getScheduler();
		
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder() + "/config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        db.initialize(config.getString("host"), config.getString("username"), config.getString("password"), config.getString("database"));
        
        getProxy().registerChannel("BungeeCord");
	}
	
	
	public static String getServer(UUID uuid){
		if (playerServers.containsKey(uuid)) return playerServers.get(uuid);
		else return "";
	}
	public static void setServer(UUID uuid, String server){
		if (!isHiding(uuid)) playerServers.put(uuid, server);
	}
	public static void deleteServer(UUID uuid){
		if (playerServers.containsKey(uuid)) playerServers.remove(uuid);
	}
	// This doesn't return whether they're just online,
	// but if they're visible to their friends as well
	public static boolean isOnline(UUID uuid){
		return playerServers.containsKey(uuid);
	}
	
	public static boolean toggleHiding(UUID uuid, String server){
		if (playerHiding.contains(uuid)){
			playerHiding.remove(uuid);
			setServer(uuid, server);
			return false;
		} else {
			playerHiding.add(uuid);
			deleteServer(uuid);
			return true;
		}
	}
	public static void deleteHiding(UUID uuid){
		if (playerHiding.contains(uuid)) playerHiding.remove(uuid);
	}
	public static boolean isHiding(UUID uuid){
		return playerHiding.contains(uuid);
	}
}
