package me.martindevans.CoulombCraft.Patterns;

import org.bukkit.block.Block;

public class FuelRodPattern extends BasePattern implements IPatternInstanceFactory
{
	public FuelRodPattern()
	{
		super(new int[][]
			{
				{ -1, 20, -1 },
				{ 20, 12, 20 },
				{ -1, 20, -1 }
			});
	}
	
	public BasePatternInstance Create(Block[][] blocks)
	{
		return new Instance();
	}
	
	public class Instance extends BasePatternInstance
	{
		@Override
		public String toString()
		{
			return "Fuel Rod";
		}
	}
}
