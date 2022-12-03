package me.cedric.siegegame.modules.lunarclient;

import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamViewUpdateTask extends BukkitRunnable {

    private final WorldGame worldGame;

    public TeamViewUpdateTask(WorldGame worldGame) {
        this.worldGame = worldGame;
    }

    @Override
    public void run() {
        for (Team team : worldGame.getTeams()) {
            LunarClientSupport.updateTeammateView(team);
        }
    }


}
