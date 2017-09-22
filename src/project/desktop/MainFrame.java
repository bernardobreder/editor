package project.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.rsta.ac.c.CLanguageSupport;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.fife.util.OSUtil;

import project.builder.BuilderManager;
import project.console.ConsolePanel;
import project.desktop.TaskQueue.Task;
import project.desktop.TaskQueue.TaskQueueListener;
import project.editor.EditorManager;
import project.explorer.ExplorerTree;
import project.explorer.ExplorerTree.ExplorerTreeListener;
import project.filesystem.FSFile;
import project.filesystem.FSManager;
import project.filesystem.FSWorkspace;
import project.outline.OutlineTree;
import project.resource.Resource;
import project.task.RunTask;

public class MainFrame extends JFrame {

	private JLabel statusLabel;

	private RSyntaxTextArea textArea;

	private ExplorerTree explorerTree;

	private OutlineTree outlineTree;

	private ConsolePanel consolePanel;

	private final BuilderManager builder = new BuilderManager();

	private final EditorManager editor;

	private TaskQueue taskQueue;

	private final FSManager fsManager = new FSManager(new FSWorkspace(new File(OSUtil.homeFile(), "workspace")));

	private final Timer timer = new Timer(true);

	public MainFrame() {
		createTaskQueue();
		editor = new EditorManager(taskQueue);
		setContentPane(createContentPane());
		setJMenuBar(createMenuBar());
		setTitle("Project Maker");
		setIconImage(Resource.getProject(64, 64));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setMinimumSize(new Dimension(950, 650));
		setSize(1000, 800);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				onWindowClose();
			}
		});
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				explorerTree.requestFocusInWindow();
			}
		});
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				onRefreshExplorer();
			}
		}, 1000, 1000);
	}

	protected void onRefreshExplorer() {
		explorerTree.refresh();
	}

	protected void onWindowClose() {
		timer.cancel();
		taskQueue.stop();
	}

	protected void onProjectNew() {
		// TODO Auto-generated method stub

	}

	protected void onFileNew() {
		// TODO Auto-generated method stub

	}

	protected void onFileOpen(FSFile file) {
		editor.save(textArea, builder, consolePanel);
		editor.open(file, textArea);
		textArea.setEditable(true);
		textArea.requestFocus();
	}

	protected void onFileSave() {
		editor.save(textArea, builder, consolePanel);
	}

	protected void onPlay() {
		editor.save(textArea, builder, consolePanel);
		taskQueue.pushWrite(new RunTask(consolePanel, editor.getEditFile()));
	}

	protected void onPause() {
		// TODO Auto-generated method stub

	}

	protected void onStop() {
		// TODO Auto-generated method stub

	}

	private void createTaskQueue() {
		taskQueue = new TaskQueue().start().setListener(new TaskQueueListener() {

			@Override
			public void onWriteEventStarted(Task task) {
				onEvent();
			}

			@Override
			public void onWriteEventFinished(Task task) {
				onEvent();
			}

			@Override
			public void onReadEventStarted(Task task) {
				onEvent();
			}

			@Override
			public void onReadEventFinished(Task task) {
				onEvent();
			}

			protected void onEvent() {
				Task lastTask = taskQueue.getExecutingList().peek();
				statusLabel.setText(lastTask == null ? " " : lastTask.getTitle());
			}
		});
	}

	private JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();
		bar.add(createFileMenu());
		return bar;
	}

	private JMenu createFileMenu() {
		JMenu menu = new JMenu("File");
		menu.add(createMenuFileSave());
		menu.add(createMenuFileRun());
		return menu;
	}

	private JMenuItem createMenuFileSave() {
		JMenuItem item = new JMenuItem("Save");
		item.setMnemonic('S');
		item.setAccelerator(KeyStroke.getKeyStroke(OSUtil.getMetaKey() + " S"));
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onFileSave();
			}
		});
		return item;
	}

	private JMenuItem createMenuFileRun() {
		JMenuItem item = new JMenuItem("Run");
		item.setMnemonic('R');
		item.setAccelerator(KeyStroke.getKeyStroke(OSUtil.getMetaKey() + " R"));
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onPlay();
			}
		});
		return item;
	}

	private JPanel createContentPane() {
		JPanel panel = new JPanel(new BorderLayout(0, 0));
		panel.setOpaque(false);
		panel.add(createPanel(), BorderLayout.CENTER);
		panel.add(createToolbarPanel(), BorderLayout.NORTH);
		panel.add(createStatusPanel(), BorderLayout.SOUTH);
		return panel;
	}

	private JPanel createPanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.add(createCenterPanel(), BorderLayout.CENTER);
		panel.add(createLeftPanel(), BorderLayout.WEST);
		panel.add(createRightPanel(), BorderLayout.EAST);
		return panel;
	}

	private Component createCenterPanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(createTextArea());
		return panel;
	}

	private Component createToolbarPanel() {
		JToolBar bar = new JToolBar();
		bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, getBackground().darker()));
		bar.setFloatable(false);
		bar.setRollover(false);
		bar.add(createToolbarProject());
		bar.add(createToolbarFile());
		bar.add(createToolbarSave());
		bar.addSeparator();
		bar.add(createToolbarPlay());
		bar.add(createToolbarPause());
		bar.add(createToolbarStop());
		return bar;
	}

	private JButton createToolbarProject() {
		ImageIcon image = new ImageIcon(Resource.getProject(24, 24));
		JButton button = new JButton(image);
		button.setFocusable(false);
		button.setRolloverEnabled(false);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onProjectNew();
			}
		});
		return button;
	}

	private JButton createToolbarFile() {
		ImageIcon image = new ImageIcon(Resource.getFileAdd(24, 24));
		JButton button = new JButton(image);
		button.setFocusable(false);
		button.setRolloverEnabled(false);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onFileNew();
			}
		});
		return button;
	}

	private JButton createToolbarSave() {
		ImageIcon image = new ImageIcon(Resource.getSave(24, 24));
		JButton button = new JButton(image);
		button.setFocusable(false);
		button.setRolloverEnabled(false);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onFileSave();
			}
		});
		return button;
	}

	private JButton createToolbarPlay() {
		ImageIcon image = new ImageIcon(Resource.getPlay(24, 24));
		JButton button = new JButton(image);
		button.setFocusable(false);
		button.setRolloverEnabled(false);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onPlay();
			}
		});
		return button;
	}

	private JButton createToolbarPause() {
		ImageIcon image = new ImageIcon(Resource.getPause(24, 24));
		JButton button = new JButton(image);
		button.setFocusable(false);
		button.setRolloverEnabled(false);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onPause();
			}
		});
		return button;
	}

	private JButton createToolbarStop() {
		ImageIcon image = new ImageIcon(Resource.getStop(24, 24));
		JButton button = new JButton(image);
		button.setFocusable(false);
		button.setRolloverEnabled(false);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onStop();
			}
		});
		return button;
	}

	private Component createStatusPanel() {
		JToolBar bar = new JToolBar();
		bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, getBackground().darker()));
		bar.setFloatable(false);
		bar.add(createStatusLabel());
		return bar;
	}

	private JLabel createStatusLabel() {
		statusLabel = new JLabel(" ");
		statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		return statusLabel;
	}

	private Component createLeftPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 1, 5, 5));
		explorerTree = new ExplorerTree(fsManager);
		explorerTree.setListener(new ExplorerTreeListener() {

			@Override
			public void onOpenFile(FSFile file) {
				onFileOpen(file);
			}
		});
		explorerTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JScrollPane scrollPane = new JScrollPane(explorerTree);
		scrollPane.setPreferredSize(new Dimension(300, 300));
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		panel.add(scrollPane);
		return panel;
	}

	private Component createRightPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
		panel.add(createOutlinePanel());
		panel.add(createConsolePanel());
		return panel;
	}

	private JPanel createOutlinePanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		outlineTree = new OutlineTree();
		outlineTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JScrollPane scrollPane = new JScrollPane(outlineTree);
		scrollPane.setPreferredSize(new Dimension(300, 300));
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		panel.add(scrollPane);
		return panel;
	}

	private JPanel createConsolePanel() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		consolePanel = new ConsolePanel();
		consolePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JScrollPane scrollPane = new JScrollPane(consolePanel);
		scrollPane.setPreferredSize(new Dimension(300, 300));
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		panel.add(scrollPane);
		return panel;
	}

	private RTextScrollPane createTextArea() {
		textArea = new RSyntaxTextArea(50, 50);
		textArea.setOpaque(false);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
		textArea.setCodeFoldingEnabled(true);
		textArea.setAntiAliasingEnabled(true);
		textArea.setEditable(false);
		RTextScrollPane scrollPane = new RTextScrollPane(textArea);
		scrollPane.setFoldIndicatorEnabled(true);
		// Suporte
		LanguageSupport languageSupport = new CLanguageSupport();
		languageSupport.install(textArea);
		return scrollPane;
	}

}
