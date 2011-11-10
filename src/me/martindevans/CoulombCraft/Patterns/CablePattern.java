package me.martindevans.CoulombCraft.Patterns;

import me.martindevans.CoulombCraft.CoulombCraft;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CablePattern extends BasePattern
{
	CoulombCraft plugin;
	
	public CablePattern(CoulombCraft plugin)
	{
		super(new int[][] { { Material.WOOL.getId() } });
		
		this.plugin = plugin;
	}

	@Override
	public BasePatternInstance Create(Block[][] blocks)
	{
		//This will either create a new network, or merge this new block into an existing network
		plugin.getResourceNetworkManager().CreateNetwork(blocks[0][0].getX(), blocks[0][0].getY(), blocks[0][0].getZ(), blocks[0][0].getWorld().getName());
		
		return null;
	}

	@Override
	protected void LoadStoredPatterns(Chunk c)
	{
	}
}
