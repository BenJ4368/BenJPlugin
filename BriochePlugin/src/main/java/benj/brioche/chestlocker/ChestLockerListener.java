package benj.brioche.chestlocker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ChestLockerListener implements Listener {

	private final ChestLockerManager chestLockerManager;

	public ChestLockerListener(ChestLockerManager chestLockerManager) {
		this.chestLockerManager = chestLockerManager;
	}

	private boolean OpeningChestListener(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK ||
				event.getPlayer().isSneaking())
			return false;

		Block block = event.getClickedBlock();
		if (block == null || !(block.getState() instanceof InventoryHolder))
			return false;

		if (!chestLockerManager.isAuthorized(block.getLocation(), event.getPlayer().getUniqueId())) {
			event.setCancelled(true);

			UUID ownerUUID = chestLockerManager.getChestOwner(block.getLocation());
			String ownerName = ownerUUID != null ? Bukkit.getOfflinePlayer(ownerUUID).getName() : "inconnu";
			event.getPlayer().sendMessage(Component.text("Vous n'avez pas l'autorisation d'ouvrir ce conteneur. (Verrouillé par " + ownerName + " )" ).color(NamedTextColor.RED));
		}
		return true;
	}

	private boolean UsingKeyListener(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK || !event.getPlayer().isSneaking())
			return false;

		ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
		if (itemInHand == null || itemInHand.getType() != Material.GOLD_NUGGET || !itemInHand.hasItemMeta())
			return false;

		Block block = event.getClickedBlock();
		if (block == null || !(block.getState() instanceof InventoryHolder))
			return false;

		ItemMeta meta = itemInHand.getItemMeta();
		Component displayName = meta.displayName();

		if (displayName != null) {
			String plainDisplayName = displayName.toString();

			if (plainDisplayName.contains("Clé de verrouillage")) {
				chestLockerManager.lockContainer(block.getLocation(), event.getPlayer().getUniqueId());

			} else if (plainDisplayName.contains("Clé de déverrouillage")) {
				chestLockerManager.unlockContainer(block.getLocation(), event.getPlayer().getUniqueId());

			} else if (plainDisplayName.contains("Clé de don d'accès")) {
				String playerName = extractPlayerNameFromKey(plainDisplayName);
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
				UUID playerUUID = offlinePlayer.getUniqueId();
				chestLockerManager.addAuthorizedPlayer(block.getLocation(), event.getPlayer().getUniqueId(),
						playerUUID);

			} else if (plainDisplayName.contains("Clé de retrait d'accès")) {
				String playerName = extractPlayerNameFromKey(plainDisplayName);
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
				UUID playerUUID = offlinePlayer.getUniqueId();
				chestLockerManager.removeAuthorizedPlayer(block.getLocation(), event.getPlayer().getUniqueId(),
						playerUUID);

			} else {
				event.getPlayer().sendMessage(Component.text("Action invalide.").color(NamedTextColor.RED));
			}
		}
		return true;
	}

	private String extractPlayerNameFromKey(String displayName) {

		int lastColonIndex = displayName.lastIndexOf(":");
		if (lastColonIndex != -1 && lastColonIndex + 1 < displayName.length()) {
			return displayName.substring(lastColonIndex + 1).trim();
		}
		return "";
	}

	/*
	 * Listeners for :
	 * 1. Opening chests
	 * 2. Using keys (lock, unlock, add access, remove access)
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (OpeningChestListener(event))
			return;
		if (UsingKeyListener(event))
			return;
	}

	/*
	 * Listener for dropping of keys (lock, unlock, add access, remove access)
	 */
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		ItemStack droppedItem = event.getItemDrop().getItemStack();
		ItemMeta meta = droppedItem.getItemMeta();

		if (meta != null && meta.hasDisplayName()) {
			Component displayName = meta.displayName();

			if (displayName != null) {
				String plainDisplayName = displayName.toString();

				// Vérifier si le nom de l'item commence par l'une des chaînes de clé
				if (plainDisplayName.contains("Clé de verrouillage") ||
						plainDisplayName.contains("Clé de déverrouillage") ||
						plainDisplayName.contains("Clé de don d'accès") ||
						plainDisplayName.contains("Clé de retrait d'accès")) {

					event.setCancelled(true); // Annuler le drop
					event.getPlayer().getInventory().removeItem(droppedItem);
				}
			}
		}
	}

	/*
	 * Listener for breaking blocks with keys in creative mode
	 * Prevents breaking blocks with keys in creative mode
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

		if (itemInHand != null && itemInHand.getType() == Material.GOLD_NUGGET ) {
			ItemMeta meta = itemInHand.getItemMeta();

			if (meta != null && meta.hasDisplayName()) {
				Component displayName = meta.displayName();

				if (displayName != null) {
					String plainDisplayName = displayName.toString();

					if (plainDisplayName.contains("Clé de verrouillage") ||
							plainDisplayName.contains("Clé de déverrouillage") ||
							plainDisplayName.contains("Clé de don d'accès") ||
							plainDisplayName.contains("Clé de retrait d'accès")) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(Component.text("Protection contre la casse de bloc avec une clé ChestLocker.").color(NamedTextColor.LIGHT_PURPLE));
					}
				}
			}
		}
		if (chestLockerManager.isChestLocked(event.getBlock().getLocation())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(Component.text("Ce conteneur est invulnerable tant qu'il est verrouillé.").color(NamedTextColor.RED));
		}
	}
}
