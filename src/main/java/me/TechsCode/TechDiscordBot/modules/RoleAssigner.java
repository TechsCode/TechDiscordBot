package me.TechsCode.TechDiscordBot.modules;

import me.TechsCode.TechDiscordBot.Module;
import me.TechsCode.TechDiscordBot.Query;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.storage.Verification;
import me.TechsCode.TechsCodeAPICli.objects.Resource;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class RoleAssigner extends Module {

    private final DefinedQuery<Role> VERIFICATION_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Verified");
        }
    };

    private final DefinedQuery<Role> REVIEW_SQUAD_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Review Squad");
        }
    };

    private final DefinedQuery<Role> RESOURCE_ROLES = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            String[] resourceNames = bot.getTechsCodeAPI().getResources().premium().getStream().map(Resource::getResourceName).toArray(String[]::new);

            return bot.getRoles(resourceNames);
        }
    };

    public RoleAssigner(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
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
        return new Requirement[]{
                new Requirement(VERIFICATION_ROLE, 1, "Missing 'Verified' Role"),
                new Requirement(REVIEW_SQUAD_ROLE, 1, "Missing 'Review Squad' Role"),
                new Requirement(RESOURCE_ROLES, 1, "Missing Resource Roles (Use Resource Names as Role Names)")
        };
    }


    public void loop(){
        if(!bot.getTechsCodeAPI().isAvailable()){
            return;
        }

        Role verificationRole = VERIFICATION_ROLE.query().first();
        Role reviewSquad = REVIEW_SQUAD_ROLE.query().first();

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
                    Role role = bot.getRoles(resource.getResourceName()).first();

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
                    Role role = bot.getRoles(resource.getResourceName()).first();

                    rolesToRemove.add(role);
                }
            }

            bot.getGuild().getController().addRolesToMember(all, rolesToAdd).complete();
            bot.getGuild().getController().removeRolesFromMember(all, rolesToRemove).complete();
        }
    }
}
