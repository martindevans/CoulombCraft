package me.martindevans.CoulombCraft;

import java.util.HashSet;
import java.util.logging.Logger;

import me.martindevans.CoulombCraft.Listeners.*;
import me.martindevans.CoulombCraft.Patterns.FuelRodPattern;
import me.martindevans.CoulombCraft.Patterns.PatternMatcher;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class CoulombCraft extends JavaPlugin
{	
	private static Logger logger;
	
	private PluginManager pluginManager;
	private PatternMatcher patterns;
	private Configuration config;
	private PositionalBlockBreakListener positionalBreakListener;
	
	public PatternMatcher getPatternMatcher() {
		return patterns;
	}
	
	public static Logger getLogger()
	{
		return logger;
	}
	
	public Configuration getConfiguration()
	{
		return config;
	}
	
	public PositionalBlockBreakListener getPositionalBreakListener()
	{
		return positionalBreakListener;
	}
	
	public void onEnable()
	{
		logger = Logger.getLogger("Minecraft");
		
		 //enable stuff
		 pluginManager = getServer().getPluginManager();
		 
		 config = super.getConfiguration();
		 LoadConfig();
		 
		 LoadPatterns();
		 RegisterListeners();
		 RegisterUpdaters();
		 
		 logger.info("CoulombCraft has been loaded");
	}
	 
	private void LoadConfig()
	{
	}
	
	private void LoadPatterns()
	{
		patterns = new PatternMatcher();
		
		patterns.AddPattern(new FuelRodPattern(this));
	}
	
	private void RegisterListeners()
	{
		pluginManager.registerEvent(Event.Type.BLOCK_PLACE, new PatternMatchBlockListener(this), Event.Priority.Normal, this);
		
		positionalBreakListener = new PositionalBlockBreakListener();
		pluginManager.registerEvent(Event.Type.BLOCK_BREAK, positionalBreakListener, Event.Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.BLOCK_BURN, positionalBreakListener, Event.Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.BLOCK_FADE, positionalBreakListener, Event.Priority.Normal, this);
		
		SignPlaceListener signPlaceListener = new SignPlaceListener(this);
		pluginManager.registerEvent(Event.Type.SIGN_CHANGE, signPlaceListener, Event.Priority.Normal, this);
		
		pluginManager.registerEvent(Event.Type.REDSTONE_CHANGE, new CoulombRedstoneListener(), Event.Priority.Normal, this);
	}
	
	private void RegisterUpdaters()
	{
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() { public void run() { Tick(); }}, 0, 5);
	}
	
	HashSet<ITick> tickListeners = new HashSet<ITick>();
	private void Tick()
	{
		for (Object t : tickListeners.toArray())
		{
			((ITick)t).Tick();
		}
	}
	
	public void AddTickListener(ITick listener)
	{
		tickListeners.add(listener);
	}
	
	public void RemoveTickListener(ITick listener)
	{
		tickListeners.remove(listener);
	}
	
	public void onDisable()
	{ 
		//disable stuff
		
		logger.info("CoulombCraft has been unloaded");
	}

	public static void Ignite(Block b)
	{
		if (b == null)
			return;
		
		Block up = b.getRelative(0, 1, 0);
		if (up.getTypeId() == 0)	//air
			up.setTypeId(51);		//fire
		
		Block down = b.getRelative(0, -1, 0);
		if (down.getTypeId() == 0)	//air
			down.setTypeId(51);		//fire
		
		Block left = b.getRelative(1, 0, 0);
		if (left.getTypeId() == 0)	//air
			left.setTypeId(51);		//fire
		
		Block right = b.getRelative(-1, 0, 0);
		if (right.getTypeId() == 0)	//air
			right.setTypeId(51);		//fire
		
		Block forwards = b.getRelative(0, 0, 1);
		if (forwards.getTypeId() == 0)	//air
			forwards.setTypeId(51);		//fire
		
		Block backwards = b.getRelative(0, 0, -1);
		if (backwards.getTypeId() == 0)	//air
			backwards.setTypeId(51);	//fire
	}
}
