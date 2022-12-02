package me.cedric.siegegame.display;

import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.teams.Team;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.awt.*;

public class ColorUtil {

    @SuppressWarnings("deprecation")
    public static ChatColor getRelationalColor(Team one, Team two) {
        if (one == null || two == null)
            return ChatColor.WHITE;

        if (!one.getWorldGame().equals(two.getWorldGame()))
            return ChatColor.WHITE;

        if (one.equals(two))
            return ChatColor.DARK_AQUA;

        WorldGame worldGame = one.getWorldGame();

        if (worldGame.getTeams().size() == 2) {
            return ChatColor.RED;
        }

        return ChatColor.of(two.getColor());
    }

    public static Material getRelationalWool(Team one, Team two) {
        ChatColor chatColor = getRelationalColor(one, two);
        Color color = chatColor.getColor();
        return woolFromColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    private static Material woolFromColor(int red, int green, int blue) {
        int distance = Integer.MAX_VALUE;
        org.bukkit.DyeColor closest = null;
        for (DyeColor dye : org.bukkit.DyeColor.values()) {
            org.bukkit.Color color = dye.getColor();
            int dist = Math.abs(color.getRed() - red) + Math.abs(color.getGreen() - green) + Math.abs(color.getBlue() - blue);
            if (dist < distance) {
                distance = dist;
                closest = dye;
            }
        }

        if (closest == null)
            return null;

        return Material.matchMaterial((closest.name() + "_wool").toUpperCase());
    }

}
