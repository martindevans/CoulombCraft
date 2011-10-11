package me.martindevans.CoulombCraft.Patterns;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.ITick;
import me.martindevans.CoulombCraft.Listeners.IBreakListener;
import me.martindevans.CoulombCraft.Listeners.PositionalBlockBreakListener;

import org.bukkit.block.Block;

/**
 * A 2Darrangement of blocks which forms a pattern
 * @author Martin
 *
 */
public abstract class BasePatternInstance implements IBreakListener, ITick
{
	protected Block[][] blocks;
	protected final CoulombCraft plugin;
	
	public Block[][] getBlocks()
	{
		return blocks;
	}

	boolean patternIntact = true;
	boolean patternUnloaded = false;
	
	public BasePatternInstance(CoulombCraft plugin, Block[][] blocks)
	{
		this.blocks = blocks;
		this.plugin = plugin;
		
		PositionalBlockBreakListener l = plugin.getPositionalBreakListener();
		for (int i = 0; i < blocks.length; i++)
		{
			for (int j = 0; j < blocks[i].length; j++)
			{
				if (blocks[i][j] != null)
					l.registerListener(this, blocks[i][j].getX(), blocks[i][j].getY(), blocks[i][j].getZ());
			}
		}
		
		plugin.AddTickListener(this);
	}

	@Override
	public void Tick()
	{
		if (!patternIntact && !patternUnloaded)
		{
			patternUnloaded = true;
			
			plugin.RemoveTickListener(this);
			
			PositionalBlockBreakListener l = plugin.getPositionalBreakListener();
			for (int i = 0; i < blocks.length; i++)
			{
				for (int j = 0; j < blocks[i].length; j++)
				{
					if (blocks[i][j] != null)
						l.unregisterListener(this, blocks[i][j].getX(), blocks[i][j].getY(), blocks[i][j].getZ());
				}
			}
		}
	}

	@Override
	public void OnBreak(Block b)
	{
		DestroyPattern();
	}
	
	protected void DestroyPattern()
	{
		if (patternIntact)
		{
			patternIntact = false;
			OnPatternDestroyed();
		}
	}
	
	protected abstract void OnPatternDestroyed();
}
