package me.martindevans.CoulombCraft;
/**
 * Returns values in response to query strings
 * @author Martin
 *
 */
public interface IQueryable
{
	/**
	 * Executes a query
	 * @param queryString The question being asked
	 * @return A value, or null if no answer can be found
	 */
	public Double Query(String queryString);
}
