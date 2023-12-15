package me.techscode.techdiscordbot.database.tables;

import com.greazi.discordbotfoundation.Common;
import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.verification.SqlPurchase;
import me.techscode.techdiscordbot.database.entities.verification.SqlVerification;
import org.jooq.Select;

import java.util.List;

import static org.jooq.impl.DSL.*;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.SQLDataType.*;

public class Purchases {
    public static final String TABLE_NAME = "purchases";

    public Purchases() {
        createTable();
    }

    public void createTable() {
        try {
            Database.sql.createTableIfNotExists(TABLE_NAME)
                    .column("id", BIGINT.identity(true)).primaryKey("id")
                    .column("verificationId", BIGINT)
                    .column("pluginId", INTEGER)
                    .column("transactionId", VARCHAR)
                    .constraint(
                            foreignKey("verificationId")
                                    .references("verifications", "id")
                    )
                    .execute();
        } catch (final Exception e) {
            Common.throwError(e, "Failed to create table " + TABLE_NAME);
        }
    }

    public void add(final SqlPurchase verification) {
        try {
            Database.sql.insertInto(table(TABLE_NAME))
                    .set(field("verificationId"), verification.getVerificationId())
                    .set(field("pluginId"), verification.getPluginId())
                    .set(field("transactionId"), verification.getTransactionId())
                    .execute();
        } catch (final Exception e) {
            Common.throwError(e, "Failed to add a new purchase to table " + TABLE_NAME + " from member " + verification.getSqlMember().getDiscordMember().getEffectiveName() + "-" + verification.getSqlMember().getDiscordId());
        }
    }

    public List<SqlPurchase> getById(final int id) {
        try {
            final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("id").eq(id));
            return select.fetch().into(SqlPurchase.class);
        } catch (final Exception e) {
            Common.throwError(e, "Failed to get a purchase from table " + TABLE_NAME + " from id " + id);
            return null;
        }
    }

    public List<SqlPurchase> getByVerificationId(final int verificationId) {
        try {
            final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("verificationId").eq(verificationId));
            return select.fetch().into(SqlPurchase.class);
        } catch (final Exception e) {
            Common.throwError(e, "Failed to get a purchase from table " + TABLE_NAME + " from verification id " + verificationId);
            return null;
        }
    }

    public List<SqlPurchase> getByDiscordId(final long discordId) {
        try {
            final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("verificationId").eq(Database.VERIFICATIONS.getByDiscordId(discordId).get(0).getId()));
            return select.fetch().into(SqlPurchase.class);
        } catch (final Exception e) {
            Common.throwError(e, "Failed to get verification from table " + TABLE_NAME + " from Discord id " + discordId);
            return null;
        }
    }

    public List<SqlPurchase> getByTransactionId(final long transactionId) {
        try {
            final Select<?> select = Database.sql.select().from(TABLE_NAME).where(field("transactionId").eq(Database.MEMBERSTable.getFromDiscordId(transactionId).get(0).getId()));
            return select.fetch().into(SqlPurchase.class);
        } catch (final Exception e) {
            Common.throwError(e, "Failed to get verification from table " + TABLE_NAME + " from discord id " + transactionId);
            return null;
        }
    }
}
