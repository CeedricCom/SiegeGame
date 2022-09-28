package me.cedric.siegegame.superitems;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.world.WorldGame;
import me.deltaorion.bukkit.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class SharpnessVI extends SuperItem {

    private Scoreboard belowName;
    private String scoreboardName = "sharp-6-owner";

    protected SharpnessVI(SiegeGame plugin) {
        super(plugin, "sharp-6");
    }

    @Override
    protected void display(GamePlayer owner) {
        Player player = owner.getBukkitPlayer();
        belowName = player.getScoreboard();
        belowName.registerNewObjective(scoreboardName, "dummy");
        Objective obj = belowName.getObjective("sharp-6");
        obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        obj.setDisplayName(ChatColor.DARK_PURPLE + "SHARPNESS VI");
    }

    @Override
    protected void removeDisplay(GamePlayer owner) {
        owner.getBukkitPlayer().getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
    }

    @Override
    protected ItemStack itemStack() {
        return new ItemBuilder(Material.NETHERITE_SWORD)
                .addEnchantment(Enchantment.DAMAGE_ALL, 6)
                .addLoreLine(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "SUPER ITEM")
                .build();
    }

    @Override
    protected void initialize(SiegeGame plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
