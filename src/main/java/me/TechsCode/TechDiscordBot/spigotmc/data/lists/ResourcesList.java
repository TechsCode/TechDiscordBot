package me.TechsCode.TechDiscordBot.spigotmc.data.lists;

import me.TechsCode.TechDiscordBot.spigotmc.data.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResourcesList extends ArrayList<Resource> {

    public ResourcesList(int initialCapacity) {
        super(initialCapacity);
    }

    public ResourcesList() {}

    public ResourcesList(Collection<? extends Resource> c) {
        super(c);
    }

    public Optional<Resource> id(String id){
        return stream().filter(resource -> resource.getId().equals(id)).findFirst();
    }

    public Optional<Resource> name(String name){
        return stream().filter(resource -> resource.getName().equalsIgnoreCase(name)).findFirst();
    }

    public ResourcesList free(){
        return stream().filter(Resource::isFree).collect(Collectors.toCollection(ResourcesList::new));
    }

    public ResourcesList premium(){
        return stream().filter(Resource::isPremium).collect(Collectors.toCollection(ResourcesList::new));
    }
}