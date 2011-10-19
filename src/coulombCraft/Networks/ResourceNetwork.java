package coulombCraft.Networks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.IDatabaseListener;

public class ResourceNetwork implements IDatabaseListener
{
	public final long Id;
	public final ResourceNetworkManager manager;
	
	Map<String, Resource> resources = new HashMap<String, Resource>();
	
	public ResourceNetwork(long id, ResourceNetworkManager manager)
	{
		Id = id;
		this.manager = manager;
		
		manager.Database.AddDatabaseListener(this);
	}
	
	public void AddBlock(int x, int y, int z, String world)
	{
		manager.AddNetworkBlock(x, y, z, world, Id);
	}
	
	public void RemoveBlock(int x, int y, int z, String world)
	{
		manager.RemoveNetworkBlock(x, y, z, world, Id);
	}
	
	ResourceNetwork MergeInto(ResourceNetwork network)
	{
		if (network.equals(this))
			return this;
		
		UpdateResourcesFromDatabase();
		
		for (Resource slave : resources.values())
		{
			Resource master = network.GetResource(slave.getName());

			master.Add(slave.getAmount());
			slave.MakeMirror(master);
			slave.Delete();
		}
		
		manager.Database.getDbConnector().updateSafeQuery("UPDATE " + ResourceNetworkManager.NETWORK_BLOCK_TABLE + " SET networkId = " + network.Id	+ " WHERE networkId = " + Id);		
		manager.Database.getDbConnector().deleteSafeQuery("DELETE FROM " + ResourceNetworkManager.NETWORK_TABLE + " WHERE id = " + Id);
		manager.cachedNetworkInstances.remove(Id);
		
		return network;
	}
	
	public Resource GetResource(String name)
	{
		Resource r = resources.get(name);
		
		if (r == null)
		{
			r = new Resource(name);
			resources.put(name, r);
		}
		
		return r;
	}
	
	public Resource[] GetResources()
	{
		UpdateResourcesFromDatabase();
		return resources.values().toArray(new Resource[0]);
	}
	
	private void UpdateResourcesFromDatabase()
	{
		ResultSet rs = manager.Database.getDbConnector().sqlSafeQuery("SELECT name, quantity FROM " + ResourceNetworkManager.NETWORK_RESOURCE_TABLE + " WHERE networkId = " + Id);
		
		try
		{
			while (rs.next())
			{
				String name = rs.getString("name");
				Resource r = resources.get(name);
				
				if (r == null)
				{
					r = new Resource(name);
					resources.put(name, r);
				}
				
				r.Amount = rs.getDouble("quantity");
			}
		}
		catch (SQLException e)
		{
			CoulombCraft.getLogger().info(e.toString());
		}
	}
	
	@Override
	public void Flush()
	{
		for (Resource r : resources.values())
			r.WriteToDatabase();
	}
	
	public class Resource
	{
		private String Name;
		private Double Amount;
		private Resource masterCopy = null;
		
		public String getName()
		{
			if (masterCopy != null)
				return masterCopy.getName();
			
			return Name;
		}

		public Double getAmount()
		{
			if (masterCopy != null)
				return masterCopy.getAmount();
			
			return Amount;
		}
		
		public void Add(double amount)
		{
			Amount += amount;
		}
		
		public Resource getMasterResource()
		{
			if (masterCopy != null)
				return masterCopy;
			
			return this;
		}
		
		public Resource(String name)
		{
			Name = name;
			UpdateFromDatabase();
		}
		
		public void UpdateFromDatabase()
		{
			if (masterCopy != null)
				masterCopy.UpdateFromDatabase();
			else
			{
				ResultSet rs = manager.Database.getDbConnector().sqlSafeQuery("SELECT quantity FROM " + ResourceNetworkManager.NETWORK_RESOURCE_TABLE + " WHERE networkId = " + Id + " AND name = '" + Name + "'");
				
				try
				{
					if (rs == null || !rs.next())
					{
						//name VARCHAR(100), quantity DOUBLE, networkId INTEGER, PRIMARY KEY(name, networkId), FOREIGN KEY(networkId) REFERENCES " + NETWORK_TABLE + "(id)";
						
						Amount = (double)0;
						manager.Database.getDbConnector().insertSafeQuery("INSERT INTO " + ResourceNetworkManager.NETWORK_RESOURCE_TABLE + " VALUES('" + Name + "',0," + Id + ")");
					}
					else
						Amount = rs.getDouble("quantity");
				}
				catch (SQLException e)
				{
					CoulombCraft.getLogger().info(e.toString());
					Amount = (double)0;
				}
			}
		}
		
		public void WriteToDatabase()
		{
			manager.Database.getDbConnector().updateSafeQuery("UPDATE " + ResourceNetworkManager.NETWORK_RESOURCE_TABLE + " SET quantity = " + getAmount() + " WHERE networkId = " + Id + " AND name = '" + Name + "'");
		}
		
		public void Delete()
		{
			manager.Database.getDbConnector().deleteSafeQuery("DELETE FROM " + ResourceNetworkManager.NETWORK_RESOURCE_TABLE + " WHERE networkId = " + Id + " AND name = '" + Name + "'");
		}
		
		void MakeMirror(Resource master)
		{
			masterCopy = master;
		}
	}
}
