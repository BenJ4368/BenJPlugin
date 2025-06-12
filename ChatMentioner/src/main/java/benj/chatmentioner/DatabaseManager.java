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
		String sql = "CREATE TABLE IF NOT EXISTS benjchatmentioner_off (" +
				"player TEXT PRIMARY";

		try (Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
			System.out.println("ChatMention table created successfully.");
		} catch (SQLException e) {
			System.out.println("Error creating table: " + e.getMessage());
		}
	}

	public boolean isPlayerMentionDisabled(String playerName) {
		String sql = "SELECT player FROM benjchatmentioner_off WHERE player = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, playerName);
			ResultSet rs = pstmt.executeQuery();
			return rs.next(); // Retourne true si le joueur est trouvé dans la table
		} catch (SQLException e) {
			System.out.println("Error checking player mention status: " + e.getMessage());
			return false;
		}
	}

	public boolean disableMentionsForPlayer(String playerName) {
		if (isPlayerMentionDisabled(playerName)) {
			Player player = Bukkit.getPlayer(playerName);
			player.sendMessage(Component.text(
					"Vous avez déjà désactivé les mentions. (Les mentions de modérateurs ne sont pas désactivable)")
					.color(NamedTextColor.RED));
			return false;
		}
		String sql = "INSERT OR IGNORE INTO benjchatmentioner_off(player) VALUES(?)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, playerName);
			pstmt.executeUpdate();
			System.out.println("Mentions disabled for player: " + playerName);
			return true;
		} catch (SQLException e) {
			System.out.println("Error disabling mentions for player: " + e.getMessage());
			return false;
		}
	}

	public boolean enableMentionsForPlayer(String playerName) {
		if (isPlayerMentionDisabled(playerName)) {
			String sql = "DELETE FROM benjchatmentioner_off WHERE player = ?";

			try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
				pstmt.setString(1, playerName);
				pstmt.executeUpdate();
				System.out.println("Mentions enabled for player: " + playerName);
				return true;
			} catch (SQLException e) {
				System.out.println("Error enabling mentions for player: " + e.getMessage());
				return false;
			}
		}
		else {
			Player player = Bukkit.getPlayer(playerName);
			player.sendMessage(Component.text("Les mentions sont déjà activées.").color(NamedTextColor.RED));
			return false;
		}
	}
}