package openrp.rolls.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Is ran right before a player is sent an OpenRP Rolls output in their chat.
 * 
 * @author Darwin Jonathan
 *
 */
public class ORPRollsOutputEvent extends Event implements Cancellable {

	private boolean isCancelled;

	private final Player player;
	private Integer min;
	private Integer max;

	public ORPRollsOutputEvent(Player player, Integer min, Integer max) {
		this.player = player;
		this.min = min;
		this.max = max;
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

	public Integer getMinimum() {
		return min;
	}

	public Integer getMaximum() {
		return max;
	}

	public void setMinimum(Integer min) {
		this.min = min;
	}

	public void setMaximum(Integer max) {
		this.max = max;
	}

}
