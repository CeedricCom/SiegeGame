package me.cedric.siegegame.command;

import com.lunarclient.bukkitapi.LunarClientAPI;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.modules.lunarclient.LunarClientSupport;
import me.cedric.siegegame.player.GamePlayer;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RallyCommand extends FunctionalCommand {

    private final SiegeGamePlugin plugin;

    public RallyCommand(SiegeGamePlugin plugin) {
        super("siegegame.rally", "/rally", Message.valueOf("Sets a waypoint at your current location. Requires lunar client"));
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
        if (sentCommand.getSender().isConsole())
            return;

        Player player = Bukkit.getPlayer(sentCommand.getSender().getUniqueId());

        if (player == null)
            return;

        if (!LunarClientAPI.getInstance().isRunningLunarClient(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Lunar Client is required to use this feature.");
            return;
        }

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        WorldGame worldGame = match.getWorldGame();
        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        if (gamePlayer == null || !gamePlayer.hasTeam())
            return;

        LunarClientSupport.sendTemporaryWaypoint(plugin, gamePlayer.getTeam(), player.getLocation(), 30 * 20);
        player.sendMessage(ChatColor.GRAY + Messages.RALLY_SET.toString());
    }
}
