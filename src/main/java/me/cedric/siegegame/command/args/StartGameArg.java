package me.cedric.siegegame.command.args;

import me.cedric.siegegame.SiegeGamePlugin;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;

public class StartGameArg extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public StartGameArg(SiegeGamePlugin plugin) {
        super("siegegame.admin.start");
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand sentCommand) {
        plugin.getGameManager().startNextGame();
    }
}
