# Converter #

**To use the converting option, run the tool the following way: "java -jar UserdataTool-*Version*-jar-with-dependencies"**

What does this do? It grabs usernames from one file, and writes username and UUID pairs (which it got from the Mojang servers) to another file. It does 10 conversions every 10 seconds to avoid passing Mojang's limit.

### How to use, and extra information: ###

* There are going to be two jar files in the target folder, get the one that says "with-dependencies".

* When you're running it, make sure there's a file called "usernames.txt" in the same folder, that you want to convert.

* The tool will create a new file called pairs.txt, where it will place the UUID and username pairs.

* Note: As it add entries to the pairs file, it is going to remove them from the usernames file, meaning that at the end, the usernames file will be empty. Also, thanks to this method, it won't be a problem if the converting process is interrupted; you just start it again whenever you can and it'll basicly resume.

* Note: This tool will not connect to nor populate the API database, it will just convert from usernames.txt to pairs.txt. This is because, as this will be run this from different locations, there wouldn't be access to the database. Therefore, after this process is finished, the pairs.txt files should all be combined.

# Verifier #

**To use the verification option, run the tool the following way: "java -jar UserdataTool-*Version*-jar-with-dependencies verify"**

What does this do, and when should it be used? When you are finished converting the files, you should combine all the files to make one large list of pairs. You should also get your original large/combined list of usernames, and place both of these files in the same directory as the tool, with the names usernames.txt and pairs.txt. What this tool does is read the files to make sure all of the usernames have been converted to pairs, and to make sure none of the UUIDs are malformed. The reason for doing this is because: 1) In one of the earlier versions of the tool there was a bug that could result in some players getting skipped (I was using that version at first). 2) When the username file was split into pieces, some usernames were cut in half.

With this tool, you will be able to detect mistakes like that, so that you can manually fix them. When this tool finds a mistake, it writes the username of the player to a file called missingUsernames.txt. After you do this, you can rename that file to usernames.txt, run that through the converter once again, and add the contents of your new pairs.txt to your previous pairs.txt.

# Uploader #

**To use the uploading option, run the tool the following way: "java -jar UserdataTool-*Version*-jar-with-dependencies database <host> <database> <username> <password>"**

What does this do? It uploads the username and UUID pairs you have in your pairs.txt file, to the database.

# Essentials Converter #

**To use the verification option, run the tool the following way: "java -jar UserdataTool-*Version*-jar-with-dependencies essentials"**

What does this do? It reads all the pairs from your pairs.txt and writes them to a file called "usermap.csv", so that Essentials can use it for its own UUID cache.