package benj.brioche.chatmention;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.audience.Audience;

public class ChatMentionListener implements Listener {

	@EventHandler
	public void onPlayerChat(AsyncChatEvent event) {
		String message = PlainTextComponentSerializer.plainText().serialize(event.message()).toLowerCase();
		//Player sender = event.getPlayer();

		for (Audience audience : event.viewers()) {
			if (audience instanceof Player player) {
				String playerName = player.getName().toLowerCase();
				if (message.contains(playerName)) {
					// Play note to the mentionned player
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 2.0f, 2f);
					// Play sound after a delay
					new BukkitRunnable() {
						@Override
						public void run() {
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 2.0f, 2f);
						}
					}.runTaskLater(Bukkit.getPluginManager().getPlugin("BriochePlugin"), 2L);
					new BukkitRunnable() {
						@Override
						public void run() {
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 2.0f, 2f);
						}
					}.runTaskLater(Bukkit.getPluginManager().getPlugin("BriochePlugin"), 4L);
				}
			}
		}
	}
}