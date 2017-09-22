package project;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.fife.util.OSUtil;

import project.desktop.MainFrame;

/**
 * @author Tecgraf/PUC-Rio
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					if (OSUtil.isWindows()) {
						for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
							if (info.getName().equals("Windows")) {
								UIManager.setLookAndFeel(info.getClassName());
							}
						}
					} else if (OSUtil.isMac()) {
						for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
							if (info.getName().equals("Nimbus")) {
								UIManager.setLookAndFeel(info.getClassName());
							}
						}
					} else if (OSUtil.isLinux()) {
						for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
							if (info.getName().equals("Windows")) {
								UIManager.setLookAndFeel(info.getClassName());
							}
						}
					}
				} catch (Exception e) {
				}
			}
		});
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
	}
	
}
