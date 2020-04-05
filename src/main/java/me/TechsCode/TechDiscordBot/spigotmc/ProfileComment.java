package me.TechsCode.TechDiscordBot.spigotmc;

public class ProfileComment {

    private String commentId, userId, message;

    public ProfileComment(String commentId, String userId, String message) {
        this.commentId = commentId;
        this.userId = userId;
        this.message = message;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return message;
    }
}