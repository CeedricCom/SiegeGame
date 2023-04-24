# SiegeGame

![Banner](https://github.com/CeedricCom/Earth/blob/master/branding/Banner.png?raw=true)

<a href="https://github.com/TownyAdvanced/SiegeWar"> SiegeWar</a> is a war system developed for <a href="https://github.com/TownyAdvanced/Towny"> Towny</a> that allows nations to go to war with one another. This is a plugin I made to turn SiegeWar into a fast-paced minigame.

This repository contains the source code for the plugin as well as default configuration files. Each configuration file allows you to customise a certain aspect of the game, including the map, shop, respawn times, etc.

# Building

1. The project depends on <a href="https://github.com/DeltaOrion/ExternusAPI"> ExternusAPI</a>. Clone this repository and install the API in your maven local repository using `mvn clean install`
2. The rest of the dependencies are managed by maven, use `mvn clean install` to download them.
3. Build the project using `mvn package`. This will generate a jar on the project's target folder that can be added to the server.

# Usage

Firstly, it is important to note that not all functionality previously available in ceedric.com Sieges is managed by this plugin. Some features such as chat and legacy combat are out of the scope of this project (except for team chat, which will be added in the future) and are not handled here. You can view the full plugin list of the Sieges server <a href="https://github.com/CeedricCom/Sieges"> here</a>

Once you install the plugin, use `/siegeg start`. The plugin will pick a random map from your `maps.yml`, create randomly assigned teams, and start the game. The resources shop can be accessed via `/resources` in-game. The shop and its prices can be customised in the `shop.yml`.

### Adding maps

Each map must be its own save folder (a Minecraft world) that must be placed in the server folder directly. The plugin will create a copy this world folder every time it needs to rotate to that map, and delete the copy once the match is over.

To configure territories, teams, spawns, and other map features, check the commented version of `maps.yml` in the plugin's <a href="https://github.com/CeedricCom/SiegeGame/tree/master/src/main/resources"> resource folder</a>

### Editing the resources menu

To edit the resources menu, go to the plugin's `shop.yml` and create your items. ItemStack serialization is not supported as of writing this. 

A commented version of this file can be found in the plugin's <a href="https://github.com/CeedricCom/SiegeGame/tree/master/src/main/resources"> resource folder</a>.

### General configuration

Similarly, you can find a commented version of the `config.yml` file in the plugin's <a href="https://github.com/CeedricCom/SiegeGame/tree/master/src/main/resources"> resource folder</a>.

# Contributing

### Pull requests

If you are willing to spend some time contributing to the project, I will be happy to review and merge pull requests, especially if they are new features.
