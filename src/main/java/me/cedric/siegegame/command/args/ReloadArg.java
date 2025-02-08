package me.cedric.siegegame.command.args;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Permissions;
import org.bukkit.command.CommandSender;

public class ReloadArg implements Command<CommandSourceStack> {

    private final SiegeGamePlugin plugin;

    public ReloadArg(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        CommandSender sender = commandContext.getSource().getSender();
        if (!sender.hasPermission(Permissions.RELOAD_FILES.getPermission())) {
            sender.sendMessage("No permission.");
            return 0;
        }

        plugin.getGameConfig().reloadConfig();
        return 0;
    }
}
