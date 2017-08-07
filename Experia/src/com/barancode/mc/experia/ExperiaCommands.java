package com.barancode.mc.experia;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ExperiaCommands implements CommandExecutor {
	
	Main plugin;
	
	public ExperiaCommands(Main plugin){
		this.plugin = plugin;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		if (!(sender instanceof Player)){
	        sender.sendMessage(ChatColor.RED + "You must be a player!");
	        return true;
	    }
		final Player player = (Player)sender;
		
		if (cmd.getName().equalsIgnoreCase("bcmckillall")){
			sender.sendMessage("Cleared");
			for (Entity e : ((Player)sender).getWorld().getEntities()){
				if (e.getType() == EntityType.PLAYER) continue;
				e.remove();
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("experia")){
			if (args.length == 0){
				player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("experia-command")).replaceAll("&&", "\n"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("spawn")){
				if (args.length == 1){
					if (!player.hasPermission("experia.spawn")){
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.nopermission")));
						return true;
					}
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.join-game")));
					
					player.getInventory().clear();
					
					int i = plugin.random.nextInt(plugin.getConfig().getInt("spawncount"));
					i++;
					
					Location loc = plugin.spawns.get(i);
					player.teleport(loc);
					
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
					player.setGameMode(GameMode.SURVIVAL);
					plugin.invincible.add(player.getName());
					
					final Player finalplayer = player;
					plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
					    @Override
					    public void run() {
							
							PlayerInventory inventory = finalplayer.getInventory();
							inventory.clear();
							inventory.setArmorContents(null);
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi get logout " + finalplayer.getName());
							if (plugin.giveKit.containsKey(finalplayer.getName())){
								
								String kitname = plugin.giveKit.get(finalplayer.getName());
								
								plugin.itemmanager.giveItems(finalplayer, kitname);
								
								plugin.itemmanager.giveHelmet(finalplayer, kitname);
								
								plugin.itemmanager.giveChestplate(finalplayer, kitname);
								
								plugin.itemmanager.giveLeggings(finalplayer, kitname);
								
								plugin.itemmanager.giveBoots(finalplayer, kitname);
								
								plugin.giveKit.remove(finalplayer.getName());
							} else {
								
								String kitname = "Soldier";
								
								plugin.itemmanager.giveItems(finalplayer, kitname);
								
								plugin.itemmanager.giveHelmet(finalplayer, kitname);
								
								plugin.itemmanager.giveChestplate(finalplayer, kitname);
								
								plugin.itemmanager.giveLeggings(finalplayer, kitname);
								
								plugin.itemmanager.giveBoots(finalplayer, kitname);
								
							}
					    }
					}, 40L);
					plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
					    @Override
					    public void run() {
							plugin.invincible.remove(finalplayer.getName());
					    }
					}, 5 * 20L);
					
					return true;
					
				} else if (args.length == 2){
						if (!player.hasPermission("experia.spawn." + args[1])){
							player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.nopermission")));
							return true;
						}
						int i = Integer.parseInt(args[1]);
						
						if (!plugin.spawns.containsKey(i)){
							player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.invalid-spawn-number")));
							return true;
						}
						
						Location loc = plugin.spawns.get(i);
						player.teleport(loc);
						
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.join-game")));
						
						player.getInventory().clear();
						
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
						player.setGameMode(GameMode.SURVIVAL);
						plugin.invincible.add(player.getName());
						
						final Player finalplayer = player;
						plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
						    @Override
						    public void run() {
								PlayerInventory inventory = finalplayer.getInventory();
								inventory.clear();
								inventory.setArmorContents(null);
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi get logout " + finalplayer.getName());
								if (plugin.giveKit.containsKey(finalplayer.getName())){
									
									String kitname = plugin.giveKit.get(finalplayer.getName());
									
									plugin.itemmanager.giveItems(finalplayer, kitname);
									
									plugin.itemmanager.giveHelmet(finalplayer, kitname);
									
									plugin.itemmanager.giveChestplate(finalplayer, kitname);
									
									plugin.itemmanager.giveLeggings(finalplayer, kitname);
									
									plugin.itemmanager.giveBoots(finalplayer, kitname);
									
									plugin.giveKit.remove(finalplayer.getName());
								} else {
									
									String kitname = "Soldier";
									
									plugin.itemmanager.giveItems(finalplayer, kitname);
									
									plugin.itemmanager.giveHelmet(finalplayer, kitname);
									
									plugin.itemmanager.giveChestplate(finalplayer, kitname);
									
									plugin.itemmanager.giveLeggings(finalplayer, kitname);
									
									plugin.itemmanager.giveBoots(finalplayer, kitname);
									
								}
						    }
						}, 40L);
						plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
						    @Override
						    public void run() {
								plugin.invincible.remove(finalplayer.getName());
						    }
						}, 5 * 20L);
											
						return true;
					}
				} else if (args[0].equalsIgnoreCase("setspawn")){
					if (!player.hasPermission("experia.setspawn")){
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.nopermission")));
						return true;
					}
					
					Location loc = player.getLocation();
					
					int i = plugin.getConfig().getInt("spawncount");
					i++;
					plugin.getConfig().set("spawncount", i);
					
					plugin.getConfig().set("spawns." + i + ".x", loc.getX());
					plugin.getConfig().set("spawns." + i + ".y", loc.getY());
					plugin.getConfig().set("spawns." + i + ".z", loc.getZ());
					plugin.getConfig().set("spawns." + i + ".world", loc.getWorld().getName());
					plugin.getConfig().set("spawns." + i + ".pitch", loc.getPitch());
					plugin.getConfig().set("spawns." + i + ".yaw", loc.getYaw());
					
					plugin.saveConfig();
					
					plugin.spawns.put(i, loc);
					
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.set-spawnpoint")).replaceAll("<id>", i + ""));
					return true;
				} else if (args[0].equalsIgnoreCase("setworldspawn")){
					if (!player.hasPermission("experia.setworldspawn")){
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.nopermission")));
						return true;
					}
					
					Location loc = player.getLocation();
					
					plugin.getConfig().set("worldspawn.x", loc.getX());
					plugin.getConfig().set("worldspawn.y", loc.getY());
					plugin.getConfig().set("worldspawn.z", loc.getZ());
					plugin.getConfig().set("worldspawn.world", loc.getWorld().getName());
					plugin.getConfig().set("worldspawn.pitch", loc.getPitch());
					plugin.getConfig().set("worldspawn.yaw", loc.getYaw());
					
					plugin.saveConfig();
					
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.set-respawnpoint")));
				} else if (args[0].equalsIgnoreCase("team")){
					if (args.length >= 3){
						if (args[1].equalsIgnoreCase("create")){
							if (plugin.teamMembers.containsKey(player.getUniqueId()) && !plugin.teamMembers.get(player.getUniqueId()).equals("")){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-create")));
								return true;
							}
							
							String finalString = "";
			    			for (int i = 2; i < args.length; i++) {
			    			    finalString += args[i] + ' ';
			    			}
			    			finalString = finalString.trim();
			    			List<String> teams = plugin.customconfig.getCustomConfig().getStringList("teams");
							for (String name : teams){
								if (name.equals(finalString)){
									player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.name-used")));
									return true;
								}
							}
							teams.add(finalString);
							
			    			List<String> members = new LinkedList<String>(); members.add(player.getUniqueId().toString());
			    			plugin.customconfig.getCustomConfig().set("team." + finalString + ".leader", player.getUniqueId().toString());
			    			plugin.customconfig.getCustomConfig().set("team." + finalString + ".members", members);
			    			plugin.customconfig.getCustomConfig().set("teams", teams);
			            	plugin.customconfig.saveCustomConfig();
			            	
			            	plugin.teamMembers.put(player.getUniqueId(), finalString);
			    			
			    			player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.created")).replaceAll("<team>", finalString));
			    			return true;
						} else if (args[1].equalsIgnoreCase("join")){
							if (plugin.teamMembers.containsKey(player.getUniqueId()) && !plugin.teamMembers.get(player.getUniqueId()).equals("")){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-join-already-in-a-team")));
								return true;
							}
							
							String finalString = "";
			    			for (int i = 2; i < args.length; i++) {
			    			    finalString += args[i] + ' ';
			    			}
			    			finalString = finalString.trim();
			    			
							plugin.getLogger().info(player.getUniqueId().toString());
							plugin.getLogger().info(finalString);
			    			if (!plugin.teamInvites.containsKey(player.getUniqueId())){
								plugin.getLogger().info("1");
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-join-not-invited")));
								return true;
			    			} else {
								plugin.getLogger().info("2");
			    				List<String> invites = plugin.teamInvites.get(player.getUniqueId());
			    				if (!invites.contains(finalString)){
									player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-join-not-invited")));
									return true;
			    				}
			    				invites.remove(finalString);
			    				plugin.teamInvites.put(player.getUniqueId(), invites);
			    			}
			    			
			    			List<String> teams = plugin.customconfig.getCustomConfig().getStringList("teams");
			    			
			    			if (!teams.contains(finalString)){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-join-not-exist").replaceAll("<team>", finalString)));
								return true;
			    			}
							
			    			List<String> players = plugin.customconfig.getCustomConfig().getStringList("team." + finalString + ".members");
			    			
			    			for (String s : players){
			    				boolean contains = false;
			    				for (Player p : Bukkit.getOnlinePlayers()){
			    					if (p.getUniqueId().toString().equals(s)) contains = true;
			    				}
			    				if (contains) Bukkit.getPlayer(UUID.fromString(s)).sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.joined-broadcast").replaceAll("<player>", player.getName())));
			    			}
			    			
							players.add(player.getUniqueId().toString());
							plugin.customconfig.getCustomConfig().set("team." + finalString + ".members", players);
							plugin.customconfig.saveCustomConfig();
							
							plugin.teamMembers.put(player.getUniqueId(), finalString);
							
							player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.joined").replaceAll("<team>", finalString)));
							
							return true;
						}
					}
					
					if (args.length == 3){
						if (args[1].equalsIgnoreCase("invite")){
							
							if (!plugin.teamMembers.containsKey(player.getUniqueId()) || plugin.teamMembers.get(player.getUniqueId()).equals("")){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-invite-no-team")));
								return true;
							}
							
							UUID uuid = plugin.utils.getUUID(args[2]);
							if (uuid == null || uuid.toString().equals("")){
								player.sendMessage(ChatColor.RED + "A player by that name has not joined BCMC");
								return true;
							}
							
			    			List<String> teams = plugin.customconfig.getCustomConfig().getStringList("teams");
			    			String team = plugin.teamMembers.get(player.getUniqueId());
			    			List<String> players = new LinkedList<String>();
							for (String name : teams){
								players = plugin.customconfig.getCustomConfig().getStringList("team." + name + ".members");
								if (players.contains(uuid.toString())){
									player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-invite-already-in-a-team").replaceAll("<player>", args[2])));
									return true;
								}
								
								if (plugin.customconfig.getCustomConfig().getString("team." + name + ".leader").equals(player.getUniqueId().toString())) team = name;
								else if (players.contains(player.getUniqueId().toString())){
									player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.not-leader")));
									return true;
								}
							}
							if (team.equals("")){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-invite-no-team")));
								return true;
							}
							OfflinePlayer op = Bukkit.getOfflinePlayer(args[2]);
							if (!op.hasPlayedBefore()){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-invite-not-exist").replaceAll("<player>", args[2])));
								return true;
							}
							
							String message = plugin.utils.replace(plugin.getConfig().getString("team-messages.invited").replaceAll("<inviter>", player.getName()).replaceAll("<team>", team));
							if (op.isOnline()) Bukkit.getPlayer(args[2]).sendMessage(message);
							else {
								if (!plugin.teamMessages.containsKey(uuid)){
									List<String> messages = new LinkedList<String>();
									messages.add(message);
									plugin.teamMessages.put(uuid, messages);
								} else {
									List<String> messages = plugin.teamMessages.get(uuid);
									messages.add(message);
									plugin.teamMessages.put(uuid, messages);
								}
							}
							
							if (!plugin.teamInvites.containsKey(uuid)){
								List<String> teamInvites = new LinkedList<String>();
								teamInvites.add(team);
								plugin.teamInvites.put(uuid, teamInvites);
							} else {
								List<String> teamInvites = plugin.teamInvites.get(uuid);
								teamInvites.add(team);
								plugin.teamInvites.put(uuid, teamInvites);
							}
							
							player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.invite").replaceAll("<player>", args[2])));
					        
							return true;
						} else if (args[1].equalsIgnoreCase("kick")){
							
							if (!plugin.teamMembers.containsKey(player.getUniqueId()) || plugin.teamMembers.get(player.getUniqueId()).equals("")){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-kick-no-team")));
								return true;
							}
							
							UUID uuid = plugin.utils.getUUID(args[2]);
							if (uuid == null || uuid.toString().equals("")){
								player.sendMessage(ChatColor.RED + "A player by that name has not joined BCMC");
								return true;
							}
							
			    			List<String> teams = plugin.customconfig.getCustomConfig().getStringList("teams");
			    			String team = "";
			    			List<String> players = new LinkedList<String>();
							for (String name : teams){
								players = plugin.customconfig.getCustomConfig().getStringList("team." + name + ".members");
								UUID leader = UUID.fromString(plugin.customconfig.getCustomConfig().getString("team." + name + ".leader"));
								if (leader.equals(uuid.toString())){
									player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-kick-yourself")));
									return true;
								}
								
								if (leader.equals(player.getUniqueId().toString())){
									team = name;
									break;
								}
								else if (players.contains(player.getUniqueId().toString())){
									player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.not-leader")));
									return true;
								}
							}
							if (team.equals("")){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-kick-no-team")));
								return true;
							}
							
							if (!players.contains(uuid.toString())){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-kick-not-member")));
								return true;
							}
							players.remove(uuid.toString());
							plugin.customconfig.getCustomConfig().set("team." + team + ".members", players);
							plugin.customconfig.saveCustomConfig();
							
							plugin.teamMembers.remove(uuid.toString());
							
							String message = plugin.utils.replace(plugin.getConfig().getString("team-messages.kicked")).replaceAll("<player>", player.getName());
							if (Bukkit.getOfflinePlayer(uuid).isOnline()){
								Bukkit.getPlayer(uuid).sendMessage(message);
							} else {
								if (!plugin.teamMessages.containsKey(uuid)){
									List<String> messages = new LinkedList<String>();
									messages.add(message);
									plugin.teamMessages.put(uuid, messages);
								} else {
									List<String> messages = plugin.teamMessages.get(uuid);
									messages.add(message);
									plugin.teamMessages.put(uuid, messages);
								}
							}
							
							player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.kick")).replaceAll("<player>", args[2]));
					        
							return true;
						}
					} else if (args.length == 2){
						if (args[1].equalsIgnoreCase("disband")){
							if (!plugin.teamMembers.containsKey(player.getUniqueId()) || plugin.teamMembers.get(player.getUniqueId()).equals("")){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-disband-no-team")));
								return true;
							}
							
			    			List<String> teams = plugin.customconfig.getCustomConfig().getStringList("teams");
			    			String team = "";
			    			List<String> players = new LinkedList<String>();
							for (String name : teams){
								players = plugin.customconfig.getCustomConfig().getStringList("team." + name + ".members");
								UUID leader = UUID.fromString(plugin.customconfig.getCustomConfig().getString("team." + name + ".leader"));								
								if (leader.equals(player.getUniqueId())){
									team = name;
									break;
								}
								else if (players.contains(player.getUniqueId().toString())){
									player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.not-leader")));
									return true;
								}
							}
							if (team.equals("")){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-disband-no-team")));
								return true;
							}
							
							plugin.customconfig.getCustomConfig().set("team." + team, null);
							teams.remove(team);
							plugin.customconfig.getCustomConfig().set("teams", teams);
							plugin.customconfig.saveCustomConfig();
							
							player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.disband")));
							final String message = plugin.utils.replace(plugin.getConfig().getString("team-messages.disband-broadcast"));
							for (final String p : players){
								plugin.teamMembers.remove(UUID.fromString(p));
								
								if (p.equals(player.getUniqueId().toString())) continue;
								
			    				boolean contains = false;
			    				for (Player tempp : Bukkit.getOnlinePlayers()){
			    					if (tempp.getUniqueId().toString().equals(p)) contains = true;
			    				}
			    				
								if (contains) Bukkit.getPlayer(UUID.fromString(p)).sendMessage(message);
								else {
									if (!plugin.teamMessages.containsKey(p)){
										List<String> messages = new LinkedList<String>();
										messages.add(message);
										plugin.teamMessages.put(UUID.fromString(p), messages);
									} else {
										List<String> messages = plugin.teamMessages.get(p);
										messages.add(message);
										plugin.teamMessages.put(UUID.fromString(p), messages);
									}
								}
							}
							return true;
						} else if (args[1].equalsIgnoreCase("leave")){
							if (!plugin.teamMembers.containsKey(player.getUniqueId()) || plugin.teamMembers.get(player.getUniqueId()).equals("")){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-leave-no-team")));
								return true;
							}
							
			    			String team = plugin.teamMembers.get(player.getUniqueId());
			    			List<String> players = plugin.customconfig.getCustomConfig().getStringList("team." + team + ".members");
							UUID leader = UUID.fromString(plugin.customconfig.getCustomConfig().getString("team." + team + ".leader"));
							if (leader.equals(player.getUniqueId())){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-leave")));
								return true;
							}
							players.remove(player.getUniqueId().toString());
							plugin.customconfig.getCustomConfig().set("team." + team + ".members", players);
							plugin.customconfig.saveCustomConfig();
							
							plugin.teamMembers.put(player.getUniqueId(), "");
							
							final String message = plugin.utils.replace(plugin.getConfig().getString("team-messages.leave-broadcast")).replaceAll("<player>", player.getName());
							for (final String p : players){
								
			    				boolean contains = false;
			    				for (Player tempp : Bukkit.getOnlinePlayers()){
			    					if (tempp.getUniqueId().toString().equals(p)) contains = true;
			    				}
			    				
								if (contains) Bukkit.getPlayer(UUID.fromString(p)).sendMessage(message);
								else {
									if (!plugin.teamMessages.containsKey(p)){
										List<String> messages = new LinkedList<String>();
										messages.add(message);
										plugin.teamMessages.put(UUID.fromString(p), messages);
									} else {
										List<String> messages = plugin.teamMessages.get(p);
										messages.add(message);
										plugin.teamMessages.put(UUID.fromString(p), messages);
									}
								}
							}
							
							player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.leave")));
							return true;
						} else if (args[1].equalsIgnoreCase("list")){
							if (!plugin.teamMembers.containsKey(player.getUniqueId()) || plugin.teamMembers.get(player.getUniqueId()).equals("")){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.cannot-list-no-team")));
								return true;
							}
							
			    			String team = plugin.teamMembers.get(player.getUniqueId());
			    			List<String> players = plugin.customconfig.getCustomConfig().getStringList("team." + team + ".members");
			    			List<String> onlineplayers = new LinkedList<String>();
			    			for (String s : players){
			    				String name = "";
			    				for (Player p : Bukkit.getOnlinePlayers()){
			    					if (p.getUniqueId().toString().equals(s)) name = p.getName();
			    				}
			    				if (!name.equals("")) onlineplayers.add(name);
			    			}
			    			if (onlineplayers.size() == 0){
								player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.list-empty")));
								return true;
			    			}
			    			
			    			player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.list.title")).replaceAll("<team>", team));
			    			for (String s : onlineplayers){
			    				player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("team-messages.list.template")).replaceAll("<player>", s));
			    			}
			    			return true;
						}
					} else if (args.length == 1){
						player.sendMessage("/experia team\n   create <team>\n   invite <player>\n   join <team>\n   kick <player>\n   disband\n   leave\n   list");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("logout")){
					if (args.length == 1){
						if (plugin.loggingOutCooldown.contains(player.getName())){
							player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("combat-messages.do-not-spam")));
							return true;
						}
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("combat-messages.logging-out")));
						plugin.loggingOut.put(player.getName(), player.getLocation());
						final Player finalplayer = player;
            			List<Integer> schedulers = plugin.loggingOutSchedulers.get(finalplayer.getName());
	            		if (schedulers != null){
	            			for (int i : schedulers){
		            			plugin.scheduler.cancelTask(i);
		            		}
	            		}
						schedulers = new LinkedList<Integer>();
						for (int i = 1; i < 11; i++){
							final int finali = i;
					        int id = plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
					            @Override
					            public void run(){
					            	if (finali == 10){
					            		plugin.PVPPlayers.remove(finalplayer.getName());
					            		plugin.PVPSchedulers.remove(finalplayer.getName());
					            		plugin.kicking.add(finalplayer.getName());
					            		finalplayer.kickPlayer(plugin.utils.replace(plugin.getConfig().getString("combat-messages.kick-message")));
					            		return;
					            	}
					            	Location loc = finalplayer.getLocation();
					            	if (!plugin.loggingOut.containsKey(finalplayer.getName())){
					            		finalplayer.sendMessage(plugin.utils.replace(plugin.getConfig().getString("combat-messages.log-out-cancel")));
					            		if (plugin.loggingOutSchedulers.containsKey(finalplayer.getName())) {
					            			List<Integer> schedulers = plugin.loggingOutSchedulers.get(finalplayer.getName());
						            		for (int i : schedulers){
						            			plugin.scheduler.cancelTask(i);
						            		}
					            		}
					            		plugin.loggingOut.remove(finalplayer.getName());
					            		plugin.loggingOutSchedulers.remove(finalplayer.getName());
					            	} else if (loc.distance(plugin.loggingOut.get(finalplayer.getName())) != 0){
					            		finalplayer.sendMessage(plugin.utils.replace(plugin.getConfig().getString("combat-messages.log-out-cancel")));
					            		List<Integer> schedulers = plugin.loggingOutSchedulers.get(finalplayer.getName());
					            		for (int i : schedulers){
					            			plugin.scheduler.cancelTask(i);
					            		}
					            		plugin.loggingOut.remove(finalplayer.getName());
					            		plugin.loggingOutSchedulers.remove(finalplayer.getName());
					            	} else {
					            		plugin.loggingOut.put(finalplayer.getName(), loc);
					            		finalplayer.sendMessage(plugin.utils.replace(plugin.getConfig().getString("combat-messages.countdown")).replaceAll("<seconds>", "" + (10 - finali)));
					            	}
					            }
					        }, 20 * i);
					        schedulers.add(id);
						}
						plugin.loggingOutSchedulers.put(player.getName(), schedulers);
						
						plugin.loggingOutCooldown.add(player.getName());
				        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
				            @Override
				            public void run(){
				            	plugin.loggingOutCooldown.remove(finalplayer.getName());
				            }
				        }, 2 * 20L);
						return true;
					}
				} else if (args[0].equalsIgnoreCase("broadcast")){
					  if (!sender.hasPermission("experia.broadcast")){
						  player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.nopermission")));
						  return true;
					  }
					  
					  String finalString = "";
					  for (int i = 1; i < args.length; i++) {
						  finalString += args[i] + ' ';
					  }
					  finalString = finalString.trim();
					  if (finalString.equals("")) return false;
					  for (Player p : Bukkit.getOnlinePlayers()){
						  p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[Experia] " + ChatColor.RESET + plugin.utils.replace(finalString) + ChatColor.DARK_AQUA + " [" + sender.getName() + "]");
					  }
					  
					  return true;
				}
		  } else if (cmd.getName().equalsIgnoreCase("kit")){
		   if (args.length == 1){
		    if (!player.hasPermission("experia.kit." + args[0])){
		     player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.kitnopermission")));
		     return true;
		    }
		    List<String> list = plugin.getConfig().getStringList("kitnames");
		    if (list.contains(args[0])){
		    	plugin.giveKit.put(player.getName(), args[0]);
			    player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.kit")).replaceAll("<kitname>", args[0]));
		    } else {
		    	player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.kitdoesntexist")));
		    }
		    return true;
			} else if (args.length == 0){
				plugin.menu.open(player);
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("setbook")){
			  if (!sender.hasPermission("experia.setbook")){
				  sender.sendMessage(plugin.plugin.utils.replace(plugin.plugin.getConfig().getString("messages.nopermission")));
				  return true;
			  }
			  plugin.book.saveBook(player, "book");
			  player.sendMessage("You have set the book");
			  return true;
		}
		return false;
	}
}
