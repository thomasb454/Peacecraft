package com.peacecraftec.bukkit.core.command;

import com.peacecraftec.bukkit.core.CorePermissions;
import com.peacecraftec.bukkit.core.PeacecraftCore;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.Executor;
import com.peacecraftec.module.cmd.sender.CommandSender;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.util.ChatPaginator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CoreCommands extends Executor {
	private PeacecraftCore module;

	public CoreCommands(PeacecraftCore module) {
		this.module = module;
	}
	
	@Command(aliases = {"modules"}, desc = "Manages modules.", usage = "<list/reload/enable/disable> [modules]", min = 1, permission = CorePermissions.MANAGE_MODULES)
	public void modules(CommandSender sender, String command, String args[]) {
		if(args[0].equals("list")) {
			sender.sendMessage("core.modules-list", this.module.getManager().listString());
		} else if(args[0].equals("reload") || args[0].equals("enable") || args[0].equals("disable")) {
			if(args.length < 2) {
				sender.sendMessage("core.specify-modules");
				return;
			}

			List<String> modules = new ArrayList<String>();
			if(args[1].equalsIgnoreCase("all")) {
				modules.addAll(this.module.getManager().getModuleTypes());
			} else {
				for(String arg : args) {
					modules.add(arg);
				}
			}

			if(args[0].equals("reload")) {
				for(String name : modules) {
					Module m = this.module.getManager().getModule(name);
					if(m != null) {
						try {
							m.reload();
							sender.sendMessage("core.reloaded-module", m.getName());
						} catch(Throwable t) {
							sender.sendMessage("core.fail-reload-module", m.getName());
							this.module.getLogger().severe("Failed to reload module \"" + m.getName() + "\"!");
							t.printStackTrace();
							this.module.getManager().unload(name);
						}
					} else {
						sender.sendMessage("core.unknown-module", name);
					}
				}
			} else if(args[0].equals("enable") || args[0].equals("disable")) {
				boolean enable = args[0].equals("enable");
				for(String name : modules) {
					if(!this.module.getManager().getModuleTypes().contains(name)) {
						sender.sendMessage("core.unknown-module", name);
						continue;
					}

					this.module.getManager().getCoreConfig().setValue("modules." + name.toLowerCase(), enable);
					if(enable) {
						if(!this.module.getManager().isEnabled(name)) {
							this.module.getManager().load(name);
						}

						sender.sendMessage("core.enabled-module", name);
					} else {
						if(this.module.getManager().isEnabled(name)) {
							this.module.getManager().unload(name);
						}

						sender.sendMessage("core.disabled-module", name);
					}
				}

				this.module.getManager().getCoreConfig().save();
			}
		} else {
			sender.sendMessage("generic.invalid-sub", "list, reload, enable, disable");
		}
	}
	
	@Command(aliases = {"permissions", "perms", "listperms"}, desc = "Shows a list of all permissions.", usage = "[startswith] [page]", permission = CorePermissions.PERMISSIONS)
	public void permissions(CommandSender sender, String command, String args[]) {
		String startsWith = "";
		int pageNumber = 1;
		if(args.length > 0) {
			if(NumberUtils.isDigits(args[args.length - 1])) {
				startsWith = args.length > 1 ? args[0] : "";
				try {
					pageNumber = Integer.parseInt(args[args.length - 1]);
					if(pageNumber < 0) {
						sender.sendMessage("core.invalid-page");
						return;
					} else if(pageNumber == 0) {
						pageNumber = 1;
					}
				} catch (NumberFormatException e) {
					sender.sendMessage("core.invalid-page");
					return;
				}
			} else {
				startsWith = args[0];
			}
		}

		int pageHeight;
		int pageWidth;
		if(sender instanceof ConsoleCommandSender) {
			pageHeight = ChatPaginator.UNBOUNDED_PAGE_HEIGHT;
			pageWidth = ChatPaginator.UNBOUNDED_PAGE_WIDTH;
		} else {
			pageHeight = ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT - 1;
			pageWidth = ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH;
		}
		
		StringBuilder list = new StringBuilder();
		List<Permission> perms = new ArrayList<Permission>(Bukkit.getServer().getPluginManager().getPermissions());
		Collections.sort(perms, new Comparator<Permission>() {
			@Override
			public int compare(Permission perm, Permission perm2) {
				return perm.getName().compareTo(perm2.getName());
			}
		});
		
		for(Permission perm : perms) {
			if(perm.getName().contains("INTERNAL_PERMISSION") || !perm.getName().startsWith(startsWith)) {
				continue;
			}
			
			list.append(ChatColor.GOLD).append(perm.getName()).append(": ").append(ChatColor.WHITE).append(perm.getDescription()).append("\n");
		}
		
		ChatPaginator.ChatPage page = ChatPaginator.paginate(list.toString(), pageNumber, pageWidth, pageHeight);
		StringBuilder header = new StringBuilder();
		header.append(ChatColor.YELLOW);
		header.append("--------- ");
		header.append(ChatColor.WHITE);
		header.append("Permissions: ");
		header.append(" ");
		if(page.getTotalPages() > 1) {
			header.append("(");
			header.append(page.getPageNumber());
			header.append("/");
			header.append(page.getTotalPages());
			header.append(") ");
		}
		
		header.append(ChatColor.YELLOW);
		for(int i = header.length(); i < ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH; i++) {
			header.append("-");
		}
		
		sender.sendMessage(header.toString());
		for(String line : page.getLines()) {
			sender.sendMessage(line);
		}
	}

	/* // Overriding help because it won't show Peacecraft commands to players because Essentials overrides /help...
	@Command(aliases = {"help"}, desc = "Shows a list of commands.", usage = "[topic] [page]", permission = CorePermissions.HELP)
	public void help(CommandSender sender, String command, String args[]) {
		String cmd;
		int pageNumber;
		int pageHeight;
		int pageWidth;
		if(args.length == 0) {
			cmd = "";
			pageNumber = 1;
		} else if(NumberUtils.isDigits(args[args.length - 1])) {
			cmd = StringUtils.join(ArrayUtils.subarray(args, 0, args.length - 1), " ");
			try {
				pageNumber = NumberUtils.createInteger(args[args.length - 1]);
			} catch (NumberFormatException exception) {
				pageNumber = 1;
			}
			
			if(pageNumber <= 0) {
				pageNumber = 1;
			}
		} else {
			cmd = StringUtils.join(args, " ");
			pageNumber = 1;
		}

		if(sender instanceof ConsoleCommandSender) {
			pageHeight = ChatPaginator.UNBOUNDED_PAGE_HEIGHT;
			pageWidth = ChatPaginator.UNBOUNDED_PAGE_WIDTH;
		} else {
			pageHeight = ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT - 1;
			pageWidth = ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH;
		}

		HelpMap helpMap = Bukkit.getServer().getHelpMap();
		HelpTopic topic = helpMap.getHelpTopic(cmd);
		if(topic == null) {
			topic = helpMap.getHelpTopic("/" + cmd);
		}

		if(topic == null) {
			topic = this.findPossibleMatches(cmd);
		}

		if(topic == null || !topic.canSee(BukkitCommandSender.unwrap(sender))) {
			sender.sendMessage(ChatColor.RED + "No help for " + cmd);
			return;
		}

		ChatPaginator.ChatPage page = ChatPaginator.paginate(topic.getFullText(BukkitCommandSender.unwrap(sender)), pageNumber, pageWidth, pageHeight);
		StringBuilder header = new StringBuilder();
		header.append(ChatColor.YELLOW);
		header.append("--------- ");
		header.append(ChatColor.WHITE);
		header.append("Help: ");
		header.append(topic.getName());
		header.append(" ");
		if(page.getTotalPages() > 1) {
			header.append("(");
			header.append(page.getPageNumber());
			header.append("/");
			header.append(page.getTotalPages());
			header.append(") ");
		}
		
		header.append(ChatColor.YELLOW);
		for(int i = header.length(); i < ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH; i++) {
			header.append("-");
		}
		
		sender.sendMessage(header.toString());
		for(String line : page.getLines()) {
			sender.sendMessage(line);
		}
	}

	protected HelpTopic findPossibleMatches(String searchString) {
		int maxDistance = (searchString.length() / 5) + 3;
		Set<HelpTopic> possibleMatches = new TreeSet<HelpTopic>(HelpTopicComparator.helpTopicComparatorInstance());
		if(searchString.startsWith("/")) {
			searchString = searchString.substring(1);
		}

		for(HelpTopic topic : Bukkit.getServer().getHelpMap().getHelpTopics()) {
			String trimmedTopic = topic.getName().startsWith("/") ? topic.getName().substring(1) : topic.getName();
			if(trimmedTopic.length() < searchString.length()) {
				continue;
			}

			if(Character.toLowerCase(trimmedTopic.charAt(0)) != Character.toLowerCase(searchString.charAt(0))) {
				continue;
			}

			if(damerauLevenshteinDistance(searchString, trimmedTopic.substring(0, searchString.length())) < maxDistance) {
				possibleMatches.add(topic);
			}
		}

		if(possibleMatches.size() > 0) {
			return new IndexHelpTopic("Search", null, null, possibleMatches, "Search for: " + searchString);
		} else {
			return null;
		}
	}

	protected static int damerauLevenshteinDistance(String s1, String s2) {
		if(s1 == null && s2 == null) {
			return 0;
		}
		
		if(s1 != null && s2 == null) {
			return s1.length();
		}
		
		if(s1 == null && s2 != null) {
			return s2.length();
		}

		int s1Len = s1.length();
		int s2Len = s2.length();
		int[][] H = new int[s1Len + 2][s2Len + 2];
		int INF = s1Len + s2Len;
		H[0][0] = INF;
		for(int i = 0; i <= s1Len; i++) {
			H[i + 1][1] = i;
			H[i + 1][0] = INF;
		}
		
		for(int j = 0; j <= s2Len; j++) {
			H[1][j + 1] = j;
			H[0][j + 1] = INF;
		}

		Map<Character, Integer> sd = new HashMap<Character, Integer>();
		for(char Letter : (s1 + s2).toCharArray()) {
			if(!sd.containsKey(Letter)) {
				sd.put(Letter, 0);
			}
		}

		for(int i = 1; i <= s1Len; i++) {
			int DB = 0;
			for(int j = 1; j <= s2Len; j++) {
				int i1 = sd.get(s2.charAt(j - 1));
				int j1 = DB;

				if(s1.charAt(i - 1) == s2.charAt(j - 1)) {
					H[i + 1][j + 1] = H[i][j];
					DB = j;
				} else {
					H[i + 1][j + 1] = Math.min(H[i][j], Math.min(H[i + 1][j], H[i][j + 1])) + 1;
				}

				H[i + 1][j + 1] = Math.min(H[i + 1][j + 1], H[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1));
			}
			
			sd.put(s1.charAt(i - 1), i);
		}

		return H[s1Len + 1][s2Len + 1];
	} */
}
