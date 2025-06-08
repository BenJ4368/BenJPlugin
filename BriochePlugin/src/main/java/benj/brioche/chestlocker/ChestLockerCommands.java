package benj.brioche.chestlocker;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.C;
import org.bukkit.inventory.InventoryHolder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ChestLockerCommands implements CommandExecutor {

	private final ChestLockerManager chestLockerManager;
	public ChestLockerCommands(ChestLockerManager chestLockerManager) {
		this.chestLockerManager = chestLockerManager;
	}


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Component.text("Cette commande ne peut être exécutée que par un joueur.").color(NamedTextColor.RED));
			return true;
		}

		Player player = (Player) sender;
		ItemStack key;

		if (command.getName().equalsIgnoreCase("chestlocker")) {
			if (args.length == 0) {
				player.sendMessage(Component.text("Usage: /chestlocker <lock | unlock | add | remove | check>").color(NamedTextColor.YELLOW));
				return true;
			}

			switch (args[0].toLowerCase()) {
				case "check":
					if (player.getTargetBlockExact(5) == null || !(player.getTargetBlockExact(5).getState() instanceof InventoryHolder)) {
						player.sendMessage(Component.text("Vous devez cibler un conteneur valide.").color(NamedTextColor.YELLOW));
						return true;
					}
					chestLockerManager.checkLocker(player.getTargetBlockExact(5).getLocation(), player.getUniqueId());
				case "lock":
					key = createCustomKey(Material.GOLD_NUGGET, "Clé de verrouillage",
							"Shift + Clique Gauche sur un conteneur pour le verrouiller.\\n" + //
																"Jetez pour détruire.");
					break;
				case "unlock":
					key = createCustomKey(Material.GOLD_NUGGET, "Clé de déverrouillage",
							"Shift+click sur un conteneur pour le déverrouiller.\\n" + //
																"Jetez pour détruire.");
					break;
				case "add":
					if (args.length < 2) {
						player.sendMessage(Component.text("Usage: /chestlocker add <player>").color(NamedTextColor.YELLOW));
						return true;
					}
					String playerNameToAdd = args[1];
					if (playerNameToAdd.equalsIgnoreCase(player.getName())) {
						player.sendMessage(Component.text("Vous ne pouvez pas vous ajouter à la liste d'accès.").color(NamedTextColor.RED));
						return true;
					}
					key = createCustomKey(Material.GOLD_NUGGET, "Clé de don d'accès: " + playerNameToAdd,
							"Shift+click sur un conteneur pour ajouter " + playerNameToAdd + " à la liste d'accès.\\n" + //
																"Jetez pour détruire.");
					break;
				case "remove":
					if (args.length < 2) {
						player.sendMessage(Component.text("Usage: /chestlocker remove <player>").color(NamedTextColor.YELLOW));
						return true;
					}
					String playerNameToRemove = args[1];
					if (playerNameToRemove.equalsIgnoreCase(player.getName())) {
						player.sendMessage(Component.text("Vous ne pouvez pas vous retirer de la liste d'accès.").color(NamedTextColor.RED));
						return true;
					}
					key = createCustomKey(Material.GOLD_NUGGET, "Clé de retrait d'accès: " + playerNameToRemove,
							"Shift+click sur un conteneur pour retirer " + playerNameToRemove + " de la liste d'accès.\\n" + //
																"Jetez pour détruire.");
					break;
				default:
					player.sendMessage(Component.text("Usage: /chestlocker <lock | unlock | add | remove | check>").color(NamedTextColor.YELLOW));
					return true;
			}

			if (player.getInventory().firstEmpty() == -1) {
				player.sendMessage(Component.text("Votre inventaire est plein.").color(NamedTextColor.RED));
				return true;
			}
			player.getInventory().addItem(key);
		}

		return true;
	}

	private ItemStack createCustomKey(Material material, String displayName, String description) {
		ItemStack key = new ItemStack(material);
		ItemMeta meta = key.getItemMeta();

		if (meta != null) {
			meta.displayName(Component.text(displayName)
					.color(NamedTextColor.GOLD));

			meta.lore(Arrays.asList(Component.text(description)
					.color(NamedTextColor.GRAY)
					.decoration(TextDecoration.ITALIC, false)));

			key.setItemMeta(meta);
		}

		return key;
	}
}
