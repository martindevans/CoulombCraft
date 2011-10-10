package me.martindevans.CoulombCraft.Patterns;

import org.bukkit.block.Block;

public interface IPatternInstanceFactory
{
	public BasePatternInstance Create(Block[] blocks);
}
