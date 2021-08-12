package me.TechsCode.TechDiscordBot.spigotmc.data.lists;

import me.TechsCode.TechDiscordBot.spigotmc.data.Purchase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class PurchasesList extends ArrayList<Purchase> {

    public PurchasesList(int initialCapacity) {
        super(initialCapacity);
    }

    public PurchasesList() {}

    public PurchasesList(Collection<? extends Purchase> c) {
        super(c);
    }

    public PurchasesList userId(String userId){
        return stream().filter(purchase -> purchase.getUser().getUserId().equals(userId)).collect(Collectors.toCollection(PurchasesList::new));
    }

    public PurchasesList username(String username){
        return stream().filter(purchase -> purchase.getUser().getUsername().equalsIgnoreCase(username)).collect(Collectors.toCollection(PurchasesList::new));
    }

    public PurchasesList resource(String resourceId){
        return stream().filter(purchase -> purchase.getResource().getId().equals(resourceId)).collect(Collectors.toCollection(PurchasesList::new));
    }

    public PurchasesList gifted(){
        return stream().filter(Purchase::isGifted).collect(Collectors.toCollection(PurchasesList::new));
    }
}
