package project.task;

import java.util.Arrays;

import javax.swing.SwingUtilities;

import org.fife.util.OSProcess;
import org.fife.util.OSProcess.ProcessModel;

import project.console.ConsoleInstance;
import project.console.ConsolePanel;
import project.desktop.TaskQueue;
import project.desktop.TaskQueue.Task;
import project.desktop.TaskQueue.TaskQuery;
import project.filesystem.FSFile;

public class RunTask implements Task {

  private ConsolePanel consolePanel;

  private FSFile file;

  private ConsoleInstance consoleInstance;

  public RunTask(ConsolePanel consolePanel, FSFile file) {
    this.consolePanel = consolePanel;
    this.file = file;
  }

  @Override
  public void perform() throws Exception {
    consoleInstance = TaskQueue.execute(new TaskQuery<ConsoleInstance>() {

      @Override
      public ConsoleInstance execute() {
        return consolePanel.createInstance();
      }
    });
    new OSProcess(new ProcessModel() {

      @Override
      public void receiveLine(String line, boolean isError) {
        write(line);
      }
    }, Arrays.asList("make", "run")).setFolder(file.getProject()
      .toExternalFile()).start().waitForExit();
  }

  @Override
  public void updateUI() {
    consoleInstance.close();
  }

  @Override
  public String getTitle() {
    return "Running project";
  }

  protected void write(final String line) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        consoleInstance.write(line + "\n");
      }
    });
  }

}
