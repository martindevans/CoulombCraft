package coulombCraft.Freezer;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.Patterns.BasePattern;
import me.martindevans.CoulombCraft.Patterns.BasePatternInstance;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

public class FreezerPattern extends BasePattern
{
	CoulombCraft plugin;
	
	public FreezerPattern(CoulombCraft plugin)
	{
		super(new int[][]
			{
				{ 101, 101, 101 },
				{ 101,  -1, 101 },
				{ 101,  -1, 101 }
			});
		
		this.plugin = plugin;
	}
	
	public BasePatternInstance Create(Block[][] blocks)
	{
		return new Freezer(plugin, blocks);		
	}

	@Override
	protected void LoadStoredPatterns(Chunk c)
	{
		// TODO Auto-generated method stub
		
	}
}
