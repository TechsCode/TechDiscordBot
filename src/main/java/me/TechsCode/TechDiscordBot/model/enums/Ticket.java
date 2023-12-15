package me.techscode.techdiscordbot.model.enums;


import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.NotNull;

public class Ticket {

	public enum Category {

		PLUGIN("PLUGIN", "Plugin Support", "<:support:1055924689299587183>", "Get support for a plugin"),
		PAYMENTS("PAYMENTS", "Payments", "<:Creditcard:1055923718326599901>", "Get help for payments"),
		DEVELOPER("DEVELOPER", "Developer Request", "<:PluginLab:1045068040615772221>", "Get help with our API's and plugin compatibility"),
		GIVEAWAY("GIVEAWAY", "Giveaway", "🎉", "Claim or host a giveaway"),
		PATREON("PATREON", "Patreon", "<:patreon:1055927571885330472>", "Get help for your Patreon perks"),
		OTHER("OTHER", "Other", "❔", "Get help for something else");

		final String id;
		final String name;
		final String emoji;
		final String description;

		Category(final String id, final String name, final String emoji, final String description) {
			this.id = id;
			this.name = name;
			this.emoji = emoji;
			this.description = description;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return this.name;
		}

		public String getDescription() {
			return this.description;
		}

		@NotNull
		public Emoji getEmoji() {
			return Emoji.fromFormatted(this.emoji);
		}

		public String getEmojiRaw() {
			return this.emoji;
		}
	}

	public enum Payment {

		PAYPAL("PAYPAL", "PayPal", "<:paypal:1063462219771301948>", "Get help for PayPal payments"),
		MARKETPLACE("MARKETPLACE", "Marketplace", "<:support:1055924689299587183>", "Get help for Marketplace payments"),
		VERIFICATION("VERIFICATION", "Verification", "🔓", "Verify purchases"),
		OTHER("OTHER", "Other", "❔", "Get help for something else");

		final String id;
		final String name;
		final String emoji;
		final String description;

		Payment(final String id, final String name, final String emoji, final String description) {
			this.id = id;
			this.name = name;
			this.emoji = emoji;
			this.description = description;
		}

		public String getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public String getDescription() {
			return this.description;
		}

		@NotNull
		public Emoji getEmoji() {
			return Emoji.fromFormatted(this.emoji);
		}

		public String getEmojiRaw() {
			return this.emoji;
		}
	}

	public enum Developer {

		API("API", "API", "<:PluginLab:1045068040615772221>", "Get help with our API's"),
		PLUGIN("PLUGIN", "Plugin", "🧑🏻‍💻", "Get help with plugin compatibility"),
		OTHER("OTHER", "Other", "❔", "Get help for something else");

		final String id;
		final String name;
		final String emoji;
		final String description;

		Developer(final String id, final String name, final String emoji, final String description) {
			this.id = id;
			this.name = name;
			this.emoji = emoji;
			this.description = description;
		}

		public String getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public String getDescription() {
			return this.description;
		}

		@NotNull
		public Emoji getEmoji() {
			return Emoji.fromFormatted(this.emoji);
		}

		public String getEmojiRaw() {
			return this.emoji;
		}
	}

	public enum Giveaway {

		CLAIM("CLAIM", "Claim", "🎉", "Claim a giveaway"),
		HOST("HOST", "Host", "🛰️", "Host a giveaway");

		final String id;
		final String name;
		final String emoji;
		final String description;

		Giveaway(final String id, final String name, final String emoji, final String description) {
			this.id = id;
			this.name = name;
			this.emoji = emoji;
			this.description = description;
		}

		public String getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public String getDescription() {
			return this.description;
		}

		@NotNull
		public Emoji getEmoji() {
			return Emoji.fromFormatted(this.emoji);
		}

		public String getEmojiRaw() {
			return this.emoji;
		}
	}

	public enum Patreon {

		PERKS("PERKS", "Perks", "<:patreon:1055927571885330472>", "Get help for your Patreon perks"),
		REWARDS("REWARDS", "Rewards", "🎉", "Get help for your Patreon rewards"),
		OTHER("OTHER", "Other", "❔", "Get help for something else");

		final String id;
		final String name;
		final String emoji;
		final String description;

		Patreon(final String id, final String name, final String emoji, final String description) {
			this.id = id;
			this.name = name;
			this.emoji = emoji;
			this.description = description;
		}

		public String getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		public String getDescription() {
			return this.description;
		}

		@NotNull
		public Emoji getEmoji() {
			return Emoji.fromFormatted(this.emoji);
		}

		public String getEmojiRaw() {
			return this.emoji;
		}
	}

	public enum Priority {

		HIGH("HIGH", "High", "<:high_priority:694648884332331008>"),
		MEDIUM("MEDIUM", "Medium", "<:medium_priority:694648883980009593>"),
		LOW("LOW", "Low", "<:low_priority:694648884353433601>"),
		PATREON("PATREON", "Patreon", "<:patreon:1055927571885330472>"),
		NONE("NONE", "None", "<:offline:496493395187990538>");

		final String id;
		final String name;
		final String emoji;

		Priority(final String id, final String name, final String emoji) {
			this.id = id;
			this.name = name;
			this.emoji = emoji;
		}

		public String getId() {
			return this.id;
		}

		public String getName() {
			return this.name;
		}

		@NotNull
		public Emoji getEmoji() {
			return Emoji.fromFormatted(this.emoji);
		}

		public String getEmojiRaw() {
			return this.emoji;
		}

	}
}
