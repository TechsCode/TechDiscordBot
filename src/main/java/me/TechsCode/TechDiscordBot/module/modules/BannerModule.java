package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.logs.ServerLogs;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.Plugin;
import net.dv8tion.jda.api.entities.Icon;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BannerModule extends Module {

    private Plugin[] plugins = Plugin.values();
    private int current = 0;

    public BannerModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        new Thread(() -> {
            while(true) {
                updateBanner();

                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(5L));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updateBanner() {
        try {
            TechDiscordBot.getGuild().getManager().setBanner(Icon.from(Objects.requireNonNull(plugins[current].getBannerAsFile()))).queue();

            current++;
            if(current >= plugins.length) current = 0;
        } catch (Exception ex) {
            ServerLogs.log(ex.getMessage());
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getName() {
        return "Banner";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[0];
    }
}
