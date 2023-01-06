package me.cedric.siegegame.command;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.player.GamePlayer;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnCommand extends FunctionalCommand {

    private SiegeGamePlugin plugin;

    public SpawnCommand(SiegeGamePlugin plugin) {
        super("siegegame.spawn");
        this.plugin = plugin;
        registerCompleter(1, sentCommand -> List.of("spawn"));
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
        if (sentCommand.getSender().isConsole())
            return;

        if (sentCommand.getArgs().size() < 1 || !sentCommand.getArgs().get(0).asString().equalsIgnoreCase("spawn"))
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        Player bukkitPlayer = Bukkit.getPlayer(sentCommand.getSender().getUniqueId());

        if (bukkitPlayer == null)
            return;

        GamePlayer player = match.getWorldGame().getPlayer(bukkitPlayer.getUniqueId());

        if (player == null || !player.hasTeam())
            return;

        player.getBukkitPlayer().teleport(player.getTeam().getSafeSpawn());
    }
}
