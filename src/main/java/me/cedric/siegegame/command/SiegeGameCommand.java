package me.cedric.siegegame.command;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.command.args.SpawnControlArea;
import me.cedric.siegegame.command.args.StartGameArg;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;

public class SiegeGameCommand extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public SiegeGameCommand(SiegeGamePlugin plugin) {
        super("siegegame.help", "/siegegame", Message.valueOf("SiegeGame help"));
        this.plugin = plugin;
        registerArguments();
    }

    private void registerArguments() {
        registerArgument("start", new StartGameArg(plugin));
        registerArgument("spawncontrolarea", new SpawnControlArea(plugin));
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
        sentCommand.getSender().sendMessage("/siegegame start");
    }
}
