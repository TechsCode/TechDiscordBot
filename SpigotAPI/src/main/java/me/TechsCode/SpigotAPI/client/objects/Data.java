package me.TechsCode.SpigotAPI.client.objects;

public class Data {

    private long retrievedTime;

    private Resource[] resources;
    private Purchase[] purchases;
    private Review[] reviews;
    private Update[] updates;

    public Data(long retrievedTime, Resource[] resources, Purchase[] purchases, Review[] reviews, Update[] updates) {
        this.retrievedTime = retrievedTime;
        this.resources = resources;
        this.purchases = purchases;
        this.reviews = reviews;
        this.updates = updates;
    }

    public long getRetrievedTime() {
        return retrievedTime;
    }

    public Resource[] getResources() {
        return resources;
    }

    public Purchase[] getPurchases() {
        return purchases;
    }

    public Review[] getReviews() {
        return reviews;
    }

    public Update[] getUpdates() {
        return updates;
    }

}
