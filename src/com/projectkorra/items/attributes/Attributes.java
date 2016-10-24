package com.projectkorra.items.attributes;

import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.projectkorra.items.attributes.nbt.Nbt;
import com.projectkorra.items.attributes.nbt.NbtCompound;
import com.projectkorra.items.attributes.nbt.NbtList;

public class Attributes {
	
	private ItemStack item;
	private NbtList attributes;
	
	public enum Operation {
		ADD_NUMBER(0), MULTIPLY_PERCENTAGE(1), ADD_PERCENTAGE(2);
		
		
		private int id;
		
		
		private Operation(int id) {
			this.id = id;
		}
		
		
		public int getId() {
			return id;
		}
		
		
		public static Operation fromId(int id) {
			for (Operation o : values()) {
				if (o.getId() == id) {
					return o;
				}
			}
			return null;
		}	
	}
	
	
	public Attributes(ItemStack item) {
		this.item = Nbt.getCraftItemStack(item);
		this.attributes = Nbt.fromTag(item).getList("AttributeModifiers", true);		
	}
	
	
	public ItemStack getItem() {
		return item;
	}
	
	
	public NbtList getAttributes() {
		return attributes;
	}
   
	
    public boolean remove(Attribute attribute) {
        UUID uuid = attribute.getId();
        
        for (Iterator<Attribute> it = values().iterator(); it.hasNext();) {
            if (Objects.equals(it.next().getId(), uuid)) {
                it.remove();
                return true;
            }
        }  
        return false;
    }
    
    public Iterable<Attribute> values() {
        return new Iterable<Attribute>() {
            @Override
            public Iterator<Attribute> iterator() {
                return Iterators.transform(attributes.iterator(), 
                  new Function<Object, Attribute>() {
             
                    public Attribute apply( Object element) {
                        return new Attribute(null, (NbtCompound) element);
                    }
                });
            }
        };
    }
}
