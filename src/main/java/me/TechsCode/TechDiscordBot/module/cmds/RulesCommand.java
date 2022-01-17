package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.List;

public class RulesCommand extends CommandModule {

    private final DefinedQuery<net.dv8tion.jda.api.entities.Role> STAFF_ROLE = new DefinedQuery<net.dv8tion.jda.api.entities.Role>() {
        @Override
        protected Query<net.dv8tion.jda.api.entities.Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    private final DefinedQuery<TextChannel> OVERVIEW_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() {
            return bot.getChannels("\uD83D\uDCCC︱overview");
        }
    };

    public RulesCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "rules";
    }

    @Override
    public String getDescription() {
        return "Resend the #overview rules messages.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[0];
    }

    @Override
    public int getCooldown() {
        return 10;
    }

    @Override
    public void onCommand(TextChannel channel, Member member, SlashCommandEvent e) {
        e.reply("Sending messages...").queue();
        OVERVIEW_CHANNEL.query().first().getIterableHistory()
                .takeAsync(200)
                .thenAccept(channel::purgeMessages);

        showAll();
    }

    public void showAll() {
        showTOS();
        showRules_Section1();
        showRules_Section2();
        showRules_Section3();
        showNote();
        showRoles();
    }

    public void showTOS() {
        new TechEmbedBuilder("Discord TOS & Guidelines")
                .text("This server is compliant with the Discord Terms of Service and Guidelines. " +
                        "We will ban if the content is not a complaint. Be sure to familiarize yourself with them here:\n" +
                        "- [Terms of Service](https://dis.gd/tos)\n- [Content Guidelines](https://dis.gd/guidelines)" +
                        "\n\nOur Staff reserve the right to request a member to confirm their age if they are perceived to be potentially under 13. Noncompliance will result in the assumption of being under 13.")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showRules_Section1() {
        new TechEmbedBuilder("Section 1 - Text & Voice Chat")
                .text("**`Article 1.1`**\n" +
                        "Attempting to bypass any blocked words or links may result in punishments.\n" +
                        "**`Article 1.2`**\n" +
                        "Hate speech is strictly prohibited.\n" +
                        "**`Article 1.3`**\n" +
                        "NSFW (Not safe for work) is strictly prohibited, resulting in heavy punishments.\n" +
                        "**`Article 1.4`**\n" +
                        "Repeating a message five or more times will be classified as spam and prohibited.\n" +
                        "**`Article 1.5`**\n" +
                        "Disrespectful, malicious, and harmful messages are strictly prohibited, resulting in heavy punishments.\n" +
                        "**`Article 1.6`**\n" +
                        "Mentioning a member then deleting your message is prohibited and may result in punishments.\n" +
                        "**`Article 1.7`**\n" +
                        "Mass mentioning users is prohibited and will result in punishments.\n" +
                        "**`Article 1.8`**\n" +
                        "Providing any kind of supports to any of Tech's plugins to unverified members is strictly prohibited.")
                .thumbnail("https://i.imgur.com/SfFEnoU.png")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showRules_Section2() {
        new TechEmbedBuilder("Section 2 - Advertising")
                .text("**`Article 2.1`**\n" +
                        "Advertising anywhere other than your about me and status is prohibited and will result in punishments.\n" +
                        "**`Article 2.2`**\n" +
                        "DM Advertising is strictly prohibited and will result in heavy punishments. If a Tech's Plugin Support member directly messages you an advertisement, please create a ticket to report them.\n" +
                        "**`Article 2.3`**\n" +
                        "Advertising content such as scams, malware, or NSFW is strictly prohibited and will result in your account being banned.")
                .thumbnail("https://i.imgur.com/SfFEnoU.png")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showRules_Section3() {
        new TechEmbedBuilder("Section 3 - Other")
                .text("**`Article 3.1`**\n" +
                        "Inappropriate usernames and nicknames are prohibited and result in punishments.\n" +
                        "**`Article 3.2`**\n" +
                        "Using leaked or cracked copies of any resource are prohibited and result in heavy punishments.\n" +
                        "**`Article 3.3`**\n" +
                        "Using or creating a modified version of premium resources is prohibited, resulting in heavy punishments.\n" +
                        "**`Article 3.4`**\n" +
                        "Asking for the staff or other particular roles is prohibited and will result in punishments.\n" +
                        "**`Article 3.5`**\n" +
                        "Promoting the use of leaked or cracked resources are strictly prohibited, resulting in heavy punishments.\n" +
                        "**`Article 3.6`**\n" +
                        "Inviting or having alternative Discord accounts in this server may result in all of your accounts getting banned unless an Assistant or higher grants permission.\n" +
                        "**`Article 3.7`**\n" +
                        "Sharing another user's Personal Identifiable Information (\"PII\") without express permission from them is strictly prohibited and will result in heavy punishments.")
                .thumbnail("https://i.imgur.com/SfFEnoU.png")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showNote() {
        new TechEmbedBuilder()
                .text("**PLEASE NOTE**: Staff do reserve the right to punish for things not listed on this list under the staff's discretion. Please use common sense, and if you are unsure about anything, please ask.")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public void showRoles() {
        StringBuilder sBuilder = new StringBuilder();

        int i = 0;
        for(Role role : Role.values()) {
            if(i != 0)
                sBuilder.append("\n\n");

            sBuilder.append(role.getAsMention()).append(": ").append(role.getDescription());

            i++;
        }

        new TechEmbedBuilder("Roles")
                .text(sBuilder + "\n\nPlease don't ask to be Staff, it's annoying.")
                .queue(OVERVIEW_CHANNEL.query().first());
    }

    public enum Role {

        CODING_WIZARD("Coding Wizard", "311178859171282944", "It was Tech & now it's MATRIX! The former head of the operations."),
        DEVELOPER("Developer", "774690360836096062", "These are the Developers! They are in charge of helping the <@&311178859171282944> with the development of our plugins."),
        ASSISTANT("Assistant", "608113993038561325", "This is the Developers Assistant! If the Developers are not online, he is in charge. Occasionally also helps with coding."),
        STAFF("Staff", "608114002387533844", "They are here to help! Don't argue with Staff. If you think there is an issue, please contact <@&608113993038561325> or <@&311178859171282944>."),
        PATREON("Patreon", "795101981051977788", "These are our incredible Patreon supporters who getting rewarded by us for their monthly support with various rewards, additions and exclusive stuff."),
        DONATOR("Donator", "311179148691505152", "These are amazing people who have donated to Tech!"),
        VERIFIED_CREATOR("Knows how to Code", "435183665719541761", "A role given to people who have well known coding projects."),
        NITRO_BOOSTERS("Nitro Booster", "585559418008109075", "These are **AMAZING** people who are helping us to unlock great features for our Discord!"),
        REVIEW_SQUAD("Review Squad", "457934035549683713", "These are the **AMAZING** people in the community who have review all of their plugin's owned by Tech."),
        VERIFIED("Verified", "416174015141642240", "This role is given to members who have successfully verified that they've purchased *as least* one of Tech's resources.");

        private final String name;
        private final String id;
        private final String description;

        Role(String name, String id, String description) {
            this.name = name;
            this.id = id;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getAsMention() {
            net.dv8tion.jda.api.entities.Role role = TechDiscordBot.getJDA().getRoleById(id);

            if(role == null) return getName();
            return role.getAsMention();
        }

        public String getDescription() {
            return description;
        }
    }
}
