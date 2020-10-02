package me.TechsCode.SpigotAPI.client.collections;

import me.TechsCode.SpigotAPI.client.objects.Review;

import java.util.Arrays;
import java.util.stream.Stream;

public class ReviewCollection {

    private Review[] array;

    public ReviewCollection(Review[] array) {
        this.array = array;
    }

    public ReviewCollection resourceName(String resourceName){
        return new ReviewCollection(getStream().filter(x -> x.getResourceName().equalsIgnoreCase(resourceName)).toArray(Review[]::new));
    }

    public ReviewCollection resourceId(String resourceId){
        return new ReviewCollection(getStream().filter(x -> x.getResourceId().equalsIgnoreCase(resourceId)).toArray(Review[]::new));
    }

    public ReviewCollection username(String username){
        return new ReviewCollection(getStream().filter(x -> x.getUsername().equalsIgnoreCase(username)).toArray(Review[]::new));
    }

    public ReviewCollection userId(String userId){
        return new ReviewCollection(getStream().filter(x -> x.getUserId().equalsIgnoreCase(userId)).toArray(Review[]::new));
    }

    public ReviewCollection older(long time){
        return new ReviewCollection(getStream().filter(x -> x.getTime().getUnixTime() > time).toArray(Review[]::new));
    }

    public ReviewCollection newer(long time){
        return new ReviewCollection(getStream().filter(x -> x.getTime().getUnixTime() < time).toArray(Review[]::new));
    }

    public Stream<Review> getStream(){
        return Arrays.stream(array);
    }

    public Review[] get(){
        return array;
    }

    public Review first(){
        return array[0];
    }

    public int size(){
        return array.length;
    }
}
