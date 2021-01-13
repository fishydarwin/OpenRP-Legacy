package openrp.descriptions.expansions;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import openrp.OpenRP;

public class PAPI_Descriptions extends PlaceholderExpansion {

	private OpenRP plugin;

	public PAPI_Descriptions(OpenRP plugin) {
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
		return "orpdesc";
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

		if (plugin.getDesc().getFields().contains(identifier)) {
			if (plugin.getDesc().isFieldSet(player, identifier)) {
				return plugin.getDesc().getUserdata().getString(player.getUniqueId().toString() + "." + identifier);
			} else {
				return plugin.getDesc().getConfig().getString("fields." + identifier + ".default-value");
			}
		}

		return null;
	}

}
