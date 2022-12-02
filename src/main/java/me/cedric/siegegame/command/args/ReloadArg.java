package me.cedric.siegegame.command.args;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Permissions;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;

public class ReloadArg extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public ReloadArg(SiegeGamePlugin plugin) {
        super(Permissions.RELOAD_FILES.getPermission(), "/siegegame reload", Message.valueOf("Reloads siegegame config"));
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
        plugin.getGameConfig().reloadConfig();
        sentCommand.getSender().sendMessage("Reloaded");
    }
}
