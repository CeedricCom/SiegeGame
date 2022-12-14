package me.cedric.siegegame.model.game;

import me.cedric.siegegame.SiegeGamePlugin;

public interface Module {

    void onStartGame(SiegeGamePlugin plugin, WorldGame worldGame);

    void onEndGame(SiegeGamePlugin plugin, WorldGame worldGame);

}
