package me.martindevans.CoulombCraft;

public class Integer3
{
	int x;
	int y;
	int z;
	
	public Integer3(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public int hashCode()
	{
		return x ^ y << 3 ^ z >> 7;
	}
}
