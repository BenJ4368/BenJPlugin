package benj.chestlocker;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestLockerPlugin extends JavaPlugin {

	private ChestLockerManager chestLockerManager;
	private ChestLockerCommands chestLockerCommands;

	/**
	 * This method is called when the plugin is enabled.
	 * It registers the necessary listeners and commands for the plugin.
	 */
    @Override
    public void onEnable() {
		// Creating the Data folder if it doesn't exist
		if (!getDataFolder().exists())
			getDataFolder().mkdir();

		// Registering the ChestLockerManager and its commands
		chestLockerManager = new ChestLockerManager(this);
		chestLockerCommands = new ChestLockerCommands(chestLockerManager);
		this.getCommand("chestlocker").setExecutor(new ChestLockerCommands(chestLockerManager));
		PluginCommand cmd = this.getCommand("chestlocker");
		if (cmd != null) {
			cmd.setExecutor(chestLockerCommands);
			cmd.setTabCompleter(chestLockerCommands);
		}
		getServer().getPluginManager().registerEvents(new ChestLockerListener(chestLockerManager), this);


		getLogger().info("ChestLocker has started.");
    }

	/**
	 * This method is called when the plugin is disabled.
	 */
    @Override
    public void onDisable() {

		chestLockerManager.saveChestData();
        getLogger().info("ChestLocker has stopped.");
    }
}
