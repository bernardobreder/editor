package project.console;

import javax.swing.JTextArea;

public class ConsolePanel extends JTextArea {
	
	/**
	 * Construtor
	 */
	public ConsolePanel() {
		setEditable(false);
	}
	
	/**
	 * Cria uma instancia do console
	 *
	 * @return
	 */
	public ConsoleInstance createInstance() {
		return new ConsolePanelInstance(this);
	}
	
	/**
	 * Uma instancia do console
	 *
	 * @author Tecgraf/PUC-Rio
	 */
	public class ConsolePanelInstance implements ConsoleInstance {
		
		/** Console */
		private ConsolePanel console;
		
		/**
		 * @param console
		 */
		public ConsolePanelInstance(ConsolePanel console) {
			this.console = console;
			console.setText("");
			console.setEditable(true);
		}
		
		/**
		 * @param text
		 */
		@Override
		public void write(String text) {
			console.setText(console.getText() + text);
		}
		
		@Override
		public void close() {
			console.setEditable(false);
		}
		
	}
	
}
