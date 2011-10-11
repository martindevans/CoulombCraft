package me.martindevans.CoulombCraft.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.martindevans.CoulombCraft.Integer3;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockListener;

public class PositionalBlockBreakListener extends BlockListener
{
	HashMap<Integer3, ArrayList<IBreakListener>> listeners = new HashMap<Integer3, ArrayList<IBreakListener>>();
	
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
		ArrayList<IBreakListener> list = GetList(x, y, z, false);
		if (list != null)
		{
			list.remove(listener);
			
			if (list.size() == 0)
			{
				listeners.remove(new Integer3(x, y, z));
			}
		}
	}
	
	private ArrayList<IBreakListener> GetList(int x, int y, int z, boolean create)
	{
		Integer3 pos = new Integer3(x, y, z);
		ArrayList<IBreakListener> l = listeners.get(pos);
		
		if (l == null)
		{
			if (!create)
				return null;
			
			l = new ArrayList<IBreakListener>();
			listeners.put(pos, l);
		}
		
		return l;
	}

}
