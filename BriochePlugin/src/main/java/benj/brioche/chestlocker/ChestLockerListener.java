package benj.brioche.chestlocker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.C;

import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.block.Chest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ChestLockerListener implements Listener {

	private final ChestLockerManager chestLockerManager;

	public ChestLockerListener(ChestLockerManager chestLockerManager) {
		this.chestLockerManager = chestLockerManager;
	}

	/*
	 * Listener for opening chests
	 * Checks if the chest is locked and if the player has permission to open it
	 */
	private boolean OpeningChestListener(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return false;

		Block block = event.getClickedBlock();
		if (block == null || !(block.getState() instanceof InventoryHolder))
			return false;

		if (!chestLockerManager.isAuthorized(block.getLocation(), event.getPlayer().getUniqueId())) {
			event.setCancelled(true);

			UUID ownerUUID = chestLockerManager.getChestOwner(block.getLocation());
			String ownerName = ownerUUID != null ? Bukkit.getOfflinePlayer(ownerUUID).getName() : "inconnu";
			event.getPlayer()
					.sendMessage(Component.text(
							"Vous n'avez pas l'autorisation d'ouvrir ce conteneur. (Verrouillé par " + ownerName + " )")
							.color(NamedTextColor.RED));
		}
		return true;
	}

	/*
	 * Listener for using keys (lock, unlock, add access, remove access)
	 * Checks if the player is using a key and performs the corresponding action
	 */
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

		if (meta != null) {
			PersistentDataContainer container = meta.getPersistentDataContainer();
			NamespacedKey keyID = new NamespacedKey("briocheplugin", "custom_key_tag");

			if (container.has(keyID, PersistentDataType.STRING)) {
				String tag = container.get(keyID, PersistentDataType.STRING);

				switch (tag) {
					case "BRIOCHE_CHESTLOCKER_LOCK":
						chestLockerManager.lockContainer(block.getLocation(), event.getPlayer().getUniqueId());
						break;
					case "BRIOCHE_CHESTLOCKER_UNLOCK":
						chestLockerManager.unlockContainer(block.getLocation(), event.getPlayer().getUniqueId());
						break;
					case "BRIOCHE_CHESTLOCKER_ADD": {
						System.out.println("Granting access to " + event.getPlayer().getName());
						NamespacedKey playerKey = new NamespacedKey("briocheplugin", "target_player_name");
						String targetPlayerName = container.get(playerKey, PersistentDataType.STRING);
						OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
						UUID targetUUID = targetPlayer.getUniqueId();
						chestLockerManager.addAuthorizedPlayer(block.getLocation(), event.getPlayer().getUniqueId(),
								targetUUID);
						break;
					}
					case "BRIOCHE_CHESTLOCKER_REMOVE": {
						System.out.println("Revoking access from " + event.getPlayer().getName());
						NamespacedKey playerKey = new NamespacedKey("briocheplugin", "target_player_name");
						String targetPlayerName = container.get(playerKey, PersistentDataType.STRING);
						OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
						UUID targetUUID = targetPlayer.getUniqueId();
						chestLockerManager.removeAuthorizedPlayer(block.getLocation(), event.getPlayer().getUniqueId(),
								targetUUID);
						break;
					}
				}
			}
		}
		return true;
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
	 * Prevents dropping of keys with specific tags
	 */
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		ItemStack droppedItem = event.getItemDrop().getItemStack();
		ItemMeta meta = droppedItem.getItemMeta();

		if (meta != null) {
			NamespacedKey keyID = new NamespacedKey("briocheplugin", "custom_key_tag");
			PersistentDataContainer container = meta.getPersistentDataContainer();

			if (container.has(keyID, PersistentDataType.STRING)) {
				String tagValue = container.get(keyID, PersistentDataType.STRING);
				Set<String> validTags = Set.of(
						"BRIOCHE_CHESTLOCKER_LOCK",
						"BRIOCHE_CHESTLOCKER_UNLOCK",
						"BRIOCHE_CHESTLOCKER_ADD",
						"BRIOCHE_CHESTLOCKER_REMOVE");

				if (tagValue != null && validTags.contains(tagValue))
					event.getItemDrop().remove();
			}
		}
	}

	/*
	 * Listener for preparing item crafting
	 * Prevents crafting items with keys
	 */
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		for (ItemStack ingredient : event.getInventory().getMatrix()) {
			if (ingredient != null) {
				ItemMeta meta = ingredient.getItemMeta();
				if (meta != null) {
					NamespacedKey keyID = new NamespacedKey("briocheplugin", "custom_key_tag");
					PersistentDataContainer container = meta.getPersistentDataContainer();

					if (container.has(keyID, PersistentDataType.STRING)) {
						String tagValue = container.get(keyID, PersistentDataType.STRING);
						Set<String> validTags = Set.of("BRIOCHE_CHESTLOCKER_LOCK", "BRIOCHE_CHESTLOCKER_UNLOCK",
								"BRIOCHE_CHESTLOCKER_ADD", "BRIOCHE_CHESTLOCKER_REMOVE");

						if (tagValue != null && validTags.contains(tagValue)) {
							event.getInventory().setResult(null); // Annuler le craft
							return;
						}
					}
				}
			}
		}
	}

	/*
	 * Listener for breaking blocks with keys
	 * Prevents breaking blocks with keys (mostly for creative mode)
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

		if (itemInHand != null && itemInHand.getType() == Material.GOLD_NUGGET) {
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
						event.getPlayer()
								.sendMessage(Component.text("Protection contre la casse avec une clé ChestLocker.")
										.color(NamedTextColor.LIGHT_PURPLE));
						return;
					}
				}
			}
		}
		if (chestLockerManager.isChestLocked(event.getBlock().getLocation())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(Component.text("Ce conteneur est invulnerable tant qu'il est verrouillé.")
					.color(NamedTextColor.RED));
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block placedBlock = event.getBlockPlaced();

		if (placedBlock.getType() == Material.CHEST) {
			BlockFace[] adjacentFaces = { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };

			for (BlockFace face : adjacentFaces) {
				Block adjacentBlock = placedBlock.getRelative(face);

				if (adjacentBlock.getType() == Material.CHEST) {
					if (chestLockerManager.isChestLocked(adjacentBlock.getLocation())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(Component.text(
								"Un coffre verrouillé est adjacent. Deverrouillez-le d'abord pour placer ce second coffre.")
								.color(NamedTextColor.RED));
						return;
					}
				}
			}
		}
	}

	/*
	 * Listener for explosions
	 * Prevents locked containers from being affected by explosions
	 */
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		// remove locked containers from the block list affected by the explosion
		for (Iterator<Block> it = event.blockList().iterator(); it.hasNext();) {
			Block block = it.next();
			Location location = block.getLocation();

			if (chestLockerManager.isChestLocked(location))
				it.remove();
		}
	}

	/*
	 * Listener for inventory item displacement
	 * Prevents items from being moved out of locked containers by hoppers
	 * that are not locked by the same owner.
	 * Minecart hoppers cannot interact with locked containers at all.
	 */
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent event) {
		Inventory sourceInventory = event.getSource();
		Inventory destinationInventory = event.getDestination();
		if (sourceInventory.getHolder() == null || destinationInventory.getHolder() == null)
			return;

		InventoryHolder sourceHolder = sourceInventory.getHolder();
		InventoryHolder destinationHolder = destinationInventory.getHolder();
		if (sourceHolder.getInventory() == null || sourceHolder.getInventory().getLocation() == null ||
				destinationHolder.getInventory() == null || destinationHolder.getInventory().getLocation() == null)
			return;

		boolean isSourceLocked = chestLockerManager.isChestLocked(sourceHolder.getInventory().getLocation());
		boolean isDestinationHopper = destinationHolder instanceof Hopper;
		boolean isDestinationMinecartHopper = destinationHolder instanceof HopperMinecart;

		if (isSourceLocked)
			if ((isDestinationHopper && !hasSameOwn(sourceHolder, destinationHolder)) ||
					(isDestinationMinecartHopper && isSourceLocked)) {
				event.setCancelled(true);
				return;
			}
	}

	@EventHandler
	public void onHopperPickupItem(InventoryPickupItemEvent event) {
		Inventory inventory = event.getInventory();

		// Vérifie si l'inventaire est un hopper
		if (inventory.getType() != InventoryType.HOPPER) return;

		// Récupère la Location du hopper
		Location hopperLocation = ((org.bukkit.block.Hopper) inventory.getHolder()).getLocation();

		// Vérifie si ce hopper est verrouillé dans tes données
		if (chestLockerManager.isChestLocked(hopperLocation)) {
			event.setCancelled(true);
		}
	}
	/*
	 * Checks if the source and destination holders have the same owner
	 */
	public boolean hasSameOwn(InventoryHolder sourceHolder, InventoryHolder destinationHolder) {
		UUID sourceOwner = chestLockerManager.getChestOwner(sourceHolder.getInventory().getLocation());
		UUID destinationOwner = chestLockerManager.getChestOwner(destinationHolder.getInventory().getLocation());
		return sourceOwner != null && sourceOwner.equals(destinationOwner);
	}

}
