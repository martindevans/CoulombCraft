package me.martindevans.CoulombCraft.Patterns;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class BasePattern implements IPatternInstanceFactory
{
	private final int[][] pattern;
	public int[][] getPattern()
	{
		return pattern;
	}
	
	protected BasePattern(int[][] pattern)
	{
		this.pattern = pattern;
	}
	
	/**
	 * Return true if the given block is surrounded by the correct blocks to form this pattern
	 * @param b
	 * @return
	 */
	public boolean Matches(Block b)
	{
		int type = b.getTypeId();
		Location blockLocation = b.getLocation();
		
		for	(int i = 0; i < pattern.length; i++)
		{
			for (int j = 0; j < pattern[i].length; j++)
			{
				if (pattern[i][j] == type)
				{					
					Location matchLocation = blockLocation.clone().add(-i, 0, -j);
					
					if (MatchAtLocation(matchLocation))
						return true;
				}
			}	
		}
		
		return false;
	}
	
	private boolean MatchAtLocation(Location location)
	{
		World w = location.getWorld();
		
		for	(int i = 0; i < pattern.length; i++)
		{
			for (int j = 0; j < pattern[i].length; j++)
			{
				if (pattern[i][j] == -1)
					continue;
				
				int blockType = w.getBlockTypeIdAt(location.getBlockX() + i, location.getBlockY(), location.getBlockZ() + j);
				
				if (blockType != pattern[i][j])
				{
					return false;
				}
			}
		}
		
		return true;
	}
}
