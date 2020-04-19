package me.TechsCode.TechDiscordBot.objects;

import com.stanjg.ptero4j.entities.panel.admin.User;

public class UserPass {

    private String pass;
    private User user;

    private UserPass(User user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public static UserPass of(User user, String pass) {
        return new UserPass(user, pass);
    }

    public User getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
}
