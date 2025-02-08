package me.cedric.siegegame.enums;

import net.md_5.bungee.api.ChatColor;

import java.awt.Color;

public abstract class Messages {

    public static final String PREFIX = ChatColor.of(new Color(247, 80, 30)) + "[Sieges] ";

    public static final String CLAIMS_ACTION_CANCELLED = PREFIX + ChatColor.RED + "You cannot do this in enemy territory.";
    public static final String CLAIMS_ENTERED = "%s";

    public static final String RALLY_SET = "Rally set.";

}
