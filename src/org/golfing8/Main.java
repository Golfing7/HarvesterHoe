package org.golfing8;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin{

	FileConfiguration config = this.getConfig();
	
	public void onEnable() {
		config.addDefault("harvesterhoe.price.sugarcane", 15.0);
		this.saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(new HarvesterHoeGive(), this);
		getCommand("harvesterhoe").setExecutor(new HarvesterHoeGive());
		this.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Enabling HarvesterHoes v1.0");
	}
	
	public void onDisable() {
		this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Disabling HarvesterHoes v1.0");
	}
}
