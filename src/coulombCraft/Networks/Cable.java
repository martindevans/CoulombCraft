package coulombCraft.Networks;

import org.bukkit.block.Block;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;

public class Cable extends BasePatternInstance
{
	ResourceNetwork network;
	
	public Cable(CoulombCraft plugin, Block[][] blocks)
	{
		super(plugin, blocks);
		
		network = plugin.getResourceNetworkManager().getNetworkByBlock(blocks[0][0].getX(), blocks[0][0].getY(), blocks[0][0].getZ(), blocks[0][0].getWorld().getName());
		
		if (network == null)
			network = plugin.getResourceNetworkManager().CreateNetwork(blocks[0][0].getX(), blocks[0][0].getY(), blocks[0][0].getZ(), blocks[0][0].getWorld().getName());
	}

	@Override
	protected void OnPatternDestroyed()
	{		
		network.RemoveBlock(blocks[0][0].getX(), blocks[0][0].getY(), blocks[0][0].getZ(), blocks[0][0].getWorld().getName());
	}

	@Override
	public boolean IsBreakable(Block b)
	{
		return network.GetResources().length == 0;
	}
	
	@Override
	public String toString()
	{
		return "Cable in network " + network.Id;
	}
}
