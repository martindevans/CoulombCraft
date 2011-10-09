package me.martindevans.CoulombCraft.Patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.martindevans.CoulombCraft.CoulombCraft;

import org.bukkit.block.Block;

/**
 * Creates an instance of the correct pattern type from an arrangement of blocks in the world
 * @author Martin
 *
 */
public class PatternMatcher
{
	private Object lock = new Object();
	
	private HashMap<Integer, List<PatternFactoryTuple>> patternsContainingBlock = new HashMap<Integer, List<PatternFactoryTuple>>();
	
	public BasePatternInstance Match(Block b)
	{
		List<PatternFactoryTuple> potentialPatterns;
		synchronized (lock)
		{
			potentialPatterns = patternsContainingBlock.get(b.getTypeId());
		}
		
		if (potentialPatterns == null)
			return null;
		
		PatternFactoryTuple result = FindPatternAround(b, potentialPatterns);
		
		if (result == null)
			return null;
		
		return result.getFactory().Create();
	}
	
	/**
	 * Given a set of potential patterns, returns the only one which currently matches this block and all the ones around it
	 * @param b
	 * @param potentials
	 * @return
	 */
	private PatternFactoryTuple FindPatternAround(Block b, List<PatternFactoryTuple> potentials)
	{
		for (PatternFactoryTuple p : potentials)
		{
			if (p.getPattern().Matches(b))
				return p;
		}
		
		return null;
	}
	
	public void AddPattern(BasePattern pattern)
	{
		AddPattern(pattern, pattern);
	}
	
	/**
	 * Add a new type of pattern to the pattern matcher
	 * @param pattern
	 * @param factory
	 */
	public void AddPattern(BasePattern pattern, IPatternInstanceFactory factory)
	{
		CoulombCraft.getLogger().info("CoulombCraft adding pattern " + pattern);
		
		PatternFactoryTuple tuple = new PatternFactoryTuple(pattern, factory);
		
		//Add all blocks used in this pattern into the interesting blocks collection
		synchronized (lock)
		{
			int[][] p = pattern.getPattern();
			
			for (int i = 0; i < p.length; i++)
			{
				for (int j = 0; j < p[i].length; j++)
				{
					List<PatternFactoryTuple> list = patternsContainingBlock.get(p[i][j]);
					if (list == null)
					{
						list = new ArrayList<PatternFactoryTuple>();
						patternsContainingBlock.put(p[i][j], list);
					}
					
					if (!list.contains(tuple))
						list.add(tuple);
				}
			}
		}
	}
	
	private class PatternFactoryTuple
	{
		private BasePattern pattern;
		private IPatternInstanceFactory factory;
		
		public PatternFactoryTuple(BasePattern pattern, IPatternInstanceFactory factory)
		{
			this.pattern = pattern;
			this.factory = factory;
		}

		public BasePattern getPattern()
		{
			return pattern;
		}
		
		public IPatternInstanceFactory getFactory()
		{
			return factory;
		}
	}
}
