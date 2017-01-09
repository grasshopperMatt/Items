package com.projectkorra.items.attributes.tmp;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import com.projectkorra.items.attributes.tmp.Attributes.Operation;

public class AttributeStorage {
	private ItemStack target;
	private UUID uuid;
	
	
	public AttributeStorage(ItemStack target, UUID uuid) {
		this.target = target;
		this.uuid = uuid;
	}
	
	
	public ItemStack getTarget() {
		return target;
	}
	
	
	public UUID getId() {
		return uuid;
	}
	
	
	private Attribute getAttribute(Attributes instance) {
		for (Attribute attribute : instance.values()) {
			long most = (Long) attribute.getCompound().get("UUIDMost");
			long least = (Long) attribute.getCompound().get("UUIDLeast");
			
			if (new UUID(most, least) == uuid) {
				return attribute;
			}
		}
		
		return null;
	}
	
	
	public String getData() {
		Attribute attribute = getAttribute(new Attributes(target));
		return attribute != null ? (String) attribute.getCompound().get("name") : null;
	}
	
	
	public void setData(String data) {
		Attributes attributes = new Attributes(target); 
		
		if (getAttribute(attributes) == null) {
			attributes.getAttributes().add(Attribute.newBuilder()
			.name(data).amount(0).uuid(uuid).operation(Operation.ADD_NUMBER).type(AttributeType.GENERIC_ATTACK_DAMAGE).build()
			);
		} else {
			getAttribute(attributes).getCompound().put("name", data);	
		}
		
		this.target = attributes.getItem();
	}
}
