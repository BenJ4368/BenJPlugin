package benj.brioche;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import benj.brioche.chatmentioner.ChatMentionerListener;
import benj.brioche.chestlocker.ChestLockerListener;
import benj.brioche.chestlocker.ChestLockerManager;
import benj.brioche.chestlocker.ChestLockerCommands;

public class BriochePlugin extends JavaPlugin {

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

		// Registering the ChatMentionerListener to handle chat mentions
		getServer().getPluginManager().registerEvents(new ChatMentionerListener(), this);

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


		getLogger().info("BriochePlugin has started.");
    }

	/**
	 * This method is called when the plugin is disabled.
	 */
    @Override
    public void onDisable() {

		chestLockerManager.saveChestData();
        getLogger().info("BriochePlugin has stopped.");
    }
}
