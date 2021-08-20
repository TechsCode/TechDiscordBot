package me.TechsCode.TechDiscordBot.spigotmc.data;

import com.google.gson.JsonObject;

public class ProfileComment {
    private final String commentId, userId, message;

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

    public JsonObject getState() {
        JsonObject comment = new JsonObject();
        comment.addProperty("commentId", commentId);
        comment.addProperty("userId", userId);
        comment.addProperty("message", message);

        return comment;
    }
}
