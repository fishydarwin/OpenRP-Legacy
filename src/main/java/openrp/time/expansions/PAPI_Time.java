package openrp.time.expansions;

import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import openrp.OpenRP;
import openrp.time.utils.TimeHandler;

public class PAPI_Time extends PlaceholderExpansion {

	private OpenRP plugin;

	public PAPI_Time(OpenRP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String getAuthor() {
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public String getIdentifier() {
		return "orptime";
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {

		if (player == null) {
			return "";
		}

		switch (identifier) {
		default:
			if (identifier.startsWith("in_")) {
				if (identifier.split("_").length == 3) {
					String[] split = identifier.split(Pattern.quote("_"));
					if (plugin.getServer().getWorld(split[1]) != null) {
						switch (split[2]) {
						default:
							return null;
						case "second":
							for (TimeHandler th : plugin.getTime().getTimes()) {
								if (th.getWorld().getName().equals(split[1])) {
									return ((Integer) th.getSecond()).toString();
								}
							}
							return "";
						case "minute":
							for (TimeHandler th : plugin.getTime().getTimes()) {
								if (th.getWorld().getName().equals(split[1])) {
									return ((Integer) th.getMinute()).toString();
								}
							}
							return "";
						case "hour":
							for (TimeHandler th : plugin.getTime().getTimes()) {
								if (th.getWorld().getName().equals(split[1])) {
									return ((Integer) th.getHour()).toString();
								}
							}
							return "";
						case "day":
							for (TimeHandler th : plugin.getTime().getTimes()) {
								if (th.getWorld().getName().equals(split[1])) {
									return ((Integer) th.getDay()).toString();
								}
							}
							return "";
						case "month":
							for (TimeHandler th : plugin.getTime().getTimes()) {
								if (th.getWorld().getName().equals(split[1])) {
									return ((Integer) th.getMonth()).toString();
								}
							}
							return "";
						case "year":
							for (TimeHandler th : plugin.getTime().getTimes()) {
								if (th.getWorld().getName().equals(split[1])) {
									return ((Integer) th.getYear()).toString();
								}
							}
							return "";
						}
					}
				}
			}
			return null;
		case "world_second":
			for (TimeHandler th : plugin.getTime().getTimes()) {
				if (th.getWorld().equals(player.getLocation().getWorld())) {
					return ((Integer) th.getSecond()).toString();
				}
			}
			return "";
		case "world_minute":
			for (TimeHandler th : plugin.getTime().getTimes()) {
				if (th.getWorld().equals(player.getLocation().getWorld())) {
					return ((Integer) th.getMinute()).toString();
				}
			}
			return "";
		case "world_hour":
			for (TimeHandler th : plugin.getTime().getTimes()) {
				if (th.getWorld().equals(player.getLocation().getWorld())) {
					return ((Integer) th.getHour()).toString();
				}
			}
			return "";
		case "world_day":
			for (TimeHandler th : plugin.getTime().getTimes()) {
				if (th.getWorld().equals(player.getLocation().getWorld())) {
					return ((Integer) th.getDay()).toString();
				}
			}
			return "";
		case "world_month":
			for (TimeHandler th : plugin.getTime().getTimes()) {
				if (th.getWorld().equals(player.getLocation().getWorld())) {
					return ((Integer) th.getMonth()).toString();
				}
			}
			return "";
		case "world_year":
			for (TimeHandler th : plugin.getTime().getTimes()) {
				if (th.getWorld().equals(player.getLocation().getWorld())) {
					return ((Integer) th.getYear()).toString();
				}
			}
			return "";
		}
	}

}
