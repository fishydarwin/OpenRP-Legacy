package openrp.chat.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import openrp.OpenRP;
import openrp.chat.listeners.ToggleSwitchListener;

public class Command_CHANNELTOGGLE implements CommandExecutor, TabCompleter {

	private OpenRP plugin;

	public Command_CHANNELTOGGLE(OpenRP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(plugin.getChat().getMessage("run-as-player"));
			return true;
		}

		if (!ToggleSwitchListener.usingToggles()) {
			sender.sendMessage(plugin.getChat().getMessage("toggle-switch-disabled"));
			return true;
		}

		if (args.length != 1) {
			sender.sendMessage(plugin.getChat().getMessage("invalid-use").replace("{usage}", label + " (channel)"));
			return true;
		}

		if (!ToggleSwitchListener.getToggleChannels().contains(args[0])) {
			sender.sendMessage(plugin.getChat().getMessage("invalid-channel").replace("{channels}",
					ToggleSwitchListener.getSwitchChannels().toString().replace("[", "").replace("]", "")));
		}

		List<String> ch = ToggleSwitchListener.getToggleList((Player) sender);
		if (!ch.contains(args[0])) {
			ch.add(args[0]);
			ToggleSwitchListener.setToggleList((Player) sender, ch);
			sender.sendMessage(plugin.getChat().getMessage("toggled-off").replace("{channel}", args[0]));
		} else {
			ch.remove(args[0]);
			ToggleSwitchListener.setToggleList((Player) sender, ch);
			sender.sendMessage(plugin.getChat().getMessage("toggled-on").replace("{channel}", args[0]));
		}

		return true;
	}

	private List<String> emptyArrayList = new ArrayList<>();

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length <= 1) {
			List<String> l = ToggleSwitchListener.getToggleChannels();
			return l;
		}
		return emptyArrayList;
	}

}
