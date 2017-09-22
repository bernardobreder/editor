package project.console;

/**
 * Uma instancia do console
 *
 * @author Tecgraf/PUC-Rio
 */
public interface ConsoleInstance {

	/**
	 * @param text
	 */
	public void write(String text);

	public void close();

}
