package me.cedric.siegegame.command;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class KitsCommand extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public KitsCommand(SiegeGamePlugin plugin) {
        super("siegegame.kits", "/kits", Message.valueOf("Open kits menu"));
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
        if (sentCommand.getSender().isConsole())
            return;

        Player player = Bukkit.getPlayer(sentCommand.getSender().getUniqueId());

        if (player == null)
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();
        match.getWorldGame().getKitGUI().show(player);
    }
}
























