package me.TechsCode.TechDiscordBot.util;

import com.stanjg.ptero4j.PteroAdminAPI;
import com.stanjg.ptero4j.PteroUserAPI;
import com.stanjg.ptero4j.entities.objects.server.MinecraftVersion;
import com.stanjg.ptero4j.entities.objects.server.PowerAction;
import com.stanjg.ptero4j.entities.objects.server.PowerState;
import com.stanjg.ptero4j.entities.panel.admin.Server;
import com.stanjg.ptero4j.entities.panel.admin.User;
import com.stanjg.ptero4j.entities.panel.user.UserServer;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.UserPass;
import net.dv8tion.jda.api.entities.Member;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PterodactylUtil {

    public static UserPass createUser(Member member, String username, int passLength) {
        try {
            String password = new PasswordGenerator.PasswordGeneratorBuilder()
                    .useDigits(true)
                    .usePunctuation(false)
                    .useLower(true)
                    .useUpper(true)
                    .build()
                    .generate(passLength);
            UserPass user = UserPass.of(getAdminAPI().getUsersController().createNew()
                    .setIsAdmin(false)
                    .setEmail(username + "@gmail.com")
                    .setFirstName("John")
                    .setLastName("Doe")
                    .setLanguage("en")
                    .setUsername(username)
                    .setPassword(password)
                    .execute(), password);
            TechDiscordBot.getStorage().createServerUser(user.getUser().getId(), member.getId());
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static UserPass createUser(Member member, int passLength) {
        return createUser(member, getUsername(member) + "-" + UUID.randomUUID().toString().split("-")[0], passLength);
    }

    public static UserPass updateUserPass(Member member, User user, int passLength) {
        String username = user.getUsername();
        UserPass newUser = createUser(member, passLength);

        if(user.getServers().size() > 0) user.getServers().get(0).editDetails().setOwner(newUser.getUser()).execute();
        user.delete();
        newUser.getUser().edit().setUsername(username).execute();
        TechDiscordBot.getStorage().retrieveServerUserWithPteroId(user.getId()).delete();
        return newUser;
    }

    public static Server updateServerOwner(Server server, User newOwner) {
        return server.getOwner().getServers().get(0).editDetails().setOwner(newOwner).execute();
    }

    private static String getUsername(Member member) {
        String name = member.getEffectiveName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        if(name.isEmpty()) return member.getId();
        return name;
    }

    public static Server createServer(User user, MinecraftVersion version) {
        if(user.getServers().size() > 0) return null;
        int port = ThreadLocalRandom.current().nextInt(25000, 25599);
        return getAdminAPI().getServersController().createNew()
                .setName(user.getUsername().split("-")[0] + "-" + UUID.randomUUID().toString().split("-")[0])
                .setServerVersion(version)
                .setEggId(3) //Paper Spigot
                .setDockerImage("quay.io/pterodactyl/core:java")
                .setDescription("Server Created by TechDiscordBot. Port: " + port)
                .setUserId(user.getId())
                .setStartOnCompletion(true)
                .setStartupCommand("java -Xms128M -Xmx1024M -Dterminal.jline=false -Dterminal.ansi=true -jar server.jar")
                .setLimits(1024, 0, 500, 500, 0)
                .setFeatureLimits(1, 1)
                .setDeployLocations(1)
                .setDedicatedIp(false)
                .setPortRange(String.valueOf(port))
                .execute();
    }

    public static boolean deleteServer(Server server, boolean andUser) {
        if(andUser) {
            User user = server.getOwner();
            TechDiscordBot.getStorage().retrieveServerUserWithPteroId(user.getId()).delete();
            return server.delete() && user.delete();
        } else {
            return server.delete();
        }
    }

    public static boolean sendPowerAction(String id, PowerAction pAction) {
        return getUserAPI().getServersController().getServer(id).sendPowerAction(pAction);
    }

    public static boolean sendCommand(String id, String command) {
        return getUserAPI().getServersController().getServer(id).sendCommand(command);
    }

    public int getOnlineServersAmount() {
        return (int)getAdminAPI().getServersController().getAllServers().stream().filter(server -> getUserAPI().getServersController().getPowerState(server.getLongId()) == PowerState.ON).count();
    }

    public UserServer getServer(String id) {
        return getUserAPI().getServersController().getServer(id);
    }

    public User getUser(int id) {
        return getAdminAPI().getUsersController().getUser(id);
    }

    private static PteroAdminAPI getAdminAPI() {
        return TechDiscordBot.getPteroAdminAPI();
    }

    private static PteroUserAPI getUserAPI() {
        return TechDiscordBot.getPteroUserAPI();
    }
}
