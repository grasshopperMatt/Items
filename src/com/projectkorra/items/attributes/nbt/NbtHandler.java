package com.projectkorra.items.attributes.nbt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.HashBiMap;
import com.google.common.primitives.Primitives;
import com.projectkorra.items.attributes.nbt.util.NbtWrapper;
import com.projectkorra.projectkorra.util.ReflectionHandler;

public class NbtHandler {
	private static final Class<?> compoundClass = getCompoundClass();
	private static final Class<?> superClass = compoundClass.getSuperclass();
	
	private static final HashBiMap<Integer, Class<?>> nbtClass = HashBiMap.create();
	private static final HashBiMap<Integer, NbtType> nbtEnum = HashBiMap.create();
	
	public enum NbtType {
		TAG_END(0, void.class), TAG_BYTE(1, byte.class), TAG_SHORT(2, short.class), TAG_INT(3, int.class), 
		TAG_LONG(4,long.class), TAG_FLOAT(5, float.class), TAG_DOUBLE(6, double.class), TAG_BYTE_ARRAY(7, byte[].class),
		TAG_STRING(8, String.class), TAG_LIST(9, List.class), TAG_COMPOUND(10, Map.class), TAG_INT_ARRAY(11, int[].class);

		
		private final int id;

		
		private NbtType(int id, Class<?> type) {
			this.id = id;

			nbtClass.put(id, type);
			nbtEnum.put(id, this);
		}

		
		public int getId() {
			return id;
		}

		
		public String getName() {
			switch (this) {
			
			case TAG_COMPOUND:
				return "map";

			case TAG_LIST:
				return "list";
			default:
				break;
			}
			
			return "";
		}
	}
	
	/**
	 * Retrieve the NMS NBT compound class
	 * @return NBT compound class
	 */
	
	
	private static Class<?> getCompoundClass() {
		try {
			return Class.forName("org.bukkit.craftbukkit.v1_10_R1.CraftOfflinePlayer").getDeclaredMethod("getData").getReturnType();
		}

		catch (NoSuchMethodException | SecurityException | ClassNotFoundException exception) {
			exception.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Retrieve the NBT type from a given NMS NBT tag.
	 * @param nms native NBT tag
	 * @return The corresponding type.
	 */

	public static NbtType getType(Object nms) {
		int type = 0;

		try {
			type = (Byte) ReflectionHandler.invokeMethod(nms, "getTypeId");
		}

		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException exception) {
			exception.printStackTrace();
		}

		return nbtEnum.get(type);
	}

	/**
	 * Retrieve the nearest NBT type for a given primitive type.
	 * @param primitive NBT type
	 * @return The corresponding type.
	 */

	public static NbtType getPrimitiveType(Object primitive) {
		NbtType type = nbtEnum.get(nbtClass.inverse().get(Primitives.unwrap(primitive.getClass())));

		if (type == null) {
			return null;
		}

		return type;
	}

	/**
	 * Construct a new NBT list of an unspecified type. 
	 * @return The NBT list.
	 */

	public static NbtList newList(Object... content) {
		NbtList list = new NbtList(createTag(NbtType.TAG_LIST, null));

		for (Object object : Arrays.asList(content)) {
			list.add(object);
		}

		return list;
	}

	/**
	 * Construct a new NBT compound.
	 * Use {@link NbtCompound#asMap()} to modify it.
	 * @return The NBT compound.
	 */

	public static NbtCompound newCompound() {
		return new NbtCompound(createTag(NbtType.TAG_COMPOUND, null));
	}

	/**
	 * Construct a new NBT wrapper from a list.
	 * @param nmsList - the NBT list.
	 * @return The wrapper.
	 */

	public static NbtList fromList(Object nmsList) {
		return new NbtList(nmsList);
	}

	/**
	 * Construct a new NBT wrapper from a compound.
	 * @param NMS NBT compound
	 * @return The wrapper.
	 */

	public static NbtCompound fromCompound(Object nmsCompound) {
		return new NbtCompound(nmsCompound);
	}
	
	/**
	 * Construct a new NMS NBT tag initialized with the given value.
	 * @param type - the NBT type
	 * @param value, or NULL to keep the original value
	 * @return The created tag.
	 */

	private static Object createTag(NbtType type, Object value) {
		Object tag = null;

		try {
			Method createTag = superClass.getDeclaredMethod("createTag", byte.class);
			ReflectionHandler.setValue(tag = createTag.invoke(null, (byte) type.id), true, type.getName(), value);
		}

		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException
		| SecurityException | NoSuchMethodException exception) {
			exception.printStackTrace();
		}

		return tag;
	}

	/**
	 * Set the NBT compound tag of a given item stack.
	 * <p>
	 * The item stack must be a wrapper for a CraftItemStack. Use
	 * {@link MinecraftReflection#getBukkitItemStack(ItemStack)} if not.
	 * 
	 * @param the Item
	 * @param compound
	 *            - the new NBT compound, or NULL to remove it.
	 * @throws IllegalArgumentException
	 *             If the stack is not a CraftItemStack, or it represents air.
	 */

	public static void setTag(ItemStack item, NbtCompound compound) {
		checkItem(item);
		
		try {
			ReflectionHandler.setValue(ReflectionHandler.getValue(item, true, "handle"), true, "handle", compound.getHandle());
		}

		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Construct a wrapper for an NBT tag stored (in memory) in an item stack.
	 * This is where auxillary data such as enchanting, name and lore is stored.
	 * It does not include items material, damage value or count.
	 * <p>
	 * The item stack must be a wrapper for a CraftItemStack.
	 * 
	 * @param stack
	 *            - the item stack.
	 * @return A wrapper for its NBT tag.
	 */

	public static NbtCompound fromTag(ItemStack item) {
		checkItem(item);
		Object tag = null;

		try {
			tag = ReflectionHandler.getValue(ReflectionHandler.getValue(item, true, "handle"), true, "tag");
			
			if (tag == null) {
				NbtCompound compound = newCompound();
				setTag(item, compound);
				return compound;
			}
		}

		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException exception) {
			exception.printStackTrace();
		}

		return fromCompound(tag);
	}

	/**
	 * Retrieve a CraftItemStack version of the stack.
	 * 
	 * @param stack
	 *            - the stack to convert.
	 * @return The CraftItemStack version.
	 */

	public static ItemStack getCraftItemStack(ItemStack item) {
		if (item.getType() == Material.AIR) {
			return item;
		}

		Class<?> craftItem = null;

		try {
			craftItem = Class.forName("org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack");
			if (craftItem.isAssignableFrom(item.getClass())) {
				return item;
			}

			Constructor<?> caller = craftItem.getDeclaredConstructor(ItemStack.class);
			return (ItemStack) caller.newInstance(item);

		}

		catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
		| IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			exception.printStackTrace();
		}

		return null;
	}

	/**
	 * Ensure that the given stack can store arbitrary NBT information.
	 * 
	 * @param stack
	 *            - the stack to check.
	 */

	public static void checkItem(ItemStack stack) {
		try {
			Class<?> craftItem = Class.forName("CraftItemStack");
			if (!craftItem.isAssignableFrom(stack.getClass())) {
				throw new IllegalArgumentException("Stack must be a CraftItemStack.");
			}
		}

		catch (ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Convert wrapped List and Map objects into their respective NBT
	 * counterparts.
	 * 
	 * @param name
	 *            - the name of the NBT element to create.
	 * @param value
	 *            - the value of the element to create. Can be a List or a Map.
	 * @return The NBT element.
	 */

	public static Object unwrapValue(String name, Object value) {

		if (value == null) {
			return null;
		}

		else if (value instanceof NbtWrapper) {
			return ((NbtWrapper) value).getHandle();
		}

		else if (value instanceof List) {
			throw new IllegalArgumentException("Can only insert a WrappedList.");
		}

		else if (value instanceof Map) {
			throw new IllegalArgumentException("Can only insert a WrappedCompound.");
		}

		else {
			return createTag(getPrimitiveType(value), value);
		}
	}

	/**
	 * Convert a given NBT element to a primitive wrapper or List/Map
	 * equivalent.
	 * <p>
	 * All changes to any mutable objects will be reflected in the underlying
	 * NBT element(s).
	 * 
	 * @param nms
	 *            - the NBT element.
	 * @return The wrapper equivalent.
	 */

	public static Object wrapNative(Object nms) {

		if (nms == null) {
			return null;
		}

		if (superClass.isAssignableFrom(nms.getClass())) {
			final NbtType type = getType(nms);

			if (type == null) {
				return null;
			}

			switch (type) {

			case TAG_COMPOUND:
				return new NbtCompound(nms);
			case TAG_LIST:
				return new NbtList(nms);
			default:
				try {
					return ReflectionHandler.getValue(nms, true, type.getName());
				}

				catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
				| SecurityException exception) {
					exception.printStackTrace();
				}
			}
		}

		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getDataMap(Object handle) {
		try {
			return (Map<String, Object>) ReflectionHandler.getValue(handle, true, NbtType.TAG_COMPOUND.getName());
		}

		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException exception) {
			exception.printStackTrace();
		}

		return null;
	}
	

	@SuppressWarnings("unchecked")
	public static List<Object> getDataList(Object handle) {
		try {
			return (List<Object>) ReflectionHandler.getValue(handle, true, NbtType.TAG_LIST.getName());
		}

		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException exception) {
			exception.printStackTrace();
		}

		return null;
	}
}