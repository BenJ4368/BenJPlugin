package benj.brioche.chestlocker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Chest;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.Component;

import java.io.*;
import java.util.*;

public class ChestLockerManager {

	private final File dataFile;
	private final Gson gson;
	private Map<String, LockedChestData> lockedChests = new HashMap<>();

	public ChestLockerManager(Plugin plugin) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.registerTypeAdapter(Location.class, new LocationTypeAdapter());
		this.gson = gsonBuilder.create();
		dataFile = new File(plugin.getDataFolder(), "ChestLockerData.json");
		loadChestData();
	}

	public String locToKey(Location loc) {
		return loc.getWorld().getName() + ":" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
	}

	public Location keyToLoc(String key) {
		String[] parts = key.split("[:|,]");
		World world = Bukkit.getWorld(parts[0]);
		int x = Integer.parseInt(parts[1]);
		int y = Integer.parseInt(parts[2]);
		int z = Integer.parseInt(parts[3]);
		return new Location(world, x, y, z);
	}

	private void loadChestData() {
		if (!dataFile.exists()) {
			Bukkit.getLogger().info("No existing chest data file found. Starting with an empty dataset.");
			return;
		}

		try {
			StringBuilder stringBuilder = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
				String line;
				while ((line = reader.readLine()) != null)
					stringBuilder.append(line);
			}
			String jsonContent = stringBuilder.toString();

			try (StringReader stringReader = new StringReader(jsonContent)) {
				java.lang.reflect.Type type = new TypeToken<Map<String, LockedChestData>>() {}.getType();
				lockedChests = gson.fromJson(stringReader, type);
			}
		} catch (IOException e) {
			Bukkit.getLogger().severe("Could not load chest data: " + e.getMessage());
		} catch (JsonSyntaxException e) {
			Bukkit.getLogger().severe("Invalid JSON syntax in chest data: " + e.getMessage());
		}
	}

	public void saveChestData() {
		try (Writer writer = new FileWriter(dataFile)) {
			gson.toJson(lockedChests, writer);
		} catch (IOException e) {
			Bukkit.getLogger().severe("Could not save chest data: " + e.getMessage());
		}
		Bukkit.getLogger().info("Chest data saved successfully.");
	}

	public void lockContainer(Location location, UUID cmdSenderUUID) {
		Block block = location.getBlock();
		BlockState blockState = block.getState();
		Player cmdSender = Bukkit.getPlayer(cmdSenderUUID);

		if (!(blockState instanceof InventoryHolder)) {
			cmdSender
					.sendMessage(Component.text("Ce bloc n'est pas un conteneur valide.").color(NamedTextColor.YELLOW));
			return;
		}
		if (blockState instanceof org.bukkit.block.Chest) {
			org.bukkit.block.Chest chest = (org.bukkit.block.Chest) blockState;
			org.bukkit.block.data.type.Chest chestData = (org.bukkit.block.data.type.Chest) chest.getBlockData();

			if (chestData.getType() != org.bukkit.block.data.type.Chest.Type.SINGLE) {
				Block adjacentChestBlock = getAdjacentChestBlock(block, chestData);
				if (adjacentChestBlock != null) {
					Location adjacentLocation = adjacentChestBlock.getLocation();
					if (isChestLocked(location) || isChestLocked(adjacentLocation)) {
						cmdSender.sendMessage(
								Component.text("Ce conteneur est déjà verrouillé.").color(NamedTextColor.YELLOW));
						return;
					}
					lockSingleContainer(location, cmdSenderUUID);
					lockSingleContainer(adjacentLocation, cmdSenderUUID);
					cmdSender.sendMessage(Component.text("Conteneur verrouillé.").color(NamedTextColor.GREEN));
					return;
				}
			} else {
				if (isChestLocked(location)) {
					cmdSender.sendMessage(
							Component.text("Ce conteneur est déjà verrouillé.").color(NamedTextColor.YELLOW));
					return;
				} else
					lockSingleContainer(location, cmdSenderUUID);
			}
		} else {
			if (isChestLocked(location)) {
				cmdSender.sendMessage(Component.text("Ce conteneur est déjà verrouillé.").color(NamedTextColor.YELLOW));
				return;
			} else
				lockSingleContainer(location, cmdSenderUUID);
		}
		cmdSender.sendMessage(Component.text("Conteneur Verrouillé.").color(NamedTextColor.GREEN));
	}

	public Block getAdjacentChestBlock(Block chestBlock, org.bukkit.block.data.type.Chest chestData) {
		BlockFace facing = chestData.getFacing();
		org.bukkit.block.data.type.Chest.Type type = chestData.getType();

		BlockFace offset = null;

		switch (facing) {
			case NORTH:
				offset = (type == Chest.Type.LEFT) ? BlockFace.EAST : BlockFace.WEST;
				break;
			case SOUTH:
				offset = (type == Chest.Type.LEFT) ? BlockFace.WEST : BlockFace.EAST;
				break;
			case EAST:
				offset = (type == Chest.Type.LEFT) ? BlockFace.SOUTH : BlockFace.NORTH;
				break;
			case WEST:
				offset = (type == Chest.Type.LEFT) ? BlockFace.NORTH : BlockFace.SOUTH;
				break;
			default:
				return null;
		}

		return offset != null ? chestBlock.getRelative(offset) : null;
	}

	private void lockSingleContainer(Location location, UUID ownerUUID) {
		LockedChestData containerData = new LockedChestData(
				location.getX(),
				location.getY(),
				location.getZ(),
				location.getWorld().getName(),
				ownerUUID,
				new ArrayList<>());

		lockedChests.put(locToKey(location), containerData);
		saveChestData();
	}

	public void unlockContainer(Location location, UUID cmdSenderUUID) {
		Player cmdSender = Bukkit.getPlayer(cmdSenderUUID);

		LockedChestData chestData = lockedChests.get(locToKey(location));
		if (chestData == null) {
			cmdSender.sendMessage(Component.text("Ce conteneur n'est pas verrouillé.").color(NamedTextColor.YELLOW));
			return;
		}

		if (!(chestData.owner.equals(cmdSenderUUID) || cmdSender.hasPermission("briocheplugin.admin"))) {
			cmdSender.sendMessage(Component.text("Vous n'avez pas les droits pour déverrouiller ce conteneur.")
					.color(NamedTextColor.RED));
			return;
		}

		Block block = location.getBlock();
		if (block.getState() instanceof org.bukkit.block.Chest) {
			org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block.getState();
			org.bukkit.block.data.type.Chest chestDataBlock = (org.bukkit.block.data.type.Chest) chest.getBlockData();

			if (chestDataBlock.getType() != org.bukkit.block.data.type.Chest.Type.SINGLE) {
				Block adjacentChestBlock = getAdjacentChestBlock(block, chestDataBlock);
				if (adjacentChestBlock != null) {
					Location adjacentLocation = adjacentChestBlock.getLocation();
					if (isChestLocked(adjacentLocation)) {
						lockedChests.remove(locToKey(adjacentLocation));
					}
				}
			}
		}

		lockedChests.remove(locToKey(location));
		saveChestData();
		cmdSender.sendMessage(Component.text("Conteneur déverrouillé.").color(NamedTextColor.GREEN));
	}

	public void addAuthorizedPlayer(Location location, UUID cmdSenderUUID, UUID playerUUID) {
		System.out.println("Adding authorized player: " + playerUUID);
		Player cmdSender = Bukkit.getPlayer(cmdSenderUUID);

		LockedChestData chestData = lockedChests.get(locToKey(location));
		if (chestData == null) {
			cmdSender.sendMessage(Component.text("Ce conteneur n'est pas verrouillé.").color(NamedTextColor.YELLOW));
			return;
		}
		if (!(chestData.owner.equals(cmdSenderUUID) || cmdSender.hasPermission("briocheplugin.admin"))) {
			cmdSender.sendMessage(Component.text("Vous n'avez pas les droits pour donner accès a ce conteneur.")
					.color(NamedTextColor.RED));
			return;
		}
		if (playerUUID == null) {
			cmdSender.sendMessage(Component.text("Le joueur spécifié est invalide.").color(NamedTextColor.RED));
			return;
		}

		Block block = location.getBlock();
		if (block.getState() instanceof org.bukkit.block.Chest) {
			org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block.getState();
			org.bukkit.block.data.type.Chest chestDataBlock = (org.bukkit.block.data.type.Chest) chest.getBlockData();

			if (chestDataBlock.getType() != org.bukkit.block.data.type.Chest.Type.SINGLE) {
				Block adjacentChestBlock = getAdjacentChestBlock(block, chestDataBlock);
				if (adjacentChestBlock != null) {
					Location adjacentLocation = adjacentChestBlock.getLocation();
					LockedChestData adjacentChestData = lockedChests.get(locToKey(adjacentLocation));
					if (adjacentChestData != null) {
						if (adjacentChestData.authorized.contains(playerUUID)) {
							cmdSender.sendMessage(Component.text("Ce joueur à déjà accès à ce conteneur.")
									.color(NamedTextColor.YELLOW));
							return;
						}
						adjacentChestData.authorized.add(playerUUID);
					}
				}
			} else {
				if (chestData.authorized.contains(playerUUID)) {
					cmdSender.sendMessage(
							Component.text("Ce joueur à déjà accès à ce conteneur.").color(NamedTextColor.YELLOW));
					return;
				}
			}
		}

		chestData.authorized.add(playerUUID);
		saveChestData();
		cmdSender.sendMessage(Component.text("Joueur ajouté à l'accès au conteneur.").color(NamedTextColor.GREEN));
	}

	public void removeAuthorizedPlayer(Location location, UUID cmdSenderUUID, UUID playerUUID) {
		System.out.println("Removing authorized player: " + playerUUID);
		Player cmdSender = Bukkit.getPlayer(cmdSenderUUID);

		LockedChestData chestData = lockedChests.get(locToKey(location));
		if (chestData == null) {
			cmdSender.sendMessage(Component.text("Ce conteneur n'est pas verrouillé.").color(NamedTextColor.YELLOW));
			return;
		}
		if (!(chestData.owner.equals(cmdSenderUUID) || cmdSender.hasPermission("briocheplugin.admin"))) {
			cmdSender.sendMessage(Component.text("Vous n'avez pas les droits pour retirer l'accès à ce conteneur.")
					.color(NamedTextColor.RED));
			return;
		}
		if (playerUUID == null) {
			cmdSender.sendMessage(Component.text("Le joueur spécifié est invalide.").color(NamedTextColor.RED));
			return;
		}

		Block block = location.getBlock();
		if (block.getState() instanceof org.bukkit.block.Chest) {
			org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block.getState();
			org.bukkit.block.data.type.Chest chestDataBlock = (org.bukkit.block.data.type.Chest) chest.getBlockData();

			if (chestDataBlock.getType() != org.bukkit.block.data.type.Chest.Type.SINGLE) {
				Block adjacentChestBlock = getAdjacentChestBlock(block, chestDataBlock);
				if (adjacentChestBlock != null) {
					Location adjacentLocation = adjacentChestBlock.getLocation();
					LockedChestData adjacentChestData = lockedChests.get(locToKey(adjacentLocation));
					if (adjacentChestData != null) {
						if (!adjacentChestData.authorized.contains(playerUUID)) {
							cmdSender.sendMessage(
									Component.text("Ce joueur n'est pas autorisé à accéder à ce conteneur.")
											.color(NamedTextColor.YELLOW));
							return;
						}
						adjacentChestData.authorized.remove(playerUUID);
					}
				}
			} else {
				if (!chestData.authorized.contains(playerUUID)) {
					cmdSender.sendMessage(Component.text("Ce joueur n'est pas autorisé à accéder à ce conteneur.")
							.color(NamedTextColor.YELLOW));
					return;
				}
			}
		}

		chestData.authorized.remove(playerUUID);
		saveChestData();
		cmdSender
				.sendMessage(Component.text("Joueur retiré de l'accès au conteneur.").color(NamedTextColor.DARK_GREEN));
	}

	public void checkLocker(Location location, UUID cmdSenderUUID) {
		Player cmdSender = Bukkit.getPlayer(cmdSenderUUID);
		LockedChestData chestData = lockedChests.get(locToKey(location));

		if (chestData == null) {
			cmdSender.sendMessage(Component.text("Ce conteneur n'est pas verrouillé.").color(NamedTextColor.YELLOW));
			return;
		}

		StringBuilder message = new StringBuilder("Conteneur verrouillé par: ");
		OfflinePlayer owner = Bukkit.getOfflinePlayer(chestData.owner);
		message.append(owner.getName() != null ? owner.getName() : chestData.owner.toString()).append("\n");

		message.append("Joueurs autorisés: ");
		if (chestData.authorized.isEmpty()) {
			message.append("Aucun joueur autorisé.");
		} else {
			for (UUID uuid : chestData.authorized) {
				OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
				String name = p.getName() != null ? p.getName() : uuid.toString();
				message.append(name).append(", ");
			}
			message.setLength(message.length() - 2); // remove trailing ", "
		}

		cmdSender.sendMessage(Component.text(message.toString()).color(NamedTextColor.LIGHT_PURPLE));
	}

	public boolean isAuthorized(Location location, UUID playerUUID) {
		LockedChestData chestData = lockedChests.get(locToKey(location));
		if (chestData == null)
			return true;

		return chestData.owner.equals(playerUUID) || chestData.authorized.contains(playerUUID);
	}

	public boolean isChestLocked(Location location) {
		return lockedChests.containsKey(locToKey(location));
	}

	public UUID getChestOwner(Location location) {
		LockedChestData chestData = lockedChests.get(locToKey(location));
		if (chestData != null)
			return chestData.owner;
		return null;
	}
}
