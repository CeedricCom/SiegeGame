package me.cedric.siegegame.event;

import me.cedric.siegegame.model.SiegeGameMatch;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SiegeGameMatchEndEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final SiegeGameMatch siegeGameMatch;

    public SiegeGameMatchEndEvent(SiegeGameMatch siegeGameMatch) {
        this.siegeGameMatch = siegeGameMatch;
    }

    public SiegeGameMatch getSiegeGameMatch() {
        return siegeGameMatch;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
