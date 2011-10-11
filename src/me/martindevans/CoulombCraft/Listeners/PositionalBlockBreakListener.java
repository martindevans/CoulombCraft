package me.martindevans.CoulombCraft.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockListener;

public class PositionalBlockBreakListener extends BlockListener
{
	HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<IBreakListener>>>> listeners = new HashMap<Integer, HashMap<Integer, HashMap<Integer, ArrayList<IBreakListener>>>>();
	
	@Override
	public void onBlockBreak(BlockBreakEvent event)
	{
		HandleEvent(event, event.getBlock());
	}
	
	public void onBlockBurn(BlockBurnEvent event)
	{
		HandleEvent(event, event.getBlock());
	}
	
	public void onBlockFade(BlockFadeEvent event)
	{
		HandleEvent(event, event.getBlock());
	}
	
	private void HandleEvent(Cancellable event, Block b)
	{
		if (event.isCancelled())
			return;
		
		Location l = b.getLocation();
		
		List<IBreakListener> correctlyPositionedListeners = GetList(l.getBlockX(), l.getBlockY(), l.getBlockZ(), false);
		
		if (correctlyPositionedListeners == null)
			return;
		
		if (CheckBreakable(correctlyPositionedListeners, b))
			OnBreak(correctlyPositionedListeners, b);
		else
		{
			event.setCancelled(true);
		}
	}
	
	private boolean CheckBreakable(List<IBreakListener> listeners, Block b)
	{
		boolean breakable = true;
		
		for (IBreakListener li : listeners)
		{
			breakable &= li.IsBreakable(b);
		}
		
		return breakable;
	}
	
	private void OnBreak(List<IBreakListener> listeners,  Block b)
	{		
		for (IBreakListener li : listeners)
		{
			li.OnBreak(b);
		}
	}
	
	public void registerListener(IBreakListener listener, int x, int y, int z)
	{
		GetList(x, y, z, true).add(listener);
	}
	
	public void unregisterListener(IBreakListener listener, int x, int y, int z)
	{
		//This leaks memory because empty lists and maps need to be cleaned up here
		GetList(x, y, z, false).remove(listener);
	}
	
	private ArrayList<IBreakListener> GetList(int x, int y, int z, boolean create)
	{
		HashMap<Integer, HashMap<Integer, ArrayList<IBreakListener>>> map1 = listeners.get(x);
		if (map1 == null)
		{
			if (!create)
				return null;
			map1 = new HashMap<Integer, HashMap<Integer, ArrayList<IBreakListener>>>();
			listeners.put(x, map1);
		}
		
		HashMap<Integer, ArrayList<IBreakListener>> map2 = map1.get(y);
		if (map2 == null)
		{
			if (!create)
				return null;
			map2 = new HashMap<Integer, ArrayList<IBreakListener>>();
			map1.put(y, map2);
		}
		
		ArrayList<IBreakListener> list = map2.get(z);
		if (list == null)
		{
			if (!create)
				return null;
			list = new ArrayList<IBreakListener>();
			map2.put(z, list);
		}
		
		return list;
	}

}
