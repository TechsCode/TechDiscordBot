package me.TechsCode.SpigotAPI.client;

import me.TechsCode.SpigotAPI.client.collections.PurchaseCollection;
import me.TechsCode.SpigotAPI.client.collections.ResourceCollection;
import me.TechsCode.SpigotAPI.client.collections.ReviewCollection;
import me.TechsCode.SpigotAPI.client.collections.UpdateCollection;
import me.TechsCode.SpigotAPI.client.objects.*;
import me.TechsCode.SpigotAPI.logging.ConsoleColor;
import me.TechsCode.SpigotAPI.logging.Logger;

import java.util.concurrent.TimeUnit;

public class SpigotAPIClient extends Thread {

    private static final long REFRESH_DELAY = TimeUnit.MINUTES.toMillis(1);
    private long timeout = 0;

    private APIScanner scanner;
    private Data latest;

    private APIScanner.APIStatus cacheStatus;

    public SpigotAPIClient(String url, String token){
        Logger.log("Connecting to SpigotAPI instance on " + url);
        scanner = new APIScanner(this, url, token);

        cacheStatus = APIScanner.APIStatus.WAITING;

        retrieveData();
        start();
    }

    private void retrieveData() {
        Data latest2 = scanner.retrieveData();

        if(latest2 == null) {
            Logger.log(ConsoleColor.RED + " Could not retrieve new data.. Waiting 5 minutes.. (NEW: Saving old data)");
            timeout = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
            if(latest == null) cacheStatus = scanner.getStatus();
        } else {
            latest = latest2;
            cacheStatus = APIScanner.APIStatus.OK;
        }
    }

    @Override
    public void run() {
        while (true) {
            if(timeout != 0 && timeout > System.currentTimeMillis()) continue;

            if(latest == null || (System.currentTimeMillis() - latest.getRetrievedTime()) > REFRESH_DELAY) {
                retrieveData();
            }
        }
    }

    public ResourceCollection getResources() {
        return new ResourceCollection(isAvailable() ? latest.getResources() : new Resource[0]);
    }

    public PurchaseCollection getPurchases() {
        return new PurchaseCollection(isAvailable() ? latest.getPurchases() : new Purchase[0]);
    }

    public ReviewCollection getReviews() {
        return new ReviewCollection(isAvailable() ? latest.getReviews() : new Review[0]);
    }

    public UpdateCollection getUpdates() {
        return new UpdateCollection(isAvailable() ? latest.getUpdates() : new Update[0]);
    }

    public boolean isAvailable() {
        if(latest == null) return false;
        return latest.getPurchases() != null;
    }

    public APIScanner.APIStatus getStatus() {
        return scanner.getStatus();
    }

    public APIScanner.APIStatus getCacheStatus() {
        return cacheStatus;
    }
}