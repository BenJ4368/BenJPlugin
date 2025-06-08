package benj.brioche;
import org.bukkit.plugin.java.JavaPlugin;
import benj.brioche.chatmention.ChatMentionListener;
import benj.brioche.chestlocker.ChestLockerListener;
import benj.brioche.chestlocker.ChestLockerManager;
import benj.brioche.chestlocker.ChestLockerCommands;

public class BriochePlugin extends JavaPlugin {

	private ChestLockerManager chestLockerManager;

    @Override
    public void onEnable() {
        // plugin startup logic

		// Registering the ChatMentionListener to handle chat mentions
		getServer().getPluginManager().registerEvents(new ChatMentionListener(), this);

		// Creating the ChestLockerData folder if it doesn't exist
		if (!getDataFolder().exists())
        getDataFolder().mkdir();
		// Registering the ChestLockerManager and its commands
		chestLockerManager = new ChestLockerManager(this);
		this.getCommand("chestlocker").setExecutor(new ChestLockerCommands(chestLockerManager));
		getServer().getPluginManager().registerEvents(new ChestLockerListener(chestLockerManager), this);


		getLogger().info("BriochePlugin has started.");
    }

    @Override
    public void onDisable() {
        // plugin shutdown logic

		chestLockerManager.saveChestData();
        getLogger().info("BriochePlugin has stopped.");
    }
}
