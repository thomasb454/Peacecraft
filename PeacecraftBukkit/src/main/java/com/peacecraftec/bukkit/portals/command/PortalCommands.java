package com.peacecraftec.bukkit.portals.command;

import com.peacecraftec.bukkit.internal.selection.Selection;
import com.peacecraftec.bukkit.internal.selection.Selector;
import com.peacecraftec.bukkit.portals.PeacecraftPortals;
import com.peacecraftec.bukkit.portals.PortalPermissions;
import com.peacecraftec.module.cmd.Command;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static com.peacecraftec.bukkit.internal.module.cmd.CommandUtil.sendMessage;

public class PortalCommands {

    private static final List<BlockFace> VALID = Arrays.asList(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST);

    private PeacecraftPortals module;

    public PortalCommands(PeacecraftPortals module) {
        this.module = module;
    }

    @Command(aliases = { "portals" }, desc = "portals.command.portals", usage = "<create/remove/dest/dir/tp> <portal> [dest/dir]", min = 2, permission = PortalPermissions.MANAGE)
    public void portals(CommandSender sender, String command, String args[]) {
        if(args[0].equalsIgnoreCase("create")) {
            if(!(sender instanceof Player)) {
                sendMessage(sender, "internal.cannot-use-command");
                return;
            }

            Player player = (Player) sender;
            Selection select = Selector.get().getSelection(player);
            if(select == null || !select.isComplete()) {
                sendMessage(sender, "internal.select-area");
                return;
            }

            if(this.module.getPortalManager().isPortal(args[1])) {
                sendMessage(sender, "portals.already-exists");
                return;
            }

            this.module.getPortalManager().createPortal(args[1], select.getFirstPoint(), select.getSecondPoint());
            sendMessage(sender, "portals.portal-created", args[1]);
        } else if(args[0].equalsIgnoreCase("remove")) {
            if(!this.module.getPortalManager().isPortal(args[1])) {
                sendMessage(sender, "portals.doesnt-exist");
                return;
            }

            this.module.getPortalManager().removePortal(args[1]);
            sendMessage(sender, "portals.portal-removed", args[1]);
        } else if(args[0].equalsIgnoreCase("dest")) {
            if(args.length < 3) {
                sendMessage(sender, "internal.usage", "/portals dest <portal> <dest>");
                return;
            }

            if(!this.module.getPortalManager().isPortal(args[1])) {
                sendMessage(sender, "portals.doesnt-exist");
                return;
            }

            if(!this.module.getPortalManager().isPortal(args[2])) {
                sendMessage(sender, "portals.dest-doesnt-exist");
                return;
            }

            this.module.getPortalManager().setDestination(args[1], args[2]);
            sendMessage(sender, "portals.dest-set", args[1], args[2]);
        } else if(args[0].equalsIgnoreCase("dir")) {
            if(args.length < 3) {
                sendMessage(sender, "internal.usage", "/portals dir <portal> <dir>");
                return;
            }

            if(!this.module.getPortalManager().isPortal(args[1])) {
                sendMessage(sender, "portals.doesnt-exist");
                return;
            }

            BlockFace face = null;
            try {
                face = BlockFace.valueOf(args[2].toUpperCase());
                if(face == null) {
                    sendMessage(sender, "portals.invalid-dir");
                    return;
                }
            } catch(IllegalArgumentException e) {
                sendMessage(sender, "portals.invalid-dir");
                return;
            }

            if(!VALID.contains(face)) {
                sendMessage(sender, "portals.invalid-dir");
                return;
            }

            this.module.getPortalManager().setDirection(args[1], face);
            sendMessage(sender, "portals.dir-set", args[1], face.name());
        } else if(args[0].equalsIgnoreCase("tp")) {
            if(!(sender instanceof Player)) {
                sendMessage(sender, "internal.cannot-use-command");
                return;
            }

            Player player = (Player) sender;
            if(!this.module.getPortalManager().isPortal(args[1])) {
                sendMessage(sender, "portals.doesnt-exist");
                return;
            }

            player.teleport(this.module.getPortalManager().getPortalPoint(args[1]));
            sendMessage(sender, "portals.teleported-to", args[1]);
        } else {
            sendMessage(sender, "internal.usage", "/portals <create/remove/dest/dir/tp> <portal>");
        }
    }

}
