package me.wagn.HomeInviteExodus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static me.wagn.HomeInviteExodus.HomeInviteExodus.ExodusHomesConfig;
import static me.wagn.HomeInviteExodus.HomeInviteExodus.connection;


public class CommandHI implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (Objects.equals(ExodusHomesConfig.getString("Permissions-System"), "true") && !sender.hasPermission("homes.command.player"))
        {
            sender.sendMessage("You do not have the permissions for this command!");
            return false;
        }

        if (args.length == 0) { return false; }

            if (sender instanceof Player) {
                Player player = (Player) sender;

                Player Invited = Bukkit.getServer().getPlayer(args[0]);
                if (Invited == null) {
                    player.sendMessage(HomeInviteExodus.PluginPrefix + ChatColor.RED + ChatColor.ITALIC + args[0] + ChatColor.RESET + ChatColor.GRAY + " is not an online player.");
                    return true;
                }

            if (Objects.equals(HomeInviteExodus.ExodusHomesConfig.getString("Database-Type"), "YAML"))
            {
                File configFile = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/plugins/ExodusHomes/storage/" + player.getUniqueId().toString() + ".yml");
                FileConfiguration HomeList = YamlConfiguration.loadConfiguration(configFile);
                String Home = "Homes." + args[1] + ".";
                if (HomeList.getString(Home + "World") == null) {
                    player.sendMessage(HomeInviteExodus.PluginPrefix + ChatColor.GRAY + "The home " + ChatColor.YELLOW + ChatColor.ITALIC + args[1] + ChatColor.RESET + ChatColor.GRAY + " does not " + ChatColor.RED + "exist" + ChatColor.GRAY + ".");
                    return true;
                }
                Location loc = new Location(Bukkit.getWorld(HomeList.getString(Home + "World")), HomeList.getInt(Home + "X"), HomeList.getInt(Home + "Y"), HomeList.getInt(Home + "Z"), 0, 0);
                loc.add(0.5, 0, 0.5);
                HomeInviteExodus.map.put(Invited.getUniqueId(), loc);
            }
            else if (Objects.equals(HomeInviteExodus.ExodusHomesConfig.getString("Database-Type"), "MySQL"))
            {
                try {
                    String sql = "SELECT * FROM " + HomeInviteExodus.ExodusHomesConfig.getString("Database-Properties.Database") + "." + HomeInviteExodus.ExodusHomesConfig.getString("Database-Properties.Table") + " WHERE `UUID` = ? AND binary `Home` = binary ?"; // Note the question mark as placeholders for input values
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, player.getUniqueId().toString()); // Set first "?" to query string
                    stmt.setString(2, args[1]);
                    ResultSet results = stmt.executeQuery();
                    if (!results.next()) {
                        player.sendMessage(HomeInviteExodus.PluginPrefix + ChatColor.GRAY + "The home " + ChatColor.YELLOW + ChatColor.ITALIC + args[1] + ChatColor.RESET + ChatColor.GRAY + " does not " + ChatColor.RED + "exist" + ChatColor.GRAY + ".");
                        return true;
                    } else {
                        Location loc = new Location(Bukkit.getWorld(results.getString("World")), results.getDouble("X"), results.getDouble("Y"), results.getDouble("Z"), results.getFloat("Yaw"), results.getFloat("Pitch"));
                        loc.add(0.5, 0, 0.5);
                        HomeInviteExodus.map.put(Invited.getUniqueId(), loc);
                    }
                }
                catch (SQLException e)
                {
                    System.out.println(e);
                    if (player.isOp()) { player.sendMessage(String.valueOf(e)); }
                    return true;
                }
            }

            Invited.sendMessage(HomeInviteExodus.PluginPrefix + ChatColor.GRAY + player.getDisplayName() + " has invited you to their home " + ChatColor.YELLOW + ChatColor.ITALIC + args[1] + ChatColor.RESET + ChatColor.GRAY + ". Use /hiaccept to teleport.");
            player.sendMessage(HomeInviteExodus.PluginPrefix + ChatColor.GRAY + "Invited " + Invited.getDisplayName() + ChatColor.GRAY + " to your home " + ChatColor.YELLOW + ChatColor.ITALIC + args[1] + ChatColor.RESET + ".");

        }
        return true;
    }

}
