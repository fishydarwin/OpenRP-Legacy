package openrp.descriptions.cmds;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import openrp.OpenRP;
import openrp.descriptions.events.ORPDescriptionsChangeEvent;

public class Command_CHARACTER implements CommandExecutor, TabCompleter {

	private OpenRP plugin;

	public Command_CHARACTER(OpenRP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (args.length == 0) {

			if (!(sender instanceof Player)) {
				sender.sendMessage("Please run this command as a player!");
				return true;
			}

			for (String s : plugin.getDesc().getFields()) {

				for (String t : plugin.getDesc().getMessages().getStringList("field-manipulation.field-set-format")) {

					if (!t.contains("{change}")) {

						((Player) sender).sendMessage(plugin
								.colorize(t.replace("{field}", s).replace("{value}", plugin.getDesc().getUserdata()
										.getString(((Player) sender).getUniqueId().toString() + "." + s))));

					} else {

						ArrayList<TextComponent> msg = new ArrayList<TextComponent>();
						for (String u : t.split(" ")) {

							if (u.equals("{change}")) {

								if (plugin.getDesc().getConfig().getString("fields." + s + ".allowed-values.type")
										.equalsIgnoreCase("list")) {

									for (String w : plugin.getDesc().getConfig()
											.getStringList("fields." + s + ".allowed-values.value")) {

										String v = ChatColor.stripColor(plugin.colorize(w));

										TextComponent add = new TextComponent(TextComponent.fromLegacyText(
												plugin.getDesc().getMessage("field-manipulation.change.list")
														.replace("{value}", v)));

										add.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												new ComponentBuilder(plugin.colorize("&a&lO")).create()));
										add.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
												"/character set " + s + " " + v));

										msg.add(add);
									}

								} else if (plugin.getDesc().getConfig()
										.getString("fields." + s + ".allowed-values.type").equalsIgnoreCase("contains")
										|| plugin.getDesc().getConfig()
												.getString("fields." + s + ".allowed-values.type")
												.equalsIgnoreCase("without")) {

									TextComponent add = new TextComponent(TextComponent.fromLegacyText(
											plugin.getDesc().getMessage("field-manipulation.change.single")));

									add.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder(plugin.colorize("&a&lO")).create()));
									add.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
											"/character set " + s + " "));

									msg.add(add);

								} else if (plugin.getDesc().getConfig()
										.getString("fields." + s + ".allowed-values.type").equalsIgnoreCase("locked")) {

									TextComponent add = new TextComponent(TextComponent.fromLegacyText(
											plugin.getDesc().getMessage("field-manipulation.change.locked")));

									msg.add(add);

								}

							}

						}

						TextComponent send = new TextComponent();
						for (TextComponent add : msg) {
							send.addExtra(add);
						}
						((Player) sender).spigot().sendMessage(send);

					}

				}
			}

		} else {

			if (args[0].equalsIgnoreCase("override")) {

				if (!sender.hasPermission(plugin.getDesc().getConfig().getString("override-perm"))) {
					sender.sendMessage(plugin.getDesc().getMessage("no-perm"));
					return true;
				}

				if (args.length < 4) {
					sender.sendMessage(plugin.getDesc().getMessage("invalid-use").replace("{usage}",
							label + " override (user) (field) (value)"));
					return true;
				}

				String field = "";
				for (String s : plugin.getDesc().getFields()) {
					if (s.equalsIgnoreCase(args[2])) {
						field = s;
					}
				}
				if (field.isEmpty()) {
					sender.sendMessage(plugin.getDesc().getMessage("invalid-field"));
					return true;
				}

				String suuid = "undefined";
				String name = args[1];
				if (plugin.getServer().getPlayer(args[1]) == null) {
					for (OfflinePlayer o : plugin.getServer().getOfflinePlayers()) {
						if (o.getName().equals(args[1])) {
							suuid = o.getUniqueId().toString();
							name = o.getName();
							break;
						}
					}
				} else {
					suuid = plugin.getServer().getPlayer(args[1]).getUniqueId().toString();
					name = plugin.getServer().getPlayer(args[1]).getName();
				}

				if (suuid.equalsIgnoreCase("undefined")) {
					sender.sendMessage(plugin.getDesc().getMessage("invalid-player"));
					return true;
				}

				String value = "";
				for (String s : args) {
					value += " " + plugin.colorize(s);
				}
				value = value.replaceFirst(plugin.colorize(" " + args[0] + " " + args[1] + " " + args[2] + " "), "");
				ORPDescriptionsChangeEvent event = new ORPDescriptionsChangeEvent(UUID.fromString(suuid), field, value);
				plugin.getServer().getPluginManager().callEvent(event);

				if (!event.isCancelled()) {

					field = event.getField();
					value = event.getValue();

					plugin.getDesc().setField(UUID.fromString(suuid), value, field);
					sender.sendMessage(plugin.getDesc().getMessage("set.override").replace("{player}", name)
							.replace("{field}", field).replace("{value}", value));

				}

			} else if (args[0].equalsIgnoreCase("check")) {

				if (!(sender instanceof Player)) {
					sender.sendMessage("Please run this command as a player!");
					return true;
				}

				if (!sender.hasPermission(plugin.getDesc().getConfig().getString("check-perm"))) {
					sender.sendMessage(plugin.getDesc().getMessage("no-perm"));
					return true;
				}

				if (args.length != 2) {
					sender.sendMessage(
							plugin.getDesc().getMessage("invalid-use").replace("{usage}", label + " check (user)"));
					return true;
				}

				Player player = (Player) sender;

				String suuid = "undefined";
				if (plugin.getServer().getPlayer(args[1]) == null) {
					for (OfflinePlayer o : plugin.getServer().getOfflinePlayers()) {
						if (o.getName().equals(args[1])) {
							suuid = o.getUniqueId().toString();
							break;
						}
					}
				} else {
					suuid = plugin.getServer().getPlayer(args[1]).getUniqueId().toString();
				}

				if (suuid.equalsIgnoreCase("undefined")) {
					sender.sendMessage(plugin.getDesc().getMessage("invalid-player"));
					return true;
				}

				for (String s : plugin.getDesc().getConfig().getStringList("description-format")) {
					player.sendMessage(plugin.colorize(plugin.parsePlaceholders(s, UUID.fromString(suuid),
							plugin.getDesc().getStandardHashMap(UUID.fromString(suuid)))));
				}

			} else if (args[0].equalsIgnoreCase("set")) {

				if (!(sender instanceof Player)) {
					sender.sendMessage(
							"Please run this command as a player! Use /character override if you want to change the field of a different player!");
				}

				Player player = (Player) sender;

				if (!player.hasPermission(plugin.getDesc().getConfig().getString("use-perm"))) {
					player.sendMessage(plugin.getDesc().getMessage("no-perm"));
					return true;
				}

				if (args.length < 3) {
					player.sendMessage(plugin.getDesc().getMessage("invalid-use").replace("{usage}",
							label + " set (field) (value)"));
					return true;
				}

				String field = "";
				for (String s : plugin.getDesc().getFields()) {
					if (s.equalsIgnoreCase(args[1])) {
						field = s;
					}
				}
				if (field.isEmpty()) {
					player.sendMessage(plugin.getDesc().getMessage("invalid-field"));
					return true;
				}

				String value = "";
				for (String s : args) {
					value += " " + plugin.colorize(s);
				}
				value = value.replaceFirst(plugin.colorize(" " + args[0] + " " + args[1] + " "), "");
				ORPDescriptionsChangeEvent event = new ORPDescriptionsChangeEvent(player.getUniqueId(), field, value);
				plugin.getServer().getPluginManager().callEvent(event);

				if (!event.isCancelled()) {

					field = event.getField();
					value = event.getValue();

					if (plugin.getDesc().getCooldowns().containsKey(player)) {
						if (!player.hasPermission(plugin.getDesc().getConfig().getString("bypass-cooldown-perm"))) {
							if (TimeUnit.MILLISECONDS.toSeconds(
									System.currentTimeMillis() - plugin.getDesc().getCooldowns().get(player)) < plugin
											.getDesc().getConfig().getInt("fields." + field + ".cooldown")) {
								player.sendMessage(plugin.getDesc().getMessage("cooldown").replace("{time}",
										((Long) (plugin.getDesc().getConfig().getInt("fields." + field + ".cooldown")
												- TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()
														- plugin.getDesc().getCooldowns().get(player)))).toString()));
								return true;
							}
						}
					}

					plugin.getDesc().getCooldowns().put(player, System.currentTimeMillis());

					if (plugin.getDesc().getConfig().isSet("fields." + field + ".color-code-perm")) {
						if (!player.hasPermission(
								plugin.getDesc().getConfig().getString("fields." + field + ".color-code-perm"))) {
							value = ChatColor.stripColor(value);
						}
					}

					if (plugin.getDesc().getConfig().isSet("fields." + field + ".length.minimum")) {
						if (plugin.getDesc().getConfig().getInt("fields." + field + ".length.minimum") > ChatColor
								.stripColor(value).length()) {
							player.sendMessage(plugin.getDesc().getMessage("invalid-length.min").replace("{len}",
									plugin.getDesc().getConfig().getString("fields." + field + ".length.minimum")));
							return true;
						}
					}

					if (plugin.getDesc().getConfig().isSet("fields." + field + ".length.maximum")) {
						if (plugin.getDesc().getConfig().getInt("fields." + field + ".length.maximum") < ChatColor
								.stripColor(value).length()) {
							player.sendMessage(plugin.getDesc().getMessage("invalid-length.max").replace("{len}",
									plugin.getDesc().getConfig().getString("fields." + field + ".length.maximum")));
							return true;
						}
					}

					if (plugin.getDesc().getConfig().isSet("fields." + field + ".allowed-values.type")) {
						if (plugin.getDesc().getConfig().getString("fields." + field + ".allowed-values.type")
								.equalsIgnoreCase("contains")) {

							if (plugin.getDesc().getConfig().isSet("fields." + field + ".allowed-values.value")) {

								boolean caseSensitive = false;

								if (plugin.getDesc().getConfig()
										.isSet("fields." + field + ".allowed-values.case-sensitive")) {
									if (plugin.getDesc().getConfig()
											.getBoolean("fields." + field + ".allowed-values.case-sensitive")) {
										caseSensitive = true;
									}
								}

								if (!caseSensitive) {
									for (String s : ChatColor.stripColor(value).split("")) {
										if (!plugin.getDesc().getConfig()
												.getString("fields." + field + ".allowed-values.value").toLowerCase()
												.contains(s.toLowerCase())) {
											String val = "";
											for (String t : plugin.getDesc().getConfig()
													.getString("fields." + field + ".allowed-values.value")
													.toLowerCase().split("")) {
												val += ", " + t;
											}
											val = val.replaceFirst(", ", "");
											player.sendMessage(plugin.getDesc().getMessage("not-allowed.contains")
													.replace("{val}", val));
											return true;
										}
									}
								} else {
									for (String s : ChatColor.stripColor(value).split("")) {
										if (!plugin.getDesc().getConfig()
												.getString("fields." + field + ".allowed-values.value").contains(s)) {
											String val = "";
											for (String t : plugin.getDesc().getConfig()
													.getString("fields." + field + ".allowed-values.value").split("")) {
												val += ", " + t;
											}
											val = val.replaceFirst(", ", "");
											player.sendMessage(plugin.getDesc().getMessage("not-allowed.contains")
													.replace("{val}", val));
											return true;
										}
									}
								}

							}

						} else if (plugin.getDesc().getConfig().getString("fields." + field + ".allowed-values.type")
								.equalsIgnoreCase("without")) {

							if (plugin.getDesc().getConfig().isSet("fields." + field + ".allowed-values.value")) {

								boolean caseSensitive = false;

								if (plugin.getDesc().getConfig()
										.isSet("fields." + field + ".allowed-values.case-sensitive")) {
									if (plugin.getDesc().getConfig()
											.getBoolean("fields." + field + ".allowed-values.case-sensitive")) {
										caseSensitive = true;
									}
								}

								if (!caseSensitive) {
									for (String s : ChatColor.stripColor(value).split("")) {
										if (plugin.getDesc().getConfig()
												.getString("fields." + field + ".allowed-values.value").toLowerCase()
												.contains(s.toLowerCase())) {
											String val = "";
											for (String t : plugin.getDesc().getConfig()
													.getString("fields." + field + ".allowed-values.value")
													.toLowerCase().split("")) {
												val += ", " + t;
											}
											val = val.replaceFirst(", ", "");
											player.sendMessage(plugin.getDesc().getMessage("not-allowed.contains")
													.replace("{val}", val));
											return true;
										}
									}
								} else {
									for (String s : ChatColor.stripColor(value).split("")) {
										if (plugin.getDesc().getConfig()
												.getString("fields." + field + ".allowed-values.value").contains(s)) {
											String val = "";
											for (String t : plugin.getDesc().getConfig()
													.getString("fields." + field + ".allowed-values.value").split("")) {
												val += ", " + t;
											}
											val = val.replaceFirst(", ", "");
											player.sendMessage(plugin.getDesc().getMessage("not-allowed.contains")
													.replace("{val}", val));
											return true;
										}
									}
								}

							}

						} else if (plugin.getDesc().getConfig().getString("fields." + field + ".allowed-values.type")
								.equalsIgnoreCase("list")) {

							if (plugin.getDesc().getConfig().isSet("fields." + field + ".allowed-values.value")) {

								boolean caseSensitive = false;

								if (plugin.getDesc().getConfig()
										.isSet("fields." + field + ".allowed-values.case-sensitive")) {
									if (plugin.getDesc().getConfig()
											.getBoolean("fields." + field + ".allowed-values.case-sensitive")) {
										caseSensitive = true;
									}
								}

								boolean found = false;

								if (!caseSensitive) {
									for (String s : plugin.getDesc().getConfig()
											.getStringList("fields." + field + ".allowed-values.value")) {
										if (ChatColor.stripColor(plugin.colorize(s))
												.equalsIgnoreCase(ChatColor.stripColor(plugin.colorize(value)))) {
											value = plugin.colorize(s);
											found = true;
											break;
										}
									}
								} else {
									for (String s : plugin.getDesc().getConfig()
											.getStringList("fields." + field + ".allowed-values.value")) {
										if (ChatColor.stripColor(plugin.colorize(s))
												.equals(ChatColor.stripColor(plugin.colorize(value)))) {
											value = plugin.colorize(s);
											found = true;
											break;
										}
									}
								}

								if (!found) {
									String val = "";
									for (String s : plugin.getDesc().getConfig()
											.getStringList("fields." + field + ".allowed-values.value")) {
										val += ", " + s;
									}
									val = val.replaceFirst(", ", "");
									player.sendMessage(
											plugin.getDesc().getMessage("not-allowed.list").replace("{val}", val));
									return true;
								}

							}

						} else if (plugin.getDesc().getConfig().getString("fields." + field + ".allowed-values.type")
								.equalsIgnoreCase("locked")) {

							player.sendMessage(plugin.getDesc().getMessage("not-allowed.locked"));
							return true;

						}
					}

					plugin.getDesc().setField(player, value, field);
					player.sendMessage(plugin.getDesc().getMessage("set.self").replace("{field}", field)
							.replace("{value}", plugin.colorize(value)));

				}

			} else {
				sender.sendMessage(plugin.getDesc().getMessage("invalid-use").replace("{usage}", label));
			}

		}

		return true;

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("character")) {
			if (args.length <= 1) {

				List<String> l = new ArrayList<String>();
				if (sender.hasPermission(plugin.getDesc().getConfig().getString("use-perm"))) {
					l.add("set");
				}
				if (sender.hasPermission(plugin.getDesc().getConfig().getString("override-perm"))) {
					l.add("override");
				}
				if (sender.hasPermission(plugin.getDesc().getConfig().getString("check-perm"))) {
					l.add("check");
				}

				return l;

			} else if (args.length == 2) {

				List<String> l = new ArrayList<String>();
				if (args[0].equalsIgnoreCase("set")) {
					if (sender.hasPermission(plugin.getDesc().getConfig().getString("use-perm"))) {
						for (String s : plugin.getDesc().getFields()) {
							l.add(s);
						}
					}
				} else if (args[0].equalsIgnoreCase("override")) {
					if (sender.hasPermission(plugin.getDesc().getConfig().getString("override-perm"))) {
						return null;
					}
				} else if (args[0].equalsIgnoreCase("check")) {
					if (sender.hasPermission(plugin.getDesc().getConfig().getString("check-perm"))) {
						return null;
					}
				}

				return l;

			} else if (args.length == 3) {

				List<String> l = new ArrayList<String>();
				if (args[0].equalsIgnoreCase("set")) {
					if (sender.hasPermission(plugin.getDesc().getConfig().getString("use-perm"))) {
						l.add("<value>");
					}
				} else if (args[0].equalsIgnoreCase("override")) {
					if (sender.hasPermission(plugin.getDesc().getConfig().getString("override-perm"))) {
						for (String s : plugin.getDesc().getFields()) {
							l.add(s);
						}
					}
				}

				return l;

			} else if (args.length == 4) {

				List<String> l = new ArrayList<String>();
				if (args[0].equalsIgnoreCase("override")) {
					if (sender.hasPermission(plugin.getDesc().getConfig().getString("override-perm"))) {
						l.add("<value>");
					}
				}

				return l;

			} else {

				return new ArrayList<String>();

			}
		}

		return null;

	}

}
