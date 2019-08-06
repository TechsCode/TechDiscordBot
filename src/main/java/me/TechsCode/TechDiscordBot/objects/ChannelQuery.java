package me.TechsCode.TechDiscordBot.objects;

import me.TechsCode.TechDiscordBot.Query;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelQuery extends Query<TextChannel> {

    public ChannelQuery(List<TextChannel> objects) {
        super(objects);
    }

    public ChannelQuery inCategory(String category){
        List<TextChannel> channels = all().stream().filter(textChannel -> textChannel.getParent().getName().equalsIgnoreCase(category)).collect(Collectors.toList());

        return new ChannelQuery(channels);
    }

    public ChannelQuery inCategory(Category category){
        return inCategory(category.getName());
    }
}
