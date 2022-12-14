package me.cedric.siegegame.modules.lunarclient;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.game.Module;
import me.cedric.siegegame.model.game.WorldGame;

public class LunarClientModule implements Module {

    private TeamViewUpdateTask teamViewUpdateTask;

    @Override
    public void onStartGame(SiegeGamePlugin plugin, WorldGame worldGame) {
        teamViewUpdateTask = new TeamViewUpdateTask(worldGame);
        teamViewUpdateTask.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void onEndGame(SiegeGamePlugin plugin, WorldGame worldGame) {
        teamViewUpdateTask.cancel();
    }
}
