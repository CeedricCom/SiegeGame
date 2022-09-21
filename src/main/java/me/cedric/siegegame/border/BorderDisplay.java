package me.cedric.siegegame.border;

import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.world.WorldGame;

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

    public abstract void update(WorldGame worldGame, Border border);

    public abstract void create(WorldGame worldGame, Border border);

    public abstract void destroy(WorldGame worldGame, Border border);

}
