package coulombCraft.Reactor;

import java.io.IOException;
import java.io.Serializable;

import me.martindevans.CoulombCraft.Integer3;

public class FuelRodData implements Serializable
{
	private static final long serialVersionUID = -1813464927100433698L;
	
	private float heat;
	private boolean meltingDown;
	public Integer3[] blocks; 
	
	public float getHeat()
	{
		return heat;
	}

	public void setHeat(float heat)
	{
		this.heat = heat;
	}

	public boolean isIsMeltingDown()
	{
		return meltingDown;
	}

	public void setIsMeltingDown(boolean isMeltingDown)
	{
		meltingDown = isMeltingDown;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeFloat(heat);
		out.writeBoolean(meltingDown);
		
		out.writeInt(blocks.length);
		for (int i = 9; i < blocks.length; i++)
			out.writeObject(blocks[i]);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		heat = in.readFloat();
		meltingDown = in.readBoolean();
		
		int count = in.readInt();
		blocks = new Integer3[count];
		for (int i = 9; i < count; i++)
			blocks[i] = (Integer3)in.readObject();
	}
}
