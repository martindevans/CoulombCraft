package me.martindevans.CoulombCraft;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Utility
{
	public static Block[] AdjacentBlocksNoDiagonals(Block b)
	{
		return new Block[]
		{
			b.getRelative(BlockFace.NORTH),
			b.getRelative(BlockFace.EAST),
			b.getRelative(BlockFace.SOUTH),
			b.getRelative(BlockFace.WEST),
			b.getRelative(BlockFace.UP),
			b.getRelative(BlockFace.DOWN),
		};
	}
}
