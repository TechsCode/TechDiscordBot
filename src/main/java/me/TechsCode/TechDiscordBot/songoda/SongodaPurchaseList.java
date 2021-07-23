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
        return new SongodaPurchaseList(this).stream().filter(purchase -> purchase.getUser().getUserId().equals(userId)).collect(Collectors.toCollection(SongodaPurchaseList::new));
    }

    public SongodaPurchaseList username(String username) {
        return new SongodaPurchaseList(this).stream().filter(purchase -> purchase.getUser().getUsername().equalsIgnoreCase(username)).collect(Collectors.toCollection(SongodaPurchaseList::new));
    }

    public SongodaPurchaseList resource(String resourceId) {
        return new SongodaPurchaseList(this).stream().filter(purchase -> purchase.getResource().getId().equals(resourceId)).collect(Collectors.toCollection(SongodaPurchaseList::new));
    }

    public SongodaPurchaseList gifted() {
        return new SongodaPurchaseList(this).stream().filter(p -> p.getCost().isPresent() && p.getCost().get().getValue() == 0F).collect(Collectors.toCollection(SongodaPurchaseList::new));
    }

    public SongodaPurchaseList discord(String discord) {
        return new SongodaPurchaseList(this).stream().filter(purchase -> purchase.getDiscord() != null && purchase.getDiscord().equals(discord)).collect(Collectors.toCollection(SongodaPurchaseList::new));
    }

    public SongodaPurchaseList discord(User user) {
        return new SongodaPurchaseList(this).stream().filter(purchase -> purchase.getDiscord() != null && (purchase.getDiscord().equals(user.getId()) || purchase.getDiscord().equals(user.getName() + "#" + user.getDiscriminator()))).collect(Collectors.toCollection(SongodaPurchaseList::new));
    }

    public SongodaPurchaseList discord(Member member) {
        return discord(member.getUser());
    }
}
