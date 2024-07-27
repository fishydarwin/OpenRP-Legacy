package openrp.chat.expansions;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import openrp.OpenRP;
import openrp.chat.listeners.ToggleSwitchListener;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;

public class PAPI_Chat extends PlaceholderExpansion {
    private OpenRP plugin;
    public PAPI_Chat(OpenRP plugin) {
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
    public @NotNull String getIdentifier() { return "orpchat"; }

    @Override
    public @NotNull String getAuthor() { return plugin.getDescription().getAuthors().toString(); }

    @Override
    public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        String returnString = plugin.getChat().getConfig().getString("default");


        switch(identifier){
            case "switched":
                if(ToggleSwitchListener.getSwitchChannel(player) == null){
                    returnString = plugin.getChat().getConfig().getString("default");
                    returnString = PlaceholderAPI.setPlaceholders(player, returnString);
                    return returnString;
                }
                else{
                    returnString = ToggleSwitchListener.getSwitchChannel(player);
                    returnString = PlaceholderAPI.setPlaceholders(player, returnString);
                    return returnString;
                }
            case "swverbose":
                if(ToggleSwitchListener.getSwitchChannel(player) == null){
                    returnString = plugin.getChat().getConfig().getString("channels." + plugin.getConfig().getString("default") + ".display-name");
                    returnString = PlaceholderAPI.setPlaceholders(player, returnString);
                    return returnString;
                }
                else {
                    returnString = plugin.getChat().getConfig().getString("channels." + ToggleSwitchListener.getSwitchChannel(player) + ".display-name");
                    returnString = PlaceholderAPI.setPlaceholders(player, returnString);
                    return returnString;
                }
            default:
                return plugin.getChat().getConfig().getString("default");
        }
    }
}
