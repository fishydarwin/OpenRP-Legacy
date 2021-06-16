package openrp.rolls;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import openrp.OpenRP;
import openrp.rolls.cmds.Command_ROLL;

/**
 * OpenRP Rolls API instance. Can be accessed from the OpenRP main class via
 * getRolls().
 * 
 * @author Darwin Jonathan
 *
 */
public class ORPRolls {

	private OpenRP plugin;
	private File directory;

	private FileConfiguration config;
	private FileConfiguration messages;

	public ORPRolls(OpenRP plugin) {
		this.plugin = plugin;
	}

	/**
	 * Rolls a number between min and max, inclusive, outputting the result.
	 * 
	 * @param min - Minimum boundary.
	 * @param max - Maximum boundary.
	 * @return The output number.
	 */
	public Integer roll(int min, int max) {
		Random r = new Random();
		int i = r.nextInt(max - min + 1) + min;
		return i;
	}

	/**
	 * Calls a HashMap with standard replacements for this plugin.
	 */
	public HashMap<String, String> getStandardHashMap(Player player, Integer roll, String boundary) {
		HashMap<String, String> h = new HashMap<String, String>();
		h.put("{player}", player.getName());
		h.put("{roll}", roll.toString());
		h.put("{max}", boundary);
		return h;
	}

	/**
	 * Registers all event classes related to OpenRP Rolls.
	 */
	public void registerEvents() {
		plugin.getLogger().info("Registering Roll Commands...");
		Command_ROLL handler_ROLL = new Command_ROLL(plugin);
		plugin.getCommand("roll").setExecutor(handler_ROLL);
		plugin.getCommand("roll").setTabCompleter(handler_ROLL);
		plugin.getCommand("roll").setPermission(getConfig().getString("use-perm"));
		plugin.getLogger().info("Rolls Loaded!");
	}

	/**
	 * If x/plugins/OpenRP/rolls does not exist, this creates it, to avoid problems.
	 */
	public void fixFilePath() {
		directory = new File(plugin.getDataFolder() + File.separator + "rolls");
		if (!directory.exists()) {
			directory.mkdir();
		}
	}

	/**
	 * Reloads OpenRP Rolls' config.yml file.
	 */
	public void reloadConfig() {
		File file_config = new File(directory, "config.yml");
		if (!file_config.exists()) {
			plugin.saveResource("rolls/config.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(file_config);
	}

	/**
	 * Saves OpenRP Rolls' config.yml file.
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
	 * Returns OpenRP Rolls' config.yml file for you to use.
	 */
	public FileConfiguration getConfig() {
		return config;
	}

	/**
	 * Reloads OpenRP Rolls' messages.yml file.
	 */
	public void reloadMessages() {
		File file_messages = new File(directory, "messages.yml");
		if (!file_messages.exists()) {
			plugin.saveResource("rolls/messages.yml", false);
		}
		messages = YamlConfiguration.loadConfiguration(file_messages);
	}

	/**
	 * Returns OpenRP Rolls' messages.yml file for you to use.
	 */
	public FileConfiguration getMessages() {
		return messages;
	}

	/**
	 * Is a quick shortcut that returns the colorized message from the messages
	 * file.
	 */
	public String getMessage(String path) {
		return plugin.colorize(getMessages().getString(path), false);
	}

}
