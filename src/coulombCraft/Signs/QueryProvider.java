package coulombCraft.Signs;

import java.util.HashMap;

import org.bukkit.Location;

import me.martindevans.CoulombCraft.CoulombCraft;

public class QueryProvider
{
	HashMap<Location, IQueryable> queryables = new HashMap<Location, IQueryable>();
	
	public QueryProvider(CoulombCraft plugin)
	{
	}
	
	public IQueryable GetAnyAdjacentQueryable(Location location, String query)
	{		
		IQueryable queryable = queryables.get(location.clone().add(-1, 0, 0));
		if (queryable != null && queryable.CanAnswer(query))
			return queryable;
		
		queryable = queryables.get(location.clone().add(1, 0, 0));
		if (queryable != null && queryable.CanAnswer(query))
			return queryable;
		
		queryable = queryables.get(location.clone().add(0, -1, 0));
		if (queryable != null && queryable.CanAnswer(query))
			return queryable;
		
		queryable = queryables.get(location.clone().add(0, 1, 0));
		if (queryable != null && queryable.CanAnswer(query))
			return queryable;
		
		queryable = queryables.get(location.clone().add(0, 0, -1));
		if (queryable != null && queryable.CanAnswer(query))
			return queryable;
		
		queryable = queryables.get(location.clone().add(0, 0, 1));
		if (queryable != null && queryable.CanAnswer(query))
			return queryable;
		
		return null;
	}
	
	public IQueryable GetQueryable(Location location)
	{
		return queryables.get(location);
	}
	
	public void RegisterQueryable(Location position, IQueryable queryable)
	{
		queryables.put(position, queryable);
	}
	
	public void UnregisterQueryable(Location position)
	{
		queryables.remove(position);
	}
}
