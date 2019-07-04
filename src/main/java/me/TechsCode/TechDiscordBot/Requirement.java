package me.TechsCode.TechDiscordBot;

public abstract class Requirement {

    public abstract boolean check(TechDiscordBot bot);

    public abstract String getFailReason();
}
