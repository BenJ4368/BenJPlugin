package benj.chestlocker;

import java.util.List;
import java.util.UUID;

public class LockedChestData {

	public double x, y, z;
	public String world;
	public UUID owner;
	public List<UUID> authorized;

	public LockedChestData(double x, double y, double z, String world, UUID owner, List<UUID> authorized) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.owner = owner;
		this.authorized = authorized;
	}
}
