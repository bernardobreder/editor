package project.explorer;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import project.filesystem.FSFile;
import project.filesystem.FSManager;
import project.filesystem.FSResource;
import project.filesystem.FSWorkspace;

public class ExplorerTree extends JTree {

	private FSManager fsManager;

	private ExplorerTreeListener listener;

	public ExplorerTree(FSManager fsManager) {
		super(new ExplorerTreeModel(fsManager));
		this.fsManager = fsManager;
		setCellRenderer(new ExplorerTreeRenderer(fsManager));
		setRootVisible(false);
		setShowsRootHandles(true);
		setRowHeight(18);
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				treeMouseClicked(e);
			}
		});
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				treeKeyPressed(e);
			}
		});
	}

	protected void treeKeyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (listener != null) {
				TreePath[] paths = getSelectionPaths();
				for (TreePath path : paths) {
					FSResource resource = (FSResource) path.getLastPathComponent();
					if (resource.isFile()) {
						FSFile file = (FSFile) resource;
						listener.onOpenFile(file);
					}
				}
			}
		}
	}

	protected void treeMouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			if (listener != null) {
				TreePath[] paths = getSelectionPaths();
				for (TreePath path : paths) {
					FSResource resource = (FSResource) path.getLastPathComponent();
					if (resource.isFile()) {
						FSFile file = (FSFile) resource;
						listener.onOpenFile(file);
					}
				}
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			if (listener != null) {
				TreePath[] paths = getSelectionPaths();
				// listener.onPopupFile(paths);
			}
		}
	}

	/**
	 * @return the dir
	 */
	public FSWorkspace getWorkspace() {
		return fsManager.getWorkspace();
	}

	public void refresh() {
		getModel().refresh();
	}

	public ExplorerTreeListener getListener() {
		return listener;
	}

	public void setListener(ExplorerTreeListener listener) {
		this.listener = listener;
	}

	@Override
	public ExplorerTreeModel getModel() {
		return (ExplorerTreeModel) super.getModel();
	}

	public static interface ExplorerTreeListener {

		public void onOpenFile(FSFile file);

	}

}
