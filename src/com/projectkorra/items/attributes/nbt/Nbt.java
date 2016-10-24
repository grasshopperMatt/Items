package com.projectkorra.items.attributes.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.primitives.Primitives;
import com.projectkorra.items.attributes.nbt.util.Wrapper;
import com.projectkorra.projectkorra.util.ReflectionHandler;

public class Nbt {
	
	public enum NbtType {
		
		TAG_END(0, Void.class), TAG_BYTE(1, byte.class), TAG_SHORT(2, short.class), TAG_INT(3, int.class), 
		TAG_LONG(4, long.class), TAG_FLOAT(5, float.class), TAG_DOUBLE(6, double.class), TAG_BYTE_ARRAY(7,byte[].class), 
		TAG_STRING(8, String.class), TAG_LIST(9, List.class), TAG_COMPOUND(10, Map.class), TAG_INT_ARRAY(11, int[].class);
		
		
		private final int id;
		private Nbt nbt;
		

		private NbtType(int uniqueId, Class<?> type) {
			id = uniqueId;
			
			nbt = Nbt.getInstance();
			nbt.NBT_CLASS.put(id, type);
			nbt.NBT_ENUM.put(id, this);
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
	
	
	private static Nbt INSTANCE;
	
	public BiMap<Integer, Class<?>> NBT_CLASS = HashBiMap.create();
	public BiMap<Integer, NbtType> NBT_ENUM = HashBiMap.create();
	
	public Class<?> BASE_CLASS;
	public Class<?> COMPOUND_CLASS;
	public Method NBT_CREATE_TAG;
	public Method NBT_GET_TYPE;
	public Field NBT_LIST_TYPE;
	
	private Class<?> CRAFT_STACK;
	private Field CRAFT_HANDLE;
	private Field STACK_TAG;
	
	public Method LOAD_COMPOUND;
	public Method SAVE_COMPOUND;
	

	private Nbt() {
		if (BASE_CLASS == null) {
			
			ClassLoader loader = Nbt.class.getClassLoader();
			String packageName = Bukkit.getServer().getClass().getPackage().getName();
			
			try {
				
				Class<?> offlinePlayer = loader.loadClass(packageName + ".CraftOfflinePlayer");
				
				COMPOUND_CLASS = ReflectionHandler.getMethod(offlinePlayer, "getData").getReturnType();
				BASE_CLASS = COMPOUND_CLASS.getSuperclass();
				NBT_GET_TYPE = ReflectionHandler.getMethod(BASE_CLASS, "getTypeId");
				NBT_CREATE_TAG = ReflectionHandler.getMethod(BASE_CLASS, "createTag", byte.class, String.class);

				CRAFT_STACK = loader.loadClass(packageName + ".inventory.CraftItemStack");
				CRAFT_HANDLE = ReflectionHandler.getField(CRAFT_STACK, true, "handle");
				STACK_TAG = ReflectionHandler.getField(CRAFT_HANDLE.getType(), true, "tag");

				LOAD_COMPOUND = ReflectionHandler.getMethod(BASE_CLASS, null, DataInput.class);
				LOAD_COMPOUND = ReflectionHandler.getMethod(BASE_CLASS, null, BASE_CLASS, DataOutput.class);
				
			} catch (NoSuchMethodException | NoSuchFieldException | SecurityException | ClassNotFoundException exception) {
				exception.printStackTrace();
			}
		}
	}
	
	public static Nbt getInstance() {
		if (INSTANCE == null)
			INSTANCE = new Nbt();
		return INSTANCE;
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
		NbtList list = new NbtList(INSTANCE.createTag(NbtType.TAG_LIST, "", null));

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
	
	public static NbtCompound createRootCompound(String name) {
		return new NbtCompound(getInstance().createTag(NbtType.TAG_COMPOUND, name, null));
	}
	
	/**
	 * Construct a new NBT compound.
	 * <p>
	 * Use {@link NbtCompound#asMap()} to modify it.
	 * 
	 * @return The NBT compound.
	 */
	
	public static NbtCompound createCompound() {
		return new NbtCompound(getInstance().createTag(NbtType.TAG_COMPOUND, "", null));
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
			nms = ReflectionHandler.getValue(stack, true, getInstance().CRAFT_HANDLE.getName());
			ReflectionHandler.setValue(compound.getHandle(), true, getInstance().STACK_TAG.getName(), nms);
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
			nms = ReflectionHandler.getValue(stack, true, getInstance().CRAFT_HANDLE.getName());
			tag = ReflectionHandler.getValue(nms, true, getInstance().STACK_TAG.getName());
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException exception) {
			exception.printStackTrace();
		}

		if (tag == null) {
			NbtCompound compound = createRootCompound("tag");
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
		if (stack == null || getInstance().CRAFT_STACK.isAssignableFrom(stack.getClass())) {
			return stack;
		}
		
		try {
			Constructor<?> caller = INSTANCE.CRAFT_STACK.getDeclaredConstructor(ItemStack.class);
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
		
		if (!getInstance().CRAFT_STACK.isAssignableFrom(stack.getClass())) {
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
			
			return createTag(getPrimitiveType(value), name, value);
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
		
		if (BASE_CLASS.isAssignableFrom(nms.getClass())) {
			
			final NbtType type = getType(nms);
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

	private Object createTag(NbtType type, String name, Object value) {
		Object tag = null;

		try {
			tag = ReflectionHandler.invokeMethod(null, NBT_CREATE_TAG.getName(), (byte) type.id, name);	
			if (value != null) {
				ReflectionHandler.setValue(tag, true, ReflectionHandler.getField(null, true, type.getFieldName()).getName(), value);
			}			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| NoSuchFieldException | SecurityException exception) {

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
			type = (Integer) ReflectionHandler.invokeMethod(nms, NBT_GET_TYPE.getName());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException exception) {

			exception.printStackTrace();
		}

		return NBT_ENUM.get(type);
	}

	/**
	 * Retrieve the nearest NBT type for a given primitive type.
	 * 
	 * @param primitive
	 *            - the primitive type.
	 * @return The corresponding type.
	 */

	public NbtType getPrimitiveType(Object primitive) {
		NbtType type = NBT_ENUM.get(NBT_CLASS.inverse().get(Primitives.unwrap(primitive.getClass())));

		if (type == null) {
			throw new IllegalArgumentException(String.format("Illegal type: %s (%s)", primitive.getClass(), primitive));
		}
		
		return type;
	}
}