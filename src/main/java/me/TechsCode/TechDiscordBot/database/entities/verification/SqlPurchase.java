package me.techscode.techdiscordbot.database.entities.verification;

import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlMember;

import javax.annotation.Nullable;
import java.util.Optional;

public class SqlPurchase {
    private int id;
    private final int verificationId;
    private final int pluginId;
    private final String transactionId;

    public SqlPurchase(final int verificationId, final int pluginId, final String transactionId) {
        this.verificationId = verificationId;
        this.pluginId = pluginId;
        this.transactionId = transactionId;
    }

    public SqlPurchase(final int id, final int verificationId, final int pluginId, final String transactionId) {
        this.id = id;
        this.verificationId = verificationId;
        this.pluginId = pluginId;
        this.transactionId = transactionId;
    }

    public int getId() {
        return id;
    }

    public int getVerificationId() {
        return verificationId;
    }

    @Nullable
    public SqlMember getSqlMember() {
        Optional<SqlVerification> sqlVerification = Database.VERIFICATIONS.getById(this.verificationId).stream().findFirst();
        return sqlVerification.map(SqlVerification::getSqlMember).orElse(null);
    }

    public int getPluginId() {
        return pluginId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void save() {
        Database.PURCHASES.add(this);
    }

}
