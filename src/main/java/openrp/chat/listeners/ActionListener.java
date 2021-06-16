package openrp.chat.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import openrp.OpenRP;
import openrp.chat.ORPChat;
import openrp.chat.events.ORPChatEvent;

/**
 * A listener built into OpenRP that handles *action*-type messages. This is due
 * to the large amount of requests for doing something like this.
 * 
 * @author Darwin Jonathan
 *
 */
public class ActionListener implements Listener {

	public ActionListener(OpenRP plugin, ORPChat chat) {
		channelsWithActions = new HashMap<>();
		for (String channel : chat.getChannels().keySet()) {
			if (chat.getConfig().isSet("channels." + channel + ".action")) {

				plugin.getLogger().info("Channel " + channel + " inhibits action behaviour. Implementing. . .");

				String[] list = new String[3];

				String symbol = chat.getConfig().getString("channels." + channel + ".action.symbol", "*");

				String prefix = plugin.colorize(chat.getConfig()
						.getString("channels." + channel + ".action.replacement.outside-of-actions", "&f"), false);
				String suffix = plugin.colorize(
						chat.getConfig().getString("channels." + channel + ".action.replacement.for-actions", "&e*"), false);

				list[0] = symbol;
				list[1] = prefix;
				list[2] = suffix;

				channelsWithActions.put(channel, list);
			}
		}
	}

	Map<String, String[]> channelsWithActions = new HashMap<>();

	@EventHandler
	public void onChatMessage(ORPChatEvent event) {
		String channel = event.getChannel();
		if (ToggleSwitchListener.usingSwitches()) {
			String switchChannel = ToggleSwitchListener.getSwitchChannel(event.getPlayer());
			if (switchChannel != null) {
				channel = switchChannel;
			}
		}
		if (!channelsWithActions.containsKey(channel)) {
			return;
		}

		if (!event.getMessage().contains(channelsWithActions.get(channel)[0])) {
			return;
		}

		// Ensures fixed regular expression pattern
		String SPECIAL_CHARACTER = Pattern.quote(channelsWithActions.get(channel)[0]);
		{
			String test = event.getMessage() + " ";
			if (test.split(SPECIAL_CHARACTER).length <= 2) {
				return;
			}
		}

		String assemble = "";
		{
			boolean isInsideAction = false;
			String prefix = channelsWithActions.get(channel)[1];
			String suffix = channelsWithActions.get(channel)[2];
			for (String substring : event.getMessage().split(SPECIAL_CHARACTER)) {
				if (isInsideAction == false) {
					assemble += prefix + substring;
				} else {
					assemble += suffix + substring + suffix;
				}
				isInsideAction = !isInsideAction; // Flip value here
			}
		}
		event.setMessage(assemble);
	}

}
