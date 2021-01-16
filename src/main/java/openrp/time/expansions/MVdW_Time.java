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
								return ((Integer) th.getSecond()).toString();
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
								return ((Integer) th.getMinute()).toString();
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
								return ((Integer) th.getHour()).toString();
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
								return ((Integer) th.getDay()).toString();
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
								return ((Integer) th.getMonth()).toString();
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
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orptime_in_" + th.getWorld().getName() + "_second",
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							return ((Integer) th.getSecond()).toString();
						}
					});
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orptime_in_" + th.getWorld().getName() + "_minute",
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							return ((Integer) th.getMinute()).toString();
						}
					});
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orptime_in_" + th.getWorld().getName() + "_hour",
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							return ((Integer) th.getHour()).toString();
						}
					});
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orptime_in_" + th.getWorld().getName() + "_day",
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							return ((Integer) th.getDay()).toString();
						}
					});
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orptime_in_" + th.getWorld().getName() + "_month",
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							return ((Integer) th.getMonth()).toString();
						}
					});
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orptime_in_" + th.getWorld().getName() + "_year",
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							return ((Integer) th.getYear()).toString();
						}
					});
		}

	}

}
