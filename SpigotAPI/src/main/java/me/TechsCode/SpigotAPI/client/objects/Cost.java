package me.TechsCode.SpigotAPI.client.objects;

public class Cost {

    private double value;
    private String currency;

    public Cost(double value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public double getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }
}
