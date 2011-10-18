package me.martindevans.CoulombCraft.Listeners;

import java.util.ArrayList;
import java.util.List;
import me.martindevans.CoulombCraft.IChunkListener;

import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.plugin.Plugin;

public class ChunkLoadListener extends WorldListener
{
	ArrayList<IChunkListener> listenersToUnloads = new ArrayList<IChunkListener>();
	List<IChunkListener> listenersToLoads = new ArrayList<IChunkListener>();
	
	@Override
	public void onChunkUnload(ChunkUnloadEvent event)
	{
		for (IChunkListener listener : listenersToUnloads.toArray(new IChunkListener[0]))
		{
			if (!listener.getChunks().contains(event.getChunk()))
				continue;
			listener.ChunksUnloaded();
		}
	}
	
	@Override
	public void onChunkLoad(ChunkLoadEvent event)
	{
		Chunk c = event.getChunk();
		
		for (IChunkListener l : listenersToLoads)
			l.ChunkLoaded(c);
	}
	
	public void Add(IChunkListener listener)
	{
		listenersToUnloads.add(listener);
	}
	
	public void Add(IChunkListener listener, boolean listenToLoad)
	{
		if (listenToLoad)
			listenersToLoads.add(listener);
		else
			Add(listener);
	}
	
	public void Remove(IChunkListener listener)
	{
		listenersToUnloads.remove(listener);
	}

	public void InitialiseWorld(Plugin p, Server s, World w)
	{
		final Chunk[] chunks = w.getLoadedChunks();
		
		s.getScheduler().scheduleSyncDelayedTask(p, CreateWorldScanLoopIteration(p, s, chunks, 0));
	}
	
	private Runnable CreateWorldScanLoopIteration(final Plugin p, final Server s, final Chunk[] chunks, final int index)
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				if (index >= chunks.length)
					return;
				
				onChunkLoad(new ChunkLoadEvent(chunks[index], false));
				
				s.getScheduler().scheduleSyncDelayedTask(p, CreateWorldScanLoopIteration(p, s, chunks, index + 1));
			}
		};
	}
}
