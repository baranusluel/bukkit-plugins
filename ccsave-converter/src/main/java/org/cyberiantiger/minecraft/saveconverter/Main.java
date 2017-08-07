package main.java.org.cyberiantiger.minecraft.saveconverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.cyberiantiger.minecraft.nbt.CompoundTag;
import org.cyberiantiger.minecraft.nbt.Tag;
import org.cyberiantiger.minecraft.nbt.TagInputStream;
import org.cyberiantiger.minecraft.nbt.TagOutputStream;
import org.cyberiantiger.minecraft.nbt.TagType;

/**
 *
 * @author antony
 */
public class Main {
    private static final Pattern VALID_USERNAME = Pattern.compile("[a-zA-Z0-9_]{2,16}");
    private static final Charset UTF8 = Charset.forName("UTF-8");
    static String host, database, username, password;
    static Connection con = null;


    private static void usage() {
        System.err.println("Usage: java -jar save-converter.jar <host> <database> <username> <password> <worldsave> <worldsave> <worldsave> ... ");
    }

    public static void main(String[] args) throws Exception {
        List<String> paths = new ArrayList<String>();
        
        if (args.length < 5){
        	usage();
        	return;
        } else {
        	host = args[0];
        	database = args[1];
        	username = args[2];
        	password = (!args[3].equals("\"\"") && !args[3].equals("''")) ? args[3] : "";
        	for (int i = 4; i < args.length; i++){
        		paths.add(args[i]);
        	}
        }
        
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("MySQL JDBC driver missing");
			e.printStackTrace();
			return;
		}
		try {
			con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
		} catch (SQLException e) {
			System.out.println("MySQL connection failed!");
			e.printStackTrace();
			return;
		}
		if (con == null){
			System.out.println("MySQL connection failed!");
			return;
		}

        for (String s : paths) {
            File file = new File(s);
            if (!file.isDirectory()) {
                System.out.println("# Skipping "  + file.getPath() + " - not a directory");
                continue;
            } else if (!file.canRead()) {
                System.out.println("# Skipping "  + file.getPath() + " - cannot read");
                continue;
            }
            
            upgradePlayerFiles(file, false, false);
        }
        
		try {
			if (con != null) con.close();
		} catch (SQLException e) {
			System.out.println("Could not close() the connection");
			e.printStackTrace();
		}
		
		print("Finished!");
    }
    
	public static void print(String s){
		System.out.println(s);
	}

    /*private static boolean isOfflineUUID(String player, UUID uuid) {
        return getOfflineUUID(player).equals(uuid);
    }*/

    private static UUID getOfflineUUID(String player) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player).getBytes(UTF8));
    }

    private static Map<String,UUID> getOnlineUUIDs(Collection<String> tmp) throws IOException {
        List<String> players = new ArrayList<String>(tmp);
        Map<String,UUID> result = new HashMap<String,UUID>();
        
		if (con != null) {
			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = con.createStatement();
				String message = "SELECT * FROM players WHERE username IN(";
				int firstLength = message.length();
				
				for (String name : players){
					if (message.length() == firstLength){
						message += "\"" + name + "\"";
					} else {
						message += ",\"" + name + "\"";
					}
				}
				message += ") ORDER BY id DESC;";
				
				rs = statement.executeQuery(message);
				
				while (rs.next()){
					String username = rs.getString("username");
					if (!result.containsKey(username)) result.put(username, UUID.fromString(rs.getString("uuid").replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")));
				}
			} catch (Exception e){
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) rs.close();
					if (statement != null) statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			print("MySQL connection failed!");
		}
        return result;
    }

    @SuppressWarnings("rawtypes")
	private static void upgradePlayerFiles(File file, boolean dryRun, boolean offline) {
        File playerDir = new File(file, "players");
        if (!playerDir.isDirectory()) {
            System.out.println("Skipping "  + file.getPath() + " - no players dir");
            return;
        } else if (!playerDir.canRead()) {
            System.out.println("Skipping "  + file.getPath() + " - players dir cannot be read");
            return;
        }
        File playerdataDir = new File(file, "playerdata");
        if (!playerdataDir.exists()) {
            if (!dryRun) {
                if (!playerdataDir.mkdir()) {
                    System.out.println("Skipping "  + file.getPath() + " - could not create playerdata directory");
                    return;
                }
            }
        } else {
            if (!playerdataDir.isDirectory()) {
                System.out.println("Skipping "  + file.getPath() + " - playerdata not a directory");
                return;
            } else if (!playerdataDir.canRead()) {
                System.out.println("Skipping "  + file.getPath() + " - playerdata cannot be read");
                return;
            } else if (!playerdataDir.canWrite()) {
                System.out.println("Skipping "  + file.getPath() + " - playerdata cannot be written to");
                return;
            }
        }
        Map<String, Tag> playersaves = new HashMap<String,Tag>();
        System.out.println("Players dir: " + playerDir.getPath());
        System.out.println("Playerdata dir: " + playerdataDir.getPath());
        for (File playerFile : playerDir.listFiles()) {
            System.out.println("Processing " + playerFile.getPath());
            if (!playerFile.isFile()) {
                System.out.println("Skipping "  + playerFile.getPath() + " - not a regular file");
                continue;
            }
            String name = playerFile.getName();
            if (!name.endsWith(".dat")) {
                System.out.println("Skipping "  + playerFile.getPath() + " - does not end in .dat");
                continue;
            }
            String playerName = name.substring(0, name.length() - ".dat".length());
            if (!VALID_USERNAME.matcher(playerName).matches()) {
                System.out.println("Skipping "  + playerFile.getPath() + " - does not look like a minecraft username");
                continue;
            }
            Tag playerData;
            try {
                TagInputStream in = new TagInputStream(new GZIPInputStream(new FileInputStream(playerFile)));
                try {
                    playerData = in.readTag();
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                System.out.println("Skipping " + playerFile.getPath() + " - could not parse file");
                continue;
            }
            if (playerData.getType() != TagType.COMPOUND) {
                System.out.println("Skipping "  + playerFile.getPath() + " - contains non compound nbt tag as root");
                continue;
            }
            CompoundTag playerDataCompound = (CompoundTag) playerData;
            CompoundTag bukkit;
            if (playerDataCompound.containsKey("bukkit")) {
                bukkit = playerDataCompound.getCompound("bukkit");
            } else {
                playerDataCompound.setCompound("bukkit", bukkit = new CompoundTag("bukkit"));
            }
            bukkit.setString("lastKnownName", playerName);
            playersaves.put(playerName, playerData);
        }
        Map<String,UUID> uuids;
        if (offline) {
            uuids = new HashMap<String, UUID>();
            for (String s : playersaves.keySet()) {
                uuids.put(s, getOfflineUUID(s));
            }
        } else {
            try {
                uuids = getOnlineUUIDs(playersaves.keySet());
            } catch (IOException ex) {
                System.out.println("Catastrophic failure of mojang profile api, try again later.");
                return;
            }
        }
        for (Map.Entry<String, Tag> e : playersaves.entrySet()) {
            File playerFile = new File(playerDir, e.getKey() + ".dat");
            if (!uuids.containsKey(e.getKey())) {
                System.out.println("Could not lookup uuid for " + playerFile + " skipping ... ");
                continue;
            }
            UUID uuid = uuids.get(e.getKey());
            File targetFile = new File(playerdataDir, uuid.toString() + ".dat");
            if (targetFile.exists()) {
                System.out.println("Skipping " + playerFile + " - target save already exists: " + targetFile);
                continue;
            }
            System.out.println(playerFile.toString() + " -> " + targetFile.toString());
            if (!dryRun) {
                try {
                    TagOutputStream out = new TagOutputStream(new GZIPOutputStream(new FileOutputStream(targetFile)));
                    try {
                        out.writeTag(e.getValue());
                    } finally {
                        out.close();
                    }
                    playerFile.delete();
                } catch (IOException ex) {
                    System.out.println("Skipping "  + e.getKey() + " - failed to write target file");
                    targetFile.delete();
                    continue;
                }
            }
        }
    }

    /*private static void downgradePlayerFiles(File file, boolean dryRun, boolean offline) {
        File playerdataDir = new File(file, "playerdata");
        if (!playerdataDir.isDirectory()) {
            System.out.println("Skipping "  + file.getPath() + " - no playerdata dir");
            return;
        } else if (!playerdataDir.canRead()) {
            System.out.println("Skipping "  + file.getPath() + " - playerdata dir cannot be read");
            return;
        }
        File playerDir = new File(file, "players");
        if (!playerDir.exists()) {
            if (!dryRun) {
                if (!playerDir.mkdir()) {
                    System.out.println("Skipping "  + file.getPath() + " - could not create players directory");
                    return;
                }
            }
        } else {
            if (!playerDir.isDirectory()) {
                System.out.println("Skipping "  + file.getPath() + " - players not a directory");
                return;
            } else if (!playerDir.canRead()) {
                System.out.println("Skipping "  + file.getPath() + " - players cannot be read");
                return;
            } else if (!playerDir.canWrite()) {
                System.out.println("Skipping "  + file.getPath() + " - players cannot be written to");
                return;
            }
        }
        System.out.println("Players dir: " + playerDir.getPath());
        System.out.println("Playerdata dir: " + playerdataDir.getPath());
        for (File playerFile : playerdataDir.listFiles()) {
            System.out.println("Processing " + playerFile.getPath());
            if (!playerFile.isFile()) {
                System.out.println("Skipping "  + playerFile.getPath() + " - not a regular file");
                continue;
            }
            String name = playerFile.getName();
            if (!name.endsWith(".dat")) {
                System.out.println("Skipping "  + playerFile.getPath() + " - does not end in .dat");
                continue;
            }
            String uuidString = name.substring(0, name.length() - ".dat".length());
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException ex) {
                System.out.println("Skipping "  + playerFile.getPath() + " - not a valid uuid");
                continue;
            }
            Tag playerData;
            try {
                TagInputStream in = new TagInputStream(new GZIPInputStream(new FileInputStream(playerFile)));
                try {
                    playerData = in.readTag();
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                System.out.println("Skipping " + playerFile.getPath() + " - could not parse file");
                continue;
            }
            if (playerData.getType() != TagType.COMPOUND) {
                System.out.println("Skipping "  + playerFile.getPath() + " - contains non compound nbt tag as root");
                continue;
            }
            CompoundTag playerDataCompound = (CompoundTag) playerData;
            if (!playerDataCompound.containsKey("bukkit")) {
                System.out.println("Skipping "  + playerFile.getPath() + " - no bukkit section in player data");
                continue;
            }
            CompoundTag bukkit = playerDataCompound.getCompound("bukkit");
            if (!bukkit.containsKey("lastKnownName")) {
                System.out.println("Skipping "  + playerFile.getPath() + " - no lastKnownName in player data");
                continue;
            }
            String playerName = bukkit.getString("lastKnownName");
            if (!VALID_USERNAME.matcher(playerName).matches()) {
                System.out.println("Skipping "  + playerFile.getPath() + " - lastKnownName: " + playerName + " is not a valid player name");
                continue;
            }
            if (offline ^ isOfflineUUID(playerName, uuid)) {
                System.out.println("Skipping "  + playerFile.getPath() + " - " + (offline?"uuid is online, and we're in offline mode":"uuid is offline, and we're in online mode."));
                continue;
            }
            File targetFile = new File(playerDir, playerName + ".dat");
            if (targetFile.exists()) {
                System.out.println("Skipping "  + playerFile.getPath() + " - target file exists: " + targetFile);
                continue;
            }
            System.out.println(playerFile.toString() + " -> " + targetFile.toString());
            if (!dryRun) {
                try {
                    TagOutputStream out = new TagOutputStream(new GZIPOutputStream(new FileOutputStream(targetFile)));
                    try {
                        out.writeTag(playerData);
                    } finally {
                        out.close();
                    }
                } catch (IOException ex) {
                    System.out.println("Skipping "  + playerFile.getPath() + " - failed to write target file");
                    targetFile.delete();
                    continue;
                }
                playerFile.delete();
            }
        }
    }*/
}
