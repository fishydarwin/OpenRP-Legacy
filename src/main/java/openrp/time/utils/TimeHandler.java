package openrp.time.utils;

import org.bukkit.World;

import openrp.OpenRP;

public class TimeHandler {

	private OpenRP plugin;
	private final World world;

	private int second = 0;
	private int minute = 0;
	private int hour = 0;

	private int day = 1;
	private int month = 1;
	private int year = 0;

	public TimeHandler(OpenRP plugin, World world, int second, int minute, int hour, int day, int month, int year) {
		this.plugin = plugin;
		this.world = world;
		this.second = second;
		this.minute = minute;
		this.hour = hour;
		this.day = day;
		this.month = month;
		this.year = year;
	}

	public void updateTimesInData() {
		plugin.getTime().getTimedata().set(world.getName() + ".second", second);
		plugin.getTime().getTimedata().set(world.getName() + ".minute", minute);
		plugin.getTime().getTimedata().set(world.getName() + ".hour", hour);
		plugin.getTime().getTimedata().set(world.getName() + ".day", day);
		plugin.getTime().getTimedata().set(world.getName() + ".month", month);
		plugin.getTime().getTimedata().set(world.getName() + ".year", year);
	}
	
	public World getWorld() {
		return world;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getSecond() {
		return second;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getMinute() {
		return minute;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getHour() {
		return hour;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getDay() {
		return day;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getMonth() {
		return month;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getYear() {
		return year;
	}

}
