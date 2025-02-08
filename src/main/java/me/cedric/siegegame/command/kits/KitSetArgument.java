package me.cedric.siegegame.command.kits;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class KitSetArgument implements Command<CommandSourceStack> {

    private final SiegeGamePlugin plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public KitSetArgument(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        // /kit set allmaps
        CommandSender sender = commandContext.getSource().getSender();
        if (!sender.hasPermission(Permissions.KITS.getPermission()))
            return 0;

        if (sender instanceof ConsoleCommandSender)
            return 0;

        Player player = (Player) sender;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null) {
            player.sendMessage(ChatColor.RED + "You need to be in a match to do this");
            return 0;
        }

        String identifier = commandContext.getArgument("map", String.class);

        if (identifier == null) {
            player.sendMessage(ChatColor.RED + "/kits set <map or allmaps>");
            player.sendMessage(ChatColor.RED + "/kits delete <map or allmaps>");
            return 0;
        }

        List<String> maps = new ArrayList<>(plugin.getGameConfig().getMapIDs());
        maps.add("allmaps");

        if (!maps.contains(identifier)) {
            player.sendMessage(ChatColor.RED + "Could not validate map: " + identifier);
            return 0;
        }

        WorldGame worldGame = match.getWorldGame();
        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        if (gamePlayer == null)
            return 0;

        if (isOnCooldown(player.getUniqueId())) {
            long cooldown = getCooldown(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "You need to wait another " + cooldown + " seconds to do this.");
            return 0;
        }

        plugin.getGameManager().getKitController().saveKit(player.getUniqueId(), identifier, worldGame, player.getInventory().getContents().clone());
        player.sendMessage(ChatColor.GREEN + "Set your current inventory as your kit for map: " + identifier);
        putOnCooldown(player.getUniqueId());
        return 0;
    }

    private boolean isOnCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid))
            return false;

        long lastTime = cooldowns.get(uuid);
        long currentTime = System.currentTimeMillis();

        return currentTime - lastTime < 15 * 1000L;
    }

    private void putOnCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }

    private long getCooldown(UUID uuid) {
        if (!cooldowns.containsKey(uuid))
            return 0L;

        long lastTime = cooldowns.get(uuid);
        long currentTime = System.currentTimeMillis();

        return ((15 * 1000L) - (currentTime - lastTime)) / 1000;
    }
}

