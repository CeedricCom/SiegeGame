package me.cedric.siegegame.command.args;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReloadArg extends FunctionalCommand {

    private final SiegeGame plugin;

    public ReloadArg(SiegeGame plugin) {
        super(Permissions.RELOAD_FILES.getPermission(), "/siegegame reload", Message.valueOf("Reloads files"));
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
        if (sentCommand.getArgOrBlank(0).asString().equalsIgnoreCase("shop")) {
            if (sentCommand.getSender().isConsole())
                return;
            Player player = Bukkit.getPlayer(sentCommand.getSender().getUniqueId());
            if (player != null) {
                SiegeGameMatch siegeGameMatch = plugin.getGameManager().getCurrentMatch();
                plugin.getConfigLoader().reloadShop(siegeGameMatch.getWorldGame());
                sentCommand.getSender().sendMessage("Reloaded.");
            }
        }
    }
}
