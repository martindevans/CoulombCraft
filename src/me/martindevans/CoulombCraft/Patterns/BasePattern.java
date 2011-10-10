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
	 * Return the blocks which form this pattern involving the given block or null if no such pattern exists
	 * @param b
	 * @return
	 */
	public Block[][] Match(Block b)
	{
		Block[][] result;
		
		result = Matches(pattern0, b);
		if (result != null)
			return result;
		
		result = Matches(pattern90, b);
		if (result != null)
			return result;
		
		result = Matches(pattern180, b);
		if (result != null)
			return result;
		
		result = Matches(pattern270, b);
		if (result != null)
			return result;
		
		return null;
	}

	
	private Block[][] Matches(int[][] pattern, Block b)
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
					
					Block[][] result = MatchAtLocation(pattern, matchLocation);
					if (result != null)
						return result;
				}
			}	
		}
		
		return null;
	}
	
	private Block[][] MatchAtLocation(int[][] pattern, Location location)
	{
		Block[][] result = new Block[size][size];
		
		World w = location.getWorld();
		
		for	(int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				if (pattern[i][j] == -1)
					continue;
				
				result[i][j] = w.getBlockAt(location.getBlockX() + i, location.getBlockY(), location.getBlockZ() + j);
				
				if (result[i][j].getTypeId() != pattern[i][j])
				{
					return null;
				}
			}
		}
		
		return result;
	}
}
