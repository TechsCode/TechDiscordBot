package me.TechsCode.TechDiscordBot.modules;

import me.TechsCode.TechDiscordBot.Module;
import me.TechsCode.TechDiscordBot.Query;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class MessageSender extends Module {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query newQuery() {
            return bot.getRoles("staff");
        }
    };

    public MessageSender(TechDiscordBot bot) {
        super(bot);
    }

    @SubscribeEvent
    public void receive(GuildMessageReceivedEvent e){
        if(!e.getMember().getRoles().contains(STAFF_ROLE.query().first())) return;

        String message = e.getMessage().getContentDisplay();

        if(message.startsWith("^^ ") && message.endsWith(" ^^")){
            e.getMessage().delete().complete();

            String text = message.substring(3, message.length()-3);
            e.getChannel().sendMessage(text).complete();
            return;
        }

        if(message.startsWith("^ ")){
            e.getMessage().delete().complete();

            String text = message.substring(2);
            String[] arguments = text.split("\\^");

            if(arguments.length != 2){
                new CustomEmbedBuilder("Invalid Arguments").setText("Usage: ^ Title ^ Message").error().sendTemporary(e.getChannel(), 5);
                return;
            }

            new CustomEmbedBuilder(arguments[0])
                    .setFooter("Posted by "+e.getAuthor().getName())
                    .setText(arguments[1])
                    .send(e.getChannel());
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() { }

    @Override
    public String getName() {
        return "Message Sender";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[]{
                new Requirement(STAFF_ROLE, 1, "Missing 'Staff' Role")
        };
    }
}
