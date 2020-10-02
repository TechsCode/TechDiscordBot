package me.TechsCode.SpigotAPI.client.collections;

import me.TechsCode.SpigotAPI.client.objects.Update;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class UpdateCollection {

    private Update[] array;

    public UpdateCollection(Update[] array) {
        this.array = array;
    }

    public UpdateCollection resourceName(String resourceName){
        return new UpdateCollection(getStream().filter(x -> x.getResourceName().equalsIgnoreCase(resourceName)).toArray(Update[]::new));
    }

    public UpdateCollection resourceId(String resourceId){
        return new UpdateCollection(getStream().filter(x -> x.getResourceId().equalsIgnoreCase(resourceId)).toArray(Update[]::new));
    }

    public UpdateCollection older(long time){
        return new UpdateCollection(getStream().filter(x -> x.getTime().getUnixTime() > time).toArray(Update[]::new));
    }

    public UpdateCollection newer(long time){
        return new UpdateCollection(getStream().filter(x -> x.getTime().getUnixTime() < time).toArray(Update[]::new));
    }

    public Stream<Update> getStream(){
        return Arrays.stream(array);
    }

    public Update[] get(){
        return array;
    }

    public Optional<Update> first(){
        return array.length == 0 ? Optional.empty() : Optional.ofNullable(array[0]);
    }

    public int size(){
        return array.length;
    }
}
