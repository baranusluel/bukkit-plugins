package com.barancode.mc.globalcommands.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.barancode.mc.db.CommandDatabase;

public class Main extends JavaPlugin {
	CommandDatabase cd = new CommandDatabase();
	BukkitScheduler scheduler;
	
    @Override
    public void onEnable() {
        /*this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);*/
    	scheduler = Bukkit.getScheduler();
    	scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
    		@Override
    		public void run(){
    			getCommands();
    		}
    	}, 0L, 30 * 20L);
    	
    	saveDefaultConfig();
    }
    
    public void getCommands(){
    	HashMap<String, UUID> commands = cd.getCommands();
    	for (String command : commands.keySet()){
    		List<String> history = getConfig().getStringList("history");
    		if (history != null && history.contains(commands.get(command).toString())){
    			continue;
    		} else {
    			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    			if (history == null) history = new ArrayList<String>();
    			history.add(commands.get(command).toString());
    			getConfig().set("history", history);
    		}
    	}
    	saveConfig();
    }

    /*@Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        try {
            String subchannel = in.readUTF();
            if (subchannel.equals("GlobalCommands")) {
            	short len = in.readShort();
            	byte[] msgbytes = new byte[len];
            	in.readFully(msgbytes);
            	DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            	String command = msgin.readUTF();
        		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0) return false;
		String message = "";
		for (String s : args){
			message += s + " ";
		}
		message = message.trim();
		
		/*ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Forward");
			out.writeUTF("ALL");
			out.writeUTF("GlobalCommands");
			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			msgout.writeUTF(message);
			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());
		} catch (IOException e){
			e.printStackTrace();
		}
		p.sendPluginMessage(this, "BungeeCord", b.toByteArray());*/
		
		cd.addCommand(message);
		sender.sendMessage(ChatColor.BLUE + "Sent: " + ChatColor.ITALIC + message);
		
		getCommands();
		
		//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message);
		return true;
	}
}