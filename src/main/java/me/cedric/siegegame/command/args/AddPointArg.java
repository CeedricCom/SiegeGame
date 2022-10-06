package me.cedric.siegegame.command.args;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.territory.Vector2D;
import me.cedric.siegegame.world.WorldGame;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class AddPointArg extends FunctionalCommand {

    private final SiegeGame plugin;

    public AddPointArg(SiegeGame plugin) {
        super(Permissions.RELOAD_FILES.getPermission(), "/siegegame addpoint", Message.valueOf("Adds point"));
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
        if (sentCommand.getSender().isConsole()) {
            sentCommand.getSender().sendMessage("No console");
            return;
        }

        String worldGameConfigKey = sentCommand.getArgOrBlank(0).asString();

        WorldGame worldGame = plugin.getGameManager().getLoadedWorlds().stream().filter(worldGame1 ->
                worldGame1.getConfigKey().equalsIgnoreCase(worldGameConfigKey)).findAny().orElse(null);

        if (worldGame == null) {
            sentCommand.getSender().sendMessage("worldgame is null dog");
            return;
        }

        String teamConfigKey = sentCommand.getArgOrBlank(1).asString();
        Team team = worldGame.getTeams().stream().filter(team1 -> team1.getConfigKey().equalsIgnoreCase(teamConfigKey)).findAny().orElse(null);

        if (team == null) {
            sentCommand.getSender().sendMessage("team is null dog");
            return;
        }

        String originalArgOne = sentCommand.getArgOrBlank(2).asString();
        String[] coordsOne = originalArgOne.split(",");

        String originalArgTwo = sentCommand.getArgOrBlank(3).asString();
        String[] coordsTwo = originalArgTwo.split(",");

        Vector2D one = new Vector2D(Integer.parseInt(coordsOne[0]), Integer.parseInt(coordsOne[1]));
        Vector2D two = new Vector2D(Integer.parseInt(coordsTwo[0]), Integer.parseInt(coordsTwo[1]));

        team.getTerritory().addSquare(one, two);
        sentCommand.getSender().sendMessage("added square");
    }
}



















