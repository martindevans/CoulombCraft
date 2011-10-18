package me.martindevans.CoulombCraft;

import java.util.Set;

import org.bukkit.Chunk;

public interface IChunkListener
{
	public Set<Chunk> getChunks();
	
	public void ChunksUnloaded();
	
	public void ChunkLoaded(Chunk c);
}
