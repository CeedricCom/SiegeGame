package me.cedric.siegegame.modules.capturepoint;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.game.Module;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.map.GameMap;

import java.util.ArrayList;
import java.util.List;

public class ControlAreaModule implements Module {

    private ControlTask controlTask;

    @Override
    public void initialise(SiegeGamePlugin plugin, WorldGame worldGame) {

    }

    @Override
    public void shutdown(SiegeGamePlugin plugin, WorldGame worldGame) {
        controlTask.cancel();
    }

    @Override
    public void onStartGame(SiegeGamePlugin plugin, WorldGame worldGame) {
        startControlAreas(plugin, worldGame);
    }

    @Override
    public void onEndGame(SiegeGamePlugin plugin, WorldGame worldGame) {
        controlTask.cancel();
    }

    private void startControlAreas(SiegeGamePlugin plugin, WorldGame worldGame) {
        GameMap map = plugin.getGameManager().getCurrentMatch().getGameMap();
        List<ControlAreaHandler> controlAreaHandlers = new ArrayList<>();

        //controlAreaHandlers.add(new EffectControlArea(plugin, map.getWorld(), new Cuboid(new Vector3D(3947, 70, -8138), new Vector3D(3967, 80, -8158)),
        //        10, 70, map, "dog", "z tier"));

        for (ControlAreaHandler area : controlAreaHandlers) {
            area.generate();
        }

        controlTask = new ControlTask(worldGame, controlAreaHandlers);
        controlTask.runTaskTimer(plugin, 0, 20);
    }

    public void addControlArea(ControlAreaHandler handler) {
        controlTask.addControlArea(handler);
    }

    public void removeControlArea(int index) {
        controlTask.removeControlArea(index);
    }
}
