package coulombCraft.Signs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;

import me.martindevans.CoulombCraft.CoulombCraft;

public class MasterQueryProvider implements IQueryProvider
{
	HashMap<Location, IQueryable> queryables = new HashMap<Location, IQueryable>();
	
	List<IQueryProvider> providers = new ArrayList<IQueryProvider>();
	
	public MasterQueryProvider(CoulombCraft plugin)
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
		
		for (IQueryProvider q : providers)
		{
			IQueryable answer = q.GetAnyAdjacentQueryable(location, query);
			if (answer != null)
				return answer;
		}
		
		return null;
	}
	
	public List<IQueryable> GetQueryables(Location location)
	{
		List<IQueryable> answer = new ArrayList<IQueryable>();
		
		IQueryable queryable = queryables.get(location);
		if (queryable != null)
			answer.add(queryable);
		
		for (IQueryProvider q : providers)
		{
			List<IQueryable> queryables = q.GetQueryables(location);
			if (queryables != null)
				answer.addAll(queryables);
		}
		
		return answer;
	}
	
	public void RegisterQueryable(Location position, IQueryable queryable)
	{
		queryables.put(position, queryable);
	}
	
	public void UnregisterQueryable(Location position)
	{
		queryables.remove(position);
	}
	
	public void RegisterQueryProvider(IQueryProvider provider)
	{
		providers.add(provider);
	}
}
