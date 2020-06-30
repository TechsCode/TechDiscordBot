package me.TechsCode.TechDiscordBot.songoda;

public class SongodaPurchase {

    private String order_number;
    private String transaction_id;
    private String product;
    private String status;
    private String amount;
    private String currency;
    private String fee;
    private String email;
    private String first_name;
    private String last_name;
    private String username;
    private String discord;
    private int created_at;
    private int updated_at;

    public String getAmount() {
        return amount;
    }

    public String getDiscord() {
        return discord;
    }

    public String getCurrency() {
        return currency;
    }

    public String getEmail() {
        return email;
    }

    public String getFee() {
        return fee;
    }

    public String getProduct() {
        return product;
    }

    public String getStatus() {
        return status;
    }

    public String getUsername() {
        return username;
    }

    public int getCreatedAt() {
        return created_at;
    }

    public int getUpdatedAt() {
        return updated_at;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getOrderNumber() {
        return order_number;
    }

    public String getTransactionId() {
        return transaction_id;
    }
}
