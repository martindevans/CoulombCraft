package coulombCraft.Reactor;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;

import me.martindevans.CoulombCraft.CoulombCraft;
import me.martindevans.CoulombCraft.IDeserialize;

public class FuelRodDeserializer implements IDeserialize
{
	static final String SerializedDataId = "CoulombCraft.FuelRodData";
	
	@Override
	public void DeserializeBlock(SpoutBlock b)
	{
		FuelRodData d = (FuelRodData)SpoutManager.getMaterialManager().getSpoutBlock(b).getData(FuelRodDeserializer.SerializedDataId);
		
		if (d == null)
			return;
		
		CoulombCraft.getLogger().info("Found a serialized reactor");
	}
}
