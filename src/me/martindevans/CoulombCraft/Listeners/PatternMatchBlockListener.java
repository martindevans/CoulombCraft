package me.martindevans.CoulombCraft.Listeners;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;
import me.martindevans.CoulombCraft.Patterns.PatternMatcher;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PatternMatchBlockListener extends BlockListener
{
	CoulombCraft plugin;
	PatternMatcher matcher;
	
	public PatternMatchBlockListener(CoulombCraft plugin)
	{
		this.plugin = plugin;
		this.matcher = plugin.getPatternMatcher();
	}
	
	public void onBlockPlace(BlockPlaceEvent event)
	{
        BasePatternInstance instance = matcher.Match(event.getBlock());
        
        if (instance != null)
        	event.getPlayer().sendMessage("[Coulomb]Created " + instance.toString());
	}
}
