package coulombCraft.Recipes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class StoneToWool extends ShapelessRecipe
{
	public StoneToWool()
	{
		super(new ItemStack(35, 1));
		
		addIngredient(1, Material.COBBLESTONE);
	}
}
