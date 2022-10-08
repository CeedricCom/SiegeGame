package me.cedric.siegegame.superitems;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.model.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import me.deltaorion.bukkit.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

public class KnockbackStick extends SuperItem {

    protected KnockbackStick(SiegeGame plugin, String key, WorldGame worldGame) {
        super(plugin, key, worldGame);
    }

    @Override
    protected void display(GamePlayer owner) {
        Player player = owner.getBukkitPlayer();
        player.sendMessage(ChatColor.LIGHT_PURPLE + "You have obtained a " + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "SUPER ITEM" + ChatColor.LIGHT_PURPLE +"!" +
                " Enjoy your " + ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Knockback Stick");
    }

    @Override
    protected void removeDisplay(GamePlayer owner) {
        owner.getBukkitPlayer().getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
    }

    @Override
    protected ItemStack itemStack() {
        return new ItemBuilder(Material.STICK)
                .addEnchantment(Enchantment.KNOCKBACK, 10)
                .setUnbreakable(true)
                .setDisplayName(ChatColor.AQUA + "qxti stick")
                .addLoreLine(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "SUPER ITEM")
                .build();
    }

    @Override
    protected void initialize(SiegeGame plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String getDisplayName() {
        return "Knockback Stick";
    }
}

