package com.projectkorra.items.attributes;

import java.util.UUID;

import com.projectkorra.items.attributes.Attributes.Operation;
import com.projectkorra.items.attributes.nbt.Nbt;
import com.projectkorra.items.attributes.nbt.NbtCompound;

public class Attribute {
	
	private NbtCompound data;
	
	
	public Attribute(AttributeBuilder builder, NbtCompound dataCopy) {
		if (dataCopy == null) {
			data = Nbt.createCompound();
		} else {
			data = dataCopy;
		}
		
		data.put("Amount", builder.amount);
		data.put("Name", builder.name);
		data.put("UUIDMost", builder.uuid.getMostSignificantBits());
		data.put("UUIDLeast", builder.uuid.getLeastSignificantBits());
		data.put("Operation", builder.operation);
		data.put("AttributeType", builder.type);
		
	}
	
	
	public double getAmount() {
		return data.getDouble("Amount", 0);
	}
	
	
	public String getName() {
		return data.getString("Name", null);
	}
	
    
    public UUID getId() {
    	long most = data.getLong("UUIDMost", 0);
    	long least = data.getLong("UUIDLeast", 0);
    	return new UUID(most, least);
    }
	
	
	public Operation getOperation() {
		int id = data.getInteger("Operation", 0);
		return Operation.fromId(id);
	}
	
	
	public AttributeType getAttributeType() {
		String id = data.getString("AttributeType", null);
		return AttributeType.fromId(id);
	}
    
    
	public static AttributeBuilder newBuilder() {
		return new AttributeBuilder();
	}
	
	
	public static class AttributeBuilder {
		
		public double amount;
		public Operation operation;
		public AttributeType type;
		public String name;
		public UUID uuid;
		
		
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
