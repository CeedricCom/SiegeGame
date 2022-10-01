package me.cedric.siegegame.config;

import java.util.ArrayList;

public enum Settings {

    POINTS_PER_KILL("points-per-kill", 500, null),
    POINTS_TO_END("points-to-end", 10000, null),
    LEVELS_PER_KILL("levels-per-kill", 10, null),
    RESPAWN_TIMER("respawn-timer", 30, null),

    START_GAME_COMMANDS("start-game-commands", new ArrayList<String>(), null),
    END_GAME_COMMANDS("end-game-commands", new ArrayList<String>(), null),
    RESPAWN_COMMANDS("respawn-commands", new ArrayList<String>(), null),
    DEATH_COMMANDS("death-commands", new ArrayList<String>(), null),

    BLACKLISTED_PROJECTILES("blacklisted-projects", new ArrayList<String>(), null);

    private final Object defaultValue;
    private final String path;
    private Object value;

    Settings(String path, Object defaultValue, Object value) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.value = value;
    }

    public Object getValue() {
        if (value == null)
            return defaultValue;
        return value;
    }

    public String getPath() {
        return path;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
