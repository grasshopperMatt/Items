package com.projectkorra.items.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class PKLoader {
	
	public abstract Object analyze(String string);
	
	protected String read(File file) {
		String configString = "";
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file.getPath()));
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		}

		try {
			StringBuilder builder = new StringBuilder();
			String line = reader.readLine();

			while (line != null) {
				builder.append(line);
				builder.append("\n");
				line = reader.readLine();
			}
			
			configString = builder.toString();
		} catch (IOException exception) {
			exception.printStackTrace();
			return configString;
		}

		try {
			reader.close();
		} catch (IOException exception) {
			exception.printStackTrace();
			return configString;
		}

		return configString;
	}
}
