package openrp.time.expansions;

import openrp.OpenRP;
import openrp.time.ORPTime;
import openrp.time.utils.TimeHandler;

public class MVdW_Time {

	public MVdW_Time(OpenRP plugin, ORPTime time) {

		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orptime_world_second",
				new be.maximvdw.placeholderapi.PlaceholderReplacer() {
					@Override
					public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
						for (TimeHandler th : plugin.getTime().getTimes()) {
							if (th.getWorld().equals(event.getPlayer().getLocation().getWorld())) {
								String s = ((Integer) th.getSecond()).toString();
								s = th.getSecond() < 10 ? "0" + s : s;
								return s;
							}
						}
						return "";
					}
				});
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orptime_world_minute",
				new be.maximvdw.placeholderapi.PlaceholderReplacer() {
					@Override
					public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
						for (TimeHandler th : plugin.getTime().getTimes()) {
							if (th.getWorld().equals(event.getPlayer().getLocation().getWorld())) {
								String s = ((Integer) th.getMinute()).toString();
								s = th.getMinute() < 10 ? "0" + s : s;
								return s;
							}
						}
						return "";
					}
				});
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orptime_world_hour",
				new be.maximvdw.placeholderapi.PlaceholderReplacer() {
					@Override
					public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
						for (TimeHandler th : plugin.getTime().getTimes()) {
							if (th.getWorld().equals(event.getPlayer().getLocation().getWorld())) {
								String s = ((Integer) th.getHour()).toString();
								s = th.getHour() < 10 ? "0" + s : s;
								return s;
							}
						}
						return "";
					}
				});
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orptime_world_day",
				new be.maximvdw.placeholderapi.PlaceholderReplacer() {
					@Override
					public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
						for (TimeHandler th : plugin.getTime().getTimes()) {
							if (th.getWorld().equals(event.getPlayer().getLocation().getWorld())) {
								String s = ((Integer) th.getDay()).toString();
								s = th.getDay() < 10 ? "0" + s : s;
								return s;
							}
						}
						return "";
					}
				});
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orptime_world_month",
				new be.maximvdw.placeholderapi.PlaceholderReplacer() {
					@Override
					public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
						for (TimeHandler th : plugin.getTime().getTimes()) {
							if (th.getWorld().equals(event.getPlayer().getLocation().getWorld())) {
								String s = ((Integer) th.getMonth()).toString();
								s = th.getMonth() < 10 ? "0" + s : s;
								return s;
							}
						}
						return "";
					}
				});
		be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orptime_world_year",
				new be.maximvdw.placeholderapi.PlaceholderReplacer() {
					@Override
					public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
						for (TimeHandler th : plugin.getTime().getTimes()) {
							if (th.getWorld().equals(event.getPlayer().getLocation().getWorld())) {
								return ((Integer) th.getYear()).toString();
							}
						}
						return "";
					}
				});

		for (TimeHandler th : time.getTimes()) {
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin,
					"orptime_in_" + th.getWorld().getName() + "_second",
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							String s = ((Integer) th.getSecond()).toString();
							s = th.getSecond() < 10 ? "0" + s : s;
							return s;
						}
					});
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin,
					"orptime_in_" + th.getWorld().getName() + "_minute",
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							String s = ((Integer) th.getMinute()).toString();
							s = th.getMinute() < 10 ? "0" + s : s;
							return s;
						}
					});
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin,
					"orptime_in_" + th.getWorld().getName() + "_hour",
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							String s = ((Integer) th.getHour()).toString();
							s = th.getHour() < 10 ? "0" + s : s;
							return s;
						}
					});
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin,
					"orptime_in_" + th.getWorld().getName() + "_day",
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							String s = ((Integer) th.getDay()).toString();
							s = th.getDay() < 10 ? "0" + s : s;
							return s;
						}
					});
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin,
					"orptime_in_" + th.getWorld().getName() + "_month",
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							String s = ((Integer) th.getMonth()).toString();
							s = th.getMonth() < 10 ? "0" + s : s;
							return s;
						}
					});
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin,
					"orptime_in_" + th.getWorld().getName() + "_year",
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							return ((Integer) th.getYear()).toString();
						}
					});
		}

	}

}
