package me.wagn.HomeInviteExodus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class HomeInviteExodus extends JavaPlugin {
    public Logger logger = Bukkit.getLogger();
    public static Map<UUID, Location> map = new HashMap<UUID, Location>();
    public static FileConfiguration ExodusHomesConfig = Bukkit.getServer().getPluginManager().getPlugin("ExodusHomes").getConfig();
    public static String PluginPrefix = ChatColor.translateAlternateColorCodes('&', ExodusHomesConfig.getString("Plugin-Prefix"));

    final String username = ExodusHomesConfig.getString("Database-Properties.Username");
    final String password = ExodusHomesConfig.getString("Database-Properties.Password"); // Enter your password for the db
    final String url = "jdbc:mysql://" + ExodusHomesConfig.getString("Database-Properties.Host") + ":" + ExodusHomesConfig.getString("Database-Properties.Port") + "/" + ExodusHomesConfig.getString("Database-Properties.Database");
    static Connection connection;

    @Override
    public void onEnable() {
        this.getCommand("homeinvite").setExecutor(new CommandHI());
        this.getCommand("hiaccept").setExecutor(new CommandAccept());
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        try {
            if (Objects.equals(HomeInviteExodus.ExodusHomesConfig.getString("Database-Type"), "MySQL")) {
                final Properties prop = new Properties();
                prop.setProperty("user", username);
                prop.setProperty("password", password);
                prop.setProperty("autoReconnect", "true");
                connection = DriverManager.getConnection(url, prop);
                logger.info("[HomeInviteExodus] Connected to the MySQL database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        try {
            if (connection!=null && !connection.isClosed()){
                connection.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}