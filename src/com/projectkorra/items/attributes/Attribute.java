package com.projectkorra.items.attributes;

import java.util.UUID;

import com.projectkorra.items.attributes.Attributes.Operation;
import com.projectkorra.items.attributes.nbt.NbtCompound;

public class Attribute {
	private NbtCompound compound;
	private AttributeBuilder builder;
	
	
	public Attribute(AttributeBuilder builder, NbtCompound compound) {
		compound.put("Name", builder.name);
		compound.put("Amount", builder.amount);
		compound.put("Operation", builder.operation);
		compound.put("AttributeType", builder.type);
		compound.put("UUIDMost", builder.uuid.getMostSignificantBits());
		compound.put("UUIDLeast", builder.uuid.getLeastSignificantBits());
		
		this.compound = compound;
		this.builder = builder;
	}
	
	
	public NbtCompound getCompound() {
		return compound;
	}
	
	
	public AttributeBuilder getBuilder() {
		return builder;
	}
 
    
	public static AttributeBuilder newBuilder() {
		return new AttributeBuilder();
	}
	
	
	public static class AttributeBuilder {
		private double amount;
		private Operation operation;
		private AttributeType type;
		private String name;
		private UUID uuid;
		
		
		private AttributeBuilder() {
			
		}
		
		
		public AttributeBuilder amount(double amount) {
			this.amount = amount;
			return this;
		}
		
		
		public AttributeBuilder operation(Operation operation) {
			this.operation = operation;
			return this;
		}
		
		
		public AttributeBuilder type(AttributeType type) {
			this.type = type;
			return this;
		}
		
		
		public AttributeBuilder name(String name) {
			this.name = name;
			return this;
		}
		
		
		public AttributeBuilder uuid(UUID uuid) {
			this.uuid = uuid;
			return this;
		}
		
		
		public Attribute build() {
			return new Attribute(this, null);
		}
	}
}
