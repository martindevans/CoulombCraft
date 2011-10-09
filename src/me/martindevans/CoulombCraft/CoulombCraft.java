package me.martindevans.CoulombCraft;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class CoulombCraft extends JavaPlugin
{	
	public void onEnable()
	{ 
		 logger = Logger.getLogger("Minecraft");
		 
		 //enable stuff
		 
		 logger.info("Coulomb Craft has been loaded");
	}
	 
	public void onDisable()
	{ 
		//disable stuff
		
		logger.info("Coulomb Craft has been unloaded");
	}
	
	private Logger logger;
}
