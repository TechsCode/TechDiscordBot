package me.TechsCode.TechDiscordBot.modules;

import me.TechsCode.TechDiscordBot.Module;
import me.TechsCode.TechDiscordBot.Requirement;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.requirements.RoleRequirement;
import me.TechsCode.TechDiscordBot.storage.Verification;
import me.TechsCode.TechsCodeAPICli.TechsCodeAPIClient;
import me.TechsCode.TechsCodeAPICli.objects.Resource;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class RoleAssigner extends Module {

    private Role verificationRole, reviewSquad;

    public RoleAssigner(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        verificationRole = bot.getRole("Verified");
        reviewSquad = bot.getRole("Review Squad");

        new Thread(() -> {
            while (true){
                loop();

                try {
                    sleep(TimeUnit.SECONDS.toMillis(3));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getName() {
        return "Role Assigner";
    }

    @Override
    public Requirement[] getRequirements() {
        Set<Requirement> requirements = new HashSet<>();
        requirements.add(new RoleRequirement("Verified"));
        requirements.add(new RoleRequirement("Review Squad"));

        bot.getTechsCodeAPI().getResources().premium().getStream().filter(resource -> requirements.add(new RoleRequirement(resource.getResourceName())));

        return requirements.stream().toArray(Requirement[]::new);
    }


    public void loop(){
        Set<Verification> verifications = bot.getStorage().retrieveVerifications();

        for(Member all : bot.getGuild().getMembers()){
            Verification verification = verifications.stream()
                    .filter(v -> v.getDiscordId().equals(all.getUser().getId()))
                    .findAny().orElse(null);


            List<Role> rolesToAdd = new ArrayList<>();
            List<Role> rolesToRemove = new ArrayList<>();

            if(verification != null){
                String userId = verification.getUserId();

                rolesToAdd.add(verificationRole);

                boolean allReviewed = true;
                int ownedPlugins = 0;

                for(Resource resource : bot.getTechsCodeAPI().getResources().premium().get()){
                    Role role = bot.getRole(resource.getResourceName());

                    boolean purchased = resource.getPurchases().userId(userId).size() > 0;
                    boolean reviewed = resource.getReviews().userId(userId).size() > 0;

                    if(purchased){
                        rolesToAdd.add(role);
                        ownedPlugins++;
                    } else {
                        rolesToRemove.add(role);
                    }

                    if(allReviewed){
                        allReviewed = purchased == reviewed;
                    }
                }

                if(allReviewed && ownedPlugins != 0){
                    rolesToAdd.add(reviewSquad);
                } else {
                    rolesToRemove.add(reviewSquad);
                }
            } else {
                rolesToRemove.add(verificationRole);
                rolesToRemove.add(reviewSquad);

                for(Resource resource : bot.getTechsCodeAPI().getResources().premium().get()) {
                    Role role = bot.getRole(resource.getResourceName());

                    rolesToRemove.add(role);
                }
            }

            bot.getGuild().getController().addRolesToMember(all, rolesToAdd).complete();
            bot.getGuild().getController().removeRolesFromMember(all, rolesToRemove).complete();
        }
    }
}
