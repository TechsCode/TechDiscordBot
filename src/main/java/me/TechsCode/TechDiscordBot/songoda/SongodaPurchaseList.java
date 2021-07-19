package me.TechsCode.TechDiscordBot.songoda;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class SongodaPurchaseList extends ArrayList<SongodaPurchase> {

    public SongodaPurchaseList() {
        super(new ArrayList<>());
    }

    public SongodaPurchaseList(Collection<? extends SongodaPurchase> c) {
        super(c);
    }

    public SongodaPurchaseList userId(String userId) {
        return stream().filter(purchase -> purchase.getUser().getUserId().equals(userId)).collect(Collectors.toCollection(SongodaPurchaseList::new));
    }

    public SongodaPurchaseList username(String username) {
        return stream().filter(purchase -> purchase.getUser().getUsername().equalsIgnoreCase(username)).collect(Collectors.toCollection(SongodaPurchaseList::new));
    }

    public SongodaPurchaseList resource(String resourceId) {
        return stream().filter(purchase -> purchase.getResource().getId().equals(resourceId)).collect(Collectors.toCollection(SongodaPurchaseList::new));
    }

    public SongodaPurchaseList gifted() {
        return stream().filter(p -> p.getCost().isPresent() && p.getCost().get().getValue() == 0F).collect(Collectors.toCollection(SongodaPurchaseList::new));
    }

    public SongodaPurchaseList discord(String discord) {
        return stream().filter(purchase -> purchase.getDiscord() != null && purchase.getDiscord().equalsIgnoreCase(discord)).collect(Collectors.toCollection(SongodaPurchaseList::new));
    }

    public SongodaPurchaseList discord(User user) {
        return stream().filter(purchase -> purchase.getDiscord() != null && purchase.getDiscord().equals(user.getName() + "#" + user.getDiscriminator())).collect(Collectors.toCollection(SongodaPurchaseList::new));
    }

    public SongodaPurchaseList discord(Member member) {
        return discord(member.getUser());
    }
}
