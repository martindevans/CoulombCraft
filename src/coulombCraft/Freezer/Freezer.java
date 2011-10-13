package coulombCraft.Freezer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;

public class Freezer extends BasePatternInstance
{
	Block coreBlock;
	
	public Freezer(CoulombCraft plugin, Block[][] blocks)
	{
		super(plugin, blocks);
		
		Location corner = blocks[0][0].getLocation();
		coreBlock = corner.getWorld().getBlockAt(corner.getBlockX() + 1, corner.getBlockY(), corner.getBlockZ() + 1);
	}

	public void Tick()
	{
		if (coreBlock.getType() == Material.WATER)
		{
			coreBlock.setType(Material.SNOW_BLOCK);
		}
		
		super.Tick();
	}
	
	@Override
	protected void OnPatternDestroyed()
	{
		
	}
}
