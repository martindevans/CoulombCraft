package coulombCraft.Networks;

import java.text.DecimalFormat;

import org.bukkit.block.Block;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;

public class Superconductor extends BasePatternInstance
{
	ResourceNetwork network;

	public Superconductor(CoulombCraft plugin, Block[][] blocks)
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
		return false;
	}
	
	@Override
	public String toString()
	{
		return "Superconductor in network " + network.Id;
	}

	DecimalFormat sigfig2 = new DecimalFormat("0.0");
	public String Query(String variable)
	{
		if (variable.startsWith("res:"))
			return sigfig2.format(network.GetResource(variable.substring(4)).getAmount());
		
		return null;
	}
	
	public boolean CanAnswer(String variable)
	{
		return variable.startsWith("res:");
	}
}
