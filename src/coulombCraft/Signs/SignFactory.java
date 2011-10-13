package coulombCraft.Signs;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Utility;

public class SignFactory
{
	public static void MakeSignFromStrings(String[] input, Block b, Player p, CoulombCraft plugin)
	{		
		if (!input[0].equalsIgnoreCase("[coulomb]"))
			return;
		
		String operation = input[2].toLowerCase();
		
		boolean success = false;
		if (operation.equalsIgnoreCase("display"))
			success = CreateDisplay(input, b, p, plugin);
		else if (operation.equalsIgnoreCase("<"))
			success = CreateLessThanOperator(input, b, p, plugin);
		else if (operation.equalsIgnoreCase("<"))
			success = CreateGreaterThanOperator(input, b, p, plugin);
		
		if (!success)
		{
			Sign s = (Sign)b.getState();
			s.setLine(0, "");
			s.setLine(1, "");
			s.setLine(2, "");
			s.setLine(3, "");
			s.update();
		}
	}

	private static IQueryable FindQueryable(CoulombCraft plugin, Player p, Block b, String variable)
	{
		IQueryable queryable = plugin.getQueryProvider().GetAnyAdjacentQueryable(b.getLocation(), variable);
		
		if (queryable == null)
		{
			p.sendMessage("[Coulomb]No queryable blocks nearby");
			return null;
		}
		
		if (!queryable.CanAnswer(variable))
		{
			p.sendMessage("[Coulomb]Blocks do not understand \"" + variable + "\"");
			return null;
		}
		
		return queryable;
	}
	
	private static IQueryable ParseAsConstantOrVariable(CoulombCraft plugin, Player p, Block b, final String input)
	{
		try
		{
			final double d = Double.parseDouble(input);
			
			//Return a queryable which always returns this constant value
			return new IQueryable()
			{

				@Override
				public String Query(String variable)
				{
					if (variable.equals(input))
						return "" + d;
					else
						return null;
				}

				@Override
				public boolean CanAnswer(String variable)
				{
					return variable.equals(input);
				}
			};
		}
		catch (NumberFormatException ex)
		{
			//return an actual queryable
			return FindQueryable(plugin, p, b, input);
		}
	}
	
	private static boolean CreateDisplay(String[] input, final Block b, Player p, final CoulombCraft plugin)
	{
		final String variable = input[1];
		final IQueryable queryable = FindQueryable(plugin, p, b, variable);
		
		if (queryable == null)
			return false;
		
		SignUpdateTask task = new SignUpdateTask(plugin.getServer().getScheduler(), b, new Runnable()
		{
			public void run()
			{
				Sign sign = (Sign)b.getState();
				String value = queryable.Query(variable);
				
				sign.setLine(3, value);
				sign.update();
			}
		});
		final int taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, task, 5, plugin.getConfiguration().getInt("Core.Sign Update Ticks", 20));
		task.AssociateTaskId(taskId);

		return true;
	}

	private static boolean CreateLessThanOperator(String[] input, final Block b, final Player p, final CoulombCraft plugin)
	{		
		final String name1 = input[1];
		final IQueryable arg1 = ParseAsConstantOrVariable(plugin, p, b, name1);
		
		final String name2 = input[3];
		final IQueryable arg2 = ParseAsConstantOrVariable(plugin, p, b, name2);
		
		final Block[] adjacent = Utility.AdjacentBlocksNoDiagonals(b);
		
		SignUpdateTask task = new SignUpdateTask(plugin.getServer().getScheduler(), b, new Runnable()
		{
			public void run()
			{
				Double value1 = Double.parseDouble(arg1.Query(name1));
				Double value2 = Double.parseDouble(arg2.Query(name2));
				
				if (value1 < value2)
				{
					for (Block adj : adjacent)
					{
						SpoutManager.getMaterialManager().getSpoutBlock(adj).setBlockPowered(true);
						adj.getState().update();
					}
				}
				else
				{
					for (Block adj : adjacent)
					{
						SpoutManager.getMaterialManager().getSpoutBlock(adj).setBlockPowered(false);
						adj.getState().update();
					}
				}
			}
		});
		final int taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, task, 5, plugin.getConfiguration().getInt("Core.Sign Update Ticks", 20));
		task.AssociateTaskId(taskId);

		return true;
	}
	
	private static boolean CreateGreaterThanOperator(String[] input, final Block b, Player p, final CoulombCraft plugin)
	{
		final String name1 = input[1];
		final IQueryable arg1 = ParseAsConstantOrVariable(plugin, p, b, name1);
		
		final String name2 = input[3];
		final IQueryable arg2 = ParseAsConstantOrVariable(plugin, p, b, name2);
		
		final Block[] adjacent = Utility.AdjacentBlocksNoDiagonals(b);
		
		SignUpdateTask task = new SignUpdateTask(plugin.getServer().getScheduler(), b, new Runnable()
		{
			public void run()
			{
				Double value1 = Double.parseDouble(arg1.Query(name1));
				Double value2 = Double.parseDouble(arg2.Query(name2));
				
				if (value1 > value2)
				{
					for (Block adj : adjacent)
					{
						SpoutManager.getMaterialManager().getSpoutBlock(adj).setBlockPowered(true);
						adj.getState().update();
					}
				}
				else
				{
					for (Block adj : adjacent)
					{
						SpoutManager.getMaterialManager().getSpoutBlock(adj).setBlockPowered(false);
						adj.getState().update();
					}
				}
			}
		});
		final int taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, task, 5, plugin.getConfiguration().getInt("Core.Sign Update Ticks", 20));
		task.AssociateTaskId(taskId);

		return true;
	}
}
