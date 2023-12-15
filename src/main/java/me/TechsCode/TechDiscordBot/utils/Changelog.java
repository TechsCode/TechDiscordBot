package me.techscode.techdiscordbot.utils;

public enum Changelog {

    V0_1_25(
            "0.1.25",
            "Added /update command",
            "Added /manage command",
            "Pre-release of the verification system"
    ),
    V0_2_0(
            "0.2.0",
            "Fixed RGB link in /answer hex",
            "Removed apply command",
            "Fixed /google command",
            "Finished /ban",
            "Cleanup in TicketStaff command",
            "Removed unneeded method",
            "Added more log messages to PayPal system",
            "Added several new auto reply messages",
            "Fixed text spacing in main file"
    );

    private final String version;
    private final String[] changes;

    Changelog(String version, String... changes) {
        this.version = version;
        this.changes = changes;
    }

    public String getVersion() {
        return version;
    }

    public String[] getChanges() {
        return changes;
    }

    public static Changelog getLatest() {
        return values()[values().length - 1];
    }

    public static Changelog getByName(String name) {
        for (Changelog changelog : values()) {
            if (changelog.getVersion().equalsIgnoreCase(name)) {
                return changelog;
            }
        }
        return null;
    }

    public static Changelog getByVersion(String version) {
        for (Changelog changelog : values()) {
            if (changelog.getVersion().equalsIgnoreCase(version)) {
                return changelog;
            }
        }
        return null;
    }

}
