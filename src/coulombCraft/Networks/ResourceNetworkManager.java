package coulombCraft.Networks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Database;

public class ResourceNetworkManager
{
	CoulombCraft plugin;
	public final Database Database;
	
	HashMap<Long, ResourceNetwork> cachedNetworkInstances = new HashMap<Long, ResourceNetwork>();
	
	static final String NETWORK_TABLE = "ResourceNetwork"; 
	public static final String NETWORK_TABLE_LAYOUT = "id INTEGER PRIMARY KEY";
	
	public static final String NETWORK_BLOCK_TABLE = "ResourceNetworkBlocks";
	public static final String NETWORK_BLOCK_TABLE_LAYOUT = "x INTEGER, y INTEGER, z INTEGER, world VARCHAR(100), networkId INTEGER, FOREIGN KEY(networkId) REFERENCES " + NETWORK_TABLE + "(id), PRIMARY KEY(x, y, z, world)";
	
	static final String NETWORK_RESOURCE_TABLE = "ResourceNetworkResources";
	public static final String NETWORK_RESOURCE_TABLE_LAYOUT = "name VARCHAR(100), quantity DOUBLE, networkId INTEGER, PRIMARY KEY(name, networkId), FOREIGN KEY(networkId) REFERENCES " + NETWORK_TABLE + "(id)";
	
	public ResourceNetworkManager(CoulombCraft plugin)
	{
		this.plugin = plugin;
		this.Database = plugin.getSqliteDatabase();
		
		Database.getDbConnector().ensureTable(NETWORK_TABLE, NETWORK_TABLE_LAYOUT);
		Database.getDbConnector().ensureTable(NETWORK_BLOCK_TABLE, NETWORK_BLOCK_TABLE_LAYOUT);
		Database.getDbConnector().ensureTable(NETWORK_RESOURCE_TABLE, NETWORK_RESOURCE_TABLE_LAYOUT);
	}
	
	public ResourceNetwork getNetworkByBlock(int x, int y, int z, String world)
	{
		ResultSet rs = Database.getDbConnector().sqlSafeQuery("SELECT networkId FROM " + NETWORK_BLOCK_TABLE + " WHERE x = " + x + " AND y = " + y + " AND z = " + z + " AND world = '" + world + "'");
		
		try
		{
			if (!rs.next())
				return null;
			
			int networkId = rs.getInt("networkId");
			
			return getNetworkById(networkId);
		}
		catch (SQLException e)
		{
			CoulombCraft.getLogger().info(e.toString());
			return null;
		}
	}
	
	public ResourceNetwork getNetworkById(long networkId)
	{
		ResourceNetwork network = cachedNetworkInstances.get(networkId);
		
		if (network == null)
		{
			network = new ResourceNetwork(networkId, this);
			cachedNetworkInstances.put(networkId, network);
		}
		
		return network;
	}
	
	public ResourceNetwork CreateNetwork(int x, int y, int z, String world)
	{
		ResultSet rs = SelectAdjacentNetworks(x, y, z, world);
		try
		{
			if (!rs.next())
			{
				Database.getDbConnector().insertSafeQuery("INSERT INTO " + NETWORK_TABLE + " DEFAULT VALUES");
				rs = Database.getDbConnector().sqlSafeQuery("SELECT last_insert_rowid()");
				
				rs.next();
			}
		}
		catch (SQLException e)
		{
			CoulombCraft.getLogger().info(e.toString());
		}
		
		try
		{
			ResourceNetwork n = new ResourceNetwork(rs.getInt(1), this);
			cachedNetworkInstances.put(n.Id, n);
			
			AddNetworkBlock(x, y, z, world, n.Id);
			
			return MergeWithSurroundingNetworks(n, x, y, z, world);
		}
		catch (SQLException e)
		{
			CoulombCraft.getLogger().info(e.toString());
			return null;
		}
	}

	private ResultSet SelectAdjacentNetworks(int x, int y, int z, String world)
	{
		return Database.getDbConnector().sqlSafeQuery("SELECT DISTINCT networkId FROM " + NETWORK_BLOCK_TABLE + " WHERE " +
				"x <= " + (x + 1) + " AND x >= " + (x - 1) + " AND " +
				"y <= " + (y + 1) + " AND y >= " + (y - 1) + " AND " +
				"z <= " + (z + 1) + " AND z >= " + (z - 1) + " AND " + 
				"world = '" + world + "'");
	}
	
	private ResourceNetwork MergeWithSurroundingNetworks(ResourceNetwork network, int x, int y, int z, String world)
	{
		ResultSet rs = SelectAdjacentNetworks(x, y, z, world);
		
		try
		{
			while (rs.next())
			{
				long id = rs.getLong("networkId");
				network = network.MergeInto(getNetworkById(id));
			}
		}
		catch (SQLException e)
		{
			CoulombCraft.getLogger().info(e.toString());
		}
		
		return network;
	}
	
	public void AddNetworkBlock(int x, int y, int z, String world, long id)
	{
		Database.getDbConnector().insertSafeQuery("INSERT INTO " + NETWORK_BLOCK_TABLE + " VALUES ( " + x + "," + y + "," + z + ",'" + world + "'," + id + ")");
	}

	public void RemoveNetworkBlock(int x, int y, int z, String world, long id)
	{
		Database.getDbConnector().deleteSafeQuery("DELETE FROM " + NETWORK_BLOCK_TABLE + " WHERE x = " + x + " AND y = " + y + " AND z = " + z + " AND networkId = " + id + " AND world = '" + world + "'");
	}
}
