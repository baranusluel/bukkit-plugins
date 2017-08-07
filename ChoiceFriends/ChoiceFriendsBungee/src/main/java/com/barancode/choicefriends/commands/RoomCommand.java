package com.barancode.choicefriends.commands;

import java.util.UUID;

import com.barancode.choicefriends.ChatroomMan;
import com.barancode.choicefriends.Main;
import com.barancode.choicefriends.Utils;
import com.barancode.choicefriends.database.DatabaseUUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RoomCommand extends Command{
	public RoomCommand(){
		super("chatroom");
	}
	
	public void help(CommandSender sender){
		// I send a separate message for each line because I couldn't find a better way, \n doesn't seem to work
		sender.sendMessage(new ComponentBuilder("-==- ChoiceFriends Chatrooms Help-==-").color(ChatColor.BLUE).create());
		sender.sendMessage(new ComponentBuilder("/chatroom create <name> - Create a new chatroom with a custom name").create());
		sender.sendMessage(new ComponentBuilder("/chatroom invite <player> - Invite a player to the chatroom you're currently in").create());
		sender.sendMessage(new ComponentBuilder("/chatroom leave - Leave the chatroom").create());
		sender.sendMessage(new ComponentBuilder("/chatroom list - List the members in the chatroom you're currently in").create());
		sender.sendMessage(new ComponentBuilder("/chatroom toggle OR /crt - Switch between the chatroom chat and server chat").create());
	}
	
    @Override
    public void execute(CommandSender commandSender, String[] strings) {
    	if (strings.length == 2){
    		if (strings[0].equalsIgnoreCase("create")){
    			if (!ChatroomMan.create(strings[1])) commandSender.sendMessage(new ComponentBuilder("A chatroom with that name already exists").color(ChatColor.RED).create());
    			else{
    				ChatroomMan.addUser(strings[1], ((ProxiedPlayer)commandSender).getUniqueId());
    				commandSender.sendMessage(new ComponentBuilder("You have created a new chatroom. Do '/chatroom invite <player>' to invite a player to the chatroom").color(ChatColor.DARK_GREEN).create());
    				CRTCommand.toggle(commandSender);
    			}
    			return;
    		} else if (strings[0].equalsIgnoreCase("invite")){
    			UUID uuid = ((ProxiedPlayer)commandSender).getUniqueId();
    			if (ChatroomMan.locations.containsKey(uuid)){
    				String chatName = ChatroomMan.locations.get(uuid).name;
    				UUID target = Utils.isOnlineIgnoreCase(strings[1]);
    				if (target == null){
    					commandSender.sendMessage(new ComponentBuilder("That player is not online!").color(ChatColor.RED).create());
    					return;
    				}
    				if (ChatroomMan.locations.containsKey(target)){
    					Main.instance.getProxy().getPlayer(target).sendMessage(new ComponentBuilder(commandSender.getName() + " invited you to the chatroom " + chatName + " but you are already a member of a chatroom."
    							+ " To leave it, do /chatroom leave, and ask for another invite from " + commandSender.getName()).color(ChatColor.RED).create());
    					commandSender.sendMessage(new ComponentBuilder("That player is a member of another chatroom, but has been notified that they can leave it and join " + chatName + " instead").color(ChatColor.RED).create());
    					return;
    				}
    				ChatroomMan.addUser(chatName, target);
					Main.instance.getProxy().getPlayer(target).sendMessage(new ComponentBuilder("You have been invited to the chatroom " + chatName + " by " + commandSender.getName() + ". Do '/chatroom toggle' or '/crt' to start talking there").color(ChatColor.GOLD).create());
    				commandSender.sendMessage(new ComponentBuilder("You have invited " + strings[1] + " to the chatroom").color(ChatColor.GOLD).create());
    			}
    			else commandSender.sendMessage(new ComponentBuilder("You are not a member of a chatroom at the moment").color(ChatColor.RED).create());
    			return;
    		}
    	} else if (strings.length == 1){
    		if (strings[0].equalsIgnoreCase("toggle")){
    			CRTCommand.toggle(commandSender);
    			return;
    		} else if (strings[0].equalsIgnoreCase("leave")){
    			UUID uuid = ((ProxiedPlayer)commandSender).getUniqueId();
    			if (ChatroomMan.locations.containsKey(uuid)){
    				ChatroomMan.removeUser(ChatroomMan.locations.get(uuid).name, uuid);
    				commandSender.sendMessage(new ComponentBuilder("You have left that chatroom").color(ChatColor.GOLD).create());
    			}
    			else commandSender.sendMessage(new ComponentBuilder("You are not a member of a chatroom at the moment").color(ChatColor.RED).create());
    			return;
    		} else if (strings[0].equalsIgnoreCase("list")){
    			UUID uuid = ((ProxiedPlayer)commandSender).getUniqueId();
    			if (ChatroomMan.locations.containsKey(uuid)){
        			commandSender.sendMessage(new ComponentBuilder("The members of the chatroom are:").color(ChatColor.GOLD).create());
        			String members = "";
        			for (UUID player : ChatroomMan.rooms.get(ChatroomMan.locations.get(uuid).name).users){
        				members += (Utils.isOnline(player) ? "§2" : "§4") + DatabaseUUID.getUsername(player) + "§r, ";
        			}
        			members = members.substring(0, members.length() - 2);
        			commandSender.sendMessage(new ComponentBuilder(members).create());
    			}
    			else commandSender.sendMessage(new ComponentBuilder("You are not a member of a chatroom at the moment").color(ChatColor.RED).create());
    			return;
    		}
    	}
    	
    	help(commandSender);
    }
}
