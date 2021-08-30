package me.TechsCode.TechDiscordBot.util;

import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.PteroApplication;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;

public class PterodactylAPI {
	private PteroClient client;
	private PteroApplication api;

	public void setup(String url, String clientToken, String appToken){
		if(!clientToken.isEmpty()){
			client = PteroBuilder.createClient(url, clientToken);
		}
		if(!appToken.isEmpty()){
			api = PteroBuilder.createApplication(url, appToken);
		}
	}

	public boolean clientConfigured(){
		return client != null;
	}

	public boolean apiConfigured(){
		return api != null;
	}

	public void startServer(String serverId){
		if(!clientConfigured()) return;
		client.retrieveServerByIdentifier(serverId).flatMap(ClientServer::start).executeAsync();
	}

	public void stopServer(String serverId){
		if(!clientConfigured()) return;
		client.retrieveServerByIdentifier(serverId).flatMap(ClientServer::stop).executeAsync();
	}

	public void restartServer(String serverId){
		if(!clientConfigured()) return;
		client.retrieveServerByIdentifier(serverId).flatMap(ClientServer::restart).executeAsync();
	}

	public void killServer(String serverId) {
		if(!clientConfigured()) return;
		client.retrieveServerByIdentifier(serverId).flatMap(ClientServer::kill).executeAsync();
	}

}
