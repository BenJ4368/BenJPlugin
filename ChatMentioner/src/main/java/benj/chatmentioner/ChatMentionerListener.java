package benj.chatmentioner;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.audience.Audience;

public class ChatMentionerListener implements Listener {

	private final FileConfiguration config;
	private final CacheManager cacheManager;

	public ChatMentionerListener(FileConfiguration config, CacheManager cacheManager) {
		this.config = config;
		this.cacheManager = cacheManager;
	}

	@EventHandler
	public void onPlayerChat(AsyncChatEvent event) {
		String message = PlainTextComponentSerializer.plainText().serialize(event.message()).toLowerCase();
		Player sender = event.getPlayer();
		float volume = (float) config.getDouble("mention_volume", 1.0);
		float admin_volume = (float) config.getDouble("admin_mention_volume", 1.0);
		boolean persistAdminMention = config.getBoolean("persist_admin_mention", true);

		for (Audience audience : event.viewers()) {
			if (audience instanceof Player player) {
				String playerName = player.getName().toLowerCase();
				if (message.contains(playerName)) {
					if (!cacheManager.isPlayerMentionDisabled(player.getUniqueId()))
					{
						if (sender.hasPermission("chatmentioner.admin")) {
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, admin_volume, 0.7f);
							delaySound(player, Sound.BLOCK_NOTE_BLOCK_BELL, admin_volume, 0.9f, 2L);
							delaySound(player, Sound.BLOCK_NOTE_BLOCK_BELL, admin_volume, 1.0f, 4L);
							return;
						} else {
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, volume, 0.7f);
							delaySound(player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, volume, 0.9f, 2L);
							delaySound(player, Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, volume, 1.0f, 4L);
						}
					} else {
						if (sender.hasPermission("chatmentioner.admin") && persistAdminMention) {
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, admin_volume, 0.7f);
							delaySound(player, Sound.BLOCK_NOTE_BLOCK_BELL, admin_volume, 0.9f, 2L);
							delaySound(player, Sound.BLOCK_NOTE_BLOCK_BELL, admin_volume, 1.0f, 4L);
						}
					}
				}
			}
		}
	}

	public void delaySound(Player player, Sound sound, float volume, float pitch, long delay) {
		new BukkitRunnable() {
			@Override
			public void run() {
				player.playSound(player.getLocation(), sound, volume, pitch);
			}
		}.runTaskLater(Bukkit.getPluginManager().getPlugin("ChatMentioner"), delay);
	}
}