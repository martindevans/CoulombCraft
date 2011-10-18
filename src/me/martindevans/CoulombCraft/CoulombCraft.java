package me.martindevans.CoulombCraft;

import java.util.HashSet;
import java.util.logging.Logger;

import me.martindevans.CoulombCraft.Listeners.*;
import me.martindevans.CoulombCraft.Patterns.BasePattern;
import me.martindevans.CoulombCraft.Patterns.CablePattern;
import me.martindevans.CoulombCraft.Patterns.FuelRodPattern;
import me.martindevans.CoulombCraft.Patterns.FuelRodPattern2;
import me.martindevans.CoulombCraft.Patterns.MiningRigPattern;
import me.martindevans.CoulombCraft.Patterns.PatternMatcher;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import coulombCraft.Freezer.FreezerPattern;
import coulombCraft.Reactor.FuelRodData;
import coulombCraft.Recipes.StoneToWool;
import coulombCraft.Signs.QueryProvider;
import coulombCraft.Networks.ResourceNetworkManager;

public class CoulombCraft extends JavaPlugin
{	
	private static Logger logger = Logger.getLogger("Minecraft");
	
	private PluginManager pluginManager;
	private PatternMatcher patterns;
	private Configuration config;
	private PositionalBlockBreakListener positionalBreakListener;
	private QueryProvider queryProvider;
	private Database database;
	private ResourceNetworkManager resourceNetworkManager;
	private ChunkLoadListener chunkUnloadListener;
	
	public ChunkLoadListener getChunkWatcher()
	{
		return chunkUnloadListener;
	}
	
	public ResourceNetworkManager getResourceNetworkManager()
	{
		return resourceNetworkManager;
	}
	
	public PatternMatcher getPatternMatcher()
	{
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
	
	public QueryProvider getQueryProvider()
	{
		return queryProvider;
	}
	
	public PositionalBlockBreakListener getPositionalBreakListener()
	{
		return positionalBreakListener;
	}
	
	public Database getSqliteDatabase()
	{
		return database;
	}
	
	public void onEnable()
	{
		 //enable stuff
		 pluginManager = getServer().getPluginManager();
		 
		 config = super.getConfiguration();
		 LoadConfig();
		 
		 LoadDatabase();
	
		 patterns = new PatternMatcher();
		 resourceNetworkManager = new ResourceNetworkManager(this);
		 queryProvider = new QueryProvider(this);
		 positionalBreakListener = new PositionalBlockBreakListener();
		 chunkUnloadListener = new ChunkLoadListener();
		 
		 RegisterListeners();
		 LoadPatterns();
		 RegisterUpdaters();
		 
		 LoadRecipes();
		 for (World w : getServer().getWorlds())
			 chunkUnloadListener.InitialiseWorld(this, this.getServer(), w);
		 
		 logger.info("CoulombCraft has been loaded");
	}
	 
	private void LoadRecipes()
	{
		super.getServer().addRecipe(new StoneToWool());
	}
	
	private void LoadDatabase()
	{
		database = new Database(this);
		
		database.getDbConnector().ensureTable(FuelRodData.TABLE_NAME, FuelRodData.TABLE_LAYOUT);
	}
	
	private void LoadConfig()
	{
	}
	
	private void LoadPatterns()
	{		
		AddPattern(new FuelRodPattern(this));
		AddPattern(new FuelRodPattern2(this));
		AddPattern(new MiningRigPattern(this));
		AddPattern(new FreezerPattern(this));
		AddPattern(new CablePattern(this));
	}
	
	private void AddPattern(BasePattern pattern)
	{
		patterns.AddPattern(pattern);
		chunkUnloadListener.Add(pattern, true);
	}
	
	private void RegisterListeners()
	{
		pluginManager.registerEvent(Event.Type.BLOCK_PLACE, new PatternMatchBlockListener(this), Event.Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, new PatternMatchingPlayerListener(this), Event.Priority.Normal, this);
		
		pluginManager.registerEvent(Event.Type.BLOCK_BREAK, positionalBreakListener, Event.Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.BLOCK_BURN, positionalBreakListener, Event.Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.BLOCK_FADE, positionalBreakListener, Event.Priority.Normal, this);
		
		pluginManager.registerEvent(Event.Type.CHUNK_LOAD, chunkUnloadListener, Event.Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.CHUNK_UNLOAD, chunkUnloadListener, Event.Priority.Normal, this);
				
		SignPlaceListener signPlaceListener = new SignPlaceListener(this);
		pluginManager.registerEvent(Event.Type.SIGN_CHANGE, signPlaceListener, Event.Priority.Normal, this);
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
		database.CloseDatabase();
		
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
