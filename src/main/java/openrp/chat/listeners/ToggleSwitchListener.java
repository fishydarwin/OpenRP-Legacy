package openrp.chat.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import openrp.OpenRP;
import openrp.chat.events.ORPChatEvent;
import openrp.chat.events.ORPChatPreprintEvent;

/**
 * A listener built into OpenRP that handles toggling and switching channels.
 * This is due to the large amount of requests for doing something like this.
 * 
 * @author Darwin Jonathan
 *
 */
public class ToggleSwitchListener implements Listener {

	private OpenRP plugin;

	private static Map<Player, List<String>> toggles;
	private static Map<Player, String> switches;

	private File file;
	private FileConfiguration configfile;

	private String defaultChannel;

	private static List<String> toggleChannels;
	private static List<String> switchChannels;

	private static boolean useToggles;
	private static boolean useSwitches;

	public ToggleSwitchListener(OpenRP plugin) {
		this.plugin = plugin;
		toggles = new HashMap<>();
		switches = new HashMap<>();
		file = new File(plugin.getDataFolder() + File.separator + "chat", "toggle-and-switcher.yml");
		if (!file.exists()) {
			plugin.saveResource("toggle-and-switcher.yml", false);
		}
		configfile = YamlConfiguration.loadConfiguration(file);
		useToggles = configfile.getBoolean("enable-channel-toggler", false);
		useSwitches = configfile.getBoolean("enable-channel-switches", false);
		defaultChannel = plugin.getChat().getConfig().getString("default", null);
		toggleChannels = configfile.isSet("toggleable-channels") ? configfile.getStringList("toggleable-channels")
				: new ArrayList<String>();
		switchChannels = configfile.isSet("switchable-channels") ? configfile.getStringList("switchable-channels")
				: new ArrayList<String>();
	}

	public static final boolean usingToggles() {
		return useToggles;
	}

	public static final boolean usingSwitches() {
		return useSwitches;
	}

	public static final List<String> getToggleChannels() {
		return toggleChannels;
	}

	public static final List<String> getSwitchChannels() {
		return switchChannels;
	}

	public static void setToggleList(Player p, List<String> l) {
		toggles.put(p, l);
	}

	public static List<String> getToggleList(Player p) {
		return toggles.containsKey(p) ? toggles.get(p) : new ArrayList<String>();
	}

	@EventHandler
	public void onToggledChannelMessage(ORPChatPreprintEvent event) {
		if (!useToggles) {
			return;
		}
		if (!toggles.containsKey(event.getPlayer())) {
			return;
		}
		if (!toggles.get(event.getPlayer()).contains(event.getChannel())) {
			return;
		}
		event.setCancelled(true);
	}

	@EventHandler
	public void onToggledOffChannelMessage(ORPChatEvent event) {
		if (!useToggles) {
			return;
		}
		if (toggles.containsKey(event.getPlayer())) {
			return;
		}
		if (!toggles.get(event.getPlayer()).contains(event.getChannel())) {
			return;
		}
		event.getPlayer().sendMessage(plugin.getChat().getMessage("cant-use-when-toggled-off"));
		event.setCancelled(true);
	}

	public static void setSwitchChannel(Player p, String c) {
		switches.put(p, c);
	}

	public static String getSwitchChannel(Player p) {
		return switches.containsKey(p) ? switches.get(p) : null;
	}

	@EventHandler
	public void onSwitchedChannelMessage(ORPChatEvent event) {
		if (!useSwitches) {
			return;
		}
		if (defaultChannel == null) {
			return;
		}
		if (!event.getChannel().equals(defaultChannel)) {
			return;
		}
		if (!switches.containsKey(event.getPlayer())) {
			return;
		}
		/* SEEMS NOT TO BE REQUIRED
		if (useToggles) {
			if (toggles.containsKey(event.getPlayer())) {
				if (toggles.get(event.getPlayer()).contains(switches.get(event.getPlayer()))) {
					event.getPlayer().sendMessage(plugin.getChat().getMessage("cant-use-when-toggled-off"));
					event.setCancelled(true);
					return;
				}
			}
		}
		*/
		if (switches.get(event.getPlayer()).equals(defaultChannel)) {
			return;
		}
		event.setChannel(switches.get(event.getPlayer()));
	}

}
