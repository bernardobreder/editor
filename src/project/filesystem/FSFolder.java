package project.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FSFolder extends FSResource {

	public FSFolder(FSFolder parent, File file) {
		super(parent, file);
	}

	public List<FSResource> list() {
		File[] files = file.listFiles();
		if (files == null) {
			return new ArrayList<FSResource>(0);
		}
		ArrayList<FSResource> list = new ArrayList<FSResource>(files.length);
		for (File file : files) {
			list.add(createResource(file));
		}
		return list;
	}

	public FSResource find(String name) {
		File[] files = file.listFiles();
		if (files == null) {
			return null;
		}
		for (File file : files) {
			if (file.getName().equals(name)) {
				return createResource(file);
			}
		}
		return null;
	}

	public FSFolder findFolder(String name) {
		File[] files = file.listFiles();
		if (files == null) {
			return null;
		}
		for (File file : files) {
			if (file.isDirectory()) {
				if (file.getName().equals(name)) {
					return (FSFolder) createResource(file);
				}
			}
		}
		return null;
	}

	public FSFile findFile(String name) {
		File[] files = file.listFiles();
		if (files == null) {
			return null;
		}
		for (File file : files) {
			if (file.isFile()) {
				if (file.getName().equals(name)) {
					return (FSFile) createResource(file);
				}
			}
		}
		return null;
	}

	protected FSResource createResource(File file) {
		if (file.isDirectory()) {
			return new FSFolder(this, file);
		} else {
			return new FSFile(this, file);
		}
	}

	public void mkdirs() {
		file.mkdirs();
	}

	public boolean isProject() {
		return this instanceof FSProject;
	}

	public boolean isWorkspace() {
		return this instanceof FSWorkspace;
	}

	@Override
	public boolean isFolder() {
		return true;
	}

}
