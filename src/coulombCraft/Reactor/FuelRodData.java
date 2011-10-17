package coulombCraft.Reactor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Location;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Database;
import me.martindevans.CoulombCraft.IDatabaseListener;

public class FuelRodData implements IDatabaseListener
{	
	private float heat;
	private boolean meltingDown = false;
	private final int x;
	private final int y;
	private final int z;
	private final String world;
	
	private Database database;
	public static final String TABLE_NAME = "FuelRodData";
	public static final String TABLE_LAYOUT = "heat FLOAT, xPos INTEGER, yPos INTEGER, zPos INTEGER, world VARCHAR(100)";
	
	public float getHeat()
	{
		return heat;
	}

	public void setHeat(float heat)
	{
		this.heat = heat;
	}

	public boolean isIsMeltingDown()
	{
		return meltingDown;
	}

	public void setIsMeltingDown(boolean isMeltingDown)
	{
		meltingDown = isMeltingDown;
	}
	
	public FuelRodData(Location coreLocation, CoulombCraft plugin)
	{
		x = coreLocation.getBlockX();
		y = coreLocation.getBlockY();
		z = coreLocation.getBlockZ();
		world = coreLocation.getWorld().getName();
		
		database = plugin.getSqliteDatabase();
		
		database.AddDatabaseListener(this);
		database.getDbConnector().ensureTable(TABLE_NAME, TABLE_LAYOUT);
		
		if (!TryLoad())
			Insert();
		Flush();
	}
	
	private boolean TryLoad()
	{
		ResultSet rs =  database.getDbConnector().sqlSafeQuery("SELECT heat FROM " + TABLE_NAME + " WHERE xPos = " + x + " AND yPos = " + y + " AND zPos = " + z + " AND world = '" + world + "'");
		try
		{
			if (!rs.next())
				return false;
			
			heat = rs.getFloat("heat");
			
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void Insert()
	{
		database.getDbConnector().insertSafeQuery("INSERT INTO " + TABLE_NAME + " VALUES (" + heat + ", " + x + ", " + y + ", " + z + ", '" + world + "')");
	}
	
	public void Destroy()
	{
		int i = database.getDbConnector().deleteSafeQuery("DELETE FROM " + TABLE_NAME + " WHERE xPos = " + x + " AND yPos = " + y + " AND zPos = " + z + " AND world = '" + world + "'");
		CoulombCraft.getLogger().info("Removing rod from database " + i);
		
		Unload();
	}
	
	public void Unload()
	{
		database.RemoveDatabaseListener(this);
	}

	@Override
	public void Flush()
	{
		database.getDbConnector().updateSafeQuery("UPDATE " + TABLE_NAME + " SET heat = " + heat + " WHERE xPos = " + x + " AND yPos = " + y + " AND zPos = " + z + " AND world = '" + world + "'");
	}
}
