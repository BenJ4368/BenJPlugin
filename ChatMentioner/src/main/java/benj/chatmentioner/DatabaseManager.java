package benj.chatmentioner;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class DatabaseManager {

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
			System.out.println("Connected to SQLite DB.");
		} catch (SQLException e) {
			System.out.println("Error connecting to SQLite DB: " + e.getMessage());
		}
	}

	public void disconnect() {
		try {
			if (connection != null) {
				connection.close();
				System.out.println("Disconnected from SQLite DB.");
			}
		} catch (SQLException e) {
			System.out.println("Error disconnecting from SQLite DB: " + e.getMessage());
		}
	}

	public void createTable() {
		String sql = "CREATE TABLE IF NOT EXISTS benjchatmentioner_off (uuid TEXT PRIMARY KEY NOT NULL)";

		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
			System.out.println("ChatMention table created successfully.");
		} catch (SQLException e) {
			System.out.println("Error creating table: " + e.getMessage());
		}
	}

	public boolean isPlayerMentionDisabled(UUID uuid) {
	String sql = "SELECT uuid FROM benjchatmentioner_off WHERE uuid = ?";

	try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
		pstmt.setString(1, uuid.toString());
		ResultSet rs = pstmt.executeQuery();
		return rs.next();
	} catch (SQLException e) {
		System.out.println("Error checking player mention status: " + e.getMessage());
		return false;
	}
}

	public boolean disableMentionsForPlayer(UUID uuid) {
	if (isPlayerMentionDisabled(uuid)) {
		Player player = Bukkit.getPlayer(uuid);
		if (player != null)
			player.sendMessage(Component.text(
				"Vous avez déjà désactivé les mentions. (Les mentions de modérateurs ne sont pas désactivables)")
				.color(NamedTextColor.RED));
		return false;
	}
	String sql = "INSERT OR IGNORE INTO benjchatmentioner_off(uuid) VALUES(?)";

	try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
		pstmt.setString(1, uuid.toString());
		pstmt.executeUpdate();
		System.out.println("Mentions disabled for player: " + uuid);
		return true;
	} catch (SQLException e) {
		System.out.println("Error disabling mentions for player: " + e.getMessage());
		return false;
	}
}

	public boolean enableMentionsForPlayer(UUID uuid) {
	if (isPlayerMentionDisabled(uuid)) {
		String sql = "DELETE FROM benjchatmentioner_off WHERE uuid = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, uuid.toString());
			pstmt.executeUpdate();
			System.out.println("Mentions enabled for player: " + uuid);
			return true;
		} catch (SQLException e) {
			System.out.println("Error enabling mentions for player: " + e.getMessage());
			return false;
		}
	} else {
		Player player = Bukkit.getPlayer(uuid);
		if (player != null)
			player.sendMessage(Component.text("Les mentions sont déjà activées.").color(NamedTextColor.RED));
		return false;
	}
}
}