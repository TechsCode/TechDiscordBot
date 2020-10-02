package me.TechsCode.SpigotAPI.server;

import me.TechsCode.SpigotAPI.logging.ConsoleColor;
import me.TechsCode.SpigotAPI.logging.Logger;
import me.TechsCode.SpigotAPI.server.data.Data;
import me.TechsCode.SpigotAPI.server.data.Entry;
import me.TechsCode.SpigotAPI.server.spigot.Parser;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataCollectingThread extends Thread {

    private static final long REFRESH_DELAY = TimeUnit.MINUTES.toMillis(15);

    private Parser spigotMC;
    private Data latest;

    public DataCollectingThread(Parser spigotMC) {
        this.spigotMC = spigotMC;
        this.latest = null;

        start();
    }

    @Override
    public void run() {
        while (true){
            if(latest == null || (System.currentTimeMillis() - latest.getRecordTime()) > REFRESH_DELAY){
                long now = System.currentTimeMillis();

                Logger.log("Fetching new data from SpigotMC..");

                List<Entry> resources = spigotMC.retrieveResources();
                Logger.log("[1/4] Collected "+resources.size()+" Resources");

                List<Entry> updates = spigotMC.retrieveUpdates(resources);
                Logger.log("[2/4] Collected "+updates.size()+" Updates");

                List<Entry> reviews = spigotMC.retrieveReviews(resources);
                Logger.log("[3/4] Collected "+reviews.size()+" Reviews");

                List<Entry> purchases = spigotMC.retrievePurchases(resources);
                Logger.log("[4/4] Collected "+purchases.size()+" Purchases");

                long delay = System.currentTimeMillis() - now;
                Logger.log(ConsoleColor.GREEN+"Completed Refreshing Cycle in "+Math.round(TimeUnit.MILLISECONDS.toMinutes(delay))+" minutes!");
                Logger.log("");

                Data data = new Data(System.currentTimeMillis());
                data.set("resources", resources);
                data.set("updates", updates);
                data.set("reviews", reviews);
                data.set("purchases", purchases);

                latest = data;
            }
        }
    }

    public Data getData(){
        return latest;
    }
}
