package me.cedric.siegegame.command.kits;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.kits.Kit;
import me.cedric.siegegame.player.kits.PlayerKitManager;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitsCommand extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public KitsCommand(SiegeGamePlugin plugin) {
        super("siegegame.kits", "/kits", Message.valueOf("Open kits menu"));
        this.plugin = plugin;
        registerCompleter(1, sentCommand -> List.of("set"));
        registerCompleter(2, sentCommand -> {
            List<String> maps = new ArrayList<>(plugin.getGameConfig().getMapIDs());
            maps.add("allmaps");
            return maps;
        });
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
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

        PlayerKitManager kitManager = gamePlayer.getPlayerKitManager();
        kitManager.setKit(player.getInventory().getContents(), worldGame, identifier);
        player.sendMessage(ChatColor.GREEN + "Set your current inventory as your kit for map: " + identifier);
    }
}
























