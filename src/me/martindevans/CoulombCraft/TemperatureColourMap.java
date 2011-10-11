package me.martindevans.CoulombCraft;

public class TemperatureColourMap
{
	public static final byte BLACK = 15;
	public static final byte DARK_BLUE = 11;
	public static final byte BLUE = 9;
	public static final byte LIGHT_BLUE = 3;
	public static final byte YELLOW = 4;
	public static final byte ORANGE = 1;
	public static final byte RED = 14;
	public static final byte WHITE = 0;
	
	private static final byte[] sequence = new byte[]
		{
			BLACK,
			DARK_BLUE,
			BLUE,
			LIGHT_BLUE,
			YELLOW,
			ORANGE,
			RED,
			WHITE
		};
	
	public static byte GetColour(double temp, double max)
	{
		temp = Math.min(temp, max);
		
		return sequence[(int)(((temp / max) * (sequence.length - 1)))];
	}
}
