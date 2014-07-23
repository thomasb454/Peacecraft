package com.peacecraftec.bukkit.core.command;

import com.peacecraftec.bukkit.core.CorePermissions;
import com.peacecraftec.bukkit.core.PeacecraftCore;
import com.peacecraftec.bukkit.core.tp.TpRequest;
import com.peacecraftec.bukkit.core.tp.TpRequestType;
import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.module.Module;
import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.Executor;
import com.peacecraftec.module.cmd.sender.CommandSender;
import com.peacecraftec.module.cmd.sender.PlayerSender;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.util.ChatPaginator;

import java.util.*;

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
				for(String arg : Arrays.copyOfRange(args, 1, args.length)) {
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

	@Command(aliases = {"wand"}, desc = "Gives you a wand.", permission = CorePermissions.WAND, console = false, commandblock = false)
	public void wand(CommandSender sender, String command, String args[]) {
		ItemStack item = new ItemStack(Material.WOOD_AXE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.GOLD + "Peacecraft Wand");
		item.setItemMeta(meta);
		((Player) BukkitCommandSender.unwrap(sender)).getInventory().addItem(item);
	}

	@Command(aliases = {"fly"}, desc = "Toggles flight.", usage = "[player]", permission = CorePermissions.FLY)
	public void fly(CommandSender sender, String command, String args[]) {
		if(args.length < 1 && !(sender instanceof PlayerSender)) {
			sender.sendMessage("generic.cannot-use-command");
			return;
		}

		PlayerSender player = null;
		if(args.length > 0) {
			if(!sender.hasPermission(CorePermissions.FLY_OTHERS)) {
				sender.sendMessage("generic.no-command-perm");
				return;
			}

			List<PlayerSender> matches = this.module.getManager().matchPlayerSender(args[0]);
			if(matches.size() == 0) {
				sender.sendMessage("generic.player-not-found");
				return;
			} else if(matches.size() > 1) {
				sender.sendMessage("generic.multiple-players");
				return;
			} else {
				player = matches.get(0);
			}
		} else {
			player = (PlayerSender) sender;
		}

		Player p = (Player) BukkitCommandSender.unwrap(player);
		p.setAllowFlight(!p.getAllowFlight());
		if(player == sender) {
			sender.sendMessage(p.getAllowFlight() ? "core.can-now-fly" : "core.can-no-longer-fly");
		} else {
			player.sendMessage(p.getAllowFlight() ? "core.can-now-fly" : "core.can-no-longer-fly");
			sender.sendMessage(p.getAllowFlight() ? "core.other-can-now-fly" : "core.other-can-no-longer-fly", player.getDisplayName());
		}
	}

	@Command(aliases = {"invis", "vanish"}, desc = "Toggles invisibility.", usage = "[player]", permission = CorePermissions.INVIS)
	public void invis(CommandSender sender, String command, String args[]) {
		if(args.length < 1 && !(sender instanceof PlayerSender)) {
			sender.sendMessage("generic.cannot-use-command");
			return;
		}

		PlayerSender player = null;
		if(args.length > 0) {
			if(!sender.hasPermission(CorePermissions.INVIS_OTHERS)) {
				sender.sendMessage("generic.no-command-perm");
				return;
			}

			List<PlayerSender> matches = this.module.getManager().matchPlayerSender(args[0]);
			if(matches.size() == 0) {
				sender.sendMessage("generic.player-not-found");
				return;
			} else if(matches.size() > 1) {
				sender.sendMessage("generic.multiple-players");
				return;
			} else {
				player = matches.get(0);
			}
		} else {
			player = (PlayerSender) sender;
		}

		this.module.setInvisible(player.getName(), !this.module.isInvisible(player.getName()));
		Player p = (Player) BukkitCommandSender.unwrap(player);
		for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
			if(this.module.isInvisible(player.getName())) {
				pl.hidePlayer(p);
			} else {
				pl.showPlayer(p);
			}
		}

		if(player == sender) {
			sender.sendMessage(this.module.isInvisible(player.getName()) ? "core.is-now-invisible" : "core.is-no-longer-invisible");
		} else {
			player.sendMessage(this.module.isInvisible(player.getName()) ? "core.is-now-invisible" : "core.is-no-longer-invisible");
			sender.sendMessage(this.module.isInvisible(player.getName()) ? "core.other-is-now-invisible" : "core.other-is-no-longer-invisible", player.getDisplayName());
		}
	}

	@Command(aliases = {"rules"}, desc = "Displays a list of rules.", permission = CorePermissions.RULES)
	public void rules(CommandSender sender, String command, String args[]) {
		sender.sendMessage(ChatColor.GOLD + "1." + ChatColor.WHITE + " No griefing, doing so will get you banned. This includes stealing animals and mobs.");
		sender.sendMessage(ChatColor.GOLD + "2." + ChatColor.WHITE + " No stealing, doing so can also get you banned.");
		sender.sendMessage(ChatColor.GOLD + "3." + ChatColor.WHITE + " Don't spam the chat with messages.");
		sender.sendMessage(ChatColor.GOLD + "4." + ChatColor.WHITE + " Respect the staff and their decisions.");
		sender.sendMessage(ChatColor.GOLD + "5." + ChatColor.WHITE + " Don't ask staff members for special favors (things like WorldEdit)");
		sender.sendMessage(ChatColor.GOLD + "6." + ChatColor.WHITE + " Don't use overly offensive language towards others (racial slurs, etc)");
		sender.sendMessage(ChatColor.GOLD + "7." + ChatColor.WHITE + " Do not advertise or link to other servers in chat.");
		sender.sendMessage(ChatColor.GOLD + "8." + ChatColor.WHITE + " No hacking or cheating. This includes x-ray mods and other things that give unfair advantages.");
		sender.sendMessage(ChatColor.GOLD + "9." + ChatColor.WHITE + " Do not intentionally kill players outside of allowed PVP.");
		sender.sendMessage(ChatColor.GOLD + "10." + ChatColor.WHITE + " Do not build any offensive structures.");
		sender.sendMessage(ChatColor.GOLD + "11." + ChatColor.WHITE + " Do not intentionally cause lag to the server.");
		sender.sendMessage(ChatColor.GOLD + "12." + ChatColor.WHITE + " Do not try to evade a ban, or you will be banned again and it will become permanent.");
		sender.sendMessage(ChatColor.GOLD + "13." + ChatColor.WHITE + " You are responsible for account security. If your account is compromised, you will be held responsible for what is done with it.");
		sender.sendMessage(ChatColor.GOLD + "If you have any questions about our rules or whether you can do something, please ask a staff member.");
	}

	@Command(aliases = {"back"}, desc = "Takes you back to your previous location before teleporting.", permission = CorePermissions.BACK, console = false, commandblock = false)
	public void back(CommandSender sender, String command, String args[]) {
		Location loc = this.module.getBackLocation(sender.getName());
		if(loc == null) {
			sender.sendMessage("core.no-back-location");
		}

		((Player) BukkitCommandSender.unwrap(sender)).teleport(loc);
		sender.sendMessage("core.sent-back");
	}

	@Command(aliases = {"tpr"}, desc = "Makes a request to teleport to a player.", usage = "<player>", min = 1, permission = CorePermissions.TPR_REQUEST, console = false, commandblock = false)
	public void tpr(CommandSender sender, String command, String args[]) {
		PlayerSender player = null;
		List<PlayerSender> matches = this.module.getManager().matchPlayerSender(args[0]);
		if(matches.size() == 0) {
			sender.sendMessage("generic.player-not-found");
			return;
		} else if(matches.size() > 1) {
			sender.sendMessage("generic.multiple-players");
			return;
		} else {
			player = matches.get(0);
		}

		this.module.setTpRequest(player.getName(), new TpRequest(TpRequestType.TPTO, sender.getName(), player.getName()));
		sender.sendMessage("core.sent-tp-request", player.getDisplayName());
		player.sendMessage("core.requested-tp", sender.getDisplayName());
		player.sendMessage("core.how-to-accept");
	}

	@Command(aliases = {"tprhere"}, desc = "Makes a request for a player to teleport to you.", usage = "<player>", min = 1, permission = CorePermissions.TPR_REQUEST, console = false, commandblock = false)
	public void tprhere(CommandSender sender, String command, String args[]) {
		PlayerSender player = null;
		List<PlayerSender> matches = this.module.getManager().matchPlayerSender(args[0]);
		if(matches.size() == 0) {
			sender.sendMessage("generic.player-not-found");
			return;
		} else if(matches.size() > 1) {
			sender.sendMessage("generic.multiple-players");
			return;
		} else {
			player = matches.get(0);
		}

		this.module.setTpRequest(player.getName(), new TpRequest(TpRequestType.TPHERE, sender.getName(), player.getName()));
		sender.sendMessage("core.sent-tphere-request", player.getDisplayName());
		player.sendMessage("core.requested-tphere", sender.getDisplayName());
		player.sendMessage("core.how-to-accept");
	}

	@Command(aliases = {"tpa"}, desc = "Accepts a teleport request.", permission = CorePermissions.TPR_RESPOND, console = false, commandblock = false)
	public void tpa(CommandSender sender, String command, String args[]) {
		TpRequest request = this.module.getTpRequest(sender.getName());
		if(request == null) {
			sender.sendMessage("core.no-tp-requests");
			return;
		}

		this.module.setTpRequest(sender.getName(), null);
		PlayerSender player = this.module.getManager().getPlayerSender(request.getSender());
		if(player == null) {
			sender.sendMessage("generic.player-not-found");
			return;
		}

		if(request.getType() == TpRequestType.TPTO) {
			((Player) BukkitCommandSender.unwrap(player)).teleport((Player) BukkitCommandSender.unwrap(sender));
		} else if(request.getType() == TpRequestType.TPHERE) {
			((Player) BukkitCommandSender.unwrap(sender)).teleport((Player) BukkitCommandSender.unwrap(player));
		}

		sender.sendMessage("core.accepted-tp-request-of", player.getDisplayName());
		player.sendMessage("core.accepted-your-tp-request", sender.getDisplayName());
	}

	@Command(aliases = {"tpd"}, desc = "Denies a teleport request.", permission = CorePermissions.TPR_RESPOND, console = false, commandblock = false)
	public void tpd(CommandSender sender, String command, String args[]) {
		TpRequest request = this.module.getTpRequest(sender.getName());
		if(request == null) {
			sender.sendMessage("core.no-tp-requests");
			return;
		}

		this.module.setTpRequest(sender.getName(), null);
		PlayerSender player = this.module.getManager().getPlayerSender(request.getSender());
		if(player == null) {
			sender.sendMessage("generic.player-not-found");
			return;
		}

		sender.sendMessage("core.denied-tp-request-of", player.getDisplayName());
		player.sendMessage("core.denied-your-tp-request", sender.getDisplayName());
	}

	@Command(aliases = {"home"}, desc = "Teleports you to your set home.", permission = CorePermissions.HOME, console = false, commandblock = false)
	public void home(CommandSender sender, String command, String args[]) {
		Location loc = this.module.getHome(sender.getName());
		if(loc == null) {
			sender.sendMessage("core.no-home");
			return;
		}

		((Player) BukkitCommandSender.unwrap(sender)).teleport(loc);
		sender.sendMessage("core.sent-home");
	}

	@Command(aliases = {"sethome"}, desc = "Sets your home location.", permission = CorePermissions.HOME, console = false, commandblock = false)
	public void sethome(CommandSender sender, String command, String args[]) {
		this.module.setHome(sender.getName(), ((Player) BukkitCommandSender.unwrap(sender)).getLocation());
		sender.sendMessage("core.set-home");
	}

	@Command(aliases = {"warps"}, desc = "Lists all available warps.", permission = CorePermissions.WARP, console = false, commandblock = false)
	public void warps(CommandSender sender, String command, String args[]) {
		StringBuilder warps = new StringBuilder();
		for(String warp : this.module.getWarps()) {
			if(warps.length() != 0) {
				warps.append(", ");
			}

			warps.append(warp);
		}

		sender.sendMessage("core.available-warps", warps.toString());
	}

	@Command(aliases = {"warp"}, desc = "Teleports you to a warp.", usage = "<name>", min = 1, permission = CorePermissions.WARP, console = false, commandblock = false)
	public void warp(CommandSender sender, String command, String args[]) {
		Location loc = this.module.getWarp(args[0].toLowerCase());
		if(loc == null) {
			sender.sendMessage("core.no-warp", args[0]);
			return;
		}

		((Player) BukkitCommandSender.unwrap(sender)).teleport(loc);
		sender.sendMessage("core.sent-to-warp", args[0]);
	}

	@Command(aliases = {"setwarp"}, desc = "Sets a warp at your location.", usage = "<name>", min = 1, permission = CorePermissions.SET_WARP, console = false, commandblock = false)
	public void setwarp(CommandSender sender, String command, String args[]) {
		this.module.setWarp(args[0].toLowerCase(), ((Player) BukkitCommandSender.unwrap(sender)).getLocation());
		sender.sendMessage("core.set-warp", args[0]);
	}

	@Command(aliases = {"invsee"}, desc = "Opens the inventory of a player.", usage = "<player>", min = 1, permission = CorePermissions.INVSEE, console = false, commandblock = false)
	public void invsee(CommandSender sender, String command, String args[]) {
		PlayerSender player = null;
		List<PlayerSender> matches = this.module.getManager().matchPlayerSender(args[0]);
		if(matches.size() == 0) {
			sender.sendMessage("generic.player-not-found");
			return;
		} else if(matches.size() > 1) {
			sender.sendMessage("generic.multiple-players");
			return;
		} else {
			player = matches.get(0);
		}

		((Player) BukkitCommandSender.unwrap(sender)).openInventory(((Player) BukkitCommandSender.unwrap(player)).getInventory());
	}

	@Command(aliases = {"enderchest"}, desc = "Opens the enderchest of a player.", usage = "<player>", min = 1, permission = CorePermissions.ENDERCHEST, console = false, commandblock = false)
	public void enderchest(CommandSender sender, String command, String args[]) {
		PlayerSender player = null;
		List<PlayerSender> matches = this.module.getManager().matchPlayerSender(args[0]);
		if(matches.size() == 0) {
			sender.sendMessage("generic.player-not-found");
			return;
		} else if(matches.size() > 1) {
			sender.sendMessage("generic.multiple-players");
			return;
		} else {
			player = matches.get(0);
		}

		((Player) BukkitCommandSender.unwrap(sender)).openInventory(((Player) BukkitCommandSender.unwrap(player)).getEnderChest());
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
