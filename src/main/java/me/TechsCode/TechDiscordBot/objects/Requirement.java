package me.TechsCode.TechDiscordBot.objects;

import java.util.Objects;

public class Requirement {

    private final DefinedQuery<?> query;
    private final int matchesRequired;
    private final String unmatchMessage;

    public Requirement(DefinedQuery<?> query, int matchesRequired, String unmatchMessage) {
        Objects.requireNonNull(query, "Defined Query cannot be null!");
        Objects.requireNonNull(unmatchMessage, "Message cannot be null!");

        this.query = query;
        this.matchesRequired = matchesRequired;
        this.unmatchMessage = unmatchMessage;
    }

    public boolean check() {
        return query.query().amount() >= matchesRequired;
    }

    public String getUnmatchMessage() {
        return unmatchMessage;
    }
}