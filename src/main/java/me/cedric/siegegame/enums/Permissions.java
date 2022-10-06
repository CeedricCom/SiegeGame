package me.cedric.siegegame.enums;

public enum Permissions {

    BORDER_BYPASS("siegegame.bypass.border"),
    RELOAD_FILES("siegegame.admin.reload"),
    CLAIMS_BYPASS("siegegame.admin.bypass.claims");

    private String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
