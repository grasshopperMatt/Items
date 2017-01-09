package com.projectkorra.items.attributes.tmp;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.projectkorra.items.attributes.nbt.NBTCompound;
import com.projectkorra.items.attributes.nbt.NBTHandler;
import com.projectkorra.items.attributes.nbt.NBTList;

public class Attributes {
	private ItemStack item;
	private NBTList attributes;
	
	
	public Attributes(ItemStack item) {
		this.item = NBTHandler.getItem(item);
		attributes = NBTHandler.fromTag(item).getList("AttributeModifiers", true);		
	}
	
	
	public ItemStack getItem() {
		return item;
	}
	
	
	public NBTList getAttributes() {
		return attributes;
	}
    
	
   	public Iterable<Attribute> values() {
  		return new Iterable<Attribute>() {
    	
       		@Override
       		public Iterator<Attribute> iterator() {
            		return Iterators.transform(attributes.iterator(), new Function<Object, Attribute>() {
            			
            			@Override
                			public Attribute apply(Object element) {
                    			return new Attribute(null, (NBTCompound) element);
                			}
            		});
        		}
  	  	};
	}
   	
    
    public boolean remove(Attribute attribute) {
    	long most = (Long) attribute.getCompound().get("UUIDMost"); 
    	long least = (Long) attribute.getCompound().get("UUIDLeast");
    	UUID id = new UUID(most, least);
    	
        for (Iterator<Attribute> i = values().iterator(); i.hasNext();) {
        	long most2 = (Long) i.next().getCompound().get("UUIDMost");
        	long least2 = (Long) i.next().getCompound().get("UUIDLeast");
        	UUID id2 = new UUID(most2, least2);
        	
            if (id2 == id) {
                i.remove();
                return true;
            }
        } 
        
        return false;
    }
   	
    
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
}
