package openrp.chat.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.ChatColor;
import openrp.OpenRP;
import openrp.chat.events.ORPChatEvent;

public class ActionListener implements Listener {
	
	private OpenRP plugin;
	
	public ActionListener(OpenRP plugin) {
		this.plugin = plugin;
		channelsWithActions = plugin.getChat().getConfig().getStringList("channels-with-actions");
	}
	
	List<String> channelsWithActions = new ArrayList<>();

	private String SPECIAL_CHARACTER = Pattern.quote("*"); // fixes regex issues, dont ask
	@EventHandler
	public void onChatMessage(ORPChatEvent event) {
	  if (!channelsWithActions.contains(event.getChannel())) { return; }
	  if (!event.getMessage().contains(SPECIAL_CHARACTER)) { return; }
	  if (event.getMessage().split(SPECIAL_CHARACTER).length <= 2) { return; }

	  String assemble = "";
	  { // this { } is for better memory
	    boolean isInsideAction = false;
	    for (String substring : event.getMessage().split(SPECIAL_CHARACTER)) {
	      if (isInsideAction == false) {
	        assemble += " " + ChatColor.WHITE + substring;
	      } else {
	        assemble += " " + ChatColor.YELLOW + SPECIAL_CHARACTER + substring + SPECIAL_CHARACTER; 
	      }
	      isInsideAction = !isInsideAction; // flip value
	    }
	  }
	  assemble = assemble.replaceFirst(" ", ""); // don't forget about this
	  event.setMessage(assemble);
	}

}
