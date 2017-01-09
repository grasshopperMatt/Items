package com.projectkorra.items;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import com.projectkorra.items.attributes.nbt.NBTCompound;
import com.projectkorra.items.attributes.nbt.NBTHandler;
import com.projectkorra.items.attributes.nbt.NBTList;

public class ItemsLoader {
	
	public static final Map<String, ItemStack> items = new HashMap<String, ItemStack>();
	public static final Map<Recipe, Map<Character, ItemStack>> recipes = new HashMap<Recipe, Map<Character, ItemStack>>();
	//<Item Type, ItemStack>
	
	public ItemsLoader() {
		items.clear();
		recipes.clear();
		
		File itemsFolder = Items.getItemsFolder();
		if (!itemsFolder.exists()) {
			itemsFolder.mkdir();
		}
		
		File recipeFolder = Items.getRecipeFolder();
		if (!recipeFolder.exists()) {
			recipeFolder.mkdir();
		}
		
		//File t = new File(file, "t2.yml");
		//FileConfiguration conf = YamlConfiguration.loadConfiguration(t);
		//conf.set("Item.Name", "TEST");
		//conf.set("Item.Material", "Diamond");
		//conf.set("Item.DisplayName", "DIMOND");
		//conf.set("Item.Lore", Arrays.asList("looooore"));
		//conf.set("Item.Triggers", Arrays.asList("AbilityStartEvent"));
		//conf.set("Item.Include", Arrays.asList("EarthBlast"));
		//Map<String, Object> m = new HashMap<String, Object>();
		//m.put("earthblast.damage", 5);
		//List<Map<?, ?>> m2 = new ArrayList<Map<?, ?>>();
		//m2.add(m);
		//conf.set("Item.Attributes", m2);
		//Items.getInstance().getLogger().log(Level.SEVERE, m2.toString());
		//try {
		//	conf.save(t);
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
		
		//File t = new File(recipeFolder, "t.yml");
		//FileConfiguration conf = YamlConfiguration.loadConfiguration(t);
		
		//conf.set("ShapedRecipe.CraftingTable.tX", "DIAMOND");
		//conf.set("ShapedRecipe.CraftingTable.tY", "DIAMOND");
		//conf.set("ShapedRecipe.CraftingTable.tZ", "DIAMOND");
		
		//conf.set("ShapedRecipe.CraftingTable.mX", "DIAMOND");
		//conf.set("ShapedRecipe.CraftingTable.mY", "DIAMOND");
		//conf.set("ShapedRecipe.CraftingTable.mZ", "DIAMOND");
		
		//conf.set("ShapedRecipe.CraftingTable.bX", "DIAMOND");
		//conf.set("ShapedRecipe.CraftingTable.bY", "DIAMOND");
		
		//conf.set("ShapedRecipe.CraftingTable.Item", "GRASS");
		
		//try {
		//	conf.save(t);
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
		
		for (File f : itemsFolder.listFiles()) {
			ItemStack item = loadItem(YamlConfiguration.loadConfiguration(f));
			NBTCompound compound = NBTHandler.fromTag(item);
			
			items.put((String) compound.get("Name"), item);
		}
		
		for (File f : recipeFolder.listFiles()) {
			Bukkit.getServer().addRecipe(loadShapedRecipe(YamlConfiguration.loadConfiguration(f)));
		}
	}
	
	@SuppressWarnings("unchecked")
	public ItemStack loadItem(FileConfiguration configuration) { 
		ConfigurationSection configurationItem = configuration.getConfigurationSection("Item");
		NBTCompound compound = NBTHandler.newCompound();
		
		if (configurationItem.contains("Name")) {
			compound.put("Name", configurationItem.getString("Name"));
		}
		
		if (configurationItem.contains("Material")) {
			compound.put("Material", configurationItem.getString("Material"));
		}
		
		if (configurationItem.contains("Triggers")) {
			NBTList list = compound.getList("Triggers", true);
			for (Object o : configurationItem.getList("Triggers")) {
				list.add(o);
			}
			compound.put("Triggers", list);
		}
		
		if (configurationItem.contains("Include")) {
			NBTList list = compound.getList("Include", true);
			for (Object o : configurationItem.getList("Include")) {
				list.add(o);
			}
			compound.put("Include", list);
		}
		
		if (configurationItem.contains("Attributes")) {
			NBTCompound list = NBTHandler.newCompound();
			for (Map<?, ?> o : configurationItem.getMapList("Attributes")) {
				list.putAll((Map<? extends String, ? extends Object>) o);
			}
			compound.put("Attributes", list);
		}
		
		ItemStack item = NBTHandler.getItem(new ItemStack(Material.matchMaterial((String) compound.get("Material"))));
		NBTHandler.setTag(item, compound);
		return item;
	}
	
	@SuppressWarnings("unchecked")
	public ItemStack loadItem2(FileConfiguration configuration) { 
		ConfigurationSection configurationItem = configuration.getConfigurationSection("Item");
		NBTCompound compound = NBTHandler.newCompound();
		
		if (configurationItem.contains("Name")) {
			compound.put("Name", configurationItem.getString("Name"));
		}
		
		if (configurationItem.contains("Material")) {
			compound.put("Material", configurationItem.getString("Material"));
		}
		
		if (configurationItem.contains("Triggers")) {
			NBTList triggers = NBTHandler.newList();
			triggers.addAll(configurationItem.getList("Triggers"));
			compound.put("Triggers", triggers);
		}
		
		if (configurationItem.contains("Include")) {
			NBTList include = NBTHandler.newList();
			include.addAll(configurationItem.getList("Include"));
			compound.put("Include", include);
		}
		
		if (configurationItem.contains("Attributes")) {
			NBTCompound attributes = NBTHandler.newCompound();
			for (Map<?, ?> o : configurationItem.getMapList("Attributes")) {
				attributes.putAll((Map<? extends String, ? extends Object>) o);
			}
			compound.put("Attributes", attributes);
		}
		
		ItemStack item = NBTHandler.getItem(new ItemStack(Material.matchMaterial((String) compound.get("Material"))));
		NBTHandler.setTag(item, compound);
		return item;
	}
	
	public ShapedRecipe loadShapedRecipe2(FileConfiguration configuration) {
		ConfigurationSection configurationRecipe = configuration.getConfigurationSection("ShapedRecipe");
		
		if (configurationRecipe.contains("CraftingTable")) {
			String type = configurationRecipe.getString("Type");
			if (type == "CraftingTable") {
				ConfigurationSection craftingTable = configurationRecipe.getConfigurationSection("CraftingTable");
				HashMap<Character, String> recipeShape = new HashMap<Character, String>();
				
				//Top row of the crafting table.
				
				recipeShape.put('1', craftingTable.getString("tX"));
				recipeShape.put('2', craftingTable.getString("tY"));
				recipeShape.put('3', craftingTable.getString("tZ"));
				
				//Middle row of the crafting table.
				
				recipeShape.put('4', craftingTable.getString("mX"));
				recipeShape.put('5', craftingTable.getString("mY"));
				recipeShape.put('6', craftingTable.getString("mZ"));
				
				//Bottom row of the crafting table.
				
				recipeShape.put('7', craftingTable.getString("bX"));
				recipeShape.put('8', craftingTable.getString("bY"));
				recipeShape.put('9', craftingTable.getString("bZ"));
				
				ItemStack itemValue = getItem(craftingTable.getString("Item"));
				
				Map<Character, ItemStack> specialItems = new HashMap<Character, ItemStack>();
				ShapedRecipe recipe = new ShapedRecipe(itemValue);
				recipe.shape("123", "456", "789");	
				
				for (char c : recipeShape.keySet()) {
					ItemStack item = getItem(recipeShape.get(c));
					
					if (items.containsKey(item)) {
						specialItems.put(c, item);
					}
					
					recipe.setIngredient(c, item.getType());
				}	
				
				Bukkit.getLogger().log(Level.SEVERE, recipe.getIngredientMap().toString());
				recipes.put(recipe, specialItems);
				return recipe;
			}
		}
		Bukkit.getServer().getLogger().log(Level.SEVERE, "TEST");
		return null;
	}
	
	public ShapedRecipe loadShapedRecipe(FileConfiguration configuration) {
		ConfigurationSection configurationRecipe = configuration.getConfigurationSection("ShapedRecipe");
		ConfigurationSection craftingTable = configurationRecipe.getConfigurationSection("CraftingTable");
		
		HashMap<Character, String> recipeShape = new HashMap<Character, String>();
				
		//Top row of the crafting table.
				
		recipeShape.put('1', craftingTable.getString("tX"));
		recipeShape.put('2', craftingTable.getString("tY"));
		recipeShape.put('3', craftingTable.getString("tZ"));
				
		//Middle row of the crafting table.
				
		recipeShape.put('4', craftingTable.getString("mX"));
		recipeShape.put('5', craftingTable.getString("mY"));
		recipeShape.put('6', craftingTable.getString("mZ"));
				
		//Bottom row of the crafting table.
				
		recipeShape.put('7', craftingTable.getString("bX"));
		recipeShape.put('8', craftingTable.getString("bY"));
		recipeShape.put('9', craftingTable.getString("bZ"));
				
		ItemStack itemValue = getItem(craftingTable.getString("Item"));
				
		Map<Character, ItemStack> specialItems = new HashMap<Character, ItemStack>();
		ShapedRecipe recipe = new ShapedRecipe(itemValue);
		recipe.shape("123", "456", "789");	
				
		for (char c : recipeShape.keySet()) {
			ItemStack item = getItem(recipeShape.get(c));
			Bukkit.getLogger().log(Level.SEVERE, recipeShape.get(c));
					
			if (items.containsKey(item)) {
				specialItems.put(c, item);
			}
					
			if (item != null) {
				recipe.setIngredient(c, item.getType());
			}
		}	
				
		recipes.put(recipe, specialItems);
		return recipe;
	}
	
	private ItemStack getItem(String materialString) {
		Material material = null;
		
		if (materialString == null) {
			return null;
		}
		
		else if (Material.matchMaterial(materialString) != null) {
			material = Material.matchMaterial(materialString);
		}
		
		else if (items.containsKey(materialString)) {
			material = items.get(materialString).getType();
		}
		
		return new ItemStack(material);
	}
	
	//TODO: SHAPELESS RECIPE
}
