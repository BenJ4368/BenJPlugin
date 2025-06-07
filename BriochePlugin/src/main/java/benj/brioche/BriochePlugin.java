package benj.brioche;
import org.bukkit.plugin.java.JavaPlugin;
import benj.brioche.chatmention.ChatMentionListener;

public class BriochePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // plugin startup logic
		getServer().getPluginManager().registerEvents(new ChatMentionListener(), this);
        getLogger().info("BriochePlugin has started.");
    }

    @Override
    public void onDisable() {
        // plugin shutdown logic
        getLogger().info("BriochePlugin has stopped.");
    }
}
