package me.martindevans.CoulombCraft.Patterns;

public class FuelRodPattern extends BasePattern implements IPatternInstanceFactory
{
	public FuelRodPattern()
	{
		super(new int[][]
			{
				{ -1, 20, -1 },
				{ 20, 12, 20 },
				{ -1, 20, -1 }
			});
	}
	
	public BasePatternInstance Create()
	{
		return new Instance();
	}
	
	public class Instance extends BasePatternInstance
	{
		
	}
}
