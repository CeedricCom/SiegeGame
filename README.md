# SiegeGame

![Banner](https://github.com/CeedricCom/Earth/blob/master/branding/Banner.png?raw=true)

<a href="https://github.com/TownyAdvanced/SiegeWar"> SiegeWar</a> is a war system developed for <a href="https://github.com/TownyAdvanced/Towny"> Towny</a> that allows nations to go to war with one another. This is a plugin I made to turn SiegeWar into a fast-paced minigame.

This repository contains the source code for the plugin as well as default configuration files. Each configuration file allows you to customise a certain aspect of the game, including the map, shop, respawn times, etc.

# Building

The project should be built using <a href="https://maven.apache.org/">Apache Maven.</a>

1. The project depends on <a href="https://github.com/DeltaOrion/ExternusAPI"> ExternusAPI</a>. Clone this repository and install the API in your maven local repository using `mvn clean install`
2. Build the project by navigating to the project's root folder and executing the command `mvn clean package`. This will generate a jar file in the newly created `target` folder.

# Usage

Please note that this plugin does not manage all functionalities previously available in Ceedric.com Sieges. Features such as chat and legacy combat are outside the scope of this project (except for team chat, which is planned for future updates) and are handled by external plugins. For a complete list of Sieges server plugins, visit <a href="https://github.com/CeedricCom/Sieges"> here</a>.

After installing the plugin, execute the Minecraft command `siegeg start`. The plugin will randomly select a map from your `maps.yml`, create random teams, and initiate the game. Access the resources shop in-game via the Minecraft Command `/resources`. Customize the shop and its prices using the `shop.yml`.

### Installation and Running

Ceedric.com Sieges should be run on a Paper Minecraft Server. The minigame can be installed and run as follows

1. Place the plugin's jar file into the Minecraft Servers `/plugins` folder
2. Launch the Paper Minecraft Server using `java -jar [server.jar]`

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
