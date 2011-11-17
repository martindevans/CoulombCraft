package coulombCraft.Networks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import coulombCraft.Signs.IQueryable;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.IDatabaseListener;

public class ResourceNetwork implements IDatabaseListener, IQueryable
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
		name = name.toLowerCase();
		if (name.length() > 11)
			name = name.substring(0, 11);
		
		Resource r = resources.get(name);
		
		if (r == null)
		{
			r = new Resource(this, name);
			resources.put(name, r);
			
			r.UpdateFromDatabase();
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
				Resource r = GetResource(name);
				
				r.Amount = rs.getDouble("quantity");
			}
		}
		catch (SQLException e)
		{
			CoulombCraft.getLogger().info(e.toString());
		}
		finally
		{
			try
			{
				rs.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void Flush()
	{
		for (Resource r : resources.values())
			r.WriteToDatabase();
	}
	
	DecimalFormat sigfig2 = new DecimalFormat("0.0");
	
	@Override
	public String Query(String variable)
	{
		if (variable.startsWith("res:"))
			return sigfig2.format(GetResource(variable.substring(4)).getAmount());
		
		return null;
	}

	@Override
	public boolean CanAnswer(String variable)
	{
		return variable.startsWith("res:");
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
			if (masterCopy != null)
				masterCopy.Add(amount);
			else
			{
				Amount += amount;
				Amount = Math.max(0, Amount);
			}
		}
		
		public Resource getMasterResource()
		{
			if (masterCopy != null)
				return masterCopy;
			
			return this;
		}
		
		private ResourceNetwork parent;
		public ResourceNetwork getNetwork()
		{
			return parent;
		}
		
		public Resource(ResourceNetwork parent, String name)
		{
			this.parent = parent;
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
				finally
				{
					try
					{
						rs.close();
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		public void WriteToDatabase()
		{
			if (masterCopy != null)
				masterCopy.WriteToDatabase();
			else
				manager.Database.getDbConnector().updateSafeQuery("UPDATE " + ResourceNetworkManager.NETWORK_RESOURCE_TABLE + " SET quantity = " + getAmount() + " WHERE networkId = " + Id + " AND name = '" + Name + "'");
		}
		
		public void Delete()
		{
			manager.Database.getDbConnector().deleteSafeQuery("DELETE FROM " + ResourceNetworkManager.NETWORK_RESOURCE_TABLE + " WHERE networkId = " + Id + " AND name = '" + Name + "'");
		}
		
		void MakeMirror(Resource master)
		{
			masterCopy = master;
			masterCopy.Add(Amount);
		}
	}

}
