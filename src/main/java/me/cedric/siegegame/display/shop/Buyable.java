package me.cedric.siegegame.display.shop;

import com.palmergames.bukkit.towny.object.Town;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.teams.Team;
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
        Town teamTown = team.getTeamTown();

        if (!teamTown.isInsideTown(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You need to be inside claims to do this");
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
