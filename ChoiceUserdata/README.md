# README #

This is an API plugin other ChoiceCraft plugins will be able to use to get information about players.

This API plugin will attempt to find the most optimized way to get a value whenever possible; if the player is online, it will use Bukkit functions to get the values. If the player is offline, then it will use the cache in the MySQL database for the last known values. Therefore, you can easily use this API without having to do those types of extra code yourself.

### Currently supported features: ###
* **UUID & Username conversions** - Get a UUID from a username, and vice versa.
* **IP retrieval** - Get a player's from their UUID

## How to use it ##

### Adding the dependency ###
Add the following to the repositories section in your POM file:

```
#!
	   <repository>
		   <id>dustcore-repo</id>
		   <url>http://repo.dustcore.net/repo/</url>
	   </repository>
```
And add this to the dependencies section:

```
#!
	<dependency>
		<groupId>com.barancode.choiceuserdata</groupId>
		<artifactId>ChoiceUserdata</artifactId>
		<version>LATEST</version>
	</dependency>

```
### Using the code ###

For example, if you wanted to get a player's UUID from their username, you could do:


```
#!Java
UUID uuid = UserAPI.getUUID("BaranCODE");

```


## API Functions ##

### UUID & Username conversions ###

- public static UUID getUUID(String name) {}

- public static String getName(UUID uuid) {}

- public static String getName(String uuid) {}

### IP Retrieval ###

- public static String getIPFromUUID(UUID uuid) {}

- public static String getIPFromUUID(String uuid) {}

- public static String getIPFromName(String name) {}

### Note ###

The API functions that take a UUID in their arguments accept them in either UUID or String form.

The API functions that have the arguments "String uuid" accept UUIDs formatted with, as well as without, dashes.