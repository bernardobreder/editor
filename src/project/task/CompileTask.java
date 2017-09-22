package project.task;

import project.builder.BuilderManager;
import project.console.ConsoleInstance;
import project.console.ConsolePanel;
import project.desktop.TaskQueue;
import project.desktop.TaskQueue.Task;
import project.desktop.TaskQueue.TaskLater;
import project.desktop.TaskQueue.TaskQuery;
import project.filesystem.FSFile;

public class CompileTask implements Task {

	private BuilderManager builderManager;

	private ConsolePanel consolePanel;

	private FSFile file;

	public CompileTask(BuilderManager builderManager, ConsolePanel consolePanel, FSFile file) {
		this.builderManager = builderManager;
		this.consolePanel = consolePanel;
		this.file = file;
	}

	@Override
	public void perform() throws Exception {
		final ConsoleInstance consoleInstance = TaskQueue.execute(new TaskQuery<ConsoleInstance>() {

			@Override
			public ConsoleInstance execute() {
				return consolePanel.createInstance();
			}
		});
		builderManager.compile(consoleInstance, file);
		TaskQueue.execute(new TaskLater() {

			@Override
			public void execute() {
				consoleInstance.close();
			}
		});
	}

	@Override
	public void updateUI() {
	}

	@Override
	public String getTitle() {
		return "Compiling source file";
	}

}
