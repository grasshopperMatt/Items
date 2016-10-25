package com.projectkorra.items.attributes;

import java.util.UUID;

import com.projectkorra.items.attributes.Attributes.Operation;
import com.projectkorra.items.attributes.nbt.NbtCompound;

public class Attribute {
	private NbtCompound compound;
	private AttributeBuilder builder;
	
	
	public Attribute(AttributeBuilder builder, NbtCompound compound) {
		compound.put("name", builder.name);
		compound.put("id", builder.uuid);
		compound.put("amount", builder.amount);
		compound.put("operation", builder.operation);
		compound.put("type", builder.type);
		
		this.compound = compound;
		this.builder = builder;
	}
	
	
	public NbtCompound getCompound() {
		return compound;
	}
	
	
	public AttributeBuilder getBuilder() {
		return builder;
	}
 
    
	public AttributeBuilder newBuilder() {
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
