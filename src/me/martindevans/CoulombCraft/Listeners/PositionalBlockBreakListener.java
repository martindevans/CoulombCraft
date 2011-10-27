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
	HashMap<Location, ArrayList<IBreakListener>> listeners = new HashMap<Location, ArrayList<IBreakListener>>();
	
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
		
		List<IBreakListener> correctlyPositionedListeners = GetList(l, false);
		
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
	
	public void registerListener(IBreakListener listener, Location location)
	{
		GetList(location, true).add(listener);
	}
	
	public void unregisterListener(IBreakListener listener, Location l)
	{
		ArrayList<IBreakListener> list = GetList(l, false);
		if (list != null)
		{
			list.remove(listener);
			
			if (list.size() == 0)
			{
				listeners.remove(l);
			}
		}
	}
	
	private ArrayList<IBreakListener> GetList(Location location, boolean create)
	{
		ArrayList<IBreakListener> l = listeners.get(location);
		
		if (l == null)
		{
			if (!create)
				return null;
			
			l = new ArrayList<IBreakListener>();
			listeners.put(location, l);
		}
		
		return l;
	}

}
