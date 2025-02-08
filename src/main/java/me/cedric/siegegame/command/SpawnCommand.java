package me.cedric.siegegame.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.player.GamePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements Command<CommandSourceStack> {

    private SiegeGamePlugin plugin;

    public SpawnCommand(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        CommandSender sender = commandContext.getSource().getSender();
        if (sender instanceof ConsoleCommandSender)
            return 0;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return 0;

        Player bukkitPlayer = (Player) sender;

        if (bukkitPlayer == null)
            return 0;

        GamePlayer player = match.getWorldGame().getPlayer(bukkitPlayer.getUniqueId());

        if (player == null || !player.hasTeam())
            return 0;

        player.getBukkitPlayer().teleport(player.getTeam().getSafeSpawn());
        return 0;
    }
}
