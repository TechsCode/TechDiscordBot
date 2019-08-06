package me.TechsCode.TechDiscordBot.modules;

import me.TechsCode.TechDiscordBot.Module;
import me.TechsCode.TechDiscordBot.Query;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class EmbedMessageSender extends Module {

    private final DefinedQuery<Role> SUPPORTER_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query newQuery() {
            return bot.getRoles("supporter");
        }
    };

    public EmbedMessageSender(TechDiscordBot bot) {
        super(bot);
    }

    @SubscribeEvent
    public void receive(MessageReceivedEvent e){
        if(!e.getMember().getRoles().contains(SUPPORTER_ROLE.query().first())) return;

        TextChannel textChannel = e.getTextChannel();

        if(e.getMessage().getContentDisplay().startsWith("^ ")){
            e.getMessage().delete().complete();

            if(e.getMessage().getContentDisplay().length() <= 2){
                return;
            }

            String text = e.getMessage().getContentDisplay().substring(2);
            String[] arguments = text.split("\\^");

            if(arguments.length != 2){
                new CustomEmbedBuilder("Invalid Arguments").setText("Usage: ^ Title ^ Message").error().sendTemporary(textChannel, 5);
                return;
            }

            CustomEmbedBuilder message = new CustomEmbedBuilder(arguments[0]);
            message.setFooter("Posted by "+e.getAuthor().getName());
            message.setText(arguments[1]);
            message.send(textChannel);
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() { }

    @Override
    public String getName() {
        return "Embed Message Sender";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[]{
                new Requirement(SUPPORTER_ROLE, 1, "Missing 'Supporter' Role")
        };
    }
}
