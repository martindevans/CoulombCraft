package me.martindevans.CoulombCraft.Patterns;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.ITick;
import me.martindevans.CoulombCraft.Integer3;
import me.martindevans.CoulombCraft.Listeners.IBreakListener;
import me.martindevans.CoulombCraft.Listeners.PositionalBlockBreakListener;

import org.bukkit.block.Block;

import coulombCraft.Signs.IQueryable;
import coulombCraft.Signs.QueryProvider;

/**
 * A 2Darrangement of blocks which forms a pattern
 * @author Martin
 *
 */
public abstract class BasePatternInstance implements IBreakListener, ITick, IQueryable
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
		
		PositionalBlockBreakListener breakListener = plugin.getPositionalBreakListener();
		QueryProvider queryProvider = plugin.getQueryProvider();
		
		for (int i = 0; i < blocks.length; i++)
		{
			for (int j = 0; j < blocks[i].length; j++)
			{
				if (blocks[i][j] != null)
				{
					int x = blocks[i][j].getX();
					int y = blocks[i][j].getY();
					int z = blocks[i][j].getZ();
					
					breakListener.registerListener(this, x, y, z);
					queryProvider.RegisterQueryable(new Integer3(x, y, z), this);
				}
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
			
			PositionalBlockBreakListener breakListener = plugin.getPositionalBreakListener();
			QueryProvider queryProvider = plugin.getQueryProvider();
			
			for (int i = 0; i < blocks.length; i++)
			{
				for (int j = 0; j < blocks[i].length; j++)
				{
					if (blocks[i][j] != null)
					{
						int x = blocks[i][j].getX();
						int y = blocks[i][j].getY();
						int z = blocks[i][j].getZ();
						
						breakListener.unregisterListener(this, x, y, z);
						queryProvider.UnregisterQueryable(new Integer3(x, y, z));
					}
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

	@Override
	public boolean IsBreakable(Block b)
	{
		return true;
	}

	@Override
	public Double Query(String queryString)
	{
		return null;
	}

	@Override
	public boolean CanAnswer(String variable)
	{
		return false;
	}
}
