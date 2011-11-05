package me.martindevans.CoulombCraft.Patterns;

import me.martindevans.CoulombCraft.CoulombCraft;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

import coulombCraft.MiningRig.MiningRig;

public class MiningRigPattern extends BasePattern implements IPatternInstanceFactory
{
	CoulombCraft plugin;
	
	public MiningRigPattern(CoulombCraft plugin)
	{
		super(new int[][]
			{
				{ 49, 49, 49, 49, 49, 49, 49, 49, 49 },
				{ 49, -1, -1, -1, -1, -1, -1, -1, 49 },
				{ 49, -1, -1, -1, -1, -1, -1, -1, 49 },
				{ 49, -1, -1, -1, -1, -1, -1, -1, 49 },
				{ 49, -1, -1, -1, -1, -1, -1, -1, 49 },
				{ 49, -1, -1, -1, -1, -1, -1, -1, 49 },
				{ 49, -1, -1, -1, -1, -1, -1, -1, 49 },
				{ 49, -1, -1, -1, -1, -1, -1, -1, 49 },
				{ 49, 49, 49, 49, 49, 49, 49, 49, 49 },
			});
		
		this.plugin = plugin;
	}
	
	public BasePatternInstance Create(Block[][] blocks)
	{
		return new MiningRig(plugin, blocks);
	}

	@Override
	protected void LoadStoredPatterns(Chunk c)
	{
		// TODO Auto-generated method stub
		
	}
}
