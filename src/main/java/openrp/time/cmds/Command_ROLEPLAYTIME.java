package openrp.time.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import openrp.OpenRP;
import openrp.time.utils.TimeHandler;
import openrp.utils.NumberParser;

public class Command_ROLEPLAYTIME implements CommandExecutor, TabCompleter {

	private OpenRP plugin;

	public Command_ROLEPLAYTIME(OpenRP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		/* No longer required. See Command#setPermission();
		if (!sender.hasPermission(plugin.getTime().getConfig().getString("manage-perm"))) {
			sender.sendMessage(plugin.getTime().getMessage("no-perm"));
			return true;
		}
		*/

		if (args.length != 7) {
			sender.sendMessage(plugin.getTime().getMessage("invalid-use").replace("{usage}",
					label + " (second) (minute) (hour) (day) (month) (year) (world)"));
			return true;
		}

		if (!NumberParser.isCreatable(args[0]) || !NumberParser.isCreatable(args[1])
				|| !NumberParser.isCreatable(args[2]) || !NumberParser.isCreatable(args[3])
				|| !NumberParser.isCreatable(args[4]) || !NumberParser.isCreatable(args[5])) {
			sender.sendMessage(plugin.getTime().getMessage("invalid-use").replace("{usage}",
					label + " (second) (minute) (hour) (day) (month) (year) (world)"));
			return true;
		}

		if (plugin.getTime().getConfig().getStringList("disabled-worlds").contains(args[6])) {
			sender.sendMessage(plugin.getTime().getMessage("invalid-use").replace("{usage}",
					label + " (second) (minute) (hour) (day) (month) (year) (world)"));
			return true;
		} else {
			boolean check = false;
			for (World w : plugin.getServer().getWorlds()) {
				if (w.getName().equals(args[6])) {
					check = true;
					break;
				}
			}
			if (!check) {
				sender.sendMessage(plugin.getTime().getMessage("invalid-use").replace("{usage}",
						label + " (second) (minute) (hour) (day) (month) (year) (world)"));
				return true;
			}
		}

		int second = NumberParser.createInt(args[0]);
		int minute = NumberParser.createInt(args[1]);
		int hour = NumberParser.createInt(args[2]);
		int day = NumberParser.createInt(args[3]);
		int month = NumberParser.createInt(args[4]);
		int year = NumberParser.createInt(args[5]);

		if (second < 0) {
			sender.sendMessage(plugin.getTime().getMessage("wrong-time-input").replace("{comparison}", "second < 0"));
			return true;
		} else if (second > 59) {
			sender.sendMessage(plugin.getTime().getMessage("wrong-time-input").replace("{comparison}", "second > 59"));
			return true;
		}

		if (minute < 0) {
			sender.sendMessage(plugin.getTime().getMessage("wrong-time-input").replace("{comparison}", "minute < 0"));
			return true;
		} else if (minute > 59) {
			sender.sendMessage(plugin.getTime().getMessage("wrong-time-input").replace("{comparison}", "minute > 59"));
			return true;
		}

		if (hour < 0) {
			sender.sendMessage(plugin.getTime().getMessage("wrong-time-input").replace("{comparison}", "hour < 0"));
			return true;
		} else if (hour > 23) {
			sender.sendMessage(plugin.getTime().getMessage("wrong-time-input").replace("{comparison}", "hour > 23"));
			return true;
		}

		if (day < 1) {
			sender.sendMessage(plugin.getTime().getMessage("wrong-time-input").replace("{comparison}", "day < 1"));
			return true;
		} else if (day > 31) {
			sender.sendMessage(plugin.getTime().getMessage("wrong-time-input").replace("{comparison}", "day > 31"));
			return true;
		}

		if (month < 1) {
			sender.sendMessage(plugin.getTime().getMessage("wrong-time-input").replace("{comparison}", "month < 1"));
			return true;
		} else if (month > 12) {
			sender.sendMessage(plugin.getTime().getMessage("wrong-time-input").replace("{comparison}", "month > 12"));
			return true;
		}

		for (TimeHandler th : plugin.getTime().getTimes()) {
			if (th.getWorld().getName().equals(args[6])) {
				th.setSecond(second);
				th.setMinute(minute);
				th.setHour(hour);
				th.setDay(day);
				th.setMonth(month);
				th.setYear(year);
				break;
			}
		}

		sender.sendMessage(plugin.getTime().getMessage("changed-time").replace("{time}",
				args[2] + ":" + args[1] + ":" + args[0] + " , " + args[4] + "/" + args[3] + "/" + args[5]));

		return true;

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("roleplaytime")) {

			List<String> l = new ArrayList<String>();

			if (args.length == 1) {
				if (sender.hasPermission(plugin.getTime().getConfig().getString("manage-perm"))) {
					l.add("<second>");
				}
			} else if (args.length == 2) {
				if (sender.hasPermission(plugin.getTime().getConfig().getString("manage-perm"))) {
					l.add("<minute>");
				}
			} else if (args.length == 3) {
				if (sender.hasPermission(plugin.getTime().getConfig().getString("manage-perm"))) {
					l.add("<hour>");
				}
			} else if (args.length == 4) {
				if (sender.hasPermission(plugin.getTime().getConfig().getString("manage-perm"))) {
					l.add("<day>");
				}
			} else if (args.length == 5) {
				if (sender.hasPermission(plugin.getTime().getConfig().getString("manage-perm"))) {
					l.add("<month>");
				}
			} else if (args.length == 6) {
				if (sender.hasPermission(plugin.getTime().getConfig().getString("manage-perm"))) {
					l.add("<year>");
				}
			} else if (args.length == 7) {
				if (sender.hasPermission(plugin.getTime().getConfig().getString("manage-perm"))) {
					l.add("<world>");
				}
			}

			return l;

		}

		return null;

	}

}
