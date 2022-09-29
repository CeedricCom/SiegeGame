package me.cedric.siegegame.config;

public enum Settings {

    POINTS_PER_KILL(500, -1),
    POINTS_TO_END(10000, -1),
    LEVELS_PER_KILL(10, -1);

    final int defaultValue;
    int value;

    Settings(int defaultValue, int value) {
        this.defaultValue = defaultValue;
        this.value = value;
    }

    public int getValue() {
        if (value == -1)
            return defaultValue;
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
