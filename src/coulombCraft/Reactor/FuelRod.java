package coulombCraft.Reactor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.config.Configuration;

import coulombCraft.Networks.Cable;
import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Integer3;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;

public class FuelRod extends BasePatternInstance
{	
	FuelRodData data;
	
	double heatDelta;
	double heatProduction;
	
	Configuration config;
	
	Random rand = new Random();
	
	Location coreLocation;
	
	Block[] blocksInRange;
	
	public FuelRod(CoulombCraft plugin, Block[][] blocks)
	{
		super(plugin, blocks);
		
		this.config = plugin.getConfiguration();
		
		coreLocation = blocks[1][1].getLocation();
		
		data = new FuelRodData(coreLocation, plugin);
		plugin.getSqliteDatabase().AddDatabaseListener(data);
		
		blocksInRange = RemoveBlocksInPattern(FindBlocksInRange(coreLocation.getWorld(), getHeatRange()), blocks).toArray(new Block[0]);
	}
	
	private List<Block> FindBlocksInRange(World w, int range)
	{
		List<Block> blocks = new ArrayList<Block>();
		
		for (int i = coreLocation.getBlockX() - range; i < coreLocation.getBlockX() + range + 1; i++)
		{
			for (int j = coreLocation.getBlockY() - range; j < coreLocation.getBlockY() + range + 1; j++)
			{
				for (int k = coreLocation.getBlockZ() - range; k < coreLocation.getBlockZ() + range + 1; k++)
				{
					Block b = w.getBlockAt(i, j, k);
					
					blocks.add(b);
				}
			}
		}
		
		return blocks;
	}
	
	private List<Block> RemoveBlocksInPattern(List<Block> blocks, Block[][] pattern)
	{
		for (int i = 0; i < pattern.length; i++)
		{
			for (int j = 0; j < pattern[i].length; j++)
			{
				blocks.remove(pattern[i][j]);
			}
		}
		
		return blocks;
	}
	
	@Override
	public String toString()
	{
		return "Fuel Rod";
	}

	private double getHeatCapacity()
	{
		return config.getDouble("Fuel Rod.Heat Capacity", 1000);
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
		
		heatProduction += heatDelta;
		data.setHeat((float)Math.max(0, data.getHeat() + heatProduction));
		
		heatDelta = 0;
		
		if (blocks[1][1].getType() != Material.LAVA)
		{
			blocks[1][1].setType(Material.LAVA);
		}
		
		if (data.getHeat() > getHeatCapacity())
		{
			data.setIsMeltingDown(true);
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
		
		super.Tick();
	}

	private void UpdateNearbyBlocks()
	{
		List<Cable> cableInRange = new ArrayList<Cable>();
		
		World world = coreLocation.getWorld();
		for (int i = 0; i < blocksInRange.length; i++)
		{
			Block b = blocksInRange[i];
			b.getChunk().load();
			int type = b.getTypeId();
			
			if (type == Material.AIR.getId())
				continue;
			
			switch (type)
			{
				case 0:		//air
					continue;
				case 35:	//wool
				{					
					IgniteWool(world, b);
					
					cableInRange.add((Cable)plugin.getQueryProvider().GetQueryable(new Integer3(b.getLocation())));
					continue;
				}
				case 20:	//Glass
				case 102:	//Glass Pane
				{
					SmashGlass(world, b);
					continue;
				}
				case 17:	//Wood
				case 5:		//Logs
				case 18:	//Leaves
				case 87:	//Netherrack
				{
					IgniteWood(world, b);
					continue;
				}
				case 8:		//water (flowing)
				{
					if (b.getY() != coreLocation.getBlockY())
						continue;
					ApplyWaterCooling(world, b);
					continue;
				}
				case 9:		//water (still)
				{
					ApplyWaterCooling(world, b);
					continue;
				}
				case 79:	//Ice
				case 80:	//Snow block
				{
					ApplyColdBlockCooling(world, b);
					continue;
				}
				case 51:	//Fire
				{
					heatDelta += config.getDouble("Fuel Rod.Fire Source Bonus", 1);
					continue;
				}
				case 10:	//Lava (flowing)
				case 11:	//Lava (still)
				{
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
		
		float cableHeat = data.getHeat() / ((float)cableInRange.size());
		for (Cable c : cableInRange)
			c.AddHeat(cableHeat);
	}
	
	private void IgniteWood(World w, Block b)
	{
		if (data.getHeat() > config.getDouble("Heat.Wood Ignition Threshold", 3) && rand.nextDouble() <= config.getDouble("Heat.Wood Ignition Chance", 0.1))
		{
			CoulombCraft.Ignite(b);
		}
	}
	
	private void ApplyWaterCooling(World w, Block b)
	{
		double chance = Math.pow(data.getHeat() / getHeatCapacity(), 6);
		double randNumber = rand.nextDouble();
		if (randNumber < chance)
		{
			heatDelta -= config.getDouble("Fuel Rod.Water Heat Reduction", 1.2);
			
			if (data.getHeat() > config.getDouble("Heat.Water Evaporation Threshold", 100))
				b.setTypeId(0);
			
			w.playEffect(b.getLocation(), Effect.EXTINGUISH, 0);
		}
	}
	
	private void ApplyColdBlockCooling(World w, Block b)
	{
		double randNumber = rand.nextDouble();
		
		heatDelta -= config.getDouble("Fuel Rod.Ice Heat Reduction", 10);
		
		//turn it into water
		if (randNumber > 1 / data.getHeat() && data.getHeat() > config.getDouble("Heat.Ice Melt Threshold", 50));
			b.setType(Material.WATER);
	}
	
	private void SmashGlass(World w, Block b)
	{
		if (data.getHeat() > config.getDouble("Heat.Glass Break Threshold", 850) && rand.nextDouble() <= config.getDouble("Heat.Glass Break Chance", 0.01))
		{
			b.setType(Material.AIR);
			
			for (int i = 0; i < blocks.length; i++)
			{
				for (int j = 0; j < blocks[i].length; j++)
				{
					if (b.equals(blocks[i][j]))
						super.DestroyPattern();
				}
			}
		}
	}
	
	private void IgniteWool(World w, Block b)
	{
		if (data.getHeat() > config.getDouble("Heat.Cable Ignition Threshold", 900) && rand.nextDouble() <= config.getDouble("Heat.Cable Ignition Chance", 0.1))
		{
			CoulombCraft.Ignite(b);
		}
	}
	
	@Override
	public boolean IsBreakable(Block b)
	{
		return super.IsBreakable(b) && data.getHeat() < getHeatCapacity() * getDestructionTemperatureThreshold();
	}
	
	@Override
	protected void OnPatternDestroyed()
	{
		data.Destroy();
	}

	DecimalFormat sigfig4 = new DecimalFormat("0.000");
	DecimalFormat sigfig2 = new DecimalFormat("0.0");
	public String Query(String variable)
	{
		if (variable.equalsIgnoreCase("temperature"))
			return sigfig2.format(data.getHeat());
		else if (variable.equalsIgnoreCase("heat production"))
			return sigfig4.format(heatProduction);
		else if (variable.equalsIgnoreCase("heat capacity"))
			return sigfig2.format(getHeatCapacity());
		return null;
	}
	
	@Override
	public boolean CanAnswer(String variable)
	{
		return variable.equalsIgnoreCase("temperature") ||
			   variable.equalsIgnoreCase("heat production") ||
			   variable.equalsIgnoreCase("heat capacity");
	}
	
	@Override
	public void ChunksUnloaded()
	{
		super.ChunksUnloaded();
		
		data.Flush();
		data.Unload();
	}
}
