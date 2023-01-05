package me.cedric.siegegame.modules.abilityitems;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.game.Module;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SuperBreakerModule implements Module, Listener {

    private WorldGame worldGame;
    private SiegeGamePlugin plugin;
    private int secondsOfSuperBreaker = 10;
    private int cooldownSeconds = 90;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public void onStartGame(SiegeGamePlugin plugin, WorldGame worldGame) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.secondsOfSuperBreaker = plugin.getGameConfig().getSuperBreakerTimer();
        this.cooldownSeconds = plugin.getGameConfig().getSuperBreakerCooldown();
        this.worldGame = worldGame;
        this.plugin = plugin;
    }

    @Override
    public void onEndGame(SiegeGamePlugin plugin, WorldGame worldGame) {
        HandlerList.unregisterAll(this);
        cooldowns.clear();
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        if (plugin == null || worldGame == null)
            return;

        if (gamePlayer == null)
            return;

        if (!event.getAction().isRightClick())
            return;

        if (!player.isSneaking())
            return;

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType().equals(Material.AIR))
            return;

        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.getCompound("siegegame-item");

        if (compound == null)
            return;

        if (!compound.hasKey("custom-properties"))
            return;

        List<String> properties = List.of(compound.getString("custom-properties").split(","));

        if (!properties.contains("super-breaker"))
            return;

        if (isOnCooldown(player.getUniqueId())) {
            long l = getCooldownSeconds(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "You cannot use this for " + ChatColor.YELLOW + l + " seconds");
            return;
        }

        // finally activate
        int previousEfficiency = item.getEnchantmentLevel(Enchantment.DIG_SPEED);
        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);
        player.playSound(Sound.sound(org.bukkit.Sound.ITEM_TRIDENT_RIPTIDE_3.key(), Sound.Source.PLAYER, 1.0F, 0.1F));
        player.sendActionBar(ChatColor.GREEN + "You have activated " + ChatColor.GOLD + "Super Breaker");
        putOnCooldown(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(plugin, () -> item.addEnchantment(Enchantment.DIG_SPEED, Math.min(previousEfficiency, 5)), secondsOfSuperBreaker * 20L);
    }

    private void putOnCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid))
            cooldowns.put(uuid, System.currentTimeMillis());
        else
            cooldowns.replace(uuid, System.currentTimeMillis());
    }

    private boolean isOnCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid))
            return false;

        long cooldownInMillis = cooldownSeconds * 1000L;
        long lastUse = cooldowns.get(uuid);

        return System.currentTimeMillis() - lastUse < cooldownInMillis;
    }

    private long getCooldownSeconds(UUID uuid) {
        if (!isOnCooldown(uuid))
            return 0L;

        long lastUse = cooldowns.get(uuid);
        long cooldownInMillis = cooldownSeconds * 1000L;
        long l = System.currentTimeMillis() - lastUse;
        return (cooldownInMillis - l) / 1000L;
    }

}
