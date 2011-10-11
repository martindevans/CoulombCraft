package coulombCraft.Signs;
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
	public Double Query(String variable);
	
	/**
	 * Gets a value indicating if this queryable understands the given query
	 * @param variable
	 * @return
	 */
	public boolean CanAnswer(String variable);
}
