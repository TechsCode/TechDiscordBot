package me.TechsCode.TechDiscordBot.modules;

import com.techeazy.spigotapi.data.collections.ResourceCollection;
import com.techeazy.spigotapi.data.objects.Resource;
import me.TechsCode.TechDiscordBot.Module;
import me.TechsCode.TechDiscordBot.Query;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.songoda.SongodaPurchase;
import me.TechsCode.TechDiscordBot.storage.Verification;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class RoleAssigner extends Module {

    private final DefinedQuery<Role> VERIFICATION_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Verified");
        }
    };

    private final DefinedQuery<Role> SONGODA_VERIFICATION_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Songoda-Verified");
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
            String[] resourceNames = bot.getSpigotAPI().getResources().premium().getStream().map(Resource::getResourceName).toArray(String[]::new);
            return bot.getRoles(resourceNames);
        }
    };

    public RoleAssigner(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        new Thread(() -> {
            while (true) {
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


    public void loop() {
        if(!bot.getSpigotAPI().isAvailable()) {
            return;
        }

        if(!bot.getSongodaAPIClient().isLoaded()) {
            return;
        }

        Role verificationRole = VERIFICATION_ROLE.query().first();
        Role songodaVerificationRole = SONGODA_VERIFICATION_ROLE.query().first();
        Role reviewSquad = REVIEW_SQUAD_ROLE.query().first();

        Set<Verification> verifications = bot.getStorage().retrieveVerifications();

        Set<Role> roles = new HashSet<>();
        roles.add(verificationRole);
        roles.add(songodaVerificationRole);
        roles.add(reviewSquad);
        roles.addAll(RESOURCE_ROLES.query().all());

        ResourceCollection resources = bot.getSpigotAPI().getResources().premium();

        for(Member all : bot.getGuild().getMembers()) {
            Verification verification = verifications.stream()
                    .filter(v -> v.getDiscordId().equals(all.getUser().getId()))
                    .findAny().orElse(null);

            Set<Role> rolesToKeep = new HashSet<>();

            if(verification != null) {
                rolesToKeep.add(verificationRole);

                int purchases = 0;
                int reviews = 0;

                for(Resource resource : resources.get()) {
                    Role role = bot.getRoles(resource.getResourceName()).first();

                    boolean purchased = resource.getPurchases().userId(verification.getUserId()).size() > 0;
                    boolean reviewed = resource.getReviews().userId(verification.getUserId()).size() > 0;

                    if(purchased) purchases++;
                    if(reviewed) reviews++;

                    if(purchased) {
                        rolesToKeep.add(role);
                    }
                }

                if(purchases != 0 && purchases == reviews) {
                    rolesToKeep.add(reviewSquad);
                }
            }

            for(SongodaPurchase songodaPurchase : bot.getSongodaAPIClient().getPurchases()) {
                if(songodaPurchase.getDiscord() != null && songodaPurchase.getDiscord().equalsIgnoreCase(all.getUser().getName() + "#" + all.getUser().getDiscriminator())) {
                    rolesToKeep.add(songodaVerificationRole);
                    rolesToKeep.add(bot.getRoles(songodaPurchase.getName()).first());
                }
            }

            Set<Role> rolesToRemove = roles.stream()
                    .filter(role -> !rolesToKeep.contains(role))
                    .filter(role -> all.getRoles().contains(role))
                    .collect(Collectors.toSet());

            Set<Role> rolesToAdd = rolesToKeep.stream()
                    .filter(role -> !all.getRoles().contains(role))
                    .collect(Collectors.toSet());

            bot.getGuild().getController().addRolesToMember(all, rolesToAdd).complete();
            bot.getGuild().getController().removeRolesFromMember(all, rolesToRemove).complete();

            rolesToRemove.forEach(x -> bot.log("[Roles] Removed the Role " + x.getName() + " from Member " + all.getEffectiveName()));
            rolesToAdd.forEach(x -> bot.log("[Roles] Added the Role " + x.getName() + " to Member " + all.getEffectiveName()));
        }
    }
}