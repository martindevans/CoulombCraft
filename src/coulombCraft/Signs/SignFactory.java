package coulombCraft.Signs;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import me.martindevans.CoulombCraft.CoulombCraft;

public class SignFactory
{
	public static void MakeSignFromStrings(String[] input, Block b, Player p, CoulombCraft plugin)
	{
		if (!input[0].equalsIgnoreCase("[coulomb]"))
			return;
		
		String operation = input[2].toLowerCase();
		
		boolean success = false;
		if (operation.equalsIgnoreCase("display"))
		{
			success = CreateDisplay(input, b, p, plugin);
		}
		
		if (!success)
		{
			Sign s = (Sign)b.getState();
			s.setLine(0, "");
			s.setLine(1, "");
			s.setLine(2, "");
			s.setLine(3, "");
		}
	}
	
	private static boolean CreateDisplay(String[] input, final Block b, Player p, final CoulombCraft plugin)
	{
		final String variable = input[1];
		final IQueryable queryable = plugin.getQueryProvider().GetAnyAdjacentQueryable(b.getLocation(), variable);
		
		if (queryable == null)
		{
			p.sendMessage("[Coulomb]No queryable blocks nearby");
			return false;
		}
		
		if (!queryable.CanAnswer(variable))
		{
			p.sendMessage("[Coulomb]Blocks do not understand \"" + variable + "\"");
			return false;
		}
		
		SignUpdateTask task = new SignUpdateTask(plugin.getServer().getScheduler(), b, new Runnable()
		{
			public void run()
			{
				Sign sign = (Sign)b.getState();
				Double value = queryable.Query(variable);
				
				sign.setLine(3, "" + Math.round(value));
				sign.update();
			}
		});
		final int taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, task, 5, plugin.getConfiguration().getInt("Core.Sign Update Ticks", 20));
		task.AssociateTaskId(taskId);

		return true;
	}
}
