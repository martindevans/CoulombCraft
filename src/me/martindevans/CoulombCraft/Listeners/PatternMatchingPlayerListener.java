package me.martindevans.CoulombCraft.Listeners;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;
import me.martindevans.CoulombCraft.Patterns.PatternMatcher;

import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerListener;

public class PatternMatchingPlayerListener extends PlayerListener
{
	CoulombCraft plugin;
	PatternMatcher matcher;
	
	public PatternMatchingPlayerListener(CoulombCraft plugin)
	{
		this.plugin = plugin;
		this.matcher = plugin.getPatternMatcher();
	}
	
	@Override
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event)
	{
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{				
			public void run()
			{
				Block blockCreated = event.getBlockClicked().getRelative(event.getBlockFace());
				
				BasePatternInstance instance = matcher.Match(blockCreated);
		        
		        if (instance != null)
		        	event.getPlayer().sendMessage("[Coulomb]Created " + instance.toString());
			}
		}, 4);
	}
}
