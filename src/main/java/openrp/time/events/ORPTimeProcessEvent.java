package openrp.time.events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Is ran right before a player is sent an OpenRP Rolls output in their chat.
 * 
 * @author Darwin Jonathan
 *
 */
public class ORPTimeProcessEvent extends Event {

	private final World world;
	private Integer second;
	private Integer minute;
	private Integer hour;
	private Integer day;
	private Integer month;
	private Integer year;

	public ORPTimeProcessEvent(World world, Integer second, Integer minute, Integer hour, Integer day, Integer month, Integer year) {
		this.world = world;
		this.second = second;
		this.minute = minute;
		this.hour = hour;
		this.day = day;
		this.month = month;
		this.year = year;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	public World getWorld() {
		return world;
	}

	public Integer getSecond() {
		return second;
	}
	
	public Integer getMinute() {
		return minute;
	}

	public Integer getHour() {
		return hour;
	}
	
	public Integer getDay() {
		return day;
	}
	
	public Integer getMonth() {
		return month;
	}
	
	public Integer getYear() {
		return year;
	}

	public void setSecond(Integer second) {
		if (second >= 60 || second < 0) {
			this.second = 0;
			return;
		}
		this.second = second;
	}
	
	public void setMinute(Integer minute) {
		if (minute >= 60 || minute < 0) {
			this.minute = 0;
			return;
		}
		this.minute = minute;
	}
	
	public void setHour(Integer hour) {
		if (hour >= 24 || hour < 0) {
			this.hour = 0;
			return;
		}
		this.hour = hour;
	}
	
	public void setDay(Integer day) {
		if (day >= 32 || day < 1) {
			this.day = 1;
			return;
		}
		this.day = day;
	}
	
	public void setMonth(Integer month) {
		if (month >= 13 || month < 1) {
			this.month = 1;
			return;
		}
		this.month = month;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
}
