package me.TechsCode.TechDiscordBot.spigotmc.data.lists;

import me.TechsCode.TechDiscordBot.spigotmc.data.ProfileComment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class ProfileCommentList extends ArrayList<ProfileComment> {

    public ProfileCommentList(int initialCapacity) {
        super(initialCapacity);
    }

    public ProfileCommentList() {}

    public ProfileCommentList(Collection<? extends ProfileComment> c) {
        super(c);
    }

    public ProfileCommentList userId(String userId){
        return stream().filter(ProfileComment -> ProfileComment.getUserId().equals(userId)).collect(Collectors.toCollection(ProfileCommentList::new));
    }
}
