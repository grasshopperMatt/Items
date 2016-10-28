package com.projectkorra.items.attributes.nbt;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.primitives.Primitives;
import com.projectkorra.items.attributes.nbt.util.Wrapper;
import com.projectkorra.projectkorra.util.ReflectionHandler;

public class NbtFactory {
	private static NbtFactory instance;
	
	public BiMap<Integer, Class<?>> nbtClass = HashBiMap.create();
	public BiMap<Integer, NbtType> nbtEnum = HashBiMap.create();
	
	public Class<?> baseClass;
	public Class<?> compoundClass;
	public Method nbtCreateTag;
	public Method nbtGetType;
	public Field nbtListType;
	
	private Class<?> craftItem;
	private Field craftItemHandle;
	private Field craftItemTag;
	
	public enum NbtType {
		
		TAG_END(0, Void.class), TAG_BYTE(1, byte.class), TAG_SHORT(2, short.class), TAG_INT(3, int.class), 
		TAG_LONG(4, long.class), TAG_FLOAT(5, float.class), TAG_DOUBLE(6, double.class), TAG_BYTE_ARRAY(7,byte[].class), 
		TAG_STRING(8, String.class), TAG_LIST(9, List.class), TAG_COMPOUND(10, Map.class), TAG_INT_ARRAY(11, int[].class);
		
		
		private final int id;
		private NbtFactory nbt;
		

		private NbtType(int uniqueId, Class<?> type) {
			id = uniqueId;
			
			nbt = NbtFactory.getInstance();
			nbt.nbtClass.put(id, type);
			nbt.nbtEnum.put(id, this);
		}
		

		public String getFieldName() {

			if (this == TAG_COMPOUND) {
				return "map";
			} else if (this == TAG_LIST) {
				return "list";
			} else {
				return "data";	
			}	
		}
		
		
		public int getId() {
			return id;
		}
	}
	

	private NbtFactory() {
		String offlinePlayer = "org.bukkit.craftbukkit.v1_10_R1.CraftOfflinePlayer";
		String itemStack = "org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack";
		
		try {
			
			//Initialization
			
			compoundClass = getClass().getClassLoader().loadClass(offlinePlayer).getDeclaredMethod("getData").getReturnType();
			baseClass = compoundClass.getSuperclass();
			
			nbtGetType = baseClass.getDeclaredMethod("getTypeId");
			nbtCreateTag = baseClass.getDeclaredMethod("createTag", byte.class);
			
			craftItem = getClass().getClassLoader().loadClass(itemStack);
			craftItemHandle = ReflectionHandler.getField(craftItem, true, "handle");
			craftItemTag = craftItemHandle.getType().getDeclaredField("tag");
			
			//Make Private Methods accessible
			
			nbtGetType.setAccessible(true);
			nbtCreateTag.setAccessible(true);
			craftItemTag.setAccessible(true);
			
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException | NoSuchFieldException exception) {
			exception.printStackTrace();
		}
	}
	
	
	public static NbtFactory getInstance() {
		if (instance == null) {
			instance = new NbtFactory();
		}
		
		return instance;
	}
	

	@SuppressWarnings("unchecked")
	public Map<String, Object> getDataMap(Object handle) {
		try {
			
			return (Map<String, Object>) ReflectionHandler.getValue(handle, true, NbtType.TAG_COMPOUND.getFieldName());
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
				| SecurityException exception) {
			exception.printStackTrace();
		}

		return null;
	}
	

	@SuppressWarnings("unchecked")
	public List<Object> getDataList(Object handle) {
		try {
			
			return (List<Object>) ReflectionHandler.getValue(handle, true, NbtType.TAG_LIST.getFieldName());
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
				| SecurityException exception) {
			exception.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a new NBT list of an unspecified type.
	 * 
	 * @return The NBT list.
	 */
	
    public static NbtList createList(Object... content) {
        return createList(Arrays.asList(content));
    }
    

	public static NbtList createList(Iterable<? extends Object> iterable) {
		NbtList list = new NbtList(instance.createTag(NbtType.TAG_LIST, null));

		for (Object object : iterable)
			list.add(object);
		
		return list;
	}

	/**
	 * Construct a new NBT root compound.
	 * <p>
	 * This compound must be given a name, as it is the root object.
	 * 
	 * @param name
	 *            - the name of the compound.
	 * @return The NBT compound.
	 */
	
	public static NbtCompound createRootCompound() {
		return new NbtCompound(getInstance().createTag(NbtType.TAG_COMPOUND, null));
	}
	
	/**
	 * Construct a new NBT compound.
	 * <p>
	 * Use {@link NbtCompound#asMap()} to modify it.
	 * 
	 * @return The NBT compound.
	 */
	
	public static NbtCompound createCompound() {
		return new NbtCompound(getInstance().createTag(NbtType.TAG_COMPOUND, null));
	}

	/**
	 * Construct a new NBT wrapper from a list.
	 * 
	 * @param nmsList
	 *            - the NBT list.
	 * @return The wrapper.
	 */

	public static NbtList fromList(Object nmsList) {
		return new NbtList(nmsList);
	}

	/**
	 * Construct a new NBT wrapper from a compound.
	 * 
	 * @param nmsCompound
	 *            - the NBT compund.
	 * @return The wrapper.
	 */
	
	public static NbtCompound fromCompound(Object nmsCompound) {
		return new NbtCompound(nmsCompound);
	}

	/**
	 * Set the NBT compound tag of a given item stack.
	 * <p>
	 * The item stack must be a wrapper for a CraftItemStack. Use
	 * {@link MinecraftReflection#getBukkitItemStack(ItemStack)} if not.
	 * 
	 * @param stack
	 *            - the item stack, cannot be air.
	 * @param compound
	 *            - the new NBT compound, or NULL to remove it.
	 * @throws IllegalArgumentException
	 *             If the stack is not a CraftItemStack, or it represents air.
	 */
	
	public static void setTag(ItemStack stack, NbtCompound compound) {
		checkItemStack(stack);
		Object nms = null;

		try {			
			nms = ReflectionHandler.getValue(stack, true, getInstance().craftItemHandle.getName());
			ReflectionHandler.setValue(nms, true, getInstance().craftItemTag.getName(), compound.getHandle());
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
				| SecurityException exception) {
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
	
	public static NbtCompound fromTag(ItemStack stack) {
		checkItemStack(stack);
		Object nms, tag = null;

		try {
			nms = ReflectionHandler.getValue(stack, true, getInstance().craftItemHandle.getName());
			tag = ReflectionHandler.getValue(nms, true, getInstance().craftItemTag.getName());
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException exception) {
			exception.printStackTrace();
		}

		if (tag == null) {
			NbtCompound compound = createRootCompound();
			setTag(stack, compound);
			
			return compound;
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
	
	public static ItemStack getCraftItemStack(ItemStack stack) {
		if (stack == null || getInstance().craftItem.isAssignableFrom(stack.getClass())) {
			return stack;
		}
		
		try {
			Constructor<?> caller = instance.craftItem.getDeclaredConstructor(ItemStack.class);
			caller.setAccessible(true);
			
			return (ItemStack) caller.newInstance(stack);
		} catch (Exception e) {
			
			throw new IllegalStateException("Unable to convert " + stack + " + to a CraftItemStack.");
		}
	}

	/**
	 * Ensure that the given stack can store arbitrary NBT information.
	 * 
	 * @param stack
	 *            - the stack to check.
	 */
	
	private static void checkItemStack(ItemStack stack) {
		if (stack == null) {
			throw new IllegalArgumentException("Stack cannot be NULL.");
		}
		
		if (!getInstance().craftItem.isAssignableFrom(stack.getClass())) {
			throw new IllegalArgumentException("Stack must be a CraftItemStack.");
		}
		
		if (stack.getType() == Material.AIR) {
			throw new IllegalArgumentException("ItemStacks representing air cannot store NMS information.");
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
	
	public Object unwrapValue(String name, Object value) {
		
		if (value == null) {
			return null;
		}

		if (value instanceof Wrapper) {
			
			return ((Wrapper) value).getHandle();
		} else if (value instanceof List) {
			
			throw new IllegalArgumentException("Can only insert a WrappedList.");
		} else if (value instanceof Map) {
			
			throw new IllegalArgumentException("Can only insert a WrappedCompound.");
		} else {
		
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

	public Object wrapNative(Object nms) {

		if (nms == null) {
			return null;
		}
		
		if (baseClass.isAssignableFrom(nms.getClass())) {			
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
					
					return ReflectionHandler.getValue(nms, true, type.getFieldName());
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
						| SecurityException exception) {
					exception.printStackTrace();
				}
			}
		}
		
		return null;
	}

	/**
	 * Construct a new NMS NBT tag initialized with the given value.
	 * 
	 * @param type
	 *            - the NBT type.
	 * @param name
	 *            - the name of the NBT tag.
	 * @param value
	 *            - the value, or NULL to keep the original value.
	 * @return The created tag.
	 */

	private Object createTag(NbtType type, Object value) {
		Object tag = null;

		try {
			tag = nbtCreateTag.invoke(null, (byte)type.id);	
			if (value != null) {
				ReflectionHandler.setValue(tag, true, ReflectionHandler.getField(tag.getClass(), true, type.getFieldName()).getName(), value);
			}			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException | SecurityException exception) {
			exception.printStackTrace();
		}
		
		return tag;
	}

	/**
	 * Retrieve the NBT type from a given NMS NBT tag.
	 * 
	 * @param nms
	 *            - the native NBT tag.
	 * @return The corresponding type.
	 */

	public NbtType getType(Object nms) {
		int type = 0;

		try {
			type = (Byte) ReflectionHandler.invokeMethod(nms, nbtGetType.getName());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException exception) {
			exception.printStackTrace();
		}

		return nbtEnum.get(type);
	}

	/**
	 * Retrieve the nearest NBT type for a given primitive type.
	 * 
	 * @param primitive
	 *            - the primitive type.
	 * @return The corresponding type.
	 */

	public NbtType getPrimitiveType(Object primitive) {
		NbtType type = nbtEnum.get(nbtClass.inverse().get(Primitives.unwrap(primitive.getClass())));

		if (type == null) {
			throw new IllegalArgumentException(String.format("Illegal type: %s (%s)", primitive.getClass(), primitive));
		}
		
		return type;
	}
}