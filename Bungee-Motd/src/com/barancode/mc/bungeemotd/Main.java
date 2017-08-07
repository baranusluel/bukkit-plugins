package com.barancode.mc.bungeemotd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

public class Main extends Plugin implements Listener{
	public static Configuration config;
    public static ConfigurationProvider cProvider;
    public static File cFile;
    public String[] messages;

    @Override
    public void onEnable() {
    	getProxy().getPluginManager().registerCommand(this, new ReloadCommand(this));
    	getProxy().getPluginManager().registerListener(this, this);
    	
        File cFolder = new File(this.getDataFolder(),"");
        if (!cFolder.exists()){
           cFolder.mkdir();
        }
        cFile = new File(this.getDataFolder() + "/config.yml");

        if (!cFile.exists()) {
            try {
                cFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            try (InputStream in = getResourceAsStream(cFile.getName()); OutputStream out = new FileOutputStream(cFile)) {
                ByteStreams.copy(in, out);
            } catch (FileNotFoundException e) {
                getLogger().log(Level.SEVERE, String.format("Config file '%s' not found.", cFile.getName()), e);
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, String.format("Could not copy defaults to file '%s'.", cFile.getName()), e);
            }
        }
        cProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
        try {
            config = cProvider.load(cFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        messages = config.getString("motd").split("\n");
    }
    
    public void reloadConfig(){
        try {
			config = cProvider.load(cFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
        messages = config.getString("motd").split("\n");
    }
    
    @EventHandler
    public void onJoin(ServerConnectedEvent event) {
    	for (String s : messages){
    		event.getPlayer().sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', s)).create());
    	}
    }
}