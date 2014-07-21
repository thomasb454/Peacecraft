package com.peacecraftec.bukkit.portals.command;

import com.peacecraftec.bukkit.internal.module.cmd.sender.BukkitCommandSender;
import com.peacecraftec.bukkit.internal.selection.Selection;
import com.peacecraftec.bukkit.internal.selection.Selector;
import com.peacecraftec.bukkit.portals.PeacecraftPortals;
import com.peacecraftec.bukkit.portals.PortalPermissions;
import com.peacecraftec.module.cmd.Command;
import com.peacecraftec.module.cmd.Executor;
import com.peacecraftec.module.cmd.sender.CommandSender;
import com.peacecraftec.module.cmd.sender.PlayerSender;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PortalCommands extends Executor {
	
	private static final List<BlockFace> VALID = Arrays.asList(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST);
	
	private PeacecraftPortals module;
	
	public PortalCommands(PeacecraftPortals module) {
		this.module = module;
	}
	
	@Command(aliases = {"portals"}, desc = "Manages the portals module.", usage = "<create/remove/dest/dir/tp> <portal> [dest/dir]", min = 2, permission = PortalPermissions.MANAGE)
	public void portals(CommandSender sender, String command, String args[]) {
		if(args[0].equalsIgnoreCase("create")) {
			if(!(sender instanceof PlayerSender)) {
				sender.sendMessage("generic.cannot-use-command");
				return;
			}
			
			Player player = (Player) BukkitCommandSender.unwrap(sender);
			Selection select = Selector.get().getSelection(player);
			if(select == null || !select.isComplete()) {
				sender.sendMessage("generic.select-area");
				return;
			}
			
			if(this.module.getPortalManager().isPortal(args[1])) {
				sender.sendMessage("portals.already-exists");
				return;
			}
			
			this.module.getPortalManager().createPortal(args[1], select.getFirstPoint(), select.getSecondPoint());
			sender.sendMessage("portals.portal-created", args[1]);
		} else if(args[0].equalsIgnoreCase("remove")) {
			if(!this.module.getPortalManager().isPortal(args[1])) {
				sender.sendMessage("portals.doesnt-exist");
				return;
			}
			
			this.module.getPortalManager().removePortal(args[1]);
			sender.sendMessage("portals.portal-removed", args[1]);
		} else if(args[0].equalsIgnoreCase("dest")) {
			if(args.length < 3) {
				sender.sendMessage("generic.usage", "/portals dest <portal> <dest>");
				return;
			}
			
			if(!this.module.getPortalManager().isPortal(args[1])) {
				sender.sendMessage("portals.doesnt-exist");
				return;
			}
			
			if(!this.module.getPortalManager().isPortal(args[2])) {
				sender.sendMessage("portals.dest-doesnt-exist");
				return;
			}
			
			this.module.getPortalManager().setDestination(args[1], args[2]);
			sender.sendMessage("portals.dest-put", args[1], args[2]);
		} else if(args[0].equalsIgnoreCase("dir")) {
			if(args.length < 3) {
				sender.sendMessage("generic.usage", "/portals dir <portal> <dir>");
				return;
			}
			
			if(!this.module.getPortalManager().isPortal(args[1])) {
				sender.sendMessage("portals.doesnt-exist");
				return;
			}
			
			BlockFace face = null;
			try {
				face = BlockFace.valueOf(args[2].toUpperCase());
				if(face == null) {
					sender.sendMessage("portals.invalid-dir");
					return;
				}
			} catch(IllegalArgumentException e) {
				sender.sendMessage("portals.invalid-dir");
				return;
			}
			
			if(!VALID.contains(face)) {
				sender.sendMessage("portals.invalid-dir");
				return;
			}
			
			this.module.getPortalManager().setDirection(args[1], face);
			sender.sendMessage("portals.dir-put", args[1], face.name());
		} else if(args[0].equalsIgnoreCase("tp")) {
			if(!(sender instanceof PlayerSender)) {
				sender.sendMessage("generic.cannot-use-command");
				return;
			}
			
			Player player = (Player) BukkitCommandSender.unwrap(sender);
			if(!this.module.getPortalManager().isPortal(args[1])) {
				sender.sendMessage("portals.doesnt-exist");
				return;
			}
			
			player.teleport(this.module.getPortalManager().getPortalPoint(args[1]));
			sender.sendMessage("portals.teleported-to", args[1]);
		} else {
			sender.sendMessage("generic.usage", "/portals <create/remove/dest/dir/tp> <portal>");
		}
	}
	
}
