package me.TechsCode.SpigotAPI.client.collections;

import me.TechsCode.SpigotAPI.client.objects.Resource;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class ResourceCollection {

    private Resource[] array;

    public ResourceCollection(Resource[] array) {
        this.array = array;
    }

    public Optional<Resource> name(String resourceName){
        return getStream().filter(x -> x.getName().equalsIgnoreCase(resourceName)).findFirst();
    }

    public Optional<Resource> id(String resourceId){
        return getStream().filter(x -> x.getId().equalsIgnoreCase(resourceId)).findFirst();
    }

    public ResourceCollection olderThen(long time){
        return new ResourceCollection(getStream().filter(x -> x.getTime().getUnixTime() > time).toArray(Resource[]::new));
    }

    public ResourceCollection newerThen(long time){
        return new ResourceCollection(getStream().filter(x -> x.getTime().getUnixTime() < time).toArray(Resource[]::new));
    }

    public ResourceCollection premium(){
        return new ResourceCollection(getStream().filter(Resource::isPremium).toArray(Resource[]::new));
    }

    public Stream<Resource> getStream(){
        return Arrays.stream(array);
    }

    public Resource[] get(){
        return array;
    }

    public Optional<Resource> first(){
        return array.length == 0 ? Optional.empty() : Optional.ofNullable(array[0]);
    }

    public int size(){
        return array.length;
    }
}
