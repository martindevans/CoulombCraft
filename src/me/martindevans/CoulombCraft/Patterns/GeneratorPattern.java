package me.martindevans.CoulombCraft.Patterns;

import me.martindevans.CoulombCraft.CoulombCraft;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

import coulombCraft.Networks.ResourceNetwork;

public class GeneratorPattern extends BasePattern
{
	CoulombCraft plugin;
	
	public GeneratorPattern(CoulombCraft plugin)
	{
		super(new int[][] { { Material.LAPIS_BLOCK.getId() } });
		
		this.plugin = plugin;
	}

	@Override
	public BasePatternInstance Create(Block[][] blocks)
	{
		//This will either create a new network, or merge this new block into an existing network
		final ResourceNetwork network = plugin.getResourceNetworkManager().CreateNetwork(blocks[0][0].getX(), blocks[0][0].getY(), blocks[0][0].getZ(), blocks[0][0].getWorld().getName());
		
		return new BasePatternInstance(plugin, blocks)
		{
			@Override
			public String toString()
			{
				return "Generator in network " + network.Id;		
			}
			
			@Override
			protected void OnPatternDestroyed()
			{
			}
		};
	}

	@Override
	protected void LoadStoredPatterns(Chunk c)
	{
	}
}
