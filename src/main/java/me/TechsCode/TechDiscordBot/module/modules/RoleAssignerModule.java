package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.SpigotAPI.data.Resource;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.songoda.SongodaPurchase;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.Plugin;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class RoleAssignerModule extends Module {

    private final DefinedQuery<Role> VERIFICATION_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Verified");
        }
    };

    private final DefinedQuery<Role> SONGODA_VERIFICATION_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Songoda Verified");
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
            String[] resourceNames = Arrays.stream(Plugin.values()).map(Plugin::getRoleName).toArray(String[]::new);
            return bot.getRoles(resourceNames);
        }
    };

    private final DefinedQuery<Role> SUB_VERIFIED_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Sub Verified");
        }
    };

    public RoleAssignerModule(TechDiscordBot bot) {
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
    public void onDisable() {}

    @Override
    public String getName() {
        return "Role Assigner";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[] {
                new Requirement(VERIFICATION_ROLE, 1, "Missing 'Verified' Role"),
                new Requirement(SONGODA_VERIFICATION_ROLE, 1, "Missing 'Songoda-Verified' Role"),
                new Requirement(REVIEW_SQUAD_ROLE, 1, "Missing 'Review Squad' Role"),
                new Requirement(RESOURCE_ROLES, 1, "Missing Resource Roles")
        };
    }

    public void loop() {
        if(!TechDiscordBot.getBot().getStatus().isUsable() || TechDiscordBot.getSpigotAPI().getPurchases().size() < 9000 || !TechDiscordBot.getSongodaAPI().isLoaded())
            return;

        Role verificationRole = VERIFICATION_ROLE.query().first();
        Role songodaVerificationRole = SONGODA_VERIFICATION_ROLE.query().first();
        Role reviewSquad = REVIEW_SQUAD_ROLE.query().first();

        Set<Verification> verifications = TechDiscordBot.getStorage().retrieveVerifications();
        Set<Role> possibleRoles = new HashSet<>();

        possibleRoles.add(verificationRole);
        possibleRoles.add(songodaVerificationRole);
        possibleRoles.add(reviewSquad);
        possibleRoles.addAll(RESOURCE_ROLES.query().all());

        Resource[] resources = TechDiscordBot.getSpigotAPI().getResources().stream().filter(Resource::isPremium).toArray(Resource[]::new);

        HashMap<String, List<String>> resourcePurchaserIds = new HashMap<>();
        HashMap<String, List<String>> resourceReviewerIds = new HashMap<>();

        Arrays.stream(resources).forEach(resource -> {
            resourcePurchaserIds.put(resource.getId(), resource.getPurchases().stream().map(p -> p.getUser().getUserId()).collect(Collectors.toList()));
            resourceReviewerIds.put(resource.getId(), resource.getReviews().stream().map(r -> r.getUser().getUserId()).collect(Collectors.toList()));
        });

        for(Member all : TechDiscordBot.getGuild().getMembers()) {
            Verification verification = verifications.stream().filter(v -> v.getDiscordId().equals(all.getUser().getId())).findAny().orElse(null);
            Set<Role> rolesToKeep = new HashSet<>();

            if(verification != null) {
                rolesToKeep.add(verificationRole);
                int purchases = 0, reviews = 0;

                for(Resource resource : resources) {
                    Role role = bot.getRoles(resource.getName()).first();
                    boolean purchased = resourcePurchaserIds.get(resource.getId()).contains(verification.getUserId());
                    boolean reviewed = resourceReviewerIds.get(resource.getId()).contains(verification.getUserId());

                    if(purchased) purchases++;
                    if(reviewed) reviews++;
                    if(purchased) rolesToKeep.add(role);
                }

                if(purchases != 0 && purchases == reviews) rolesToKeep.add(reviewSquad);
            }

            for (SongodaPurchase songodaPurchase : TechDiscordBot.getSongodaAPI().getPurchases().stream().filter(Objects::nonNull).collect(Collectors.toList())) {
                if (songodaPurchase.getDiscord() != null && songodaPurchase.getDiscord().equalsIgnoreCase(all.getUser().getName() + "#" + all.getUser().getDiscriminator())) {
                    rolesToKeep.add(songodaVerificationRole);

                    if(songodaPurchase.getResource() == null) {
                        System.out.println(songodaPurchase.getState().toString());
                        System.out.println(songodaPurchase.getDiscord());
                    } else {
                        rolesToKeep.add(bot.getRoles(songodaPurchase.getResource().getName()).first());
                    }

                }
            }

            /*if(TechDiscordBot.getStorage().isSubVerifiedUser(all.getId())) {
                if(TechDiscordBot.getStorage().getVerifiedIdFromSubVerifiedId(all.getId()) != null && TechDiscordBot.getGuild().getMemberById(TechDiscordBot.getStorage().getVerifiedIdFromSubVerifiedId(all.getId())) != null) {
                    TechDiscordBot.getGuild().addRoleToMember(all, SUB_VERIFIED_ROLE.query().first()).queue();
                } else {
                    TechDiscordBot.getGuild().removeRoleFromMember(all, SUB_VERIFIED_ROLE.query().first()).queue();
                }
            } else {
                if(all.getRoles().contains(SUB_VERIFIED_ROLE.query().first())) {
                    TechDiscordBot.getGuild().removeRoleFromMember(all, SUB_VERIFIED_ROLE.query().first()).queue();
                }
            }*/

            Set<Role> rolesToRemove = new HashSet<>();

            if(all.getRoles().stream().map(Role::getName).noneMatch(r -> r.equals("Keep Roles")))
                rolesToRemove = possibleRoles.stream()
                    .filter(role -> !rolesToKeep.contains(role))
                    .filter(role -> all.getRoles().contains(role))
                    .collect(Collectors.toSet());

            Set<Role> rolesToAdd = rolesToKeep.stream()
                    .filter(role -> !all.getRoles().contains(role))
                    .collect(Collectors.toSet());

            rolesToAdd.forEach(r -> {
                TechDiscordBot.getGuild().addRoleToMember(all, r).complete();
                TechDiscordBot.log("Role » Added " + r.getName() + " (" + all.getEffectiveName() + ")");
            });

            rolesToRemove.forEach(r -> {
                TechDiscordBot.getGuild().removeRoleFromMember(all, r).complete();
                TechDiscordBot.log("Role » Removed " + r.getName() + " (" + all.getEffectiveName() + ")");
            });
        }
    }
}