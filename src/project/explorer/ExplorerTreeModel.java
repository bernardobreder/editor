package project.explorer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import project.filesystem.FSFolder;
import project.filesystem.FSManager;
import project.filesystem.FSResource;
import project.filesystem.FSWorkspace;

public class ExplorerTreeModel implements TreeModel {
	
	/** Listenres */
	private final List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
	
	/** Root */
	private FSWorkspace root;
	
	/** Arquivos lidos */
	private Map<FSFolder, List<FSResource>> cache = new TreeMap<FSFolder, List<FSResource>>();
	
	/** Sistema de arquivo */
	private FSManager fsManager;
	
	public ExplorerTreeModel(FSManager fsManager) {
		this.fsManager = fsManager;
		root = fsManager.getWorkspace();
		root.mkdirs();
	}
	
	@Override
	public Object getRoot() {
		return root;
	}
	
	@Override
	public Object getChild(Object parent, int index) {
		FSFolder file = (FSFolder) parent;
		List<FSResource> files = cache.get(file);
		return files.get(index);
	}
	
	@Override
	public int getChildCount(Object parent) {
		FSResource res = (FSResource) parent;
		if (res.isFile()) {
			return 0;
		}
		FSFolder file = (FSFolder) parent;
		List<FSResource> files = file.list();
		if (files == null) {
			return 0;
		}
		Collections.sort(files);
		cache.put(file, files);
		return files.size();
	}
	
	@Override
	public boolean isLeaf(Object node) {
		FSResource file = (FSResource) node;
		return file.isFile();
	}
	
	@Override
	public int getIndexOfChild(Object parent, Object child) {
		FSResource file = (FSResource) parent;
		List<FSResource> files = cache.get(file);
		for (int n = 0; n < files.size(); n++) {
			if (files.get(n).equals(child)) {
				return n;
			}
		}
		return -1;
	}
	
	public void refresh() {
		for (Entry<FSFolder, List<FSResource>> entry : cache.entrySet()) {
			FSFolder parentFile = entry.getKey();
			List<FSResource> oldFiles = entry.getValue();
			List<FSResource> newFiles = parentFile.list();
			if (!newFiles.equals(oldFiles)) {
				entry.setValue(newFiles);
				Object[] path = path(parentFile).getPath();
				Collections.sort(newFiles);
				int i, j;
				for (i = 0, j = 0; i < oldFiles.size() && j < newFiles.size();) {
					int cmp = oldFiles.get(i).compareTo(newFiles.get(j));
					if (cmp > 0) {
						treeNodesInserted(path, new int[] { j }, new Object[] { newFiles.get(j++) });
					} else if (cmp < 0) {
						treeNodesRemoved(path, new int[] { i }, new Object[] { oldFiles.get(i++) });
					} else {
						i++;
						j++;
					}
				}
				for (; i < oldFiles.size(); i++) {
					treeNodesRemoved(path, new int[] { i }, new Object[] { oldFiles.get(i) });
				}
				for (; j < newFiles.size(); j++) {
					treeNodesInserted(path, new int[] { j }, new Object[] { newFiles.get(j) });
				}
			}
		}
	}
	
	public TreePath path(FSResource file) {
		LinkedList<FSResource> files = new LinkedList<FSResource>();
		while (!file.equals(root)) {
			files.addFirst(file);
			file = file.getParent();
		}
		files.addFirst(file);
		return new TreePath(files.toArray());
	}
	
	public void treeStructureChanged(Object[] path, int[] childIndices, Object[] children) {
		if (listeners.isEmpty()) {
			return;
		}
		TreeModelEvent event = new TreeModelEvent(this, path, childIndices, children);
		for (int n = listeners.size() - 1; n >= 0; n--) {
			listeners.get(n).treeStructureChanged(event);
		}
	}
	
	public void treeNodesChanged(Object[] path, int[] childIndices, List<FSResource> oldFiles) {
		if (listeners.isEmpty()) {
			return;
		}
		TreeModelEvent event = new TreeModelEvent(this, path, childIndices, oldFiles.toArray());
		for (int n = listeners.size() - 1; n >= 0; n--) {
			listeners.get(n).treeNodesChanged(event);
		}
	}
	
	public void treeNodesInserted(Object[] path, int[] childIndices, Object[] children) {
		if (listeners.isEmpty()) {
			return;
		}
		TreeModelEvent event = new TreeModelEvent(this, path, childIndices, children);
		for (int n = listeners.size() - 1; n >= 0; n--) {
			listeners.get(n).treeNodesInserted(event);
		}
	}
	
	public void treeNodesRemoved(Object[] path, int[] childIndices, Object[] children) {
		if (listeners.isEmpty()) {
			return;
		}
		TreeModelEvent event = new TreeModelEvent(this, path, childIndices, children);
		for (int n = listeners.size() - 1; n >= 0; n--) {
			listeners.get(n).treeNodesRemoved(event);
		}
	}
	
	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}
	
	@Override
	public void addTreeModelListener(TreeModelListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void removeTreeModelListener(TreeModelListener listener) {
		listeners.remove(listener);
	}
	
}
