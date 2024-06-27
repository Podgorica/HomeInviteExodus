package me.wagn.HomeInviteExodus;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.wagn.HomeInviteExodus.HomeInviteExodus.ExodusHomesConfig;

public class CommandAccept implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (ExodusHomesConfig.getString("Permissions-System") == "true"  && !sender.hasPermission("homes.command.player")) {
            sender.sendMessage("You do not have the permissions for this command!");
            return false;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (HomeInviteExodus.map.get(player.getUniqueId()) == null) {
                player.sendMessage(HomeInviteExodus.PluginPrefix + ChatColor.GRAY + "You have no invitation to accept.");
            }
            else {
                player.teleport(HomeInviteExodus.map.get(player.getUniqueId()));
                HomeInviteExodus.map.remove(player.getUniqueId());
            }
        }
        return true;
    }
}
