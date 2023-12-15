package me.techscode.techdiscordbot.settings;

import com.greazi.discordbotfoundation.settings.SimpleSettings;

import java.util.List;

/**
 * A very ugly way to create an settings file
 * This is TEMPORARILY a better settings system
 * will arrive later
 */
public class Settings extends SimpleSettings {

	public static class Roles {
		static String mainPath = "Roles.";

		public static long staff = getLong(mainPath + "Staff");

		public static long leadership = getLong(mainPath + "Leadership");
		public static long development = getLong(mainPath + "Development");
		public static long support = getLong(mainPath + "Support");
		public static long developer = getLong(mainPath + "Developer");
		public static long seniorSupporter = getLong(mainPath + "SeniorSupporter");
		public static long supporter = getLong(mainPath + "Supporter");
		public static long juniorSupporter = getLong(mainPath + "JuniorSupporter");

		public static long verified = getLong(mainPath + "Verified");
		public static long spigot = getLong(mainPath + "Spigot");
		public static long builtByBit = getLong(mainPath + "BuiltByBit");
		public static long songoda = getLong(mainPath + "Songoda");
		public static long polymart = getLong(mainPath + "Polymart");

		public static long ultraPermissions = getLong(mainPath + "UltraPermissions");
		public static long ultraCustomizer = getLong(mainPath + "UltraCustomizer");
		public static long ultraEconomy = getLong(mainPath + "UltraEconomy");
		public static long ultraPunishments = getLong(mainPath + "UltraPunishments");
		public static long ultraRegions = getLong(mainPath + "UltraRegions");
		public static long ultraScoreboards = getLong(mainPath + "UltraScoreboards");
		public static long ultraMotd = getLong(mainPath + "UltraMotd");
		public static long insaneShops = getLong(mainPath + "InsaneShops");
		public static long insaneVaults = getLong(mainPath + "InsaneVaults");
		public static long insaneAnnouncer = getLong(mainPath + "InsaneAnnouncer");

		public static long updates = getLong(mainPath + "Updates");
		public static long announcements = getLong(mainPath + "Announcements");
		public static long patreonNews = getLong(mainPath + "PatreonNews");
		public static long pluginLab = getLong(mainPath + "PluginLab");
		public static long giveaways = getLong(mainPath + "Giveaways");
		public static long community = getLong(mainPath + "Community");

		public static long preorder = getLong(mainPath + "Preorder");

		public static class Patreon {

			static String path = "Roles.Patreon.";

			public static long patreon = getLong(path + "Patreon");
			public static long traveler = getLong(path + "Traveler");
			public static long advanturer = getLong(path + "Advanturer");
			public static long pioneer = getLong(path + "Pioneer");
			public static long wizzard = getLong(path + "Wizzard");
		}
	}

	public static class Modules {

		static String mainPath = "Modules.";

		public static class Logs {

			static String path = mainPath + "Logs.";

			public static long chat = getLong(path + "Chat");

			public static long server = getLong(path + "Server");

			public static long verification = getLong(path + "Verification");

			public static long punish = getLong(path + "Punish");

			public static long tickets = getLong(path + "Tickets");

			public static long roles = getLong(path + "Roles");

		}

		public static class SelfRoles {

			static String path = mainPath + "SelfRoles.";

			public static long channel = getLong(path + "Channel");
		}

		public static class Verification {

			static String verificationPath = mainPath + "Verification.";

			public static String enabled = getString(verificationPath + "Enabled");

			public static String channel = getString(verificationPath + "Channel");

			public static List<String> dmUsers = getStringList(verificationPath + "DmUsers");

			public static long manualVerification = getLong(verificationPath + "ManualVerification");

			public static long pingChannel = getLong(verificationPath + "PingChannel");

			public static class UpdatePurchase {
				static String purchasePath = verificationPath + "UpdatePurchase.";

				public static boolean enabled = getBoolean(purchasePath + "Enabled");
			}

			public static class Api {
				static String apiPath = verificationPath + "Api.";

				public static class Paypal {
					static String paypalPath = apiPath + "Paypal.";

					public static String link() {
						String link = getString(paypalPath + "Link");
						if (!link.endsWith("/")) {
							link = link + "/";
						}
						return link;
					}

					public static String token() {
						return getString(paypalPath + "Token");
					}
				}

				public static class Spigot {
					static String spigotPath = apiPath + "Spigot.";

					public static String link() {
						String link = getString(spigotPath + "Link");
						if (!link.endsWith("/")) {
							link = link + "/";
						}
						return link;
					}

					public static String token() {
						return getString(spigotPath + "Token");
					}
				}

				public static class McMarket {
					static String mcMarketPath = apiPath + "McMarket.";

					public static String link() {
						return getString(mcMarketPath + "Link");
					}

					public static String token() {
						return getString(mcMarketPath + "Token");
					}
				}

				public static class Songoda {
					static String songodaPath = apiPath + "Songoda.";

					public static String link() {
						return getString(songodaPath + "Link");
					}

					public static String token() {
						return getString(songodaPath + "Token");
					}
				}

				public static class Polymart {
					static String PolymartPath = apiPath + "Polymart.";

					public static String link() {
						return getString(PolymartPath + "Link");
					}

					public static String token() {
						return getString(PolymartPath + "Token");
					}
				}
			}
		}

		public static class Ticket {
			static String ticketPath = mainPath + "Ticket.";

			public static boolean enabled = getBoolean(ticketPath + "Enabled");

			public static String channel = getString(ticketPath + "Channel");

			public static class Category {
				static String categoryPath = ticketPath + "Category.";

				public static long support = getLong(categoryPath + "Support");
				public static long development = getLong(categoryPath + "Development");
				public static long management = getLong(categoryPath + "Management");

				public static long greazi = getLong(categoryPath + "Greazi");
				public static long timo = getLong(categoryPath + "Timo");
				public static long lucifer = getLong(categoryPath + "Lucifer");
				public static long ghost = getLong(categoryPath + "Ghost");
				public static long fabian = getLong(categoryPath + "Fabian");
				public static long peng = getLong(categoryPath + "Peng");
				public static long das = getLong(categoryPath + "Das");
				public static long opti = getLong(categoryPath + "Opti");

			}
		}

		public static class Apply {
			static String applyPath = mainPath + "Application.";

			public static boolean enabled = getBoolean(applyPath + "Enabled");

			public static String channel = getString(applyPath + "Channel");

			public static String category = getString(applyPath + "Category");
		}

		public static class Preorder {
			static String preorderPath = mainPath + "Preorder.";

			public static boolean enabled = getBoolean(preorderPath + "Enabled");

			public static long channel = getLong(preorderPath + "AnnounceChannel");
		}

		public static class MessageReceive {
			static String messageReceivePath = mainPath + "MessageReceive.";

			public static boolean image = getBoolean(messageReceivePath + "Image");

			public static boolean pastebin = getBoolean(messageReceivePath + "Pastebin");

		}
	}

	public static class Database {

		static String mainPath = "Database.";

		public static class Transcript {

			static String preorderPath = mainPath + "Transcript.";

			public static String username = getString(preorderPath + "Username");

			public static String password = getString(preorderPath + "Password");
		}


	}
}
