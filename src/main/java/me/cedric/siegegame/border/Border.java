package me.cedric.siegegame.border;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class Border {

    @NotNull
    private BoundingBox boundingBox;
    @NotNull private final UUID uniqueId;
    private boolean blockBreakAllowed = false;
    private boolean blockPlaceAllowed = false;
    private boolean canLeave = false;
    private boolean inverse = false;

    public Border(@NotNull BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
        uniqueId = UUID.randomUUID();
    }

    protected Border(@NotNull UUID borderId, @NotNull BoundingBox boundingBox) {
        this.uniqueId = borderId;
        this.boundingBox = boundingBox;
    }

    public static Border load(String borderId, BoundingBox boundingBox) {
        return new Border(UUID.fromString(borderId),boundingBox);
    }

    public boolean isInverse() {
        return this.inverse;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    @NotNull
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(@NotNull BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    @NotNull
    public UUID getUniqueId() {
        return uniqueId;
    }

    public boolean blockBreakAllowed() {
        return blockBreakAllowed;
    }

    public boolean blockPlaceAllowed() {
        return blockPlaceAllowed;
    }

    public boolean canLeave() {
        return canLeave;
    }

    public void setBlockBreakAllowed(boolean blockBreakAllowed) {
        this.blockBreakAllowed = blockBreakAllowed;
    }

    public void setBlockPlaceAllowed(boolean blockPlaceAllowed) {
        this.blockPlaceAllowed = blockPlaceAllowed;
    }

    public void setCanLeave(boolean canLeave) {
        this.canLeave = canLeave;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Border border = (Border) o;
        return Objects.equals(boundingBox, border.boundingBox) && Objects.equals(uniqueId, border.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boundingBox, uniqueId);
    }

    public void update(Border border) {
        this.boundingBox = border.boundingBox;
        this.blockBreakAllowed = border.blockBreakAllowed;
        this.blockPlaceAllowed = border.blockPlaceAllowed;
        this.canLeave = border.canLeave;
    }

    public void updateBoundingBox(Border border) {
        this.boundingBox = border.boundingBox;
    }

    public void setPermissions(boolean leave, boolean canBreak, boolean canPlace) {
        this.canLeave = leave;
        this.blockBreakAllowed = canBreak;
        this.blockPlaceAllowed = canPlace;
    }
}

