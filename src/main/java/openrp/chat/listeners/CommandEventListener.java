package openrp.chat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.md_5.bungee.api.ChatColor;
import openrp.OpenRP;
import openrp.chat.events.ORPChatEvent;

/**
 * A listener built into OpenRP Chat that runs when Chat functions get used,
 * such as channel commands, talking in the default chat, or tab-completing a
 * command.
 * 
 * Each chat event will also create an ORPChatEvent that can be listened to.
 * 
 * @author Darwin Jonathan
 *
 */
public class CommandEventListener implements Listener {

	private OpenRP plugin;

	public CommandEventListener(OpenRP plugin) {
		this.plugin = plugin;
	}

	// Highest priority because - it has the final say: "check if event is cancelled by someone else first"
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChatCommandSent(PlayerCommandPreprocessEvent event) {

		// Fast-checking if its our command is actually an OpenRP Chat Command. Just to
		// make sure we don't lag any other commands that aren't OpenRP.
		String[] parser = event.getMessage().split(" ");
		if (!plugin.getChat().getSimpleAliasList().contains(parser[0].toLowerCase().replaceFirst("/", "")
				.replaceFirst(plugin.getDescription().getName().toLowerCase() + ":", ""))) {
			return;
		}

		// Check if event is cancelled by someone else first.
		if (event.isCancelled()) {
			return;
		}

		// It's ours, so let's cancel everything.
		event.setCancelled(true);

		// Let's quickly check if there's any message, no point in bothering otherwise.
		if (parser.length <= 1) {
			event.getPlayer()
					.sendMessage(
							plugin.getChat().getMessage("invalid-use")
									.replace("{usage}",
											parser[0].replaceFirst("/", "").replaceFirst(
													plugin.getDescription().getName().toLowerCase() + ":", "")
													+ " (msg)"));
			return;
		}

		// Let's grab our exact channel from config.
		String channel = null;
		for (String key : plugin.getChat().getChannels().keySet()) {
			if (plugin.getChat().getChannels().get(key).contains(parser[0].toLowerCase().replaceFirst("/", "")
					.replaceFirst(plugin.getDescription().getName().toLowerCase() + ":", ""))) {
				channel = key;
				break;
			}
		}

		// Let's check permissions.
		if (plugin.getChat().getConfig().isSet("channels." + channel + ".use-perm")) {
			if (!event.getPlayer()
					.hasPermission(plugin.getChat().getConfig().getString("channels." + channel + ".use-perm"))) {
				event.getPlayer().sendMessage(plugin.getChat().getMessage("no-use-perm"));
				return;
			}
		}

		// Let's check cooldowns.
		if (!event.getPlayer().hasPermission(plugin.getChat().getConfig().getString("bypass-cooldown-perm"))) {
			if (!plugin.getChat().getCooldowns().isEmpty()) {
				if (plugin.getChat().getCooldowns().containsKey(event.getPlayer())) {
					if (!plugin.getChat().getCooldowns().get(event.getPlayer()).isEmpty()) {
						if (plugin.getChat().getCooldowns().get(event.getPlayer()).containsKey(channel)) {
							if ((System.currentTimeMillis()
									- plugin.getChat().getCooldowns().get(event.getPlayer()).get(channel))
									/ 1000 < plugin.getChat().getConfig().getInt("channels." + channel + ".cooldown")) {
								event.getPlayer().sendMessage(plugin.getChat().getMessage("cooldown").replace("{time}",
										((Long) (plugin.getChat().getConfig()
												.getInt("channels." + channel + ".cooldown")
												- (System.currentTimeMillis() - plugin.getChat().getCooldowns()
														.get(event.getPlayer()).get(channel)) / 1000)).toString()));
								return;
							}
						}
					}

				}
			}
		}

		// Let's have our message assembled.
		String msg = "";
		for (String s : parser) {
			msg += " " + s;
		}
		msg = msg.replaceFirst(" " + parser[0] + " ", "");

		// Let's strip any colors if the player doesn't have perms here.
		// This way we can replace colors properly in both chat & preprint events.
		msg = plugin.colorize(msg);
		if (!event.getPlayer()
				.hasPermission(plugin.getChat().getConfig().getString("channels." + channel + ".color-code-perm"))) {
			msg = ChatColor.stripColor(msg);
		}

		// Everything's going well. Let's call an ORPChatEvent.

		final String fmsg = msg;
		final String fchannel = channel;

		plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
			@Override
			public void run() {

				ORPChatEvent chatevent = new ORPChatEvent(event.getPlayer(), fmsg, fchannel);
				plugin.getServer().getPluginManager().callEvent(chatevent);

				// Let's see if anyone cancelled this event...
				if (!chatevent.isCancelled()) {

					// All good! We can now send the message in the correct channel!
					plugin.getChat().sendMessage(event.getPlayer(), chatevent.getMessage(), chatevent.getChannel());

				}

			}
		});

	}
	
	// Highest priority because - it has the final say: "check if event is cancelled by someone else first"
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChatMessageInDefaultChannel(AsyncPlayerChatEvent event) {

		// Fast check to make sure there is a default channel
		if (!plugin.getChat().getConfig().isSet("default")) {
			return;
		}

		// Check if event is cancelled by someone else first.
		if (event.isCancelled()) {
			return;
		}

		// All good, let's do this.
		event.setCancelled(true);

		// Let's grab our default channel from config for convenience.
		String channel = plugin.getChat().getConfig().getString("default");

		// Let's check permissions.
		if (plugin.getChat().getConfig().isSet("channels." + channel + ".use-perm")) {
			if (!event.getPlayer()
					.hasPermission(plugin.getChat().getConfig().getString("channels." + channel + ".use-perm"))) {
				event.getPlayer().sendMessage(plugin.getChat().getMessage("no-use-perm"));
				return;
			}
		}

		// Let's check cooldowns.
		if (!event.getPlayer().hasPermission(plugin.getChat().getConfig().getString("bypass-cooldown-perm"))) {
			if (!plugin.getChat().getCooldowns().isEmpty()) {
				if (plugin.getChat().getCooldowns().containsKey(event.getPlayer())) {
					if (!plugin.getChat().getCooldowns().get(event.getPlayer()).isEmpty()) {
						if (plugin.getChat().getCooldowns().get(event.getPlayer()).containsKey(channel)) {
							if ((System.currentTimeMillis()
									- plugin.getChat().getCooldowns().get(event.getPlayer()).get(channel))
									/ 1000 < plugin.getChat().getConfig().getInt("channels." + channel + ".cooldown")) {
								event.getPlayer().sendMessage(plugin.getChat().getMessage("cooldown").replace("{time}",
										((Long) (plugin.getChat().getConfig()
												.getInt("channels." + channel + ".cooldown")
												- (System.currentTimeMillis() - plugin.getChat().getCooldowns()
														.get(event.getPlayer()).get(channel)) / 1000)).toString()));
								return;
							}
						}
					}

				}
			}
		}

		// Let's strip any colors if the player doesn't have perms here.
		// This way we can replace colors properly in both chat & preprint events.
		String msg = plugin.colorize(event.getMessage());
		if (!event.getPlayer()
				.hasPermission(plugin.getChat().getConfig().getString("channels." + channel + ".color-code-perm"))) {
			msg = ChatColor.stripColor(msg);
		}

		// Everything's going well. Let's call an ORPChatEvent.

		final String fmsg = msg;

		plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
			@Override
			public void run() {
				ORPChatEvent chatevent = new ORPChatEvent(event.getPlayer(), fmsg, channel);
				plugin.getServer().getPluginManager().callEvent(chatevent);

				// Let's see if anyone cancelled this event...
				if (!chatevent.isCancelled()) {

					// All good! We can now send the message in the correct channel!
					plugin.getChat().sendMessage(event.getPlayer(), chatevent.getMessage(), chatevent.getChannel());

				}
			}
		});

	}

	@EventHandler
	public void onChatMessageSent(ORPChatEvent event) {

		plugin.getLogger()
				.info(event.getPlayer().getName() + " in "
						+ plugin.getChat().getConfig().getString("channels." + event.getChannel() + ".display-name")
						+ ": " + event.getMessage());

	}

}
