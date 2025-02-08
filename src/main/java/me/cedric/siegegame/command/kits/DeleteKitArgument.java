package me.cedric.siegegame.command.kits;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeleteKitArgument implements Command<CommandSourceStack> {

    private final SiegeGamePlugin plugin;

    public DeleteKitArgument(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int run(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        CommandSender sender = commandContext.getSource().getSender();
        if (!sender.hasPermission(Permissions.KITS.getPermission()))
            return 0;

        List<String> maps = new ArrayList<>(plugin.getGameConfig().getMapIDs());
        maps.add("allmaps");

        String identifier = commandContext.getArgument("identifier", String.class);
        if (identifier == null || identifier.isEmpty() || !maps.contains(identifier)) {
            sender.sendMessage("Could not find map with identifier: " + identifier);
            return 0;
        }

        Player player = (Player) sender;
        plugin.getGameManager().getKitController().removeKit(player.getUniqueId(), identifier);
        player.sendMessage(ChatColor.GREEN + "Deleted kit for map " + identifier);

        return 0;
    }
}
