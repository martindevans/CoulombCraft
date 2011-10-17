package me.martindevans.CoulombCraft;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.plugin.PluginManager;

import moc.MOCDBLib.DBConnector;
import moc.MOCDBLib.MOCDBLib;

public class Database
{
	private MOCDBLib db;
	private DBConnector dbConnector;
	
	public DBConnector getDbConnector()
	{
		return dbConnector;
	}
	public void setDbConnector(DBConnector dbConnector)
	{
		this.dbConnector = dbConnector;
	}
	
	private Set<IDatabaseListener> listeners = new HashSet<IDatabaseListener>();
	
	CoulombCraft plugin;
	
	public Database(CoulombCraft plugin)
	{
		this.plugin = plugin;
		
		PluginManager pm = plugin.getServer().getPluginManager();
		db = (MOCDBLib)pm.getPlugin("MOCDBLib");
		setDbConnector(db.getMineCraftDB("CoulombCraft", CoulombCraft.getLogger()));
	}

	public void FlushDatabase()
	{
		CoulombCraft.getLogger().info("Flushing CoulombCraft database (" + listeners.size() + " items)");
		
		for (IDatabaseListener l : listeners)
		{
			l.Flush();
		}
	}
	
	public void CloseDatabase()
	{		
		FlushDatabase();
		dbConnector.closeConnection();
		
		CoulombCraft.getLogger().info("Closed CoulombCraft database");
	}
	
	public void AddDatabaseListener(IDatabaseListener listener)
	{
		listeners.add(listener);
	}
	
	public void RemoveDatabaseListener(IDatabaseListener listener)
	{
		listener.Flush();
		listeners.remove(listener);
	}
}
