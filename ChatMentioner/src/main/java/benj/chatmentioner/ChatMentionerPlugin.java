package benj.chatmentioner;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatMentionerPlugin extends JavaPlugin {

	DatabaseManager databaseManager;
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
		this.getCommand("chatmentioner").setExecutor(new ChatMentionerCommands(databaseManager));
		getServer().getPluginManager().registerEvents(new ChatMentionerListener(databaseManager, config), this);
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
