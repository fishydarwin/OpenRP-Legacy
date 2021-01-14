package openrp.chat;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import openrp.OpenRP;
import openrp.chat.events.ORPChatPreprintEvent;
import openrp.chat.listeners.ActionListener;
import openrp.chat.listeners.CommandEventListener;
import openrp.chat.utils.ORPChatCommand;

/**
 * OpenRP Chat API instance. Can be accessed from the OpenRP main class via
 * getChat().
 * 
 * @author Darwin Jonathan
 *
 */
public class ORPChat {

	private OpenRP plugin;
	private File directory;

	private HashMap<Player, HashMap<String, Long>> cooldowns = new HashMap<Player, HashMap<String, Long>>();

	private FileConfiguration config;
	private FileConfiguration messages;

	private HashMap<String, ArrayList<String>> channels = new HashMap<String, ArrayList<String>>();
	private ArrayList<String> simpleAliasList = new ArrayList<String>();

	protected ArrayList<ORPChatCommand> commands = new ArrayList<ORPChatCommand>();

	public ORPChat(OpenRP plugin) {
		this.plugin = plugin;
	}

	/**
	 * Registers every chat channel that can be used in OpenRP's Chat.
	 */
	public void registerChannels() {
		if (!getConfig().isSet("channels")) {
			plugin.getLogger().info("No Chat Channels to load, skipping. . .");
			return;
		}
		resetFakeCommands();
		HashMap<String, ArrayList<String>> channels = new HashMap<String, ArrayList<String>>();
		ArrayList<String> simpleAliasList = new ArrayList<String>();
		for (String key : getConfig().getConfigurationSection("channels").getKeys(false)) {

			ArrayList<String> aliases = new ArrayList<String>();
			if (getConfig().isSet("channels." + key + ".commands")) {
				for (String s : getConfig().getStringList("channels." + key + ".commands")) {
					aliases.add(s.toLowerCase());
					simpleAliasList.add(s.toLowerCase());
				}
				if (!getConfig().getStringList("channels." + key + ".commands").isEmpty()) {
					ArrayList<String> a = new ArrayList<String>(aliases);
					a.remove(0);
					plugin.getLogger().info("Creating fake command for " + key + ". . .");
					createFakeCommand(getConfig().getStringList("channels." + key + ".commands").get(0).toLowerCase(),
							a);
				}
			}

			channels.put(key, aliases);
			plugin.getLogger().info(key + ": " + aliases.toString() + "");

		}

		this.channels = channels;
		this.simpleAliasList = simpleAliasList;
	}

	/**
	 * Resets all fake commands for chat channels.
	 */
	private void resetFakeCommands() {
		try {

			Field commandMapField;
			commandMapField = plugin.getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			CommandMap commandMap = (CommandMap) commandMapField.get(plugin.getServer());

			for (ORPChatCommand command : commands) {
				command.unregister(commandMap);
			}

			commands = new ArrayList<ORPChatCommand>();

			plugin.getLogger().info("Reset all fake commands!");

		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates commands that have no functionality for all chat channels, so that
	 * they can be tab-completed.
	 */
	private void createFakeCommand(String name, ArrayList<String> aliases) {
		try {

			Field commandMapField;
			commandMapField = plugin.getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			CommandMap commandMap = (CommandMap) commandMapField.get(plugin.getServer());

			ORPChatCommand command = new ORPChatCommand(name, aliases);
			command.setDescription("name");

			commandMap.register(plugin.getDescription().getName().toLowerCase(), command);
			commands.add(command);

		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gives a HashMap with the channel's path from the configuration as the key,
	 * and a list of all commands that can be used for it as the value.
	 * 
	 * @return HashMap of channel-key, commands-value.
	 */
	public HashMap<String, ArrayList<String>> getChannels() {
		return channels;
	}

	/**
	 * Gives every existing chat command. This is useful for efficiently checking if
	 * a command is part of our channel list.
	 * 
	 * @return An ArrayList of all existing commands.
	 */
	public ArrayList<String> getSimpleAliasList() {
		return simpleAliasList;
	}

	/**
	 * Performs all calculations and replacements required, and then sends the
	 * message using OpenRP Chat.
	 * 
	 * @param player  - The player.
	 * @param message - The player's message.
	 * @param channel - The channel to use.
	 */
	public void sendMessage(Player player, String message, String channel) {

		ArrayList<Player> recipients = new ArrayList<Player>();
		if (getConfig().isSet("channels." + channel + ".range")) {
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (!p.equals(player)) {
					if (player.getWorld().equals(p.getWorld())) {
						if (player.getLocation().distance(p.getLocation()) <= getConfig()
								.getInt("channels." + channel + ".range")) {
							recipients.add(p);
						}
					}
				}
			}
		} else {
			recipients = new ArrayList<Player>(plugin.getServer().getOnlinePlayers());
			recipients.remove(player);
		}

		ORPChatPreprintEvent preprintevent = new ORPChatPreprintEvent(player, message, channel, player);
		plugin.getServer().getPluginManager().callEvent(preprintevent);

		if (!preprintevent.isCancelled()) {

			if (!getCooldowns().isEmpty()) {
				if (getCooldowns().containsKey(player)) {
					getCooldowns().get(player).put(channel, System.currentTimeMillis());
				} else {
					getCooldowns().put(player, new HashMap<String, Long>());
					getCooldowns().get(player).put(channel, System.currentTimeMillis());
				}
			} else {
				getCooldowns().put(player, new HashMap<String, Long>());
				getCooldowns().get(player).put(channel, System.currentTimeMillis());
			}

			String messageToSend = plugin
					.parsePlaceholders(
							plugin.colorize(
									getConfig().getString("channels." + preprintevent.getChannel() + ".format")),
							player, getStandardHashMap(player))
					.replace("{message}", plugin.colorize(preprintevent.getMessage()));

			player.sendMessage(messageToSend);

		}

		for (Player p : recipients) {
			preprintevent = new ORPChatPreprintEvent(p, message, channel, player);
			plugin.getServer().getPluginManager().callEvent(preprintevent);

			if (!preprintevent.isCancelled()) {
				if (p.hasPermission(getConfig().getString("channels." + preprintevent.getChannel() + ".read-perm"))) {

					String messageToSend = plugin
							.parsePlaceholders(
									plugin.colorize(getConfig()
											.getString("channels." + preprintevent.getChannel() + ".format")),
									player, getStandardHashMap(player))
							.replace("{message}", plugin.colorize(preprintevent.getMessage()));

					p.sendMessage(messageToSend);

				}
			}
		}

	}

	/**
	 * Get the cooldown map.
	 * 
	 * @return
	 */
	public HashMap<Player, HashMap<String, Long>> getCooldowns() {
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
	 * Registers all event classes related to OpenRP Chat.
	 */
	public void registerEvents() {
		plugin.getLogger().info("Registering Chat Channels...");
		registerChannels();
		plugin.getLogger().info("Registering Chat Listeners...");
		plugin.getServer().getPluginManager().registerEvents(new CommandEventListener(plugin), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ActionListener(plugin), plugin);
		plugin.getLogger().info("Chat Loaded!");
	}

	/**
	 * If x/plugins/OpenRP/chat does not exist, this creates it, to avoid problems.
	 */
	public void fixFilePath() {
		directory = new File(plugin.getDataFolder() + File.separator + "chat");
		if (!directory.exists()) {
			directory.mkdir();
		}
	}

	/**
	 * Reloads OpenRP Chat's config.yml file.
	 */
	public void reloadConfig() {
		File file_config = new File(directory, "config.yml");
		if (!file_config.exists()) {
			plugin.saveResource("chat/config.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(file_config);
	}

	/**
	 * Saves OpenRP Chat's config.yml file.
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
	 * Returns OpenRP Chat's config.yml file for you to use.
	 */
	public FileConfiguration getConfig() {
		return config;
	}

	/**
	 * Reloads OpenRP Chat's messages.yml file.
	 */
	public void reloadMessages() {
		File file_messages = new File(directory, "messages.yml");
		if (!file_messages.exists()) {
			plugin.saveResource("chat/messages.yml", false);
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
		return plugin.colorize(getMessages().getString(path));
	}

}
