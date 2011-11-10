package me.martindevans.CoulombCraft.Patterns;

import java.util.HashSet;
import java.util.Set;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.IChunkListener;
import me.martindevans.CoulombCraft.ITick;
import me.martindevans.CoulombCraft.Listeners.IBreakListener;
import me.martindevans.CoulombCraft.Listeners.PositionalBlockBreakListener;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import coulombCraft.Signs.IQueryable;
import coulombCraft.Signs.MasterQueryProvider;

/**
 * A 2Darrangement of blocks which forms a pattern
 * @author Martin
 *
 */
public abstract class BasePatternInstance implements IBreakListener, ITick, IQueryable, IChunkListener
{
	protected Block[][] blocks;
	protected final CoulombCraft plugin;
	
	public Block[][] getBlocks()
	{
		return blocks;
	}
	
	private Set<Chunk> chunks = new HashSet<Chunk>();
	public Set<Chunk> getChunks()
	{
		return chunks;
	}

	boolean patternIntact = true;
	boolean patternUnloaded = false;
	
	public BasePatternInstance(CoulombCraft plugin, Block[][] blocks)
	{
		this.blocks = blocks;
		this.plugin = plugin;
		
		PositionalBlockBreakListener breakListener = plugin.getPositionalBreakListener();
		MasterQueryProvider queryProvider = plugin.getQueryProvider();
		
		for (int i = 0; i < blocks.length; i++)
		{
			for (int j = 0; j < blocks[i].length; j++)
			{
				if (blocks[i][j] != null)
				{
					Location location = blocks[i][j].getLocation();
					
					chunks.add(blocks[i][j].getChunk());
					
					breakListener.registerListener(this, location);
					queryProvider.RegisterQueryable(blocks[i][j].getLocation(), this);
				}
			}
		}
		
		plugin.getChunkWatcher().Add(this);
		plugin.AddTickListener(this);
	}
	
	public void ChunksUnloaded()
	{
		UnloadPattern();
	}
	
	@Override
	public void Tick()
	{
		if (!patternIntact && !patternUnloaded)
		{
			UnloadPattern();
		}
	}

	private void UnloadPattern()
	{
		patternUnloaded = true;
		
		plugin.RemoveTickListener(this);
		plugin.getChunkWatcher().Remove(this);
		
		PositionalBlockBreakListener breakListener = plugin.getPositionalBreakListener();
		MasterQueryProvider queryProvider = plugin.getQueryProvider();
		
		for (int i = 0; i < blocks.length; i++)
		{
			for (int j = 0; j < blocks[i].length; j++)
			{
				if (blocks[i][j] != null)
				{						
					Location location = blocks[i][j].getLocation();
					
					breakListener.unregisterListener(this, location);
					queryProvider.UnregisterQueryable(blocks[i][j].getLocation());
				}
			}
		}
		
		OnPatternDestroyed();
	}

	@Override
	public void OnBreak(Block b)
	{
		DestroyPattern();
	}
	
	protected void DestroyPattern()
	{
		patternIntact = false;
	}
	
	protected abstract void OnPatternDestroyed();

	@Override
	public boolean IsBreakable(Block b)
	{
		return true;
	}

	@Override
	public String Query(String queryString)
	{
		return null;
	}

	@Override
	public boolean CanAnswer(String variable)
	{
		return false;
	}

	@Override
	public void ChunkLoaded(Chunk c)
	{
	}
}
