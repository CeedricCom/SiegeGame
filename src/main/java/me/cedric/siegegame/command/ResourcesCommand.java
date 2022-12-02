package me.cedric.siegegame.command;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResourcesCommand extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public ResourcesCommand(SiegeGamePlugin plugin) {
        super("siegegame.resources", "/resources", Message.valueOf("Opens the resource menu or shop"));
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
        if (sentCommand.getSender().isConsole()) {
            sentCommand.getSender().sendMessage("players only plz");
            return;
        }

        Player player = Bukkit.getPlayer(sentCommand.getSender().getUniqueId());

        if (player == null) {
            System.out.println("player equals null for some fucked reason");
            return;
        }

        SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();

        if (gameMatch == null)
            return;

        gameMatch.getWorldGame().getShopGUI().getGUI().show(player);
    }
}
