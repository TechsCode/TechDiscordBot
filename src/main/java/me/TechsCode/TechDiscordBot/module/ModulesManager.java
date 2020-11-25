package me.TechsCode.TechDiscordBot.module;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.Cooldown;
import me.TechsCode.TechDiscordBot.util.ProjectUtil;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ModulesManager {

    private final List<CommandModule> cmdModules = new ArrayList<>();
    private final List<Module> modules = new ArrayList<>();

    public void load() {
        for (Class<?> each : ProjectUtil.getClasses("me.TechsCode.TechDiscordBot.module")) {
            if (CommandModule.class.isAssignableFrom(each) && !Modifier.isAbstract(each.getModifiers())) {
                try {
                    CommandModule module = (CommandModule)each.getConstructor(TechDiscordBot.class).newInstance(TechDiscordBot.getBot());
                    module.enable();
                    cmdModules.add(module);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else if (Module.class.isAssignableFrom(each) && !Modifier.isAbstract(each.getModifiers())) {
                try {
                    Module module = (Module) each.getConstructor(TechDiscordBot.class).newInstance(TechDiscordBot.getBot());
                    module.enable();
                    modules.add(module);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        TechDiscordBot.getJDA().addEventListener(modules.toArray());
        TechDiscordBot.getJDA().addEventListener(cmdModules.toArray());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> modules.forEach(Module::onDisable)));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> cmdModules.forEach(CommandModule::onDisable)));
    }

    public void logLoad() {
        int successfulAmountModules = (int)modules.stream().filter(Module::isEnabled).count();
        int successfulAmountCmdModules = (int)cmdModules.stream().filter(CommandModule::isEnabled).count();

        int successfulAmount = successfulAmountModules + successfulAmountCmdModules;

        int failedModules = (int)modules.stream().filter(Module::isDisabled).count() + (int)cmdModules.stream().filter(CommandModule::isDisabled).count();

        TechDiscordBot.log("Modules:");
        TechDiscordBot.log("  » All: " + (modules.size() + cmdModules.size()));
        TechDiscordBot.log("  » Modules: " + modules.size());
        TechDiscordBot.log("  » Command: " + cmdModules.size());
        TechDiscordBot.log("  » Success: " + successfulAmount);
        TechDiscordBot.log("  » Failed: " + failedModules);
    }

    @SubscribeEvent
    public void onMessage(GuildMessageReceivedEvent e) {
        if(e.getMember() == null) return;
        String first = e.getMessage().getContentDisplay().split(" ")[0];

        CommandModule cmd = cmdModules.stream().filter(cmdM -> cmdM.getCommand() != null && cmdM.getCommand().equalsIgnoreCase(first) || (cmdM.getAliases() != null && Arrays.asList(cmdM.getAliases()).contains(first))).findFirst().orElse(null);
        if(cmd == null) return;

        List<Role> restrictedRoles = new ArrayList<>();
        if(cmd.getRestrictedRoles() != null && cmd.getRestrictedRoles().query() != null && cmd.getRestrictedRoles().query().all() != null) restrictedRoles.addAll(cmd.getRestrictedRoles().query().all());
        List<TextChannel> restrictedChannels =  new ArrayList<>();
        if(cmd.getRestrictedChannels() != null && cmd.getRestrictedChannels().query() != null && cmd.getRestrictedChannels().query().all() != null) restrictedChannels.addAll(cmd.getRestrictedChannels().query().all());

        // Check if the player has at least one of the restricted roles
        if(!restrictedRoles.isEmpty() && Collections.disjoint(e.getMember().getRoles(), restrictedRoles)) {
            new TechEmbedBuilder("Not Enough Perms")
                    .error()
                    .setText("You don't have enough permissions to execute this command!")
                    .sendTemporary(e.getChannel(), 5, TimeUnit.SECONDS);
            return;
        }

        if(cmd.getCooldown() > 0 && cmd.getCooldowns().containsKey(e.getMember().getId())) {
            boolean remaining = cmd.getCooldowns().get(e.getMember().getId()).isCooldownRemaining();
            if(remaining) return;
            cmd.getCooldowns().remove(e.getMember().getId());
        }

        // Check if the message was sent in one of the restricted channels (if there are any)
        if (!restrictedChannels.isEmpty() && !restrictedChannels.contains(e.getChannel())) return;

        String message = e.getMessage().getContentDisplay();
        String[] args = Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length);

        if(cmd.deleteCommandMsg())
            e.getMessage().delete().complete();

        cmd.onCommand(e.getChannel(), e.getMessage(), e.getMember(), args);
        if(!cmd.getCooldowns().containsKey(e.getMember().getId())) cmd.getCooldowns().put(e.getMember().getId(), new Cooldown(OffsetDateTime.now().plusSeconds(cmd.getCooldown())));
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<CommandModule> getCommandModules() {
        return cmdModules;
    }

    public void disableAll() {
        getModules().forEach(Module::onDisable);
        getCommandModules().forEach(CommandModule::onDisable);
    }
}