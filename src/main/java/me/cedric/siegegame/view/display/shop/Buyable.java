package me.cedric.siegegame.view.display.shop;

import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.model.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Buyable {

    ItemStack getDisplayItem();

    Purchase getPurchase();

    int getPrice();

    default boolean handlePurchase(GamePlayer gamePlayer) {
        Player player = gamePlayer.getBukkitPlayer();

        if (!gamePlayer.hasTeam()) {
            player.sendMessage(ChatColor.RED + "You need to be in a team to buy items");
            return false;
        }

        Team team = gamePlayer.getTeam();

        if (!team.getTerritory().isInside(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You need to be inside claims to do this");
            return false;
        }

        if (gamePlayer.isDead()) {
            player.sendMessage(ChatColor.RED + "You cannot do this while dead");
            return false;
        }

        if (player.getLevel() < getPrice()) {
            player.sendMessage(ChatColor.RED + "You do not have enough levels to buy this");
            return false;
        }

        int levels = player.getLevel();
        player.setLevel(levels - getPrice());
        getPurchase().accept(gamePlayer);
        return true;
    }

}
