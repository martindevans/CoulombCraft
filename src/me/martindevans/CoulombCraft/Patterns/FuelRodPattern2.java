package me.martindevans.CoulombCraft.Patterns;

import me.martindevans.CoulombCraft.CoulombCraft;

import org.bukkit.block.Block;

import coulombCraft.Reactor.FuelRod;

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
}