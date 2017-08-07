# README #

These are the Minigame plugins that I (BaranCODE) had made for my own network.

**Quick overview:**

- BuildIt/BuildGuess/GuessThatBuild: It's pretty awesome, has some great features. You can "guess" what this game is. It needs some in-game setup to work correctly.

- TNT Dodge: Unique minigame. There are two layers, the top layer is made of glass. There is one person (called the dropper) standing on the top layer, and all the other players are at the bottom layer. When the dropper places a tnt block (he has unlimited tnt blocks) on his layer, a primed/lighted tnt entity appears right below that location, and falls to the bottom layer. Runners have to dodge it; the floor is slowly destroyed by the tnt explosions.

- LavaSlap: Based off of minigames like CookieSlap or BudderSlap. You can double jump and you have a lava item in your hand which has a very large knockback enchantment. There is also lava everywhere in the map, and there's a mini "volcano" in the middle of the arena that shoots lava into the air which lands in random locations.

- TNT Run: Classic tnt run. You run, the blocks that you moved over dissapear.

- Spleef: Classic spleef. You click the blocks underneath you to try to make other players fall.

- SkyWars: Classic skywars. Players have islands in the air, they fight each other.

**Details:**

* All of these plugins need some configuration/setup to work properly (the BuildGuess plugin more so than the other ones).

* These plugins are designed to run on their own server instances.

* After each match, the plugin makes its server shut down, and start back up (by executing the server start script).

* *For all the minigames except BuildGuess, this applies:*
  You place your world with the name "world". Every time the server restarts, the plugin deletes the directory called "arena", copies "world" to "arena", and then loads the world "arena". Players play in the world "arena". Therefore, players will never get to actually edit your actual world, they just see a copy of it that gets re-copied each time the minigame server restarts (after every match).

* For the BuildGuess minigame, it just rolls back the building area whenever necessary (for example, when changing between different builders).

* I have the files of server instances for these minigames (all except BuildGuess), ready. They probably won't require much work to setup and use for ChoiceCraft. They contain the plugin for that minigame, as well as a few other plugins (I'll check if they're necessary for the gameplay, or if they were specific for my network, if I am to give these files to you). They also contain worlds for these minigames, and I don't think they're that bad. The SkyWars maps especially are pretty good, and I think the chests are well-prepared. Also, the SkyWars maps use a very good looking small waiting lobby (when waiting for enough players to join), whereas the others just use a glass box; you may want to copy the waiting lobby from skywars to the other maps.