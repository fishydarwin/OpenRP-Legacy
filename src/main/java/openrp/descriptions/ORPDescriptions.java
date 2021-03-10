package openrp.descriptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import openrp.OpenRP;
import openrp.descriptions.cmds.Command_CHARACTER;
import openrp.descriptions.events.ORPDescriptionsChangeEvent;
import openrp.descriptions.listeners.DescriptionCheckListener;

/**
 * OpenRP Descriptions API instance. Can be accessed from the OpenRP main class
 * via getDesc().
 * 
 * @author Darwin Jonathan
 *
 */
public class ORPDescriptions {

	private OpenRP plugin;
	private File directory;

	private HashMap<Player, Long> cooldowns = new HashMap<Player, Long>();

	private FileConfiguration config;
	private FileConfiguration messages;
	private FileConfiguration userdata;

	private ArrayList<String> fields;

	public ORPDescriptions(OpenRP plugin) {
		this.plugin = plugin;
	}

	/**
	 * Registers all Description fields usable for characters.
	 */
	public void registerFields() {

		if (!getConfig().isSet("fields")) {
			plugin.getLogger().info("No Description Fields to load, skipping. . .");
			return;
		}
		ArrayList<String> fields = new ArrayList<String>();
		for (String key : getConfig().getConfigurationSection("fields").getKeys(false)) {

			fields.add(key);
			plugin.getLogger().info("added : " + key);

		}
		this.fields = fields;

	}

	/**
	 * Get a list of all Description fields.
	 * 
	 * @return A list of all fields.
	 */
	public ArrayList<String> getFields() {
		return fields;
	}

	/**
	 * Checks to see if a field is set.
	 * 
	 * @param player - The player to check for.
	 * @param field  - The field to check.
	 * @return True if it's set, false otherwise.
	 */
	public boolean isFieldSet(Player player, String field) {
		if (plugin.getDesc().getUserdata().isSet(player.getUniqueId().toString() + "." + field)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks to see if a field is set.
	 * 
	 * @param player - The UUID of the player to check for.
	 * @param field  - The field to check.
	 * @return True if it's set, false otherwise.
	 */
	public boolean isFieldSet(UUID uniqueId, String field) {
		if (plugin.getDesc().getUserdata().isSet(uniqueId + "." + field)) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the value of a field for a player.
	 * 
	 * @param player - The player to set for.
	 * @param value  - The new value of the field.
	 * @param field  - The field to set.
	 */
	public void setField(Player player, String value, String field) {
		getUserdata().set(player.getUniqueId().toString() + "." + field, value);
		saveUserdata();
	}

	/**
	 * Sets the value of a field for a player.
	 * 
	 * @param uuid  - The player's uuid to set for.
	 * @param value - The new value of the field.
	 * @param field - The field to set.
	 */
	public void setField(UUID uuid, String value, String field) {
		getUserdata().set(uuid.toString() + "." + field, value);
		saveUserdata();
	}

	/**
	 * Checks to see if an input String contains any OpenRP Description fields.
	 * 
	 * @param input - The input String.
	 * @return True if it contains any fields, false otherwise.
	 */
	public boolean containsFields(String input) {
		for (String f : getFields()) {
			if (input.contains("{orpdesc_" + f + "}")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Replaces a field's value for a specific player in an input string.
	 * 
	 * @param input  - The input String.
	 * @param player - The player to replace for.
	 * @return The replacements' value.
	 */
	public String replaceFieldPlaceholders(String input, Player player) {
		if (!containsFields(input)) {
			return input;
		}
		String output = input;
		for (String f : getFields()) {
			output = output.replaceAll("\\{orpdesc_" + f + "\\}",
					getUserdata().getString(player.getUniqueId().toString() + "." + f));
		}
		return output;
	}

	/**
	 * Replaces a field's value for a specific player in an input string.
	 * 
	 * @param input  - The input String.
	 * @param player - The player to replace for.
	 * @return The replacements' value.
	 */
	public String replaceFieldPlaceholders(String input, OfflinePlayer player) {
		if (!containsFields(input)) {
			return input;
		}
		String output = input;
		for (String f : getFields()) {
			output = output.replaceAll("\\{orpdesc_" + f + "\\}",
					getUserdata().getString(player.getUniqueId().toString() + "." + f));
		}
		return output;
	}

	/**
	 * Replaces a field's value for a specific player in an input string.
	 * 
	 * @param input - The input String.
	 * @param uuid  - The player's uuid to replace for.
	 * @return The replacements' value.
	 */
	public String replaceFieldPlaceholders(String input, UUID uuid) {
		if (!containsFields(input)) {
			return input;
		}
		String output = input;
		for (String f : getFields()) {
			output = output.replaceAll("\\{orpdesc_" + f + "\\}", getUserdata().getString(uuid.toString() + "." + f));
		}
		return output;
	}

	public HashMap<Player, Long> getCooldowns() {
		return cooldowns;
	}

	/**
	 * Calls a HashMap with standard replacements for this plugin.
	 */
	public HashMap<String, String> getStandardHashMap(Player player) {
		HashMap<String, String> h = new HashMap<String, String>();
		h.put("{player}", player.getName());
		return h;
	}

	/**
	 * Calls a HashMap with standard replacements for this plugin.
	 */
	public HashMap<String, String> getStandardHashMap(UUID uuid) {
		HashMap<String, String> h = new HashMap<String, String>();
		h.put("{player}", plugin.getServer().getOfflinePlayer(uuid).getName());
		return h;
	}

	/**
	 * Registers all event classes related to OpenRP Descriptions.
	 */
	public void registerEvents() {
		plugin.getLogger().info("Registering Descriptions Fields...");
		registerFields();
		plugin.getLogger().info("Registering Descriptions Commands...");
		Command_CHARACTER handler_CHARACTER = new Command_CHARACTER(plugin);
		plugin.getCommand("character").setExecutor(handler_CHARACTER);
		plugin.getCommand("character").setTabCompleter(handler_CHARACTER);
		plugin.getLogger().info("Registering Descriptions Listeners...");
		plugin.getServer().getPluginManager().registerEvents(new DescriptionCheckListener(plugin), plugin);
		plugin.getLogger().info("Registering Descriptions Expansions...");
		
		plugin.getLogger().info("Running Descriptions Default Info Completer. This is Async, but might take a bit...");
		plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (plugin.getDesc().getFields().isEmpty()) {
						break;
					}
					for (String s : plugin.getDesc().getFields()) {
						if (!plugin.getDesc().isFieldSet(p, s)) {
							String field = s;
							String value = plugin.getDesc().getConfig().getString("fields." + s + ".default-value");
							ORPDescriptionsChangeEvent changeevent = new ORPDescriptionsChangeEvent(p.getUniqueId(),
									field, value);
							if (!changeevent.isCancelled()) {
								plugin.getDesc().setField(p, changeevent.getValue(), changeevent.getField());
							}
						}
					}
				}
			}
		}, 1L);
		plugin.getLogger().info("Descriptions Loaded!");
	}

	/**
	 * If x/plugins/OpenRP/descriptions does not exist, this creates it, to avoid
	 * problems.
	 */
	public void fixFilePath() {
		directory = new File(plugin.getDataFolder() + File.separator + "descriptions");
		if (!directory.exists()) {
			directory.mkdir();
		}
	}

	/**
	 * Reloads OpenRP Descriptions' config.yml file.
	 */
	public void reloadConfig() {
		File file_config = new File(directory, "config.yml");
		if (!file_config.exists()) {
			plugin.saveResource("descriptions/config.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(file_config);
	}

	/**
	 * Saves OpenRP Descriptions' config.yml file.
	 */
	public void saveConfig() {
		File file_config = new File(directory, "config.yml");
		try {
			config.save(file_config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns OpenRP Descriptions' config.yml file for you to use.
	 */
	public FileConfiguration getConfig() {
		return config;
	}

	/**
	 * Reloads OpenRP Descriptions' messages.yml file.
	 */
	public void reloadMessages() {
		File file_messages = new File(directory, "messages.yml");
		if (!file_messages.exists()) {
			plugin.saveResource("descriptions/messages.yml", false);
		}
		messages = YamlConfiguration.loadConfiguration(file_messages);
	}

	/**
	 * Returns OpenRP Descriptions' messages.yml file for you to use.
	 */
	public FileConfiguration getMessages() {
		return messages;
	}

	/**
	 * Is a quick shortcut that returns the colorized message from the messages
	 * file.
	 */
	public String getMessage(String path) {
		return plugin.colorize(getMessages().getString(path));
	}

	/**
	 * Reloads OpenRP Descriptions' userdata.yml file.
	 */
	public void reloadUserdata() {
		File file_userdata = new File(directory, "userdata.yml");
		if (!file_userdata.exists()) {
			plugin.saveResource("descriptions/userdata.yml", false);
		}
		userdata = YamlConfiguration.loadConfiguration(file_userdata);
	}

	/**
	 * Saves OpenRP Descriptions' userdata.yml file.
	 */
	public void saveUserdata() {
		File file_userdata = new File(directory, "userdata.yml");
		try {
			userdata.save(file_userdata);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns OpenRP Descriptions' userdata.yml file for you to use.
	 */
	public FileConfiguration getUserdata() {
		return userdata;
	}
	
}
