package dev.lbuddyboy.vouchers.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CC {
	public static final String BLUE = ChatColor.BLUE.toString();
	public static final String AQUA = ChatColor.AQUA.toString();
	public static final String YELLOW = ChatColor.YELLOW.toString();
	public static final String RED = ChatColor.RED.toString();
	public static final String GRAY = ChatColor.GRAY.toString();
	public static final String GOLD = ChatColor.GOLD.toString();
	public static final String GREEN = ChatColor.GREEN.toString();
	public static final String WHITE = ChatColor.WHITE.toString();
	public static final String BLACK = ChatColor.BLACK.toString();
	public static final String BOLD = ChatColor.BOLD.toString();
	public static final String ITALIC = ChatColor.ITALIC.toString();
	public static final String UNDER_LINE = ChatColor.UNDERLINE.toString();
	public static final String STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
	public static final String RESET = ChatColor.RESET.toString();
	public static final String MAGIC = ChatColor.MAGIC.toString();
	public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
	public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
	public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
	public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
	public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
	public static final String DARK_RED = ChatColor.DARK_RED.toString();
	public static final String PINK = ChatColor.LIGHT_PURPLE.toString();

	private static final List<String> SPECIAL_COLORS = Arrays.asList("&l", "&n", "&o", "&k", "&m", "§l", "§n", "§o", "§k", "§m");
	public static java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<G:([0-9A-Fa-f]{6})>(.*?)</G:([0-9A-Fa-f]{6})>");

	public static String translate(String string) {
		string = string.replaceAll("&g", "&6");
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	private static String withoutSpecialChar(String source) {
		String workingString = source;
		for (String color : SPECIAL_COLORS) {
			if (workingString.contains(color)) {
				workingString = workingString.replace(color, "");
			}
		}
		return workingString;
	}

	public static void broadCastStaff(String message, Object... format) {
		for (Player staff : Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("flash.staff")).collect(Collectors.toList())) {
			staff.sendMessage(translate(message, format));
		}
		Bukkit.getConsoleSender().sendMessage(translate(message, format));
	}

	public static String translate(String string, Object... format) {
		string = deserialize(string);
		for (int i = 0; i < format.length; i += 2) {
			string = string.replace((String) format[i], String.valueOf(format[i + 1]));
		}
		return translate(string);
	}

	public static List<String> translate(List<String> lore) {
		ArrayList<String> toAdd = new ArrayList();

		for (String lor : lore) {
			toAdd.add(translate(lor));
		}

		return toAdd;
	}

	public static String serialize(String name) {
		return name
				.replaceAll(";", "30302030230-")
				.replaceAll("\\|", "20320302302-")
				;
	}

	public static String deserialize(String name) {
		return name
				.replaceAll("30302030230-", ";")
				.replaceAll("20320302302-", "|");
	}

	public static List<String> translate(List<String> lore, Object... objects) {
		ArrayList<String> toAdd = new ArrayList();

		for (String lor : lore) {
			toAdd.add(translate(lor, objects));
		}

		return toAdd;
	}


	public static String ARROWS_LEFT = SymbolUtil.UNICODE_ARROW_LEFT;
	public static String ARROWS_RIGHT = SymbolUtil.UNICODE_ARROW_RIGHT;

}