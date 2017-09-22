package project.filesystem;

import java.io.File;

public class FSWorkspace extends FSFolder {

	public FSWorkspace(File file) {
		super(null, file);
	}

	@Override
	protected FSResource createResource(File file) {
		if (file.isDirectory()) {
			return new FSProject(this, file);
		}
		return super.createResource(file);
	}

}
