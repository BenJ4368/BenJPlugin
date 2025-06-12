package benj.chatmentioner;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatMentionerPlugin extends JavaPlugin {


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
		getLogger().info("ChatMentioner has started.");
    }

	/**
	 * This method is called when the plugin is disabled.
	 */
    @Override
    public void onDisable() {
        getLogger().info("ChatMentioner has stopped.");
    }
}
