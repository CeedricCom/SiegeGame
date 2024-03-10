package me.cedric.siegegame.modules.lunarclient;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.game.Module;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.player.GamePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LunarClientModule implements Module {

    private TeamUpdateTask teamViewUpdateTask;
    private WaypointSender waypointSender;

    @Override
    public void onStartGame(SiegeGamePlugin plugin, WorldGame worldGame) {
        teamViewUpdateTask = new TeamUpdateTask(worldGame);
        teamViewUpdateTask.runTaskTimer(plugin, 1L, 1L);

        waypointSender = new WaypointSender(worldGame);
        waypointSender.send();
    }

    @Override
    public void onEndGame(SiegeGamePlugin plugin, WorldGame worldGame) {
        teamViewUpdateTask.cancel();
        waypointSender.removeAll();
    }

    public static boolean isLunarClient(UUID uuid) {
        return Apollo.getPlayerManager().hasSupport(uuid);
    }
}
