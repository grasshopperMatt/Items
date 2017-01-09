package com.projectkorra.items.configuration;

import java.io.File;

public class Configuration {
	private String name;
	private File file;
	
	public Configuration(File file, String name) {
		this.file = file;
		this.name = name;
	}
	
	public File getFile() {
		return file;	
	}
	
	public String getName() {
		return name;
	}
}
