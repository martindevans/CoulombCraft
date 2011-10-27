package me.martindevans.CoulombCraft.Patterns;

import me.martindevans.CoulombCraft.CoulombCraft;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

import coulombCraft.Networks.Superconductor;

public class SuperconductorPattern extends BasePattern
{
	CoulombCraft plugin;
	
	public SuperconductorPattern(CoulombCraft plugin)
	{
		super(new int[][] { { Material.LAPIS_BLOCK.getId() } });
		
		this.plugin = plugin;
	}

	@Override
	public BasePatternInstance Create(Block[][] blocks)
	{
		return new Superconductor(plugin, blocks);
	}

	@Override
	protected void LoadStoredPatterns(Chunk c)
	{
		//Pattern will be picked up by the cable loading of stored patterns
	}
}
