package coulombCraft.Signs;

import java.util.List;

import org.bukkit.Location;

public interface IQueryProvider
{
	public IQueryable GetAnyAdjacentQueryable(Location location, String query);
	
	public List<IQueryable> GetQueryables(Location location);
}
