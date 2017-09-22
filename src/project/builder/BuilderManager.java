package project.builder;

import project.builder.c.CBuilder;
import project.console.ConsoleInstance;
import project.filesystem.FSFile;

public class BuilderManager {
	
	private final CBuilder cBuilder = new CBuilder();
	
	public void compile(ConsoleInstance console, FSFile file) {
		cBuilder.compile(console, file);
	}
	
}
