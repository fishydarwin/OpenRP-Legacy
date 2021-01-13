package openrp.chat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Is ran right before a player is sent an OpenRP Chat message in their chat.
 * @author Darwin Jonathan
 *
 */
public class ORPChatPreprintEvent extends Event implements Cancellable {

	private boolean isCancelled;

	private final Player player;
	private String message;
	private String channel;
	private final Player sender;

	public ORPChatPreprintEvent(Player player, String message, String channel, Player sender) {
		this.player = player;
		this.message = message;
		this.channel = channel;
		this.sender = sender;
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
	
	public Player getSender() {
		return sender;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

}
