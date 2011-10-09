package me.martindevans.CoulombCraft.Patterns;

import java.util.HashSet;
import java.util.concurrent.Future;

import org.bukkit.block.Block;

/**
 * Creates an instance of the correct pattern type from an arragement of blocks in the world
 * @author Martin
 *
 */
public class PatternMatcher
{
	private HashSet<Integer> interestingBlockTypes = new HashSet<Integer>();
	
	public Future<BasePatternInstance> Match(Block b)
	{
		synchronized (interestingBlockTypes)
		{
			if (!interestingBlockTypes.contains(b.getTypeId()))
				return null;
		}
	}
	
	public void AddPattern(Integer[,] pattern, )
}
