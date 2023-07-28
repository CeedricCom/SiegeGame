package me.cedric.siegegame.command.kits;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.kits.Kit;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class KitsCommand extends FunctionalCommand {

    private final SiegeGamePlugin plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public KitsCommand(SiegeGamePlugin plugin) {
        super("siegegame.kits", "/kits", Message.valueOf("Open kits menu"));
        this.plugin = plugin;
        registerCompleter(1, sentCommand -> List.of("set", "delete"));
        registerCompleter(2, sentCommand -> {
            List<String> maps = new ArrayList<>(plugin.getGameConfig().getMapIDs());
            maps.add("allmaps");
            return maps;
        });
    }

    @Override
    public void commandLogic(SentCommand sentCommand) {
        // /kit set allmaps
        if (sentCommand.getSender().isConsole())
            return;

        Player player = Bukkit.getPlayer(sentCommand.getSender().getUniqueId());

        if (player == null)
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null) {
            player.sendMessage(ChatColor.RED + "You need to be in a match to do this");
            return;
        }

        if (sentCommand.getArgs().size() != 2) {
            player.sendMessage(ChatColor.RED + "/kits set <map or allmaps>");
            player.sendMessage(ChatColor.RED + "/kits delete <map or allmaps>");
            return;
        }

        List<String> maps = new ArrayList<>(plugin.getGameConfig().getMapIDs());
        maps.add("allmaps");

        String identifier = sentCommand.getArgs().get(1).asString();
        if (!maps.contains(identifier)) {
            player.sendMessage(ChatColor.RED + "Could not validate map: " + identifier);
            return;
        }

        WorldGame worldGame = match.getWorldGame();
        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        if (gamePlayer == null)
            return;

        if (isOnCooldown(player.getUniqueId())) {
            long cooldown = getCooldown(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "You need to wait another " + cooldown + " seconds to do this.");
            return;
        }

        if (sentCommand.getArgs().get(0).asString().equalsIgnoreCase("delete")) {
            plugin.getGameManager().getKitStorage().removeKit(player.getUniqueId(), identifier);
            player.sendMessage(ChatColor.GREEN + "Deleted kit for map " + identifier);
            putOnCooldown(player.getUniqueId());
            return;
        }

        plugin.getGameManager().getKitStorage().setKit(player.getUniqueId(), identifier, worldGame, player.getInventory().getContents().clone());
        player.sendMessage(ChatColor.GREEN + "Set your current inventory as your kit for map: " + identifier);
        putOnCooldown(player.getUniqueId());
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
























