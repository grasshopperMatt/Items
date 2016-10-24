package com.projectkorra.items.attributes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AttributeType {
	public static final AttributeType GENERIC_MAX_HEALTH = new AttributeType("generic.maxHealth").register();
	public static final AttributeType GENERIC_FOLLOW_RANGE = new AttributeType("generic.followRange").register();
	public static final AttributeType GENERIC_ATTACK_DAMAGE = new AttributeType("generic.attackDamage").register();
	public static final AttributeType GENERIC_MOVEMENT_SPEED = new AttributeType("generic.movementSpeed").register();
	public static final AttributeType GENERIC_KNOCKBACK_RESISTANCE = new AttributeType("generic.knockbackResistance").register();
	private static final ConcurrentMap<String, AttributeType> LOOKUP = new ConcurrentHashMap<>();
	
	
	private final String id;

	
	public AttributeType(String minecraftId) {
		id = minecraftId;
	}
	
	
	public static AttributeType fromId(String id) {
		return LOOKUP.get(id);
	}
	
	
	public static Iterable<AttributeType> values() {
		return LOOKUP.values();
	}
	
	
	public AttributeType register() {
		AttributeType attribute = LOOKUP.putIfAbsent(id, this);
		return attribute != null ? attribute : this;
	}	
	
	
	public String getId() {
		return id;
	}	
}
