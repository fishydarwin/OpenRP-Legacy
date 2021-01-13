package openrp.descriptions.expansions;

import openrp.OpenRP;
import openrp.descriptions.ORPDescriptions;

public class MVdW_Descriptions {

	public MVdW_Descriptions(OpenRP plugin, ORPDescriptions desc) {

		for (String s : desc.getFields()) {
			be.maximvdw.placeholderapi.PlaceholderAPI.registerPlaceholder(plugin, "orpdesc_" + s,
					new be.maximvdw.placeholderapi.PlaceholderReplacer() {
						@Override
						public String onPlaceholderReplace(be.maximvdw.placeholderapi.PlaceholderReplaceEvent event) {
							return (desc.getUserdata().getString(event.getPlayer().getUniqueId().toString() + s));
						}
					});
		}

	}

}
