package benj.chatmentioner;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ChatMentionerCommands implements CommandExecutor {

	private final CacheManager cacheManager;

	public ChatMentionerCommands(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Seuls les joueurs peuvent exécuter cette commande.");
			return true;
		}
		Player player = (Player) sender;

		if (args.length != 1) {
			player.sendMessage("Usage: /chatmentioner <on | off>");
			return false;
		}

		String arg = args[0].toLowerCase();

		switch (arg) {
            case "on":
				if (!cacheManager.isPlayerMentionDisabled(player.getUniqueId())) {
					player.sendMessage(Component.text(
						"Les mentions sont déjà activées.")
						.color(NamedTextColor.RED));
					break;
				}
                cacheManager.updateMentionStatus(player.getUniqueId(), false);
                player.sendMessage("Les notifications de mention ont été activées.");
                break;
            case "off":
				if (cacheManager.isPlayerMentionDisabled(player.getUniqueId())) {
					player.sendMessage(Component.text(
						"Vous avez déjà désactivé les mentions. (Les mentions de modérateurs ne sont pas désactivables)")
						.color(NamedTextColor.RED));
					break;
				}
                cacheManager.updateMentionStatus(player.getUniqueId(), true);
                player.sendMessage("Les notifications de mention ont été désactivées.");
                break;
            default:
                player.sendMessage("Usage: /chatmentioner <on | off>");
                return false;
        }

		return true;
	}
}
