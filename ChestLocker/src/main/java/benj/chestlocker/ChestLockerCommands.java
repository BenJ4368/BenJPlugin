package benj.chestlocker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.InventoryHolder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.TabCompleter;

public class ChestLockerCommands implements CommandExecutor, TabCompleter {

	private final ChestLockerManager chestLockerManager;
	public ChestLockerCommands(ChestLockerManager chestLockerManager) {
		this.chestLockerManager = chestLockerManager;
	}

	private ItemStack createCustomKey(Material material, String displayName, String description, String nbtTagValue, String targetPlayerName) {
		ItemStack key = new ItemStack(material);
		ItemMeta meta = key.getItemMeta();

		if (meta != null) {
			meta.displayName(Component.text(displayName)
					.color(NamedTextColor.GOLD)
					.decoration(TextDecoration.ITALIC, false));


			List<Component> loreLines = new ArrayList<>();
			for (String line : description.split("\n"))
				loreLines.add(Component.text(line).color(NamedTextColor.GRAY));
			meta.lore(loreLines);

			 PersistentDataContainer container = meta.getPersistentDataContainer();

			NamespacedKey tagKey = new NamespacedKey("chestlocker", "custom_key_tag");
			container.set(tagKey, PersistentDataType.STRING, nbtTagValue);

			if (targetPlayerName != null && !targetPlayerName.isEmpty()) {
				NamespacedKey playerKey = new NamespacedKey("chestlocker", "target_player_name");
				container.set(playerKey, PersistentDataType.STRING, targetPlayerName);
			}

			key.setItemMeta(meta);
		}

		return key;
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
							"Shift + Clique Gauche sur un conteneur pour le verrouiller.\nJetez pour détruire.", "BRIOCHE_CHESTLOCKER_LOCK", null);
					break;
				case "unlock":
					key = createCustomKey(Material.GOLD_NUGGET, "Clé de déverrouillage",
							"Shift + Clique Gauche sur un conteneur pour le déverrouiller.\nJetez pour détruire.", "BRIOCHE_CHESTLOCKER_UNLOCK", null);
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
							"Shift + Clique Gauche sur un conteneur pour ajouter " + playerNameToAdd + " à la liste d'accès.\nJetez pour détruire.", "BRIOCHE_CHESTLOCKER_ADD", playerNameToAdd);
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
							"Shift + Clique Gauche sur un conteneur pour retirer " + playerNameToRemove + " de la liste d'accès.\nJetez pour détruire.", "BRIOCHE_CHESTLOCKER_REMOVE", playerNameToRemove);
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

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			return List.of("lock", "unlock", "add", "remove", "check").stream()
				.filter(s -> s.startsWith(args[0].toLowerCase()))
				.collect(Collectors.toList());
		} else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
			return Bukkit.getOnlinePlayers().stream()
				.map(Player::getName)
				.filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
				.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

}
