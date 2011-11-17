package me.martindevans.CoulombCraft;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

public class MaterialDropMap
{
	@SuppressWarnings("serial")
	public static final Map<Material, Material> Map = new HashMap<Material, Material>()
	{
		{
			put(Material.BED_BLOCK, Material.BED);
			put(Material.BOOKSHELF, Material.BOOKSHELF);
			put(Material.BRICK, Material.BRICK);
			put(Material.BRICK_STAIRS, Material.BRICK_STAIRS);
			put(Material.CACTUS, Material.CACTUS);
			put(Material.CAKE_BLOCK, Material.CAKE);
			put(Material.CLAY, Material.CLAY_BALL);
			put(Material.COAL_ORE, Material.COAL);
			put(Material.COBBLESTONE, Material.COBBLESTONE);
			put(Material.COBBLESTONE_STAIRS, Material.COBBLESTONE_STAIRS);
			put(Material.DIAMOND_BLOCK, Material.DIAMOND_BLOCK);
			put(Material.DIAMOND_ORE, Material.DIAMOND);
			put(Material.DIRT, Material.DIRT);
			put(Material.GLOWSTONE, Material.GLOWSTONE_DUST);
			put(Material.GOLD_BLOCK, Material.GOLD_BLOCK);
			put(Material.GOLD_ORE, Material.GOLD_ORE);
			put(Material.IRON_BLOCK, Material.IRON_BLOCK);
			put(Material.IRON_ORE, Material.IRON_ORE);
			put(Material.LAPIS_BLOCK, Material.LAPIS_BLOCK);
			put(Material.LAPIS_ORE, Material.LAPIS_ORE);
			put(Material.LOG, Material.LOG);
			put(Material.MELON_BLOCK, Material.MELON);
			put(Material.MOSSY_COBBLESTONE, Material.COBBLESTONE);
			put(Material.NETHERRACK, Material.NETHERRACK);
			put(Material.OBSIDIAN, Material.OBSIDIAN);
			put(Material.REDSTONE_ORE, Material.REDSTONE);
			put(Material.REDSTONE_WIRE, Material.REDSTONE);
			put(Material.SAND, Material.SAND);
			put(Material.SANDSTONE, Material.SANDSTONE);
			put(Material.SOIL, Material.SOIL);
			put(Material.SOUL_SAND, Material.SOUL_SAND);
			put(Material.STONE, Material.COBBLESTONE);
			put(Material.WOOD, Material.WOOD);
			put(Material.WOOD_STAIRS, Material.WOOD_STAIRS);
			put(Material.WOOL, Material.WOOL);
		}
	};
}
