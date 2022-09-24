package me.cedric.siegegame.display.placeholderapi;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.GamePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SiegeGameExpansion extends PlaceholderExpansion {

    private final SiegeGame plugin;

    public SiegeGameExpansion(SiegeGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "siegegame";
    }

    @Override
    public @NotNull String getAuthor() {
        return "cedric";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.1";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null)
            return null;

        if (params.equalsIgnoreCase(getIdentifier()))
            return null;

        //siegegame_team_name_someteam
        String p = params.replace(getIdentifier() + "_", "");
        // now team_name_someteam

        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        for (Placeholder placeholder : Placeholder.values()) {
            if (p.contains(placeholder.getParameter())) {

                String m = p.replace(placeholder.getParameter(), "");
                // now _someteam
                m = m.replaceAll("_", "");
                // now someteam

                return placeholder.getAction().apply(plugin, gamePlayer, m);
            }

        }

        return null;
    }
}
