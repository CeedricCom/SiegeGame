package me.cedric.siegegame.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.SiegeGameMatch;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ResourcesCommand implements Command<CommandSourceStack> {

    private final SiegeGamePlugin plugin;

    public ResourcesCommand(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        CommandSender sender = commandContext.getSource().getSender();
        if (!sender.hasPermission(Permissions.RESOURCE_MENU.getPermission()))
            return 0;

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("players only plz");
            return 0;
        }

        Player player = (Player) sender;
        SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();

        if (gameMatch == null)
            return 0;

        gameMatch.getWorldGame().getShopGUI().getGUI().show(player);
        return 0;
    }
}
