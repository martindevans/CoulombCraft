package me.martindevans.CoulombCraft;

import java.util.logging.Logger;

import me.martindevans.CoulombCraft.Listeners.*;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CoulombCraft extends JavaPlugin
{	
	private Logger logger;
	private PluginManager pluginManager;
	
	public void onEnable()
	{ 
		 logger = Logger.getLogger("Minecraft");
		 
		 //enable stuff
		 pluginManager = this.getServer().getPluginManager();
		 RegisterListeners();
		 
		 logger.info("Coulomb Craft has been loaded");
	}
	 
	private void RegisterListeners()
	{
		pluginManager.registerEvent(Event.Type.BLOCK_PLACE, new CoulombBlockListener(), Event.Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.BLOCK_BREAK, new CoulombBlockListener(), Event.Priority.Normal, this);
		
		pluginManager.registerEvent(Event.Type.REDSTONE_CHANGE, new CoulombRedstoneListener(), Event.Priority.Normal, this);
	}
	
	public void onDisable()
	{ 
		//disable stuff
		
		logger.info("Coulomb Craft has been unloaded");
	}
}
