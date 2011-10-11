package coulombCraft.Reactor;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.config.Configuration;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.TemperatureColourMap;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;

public class FuelRod extends BasePatternInstance
{	
	double heat;
	double heatDelta;
	
	double heatProduction;
	
	boolean meltingDown = false;
	
	Configuration config;
	
	Random rand = new Random();
	
	Location coreLocation;
	
	public FuelRod(CoulombCraft plugin, Block[][] blocks)
	{
		super(plugin, blocks);
		
		this.config = plugin.getConfiguration();
		
		coreLocation = blocks[1][1].getLocation();
	}
	
	@Override
	public String toString()
	{
		return "Fuel Rod";
	}

	private double getHeatCapacity()
	{
		return config.getDouble("Fuel Rod.Heat Capacity", 200);
	}
	
	private double getMeltdownCapacity()
	{
		return config.getDouble("Fuel Rod.Meltdown Heat Capacity", 1000);
	}
	
	private double getHeatProduction()
	{
		return config.getDouble("Fuel Rod.Base Heat Production", 1);
	}
	
	private int getHeatRange()
	{
		return config.getInt("Heat.Range", 4);
	}
	
	private double getDestructionTemperatureThreshold()
	{
		return config.getDouble("Fuel Rod.Allow Destruction Temperature Threshold", 0.25);
	}
	
	@Override
	public void Tick()
	{
		heatProduction = getHeatProduction();
		
		UpdateNearbyBlocks();
		
		heat += heatDelta + heatProduction;
		heat = Math.max(0, heat);
		
		heatDelta = 0;
		
		if (heat > getHeatCapacity())
		{
			//meltdown
			meltingDown = true;
			
			if (rand.nextDouble() < 0.1)
			{
				for (int i = 0; i < blocks.length; i++)
				{
					for (int j = 0; j < blocks[i].length; j++)
					{
						CoulombCraft.Ignite(blocks[i][j]);
					}
				}
			}
			
			if (heat >= getMeltdownCapacity())
			{
				for (int i = 0; i < blocks.length; i++)
				{
					for (int j = 0; j < blocks[i].length; j++)
					{
						if (blocks[i][j] != null)
							blocks[i][j].setType(Material.LAVA);
					}
				}
				
				DestroyPattern();
			}
		}
		
		super.Tick();
	}

	private void UpdateNearbyBlocks()
	{
		World world = coreLocation.getWorld();
		int range = getHeatRange();
		
		for (int i = coreLocation.getBlockX() - range; i < coreLocation.getBlockX() + range + 1; i++)
		{
			for (int j = coreLocation.getBlockY() - range; j < coreLocation.getBlockY() + range + 1; j++)
			{
				for (int k = coreLocation.getBlockZ() - range; k < coreLocation.getBlockZ() + range + 1; k++)
				{
					Block b = world.getBlockAt(i, j, k);
					int type = b.getTypeId();
					
					if (type == Material.AIR.getId())
						continue;
					
					switch (type)
					{
						case 0:		//air
							continue;
						case 35:	//wool
						{
							//temp code to change colour
							b.setData(TemperatureColourMap.GetColour(heat, getHeatCapacity()));
							
							if (meltingDown)
								CoulombCraft.Ignite(b);
							
							//TODO: Increase temperature of wool
							continue;
						}
						case 17:	//Wood
						case 5:		//Logs
						case 18:	//Leaves
						case 87:	//Netherrack
						{
							IgniteWood(world, b, i, j, k);
							continue;
						}
						case 8:		//water (flowing)
						{
							if (j != coreLocation.getBlockY())
								continue;
							ApplyWaterCooling(world, b, i, j, k);
							continue;
						}
						case 9:		//water (still)
						{
							ApplyWaterCooling(world, b, i, j, k);
							continue;
						}
						case 79:	//Ice
						case 80:	//Snow block
						{
							ApplyColdBlockCooling(world, b, i, j, k);
							continue;
						}
						case 51:	//Fire
						{
							heatDelta += config.getDouble("Fuel Rod.Fire Source Bonus", 5);
							continue;
						}
						case 10:	//Lava (flowing)
						case 11:	//Lava (still)
						{
							//Increase core temperature
							heatProduction *= config.getDouble("Fuel Rod.Lava Source Bonus", 1.5);
							continue;
						}
						case 12:	//Sand
						{
							heatProduction *= config.getDouble("Fuel Rod.Sand Damp Multiplier", 0.75);
							continue;
						}
						case 42:	//Iron block
						{
							heatProduction *= config.getDouble("Fuel Rod.Iron Damp Multiplier", 0.6);
							continue;
						}
					}
				}
			}
		}
	}
	
	private void IgniteWood(World w, Block b, int x, int y, int z)
	{
		if (heat > config.getDouble("Heat.Wood Ignition Threshold", 3) && rand.nextDouble() <= config.getDouble("Heat.Wood Ignition Chance", 0.1))
		{
			CoulombCraft.Ignite(b);
		}
	}
	
	private void ApplyWaterCooling(World w, Block b, int x, int y, int z)
	{
		double chance = Math.pow(heat / getHeatCapacity(), 4);
		double randNumber = rand.nextDouble();
		if (randNumber < chance)
		{
			heatDelta -= config.getDouble("Fuel Rod.Water Heat Reduction", 0.8);
			
			if (heat > config.getDouble("Heat.Water Evaporation Threshold", 5))
				b.setTypeId(0);
			
			w.playEffect(b.getLocation(), Effect.EXTINGUISH, 0);
		}
	}
	
	private void ApplyColdBlockCooling(World w, Block b, int x, int y, int z)
	{
		double randNumber = rand.nextDouble();
		
		double reduction = config.getDouble("Fuel Rod.Ice Cooling Percentage", 0.05);
		heatDelta -= config.getDouble("Fuel Rod.Ice Heat Reduction", reduction);
		
		//turn it into water
		if (randNumber > 1 / heat)
			b.setType(Material.WATER);
	}
	
	@Override
	public boolean IsBreakable(Block b)
	{
		return heat < getHeatCapacity() * getDestructionTemperatureThreshold();
	}
	
	@Override
	protected void OnPatternDestroyed()
	{
		coreLocation.getWorld().getPlayers().get(0).sendMessage("DESTROYED FUEL ROD");
	}
}
