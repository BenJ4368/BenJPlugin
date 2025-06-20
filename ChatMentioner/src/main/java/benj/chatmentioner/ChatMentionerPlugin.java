package benj.chatmentioner;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatMentionerPlugin extends JavaPlugin {

	DatabaseManager databaseManager;
	CacheManager cacheManager;
	FileConfiguration config;

	/**
	 * This method is called when the plugin is enabled.
	 * It registers the necessary listeners and commands for the plugin.
	 */
	@Override
	public void onEnable() {
		if(!getDataFolder().exists())
			getDataFolder().mkdirs();

		saveDefaultConfig();
		config = getConfig();

		if (!config.getBoolean("enable_chatmentioner")) {
			getLogger().info("ChatMentioner is disabled in the config. Shutting down plugin.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		databaseManager = new DatabaseManager(new File(getDataFolder(), "benjchatmentioner.db").getPath());
		databaseManager.connect();
		databaseManager.createTable();

		cacheManager = new CacheManager(databaseManager);

		this.getCommand("chatmentioner").setExecutor(new ChatMentionerCommands(cacheManager));
		getServer().getPluginManager().registerEvents(new ChatMentionerListener(config, cacheManager), this);

		getLogger().info("ChatMentioner has started.");
	}

	/**
	 * This method is called when the plugin is disabled.
	 */
	@Override
	public void onDisable() {
		if (databaseManager != null) {
			databaseManager.disconnect();
		}
		getLogger().info("ChatMentioner has stopped.");
	}
}
