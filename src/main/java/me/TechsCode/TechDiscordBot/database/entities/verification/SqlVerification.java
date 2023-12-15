package me.techscode.techdiscordbot.database.entities.verification;

import me.techscode.techdiscordbot.database.Database;
import me.techscode.techdiscordbot.database.entities.SqlMember;
import me.techscode.techdiscordbot.model.enums.Marketplace;

import javax.annotation.Nullable;
import java.util.Optional;

public class SqlVerification {
	private int id;
	private final int memberId;
	private final int marketplace;
	private final int marketplaceUserId;

	public SqlVerification(final int memberId, final Marketplace marketplace, final int marketplaceUserId) {
		this.memberId = memberId;
		this.marketplace = marketplace.getId();
		this.marketplaceUserId = marketplaceUserId;
	}

	public SqlVerification(final int id, final int memberId, final int marketplace, final int marketplaceUserId) {
		this.id = id;
		this.memberId = memberId;
		this.marketplace = marketplace;
		this.marketplaceUserId = marketplaceUserId;
	}

	public int getId() {
		return id;
	}

	@Nullable
	public SqlMember getSqlMember() {
		final Optional<SqlMember> SqlMember = Database.MEMBERSTable.getFromId(this.memberId).stream().findFirst();
		return SqlMember.orElse(null);
	}

	public int getMarketplace() {
		return marketplace;
	}

	public int getMarketplaceUserId() {
		return marketplaceUserId;
	}

	public boolean save() {
		return Database.VERIFICATIONS.add(this);
	}

	public boolean delete() {
		return Database.VERIFICATIONS.delete(this);
	}

}
