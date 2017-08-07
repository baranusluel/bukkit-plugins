package com.barancode.choicefriends.commands;

import com.barancode.choicefriends.ChatroomMan;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CRTCommand extends Command{
	public CRTCommand(){
		super("crt");
	}
	
    @Override
    public void execute(CommandSender commandSender, String[] strings) {
    	toggle(commandSender);
    }
    
    public static void toggle(CommandSender sender){
    	String result = ChatroomMan.toggleUser(((ProxiedPlayer)sender).getUniqueId());
    	if (result.equals("error")) sender.sendMessage(new ComponentBuilder("You are not in a chatroom, so you can't toggle it").color(ChatColor.RED).create());
    	else if (result.equals("active")) sender.sendMessage(new ComponentBuilder("+ You are now speaking in the chatroom").color(ChatColor.AQUA).create());
    	else if (result.equals("disabled")) sender.sendMessage(new ComponentBuilder("- You are no longer speaking in the chatroom").color(ChatColor.AQUA).create());
    }
}
