package me.TechsCode.TechDiscordBot.spigotmc.data.lists;

import me.TechsCode.TechDiscordBot.spigotmc.data.Review;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class ReviewsList extends ArrayList<Review> {

    public ReviewsList(int initialCapacity) {
        super(initialCapacity);
    }

    public ReviewsList() {}

    public ReviewsList(Collection<? extends Review> c) {
        super(c);
    }

    public ReviewsList userId(String userId){
        return stream().filter(review -> review.getUser().getUserId().equals(userId)).collect(Collectors.toCollection(ReviewsList::new));
    }

    public ReviewsList username(String username){
        return stream().filter(review -> review.getUser().getUsername().equalsIgnoreCase(username)).collect(Collectors.toCollection(ReviewsList::new));
    }

    public ReviewsList resource(String resourceId){
        return stream().filter(review -> review.getResource().getId().equals(resourceId)).collect(Collectors.toCollection(ReviewsList::new));
    }
}