package me.martindevans.CoulombCraft.Listeners;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.IDeserialize;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;

public class DeserializerChunkListener extends WorldListener
{
	ArrayList<IDeserialize> deserializers = new ArrayList<IDeserialize>();
	
	CoulombCraft plugin;
	
	public DeserializerChunkListener(CoulombCraft plugin)
	{
		this.plugin = plugin;
	}
	
	public void AddNewDeserializer(IDeserialize d)
	{
		deserializers.add(d);
	}
	
	@Override
	public void onChunkLoad(ChunkLoadEvent event)
	{
		if (event.isNewChunk())
			return;
		
		Chunk c = event.getChunk();
		c.load();
		
		String query = "SELECT * FROM Important_Blocks WHERE " +
				"x >= " + c.getX() + " AND x < " + c.getX() + 16 + " AND " +
				"z >= " + c.getZ() + " AND z < " + c.getZ() + 16;
		final ResultSet rs = plugin.getDbConnector().sqlSafeQuery(query);
		
		if (rs == null)
			return;
		
		int count = 0;
		try
		{
			while (rs.next())
			{
				count++;
				
				int x = rs.getInt("x");
				int y = rs.getInt("y");
				int z = rs.getInt("z");
				
				Block b = c.getBlock(x, y, z);
				SpoutBlock sb = SpoutManager.getMaterialManager().getSpoutBlock(b);
				
				for (IDeserialize d : deserializers)
					d.DeserializeBlock(sb);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
