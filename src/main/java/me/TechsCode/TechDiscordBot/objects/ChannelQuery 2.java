package me.TechsCode.TechDiscordBot.objects;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChannelQuery extends Query<TextChannel> {

    public ChannelQuery(List<TextChannel> objects) {
        super(objects);
    }

    public ChannelQuery inCategory(String category) {
        List<TextChannel> channels = all().stream().filter(textChannel -> Objects.requireNonNull(textChannel.getParent()).getName().equalsIgnoreCase(category)).collect(Collectors.toList());
        return new ChannelQuery(channels);
    }

    public ChannelQuery inCategory(Category category) {
        return inCategory(category.getName());
    }
}