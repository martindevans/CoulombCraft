package me.martindevans.CoulombCraft.Listeners;

import org.bukkit.block.Block;

public interface IBreakListener
{
	public boolean IsBreakable(Block b);
	
	public void OnBreak(Block b);
}
