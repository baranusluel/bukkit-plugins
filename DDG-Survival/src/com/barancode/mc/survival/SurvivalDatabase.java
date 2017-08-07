package com.barancode.mc.survival;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
				  	    plugin.dbchunks.add(new SurvivalChunk(rs.getInt("x"), rs.getInt("z"), rs.getInt("time"), UUID.fromString(rs.getString("player"))));
				    }
				    
					rs = statement.executeQuery("SELECT * FROM Players;");
				    while (rs.next()){
				  	    plugin.dbplayers.add(new SurvivalPlayer(rs.getInt("points"), rs.getInt("power"), UUID.fromString(rs.getString("player"))));
				    }
				    
					rs = statement.executeQuery("SELECT * FROM ChunkBans;");
				    while (rs.next()){
				  	    plugin.dbbans.add(new SurvivalChunkBan(rs.getInt("x"), rs.getInt("z"), UUID.fromString(rs.getString("banned"))));
				    }
				    
					rs = statement.executeQuery("SELECT * FROM ChunkMutes;");
				    while (rs.next()){
				  	    plugin.dbmutes.add(new SurvivalChunkMute(rs.getInt("x"), rs.getInt("z"), UUID.fromString(rs.getString("muted"))));
				    }
				    
					rs = statement.executeQuery("SELECT * FROM ChunkFriends;");
				    while (rs.next()){
				  	    plugin.dbfriends.add(new SurvivalFriend(rs.getInt("x"), rs.getInt("z"), UUID.fromString(rs.getString("friend"))));
				    }
				    
					rs = statement.executeQuery("SELECT * FROM Homes;");
				    while (rs.next()){
				    	SurvivalHome sh = new SurvivalHome(UUID.fromString(rs.getString("player")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
				  	    plugin.dbhomes.add(sh);
				    }
				    
					rs = statement.executeQuery("SELECT * FROM ArrowPoints;");
				    while (rs.next()){
				    	ArrowPoint ap = new ArrowPoint(UUID.fromString(rs.getString("player")), rs.getString("name"), new Location(Bukkit.getWorld("world"), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")));
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
	  }
	  
	public void claimChunk(final Player p, final int x, final int z){
			if (con != null) {
				Statement statement = null;
				try {
					statement = con.createStatement();
					statement.execute("INSERT INTO Chunks(x, z, time, player) VALUES(" + x + ", " + z + ", " + (int)System.currentTimeMillis() + ", \"" + p.getUniqueId() + "\");");
					plugin.dbchunks.add(new SurvivalChunk(x, z, (int)System.currentTimeMillis(), p.getUniqueId()));
					
					List<UUID> mutePlayers = new LinkedList<UUID>();
					for (SurvivalChunkMute sm : plugin.dbmutes){
						Chunk c = Bukkit.getWorld("world").getChunkAt(sm.x, sm.z);
						UUID owner = getOwner(c);
						if (p.getUniqueId().equals(owner) && !mutePlayers.contains(sm.muted)) mutePlayers.add(sm.muted);
					}
					for (UUID s : mutePlayers) muteInChunk(Bukkit.getWorld("world").getChunkAt(x, z), s);
					
					List<UUID> banPlayers = new LinkedList<UUID>();
					for (SurvivalChunkBan sb : plugin.dbbans){
						Chunk c = Bukkit.getWorld("world").getChunkAt(sb.x, sb.z);
						UUID owner = getOwner(c);
						if (p.getUniqueId().equals(owner) && !banPlayers.contains(sb.banned)) banPlayers.add(sb.banned);
					}
					for (UUID s : banPlayers) banFromChunk(Bukkit.getWorld("world").getChunkAt(x, z), s);
					
					List<UUID> friendPlayers = new LinkedList<UUID>();
					for (SurvivalFriend sf : plugin.dbfriends){
						Chunk c = Bukkit.getWorld("world").getChunkAt(sf.x, sf.z);
						UUID owner = getOwner(c);
						if (p.getUniqueId().equals(owner) && !friendPlayers.contains(sf.friend)) friendPlayers.add(sf.friend);
					}
					for (UUID s : friendPlayers) addFriend(Bukkit.getWorld("world").getChunkAt(x, z), s);
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						claimChunk(p, x, z);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
	  }
	  
	  public List<Chunk> getChunks(UUID p){
		  List<Chunk> chunks = new LinkedList<Chunk>();
		  for (SurvivalChunk sc : plugin.dbchunks){
			  if (sc.player.equals(p)){
				  chunks.add(Bukkit.getWorld("world").getChunkAt(new Location(Bukkit.getWorld("world"), sc.x * 16, 0, sc.z * 16)));
			  }
		  }
		  return chunks;
	  }
	  
	  public List<Chunk> getAllChunks(){
		  List<Chunk> chunks = new LinkedList<Chunk>();
		  for (SurvivalChunk sc : plugin.dbchunks){
			  chunks.add(Bukkit.getWorld("world").getChunkAt(new Location(Bukkit.getWorld("world"), sc.x * 16, 0, sc.z * 16)));
		  }
		  return chunks;
	  }
	  
	  public UUID getOwner(Chunk c){
		  for (SurvivalChunk sc : plugin.dbchunks){
			  if (sc.x == c.getX() && sc.z == c.getZ()){
				  return sc.player;
			  }
		  }
		  return null;
	  }
	  
	  public UUID getOwner(int x, int z){
		  for (SurvivalChunk sc : plugin.dbchunks){
			  if (sc.x == x && sc.z == z){
				  return sc.player;
			  }
		  }
		  return null;
	  }
	  
	  public int getTime(Chunk c){
		  for (SurvivalChunk sc : plugin.dbchunks){
			  if (sc.x == c.getX() && sc.z == c.getZ()){
				  return sc.time;
			  }
		  }
		  return 0;
	  }
	  
	public void declaimChunk(final int x, final int z){	  
			if (con != null) {
				Statement statement = null;
				try {
					UUID owner = getOwner(x, z);
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
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						declaimChunk(x, z);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
	  }
	  
	  
	@SuppressWarnings("deprecation")
	public void increasePower(final Player p){
			if (con != null) {
				Statement statement = null;
				ResultSet rs;
				try {
					statement = con.createStatement();
					rs = statement.executeQuery("SELECT * FROM Players WHERE player=\"" + p.getUniqueId() + "\";");
					
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
						
						statement.execute("UPDATE Players SET power=" + (power + 1) + " WHERE player=\"" + p.getUniqueId() + "\";");
						
						  for (SurvivalPlayer sp : plugin.dbplayers){
							  if (sp.player.equals(p.getUniqueId())){
								  sp.power = power + 1;
							  }
						  }
						  finalpower = power + 1;
					} else {
						statement.execute("INSERT INTO Players(points, power, player) VALUES(10, 10, \"" + p.getUniqueId() + "\");");
						plugin.dbplayers.add(new SurvivalPlayer(10, 10, p.getUniqueId()));
						finalpower = 10;
					}
					
					
					Scoreboard board = plugin.scoreboards.get(p.getUniqueId());
					if (board != null){
						Objective objective = board.getObjective("scoreboard");
						objective.getScore(Bukkit.getOfflinePlayer(Utils.replace(plugin.getConfig().getString("scoreboard-power")))).setScore(finalpower);
						p.setScoreboard(board);
					} else {
						Scoreboard board2 = plugin.manager.getNewScoreboard();
					    Objective objective = board2.registerNewObjective("scoreboard", "dummy");
					    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
					    objective.setDisplayName(Utils.replace(plugin.getConfig().getString("scoreboard")));
					    Score power = objective.getScore(Bukkit.getOfflinePlayer(Utils.replace(plugin.getConfig().getString("scoreboard-power"))));
					    power.setScore(finalpower);
					    p.setScoreboard(board2);
						plugin.scoreboards.put(p.getUniqueId(), board2);
					}
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						increasePower(p);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
	  }
	  
	  public int getPower(UUID player){
		  for (SurvivalPlayer sp : plugin.dbplayers){
			  if (sp.player.equals(player)){
				  return sp.power;
			  }
		  }
		  return 0;
	  }
	  
	@SuppressWarnings("deprecation")
	public void setPower(final UUID player, final int power){
			if (con != null) {
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
					
					boolean contains = false;
					for (Player p : Bukkit.getOnlinePlayers()){
						if (p.getUniqueId() == player) contains = true;
					}
					
					if (contains){
						Player p = Bukkit.getPlayer(player);
						Scoreboard board = plugin.scoreboards.get(player);
						if (board != null){
							Objective objective = board.getObjective("scoreboard");
							objective.getScore(Bukkit.getOfflinePlayer(Utils.replace(plugin.getConfig().getString("scoreboard-power")))).setScore(power);
							p.setScoreboard(board);
						} else {
							Scoreboard board2 = plugin.manager.getNewScoreboard();
						    Objective objective = board2.registerNewObjective("scoreboard", "dummy");
						    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
						    objective.setDisplayName(Utils.replace(plugin.getConfig().getString("scoreboard")));
						    Score powerscore = objective.getScore(Bukkit.getOfflinePlayer(Utils.replace(plugin.getConfig().getString("scoreboard-power"))));
						    powerscore.setScore(power);
						    p.setScoreboard(board2);
							plugin.scoreboards.put(player, board2);
						}
					}
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						setPower(player, power);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
	  }
	  
	@SuppressWarnings("deprecation")
	public void chunkClaimPrice(final Player p){
			if (con != null) {
				  for (SurvivalPlayer sp : plugin.dbplayers){
					  if (sp.player.equals(p.getUniqueId())){
							sp.power = sp.power - 10;
					  }
				  }
				Statement statement = null;
				ResultSet rs;
				try {
					statement = con.createStatement();
					rs = statement.executeQuery("SELECT * FROM Players WHERE player=\"" + p.getUniqueId() + "\";");
					int newpower;
					if (rs.next()){
						newpower = rs.getInt("power") - 10;
						statement.execute("UPDATE Players SET power=" + newpower + " WHERE player=\"" + p.getUniqueId() + "\";");
					} else {
						newpower = 0;
						statement.execute("INSERT INTO Players(points, power, player) VALUES(10, 0, \"" + p.getUniqueId() + "\");");
					}
					
					
					Scoreboard board = plugin.scoreboards.get(p.getUniqueId());
					if (board != null){
						Objective objective = board.getObjective("scoreboard");
						objective.getScore(Bukkit.getOfflinePlayer(Utils.replace(plugin.getConfig().getString("scoreboard-power")))).setScore(newpower);
						p.setScoreboard(board);
					} else {
						Scoreboard board2 = plugin.manager.getNewScoreboard();
					    Objective objective = board2.registerNewObjective("scoreboard", "dummy");
					    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
					    objective.setDisplayName(Utils.replace(plugin.getConfig().getString("scoreboard")));
					    Score powerscore = objective.getScore(Bukkit.getOfflinePlayer(Utils.replace(plugin.getConfig().getString("scoreboard-power"))));
					    powerscore.setScore(newpower);
					    p.setScoreboard(board2);
						plugin.scoreboards.put(p.getUniqueId(), board2);
					}
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						chunkClaimPrice(p);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
	  }
	  
	public boolean unbanFromChunk(final Chunk chunk, final UUID banned){
			if (con != null) {
				final int x = chunk.getX();
				final int z = chunk.getZ();
				
				if (!isBanned(chunk, banned)) return false;
				
            	Statement statement = null;
				try {
	            	statement = con.createStatement();
					statement.execute("DELETE FROM ChunkBans WHERE x=" + x + " AND z=" + z + " AND banned=\"" + banned + "\";");
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						unbanFromChunk(chunk, banned);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
			    for (Iterator<SurvivalChunkBan> it = plugin.dbbans.iterator(); it.hasNext(); ){
			    	SurvivalChunkBan scb = it.next();
					if (scb.x == x && scb.z == z && scb.banned.equals(banned)){
						it.remove();
					}
			    }
				return true;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
			return false;
	  }
	  
	public boolean banFromChunk(final Chunk chunk, final UUID banned){
			if (con != null) {
				final int x = chunk.getX();
				final int z = chunk.getZ();
				
				if (isBanned(chunk, banned)) return false;
					
            	Statement statement = null;
				try {
	            	statement = con.createStatement();
	            	statement.execute("INSERT INTO ChunkBans(x, z, banned) VALUES(" + x + ", " + z + ", \"" + banned + "\");");
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						banFromChunk(chunk, banned);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
				plugin.dbbans.add(new SurvivalChunkBan(x, z, banned));
				return true;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
				return false;
			}
	  }
	  
	  public boolean isBanned(Chunk chunk, UUID player){
		  for (SurvivalChunkBan scb : plugin.dbbans){
			  if (scb.x == chunk.getX() && scb.z == chunk.getZ() && scb.banned.equals(player)) return true;
		  }
		  return false;
	  }
	  
	  public List<UUID> getBannedPlayers(Chunk chunk){
		  List<UUID> players = new LinkedList<UUID>();
		  for (SurvivalChunkBan scb : plugin.dbbans){
			  if (scb.x == chunk.getX() && scb.z == chunk.getZ()) players.add(scb.banned);
		  }
		  return players;
	  }
	  
	  public boolean isMuted(Chunk chunk, UUID player){
		  for (SurvivalChunkMute scm : plugin.dbmutes){
			  if (scm.x == chunk.getX() && scm.z == chunk.getZ() && scm.muted.equals(player)) return true;
		  }
		  return false;
	  }
	  
	  public boolean isFriend(Chunk chunk, UUID player){
		  for (SurvivalFriend sf : plugin.dbfriends){
			  if (sf.x == chunk.getX() && sf.z == chunk.getZ() && sf.friend.equals(player)) return true;
		  }
		  return false;
	  }
	  
	  public List<UUID> getFriends(Chunk chunk){
		  List<UUID> friends = new LinkedList<UUID>();
		  for (SurvivalFriend sf : plugin.dbfriends){
			  if (sf.x == chunk.getX() && sf.z == chunk.getZ()) friends.add(sf.friend);
		  }
		  return friends;
	  }
	  
	public void setHome(final UUID player, final int x, final int y, final int z){
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
					Statement statement = null;
					try {
						statement = con.createStatement();
						statement.execute("UPDATE Homes SET x=" + x + ", y=" + y + ", z=" + z + " WHERE player=\"" + player + "\";");
					} catch (SQLNonTransientConnectionException e){
						try {
							con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
							setHome(player, x, y, z);
						} catch (SQLException e1) {
							System.out.println("MySQL re-connection failed!");
							e1.printStackTrace();
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
					plugin.dbhomes.add(new SurvivalHome(player, x, y, z));
					Statement statement = null;
					try {
						statement = con.createStatement();
						statement.execute("INSERT INTO Homes VALUES(" + x + ", " + y + ", " + z + ", \"" + player + "\");");
					} catch (SQLNonTransientConnectionException e){
						try {
							con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
							setHome(player, x, y, z);
						} catch (SQLException e1) {
							System.out.println("MySQL re-connection failed!");
							e1.printStackTrace();
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
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
	  }
	  
	  public Location getHome(UUID player){
		  for (SurvivalHome sh : plugin.dbhomes){
			  if (sh.player.equals(player)){
				  return new Location(Bukkit.getWorld("world"), sh.x, sh.y, sh.z);
			  }
		  }
		  return null;
	  }
	  
	public boolean muteInChunk(final Chunk chunk, final UUID muted){
			if (con != null) {
				final int x = chunk.getX();
				final int z = chunk.getZ();
				
				if (isMuted(chunk, muted)) return false;
				
				Statement statement = null;
				try {
					statement = con.createStatement();
					statement.execute("INSERT INTO ChunkMutes(x, z, muted) VALUES(" + x + ", " + z + ", \"" + muted + "\");");
					plugin.dbmutes.add(new SurvivalChunkMute(x, z, muted));
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						muteInChunk(chunk, muted);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
				return true;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
				return false;
			}
	  }
	  
	public boolean unmuteInChunk(final Chunk chunk, final UUID muted){
			if (con != null) {
				final int x = chunk.getX();
				final int z = chunk.getZ();
				
				if (!isMuted(chunk, muted)) return false;
				
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
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						unmuteInChunk(chunk, muted);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
		        return true;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
			return false;
	  }
	  
	public boolean addFriend(final Chunk chunk, final UUID friend){
			if (con != null) {
				final int x = chunk.getX();
				final int z = chunk.getZ();
				
				if (isFriend(chunk, friend)) return false;
				
				Statement statement = null;
				try {
					statement = con.createStatement();					
					statement.execute("INSERT INTO ChunkFriends(x, z, friend) VALUES(" + x + ", " + z + ", \"" + friend + "\");");
					plugin.dbfriends.add(new SurvivalFriend(x, z, friend));
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						addFriend(chunk, friend);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
				return true;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
				return false;
			}
	  }
	  
	public boolean removeFriend(final Chunk chunk, final UUID friend){
			if (con != null) {
				final int x = chunk.getX();
				final int z = chunk.getZ();
				
				if (!isFriend(chunk, friend)) return false;
				
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
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						removeFriend(chunk, friend);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
		        return true;
			} else {
				System.out.println("MySQL connection failed!");
				System.out.println("Attempting to re-initialize");
				initialize();
			}
			return false;
	  }
	  
	public void setOwner(final Chunk c, final UUID p){
			if (con != null) {
				final int x = c.getX();
				final int z = c.getZ();
				  
				Statement statement = null;
				try {
					int time = (int)System.currentTimeMillis();
					statement = con.createStatement();
					statement.execute("UPDATE Chunks SET player=\"" + p + "\", time=" + time + " WHERE x=" + x + " AND z=" + z + ";");
					
					for (SurvivalChunk sc : plugin.dbchunks){
						if (sc.x == x && sc.z == z) sc.player = p; sc.time = time;
					}
					
					
				    for (Iterator<SurvivalFriend> it = plugin.dbfriends.iterator(); it.hasNext(); ){
				    	SurvivalFriend sf = it.next();
						if (sf.x == x && sf.z == z) {
							it.remove();
							removeFriend(c, sf.friend);
						}
				    }
				    for (Iterator<SurvivalChunkBan> it = plugin.dbbans.iterator(); it.hasNext(); ){
				    	SurvivalChunkBan sb = it.next();
						if (sb.x == x && sb.z == z){
							it.remove();
							unbanFromChunk(c, sb.banned);
						}
				    }
				    for (Iterator<SurvivalChunkMute> it = plugin.dbmutes.iterator(); it.hasNext(); ){
				    	SurvivalChunkMute sm = it.next();
						if (sm.x == x && sm.z == z){
							it.remove();
							unmuteInChunk(c, sm.muted);
						}
				    }
				    
				    
					List<UUID> mutePlayers = new LinkedList<UUID>();
					for (SurvivalChunkMute sm : plugin.dbmutes){
						Chunk c2 = Bukkit.getWorld("world").getChunkAt(sm.x, sm.z);
						UUID owner = getOwner(c2);
						if (p.equals(owner) && !mutePlayers.contains(sm.muted)) mutePlayers.add(sm.muted);
					}
					for (UUID s : mutePlayers) muteInChunk(Bukkit.getWorld("world").getChunkAt(x, z), s);
					
					List<UUID> banPlayers = new LinkedList<UUID>();
					for (SurvivalChunkBan sb : plugin.dbbans){
						Chunk c2 = Bukkit.getWorld("world").getChunkAt(sb.x, sb.z);
						UUID owner = getOwner(c2);
						if (p.equals(owner) && !banPlayers.contains(sb.banned)) banPlayers.add(sb.banned);
					}
					for (UUID s : banPlayers) banFromChunk(Bukkit.getWorld("world").getChunkAt(x, z), s);
					
					List<UUID> friendPlayers = new LinkedList<UUID>();
					for (SurvivalFriend sf : plugin.dbfriends){
						Chunk c2 = Bukkit.getWorld("world").getChunkAt(sf.x, sf.z);
						UUID owner = getOwner(c2);
						if (p.equals(owner) && !friendPlayers.contains(sf.friend)) friendPlayers.add(sf.friend);
					}
					for (UUID s : friendPlayers) addFriend(Bukkit.getWorld("world").getChunkAt(x, z), s);
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						setOwner(c, p);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
	  }
	  
	public void setArrowPoint(final UUID player, final String name, Location loc){
			if (con != null) {
				final int x = (int) loc.getX();
				final int y = (int) loc.getY();
				final int z = (int) loc.getZ();
				
				plugin.dbarrowpoints.add(new ArrowPoint(player, name, loc));
				  
				Statement statement = null;
				try {
					statement = con.createStatement();
					statement.execute("INSERT INTO ArrowPoints(x, y, z, player, name) VALUES(" + x + ", " + y + ", " + z + ", \"" + player + "\", \"" + name + "\");");
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						setArrowPoint(player, name, loc);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
	  }
	  
	  public List<ArrowPoint> getArrowPoints(UUID player){
		  List<ArrowPoint> list = new LinkedList<ArrowPoint>();
		  for (ArrowPoint ap : plugin.dbarrowpoints){
			  if (ap.player.equals(player)) list.add(ap);
		  }
		  return list;
	  }
	  
	public void deleteArrowPoint(final UUID player, final String name){
			if (con != null) {
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
				} catch (SQLNonTransientConnectionException e){
					try {
						con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysql.host") + "/" + plugin.getConfig().getString("mysql.database"), plugin.getConfig().getString("mysql.username"), plugin.getConfig().getString("mysql.password"));
						deleteArrowPoint(player, name);
					} catch (SQLException e1) {
						System.out.println("MySQL re-connection failed!");
						e1.printStackTrace();
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
	  }
}
