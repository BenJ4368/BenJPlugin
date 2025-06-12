package benj.chatmentioner;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatMentionerCommands implements CommandExecutor {

	private final DatabaseManager databaseManager;

	public ChatMentionerCommands(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
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
		if (arg.equals("on")) {
			if (databaseManager.enableMentionsForPlayer(player.getName())) {
				player.sendMessage("Les notifications de mention ont été activées.");
			}
		} else if (arg.equals("off")) {
			if (databaseManager.disableMentionsForPlayer(player.getName())) {
				player.sendMessage("Les notifications de mention ont été désactivées.");
			}
		} else {
			player.sendMessage("Usage: /chatmentioner <on | off>");
			return false;
		}

		return true;
	}
}
