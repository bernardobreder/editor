package project.explorer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeCellRenderer;

import project.filesystem.FSFolder;
import project.filesystem.FSManager;
import project.filesystem.FSResource;
import project.resource.Resource;

public class ExplorerTreeRenderer extends JPanel implements TreeCellRenderer {
	
	private JLabel icon;
	
	private JLabel text;
	
	private JLabel status;
	
	private int size = 16;
	
	private FSManager fsManager;
	
	public ExplorerTreeRenderer(FSManager fsManager) {
		super(new BorderLayout(5, 5));
		this.fsManager = fsManager;
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		add(icon = new JLabel(), BorderLayout.WEST);
		add(text = new JLabel(), BorderLayout.CENTER);
		add(status = new JLabel(), BorderLayout.EAST);
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		ExplorerTreeModel model = (ExplorerTreeModel) tree.getModel();
		FSResource file = (FSResource) value;
		if (file.isFolder()) {
			FSFolder folder = (FSFolder) file;
			if (folder.isProject()) {
				icon.setIcon(new ImageIcon(Resource.getProject(size, size)));
			} else {
				icon.setIcon(new ImageIcon(Resource.getFolder(size, size)));
			}
		} else {
			icon.setIcon(new ImageIcon(Resource.getFile(size, size)));
		}
		text.setText(file.getName());
		setOpaque(selected);
		if (selected) {
			setBackground(new Color(0.8f, 0.8f, 1.0f));
		}
		BasicTreeUI treeUI = (BasicTreeUI) tree.getUI();
		int offset = treeUI.getLeftChildIndent() + treeUI.getRightChildIndent();
		if (tree.getParent() != null) {
			setPreferredSize(new Dimension(tree.getParent().getParent().getPreferredSize().width - offset * model.path(file).getPathCount() + treeUI.getExpandedIcon().getIconWidth(), tree.getRowHeight()));
		}
		return this;
	}
}
