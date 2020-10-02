package me.TechsCode.SpigotAPI.client.collections;

import me.TechsCode.SpigotAPI.client.objects.Purchase;
import me.TechsCode.SpigotAPI.client.objects.Resource;

import java.util.Arrays;
import java.util.stream.Stream;

public class PurchaseCollection {

    private Purchase[] array;

    public PurchaseCollection(Purchase[] array) {
        this.array = array;
    }

    public PurchaseCollection resource(Resource resource){
        return resourceId(resource.getId());
    }

    public PurchaseCollection resourceName(String resourceName){
        return new PurchaseCollection(getStream().filter(x -> x.getResourceName().equalsIgnoreCase(resourceName)).toArray(Purchase[]::new));
    }

    public PurchaseCollection resourceId(String resourceId){
        return new PurchaseCollection(getStream().filter(x -> x.getResourceId().equalsIgnoreCase(resourceId)).toArray(Purchase[]::new));
    }

    public PurchaseCollection username(String username){
        return new PurchaseCollection(getStream().filter(x -> x.getUsername().equalsIgnoreCase(username)).toArray(Purchase[]::new));
    }

    public PurchaseCollection userId(String userId){
        return new PurchaseCollection(getStream().filter(x -> x.getUserId().equalsIgnoreCase(userId)).toArray(Purchase[]::new));
    }

    public PurchaseCollection older(long time){
        return new PurchaseCollection(getStream().filter(x -> x.getTime().getUnixTime() > time).toArray(Purchase[]::new));
    }

    public PurchaseCollection newer(long time){
        return new PurchaseCollection(getStream().filter(x -> x.getTime().getUnixTime() < time).toArray(Purchase[]::new));
    }

    public Stream<Purchase> getStream(){
        return Arrays.stream(array);
    }

    public Purchase[] get(){
        return array;
    }

    public Purchase first(){
        return array[0];
    }

    public int size(){
        return array.length;
    }
}
