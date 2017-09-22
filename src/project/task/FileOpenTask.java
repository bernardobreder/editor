package project.task;

import javax.swing.text.JTextComponent;

import project.desktop.TaskQueue.Task;
import project.editor.EditorManager;
import project.filesystem.FSFile;

public class FileOpenTask implements Task {
	
	private FSFile file;
	
	private String content;
	
	private JTextComponent area;
	
	private EditorManager editor;
	
	public FileOpenTask(FSFile file, JTextComponent area, EditorManager editor) {
		this.file = file;
		this.area = area;
		this.editor = editor;
	}
	
	@Override
	public void perform() throws Exception {
		content = file.readText();
	}
	
	@Override
	public void updateUI() {
		area.setText(content);
		editor.setEditFile(file);
		editor.setContentSaved(content);
	}
	
	@Override
	public String getTitle() {
		return "Opening file";
	}
	
}
