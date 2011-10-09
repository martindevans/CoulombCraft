package me.martindevans.CoulombCraft.Listeners;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;
import me.martindevans.CoulombCraft.Patterns.PatternMatcher;

import org.bukkit.Material;
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
        Material mat = block.getType();

        player.sendMessage("You placed a block with ID : " + mat + " @ " + block.getLocation().getBlockX() + "," + block.getLocation().getBlockZ());
        
        BasePatternInstance instance = matcher.Match(block);
        
        player.sendMessage((instance == null) ? "pattern is null" : "Pattern is " + instance.getClass());
	}
}
