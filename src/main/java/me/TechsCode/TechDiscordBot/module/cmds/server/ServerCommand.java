package me.TechsCode.TechDiscordBot.module.cmds.server;

import com.stanjg.ptero4j.entities.objects.server.MinecraftVersion;
import com.stanjg.ptero4j.entities.panel.admin.Server;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.mysql.storage.ServerUser;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.UserPass;
import me.TechsCode.TechDiscordBot.util.PterodactylUtil;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.util.Arrays;

public class ServerCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    public ServerCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getCommand() {
        return "!server";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() {
        return STAFF_ROLE;
    }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.ADMIN;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if (args.length == 0) {
            new TechEmbedBuilder("Server Cmd")
                    .setText("`!server create <mention/id> [mc version]`\n*Creates a server for the member. (Max 1)*\n\n`!server delete <mention/id>`\n*Deletes their server & ptero user if the member has one.*\n\n`!server pass <mention/id>`\n*Resets the members password. (DMs them the new one)*\n\n`!server command <mention/id> <cmd>`\n*Sends a command to the user's server.*\n\n**<> Required**\n*[] Optional*")
                    .send(channel);
        } else {
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length > 1) {
                    Member memberServer = TechDiscordBot.getMemberFromString(message, args[1]);
                    if (memberServer == null) {
                        new TechEmbedBuilder().error().setText("Please specify a valid user id/mention!").sendTemporary(channel, 10);
                    } else {
                        UserPass user = PterodactylUtil.createUser(member, 8);
                        if (user != null) {
                            MinecraftVersion version = MinecraftVersion.getLatestVersion();
                            boolean defaulted = true;
                            if (args.length == 3) {
                                version = MinecraftVersion.getByVersion(args[2]);
                                defaulted = false;
                            }

                            if (defaulted)
                                new TechEmbedBuilder().error().setText("You didn't specify a Minecraft Version, defaulting to the latest version! (" + version.getVersion() + ")").sendTemporary(channel, 8);

                            Server server = PterodactylUtil.createServer(user.getUser(), version);
                            if (server == null) {
                                new TechEmbedBuilder().error().setFooter("Something went wrong while trying to create the server!").sendTemporary(channel, 10);
                            } else {
                                String port = server.getDescription().split(": ")[1];
                                if (channel.getName().startsWith("ticket-")) {
                                    new TechEmbedBuilder("User/Server Information")
                                            .setText(member.getAsMention() + " has created a server for you! Details below.")
                                            .addField("Username", user.getUser().getUsername(), true)
                                            .addField("Password", "`" + user.getPass() + "`", true)
                                            .addField("Server ID", server.getLongId(), true)
                                            .addField("Server IP", "servers.techscode.de:" + port, true)
                                            .addField("Server MC Version", version.getVersion(), true)
                                            .addField("Max Databases", String.valueOf(server.getFeatureLimits().getMaxDatabases()), true)
                                            .addField("Instructions", "Go to https://servers.techscode.de/auth/login and login with the information above. By the time you login, you should see the server!", false)
                                            .send(channel);
                                } else {
                                    try {
                                        new TechEmbedBuilder("User/Server Information")
                                                .setText(member.getAsMention() + " has created a server for you! Details below.")
                                                .addField("Username", user.getUser().getUsername(), true)
                                                .addField("Password", "`" + user.getPass() + "`", true)
                                                .addField("Server ID", server.getLongId(), true)
                                                .addField("Server IP", "servers.techscode.de:" + port, true)
                                                .addField("Server MC Version", version.getVersion(), true)
                                                .addField("Max Databases", String.valueOf(server.getFeatureLimits().getMaxDatabases()), true)
                                                .addField("Instructions", "Go to https://servers.techscode.de/auth/login and login with the information above. By the time you login, you should see the server!", false)
                                                .send(memberServer);
                                    } catch (ErrorResponseException ex) {
                                        new TechEmbedBuilder("User/Server Information")
                                                .setText(member.getAsMention() + " has created a server for you! Details below.")
                                                .addField("Username", user.getUser().getUsername(), true)
                                                .addField("Password", "`" + user.getPass() + "`", true)
                                                .addField("Server ID", server.getLongId(), true)
                                                .addField("Server IP", "servers.techscode.de:" + port, true)
                                                .addField("Server MC Version", version.getVersion(), true)
                                                .addField("Max Databases", String.valueOf(server.getFeatureLimits().getMaxDatabases()), true)
                                                .addField("Instructions", "Go to https://servers.techscode.de/auth/login and login with the information above. By the time you login, you should see the server!", false)
                                                .send(channel);
                                    }
                                }
                            }
                        } else {
                            new TechEmbedBuilder().error().setText("Something went wrong while trying to create the pterodactyl user!").sendTemporary(channel, 10);
                        }
                    }
                } else {
                    new TechEmbedBuilder().error().setText("Please specify a user!").sendTemporary(channel, 10);
                }
            } else if(args[0].equalsIgnoreCase("delete")) {
                if (args.length > 1) {
                    Member memberServer = TechDiscordBot.getMemberFromString(message, args[1]);
                    if (memberServer == null) {
                        new TechEmbedBuilder().error().setText("Please specify a valid user id/mention!").sendTemporary(channel, 10);
                    } else {
                        ServerUser user = TechDiscordBot.getStorage().retrieveServerUserWithDiscord(memberServer);
                        if(user == null || user.getPteroUser() == null || user.getPteroUser().getServers().size() == 0) {
                            new TechEmbedBuilder().error().setText("That user does not have an active server/an account on the panel!").sendTemporary(channel, 10);
                        } else {
                            Server server = user.getPteroUser().getServers().get(0);
                            boolean success = PterodactylUtil.deleteServer(server, true);
                            if(success) {
                                new TechEmbedBuilder().success().setText("Successfully deleted the user & user's server!").sendTemporary(channel, 10);
                            } else {
                                new TechEmbedBuilder().error().setText("Something went wrong while trying to delete the user * user's server!").sendTemporary(channel, 10);
                            }
                        }
                    }
                } else {
                    new TechEmbedBuilder().error().setText("Please specify a user!") .sendTemporary(channel, 10);
                }
            } else if(args[0].equalsIgnoreCase("pass")) {
                if (args.length > 1) {
                    Member memberPtero = TechDiscordBot.getMemberFromString(message, args[1]);
                    if (memberPtero == null) {
                        new TechEmbedBuilder().error().setText("Please specify a valid user id/mention!").sendTemporary(channel, 10);
                    } else {
                        ServerUser user = TechDiscordBot.getStorage().retrieveServerUserWithDiscord(memberPtero);
                        if(user == null || user.getPteroUser() == null) {
                            new TechEmbedBuilder().error().setText("That user does not have an active server/an account on the panel!").sendTemporary(channel, 10);
                        } else {
                            UserPass user2 = PterodactylUtil.updateUserPass(memberPtero, user.getPteroUser(), 8);
                            try {
                                new TechEmbedBuilder("Password Reset")
                                        .setText(member.getAsMention() + " has changed your password! Check out the new password below.")
                                        .addField("New Password", user2.getPass(), true)
                                        .send(memberPtero);
                            } catch (ErrorResponseException ex) {
                                new TechEmbedBuilder("Password Reset")
                                        .setText(member.getAsMention() + " has changed your password! Check out the new password below.")
                                        .addField("New Password", user2.getPass(), true)
                                        .send(channel);
                            }
                        }
                    }
                } else {
                    new TechEmbedBuilder().error().setText("Please specify a user!").sendTemporary(channel, 10);
                }
            } else if(args[0].equalsIgnoreCase("command")) {
                if (args.length > 1) {
                    Member memberPtero = TechDiscordBot.getMemberFromString(message, args[1]);
                    if (memberPtero == null) {
                        new TechEmbedBuilder().error().setText("Please specify a valid user id/mention!").sendTemporary(channel, 10);
                    } else {
                        ServerUser user = TechDiscordBot.getStorage().retrieveServerUserWithDiscord(memberPtero);
                        if(user == null || user.getPteroUser() == null) {
                            new TechEmbedBuilder().error().setText("That user does not have an active server/an account on the panel!").sendTemporary(channel, 10);
                        } else {
                            Server server = user.getPteroUser().getServers().get(0);

                            String command = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                            boolean success = PterodactylUtil.sendCommand(server.getUuid().split("-")[0], command);
                            if(success) {
                                new TechEmbedBuilder().success().setText("The command sent successfully!").sendTemporary(channel, 10);
                            } else {
                                new TechEmbedBuilder().error().setText("The command did not send successfully! Is the server offline?!?").sendTemporary(channel, 10);
                            }
                        }
                    }
                } else {
                    new TechEmbedBuilder().error().setText("Please specify a user!").sendTemporary(channel, 10);
                }
            } else if(args[0].equalsIgnoreCase("test")) {

            }
        }
    }
}
