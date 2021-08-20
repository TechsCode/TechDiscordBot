package me.TechsCode.TechDiscordBot.spigotmc.data.lists;

import me.TechsCode.TechDiscordBot.spigotmc.data.Update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class UpdatesList extends ArrayList<Update> {

    public UpdatesList(int initialCapacity) {
        super(initialCapacity);
    }

    public UpdatesList() {}

    public UpdatesList(Collection<? extends Update> c) {
        super(c);
    }

    public UpdatesList resource(String resourceId){
        return stream().filter(update -> update.getResource().getId().equals(resourceId)).collect(Collectors.toCollection(UpdatesList::new));
    }
}