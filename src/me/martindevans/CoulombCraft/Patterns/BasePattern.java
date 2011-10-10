package me.martindevans.CoulombCraft.Patterns;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class BasePattern implements IPatternInstanceFactory
{
	private final int[][] pattern0;
	private final int[][] pattern90;
	private final int[][] pattern180;
	private final int[][] pattern270;
	public int[][] getPattern()
	{
		return pattern0;
	}
	
	private final int size;
	
	protected BasePattern(int[][] pattern)
	{
		this.pattern0 = pattern;
		
		//Assert it is square
		for (int i = 0; i < pattern.length; i++)
		{
			if (pattern.length != pattern[i].length)
				throw new IllegalArgumentException("Patterns must be square");
		}
		size = pattern.length;
		
		this.pattern90 = rotateMatrixRight(pattern0);
		this.pattern180 = rotateMatrixRight(pattern90);
		this.pattern270 = rotateMatrixRight(pattern180);
	}
	
	public int[][] rotateMatrixRight(int[][] matrix)
	{
	    int w = matrix.length;
	    int h = matrix[0].length;
	    int[][] ret = new int[h][w];
	    for (int i = 0; i < h; ++i) {
	        for (int j = 0; j < w; ++j) {
	            ret[i][j] = matrix[w - j - 1][i];
	        }
	    }
	    return ret;
	}
	
	/**
	 * Return true if the given block is surrounded by the correct blocks to form this pattern
	 * @param b
	 * @return
	 */
	public boolean Matches(Block b)
	{
		return
			Matches(pattern0, b) ||
			Matches(pattern90, b) ||
			Matches(pattern180, b) ||
			Matches(pattern270, b);
	}
	
	private boolean Matches(int[][] pattern, Block b)
	{
		int type = b.getTypeId();
		Location blockLocation = b.getLocation();
		
		for	(int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				if (pattern[i][j] == type)
				{					
					Location matchLocation = blockLocation.clone().add(-i, 0, -j);
					
					if (MatchAtLocation(pattern, matchLocation))
						return true;
				}
			}	
		}
		
		return false;
	}
	
	private boolean MatchAtLocation(int[][] pattern, Location location)
	{
		World w = location.getWorld();
		
		for	(int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
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
