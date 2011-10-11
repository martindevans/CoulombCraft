package me.martindevans.CoulombCraft.Listeners;

import me.martindevans.CoulombCraft.CoulombCraft;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;

import coulombCraft.Signs.SignFactory;

public class SignPlaceListener extends BlockListener
{
	CoulombCraft plugin;
	
	public SignPlaceListener(CoulombCraft plugin)
	{
		this.plugin = plugin;
	}
	
	public void onSignChange(SignChangeEvent event)
	{
		final Block b = event.getBlock();
		final Player p = event.getPlayer();
		Material type = b.getType();
		
		if (type == Material.SIGN || type == Material.WALL_SIGN || type == Material.SIGN_POST)
		{
			final Sign s = ((Sign)b.getState());
			
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
			{
				public void run()
				{
					if (s.getLine(0).equalsIgnoreCase("[coulomb]"))
					{						
						SignFactory.MakeSignFromStrings(s.getLines(), b, p, plugin);
					}
				}
			}, 4);
		}
	}
}
