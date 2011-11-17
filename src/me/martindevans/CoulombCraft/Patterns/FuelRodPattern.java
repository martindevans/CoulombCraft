package me.martindevans.CoulombCraft.Patterns;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.martindevans.CoulombCraft.CoulombCraft;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import coulombCraft.Reactor.FuelRod;
import coulombCraft.Reactor.FuelRodData;

public class FuelRodPattern extends BasePattern implements IPatternInstanceFactory
{
	CoulombCraft plugin;
	
	public FuelRodPattern(CoulombCraft plugin)
	{
		super(new int[][]
			{
				{ -1, 20, -1 },
				{ 20, 10, 20 },
				{ -1, 20, -1 }
			});
		
		this.plugin = plugin;
	}
	
	public BasePatternInstance Create(Block[][] blocks)
	{
		FuelRod r = new FuelRod(plugin, blocks);		
		return r;
	}

	@Override
	protected void LoadStoredPatterns(Chunk c)
	{		
		int chunkX = c.getX() * 16;
		int chunkZ = c.getZ() * 16;
		
		String query = "SELECT * FROM " + FuelRodData.TABLE_NAME + " WHERE " +
				"xPos >= " + chunkX + " AND xPos < " + (chunkX + 16) + " AND " + 
				"zPos >= " + chunkZ + " AND zPos < " + (chunkZ + 16) + " AND " + 
				"world = '" + c.getWorld().getName() + "'";
		
		ResultSet rs = plugin.getSqliteDatabase().getDbConnector().sqlSafeQuery(query);
		
		if (rs == null)
			return;
		
		try
		{
			while (rs.next())
			{
				World w = plugin.getServer().getWorld(rs.getString("world"));
				
				if (w == null)
					continue;
				
				plugin.getPatternMatcher().Match(w.getBlockAt(rs.getInt("xPos"), rs.getInt("yPos"), rs.getInt("zPos")));
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				rs.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
}
