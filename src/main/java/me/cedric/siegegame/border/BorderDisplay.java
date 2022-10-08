package me.cedric.siegegame.border;

import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.map.GameMap;
import me.cedric.siegegame.player.GamePlayer;

public abstract class BorderDisplay {

    protected final GamePlayer player;
    protected final Border border;
    public final static int MIN_DISTANCE = 20;

    protected BorderDisplay(GamePlayer player, Border border) {
        this.border = border;
        this.player = player;
    }

    public Border getBorder() {
        return border;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public abstract void update(GameMap gameMap, Border border);

    public abstract void create(GameMap gameMap, Border border);

    public abstract void destroy(GameMap gameMap, Border border);

}
