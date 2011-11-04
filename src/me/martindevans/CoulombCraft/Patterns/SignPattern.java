package me.martindevans.CoulombCraft.Patterns;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.martindevans.CoulombCraft.CoulombCraft;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import coulombCraft.Signs.SignFactory;
import coulombCraft.Signs.SignUpdateTask;

public class SignPattern extends BasePattern
{
	CoulombCraft plugin;
	
	public SignPattern(CoulombCraft plugin)
	{
		super(new int[][] { });
		
		this.plugin = plugin;
	}
	
	@Override
	public BasePatternInstance Create(Block[][] blocks)
	{
		//this never runs, sign pattern is matched in a different way
		return null;
	}

	@Override
	protected void LoadStoredPatterns(Chunk c)
	{
		int chunkX = c.getX() * 16;
		int chunkZ = c.getZ() * 16;
		
		String query = "SELECT * FROM " + SignUpdateTask.TABLE_NAME + " WHERE " +
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
				
				Block b = c.getWorld().getBlockAt(rs.getInt("xPos"), rs.getInt("yPos"), rs.getInt("zPos"));
				
				if (!b.getType().equals(Material.SIGN) && !b.getType().equals(Material.SIGN_POST))
				{
					CoulombCraft.getLogger().info("Cannot load sign, it is no longer a sign");
					
					plugin.getSqliteDatabase().getDbConnector().deleteSafeQuery("DELETE FROM " + SignUpdateTask.TABLE_NAME + " WHERE xPos = " + b.getX() + " AND yPos = " + b.getY() + " AND zPos = " + b.getZ() + " AND world = '" + b.getWorld().getName() + "'");
					
					continue;
				}
				
				Sign s = (Sign)b.getState();
				
				String[] strings = new String[] { rs.getString("line1"), rs.getString("line2"), rs.getString("line3"), rs.getString("line4") };
				
				s.setLine(0, strings[0]);
				s.setLine(1, strings[1]);
				s.setLine(2, strings[2]);
				s.setLine(3, strings[3]);
				
				SignFactory.MakeSignFromStrings(strings, b, null, plugin);
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
