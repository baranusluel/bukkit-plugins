package com.barancode.mc.ddg.survival;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class SurvivalDatabase {
	
	  Main plugin;
	  Connection con;

	  public SurvivalDatabase(Main plugin)
	  {
	    this.plugin = plugin;
	  }
	  
	  public void initialize(){	 
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				System.out.println("MySQL JDBC driver missing");
				e.printStackTrace();
				return;
			}
			try {
				con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
			} catch (SQLException e) {
				System.out.println("MySQL connection failed!");
				e.printStackTrace();
				return;
			}
		 
			if (con != null) {
				Statement statement = null;
				try {
					statement = con.createStatement();
					statement.execute("CREATE TABLE IF NOT EXISTS Chunks(x BIGINT, z BIGINT, time BIGINT, player TEXT);");
					statement.execute("CREATE TABLE IF NOT EXISTS Players(points BIGINT, power BIGINT, player TEXT);");
					statement.execute("CREATE TABLE IF NOT EXISTS ChunkBans(x BIGINT, z BIGINT, banned TEXT);");
					statement.execute("CREATE TABLE IF NOT EXISTS Homes(x BIGINT, y BIGINT, z BIGINT, player TEXT);");
					statement.execute("CREATE TABLE IF NOT EXISTS ChunkMutes(x BIGINT, z BIGINT, muted TEXT);");
					statement.execute("CREATE TABLE IF NOT EXISTS ChunkFriends(x BIGINT, z BIGINT, friend TEXT);");
					statement.execute("CREATE TABLE IF NOT EXISTS ArrowPoints(x BIGINT, y BIGINT, z BIGINT, player TEXT, name TEXT);");
					
					ResultSet rs = statement.executeQuery("SELECT * FROM Chunks;");
				    while (rs.next()){
				  	    plugin.dbchunks.add(new SurvivalChunk(rs.getInt("x"), rs.getInt("z"), rs.getInt("time"), rs.getString("player")));
				    }
				    
					rs = statement.executeQuery("SELECT * FROM Players;");
				    while (rs.next()){
				  	    plugin.dbplayers.add(new SurvivalPlayer(rs.getInt("points"), rs.getInt("power"), rs.getString("player")));
				    }
				    
					rs = statement.executeQuery("SELECT * FROM ChunkBans;");
				    while (rs.next()){
				  	    plugin.dbbans.add(new SurvivalChunkBan(rs.getInt("x"), rs.getInt("z"), rs.getString("banned")));
				    }
				    
					rs = statement.executeQuery("SELECT * FROM ChunkMutes;");
				    while (rs.next()){
				  	    plugin.dbmutes.add(new SurvivalChunkMute(rs.getInt("x"), rs.getInt("z"), rs.getString("muted")));
				    }
				    
					rs = statement.executeQuery("SELECT * FROM ChunkFriends;");
				    while (rs.next()){
				  	    plugin.dbfriends.add(new SurvivalFriend(rs.getInt("x"), rs.getInt("z"), rs.getString("friend")));
				    }
				    
					rs = statement.executeQuery("SELECT * FROM Homes;");
				    while (rs.next()){
				    	SurvivalHome sh = new SurvivalHome(rs.getString("player"), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
				  	    plugin.dbhomes.add(sh);
				    }
				    
					rs = statement.executeQuery("SELECT * FROM ArrowPoints;");
				    while (rs.next()){
				    	ArrowPoint ap = new ArrowPoint(rs.getString("player"), rs.getString("name"), new Location(Bukkit.getWorld("world"), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")));
				  	    plugin.dbarrowpoints.add(ap);
				    }
				} catch (SQLException e) {
					System.out.println("Initializing MySQL failed!");
					e.printStackTrace();
				} finally {
					if (statement != null) {
						try {
							statement.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}		 
				}
			} else {
				System.out.println("MySQL connection failed!");
			}
		  
		  
		  
		  /*try{
			  Class.forName("com.mysql.jdbc.Driver");
			  
			  Connection conn = DriverManager.getConnection(url, user, pass);
			  conn.setAutoCommit(false);
			  Statement statement = con.createStatement();
			  statement.executeUpdate("CREATE TABLE Chunks(x INTEGER, z INTEGER, time INTEGER, player TEXT);");
			  con.commit();
			  statement.close(); 
			  conn.close();
		  } catch (Exception e){
			  e.printStackTrace();
		  }*/
	  }
	  
	  @SuppressWarnings("deprecation")
	public void claimChunk(final Player p, final int x, final int z){
			if (con != null) {
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Statement statement = null;
						try {
							statement = con.createStatement();
							statement.execute("INSERT INTO Chunks(x, z, time, player) VALUES(" + x + ", " + z + ", " + (int)System.currentTimeMillis() + ", \"" + p.getName() + "\");");
							plugin.dbchunks.add(new SurvivalChunk(x, z, (int)System.currentTimeMillis(), p.getName()));
							
							List<String> mutePlayers = new LinkedList<String>();
							for (SurvivalChunkMute sm : plugin.dbmutes){
								Chunk c = Bukkit.getWorld("world").getChunkAt(sm.x, sm.z);
								String owner = getOwner(c);
								if (p.getName().equals(owner) && !mutePlayers.contains(sm.muted)) mutePlayers.add(sm.muted);
							}
							for (String s : mutePlayers) muteInChunk(Bukkit.getWorld("world").getChunkAt(x, z), s);
							
							List<String> banPlayers = new LinkedList<String>();
							for (SurvivalChunkBan sb : plugin.dbbans){
								Chunk c = Bukkit.getWorld("world").getChunkAt(sb.x, sb.z);
								String owner = getOwner(c);
								if (p.getName().equals(owner) && !banPlayers.contains(sb.banned)) banPlayers.add(sb.banned);
							}
							for (String s : banPlayers) banFromChunk(Bukkit.getWorld("world").getChunkAt(x, z), s);
							
							List<String> friendPlayers = new LinkedList<String>();
							for (SurvivalFriend sf : plugin.dbfriends){
								Chunk c = Bukkit.getWorld("world").getChunkAt(sf.x, sf.z);
								String owner = getOwner(c);
								if (p.getName().equals(owner) && !friendPlayers.contains(sf.friend)) friendPlayers.add(sf.friend);
							}
							for (String s : friendPlayers) addFriend(Bukkit.getWorld("world").getChunkAt(x, z), s);
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}		 
						}
		            }
		        }, 0L);
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
	  }
	  
	  public List<Chunk> getChunks(String p){
		  List<Chunk> chunks = new LinkedList<Chunk>();
		  for (SurvivalChunk sc : plugin.dbchunks){
			  if (sc.player.equals(p)){
				  chunks.add(Bukkit.getWorld("world").getChunkAt(new Location(Bukkit.getWorld("world"), sc.x * 16, 0, sc.z * 16)));
			  }
		  }
		  return chunks;
			/*if (con != null) {
				List<Chunk> chunks = new LinkedList<Chunk>();
				Statement statement = null;
				ResultSet rs;
				try {
					statement = con.createStatement();
					rs = statement.executeQuery("SELECT * FROM Chunks WHERE player=\"" + p.getName() + "\";");
				    while (rs.next()){
				  	    chunks.add(p.getWorld().getChunkAt(rs.getInt("x"), rs.getInt("z")));
				    }
				} catch (SQLException e) {
					System.out.println("MySQL connection failed!");
					e.printStackTrace();
				} finally {
					if (statement != null) {
						try {
							statement.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				return chunks;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
				return null;
			}*/
	  }
	  
	  public List<Chunk> getAllChunks(){
		  List<Chunk> chunks = new LinkedList<Chunk>();
		  for (SurvivalChunk sc : plugin.dbchunks){
			  chunks.add(Bukkit.getWorld("world").getChunkAt(new Location(Bukkit.getWorld("world"), sc.x * 16, 0, sc.z * 16)));
		  }
		  return chunks;
	  }
	  
	  public String getOwner(Chunk c){
		  for (SurvivalChunk sc : plugin.dbchunks){
			  if (sc.x == c.getX() && sc.z == c.getZ()){
				  return sc.player;
			  }
		  }
		  return "";
			/*if (con != null) {
				Statement statement = null;
				ResultSet rs;
				int x = c.getX();
				int z = c.getZ();
				try {
					statement = con.createStatement();
					rs = statement.executeQuery("SELECT * FROM Chunks WHERE x=" + x + " AND z=" + z + ";");
					while (rs.next()){
						return rs.getString("player");
					}
				} catch (SQLException e) {
					System.out.println("MySQL connection failed!");
					e.printStackTrace();
				} finally {
					if (statement != null) {
						try {
							statement.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
			return "";*/
	  }
	  
	  public String getOwner(int x, int z){
		  for (SurvivalChunk sc : plugin.dbchunks){
			  if (sc.x == x && sc.z == z){
				  return sc.player;
			  }
		  }
		  return "";
	  }
	  
	  public int getTime(Chunk c){
		  for (SurvivalChunk sc : plugin.dbchunks){
			  if (sc.x == c.getX() && sc.z == c.getZ()){
				  return sc.time;
			  }
		  }
		  return 0;
			/*if (con != null) {
				Statement statement = null;
				ResultSet rs;
				int x = c.getX();
				int z = c.getZ();
				try {
					statement = con.createStatement();
					rs = statement.executeQuery("SELECT * FROM Chunks WHERE x=" + x + " AND z=" + z + ";");
					while (rs.next()){
						return rs.getInt("time");
					}
				} catch (SQLException e) {
					System.out.println("MySQL connection failed!");
					e.printStackTrace();
				} finally {
					if (statement != null) {
						try {
							statement.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
			return 0;*/
	  }
	  
	  @SuppressWarnings("deprecation")
	public void declaimChunk(final int x, final int z){	  
			if (con != null) {
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Statement statement = null;
						try {
							String owner = getOwner(x, z);
							statement = con.createStatement();
							statement.execute("DELETE FROM Chunks WHERE x=" + x + " AND z=" + z + ";");
								
							    for (Iterator<SurvivalFriend> it = plugin.dbfriends.iterator(); it.hasNext(); ){
							    	SurvivalFriend sf = it.next();
									if (sf.x == x && sf.z == z) {
										it.remove();
										removeFriend(Bukkit.getWorld("world").getChunkAt(sf.x, sf.z), sf.friend);
									}
							    }
							    for (Iterator<SurvivalChunkBan> it = plugin.dbbans.iterator(); it.hasNext(); ){
							    	SurvivalChunkBan sb = it.next();
									if (sb.x == x && sb.z == z){
										it.remove();
										unbanFromChunk(Bukkit.getWorld("world").getChunkAt(sb.x, sb.z), sb.banned);
									}
							    }
							    for (Iterator<SurvivalChunkMute> it = plugin.dbmutes.iterator(); it.hasNext(); ){
							    	SurvivalChunkMute sm = it.next();
									if (sm.x == x && sm.z == z){
										it.remove();
										unmuteInChunk(Bukkit.getWorld("world").getChunkAt(sm.x, sm.z), sm.muted);
									}
							    }
								
								
							    for (Iterator<SurvivalChunk> it = plugin.dbchunks.iterator(); it.hasNext(); ){
							    	SurvivalChunk sc = it.next();
									if (sc.x == x && sc.z == z) it.remove();
							    }
							    
							    setPower(owner, getPower(owner) + 5);
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}		 
						}
		            }
		        }, 0L);
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
	  }
	  
	  
	  @SuppressWarnings("deprecation")
	public void increasePower(final Player p){
			if (con != null) {
				
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Statement statement = null;
						ResultSet rs;
						try {
							statement = con.createStatement();
							rs = statement.executeQuery("SELECT * FROM Players WHERE player=\"" + p.getName() + "\";");
							
							int finalpower;
							if (rs.next()){
								int power = rs.getInt("power");
								if (power > 199){
									if (plugin.listener.powerIncreasingSchedulers.containsKey(p)){
										plugin.scheduler.cancelTask(plugin.listener.powerIncreasingSchedulers.get(p));
										plugin.listener.powerIncreasingSchedulers.remove(p);
									}
									return;
								}
								
								statement.execute("UPDATE Players SET power=" + (power + 1) + " WHERE player=\"" + p.getName() + "\";");
								
								  for (SurvivalPlayer sp : plugin.dbplayers){
									  if (sp.player.equals(p.getName())){
										  sp.power = power + 1;
									  }
								  }
								  finalpower = power + 1;
							} else {
								statement.execute("INSERT INTO Players(points, power, player) VALUES(10, 10, \"" + p.getName() + "\");");
								plugin.dbplayers.add(new SurvivalPlayer(10, 10, p.getName()));
								finalpower = 10;
							}
							
							
							Scoreboard board = plugin.scoreboards.get(p.getName());
							if (board != null){
								Objective objective = board.getObjective("scoreboard");
								objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-power")))).setScore(finalpower);
								objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-points")))).setScore(plugin.db.getPoints(p.getName()));
								p.setScoreboard(board);
							} else {
								Scoreboard board2 = plugin.manager.getNewScoreboard();
							    Objective objective = board2.registerNewObjective("scoreboard", "dummy");
							    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
							    objective.setDisplayName(plugin.utils.replace(plugin.getConfig().getString("scoreboard")));
							    Score power = objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-power"))));
							    power.setScore(finalpower);
							    Score points = objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-points"))));
							    points.setScore(plugin.db.getPoints(p.getName()));
							    p.setScoreboard(board2);
								plugin.scoreboards.put(p.getName(), board2);
							}
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
		            }
		        }, 0L);
		        
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
	  }
	  
	  public int getPower(String player){
		  
		  for (SurvivalPlayer sp : plugin.dbplayers){
			  if (sp.player.equals(player)){
				  return sp.power;
			  }
		  }
		  return 0;
		  
			/*if (con != null) {
				
				Statement statement = null;
				ResultSet rs;
				try {
					statement = con.createStatement();
					rs = statement.executeQuery("SELECT * FROM Players WHERE player=\"" + p.getName() + "\";");
					if (rs.next()) return rs.getInt("power");
					else return 0;
				} catch (SQLException e) {
					System.out.println("MySQL connection failed!");
					e.printStackTrace();
				} finally {
					if (statement != null) {
						try {
							statement.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
			return 0;*/
	  }
	  
	  public int getPoints(String player){
		  for (SurvivalPlayer sp : plugin.dbplayers){
			  if (sp.player.equals(player)){
				  return sp.points;
			  }
		  }
		  return 0;
		  
			/*if (con != null) {
				Statement statement = null;
				ResultSet rs;
				try {
					statement = con.createStatement();
					rs = statement.executeQuery("SELECT * FROM Players WHERE player=\"" + p.getName() + "\";");
					if (rs.next()) return rs.getInt("points");
					else return 0;
				} catch (SQLException e) {
					System.out.println("MySQL connection failed!");
					e.printStackTrace();
				} finally {
					if (statement != null) {
						try {
							statement.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
			return 0;*/
	  }
	  
	  @SuppressWarnings("deprecation")
	public void setPower(final String player, final int power){
			if (con != null) {
				
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						  for (SurvivalPlayer sp : plugin.dbplayers){
							  if (sp.player.equals(player)){
									sp.power = power;
							  }
						  }
						  
						Statement statement = null;
						ResultSet rs;
						try {
							statement = con.createStatement();
							rs = statement.executeQuery("SELECT * FROM Players WHERE player=\"" + player + "\";");
							if (rs.next()){
								statement.execute("UPDATE Players SET power=" + power + " WHERE player=\"" + player + "\";");
							} else {
								statement.execute("INSERT INTO Players(points, power, player) VALUES(10, " + power + ", \"" + player + "\");");
							}
							
							if (Bukkit.getOfflinePlayer(player).isOnline()){
								Scoreboard board = plugin.scoreboards.get(player);
								if (board != null){
									Objective objective = board.getObjective("scoreboard");
									objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-power")))).setScore(power);
									objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-points")))).setScore(plugin.db.getPoints(player));
									Bukkit.getPlayer(player).setScoreboard(board);
								} else {
									Scoreboard board2 = plugin.manager.getNewScoreboard();
								    Objective objective = board2.registerNewObjective("scoreboard", "dummy");
								    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
								    objective.setDisplayName(plugin.utils.replace(plugin.getConfig().getString("scoreboard")));
								    Score powerscore = objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-power"))));
								    powerscore.setScore(power);
								    Score points = objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-points"))));
								    points.setScore(plugin.db.getPoints(player));
								    Bukkit.getPlayer(player).setScoreboard(board2);
									plugin.scoreboards.put(player, board2);
								}
							}
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
		            }
		        }, 0L);

			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
	  }
	  
	  @SuppressWarnings("deprecation")
	public void setPoints(final String player, final int points){
			if (con != null) {
				
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						  for (SurvivalPlayer sp : plugin.dbplayers){
							  if (sp.player.equals(player)){
									sp.points = points;
							  }
						  }
						
						Statement statement = null;
						ResultSet rs;
						try {
							statement = con.createStatement();
							rs = statement.executeQuery("SELECT * FROM Players WHERE player=\"" + player + "\";");
							if (rs.next()){
								statement.execute("UPDATE Players SET points=" + points + " WHERE player=\"" + player + "\";");
							} else {
								statement.execute("INSERT INTO Players(points, power, player) VALUES(" + points + ", 10, \"" + player + "\");");
							}
							
							if (Bukkit.getOfflinePlayer(player).isOnline()){
								Scoreboard board = plugin.scoreboards.get(player);
								if (board != null){
									Objective objective = board.getObjective("scoreboard");
									objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-power")))).setScore(plugin.db.getPower(player));
									objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-points")))).setScore(points);
									Bukkit.getPlayer(player).setScoreboard(board);
								} else {
									Scoreboard board2 = plugin.manager.getNewScoreboard();
								    Objective objective = board2.registerNewObjective("scoreboard", "dummy");
								    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
								    objective.setDisplayName(plugin.utils.replace(plugin.getConfig().getString("scoreboard")));
								    Score power = objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-power"))));
								    power.setScore(plugin.db.getPower(player));
								    Score pointsscore = objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-points"))));
								    pointsscore.setScore(points);
								    Bukkit.getPlayer(player).setScoreboard(board2);
									plugin.scoreboards.put(player, board2);
								}
							}
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
		            }
		        }, 0L);
		        
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
	  }
	  
	  @SuppressWarnings("deprecation")
	public void chunkClaimPrice(final Player p){
			if (con != null) {
				
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						  for (SurvivalPlayer sp : plugin.dbplayers){
							  if (sp.player.equals(p.getName())){
									sp.power = sp.power - 10;
							  }
						  }
						Statement statement = null;
						ResultSet rs;
						try {
							statement = con.createStatement();
							rs = statement.executeQuery("SELECT * FROM Players WHERE player=\"" + p.getName() + "\";");
							int newpower;
							if (rs.next()){
								newpower = rs.getInt("power") - 10;
								statement.execute("UPDATE Players SET power=" + newpower + " WHERE player=\"" + p.getName() + "\";");
							} else {
								newpower = 0;
								statement.execute("INSERT INTO Players(points, power, player) VALUES(10, 0, \"" + p.getName() + "\");");
							}
							
							
							Scoreboard board = plugin.scoreboards.get(p.getName());
							if (board != null){
								Objective objective = board.getObjective("scoreboard");
								objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-power")))).setScore(newpower);
								objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-points")))).setScore(plugin.db.getPoints(p.getName()));
								p.setScoreboard(board);
							} else {
								Scoreboard board2 = plugin.manager.getNewScoreboard();
							    Objective objective = board2.registerNewObjective("scoreboard", "dummy");
							    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
							    objective.setDisplayName(plugin.utils.replace(plugin.getConfig().getString("scoreboard")));
							    Score powerscore = objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-power"))));
							    powerscore.setScore(newpower);
							    Score points = objective.getScore(Bukkit.getOfflinePlayer(plugin.utils.replace(plugin.getConfig().getString("scoreboard-points"))));
							    points.setScore(plugin.db.getPoints(p.getName()));
							    p.setScoreboard(board2);
								plugin.scoreboards.put(p.getName(), board2);
							}
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
		            }
		        }, 0L);

			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
	  }
	  
	  @SuppressWarnings("deprecation")
	public boolean unbanFromChunk(final Chunk chunk, final String banned){
			if (con != null) {
				final int x = chunk.getX();
				final int z = chunk.getZ();
				
				if (!isBanned(chunk, banned)) return false;
				
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
		            	Statement statement = null;
						try {
			            	statement = con.createStatement();
							statement.execute("DELETE FROM ChunkBans WHERE x=" + x + " AND z=" + z + " AND banned=\"" + banned + "\";");
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
					    for (Iterator<SurvivalChunkBan> it = plugin.dbbans.iterator(); it.hasNext(); ){
					    	SurvivalChunkBan scb = it.next();
							if (scb.x == x && scb.z == z && scb.banned.equals(banned)){
								it.remove();
							}
					    }
		            }
		        }, 0L);
				return true;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
			return false;
	  }
	  
	  @SuppressWarnings("deprecation")
	public boolean banFromChunk(final Chunk chunk, final String banned){
			if (con != null) {
				final int x = chunk.getX();
				final int z = chunk.getZ();
				
				if (isBanned(chunk, banned)) return false;
					
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
		            	Statement statement = null;
						try {
			            	statement = con.createStatement();
			            	statement.execute("INSERT INTO ChunkBans(x, z, banned) VALUES(" + x + ", " + z + ", \"" + banned + "\");");
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
						plugin.dbbans.add(new SurvivalChunkBan(x, z, banned));
		            }
		        }, 0L);
				return true;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
				return false;
			}
	  }
	  
	  public boolean isBanned(Chunk chunk, String player){
		  for (SurvivalChunkBan scb : plugin.dbbans){
			  if (scb.x == chunk.getX() && scb.z == chunk.getZ() && scb.banned.equals(player)) return true;
		  }
		  return false;
	  }
	  
	  public List<String> getBannedPlayers(Chunk chunk){
		  List<String> players = new LinkedList<String>();
		  for (SurvivalChunkBan scb : plugin.dbbans){
			  if (scb.x == chunk.getX() && scb.z == chunk.getZ()) players.add(scb.banned);
		  }
		  return players;
	  }
	  
	  public boolean isMuted(Chunk chunk, String player){
		  for (SurvivalChunkMute scm : plugin.dbmutes){
			  if (scm.x == chunk.getX() && scm.z == chunk.getZ() && scm.muted.equals(player)) return true;
		  }
		  return false;
	  }
	  
	  public boolean isFriend(Chunk chunk, String player){
		  for (SurvivalFriend sf : plugin.dbfriends){
			  if (sf.x == chunk.getX() && sf.z == chunk.getZ() && sf.friend.equals(player)) return true;
		  }
		  return false;
	  }
	  
	  public List<String> getFriends(Chunk chunk){
		  List<String> friends = new LinkedList<String>();
		  for (SurvivalFriend sf : plugin.dbfriends){
			  if (sf.x == chunk.getX() && sf.z == chunk.getZ()) friends.add(sf.friend);
		  }
		  return friends;
	  }
	  
	  @SuppressWarnings("deprecation")
	public void setHome(final String player, final int x, final int y, final int z){
			if (con != null) {
				boolean contains = false;
				  for (SurvivalHome sh : plugin.dbhomes){
					  if (sh.player.equals(player)){
						  sh.x = x;
						  sh.y = y;
						  sh.z = z;
						  contains = true;
					  }
				  }
				
				if (contains){
			        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			            @Override
			            public void run(){
							Statement statement = null;
							try {
								statement = con.createStatement();
								statement.execute("UPDATE Homes SET x=" + x + ", y=" + y + ", z=" + z + " WHERE player=\"" + player + "\";");
							} catch (SQLException e) {
								System.out.println("MySQL connection failed!");
								e.printStackTrace();
							} finally {
								if (statement != null) {
									try {
										statement.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
							}
			            }
			        }, 0L);
				} else {
					plugin.dbhomes.add(new SurvivalHome(player, x, y, z));
			        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			            @Override
			            public void run(){
							Statement statement = null;
							try {
								statement = con.createStatement();
								statement.execute("INSERT INTO Homes VALUES(" + x + ", " + y + ", " + z + ", \"" + player + "\");");
							} catch (SQLException e) {
								System.out.println("MySQL connection failed!");
								e.printStackTrace();
							} finally {
								if (statement != null) {
									try {
										statement.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
							}
			            }
			        }, 0L);
				}
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
	  }
	  
	  public Location getHome(String player){
		  for (SurvivalHome sh : plugin.dbhomes){
			  if (sh.player.equals(player)){
				  return new Location(Bukkit.getWorld("world"), sh.x, sh.y, sh.z);
			  }
		  }
		  return null;
	  }
	  
	  @SuppressWarnings("deprecation")
	public boolean muteInChunk(final Chunk chunk, final String muted){
			if (con != null) {
				final int x = chunk.getX();
				final int z = chunk.getZ();
				
				if (isMuted(chunk, muted)) return false;
				
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Statement statement = null;
						try {
							statement = con.createStatement();
							statement.execute("INSERT INTO ChunkMutes(x, z, muted) VALUES(" + x + ", " + z + ", \"" + muted + "\");");
							plugin.dbmutes.add(new SurvivalChunkMute(x, z, muted));
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
		            }
		        }, 0L);
				return true;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
				return false;
			}
	  }
	  
	  @SuppressWarnings("deprecation")
	public boolean unmuteInChunk(final Chunk chunk, final String muted){
			if (con != null) {
				final int x = chunk.getX();
				final int z = chunk.getZ();
				
				if (!isMuted(chunk, muted)) return false;
				
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Statement statement = null;
						try {
							statement = con.createStatement();
							statement.execute("DELETE FROM ChunkMutes WHERE x=" + x + " AND z=" + z + " AND muted=\"" + muted + "\";");
						    for (Iterator<SurvivalChunkMute> it = plugin.dbmutes.iterator(); it.hasNext(); ){
						    	SurvivalChunkMute scm = it.next();
								if (scm.x == x && scm.z == z && scm.muted.equals(muted)){
									it.remove();
								}
						    }
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
		            }
		        }, 0L);
		        return true;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
			return false;
	  }
	  
	  @SuppressWarnings("deprecation")
	public boolean addFriend(final Chunk chunk, final String friend){
			if (con != null) {
				final int x = chunk.getX();
				final int z = chunk.getZ();
				
				if (isFriend(chunk, friend)) return false;
				
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Statement statement = null;
						try {
							statement = con.createStatement();					
							statement.execute("INSERT INTO ChunkFriends(x, z, friend) VALUES(" + x + ", " + z + ", \"" + friend + "\");");
							plugin.dbfriends.add(new SurvivalFriend(x, z, friend));
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
		            }
		        }, 0L);
				return true;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
				return false;
			}
	  }
	  
	  @SuppressWarnings("deprecation")
	public boolean removeFriend(final Chunk chunk, final String friend){
			if (con != null) {
				final int x = chunk.getX();
				final int z = chunk.getZ();
				
				if (!isFriend(chunk, friend)) return false;
				
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Statement statement = null;
						try {
							statement = con.createStatement();
							statement.execute("DELETE FROM ChunkFriends WHERE x=" + x + " AND z=" + z + " AND friend=\"" + friend + "\";");
						    for (Iterator<SurvivalFriend> it = plugin.dbfriends.iterator(); it.hasNext(); ){
						    	SurvivalFriend sf = it.next();
								if (sf.x == x && sf.z == z && sf.friend.equals(friend)){
									it.remove();
								}
						    }
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
		            }
		        }, 0L);
		        return true;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
			return false;
	  }
	  
	  @SuppressWarnings("deprecation")
	public void setOwner(final Chunk c, final String p){
			if (con != null) {
				final int x = c.getX();
				final int z = c.getZ();
				  
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Statement statement = null;
						try {
							statement = con.createStatement();
							statement.execute("UPDATE Chunks SET player=\"" + p + "\" WHERE x=" + x + " AND z=" + z + ";");
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
		            }
		        }, 0L);
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
	  }
	  
	  @SuppressWarnings("deprecation")
	public void setArrowPoint(final String player, final String name, Location loc){
			if (con != null) {
				final int x = (int) loc.getX();
				final int y = (int) loc.getY();
				final int z = (int) loc.getZ();
				
				plugin.dbarrowpoints.add(new ArrowPoint(player, name, loc));
				  
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Statement statement = null;
						try {
							statement = con.createStatement();
							statement.execute("INSERT INTO ArrowPoints(x, y, z, player, name) VALUES(" + x + ", " + y + ", " + z + ", \"" + player + "\", \"" + name + "\");");
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
		            }
		        }, 0L);
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
	  }
	  
	  public List<ArrowPoint> getArrowPoints(String player){
		  List<ArrowPoint> list = new LinkedList<ArrowPoint>();
		  for (ArrowPoint ap : plugin.dbarrowpoints){
			  if (ap.player.equals(player)) list.add(ap);
		  }
		  return list;
	  }
	  
	  @SuppressWarnings("deprecation")
	public void deleteArrowPoint(final String player, final String name){
			if (con != null) {
		        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		            @Override
		            public void run(){
						Statement statement = null;
						try {
							statement = con.createStatement();
							statement.execute("DELETE FROM ArrowPoints WHERE player=\"" + player + "\" AND name=\"" + name + "\";");
						    for (Iterator<ArrowPoint> it = plugin.dbarrowpoints.iterator(); it.hasNext(); ){
						    	ArrowPoint ap = it.next();
								if (ap.player.equals(player) && ap.name.equals(name)){
									it.remove();
								}
						    }
						} catch (SQLException e) {
							System.out.println("MySQL connection failed!");
							e.printStackTrace();
						} finally {
							if (statement != null) {
								try {
									statement.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
		            }
		        }, 0L);
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
	  }
}
