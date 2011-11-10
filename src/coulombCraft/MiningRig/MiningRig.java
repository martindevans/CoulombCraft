package coulombCraft.MiningRig;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import coulombCraft.Networks.ResourceNetwork;
import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.MaterialDropMap;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;

public class MiningRig extends BasePatternInstance
{
	int nextMove = 0;
	
	boolean stop = false;
	int x = 0;
	int z = 0;
	int y = 0;
	Location homeHeadPosition;
	
	Block networkBlock;
	ResourceNetwork.Resource power;
	
	public MiningRig(CoulombCraft plugin, Block[][] blocks)
	{
		super(plugin, blocks);
		
		homeHeadPosition = blocks[0][0].getLocation().add(1, 0, 1);
		
		plugin.getServer().broadcastMessage("Created mining rig");
	}

	@Override
	public String toString()
	{		
		return "Automated Mining Rig";
	}
	
	@Override
	protected void OnPatternDestroyed()
	{
		getBlockInFrameCoordinates(x, y, z).setType(Material.AIR);
	}

	private int getNextMoveCount()
	{
		return plugin.getConfiguration().getInt("Mining Rig.Move Ticks", 4);
	}
	
	public void Tick()
	{
		super.Tick();
		
		nextMove--;
		
		if (nextMove <= 0)
		{
			Move();
			nextMove = getNextMoveCount();
		}
	}
	
	private Block getBlockInFrameCoordinates(int x, int y, int z)
	{
		return homeHeadPosition.getBlock().getRelative(x, y, z);
	}
	
	private void FindNetwork()
	{
		for (int i = 0; i < blocks.length; i++)
		{
			for (int j = 0; j < blocks[i].length; j++)
			{
				if (blocks[i][j] == null)
					continue;
				
				Block[] netBlocks = plugin.getResourceNetworkManager().getAdjacentNetworkBlocks(blocks[i][j].getLocation());
				if (netBlocks.length == 0)
					continue;
				
				ResourceNetwork network = plugin.getResourceNetworkManager().getNetworkByBlock(netBlocks[0]);
				
				networkBlock = netBlocks[0];
				power = network.GetResource("power");
			}
		}
	}
	
	private void Move()
	{
		if (stop)
		{
			DestroyPattern();
			return;
		}
		
		if (power == null)
		{
			FindNetwork();
			return;
		}
		
		double powerDrain = plugin.getConfiguration().getDouble("Mining Rig.Power Draw", 250);
		
		if (power.getAmount() < powerDrain)
			return;
		
		power.Add(-powerDrain);
		
		Block headA = getBlockInFrameCoordinates(x, y, z);
		
		x++;
		if (x > blocks.length - 3)
		{
			x = 0;
			z++;
			
			if (z > blocks.length - 3)
			{
				z = 0;
				y--;
			}
		}
		
		Block headB = getBlockInFrameCoordinates(x, y, z);
		
		if (headB.getY() < 5)
			stop = true;
		
		Material drop = MaterialDropMap.Map.get(headB.getType());
		if (drop != null)
			power.getNetwork().GetResource(drop.name()).Add(1);
		
		headA.setType(Material.AIR);
		headB.setType(Material.DIAMOND_BLOCK);
	}
}
