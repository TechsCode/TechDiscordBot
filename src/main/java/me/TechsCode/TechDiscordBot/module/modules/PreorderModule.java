//package me.TechsCode.TechDiscordBot.module.modules;
//
//import me.TechsCode.TechDiscordBot.TechDiscordBot;
//import me.TechsCode.TechDiscordBot.module.Module;
//import me.TechsCode.TechDiscordBot.mysql.storage.Preorder;
//import me.TechsCode.TechDiscordBot.objects.Requirement;
//import net.dv8tion.jda.api.entities.Member;
//import net.dv8tion.jda.api.entities.Role;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//public class PreorderModule extends Module {
//
//    private List<String> roles = new ArrayList<>();
//
//    public PreorderModule(TechDiscordBot bot) {
//        super(bot);
//    }
//
//    @Override
//    public void onEnable() {
//        new Thread(() -> {
//            while (true) {
//                updateRoles();
//
//                try {
//                    Thread.sleep(TimeUnit.MINUTES.toMillis(5)); //Wait every 5 minutes
//                } catch (Exception ignored) {}
//            }
//        }).start();
//
//        new Thread(() -> {
//            while (true) {
//                updateRolesMembers();
//
//                try {
//                    Thread.sleep(TimeUnit.SECONDS.toMillis(10)); //Wait every 10 seconds
//                } catch (Exception ignored) {}
//            }
//        }).start();
//    }
//
//    public void updateRoles() {
//        roles = TechDiscordBot.getJDA().getRoles().stream().filter(role -> role.getName().endsWith(" Preorder")).map(Role::getName).collect(Collectors.toList());
//    }
//
//    public void updateRolesMembers() {
//        if(roles.size() == 0) return;
//
//        for(String roleS : roles) {
//            if(!bot.getRoles(roleS).hasAny()) continue;
//            String pluginName = roleS.replace(" Preorder", "");
//
//            List<Preorder> preorders = new ArrayList<>(TechDiscordBot.getStorage().getPreorders(pluginName, false));
//
//            for(Preorder preorder : preorders) {
//                Member member = bot.getMember(String.valueOf(preorder.getDiscordId()));
//                if(member == null) continue;
//
//                if(member.getRoles().stream().anyMatch(r -> r.getName().equals(roleS))) continue;
//
//                Role role = bot.getRoles(roleS).first();
//                TechDiscordBot.getGuild().addRoleToMember(member, role).queue();
//            }
//        }
//    }
//
//    @Override
//    public void onDisable() {}
//
//    @Override
//    public String getName() {
//        return "Preorder";
//    }
//
//    @Override
//    public Requirement[] getRequirements() {
//        return new Requirement[0];
//    }
//}
