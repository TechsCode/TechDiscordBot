package me.TechsCode.SpigotAPI.client;

import me.TechsCode.SpigotAPI.client.objects.Data;

import java.util.concurrent.TimeUnit;

public class DataManager extends Thread {

    private static final long REFRESH_DELAY = TimeUnit.MINUTES.toMillis(15);
    private long timeout = 0;

    private APIScanner apiEndpoint;
    private Data latest;

    public DataManager(APIScanner apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
        this.latest = null;

        start();
    }

    public Data getData(){
        return latest;
    }
}


