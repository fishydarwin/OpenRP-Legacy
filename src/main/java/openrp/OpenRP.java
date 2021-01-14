package openrp;

/*
 *   OpenRP is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *   
 *   OpenRP is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *   
 *   Check the full license at <http://www.gnu.org/licenses/>.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import openrp.chat.ORPChat;
import openrp.descriptions.ORPDescriptions;
import openrp.descriptions.expansions.MVdW_Descriptions;
import openrp.descriptions.expansions.PAPI_Descriptions;
import openrp.rolls.ORPRolls;
import openrp.time.ORPTime;
import openrp.time.utils.TimeHandler;

/**
 * The main class of OpenRP. Fork into this to get general access to OpenRP's
 * API.
 * 
 * @author Darwin Jonathan
 *
 */
public class OpenRP extends JavaPlugin {

	private static final Integer resourceID = 73785;
	private OpenRP plugin = this;

	private FileConfiguration messages;

	private boolean api_papi = false;
	private boolean api_mvdw = false;

	private ORPChat chat;
	private ORPDescriptions desc;
	private ORPRolls rolls;
	private ORPTime time;

	private boolean canUseHex = false;

	/**
	 * Return OpenRP's Chat API.
	 */
	public ORPChat getChat() {
		return chat;
	}

	/**
	 * Return OpenRP's Descriptions API.
	 */
	public ORPDescriptions getDesc() {
		return desc;
	}

	/**
	 * Return OpenRP's Rolls API.
	 */
	public ORPRolls getRolls() {
		return rolls;
	}

	/**
	 * Return OpenRP's Time API.
	 */
	public ORPTime getTime() {
		return time;
	}

	/**
	 * Fixes all file paths automatically.
	 */
	private void fixFilePaths() {
		chat.fixFilePath();
		desc.fixFilePath();
		rolls.fixFilePath();
		time.fixFilePath();
	}

	/**
	 * Loads an arbitrary configuration file in the OpenRP Directory.
	 */
	public FileConfiguration loadArbitraryFile(String nameOfFile, String path) {
		File file_file = new File(getDataFolder() + File.separator + path, nameOfFile);
		{
			File file_test = new File(getDataFolder() + File.separator + path);
			if (!file_test.exists()) {
				file_test.mkdir();
			}
		}
		if (!file_file.exists()) {
			try {
				file_file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return YamlConfiguration.loadConfiguration(file_file);
	}

	/**
	 * Saves an arbitrary configuration file in the OpenRP Directory.
	 */
	public void saveArbitraryFile(FileConfiguration file, String nameOfFile, String path) {
		File file_file = new File(getDataFolder() + File.separator + path, nameOfFile);
		try {
			file.save(file_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parses the input in accordance to the given extra placeholders and according
	 * to PAPI or MVdW.
	 * 
	 * @param input  - The input String to parse.
	 * @param player - The input Player to parse for.
	 * @param extra  - A HashMap of two strings, the key being the regex, and the
	 *               value being the replacement.
	 * @return The final parsed String.
	 */
	public String parsePlaceholders(String input, Player player, HashMap<String, String> extra) {
		String output = input;
		for (String key : extra.keySet()) {
			output = output.replace(key, extra.get(key));
		}

		if (getConfig().getStringList("enabled").contains("descriptions")) {
			output = getDesc().replaceFieldPlaceholders(output, player);
		}

		if (api_papi) {
			if (me.clip.placeholderapi.PlaceholderAPI.containsPlaceholders(output)) {
				output = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, output);
			}
		}

		if (api_mvdw) {
			output = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, output);
		}

		return output;
	}

	/**
	 * Parses the input in accordance to the given extra placeholders and according
	 * to PAPI or MVdW.
	 * 
	 * @param input - The input String to parse.
	 * @param uuid  - The input player's uuid to parse for.
	 * @param extra - A HashMap of two strings, the key being the regex, and the
	 *              value being the replacement.
	 * @return The final parsed String.
	 */
	public String parsePlaceholders(String input, UUID uuid, HashMap<String, String> extra) {
		String output = input;
		for (String key : extra.keySet()) {
			output = output.replace(key, extra.get(key));
		}

		if (getConfig().getStringList("enabled").contains("descriptions")) {
			output = getDesc().replaceFieldPlaceholders(output, uuid);
		}

		if (api_papi) {
			if (me.clip.placeholderapi.PlaceholderAPI.containsPlaceholders(output)) {
				output = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(getServer().getOfflinePlayer(uuid),
						output);
			}
		}

		if (api_mvdw) {
			output = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(getServer().getOfflinePlayer(uuid),
					output);
		}

		return output;
	}

	/**
	 * Returns the colorized String according to the ChatColor rules, using the &
	 * color code. 1.16 addition: also handles hex codes. Exists for convenience.
	 * 
	 * @param input - The input String to parse.
	 * @return The final colorized String.
	 */
	public String colorize(String input) {
		String formatted = ChatColor.translateAlternateColorCodes('&', input);
		if (canUseHex) {
			return formatHexCodes(formatted);
		}
		return formatted;
	}

	private static final Pattern hexColorPattern = Pattern.compile("^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$");

	private String formatHexCodes(String input) {
		String value = input;
		Matcher matcher = hexColorPattern.matcher(input);
		while (matcher.find()) {
			String hexcode = matcher.group();
			String fixed = new String(new char[] { '&', hexcode.charAt(1), '&', hexcode.charAt(2), '&',
					hexcode.charAt(3), '&', hexcode.charAt(4), '&', hexcode.charAt(5), '&', hexcode.charAt(6) });
			value = value.replace(hexcode, "&x" + fixed);
		}
		return value;
	}

	/**
	 * Gets the latest version of OpenRP from SpigotMC.
	 * 
	 * @return A String representing the latest version.
	 */
	private void getLatestVersion(final Consumer<String> consumer) {
		getServer().getScheduler().runTaskAsynchronously(this, () -> {
			try (InputStream inputStream = new URL(
					"https://api.spigotmc.org/legacy/update.php?resource=" + resourceID.toString()).openStream();
					Scanner scanner = new Scanner(inputStream)) {
				if (scanner.hasNext()) {
					consumer.accept(scanner.next());
				}
			} catch (IOException exception) {
				getLogger().warning("Cannot look for updates: " + exception.getMessage());
			}
		});
	}

	/**
	 * Runs on plugin enable.
	 */
	@Override
	public void onEnable() {

		// Load the basic stuff.
		saveDefaultConfig();
		saveResource("messages.yml", false);

		reloadMessages();

		// Update check.
		if (getConfig().isSet("auto-updates")) {
			if (getConfig().getBoolean("auto-updates")) {
				getLatestVersion(version -> {
					if (!getDescription().getVersion().equalsIgnoreCase(version)) {
						getLogger().info(getMessage("auto-update").replace("{v}", getDescription().getVersion())
								.replace("{l}", version));
					}
				});
			}
		}

		// Check for version to see if 1.16 color codes are allowed
		{
			String version = getServer().getBukkitVersion();
			getLogger().info("Detected Bukkit Version: " + version);
			String[] list = version.split("-");
			list = list[0].split(".");
			int i = 0;
			i = Integer.valueOf(list[0]) + Integer.valueOf(list[1]);
			// 1 + 13 = 14 -> 1.13
			if (i < 14) {
				getLogger().severe("Bukkit Version looks to be lower than 1.13, OpenRP only works on 1.13+");
				getServer().getPluginManager().disablePlugin(this);
			}
			// 1 + 16 = 17 -> 1.16
			if (i > 17) {
				canUseHex = true;
			}
		}

		/*
		 * Why was this needed? // Run other things after 1 tick.
		 * getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
		 * 
		 * @Override public void run() {
		 */

		// Registering standard commands.
		Command_OPENRP handler_OPENRP = new Command_OPENRP(plugin);
		getCommand("openrp").setExecutor(handler_OPENRP);
		getCommand("openrp").setTabCompleter(handler_OPENRP);
		getCommand("openrp").setPermission(plugin.getConfig().getString("admin-perm"));

		// Load all API instances.
		chat = new ORPChat(plugin);
		desc = new ORPDescriptions(plugin);
		rolls = new ORPRolls(plugin);
		time = new ORPTime(plugin);

		// Load configurations.
		fixFilePaths();

		getChat().reloadConfig();
		getChat().reloadMessages();

		getDesc().reloadConfig();
		getDesc().reloadMessages();
		getDesc().reloadUserdata();

		getRolls().reloadConfig();
		getRolls().reloadMessages();

		getTime().reloadConfig();
		getTime().reloadMessages();
		getTime().reloadTimedata();

		// Register commands & events based on enabled module state.
		ArrayList<String> enabledModules = new ArrayList<String>();
		enabledModules = (ArrayList<String>) getConfig().getStringList("enabled");
		for (String s : enabledModules) {
			if (s.equalsIgnoreCase("chat")) {
				getChat().registerEvents();
			} else if (s.equalsIgnoreCase("descriptions")) {
				getDesc().registerEvents();
			} else if (s.equalsIgnoreCase("rolls")) {
				getRolls().registerEvents();
			} else if (s.equalsIgnoreCase("time")) {
				getTime().registerEvents();
			}
		}

		/*
		 * } }, 1);
		 */

		getServer().getScheduler().runTask(plugin, new Runnable() {
			@Override
			public void run() {

				// Checking for Soft-Dependencies.
				if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
					getLogger().info("Found PlaceholderAPI. Hooking into it!");
					api_papi = true;
				}
				if (getServer().getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
					getLogger().info("Found MVdWPlaceholderAPI. Hooking into it!");
					api_mvdw = true;
				}

				plugin.getLogger().info("Registering Descriptions Expansions...");
				if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
					PAPI_Descriptions papi_ORPDESC = new PAPI_Descriptions(plugin);
					papi_ORPDESC.register();
				}
				if (plugin.getServer().getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
					new MVdW_Descriptions(plugin, plugin.getDesc());
				}

			}
		});

	}

	/**
	 * Runs on plugin disable.
	 */
	@Override
	public void onDisable() {

		// Save everything.
		getDesc().saveUserdata();
		for (TimeHandler th : getTime().getTimes()) {
			th.updateTimesInData();
		}
		getTime().saveTimedata();

	}

	/**
	 * Reloads OpenRP Chat's messages.yml file.
	 */
	public void reloadMessages() {
		File file_messages = new File(getDataFolder(), "messages.yml");
		if (!file_messages.exists()) {
			saveResource("messages.yml", false);
		}
		messages = YamlConfiguration.loadConfiguration(file_messages);
	}

	/**
	 * Returns OpenRP Chat's messages.yml file for you to use.
	 */
	public FileConfiguration getMessages() {
		return messages;
	}

	/**
	 * Is a quick shortcut that returns the colorized message from the messages
	 * file.
	 */
	public String getMessage(String path) {
		return colorize(getMessages().getString(path));
	}

}
