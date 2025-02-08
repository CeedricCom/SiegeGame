package me.cedric.siegegame.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.player.GamePlayer;
import me.cedric.siegegame.lunarclient.LunarClientModule;
import me.cedric.siegegame.lunarclient.WaypointSender;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class RallyCommand implements Command<CommandSourceStack> {

    private final SiegeGamePlugin plugin;

    public RallyCommand(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        CommandSender sender = commandContext.getSource().getSender();

        if (!sender.hasPermission(Permissions.RALLY.getPermission()))
            return 0;

        if (sender instanceof ConsoleCommandSender)
            return 0;

        Player player = (Player) sender;

        if (!LunarClientModule.isLunarClient(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Lunar Client is required to use this feature.");
            return 0;
        }

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return 0;

        WorldGame worldGame = match.getWorldGame();
        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        if (gamePlayer == null || !gamePlayer.hasTeam())
            return 0;

        WaypointSender.sendTemporaryWaypoint(plugin, gamePlayer.getTeam(), player.getLocation(), "Rally", 30 * 20);
        player.sendMessage(ChatColor.GRAY + Messages.RALLY_SET);

        return 0;
    }
}
