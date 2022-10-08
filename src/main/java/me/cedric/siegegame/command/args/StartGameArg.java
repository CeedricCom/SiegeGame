package me.cedric.siegegame.command.args;

import me.cedric.siegegame.SiegeGame;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;

public class StartGameArg extends FunctionalCommand {

    private final SiegeGame plugin;

    public StartGameArg(SiegeGame plugin) {
        super("siegegame.admin.start");
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand sentCommand) {
        if (plugin.getGameManager().getNextMatch() == null) {
            sentCommand.getSender().sendMessage("No map at the head of queue. Cannot start game");
            return;
        }

        plugin.getGameManager().startNextMap();
    }
}
