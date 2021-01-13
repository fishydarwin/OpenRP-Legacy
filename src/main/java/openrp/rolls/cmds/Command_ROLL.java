package openrp.rolls.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import openrp.OpenRP;
import openrp.rolls.events.ORPRollsOutputEvent;
import openrp.utils.NumberParser;

public class Command_ROLL implements CommandExecutor, TabCompleter {

	private OpenRP plugin;

	public Command_ROLL(OpenRP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("Please run this command as a player!");
			return true;
		}

		Player player = (Player) sender;

		/* No longer required. See Command#setPermission();
		if (!player.hasPermission(plugin.getRolls().getConfig().getString("use-perm"))) {
			player.sendMessage(plugin.getRolls().getMessage("no-perm"));
			return true;
		}
		*/

		if (args.length == 0) {

			ArrayList<Player> recipients = new ArrayList<Player>();
			if (plugin.getRolls().getConfig().isSet("message.range")) {
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (!p.equals(player)) {
						if (player.getWorld().equals(p.getWorld())) {
							if (player.getLocation().distance(p.getLocation()) <= plugin.getRolls().getConfig()
									.getInt("message.range")) {
								recipients.add(p);
							}
						}
					}
				}
			} else {
				recipients = new ArrayList<Player>(plugin.getServer().getOnlinePlayers());
				recipients.remove(player);
			}

			ORPRollsOutputEvent preprintevent = new ORPRollsOutputEvent(player,
					plugin.getRolls().getConfig().getInt("default-rolls.min"),
					plugin.getRolls().getConfig().getInt("default-rolls.max"));
			plugin.getServer().getPluginManager().callEvent(preprintevent);

			if (!preprintevent.isCancelled()) {

				Integer output = plugin.getRolls().roll(preprintevent.getMinimum(), preprintevent.getMaximum());

				String messageToSend = plugin.parsePlaceholders(
						plugin.colorize(plugin.getRolls().getConfig().getString("message.text")), player,
						plugin.getRolls().getStandardHashMap(player, output, preprintevent.getMaximum().toString()));

				plugin.getLogger()
						.info(player.getName() + " rolled " + output.toString() + " / " + preprintevent.getMaximum());

				player.sendMessage(messageToSend);
				
				for (Player p : recipients) {

					p.sendMessage(messageToSend);

				}

			}

		} else {
			if (args.length == 1) {

				if (!player.hasPermission(plugin.getRolls().getConfig().getString("alternative-rolls-use-perm"))) {
					player.sendMessage(plugin.getRolls().getMessage("no-perm"));
					return true;
				}

				Integer min = plugin.getRolls().getConfig().getInt("default-rolls.min");
				Integer max = plugin.getRolls().getConfig().getInt("default-rolls.max");
				if (!args[0].toLowerCase().contains("-")) {
					if (!NumberParser.isCreatable(args[0])) {
						player.sendMessage(plugin.getRolls().getMessage("invalid-use").replace("{usage}",
								label + " (max/min-max)"));
						return true;
					} else {
						max = NumberParser.createInt(args[0]);
					}
				} else {
					int matchCount = 0;
					for (int i = 0; i < args[0].length(); i++) {
						if (args[0].charAt(i) == '-') {
							matchCount++;
						}
					}
					if (matchCount == 1) {
						String[] parser = args[0].split("-");
						if (!NumberParser.isCreatable(parser[0]) || !NumberParser.isCreatable(parser[1])) {
							player.sendMessage(plugin.getRolls().getMessage("invalid-use").replace("{usage}",
									label + " (max/min-max)"));
							return true;
						} else {
							min = NumberParser.createInt(parser[0]);
							max = NumberParser.createInt(parser[1]);
							if (min >= max) {
								player.sendMessage(plugin.getRolls().getMessage("invalid-use").replace("{usage}",
										label + " (max/min-max)"));
								return true;
							}
						}
					} else {
						player.sendMessage(plugin.getRolls().getMessage("invalid-use").replace("{usage}",
								label + " (max/min-max)"));
						return true;
					}
				}

				if (plugin.getRolls().getConfig().getInt("alternative-rolls.min") > min
						|| plugin.getRolls().getConfig().getInt("alternative-rolls.max") < max) {
					player.sendMessage(plugin.getRolls().getMessage("limits")
							.replace("{min}", plugin.getRolls().getConfig().getString("alternative-rolls.min"))
							.replace("{max}", plugin.getRolls().getConfig().getString("alternative-rolls.max")));
					return true;
				}

				ArrayList<Player> recipients = new ArrayList<Player>();
				if (plugin.getRolls().getConfig().isSet("message.range")) {
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						if (!p.equals(player)) {
							if (player.getWorld().equals(p.getWorld())) {
								if (player.getLocation().distance(p.getLocation()) <= plugin.getRolls().getConfig()
										.getInt("message.range")) {
									recipients.add(p);
								}
							}
						}
					}
				} else {
					recipients = new ArrayList<Player>(plugin.getServer().getOnlinePlayers());
					recipients.remove(player);
				}

				ORPRollsOutputEvent preprintevent = new ORPRollsOutputEvent(player, min, max);
				plugin.getServer().getPluginManager().callEvent(preprintevent);

				if (!preprintevent.isCancelled()) {

					Integer output = plugin.getRolls().roll(preprintevent.getMinimum(), preprintevent.getMaximum());

					String messageToSend = "";

					if (!args[0].toLowerCase().contains("-")) {
						messageToSend = plugin.parsePlaceholders(
								plugin.colorize(plugin.getRolls().getConfig().getString("message.text")), player,
								plugin.getRolls().getStandardHashMap(player, output,
										preprintevent.getMaximum().toString()));
						plugin.getLogger().info(
								player.getName() + " rolled " + output.toString() + " / " + preprintevent.getMaximum());
					} else {
						messageToSend = plugin.parsePlaceholders(
								plugin.colorize(plugin.getRolls().getConfig().getString("message.text")), player,
								plugin.getRolls().getStandardHashMap(player, output,
										preprintevent.getMinimum() + "-" + preprintevent.getMaximum()));
						plugin.getLogger().info(player.getName() + " rolled " + output.toString() + " / "
								+ preprintevent.getMinimum() + "-" + preprintevent.getMaximum());
					}

					player.sendMessage(messageToSend);

					for (Player p : recipients) {

						p.sendMessage(messageToSend);

					}

				}

			} else {
				player.sendMessage(
						plugin.getRolls().getMessage("invalid-use").replace("{usage}", label + " (max/min-max)"));
			}
		}

		return true;

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		List<String> l = new ArrayList<String>();
		if (cmd.getName().equalsIgnoreCase("roll")) {
			if (sender.hasPermission(plugin.getRolls().getConfig().getString("alternative-rolls-use-perm"))) {
				if (args.length <= 1) {
					l.add("<max>");
					l.add("<min-max>");
					return l;
				}
			}
			return l;
		}

		return null;

	}

}
