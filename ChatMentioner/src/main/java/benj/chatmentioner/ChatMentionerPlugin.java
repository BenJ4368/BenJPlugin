package benj.chatmentioner;

import org.bukkit.plugin.java.JavaPlugin;

public class ChatMentionerPlugin extends JavaPlugin {

	DatabaseManager databaseManager;

	/**
	 * This method is called when the plugin is enabled.
	 * It registers the necessary listeners and commands for the plugin.
	 */
	@Override
	public void onEnable() {
		if(!getDataFolder().exists())
			getDataFolder().mkdirs();

		databaseManager = new DatabaseManager(getDataFolder().getAbsolutePath() + "/benjchatmentioner.db");
		databaseManager.connect();
		databaseManager.createTable();
		this.getCommand("chatmentioner").setExecutor(new ChatMentionerCommands(databaseManager));
		getServer().getPluginManager().registerEvents(new ChatMentionerListener(databaseManager), this);
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
