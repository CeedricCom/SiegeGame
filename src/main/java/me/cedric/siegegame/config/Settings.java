package me.cedric.siegegame.config;

public enum Settings {

    POINTS_PER_KILL("points-per-kill", 500, -1),
    POINTS_TO_END("points-to-end", 10000, -1),
    LEVELS_PER_KILL("levels-per-kill", 10, -1),
    RESPAWN_TIMER("respawn-timer", 30, -1);

    private final int defaultValue;
    private final String path;
    private int value;

    Settings(String path, int defaultValue, int value) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.value = value;
    }

    public int getValue() {
        if (value == -1)
            return defaultValue;
        return value;
    }

    public String getPath() {
        return path;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
