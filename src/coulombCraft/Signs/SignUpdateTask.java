package coulombCraft.Signs;

import me.martindevans.CoulombCraft.Database;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitScheduler;

public class SignUpdateTask implements Runnable
{
	BukkitScheduler scheduler;
	int taskId;
	Block block;
	Runnable updateTask;
	Database database;
	
	public static final String TABLE_NAME = "Signs";
	public static final String TABLE_LAYOUT = "line1 VARCHAR(100), line2 VARCHAR(100), line3 VARCHAR(100), line4 VARCHAR(100), xPos INTEGER, yPos INTEGER, zPos INTEGER, world VARCHAR(100)";
	
	public SignUpdateTask(BukkitScheduler scheduler, Block block, Database database, Runnable updateTask)
	{
		this.scheduler = scheduler;
		this.updateTask = updateTask;
		this.block = block;
		this.database = database;
	}
	
	public void AssociateTaskId(int id)
	{
		taskId = id;
		
		Sign state = (Sign)block.getState();
		database.getDbConnector().insertSafeQuery("INSERT INTO " + TABLE_NAME + " VALUES ('" + state.getLine(0) + "','" + state.getLine(1) + "','" + state.getLine(2) + "','" + state.getLine(3) + "'," + 
				block.getX() + "," + block.getY() + "," + block.getZ() + ",'" + block.getWorld().getName() + "')");
	}
	
	@Override
	public void run()
	{
		Material mat = block.getType();
		if (mat != Material.SIGN && mat != Material.SIGN_POST && mat != Material.WALL_SIGN)
		{
			scheduler.cancelTask(taskId);
			
			database.getDbConnector().deleteSafeQuery("DELETE FROM " + TABLE_NAME + " WHERE xPos = " + block.getX() + " AND yPos = " + block.getY() + " AND zPos = " + block.getZ() + " AND world = " + block.getWorld().getName());
		}
		else
			updateTask.run();
	}
	
}
