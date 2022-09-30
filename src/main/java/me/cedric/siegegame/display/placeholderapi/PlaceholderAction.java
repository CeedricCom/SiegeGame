package me.cedric.siegegame.display.placeholderapi;

@FunctionalInterface
public interface PlaceholderAction<T, U, R, X, Z> {

    X apply(T t, U u, R r, Z z);

}
