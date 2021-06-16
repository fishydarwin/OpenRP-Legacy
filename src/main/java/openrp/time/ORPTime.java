package openrp.time;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import openrp.OpenRP;
import openrp.time.cmds.Command_ROLEPLAYTIME;
import openrp.time.events.ORPTimeProcessEvent;
import openrp.time.utils.TimeHandler;

/**
 * OpenRP Time API instance. Can be accessed from the OpenRP main class via
 * getTime().
 * 
 * @author Darwin Jonathan
 *
 */
public class ORPTime {

	private OpenRP plugin;
	private File directory;

	private static final long HOUR_0 = 18000;
	private ArrayList<TimeHandler> times = new ArrayList<TimeHandler>();
	private HashMap<World, BukkitTask> scheduleTracker = new HashMap<World, BukkitTask>();

	private FileConfiguration config;
	private FileConfiguration messages;
	private FileConfiguration timedata;

	public ORPTime(OpenRP plugin) {
		this.plugin = plugin;
	}

	/**
	 * Register all times for OpenRP Time to be able to use.
	 */
	public void registerTimes() {

		for (World w : plugin.getServer().getWorlds()) {

			boolean skip = false;
			if (getConfig().isSet("disabled-worlds")) {
				if (getConfig().getStringList("disabled-worlds").contains(w.getName())) {
					plugin.getLogger().info("World " + w.getName() + " ignores Time. Skipping. . .");
					skip = true;
				}
			}

			if (!skip) {

				if (getTimedata().contains(w.getName())) {
					times.add(new TimeHandler(plugin, w, getTimedata().getInt(w.getName() + ".second"),
							getTimedata().getInt(w.getName() + ".minute"), getTimedata().getInt(w.getName() + ".hour"),
							getTimedata().getInt(w.getName() + ".day"), getTimedata().getInt(w.getName() + ".month"),
							getTimedata().getInt(w.getName() + ".year")));
				} else {
					if (getConfig().isSet("default-time")) {
						times.add(new TimeHandler(plugin, w, getConfig().getInt("default-time.second"),
								getConfig().getInt("default-time.minute"), getConfig().getInt("default-time.hour"),
								getConfig().getInt("default-time.day"), getConfig().getInt("default-time.month"),
								getConfig().getInt("default-time.year")));
					}
				}

				plugin.getLogger().info("Added " + w.getName() + " to Time.");
				if (getConfig().getBoolean("handle-time")) {
					if (w.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE)) {
						plugin.getLogger().warning("Gamerule doDaylightCycle for " + w.getName()
								+ " is set to true. Please make sure it's set to false, or change 'handle-time' to false in config!");
					}
				}

			}

		}

	}

	/**
	 * Get all of OpenRP Time's times for all registered worlds.
	 * 
	 * @return An ArrayList TimeHandler objects for each registered world.
	 */
	public ArrayList<TimeHandler> getTimes() {
		return times;
	}

	/**
	 * Restarts the time handler for OpenRP Time. This handles all the time
	 * operations, including updating times, sending bossbars/actionbars, and
	 * changing the worlds' times if needed.
	 */
	public void restartTimeHandler() {

		if (!scheduleTracker.isEmpty()) {
			for (World w : scheduleTracker.keySet()) {
				scheduleTracker.get(w).cancel();
			}
			scheduleTracker = new HashMap<World, BukkitTask>();
		}

		for (TimeHandler th : getTimes()) {

			scheduleTracker.put(th.getWorld(), plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
				@Override
				public void run() {

					int second = th.getSecond();
					int minute = th.getMinute();
					int hour = th.getHour();
					int day = th.getDay();
					int month = th.getMonth();
					int year = th.getYear();

					if (getConfig().getBoolean("handle-time")) {

						second += (int) Math.ceil(getConfig().getInt("run-time-event-every-in-ticks")
								/ getConfig().getInt("one-second-in-ticks-is"));

						if (second >= 60) {

							second = second - 60;
							minute++;

							if (minute >= 60) {

								minute = 0;
								hour++;

								if (hour >= 24) {

									hour = 0;
									day++;

									if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
											|| month == 10 || month == 12) {
										if (day >= 32) {

											day = 1;
											month++;

											if (month >= 13) {

												month = 1;
												year++;

											}

										}
									} else if (month == 4 || month == 6 || month == 9 || month == 11) {
										if (day >= 31) {

											day = 1;
											month++;

											if (month >= 13) {

												month = 1;
												year++;

											}

										}
									} else if (month == 2) {
										if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
											if (day >= 30) {

												day = 1;
												month++;

												if (month >= 13) {

													month = 1;
													year++;

												}

											}
										} else {
											if (day >= 29) {

												day = 1;
												month++;

												if (month >= 13) {

													month = 1;
													year++;

												}

											}
										}
									}

								}

							}

						}

					} else {

						second = (int) Math.floor(((th.getWorld().getTime() / 1000.0) % 1 * 60) % 1 * 60);

						minute = (int) Math.floor((th.getWorld().getTime() / 1000.0) % 1 * 60);

						hour = (int) (6 + th.getWorld().getTime() / 1000);
						if (hour >= 24) {
							hour = hour - 24;
						}

						if (hour < th.getHour()) {

							day++;

							if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10
									|| month == 12) {
								if (day >= 32) {

									day = 1;
									month++;

									if (month >= 13) {

										month = 1;
										year++;

									}

								}
							} else if (month == 4 || month == 6 || month == 9 || month == 11) {
								if (day >= 31) {

									day = 1;
									month++;

									if (month >= 13) {

										month = 1;
										year++;

									}

								}
							} else if (month == 2) {
								if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
									if (day >= 30) {

										day = 1;
										month++;

										if (month >= 13) {

											month = 1;
											year++;

										}

									}
								} else {
									if (day >= 29) {

										day = 1;
										month++;

										if (month >= 13) {

											month = 1;
											year++;

										}

									}
								}
							}

						}

					}

					ORPTimeProcessEvent event = new ORPTimeProcessEvent(th.getWorld(), second, minute, hour, day, month,
							year);
					plugin.getServer().getPluginManager().callEvent(event);

					th.setSecond(event.getSecond());
					th.setMinute(event.getMinute());
					th.setHour(event.getHour());
					th.setDay(event.getDay());
					th.setMonth(event.getMonth());
					th.setYear(event.getYear());

					if (getConfig().isSet("format")) {
						for (Player p : th.getWorld().getPlayers()) {
							p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
									TextComponent.fromLegacyText(
											plugin.colorize(plugin.parsePlaceholders(getConfig().getString("format"), p,
													getStandardHashMap(p, th.getSecond(), th.getMinute(), th.getHour(),
															th.getDay(), th.getMonth(), th.getYear())), false)));
						}
					}

					if (getConfig().getBoolean("handle-time")) {
						th.getWorld().setTime(
								calculateWorldTimeFromHandlerTime(th.getHour(), th.getMinute(), th.getSecond()));
					}

				}
			}, 0L, getConfig().getInt("run-time-event-every-in-ticks")));

		}

	}

	/**
	 * A convenient method to convert hour:minute:second time that is used in OpenRP
	 * Time to ticks.
	 * 
	 * @return The world's time in ticks.
	 */
	public long calculateWorldTimeFromHandlerTime(int hour, int minute, int second) {
		long l = ORPTime.HOUR_0;
		l += 1000 * hour;
		l += Math.round(16.6666 * minute);
		l += Math.round(0.2777 * second);
		if (l >= 24000) {
			l = l - 24000;
		}
		return l;
	}

	/**
	 * Returns the day of the week as text from the current day in the month of the
	 * specific year.
	 * 
	 * @param day   - the day to calculate for.
	 * @param month - the month to calculate for.
	 * @param year  - the year to calculate for.
	 * @return A String representing the day of the week as text.
	 */
	public String getDayFromNumber(int day, int month, int year) {
		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, day);
		switch (c.get(Calendar.DAY_OF_WEEK)) {
		case 1:
			return getConfig().getString("days.sunday");
		case 2:
			return getConfig().getString("days.monday");
		case 3:
			return getConfig().getString("days.tuesday");
		case 4:
			return getConfig().getString("days.wednesday");
		case 5:
			return getConfig().getString("days.thursday");
		case 6:
			return getConfig().getString("days.friday");
		case 7:
			return getConfig().getString("days.saturday");
		default:
			return getConfig().getString("days.monday");
		}
	}

	/**
	 * Returns the month as text.
	 * 
	 * @param month - the month to calculate for
	 * @return A String representing the month as text.
	 */
	public String getMonthFromNumber(int month) {
		switch (month) {
		case 1:
			return getConfig().getString("months.january");
		case 2:
			return getConfig().getString("months.february");
		case 3:
			return getConfig().getString("months.march");
		case 4:
			return getConfig().getString("months.april");
		case 5:
			return getConfig().getString("months.may");
		case 6:
			return getConfig().getString("months.june");
		case 7:
			return getConfig().getString("months.july");
		case 8:
			return getConfig().getString("months.august");
		case 9:
			return getConfig().getString("months.september");
		case 10:
			return getConfig().getString("months.october");
		case 11:
			return getConfig().getString("months.november");
		case 12:
			return getConfig().getString("months.december");
		default:
			return getConfig().getString("months.january");
		}
	}

	/**
	 * Calls a HashMap with standard replacements for this plugin.
	 */
	public HashMap<String, String> getStandardHashMap(Player player, Integer second, Integer minute, Integer hour,
			Integer day, Integer month, Integer year) {
		HashMap<String, String> h = new HashMap<String, String>();
		h.put("{player}", player.getName());
		if (second < 10) {
			h.put("{second}", "0" + second.toString());
		} else {
			h.put("{second}", second.toString());
		}
		if (minute < 10) {
			h.put("{minute}", "0" + minute.toString());
		} else {
			h.put("{minute}", minute.toString());
		}
		if (hour < 10) {
			h.put("{hour}", "0" + hour.toString());
		} else {
			h.put("{hour}", hour.toString());
		}
		if (day < 10) {
			if (getConfig().getBoolean("day-as-words")) {
				h.put("{day}", getDayFromNumber(day, month, year));
			} else {
				h.put("{day}", "0" + day.toString());
			}
		} else {
			if (getConfig().getBoolean("day-as-words")) {
				h.put("{day}", getDayFromNumber(day, month, year));
			} else {
				h.put("{day}", day.toString());
			}
		}
		if (month < 10) {
			if (getConfig().getBoolean("month-as-words")) {
				h.put("{month}", getMonthFromNumber(month));
			} else {
				h.put("{month}", "0" + month.toString());
			}
		} else {
			if (getConfig().getBoolean("month-as-words")) {
				h.put("{month}", getMonthFromNumber(month));
			} else {
				h.put("{month}", month.toString());
			}
		}
		h.put("{rawday}", day.toString());
		h.put("{rawmonth}", month.toString());
		h.put("{year}", year.toString());
		return h;
	}

	/**
	 * Registers all event classes related to OpenRP Time.
	 */
	public void registerEvents() {
		plugin.getLogger().info("Registering Time Worlds...");
		registerTimes();
		restartTimeHandler();
		plugin.getLogger().info("Registering Time Commands...");
		Command_ROLEPLAYTIME handler_TIME = new Command_ROLEPLAYTIME(plugin);
		plugin.getCommand("roleplaytime").setExecutor(handler_TIME);
		plugin.getCommand("roleplaytime").setTabCompleter(handler_TIME);
		plugin.getCommand("roleplaytime").setPermission(getConfig().getString("manage-perm"));
		plugin.getLogger().info("Time Loaded!");
	}

	/**
	 * If x/plugins/OpenRP/time does not exist, this creates it, to avoid problems.
	 */
	public void fixFilePath() {
		directory = new File(plugin.getDataFolder() + File.separator + "time");
		if (!directory.exists()) {
			directory.mkdir();
		}
	}

	/**
	 * Reloads OpenRP Time's config.yml file.
	 */
	public void reloadConfig() {
		File file_config = new File(directory, "config.yml");
		if (!file_config.exists()) {
			plugin.saveResource("time/config.yml", false);
		}
		config = YamlConfiguration.loadConfiguration(file_config);
	}

	/**
	 * Saves OpenRP Time's config.yml file.
	 */
	public void saveConfig() {
		File file_config = new File(directory, "time.yml");
		try {
			config.save(file_config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns OpenRP Time's config.yml file for you to use.
	 */
	public FileConfiguration getConfig() {
		return config;
	}

	/**
	 * Reloads OpenRP Descriptions' messages.yml file.
	 */
	public void reloadMessages() {
		File file_messages = new File(directory, "messages.yml");
		if (!file_messages.exists()) {
			plugin.saveResource("time/messages.yml", false);
		}
		messages = YamlConfiguration.loadConfiguration(file_messages);
	}

	/**
	 * Returns OpenRP Descriptions' messages.yml file for you to use.
	 */
	public FileConfiguration getMessages() {
		return messages;
	}

	/**
	 * Is a quick shortcut that returns the colorized message from the messages
	 * file.
	 */
	public String getMessage(String path) {
		return plugin.colorize(getMessages().getString(path), false);
	}

	/**
	 * Reloads OpenRP Time's timedata.yml file.
	 */
	public void reloadTimedata() {
		File file_timedata = new File(directory, "timedata.yml");
		if (!file_timedata.exists()) {
			plugin.saveResource("time/timedata.yml", false);
		}
		timedata = YamlConfiguration.loadConfiguration(file_timedata);
	}

	/**
	 * Saves OpenRP Time's timedata.yml file.
	 */
	public void saveTimedata() {
		File file_timedata = new File(directory, "timedata.yml");
		try {
			timedata.save(file_timedata);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns OpenRP Time's timedata.yml file for you to use.
	 */
	public FileConfiguration getTimedata() {
		return timedata;
	}

}
