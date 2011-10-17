package me.martindevans.CoulombCraft.Patterns;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Database;

import org.bukkit.World;
import org.bukkit.block.Block;

import coulombCraft.Reactor.FuelRod;
import coulombCraft.Reactor.FuelRodData;

public class FuelRodPattern2 extends BasePattern implements IPatternInstanceFactory
{
	CoulombCraft plugin;
	
	public FuelRodPattern2(CoulombCraft plugin)
	{
		super(new int[][]
			{
				{ -1, 20, -1 },
				{ 20, 11, 20 },
				{ -1, 20, -1 }
			});
		
		this.plugin = plugin;
	}
	
	public BasePatternInstance Create(Block[][] blocks)
	{
		FuelRod r = new FuelRod(plugin, blocks);		
		return r;
	}
	
	public static void LoadAllFromDatabase(CoulombCraft plugin)
	{
		Database db = plugin.getSqliteDatabase();
		db.getDbConnector().ensureTable(FuelRodData.TABLE_NAME, FuelRodData.TABLE_LAYOUT);
		
		ResultSet rs = db.getDbConnector().sqlSafeQuery("SELECT * FROM " + FuelRodData.TABLE_NAME);
		
		if (rs == null)
		{
			CoulombCraft.getLogger().info("RS is null");
			return;
		}
		
		try
		{
			int count = 0;
			while (rs.next())
			{
				count++;
				
				String worldName = rs.getString("world");
				
				CoulombCraft.getLogger().info(worldName);
				
				World w = plugin.getServer().getWorld(worldName);
				if (w == null)
					continue;
				
				int x = rs.getInt("xPos");
				int y = rs.getInt("yPos");
				int z = rs.getInt("zPos");
				
				Block b = w.getBlockAt(x, y, z);
				b.getChunk().load();
				
				BasePatternInstance i = plugin.getPatternMatcher().Match(b);
				if (i == null)
				{
					CoulombCraft.getLogger().info("Removing an unmatchd fuel rod");
					Remove(db, worldName, x, y, z);
				}
				else
				{
					CoulombCraft.getLogger().info("Matched a fuel rod");
				}
			}
			
			CoulombCraft.getLogger().info(count + " rods processed");
		}
		catch (SQLException e)
		{
		}
	}
	
	private static void Remove(Database db, String world, int x, int y, int z)
	{
		db.getDbConnector().deleteSafeQuery("DELETE FROM " + FuelRodData.TABLE_NAME + " WHERE xPos = " + x + " AND yPos = " + y + " AND zPos = " + z + " AND world = " + world);
	}
}