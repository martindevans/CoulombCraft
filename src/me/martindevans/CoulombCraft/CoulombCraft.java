package me.martindevans.CoulombCraft;

import java.util.logging.Logger;

import me.martindevans.CoulombCraft.Listeners.*;
import me.martindevans.CoulombCraft.Patterns.FuelRodPattern;
import me.martindevans.CoulombCraft.Patterns.PatternMatcher;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CoulombCraft extends JavaPlugin
{	
	private static Logger logger;
	private PluginManager pluginManager;
	private PatternMatcher patterns;
	
	public PatternMatcher getPatternMatcher() {
		return patterns;
	}
	
	public static Logger getLogger()
	{
		return logger;
	}
	
	public void onEnable()
	{
		logger = Logger.getLogger("Minecraft");
		
		 //enable stuff
		 pluginManager = this.getServer().getPluginManager();
		 LoadPatterns();
		 RegisterListeners();
		 RegisterUpdaters();
		 
		 logger.info("CoulombCraft has been loaded");
	}
	 
	private void LoadPatterns()
	{
		patterns = new PatternMatcher();
		
		patterns.AddPattern(new FuelRodPattern());
	}
	
	private void RegisterListeners()
	{
		PatternMatchBlockListener patternListener = new PatternMatchBlockListener(this);
		pluginManager.registerEvent(Event.Type.BLOCK_PLACE, patternListener, Event.Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.BLOCK_BREAK, patternListener, Event.Priority.Normal, this);
		
		pluginManager.registerEvent(Event.Type.REDSTONE_CHANGE, new CoulombRedstoneListener(), Event.Priority.Normal, this);
	}
	
	private void RegisterUpdaters()
	{
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() { public void run() { Tick(); }}, 0, 20);
	}
	
	private void Tick()
	{
		
	}
	
	public void onDisable()
	{ 
		//disable stuff
		
		logger.info("CoulombCraft has been unloaded");
	}

}
