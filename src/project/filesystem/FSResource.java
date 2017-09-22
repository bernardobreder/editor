package project.filesystem;

import java.io.File;
import java.util.LinkedList;

public abstract class FSResource implements Comparable<FSResource> {
	
	protected FSFolder parent;
	
	protected final File file;
	
	public FSResource(FSFolder parent, File file) {
		super();
		this.parent = parent;
		this.file = file;
	}
	
	public String getName() {
		return file.getName();
	}
	
	public FSFolder getParent() {
		return parent;
	}
	
	public boolean exist() {
		return file.exists();
	}
	
	public boolean isFile() {
		return false;
	}
	
	public boolean isFolder() {
		return false;
	}
	
	public boolean isProject() {
		return this instanceof FSProject;
	}
	
	public boolean isWorkspace() {
		return this instanceof FSWorkspace;
	}
	
	public FSWorkspace getWorkspace() {
		FSResource file = this;
		while (file instanceof FSWorkspace == false) {
			file = file.getParent();
		}
		return (FSWorkspace) file;
	}
	
	public FSProject getProject() {
		FSResource file = this;
		while (file instanceof FSProject == false) {
			file = file.getParent();
		}
		return (FSProject) file;
	}
	
	public File toExternalFile() {
		return file;
	}
	
	@Override
	public int compareTo(FSResource o) {
		if (file.isDirectory()) {
			if (!o.file.isDirectory()) {
				return -1;
			}
		} else if (o.file.isDirectory()) {
			if (!file.isDirectory()) {
				return 1;
			}
		}
		return file.getAbsolutePath().compareTo(o.file.getAbsolutePath());
	}
	
	@Override
	public int hashCode() {
		return file.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FSResource other = (FSResource) obj;
		if (!file.equals(other.file)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		LinkedList<FSResource> list = new LinkedList<FSResource>();
		FSResource res = this;
		while (!res.isWorkspace()) {
			list.addFirst(res);
			res = res.getParent();
		}
		StringBuilder sb = new StringBuilder();
		sb.append('/');
		for (FSResource item : list) {
			if (item.isWorkspace()) {
				sb.append("/");
			} else if (item.isFile()) {
				sb.append(item.getName());
			} else {
				sb.append(item.getName());
				sb.append("/");
			}
		}
		return sb.toString();
	}
	
}
