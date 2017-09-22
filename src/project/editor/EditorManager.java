package project.editor;

import javax.swing.text.JTextComponent;

import project.builder.BuilderManager;
import project.console.ConsolePanel;
import project.desktop.TaskQueue;
import project.filesystem.FSFile;
import project.task.CompileTask;
import project.task.FileOpenTask;
import project.task.FileSaveTask;

public class EditorManager {

	private FSFile editFile;

	private String contentSaved;

	private TaskQueue taskQueue;

	public EditorManager(TaskQueue taskQueue) {
		this.taskQueue = taskQueue;
	}

	public void save(JTextComponent field, BuilderManager builderManager, ConsolePanel consolePanel) {
		if (editFile != null) {
			String text = field.getText();
			if (!text.equals(contentSaved)) {
				taskQueue.pushWrite(new FileSaveTask(editFile, text));
				compile(builderManager, consolePanel, editFile);
				contentSaved = text;
			}
		}
	}

	public void compile(BuilderManager builderManager, ConsolePanel consolePanel, FSFile file) {
		taskQueue.pushWrite(new CompileTask(builderManager, consolePanel, file));
	}

	public void open(FSFile file, JTextComponent field) {
		taskQueue.pushRead(new FileOpenTask(file, field, this));
	}

	public FSFile getEditFile() {
		return editFile;
	}

	public String getContentSaved() {
		return contentSaved;
	}

	public void setContentSaved(String contentSaved) {
		this.contentSaved = contentSaved;
	}

	public void setEditFile(FSFile editFile) {
		this.editFile = editFile;
	}

}
