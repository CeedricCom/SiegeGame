package me.cedric.siegegame.enums;

public enum Permissions {

    BORDER_BYPASS("siegegame.bypass.border"),
    RELOAD_FILES("siegegame.admin.reload"),
    CLAIMS_BYPASS("siegegame.admin.bypass.claims"),
    START_GAME("siegegame.admin.start"),
    RESOURCE_MENU("siegegame.resources"),
    RALLY("siegegame.rally"),
    SPAWN("siegegame.spawn"),
    KITS("siegegame.kits"),

    ;

    private String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
