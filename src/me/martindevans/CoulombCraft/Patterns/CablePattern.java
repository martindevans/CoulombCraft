package me.martindevans.CoulombCraft.Patterns;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.martindevans.CoulombCraft.CoulombCraft;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import coulombCraft.Networks.Cable;
import coulombCraft.Networks.ResourceNetworkManager;

public class CablePattern extends BasePattern
{
	CoulombCraft plugin;
	
	public CablePattern(CoulombCraft plugin)
	{
		super(new int[][] { { 35 } });
		
		this.plugin = plugin;
	}

	@Override
	public BasePatternInstance Create(Block[][] blocks)
	{
		return new Cable(plugin, blocks);
	}

	@Override
	protected void LoadStoredPatterns(Chunk c)
	{
		int chunkX = c.getX() * 16;
		int chunkZ = c.getZ() * 16;
		
		String query = "SELECT * FROM " + ResourceNetworkManager.NETWORK_BLOCK_TABLE + " WHERE " +
				"x >= " + chunkX + " AND x < " + (chunkX + 16) + " AND " + 
				"z >= " + chunkZ + " AND z < " + (chunkZ + 16) + " AND " + 
				"world = '" + c.getWorld().getName() + "'";
		
		ResultSet rs = plugin.getSqliteDatabase().getDbConnector().sqlSafeQuery(query);
		
		if (rs == null)
			return;
		
		try
		{
			while (rs.next())
			{
				final World w = plugin.getServer().getWorld(rs.getString("world"));
				
				if (w == null)
					continue;
				
				final int x = rs.getInt("x");
				final int y = rs.getInt("y");
				final int z = rs.getInt("z");
				
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						plugin.getPatternMatcher().Match(w.getBlockAt(x, y, z));
					}
				});
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
