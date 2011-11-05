package coulombCraft.MiningRig;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;

public class MiningRig extends BasePatternInstance
{
	int nextMove = 0;
	
	boolean stop = false;
	int x = 0;
	int z = 0;
	int y = 0;
	Location homeHeadPosition;
	
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
		return plugin.getConfiguration().getInt("Mining Rig.Move Ticks", 1);
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
	
	private void Move()
	{
		if (stop)
			return;
		
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
		
		headA.setType(Material.AIR);
		headB.setType(Material.DIAMOND_BLOCK);
	}
}
