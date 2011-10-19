package coulombCraft.Signs;

import java.util.HashMap;

import org.bukkit.Location;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Integer3;

public class QueryProvider
{
	HashMap<Integer3, IQueryable> queryables = new HashMap<Integer3, IQueryable>();
	
	public QueryProvider(CoulombCraft plugin)
	{
	}
	
	public IQueryable GetAnyAdjacentQueryable(Location location, String query)
	{
		IQueryable queryable = queryables.get(new Integer3(location.getBlockX() - 1, location.getBlockY(), location.getBlockZ()));
		if (queryable != null && queryable.CanAnswer(query))
			return queryable;
		
		queryable = queryables.get(new Integer3(location.getBlockX() + 1, location.getBlockY(), location.getBlockZ()));
		if (queryable != null && queryable.CanAnswer(query))
			return queryable;
		
		queryable = queryables.get(new Integer3(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ()));
		if (queryable != null && queryable.CanAnswer(query))
			return queryable;
		
		queryable = queryables.get(new Integer3(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ()));
		if (queryable != null && queryable.CanAnswer(query))
			return queryable;
		
		queryable = queryables.get(new Integer3(location.getBlockX(), location.getBlockY(), location.getBlockZ() - 1));
		if (queryable != null && queryable.CanAnswer(query))
			return queryable;
		
		queryable = queryables.get(new Integer3(location.getBlockX(), location.getBlockY(), location.getBlockZ() + 1));
		if (queryable != null && queryable.CanAnswer(query))
			return queryable;
		
		return null;
	}
	
	public IQueryable GetQueryable(Integer3 position)
	{
		return queryables.get(position);
	}
	
	public void RegisterQueryable(Integer3 position, IQueryable queryable)
	{
		queryables.put(position, queryable);
	}
	
	public void UnregisterQueryable(Integer3 position)
	{
		queryables.remove(position);
	}
}
