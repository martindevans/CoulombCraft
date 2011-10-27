package coulombCraft.Networks;

import java.text.DecimalFormat;

import org.bukkit.block.Block;

import coulombCraft.Networks.ResourceNetwork.Resource;
import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.TemperatureColourMap;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;

public class Cable extends BasePatternInstance
{
	ResourceNetwork network;
	Resource power;
	
	private float heatLastTick = 0;
	private float heat = 0;
	private float colourHeat;
	
	public Cable(CoulombCraft plugin, Block[][] blocks)
	{
		super(plugin, blocks);
		
		network = plugin.getResourceNetworkManager().getNetworkByBlock(blocks[0][0].getX(), blocks[0][0].getY(), blocks[0][0].getZ(), blocks[0][0].getWorld().getName());
		
		if (network == null)
			network = plugin.getResourceNetworkManager().CreateNetwork(blocks[0][0].getX(), blocks[0][0].getY(), blocks[0][0].getZ(), blocks[0][0].getWorld().getName());
		
		power = network.GetResource("power");
	}

	@Override
	public void Tick()
	{
		double generation = -plugin.getConfiguration().getDouble("Power.Cable Power Drain", 0.5) + 
				Math.pow(heat * plugin.getConfiguration().getDouble("Power.Heat Multiplier", 0.03), plugin.getConfiguration().getDouble("Power.Heat Exponent", 1.5))
				* plugin.getConfiguration().getDouble("Power.Generation Efficiency", 0.5);
		power.Add(generation);
		
		heatLastTick = heat;
		heat = 0;
		
		Block b = blocks[0][0];
		byte colour = TemperatureColourMap.GetColour(colourHeat, plugin.getConfiguration().getDouble("Heat.Cable Ignition Threshold", 900));
		if (b.getData() != colour)
			b.setData(colour);
		
		colourHeat = 0;
	}
	
	public void AddHeat(float heat)
	{		
		if (heat > colourHeat)
			colourHeat = heat;
		
		this.heat = heat;
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
		return "Cable in network " + network.Id;
	}

	DecimalFormat sigfig2 = new DecimalFormat("0.0");
	public String Query(String variable)
	{
		if (variable.equalsIgnoreCase("heat"))
			return sigfig2.format(heatLastTick);
		else if (variable.startsWith("res:"))
			return sigfig2.format(network.GetResource(variable.substring(4)).getAmount());
		
		return null;
	}
	
	public boolean CanAnswer(String variable)
	{
		return variable.equalsIgnoreCase("heat") ||
				variable.startsWith("res:");
	}
}
