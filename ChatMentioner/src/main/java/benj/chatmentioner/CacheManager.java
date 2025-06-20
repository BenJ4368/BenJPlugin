package benj.chatmentioner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class CacheManager {

	private final Map<UUID, Boolean> mentionStatusCache = new HashMap<>();
	DatabaseManager databaseManager;

	public CacheManager(DatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	public boolean isPlayerMentionDisabled(UUID playerId) {
		if (mentionStatusCache.containsKey(playerId))
			return mentionStatusCache.get(playerId);

		boolean isDisabled = databaseManager.isPlayerMentionDisabled(playerId);
		mentionStatusCache.put(playerId, isDisabled);
		return isDisabled;
	}

	public void updateMentionStatus(UUID playerId, boolean isDisabled) {
		if (isDisabled)
			databaseManager.disableMentionsForPlayer(playerId);
		else
			databaseManager.enableMentionsForPlayer(playerId);
		mentionStatusCache.put(playerId, isDisabled);
	}
}
