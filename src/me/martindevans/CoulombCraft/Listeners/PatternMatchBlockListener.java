package me.martindevans.CoulombCraft.Listeners;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;
import me.martindevans.CoulombCraft.Patterns.PatternMatcher;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PatternMatchBlockListener extends BlockListener
{
	PatternMatcher matcher;
	
	public PatternMatchBlockListener(CoulombCraft plugin)
	{
		matcher = plugin.getPatternMatcher();
	}
	
	public void onBlockPlace(BlockPlaceEvent event)
	{		
        Player player = event.getPlayer();
        Block block = event.getBlock();

        BasePatternInstance instance = matcher.Match(block);
        
        if (instance != null)
        	player.sendMessage("Created " + instance.getClass().getName());
	}
}
