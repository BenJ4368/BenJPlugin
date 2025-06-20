package benj.chatmentioner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public class DatabaseManager {

	// logger
	private final Logger logger = JavaPlugin.getPlugin(ChatMentionerPlugin.class).getLogger();
	private Connection connection;
	private final String url;

	public DatabaseManager(String databaseName) {
		this.url = "jdbc:sqlite:" + databaseName;
	}

	public Connection getConnection() {
		return connection;
	}

	public void connect() {
		try {
			this.connection = DriverManager.getConnection(url);
			logger.info("Connected to SQLite DB");
		} catch (SQLException e) {
			logger.severe("Error connecting to SQLite DB: " + e.getMessage());
		}
	}

	public void disconnect() {
		try {
			if (connection != null) {
				connection.close();
				logger.info("Disconnected from SQLite DB");
			}
		} catch (SQLException e) {
			logger.severe("Error disconnecting from SQLite DB: " + e.getMessage());
		}
	}

	public void createTable() {
		String sql = "CREATE TABLE IF NOT EXISTS benjchatmentioner_off (uuid TEXT PRIMARY KEY NOT NULL)";

		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
			logger.info("Table benjchatmentioner_off created successfully.");
		} catch (SQLException e) {
			logger.severe("Error creating table benjchatmentioner_off: " + e.getMessage());
		}
	}

	public boolean isPlayerMentionDisabled(UUID uuid) {
	String sql = "SELECT uuid FROM benjchatmentioner_off WHERE uuid = ?";

	try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
		pstmt.setString(1, uuid.toString());
		ResultSet rs = pstmt.executeQuery();
		return rs.next();
	} catch (SQLException e) {
		logger.severe("Error checking if player mention is disabled: " + e.getMessage());
		return false;
	}
}

	public boolean disableMentionsForPlayer(UUID uuid) {
		String sql = "INSERT OR IGNORE INTO benjchatmentioner_off(uuid) VALUES(?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, uuid.toString());
			pstmt.executeUpdate();// Replace with actual player name retrieval if needed
			logger.info("Mentions disabled for player: " + Bukkit.getOfflinePlayer(uuid).getName());
			return true;
		} catch (SQLException e) {
			logger.severe("Error disabling mentions for player: " + Bukkit.getOfflinePlayer(uuid).getName() + " : " + e.getMessage());
			return false;
		}
}

	public boolean enableMentionsForPlayer(UUID uuid) {
		String sql = "DELETE FROM benjchatmentioner_off WHERE uuid = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, uuid.toString());
			pstmt.executeUpdate();
			logger.info("Mentions enabled for player: " + Bukkit.getOfflinePlayer(uuid).getName());
			return true;
		} catch (SQLException e) {
			logger.severe("Error enabling mentions for player: " + Bukkit.getOfflinePlayer(uuid).getName() + " : " + e.getMessage());
			return false;
		}
	}

}