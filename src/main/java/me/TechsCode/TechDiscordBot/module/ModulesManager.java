package me.TechsCode.TechDiscordBot.module;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.Cooldown;
import me.TechsCode.TechDiscordBot.util.ProjectUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModulesManager {

    private final List<CommandModule> cmdModules = new ArrayList<>();
    private final List<Module> modules = new ArrayList<>();

    public void load() {
        TechDiscordBot.getJDA().updateCommands().queue();
        CommandListUpdateAction commands = TechDiscordBot.getGuild().updateCommands();

        for (Class<?> each : ProjectUtil.getClasses("me.TechsCode.TechDiscordBot.module")) {
            if (CommandModule.class.isAssignableFrom(each) && !Modifier.isAbstract(each.getModifiers())) {
                try {
                    CommandModule module = (CommandModule)each.getConstructor(TechDiscordBot.class).newInstance(TechDiscordBot.getBot());
                    if(module.getName() == null)
                        continue;

                    cmdModules.add(module);

                    CommandData cmdData = new CommandData(module.getName(), module.getDescription() == null ? "No description set." : module.getDescription())
                            .addOptions(module.getOptions())
                            .setDefaultEnabled(module.getCommandPrivileges().length == 0);

                    commands.addCommands(cmdData);
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

        commands.addCommands(
            new CommandData("ticket", "Manage tickets.")
                .addSubcommands(
                    new SubcommandData("add", "Add a member to a ticket.")
                        .addOptions(
                            new OptionData(OptionType.USER, "member", "Member to add.", true)
                    ),
                    new SubcommandData("remove", "Remove a member from a ticket.")
                        .addOptions(
                            new OptionData(OptionType.USER, "member", "Member to remove.", true)
                    ),
                    new SubcommandData("close", "Close a ticket.")
                        .addOptions(
                            new OptionData(OptionType.STRING, "reason", "Reason to close the ticket. (Optional)")
                    ),
                    new SubcommandData("transcript", "Force make a ticket transcript.")
                )
        ).queue(cmds -> {
            cmds.forEach(command -> {
                CommandPrivilege[] privilege = cmdModules.stream().filter(c -> c.getName().equals(command.getName())).map(CommandModule::getCommandPrivileges).findFirst().orElse(new CommandPrivilege[]{});

                if (privilege.length > 0)
                    TechDiscordBot.getGuild().updateCommandPrivilegesById(command.getId(), Arrays.asList(privilege)).queue();
            });
        });

        TechDiscordBot.getJDA().addEventListener(modules.toArray());
        TechDiscordBot.getJDA().addEventListener(cmdModules.toArray());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> modules.forEach(Module::onDisable)));
    }

    public void logLoad() {
        int successfulAmountModules = (int)modules.stream().filter(Module::isEnabled).count();

        TechDiscordBot.log("Modules:");
        TechDiscordBot.log("  » All: " + (modules.size() + cmdModules.size()));
        TechDiscordBot.log("  » Modules: " + modules.size());
        TechDiscordBot.log("  » Command: " + cmdModules.size());
        TechDiscordBot.log("  » Success: " + successfulAmountModules);
    }

    @SubscribeEvent
    public void onSlashCommand(SlashCommandEvent e) {
        CommandModule cmd = cmdModules.stream().filter(c -> c.getName().equalsIgnoreCase(e.getName())).findFirst().orElse(null);
        if(cmd == null || e.getMember() == null || e.getUser().isBot())
            return;

        if(cmd.getCooldown() > 0 && cmd.getCooldowns().containsKey(e.getMember().getId())) {
            Cooldown cooldown = cmd.getCooldowns().get(e.getMember().getId());
            if(cooldown.isCooldownRemaining()) {
                e.deferReply(true).queue();

                e.reply("**Woah there... slow down!** There's still **" + cooldown.getRemainingCooldown() + "** seconds left on your cooldown!").queue();
                return;
            }

            cmd.getCooldowns().remove(e.getMember().getId());
        }

        cmd.onCommand(e.getTextChannel(), e.getMember(), e);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<CommandModule> getCommandModules() {
        return cmdModules;
    }
}