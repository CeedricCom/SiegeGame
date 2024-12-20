package me.cedric.siegegame.enums;

import me.deltaorion.common.locale.message.Message;
import net.md_5.bungee.api.ChatColor;

import java.awt.Color;

public abstract class Messages {

    public static final Message PREFIX = Message.valueOf(ChatColor.of(new Color(247, 80, 30)) + "[Sieges] ");

    public static final Message CLAIMS_ACTION_CANCELLED = Message.valueOf(PREFIX.toString() + ChatColor.RED + "You cannot do this in enemy territory.");
    public static final Message CLAIMS_ENTERED = Message.valueOf("%s");

    public static final Message RALLY_SET = Message.valueOf("Rally set.");

}
