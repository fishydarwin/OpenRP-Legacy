package openrp.descriptions.events;

import java.util.UUID;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Describes a person sending an OpenRP Chat message in a chat channel.
 * @author Darwin Jonathan
 *
 */
public class ORPDescriptionsChangeEvent extends Event implements Cancellable {

	private boolean isCancelled;

	private final UUID uuid;
	private String field;
	private String value;

	public ORPDescriptionsChangeEvent(UUID uuid, String field, String value) {
		this.uuid = uuid;
		this.field = field;
		this.value = value;
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

	public UUID getUUID() {
		return uuid;
	}

	public String getField() {
		return field;
	}

	public String getValue() {
		return value;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setValue(String value) {
		this.value = value;
	}

}