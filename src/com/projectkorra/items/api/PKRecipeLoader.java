package com.projectkorra.items.api;

import java.io.File;

public class PKRecipeLoader extends PKLoader {
	
	/**
	 * Recipe's use a separate loader so that you may include ProjectKorraItems in your recipes
	 */
	
	@SuppressWarnings("unused")
	private File file;
	
	public PKRecipeLoader(File file) {
		this.file = file;
	}

	@Override
	public Object analyze(String string) {
		return null;
	}
	
	

}
