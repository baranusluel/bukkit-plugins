package com.barancode.mc.ddg.survival;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SurvivalCommands implements CommandExecutor{
	
	Main plugin;
	HashMap<String, String> pendingtpas = new HashMap<String, String>();
	
	public SurvivalCommands(Main plugin){
		this.plugin = plugin;
	}
	
	  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	  {
		  if (cmd.getName().equalsIgnoreCase("broadcast")){
			  
			  
			  if (!sender.hasPermission("survival.broadcast")){
				  sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("command-permission")));
				  return true;
			  }
			  String finalString = "";
			  for (int i = 0; i < args.length; i++) {
				  finalString += args[i] + ' ';
			  }
			  finalString = finalString.trim();
			  if (finalString.equals("")) return false;
			  for (Player p : Bukkit.getOnlinePlayers()){
				  p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[Survival] " + ChatColor.RESET + plugin.utils.replace(finalString) + ChatColor.DARK_AQUA + " [" + sender.getName() + "]");
			  }
			  
			  return true;
			  
			  
		  } else if (cmd.getName().equalsIgnoreCase("ban") && args.length > 1){
			  
			  
			  if (!sender.hasPermission("survival.admin")){
				  sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("command-permission")));
				  return true;
			  }
				if (plugin.banfile.getCustomConfig().getBoolean("players." + args[0] + ".banned")){
					sender.sendMessage(plugin.utils.replace(plugin.banfile.getCustomConfig().getString("already-banned")));
					return true;
				}
				
				OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
				String reason = "";
				boolean time = false;
				String timesentence = "";
				for (int i = 1; i < args.length; i++) {
					if (args[i].equalsIgnoreCase("t:") || args[i].equalsIgnoreCase("time:")){
						time = true;
					}
				    if (!time) reason += args[i] + ' ';
				    else timesentence += args[i] + ' ';
				}
				reason = reason.trim();
				timesentence = timesentence.trim();
				
				if (time){
					String newtimesentence = "";
					String timeparts[] = timesentence.toLowerCase().split(" ");
					int days = 0;
					int hours = 0;
					int minutes = 0;
					for (String s : timeparts){
						if (s.contains("d")){
							s = s.replaceAll("d", "");
							days = Integer.parseInt(s);
							if (days == 1) newtimesentence += "1 " + plugin.banfile.getCustomConfig().getString("day") + " ";
							else if (days > 1) newtimesentence += days + " " + plugin.banfile.getCustomConfig().getString("days") + " ";
						} else if (s.contains("h")){
							s = s.replaceAll("h", "");
							hours = Integer.parseInt(s);
							if (hours == 1) newtimesentence += "1 " + plugin.banfile.getCustomConfig().getString("hour") + " ";
							else if (hours > 1) newtimesentence += hours + " " + plugin.banfile.getCustomConfig().getString("hours") + " ";
						} else if (s.contains("m")){
							s = s.replaceAll("m", "");
							minutes = Integer.parseInt(s);
							if (minutes == 1) newtimesentence += "1 " + plugin.banfile.getCustomConfig().getString("minute") + " ";
							else if (minutes > 1) newtimesentence += minutes + " " + plugin.banfile.getCustomConfig().getString("minutes") + " ";
						}
					}
					newtimesentence = newtimesentence.trim();
					
					if (player.isOnline()){
						String bannedmessage = plugin.banfile.getCustomConfig().getString("temp-bannedmessage");
						bannedmessage = bannedmessage.replaceAll("<banner>", sender.getName());
						bannedmessage = bannedmessage.replaceAll("<reason>", reason);
						bannedmessage = bannedmessage.replaceAll("<time>", newtimesentence);
						Bukkit.getPlayer(args[0]).kickPlayer(plugin.utils.replace(bannedmessage));
					}
					
					String bannermessage = plugin.banfile.getCustomConfig().getString("temp-bannermessage");
					bannermessage = bannermessage.replaceAll("<banned>", args[0]);
					bannermessage = bannermessage.replaceAll("<reason>", reason);
					bannermessage = bannermessage.replaceAll("<time>", newtimesentence);
					sender.sendMessage(plugin.utils.replace(bannermessage));
					
					String broadcast = plugin.banfile.getCustomConfig().getString("temp-banbroadcast");
					broadcast = broadcast.replaceAll("<banner>", sender.getName());
					broadcast = broadcast.replaceAll("<reason>", reason);
					broadcast = broadcast.replaceAll("<banned>", args[0]);
					broadcast = broadcast.replaceAll("<time>", newtimesentence);
					for (Player p : Bukkit.getOnlinePlayers()){
						if (p.getName() != sender.getName()){
							p.sendMessage(plugin.utils.replace(broadcast));
						}
					}
					
					plugin.banfile.getCustomConfig().set("players." + args[0] + ".banned", true);
					plugin.banfile.getCustomConfig().set("players." + args[0] + ".banner", sender.getName());
					plugin.banfile.getCustomConfig().set("players." + args[0] + ".reason", reason);
					long milli = new Date().getTime();
					milli = milli + (days * 24 * 60 * 60 * 1000) + (hours * 60 * 60 * 1000) + (minutes * 60 * 1000);
					plugin.banfile.getCustomConfig().set("players." + args[0] + ".time", milli);
					plugin.banfile.saveCustomConfig();
				} else {
					if (player.isOnline()){
						String bannedmessage = plugin.banfile.getCustomConfig().getString("permanent-bannedmessage");
						bannedmessage = bannedmessage.replaceAll("<banner>", sender.getName());
						bannedmessage = bannedmessage.replaceAll("<reason>", reason);
						Bukkit.getPlayer(args[0]).kickPlayer(plugin.utils.replace(bannedmessage));
					}
					
					String bannermessage = plugin.banfile.getCustomConfig().getString("permanent-bannermessage");
					bannermessage = bannermessage.replaceAll("<banned>", args[0]);
					bannermessage = bannermessage.replaceAll("<reason>", reason);
					sender.sendMessage(plugin.utils.replace(bannermessage));
					
					String broadcast = plugin.banfile.getCustomConfig().getString("permanent-banbroadcast");
					broadcast = broadcast.replaceAll("<banner>", sender.getName());
					broadcast = broadcast.replaceAll("<reason>", reason);
					broadcast = broadcast.replaceAll("<banned>", args[0]);
					for (Player p : Bukkit.getOnlinePlayers()){
						if (p.getName() != sender.getName()){
							p.sendMessage(plugin.utils.replace(broadcast));
						}
					}
					
					plugin.banfile.getCustomConfig().set("players." + args[0] + ".banned", true);
					plugin.banfile.getCustomConfig().set("players." + args[0] + ".banner", sender.getName());
					plugin.banfile.getCustomConfig().set("players." + args[0] + ".reason", reason);
					plugin.banfile.saveCustomConfig();
				}
				
				return true;
				
				
			} else if (cmd.getName().equalsIgnoreCase("kick") && args.length > 1){
				
				
				  if (!sender.hasPermission("survival.admin")){
					  sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("command-permission")));
					  return true;
				  }
				if (!Bukkit.getOfflinePlayer(args[0]).isOnline()){
					sender.sendMessage(plugin.utils.replace(plugin.banfile.getCustomConfig().getString("notonline")));
					return true;
				}
				String reason = "";
				for (int i = 1; i < args.length; i++) {
				    reason += args[i] + ' ';
				}
				
				String kickedmessage = plugin.banfile.getCustomConfig().getString("kickedmessage");
				kickedmessage = kickedmessage.replaceAll("<kicker>", sender.getName());
				kickedmessage = kickedmessage.replaceAll("<reason>", reason);
				Bukkit.getPlayer(args[0]).kickPlayer(plugin.utils.replace(kickedmessage));
				
				String kickermessage = plugin.banfile.getCustomConfig().getString("kickermessage");
				kickermessage = kickermessage.replaceAll("<kicked>", args[0]);
				kickermessage = kickermessage.replaceAll("<reason>", reason);
				sender.sendMessage(plugin.utils.replace(kickermessage));
				
				String broadcast = plugin.banfile.getCustomConfig().getString("kickbroadcast");
				broadcast = broadcast.replaceAll("<kicker>", sender.getName());
				broadcast = broadcast.replaceAll("<reason>", reason);
				broadcast = broadcast.replaceAll("<kicked>", args[0]);
				for (Player p : Bukkit.getOnlinePlayers()){
					if (p.getName() != sender.getName()){
						p.sendMessage(plugin.utils.replace(broadcast));
					}
				}
				
				return true;
				
				
			} else if (cmd.getName().equalsIgnoreCase("unban") && args.length == 1){
				
				
				  if (!sender.hasPermission("survival.admin")){
					  sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("command-permission")));
					  return true;
				  }
				if (!plugin.banfile.getCustomConfig().getBoolean("players." + args[0] + ".banned")){
					sender.sendMessage(plugin.utils.replace(plugin.banfile.getCustomConfig().getString("not-banned")));
					return true;
				}
				
				if (sender.getName().equals(plugin.banfile.getCustomConfig().getString("players." + args[0] + ".banner"))){
					plugin.banfile.getCustomConfig().set("players." + args[0], "");
					plugin.banfile.saveCustomConfig();
					String banner = plugin.banfile.getCustomConfig().getString("unban");
					banner = banner.replaceAll("<banned>", args[0]);
					sender.sendMessage(plugin.utils.replace(banner));
					String broadcast = plugin.banfile.getCustomConfig().getString("unbanbroadcast");
					broadcast = broadcast.replaceAll("<banned>", args[0]);
					broadcast = broadcast.replaceAll("<banner>", sender.getName());
					for (Player p : Bukkit.getOnlinePlayers()){
						if (p.getName() != sender.getName()){
							p.sendMessage(plugin.utils.replace(broadcast));
						}
					}
				} else {
					String message = plugin.banfile.getCustomConfig().getString("cannotunban");
					message = message.replaceAll("<banner>", plugin.banfile.getCustomConfig().getString("players." + args[0] + ".banner"));
					message = message.replaceAll("<banned>", args[0]);
					sender.sendMessage(plugin.utils.replace(message));
				}
				return true;
				
				
			} else if (cmd.getName().equalsIgnoreCase("tpa") && args.length == 1){
				
				
				Player player;
				if (sender instanceof Player) {
			        player = (Player) sender;
			    } else {
			        sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
			        return true;
			    }
								
				if (!Bukkit.getOfflinePlayer(args[0]).isOnline()){
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("tpa-not-online")).replaceAll("<player>", args[0]));
					return true;
				}
				Player target = Bukkit.getPlayer(args[0]);
				
				player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("tpa-asker")).replaceAll("<player>", args[0]));
				target.sendMessage(plugin.utils.replace(plugin.getConfig().getString("tpa-receiver")).replaceAll("<player>", player.getName()));
				pendingtpas.put(target.getName(), player.getName());
				
				Menu menu;
				menu = plugin.menuTPA.clone();
				menu.setName(plugin.utils.replace(plugin.getConfig().getString("menu.tpa.name")).replaceAll("<player>", player.getName()));
				menu.open(target);
				
				return true;
				
				
			} else if ((cmd.getName().equalsIgnoreCase("tpaccept") || cmd.getName().equalsIgnoreCase("tpaaccept")) && args.length == 0){
				
				
				Player target;
				if (sender instanceof Player) {
			        target = (Player) sender;
			    } else {
			    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
			        return true;
			    }
				
				if (!pendingtpas.containsKey(target.getName())){
					target.sendMessage(plugin.utils.replace(plugin.getConfig().getString("tpa-not-receive")));
					return true;
				}
				
				String playername = pendingtpas.get(target.getName());
				
				if (!Bukkit.getOfflinePlayer(playername).isOnline()){
					target.sendMessage(plugin.utils.replace(plugin.getConfig().getString("tpa-not-online")).replaceAll("<player>", args[0]));
					return true;
				}
				Player player = Bukkit.getPlayer(playername);
				
				pendingtpas.remove(target.getName());
				
				player.teleport(target);
				player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("tpa-success-asker")).replaceAll("<player>", target.getName()));
				target.sendMessage(plugin.utils.replace(plugin.getConfig().getString("tpa-success-receiver")).replaceAll("<player>", player.getName()));
				
				return true;
				
				
			} else if (cmd.getName().equalsIgnoreCase("tpdeny") && args.length == 0){
				
				
				Player target;
				if (sender instanceof Player) {
			        target = (Player) sender;
			    } else {
			    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
			        return true;
			    }
				
				if (!pendingtpas.containsKey(target.getName())){
					target.sendMessage(plugin.utils.replace(plugin.getConfig().getString("tpa-not-receive")));
					return true;
				}
				
				String playername = pendingtpas.get(target.getName());
				
				if (!Bukkit.getOfflinePlayer(playername).isOnline()){
					target.sendMessage(plugin.utils.replace(plugin.getConfig().getString("tpa-not-online")).replaceAll("<player>", args[0]));
					return true;
				}
				Player player = Bukkit.getPlayer(playername);
				
				pendingtpas.remove(target.getName());
				
				player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("tpa-deny-asker")).replaceAll("<player>", target.getName()));
				target.sendMessage(plugin.utils.replace(plugin.getConfig().getString("tpa-deny-receiver")).replaceAll("<player>", player.getName()));
				
				return true;
				
				
			} else if (cmd.getName().equalsIgnoreCase("chunk")){
				
				
				if (args.length == 1){
					if (args[0].equalsIgnoreCase("declaim")){
						  if (!sender.hasPermission("survival.admin")){
							  sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("command-permission")));
							  return true;
						  }
						Player player;
						if (sender instanceof Player) {
							player = (Player) sender;
					    } else {
					    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
					        return true;
					    }
						String owner = plugin.db.getOwner(player.getLocation().getChunk());
						if (owner.equals("")){
							sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("not-claimed")));
					        return true;
						}
						plugin.db.declaimChunk(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("declaim")));
						return true;
					} else if (args[0].equalsIgnoreCase("info")){
						  if (!sender.hasPermission("survival.admin")){
							  sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("command-permission")));
							  return true;
						  }
						Player player;
						if (sender instanceof Player) {
							player = (Player) sender;
					    } else {
					    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
					        return true;
					    }
						String owner = plugin.db.getOwner(player.getLocation().getChunk());
						if (owner.equals("")){
							player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("not-claimed")));
							return true;
						}
						int milli = (int)System.currentTimeMillis() - plugin.db.getTime(player.getLocation().getChunk());
						milli = milli / (1000 * 60);
						int minutes = (int) (milli % 60);
						milli /= 60;
						int hours = (int) (milli % 24);
						milli /= 24;
						int days = (int) milli;
						String time = "";
						if (days == 1) time += "1 " + plugin.getConfig().getString("day") + " ";
						else if (days > 1) time += days + " " + plugin.getConfig().getString("days") + " ";
						if (hours == 1) time += "1 " + plugin.getConfig().getString("hour") + " ";
						else if (hours > 1) time += hours + " " + plugin.getConfig().getString("hours") + " ";
						if (minutes == 1) time += "1 " + plugin.getConfig().getString("minute") + " ";
						else if (minutes > 1) time += minutes + " " + plugin.getConfig().getString("minutes") + " ";
						
						if (time.equals("")) time = "< 1 " + plugin.banfile.getCustomConfig().getString("minute") + " ";
						time = time.trim();
						
						String message = plugin.getConfig().getString("info");
						message = message.replaceAll("<owner>", owner);
						message = message.replaceAll("<time>", time);
						player.sendMessage(plugin.utils.replace(message));
						return true;
					} else if (args[0].equalsIgnoreCase("claim")){
						Player player;
						if (sender instanceof Player) {
							player = (Player) sender;
					    } else {
					    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
					        return true;
					    }
						int power = plugin.db.getPower(player.getName());
						if (power < 10){
					        sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("not-enough-power")));
					        return true;
						}
						String owner = plugin.db.getOwner(player.getLocation().getChunk());
						if (!owner.equals("")){
							sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("already-claimed")).replaceAll("<owner>", owner));
					        return true;
						}
						plugin.db.claimChunk(player, player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());
						plugin.db.chunkClaimPrice(player);
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("claim")));
						return true;
					}
				}
				
				if (args.length > 0 && args[0].equalsIgnoreCase("setowner")){
					Player player;
					if (sender instanceof Player) {
						player = (Player) sender;
				    } else {
				    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
				        return true;
				    }
					if (args.length != 2){
						player.sendMessage("/chunk setowner <name>");
						return true;
					}
					String oldowner = plugin.db.getOwner(player.getLocation().getChunk());
					if (oldowner.equals("")){
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("not-claimed")));
						return true;
					}
					plugin.db.setOwner(player.getLocation().getChunk(), args[1]);
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("set-owner")).replaceAll("<player>", args[1]));
					return true;
				}
				
				
			} else if (cmd.getName().equalsIgnoreCase("setpower")){
				
				
				  if (!sender.hasPermission("survival.admin")){
					  sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("command-permission")));
					  return true;
				  }
				if (args.length == 2){
					plugin.db.setPower(args[0], Integer.parseInt(args[1]));
					sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("set-power")).replaceAll("<player>", args[0]).replaceAll("<power>", args[1]));
					return true;
				}
				
				
			} else if (cmd.getName().equalsIgnoreCase("setpoints")){
				
				
				  if (!sender.hasPermission("survival.admin")){
					  sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("command-permission")));
					  return true;
				  }
				if (args.length == 2){
					plugin.db.setPoints(args[0], Integer.parseInt(args[1]));
					sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("set-points")).replaceAll("<player>", args[0]).replaceAll("<points>", args[1]));
					return true;
				}
				
				
			} else if (cmd.getName().equalsIgnoreCase("power")){
				
				
				if (args.length == 0){
					Player player;
					if (sender instanceof Player) {
						player = (Player) sender;
				    } else {
				    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
				        return true;
				    }
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("power-self")).replaceAll("<power>", "" + plugin.db.getPower(player.getName())));
					return true;
				} else if (args.length == 1){
					sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("power-other")).replaceAll("<power>", "" + plugin.db.getPower(args[0])).replace("<player>", args[0]));
					return true;
				}
				
				
			} else if (cmd.getName().equalsIgnoreCase("points")){
				
				
				if (args.length == 0){
					Player player;
					if (sender instanceof Player) {
						player = (Player) sender;
				    } else {
				    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
				        return true;
				    }
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("points-self")).replaceAll("<points>", "" + plugin.db.getPoints(player.getName())));
					return true;
				} else if (args.length == 1){
					sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("points-other")).replaceAll("<points>", "" + plugin.db.getPoints(args[0])).replace("<player>", args[0]));
					return true;
				}
				
				
			} else if (cmd.getName().equalsIgnoreCase("fly")){
				
				
				  if (!sender.hasPermission("survival.fly")){
					  sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("fly-permission")));
					  return true;
				  }
				  Player player;
					if (sender instanceof Player) {
						player = (Player) sender;
				    } else {
				    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
				        return true;
				    }
					if (plugin.flying.contains(player.getName())){
						plugin.flying.remove(player.getName());
						if (player.hasPermission("survival.doublejump")) player.setAllowFlight(true);
						else player.setAllowFlight(false);
						player.setFlying(false);
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("not-flying")));
					} else {
						plugin.flying.add(player.getName());
						player.setAllowFlight(true);
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("flying")));
					}
					return true;
				
				
			} else if (cmd.getName().equalsIgnoreCase("chunkban") && args.length == 1){
				
				
				  Player player;
					if (sender instanceof Player) {
						player = (Player) sender;
				    } else {
				    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
				        return true;
				    }
					
					if (Bukkit.getPlayer(args[0]).hasPermission("survival.admin")) {
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("ban-admin")));
						return true;
					}
					
				List<Chunk> chunks = plugin.db.getChunks(player.getName());
				for (Chunk chunk : chunks){
					boolean b = plugin.db.banFromChunk(chunk, args[0]);
					if (!b){
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("already-banned")).replaceAll("<player>", args[0]));
						return true;
					}
				}
				
				Chunk chunk = chunks.get(0);
				int x = chunk.getX();
				int z = chunk.getZ();
				do{
					x = x - 1;
					chunk = chunk.getWorld().getChunkAt(x, z);
				} while (chunks.contains(chunk));
				player.teleport(chunk.getBlock(7, chunk.getWorld().getHighestBlockYAt(chunk.getBlock(7, 0, 7).getLocation()), 7).getLocation());
				player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("chunk-ban")).replaceAll("<player>", args[0]));
				return true;
				
				
			} else if (cmd.getName().equalsIgnoreCase("chunkunban") && args.length == 1){
				
				
				  Player player;
					if (sender instanceof Player) {
						player = (Player) sender;
				    } else {
				    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
				        return true;
				    }
					
				List<Chunk> chunks = plugin.db.getChunks(player.getName());
				for (Chunk chunk : chunks){
					boolean b = plugin.db.unbanFromChunk(chunk, args[0]);
					if (!b){
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("not-banned")).replaceAll("<player>", args[0]));
						return true;
					}
				}
				player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("chunk-unban")).replaceAll("<player>", args[0]));
				return true;
				
				
			} else if (cmd.getName().equalsIgnoreCase("sethome")){
				
				
				  Player player;
					if (sender instanceof Player) {
						player = (Player) sender;
				    } else {
				    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
				        return true;
				    }
				plugin.db.setHome(player.getName(), (int)player.getLocation().getX(), (int)player.getLocation().getY(), (int)player.getLocation().getZ());
				player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("set-home")));
				plugin.db.deleteArrowPoint(player.getName(), "Home");
				plugin.db.setArrowPoint(player.getName(), "Home", player.getLocation());
				return true;
				
				
			} else if (cmd.getName().equalsIgnoreCase("home")){
				
				
				 Player player;
					if (sender instanceof Player) {
						player = (Player) sender;
				    } else {
				    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
				        return true;
				    }
					Location loc = plugin.db.getHome(player.getName());
					if (loc != null){
						player.teleport(loc);
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("home")));
					} else {
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("no-home")));
					}
					return true;
				
				
			} else if (cmd.getName().equalsIgnoreCase("chunkmute") && args.length == 1){
				
				
				  Player player;
					if (sender instanceof Player) {
						player = (Player) sender;
				    } else {
				    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
				        return true;
				    }
					
					if (Bukkit.getPlayer(args[0]).hasPermission("survival.admin")) {
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("ban-admin")));
						return true;
					}
					
				List<Chunk> chunks = plugin.db.getChunks(player.getName());
				for (Chunk chunk : chunks){
					boolean b = plugin.db.muteInChunk(chunk, args[0]);
					if (!b){
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("already-muted")).replaceAll("<player>", args[0]));
						return true;
					}
				}
				player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("chunk-mute")).replaceAll("<player>", args[0]));
				return true;
				
				
			} else if (cmd.getName().equalsIgnoreCase("chunkunmute") && args.length == 1){
				
				
				  Player player;
					if (sender instanceof Player) {
						player = (Player) sender;
				    } else {
				    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
				        return true;
				    }
					
				List<Chunk> chunks = plugin.db.getChunks(player.getName());
				for (Chunk chunk : chunks){
					boolean b = plugin.db.unmuteInChunk(chunk, args[0]);
					if (!b){
						player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("not-muted")).replaceAll("<player>", args[0]));
						return true;
					}
				}
				player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("chunk-unmute")).replaceAll("<player>", args[0]));
				return true;
				
				
			} else if (cmd.getName().equalsIgnoreCase("friend") && args.length == 2){
				
				
				  Player player;
					if (sender instanceof Player) {
						player = (Player) sender;
				    } else {
				    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
				        return true;
				    }
				if (args[0].equalsIgnoreCase("add")){
					List<Chunk> chunks = plugin.db.getChunks(player.getName());
					
					int size = plugin.db.getFriends(chunks.get(0)).size();
					if (player.hasPermission("survival.max")){
						if (size > 53){
							player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("too-many-friends")).replaceAll("<number>", "54"));
							return true;
						}
					} else {
						if (size > 9){
							 player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("too-many-friends")).replaceAll("<number>", "10"));
							 return true;
						}
					}
					

					for (Chunk chunk : chunks){
						boolean b = plugin.db.addFriend(chunk, args[1]);
						if (!b){
							player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("friend-already-added")).replaceAll("<player>", args[1]));
							return true;
						}
					}
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("add-friend")).replaceAll("<player>", args[1]));
					return true;
				} else if (args[0].equalsIgnoreCase("remove")){
					List<Chunk> chunks = plugin.db.getChunks(player.getName());
					for (Chunk chunk : chunks){
						boolean b = plugin.db.removeFriend(chunk, args[1]);
						if (!b){
							player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("not-a-friend")).replaceAll("<player>", args[1]));
							return true;
						}
					}
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("remove-friend")).replaceAll("<player>", args[1]));
					return true;
				}
				
				
			} else if (cmd.getName().equalsIgnoreCase("vote") && args.length == 1){
				
				
				Player player;
				if (sender instanceof Player) {
					player = (Player) sender;
			    } else {
			    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
			        return true;
			    }
				
				for (SurvivalVoter sv : plugin.voters){
                	if (sv.c == player.getLocation().getChunk() && sv.p == player){
                		player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("cannot-vote-again")));
                		return true;
                	}
                }
				
				String owner = plugin.db.getOwner(player.getLocation().getChunk());
				
				if (owner.equals(player.getName())){
            		player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("cannot-vote-for-self")));
            		return true;
				}
				
				if (args[0].equalsIgnoreCase("up")){
					plugin.db.setPoints(owner, plugin.db.getPoints(owner) + 1);
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("vote-up")).replaceAll("<player>", owner));
					voteTimer(player.getLocation().getChunk(), player);
					return true;
				} else if (args[0].equalsIgnoreCase("down")){
					plugin.db.setPoints(owner, plugin.db.getPoints(owner) - 1);
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("vote-down")).replaceAll("<player>", owner));
					voteTimer(player.getLocation().getChunk(), player);
					return true;
				}
				
				
			} else if (cmd.getName().equalsIgnoreCase("setbook")){
				
				
				Player player;
				if (sender instanceof Player) {
					player = (Player) sender;
			    } else {
			    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
			        return true;
			    }
				  if (!sender.hasPermission("survival.admin")){
					  sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("command-permission")));
					  return true;
				  }
				  plugin.book.saveBook(player, "book");
				  player.sendMessage("You have set the book");
				  return true;
				  
				
			} else if (cmd.getName().equalsIgnoreCase("togglearrow")){
				
				
				Player player;
				if (sender instanceof Player) {
					player = (Player) sender;
			    } else {
			    	sender.sendMessage(plugin.utils.replace(plugin.getConfig().getString("must-be-player")));
			        return true;
			    }
				if (plugin.arrows.containsKey(player.getName())){
					plugin.oldarrows.put(player.getName(), plugin.arrows.get(player.getName()));
					plugin.arrows.remove(player.getName());
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("menu.arrow.toggle.select-off")));
				} else if (plugin.oldarrows.containsKey(player.getName())){
					plugin.arrows.put(player.getName(), plugin.oldarrows.get(player.getName()));
					plugin.oldarrows.remove(player.getName());
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("menu.arrow.toggle.select-on")));
				} else {
					player.sendMessage(plugin.utils.replace(plugin.getConfig().getString("menu.arrow.toggle.select-not-set")));
				}
				return true;
				
				
			}
		  return false;
	  }
	  
	  public void voteTimer(final Chunk c, final Player p){
		  plugin.voters.add(new SurvivalVoter(c, p));
	        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
	            @Override
	            public void run() {
	                for (SurvivalVoter sv : plugin.voters){
	                	if (sv.c == c && sv.p == p) plugin.voters.remove(sv);
	                }
	            }
	        }, 24*60*60*20L);
	  }
}
