package me.martindevans.CoulombCraft.Listeners;

import me.martindevans.CoulombCraft.CoulombCraft;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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
		Block b = event.getBlock();
		Material type = b.getType();
		
		event.getPlayer().sendMessage("sign change");
		
		if (type == Material.SIGN || type == Material.WALL_SIGN || type == Material.SIGN_POST)
		{
			final Sign s = ((Sign)b.getState());
			
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable()
			{
				public void run()
				{
					if (s.getLine(0).equalsIgnoreCase("[coulomb]"))
					{						
						SignFactory.MakeSignFromStrings(s.getLines(), plugin);
					}
				}
			});
		}
	}
}
