package com.barancode.choicefriends.commands;

import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import com.barancode.choicefriends.Main;
import com.barancode.choicefriends.Utils;
import com.barancode.choicefriends.database.DatabaseFriend;
import com.barancode.choicefriends.database.DatabaseFriendBlocks;
import com.barancode.choicefriends.database.DatabaseFriendRequest;
import com.barancode.choicefriends.database.DatabaseUUID;

public class FCommand extends Command{
	
	public FCommand(){
		super("friend");
	}
	
	public void help(CommandSender sender){
		// I send a separate message for each line because I couldn't find a better way, \n doesn't seem to work
		sender.sendMessage(new ComponentBuilder("-==- ChoiceFriends Help-==-").color(ChatColor.DARK_PURPLE).create());
		sender.sendMessage(new ComponentBuilder("/friend list - Lists your friends and their statuses").create());
		sender.sendMessage(new ComponentBuilder("/friend hide - Appear offline to your friends").create());
		sender.sendMessage(new ComponentBuilder("/friend add <player> - Send a friend request").create());
		sender.sendMessage(new ComponentBuilder("/friend accept <player> - Accept a friend request").create());
		sender.sendMessage(new ComponentBuilder("/friend deny <player> - Deny a friend request").create());
		sender.sendMessage(new ComponentBuilder("/friend remove <player> - Remove a friend").create());
		sender.sendMessage(new ComponentBuilder("/friend block <player> - Block the friend requests coming from a player").create());
		sender.sendMessage(new ComponentBuilder("/friend unblock <player> - Unblock a player").create());
		sender.sendMessage(new ComponentBuilder("/cmsg <player> <message> - Send a message to a friend, even when they are on another server in ChoiceCraft").create());
	}
	
    @Override
    public void execute(final CommandSender commandSender, final String[] strings) {
        if (strings.length == 1){
        	if (strings[0].equalsIgnoreCase("list")){
        		Main.scheduler.runAsync(Main.instance, new Runnable(){
        			@Override
        			public void run(){
                		List<UUID> list = DatabaseFriend.getFriends(((ProxiedPlayer)commandSender).getUniqueId());
                		if (list.size() > 0){
            				commandSender.sendMessage(new ComponentBuilder("Your current friends:").color(ChatColor.GOLD).create());
                		} else {
            				commandSender.sendMessage(new ComponentBuilder("You do not have any friends").color(ChatColor.RED).create());
            				return;
                		}
                		for (UUID uuid : list){
                			String name = DatabaseUUID.getUsername(uuid);
                			String server = Main.getServer(uuid);
                			if (!server.equals("")){
                				TextComponent tc = new TextComponent(name + " - Online: " + server);
                				tc.setColor(ChatColor.GREEN);
                				commandSender.sendMessage(tc);
                			} else {
                				TextComponent tc = new TextComponent(name + " - Offline");
                				tc.setColor(ChatColor.RED);
                				commandSender.sendMessage(tc);
                			}
                		}
        			}
        		});
        		return;
        	} else if (strings[0].equalsIgnoreCase("hide")){
        		ProxiedPlayer p = (ProxiedPlayer)commandSender;
        		boolean isHiding = Main.toggleHiding(p.getUniqueId(), p.getServer().getInfo().getName());
        		if (isHiding){
        			commandSender.sendMessage(new ComponentBuilder("You are now hidden, and will appear offline to your friends").color(ChatColor.GRAY).create());
        		} else {
        			commandSender.sendMessage(new ComponentBuilder("You have disabled hiding, and will appear online").color(ChatColor.GRAY).create());
        		}
        		return;
        	}
        } else if (strings.length == 2){
        	if (strings[0].equalsIgnoreCase("add")){
        		Main.scheduler.runAsync(Main.instance, new Runnable(){
        			@Override
        			public void run(){
		        		String friendname = strings[1];
		        		UUID frienduuid = DatabaseUUID.getUUID(friendname);
		        		if (frienduuid == null){
                            commandSender.sendMessage(new ComponentBuilder("A player by that name has not played on ChoiceCraft!").color(ChatColor.RED).create());
                            return;
		        		}
		        		
		        		UUID uuid = ((ProxiedPlayer)commandSender).getUniqueId();
		        		String sender = commandSender.getName();
		        		
		        		List<UUID> friends = DatabaseFriend.getFriends(frienduuid);
		        		if (friends.contains(uuid)){
		        			commandSender.sendMessage(new ComponentBuilder("You are already friends with that person!").color(ChatColor.RED).create());
		        			return;
		        		}
		        		
		        		List<UUID> blocked = DatabaseFriendBlocks.getFriendBlocks(frienduuid);
		        		if (blocked.contains(uuid)){
		        			commandSender.sendMessage(new ComponentBuilder("That player has blocked you! You cannot send them a friend request").color(ChatColor.RED).create());
		        			return;
		        		}
		        		
		        		DatabaseFriendRequest.addFriendRequest(frienduuid, uuid);
		        		commandSender.sendMessage(new ComponentBuilder("You have sent a friend request to " + friendname).color(ChatColor.DARK_AQUA).create());
		        		if (Utils.isOnlineIgnoreCase(strings[1]) != null) Main.instance.getProxy().getPlayer(strings[1]).sendMessage(
		        				new ComponentBuilder(sender + " has sent you a friend request. Do '/friend accept " + sender + "' or '/friend deny " + sender + "'").color(ChatColor.DARK_AQUA).create());
	        		}
    			});
        		return;
        	} else if (strings[0].equalsIgnoreCase("accept")){
        		Main.scheduler.runAsync(Main.instance, new Runnable(){
        			@Override
        			public void run(){
		        		List<UUID> requests = DatabaseFriendRequest.getFriendRequests(((ProxiedPlayer)commandSender).getUniqueId());
		        		UUID uuid = DatabaseUUID.getUUID(strings[1]);
		        		if (uuid == null){
                            commandSender.sendMessage(new ComponentBuilder("A player by that name has not played on ChoiceCraft!").color(ChatColor.RED).create());
                            return;
		        		}
		        		if (!requests.contains(uuid)){
		        			commandSender.sendMessage(new ComponentBuilder("You did not receive a friend request from that player!").color(ChatColor.RED).create());
		        			return;
		        		}
		        		
		        		DatabaseFriendRequest.removeFriendRequest(((ProxiedPlayer)commandSender).getUniqueId(), uuid);
		        		
		        		List<UUID> friends = DatabaseFriend.getFriends(((ProxiedPlayer)commandSender).getUniqueId());
		        		if (!friends.contains(uuid)){
			        		DatabaseFriend.addFriend(((ProxiedPlayer)commandSender).getUniqueId(), uuid);
			        		DatabaseFriend.addFriend(uuid, ((ProxiedPlayer)commandSender).getUniqueId());
		        		}
		        		commandSender.sendMessage(new ComponentBuilder(strings[1] + " is now your friend").color(ChatColor.YELLOW).create());
		        		if (Utils.isOnlineIgnoreCase(strings[1]) != null) Main.instance.getProxy().getPlayer(strings[1]).sendMessage(new ComponentBuilder(commandSender.getName() + " is now your friend").color(ChatColor.YELLOW).create());
        			}
    			});
        		return;
        	} else if (strings[0].equalsIgnoreCase("deny")){
        		Main.scheduler.runAsync(Main.instance, new Runnable(){
        			@Override
        			public void run(){
		        		List<UUID> requests = DatabaseFriendRequest.getFriendRequests(((ProxiedPlayer)commandSender).getUniqueId());
		        		UUID uuid = DatabaseUUID.getUUID(strings[1]);
		        		if (uuid == null){
                            commandSender.sendMessage(new ComponentBuilder("A player by that name has not played on ChoiceCraft!").color(ChatColor.RED).create());
                            return;
		        		}
		        		if (!requests.contains(uuid)){
		        			commandSender.sendMessage(new ComponentBuilder("You did not receive a friend request from that player!").color(ChatColor.RED).create());
		        			return;
		        		}
		        		
		        		DatabaseFriendRequest.removeFriendRequest(((ProxiedPlayer)commandSender).getUniqueId(), uuid);
		        		commandSender.sendMessage(new ComponentBuilder("You have denied " + strings[1] + "'s friend request").color(ChatColor.RED).create());
		        		if (Utils.isOnlineIgnoreCase(strings[1]) != null) Main.instance.getProxy().getPlayer(strings[1]).sendMessage(new ComponentBuilder(commandSender.getName() + " denied your friend request").color(ChatColor.RED).create());
        			}
    			});
        		return;
        	} else if (strings[0].equalsIgnoreCase("remove")){
        		Main.scheduler.runAsync(Main.instance, new Runnable(){
        			@Override
        			public void run(){
        				List<UUID> friends = DatabaseFriend.getFriends(((ProxiedPlayer)commandSender).getUniqueId());
        				UUID uuid = DatabaseUUID.getUUID(strings[1]);
		        		if (uuid == null){
                            commandSender.sendMessage(new ComponentBuilder("A player by that name has not played on ChoiceCraft!").color(ChatColor.RED).create());
                            return;
		        		}
        				if (friends.contains(uuid)){
        					DatabaseFriend.removeFriend(((ProxiedPlayer)commandSender).getUniqueId(), uuid);
        					DatabaseFriend.removeFriend(uuid, ((ProxiedPlayer)commandSender).getUniqueId());
        					commandSender.sendMessage(new ComponentBuilder("You are no longer friends with " + strings[1]).color(ChatColor.AQUA).create());
        				} else {
        					commandSender.sendMessage(new ComponentBuilder("You are not friends with that player").color(ChatColor.RED).create());
        				}
        			}
        		});
        		return;
        	} else if (strings[0].equalsIgnoreCase("block")){
        		Main.scheduler.runAsync(Main.instance, new Runnable(){
        			@Override
        			public void run(){
		        		String friendname = strings[1];
		        		UUID frienduuid = DatabaseUUID.getUUID(friendname);
		        		if (frienduuid == null){
                            commandSender.sendMessage(new ComponentBuilder("A player by that name has not played on ChoiceCraft!").color(ChatColor.RED).create());
                            return;
		        		}
		        		
		        		UUID uuid = ((ProxiedPlayer)commandSender).getUniqueId();
		        		
		        		DatabaseFriendBlocks.addFriendBlock(uuid, frienduuid);
		        		commandSender.sendMessage(new ComponentBuilder("You have blocked " + friendname + ". They will not be able to send you friend requests").color(ChatColor.DARK_AQUA).create());
	        		}
    			});
        		return;
        	} else if (strings[0].equalsIgnoreCase("unblock")){
        		Main.scheduler.runAsync(Main.instance, new Runnable(){
        			@Override
        			public void run(){
		        		String friendname = strings[1];
		        		UUID frienduuid = DatabaseUUID.getUUID(friendname);
		        		if (frienduuid == null){
                            commandSender.sendMessage(new ComponentBuilder("A player by that name has not played on ChoiceCraft!").color(ChatColor.RED).create());
                            return;
		        		}
		        		
		        		UUID uuid = ((ProxiedPlayer)commandSender).getUniqueId();
		        		
		        		DatabaseFriendBlocks.removeFriendBlock(uuid, frienduuid);
		        		commandSender.sendMessage(new ComponentBuilder("You have unblocked " + friendname + ". They will be able to send you friend requests again").color(ChatColor.DARK_AQUA).create());
	        		}
    			});
        		return;
        	}
        }
        
        help(commandSender);
    }
}
