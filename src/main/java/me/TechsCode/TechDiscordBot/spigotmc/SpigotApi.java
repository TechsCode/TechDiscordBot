package me.TechsCode.TechDiscordBot.spigotmc;

import me.TechsCode.TechDiscordBot.spigotmc.data.APIWebStatus;
import me.TechsCode.TechDiscordBot.spigotmc.data.lists.*;

import java.util.concurrent.TimeUnit;

public class SpigotApi {
    private static SpigotAPIManager spigotAPIClient;
    private static long lastBotFetch;

    ResourcesList spigotResourcesList;
    ReviewsList spigotReviewsList;
    UpdatesList spigotUpdateList;
    PurchasesList spigotPurchasesList;

    ResourcesList marketResourcesList;
    ReviewsList marketReviewsList;
    UpdatesList marketUpdateList;
    PurchasesList marketPurchasesList;

    public SpigotApi(String url, String token) {
        spigotAPIClient = new SpigotAPIManager(url, token);

        new Thread(() -> {
            while (true) {
                fetchNewData();

                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(5));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public SpigotAPIManager getSpigotAPIManager() {
        return spigotAPIClient;
    }

    public void fetchNewData(){
        spigotResourcesList = spigotAPIClient.fetchSpigotResource();
        spigotReviewsList = spigotAPIClient.fetchSpigotReviews();
        spigotUpdateList = spigotAPIClient.fetchSpigotUpdates();
        spigotPurchasesList = spigotAPIClient.fetchSpigotPurchases();

        marketResourcesList = spigotAPIClient.fetchMarketResource();
        marketReviewsList = spigotAPIClient.fetchMarketReviews();
        marketUpdateList = spigotAPIClient.fetchMarketUpdates();
        marketPurchasesList = spigotAPIClient.fetchMarketPurchases();

        lastBotFetch = System.currentTimeMillis();
    }

    public long getLastBotFetch() {
        return lastBotFetch;
    }

    public APIWebStatus getStatus(){
        return spigotAPIClient.getStatus();
    }

    public ProfileCommentList getSpigotProfileComments(String userid, boolean showAll){
        return spigotAPIClient.fetchSpigotProfileComments(userid, showAll);
    }

    public ResourcesList getSpigotResources() {
        return spigotResourcesList;
    }

    public ReviewsList getSpigotReviews() {
        return spigotReviewsList;
    }

    public UpdatesList getSpigotUpdates() {
        return spigotUpdateList;
    }

    public PurchasesList getSpigotPurchases() {
        return spigotPurchasesList;
    }

    public ProfileCommentList getMarketProfileComments(String userid, boolean showAll){
        return spigotAPIClient.fetchMarketProfileComments(userid, showAll);
    }

    public ResourcesList getMarketResources() {
        return marketResourcesList;
    }

    public ReviewsList getMarketReviews() {
        return marketReviewsList;
    }

    public UpdatesList getMarketUpdates() {
        return marketUpdateList;
    }

    public PurchasesList getMarketPurchases() {
        return marketPurchasesList;
    }
}
