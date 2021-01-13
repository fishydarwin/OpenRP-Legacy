package openrp.chat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Describes a person sending an OpenRP Chat message in a chat channel.
 * @author Darwin Jonathan
 *
 */
public class ORPChatEvent extends Event implements Cancellable {

	private boolean isCancelled;

	private final Player player;
	private String message;
	private String channel;
	
	public ORPChatEvent(Player player, String message, String channel) {
		this.player = player;
		this.message = message;
		this.channel = channel;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public boolean isCancelled() {
		return this.isCancelled;
	}

	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public Player getPlayer() {
		return player;
	}

	public String getMessage() {
		return message;
	}

	public String getChannel() {
		return channel;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

}
