package me.cedric.siegegame.command;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.command.args.StartGameArg;
import me.cedric.siegegame.command.args.TeamsArg;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;

public class SiegeGameCommand extends FunctionalCommand {

    private final SiegeGame plugin;

    public SiegeGameCommand(SiegeGame plugin) {
        super("siegegame.help", "/siegegame", Message.valueOf("SiegeGame help"));
        this.plugin = plugin;
        registerArguments();
    }

    private void registerArguments() {
        registerArgument("start", new StartGameArg(plugin));
        registerArgument("teams", new TeamsArg(plugin));
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
        sentCommand.getSender().sendMessage("/siegegame start");
        sentCommand.getSender().sendMessage("/siegegame teams");
        sentCommand.getSender().sendMessage("/siegegame resources");
    }
}
