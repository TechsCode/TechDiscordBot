package me.techscode.techdiscordbot.database.entities;

import me.techscode.techdiscordbot.database.Database;

import javax.annotation.Nullable;
import java.util.Optional;

public class SqlPatreon {
	private int id;
	private final int memberId;
	private final long join;
	private long left;
	private int tier;

	public SqlPatreon(final int memberId, final long join) {
		this.memberId = memberId;
		this.join = join;
	}


	public SqlPatreon(final int id, final int memberId, final long join, final long left, int tier) {
		this.id = id;
		this.memberId = memberId;
		this.join = join;
		this.left = left;
	}

	public int getId() {
		return id;
	}

	@Nullable
	public SqlMember getSqlMember() {
		final Optional<SqlMember> SqlMember = Database.MEMBERSTable.getFromId(this.memberId).stream().findFirst();
		return SqlMember.orElse(null);
	}

	public long getJoin() {
		return join;
	}

	public long getLeft() {
		return left;
	}

	public int getTier() {
		return tier;
	}

	public boolean save() {
		return Database.PATREONTable.add(this);
	}

	public boolean delete() {
		return Database.PATREONTable.delete(this);
	}

}
